package de.suma.misc;

import org.apache.lucene.search.spell.LuceneLevenshteinDistance;

public class LuceneDemo {

    public static void main(String[] args) {

        String queryTerm = "leut";
        String dictionaryTerm = "leute";

        LuceneLevenshteinDistance luceneLevenshteinDistance = new LuceneLevenshteinDistance();
        // Achtung: der Wert distance ist eigentlich eine Similarity,
        // d.h. 0: keine Ähnlichkeit; 1: maximale Ähnlichkeit
        float distance = luceneLevenshteinDistance.getDistance(queryTerm, dictionaryTerm);
        System.out.println("luceneLevenshteinDistance = " + distance);

        FixedLuceneLevenshteinDistance fixedLuceneLevenshteinDistance = new FixedLuceneLevenshteinDistance();
        distance = fixedLuceneLevenshteinDistance.getDistance(queryTerm, dictionaryTerm);
        System.out.println("fixedLuceneLevenshteinDistance = " + distance);

        float d = luceneLevenshteinDistance.getDistance("a", "bb");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "bb");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "aa");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "aa");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "a");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "a");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "bb");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "bb");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "aa");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "aa");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "aab");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "aab");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "baa");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "baa");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("a", "aba");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("a", "aba");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("", "1");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("", "1");
        System.out.println(d);

        System.out.println("~~~");

        d = luceneLevenshteinDistance.getDistance("ab", "ba");
        System.out.println(d);

        d = fixedLuceneLevenshteinDistance.getDistance("ab", "ba");
        System.out.println(d);

    }


}
