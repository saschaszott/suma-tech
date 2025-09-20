import requests
import os
import json

SOLR_CORE_NAME = "gutenberg"
SOLR_URL = f"http://localhost:8983/solr/{SOLR_CORE_NAME}"

def add_spellcheck_field():
    payload = {
        "add-field": {
            "name": "spellcheck_base",
            "type": "gb_text_general",
            "stored": False,
            "indexed": True,
            "multiValued": True
        }
    }
    response = requests.post(f"{SOLR_URL}/schema/fieldtypes", json=payload)
    if response.status_code == 200:
        print(f"Indexfeld 'spellcheck_base' erfolgreich hinzugefügt.")
    else:
        print(f"Fehler beim Hinzufügen des Indexfelds 'spellcheck_base': {response.status_code} - {response.text}")

def index_content():
    # alle JSON-Dateien im Verzeichnis metadata durchlaufen und Inhalt ausgewählter Metadatenfelder in
    # neu angelegtes Indexfeld spellcheck_base einfügen
    for file in os.listdir("metadata"):
        if file.endswith(".json"):
            with open(os.path.join("metadata", file), "r") as json_file:
                metadata = json.load(json_file)

                # zugehörige Textdatei mit dem Volltext finden
                text_file = os.path.join("fulltexts", str(metadata.get("id")) + ".txt")
                full_text = ""
                if os.path.exists(text_file):
                    with open(text_file, "r", encoding="utf-8") as full_text_file:
                        full_text = full_text_file.read()

                id = metadata.get("id")
                doc = {
                    "id": id,
                    "spellcheck_base": {
                        "set": [
                            metadata.get("title"),
                            metadata.get("summaries"),
                            full_text
                        ]
                    }
                }
                headers = { "Content-Type": "application/json" }
                resp = requests.post(f"{SOLR_URL}/update?commit=true", headers=headers, json=[doc])
                if resp.status_code == 200:
                    print(f"Solr-Dokument mit ID {id} erfolgreich aktualisiert.")
                else:
                    print(f"Fehler bei Solr-Update für Dokument mit ID {id}: {resp.text}")

if __name__ == "__main__":
    add_spellcheck_field()
    index_content()
