import pysolr

# Verbindungsdaten des Solr-Servers
solr_host = "localhost"
solr_port = "8983"
# Name des zu verwendenden Solr Kerns
solr_core = "my1stcore"

# Verbindung zu Solr-Server herstellen
solr_url = f"http://{solr_host}:{solr_port}/solr/{solr_core}"
solr = pysolr.Solr(solr_url)

def search(query):
    params = {
        "qf": "title^3 authors^2 keywords^1",   # Gewichtung der Felder, die durchsucht werden
        "defType": "edismax",                   # Erweiterte DisMax-Suche
        "rows": 20,                             # maximale Anzahl der Suchergebnisse
        "fl": "title, authors, publication_year, score",    # Felder, die zurückgegeben werden
    }

    results = solr.search(query, **params)  # Top-10-Ranking ermitteln
    if not results or len(results) == 0:
        print("Ihre Suchanfrage liefert keine Treffer!")
        return
    
    print(f"\n{len(results)} Suchergebnisse gefunden\n")
    
    # Suchergebnisse ausgeben
    for i, result in enumerate(results):
        title = result.get("title")[0]
        authors = ", ".join(result.get("authors"))
        year = result.get("publication_year")[0]
        score = result.get("score")
        print(f"Suchtreffer # {i+1}: {title} ({year}) von {authors} (Score: {score})")

if __name__ == "__main__":
    print("Gib eine Suchanfrage ein oder 'quit!' zum Verlassen.")
    
    while True:
        user_query = input("\nDeine Suchanfrage: ").strip()
        if user_query.lower() == "quit!":
            break
        elif user_query:
            search(user_query)