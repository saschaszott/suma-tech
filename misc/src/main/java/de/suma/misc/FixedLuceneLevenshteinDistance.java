package de.suma.misc;

import org.apache.lucene.util.IntsRef;

public final class FixedLuceneLevenshteinDistance {

    public float getDistance(String target, String other) {
        IntsRef targetPoints;
        IntsRef otherPoints;
        int n;
        int d[][]; // cost array

        // NOTE: if we cared, we could 3*m space instead of m*n space, similar to
        // what LevenshteinDistance does, except cycling thru a ring of three
        // horizontal cost arrays... but this comparator is never actually used by
        // DirectSpellChecker, it's only used for merging results from multiple shards
        // in "distributed spellcheck", and it's inefficient in other ways too...

        // cheaper to do this up front once
        targetPoints = toIntsRef(target);
        otherPoints = toIntsRef(other);
        n = targetPoints.length;
        final int m = otherPoints.length;
        d = new int[n+1][m+1];

        if (n == 0 || m == 0) {
            return 0; // FIX
        }

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        int t_j; // jth character of t

        int cost; // cost

        for (i = 0; i<=n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j<=m; j++) {
            d[0][j] = j;
        }

        for (j = 1; j<=m; j++) {
            t_j = otherPoints.ints[j-1];

            for (i=1; i<=n; i++) {
                cost = targetPoints.ints[i-1]==t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i][j] = Math.min(Math.min(d[i-1][j]+1, d[i][j-1]+1), d[i-1][j-1]+cost);
                // transposition
                if (i > 1 && j > 1 && targetPoints.ints[i-1] == otherPoints.ints[j-2] && targetPoints.ints[i-2] == otherPoints.ints[j-1]) {
                    d[i][j] = Math.min(d[i][j], d[i-2][j-2] + cost);
                }
            }
        }

        return 1.0f - ((float) d[n][m] / Math.max(m, n)); // FIX
    }

    private static IntsRef toIntsRef(String s) {
        IntsRef ref = new IntsRef(s.length()); // worst case
        int utf16Len = s.length();
        for (int i = 0, cp = 0; i < utf16Len; i += Character.charCount(cp)) {
            cp = ref.ints[ref.length++] = Character.codePointAt(s, i);
        }
        return ref;
    }
}