import json

def load_inverted_index(file_path):
    """
    Lädt den invertierten Index aus einer JSON-Datei in der Arbeitsspeicher.
    """
    with open(file_path, "r", encoding="utf-8") as file:
        inverted_index = json.load(file)
    return inverted_index

def intersect(p1, p2):
    """
        Berechnet die Schnittmenge zweier sortierter Postingslisten.
    """
    result = []
    i = j = 0
    while i < len(p1) and j < len(p2):
        if p1[i] == p2[j]:
            result.append(p1[i])
            i += 1
            j += 1
        elif p1[i] < p2[j]:
            i += 1
        else:
            j += 1
    return result

def union(p1, p2):
    """
        Berechnet die Vereinigung zweier sortierter Postingslisten.
    """
    result = []
    i = j = 0
    while i < len(p1) and j < len(p2):
        if p1[i] == p2[j]:
            result.append(p1[i])
            i += 1
            j += 1
        elif p1[i] < p2[j]:
            result.append(p1[i])
            i += 1
        else:
            result.append(p2[j])
            j += 1
    while i < len(p1):
        result.append(p1[i])
        i += 1
    while j < len(p2):
        result.append(p2[j])
        j += 1
    return result

def intersect_complement(p1, p2):
    """
        Berechnet das Ergebnis von NOT p1 AND p2.
    """
    result = []
    i = j = 0
    while i < len(p1) and j < len(p2):
        if p1[i] == p2[j]:
            i += 1
            j += 1
        elif p1[i] < p2[j]:
            i += 1
        else:
            result.append(p2[j])
            j += 1
    while j < len(p2):
        result.append(p2[j])
        j += 1
    return result


def complement(p):
    """
        Berechnet das Ergebnis von NOT p.
    """
    # Menge aller Dokument-IDs im invertierten Index
    universe = {doc_id for postings in inverted_index.values() for doc_id in postings}

    return [doc_id for doc_id in universe if doc_id not in set(p)]

def parse_boolean_query(query, inverted_index):
    """
        Ein sehr einfacher Parser, der eine Boolesche Suchanfrage in ihre Bestandteile
        zerlegt. Er unterstützt die Operatoren AND bzw. OR, wobei die Suchanfrage 
        keine gemischten Operatoren enthalten darf. NOT wird nur in Ein-Term-Suchanfragen
        unterstützt. Ebenso werden keine Klammern in der Suchanfrage unterstützt. 
    """
    conjunctive_query = False
    disjunctive_query = False
    if " AND " in query:
        conjunctive_query = True
    if " OR " in query:
        disjunctive_query = True

    if conjunctive_query and disjunctive_query:
        raise ValueError("Gemischte Boolesche Operatoren sind in der Suchanfrage nicht erlaubt. Bitte verwenden Sie nur AND oder OR.")

    if not conjunctive_query and not disjunctive_query:
        # Ein-Term-Anfrage
        if query.upper().startswith("NOT "):
            return complement(inverted_index.get(query[3:].lower().strip(), []))
        return inverted_index.get(query.lower(), [])
    
    query_parts = query.split()
    result = []
    for query_part in query_parts:
        if conjunctive_query and query_part.upper() == "AND":
            continue
        if disjunctive_query and query_part.upper() == "OR":
            continue
        if len(result) == 0:
            result = inverted_index.get(query_part.lower(), [])
            continue
        
        if conjunctive_query:
            result = intersect(result, inverted_index.get(query_part.lower(), []))
        elif disjunctive_query:
            result = union(result, inverted_index.get(query_part.lower(), []))

    return result

if __name__ == "__main__":
    inverted_index = load_inverted_index("Goethe-inverted-index.json")

    query = input("Geben Sie Ihre Boolesche Suchanfrage ein: ")
    
    try:
        result = parse_boolean_query(query, inverted_index)
        print(f"Suchergebnis (mit {len(result)} Treffern): {result}")
    except ValueError as e:
        print(f"Syntaxfehler: {e}")
