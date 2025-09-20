import math

def compute_idf(N, df):
    return math.log((N - df + 0.5) / (df + 0.5) + 1)

def compute_bm25_score(f, idf, doc_len, avgdl, k1=1.5, b=0.75):
    norm = 1 - b + b * (doc_len / avgdl)
    return idf * ((f * (k1 + 1)) / (f + k1 * norm))

N = 1000             # Anzahl der Dokumente im Korpus
avgdl = 80           # durchschnittliche Dokumentlänge
doc_len = 100        # Länge des aktuellen Dokuments

# 2-Term-Suchanfrage
# Angabe der Häufigkeiten der Anfrageterme im aktuellen Dokument
# sowie der Dokumenthäufigkeiten für die Anfrageterme im Korpus:
query_terms = {
    "information": {"tf": 3, "df": 100},
    "retrieval": {"tf": 1, "df": 10}
}

total_score = 0

for term, values in query_terms.items():
    tf = values["tf"]
    df = values["df"]
    idf = compute_idf(N, df)
    score = compute_bm25_score(tf, idf, doc_len, avgdl)
    total_score += score

    print(f"tf({term}, .): {tf}")
    print(f"df({term}): {df}")
    print(f"idf({term}): {idf:.4f}")
    print(f"w_bm25({term}): {score:.4f}")
    print()
