# Ranked Retrieval

Mit dem *Jaccard-Koeffizient* haben wir ein erstes (sehr einfaches) Retrievalmodell 
gesehen, das für die Berechnung eines Rankings der Dokumente auf Basis einer vom
Benutzer gegebenen Suchanfrage genutzt werden kann.

Jedes Dokument als auch die Suchanfrage wird als Menge von Termen interpretiert.
Die Häufigkeit eines Terms als auch die Positionen der Terme innerhalb der 
Dokumente (und der Anfrage) werden in diesem Modell nicht betrachtet. Man spricht
daher auch von einem Modell, das auf der *Set Sematics* basiert.

# Ausführung des Programms

Das Programm kann mittels Maven gebaut, getestet und ausgeführt werden. Die
verwendeten Testdokumente stehen in der Konstante `JaccardCoefficient.documents`.

Das Programm erwartet die Eingabe einer Suchanfrage auf der Kommandozeile. Die
Eingabe von `quit!` beendet das Programm.

Das Programm kann mittels

````
mvn clean compile
````
kompiliert werden.

Die Testfälle zur Verifkation der korrekten Berechnung des Jaccard-Koeffizienten können
mittels

````
mvn test
````
ausgeführt werden. Hierfür wird die Java-Bibliothek JUnit verwendet.
Sind alle Test erfolgreich durchgelaufen, so sollte folgende Ausgabe auf der
Kommandozeile ausgegeben werden:

````
Running de.suma.JaccardCoefficientTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.193 sec

Results :

Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
````

Um das Programm schließlich auszuführen, wird der Befehl

````
mvn exec:java
````

ausgeführt.

# Beispielausgabe

Für die Suchanfrage `Berliner Mauer` sollte das Programm folgende Ausgabe
liefern:

````
Es wurden 3 Treffer gefunden. Nachfolgend das Ranking der Dokumente:
1: Der Bauer der Berliner Mauer (score: 0.5)
2: Chinesische Mauer (score: 0.3333333333333333)
0: Die Mauer in der Berliner Straße (score: 0.3333333333333333)
````

Die Groß- und Kleinschreibung wird hierbei nicht beachtet. Das ist bei den
meisten Suchmaschinen die Standardeinstellung. Daher liefert die Anfrage
`berliner MAUER` die gleiche Ausgabe.
