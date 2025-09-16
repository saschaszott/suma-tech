import os
import json

def construct_index(base_dir):
    inverted_index = dict()    
    num_of_terms = 0

    # über alle Termdateien im Import-Verzeichnis iterieren
    for datei in os.listdir(base_dir):        
        if datei.endswith("-filtered-terms.txt"):
            document_id = datei.split("-", maxsplit=1)[0]

            # Termdatei einlesen und Terme in invertierten Index eintragen
            with open(os.path.join(base_dir, datei), "r", encoding="utf-8") as term_file:
                terms = term_file.read().splitlines()
                for term in terms:
                    if term not in inverted_index:
                        num_of_terms += 1
                        inverted_index[term] = []
                    inverted_index[term].append(document_id)
    
    print(f"Es wurden {num_of_terms} Terme in den invertierten Index eingetragen.")

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
    with open(inverted_index_file, "w", encoding="utf-8") as outfile:
        json.dump(inverted_index, outfile, ensure_ascii=False, indent=4, sort_keys=True)
    
    print(f"Invertierter Index wurde in {file_name} gespeichert.")

if __name__ == "__main__":
    # Import-Verzeichnis mit allen heruntergeladenen E-Books und den Termdateien (*-filtered-terms.txt)
    base_dir = "goethe-works"

    inverted_index = construct_index(base_dir)
    inverted_index_file = "Goethe-inverted-index.json"
    save_index(inverted_index, inverted_index_file)
    