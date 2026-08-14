import os

def compute_similarity(terms_A, terms_B, mode="jaccard"):
    """
    Berechnet die Ähnlichkeit zwischen zwei Autoren basierend auf den zugehörigen Term-Mengen ihrer Werke.

    Parameter:
    - terms_A: Menge der Terme aus den Werken des ersten Autors
    - terms_B: Menge der Terme aus den Werken des zweiten Autors
    - mode: Modus der Ähnlichkeitsberechnung ("jaccard", "dice", "otsuka-ochiai")
    """
    intersection = terms_A & terms_B # Schnittmenge der Term-Mengen beider Autoren
    if mode == "jaccard":
        union = terms_A | terms_B # Vereinigungsmenge der Term-Mengen beider Autoren
        similarity = len(intersection) / len(union) if union else 0
    elif mode == "dice":
        # bietet sich an, wenn die beiden Term-Mengen sehr unterschiedlich groß sind (Normierung der Größe der
        # Schnittmenge durch das arithmetische Mittel der Größen der beiden Term-Mengen)
        len_arithmetic_mean = 0.5 * (len(terms_A) + len(terms_B))
        similarity = len(intersection) / len_arithmetic_mean if len_arithmetic_mean > 0 else 0
    elif mode == "otsuka-ochiai":
        # bietet sich an, wenn die beiden Term-Mengen sehr unterschiedlich groß sind (Normierung der Größe der
        # Schnittmenge durch das geometrische Mittel der Größen der beiden Term-Mengen)
        len_geom_mean = (len(terms_A) * len(terms_B)) ** 0.5
        similarity = len(intersection) / len_geom_mean if len_geom_mean > 0 else 0
    else:
        similarity = 0

    print(f"{mode.capitalize()}-Similarity({author_A}, {author_B}) = {mode.capitalize()}-Similarity({author_B}, {author_A}) = {similarity:.4f}")
    return similarity

if __name__ == "__main__":
    input_dir = "german-works"
    mode = "jaccard"  # Ähnlichkeitsmodus für Term-Mengen: "jaccard", "dice" oder "otsuka-ochiai"
    similarity_matrix = {}
    file_list = []

    # Suche in Autorverzeichnissen nach Textdatei "terms"
    for author_dir in os.listdir(input_dir):
        terms_file = os.path.join(input_dir, author_dir, "terms")
        if os.path.isfile(terms_file):
            file_list.append(author_dir)

    for author_A in file_list:
        # da die Ähnlichkeit symmetrisch ist, müssen wir für zwei Autoren nur einmal die Ähnlichkeit berechnen
        for author_B in file_list[file_list.index(author_A) + 1:]:
            terms_A = list()
            with open(os.path.join(input_dir, author_A, "terms"), "r", encoding="utf-8") as f:
                for line in f:
                    terms_A.append(line.strip())
            terms_B = list()
            with open(os.path.join(input_dir, author_B, "terms"), "r", encoding="utf-8") as f:
                for line in f:
                    terms_B.append(line.strip())

            similarity = compute_similarity(set(terms_A), set(terms_B))
            similarity_matrix[(author_A, author_B)] = similarity

    # Ausgabe der Ähnlichkeitswerte zwischen den Autoren als CSV-Datei
    with open(f"author-similarity_{mode}.csv", "w", encoding="utf-8") as csv_file:
        for (author_A, author_B), similarity in similarity_matrix.items():
            csv_file.write(f"{os.path.basename(author_A).split('.')[0]},{os.path.basename(author_B).split('.')[0]},{similarity:.4f}\n")

