# Indexierung von E-Books aus dem Gutenberg-Projekt mittels SolrJ

Die Übung kann in Zweierteams bearbeitet werden. Bitte beantworten Sie die
Fragen in der Textdatei `answers.txt`, die ebenfalls im Projektverzeichnis
abgelegt ist.

Bitte reichen Sie das gesamte Projekt `ueb1` als ein ZIP-Archiv als Lösung
ein (Upload im Moodle-Raum zur Vorlesung).

Für Fragen nutzen Sie bitte das Moodle-Diskussionsforum zur Vorlesung.

## Anlegen eines neuen Solr-Cores

Legen Sie einen neuen Solr-Core mit dem Namen `ueb1` an.
 
Kopieren Sie dazu die beiden Konfigurationsdateien `solrconfig.xml` und
`schema.xml` aus dem bereits vorhandenen Solr-Core `my1stcore` in das 
Konfigurationsverzeichnis `conf` des neu angelegten Solr-Cores `ueb1`.
 
Starten Sie anschließend den Solr-Server neu und prüfen Sie über
die Solr-Webadministrationsoberfläche, dass der neu angelegte Solr-Core
mit den Konfigurationsänderungen erfolgreich geladen werden konnte.
 
## Definition des Indexschemas

Erweitern Sie nun die Schemadefinitionsdatei `schema.xml` des neu angelegten
Solr-Cores `ueb1`.

Definieren Sie zwei weitere Feldtypen:
* Feldtyp `pint` für die Speicherung von Ganzzahlen:
  ```
  <fieldType name="pint" class="solr.IntPointField" docValues="true"/>
  ```

* Feldtyp `text_de` zur Indexierung von textuellem Inhalt:
   ```
  <fieldType name="text_de" class="solr.TextField" positionIncrementGap="100">
    <analyzer>
      <tokenizer class="solr.StandardTokenizerFactory"/>
      <filter class="solr.LowerCaseFilterFactory"/>
      <filter class="solr.GermanNormalizationFilterFactory"/>
      <filter class="solr.GermanLightStemFilterFactory"/>
    </analyzer>
  </fieldType>
  ```

Erweitern Sie nun das Indexschema um folgende Indexfelder:

| Feldname | Feldtyp | Unterstützt Suche? | Erlaubt Ausgabe? | Mehrwertig? |
|----------|---------|--------------------|------------------|-------------|
| `title_stemmed` | `text_de` | ja | nein | nein |
| `author` | `text_general` | ja | nein | nein |
| `author_exact` | `string` | ja | ja | nein |
| `authorWikipediaURL` | `string` | nein | ja | nein |
| `numOfDownloadsLast30Days` | `pint` | ja | ja | nein |
| `docType` | `string` | ja | ja | nein |
| `fulltext` | `text_general` | ja | nein | nein |
| `fulltext_stemmed` | `text_de` | ja | nein | nein |
| `languages` | `string` | ja | ja | ja |
| `subjectHeadings` | `text_general` | ja | ja | ja | nein |

Starten Sie anschließend den Solr-Server neu, um die Änderungen an der 
Indexschemakonfiguration `schema.xml` zu übernehmen. Prüfen Sie, dass die
Änderungen erfolgreich übernommen wurden, indem Sie die Solr-Webadministrationoberfläche
aufrufen (dort dürfen keine Fehlermeldungen erscheinen).

## Verhalten des neuen Indexfeldtyps `text_de`

Testen Sie, wie sich der Feldtyp `text_de` während Indexierung und Suche verhält,
indem Sie in der Solr-Webadministrationsoberfläche den Menüpunkt *Analysis*
innerhalb des Administrationsbereichs für den Solr-Core `ueb1` öffnen: 

