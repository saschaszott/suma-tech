import nltk
from nltk.tokenize import word_tokenize
import re
import os

def tokenize_and_save(input_file, output_file, case_insensitive=False):
    """
    Tokenisiert den Text der Eingabedatei basierend auf Whitespaces 
    und speichert die Tokens in einer Ausgabedatei.
    Hierbei wird eine ggf. vorhandene Korrektur (in eckigen Klammern) 
    am Ende eines Token entfernt.
    
    Parameter:
    - input_file (str): Der Pfad zur Eingabedatei, die tokenisiert werden soll.
    - output_file (str): Der Pfad zur Ausgabedatei, in die die Tokens geschrieben werden.
    - case_insensitive (bool): Wenn True, werden die Tokens in Kleinbuchstaben umgewandelt.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        text = infile.read() # Text aus der Datei lesen
    
    # NLTK zur Tokenisierung verwenden (deutsche Sprache)
    tokens = word_tokenize(text, "german")

    num_of_tokens_in_file = 0
    with open(output_file, "w", encoding="utf-8") as outfile:
        for token in tokens:
            # Korrektur am Tokenende entfernen
            token_cleaned = re.sub(r"\[[^\[\]]+\]$", "", token)
            if token_cleaned != token:
                print(f"Korrektur entfernt: {token} -> {token_cleaned}")
                token = token_cleaned
            
            # führender und abschließender Unterstrich entfernen
            # und anschließend möglichen Punkt am Ende entfernen
            token = token.strip("_")
            if token.endswith("."):
                token = token[:-1]
            
            if len(token.strip()) < 2: # Token mit nur einem Zeichen ignorieren
                continue
            
            if case_insensitive:
                token = token.lower() # alle Zeichen im Token in Kleinbuchstaben umwandeln
            outfile.write(token.strip()+ "\n") # Jedes Token in eine neue Zeile schreiben
            num_of_tokens_in_file += 1

    print(f"Es wurden {num_of_tokens_in_file} Tokens in der Datei {output_file} gespeichert.")

if __name__ == "__main__":

    # NLTK-Tokenizer initialisieren
    if not os.path.exists("nltk_data"):
        nltk.download("punkt_german", download_dir="nltk_data")

    # Import-Verzeichnis mit allen heruntergeladenen E-Books
    verzeichnis = "goethe-works"

    avg_ttr = 0
    num_of_files = 0

    # über alle Textdateien im Import-Verzeichnis iterieren
    for datei in os.listdir(verzeichnis):
        if datei.endswith("-filtered.txt"):
            num_of_files += 1
            # Aufspaltung des Dateinamens in den Dateinamen (ohne Erweiterung) und die Dateierweiterung
            basename, extension = os.path.splitext(datei)

            # Tokenizer aufrufen
            tokenize_and_save(
                os.path.join(verzeichnis, datei),
                os.path.join(verzeichnis, f"{basename}-tokens{extension}"),
                case_insensitive=True)
            
            # Termliste erstellen
            token_count = 0
            term_list = set()
            with open(os.path.join(verzeichnis, f"{basename}-tokens{extension}"), "r", encoding="utf-8") as token_file:
                for line in token_file:
                    term_list.add(line.strip())
                    token_count += 1
            
            # alphabetisch sortierte Termliste in Datei speichern
            term_file = os.path.join(verzeichnis, f"{basename}-terms{extension}")
            with open(term_file, "w", encoding="utf-8") as outfile:
                for term in sorted(term_list):
                    outfile.write(term + "\n")
            print(f"Es wurden {len(term_list)} Terme in der Datei {basename}-terms{extension} gespeichert.")
            ttr = len(term_list) / token_count
            print(f"Type Token Ratio (TTR): {ttr:.3f}")
            avg_ttr += ttr

    if num_of_files > 0:
        avg_ttr /= num_of_files
        print(f"\ndurchschnittliches TTR über alle Dateien: {avg_ttr:.3f}")