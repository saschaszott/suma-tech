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
        with open(filename, "wb") as file:
            file.write(response.content)
        print(f"Datei erfolgreich heruntergeladen: {filename}")
        return True

    print(f"Fehler beim Herunterladen der Datei: {response.status_code}")
    return False

if __name__ == "__main__":
    base_dir = "german-works"
    if not os.path.exists(base_dir):
        os.mkdir(base_dir)

    # URLs aus Textdateien im Verzeichnis german-work-urls einlesen
    base_dir_urls = "german-work-urls"
    for file_name in os.listdir(base_dir_urls):
        if file_name.endswith("-ebooks.txt"):
            input_file = os.path.join(base_dir_urls, file_name)
            author_name = file_name.replace("-ebooks.txt", "")
            if not os.path.exists(os.path.join(base_dir, author_name)):
                os.mkdir(os.path.join(base_dir, author_name))

            num_of_downloads = 0
            with open(input_file, "r", encoding="utf-8") as file:
                for base_url in file:
                    match = re.search(r"/ebooks/(\d+)$", base_url)
                    if match:
                        ebook_id = match.group(1)
                        url = f"{base_url.rstrip()}.txt.utf-8"
                        print(f"Herunterladen von {url}")
                        file = os.path.join(base_dir, author_name, f"{ebook_id}.txt")
                        if download_file(url, file):
                            num_of_downloads += 1

            print(f"{num_of_downloads} Dateien erfolgreich für Autor {author_name} heruntergeladen.")