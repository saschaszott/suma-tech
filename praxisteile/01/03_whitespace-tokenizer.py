def tokenize_and_save(input_file, output_file):
    """
    Tokenisiert den Text der Eingabedatei basierend auf Whitespaces und speichert die Tokens zeilenweise
    in einer Ausgabedatei mit dem übergebenen Namen ab.

    Parameter:
    - input_file (str): Der Pfad zur Eingabedatei mit dem Text, der tokenisiert werden soll.
    - output_file (str): Der Pfad zur Ausgabedatei, in die die Tokens zeilenweise geschrieben werden.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        text = infile.read() # Text aus der Datei lesen

    # Whitespace-Tokenisierung durchführen (d.h. Zeichenfolgen an Leerzeichen trennen)
    tokens = text.split()

    with open(output_file, "w", encoding="utf-8") as outfile:
        for token in tokens:
            outfile.write(token.strip() + "\n") # Jedes Token in eine neue Zeile der Ausgabedatei schreiben

    print(f"Es wurden {len(tokens)} Tokens in der Datei {output_file} gespeichert.")

if __name__ == "__main__":
    input_file = "21000_filtered.txt"
    output_file = "21000_tokens.txt"
    tokenize_and_save(input_file, output_file)