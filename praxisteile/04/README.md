# Erste Schritte mit Solr

## Anlegen eines Solr Kerns

In einem Solr-Server können mehrere unabhängige (d.h. voneinander getrennte) Kerne (_cores_) verwaltet werden.

Jeder Solr Kern besteht aus einer Menge von Konfigurationsdateien sowie aus einem Suchindex.

Durch den nachfolgenden Befehl wird ein neuer Solr Kern mit dem Namen `my1stcore` angelgt (der Befehl muss im Verzeichnis `2026/solr` ausgeführt werden):

```sh
docker compose exec solr solr create -c my1stcore
```

Durch den Aufruf des Befehls `create` wird ein neuer Solr-Kern mit dem Namen `my1stcore` angelegt. Die Administration dieses Kerns kann durch die Auswahl des Namens `my1stcore` im _Core Selector_ in der linken Spalte der Solr-Admin-Weboberfläche aufgerufen werden. 

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

# Indexierung mittels Python

Aufgaben:

1. Solr-Schema mit Indexfeldtypen, Indexfeldern und Copy-Fields anlegen
2. CSV-Datei mit den Metadaten der im Project Gutenberg verfügbaren Ressourcen herunterladen
3. Indexierung der Metadaten in den Solr-Kern `my1stcore` mittels Python (zwei Varianten: mit der Python-Bibliothek
  `requests`, mit der Python-Bibliothek `pysolr`)

Für die Indexierung von Dokumenten in einen Solr-Kern gibt es mehrere Möglichkeiten:

* Nutzung der Importfunktion in der Solr Admin-Weboberfläche (http://localhost:8983/solr/#/my1stcore/documents)
* Nutzung des mitgelieferten Post Tool (Kommandozeilenwerkzeug) von Solr (beschrieben in https://solr.apache.org/guide/solr/latest/indexing-guide/post-tool.html)
* Ausführung eines HTTP GET Request (z.B. mittels curl, wget, Postman, REST Client in VS Code, etc.)
* Nutzung eines Solr Clients (solche Clients sind für verschiedene Programmiersprachen vorhanden; u.a. für Java, Python etc.)

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
