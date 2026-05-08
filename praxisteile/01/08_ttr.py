def lexical_diversity(input_file):
    """
    Berechnet das Type-Token-Ratio (TTR) als Maß für die lexikalische Diversität eines Textes.
    """
    with open(input_file, "r", encoding="utf-8") as infile:
        tokens = infile.read().splitlines()

    print(f"Type-Token-Ratio (ohne Lowercasing) = {round(len(set(tokens)) / len(tokens) * 100, 2)} %")

    tokens_lowercased = [token.lower() for token in tokens] # Umwandlung der Tokens in Kleinbuchstaben
    print(f"Type-Token-Ratio (mit Lowercasing)  = {round(len(set(tokens_lowercased)) * 100 / len(tokens), 2)} %")

if __name__ == "__main__":
    input_file = "21000_tokens.txt"
    lexical_diversity(input_file)
