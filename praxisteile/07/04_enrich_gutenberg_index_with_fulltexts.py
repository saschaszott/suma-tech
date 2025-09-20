import requests
import time
import os

SOLR_CORE_NAME = "gutenberg"
SOLR_URL = f"http://localhost:8983/solr/{SOLR_CORE_NAME}"
OUTPUT_DIR = "fulltexts"

def get_all_docs_with_formats():
    query = {
        "q": "formats:text/plain*",
        "fl": "id,formats",
        "rows": 10000
    }
    resp = requests.get(f"{SOLR_URL}/select", params=query)
    resp.raise_for_status()
    return resp.json()["response"]["docs"]

def extract_us_ascii_url(formats):
    for entry in formats:
        if entry.lower().startswith("text/plain"):
            parts = entry.split(":", 1)
            if len(parts) == 2:
                return parts[1].strip()
    return None

def download_fulltext(url):
    try:
        resp = requests.get(url)
        if resp.status_code == 200:            
            return resp.text
        else:
            print(f"Fehler {resp.status_code} beim Herunterladen: {url}")
    except Exception as e:
        print(f"Fehler beim Download: {e}")
    return None

def save_fulltext(id, fulltext):
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    filepath = os.path.join(OUTPUT_DIR, f"{id}.txt")
    with open(filepath, "w", encoding="utf-8") as f:
        f.write(fulltext)    

def update_solr(gid, fulltext):
    doc = {
        "id": gid,
        "fulltext": { "set": fulltext }
    }
    headers = { "Content-Type": "application/json" }
    resp = requests.post(f"{SOLR_URL}/update?commit=true", headers=headers, json=[doc])
    if resp.status_code == 200:
        print(f"Solr-Dokument mit ID {gid} aktualisiert.")
    else:
        print(f"Fehler bei Solr-Update für {gid}: {resp.text}")

if __name__ == "__main__":
    docs = get_all_docs_with_formats()
    print(f"[INFO] {len(docs)} Dokumente mit ASCII-Textformat gefunden.")
    for doc in docs:
        id = doc["id"]
        url = extract_us_ascii_url(doc.get("formats", []))
        if not url:
            print(f"[INFO] Keine gültige URL für ID {id}")
            continue
        
        time.sleep(2) # Rate-Limiting: Pause zwischen den Anfragen
        fulltext = download_fulltext(url)
        if fulltext:
            save_fulltext(id, fulltext)
            update_solr(id, fulltext)
