# Projektarbeit Suchmaschinentechnologie Bibliotheksinformatik BIM-25

Veröffentlicht am 13.09.2026

In der Vorlesung haben wir einen Solr-Server in einem Docker Container gestartet. Anschließend haben wir im Solr-Server
einen Solr-Core mit dem Namen `my1stcore` erstellt. Anschließend haben wir ein Solr-Schema, das aus Feldtypen,
Indexfeldern und 2 Copy-Fields besteht, erstellt. Schließlich haben wir die Metadaten aller E-Books aus dem Project
Gutenberg als CSV-Datei heruntergeladen und die Metadaten jedes E-Book als ein Dokument (das aus mehreren Feldern
besteht) im Solr-Core `my1stcore` indexiert.

Wir wollen nun den Index mit Volltexten anreichern, die wir erneut im Project Gutenberg herunterladen. Sie haben bereits
gesehen, dass die Textdatei eines E-Books im Project Gutenberg unter der URL

https://www.gutenberg.org/ebooks/<ebook_id>.txt.utf-8

heruntergeladen werden kann. Beachten Sie hierbei, dass nicht jedes E-Book eine UTF-8 codierte Textdatei besitzt. So ist
z.B. bei dem E-Book mit der ID 11925 nur eine PDF- und eine TeX-Datei, aber keine TXT-Datei vorhanden.

Die Menge der E-Books, die für den Download der Volltexte berücksichtigt werden sollen, soll auf Basis einer Suchanfrage
auf dem Solr-Core `my1stcore` bestimmt werden. Wir wollen nur solche E-Books berücksichtigen, die in deutscher Sprache
vorliegen *und* den Dokumenttyp `Text` besitzen. Formulieren Sie eine passende Solr-Suchanfrage, die die E-Books nach
diesen Kriterien filtert. Die Suchanfrage soll nur die IDs der E-Books (Indexfeld `id`) zurückliefern.

Wir hatten schon gesehen, dass Solr über den `rows`-Parameter die Anzahl der zurückgelieferten Dokumente begrenzt (wenn
Sie `rows` nicht explizit angeben, wird standardmäßig ein Top-10-Ranking berechnet, d.h. `rows` hat den Wert 10). In
unseren bisherigen Beispielen hatten wir `rows` auf den Wert 10 bzw. 20 gesetzt, d.h. Solr liefert in diesen Fällen ein
Top-10 bzw. Top-20-Ranking (Rangliste der Dokumente, absteigend sortiert nach Relevanz) zurück.

Für das Zurückliefern von großen Mengen von Dokumenten ist die Verwendung von `rows` nicht geeignet. Lesen Sie hierzu
die Ausführungen im Solr Reference Guide unter

https://solr.apache.org/guide/solr/latest/query-guide/pagination-of-results.html#performance-problems-with-deep-paging

Daher wollen wir für die Ermittlung der E-Book-IDs mittels Deep Paging den `cursorMark`-Parameter verwenden. Der
Parameter wird im Solr Reference Guide unter

https://solr.apache.org/guide/solr/latest/query-guide/pagination-of-results.html#fetching-a-large-number-of-sorted-results-cursors

erläutert. Pysolr unterstützt die Verwendung des `cursorMark`-Parameters und führt das Deep Paging automatisch durch,
wenn Sie den Parameter `cursorMark` in der Funktion `search()` angeben.

## Aufgaben

1. Entwickeln Sie ein Python-Skript `01_get_ebook_ids.py`, das die IDs aller E-Books ermittelt, die in deutscher Sprache
vorliegen und den Dokumenttyp `Text` besitzen, wobei Sie statt `rows` den `cursorMark`-Parameter verwenden sollen. Die
zurückgelieferten E-Book-IDs sollen in der Textdatei `ebook_ids.txt` abgespeichert werden (eine ID pro Zeile). Geben Sie
die Anzahl der gespeicherten E-Book-IDs auf der Konsole aus.

2. Nun können Sie auf Basis der E-Book-IDs die UTF-8 codierten Volltexte der E-Books herunterladen. Hierzu können Sie
das bereits im Rahmen der letzten Übung verwendete Python-Skript wiederverwenden. Kopieren Sie hierzu das Skript
`harvesting/02_fetch-works.py` und passen Sie das Skript so an, dass es die E-Book-IDs aus der Textdatei `ebook_ids.txt`
einliest und die Volltextdateien der E-Books herunterlädt und in dem Verzeichnis `fulltexts` abspeichert. Die
heruntergeladenen Volltextdateien sollen den Namen `<ebook_id>.txt` erhalten. Geben Sie am Ende des Programms die Anzahl
der erfolgreich heruntergeladenen Volltextdateien sowie die Fehler (TXT-Datei konnte nicht gefunden / heruntergeladen
werden) auf der Konsole aus. Sie können das Programm so adaptieren, dass es eine inkrementelle Arbeitsweise
unterstützt, so dass Sie es während der Ausführung jederzeit unterbrechen können und beim nächsten Start des Programms
nur die noch nicht heruntergeladenen Volltextdateien heruntergeladen werden. Bereits heruntergeladene Volltextdateien
können Sie anhand der Dateinamen im Verzeichnis `fulltexts` erkennen. Sie sollten zusätzlich prüfen, dass die Datei
nicht leer ist.

