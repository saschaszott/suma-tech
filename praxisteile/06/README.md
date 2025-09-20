# Praxisteil Nr. 6

## Scoring auf Basis der Jaccard-Ähnlichkeit

Hierbei werden Dokumente und Suchanfrage als Mengen von Termen aufgefasst. Die Häufigkeit von Termen bleibt unbeachtet, ebenso die Reihenfolge des Auftretens bestimmter Termfolgen innerhalb von Dokumenten und Suchanfrage.

Die Jaccard-Ähnlichkeit zwischen einem Dokument _d_ und der Suchanfrage _q_ wird definiert als Quotient der Größe der Schnittmenge der Termmengen von _d_ und _q_ und der Größe der Vereinigungsmenge der Termmengen von _d_ und _q_.

Für die Ermittlung des Top-k-Rankings werden schließlich nur solche Dokumente betrachtet, die bezüglich der vorgegebenen Suchanfrage eine Jaccard-Ähnlichkeit größer 0 besitzen. Die übrig bleibenden Dokumente werden schließlich absteigend nach der zugehörigen Jaccard-Ähnlichkeit sortiert. Die Suchausgabe enthält maximal _k_ Dokumente.

Ausführung des Demonstrators:

```sh
python 01_jaccard-similarity.py
```

Das Programm nutzt eine definierte Menge von 6 Beispieldokumenten _d1_ bis _d6_. 

Die Termmengen dieser Dokumente sind innerhalb des Programms definiert. Beim Start des Programms werden die Termmengen der 6 Beispieldokumente ausgegeben. Anschließend kann der Benutzer eine beliebige Freitextsuchanfrage eingeben. Das Programm gibt als Ergebnis ein Top-5-Ranking (hier ist _k_=5) aus.

Beispielsitzung für die Suchanfrage _data retrieval_:

```
d1: {'information', 'system', 'retrieval'}
d2: {'information', 'search', 'science'}
d3: {'retrieval', 'data', 'database'}
d4: {'space', 'vector', 'retrieval', 'ranking'}
d5: {'sql', 'data', 'database'}
d6: {'sql', 'data', 'database'}
Geben Sie Ihre Suchanfrage ein: data retrieval
Top-5-Ranking der Dokumente bezüglich ihrer Jaccard-Ähnlichkeit:
d3 (J-K = 0.667)
d1 (J-K = 0.250)
d5 (J-K = 0.250)
d6 (J-K = 0.250)
d4 (J-K = 0.200)
```

Da die Häufigkeit der Suchanfrageterme in den einzelnen Dokumenten nicht beachtet wird, erhalten die Dokumente _d5_ und _d6_ den gleichen Scorewert auf Basis der Jaccard-Ähnlichkeit, obwohl der Term
_data_ dreimal in _d6_, aber nur einmal in _d5_ existiert.

Da die Reihenfolge der Suchanfrageterme in den einzelnen Dokumenten nicht betrachtet wird, liefert die Suchanfrage _retrieval data_ das gleiche Top-5-Ranking (die Score-Werte auf Basis der Jaccard-Ähnlichkeit stimmen überein):

```
Geben Sie Ihre Suchanfrage ein: retrieval data
Top-5-Ranking der Dokumente bezüglich ihrer Jaccard-Ähnlichkeit:
d3 (J-K = 0.667)
d1 (J-K = 0.250)
d5 (J-K = 0.250)
d6 (J-K = 0.250)
d4 (J-K = 0.200)
```

Aus dem Suchergebnis können wir ableiten, dass ein Dokument im Top-_k_-Ranking nicht alle Suchanfrageterme besitzen muss. Beispielsweise enthalten die Dokumente _d5_ und _d6_ den Suchanfrageterm _retrieval_ nicht.

## Vektoren in ℝ³

Darstellung von Ortsvektoren

```sh
pip install matplotlib
python 01_3-d-vectors.py
```

## tf-idf-Termgewichtung

```sh
pip install pandas
python 02_tf-idf-weighting.py
```

## Probabilistisches Retrieval-Modell Okapi BM25 

```sh
python 03_okapi-bm-25.py
```
