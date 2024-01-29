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

Bitte beachten Sie, dass statt Uniword in der Literatur auch die Bezeichnung
(Word-)Unigramm verwendet wird. Analog werden Biwords als (Word-)Bigramme und 
Triwords als (Word-)Trigramme bezeichnet. Es sollte stets aus dem Kontext
klar sein, ob ein _n-Gramm_ eine Folge von Zeichen (wie im _k_-Gramm-Index)
oder eine Folge von Wörtern (wie im _k_-Word-Index) bezeichnet.

Im vorliegenden Java-Code werden die englischen Bezeichnungen 
_Unigram_, _Bigram_ sowie _Trigram_ verwendet, um einzelne Terme,
Term-Paare bzw. Term-Tripel zu bezeichnen.

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

```sh
$ unzip ueb3-ebooks-sample.zip
```
Anschließend sollte im Projektverzeichnis ein Verzeichnis mit dem Namen `ueb3-ebooks-sample` existieren.

## Aufgaben

### Vervollständigung der Klasse `IndexSizeComparison`
                           
Wir beschäftigen uns zuerst mit der Implementierung der fehlenden Teile der 
Klasse `IndexSizeComparison`. Diese sind in der Klasse mit `TODO` markiert.

Das Projekt enthält _Unit Tests_, die automatisch prüfen, ob die von Ihnen vorgenommene
Implementierung in der Methode `ngrams` korrekt ist. Aufgrund der initial
unvollständigen Implementierung dieser Methode werden die Tests erst einmal 
nicht erfolgreich durchlaufen.

Sie können die Testausführung direkt in IntelliJ starten, indem Sie einen Rechtsklick
auf die Testklasse `IndexSizeComparisonTest` durchführen und den Eintrag _Run_
auswählen. Anschließend werden die in der Testklasse enthaltenen Testmethoden (erkennbar
an der Annotation `@Test`) nacheinander ausgeführt.

Analog können Sie die Tests in der Klasse `IndexSizeComparisonTest` auch auf der Kommandozeile
starten, indem Sie im Projektverzeichnis das folgende Kommando aufrufen:

```sh
$ ./mvnw -Dtest=IndexSizeComparisonTest test
```
           
In beiden Fällen sollte Sie folgende Ausgabe erhalten (in IntelliJ etwas gekürzt):

```
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeComparisonTest
[ERROR] Tests run: 6, Failures: 2, Errors: 0, Skipped: 0, Time elapsed: 0.157 s <<< FAILURE! - in de.suma.IndexSizeComparisonTest
[ERROR] generateTrigram  Time elapsed: 0.02 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Es sollten 7 Bigramme existieren. ==> expected: <7> but was: <0>
	at de.suma.IndexSizeComparisonTest.generateTrigram(IndexSizeComparisonTest.java:69)

[ERROR] generateBigram  Time elapsed: 0.002 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Es sollten 8 Bigramme existieren. ==> expected: <8> but was: <0>
	at de.suma.IndexSizeComparisonTest.generateBigram(IndexSizeComparisonTest.java:42)

[INFO]
[INFO] Results:
[INFO]
[ERROR] Failures:
[ERROR]   IndexSizeComparisonTest.generateBigram:42 Es sollten 8 Bigramme existieren. ==> expected: <8> but was: <0>
[ERROR]   IndexSizeComparisonTest.generateTrigram:69 Es sollten 7 Bigramme existieren. ==> expected: <7> but was: <0>
[INFO]
[ERROR] Tests run: 6, Failures: 2, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 5.684 s
[INFO] Finished at: 2024-01-28T16:03:16+01:00
[INFO] Final Memory: 10M/40M
[INFO] ------------------------------------------------------------------------
```

Beginnen Sie nun mit der Implementierung der fehlenden Teile in der Methode `ngrams`.
Die Stelle ist im Quellcode mit `TODO` markiert. Sie können jederzeit die Tests erneut ausführen, 
um zu prüfen, ob die von Ihnen vorgenommene Implementierung der Methode die Spezifikation erfüllt, 
d.h. tatsächlich die _n_-Gramme einer übergebenen Zeichenkette `textToIndex` berechnet.

