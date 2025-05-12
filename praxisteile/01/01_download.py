import requests

def download_file(url, filename):
    """
    Lädt eine UTF-8-codierte Datei von der angegebenen URL herunter 
    und speichert sie als UTF-8-codierte Textdatei unter dem angegebenen Namen.

    Parameter:
    - url (str): URL der Datei, die heruntergeladen werden soll.
    - filename (str): Der Name der lokal abzuspeichernden Datei.

    Rückgabe:
    - None: Die Funktion speichert die Datei und gibt keine Werte zurück.
    """
    response = requests.get(url)
    if response.status_code == requests.codes.ok:
        with open(filename, "wb") as file: # Datei im Binärmodus schreiben
            file.write(response.content) # Inhalt der Antwort in Datei schreiben
        print(f"Datei erfolgreich heruntergeladen: {filename}")
    else:
        print(f"Fehler beim Herunterladen der Datei: {response.status_code}")

if __name__ == "__main__":
    url = "https://www.gutenberg.org/ebooks/21000.txt.utf-8"
    filename = "21000.txt"
    download_file(url, filename)
