import os
import json

def construct_index(author_path, author_dir):
    inverted_index = dict()
    num_of_terms = 0

    # über alle Termdateien im Import-Verzeichnis iterieren
    for file in os.listdir(author_path):
        if file.endswith("-filtered-terms.txt"):
            # ID des Dokuments aus dem Dateinamen extrahieren
            document_id = int(file.split("-", maxsplit=1)[0])

            # Termdatei einlesen und Terme in invertierten Index eintragen
            with open(os.path.join(author_path, file), "r", encoding="utf-8") as term_file:
                terms = term_file.read().splitlines()
                for term in terms:
                    if term not in inverted_index:
                        num_of_terms += 1
                        inverted_index[term] = []
                    inverted_index[term].append(document_id)

    print(f"Es wurden {num_of_terms} Terme in den invertierten Index von {author_dir} eingetragen.")

    # sortiere nun noch die Postings-Listen (Listen von Dokument-IDs) numerisch
    for term in inverted_index:
        inverted_index[term].sort(key=int)

    return inverted_index

def save_index(inverted_index, file_name):
    """
    Speichert den invertierten Index in einer JSON-Datei.
    Hierbei werden sowohl die Terme im Term Dictionary als auch die Dokument-IDs
    in den Postings-Listen sortiert. Terme werden lexikographisch sortiert.
    Dokument-IDs sind numerisch und werden daher numerisch sortiert.

    :param inverted_index: Der invertierte Index als Python-Dictionary
    :param file_name: Der Name der Ausgabedatei
    """
    with open(file_name, "w", encoding="utf-8") as outfile:
        json.dump(inverted_index, outfile, ensure_ascii=False, indent=4, sort_keys=True)

    print(f"Invertierter Index wurde in {file_name} gespeichert.")

if __name__ == "__main__":
    # Import-Verzeichnis mit allen heruntergeladenen E-Books und den Termdateien (*-filtered-terms.txt)
    base_dir = "german-works"
    index_base_dir = "inverted-index"

    full_inverted_index = None
    for author_dir in os.listdir(base_dir):
        author_path = os.path.join(base_dir, author_dir)
        if os.path.isdir(author_path):
            inverted_index = construct_index(author_path, author_dir)
            inverted_index_file = f"{author_dir}-inverted-index.json"
            save_index(inverted_index, os.path.join(index_base_dir, inverted_index_file))

            if full_inverted_index is None:
                full_inverted_index = inverted_index
            else:
                # Füge den invertierten Index des Autors zum Gesamtindex hinzu
                for term, postings in inverted_index.items():
                    if term not in full_inverted_index:
                        full_inverted_index[term] = []
                    full_inverted_index[term].extend(postings)

    # Speichere den Gesamtindex (auf Basis der Werke aller Autoren) in einer JSON-Datei
    # vorher werden die Dokument-IDs in den Postings-Listen numerisch sortiert
    for term in full_inverted_index:
        full_inverted_index[term].sort(key=int)
    save_index(full_inverted_index, os.path.join(index_base_dir, "full-inverted-index.json"))