3. Führen Sie nun, analog zur vorherigen Übung, eine Filterung der heruntergeladenen Volltexte durch. Hierzu können Sie
das Skript `harvesting/03_filter-works.py` wiederverwenden. Kopieren Sie das Skript und passen Sie es so an, dass es die
Volltextdateien aus dem Verzeichnis `fulltexts` einliest und die gefilterten Volltexte (ohne Header und Footer)
ebenfalls im Verzeichnis `fulltexts` unter dem Namenschema `<ebook_id>_filtered.txt` speichert. Ihr Programm soll eine
Fehlermeldung ausgeben, wenn in einer Volltextdatei keine gültigen Start- und End-Marker für Header und Footer gefunden
werden. Gibt es E-Books, in denen keine gültigen Start- und End-Marker für Header und Footer gefunden wurden?

4. Wir wollen nun die heruntergeladenen Volltexte in einem neuen Solr-Core indexieren, um eine Volltextsuche zu
ermöglichen. Dafür erstellen Sie zuerst einen neuen Solr-Core mit dem Namen `pg_fulltexts`. Anschließend erstellen Sie
ein Python-Skript `04_create_schema.py` (analog zur Vorlesung), das in dem neu erstellten Solr-Core ein Solr-Schema
gemäß folgender Spezifikationstabelle erstellt:

| Indexfeld | Feldtyp | Beschreibung | Feldinhalt durchsuchbar | Feldinhalt kann ausgegeben werden | Mehrwertig | Copy-Field | Bemerkung |
|-----------|---------|--------------|-------------------------|-----------------------------------|------------|------------|-----------|
| id | string | eindeutige ID des E-Books aus dem Project Gutenberg | ja | ja | nein | – | |
| title | text_de | Titel des E-Books | ja | ja | nein | – | |
| authors | text_general | Name des bzw. der Autoren des E-Books | ja | ja | ja | – | |
| language | string | Sprache des E-Books | ja | ja | ja | – | |
| subjects | text_general | Thema bzw. Themen des E-Books | ja | ja | ja | – | |
| issued | pdate | Datum der Veröffentlichung des E-Books | ja | ja | nein | – | |
| fulltext | text_de | Volltext des E-Books | ja | **nein** | nein | – | `termVectors=true`
| fulltext_alternative | **text_de_custom** | Volltext des E-Books | ja | **nein** | nein | ja, aus fulltext | |

Der Indexfeldtyp `text_de_custom` sollen im Solr-Schema neu definiert werden. Dieser Feldtyp soll folgende Eigenschaften
besitzen:
- verwendeter Tokenizer: `StandardTokenizerFactory`
- Filterkette, welche die Tokens durchlaufen sollen: `LowerCaseFilterFactory`, `StopFilterFactory` (auf Basis der Datei
`stopwords_de.txt`), `GermanMinimalStemFilterFactory`

Sie können sich auch hier bei der Implementierung an dem Script `_01_create_schema.py` aus dem Verzeichnis
`praxisteile/04` orientieren, das wir in der Vorlesung besprochen haben.

5. Schreiben Sie nun ein Programm `05_index_fulltexts.py`, das die gefilterten Volltexte aus dem Verzeichnis `fulltexts`
einliest. Für jede eingelesene Volltextdatei soll auf Basis der E-Book-ID eine Solr-Query im Solr-Core `my1stcore`
abgesetzt werden, um die folgenden Metadaten des E-Books zu ermitteln:

- Titel
- Liste der Autoren (1..n)
- Liste der Sprachen (1..n)
- Liste der Subjects (1..n)
- Datum der Aufnahme des E-Books in Project Gutenberg

Die zurückgelieferten Metadaten des E-Books sollen zusammen mit dem Volltext des E-Books im Solr-Core `pg_fulltexts`
indexiert werden (auf Basis des zuvor angelegten Solr-Schemas). Sie können Sie auch hier an der Implementierung des
Skripts `_04_index_metadata_pysolr.py` aus dem Verzeichnis `praxisteile/04` orientieren, das wir in der Vorlesung
verwendet haben.

