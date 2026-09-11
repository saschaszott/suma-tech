"""Alternative zu _03_index_metadata.py: nutzt pysolr statt HTTP-Requests mit requests zu erzeugen."""

import csv
import pysolr
import locale
from _03_index_metadata import row_to_doc

SOLR_CORE_URL = "http://localhost:8983/solr/my1stcore"
BATCH_SIZE = 1000
CATALOG_PATH = "pg_catalog.csv"
locale.setlocale(locale.LC_ALL, "de_DE.UTF-8")

def index_metadata(csv_path: str, solr_core_url: str = SOLR_CORE_URL, batch_size: int = BATCH_SIZE):
    solr = pysolr.Solr(solr_core_url, timeout=60, always_commit=False)

    total = 0
    batch = []

    with open(csv_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            batch.append(row_to_doc(row))
            if len(batch) >= batch_size:
                solr.add(batch)
                total += len(batch)
                print(f"{total:n} Dokumente indexiert.")
                batch = []

        if batch:
            solr.add(batch)
            total += len(batch)

    solr.commit()
    print(f"{total:n} Metadatensätze indexiert und erfolgreich in Solr gespeichert.")

if __name__ == "__main__":
    index_metadata(CATALOG_PATH)
