from datetime import datetime

def main():
    now = datetime.now()
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d"))

if __name__ == "__main__":
    main()
