"""Indexiert die Metadaten der E-Books aus dem Project Gutenberg mit Apache Solr.
Dazu wird die CSV-Datei pg_catalog.csv eingelesen und für jede Zeile ein Solr-Dokument angelegt und an Solr gesendet."""

import csv
import re
import requests

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
CATALOG_PATH = "pg_catalog.csv"
BATCH_SIZE = 1000
DATE_RE = re.compile(r"^\d{4}-\d{2}-\d{2}$")

def split_multivalue(value: str) -> list:
    if not value:
        return []
    return [part.strip() for part in value.split(";") if part.strip()]

def row_to_doc(row: dict) -> dict:
    doc = {
        "id": row["Text#"],
        "type": row["Type"] or None,
        "title": row["Title"] or None,
        "language": split_multivalue(row["Language"]),
        "authors": split_multivalue(row["Authors"]),
        "subjects": split_multivalue(row["Subjects"]),
    }

    issued = (row["Issued"] or "").strip()
    if DATE_RE.match(issued):
        doc["issued"] = f"{issued}T00:00:00Z"

    return doc

def send_batch(solr_core_url: str, docs: list) -> None:
    # Senden der Dokumente an Solr
    resp = requests.post(f"{solr_core_url}/update/json/docs", json=docs, timeout=60)
    resp.raise_for_status()

def index_metadata(csv_path: str, solr_core_url: str = SOLR_CORE_URL, batch_size: int = BATCH_SIZE):
    total = 0
    batch = []

    with open(csv_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            batch.append(row_to_doc(row))
            if len(batch) >= batch_size:
                send_batch(solr_core_url, batch)
                total += len(batch)
                print(f"{total} Dokumente indexiert.")
                batch = []

        if batch:
            send_batch(solr_core_url, batch)
            total += len(batch)

    # Commit durchführen, um die Änderungen effektiv zu speichern
    requests.post(f"{solr_core_url}/update", json={"commit": {}}, timeout=60).raise_for_status()
    print(f"{total} Metadatensätze indexiert und erfolgreich in Solr gespeichert.")

if __name__ == "__main__":
    index_metadata(CATALOG_PATH)
