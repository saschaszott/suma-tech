# 3. Übung zum Modul Suchmaschinentechnologie

## Vergleich des Speicherbedarfs für Positional Index und Non-Positional Index 

Diese Übung kann erneut in Teams mit bis zu 3 Personen bearbeitet werden. 
Bitte speichern Sie Ihre Antworten auf die gestellten Fragen in der Textdatei
`answers.txt`, die ebenfalls im Projektverzeichnis abgelegt ist.

Bitte reichen Sie das gesamte Projekt `ueb3` in Form eines ZIP-Archivs als
Lösung ein (Upload des ZIP-Archivs im Moodle-Raum zur Vorlesung). Rufen Sie vor 
dem Erzeugen des ZIP-Archivs den Befehl

```sh
$ ./mvnw clean
```

innerhalb des Verzeichnis `ueb3` auf, so dass keine kompilierten `.class`-Dateien 
im ZIP-Archiv landen.

Anschließend führen Sie folgenden Befehl (im Oberverzeichnis `2023`) aus:

```sh
$ cd ~/sumatech/suma-tech/2023
$ zip -r ueb3.zip ueb3/src ueb3/answers.txt
```

Das dadurch erzeugte ZIP-Archiv `ueb3.zip` laden Sie bitte im Moodle-Raum zur Vorlesung hoch.
Reichen Sie bitte pro Team nur eine Lösung ein und vermerken Sie in der Datei `answers.txt`
die Namen der Team-Mitglieder.

Für Fragen nutzen Sie bitte das Moodle-Diskussionsforum zur Vorlesung.

## Die Klasse `IndexSizeComparison`

In der Übung soll untersucht werden wie viel Speicherplatz für
unterschiedliche Typen des invertierten Index benötigt wird. Außerdem werden Sie
einen kurzen praktischen Einblick in das programmatische Testen von Programmcode,
dem sogenannten _Unit Testing_, unter Verwendung von **JUnit 5** erhalten.

Es werden hierbei vier Indextypen untersucht:

* Positional Index
* Non-Positional Index mit Ein-Term-Dictionary (Uniword-Index)
* Non-Positional Index mit Zwei-Term-Dictionary (Biword-Index)
* Non-Positional Index mit Drei-Term-Dictionary (Triword-Index)

Zur Bestimmung des Speicherbedarfs verwenden wir die bereits aus 
der vorherigen Übung bekannte Dokumentkollektion, diesmal allerdings
beschränkt auf 180 freie E-Books aus dem Gutenberg-Projekt.

Der Speicherbedarf beim **Non-Positional Index** entsteht durch 
das Dictionary, in dem jeder Term der Kollektion gespeichert wird.
Außerdem muss die ID jedes Dokuments in die entsprechenden
Posting-Listen der Terme eingetragen werden, die im Dokument
auftreten. Die Häufigkeit eines Terms in einem Dokument (*term frequency*)
hat hierbei keinen Einfluss auf den Speicherbedarf für den invertierten
Index. Wir nehmen hierbei an, dass für einen Eintrag im 
Dictionary **ein Byte pro Zeichen** benötigt wird (das stimmt nicht
immer, weil z.B. bei der Verwendung der UTF-8 Codierung bis zu 
4 Bytes pro Zeichen benötigt werden können). Für die Speicherung 
einer Dokument-ID wird ein `int` verwendet. Dafür fällt ein
Speicherbedarf von 4 Bytes an. Unberücksichtigt lassen wir hierbei
den erforderlichen Speicherplatz für die Zeiger vom Dictionary 
auf die Postinglisten.

Auch beim **Positional Index** müssen wir den Speicherbedarf
für das Dictionary als auch die Postinglisten betrachten. Der 
Speicherbedarf für das Dictionary entspricht dem Speicherbedarf
für das Dictionary beim Non-Positional Index (mit Ein-Term-Dictionary).
Die Postinglisten enthalten neben den Dokument-IDs (analog zum
Non-Positional Index) auch die entsprechenden Positionen eines
Terms in einem Dokument. Die Häufigkeit eines Terms in einem Dokument
hat bei dieser Indexvariante Einfluss auf den Speicherplatz. 
Kommt ein Term mehrfach innerhalb eines Dokuments vor, so fällt 
bei dieser Indexvariante mehr Speicherplatz als beim Non-Positional Index 
an. Wir nehmen hierbei an, dass die Postinglisten im Positional Index 
folgenden Aufbau haben (hier am Beispiel von zwei Dokumenten mit den IDs `1` und `2`, 
in denen ein Term jeweils an 3 Positionen auftritt):
```
1:[10 42 55] 2:[11 32 52] 
``` 
Für die Speicherung einer Positionsangabe vewenden wir einen 32-Bit
`int`, für den ein Speicherbedarf von 4 Bytes anfällt.

## Die Klasse `IndexSizeStatistics`  

Die Klasse `IndexSizeStatistics` speichert die statistischen Angaben,
die für die Berechnung des Speicherbedarfs benötigt werden, das sind
u.a.
* die Menge der Terme im Dictionary
* die Anzahl aller Positionsangaben (in allen Postinglisten)
* die Anzahl aller Dokument-IDs (in allen Postinglisten) 
* die Anzahl der für die Bestimmung betrachteten Dokumente

