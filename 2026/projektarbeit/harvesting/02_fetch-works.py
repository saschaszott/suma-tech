import requests
import re
import os
import time

def download_file(url, filename):
    """
    Lädt eine UTF-8-codierte Textdatei von der angegebenen URL herunter und speichert sie als UTF-8-codierte Textdatei
    unter dem angegebenen Namen im lokalen Dateisystem. Bei der Textdatei handelt es sich um den Volltext eines
    E-Books aus dem Project Gutenberg. Die Funktion wartet zufällig zwischen 0.5 und 2 Sekunden, bevor sie die Datei
    von der angegebenen URL herunterlädt, um den Webserver von Project Gutenberg nicht zu überlasten.

    Parameter:
    - url (str): URL der Textdatei, die vom Project Gutenberg heruntergeladen werden soll.
    - filename (str): Der Name der lokal abzuspeichernden Datei.
    """

    # zwischen 0.5 und 2 Sekunden zufällig warten
    time.sleep(0.5 + 1.5 * (os.urandom(1)[0] / 255))

    response = requests.get(url, timeout=60, stream=True)
    response.encoding = 'utf-8'
    if response.status_code == requests.codes.ok:
        with open(filename, "w", encoding="utf-8") as file:
            file.write(response.text)
        print(f"Datei erfolgreich heruntergeladen und gespeichert unter {filename}")
        return True

    print(f"Fehler beim Herunterladen der Datei von URL {url}: {response.status_code}")
    return False

if __name__ == "__main__":
    base_dir = "german-works"
    if not os.path.exists(base_dir):
        print(f"Das Verzeichnis '{base_dir}' existiert nicht. Bitte zuerst die URLs der E-Books ermitteln.")
        exit(1)

    num_of_overall_downloads = 0
    num_of_errors = 0
    for filename in os.listdir(base_dir):
        if filename.endswith("-ebook-urls.txt"):
            author_name = filename.replace("-ebook-urls.txt", "")
            file_path = os.path.join(base_dir, filename)
            num_of_downloads = 0
            print(f"Verarbeite Datei {filename}")
            with open(file_path, "r", encoding="utf-8") as file:
                for base_url in file:
                    match = re.search(r"/ebooks/(\d+)$", base_url)
                    if match:
                        ebook_id = match.group(1)
                        url = f"https://www.gutenberg.org/ebooks/{ebook_id}.txt.utf-8"
                        print(f"Herunterladen der Textdatei von {url}")
                        os.makedirs(os.path.join(base_dir, author_name), exist_ok=True)
                        output_file = os.path.join(base_dir, f"{author_name}", f"{ebook_id}.txt")
                        if download_file(url, output_file):
                            num_of_downloads += 1
                        else:
                            num_of_errors += 1
            print(f"{num_of_downloads} Volltextdateien für Autor {author_name} heruntergeladen.\n")
            num_of_overall_downloads += num_of_downloads

    print(f"Insgesamt {num_of_overall_downloads} Volltextdateien erfolgreich heruntergeladen (Anzahl Fehler: {num_of_errors}).")