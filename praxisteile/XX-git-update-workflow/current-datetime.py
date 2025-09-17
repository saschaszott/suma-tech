from datetime import datetime

def main():
    now = datetime.now()
    # ein Kommentar, der vom Dozenten eingefügt wurde <-- weitere Änderung
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
