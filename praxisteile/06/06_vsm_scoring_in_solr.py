"""Scoring im Vektorraummodell (tf-idf) in Solr

Seit Solr 6 ist BM25 die Standard-Scoring-Funktion. Bis einschließlich Solr 5
nutzte Solr die ClassicSimilarity, die auf dem Vektorraummodell mit tf-idf-Termgewichtung
basiert. Diese steht weiterhin zur Verfügung und kann pro Indexfeldtyp aktiviert werden.

Mit dem Parameter --init legt dieses Skript (falls noch nicht vorhanden) im Schema
an und beendet sich danach:

- den Feldtyp text_classic: Analysedefinition wie text_general,
  aber mit "similarity": {"class": "solr.ClassicSimilarityFactory"}
- das Indexfeld title_classic vom Typ text_classic
- das Copy-Field title -> title_classic

Da Copy-Fields nur beim Indexieren befüllt werden, müssen die Dokumente nach
dem Anlegen des Felds einmal neu indexiert werden (siehe README.md).

Ohne --init werden nach Eingabe einer Suchanfrage die Top-3-Dokumente bzgl.
des Felds title_classic ausgegeben und die Berechnung des Score-Werts mittels
der Explain-Funktion von Solr nachvollzogen:

  - Blattknoten "weight(feld:term ...) [ClassicSimilarity]": Gewicht eines Terms
        w = boost · idf(t) · tf(t, d) · fieldNorm(d)
        idf(t)       = 1 + ln((N + 1) / (df(t) + 1))
        tf(t, d)     = sqrt(freq)
        fieldNorm(d) ≈ 1 / sqrt(doc_length(d))   (Längennormalisierung)
  - innere Knoten "sum of": Summe der Termgewichte über alle Anfrageterme
"""

import argparse
import math
import requests
import pysolr

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
FIELD = "title_classic"
TOP_K = 3

FIELD_TYPE = {
    "name": "text_classic",
    "class": "solr.TextField",
    "positionIncrementGap": "100",
    # identische Analyse wie text_general (Feldtyp von title), damit die
    # Ergebnisse mit dem BM25-Scoring aus 05_scoring_in_solr.py vergleichbar sind
    "indexAnalyzer": {
        "tokenizer": {"class": "solr.StandardTokenizerFactory"},
        "filters": [
            {"class": "solr.StopFilterFactory", "words": "stopwords.txt", "ignoreCase": "true"},
            {"class": "solr.LowerCaseFilterFactory"},
        ],
    },
    "queryAnalyzer": {
        "tokenizer": {"class": "solr.StandardTokenizerFactory"},
        "filters": [
            {"class": "solr.StopFilterFactory", "words": "stopwords.txt", "ignoreCase": "true"},
            {"class": "solr.SynonymGraphFilterFactory", "synonyms": "synonyms.txt", "ignoreCase": "true", "expand": "true"},
            {"class": "solr.LowerCaseFilterFactory"},
        ],
    },
    # tf-idf-Scoring (Vektorraummodell) statt BM25 für alle Indexfelder, die diesen Feldtyp haben
    "similarity": {"class": "solr.ClassicSimilarityFactory"},
}

FIELD_DEF = {"name": FIELD, "type": "text_classic", "stored": False, "indexed": True}
COPY_FIELD = {"source": "title", "dest": FIELD}

SEARCH_PARAMS = {
    "defType": "edismax",
    "qf": FIELD,
    "rows": TOP_K,
    "fl": "id, title, authors, score",
    "debug": "true",                     # Explain-Infos + geparste Anfrage anfordern
    "debug.explain.structured": "true",  # Ausgabe der Explain-Infos als JSON-Baum statt als Text
}

def ensure_schema(solr_core_url: str = SOLR_CORE_URL) -> None:
    """Feldtyp, Feld und Copy-Field anlegen, sofern noch nicht vorhanden."""
    schema = requests.get(f"{solr_core_url}/schema", timeout=30)
    schema.raise_for_status()
    schema = schema.json()["schema"]

    commands = {}
    if FIELD_TYPE["name"] not in {t["name"] for t in schema["fieldTypes"]}:
        commands["add-field-type"] = FIELD_TYPE
    if FIELD not in {f["name"] for f in schema["fields"]}:
        commands["add-field"] = FIELD_DEF
    if (COPY_FIELD["source"], COPY_FIELD["dest"]) not in {(c["source"], c["dest"]) for c in schema["copyFields"]}:
        commands["add-copy-field"] = COPY_FIELD

    if commands:
        resp = requests.post(f"{solr_core_url}/schema", json=commands, timeout=30)
        resp.raise_for_status()
        print(f"Schema erweitert: {', '.join(commands)}")

def field_is_populated(solr: pysolr.Solr) -> bool:
    return solr.search(f"{FIELD}:[* TO *]", rows=0).hits > 0

