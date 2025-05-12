def lexical_diversity(input_file):
    """
    Berechnet das Type-Token-Ratio (TTR) als Maß für die lexikalische Diversität eines Textes.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        tokens = infile.read().splitlines()

    print(f"TTR (ohne Lowercasing) = {len(set(tokens)) / len(tokens)}")

    tokens_lowercased = [token.lower() for token in tokens] # Umwandlung der Tokens in Kleinbuchstaben
    print(f"TTR (mit Lowercasing) = {len(set(tokens_lowercased)) / len(tokens)}")

if __name__ == "__main__":
    input_file = "tokens.txt"
    lexical_diversity(input_file)
