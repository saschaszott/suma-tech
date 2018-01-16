package de.suma;

import java.util.ArrayList;
import java.util.List;

public class PositionalIntersection {

    private PositionalIndex index;

    public static void main(String[] args) {
        PositionalIntersection pi = new PositionalIntersection();

        List<List<Integer>> result = pi.positionalIntersect("foo", "bar", 1, true);
        System.out.println("q = foo/1 bar");
        printResult(result);

        result = pi.positionalIntersect("foo", "bar", 1, false);
        System.out.println("q = \"foo bar\"");
        printResult(result);

        result = pi.positionalIntersect("bar", "foo", 2, true);
        System.out.println("q = bar/2 foo");
        printResult(result);

        result = pi.positionalIntersect("bar", "foo", 4, true);
        System.out.println("q = bar/4 foo");
        printResult(result);

        result = pi.positionalIntersect("bar", "foo", 1, false);
        System.out.println("q = \"bar foo\"");
        printResult(result);

    }

    private static void printResult(List<List<Integer>> result) {
        for (List<Integer> match : result) {
            System.out.println(match.get(0) + ": " + match.get(1) + " " + match.get(2));
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public PositionalIntersection() {
        index = new PositionalIndex();

        index.addPosition("foo", 7, 11);

        index.addPosition("foo", 12, 10);
        index.addPosition("foo", 12, 19);
        index.addPosition("foo", 12, 39);
        index.addPosition("foo", 12, 87);
        index.addPosition("foo", 12, 112);

        index.addPosition("bar", 12, 18);
        index.addPosition("bar", 12, 20);
        index.addPosition("bar", 12, 44);
        index.addPosition("bar", 12, 86);
        index.addPosition("bar", 12, 109);
        index.addPosition("bar", 12, 114);

        index.addPosition("bar", 32, 12);

        index.printIndex();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    /**
     *
     * @param term1 erster Term
     * @param term2 zweiter Term
     * @param offset Parameter, der angibt, wie groß der maximale Abstand zwischen den beiden
     *               übergebenen Termen sein darf, damit ein "Positional Match" gefunden wird
     * @param bothSides soll die Reihenfolge der beiden Terme bei der Bestimmung eines Matchings betrachtet werden
     *                  wenn false, dann muss der zweite Term nach dem ersten Term stehen
     *                  wenn true, dann ist die Reihenfolge der beiden Terme unerheblich
     */
    private List<List<Integer>> positionalIntersect(String term1, String term2, int offset, boolean bothSides) {

        PositionalPostingsList pp1 = index.getPositionalPostings(term1);
        PositionalPostingsList pp2 = index.getPositionalPostings(term2);

        List<Integer> docIds1 = pp1.getDocIds();
        List<Integer> docIds2 = pp2.getDocIds();

        List<List<Integer>> positionMatches = new ArrayList<List<Integer>>();

        int cnt1 = 0; // Zeiger auf die Einträge in docIds1
        int cnt2 = 0; // Zeiger auf die Einträge in docIds2

        while (cnt1 < docIds1.size() && cnt2 < docIds2.size()) {
            int docId1 = docIds1.get(cnt1);
            int docId2 = docIds2.get(cnt2);

            if (docId1 == docId2) {

                // beide Terme tauchen im selben Dokument (mit der ID docId1) auf
                // nun müssen die Positionen des Auftretens der beiden Terme verglichen werden
                // sie dürfen maximal um offset viele Positionen voneinander abweichen

                List<Integer> posList1 = pp1.getPositionsInDocument(docId1);
                List<Integer> posList2 = pp2.getPositionsInDocument(docId1);

                for (Integer pos1 : posList1) {
                    for (Integer pos2 : posList2) {

                        boolean matchingFound = (Math.abs(pos1.intValue() - pos2.intValue()) <= offset);
                        if (!bothSides && matchingFound) {
                            // prüfe noch, dass Term term2 tatsächlich nach Term term1 auftritt
                            matchingFound = pos2.intValue() > pos1.intValue();
                        }

                        if (matchingFound) {
                            List<Integer> match = new ArrayList<Integer>(3);
                            match.add(docId1);
                            match.add(pos1);
                            match.add(pos2);
                            positionMatches.add(match);
                        }
                        else if (pos2.intValue() > pos1.intValue()) {
                            // wir können den aktuellen Durchlauf von posList2 hier abbrechen, da
                            // die Positionseinträge in pos2List aufsteigend sortiert sind und wir bereits
                            // eine Position gefunden haben, die mehr als offset viele Positionen
                            // von pos1 entfernt ist
                            break; // betrachte die nächste Positionsangabe in pos1List1
                        }
                    }
                }
                cnt1++;
                cnt2++;
            }
            else if (docId1 < docId2) {
                cnt1++;
            }
            else {
                cnt2++;
            }
        }

        return positionMatches;
    }
}
