"""Anzeige von Facetten für ausgewählte Felder wie type, language, subjects und authors."""

import pysolr

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
FACET_FIELDS = ["type", "language", "subjects", "authors"]
FACET_LIMIT = 20

def get_facets(
    query: str = "*:*",
    solr_core_url: str = SOLR_CORE_URL,
    fields: list = FACET_FIELDS,
    limit: int = FACET_LIMIT,
) -> dict:
    solr = pysolr.Solr(solr_core_url, timeout=30)
    results = solr.search(
        query,
        **{
            "rows": 0,
            "facet": "true",
            "facet.field": fields,
            "facet.limit": limit,
            "facet.mincount": 1,
            "df": "title",
        },
    )
    facet_fields = results.facets["facet_fields"]

    # Solr liefert je Feld eine "flache" Liste [wert1, anzahl1, wert2, anzahl2, ...]
    return {
        field: list(zip(flat[0::2], flat[1::2]))
        for field, flat in facet_fields.items()
    }

def print_facets(facets: dict) -> None:
    for field, pairs in facets.items():
        print(f"\n=== {field.capitalize()} ===")
        if pairs:
            for value, count in pairs:
                print(f"{value} ({count})")

if __name__ == "__main__":
    while True:
        user_query = input("Suchanfrage: ").strip()
        if user_query.lower() == "quit!":
            break
        elif user_query:
            facets = get_facets(user_query)
            print_facets(facets)
