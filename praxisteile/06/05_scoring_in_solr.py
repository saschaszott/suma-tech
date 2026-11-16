"""BM25-Score-Berechnung in Solr

Nach Eingabe einer Suchanfrage werden die Top-3-Dokumente auf Basis des
BM25-Score-Werts ausgegeben. Für jedes Dokument wird die Berechnung des
BM25-Score-Werts detailliert erklärt.

Grundlage hierfür ist die "Explain"-Funktion von Solr (Parameter debug=results,
debug.explain.structured=true). Solr liefert pro Treffer einen Baum, der
zeigt, wie sich der BM25-Score-Wert aus den Einzelteilen zusammensetzt:

  - Blattknoten "weight(feld:term ...)": BM25-Gewicht eines Terms in einem Feld
        w = boost · idf · tf
        idf = log(1 + (N - n + 0.5) / (n + 0.5))
        tf  = freq / (freq + k1 · (1 - b + b · dl / avgdl))
  - innere Knoten "max of" / "sum of": Kombination der Teil-Scores
        (eDisMax: pro Anfrageterm das Maximum über alle Felder in qf,
         anschließend Summe über alle Anfrageterme)

Hinweis: Lucene verzichtet im tf-Anteil auf den Faktor (k1 + 1) im Zähler,
da dieser für alle Dokumente gleich ist und das Ranking nicht verändert.
"""

import math
import pysolr

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
TOP_K = 3

SEARCH_PARAMS = {
    "defType": "edismax",
    "qf": "title^2",
    "rows": TOP_K,
    "fl": "id, title, authors, score",
    "debug": "true",                     # Explain-Infos + geparste Anfrage anfordern
    "debug.explain.structured": "true",  # Ausgabe der Explain-Infos als JSON-Baum statt als Text
}

def detail_value(node, prefix):
    """Wert des Kindknotens, dessen Beschreibung mit prefix beginnt."""
    for child in node.get("details", []):
        if child["description"].startswith(prefix):
            return child["value"]
    return None

def explain_weight(node, indent):
    """BM25-Berechnung eines Blattknotens 'weight(feld:term ...)' ausgeben."""
    pad = " " * indent
    # z. B. "weight(title:alice in 113) [SchemaSimilarity], result of:"
    field_term = node["description"].split("(", 1)[1].split(" in ", 1)[0]
    print(f"{pad}{node['value']:.4f}  BM25-Gewicht für {field_term}")

    score_node = node["details"][0]  # "score(freq=...), computed as boost · idf · tf"
    boost = detail_value(score_node, "boost") or 1.0
    idf_node = next(d for d in score_node["details"] if d["description"].startswith("idf"))
    tf_node = next(d for d in score_node["details"] if d["description"].startswith("tf"))

    n = detail_value(idf_node, "n,") # document frequency df(t)
    N = detail_value(idf_node, "N,") # Anzahl der Dokumente im Indexfeld
    freq = detail_value(tf_node, "freq") # term frequency tf(t, d)
    k1 = detail_value(tf_node, "k1") # BM25-Parameter k1 (in Solr konfigurierbar, Standardwert 1.2)
    b = detail_value(tf_node, "b,") # BM25-Parameter b (in Solr konfigurierbar, Standardwert 0.75)
    dl = detail_value(tf_node, "dl") # document length doc_length(d), # Tokens im Feld des Dokuments
    avgdl = detail_value(tf_node, "avgdl") # average document length avg_doc_length

    idf = math.log(1 + (N - n + 0.5) / (n + 0.5))
    tf = freq / (freq + k1 * (1 - b + b * dl / avgdl))
    w = boost * idf * tf

    pad2 = pad + "    "
    print(f"{pad2}boost         = {boost:g}  (Feldgewichtung aus qf)")
    print(f"{pad2}idf_bm25(t)   = ln(1 + (N - df(t) + 0.5) / (df(t) + 0.5))")
    print(f"{pad2}              = ln(1 + ({N} - {n} + 0.5) / ({n} + 0.5)) = {idf:.4f}")
    print(f"{pad2}                N = {N} Dokumente im TD des Indexfelds; df(t) = {n}")
    print(f"{pad2}tf_bm25(t, d) = tf(t, d) / (tf(t, d) + k1 · (1 - b + b · doc_length(d) / avg_doc_lenght))")
    print(f"{pad2}              = {freq:g} / ({freq:g} + {k1:g} · (1 - {b:g} + {b:g} · {dl:g} / {avgdl:.4f})) = {tf:.4f}")
    print(f"{pad2}                Längennormalisierung: dl / avgdl = {dl / avgdl:.4f}")
    print(f"{pad2}w_bm25(t, d)  = boost · idf_bm25(t) · tf_bm25(t, d) = {boost:g} · {idf:.4f} · {tf:.4f} = {w:.4f}")

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
        if desc.startswith("sum"):
            print(f"{pad}{node['value']:.4f}  = Summe: {values}")
        else:
            print(f"{pad}{node['value']:.4f}  = Maximum von: {values}")
        for child in children:
            explain(child, indent + 4)
    else:
        # sonstige Knoten (z. B. Phrasen, Boost-Funktionen, Konstanten)
        print(f"{pad}{node['value']:.4f}  {desc}")
        for child in children:
            explain(child, indent + 4)

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
    print("Gib eine Suchanfrage ein oder 'quit!' zum Verlassen.")
    solr = pysolr.Solr(SOLR_CORE_URL)

    while True:
        user_query = input("\nSuchanfrage: ").strip()
        if user_query.lower() == "quit!":
            break
        elif user_query:
            search(solr, user_query)
