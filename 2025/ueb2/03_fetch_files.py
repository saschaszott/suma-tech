import os
import time
import random
import requests

RESOURCE_TYPES = {
    "metadata": {
        "suffix": ".rdf",
        "dir": "pg-metadata",
        "url": "https://www.gutenberg.org/cache/epub/{id}/pg{id}.rdf",
        "label": "RDF-Datei"
    },
    "full_texts": {
        "suffix": ".txt",
        "dir": "pg-fulltexts",
        "url": "https://www.gutenberg.org/cache/epub/{id}/pg{id}.txt",
        "label": "Volltextdatei"
    }
}

def read_ids(path):
    """
    Liest die Ebook-IDs aus der angegebenen Datei ein.
    """
    with open(path, "r", encoding="utf-8") as f:
        return [line.strip() for line in f if line.strip()]
    print(f"{len(ids)} Ebook-IDs geladen.")

def fetch_one(idx, ebook_id, resource_type, delay, timeout):
    """
    Lädt eine Datei eines bestimmten Typs für die angegebene Ebook-ID herunter.
    """
    if resource_type not in RESOURCE_TYPES:
        raise ValueError(f"Unbekannter Ressourcentyp: {resource_type}")
    url = RESOURCE_TYPES[resource_type]["url"].format(id=ebook_id)
    out_path = os.path.join(RESOURCE_TYPES[resource_type]["dir"], f"pg{ebook_id}{RESOURCE_TYPES[resource_type]['suffix']}")
    if os.path.exists(out_path):
        print(f"[{idx}] {RESOURCE_TYPES[resource_type]['label']} {out_path} existiert bereits.")
        return

    print(f"[{idx}] Lade {url} ...", end="")
    try:
        time.sleep(delay)
        resp = requests.get(url, timeout=timeout)
        resp.raise_for_status()
        with open(out_path, "wb") as f:
            f.write(resp.content)
        print(" OK.")
    except requests.RequestException as e:
        print(f" Fehler: {e}")

def fetch_all(ids, type, min_delay, max_delay, timeout):
    """
    Lädt alle Dateien eines bestimmten Typs für die angegebenen Ebook-IDs herunter.
    """
    if type not in RESOURCE_TYPES:
        raise ValueError(f"Unbekannter Ressourcentyp: {type}")
    os.makedirs(RESOURCE_TYPES[type]["dir"], exist_ok=True)
    for idx, eid in enumerate(ids):
        delay = random.uniform(min_delay, max_delay)
        fetch_one(idx, eid, type, delay, timeout)

if __name__ == "__main__":
    ids = read_ids("gutenberg_ids.txt") # IDs der deutschsprachigen E-Books aus Project Gutenberg
    fetch_all(ids, "metadata", 1, 2, 5)
    fetch_all(ids, "full_texts", 1, 2, 5)
