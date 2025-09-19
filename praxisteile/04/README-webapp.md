# Entwicklung einer einfachen Webanwendung als Benutzungsschnittstelle

Wir wollen eine einfache Webanwendung schreiben, die

1. Suchanfragen entgegennimmt
2. die Suchanfrage an den Solr-Server sendet
3. die Suchergebnisse aus dem Solr-Server in einer tabellarischen Übersicht ausgibt

Die Suchanfrage kann vom Benutzer in einem Eingabeformular mit einem Suchschlitz übergeben werden. Gibt der Nutzer dort eine Suchanfrage ein und drückt auf den Button "Suchen", so wird die 1-Term-Anfrage auf dem Solr-Server ausgeführt.

Zuerst installieren wir die erforderlichen Python-Pakete:

```sh
pip install flask
pip install pysolr
```

Anschließend kann die Webanwendung mit dem folgenden Befehl gestartet:

```sh
python app.py
```

Nach dem Start der (lokal ausgeführten) Anwendung kann auf diese über den Webbrowser zugegriffen werden:

http://127.0.0.1:5000
