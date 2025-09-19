import re

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
    
    # Regex zur Tokenisierung verwenden
    tokens = re.findall(r'\w+', text)  

    with open(output_file, "w", encoding="utf-8") as outfile:
        for token in tokens:
            if input_file.endswith("_filtered.txt"):
                # Korrektur am Tokenende entfernen
                token_cleaned = re.sub(r"\[[^\[\]]+\]$", "", token)
                if token_cleaned != token:
                    print(f"Korrektur entfernt: {token} -> {token_cleaned}")
                    token = token_cleaned
            if case_insensitive:
                token = token.lower() # alle Zeichen im Token in Kleinbuchstaben umwandeln
            outfile.write(token.strip()+ "\n") # Jedes Token in eine neue Zeile schreiben

    num_of_tokens = len(tokens)
    print(f"Es wurden {num_of_tokens} Tokens in der Datei {output_file} gespeichert.")

if __name__ == "__main__":
    input_file = "21000_filtered.txt"
    output_file = "tokens.txt"
    tokenize_and_save(input_file, output_file, case_insensitive=False)