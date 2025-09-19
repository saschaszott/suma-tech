import matplotlib.pyplot as plt

def count_term_frequencies(input_file, tokenize_chars = False):
    """
    Liest eine Datei mit Tokens und bestimmt daraus die Häufigkeit der einzelnen Terme.
    Anschließend werden die Terme nach Häufigkeit absteigend sortiert. Die sortierte Liste der
    Häufigkeiten wird zurückgegeben.
    
    Parameter:
    - input_file (str): Der Pfad zur Datei mit den extrahierten Tokens.
    - tokenize_chars (bool): Gibt an, ob einzelne Zeichen als Terme betrachtet werden sollen.
    
    Rückgabe:
    - list: Eine Liste von absteigend sortierten Häufigkeitswerten.
    """
    # Dictionary zur Zählung der Termhäufigkeiten
    term_frequencies = {}

    with open(input_file, "r", encoding="utf-8") as infile:
        # Datei zeilenweise einlesen
        tokens = infile.read().splitlines()

    # Häufigkeiten bestimmen
    for token in tokens:
        if tokenize_chars:
            # Tokenisierung auf Zeichenebene: ein Term ist ein einzelnes Zeichen
            for char in token:
                if char in term_frequencies:
                    term_frequencies[char] += 1
                else:
                    term_frequencies[char] = 1
        else :
            token = token.lower()
            if token in term_frequencies:
                term_frequencies[token] += 1
            else:
                term_frequencies[token] = 1
    
    # Sortiere die Terme nach ihrer Häufigkeit absteigend
    values_list = sorted(list(term_frequencies.values()), reverse=True)
    return values_list

def plot_frequency_distribution(term_frequencies, log_y = False):
    x_values = []
    y_values = []
    for index, term_freq in enumerate(term_frequencies):
        x_values.append(index + 1) # Rang
        y_values.append(term_freq) # Häufigkeitswert

    plt.figure(figsize=(8, 8))
    plt.scatter(x_values, y_values, color="red", s=5)
    x_label = "Rang des Terms"
    y_label = "Häufigkeit des Terms"
    if log_y:
        plt.yscale("log")  # Logarithmische Skalierung der y-Achse
        y_label += " (log)"
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.grid(True, which="both", linestyle="--", linewidth=0.5)  # Gitter für bessere Lesbarkeit
    plt.show()

if __name__ == "__main__":
    input_file = "tokens.txt"
    term_frequencies = count_term_frequencies(input_file, tokenize_chars=False)

    plot_frequency_distribution(term_frequencies, log_y=False)
    #plot_frequency_distribution(term_frequencies, log_y=True)
