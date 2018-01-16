package de.suma;

import java.util.*;

/**
 * Die Positional Postingliste für einen Term.
 * Enthält die Positionen des Auftretens des Terms in allen Dokumenten der Kollektion.
 *
 * Intern wird für jedes Dokument, das den Term enthält, die DocID als Schlüssel
 * in docIds gehalten. Als Wert wird die Liste der Positionen des Terms innerhalb
 * des Dokuments gespeichert.
 *
 */
public class PositionalPostingsList {

    private Map<Integer, Set<Integer>> docIds = new HashMap<Integer, Set<Integer>>();

    public void addEntry(int docId, int position) {
        Set<Integer> positions;
        if (!docIds.containsKey(docId)) {
            // docId wurde zum ersten Mal "gesehen"
            positions = new TreeSet<Integer>();
            positions.add(position);
        }
        else {
            // es gibt schon mindestens eine gespeicherte Position für das Dokument mit der übergebenen ID
            positions = docIds.get(docId);
            positions.add(position);
        }
        docIds.put(docId, positions);
    }

    public List<Integer> getDocIds() {
        List<Integer> result = new ArrayList<Integer>(docIds.keySet());
        Collections.sort(result);
        return result;
    }

    public List<Integer> getPositionsInDocument(int docId) {
        Set<Integer> positions = docIds.get(docId);
        if (positions == null || positions.isEmpty()) {
            return null;
        }
        return new ArrayList<Integer>(positions);
    }

    public String toString() {
        List<Integer> docIdsSorted = new ArrayList<Integer>(docIds.keySet());
        Collections.sort(docIdsSorted);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Integer docId : docIdsSorted) {
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append(docId + ": { ");
            Set<Integer> positions = docIds.get(docId);
            for (Integer position : positions) {
                sb.append(position + " ");
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

}