Die Methode `print` ermöglicht die Berechnung und Ausgabe des
Speicherbedarfs.

## Vorbereitung

Für diese Übung nutzen wir eine Auswahl von 180 E-Books vom Gutenberg Projekt.
Die Auswahl steht in Form einer ZIP-Datei im Moodle-Raum zur Vorlesung unter
*Übung 3* zum Download zur Verfügung. Laden Sie diese ZIP-Datei aus Moodle herunter
und legen Sie die ZIP-Datei in das Projektverzeichnis `ueb3` (das Verzeichnis, in dem sich
diese `README.md` befindet). Entpacken Sie die ZIP-Datei anschließend mittels

```bash
unzip ueb3-ebooks-sample.zip
```
Anschließend sollte im Projektverzeichnis ein Verzeichnis mit dem Namen `ueb3-ebooks-sample` existieren.

## Aufgaben

Das Projekt enthält _Unit Tests_, die automatisch prüfen, ob die von Ihnen vorgenommene
Implementierung in der Methode `ngrams` korrekt ist. Aufgrund der initial
unvollständigen Implementierung dieser Methode werden die Tests erst einmal nicht erfolgreich
durchlaufen.

Sie können die Testausführung direkt in IntelliJ starten, indem Sie einen Rechtsklick
auf die Testklasse `IndexSizeComparisonTest` durchführen und den Eintrag _Run_
auswählen. Anschließend werden die in der Testklasse enthaltenen Testmethoden (erkennbar
an der Annotation `@Test`) nacheinander ausgeführt.

Analog können Sie die Tests auch auf der Kommandozeile starten, indem Sie im
Projektverzeichnis das folgende Kommando aufrufen:

```
./mvnw test
```
           
In beiden Fällen sollte Sie folgende Ausgabe erhalten (in IntelliJ etwas gekürzt):

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeComparisonTest
[ERROR] Tests run: 6, Failures: 0, Errors: 2, Skipped: 0, Time elapsed: 0.092 s <<< FAILURE! - in de.suma.IndexSizeComparisonTest
[ERROR] generateTrigram  Time elapsed: 0.014 s  <<< ERROR!
java.lang.NullPointerException
	at de.suma.IndexSizeComparisonTest.generateTrigram(IndexSizeComparisonTest.java:66)

[ERROR] generateBigram  Time elapsed: 0.002 s  <<< ERROR!
java.lang.NullPointerException
	at de.suma.IndexSizeComparisonTest.generateBigram(IndexSizeComparisonTest.java:39)

