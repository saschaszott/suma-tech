package de.suma;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexSizeStatisticsTest {

    // der zu indexierende Test
    private static final String TEXT_TO_INDEX = "to be or not to be that is the question";

    private static final IndexSizeComparison indexSizeComparison = new IndexSizeComparison();

    @Test
    void testDictionarySizeOfUniwordIndex() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        Set<String> tokenSet = new HashSet<>(indexSizeComparison.getUnigrams(TEXT_TO_INDEX));
        stats.getDictionary(1).addAll(tokenSet);
        assertEquals(
                26,
                stats.getSizeOfNWordDictionaryInBytes(1),
                "Es gibt 8 unterschiedliche Terme im Term-Dictionary, die aus insgesamt 26 Zeichen bestehen.");
    }

    @Test
    void testDictionarySizeOfBiwordIndex() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        Set<String> tokenSet = new HashSet<>(indexSizeComparison.getBigrams(TEXT_TO_INDEX));
        stats.getDictionary(2).addAll(tokenSet);
        assertEquals(
                46 + 8,
                stats.getSizeOfNWordDictionaryInBytes(2),
                "Es gibt 8 Biwords und die Gesamtzahl der Zeichen in allen Biwords ist 54, wobei pro Biword ein Leerzeichen mitzuzählen ist.");
    }

    @Test
    void testDictionarySizeOfTriwordIndex() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        Set<String> tokenSet = new HashSet<>(indexSizeComparison.getTrigrams(TEXT_TO_INDEX));
        stats.getDictionary(3).addAll(tokenSet);
        assertEquals(
                65 + 2 * 8,
                stats.getSizeOfNWordDictionaryInBytes(3),
                "Es gibt 8 Triwords und die Gesamtzahl der Zeichen in allen Triwords ist 65, wobei pro Triword zwei Leerzeichen mitzuzählen sind.");
    }

    @Test
    void testPositionalIndexSizeWithSingleDoc() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        List<String> unigrams = indexSizeComparison.getUnigrams(TEXT_TO_INDEX);
        stats.addNumOfPositionsInAllPostingLists(unigrams.size());
        Set<String> tokenSet = new HashSet<>(unigrams);
        stats.getDictionary(1).addAll(tokenSet);
        stats.addNumOfDocIdsInAllPostingLists(1, tokenSet.size());
        assertEquals(
                26 + 4 * 8 + 4 * 10,
                stats.getOverallSizeOfPositionalIndexInBytes(stats.getSizeOfNWordDictionaryInBytes(1)),
                "Die Speichergröße des Positional Index ergibt sich als Summe aus der Speichergröße " +
                "des Term-Dictionary sowie der Speichergröße für die DocIds in allen Positinglisten sowie " +
                "der Speichergröße für die Positionsangaben.");
    }

    @Test
    void testNonPositionalUniwordIndexWithSingleDoc() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        List<String> unigrams = indexSizeComparison.getUnigrams(TEXT_TO_INDEX);
        stats.addNumOfPositionsInAllPostingLists(unigrams.size());
        Set<String> tokenSet = new HashSet<>(unigrams);
        stats.getDictionary(1).addAll(tokenSet);
        stats.addNumOfDocIdsInAllPostingLists(1, tokenSet.size());
        assertEquals(
                4 * 8,
                stats.getOverallSizeOfNonPositionalNWordIndexInBytes(1, 0),
                "Die Speichergröße des Non-Positional Uniword Index ergibt sich als Summe aus der " +
                "Speichergröße für das Term-Dictionary (aus einzelnen Termen) sowie die Speichergröße für die " +
                "DocIds in allen Postinglisten.");
    }

    @Test
    void testNonPositionalBiwordIndexWithSingleDoc() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        List<String> unigrams = indexSizeComparison.getUnigrams(TEXT_TO_INDEX);
        stats.addNumOfPositionsInAllPostingLists(unigrams.size());
        Set<String> tokenSet = new HashSet<>(unigrams);
        stats.getDictionary(2).addAll(tokenSet);
        stats.addNumOfDocIdsInAllPostingLists(2, tokenSet.size());
        assertEquals(
                8 * 4,
                stats.getOverallSizeOfNonPositionalNWordIndexInBytes(2, 0),
                "Die Speichergröße des Non-Positional Biword Index ergibt sich als Summe aus der " +
                "Speichergröße für das Term-Dictionary (aus Term-Paaren) sowie die Speichergröße für die " +
                "DocIds in allen Postinglisten.");
    }

    @Test
    void testNonPositionalTriwordIndexWithSingleDoc() {
        IndexSizeStatistics stats = new IndexSizeStatistics("title");
        List<String> unigrams = indexSizeComparison.getUnigrams(TEXT_TO_INDEX);
        stats.addNumOfPositionsInAllPostingLists(unigrams.size());
        Set<String> tokenSet = new HashSet<>(unigrams);
        stats.getDictionary(3).addAll(tokenSet);
        stats.addNumOfDocIdsInAllPostingLists(3, tokenSet.size());
        assertEquals(
                8 * 4,
                stats.getOverallSizeOfNonPositionalNWordIndexInBytes(3, 0),
                "Die Speichergröße des Non-Positional Triword Index ergibt sich als Summe aus der " +
                "Speichergröße für das Term-Dictionary (aus Term-Tripeln) sowie die Speichergröße für die " +
                "DocIds in allen Postinglisten.");
    }

}
