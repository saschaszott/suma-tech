import nltk
from nltk.util import ngrams
from nltk.tokenize import word_tokenize
import re

def tokenize(input_file):
    with open(input_file, "r", encoding="utf-8") as infile:
        text = infile.read() # Text aus der Datei lesen
    
    # NLTK zur Tokenisierung verwenden (deutsche Sprache)
    tokens = word_tokenize(text, "german")

    token_list = []

    for token in tokens:
        # Korrektur am Tokenende entfernen
        token_cleaned = re.sub(r"\[[^\[\]]+\]$", "", token)
        if token_cleaned != token:
            token = token_cleaned
        
        # führender und abschließender Unterstrich entfernen
        # und anschließend möglichen Punkt am Ende entfernen
        token = token.strip("_")
        if token.endswith("."):
            token = token[:-1]
        
        if len(token.strip()) < 2: # Token mit nur einem Zeichen ignorieren
            continue
        
        token = token.lower() # alle Zeichen im Token in Kleinbuchstaben umwandeln
        token_list.append(token)
    
    return token_list

def get_top_bigrams(tokens, n):
    """
    Gibt die n am häufigsten auftretenden Bigramme aus.
    """
    bigram_freq = dict()
    for i in range(len(tokens) - 1):
        bigram = (tokens[i], tokens[i+1])
        if bigram not in bigram_freq:
            bigram_freq[bigram] = 1
        else:
            bigram_freq[bigram] += 1
    return sorted(bigram_freq.items(), key=lambda x: x[1], reverse=True)[:n]

def get_top_trigrams(tokens, n):
    """
    Gibt die n am häufigsten auftretenden Trigramme aus.
    """
    trigram_freq = dict()
    for i in range(len(tokens) - 2):
        trigram = (tokens[i], tokens[i+1], tokens[i+2])
        if trigram not in trigram_freq:
            trigram_freq[trigram] = 1
        else:
            trigram_freq[trigram] += 1
    return sorted(trigram_freq.items(), key=lambda x: x[1], reverse=True)[:n]

if __name__ == "__main__":
    filename = "21000_filtered.txt"
    token_list = tokenize(filename)
    k = 20
    bigrams = get_top_bigrams(token_list, k)
    print(f"Top {k} Bigramme:")
    for bigram, count in bigrams:
        print(f"{bigram[0]} {bigram[1]}: {count}")

    trigrams = get_top_trigrams(token_list, k)
    print(f"\nTop {k} Trigramme:")
    for trigram, count in trigrams:
        print(f"{trigram[0]} {trigram[1]} {trigram[2]}: {count}")
    