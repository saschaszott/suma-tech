# Indexierung von E-Books aus dem Gutenberg-Projekt mittels Python und PySolr

Diese Übung kann in Zweierteams bearbeitet werden. Bitte beantworten Sie alle Fragen in der Textdatei `answers.txt`,
die ebenfalls im Projektverzeichnis abgelegt ist.

Pro Team ist genau eine Abgabe vorgesehen. Vermerken Sie daher die Namen der Mitglieder Ihres Teams in der
Textdatei `answers.txt`.

Nach der Lösung der nachfolgenden Aufgaben erzeugen Sie bitte aus dem Verzeichnis `ueb2` ein ZIP-Archiv. Dieses
ZIP-Archiv laden Sie im Moodle-Raum als Lösung hoch.

Für Fragen können Sie das Diskussionsforum im Moodle-Raum nutzen.

## Anlegen einer virtuellen Python-Umgebung

Legen Sie im Verzeichnis `2025/ueb2` zuerst eine virtuelle Python-Umgebung mit dem Namen `venv` an.

Starten Sie die virtuelle Python-Umgebung `venv` und installieren Sie in dieser Umgebung mit dem Paketmanager `pip`
die folgenden Programmbibliotheken:

- requests
- pysolr
- matplotlib

Welche Ausgabe liefert der Befehl `pip list` nach der Installation der o.g. Programmbibliotheken? Kopieren Sie die
gesamte Ausgabe des Befehls in die Textdatei `answers.txt`.

## Anlegen eines neuen Solr-Cores

Starten Sie Ihren Solr-Server (Docker Container), indem Sie den bereits angelegten Docker Container `solr` starten.
Sie können dazu entweder die graphische Benutzungsschnittstelle von Docker Desktop verwenden oder den `solr`-Container
über die Eingabe eines Befehls im Terminal starten.

Vergewissern Sie sich, dass der Solr-Server erfolgreich gestartet wurde. Sie können dazu die Weboberfläche von Solr in
Ihrem Browser unter der URL http://localhost:8983/solr aufrufen.

Legen Sie nun, analog zum Vorgehen beim Anlegen des Solr-Cores `my1stcore`, einen neuen Solr-Core mit dem Namen `ueb2`
im Solr-Server an.

## Anlegen eines Index-Schemas im neu angelegten Solr-Core

Nun betrachten wir das Python-Script `01_create_solr_schema.py`. Es ermöglicht die Konfiguration eines Indexschemas
im neu angelegten Solr-Core `ueb2`.

Zuerst soll der Modus _Auto-Create-Fields_ mit der Methode `disable_auto_create_fields_mode` im Solr-Core `ueb2`
deaktiviert werden, so dass wir das Indexschema des Solr-Cores `ueb2` selbst definieren können.

Durch die Deaktivierung des Modus _Auto-Create-Fields_ wird verhindert, dass Solr automatisch neue Indexfelder anlegt,
wenn ein Indexfeld in einem Eingabedokument referenziert wird, aber das Indexfeld im Indexschema noch nicht existiert.
Die Deaktivierung dieses Modus ist notwendig, damit wir das Indexschema exakt gemäß der nachfolgenden Spezifikation
anlegen können. Die Deaktivierung des Modus _Auto-Create-Fields_ erfolgt über die Solr Config API. Hierbei wird die
Solr-Konfigurationsdatei `solrconfig.xml` des Solr-Cores `ueb2` nicht direkt verändert. Stattdessen werden die
vorgenommenen Änderungen in einer separaten Datei mit dem Namen `configoverlay.json`gespeichert, die ebenfalls im
Verzeichnis `conf` des Solr-Cores `ueb2` gespeichert wird.

Jedes Solr-Indexschema ist durch eine Menge von **Feldtypen** und **Indexfeldern** gekennzeichnet.
Jedes Indexfeld besitzt (genau) einen Feldtyp. Ein Feldtyp kann von mehreren Indexfeldern verwendet werden.
Letztendlich bestimmt der Feldtyp:

1. die Verarbeitung des Inhalts eines Indexfelds des entsprechenden Typs während der Analysephase der Indexierung
2. die Verarbeitung der Suchanfrage während der Anfrageverarbeitung, wenn in einem Indexfeld des
   entsprechenden Typs gesucht wird

 Ein Indexfeld besitzt neben dem Feldtyp einen eindeutigen Namen und es legt fest, ob der Feldinhalt indexiert bzw.
 der ursprüngliche Inhalt (als Eingabe der Indexierung) gespeichert werden soll, so dass er später noch auf Basis der 
 Lucene-Indexdateien ausgegeben werden kann (ohne einen Zugriff auf die Ausgangsdaten).

