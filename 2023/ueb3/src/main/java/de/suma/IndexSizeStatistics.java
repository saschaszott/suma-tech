package de.suma;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndexSizeStatistics {

    /**
     * Name des Felds, das indexiert werden soll
     */
    private String fieldName;

    /**
     * Anzahl der Dokumente, die indexiert werden
     */
    private int numOfDocs;

    /**
     * Menge der Terme im Dictionary des invertierten Index (für die unterschiedlichen Dictionary-Arten: Unigramme, Bigramme, Trigramme)
     */
    private List<Set<String>> dictionary;

    /**
     * Gesamtanzahl aller Zeichen der zu indexierenden Dokumentkollektion (inklusive Leerzeichen)
     */
    private int numOfOverallCharactersInCollection;

    /**
     * Gesamtanzahl aller Dokument-IDs in allen Posting-Listen des Non-Positional Index (für die unterschiedlichen Dictionary-Arten)
     */
    private int[] numOfDocIdsInAllPostingLists = new int[3];

    /**
     * Gesamtanzahl aller Positionsangaben in allen Posting-Listen des Positional Index
     */
    private int numOfPositionsInAllPostingLists;

    public IndexSizeStatistics(String fieldName) {
        this.fieldName = fieldName;

        // drei leere Dictionaries initialisieren
        dictionary = new ArrayList<>(3);
        dictionary.add(new HashSet<>()); // für Unigramme (Dictionary-Einträge besteht aus einzelnen Termen)
        dictionary.add(new HashSet<>()); // für Bigramme (Dictionary-Einträge bestehen aus zwei aufeinanderfolgenden Termen)
        dictionary.add(new HashSet<>()); // für Trigramme (Dictionary-Einträge bestehen aus drei aufeinanderfolgenden Termen)
    }

    public Set<String> getDictionary(int n) {
        return dictionary.get(n - 1);
    }

    public void addNumOfOverallCharactersInCollection(int value) {
        numOfOverallCharactersInCollection += value;
    }

    public void addNumOfPositionsInAllPostingLists(int value) {
        numOfPositionsInAllPostingLists += value;
    }

    public void addNumOfDocIdsInAllPostingLists(int n, int value) {
        numOfDocIdsInAllPostingLists[n - 1] += value;
    }

    public void incDocCount() {
        numOfDocs++;
    }

    public long getSizeOfNWordDictionary(int n) {
        // Speicherbedarf für das Dictionary, wobei hier vereinfachend angenommen wird, dass pro Zeichen 1 Byte benötigt wird
        int numOfCharactersInAllDictionaryTerms = 0;
        for (String term : dictionary.get(n - 1)) {
            numOfCharactersInAllDictionaryTerms += term.length();
        }

        // TODO berechnen Sie den Speicherplatz in kB für das Dictionary mit n-Grammen (d.h. für Folgen von n
        //  aufeinanderfolgenden Termen)
        // TODO Hinweis: vermeiden Sie hierbei die Verwendung der Methode toString()

        return 0;
    }

    public long getOverallSizeOfPositionalIndex(long dictionarySize) {
        // in der Postingliste eines Terms t: Dokument-ID wird einmal gespeichert; zusätzlich alle Positionen innerhalb des Dokuments, an denen Term t auftritt
        // Speicherbedarf für eine Dokument-ID bzw. eine Positionsangabe beträgt 4 Byte (32-Bit int)

        // TODO berechnen Sie hier den Speicherplatz für den Positional Index in kB
        long positionalIndexSize = dictionarySize + 0;

        return positionalIndexSize;
    }

    public long getOverallSizeOfNonPositionalNWordIndex(int n, long dictionarySize) {
        // Speicherbedarf für eine Dokument-ID beträgt 4 Byte (32-Bit int)

        // TODO berechnen Sie hier den Speicherplatz für den Non-Positional Index in kB
        //  mit n-Grammen (Folge von n aufeinanderfolgenden Termen) im Dictionary
        long nonPositionalIndexSize = dictionarySize + 0;

        return nonPositionalIndexSize;
    }

    public void print() {
        System.out.println("~~~~~~~~~~~~ Auswertung für das Feld " + fieldName + " ~~~~~~~~~~~~\n");

        // Speicherbedarf für die Kollektion (hier wird vereinfachend angenommen, dass wir pro Zeichen 1 Byte benutzen)
        System.out.println("Speicherbedarf für die Kollektion mit " + numOfDocs + " Dokumenten: " + Math.round(numOfOverallCharactersInCollection / 1024) + " kB");

        for (int n = 1; n <= 3; n++) {
            System.out.println("Anzahl der Dictionary-Einträge (für n = " + n + "): " + dictionary.get(n - 1).size());

            long dictionarySize = getSizeOfNWordDictionary(n);
            System.out.println("Speicherbedarf für das Dictionary: " + dictionarySize + " kB");

            if (n == 1) {
                System.out.println("Anzahl der Positionseinträge in allen Postinglisten: " + numOfPositionsInAllPostingLists);
                System.out.println("Speicherbedarf für Positional Index: " + getOverallSizeOfPositionalIndex(dictionarySize) + " kB");
            }

            System.out.println("Anzahl der DocId-Einträge in allen Postinglisten (für n = " + n + "): " + numOfDocIdsInAllPostingLists[n - 1]);

            System.out.println("Speicherbedarf für Non-Positional Index (für n = " + n + "): " + getOverallSizeOfNonPositionalNWordIndex(n, dictionarySize) + " kB");
       }

        System.out.println("");
    }

}
