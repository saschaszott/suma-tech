# Indexierung von E-Books aus dem Gutenberg-Projekt mittels Python und PySolr

Diese Übung kann in Zweierteams bearbeitet werden. Bitte beantworten Sie alle Fragen in der Textdatei `answers.txt`,
die ebenfalls im Projektverzeichnis abgelegt ist.

Pro Team ist genau eine Abgabe vorgesehen. Vermerken Sie daher die Namen der Mitglieder Ihres Teams in der
Textdatei `answers.txt`.

Nach der Lösung der nachfolgenden Aufgaben erzeugen Sie bitte aus dem Verzeichnis `ueb2` ein ZIP-Archiv. Dieses
ZIP-Archiv laden Sie im Moodle-Raum als Lösung hoch.

Für Fragen können Sie das Diskussionsforum im Moodle-Raum nutzen.

## Anlegen einer virtuellen Python-Umgebung

Legen Sie im Verzeichnis `2025/ueb2` zuerst eine virtuelle Python-Umgebung, z. B. mit dem Namen `venv`, an.

Starten Sie die virtuelle Python-Umgebung und installieren Sie in dieser Umgebung mit dem Paketmanager `pip`
die folgenden Programmbibliotheken:

- requests
- pysolr
- matplotlib
- bs4
- regex
- lxml

Welche Ausgabe liefert der Befehl `pip list` nach der Installation der o.g. Programmbibliotheken? Kopieren Sie die
gesamte Ausgabe des Befehls in die Textdatei `answers.txt`.

## Anlegen eines neuen Solr-Cores `ueb2`

Starten Sie Ihren Solr-Server, indem Sie den bereits angelegten Docker Container `solr-server` starten.
Sie können dazu entweder die graphische Benutzungsschnittstelle von Docker Desktop verwenden oder
den `solr-server`-Container über die Eingabe eines Befehls im Terminal starten.

Vergewissern Sie sich, dass der Solr-Server (im Docker Container) erfolgreich gestartet wurde. Sie können dazu die Weboberfläche von Solr in Ihrem Browser unter der URL http://localhost:8983/solr aufrufen.

Legen Sie nun einen neuen Solr-Core mit dem Namen `ueb2` im Solr-Server an, indem Sie folgenden Befehl ausführen (auf dem Host-System, nicht im Docker Container `solr-server`):

```bash
docker exec -it solr-server solr create_core -c ueb2
```

## Anlegen eines Index-Schemas im neu angelegten Solr-Core `ueb2`

Nun betrachten wir das Python-Script `01_create_solr_schema.py`. Es ermöglicht die Konfiguration eines Indexschemas
im neu angelegten Solr-Core `ueb2`.

Zuerst soll der Modus _Auto-Create-Fields_ mit der Methode `disable_auto_create_fields_mode` im Solr-Core `ueb2`
deaktiviert werden, so dass wir das Indexschema des Solr-Cores `ueb2` selbst definieren können.

Durch die Deaktivierung des Modus _Auto-Create-Fields_ wird verhindert, dass Solr automatisch neue Indexfelder anlegt,
wenn ein Indexfeld in einem Eingabedokument referenziert wird, aber das Indexfeld im Indexschema noch nicht existiert.
Die Deaktivierung dieses Modus ist notwendig, damit wir das Indexschema exakt gemäß der nachfolgenden Spezifikation
anlegen können. Die Deaktivierung des Modus _Auto-Create-Fields_ erfolgt über die Solr Config API. Hierbei wird die
Solr-Konfigurationsdatei `solrconfig.xml` des Solr-Cores `ueb2` nicht direkt verändert. Stattdessen werden die
vorgenommenen Änderungen in einer separaten Datei mit dem Namen `configoverlay.json` gespeichert, die ebenfalls im
Verzeichnis `conf` des Solr-Cores `ueb2` gespeichert wird.

Jedes Solr-Indexschema ist durch eine Menge von **Feldtypen** und **Indexfeldern** gekennzeichnet.
Jedes Indexfeld besitzt (genau) einen Feldtyp. Ein Feldtyp kann von mehreren Indexfeldern verwendet werden.
Letztendlich bestimmt ein Feldtyp:

1. die Verarbeitung des Inhalts eines Indexfelds des entsprechenden Feldtyps während der Analysephase im Rahmen der Indexierung
2. die Verarbeitung der Suchanfrage während der Anfrageverarbeitung, wenn in einem Indexfeld des
   entsprechenden Feldtyps gesucht wird

 Ein Indexfeld besitzt neben dem Feldtyp einen eindeutigen Namen und es legt fest, ob der Feldinhalt indexiert bzw.
 der ursprüngliche Inhalt (als Eingabe der Indexierung) gespeichert werden soll, so dass er später noch auf Basis der
 Lucene-Indexdateien ausgegeben werden kann (ohne Zugriff auf die Ausgangsdaten).

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

