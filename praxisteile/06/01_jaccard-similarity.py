# Beispiel-Dokumente (Modellierung von Dokumenten als Termmengen)
documents = {
    "d1": {"information", "retrieval", "system"},
    "d2": {"information", "science", "search"},
    "d3": {"data", "retrieval", "database"},
    "d4": {"retrieval", "ranking", "vector", "space"},
    "d5": {"database", "sql", "data"},
    "d6": {"database", "sql", "data", "data", "data"}
}

def print_all_documents():
    for key, value in documents.items():
        print(f"{key}: {value}")

def jaccard_similarity(set1, set2):
    intersection = set1 & set2 # Schnittmenge
    union = set1 | set2 # Vereinigungsmenge
    return len(intersection) / len(union) if union else 0 # Vermeidung von Division durch Null

def jaccard_scoring(query, documents, k):
    # Jaccard-Ähnlichkeit der einzelnen Dokumente bezüglich Anfrage berechnen
    # und Top-k-Ranking ausgeben
    results = []
    for doc_id, terms in documents.items():
        score = jaccard_similarity(query, terms)
        if score > 0:
            # Dokummente mit Jaccard-Ähnlichkeit 0 werden nicht berücksichtigt
            results.append((doc_id, score))

    # Dokumente absteigend nach ihrer Jaccard-Ähnlichkeit sortieren
    ranked = sorted(results, key=lambda x: x[1], reverse=True)

    # Ausgabe des Top-k-Rankings
    print(f"Top-{k}-Ranking der Dokumente bezüglich ihrer Jaccard-Ähnlichkeit:")
    for doc_id, score in ranked[:k]:
        print(f"{doc_id} (J-K = {score:.3f})")

def build_query_term_set(query):
    # Hier könnte eine erweiterte Behandlung der vom Benutzer eingegeben Suchanfrage erfolgen, z.B. Normalisierung, Stemming
    return set(query.lower().split())

if __name__ == "__main__":
    print_all_documents()
    
    query = input("Geben Sie Ihre Suchanfrage ein: ")
    query_terms = build_query_term_set(query)
    jaccard_scoring(query_terms, documents, 5)
