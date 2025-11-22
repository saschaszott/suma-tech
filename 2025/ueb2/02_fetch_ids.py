import requests
from bs4 import BeautifulSoup
import re

BASE_URL = "https://www.gutenberg.org/ebooks/search/?query=l.de"
OUTPUT_FILE = "gutenberg_ids.txt"

def fetch_ebook_ids(start_index=1):
    """
    Extrahiere alle Ebook-IDs von einer Suchergebnisseite der Website vom Project Gutenberg.
    """
    url = f"{BASE_URL}&start_index={start_index}"
    print(f"Abrufen der Ergebnisseite {url}")
    r = requests.get(url)
    r.raise_for_status()
    soup = BeautifulSoup(r.text, "html.parser")

    # Alle Links suchen, die wie /ebooks/<ID> aussehen
    ids = []
    for a in soup.find_all("a", href=True):
        match = re.match(r"^/ebooks/(\d+)$", a["href"])
        if match:
            ids.append(match.group(1))
    return ids

if __name__ == "__main__":
    start_index = 1
    all_ids = []

    while True:
        ids = fetch_ebook_ids(start_index)
        if not ids:
            # Keine weiteren IDs gefunden, Programm beenden
            break
        for ebook_id in ids:
            if ebook_id not in all_ids:  # Duplikate vermeiden
                all_ids.append(ebook_id)
        start_index += 25 # Nächste Ergebnisseite (25 Treffer pro Seite)

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        for id in all_ids:
            f.write(id + "\n")

    print(f"Es wurden {len(all_ids)} IDs erfolgreich in {OUTPUT_FILE} gespeichert.")