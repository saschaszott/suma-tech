# 3. Übung zum Modul Suchmaschinentechnologie

## Vergleich des Speicherbedarfs für Positional Index und Non-Positional Index 

Die Übung kann erneut in Zweierteams bearbeitet werden. Bitte speichern Sie
Ihre Antworten auf die gestellten Fragen in der Textdatei
`answers.txt`, die ebenfalls im Projektverzeichnis abgelegt ist.

Bitte reichen Sie das gesamte Projekt `ueb3` in Form eines ZIP-Archiv als
Lösung ein (Upload des ZIP-Archivs im Moodle-Raum zur Vorlesung). Rufen Sie vor 
dem Erzeugen des ZIP-Archivs den Befehl
```
$ ./mvnw clean
```
auf, so dass keine kompilierten `.class`-Dateien im Archiv landen.

Für Fragen nutzen Sie bitte das Moodle-Diskussionsforum zur Vorlesung:
https://elearning.th-wildau.de/mod/forum/view.php?id=167636

## Die Klasse `IndexSizeComparison`

In der Übung soll untersucht werden wieviel Speicherplatz für
unterschiedliche Typen des invertierten Index benötigt wird.

Es werden hierbei vier Indextypen untersucht:

* Positional Index
* Non-Positional Index mit Ein-Term-Dictionary (Uniword-Index)
* Non-Positional Index mit Zwei-Term-Dictionary (Biword-Index)
* Non-Positional Index mit Drei-Term-Dictionary (Triword-Index)

Zur Bestimmung des Speicherbedarfs verwenden wir die bereits aus 
der ersten Übung bekannte Dokumentkollektion mit 1523 freien
E-Books aus dem Gutenberg-Projekt.

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
Kommt ein Term mehrfach innerhalb eines Dokument vor, so fällt 
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

## Aufgaben

Vervollständigen Sie die mit `TODO` im Quelltext gekennzeichneten Stellen, so
dass der Speicherbedarf für die vier oben genannten Index-Typen vom
Programm berechnet wird.

Führen Sie anschließend die Klasse `IndexSizeComparison` aus. Das Programm
gibt den Speicherbedarf für die Felder `title` und `fulltext` aus.

Für diese Übung nutzen wir ein Auswahl von 200 E-Books vom Gutenberg Projekt.
Die Auswahl steht in Form einer ZIP-Datei im Moodle-Raum zur Vorlesung unter
*Übung 3* zum Download zur Verfügung. Laden Sie diese ZIP-Datei herunter (in das
Verzeichnis `ueb3`) und entpacken Sie die Datei mittels

```bash
unzip ueb3-ebooks-sample.zip
```
Anschließend sollte sich im Projektverzeichnis (in dem auch diese `README.md` Datei
liegt) ein Verzeichnis mit dem Namen `ueb3-ebooks-sample` existieren.

Das Programm können Sie auf der Kommandozeile mit dem Befehl
```
$ ./mvnw clean compile exec:java
```
starten. Setzen Sie ggf. vorher die maximale Größe des allokierten Hauptspeichers
mittels `MAVEN_OPTS` auf 1 GB:
```
$ export MAVEN_OPTS='-Xmx1G'
```

Alternativ können Sie das Programm auch direkt aus IntelliJ IDEA starten.
Dazu importieren Sie das Projekt unter `2019/ueb3` (als Maven-Projekt). Zum
Ausführen starten Sie dann die Klasse `de.suma.IndexSizeComparison`.

Diskutieren Sie kurz die vom Programm berechneten Werte für den Speicherbedarf
der unterschiedlichen Indexvarianten bezüglich der beiden Indexfelder `title`
und `fulltext`.
