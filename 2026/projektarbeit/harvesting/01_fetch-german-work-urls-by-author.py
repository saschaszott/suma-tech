import requests
from bs4 import BeautifulSoup
import re
from pathlib import Path
import os

def fetch_book_urls_by_author_id(author_id):
    """
    Gibt eine Liste von URLs für alle Werke des Autors mit der angegebenen Autor-ID aus dem Project Gutenberg zurück.
    Es werden hierbei nur Werke berücksichtigt, die in deutscher Sprache verfügbar sind und bei denen der Autor
    tatsächlich in der Autor-Rolle angegeben ist.

    Parameter:
    - author_id (int): Die ID des Autors im Project Gutenberg, für den die URLs der Werke abgerufen werden sollen.
    """

    # URL der HTML-Seite im Project Gutenberg mit allen deutschsprachigen E-Books
    URL = "https://www.gutenberg.org/browse/languages/de"

    # HTML-Seite abrufen
    response = requests.get(URL)
    response.encoding = 'utf-8'
    if response.status_code != requests.codes.ok:
        print(f"Fehler beim Abrufen der Seite {URL}: {response.status_code}")
        return []

    # HTML-Seite parsen
    soup = BeautifulSoup(response.text, "html.parser")

    # Abschnitt mit h2-Überschrift des Autorennamens in der HTML-Seite finden
    author_section = None
    for h2 in soup.find_all("h2"):
        a_tag = h2.find("a", {"id": "a" + str(author_id)})
        if a_tag:
            author_section = h2
            break

    if not author_section:
        print(f"Autor-Abschnitt für Autor-ID {author_id} im HTML-Code nicht gefunden.")
        return []

    # Die direkt auf <h2> folgende <ul>-Liste durchsuchen
    book_list = author_section.find_next_sibling("ul")
    if not book_list:
        print(f"Keine Liste mit Werken des Autors mit ID {author_id} im HTML-Code gefunden.")
        return []

    # Alle <li>-Elemente extrahieren, die einen Link enthalten
    book_urls = []
    for li in book_list.find_all("li", class_="pgdbetext"):
        link = li.find("a")
        if link:
            href = link["href"]
            if re.match(r"/ebooks/\d+", href) and li.text.endswith("(German) (as Author)"):
                book_urls.append("https://www.gutenberg.org" + href)

    return book_urls

if __name__ == "__main__":
    # Mapping von Autoren-IDs im Project Gutenberg zu den entsprechenden Autorennamen
    authors = [
        {"id": 586,     "name": "Goethe"},
        {"id": 289,     "name": "Schiller"},
        {"id": 1049,    "name": "Heine"},
        {"id": 35073,   "name": "Mann"},
        {"id": 1735,    "name": "Kafka"},
        {"id": 941,     "name": "Hesse"},
        {"id": 1426,    "name": "Kant"},
        {"id": 1995,    "name": "Humboldt"},
        {"id": 1765,    "name": "Fontane"},
        {"id": 846,     "name": "Rilke"},
        {"id": 990,     "name": "Lessing"},
        {"id": 2128,    "name": "Storm"},
        {"id": 53,      "name": "Twain"},
        {"id": 845,     "name": "Wieland"},
    ]
    base_dir = "german-works"
    os.makedirs(base_dir, exist_ok=True)

    overall_num_of_ebook_urls = 0
    for author in authors:
        book_urls = fetch_book_urls_by_author_id(author["id"])
        print(f"Es wurden {len(book_urls)} E-Book-URLs von Werken des Autors {author['name']} gefunden.")
        overall_num_of_ebook_urls += len(book_urls)

        num_of_lines_written = 0
        if book_urls and len(book_urls) > 0:
            output_path = Path(base_dir) / f"{author['name']}-ebook-urls.txt"
            with output_path.open("w", encoding="utf-8") as file:
                for book_url in book_urls:
                    file.write(book_url + "\n")
                    num_of_lines_written += 1
            print(f"Die {num_of_lines_written} E-Book-URLs der Werke von {author['name']} wurden in der Datei {output_path.name} gespeichert.")

    print(f"Insgesamt wurden {overall_num_of_ebook_urls} E-Book-URLs gefunden.")