[INFO] Running de.suma.TokenizerTest
[WARNING] Tests run: 6, Failures: 0, Errors: 0, Skipped: 3, Time elapsed: 0.001 s - in de.suma.TokenizerTest
[INFO] 
[INFO] Results:
[INFO] 
[ERROR] Errors: 
[ERROR]   IndexSizeComparisonTest.generateBigram:39 NullPointer
[ERROR]   IndexSizeComparisonTest.generateTrigram:66 NullPointer
[INFO] 
[ERROR] Tests run: 12, Failures: 0, Errors: 2, Skipped: 3
```

Beginnen Sie nun mit der Implementierung der fehlenden Teile in der Methode `ngrams`.
Die Stelle ist im Quellcode mit `TODO` markiert. Sie können jederzeit die Tests erneut ausführen, 
um zu prüfen, ob die von Ihnen vorgenommene Implementierung der Methode die Spezifikation erfüllt, 
d.h. tatsächlich die _n_-Gramme einer übergebenen Zeichenkette `textToIndex` berechnet.

Haben Sie eine korrekte Implementierung der Methode `ngrams` vorgenommen, so ergibt die Testausführung 
folgende Ausgabe:

```
INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeComparisonTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.104 s - in de.suma.IndexSizeComparisonTest
[INFO] Running de.suma.TokenizerTest
[WARNING] Tests run: 6, Failures: 0, Errors: 0, Skipped: 3, Time elapsed: 0.001 s - in de.suma.TokenizerTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 3
```

Implementieren Sie nun noch die fehlenden Teile (ebenfalls im Quellcode mit `TODO` gekennzeichnet)
in der Klasse `IndexSizeStatistics`. In dieser Klasse wird der Speicherbedarf für die vier o.g.
Indextypen ermittelt.

Führen Sie anschließend die Klasse `IndexSizeComparison` aus. Das Java-Programm
gibt auf der Standardausgabe den Speicherbedarf für die vier o.g. Indextypen bezogen auf
die beiden Felder `title` und `fulltext` aus.

Das Java-Programm können Sie in IntelliJ ausführen (Rechtsklick auf die Klasse `IndexSizeComparision`
und den Eintrag _Run_ auswählen) oder Sie rufen auf der Kommandozeile den Befehl aus:

```
$ ./mvnw clean compile exec:java
```

Setzen Sie ggf. vorher die maximale Größe des allokierten Hauptspeichers mittels `MAVEN_OPTS` auf 2 GB:
```
$ export MAVEN_OPTS='-Xmx2G'
```

Diskutieren Sie die vom Programm berechneten Werte für den Speicherbedarf
der unterschiedlichen Indexvarianten bezüglich der beiden Indexfelder `title`
und `fulltext`. Vergleichen Sie die unterschiedlichen Indextypen bezüglich
ihrer Speichergröße. Wie sind die Unterschiede in den berechneten Werten zu
erklären? Ihre Diskussion fügen Sie bitte in die Datei `answers.txt` ein.

In der vorliegenden Implementierung wird mittels eines _Whitespace-Tokenizers_ aus dem Feldwert (d.h. aus dem
Titel oder Volltext) eine Liste von Tokens erzeugt. Hierbei wird der Feldwert an Whitespace-Zeichen aufgespalten.
Jedes so entstehende Token wird anschließend durch einen Lowercasing-Schritt in Kleinbuchstaben umgewandelt.

Beispielsweise ergibt das Whitespace-Tokenizing für den Feldwert 

```
The quick brown fox jumps over the lazy dog
```

die folgende Liste von Tokens

```
the
quick
brown
fox
jumps
over
the
lazy
dog
```

Sie sollen nun den Tokenisierungsschritt verbessern. Dazu sollen Sie die Methode `getTokensImproved`
in der Klasse `Tokenizer` implementieren. Die durchzuführenden Operationen sind als `TODO`-Kommentare
in der Methode vermerkt.

Um die Korrektheit Ihrer Implementierung sicherzustellen, stehen in der Testklasse `TokenizerTest`
entsprechende Unit-Tests zur Verfügung. Die Tests laufen aktuell nicht erfolgreich durch und wurden
deshalb mit der Annotation `@Disabled` versehen. Entfernen Sie die Annotation (dreimal) in der Testklasse
und führen Sie die Testklasse anschließend aus (analog zur Ausführung von `IndexSizeComparisonTest`).

Es sollte sich folgende Ausgabe ergeben:

```
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeComparisonTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.122 s - in de.suma.IndexSizeComparisonTest
[INFO] Running de.suma.TokenizerTest
[ERROR] Tests run: 6, Failures: 3, Errors: 0, Skipped: 0, Time elapsed: 0.028 s <<< FAILURE! - in de.suma.TokenizerTest
[ERROR] testGetTokensImprovedWithMultipleTokensAndParentheses  Time elapsed: 0.014 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: expected: <token1> but was: <(token1;>
	at de.suma.TokenizerTest.testGetTokensImprovedWithMultipleTokensAndParentheses(TokenizerTest.java:47)

[ERROR] testGetTokensImprovedWithMultipleTokens  Time elapsed: 0.003 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: expected: <token1> but was: <token1,>
	at de.suma.TokenizerTest.testGetTokensImprovedWithMultipleTokens(TokenizerTest.java:37)

[ERROR] testGetTokensImprovedWithApostropheS  Time elapsed: 0.003 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: expected: <token1> but was: <(token1's,>
	at de.suma.TokenizerTest.testGetTokensImprovedWithApostropheS(TokenizerTest.java:59)

[INFO] 
[INFO] Results:
[INFO] 
[ERROR] Failures: 
[ERROR]   TokenizerTest.testGetTokensImprovedWithApostropheS:59 expected: <token1> but was: <(token1's,>
[ERROR]   TokenizerTest.testGetTokensImprovedWithMultipleTokens:37 expected: <token1> but was: <token1,>
[ERROR]   TokenizerTest.testGetTokensImprovedWithMultipleTokensAndParentheses:47 expected: <token1> but was: <(token1;>
[INFO] 
[ERROR] Tests run: 12, Failures: 3, Errors: 0, Skipped: 0
```
              
Implementieren Sie die Methode `getTokensImproved`. Sie können die Korrektheit Ihrer Implementierung
jederzeit prüfen, indem Sie die Testklasse erneut ausführen.

Sofern Sie eine korrekte Implementierung erzielt haben, zeigt sich folgende Ausgabe nach der
Ausführung der Tests:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeComparisonTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.09 s - in de.suma.IndexSizeComparisonTest
[INFO] Running de.suma.TokenizerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s - in de.suma.TokenizerTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

Tauschen Sie nun den Aufruf der Tokenizer-Methode innerhalb der Klasse `IndexSizeComparison` aus.
Dazu ersetzen Sie in der Methode `ngrams` den Aufruf von

```
String[] tokens = new Tokenizer().getTokens(textToIndex);
```

durch

```
String[] tokens = new Tokenizer().getTokensImproved(textToIndex);
```

Führen Sie anschließend (analog zu oben) die Klasse `IndexSizeComparison` aus. Vergleichen Sie
die Ausgabe mit der Ausgabe bei der vorherigen Ausführung der Klasse unter Nutzung der einfachen
Whitespace-Tokenisierung, bei der bis auf das Lowercasing keine weitere Behandlung der Tokens
erfolgte. Wie sind die Unterschiede in den berechneten Werten zu erklären? Ihre Diskussion 
fügen Sie bitte ebenfalls in die Datei `answers.txt` ein.
