# Vector Space Model / Vektorraum-Modell

* Berechnung der Cosinusähnlich zwischen Dokument- und Anfragevektor
* Bestimmung von Termgewichten (SMART-Notation)
* Berechnung mittels Skalarprodukt
* es brauchen generell nur die Anfrageterme betrachtet, die im Vokabular (Dictionary des invertierten Index) vorkommen, d.h. in mindestens einem Dokument aus der Kollektion existieren

Das **Gewicht von Term _t_** in Dokument- und Anfragevektor wird in diesem Beispiel wie folgt bestimmt (_V_ sei die Anzahl der Einträge im Dictionary
des invertierten Index; die Größe des Term-Vokabulars):

![\vec{d}[t]=\mathrm{tf}(t,d)](https://render.githubusercontent.com/render/math?math=%5CLarge+%5Cdisplaystyle+%5Cvec%7Bd%7D%5Bt%5D%3D%5Cmathrm%7Btf%7D%28t%2Cd%29)

![\vec{q}[t]=\mathrm{tf}(t,q)](https://render.githubusercontent.com/render/math?math=%5CLarge+%5Cdisplaystyle+%5Cvec%7Bq%7D%5Bt%5D%3D%5Cmathrm%7Btf%7D%28t%2Cq%29)

Der Score-Wert ![formula](https://render.githubusercontent.com/render/math?math=s(d,q)) für das Dokument _d_ bezüglich der Suchanfrage _q_ ergibt sich dann mit

![s(d,q)= \frac{\sum_{i=1}^V \vec{d}[i]\cdot\vec{q}[i]}{\sqrt{\sum_{i=1}^V \vec{d}[i]^2}\cdot\sqrt{\sum_{i=1}^V \vec{q}[i]^2}}
=\frac{\sum_{i=1}^V \textrm{tf}(t_i,d)\cdot\textrm{tf}(t_i,q)}{\sqrt{\sum_{i=1}^V \textrm{tf}(t_i,d)^2}\cdot \sqrt{\sum_{i=1}^V \textrm{tf}(t_i,q)^2}}
=\frac{\sum_{t\in q\cap d} \textrm{tf}(t,d)\cdot\textrm{tf}(t,q)}{\sqrt{\sum_{t\in q\cap d} \textrm{tf}(t,d)^2}\cdot \sqrt{\sum_{t\in q\cap d} \textrm{tf}(t,q)^2}}](https://render.githubusercontent.com/render/math?math=\Large+\displaystyle+s(d,q)=%20\frac{\sum_{i=1}^V%20\vec{d}[i]\cdot\vec{q}[i]}{\sqrt{\sum_{i=1}^V%20\vec{d}[i]^2}\cdot\sqrt{\sum_{i=1}^V%20\vec{q}[i]^2}}%20=\frac{\sum_{i=1}^V%20\textrm{tf}(t_i,d)\cdot\textrm{tf}(t_i,q)}{\sqrt{\sum_{i=1}^V%20\textrm{tf}(t_i,d)^2}\cdot%20\sqrt{\sum_{i=1}^V%20\textrm{tf}(t_i,q)^2}}%20=\frac{\sum_{t\in%20q\cap%20d}%20\textrm{tf}(t,d)\cdot\textrm{tf}(t,q)}{\sqrt{\sum_{t\in%20q\cap%20d}%20\textrm{tf}(t,d)^2}\cdot%20\sqrt{\sum_{t\in%20q\cap%20d}%20\textrm{tf}(t,q)^2}})

In diesem Beispiel wird die _Inverse Document Frequency_ zur Berechnung des Score-Werts **nicht** verwendet.

Gemäß **SMART-Notation** entspricht die o.g. Formel zur Berechnung des Score-Wert damit `nnc.nnc`.
