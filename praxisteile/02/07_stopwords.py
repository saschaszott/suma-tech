def count_term_frequencies(input_file):
    """
    Liest eine Datei mit Tokens und bestimmt daraus die Häufigkeiten der einzelnen Tokens.
    
    Parameter:
    - input_file (str): Der Pfad zur Datei mit den extrahierten Tokens.
    
    Rückgabe:
    - list: Eine Liste von Termhääufigkeitswerten.
    """
    # Dictionary zur Zählung der Termhäufigkeiten
    term_frequencies = {}

    with open(input_file, "r", encoding="utf-8") as infile:        
        tokens = infile.read().splitlines() # Datei zeilenweise einlesen

    for token in tokens:
        token = token.lower()
        if token in term_frequencies:
            term_frequencies[token] += 1
        else:
            term_frequencies[token] = 1
    
    return term_frequencies

def get_most_frequent_terms(input_file, ratio=0.05):
    term_frequencies = count_term_frequencies(input_file)
    num_of_distinct_terms = len(term_frequencies)
    max_num_of_terms_to_show = int(num_of_distinct_terms * ratio)
    sorted_keys = sorted(term_frequencies, key=term_frequencies.get, reverse=True)
    for index, key in enumerate(sorted_keys):
        print(f"{key}:{term_frequencies[key]}")
        if index > max_num_of_terms_to_show: # die gewünschte Anzahl an auszugegebenen Termhäufigkeiten wurde erreicht
            break

if __name__ == "__main__":
    input_file = "tokens.txt"
    get_most_frequent_terms(input_file, ratio=0.005)