[Beispiel-Link für das Matching von **Türme** und **Turm**](http://localhost:8983/solr/#/ueb1/analysis?analysis.fieldvalue=t%C3%BCrme&analysis.query=turm&analysis.fieldtype=text_de&verbose_output=1)

Prüfen Sie, in welchen Fällen der Stemmer gut funktioniert und in welchen
Fällen der Stemmer an seine Grenzen stößt.

Hierzu können Sie auch einen Blick in den Quellcode der Java-Klassen

* des [*German Normalization Filter*](https://github.com/apache/lucene-solr/blob/master/lucene/analysis/common/src/java/org/apache/lucene/analysis/de/GermanNormalizationFilter.java).
* des [*German Light Stemmer*](https://github.com/apache/lucene-solr/blob/master/lucene/analysis/common/src/java/org/apache/lucene/analysis/de/GermanLightStemmer.java)
 
werfen.

## Indexierung der Gutenberg-Dokumentkollektion mittels Java

In dieser Aufgabe sollen Sie die Klasse `de.suma.SolrIndexer` an den mit 
**`TODO`** gekennzeichneten Stellen vervollständigen.

Die Klasse `SolrIndexer` können Sie in Netbeans ausführen, indem Sie das Projekt `ueb1`
importieren. Alternativ können Sie die Klasse auf der Kommandozeile mittels des
Befehls

```
$ ./mvnw clean compile exec:java
```

starten.

Die Klasse `de.suma.GutenbergRDFParser` ist für das Parsen der
Gutenberg-Metadatendateien im RDF/XML-Format zuständig. Dazu wird
die zu verarbeitende Datei als Argument der Methode `parse`
übergeben. Die Methode gibt ein mit Daten gefülltes Objekt
der Klasse `GutenbergDoc` zurück. Dieses können Sie schließlich nutzen,
um ein `SolrInputDocument` zu erzeugen und im Solr-Index abzuspeichern.

Indexieren Sie nun mittels der Klasse `SolrIndexer` die 
Gutenberg-Dokumentkollektion. Die Rohdaten (Metadaten und Volltexte)
sind im Moodle-Kursraum abgelegt. Beachten Sie dazu bitte auch die 
Hinweise im Kopfkommentar der Klasse. 

Ergänzen Sie die im Quellcode mit **`TODO`** markierten Stellen und 
führen Sie dann die Methode `main` der Klasse `SolrIndexer` aus, 
um die Dokumentkollektion zu indexieren.

Wie viele Dokumente sind nach der Indexierung im Solr-Index des Core `ueb1`
abgelegt? Stimmt die Anzahl der Dokumente im Index mit der Anzahl der 
XML-Dateien im Verzeichnis `gutenberg/selection` überein?

## Erweiterung der Klasse `SolrSearcher` (Feldsuche)
 
Die Klasse `de.suma.SolrSearcher` kennen Sie bereits aus der Vorlesung. 
Sie ermöglicht das Absetzen von Suchanfragen und die Anzeige der Suchergebnisse
auf der Kommandozeile. Die Klasse wurde erweitert und führt Suchanfragen
auf dem neu angelegten Solr-Core `ueb1` aus.

Starten Sie nun die Klasse `SolrSearcher` und führen Sie die Suchanfrage
`Calpurnia` aus. Was stellen Sie fest (Fehlermeldung prüfen)?

Offenbar verwendet die Suche standardmäßig das Indexfeld `_text_`.
Dieses Feld existiert im Indexschema des Solr-Cores `ueb1` aber nicht. 
Schauen Sie in die Konfigurationsdatei `solrconfig.xml`. Wo wird das 
Standardsuchfeld (_default search field_) `_text_` definiert?

Ändern Sie die Klasse `SolrSearcher`, so dass standardmäßig 
im Feld `fulltext_stemmed` gesucht wird. Suchen Sie nun 
erneut nach `Calpurnia`. Wie viele Treffer erzielt die Suchanfrage?

Erweitern Sie die Ausgabe der Suchergebnisse, so dass neben der
Dokument-ID auch folgende Felder ausgegeben werden:

* Titel
* Autor
* URL der Wikipediaseite des Autors
* Anzahl der Downloads in den letzten 30 Tagen
* Dokumentsprache(n)
* Subject Headings
* der von Lucene berechnete Score-Wert

Erweitern Sie die unterstützte Anfragesprache, so dass der Benutzer neben 
der Suchanfrage auch ein Indexfeld angeben kann, in dem gesucht werden soll.

Das Indexfeld wird über den Namen referenziert und mit Doppelpunkt vom 
Suchterm getrennt. Mehrere Suchterme können mit einer Klammer gruppiert werden.

Beispiel:

- Suche nach Autor _Schiller_: `author:Schiller`
- Suche nach Titel _Abenteuer Wunderland_: `title:(Abenteuer Wunderland)`
- Phrasensuche nach Titel _Abenteuer im Wunderland_: `title:"Abenteuer im Wunderland"`

Vergleichen Sie die Volltextsuche im Feld `fulltext_stemmed` (mit Stemming)
und `fulltext` (ohne Stemming), in dem Sie unterschiedliche Formen
eines Terms (z.B. Singular / Plural) für die Suche verwenden.

## Implementierung einer feldübergreifenden Suche mittels `DisMax Query Parser`

Für das gleichzeitige Durchsuchen mehrerer Indexfelder bietet sich die 
Verwendung des _DisMax Query Parsers_ bzw. der erweiterten Version 
_Extended DisMax Query Parser_ an.

Informieren Sie sich über die grundlegene Funktionsweise des _DisMax Query Parser_
unter [https://lucene.apache.org/solr/guide/7_3/the-dismax-query-parser.html].

Erweitern Sie die Klasse `SolrSearcher`, so dass eine Suche über alle 
Indexfelder ermöglicht wird. Die Indexfelder sollen hierbei unterschiedlich
bei der Suche gewichtet werden: 

- ein Titeltreffer mit Faktor 8
- ein Autortreffer mit Faktor 4
- ein Volltexttreffer (ohne Stemming) mit Faktor 2
- ein Volltexttreffer (mit Stemming) soll nicht besonders gewichtet werden
 
Zusätzlich soll ein Boosting auf Basis der Downloadanzahl erfolgen.
Dazu soll der Logarithmus (zur Basis 10) der Downloadanzahl als 
Boosting-Faktor einbezogen werden (`bf` Parameter mit Funktionsargument `log`).

Ergänzen Sie dazu die Methode `runQueryOnMultipleFields` in der Klasse `SolrSearcher`
und geben Sie das Top-10-Ranking auf Basis des `Standard Query Parser` (der 
standardmäßig nur das Indexfeld `fulltext_stemmed` durchsucht) und des
`DisMax Query Parser` aus.

## Implementierung einer Autovervollständigung mittels `Terms Component`

Unter Autovervollständigung (_Autocompletion_) versteht man
die kontextsensitive Erweiterung der Benutzereingabe durch 
Terme, die im Dictionary des invertierten Index existieren. 

Die vorgeschlagenen Terme werden dabei meist nicht
alphabetisch, sondern absteigend nach ihrer 
Dokumenthäufigkeit (_document frequency_) vorgeschlagen.

Beschäftigen Sie sich dazu mit der Solr-Komponente _Terms Component_.
Einzelheiten sind unter [https://lucene.apache.org/solr/guide/7_3/the-terms-component.html]
beschrieben.

Erweitern Sie die Klasse `de.suma.Autocompletion`, um eine einfache 
Autovervollständigung auf Basis der _Terms Component_ zu implementieren. 
In der Klasse ist bereits in der Methode `printStats` die Arbeit mit dieser
Komponente demonstiert.

Die Implementierung wird hier bewusst einfach gehalten. Der Benutzer gibt
ein Zeichen ein und drückt die Eingabetaste. Es werden daraufhin maximal 15
Vervollständigungsvorschläge angezeigt. 

Anschließend kann der Benutzer ein weiteres Zeichen eingeben und erneut 
die Eingabetaste drücken, worauf eine verfeinerte Vorschlagsliste erscheint
(erneut mit maximal 15 Vorschlägen). 

Wird ein Leerzeichen eingegeben, so wird das Programm beendet
(in der Praxis würde die tatsächliche Dokumentsuche starten).

Starten Sie die Klasse `Autocompletion` und testen Sie die 
Autovervollständigung für verschiedene Suchterme.

## Löschen von wenig nachgefragten E-Books

Es sollen nun alle E-Books aus dem Index gelöscht werden, 
die in den letzten 30 Tagen nicht mehr als 5 Downloads hatten.

Implementieren Sie dazu in der Klasse `SolrIndexer` eine
Methode `deleteDocsByNumOfDownloads`, die als Argument 
einen Schwellwert `maxNumOfDownloads` vom Typ `int` enthält.
Die Methode soll alle Dokumente aus dem Index löschen, die
nicht mehr als `maxNumOfDownloads` viele Downloads in den 
letzten 30 Tagen hatten.

Führen Sie nun die `deleteDocsByNumOfDownloads(5)` aus, so
dass die wenig nachgefragten E-Books aus dem Index des Solr-Core `ueb1`
gelöscht werden.

Wie viele Dokumente sind im verbleibenden Index enthalten?
