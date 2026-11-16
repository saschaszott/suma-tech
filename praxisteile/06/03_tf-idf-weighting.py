import pandas as pd
import numpy as np

# Term Frequencies
tf = {
    'Affe':  [2, 1, 0],
    'Bär':   [1, 0, 2],
    'Maus':  [0, 0, 1],
    'Zebra': [0, 1, 0]
}
df = pd.DataFrame(tf, index=['D1', 'D2', 'D3'])

print("== Term Frequencies ==\n", df)

# Dokumentanzahl
N = len(df)

# Document Frequencies
df_counts = (df > 0).sum()

# Inverse Document Frequencies (logarithmisch)
# Addition von 1, damit log(1) nicht auftritt, wenn ein Term in allen Dokumenten vorkommt (df(t) = N)
# log ist der natürlicher Logarithmus (zur Basis e): ln
idf_log = np.log1p(N / df_counts) # entspricht np.log((N / df_counts) + 1)

tf_log = np.log1p(df) # entspricht np.log(df + 1)

print("\n== TF-IDF-Termgewichtsmatrix (ln-gedämpft) ==\n", tf_log.multiply(idf_log).round(3))