6. Implementieren Sie nun ein Python-Skript `06_searcher.py`, das eine integrierte Metadaten- und Volltextsuche auf dem
Solr-Core `pg_fulltexts` realisiert. Das Programm soll für jede Suchanfrage ein Top-10-Ranking ermitteln und für jeden
Suchtreffer die folgenden Metadaten ausgeben:

- Rang
- ID
- Titel
- Datum der Aufnahme des E-Books in Project Gutenberg
- Autor(en)
- Sprache(n)
- Subject(s)

Hierzu können Sie sich am Script `_05_searcher.py` aus dem Verzeichnis `praxisteile/04` orientieren, das wir in der
Vorlesung verwendet haben.

Informieren Sie sich über die Konfigurationsmöglichkeiten des Edismax-Query-Parsers, die im Solr Reference Guide unter

https://solr.apache.org/guide/solr/latest/query-guide/edismax-query-parser.html

Passen Sie die Konfiguration des Edismax-Query-Parsers im Python-Script `06_searcher.py` so an, dass die folgenden
Indexfelder durchsucht werden (in Klammern sind die Gewichtungen angegeben, die Sie für die Indexfelder verwenden
sollen):

- `title` (Gewichtung 5)
- `authors` (Gewichtung 2)
- `subjects` (Gewichtung 1)
- `fulltext` (Gewichtung 2)
- `fulltext_alternative` (Gewichtung 3)

Außerdem sollen Sie die Konfiguration des Edismax-Query-Parsers so anpassen, dass die Suchterme standardmäßig per `OR`
verknüpft werden. Setzen Sie dazu `q.op=OR`. Stellen Sie weiterhin sicher (über den Parameter `mm`), dass bei der
Eingabe von mehreren Suchtermen mindestens 10% der Suchterme in einem Dokument enthalten sein müssen, damit das
Dokument als Treffer zurückgeliefert wird.

Informieren Sie sich über den Parameter `bf` (Boost Functions) des Dismax-Query-Parsers, der im Solr Reference Guide
unter

https://solr.apache.org/guide/solr/latest/query-guide/dismax-query-parser.html#bf-boost-functions-parameter

beschrieben wird. Passen Sie die Konfiguration des Edismax-Query-Parsers im Python-Script so an, dass die
Relevanzbewertung der Dokumente durch die Funktion

```
recip(ms(NOW,issued),3.16e-11,1,1)
```

beeinflusst wird. Diese Funktion bewirkt, dass neuere E-Books eine höhere Relevanzbewertung erhalten als ältere
E-Books. Die Funktion `recip(ms(NOW,issued),3.16e-11,1,1)` bewirkt, dass die Relevanzbewertung eines E-Books, das vor
einem Jahr veröffentlicht wurde, um den Faktor 2 niedriger ist als die Relevanzbewertung eines E-Books, das heute
veröffentlicht wurde. Die Relevanzbewertung eines E-Books, das vor 10 Jahren veröffentlicht wurde, ist um den Faktor 20
niedriger als die Relevanzbewertung eines E-Books, das heute veröffentlicht wurde. Hierbei ist 3,16 * 10^11 die
gerundete Anzahl der Millisekunden eines Jahres: 365 × 24 × 3600 × 1000 = 31.536.000.000 ≈ 3,16 * 10^10. Somit ergibt
1 / (3,16 * 10^10) ≈ 3,16 * 10^-11.

Führen Sie einige Testsuchanfragen durch und überprüfen Sie, ob die Top-3-Treffer tatsächlich die Suchbegriffe enthalten
und ob die Relevanzbewertung der Treffer plausibel ist. Passen Sie ggf. die Gewichtungen der Indexfelder oder die
Konfiguration des Edismax-Query-Parsers an, um die Qualität des Top-10-Rankings zu verbessern.

## Bonusaufgabe

Diese Aufgabe ist optional.Für eine erfolgreiche Bearbeitung dieser Bonusaufgabe erhalten Sie einen halben Bonuspunkt
in der SumaTech-Klausur.

Solr bietet mit der MoreLikeThis-Funktion (MLT) eine eingebaute Funktion, um zu einem gegebenen Solr-Dokument
automatisch ähnliche Dokumente im Suchindex zu finden.

Informieren Sie sich über die Funktionsweise der MoreLikeThis-Funktion im Solr Reference Guide unter

https://solr.apache.org/guide/solr/latest/query-guide/morelikethis.html

sowie über den MoreLikeThis Request Handler unter

https://solr.apache.org/guide/solr/latest/query-guide/morelikethis.html#morelikethis-request-handler

