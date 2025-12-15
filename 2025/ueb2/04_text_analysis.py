import os
import regex
from collections import Counter
import heapq
import matplotlib.pyplot as plt

FULLTEXT_DIR = "pg-fulltexts"

# IDs von E-Books, die noch unter deutschem Urheberrecht stehen und daher nicht analysiert werden sollen
# Thomas Mann (endet am 1.1.2026) und Alfred Döblin (endet am 1.1.2028)
ID_BLACKLIST = [
    12108,
    12053,
    38692,
    31660,
    23313,
    36779,
    34811,
    35328,
    43932,
    36766,
    65662,
    65661,
    43987,
    13810,
    43931,
]

TOKEN_PATTERN = regex.compile(r"""
(
    https?://[^\s]+                                     # URLs
  | [\p{L}\p{N}._%+\-]+@[\p{L}\p{N}.\-]+\.[\p{L}]{2,}   # E-Mails
  | \p{N}+(?:[.,]\p{N}+)*                               # Zahlen mit Dezimalteilen
  | [\p{L}\p{M}]+(?:['’\-._][\p{L}\p{M}\p{N}]+)*        # Wörter mit internen Apostrophen/Bindestrichen/Punkten
)""", regex.VERBOSE | regex.UNICODE)

# Zeichen, die am Ende eines Tokens abgeschnitten werden sollen, falls sie "anheften"
_STRIP_EDGE = "._-’'"

def get_tokens_from_file(path):
    result = []
    with open(path, "r", encoding="utf-8", errors="ignore") as f:
        start_found = False
        for line in f:
            line = line.strip()
            if not start_found:
                if line.startswith("*** START OF "):
                    start_found = True
                continue
            if line.startswith("*** END OF "):
                return result
            result.extend(tokenize(line))
    return result

def tokenize(text):
    if not text:
        return []
    tokens = []
    for m in TOKEN_PATTERN.finditer(text):
        tok = m.group(1)
        cleaned = tok.strip(_STRIP_EDGE)
        if not cleaned:
            continue
        cleaned = cleaned.lower()

        # URLs herausfiltern
        if cleaned.startswith("http://") or cleaned.startswith("https://"):
            continue
        tokens.append(cleaned)
    return tokens

def ignore_file(path):
    fname = os.path.basename(path)
    if not fname.endswith(".txt"):
        return True
    id_str = os.path.splitext(fname)[0][2:] # "pg" am Anfang des Dateinamens entfernen
    try:
        ebook_id = int(id_str)
        if ebook_id in ID_BLACKLIST:
            return True
    except ValueError:
        return True
    return False

def update_longest_terms(longest_terms_heap, term, max_heap_size=10):
    # longest_terms_heap ist ein Min-Heap, der die bis zu max_heap_size längsten Terme speichert
    if len(longest_terms_heap) < max_heap_size:
        # der Heap hat noch nicht seine maximale Größe erreicht
        heapq.heappush(longest_terms_heap, term)
        return

    # Heap hat seine maximale Größe erreicht
    # Term mit der kürzesten Länge steht in der Wurzel
    # dieser Term muss verdrängt werden, wenn der neue Term (term) länger ist
    root_term = longest_terms_heap[0]
    if len(term) > len(root_term):
        heapq.heappushpop(longest_terms_heap, term)

