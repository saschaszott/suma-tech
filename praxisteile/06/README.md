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
python 02_3-d-vectors.py
```

## tf-idf-Termgewichtung

```sh
pip install pandas
python 03_tf-idf-weighting.py
```

## Probabilistisches Retrieval-Modell Okapi BM25

```sh
python 04_okapi-bm-25.py
```

### Scoring in Solr

Berechnung des BM25-Score-Werts für eine Suchanfrage in Solr.

```sh
pip install pysolr

python 05_scoring_in_solr.py
```

Die Standardwerte für die BM25-Parameter in Solr sind `k1 = 1.2` und `b = 0.75` (aus Lucene übernommen,
siehe https://lucene.apache.org/core/10_3_2/core/org/apache/lucene/search/similarities/BM25Similarity.html).
Die Parameterwerte können im Solr-Schema anpasst werden.

### Vektorraummodell (tf-idf) in Solr

Bis einschließlich Solr Version 5 war die `ClassicSimilarity` (tf-idf-Termgewichtung, Vektorraummodell mit
Cosinus-Ähnlichkeit) die Standard-Scoring-Funktion. Seit Solr Version 6 ist es BM25.

Das Skript `06_vsm_scoring_in_solr.py` legt bei Aufruf mit dem Parameter `--init` (falls noch nicht vorhanden)

1. den Indexfeldtyp `text_classic` (Analysedefinition analog zum Indexfeldtyp `text_general`, aber mit `solr.ClassicSimilarityFactory`)
2. das Indexfeld `title_classic` und
3. das Copy-Field `title` → `title_classic` an.

Da Copy-Fields nur beim Indexieren befüllt werden, müssen die Metadaten der E-Books aus dem Projekt Gutenberg
anschließend neu indexiert werden:

```sh
python 06_vsm_scoring_in_solr.py --init
cd ../04 && python _04_index_metadata_pysolr.py
cd ../06
python 06_vsm_scoring_in_solr.py
```

Anschließend wird das Vektorraummodell-Scoring in Solr getestet.

Hierzu fragt das Skript eine beliebige Suchanfrage ab und gibt die Top-3-Treffer
mit den zugehörigen Score-Werten (nach Vektorraummodell) aus.

Für jeden Suchtreffer im Top-3-Ranking kann der Score-Wert mit der Explain-Funktion
nachvollzogen werden.

Die Termgewichtung `weight(t, d)` eines Terms `t` im Dokument `d` erfolgt nach der Formel:

`weight(t, d) = boost · idf(t) · tf(t, d) · fieldNorm(d)`

mit

* `idf(t) = 1 + ln((N + 1) / (df(t) + 1))`
* `tf(t, d) = sqrt(freq)`
* `fieldNorm(d) ≈ 1 / sqrt(doc_length(d))`

Einzelheiten zum Vektorraummodell-Scoring sind in der Lucene-Dokumentation zu finden:

https://lucene.apache.org/core/10_3_2/core/org/apache/lucene/search/similarities/TFIDFSimilarity.html

nachzulesen.