Haben Sie eine korrekte Implementierung der Methode `ngrams` vorgenommen, so ergibt die Testausführung 
folgende Ausgabe:

```
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeComparisonTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.124 s - in de.suma.IndexSizeComparisonTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.401 s
[INFO] Finished at: 2024-01-28T16:09:17+01:00
[INFO] Final Memory: 14M/54M
[INFO] ------------------------------------------------------------------------
```

### Vervollständigung der Klasse `IndexSizeStatistics`

In der Klasse `IndexSizeStatistics` wird der Speicherbedarf für die vier o.g.
Indextypen ermittelt. Mehrere Methoden in dieser Klasse sind mit `TODO` markiert
und müssen von Ihnen implementiert werden.

Auch für diese Klasse stehen _Unit Tests_ in der Klasse `IndexSizeStatisticsTest` zur Verfügung.
Sie können die Testfälle in dieser Testklasse direkt in IntelliJ starten, indem Sie einen Rechtsklick
auf die Testklasse `IndexSizeStatisticsTest` durchführen und den Eintrag _Run_
auswählen. Anschließend werden die in der Testklasse enthaltenen Testmethoden (erkennbar
an der Annotation `@Test`) nacheinander ausgeführt.

Analog können Sie die Tests in der Klasse `IndexSizeStatisticsTest` auch auf der Kommandozeile
starten, indem Sie im Projektverzeichnis das folgende Kommando aufrufen:

```sh
$ ./mvnw -Dtest=IndexSizeStatisticsTest test
```

In beiden Fällen sollte Sie folgende Ausgabe erhalten (in IntelliJ etwas gekürzt):

