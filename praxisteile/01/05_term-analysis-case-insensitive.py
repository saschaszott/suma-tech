def count_term_frequencies(input_file):
    """
    Liest eine Datei mit Tokens und bestimmt daraus die Häufigkeit der einzelnen Terme, wobei die Groß-/Kleinschreibung
    der Tokens ignoriert wird.
    Auf Basis der Häufigkeitswerte werden schließlich die häufigsten und seltensten Terme ausgegeben.

    Parameter:
    - input_file (str): Der Pfad zur Datei mit den extrahierten Tokens.

    Rückgabe:
    - dict: Ein Dictionary mit den Termen und ihren zugehörigen Häufigkeitswerten.
    """
    # Dictionary zur Zählung der Termhäufigkeiten
    term_frequencies = {}

    with open(input_file, "r", encoding="utf-8") as infile:
        # Datei zeilenweise einlesen
        tokens = infile.read().splitlines()

    # Häufigkeiten bestimmen
    for token in tokens:
        token = token.lower() # Token zuerst in Kleinbuchstaben umwandeln
        if token in term_frequencies:
            term_frequencies[token] += 1
        else:
            term_frequencies[token] = 1

    sorted_terms = sorted(term_frequencies.items(), key=lambda item: item[1])

    # 10 seltenste Terme
    print("\nSeltenste Terme:")
    for term, count in sorted_terms[:10]: # Ausgabe der ersten 10 Elemente der sortierten Liste (seltenste Terme)
        print(f"{term} ({count})")

    # 10 häufigste Terme
    print("\nHäufigste Terme:")
    for term, count in sorted_terms[-10:]: # Ausgabe der letzten 10 Elemente der sortierten Liste (häufigste Terme)
        print(f"{term} ({count})")

    return term_frequencies

if __name__ == "__main__":
    input_file = "21000_tokens.txt"
    term_frequencies = count_term_frequencies(input_file)
