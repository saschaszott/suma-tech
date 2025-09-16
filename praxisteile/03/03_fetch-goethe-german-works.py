import requests
import re
import os

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
    response = requests.get(url, timeout=60, stream=True)
    response.encoding = 'utf-8'
    if response.status_code == requests.codes.ok:
        with open(filename, "w", encoding="utf-8") as file:
            file.write(response.text)
        print(f"Datei erfolgreich heruntergeladen: {filename}")
        return True
    
    print(f"Fehler beim Herunterladen der Datei: {response.status_code}")
    return False

if __name__ == "__main__":
    base_dir = "goethe-works"
    if not os.path.exists(base_dir):
        os.mkdir(base_dir)
    
    num_of_downloads = 0
    with open("Goethe-ebooks.txt", "r", encoding="utf-8") as file:
        for base_url in file:
            match = re.search(r"/ebooks/(\d+)$", base_url)
            if match:
                ebook_id = match.group(1)
                url = f"{base_url.rstrip()}.txt.utf-8"
                print(f"Herunterladen von {url}")
                file = os.path.join(base_dir, f"{ebook_id}.txt")
                if download_file(url, file):
                    num_of_downloads += 1
    
    print(f"{num_of_downloads} Dateien erfolgreich heruntergeladen.")