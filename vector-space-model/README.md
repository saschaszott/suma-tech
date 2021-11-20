# Vector Space Model / Vektorraum-Modell

* Berechnung der Cosinusähnlich zwischen Dokument- und Anfragevektor
* Bestimmung von Termgewichten (SMART-Notation)
* Berechnung mittels Skalarprodukt
* es brauchen generell nur die Anfrageterme betrachtet, die im Vokabular (Dictionary des invertierten Index) vorkommen, d.h. in mindestens einem Dokument aus der Kollektion existieren

Das Gewicht von Term t in Dokument- und Anfragevektor wird in diesem Beispiel wie folgt bestimmt (V sei die Anzahl der Einträge im Dictionary
des invertierten Index; die Größe des Term-Vokabulars):

![\vec{d}[t]=\mathrm{tf}(t,d)](https://render.githubusercontent.com/render/math?math=%5CLarge+%5Cdisplaystyle+%5Cvec%7Bd%7D%5Bt%5D%3D%5Cmathrm%7Btf%7D%28t%2Cd%29)

![\vec{q}[t]=\mathrm{tf}(t,q)](https://render.githubusercontent.com/render/math?math=%5CLarge+%5Cdisplaystyle+%5Cvec%7Bq%7D%5Bt%5D%3D%5Cmathrm%7Btf%7D%28t%2Cq%29)

Der Score-Wert ![formula](https://render.githubusercontent.com/render/math?math=s(d,q)) ergibt sich dann mit

![s(d,q)=\frac{\sum_{i=1}^{V}\vec{d}[i]\cdot\vec{q}[i]}{\sqrt{\sum_{i=1}^V\vec{d}[i]^2}\cdot\sqrt{\sum_{i=1}^V\vec{q}[i]^2}}](https://render.githubusercontent.com/render/math?math=%5CLarge+%5Cdisplaystyle+s%28d%2Cq%29%3D%5Cfrac%7B%5Csum_%7Bi%3D1%7D%5E%7BV%7D%5Cvec%7Bd%7D%5Bi%5D%5Ccdot%5Cvec%7Bq%7D%5Bi%5D%7D%7B%5Csqrt%7B%5Csum_%7Bi%3D1%7D%5EV%5Cvec%7Bd%7D%5Bi%5D%5E2%7D%5Ccdot%5Csqrt%7B%5Csum_%7Bi%3D1%7D%5EV%5Cvec%7Bq%7D%5Bi%5D%5E2%7D%7D)

In diesem Beispiel wird die _Inverse Document Frequency_ zur Berechnung des Score-Werts **nicht** verwendet.

Gemäß SMART-Notation entspricht die o.g. Formel zur Berechnung des Score-Wert damit `nnc.nnc`.
