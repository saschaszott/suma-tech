def extract_gutenberg_text(input_file, output_file):
    """
    Extrahiert den Text zwischen '*** START … ***' und '*** END … ***' aus der übergebenen E-Book-Textdatei vom
    Project Gutenberg und speichert den gefilterten Inhalt in einer neuen Textdatei mit dem angegebenen Namen ab.

    Parameter:
    - input_file (str): Der Pfad zur Originaldatei.
    - output_file (str): Der Pfad zur bereinigten Ausgabedatei.
    """
    start_marker = "*** START OF THE PROJECT GUTENBERG EBOOK "
    end_marker = "*** END OF THE PROJECT GUTENBERG EBOOK "
    inside_text = False  # Flag, um zu verfolgen, ob wir uns innerhalb des relevanten Textabschnitts befinden
    num_of_lines = 0  # Zähler für die Anzahl der Zeilen im extrahierten Text

    # zeilenweises Einlesen der Originaldatei, um relevanten Textabschnitte zu extrahieren und in Ausgabedatei zu schreiben
    with open(input_file, "r", encoding="utf-8") as infile, open(output_file, "w", encoding="utf-8") as outfile:
        for line in infile:
            if line.startswith(start_marker):
                inside_text = True  # Start-Marker gefunden → Ab jetzt den Inhalt aus dem E-Book aufsammeln
                continue
            if line.startswith(end_marker):
                break # Ende-Marker gefunden → Den weiteren Inhalt der Textdatei ignorieren
            if inside_text:
                outfile.write(line)  # relevanter Text für die Ausgabedatei
                num_of_lines += 1

    print(f"Extrahierter Text wurde gespeichert in: {output_file} ({num_of_lines} Zeilen)")

if __name__ == "__main__":
    input_file = "21000.txt"            # Originaldatei
    output_file = "21000_filtered.txt"  # Gefilterte Ausgabedatei ohne Start- und End-Bereiche
    extract_gutenberg_text(input_file, output_file)