import matplotlib.pyplot as plt

def count_term_frequencies(input_file):
    """
    Liest eine Datei mit Tokens und bestimmt daraus die Häufigkeit der einzelnen Terme, wobei die Groß-/Kleinschreibung
    der Tokens ignoriert wird.

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
        token = token.lower()
        if token in term_frequencies:
            term_frequencies[token] += 1
        else:
            term_frequencies[token] = 1

    return term_frequencies

def plot_frequency_distribution(term_frequencies, log_x = False, log_y = False):
    """
        Auf Basis der Häufigkeitswerte wird ein Histogramm der Termhäufigkeiten erstellt und als Visualisierung dargestellt.

        Parameter:
        - term_frequencies (dict): Ein Dictionary mit den Termen und ihren zugehörigen Häufigkeitswerten.
        - log_x (bool): Wenn True, wird die x-Achse logarithmisch skaliert.
        - log_y (bool): Wenn True, wird die y-Achse logarithmisch skaliert.
    """
    x_values = []
    y_values = []
    freq_values = set(term_frequencies.values())  # Menge aller Häufigkeitswerte
    print(f"Anzahl der unterschiedlichen Häufigkeiten: {len(freq_values)}")
    for freq_value in sorted(freq_values):
        x_values.append(freq_value)
        # Bestimme die Anzahl der Terme mit dem Häufigkeitswert freq_value
        num_of_terms = 0
        for term_freq in term_frequencies:
            if term_frequencies[term_freq] == freq_value:
                num_of_terms += 1
        y_values.append(num_of_terms)

    plt.figure(figsize=(8, 6))
    plt.scatter(x_values, y_values, color="red", s=5)
    x_label = "Termhäufigkeit"
    y_label = "Anzahl der Terme"
    if log_x:
        plt.xscale("log")  # Logarithmische Skalierung der x-Achse
        x_label += " (log)"
    if log_y:
        plt.yscale("log")  # Logarithmische Skalierung der y-Achse
        y_label += " (log)"
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.title(f"Histogramm der Termhäufigkeiten mit {len(freq_values)} Punkten")
    plt.grid(True, which="both", linestyle="--", linewidth=0.5)  # Gitter für bessere Lesbarkeit
    plt.show()

if __name__ == "__main__":
    input_file = "21000_tokens.txt"
    term_frequencies = count_term_frequencies(input_file)

    # Termhäufigkeiten (Term Frequencies, tf) ausgeben
    print(f"tf(faust): {term_frequencies['faust']}")
    print(f"tf(pudel): {term_frequencies['pudel']}")

    # Ausgabe aller Hapax Legomena (Terme, die nur einmal im Text vorkommen)
    hapax_legomena = [term for term, freq in term_frequencies.items() if freq == 1]
    print(f"\nAnzahl der Hapax Legomena: {len(hapax_legomena)}")
    for term in hapax_legomena:
        print(term)

    # Termhäufigkeitsverteilung plotten
    plot_frequency_distribution(term_frequencies, log_x=False, log_y=False)
    plot_frequency_distribution(term_frequencies, log_x=True, log_y=True)
