import pysolr
from configuration import SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME

def delete_ebooks_with_few_downloads(max_downloads):
    """
        Löscht alle E-Books aus dem Solr-Index, die in den letzten 30 Tagen nicht mehr als `max_downloads` Downloads
        hatten.
    """
    solr_server = pysolr.Solr(f'http://{SOLR_HOST}:{SOLR_PORT}/solr/{SOLR_CORE_NAME}', always_commit=True, timeout=10)
    solr_server.ping()

    results = solr_server.search("*:*")
    print(f"Ursprüngliche Anzahl der E-Books im Index: {results.hits}")

    try:
        solr_server.delete(q='') # TODO: Delete Query ergänzen
    except Exception as e:
        print(f"Fehler beim Löschen der wenig nachgefragten E-Books: {e}")
        return

    results = solr_server.search("*:*")
    print(f"Verbleibende Anzahl der E-Books im Index: {results.hits}")

if __name__ == '__main__':
    # TODO delete_ebooks_with_few_downloads mit einem sinnvollen Parameter aufrufen
    delete_ebooks_with_few_downloads(-1)
