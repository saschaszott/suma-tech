import requests
import time
import os
import json

SOLR_CORE_NAME = "gutenberg"
SOLR_URL = f"http://localhost:8983/solr/{SOLR_CORE_NAME}"

def get_all_docs_with_formats():
    query = {
        "q": "formats:text/plain*",
        "fl": "id,formats",
        "rows": 10000
    }
    resp = requests.get(f"{SOLR_URL}/select", params=query)
    resp.raise_for_status()
    return resp.json()["response"]["docs"]

def update_solr(id, metadata, fulltext):
    doc = {
        "id": id,
        "title_classic": { "set": metadata.get("title") },
        "summaries_classic": { "set": metadata.get("summaries") },
        "fulltext_classic": { "set": fulltext },
    }
    headers = { "Content-Type": "application/json" }
    resp = requests.post(f"{SOLR_URL}/update?commit=true", headers=headers, json=[doc])
    if resp.status_code == 200:
        print(f"Solr-Dokument mit ID {id} aktualisiert.")
    else:
        print(f"Fehler bei Solr-Update für {id}: {resp.text}")

def get_metadata(id):
    filepath = os.path.join("metadata", f"{id}.json")
    if not os.path.exists(filepath):
        print(f"Metadatendatei für ID {id} nicht gefunden.")
        return {}

    with open(filepath, "r", encoding="utf-8") as f:
        data = json.load(f)
    return data

def get_fulltext(id):
    filepath = os.path.join("fulltexts", f"{id}.txt")
    if not os.path.exists(filepath):
        print(f"Volltextdatei für ID {id} nicht gefunden.")
        return None

    with open(filepath, "r", encoding="utf-8") as f:
        fulltext = f.read()
    return fulltext


if __name__ == "__main__":
    docs = get_all_docs_with_formats()
    print(f"[INFO] {len(docs)} Dokumente mit ASCII-Textformat gefunden.")
    for doc in docs:
        id = doc["id"]
        metadata = get_metadata(id)
        fulltext = get_fulltext(id)
        update_solr(id, metadata, fulltext)