Hinweis: Die Stellen im Python-Script `01_create_solr_schema.py`, die Sie bearbeiten sollen, sind mit dem Kommentar `TODO` gekennzeichnet.

1. Erweitern Sie die Methode `create_index_fields` im Python-Script `01_create_solr_schema.py` um eine Prüfung,
so dass bereits im Schema vorhandene Indexfelder nicht erneut angelegt werden.

Sie können sich hierzu an der Implementierung der Methode `create_index_field_types` orientieren. Der Versuch, ein
bereits existierendes Indexfeld erneut anzulegen, soll keinen Fehler verursachen (normalerweise würde Solr in diesem
Fall den Fehlercode 400 zurückliefern). Konsultieren Sie ggf. hierzu die Solr Schema API Dokumentation, die Sie unter
https://solr.apache.org/guide/solr/latest/indexing-guide/schema-api.html finden.

Sie können den Fehler provozieren, indem Sie das Python-Script `01_create_solr_schema.py` zweimal hintereinander ausführen.
Dann wird beim zweiten Durchlauf ein Fehler auftreten, da bei diesem Durchlauf das Indexfeld `title_stemmed` bereits
im Schema des Solr-Cores `ueb2` existiert und daher nicht neu angelegt werden kann. Nach Ihrer Änderung soll das
Script auch beim zweiten Durchlauf erfolgreich ausgeführt werden können, ohne dass ein Fehler auftritt (die Erzeugung
von bereits im Schema vorhandenen Indexfeldern soll unterbunden werden).

2. Erweitern Sie das Python-Script `01_create_solr_schema.py`, so dass im Solr-Core `ueb2`
ein Indexschema gemäß der nachfolgenden Tabelle angelegt wird:

| Feldname                   | Feldtyp | Unterstützt Suche? | Erlaubt Ausgabe? | Mehrwertig? | Pflichtfeld / "required"? |
|----------------------------|---------|--------------------|------------------|-------------|---------------------------|
| `title_stemmed`            | `pg_text_de` | ja | nein | nein | ja |
| `author`                   | `text_general` | ja | nein | nein | nein |
| `author_exact`             | `string` | ja | ja | nein | nein |
| `numOfDownloadsLast30Days` | `pg_pint` | ja | ja | nein | ja |
| `docType`                  | `string` | ja | ja | nein | ja |
| `fulltext`                 | `text_general` | ja | nein | nein | ja |
| `fulltext_stemmed`         | `pg_text_de` | ja | nein | nein | ja |
| `language`                 | `string` | ja | ja | nein | ja |
| `subjectHeadings`          | `text_general` | ja | ja | ja | nein |


3. Führen Sie das Programm `01_create_solr_schema.py` aus und prüfen Sie den Erfolg Ihrer Umsetzung, indem Sie

   3.1 die Existenz der Datei `configoverlay.json` im Verzeichnis `conf` des Solr-Cores `ueb2` kontrollieren. In dieser Datei sollten die vorgenommenen Konfigurationsänderungen gespeichert worden sein. Außerdem können Sie durch Aufruf der Methode `print_overlays` im Python-Script `01_create_solr_schema.py` den Inhalt der Datei `configoverlay.json` auf der Kommandozeile ausgeben lassen.

   3.2 den neu angelegten Solr-Core `ueb2` in der Solr-Administration auswählen (die Auswahl erfolgt über den Core Selector) und die Funktion _Schema_ (in der linken Spalte, vorletzter Eintrag) aufrufen. Dort sollten alle zuvor angelegten Indexfelder angezeigt werden.

4. Wie viele Indexfelder besitzt das Indexschema des neu angelegten Cores `ueb02` insgesamt? Tragen Sie diese Anzahl in die Datei `answers.txt` ein.

## Untersuchung des Verhaltens des neuen Indexfeldtyps `pg_text_de`

Schauen Sie sich in Vorbereitung auf diesen Aufgabenteil das Video zur _Solr Analysis_ im YouTube-Kanal zur Vorlesung an:
https://youtu.be/5938k7UeCcg

Testen Sie nun, wie sich der neu angelegte Feldtyp `pg_text_de` bei der Indexierung und bei der Suchanfrageverarbeitung
verhält, indem Sie in der Solr-Webadministrationsoberfläche den Menüpunkt *Analysis* innerhalb des
Administrationsbereichs für den Solr-Core `ueb2` öffnen:

