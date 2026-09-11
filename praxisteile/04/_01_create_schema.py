"""Legt im Solr-Core my1stcore ein Schema fuer die Spalten der pg_catalog.csv an.

Nutzt die Solr Schema-API, zum Anlegen von Indexfeldtypen, Indexfeldern und Copy-Fields.

Es werden zwei Feldtypen definiert, die unterschiedlich stark analysieren:

  - text_conservative: nur Tokenisierung und Umwandlung in Kleinschreibung (präzise, aber geringer Recall)
  - text_aggressive: zusätzliche Umlaut-/Akzent-Normalisierung (ASCII-Folding), Stoppwort-Entfernung und Stemming.
    (größerer Recall erwartbar, dafür aber weniger präzise). Bei text_aggressive wird zudem zwischen Index- und
    Anfrageverarbeitung unterschieden: nur der queryAnalyzer enthält zusätzlich eine Synonym-Expansion
    (SynonymGraphFilterFactory).
"""

import requests

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"

FIELD_TYPES = [
    {
        "name": "text_conservative",
        "class": "solr.TextField",
        "positionIncrementGap": "100",
        "analyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [
                {"class": "solr.LowerCaseFilterFactory"},
            ],
        },
    },
    {
        "name": "text_aggressive",
        "class": "solr.TextField",
        "positionIncrementGap": "100",
        "indexAnalyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [
                {"class": "solr.LowerCaseFilterFactory"},
                {"class": "solr.ASCIIFoldingFilterFactory"},
                {"class": "solr.StopFilterFactory", "words": "lang/stopwords_en.txt", "ignoreCase": "true"},
                {"class": "solr.PorterStemFilterFactory"},
            ],
        },
        "queryAnalyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [
                {"class": "solr.SynonymGraphFilterFactory", "synonyms": "synonyms.txt", "ignoreCase": "true", "expand": "true"},
                {"class": "solr.LowerCaseFilterFactory"},
                {"class": "solr.ASCIIFoldingFilterFactory"},
                {"class": "solr.StopFilterFactory", "words": "lang/stopwords_en.txt", "ignoreCase": "true"},
                {"class": "solr.PorterStemFilterFactory"},
            ],
        },
    },
]

FIELDS = [
    #{"name": "id", "type": "string", "stored": True, "indexed": True},
    {"name": "type", "type": "string", "stored": True, "indexed": True},
    {"name": "issued", "type": "pdate", "stored": True, "indexed": True},
    {"name": "title", "type": "text_general", "stored": True, "indexed": True},
    {"name": "title_conservative", "type": "text_conservative", "stored": False, "indexed": True},
    {"name": "title_aggressive", "type": "text_aggressive", "stored": False, "indexed": True},
    {"name": "language", "type": "string", "stored": True, "indexed": True, "multiValued": True},
    {"name": "authors", "type": "string", "stored": True, "indexed": True, "multiValued": True},
    {"name": "subjects", "type": "string", "stored": True, "indexed": True, "multiValued": True},
]

COPY_FIELDS = [
    {"source": "title", "dest": "title_conservative"},
    {"source": "title", "dest": "title_aggressive"},
]

def get_existing_field_type_names(solr_core_url: str) -> set:
    resp = requests.get(f"{solr_core_url}/schema/fieldtypes", timeout=30)
    resp.raise_for_status()
    return {t["name"] for t in resp.json().get("fieldTypes", [])}

def get_existing_field_names(solr_core_url: str) -> set:
    resp = requests.get(f"{solr_core_url}/schema/fields", timeout=30)
    resp.raise_for_status()
    fields = resp.json().get("fields", [])
    return {f["name"] for f in fields}

def get_existing_copy_fields(solr_core_url: str) -> set:
    resp = requests.get(f"{solr_core_url}/schema/copyfields", timeout=30)
    resp.raise_for_status()
    return {(c["source"], c["dest"]) for c in resp.json().get("copyFields", [])}

def create_schema(solr_core_url: str = SOLR_CORE_URL) -> None:
    existing_types = get_existing_field_type_names(solr_core_url)
    new_types = [t for t in FIELD_TYPES if t["name"] not in existing_types]
    if new_types:
        resp = requests.post(f"{solr_core_url}/schema", json={"add-field-type": new_types}, timeout=30)
        resp.raise_for_status()
        print(f"{len(new_types)} Feldtyp(en) angelegt: {[t['name'] for t in new_types]}")
    else:
        print("Alle Feldtypen sind bereits vorhanden.")

    existing_fields = get_existing_field_names(solr_core_url)
    new_fields = [f for f in FIELDS if f["name"] not in existing_fields]
    if new_fields:
        resp = requests.post(f"{solr_core_url}/schema", json={"add-field": new_fields}, timeout=30)
        resp.raise_for_status()
        print(f"{len(new_fields)} Feld(er) angelegt: {[f['name'] for f in new_fields]}")
    else:
        print("Alle Felder sind bereits im Schema vorhanden.")

    existing_copy_fields = get_existing_copy_fields(solr_core_url)
    new_copy_fields = [
        c for c in COPY_FIELDS if (c["source"], c["dest"]) not in existing_copy_fields
    ]
    if new_copy_fields:
        resp = requests.post(f"{solr_core_url}/schema", json={"add-copy-field": new_copy_fields}, timeout=30)
        resp.raise_for_status()
        print(f"{len(new_copy_fields)} Copy-Field(s) angelegt: {new_copy_fields}")
    else:
        print("Alle Copy-Fields sind bereits vorhanden.")

if __name__ == "__main__":
    create_schema()
