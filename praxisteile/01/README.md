# Praxisteil Nr. 1

## GitHub-Projekt suma-tech klonen

```sh
git clone https://github.com/saschaszott/suma-tech.git
```

## virtuelle Python-Umgebung erstellen

```sh
cd suma-tech/praxisteile
python3 -m venv venv
```

## Python-Umgebung startem

```sh
# Linux und macOS
source venv/bin/activate

# Windows CMD bzw. Powershell
venv\Scripts\activate
```

## Volltextdatei herunterladen und lokal speichern

```sh
pip install requests
cd 01
python 01_download.py
```

Nach der Ausführung liegt die heruntergeladene Datei unter `21000.txt` vor.

## Erste Strukturanalyse der Volltextdatei

Alles vor dieser Zeile kann ignoriert werden:

```
*** START OF THE PROJECT GUTENBERG EBOOK FAUST: EINE TRAGÖDIE [ERSTER TEIL] ***
```

Alles nach dieser Zeile kann ignoriert werden:

```
*** END OF THE PROJECT GUTENBERG EBOOK FAUST: EINE TRAGÖDIE [ERSTER TEIL] ***
```

## Filterung des Dateiinhalts

```sh
python 02_filter-content.py
```

Nach der Ausführung liegt das Ergebnis der Filterung in der Textdatei `21000_filtered.txt` vor.

## Tokenisierung

Wir wollen nun den Volltext in seine Bestandteile zerlegen. Diesen Prozess nennt man **Tokenisierung**. 

Aus einem Volltext entsteht so eine Folge von einzelnen **Tokens**. Ein Token ist eine Ausprägung (Instanz) eines **Terms**.

Für die Tokenisierung muss eine Festlegung getroffen werden, wie der Text in seine Bestandteile (Tokens) zerlegt werden soll. Ein sehr einfacher Ansatz ist die Aufspaltung an Leerzeichen, das sogenannte **Whitespace Tokenization**.

Ausgehend von der Datei `21000_filtered.txt` erzeugen wir nun die Datei `tokens.txt`, die alle Tokens enthält, die als Ergebnis der Whitespace Tokenization erzeugt wurden:

```sh
python 03_whitespace-tokenizer.py
```

## Analyse der Termhäufigkeiten

Die **Termhäufigkeit** (term frequency, tf) eines Terms bezeichnet die Anzahl der zugehörigen Tokens in einem Text, oder anders ausgedrückt: wie häufig tritt der Term im Text auf.

Zuerst wollen wir prüfen, welche Terme im Volltext am seltensten bzw. häufigsten auftreten, d.h die kleinste bzw. größte Termhäufigkeit besitzen. 

Dazu lesen wir die Tokens aus `tokens.txt` in ein **Dictionary**. Hierbei ist der Term der Schlüssel und die zugehörige Häufigkeit der Wert. Beachte, dass im Dictionary kein Term mit einer Häufigkeit kleiner 1 existiert.

Nach der Erzeugung des Dictionary können wir nach seltenen und häufigen Termen suchen und diese ausgeben lassen.

```sh
python 04_term-analysis.py
```

Wir sehen eine Reihe von Termen, die nur einmal im Dokument existieren. Einen solchen Terme nennt man auch **Hapax Legomenon**.

Wir stellen fest, dass Terme bezüglich der Groß- und Kleinschreibung unterschieden werden, z.B. `und` bzw. `Und`. Gewöhnlich beachten Suchmaschinen die Groß- und Kleinschreibung nicht. Man sagt auch, dass die Suche _case-insensitive_ ist.

Daher wollen wir nun die Datei `tokens.txt` erneut einlesen und diesmal alle eingelesenen Token vor der weiteren Verarbeitung in Kleinbuchstaben umwandeln. Diesen Prozess nennt man auch **Lowercasing**:

```sh
python 05_term-analysis-case-insensitive.py
```

Für den Term `und` sollte nun 906 als zugehöriger Häufigkeitswert ausgegeben werden (zuvor: `und` 507 mal bzw. `Und` 399 mal).

## Darstellung der Termhäufigkeitsverteilung als Histogramm

Wir wollen nun die Verteilung der Häufigkeiten der Terme im Volltext analysieren. Als graphische Darstellung bietet sich ein **Histogramm** an. Dazu werden auf der horizontalen Achse die Häufigkeitswerte abgetragen. Auf der vertikalen Achse wird die Anzahl der Terme mit einem gegebenen Häufigkeitswert abgetragen. Bei natürlichsprachlichen Texten ergibt sich eine charakteristische Form, ein sogannanter **Long Tail**: es gibt viele Terme mit (sehr) geringer Häufigkeit (Punkt links oben) und es gibt wenige Terme mit (sehr) großer Häufigkeit ("Kurve" fällt schnell nach rechts unten ab).

Mit dem folgenden Programm kann das Histogramm angezeigt werden:

```sh
pip install matplotlib
python 06_histogramm.py
```

Zur besseren Veranschaulichung bietet sich ein einfach- oder doppel-logarithmisches Koordinatensystem an.

## Detailanalyse der Terme

### Termlängen

Nun wollen wir uns noch die Termlängen ansehen. Welcher Term ist am kürzesten und welcher am längsten? Wie groß ist die durchschnittliche Termlänge?

```sh
python 07_term-lengths.py
``` 

### Lexikalische Vielfalt / TTR

Ein Maß für die **lexikalische Vielfalt** eines Textes ist das sogenannte **Type Token Ratio** (TTR). Es berechnet sich als Verhältnis der Anzahl der Terme (in diesem Zusammenhang auch als Types bezeichnet) zur Gesamtanzahl der Tokens im Text.

```sh
python 08_ttr.py
```