```
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeStatisticsTest
[ERROR] Tests run: 7, Failures: 7, Errors: 0, Skipped: 0, Time elapsed: 0.02 s <<< FAILURE! - in de.suma.IndexSizeStatisticsTest
[ERROR] testNonPositionalTriwordIndexWithSingleDoc  Time elapsed: 0.014 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Die Speichergröße des Non-Positional Triword Index ergibt sich als Summe aus der Speichergröße für das Term-Dictionary (aus Term-Tripeln) sowie die Speichergröße für die DocIds in allen Postinglisten. ==> expected: <113> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testNonPositionalTriwordIndexWithSingleDoc(IndexSizeStatisticsTest.java:107)

[ERROR] testPositionalIndexSizeWithSingleDoc  Time elapsed: 0 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Die Speichergröße des Positional Index ergibt sich als Summe aus der Speichergröße des Term-Dictionary sowie der Speichergröße für die DocIds in allen Positinglisten sowie der Speichergröße für die Positionsangaben. ==> expected: <98> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testPositionalIndexSizeWithSingleDoc(IndexSizeStatisticsTest.java:59)

[ERROR] testDictionarySizeOfUniwordIndex  Time elapsed: 0.001 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Es gibt 8 unterschiedliche Terme im Term-Dictionary, die aus insgesamt 26 Zeichen bestehen. ==> expected: <26> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testDictionarySizeOfUniwordIndex(IndexSizeStatisticsTest.java:23)

[ERROR] testNonPositionalUniwordIndexWithSingleDoc  Time elapsed: 0 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Die Speichergröße des Non-Positional Uniword Index ergibt sich als Summe aus der Speichergröße für das Term-Dictionary (aus einzelnen Termen) sowie die Speichergröße für die DocIds in allen Postinglisten. ==> expected: <58> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testNonPositionalUniwordIndexWithSingleDoc(IndexSizeStatisticsTest.java:75)

[ERROR] testDictionarySizeOfBiwordIndex  Time elapsed: 0.001 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Es gibt 8 Biwords und die Gesamtzahl der Zeichen in allen Biwords ist 54, wobei pro Biword ein Leerzeichen mitzuzählen ist. ==> expected: <54> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testDictionarySizeOfBiwordIndex(IndexSizeStatisticsTest.java:34)

[ERROR] testDictionarySizeOfTriwordIndex  Time elapsed: 0 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Es gibt 8 Triwords und die Gesamtzahl der Zeichen in allen Triwords ist 65, wobei pro Triword zwei Leerzeichen mitzuzählen sind. ==> expected: <81> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testDictionarySizeOfTriwordIndex(IndexSizeStatisticsTest.java:45)

[ERROR] testNonPositionalBiwordIndexWithSingleDoc  Time elapsed: 0 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Die Speichergröße des Non-Positional Biword Index ergibt sich als Summe aus der Speichergröße für das Term-Dictionary (aus Term-Paaren) sowie die Speichergröße für die DocIds in allen Postinglisten. ==> expected: <86> but was: <0>
        at de.suma.IndexSizeStatisticsTest.testNonPositionalBiwordIndexWithSingleDoc(IndexSizeStatisticsTest.java:91)

[INFO] 
[INFO] Results:
[INFO] 
[ERROR] Failures: 
[ERROR]   IndexSizeStatisticsTest.testDictionarySizeOfBiwordIndex:34 Es gibt 8 Biwords und die Gesamtzahl der Zeichen in allen Biwords ist 54, wobei pro Biword ein Leerzeichen mitzuzählen ist. ==> expected: <54> but was: <0>
[ERROR]   IndexSizeStatisticsTest.testDictionarySizeOfTriwordIndex:45 Es gibt 8 Triwords und die Gesamtzahl der Zeichen in allen Triwords ist 65, wobei pro Triword zwei Leerzeichen mitzuzählen sind. ==> expected: <81> but was: <0>
[ERROR]   IndexSizeStatisticsTest.testDictionarySizeOfUniwordIndex:23 Es gibt 8 unterschiedliche Terme im Term-Dictionary, die aus insgesamt 26 Zeichen bestehen. ==> expected: <26> but was: <0>
[ERROR]   IndexSizeStatisticsTest.testNonPositionalBiwordIndexWithSingleDoc:91 Die Speichergröße des Non-Positional Biword Index ergibt sich als Summe aus der Speichergröße für das Term-Dictionary (aus Term-Paaren) sowie die Speichergröße für die DocIds in allen Postinglisten. ==> expected: <86> but was: <0>
[ERROR]   IndexSizeStatisticsTest.testNonPositionalTriwordIndexWithSingleDoc:107 Die Speichergröße des Non-Positional Triword Index ergibt sich als Summe aus der Speichergröße für das Term-Dictionary (aus Term-Tripeln) sowie die Speichergröße für die DocIds in allen Postinglisten. ==> expected: <113> but was: <0>
[ERROR]   IndexSizeStatisticsTest.testNonPositionalUniwordIndexWithSingleDoc:75 Die Speichergröße des Non-Positional Uniword Index ergibt sich als Summe aus der Speichergröße für das Term-Dictionary (aus einzelnen Termen) sowie die Speichergröße für die DocIds in allen Postinglisten. ==> expected: <58> but was: <0>
[ERROR]   IndexSizeStatisticsTest.testPositionalIndexSizeWithSingleDoc:59 Die Speichergröße des Positional Index ergibt sich als Summe aus der Speichergröße des Term-Dictionary sowie der Speichergröße für die DocIds in allen Positinglisten sowie der Speichergröße für die Positionsangaben. ==> expected: <98> but was: <0>
[INFO] 
[ERROR] Tests run: 7, Failures: 7, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 9.560 s
[INFO] Finished at: 2024-01-28T16:43:15+01:00
[INFO] Final Memory: 14M/50M
[INFO] ------------------------------------------------------------------------
```

Wie Sie der Ausgabe entnehmen können, scheitern anfänglich die 7 Testfälle in der
Testklasse `IndexSizeStatisticsTest`.

Implementieren Sie nun die fehlenden Teile in der Klasse `IndexSizeStatistics`.
Die Stellen sind im Quellcode mit `TODO` markiert. Sie können jederzeit die Tests erneut ausführen,
um zu prüfen, ob die von Ihnen vorgenommene Implementierung korrekt ist.

