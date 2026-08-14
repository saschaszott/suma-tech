import os

def tokenize_words(input_file, output_file, min_token_length=4):
    """
    Liest den bereinigten Text (ohne Header und Footer des Project Gutenberg) aus der übergebenen Datei ein, tokenisiert
    die Wörter an Leerzeichen und speichert die so ermittelten Tokens zeilenweise in einer neuen Datei. Die Token
    werden vor der Speicherung in Kleinbuchstaben umgewandelt. Zusätzlich werden am Anfang und Ende der Tokens alle
    nicht-alphanumerischen Zeichen entfernt, um z.B. Satzzeichen zu entfernen. Vor der Tokenisierung werden
    Doppelstriche "--" durch ein Leerzeichen ersetzt, um zusammengesetzte Wörter zu trennen. Es werden nur Tokens mit
    einer Mindestlänge von 4 Zeichen verarbeitet.

    Parameter:
    - input_file (str): Pfad zur bereinigten Textdatei.
    - output_file (str): Pfad zur Ausgabedatei, in der die Tokens zeilenweise gespeichert werden.
    - min_token_length (int): Die Mindestlänge eines Tokens, um in die Ausgabe aufgenommen zu werden (Standard: 4).
    """
    with open(input_file, "r", encoding="utf-8") as infile, open(output_file, "w", encoding="utf-8") as outfile:
        for line in infile:
            if "--" in line:
                line = line.replace("--", " ")

            tokens = line.split() # Tokenisierung an Whitespaces
            for token in tokens:
                token = token.lower() # Umwandlung in Kleinbuchstaben

                # entferne am Tokenanfang alles bis zum ersten alphanumerischen Zeichen
                while token and not token[0].isalpha():
                    token = token[1:]
                if not token:
                    continue

                # entferne am Tokenende alles ab dem letzten alphanumerischen Zeichen
                while token and not token[-1].isalpha():
                    token = token[:-1]
                if not token:
                    continue

                # Mindestlänge von Tokens, um häufige Stoppwörter zu vermeiden, z.B. "der", "die", "und", etc.
                if len(token) < min_token_length:
                    continue

                outfile.write(token + "\n") # jedes Token in neue Zeile schreiben

if __name__ == "__main__":
    input_dir = "german-works"
    # Suche in Autorenverzeichnissen nach Textdateien mit Endung ".txt.clean" (von Footer und Header bereinigte Dateien)
    num_of_files_processed = 0
    for author_dir in os.listdir(input_dir):
        author_path = os.path.join(input_dir, author_dir)
        if os.path.isdir(author_path):
            print(f"Verarbeite Autor {author_dir}")
            for filename in os.listdir(author_path):
                if filename.endswith(".txt.clean"):
                    num_of_files_processed += 1
                    input_file = os.path.join(author_path, filename)
                    output_file = os.path.join(author_path, filename.replace(".txt.clean", ".tokens"))
                    tokenize_words(input_file, output_file)

    print(f"{num_of_files_processed} Dateien verarbeitet.")