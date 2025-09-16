import json

def load_index(file_name):
    """
    Lädt invertierten Index aus einer JSON-Datei.
    :param file_name: Name der Eingabedatei mit zuvor serialisiertem Index
    :return: Der invertierte Index als Python-Dictionary
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
    
if __name__ == "__main__":
    inverted_index_file = "Goethe-inverted-index.json"

    inverted_index = load_index(inverted_index_file)
    if inverted_index is None:
        print("Der invertierte Index konnte nicht geladen werden.")
    
    while True:
        # Benutzereingabe abfragen
        wort = input("Gib ein Suchwort ein (oder 'quit!'): ").strip()

        # Programm beenden, wenn Nutzer 'quit!' eingibt
        if wort == "quit!":
            break

        # Lookup im Dictionary durchführen
        wort = wort.lower()
        if wort in inverted_index:
            # Ausgabe der Dokument-IDs
            for rank, document_id in enumerate(inverted_index[wort], start=1):
                print(f"{rank}. Treffer in Dokument {document_id}")
        else:
            print("Es wurden leider keine Treffer gefunden.")
    