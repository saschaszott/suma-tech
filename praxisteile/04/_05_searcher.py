import pysolr
import locale
from datetime import datetime

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
locale.setlocale(locale.LC_TIME, "de_DE.UTF-8")

def search(solr, query):
    params = {
        "qf": "title^3 title_conservative title_aggressive authors^2 subjects^1",   # Gewichtung der Felder, die durchsucht werden
        "defType": "edismax",                                                       # Erweiterte DisMax-Suche
        "rows": 20,                                                                 # maximale Anzahl der Suchergebnisse
        "fl": "id, type, title, issued, authors, language, subjects, score",        # Felder, die zurückgegeben werden
    }

    results = solr.search(query, **params)  # Top-20-Ranking ermitteln
    if not results or len(results) == 0:
        print("Ihre Suchanfrage liefert keine Treffer!")
        return

    print(f"\n{len(results)} Suchergebnisse gefunden\n")

    # einzelne Suchergebnisse ausgeben
    for i, result in enumerate(results, start=1):
        issued = datetime.fromisoformat(result.get("issued").replace("Z", "+00:00"))
        print(f"""Suchtreffer # {i} (ID: {result.get('id')}): {', '.join(result.get("title"))}
          Autor: {", ".join(result.get("authors"))}
          Aufnahmedatum: {issued:%d. %B %Y}
          Sprache: {', '.join(result.get('language'))}
          Themengebiete: {', '.join(result.get('subjects'))}
          Typ: {result.get('type')}
          Score-Wert: {result.get("score")}""")

if __name__ == "__main__":
    print("Gib eine Suchanfrage ein oder 'quit!' zum Verlassen.")
    solr = pysolr.Solr(SOLR_CORE_URL)

    while True:
        user_query = input("Suchanfrage: ").strip()
        if user_query.lower() == "quit!":
            break
        elif user_query:
            search(solr, user_query)