def child(node, prefix):
    """Kindknoten, dessen Beschreibung mit prefix beginnt."""
    return next((c for c in node.get("details", []) if c["description"].startswith(prefix)), None)

def explain_weight(node, indent):
    """tf-idf-Berechnung eines Blattknotens 'weight(feld:term ...)' ausgeben."""
    pad = " " * indent
    # z. B. "weight(title_classic:alice in 113) [ClassicSimilarity], result of:"
    field_term = node["description"].split("(", 1)[1].split(" in ", 1)[0]
    print(f"{pad}{node['value']:.4f}  tf-idf-Gewicht für {field_term}")

    score_node = node["details"][0]  # "score(freq=...), product of:"
    boost_node = child(score_node, "boost")
    boost = boost_node["value"] if boost_node else 1.0
    idf_node = child(score_node, "idf")
    tf_node = child(score_node, "tf")
    norm_node = child(score_node, "fieldNorm")

    n = child(idf_node, "docFreq")["value"]   # df(t)
    N = child(idf_node, "docCount")["value"]  # Anzahl der Dokumente mit diesem Indexfeld
    freq = tf_node["details"][0]["value"]     # tf(t, d)
    field_norm = norm_node["value"]           # 1/sqrt(doc_length(d))

    idf = 1 + math.log((N + 1) / (n + 1))
    tf = math.sqrt(freq)
    w = boost * idf * tf * field_norm
    dl_approx = 1 / field_norm ** 2

    pad2 = pad + "    "
    print(f"{pad2}boost        = {boost:g}  (Feldgewichtung aus qf)")
    print(f"{pad2}idf(t)       = 1 + ln((N + 1) / (df(t) + 1))")
    print(f"{pad2}             = 1 + ln(({N} + 1) / ({n} + 1)) = {idf:.4f}")
    print(f"{pad2}               N = {N} Dokumente mit Indexfeld; df(t) = {n}")
    print(f"{pad2}tf(t, d)     = sqrt(freq) = sqrt({freq:g}) = {tf:.4f}")
    print(f"{pad2}fieldNorm(d) ≈ 1 / sqrt(doc_length(d)) = {field_norm:.4f}")
    print(f"{pad2}               (entspricht doc_length(d) ≈ {dl_approx:.1f} Tokens)")
    print(f"{pad2}w(t, d)      = boost · idf(t) · tf(t, d) · fieldNorm(d)")
    print(f"{pad2}             = {boost:g} · {idf:.4f} · {tf:.4f} · {field_norm:.4f} = {w:.4f}")

def explain(node, indent=4):
    """Explain-Baum rekursiv ausgeben."""
    pad = " " * indent
    desc = node["description"]
    children = [c for c in node.get("details", []) if c.get("match", True)]

    if desc.startswith("weight("):
        explain_weight(node, indent)
    elif desc.startswith(("sum of", "max of")) and children:
        op = " + " if desc.startswith("sum") else ", "
        values = op.join(f"{c['value']:.4f}" for c in children)
        label = "Summe" if desc.startswith("sum") else "Maximum von"
        print(f"{pad}{node['value']:.4f}  = {label}: {values}")
        for c in children:
            explain(c, indent + 4)
    else:
        # sonstige Knoten (z. B. Phrasen, Boost-Funktionen, Konstanten)
        print(f"{pad}{node['value']:.4f}  {desc}")
        for c in children:
            explain(c, indent + 4)

def search(solr, query):
    results = solr.search(query, **SEARCH_PARAMS)
    if len(results) == 0:
        print("Suchanfrage liefert keine Treffer")
        return

    print(f"\nSuchanfrage mit {results.hits} Treffern")
    print(f"geparste Anfrage: {results.debug.get('parsedquery')}")

    explanations = results.debug.get("explain", {})
    for rank, doc in enumerate(results, start=1):
        print(f"\n#{rank}  ID {doc['id']}: {doc.get('title')}")
        print(f"    Score-Wert: {doc['score']:.4f}")
        print("    " + "-" * 70)
        explain(explanations[doc["id"]])

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Scoring im Vektorraummodell (tf-idf) in Solr")
    parser.add_argument("--init", action="store_true",
                        help="Feldtyp, Feld und Copy-Field im Schema anlegen und beenden")
    args = parser.parse_args()

    if args.init:
        ensure_schema()
        print("Bitte die Dokumentkollektion jetzt neu indexieren.")
        raise SystemExit(0)

    solr = pysolr.Solr(SOLR_CORE_URL)

    if not field_is_populated(solr):
        print(f"Das Feld {FIELD} ist noch leer. Bitte zuerst das Indexschema erweitern.")
        raise SystemExit(1)

    print("Gib eine Suchanfrage ein oder 'quit!' zum Verlassen.")
    while True:
        user_query = input("\nSuchanfrage: ").strip()
        if user_query.lower() == "quit!":
            break
        elif user_query:
            search(solr, user_query)
