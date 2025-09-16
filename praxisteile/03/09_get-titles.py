import requests
import sqlite3

def get_title_from_gutendex(book_id):
    """
    Holt den Titel eines E-Books von der Gutendex API basierend auf der übergebenen Buch-ID.
    """
    url = f"https://gutendex.com/books/{book_id}"
    response = requests.get(url)
    response.encoding = 'utf-8'
    
    if response.status_code == 200:
        data = response.json()
        title = data.get('title')
        return title
    
    print(f"Fehler beim Abruf der Metadaten von E-Book mit ID {book_id}: {response.status_code}")
    return None

def create_database():
    conn = sqlite3.connect('pg-metadata.db')
    cursor = conn.cursor()
    
    # Tabelle erstellen, falls sie nicht existiert
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS works (
            id INTEGER PRIMARY KEY,
            title TEXT,
            url TEXT
        )
    ''')
    conn.commit()
    return conn, cursor

# Funktion, um die Buchdaten in die Datenbank einzufügen
def insert_ebook_into_db(cursor, book_id, title, url):
    cursor.execute('''
        INSERT INTO works (id, title, url)
        VALUES (?, ?, ?)
    ''', (book_id, title, url))

def get_ebook_ids(file):
    """
    Ermittelt die E-Book-IDs aus einer Datei von E-Book-URLs.
    Die IDs werden als Schlüssel in einem Dictionary gespeichert,
    wobei der Wert die vollständige URL ist.
    """
    ebook_ids = {}
    with open(file, 'r') as file:
        for line in file:
            # ID steht hinter dem letzten Schrägstrich
            ebook_id = line.split('/')[-1].strip()
            ebook_ids[ebook_id] = line.strip()
    return ebook_ids

if __name__ == "__main__":
    conn, cursor = create_database()

    titles_file = 'Goethe-ebooks.txt'
    ebook_ids = get_ebook_ids(titles_file)

    for ebook_id in ebook_ids:
        print(f"Verarbeite E-Book mit ID {ebook_id}")
        title = get_title_from_gutendex(ebook_id)
        if title:
            insert_ebook_into_db(cursor, ebook_id, title, ebook_ids[ebook_id])
    
    # Änderungen in Datenbank speichern (Transaktion beenden)
    conn.commit()
    # Datenbankverbindung schließen
    conn.close()
