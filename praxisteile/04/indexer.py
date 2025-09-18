import pysolr
import json

# Verbindungsdaten des Solr-Servers
solr_host = "localhost"
solr_port = "8983"
# Name des zu verwendenden Solr Kerns
solr_core = "my1stcore"

input_file_name = "cs-books.json"

# Metadaten aus der JSON-Datei cs-books.json auslesen
with open(input_file_name, 'r', encoding='utf-8') as file:
    books_data = json.load(file)
print(f"Aus der Datei {input_file_name} wurden die Metdaten von {len(books_data)} Büchern ausgelesen.")    

# Verbindung zu Solr-Server herstellen
solr_url = f"http://{solr_host}:{solr_port}/solr/{solr_core}"
solr = pysolr.Solr(solr_url)

# Indexierung der Dokumente
solr.add(books_data)
# Änderungen am Index übernehmen
solr.commit()
print(f"{len(books_data)} Dokumente wurden erfolgreich indexiert!")