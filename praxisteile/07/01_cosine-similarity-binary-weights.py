import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

# Beispiel-Dokumente (binäre Termgewichte)
documents = np.array([
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],  # D1
    [1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],  # D2
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],  # D3
    [0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],  # D4
    [0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],  # D5
])

def jaccard_similarity(doc, query):
    intersection = np.sum(np.logical_and(doc, query))
    union = np.sum(np.logical_or(doc, query))
    return intersection / union if union > 0 else 0.0

def filter_zero_similarities(similarities):
    nonzero_mask = similarities > 0

    # Indizes der relevanten Einträge
    relevant_indices = np.where(nonzero_mask)[0]

    # Ähnlichkeiten > 0 extrahieren
    filtered_similarities = similarities[nonzero_mask]

    # sortierte Indizes innerhalb der gefilterten Werte (absteigend)
    sorted_within_filtered = np.argsort(-filtered_similarities)

    # Originalindizes der sortierten Einträge
    final_sorted_indices = relevant_indices[sorted_within_filtered] + 1

    return final_sorted_indices[:5]  # Top-5 zurückgeben

if __name__ == "__main__":
    query = np.array([1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0])
    print(f"Suchanfrage:\n q: {query}")
    print("Dokumente:")
    for i, doc in enumerate(documents):
        print(f"d{i+1}: {doc}")
    print()

    cos_similarities = cosine_similarity([query], documents)[0]
    jaccard_similarities = np.array([jaccard_similarity(query, doc) for doc in documents])

    print("Dokument | Jaccard-Ähnlichkeit | Kosinus-Ähnlichkeit")
    print("---------|---------------------|---------------------")
    for i, (jac, cos) in enumerate(zip(jaccard_similarities, cos_similarities)):
        print(f"   d{i+1}    |  {jac:.2f}               |  {cos:.2f}")

    # Ranking-Vergleich
    print("\nTop-5-Ranking nach Jaccard-Ähnlichkeit:\n", filter_zero_similarities(jaccard_similarities))
    print("\nTop-5-Ranking nach Kosinusähnlichkeit:\n", filter_zero_similarities(cos_similarities))