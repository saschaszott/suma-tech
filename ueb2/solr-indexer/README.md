# Indexierung von Dokumenten mittels SolrJ

Ausführung der Klasse `SolrIndexer` auf der Kommandozeile mittels

````
$ ./mvnw clean compile exec:java
````

Alternativ importieren Sie das Maven-Projekt in Netbeans 
und führen die Klasse `SolrIndexer` aus.

## Aufgaben

#### Vorbereitung

Löschen Sie zuerst alle Dokumente aus dem neu angelegten Solr-Core `solr-ueb2`.

Das können Sie z.B. durch das Aktivieren der Methode 

````
solrIndexer.deleteAllDocs();
````

in der `main`-Methode der Klasse `SolrIndexer` und einmaliger Programmausführung erreichen.

Prüfen Sie anschließend über die Solr-Administrationsoberfläche, dass die 
_catch all_ Anfrage `q=*:*` keine Treffer mehr zurückliefert.


#### Schemaerweiterung

Erweitern Sie das Solr-Indexschema des Solr-Core `solr-ueb2`, indem
Sie die Schemakonfigurationsdatei `schema.xml` anpassen.

Das Indexschema soll wie folgt erweitert werden:

- Legen Sie einen neuen Feldtyp für die Speicherung von Ganzzahlen an:
```
<fieldType name="pint" class="solr.IntPointField" docValues="true"/>
```
- Fügen Sie folgende Indexfelder zum Solr-Schema hinzu:

| Feldname | Feldtyp | Suche im Inhalts | Ausgabe des Inhalt | Mehrwertig |
|---|---|---|---|---|
| title | text_general | ja | ja | nein |
| title_stemmed | text_de | ja | nein | nein |
| author | text_general | ja | nein | nein |
| author_exact | string | ja | ja | nein |
| authorWikipediaURL | string | nein | ja | nein |
| numOfDownloadsLast30Days | pint | ja | ja | nein |
| docType | string | ja | ja | nein |
| fulltext | text_general | ja | nein | nein |
| fulltext_stemmed | text_de | ja | nein | nein |
| languages | string | ja | ja | ja |
| subjectHeadings | text_general | ja | ja | ja |

Die Wohlgeformtheit der XML-Datei `schema.xml` können Sie 
nach den Anpassungen z.B. mittels `xmllint` prüfen:

```
cd solr-6.6.0
xmllint --noout server/solr/solr-ueb2/conf/schema.xml 
```

Im Fehlerfall gibt `xmllint` die betroffene Zeilen- und Spaltennummer
aus.

Starten Sie den Solr-Server neu, um die Schemaänderung zu übernehmen. Rufen Sie die Solr-Administrationsoberfläche im Webbrowser auf
und prüfen Sie, dass dort keine Fehlermeldung angezeigt wird.


#### Indexierung der Gutenberg-Dokumentkollektion mit der Klasse `SolrIndexerComplete`

Aktualisieren Sie ihre lokale Git-Arbeitskopie mittels

```
git pull
```

Die Klasse `GutenbergRDFParser` ist für das Parsen der
Gutenberg-Metadatendateien im RDF/XML-Format zuständig. Dazu wird
die zu verarbeitende Datei als Argument der Methode `parse`
übergeben. Die Methode gibt ein mit Daten gefülltes Objekt
der Klasse `GutenbergDoc` zurück. Dieses können Sie schließlich nutzen,
um ein `SolrInputDocument` zu erzeugen und im Solr-Index abzuspeichern.

Indexieren Sie nun mittels der Klasse `SolrIndexerComplete` die 
Gutenberg-Dokumentkollektion. Die Rohdaten (Metadaten und Volltexte)
sind im Moodle-System abgelegt. Beachten Sie dazu bitte die 
Hinweise im Kommentar der Klasse. Ergänzen Sie die im 
Quellcode markierten Stellen und führen Sie die `main`-Methode
der Klasse aus.

Wie viele Dokumente sind nach der Indexierung im Solr-Index abgelegt?
Stimmt die Anzahl der Dokumente im Index mit der Anzahl der XML-Dateien
aus dem Dateisystem überein?

#### Erweiterung der Klasse `SolrSearcher` (Feldsuche)
 
Im Github-Arbeitsverzeichnis `suma-tech/ueb2/solr-indexer` ist
eine angepasste Version der Klasse `SolrSearcher` abgelegt, die
den Solr-Core `solr-ueb2` für die Suche verwendet. 

Starten Sie die Klasse `SolrSearcher` und führen Sie die Suchanfrage
`Calpurnia` aus. Was stellen Sie fest (Fehlermeldung prüfen)?

Offenbar verwendet die Suche standardmäßig das Indexfeld `_text_`.
Dieses Feld existiert in unserem Solr-Schema aber nicht. 
Schauen Sie in die Konfigurationsdatei `solrconfig.xml`. 
Wo wird das Standardsuchfeld (_default search field_) `_text_`
definiert?

Ändern Sie die Klasse `SolrSearcher`, so dass standardmäßig 
im Feld `fulltext_stemmed` gesucht wird. Suchen Sie nun 
erneut nach `Calpurnia`. Wie viele Treffer erzielt die Suchanfrage?

