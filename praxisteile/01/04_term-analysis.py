def count_term_frequencies(input_file):
    """
    Liest eine Datei mit Tokens und bestimmt daraus die Häufigkeit der einzelnen Terme.
    Auf Basis der Häufigkeitswerte werden schließlich die häufigsten und seltensten Terme 
    ausgegeben.
    
    Parameter:
    - input_file (str): Der Pfad zur Datei mit den extrahierten Tokens.
    
    Rückgabe:
    - dict: Ein Dictionary mit den Termen und zugehörigen Häufigkeitswerten.
    """
    # Dictionary zur Zählung der Termhäufigkeiten
    term_frequencies = {}

    with open(input_file, "r", encoding="utf-8") as infile:
        # Datei zeilenweise einlesen
        tokens = infile.read().splitlines()

    # Häufigkeiten bestimmen
    for token in tokens:
        if token in term_frequencies:
            term_frequencies[token] += 1
        else:
            term_frequencies[token] = 1

    sorted_terms = sorted(term_frequencies.items(), key=lambda item: item[1])
    # 10 seltensten Terme
    print("\nSeltenste Terme:")
    for term, count in sorted_terms[:10]:
        print(f"{term} ({count})")

    # 10 häufigsten Terme
    print("\nHäufigste Terme:")
    for term, count in sorted_terms[-10:]:
        print(f"{term} ({count})")

    return term_frequencies

if __name__ == "__main__":
    input_file = "tokens.txt"
    term_frequencies = count_term_frequencies(input_file)
