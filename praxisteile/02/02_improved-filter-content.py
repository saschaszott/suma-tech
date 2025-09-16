import os

def extract_gutenberg_text(input_file, output_file):
    """
    Die Filterung wird erweitert. Auf den Bearbeitungshinweis am Anfang folgen
    6 Zeilenumbrüche (Newlines \n). Wir können alle Zeilen bis zum Finden des 6.
    Zeilenumbruchs ignorieren.

    Am Ende der Datei können wir die Zeile, die mit "[Anmerkungen zur Transkription:"
    beginnt, als Endmarker verwenden. Alles, was danach kommt, wird fortan ignoriert.

    Die Methode speichert den gefilterten Inhalt in einer neuen Textdatei mit dem 
    angegebenen Namen.

    Parameter:
    - input_file (str): Der Pfad zur Originaldatei.
    - output_file (str): Der Pfad zur bereinigten Ausgabedatei.
    """
    inside_text = False

    with open(input_file, "r", encoding="utf-8") as infile, open(output_file, "w", encoding="utf-8") as outfile:
        num_of_newlines = 0
        for line in infile:
            if num_of_newlines == 6:
                inside_text = True # Start gefunden → Ab jetzt den Inhalt aufsammeln und in Ausgabedatei schreiben
            if not inside_text:
                if line == "\n":
                    num_of_newlines += 1
                else:
                    num_of_newlines = 0
            else:
                if line.startswith("[Anmerkungen zur Transkription:"):
                    break # Verarbeitung der Eingabedatei beenden
                outfile.write(line) # relevanter Text für die Ausgabedatei

    print(f"Gefilteter Text wurde gespeichert in: {output_file}")

if __name__ == "__main__":
    input_file = "21000_filtered.txt" # Originaldatei
    input_file_backup = input_file + ".bak" # Backup der Originaldatei
    if os.path.exists(input_file_backup):
        print(f"Backup-Datei {input_file_backup} existiert bereits. Program wird beendet.")
        exit(1)
    
    os.rename(input_file, input_file_backup)
    extract_gutenberg_text(input_file_backup, input_file)