Die MLT-Funktion inSolr ermittelt dazu automatisch die "interessantesten" Terme eines vorgegebenenDokuments (auf Basis
von Term und Document Frequency) und baut aus den ermittelten Termen intern eine Suchanfrage (Solr Query), die an
den Solr-Core geschickt wird. Die Suchanfrage liefert dann Dokumente zurück, die ähnliche Terme enthalten wie das
Ausgangsdokument.

Für die Ähnlichkeitssuche wollen wir das Indexfeld `fulltext` verwenden. Wir hatten dazu beim Anlegen des Solr-Schemas
das Indexfeld `fulltext` mit der Option `termVectors=true` versehen. Diese Option bewirkt, dass Solr beim Indexieren
eines Dokuments die Terme des Indexfeldes `fulltext` zusammen mit der Term- und Document Frequency abspeichert. Die
Term- und Document Frequency werden von Solr für die MoreLikeThis-Funktion benötigt, um effizient die "interessantesten"
Terme eines Dokuments zu ermitteln.

Implementieren Sie dazu ein Python-Skript `07_mlt.py`, das die MoreLikeThis-Funktion auf Basis des Indexfeldes `fulltext`
implementiert.

In einem ersten Schritt soll das Programm einen neuen Request Handler im Solr-Core `pg_fulltexts` anlegen, der die
MoreLikeThis-Funktionalität bereitstellt. Der Request Handler soll den Namen `/mlt` erhalten und nur dann angelegt
werden, wenn er noch nicht in der Konfiguration des Solr-Cores `pg_fulltexts` vorhanden ist. Die folgenden Parameter
sollen für den Request Handler verwendet werden:

```py
MLT_HANDLER_DEFAULTS = {
    "mlt.fl": "fulltext",
    "mlt.mintf": 2,
    "mlt.mindf": 5,
    "mlt.minwl": 5,
    "mlt.maxdfpct": 25,
    "mlt.interestingTerms": "list",
    # das Ausgangsdokument selbst soll nicht in der Trefferliste erscheinen
    "mlt.match.include": "false",
    "mlt.boost": "true",
}
```

Beachten Sie hierbei, dass pysolr keine direkte Unterstützung für das Anlegen eines Request Handlers bietet. Sie können jedoch die Funktion `requests.post()` aus dem Python-Modul `requests` verwenden, um den Request Handler `/mlt`
anzulegen. Dazu müssen Sie als Payload im POST-Request ein JSON-Objekt mit dem Schlüssel `add-requesthandler`
und den entsprechenden Parametern übergeben. Für die Abfrage der bereits im Solr-Core existierenden Request Handler
müssen Sie ebenfalls einen eigenen HTTP-GET-Request an den Solr-Core absetzen, da pysolr auch diese Funktionalität nicht unterstützt.

Nach dem Anlegen des MLT Request Handlers (sofern erforderlich) soll das Script eine Suchanfrage vom Benutzer abfragen.
Anschließend wird die eingegebene Suchanfrage auf dem Solr-Core `pg_fulltexts` ausgeführt und ein Top-1-Ranking
berechnet.

Das zurückgelieferte Dokument (E-Book) soll auf der Konsole ausgegeben (Metadatenfelder ID, Titel, Autor). Dieses
Dokument wird nun als Ausgangsdokument für die Ähnlichkeitssuche verwendet. Hierzu wird die ID des Dokuments zum Request
Handler `/mlt` geschickt Mittels der MLT-Funktion sollen dann die (bis zu) 3 ähnlichsten E-Books im Solr-Core
`pg_fulltexts` ermittelt und zurückgeliefert (ebenfalls Ausgabe von ID, Titel, Autor).

Außerdem soll das Skript `07_mlt.py` die "interessantesten" Terme des Ausgangsdokuments ausgeben, die Solr intern für die
Suche nach ähnlichen E-Books verwendet hat. Die "interessantesten" Terme werden von Solr im Feld `interestingTerms`
zurückgeliefert.

Geben Sie einige Testsuchanfragen ein und überprüfen Sie, ob die (bis zu 3) zurückgelieferten E-Books tatsächlich
ähnliche Inhalte wie das Top-1-Dokument der ursprünglichen Suchanfrage besitzen. Überprüfen Sie auch, ob die
"interessantesten" Terme des Ausgangsdokuments tatsächlich in den Volltexten der vorgeschlagenen E-Books vorkommen.

Sie können die Parameter des MLT Request Handlers überschreiben, indem Sie die geänderten Parameter in der Suchanfrage
an den Request Handler `/mlt` übergeben. Testen Sie, wie sich die Änderung einzelner MLT-Parameter auf die
zurückgelieferten E-Books auswirkt. Welche Parameter haben den geringsten bzw. größten Einfluss auf die Qualität der
zurückgelieferten E-Books?