[Beispiel-Link für das Matching von **Türme** und **Turm**](http://localhost:8983/solr/#/ueb2/analysis?analysis.fieldvalue=t%C3%BCrme&analysis.query=turm&analysis.fieldtype=pg_text_de&verbose_output=1)

Prüfen Sie, in welchen Fällen der Stemmer gut funktioniert und in welchen Fällen der Stemmer an seine Grenzen stößt.
Beschreiben Sie Ihre Beobachtungen kurz und stichpunktartig in der Datei `answers.txt`.

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
ermitteln. Hierzu führen wir eine passende Suchanfrage im Project Gutenberg aus und iterieren anschließend über die
Suchergebnisse. Für jedes Suchergebnis speichern wir die ID des zugehörigen E-Books in der Datei `gutenberg_ids.txt`.

Das Python-Script `02_fetch_ids` erfüllt diese Aufgabe. Führen Sie das Script aus, um die Datei `gutenberg_ids.txt`
zu erzeugen.

Anschließend können wir die Metadaten und Volltexte der E-Books auf Basis der in der Datei `gutenberg_ids.txt`
gespeicherten E-Book-IDs herunterladen. Hierzu steht das Python-Script `03_fetch_files.py` zur Verfügung. Führen Sie
dieses Script aus, um die Metadaten und Volltexte der deutschsprachigen E-Books herunterzuladen.

Ermitteln Sie die Anzahl der Dateien im Verzeichnis `pg-metadata` sowie im Verzeichnis `pg-fulltexts` und tragen Sie
die Werte in die Datei `answers.txt` ein. Was fällt Ihnen hierbei auf? Wie erklären Sie sich dieses Ergebnis? Notieren
Sie Ihre Überlegungen in der Datei `answers.txt`.

### Statistische Analyse der Volltexte und Ausgabe der 10 längsten Terme

Mit dem Python-Script `04_text_analysis.py` können Sie eine einfache statistische Analyse der heruntergeladenen
Volltexte durchführen. Führen Sie das Script aus, um die Analyse zu starten. Betrachten Sie die Ausgabe des Scripts
und die beiden generierten PNG-Dateien. Beschreiben Sie Ihre Beobachtungen kurz und stichpunktartig in der Datei
`answers.txt`.

Erweitern Sie das Script `04_text_analysis.py`, so dass zusätzlich die zehn längsten Terme (gemessen an der Anzahl der
Zeichen) in den Volltexten ausgegeben werden. Führen Sie das erweiterte Script aus und tragen Sie die zehn längsten
Terme in die Datei `answers.txt` ein. Für die Bestimmung der zehn längsten Terme können Sie einen Min-Heap verwenden.
Hierfür steht die Methode `update_longest_terms` im Script `04_text_analysis.py` zur Verfügung.

### Indexierung der E-Books in den Solr-Core `ueb2`

Das Programm `05_solr_indexer.py` soll nun genutzt werden, um die heruntergeladene E-Book-Dokumentkollektion
(d.h. Metadaten und Volltexte aus Project Gutenberg) zu indexieren. Dazu müssen Sie das Programm noch an den mit
`TODO` gekennzeichneten Stellen vervollständigen. Anschließend starten Sie das Programm zur Indexierung der
Dokumentkollektion.

Kontrollieren Sie schließlich den Erfolg der Indexierung, indem Sie in der Solr-Webanwendung die Suchanfrage `*:*`
ausführen und den Wert von `numFound` prüfen. Geben Sie die Anzahl der gefundenen Dokumente in die Datei `answers.txt`
an. Vergleichen Sie die Anzahl der indexierten Dokumente mit der Anzahl der heruntergeladenen Metadatendateien.
Was beobachten Sie? Notieren Sie Ihre Beobachtungen in der Datei `answers.txt`.

### Umsetzung einer einfachen Feldsuche

Das Programm `06_solr_searcher.py` ermöglicht das Absetzen von einfachen Suchanfragen und die Anzeige der Suchergebnisse
in einem Top-10-Ranking auf der Kommandozeile. Die eingegebene Suchanfrage wird hierbei standardmäßig im Indexfeld
`title_stemmed` gesucht.

Für jedes E-Book im Top-10-Ranking wird die E-Book-ID, der Titel und der von Lucene/Solr berechnete Score-Wert
ausgegeben.

Erweitern Sie die Ausgabe der Suchergebnisse an der mit `TODO` gekennzeichneten Stelle, so dass neben Dokument-ID,
Titel und Score-Wert auch folgende Felder ausgegeben werden:

* Namen aller AutorInnen
* Anzahl der Downloads des E-Books in den letzten 30 Tagen
* Dokumentsprache(n)
* Library of Congress Subject Headings

Der Benutzer kann neben dem Suchterm auch ein Indexfeld angeben, in dem der Suchterm gesucht werden soll. Das Indexfeld
wird hierbei über den Namen referenziert und mit Doppelpunkt vom Suchterm getrennt. Mehrere Suchterme in einer Anfrage
können mit einer Klammer gruppiert werden.

Beispielanfragen:

- Suche nach Autor _Schiller_: `author:Schiller`
- Suche nach Titel _Abenteuer Wunderland_: `title:(Abenteuer Wunderland)`
- Phrasensuche nach Titel _Abenteuer im Wunderland_: `title:"Abenteuer im Wunderland"`

Vergleichen Sie die Volltextsuche im Feld `fulltext_stemmed` (mit Stemming) und `fulltext` (ohne Stemming), in dem Sie
unterschiedliche Formen eines Terms (z.B. Singular / Plural) für die Suche verwenden. Beschreiben Sie Ihre Beobachtungen
kurz und stichpunktartig in der Datei `answers.txt`.

Betrachten Sie nochmals das Ergebnis der Beispielsuchanfrage `title:(Abenteuer Wunderland)`. Was stellen Sie fest?
Wie kann man sicherstellen, dass beide Suchterme tatsächlich im Titel des E-Books vorkommen müssen, damit ein E-Book
als Treffer zurückgeliefert wird? Notieren Sie Ihre Überlegungen in der Datei `answers.txt`.

### Implementierung einer feldübergreifenden Suche mittels `DisMax Query Parser`

Für das gleichzeitige Durchsuchen mehrerer Indexfelder bietet sich die Verwendung des _DisMax Query Parsers_ bzw.
der erweiterten Version _Extended DisMax Query Parser_ an.

Informieren Sie sich über die grundlegende Funktionsweise des _DisMax Query Parser_
unter [https://solr.apache.org/guide/solr/9_10/query-guide/dismax-query-parser.html] sowie des
_Extended DisMax Query Parser_ unter [https://solr.apache.org/guide/solr/9_10/query-guide/edismax-query-parser.html].

Erweitern Sie nun das Programm `06_solr_searcher.py`, so dass eine Suche über mehrere
Indexfelder mittels _DisMax Query Parser_ ermöglicht wird. Hierzu müssen Sie die Einstellungen in `params` anpassen.
Die Indexfelder sollen hierbei unterschiedlich bei der Suche gewichtet werden:

- ein Titeltreffer mit Faktor 8
- ein Autortreffer mit Faktor 4
- ein Volltexttreffer (ohne Stemming) mit Faktor 2
- ein Volltexttreffer (mit Stemming) soll nicht besonders gewichtet werden (Standardgewichtung 1)

Zusätzlich soll ein Boosting auf Basis der Downloadanzahl (im Indexfeld `numOfDownloadsLast30Days`) erfolgen. Dazu soll
der Logarithmus (zur Basis 10) der Downloadanzahl als Boosting-Faktor einbezogen werden (`bf` Parameter mit
Funktionsargument `log`).

### Implementierung einer Autovervollständigung mittels `Terms Component`

Unter Autovervollständigung (_Autocompletion_) versteht man die kontextsensitive Erweiterung der Benutzereingabe durch
Terme, die im Dictionary des invertierten Index existieren. Die vorgeschlagenen Terme werden dabei meist nicht
alphabetisch, sondern absteigend nach ihrer Dokumenthäufigkeit (_document frequency_) vorgeschlagen.

Beschäftigen Sie sich dazu mit der Solr-Komponente _Terms Component_. Einzelheiten sind unter
[https://solr.apache.org/guide/solr/9_10/query-guide/terms-component.html] beschrieben.

Erweitern Sie das Programm `07_autocompletion.py`, so dass die Autovervollständigung auf Basis des Indexfeld `title`
mittels der _Terms Component_ realisiert wird. Dazu müssen Sie die Anfrageparameter an der mit `TODO` markierten
Stelle entsprechend anpassen. Es sollen nach jedem Tastendruck maximal 10 Vervollständigungsvorschläge ausgegeben
werden, die nach Dokumenthäufigkeit absteigend sortiert sind.

Starten Sie nach der Fertigstellung das Programm und testen Sie die Autovervollständigung für verschiedene Suchterme.
Sie können das Programm durch die Enter-Taste beenden.

Kopieren Sie in die Datei `answers.txt` die Ausgabe des Programms `07_autocompletion.py` für die Eingabe `fran`.

### Löschen von wenig nachgefragten E-Books

Es sollen nun noch alle E-Books aus dem Index gelöscht werden, die in den letzten 30 Tagen nicht mehr als 5 Downloads
hatten.

Hierzu müssen Sie das Programm `08_delete_ebooks.py` an den mit `TODO` gekennzeichneten Stellen vervollständigen.

Führen Sie anschließend das Programm aus, um die wenig nachgefragten E-Books aus dem Index zu löschen. Wie groß ist
die Anzahl der Dokumente im Index vor und nach der Löschung der wenig nachgefragten E-Books? Tragen Sie beide Werte
in die Datei `answers.txt` ein.
