# Berechnung der Ähnlichkeit von zwei Zeichenketten

Es werden mehrere Ähnlichkeitsmaße für den Vergleich von zwei
Zeichenketten verwendet:

* Levenshtein-Distanz
* normalisierte Levenshtein-Distanz
* Jaro-Winkler-Distanz
* Kosinus-Distanz

Hierbei wird die Java-Bibliothek `commons-text` 
von Apache verwendet.

## Levenshtein-Distanz

Edit-Distanz mit der Operationsmenge {`insert`, `delete`,
`substitute`}

Jede der drei Operationen hat Kosten in Höhe von 1.

## normalisierte Levenshtein-Distanz

Liefert Werte im Intervall [0, 1].

## Jaro-Winkler-Distanz

Hierbei wird unterstellt, dass in der Praxis Schreibfehler meist nicht 
schon am Wortanfang auftreten; d.h. Zeichenketten mit übereinstimmenden 
Präfix erhalten einen erhöhten Ähnlichkeitswert

## Kosinus-Distanz

Entspricht: 1 - Kosinus-Ähnlichkeit

Nur sinnvoll anwendbar, wenn eine der beiden
Zeichenketten aus mehr als einen Term besteht.
 
Vektoren werden hierbei mittels *Term Frequency*
erzeugt. Die Normalisierung erfolgt mittels *2-Norm* / *Euklidische Norm*.