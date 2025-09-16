from nltk.stem.snowball import SnowballStemmer

def apply_snowball_stemming(input_file):
    with open(input_file, "r", encoding="utf-8") as infile:
        tokens = infile.read().splitlines()

    stemmer = SnowballStemmer("german")
    terms = set(tokens)
    for term in terms:
        stem = stemmer.stem(term)
        if stem != term.lower():
            print(f"{term.lower()} → {stem}")

if __name__ == "__main__":
    input_file = "tokens.txt"
    apply_snowball_stemming(input_file)
