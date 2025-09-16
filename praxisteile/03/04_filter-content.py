import os

def extract_gutenberg_text(input_file, output_file):
    """
    Extrahiert den Text zwischen '*** START … ***' und '*** END … ***'
    aus der übergebenen E-Book-Textdatei vom Project Gutenberg und speichert 
    den gefilterten Inhalt in einer neuen Textdatei mit dem angegebenen Namen.

    Parameter:
    - input_file (str): Der Pfad zur Originaldatei.
    - output_file (str): Der Pfad zur bereinigten Ausgabedatei.
    """
    start_marker = "*** START OF THE PROJECT GUTENBERG EBOOK "
    end_marker = "*** END OF THE PROJECT GUTENBERG EBOOK "
    inside_text = False

    with open(input_file, "r", encoding="utf-8") as infile, open(output_file, "w", encoding="utf-8") as outfile:
        for line in infile:
            if start_marker in line:
                inside_text = True  # Start gefunden → Ab jetzt den Inhalt aufsammeln
                continue
            if end_marker in line:
                inside_text = False  # End gefunden → Den weiteren Inhalt ignorieren
                break
            if inside_text:
                outfile.write(line)  # relevanter Text für die Ausgabedatei

    print(f"Extrahierter Text wurde gespeichert in: {output_file}")

if __name__ == "__main__":
    # Import-Verzeichnis mit allen heruntergeladenen E-Books
    verzeichnis = "goethe-works"

    # über alle Textdateien im Import-Verzeichnis iterieren
    for datei in os.listdir(verzeichnis):
        if datei.endswith(".txt"):
            # Aufspaltung des Dateinamens in den Dateinamen (ohne Erweiterung) und die Dateierweiterung
            basename, extension = os.path.splitext(datei)

            # Textextraktion aufrufen
            extract_gutenberg_text(
                os.path.join(verzeichnis, datei),
                os.path.join(verzeichnis, f"{basename}-filtered{extension}"))