Das Python-Script `01_create_solr_schema.py` bietet die Methode `create_index_field_types`, um zwei neue Feldtypen
zur Menge der bestehenden Feldtypen im Solr-Core mit dem übergebenen Namen hinzuzufügen:

* den Feldtyp `pg_pint` für die Speicherung von Ganzzahlen:
  ```xml
  <fieldType name="pg_pint" class="solr.IntPointField" docValues="true"/>
  ```

* den Feldtyp `pg_text_de` zur Indexierung von textuellem Inhalt:
   ```xml
  <fieldType name="pg_text_de" class="solr.TextField" positionIncrementGap="100">
    <analyzer>
      <tokenizer class="solr.StandardTokenizerFactory"/>
      <filter class="solr.LowerCaseFilterFactory"/>
      <filter class="solr.GermanNormalizationFilterFactory"/>
      <filter class="solr.GermanLightStemFilterFactory"/>
    </analyzer>
  </fieldType>
  ```

Sie können die Liste der verfügbaren Feldtypen in der Solr-Administration des Solr-Cores `ueb2` einsehen (unter dem
Punkt _Schema_) oder auch direkt über die Solr API abrufen.

### Aufgaben

1. Erweitern Sie die beiden Methoden `create_index_field_types` und `create_index_fields` im Python-Script
`01_create_solr_schema.py` um eine Prüfung, so dass bestehende Feldtypen bzw. Indexfelder nicht erneut angelegt werden.
Der Versuch, einen bereits existierenden Feldtyp bzw. ein bereits existierendes Indexfeld anzulegen, soll
keinen Fehler verursachen (normalerweise würde Solr in diesem Fall den Fehlercode 400 zurückliefern). Konsultieren Sie
hierzu die Solr Schema API Dokumentation, die Sie unter
https://solr.apache.org/guide/solr/latest/indexing-guide/schema-api.html finden.

2. Erweitern Sie das Python-Script `01_create_solr_schema.py`, so dass im Solr-Core `ueb2`
ein Indexschema gemäß der nachfolgenden Tabelle angelegt wird:

| Feldname             | Feldtyp | Unterstützt Suche? | Erlaubt Ausgabe? | Mehrwertig? | Pflichtfeld / "required"? |
|----------------------|---------|--------------------|------------------|-------------|---------------------------|
| `title_stemmed`      | `pg_text_de` | ja | nein | nein | ja |
| `author`             | `text_general` | ja | nein | nein | nein |
| `author_exact`       | `string` | ja | ja | nein | nein |
| `numOfDownloadsLast30Days` | `pg_pint` | ja | ja | nein | ja |
| `docType`            | `string` | ja | ja | nein | ja |
| `fulltext`           | `text_general` | ja | nein | nein | ja |
| `fulltext_stemmed`   | `pg_text_de` | ja | nein | nein | ja |
| `languages`          | `string` | ja | ja | ja | ja |
| `subjectHeadings`    | `text_general` | ja | ja | ja | nein | nein |


3. Führen Sie das Programm `01_create_solr_schema.py` aus und prüfen Sie den Erfolg Ihrer Umsetzung, indem Sie

3.1 die Existenz der Datei `configoverlay.json` im Verzeichnis `conf` des Solr-Cores `ueb2` kontrollieren. In dieser
Datei sollten die vorgenommenen Konfigurationsänderungen gespeichert worden sein. Außerdem können Sie durch Aufruf der
Methode `print_overlays` im Python-Script `01_create_solr_schema.py` den Inhalt der Datei `configoverlay.json` auf der
Kommandozeile ausgeben lassen.
3.2 den neu angelegten Solr-Core `ueb2` in der Solr-Administration auswählen (die Auswahl erfolgt über den Core
Selector) und die Funktion _Schema_ (in der linken Spalte, vorletzter Eintrag) aufrufen. Dort sollten alle zuvor
angelegten Indexfelder angezeigt werden.

4. Wie viele Indexfelder besitzt das Indexschema des neu angelegten Cores `ueb02` insgesamt? Tragen Sie diese Anzahl
in die Datei `answers.txt` ein.

## Untersuchung des Verhaltens des neuen Indexfeldtyps `pg_text_de`

Schauen Sie sich in Vorbereitung auf diesen Aufgabenteil das Video zur _Solr Analysis_ im YouTube-Kanal zur Vorlesung an:
https://youtu.be/5938k7UeCcg