Haben Sie eine korrekte Implementierung vorgenommen, so ergibt die Testausführung
folgende Ausgabe:

```
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.IndexSizeStatisticsTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.106 s - in de.suma.IndexSizeStatisticsTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 5.363 s
[INFO] Finished at: 2024-01-28T17:09:21+01:00
[INFO] Final Memory: 14M/50M
[INFO] ------------------------------------------------------------------------
```
       
### Ermittlung der unterschiedlichen Indexgrößen für die vorgegebene Testkollektion

Nachdem Sie die fehlenden Teile in der Klassen `IndexSizeComparison` und `IndexSizeStatistics`
implementiert haben (und die _Unit Tests_ erfolgreich durchlaufen werden), können Sie
die unterschiedlichen Indexgrößen für die vorgegebene Testkollektion mittels des Java-Programms
berechnen.

Führen Sie dazu die Klasse `IndexSizeComparison` aus (hierbei wird die Methode `main` gestartet). 
Das Java-Programm gibt auf der Standardausgabe den Speicherbedarf für die vier o.g. Indextypen bezogen auf
die beiden Felder `title` und `fulltext` aus.

Das Java-Programm können Sie direkt in IntelliJ ausführen (Rechtsklick auf die Klasse `IndexSizeComparision`
und den Eintrag _Run_ auswählen) oder Sie rufen auf der Kommandozeile den Befehl aus:

```sh
$ ./mvnw clean compile exec:java
```

Setzen Sie ggf. vorher die maximale Größe des allokierten Hauptspeichers mittels `MAVEN_OPTS` auf 2 GB:
```sh
$ export MAVEN_OPTS='-Xmx2G'
```
           
Die Programm sollte für die Testkollektion aus 180 Dokumenten folgende Ausgabe generieren:

```
~~~~~~~~~~~~ Auswertung für das Feld title ~~~~~~~~~~~~

Speicherbedarf für die Kollektion mit 180 Dokumenten: 8 kB
Anzahl der Dictionary-Einträge (für n = 1): 655
Speicherbedarf für das Dictionary: 5 kB
Anzahl der Positionseinträge in allen Postinglisten: 1135
Speicherbedarf für Positional Index: 14 kB
Anzahl der DocId-Einträge in allen Postinglisten (für n = 1): 1089
Speicherbedarf für Non-Positional Index (für n = 1): 9 kB
Anzahl der Dictionary-Einträge (für n = 2): 890
Speicherbedarf für das Dictionary: 12 kB
Anzahl der DocId-Einträge in allen Postinglisten (für n = 2): 950
Speicherbedarf für Non-Positional Index (für n = 2): 15 kB
Anzahl der Dictionary-Einträge (für n = 3): 765
Speicherbedarf für das Dictionary: 15 kB
Anzahl der DocId-Einträge in allen Postinglisten (für n = 3): 787
Speicherbedarf für Non-Positional Index (für n = 3): 18 kB

~~~~~~~~~~~~ Auswertung für das Feld fulltext ~~~~~~~~~~~~

Speicherbedarf für die Kollektion mit 180 Dokumenten: 44804 kB
Anzahl der Dictionary-Einträge (für n = 1): 448736
Speicherbedarf für das Dictionary: 4383 kB
Anzahl der Positionseinträge in allen Postinglisten: 6934957
Speicherbedarf für Positional Index: 38200 kB
Anzahl der DocId-Einträge in allen Postinglisten (für n = 1): 1722079
Speicherbedarf für Non-Positional Index (für n = 1): 11110 kB
Anzahl der Dictionary-Einträge (für n = 2): 2926307
Speicherbedarf für das Dictionary: 40813 kB
Anzahl der DocId-Einträge in allen Postinglisten (für n = 2): 5180610
Speicherbedarf für Non-Positional Index (für n = 2): 61049 kB
Anzahl der Dictionary-Einträge (für n = 3): 5382021
Speicherbedarf für das Dictionary: 99940 kB
Anzahl der DocId-Einträge in allen Postinglisten (für n = 3): 6600747
Speicherbedarf für Non-Positional Index (für n = 3): 125724 kB
```
             
