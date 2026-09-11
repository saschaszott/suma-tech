"""Zeigt das Term Dictionary eines Indexfelds an."""

import pysolr

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
DEFAULT_FIELD = "title"
DEFAULT_LIMIT = 50
DEFAULT_SORT = "count"  # "index" = alphabetisch, "count" = nach Häufigkeit sortiert

def get_terms(
    field: str = DEFAULT_FIELD,
    limit: int = DEFAULT_LIMIT,
    sort: str = DEFAULT_SORT,
    prefix: str = "",
    mincount: int = None,
) -> list:
    solr = pysolr.Solr(SOLR_CORE_URL, timeout=30)

    extra_params = {"terms.limit": limit, "terms.sort": sort}
    if mincount is not None:
        extra_params["terms.mincount"] = mincount

    result = solr.suggest_terms([field], prefix, **extra_params)
    return result.get(field, [])

def print_terms(terms: list) -> None:
    print(f"\nTerm Dictionary mit {len(terms)} Termen:\n")
    if terms:
        for term, count in terms:
            print(f"{term} ({count})")

if __name__ == "__main__":
    terms = get_terms()
    print_terms(terms)
