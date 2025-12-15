import pysolr
from configuration import SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME

def search(query, k=10):
    """
    Führt eine Suche im Solr-Core mit der übergebenen Suchanfrage durch und gibt die
    Top-k Suchergebnisse mit Titel, Autor und Score-Wert aus.
    """
    solr_server = pysolr.Solr(f'http://{SOLR_HOST}:{SOLR_PORT}/solr/{SOLR_CORE_NAME}')
    solr_server.ping()
    # TODO Auf dismax umschalten und Parameter ergänzen bzw. anpassen
    params = {
        "df": "title_stemmed",
        "defType": "lucene",
        "rows": k,
        "fl": "id title score",
        "q.op": "OR"
    }

    results = solr_server.search(query, **params)
    print(f"Anzahl Suchtreffer: {len(results)}")
    if len(results) > 0:
        print(f"Top-{k} Ranking für die Suchanfrage '{query}'")

        for rank, doc in enumerate(results):
            print(f"= Dokument mit Rang {rank + 1} =")
            print(f"  ID: {doc['id']}")
            print("  Titel: " + (doc['title'][0] if 'title' in doc else '-'))
            # TODO Ausgabe erweitern
            print(f"  Score-Wert: {doc['score']}")

if __name__ == "__main__":
    query = input("Bitte geben Sie Ihre Suchanfrage ein: ")
    search(query)