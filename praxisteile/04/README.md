# Erste Schritte mit Solr

## Anlegen eines Solr Kerns

In einem Solr-Server können mehrere unabhängige (d.h. voneinander getrennte) Kerne (_cores_) verwaltet werden. 

Jeder Solr Kern besteht aus einer Menge von Konfigurationsdateien sowie aus einem Suchindex.

Durch den nachfolgenden Befehl wird ein neuer Solr Kern mit dem Namen `my1stcore` angelgt (der Befehl muss im Verzeichnis `2025-04-05` ausgeführt werden):

```sh
docker compose exec solr solr create_core -c my1stcore
```

Durch den Aufruf des Befehls `create_core` wird ein neuer Solr-Kern mit dem Namen `my1stcore` angelegt. Die Administration dieses Kerns kann durch die Auswahl des Namens `my1stcore` im _Core Selector_ in der linken Spalte der Solr-Admin-Weboberfläche aufgerufen werden. 

Alternativ kann man auch folgende URL im Browser aufrufen:

```
http://localhost:8983/solr/#/my1stcore/core-overview
```

## Konfiguration des Solr Kerns `my1stcore`

Alle Solr Kerne werden im Verzeichnis `solrdata/data` verwaltet, indem ein Unterverzeichnis pro Kern angelegt wird. Nach dem erfolgreichen Anlegen des Solr Kerns `my1stcore` existiert somit im Verzeichnis `solrdata/data` ein Unterverzeichnis `my1stcore`, das beim Anlegen des Kerns erstellt wurde.

Der Inhalt des Verzeichnisses `my1stcore` hat folgende Struktur (gekürzt):

```
.
├── conf
│   ├── lang
│   │   ├── contractions_ca.txt
│   │   ├── contractions_fr.txt
│   │   ├── contractions_ga.txt
│   │   ├── contractions_it.txt
│   │   ├── hyphenations_ga.txt
│   │   ├── stemdict_nl.txt
│   │   ├── stoptags_ja.txt
│   │   ├── stopwords_de.txt
│   │   ├── stopwords_en.txt
│   │   ├── stopwords_...
│   │   └── userdict_ja.txt
│   ├── managed-schema.xml
│   ├── protwords.txt
│   ├── solrconfig.xml
│   ├── stopwords.txt
│   └── synonyms.txt
├── core.properties
└── data
    ├── index
    │   ├── segments_1
    │   └── write.lock
    ├── snapshot_metadata
    └── tlog
```

Im Verzeichnis `conf` werden die Konfigurationsdateien des Solr Kerns verwaltet. Die wichtigsten Konfigurationsdateien eines Solr Kerns sind die Dateien `solrconfig.xml` sowie `managed-schema.xml`. Wir werden später auf die Details dieser Dateien eingehen.

Im Verzeichnis `data` wird der Suchindex (ein gewöhnlicher Lucene Index) sowie das Transaktions-Log gespeichert. Wir gehen später auf die Details ein.

# Indexierung von Testdokumenten mittels Python

Wir wollen nun einige Testdokumente in den neu erzeugten Solr-Kern `my1stcore` laden. Dafür gibt es mehrere Möglichkeiten:

* Nutzung der Importfunktion in der Solr Admin-Weboberfläche (http://localhost:8983/solr/#/my1stcore/documents)
* Nutzung des mitgelieferten Post Tool (Kommandozeilenwerkzeug) von Solr (beschrieben in https://solr.apache.org/guide/solr/latest/indexing-guide/post-tool.html)
* Ausführung eines HTTP GET Request (z.B. mittels curl, wget, Postman, REST Client in VS Code, etc.)
* Nutzung eines Solr Clients (solche Clients sind für verschiedene Programmiersprachen vorhanden; u.a. für Java, Python etc.)

Im Rahmen des Moduls wollen wir den Solr Client `pysolr` verwenden. Dieser ermöglicht es einen Solr-Server über eine Python-Programmbibliothek (_library_) anzusprechen. D.h. mittels Python können programmatisch sowohl Dokumente indexiert als auch Suchanfragen abgesetzt (und die Ergebnisse empfangen) werden.

Wir schauen uns zuerst die **Indexierung** von einigen Testdokumenten an. Im nächsten Abschnitt beschäftigen wir uns schließlich mit dem Absetzen von Suchanfragen und der Verarbeitung des vom Solr-Server zurückgelieferten Suchergebnisses.

Für die Indexierung von Testdokumenten steht das Python-Script `indexer.py` zur Verfügung. Es kann wie folgt ausgeführt werden:

```sh
# virtuelle Python Umgebung anlegen
python -m venv venv

# virtuelle Umgebung starten
# … unter Linux / macOS
source venv/bin/activate
# … unter Windows CMD
venv\Scripts\activate
# … unter Windows Powershell
venv\Scripts\Activate.ps1

# Python-Programmbibliothek pysolr installieren
pip install pysolr

# Indexer ausführen
python indexer.py
```

Als Dokumentkollektion nutzen wir die Metadaten von 20 Informatik-Klassikern. Die Metadaten sind in der JSON-Datei `cs-books.json` gespeichert. Das erste Buch hat folgende Metadatenfelder:

```json
{
    "id": "1",
    "url": "https://www.pearson.com/store/p/introduction-to-algorithms/P100000323631",
    "title": "Introduction to Algorithms",
    "authors": ["Thomas H. Cormen", "Charles E. Leiserson", "Ronald L. Rivest", "Clifford Stein"],
    "publication_year": 2009,
    "abstract": "This textbook provides a comprehensive introduction to the modern study of computer algorithms. It covers a wide range of algorithms in depth, yet makes their design and analysis accessible to all levels of readers.",
    "publisher": "MIT Press",
    "keywords": ["Algorithms", "Computer Science", "Data Structures", "Mathematics"]
  },
```

Der Indexer fügt die in der Datei `cs-books.json` gespeicherten Metadaten zum Index des Solr-Kerns `my1stcore` hinzu. Den Indexierungsprozess werden wir in der Vorlesung im Detail besprechen.

# Suche im Solr Kern `my1stcore` mittels Python

Auch für die Suche in einem Solr-Kern gibt es unterschiedliche Möglichkeiten:

* Absetzen von Suchanfragen über das Webformular im Bereich _Query_ in der Solr-Admin-Weboberfläche
* Absetzen eines HTTP GET Requests (z.B. mittels curl, wget, Postman, REST Client in VS Code, etc.)
* Nutzung eines Solr Clients (siehe oben)

Wir wollen – analog zur Indexierung – auch für die Suche ein Python-Programm nutzen, das die Python Bibliothek `pysolr` verwendet. Damit können wir programmatisch Suchanfragen an einen Solr-Server senden und die vom Solr-Server zurückgelieferten Suchergebnisse in unserer Anwendung verarbeiten. Das Python-Porgramm befindet sich in der Datei `searcher.py`.

Wir nutzen die bereits beim Indexer beschriebene virtuelle Python-Umgebung, in der wir das Python-Paket `pysolr` über den Python-Paketmanager `pip` installiert haben.

```sh
python searcher.py
```

Das Programm erlaubt die Eingabe einer Suchanfrage auf der Kommandozeile. Nach dem Absetzen der Suchanfrage (Enter) wird die Suchanfrage an den Solr-Server gesendet. Anschließend werden die vom Solr-Server zurückgelieferten Suchergebnisse auf der Kommandozeile ausgegeben.

Das Programm kann durch die Eingabe von `quit!` beendet werden. Andernfalls wartet es auf die Eingabe der nächsten Suchanfrage.