### Diskussion der Ergebnisse

Diskutieren Sie abschließend die vom Java-Programm berechneten Werte für den 
Speicherbedarf der unterschiedlichen Indexvarianten bezüglich der beiden 
Indexfelder `title` und `fulltext`.

Vergleichen Sie die unterschiedlichen Indextypen bezüglich
ihrer Speichergröße. 

Wie sind die Unterschiede in den berechneten Werten zu
erklären? 

Ihre Diskussion fügen Sie bitte in die Datei `answers.txt` ein.

### Verbesserung der Tokenisierung (in der Klasse `Tokenizer`)

In der vorliegenden Implementierung wird mittels eines _Whitespace-Tokenizers_ 
aus dem Feldwert (d.h. aus dem Titel oder Volltext) eine Liste von Tokens erzeugt.
Hierbei wird der Feldwert an Whitespace-Zeichen aufgespalten. Jedes so entstehende Token
wird anschließend durch einen _Lowercasing_-Schritt in Kleinbuchstaben umgewandelt.

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
deshalb mit der Annotation `@Disabled` versehen. 

Eine Ausführung der Testklasse `TokenizerTest` in IntelliJ (wie oben beschrieben) oder auf der
Kommandozeile mit dem Befehl:

```sh
$ ./mvnw -Dtest=TokenizerTest test
```

ergibt anfänglich folgende Aussage:

```
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.TokenizerTest
[WARNING] Tests run: 6, Failures: 0, Errors: 0, Skipped: 3, Time elapsed: 0.096 s - in de.suma.TokenizerTest
[INFO]
[INFO] Results:
[INFO]
[WARNING] Tests run: 6, Failures: 0, Errors: 0, Skipped: 3
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 5.239 s
[INFO] Finished at: 2024-01-28T17:17:28+01:00
[INFO] Final Memory: 14M/50M
[INFO] ------------------------------------------------------------------------
```

Wie Sie der Ausgabe entnehmen können, werden 3 Testfälle in der Testklasse `TokenizerTest` übersprungen.

Implementieren Sie nun die Methode `getTokensImproved` in der Klasse `Tokenizer`. Entfernen Sie die drei
`@Disabled`-Annotationen in der Testklasse `TokenizerTest` und führen Sie die Testklasse erneut anschließend
aus. 

Sofern Ihre Implementierung der Methode `getTokensImproved` korrekt ist, erhalten Sie folgende Ausgabe:

```
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running de.suma.TokenizerTest
[WARNING] Tests run: 6, Failures: 0, Errors: 0, Skipped: 3, Time elapsed: 0.127 s - in de.suma.TokenizerTest
[INFO]
[INFO] Results:
[INFO]
[WARNING] Tests run: 6, Failures: 0, Errors: 0, Skipped: 3
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.813 s
[INFO] Finished at: 2024-01-28T17:22:14+01:00
[INFO] Final Memory: 14M/54M
[INFO] ------------------------------------------------------------------------
```

### Ermittlung der unterschiedlichen Indexgrößen mit dem verbesserten Tokenizer

Tauschen Sie nun den Aufruf der Tokenizer-Methode innerhalb der Klasse `IndexSizeComparison` aus.
Dazu ersetzen Sie in der Methode `ngrams` den Aufruf von

```java
String[] tokens = new Tokenizer().getTokens(textToIndex);
```

durch

```java
String[] tokens = new Tokenizer().getTokensImproved(textToIndex);
```

Führen Sie anschließend (analog zu oben) die Klasse `IndexSizeComparison` aus. 

Vergleichen Sie die Ausgabe des Java-Programms mit der Ausgabe des Programms 
bei der vorherigen Ausführung der Klasse unter Nutzung der einfachen Whitespace-Tokenisierung,
bei der bis auf das _Lowercasing_ keine weitere Behandlung der Tokens
erfolgte. 

Wie sind die Unterschiede in den berechneten Werten zu erklären? 

Ihre Diskussion fügen Sie bitte ebenfalls in die Datei `answers.txt` ein.
