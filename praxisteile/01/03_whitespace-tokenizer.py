def tokenize_and_save(input_file, output_file):
    """
    Tokenisiert den Text der Eingabedatei basierend auf Whitespaces 
    und speichert die Tokens in einer Ausgabedatei.
    
    Parameter:
    - input_file (str): Der Pfad zur Eingabedatei, die tokenisiert werden soll.
    - output_file (str): Der Pfad zur Ausgabedatei, in die die Tokens geschrieben werden.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        text = infile.read() # Text aus der Datei lesen
    
    # Whitespace-Tokenisierung durchführen (Wörter durch Leerzeichen trennen)
    tokens = text.split()

        
    with open(output_file, "w", encoding="utf-8") as outfile:
        for token in tokens:
            outfile.write(token.strip() + "\n") # Jedes Token in eine neue Zeile schreiben

    num_of_tokens = len(tokens)
    print(f"Es wurden {num_of_tokens} Tokens in der Datei {output_file} gespeichert.")

if __name__ == "__main__":
    input_file = "21000_filtered.txt"
    output_file = "tokens.txt"
    tokenize_and_save(input_file, output_file)