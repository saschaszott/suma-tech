package de.suma;

import java.util.*;

/**
 * Ein Positional Index besteht aus einem Term-Dictionary. Jedem Term ist eine Postional Postings List
 * zugeordnet. Neben der ID des Dokuments, in dem der Term vorkommt, wird auch noch die Position des
 * Terms im Dokument gespeichert. Kommt ein Term mehrfach in einem Dokument vor, so werden alle
 * Positionen des Auftretens des Terms in der Positional Postings List gespeichert.
 *
 * Im Vergleich zu einem Non-Positional Index können innerhalb einer Postingliste im Positional Index
 * mehrere Einträge (Positionen) pro Dokument-ID existieren.
 *
 */
public class PositionalIndex {

    private Map<String, PositionalPostingsList> index = new HashMap<String, PositionalPostingsList>();

    public void addPosition(String term, int docId, int position) {
        PositionalPostingsList ppl = index.get(term);
        if (ppl == null) {
            // es wird erstmalig eine Position für den Term term erfasst
            ppl = new PositionalPostingsList();
        }
        ppl.addEntry(docId, position);
        index.put(term, ppl);
    }

    public PositionalPostingsList getPositionalPostings(String term) {
        return index.get(term);
    }

    public void printIndex() {
        List<String> terms = new ArrayList<String>(index.keySet());
        Collections.sort(terms);

        for (String term : terms) {
            PositionalPostingsList ppl = index.get(term);
            System.out.println(term + ": " + ppl);
        }
    }

}