def collect_tokens():
    if not os.path.isdir(FULLTEXT_DIR):
        print(f"Verzeichnis mit Volltextdateien {FULLTEXT_DIR} nicht gefunden.")
        exit(1)

    term_counter = Counter()
    term_to_docs = {}
    files_processed = 0
    total_token_len = 0
    total_tokens = 0

    for fname in os.listdir(FULLTEXT_DIR):
        fpath = os.path.join(FULLTEXT_DIR, fname)
        print(f"Verarbeite {fpath} ...", end="")
        if ignore_file(fpath):
            print(" (SKIPPED)")
            continue
        files_processed += 1
        terms = set()
        for tok in get_tokens_from_file(fpath):
            terms.add(tok)
            term_counter[tok] += 1
            total_token_len += len(tok)
            total_tokens += 1
        for term in terms:
            if term not in term_to_docs:
                term_to_docs[term] = []
            term_to_docs[term].append(fname)

        print(" (OK)")

    longest_terms = []
    # TODO aktuell wird nur der längste Term insgesamt ermittelt und in longest_terms gespeichert.
    # nun sollen jedoch die 10 längsten Terme ermitteln, wobei Sie hierfür die Methode update_longest_terms nutzen
    # können, die intern einen Min-Heap verwendet, um die 10 längsten Terme effizient zu verwalten.
    for term in term_to_docs.keys():
        if len(longest_terms) == 0:
            longest_terms.append(term)
            continue

        if len(term) > len(longest_terms[0]):
            longest_terms[0] = term

    return term_counter, total_tokens, total_token_len, longest_terms, term_to_docs, files_processed

def plot_term_frequencies(counter):
    freqs = [f for _, f in counter.most_common()]
    ranks = range(1, len(freqs) + 1)

    plt.figure(figsize=(16, 8))
    plt.plot(ranks, freqs, marker=".", linestyle="none", markersize=1)
    plt.yscale("log")
    plt.xlabel("Rang")
    plt.ylabel("absolute Häufigkeit (log)")
    plt.title("Rang-Häufigkeitsverteilung")
    plt.tight_layout()
    png_filename = "term_rank_frequency.png"
    plt.savefig(png_filename, dpi=300)
    print(f"Rang-Häufigkeitsverteilung gespeichert in {png_filename}.")

def plot_document_frequencies(term_to_docs, num_docs):
    # ermittle die Anzahl der Terme pro Dokumentfrequenz
    num_of_terms = Counter()
    for _, doc_list in term_to_docs.items():
        num_of_terms[len(doc_list)] += 1
    for doc_freq in range(1, num_docs + 1):
        if doc_freq not in num_of_terms:
            num_of_terms[doc_freq] = 0
        else:
            if num_of_terms[doc_freq] >= 1000:
                print(f"Dokumentfrequenz {doc_freq}: {num_of_terms[doc_freq]} Terme")

    plt.figure(figsize=(16, 8))
    plt.plot(num_of_terms.keys(), num_of_terms.values(), marker=".", linestyle="none", markersize=1)
    plt.yscale("log")
    plt.xlabel("Dokumentfrequenz")
    plt.ylabel("Anzahl Terme (log)")
    plt.title("Verteilung der Dokumentfrequenzen")
    plt.tight_layout()
    png_filename = "document_frequency_distribution.png"
    plt.savefig(png_filename, dpi=300)
    print(f"Verteilung der Dokumentfrequenzen gespeichert in {png_filename}.")

if __name__ == "__main__":
    counter, total_tokens, total_token_len, longest_terms, term_to_docs, files_processed = collect_tokens()

    if total_tokens == 0:
        print("Keine Tokens gefunden.")
        exit(1)

    avg_len = total_token_len / total_tokens
    top10 = counter.most_common(10)

    print(f"Anzahl verarbeiteter Dateien: {files_processed}")
    print(f"Anzahl Tokens (d.h. gefundener Term-Vorkommen): {total_tokens}")
    print(f"Anzahl Terme: {len(counter)}")
    print(f"Durchschnittliche Termlänge (über alle Vorkommen): {avg_len:.3f}")

    # TODO geben Sie hier die 10 längsten Terme aus
    print(f"Längster Term: {longest_terms[0]} (mit Länge {len(longest_terms[0])})")

    print("10 häufigsten Terme:")
    for term, freq in top10:
        print(f"\t{term}: {freq}")

    plot_term_frequencies(counter)
    plot_document_frequencies(term_to_docs, files_processed)