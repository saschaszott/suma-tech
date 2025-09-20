import requests
import json
import time
import os

SOLR_CORE_NAME = "gutenberg"
SOLR_URL = f"http://localhost:8983/solr/{SOLR_CORE_NAME}/update?commit=true"
BASE_URL = "https://gutendex.com/books/?languages=en"

PAGES = 10  # Anzahl der Seiten (32 Metadatensätze pro Seite)

def fetch_books():
    all_books = []
    url = BASE_URL
    for page in range(PAGES):
        print(f"Abrufe von Seite {page + 1}...")
        resp = requests.get(url)
        if resp.status_code != 200:
            print(f"Fehler beim Abrufen: {resp.status_code}")
            break
        data = resp.json()
        all_books.extend(data.get("results", []))
        url = data.get("next")
        if not url:
            break
        time.sleep(2) # Rate-Limiting: Pause zwischen den Anfragen
    print(f"Insgesamt {len(all_books)} Metadatensätze heruntergeladen.")
    return all_books

def transform_to_solr(book):
    return {
        "id": book["id"],
        "title": book["title"],
        "subjects": book["subjects"],
        "author_names": [a["name"] for a in book["authors"]],
        "summaries": book["summaries"],
        "bookshelves": book["bookshelves"],
        "languages": book["languages"],
        "copyright": book["copyright"],
        "media_type": book["media_type"],
        "formats": [f"{k}: {v}" for k, v in book["formats"].items()],
        "download_count": book["download_count"]
    }

def index_solr_docs(docs):
    headers = {"Content-Type": "application/json"}
    resp = requests.post(SOLR_URL, headers=headers, data=json.dumps(docs))
    if resp.status_code != 200:
        print(f"unerwarteter Fehler: {resp.status_code} - {resp.text}")

def save_solr_docs(solr_docs):
    output_dir = 'metadata'
    os.makedirs(output_dir, exist_ok=True)
    
    for solr_doc in solr_docs:
        filepath = os.path.join(output_dir, str(solr_doc['id']) + ".json")
        with open(filepath, "w", encoding="utf-8") as outfile:
            json.dump(solr_doc, outfile, ensure_ascii=False, indent=4)

if __name__ == "__main__":
    books = fetch_books()
    solr_docs = [transform_to_solr(b) for b in books]
    save_solr_docs(solr_docs)
    index_solr_docs(solr_docs)
