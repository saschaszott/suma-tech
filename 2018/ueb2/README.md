# Vergleich des Speicherbedarfs für Positional Index und Non-Positional Index 

Die Übung kann in Zweierteams bearbeitet werden. Bitte speichern Sie
Ihre Antworten auf die gestellten Fragen in der Textdatei
`answers.txt`, die ebenfalls im Projektverzeichnis abgelegt ist.

Bitte reichen Sie das gesamte Projekt `ueb2` als ein ZIP-Archiv als
Lösung ein (Upload im Moodle-Raum zur Vorlesung). Rufen Sie vor 
dem Erzeugen des ZIP-Archivs den Befehl
```
$ ./mvnw clean
```
auf, so dass keine kompilierten `.class`-Dateien im Archiv landen.

Für Fragen nutzen Sie bitte das Moodle-Diskussionsforum zur Vorlesung.

## Die Klasse `IndexSizeComparison`

In der Übung soll untersucht werden wieviel Speicherplatz für
unterschiedliche Typen des invertierten Index benötigt wird.

Es werden hierbei vier Indextypen untersucht:

* Positional Index
* Non-Positional Index mit Ein-Term-Dictionary
* Non-Positional Index mit Zwei-Term-Dictionary (Biword-Index)
* Non-Positional Index mit Drei-Term-Dictionary (Triword-Index)

Zur Bestimmung des Speicherbedarfs verwenden wir die bereits aus 
der ersten Übung bekannte Dokumentkollektion mit 1542 freien
E-Books aus dem Gutenberg-Projekt.

Der Speicherbedarf beim **Non-Positional Index** entsteht durch 
das Dictionary, in dem jeder Term der Kollektion gespeichert wird.
Außerdem muss die ID jedes Dokuments in die entsprechenden
Posting-Listen der Terme eingetragen werden, die im Dokument
auftreten. Wir nehmen hierbei an, dass für einen Eintrag im 
Dictionary ein Byte pro Zeichen benötigt wird (das stimmt nicht
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
Terms in einem Dokument. Kommt ein Term mehrfach innerhalb eines
Dokument vor, so fällt bei dieser Indexvariante mehr Speicherplatz
als beim Non-Positional Index an. Wir nehmen hierbei an, dass
die Postinglisten im Positional Index folgenden Aufbau haben
(hier am Beispiel von zwei Dokumenten mit den IDs `1` und `2`, 
in denen ein Term jeweils an 3 Positionen auftritt):
```
1:[10 42 55] 2:[11 32 52] 
``` 
Für die Speicherung der Positionsangabe vewenden wir eine 32-Bit
`int` für den ein Speicherbedarf von 4 Bytes anfällt.

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

## Aufgaben

Vervollständigen Sie die mit `TODO` im Quelltext gekennzeichneten Stellen, so
dass der Speicherbedarf für die vier oben genannten Index-Typen vom
Programm berechnet wird.

Führen Sie anschließend die Klasse `IndexSizeComparison` aus. Das Programm
gibt den Speicherbedarf für die Felder `title` und `fulltext` aus.

Das Programm können Sie auf der Kommandozeile mit dem Befehl
```
$ ./mvnw clean compile exec:java
```
starten.

Bei der Berechnung für das Feld `fulltext` werden nicht alle 1542 Dokumente
der Dokumentkollektion betrachtet, sondern nur ein Sample von 200 Dokumenten,
um die Berechnung etwas zu beschleunigen. Sie können bei Bedarf den 
Sampling-Parameter vergrößern (in der `main`-Methode der Klasse 
`IndexSizeComparison`).

Diskutieren Sie kurz die vom Programm berechneten Werte für den Speicherbedarf
der unterschiedlichen Indexvarianten bezüglich der beiden Indexfelder `title`
und `fulltext`.
