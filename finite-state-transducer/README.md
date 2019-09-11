# Finite State Transducer

Für die Ausführung wird `sed` und `graphviz` benötigt. Die beiden Programm können mittels `sudo apt install sed graphviz`
installiert werden.

Zum Starten des Programms rufen Sie anschließend das Script `run.sh` auf.

Nach der erfolgreichen Programmausführung wird im Wurzelverzeichnis eine PNG-Datei `fst.png` angelegt.

Beispielausgabe für ein Dictionary `dict` mit den 12 Termen:

```
dict[1]  = car
dict[2]  = cards
dict[3]  = cast
dict[4]  = cat
dict[5]  = cats
dict[6]  = do
dict[7]  = dogs
dict[8]  = sea
dict[9]  = seal
dict[10] = seat
dict[11] = zoo
dict[12] = zoom
```

![Alt text](img/fst.png?raw=true)

Für die Eingabe `cards` durchläuft man den FST beginnend im Startzustand (links außen). Beim Durchlaufen des FST
sammelt man die Ausgaben der einzelnen Kanten auf und addiert schließlich die aufgesammelten Werte. Kanten ohne
Ausgabebeschriftung werden als 0 gezählt. Für `cards` ergibt sich somit `1 + 0 + 0 + 1 + 0 = 2`, wonach der zweite
Eintrag in `dict` den Term `cards` repräsentiert und demzufolge der ausgehende Pointer auf die Postingliste von
`cards` zeigt. 

In der Darstellung wird nicht jeder Term aus dem Dictionary durch einen Pfad vom Start- zum Zielzustand repräsentiert.
Daher sind einige Knoten im FST mit einer dicken eingehenden Kante markiert, so z.B. die oberste Kante mit der 
Beschriftung `r`. Hierdurch soll angezeigt werden, dass an dem Knoten, auf die die dick markierte Kante zeigt, ein Pfad
(ausgehend vom Startzustand) endet, der mit einem Dictionary Term korrespondiert (in diesem Fall ist das der Term `car`).
Die Ermittlung der Indexposition des Terms in `dict` erfolgt analog zum oben beschriebenen Verfahren (`car` erhält
demnach den Wert `1`, wonach an der ersten Stelle in `dict` der Eintrag für den Term `car` steht).
