import requests
import os
from pathlib import Path

SOLR_CORE = "gutenberg"
SOLR_CORE_URL = f"http://localhost:8983/solr/{SOLR_CORE}"
COVER_URL_TEMPLATE = "https://www.gutenberg.org/cache/epub/{id}/pg{id}.cover.medium.jpg"
COVER_DIR = Path("covers")
COVER_DIR.mkdir(exist_ok=True)

def get_all_gutenberg_ids():
    query = {
        "q": "*:*", # Suche nach allen Dokumente
        "fl": "id",
        "rows": 1000
    }
    response = requests.get(f"{SOLR_CORE_URL}/select", params=query)
    response.raise_for_status()
    docs = response.json()["response"]["docs"]
    return [doc["id"] for doc in docs]

def download_cover(id):
    url = COVER_URL_TEMPLATE.format(id=id)
    response = requests.get(url, stream=True)
    if response.status_code == 200:
        file_path = COVER_DIR / f"{id}.jpg"
        with open(file_path, "wb") as f:
            for chunk in response.iter_content(1024):
                f.write(chunk)
        print(f"Cover-Bild für {id} gespeichert unter {file_path}")
    else:
        print(f"Kein Cover-Bild für ID {id} ({response.status_code})")

if __name__ == "__main__":
    ids = get_all_gutenberg_ids()
    for id in ids:
        download_cover(id)
