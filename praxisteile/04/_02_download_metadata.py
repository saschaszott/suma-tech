"""Lädt die CSV-Datei mit Metadaten zu allen E-Books im Project Gutenberg herunter."""

import requests

CATALOG_URL = "https://www.gutenberg.org/cache/epub/feeds/pg_catalog.csv"
CATALOG_PATH = "pg_catalog.csv"

def download_catalog(dest_path: str, url: str = CATALOG_URL) -> str:
    response = requests.get(url, stream=True, timeout=60)
    response.raise_for_status()

    with open(dest_path, "wb") as f:
        for chunk in response.iter_content(chunk_size=8192):
            f.write(chunk)

    return dest_path

if __name__ == "__main__":
    path = download_catalog(CATALOG_PATH)
    size_mb = round(len(open(path, "rb").read()) / (1024 *1024), 2)
    print(f"CSV-Datei erfolgreich heruntergeladen: {path} ({size_mb} MB)")
