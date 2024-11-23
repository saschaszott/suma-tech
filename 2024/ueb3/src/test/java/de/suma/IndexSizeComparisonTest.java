package de.suma;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexSizeComparisonTest {

    @Test
    void generateUnigram() {
        IndexSizeComparison isc = new IndexSizeComparison();
        List<String> unigrams = isc.getUnigrams("The quick brown fox jumps over the lazy dog");
        assertEquals(9, unigrams.size(), "Es sollten 9 Unigramme existieren.");
        assertArrayEquals(
                new String[]{"the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"},
                unigrams.toArray(),
                "Die erzeugten Unigramme sind fehlerhaft.");
    }

    @Test
    void generateUnigramWithEmptyText() {
        IndexSizeComparison isc = new IndexSizeComparison();
        List<String> unigrams = isc.getUnigrams("  ");
        assertEquals(0, unigrams.size(), "Es sollten keine Unigramme existieren.");
    }

    @Test
    void generateBigramWithShortText() {
        IndexSizeComparison isc = new IndexSizeComparison();
        String textToIndex = "shorttext";
        List<String> bigrams = isc.getBigrams(textToIndex);
        assertEquals(0, bigrams.size(), "Es sollten keine Bigramme existieren, weil der Text zu kurz ist.");
    }

    @Test
    void generateBigram() {
        IndexSizeComparison isc = new IndexSizeComparison();
        List<String> bigrams = isc.getBigrams("The quick brown fox jumps over the lazy dog");
        assertEquals(8, bigrams.size(), "Es sollten 8 Bigramme existieren.");
        assertArrayEquals(
                new String[]{
                        "the quick",
                        "quick brown",
                        "brown fox",
                        "fox jumps",
                        "jumps over",
                        "over the",
                        "the lazy",
                        "lazy dog"},
                bigrams.toArray(),
                "Die erzeugten Bigramme sind fehlerhaft.");
    }

    @Test
    void generateTrigramWithShortText() {
        IndexSizeComparison isc = new IndexSizeComparison();
        String textToIndex = "short text";
        List<String> trigram = isc.getTrigrams(textToIndex);
        assertEquals(0, trigram.size(), "Es sollten keine Trigramme existieren, weil der Text zu kurz ist.");
    }

    @Test
    void generateTrigram() {
        IndexSizeComparison isc = new IndexSizeComparison();
        List<String> trigram = isc.getTrigrams("The quick brown fox jumps over the lazy dog");
        assertEquals(7, trigram.size(), "Es sollten 7 Bigramme existieren.");
        assertArrayEquals(
                new String[]{
                        "the quick brown",
                        "quick brown fox",
                        "brown fox jumps",
                        "fox jumps over",
                        "jumps over the",
                        "over the lazy",
                        "the lazy dog"},
                trigram.toArray(),
                "Die erzeugten Trigramme sind fehlerhaft.");
    }
}