Erweitern Sie die Ausgabe der Suchergebnisse, so dass neben der
Dokument-ID auch Titel, Autor, URL der Wikipediaseite des Autors,
Anzahl der Downloads in den letzten 30 Tagen, die Dokumentsprache(n),
die Subject Headings sowie der von Lucene berechnete Score-Wert 
ausgegeben werden.

Erweitern Sie die unterstützte Anfragesprache, so dass
der Benutzer neben der Suchanfrage auch ein Indexfeld angeben
kann, in dem gesucht werden soll.

Das Indexfeld wird über den Namen referenziert und mit
Doppelpunkt vom Suchterm getrennt. Mehrere Suchterme
können mit einer Klammer gruppiert werden.

Beispiel:

- Suche nach Autor _Schiller_: `author:Schiller`
- Suche nach Titel _Abenteuer Wunderland_: `title:(Abenteuer Wunderland)`
- Phrasensuche nach Titel _Abenteuer im Wunderland_: `title:"Abenteuer im Wunderland"`

Vergleichen Sie die Volltextsuche im Feld `fulltext_stemmed` (mit Stemming)
und `fulltext` (ohne Stemming), in dem Sie unterschiedliche Formen
eines Terms (z.B. Singular / Plural) für die Suche verwenden.

#### Implementierung einer feldübergreifenden Suche mittels `DisMax Query Parser`

Für das gleichzeitige Durchsuchen mehrerer Indexfelder
bietet sich die Verwendung des _DisMax Query Parsers_ bzw.
der erweiterten Version _Extended DisMax Query Parser_ an.

Informieren Sie sich über die grundlegene Funktionsweise des _DisMax Query Parser_
unter [https://lucene.apache.org/solr/guide/6_6/the-dismax-query-parser.html].

Erweitern Sie die Klasse `SolrSearcher`, so dass eine Suche
über alle Indexfelder ermöglicht wird. Die Indexfelder sollen
unterschiedlich gewichtet werden: 

- ein Titeltreffer mit Faktor 8
- ein Autortreffer mit Faktor 4
- ein Volltexttreffer (ohne Stemming) mit Faktor 2
- ein Volltexttreffer (mit Stemming )soll nicht besonders gewichtet werden
 
Zusätzlich soll ein Boosting auf Basis der Downloadanzahl erfolgen.
Dazu soll der Logarithmus (zur Basis 10) der Downloadanzahl als 
Boosting-Faktor einbezogen werden (`bf` Parameter mit Funktionsargument `log`).

Ergänzen Sie dazu die Methode `runQueryOnMultipleFields`
und geben Sie im Programm das Top-10-Ranking auf Basis des
`Standard Query Parser` (der standardmäßig nur das Indexfeld
`fulltext_stemmed` durchsucht) und des `DisMax Query Parser` aus.


#### Implementierung einer Autovervollständigung mittels `Terms Component`

Unter Autovervollständigung (_Autocompletion_) versteht man
die kontextsensitive Erweiterung der Benutzereingabe durch 
Terme, die im Dictionary des invertierten Index existieren. 

Die vorgeschlagenen Terme werden dabei meist nicht
alphabetisch, sondern absteigend nach ihrer 
Dokumenthäufigkeit (_document frequency_) vorgeschlagen.

Beschäftigen Sie sich dazu mit der Solr-Komponente _Terms Component_.
Einzelheiten sind unter [https://lucene.apache.org/solr/guide/6_6/the-terms-component.html]
beschrieben.

Erweitern Sie die im Github-Repository abgelegte 
Klasse `Autocompletion`, um eine einfache Autovervollständigung
auf Basis der _Terms Component_ zu implementieren. In der Klasse
ist bereits in der Methode `printStats` die Arbeit mit dieser
Komponente demonstiert.

Die Implementierung wird hier bewusst einfach gehalten. Der
Benutzer gibt ein Zeichen ein und drückt die Eingabetaste.
Es werden daraufhin 15 Vervollständigungsvorschläge angezeigt. 

Anschließend kann der Benutzer ein weiteres Zeichen eingeben
und erneut die Eingabetaste drücken, worauf eine verfeinerte 
Vorschlagsliste erscheint. 

Wird ein Leerzeichen eingegeben, so wird das Programm beendet
(in der Praxis würde die tatsächliche Dokumentsuche starten).

Starten Sie die Klasse `Autocompletion` und testen Sie die
Autovervollständigung für verschiedene Suchterme.

#### Löschen von Dokumenten

Es sollen nun alle E-Books aus dem Index gelöscht werden, 
die in den letzten 30 Tagen nicht mehr als 5 Downloads hatten.

Implementieren Sie dazu in der Klasse `SolrIndexerComplete` eine
Methode `deleteDocsByNumOfDownloads`, die als Argument 
einen Schwellwert `maxNumOfDownloads` vom Typ `int` enthält.
Die Methode soll alle Dokumente aus dem Index löschen, die
nicht mehr als `maxNumOfDownloads` viele Downloads in den 
letzten 30 Tagen hatten.

Führen Sie nun die `deleteDocsByNumOfDownloads(5)` aus, so
dass die wenig nachgefragten E-Books aus dem Index gelöscht werden.

Wie viele Dokumente sind im verbleibenden Index enthalten?