Testen Sie nun, wie sich der neu angelegte Feldtyp `pg_text_de` bei der Indexierung und bei der Suchanfrageverarbeitung
verhält, indem Sie in der Solr-Webadministrationsoberfläche den Menüpunkt *Analysis* innerhalb des
Administrationsbereichs für den Solr-Core `ueb2` öffnen:

[Beispiel-Link für das Matching von **Türme** und **Turm**](http://localhost:8983/solr/#/ueb2/analysis?analysis.fieldvalue=t%C3%BCrme&analysis.query=turm&analysis.fieldtype=pg_text_de&verbose_output=1)

Prüfen Sie, in welchen Fällen der Stemmer gut funktioniert und in welchen Fällen der Stemmer an seine Grenzen stößt.
Beschreiben Sie Ihre Beobachtungen stichpunktartig in der Datei `answers.txt`.

Um die Funktionsweise der beiden Filter `GermanNormalizationFilter` und `GermanLightStemFilter` genauer zu verstehen,
können Sie auch einen Blick in den Quellcode der zugehörigen Java-Klassen von Apache Lucene werfen:

* [*German Normalization Filter*](https://github.com/apache/lucene/blob/main/lucene/analysis/common/src/java/org/apache/lucene/analysis/de/GermanNormalizationFilter.java)

* [*German Light Stemmer*](https://github.com/apache/lucene/blob/main/lucene/analysis/common/src/java/org/apache/lucene/analysis/de/GermanLightStemmer.java)

## Indexierung einer ausgewählten Dokumentkollektion aus Project Gutenberg

### Datenakquisition

Wir wollen nun einen größeren Datenbestand aus dem Project Gutenberg indexieren. Hierzu wollen wir die deutschsprachigen
E-Books aus Project Gutenberg betrachten.

Für jedes E-Book sollen zwei Dateien (UTF-8 codiert) gespeichert werden:

* eine XML-Datei mit den Metadaten (im Verzeichnis `pg-metadata`)
* eine Textdatei mit dem Volltext (im Verzeichnis `pg-fulltexts`)

Um die E-Books aus dem Project Gutenberg herunterzuladen, müssen wir zunächst die IDs der deutschsprachigen E-Books
ermitteln. Hierzu führen wir eine Suchanfrage im Project Gutenberg und iterieren anschließend über die Suchergebnisse.
Für jedes Suchergebnis speichern wir die ID des zugehörigen E-Books in der Datei `gutenberg_ids.txt`.

Das Python-Script `02_fetch_ids` erfüllt diese Aufgabe. Führen Sie das Script aus, um die Datei `gutenberg_ids.txt`
zu erzeugen.

Anschließend können wir die Metadaten und Volltexte der E-Books auf Basis der in der Datei `gutenberg_ids.txt`
gespeicherten IDs herunterladen. Hierzu steht das Python-Script `03_fetch_files.py` zur Verfügung. Führen Sie das
Script aus, um die Metadaten und Volltexte der E-Books herunterzuladen.

### Statistische Analyse der Volltexte

Mit dem Python-Script `04_text_analysis.py` können Sie eine einfache statistische Analyse der heruntergeladenen
Volltexte durchführen. Führen Sie das Script aus, um die Analyse zu starten. Betrachten Sie die Ausgabe des Scripts
und die beiden generierten PNG-Dateien. Beschreiben Sie Ihre Beobachtungen stichpunktartig in der Datei `answers.txt`.

### Indexierung der E-Books in den Solr-Core `ueb2`

Das Programm `05_solr_indexer.py` soll nun genutzt werden, um die heruntergeladene Dokumentkollektion
(d.h. Metadaten und Volltexte) zu indexieren.
Dazu müssen Sie das Programm noch an den mit `TODO` gekennzeichneten Stellen vervollständigen.
Anschließend starten Sie das Programm zur Indexierung der Dokumentkollektion. Kontrollieren Sie schließlich den Erfolg
der Indexierung, indem Sie in der Solr-Webanwendung die Suchanfrage `*:*` ausführen und den Wert von `numFounds` prüfe.

Geben Sie die Anzahl der gefundenen Dokumente in die Datei `answers.txt` an.

### Umsetzung einer einfachen Feldsuche

TODO

### Implementierung einer feldübergreifenden Suche mittels `DisMax Query Parser`

TODO

### Implementierung einer Autovervollständigung mittels `Terms Component`

TODO

### Löschen von wenig nachgefragten E-Books

TODO