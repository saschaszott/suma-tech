def compute_term_lengths(input_file):
    """
    Berechnet die minimale, maximale und durchschnittliche Länge der Terme.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        # Datei zeilenweise einlesen
        tokens = infile.read().splitlines()
    
    # Termlängen bestimmen
    min_length = None
    min_length_tokens = []
    max_length = None
    max_length_tokens = []
    sum_of_lengths = 0
    for token in set(tokens):
        length = len(token)
        sum_of_lengths += length
        if min_length is None or length < min_length:
            min_length = length
            min_length_tokens = [token]
        elif length == min_length:
            min_length_tokens.append(token)
        if max_length is None or length > max_length:
            max_length = length
            max_length_tokens = [token]
        elif length == max_length:
            max_length_tokens.append(token)
    
    print(f"Minimale Termlänge: {min_length}")
    print(f"Maximale Termlänge: {max_length}")
    print(f"Durchschnittliche Termlänge: {sum_of_lengths / len(set(tokens)):}")
    print("\nTerme mit minimaler Länge:")
    for token in min_length_tokens:
        utf8_bytes = token.encode("utf-8")
        hex_bytes = ' '.join(f"{byte:02X}" for byte in utf8_bytes)
        print(f"{token} in UTF-8 Kodierung: {hex_bytes}")

    print("\nTerme mit maximaler Länge:")        
    for token in max_length_tokens:
        print(token)

if __name__ == "__main__":
    input_file = "tokens.txt"
    compute_term_lengths(input_file)
