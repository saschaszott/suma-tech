import json
import sqlite3

def load_index(file_name):
    """
    Lädt invertierten Index aus der JSON-Datei mit dem übergebenen Namen.
    """
    try:
        with open(file_name, "r", encoding="utf-8") as index_file:
            return json.load(index_file)
    except FileNotFoundError:
        print(f"Die Indexdatei {file_name} konnte nicht gefunden werden.")
        return None
    except json.JSONDecodeError:
        print(f"Die Indexdatei {file_name} besitzt kein gültiges JSON-Format.")
        return None

def get_title_from_database(conn, doc_id):
    cursor = conn.cursor()
    
    cursor.execute("SELECT title FROM works WHERE id = ?", (doc_id,))
    result = cursor.fetchone()

    if result:
        return result[0]
    
    return None
    
if __name__ == "__main__":
    inverted_index_file = "Goethe-inverted-index.json"

    inverted_index = load_index(inverted_index_file)
    if inverted_index is None:
        print("Der invertierte Index konnte nicht geladen werden.")
        exit(1)
    
    conn = sqlite3.connect('pg-metadata.db')
    
    while True:
        wort = input("Gib ein Suchwort ein (oder 'quit!'): ").strip()

        # Programm beenden, wenn Nutzer 'quit!' eingibt
        if wort == "quit!":
            conn.close()
            break

        # Lookup im Dictionary durchführen
        wort = wort.lower()
        if wort in inverted_index:
            # Ausgabe der Dokument-IDs
            for rank, document_id in enumerate(inverted_index[wort], start=1):
                title = get_title_from_database(conn, document_id)
                if title is None:
                    title = f"Titel von Dokument mit ID {document_id} unbekannt"
                print(f"{rank}: {title}")
        else:
            print("Es wurden leider keine Treffer gefunden.")
    