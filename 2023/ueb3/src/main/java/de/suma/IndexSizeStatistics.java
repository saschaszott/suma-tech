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

    public int getDocCount() {
        return numOfDocs;
    }

    public void print() {
        System.out.println("~~~~~~~~~~~~ Auswertung für das Feld " + fieldName + " ~~~~~~~~~~~~\n");

        // Speicherbedarf für die Kollektion (hier wird vereinfachend angenommen, dass wir pro Zeichen 1 Byte benutzen)
        System.out.println("Speicherbedarf für die Kollektion mit " + numOfDocs + " Dokumenten: " + Math.round(numOfOverallCharactersInCollection / 1024) + " kB");

        for (int n = 1; n <= 3; n++) {
            // Speicherbedarf für das Dictionary (hier wird vereinfachend angenommen, dass wir pro Zeichen 1 Byte benötigen)
            int numOfCharactersInAllDictionaryTerms = 0;

            for (String term : dictionary.get(n - 1)) {
                numOfCharactersInAllDictionaryTerms += term.length();
            }

            System.out.println("Anzahl der Dictionary-Einträge (für n = " + n + "): " + dictionary.get(n - 1).size());
            long dictionarySize = Math.round(numOfCharactersInAllDictionaryTerms / 1024.0);
            System.out.println("Speicherbedarf für das Dictionary: " + dictionarySize + " kB");

            if (n == 1) {
                System.out.println("Anzahl der Positionseinträge in allen Postinglisten: " + numOfPositionsInAllPostingLists);
                // Dokument-ID wird einmal gespeichert; zusätzlich alle Positionen innerhalb des Dokuments, an denen ein Term auftritt
                // Speicherbedarf für eine Dokument-ID bzw. eine Positionsangabe beträgt 4 Byte (32-Bit int)
                long positionalIndexSize = dictionarySize + Math.round(4 / 1024.0 * (numOfDocIdsInAllPostingLists[0] + numOfPositionsInAllPostingLists));
                System.out.println("Speicherbedarf für Positional Index: " + positionalIndexSize + " kB");
            }

            System.out.println("Anzahl der DocId-Einträge in allen Postinglisten (für n = " + n + "): " + numOfDocIdsInAllPostingLists[n - 1]);
            // Speicherbedarf für eine Dokument-ID beträgt 4 Byte (32-Bit int)
            long nonPositionalIndexSize = dictionarySize + Math.round(4 / 1024.0 * numOfDocIdsInAllPostingLists[n - 1]);
            System.out.println("Speicherbedarf für Non-Positional Index (für n = " + n + "): " + nonPositionalIndexSize + " kB");
       }

        System.out.println("");
    }

}
