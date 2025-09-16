import re

def tokenize(input_file, case_insensitive=False):
    """
    Tokenisiert den Text der übergebenen Eingabedatei basierend auf Whitespaces.
    Hierbei wird eine ggf. vorhandene Korrektur, die in eckigen Klammern
    am Ende eines Tokens angeben ist, entfernt.
    
    Parameter:
    - input_file (str): Der Pfad zur Eingabedatei, die den Volltext enthält, der tokenisiert werden soll.
    - case_insensitive (bool): wenn True, werden die Tokens in Kleinbuchstaben umgewandelt.

    Rückgabewert:
    - tokens (list): Eine Liste von Tokens.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        text = infile.read() # Text aus der Datei lesen
    
    # Whitespace-Tokenisierung durchführen (einzelne Token werden durch Leerzeichen getrennt)
    tokens = text.split()

    result = []

    for token in tokens:
        # Korrekturannotation am Tokenende entfernen über regulären Ausdruck
        # Beispiel: "Faust[Korrektur]" wird zu "Faust"
        token_cleaned = re.sub(r"\[[^\[\]]+\]$", "", token)
        if token_cleaned != token:
            print(f"Suffix entfernt: {token} -> {token_cleaned}")
            token = token_cleaned
        if case_insensitive:
            token = token.lower() # alle Zeichen im Token in Kleinbuchstaben umwandeln
        result.append(token.strip()) # Token in Ergebnisliste speichern
    
    return result

def save_tokens(tokens, output_file):
    """
    Speichert die Liste der Tokens in einer Ausgabedatei.
    
    Parameter:
    - tokens (list): Eine Liste von Tokens.
    - output_file (str): Der Pfad zur Ausgabedatei, in die die Tokens geschrieben werden.
    """
    with open(output_file, "w", encoding="utf-8") as outfile:
        for token in tokens:
            outfile.write(token + "\n")

    print(f"Es wurden {len(tokens)} Tokens in der Datei {output_file} gespeichert.")

if __name__ == "__main__":
    input_file = "21000_filtered.txt" # Volltext Faust I (PG Header und PG Header bereits entfernt)
    output_file = "tokens.txt"
    tokens = tokenize(input_file, case_insensitive=True)
    save_tokens(tokens, output_file)