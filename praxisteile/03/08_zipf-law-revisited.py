import matplotlib.pyplot as plt
import json

def count_frequencies(inverted_index):
    """
    Bestimmt auf Basis des übergebenen invertierten Index
    die Dokumenthäufigkeiten (document frequencies) der einzelnen Terme. 
    Anschließend werden die Terme nach Dokmenthäufigkeit absteigend sortiert.
    Die sortierte Liste der Dokumenthäufigkeiten wird zurückgegeben.
    """
    
    # Dictionary zur Zählung der Dokumenthäufigkeiten / Dokumentfrequenzen
    # (Anzahl der Dokumente, in denen der Term vorkommt)
    frequencies = {}

    # Dokumenthäufigkeiten bestimmen
    for term, postings_list in inverted_index.items():
        frequencies[term] = len(postings_list)
    
    # sortiere die Terme nach ihrer Dokumenthäufigkeit absteigend
    values_list = sorted(list(frequencies.values()), reverse=True)
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

def load_index(file_name):
    """
    Lädt invertierten Index aus einer JSON-Datei.
    """
    try:
        with open(file_name, "r", encoding="utf-8") as index_file:
            return json.load(index_file)
    except FileNotFoundError:
        print(f"Die Indexdatei {file_name} konnte nicht gefunden werden.")
        return None
    except json.JSONDecodeError:
        print(f"Die Indexdatei {file_name} besitzt kein gültiges JSON-Format.")
        return None

if __name__ == "__main__":
    
    inverted_index_file = "Goethe-inverted-index.json"

    inverted_index = load_index(inverted_index_file)
    if inverted_index is None:
        print("Der invertierte Index konnte nicht geladen werden.")
        exit(1)
    
    document_frequencies = count_frequencies(inverted_index)

    #plot_frequency_distribution(document_frequencies, log_y=False)
    plot_frequency_distribution(document_frequencies, log_y=True)
