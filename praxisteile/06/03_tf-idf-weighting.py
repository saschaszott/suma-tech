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
idf_log = np.log(N / df_counts) # natürlicher Logarithmus (zur Basis e): ln

tf_log = df.replace(0, np.nan).map(lambda x: np.log(x + 1)).fillna(0) # ln

print("\n== TF-IDF-Termgewichtsmatrix ==\n", tf_log.multiply(idf_log).round(3))