package de.suma;

import java.util.*;

public class JaccardCoefficient {

    // define the set of test documents for which we like to compute a ranking based on Jaccard coefficient
    public static final String[] documents = new String[] {
            "Die Mauer in der Berliner Straße",
            "Der Bauer der Berliner Mauer",
            "Chinesische Mauer",
            "Brandenburger Seen"
    };


    public List<SearchHit> computeRanking(String query) {

        // compute the Jaccard coefficient for every doc-query pair
        Map<Integer, Double> documentScores = new HashMap<>();
        Set<Double> scoresValues = new TreeSet<>();
        int docId = 0;
        for (String document : documents) {
            double jaccardCoefficient = computeJaccardCoefficient(query, document);
            scoresValues.add(jaccardCoefficient);
            if (jaccardCoefficient > 0) {
                // ignore documents with score value of 0 since they won't show up in the final ranking
                documentScores.put(docId, jaccardCoefficient);
            }
            docId++;
        }

        // create a SearchHit object for every document in the final ranking
        List<SearchHit> resultList = new LinkedList<>();
        for (Double scoreValue : scoresValues) {
            for (int documentId : documentScores.keySet()) {
                if (documentScores.get(documentId).equals(scoreValue)) {
                    SearchHit searchHit = new SearchHit();
                    searchHit.setId(documentId);
                    searchHit.setText(documents[documentId]);
                    searchHit.setScore(scoreValue);

                    // add element at the beginning (since we output SearchHits in decreasing order of score values)
                    resultList.add(0, searchHit);
                }
            }
        }

        return resultList;
    }

    private double computeJaccardCoefficient(String query, String document) {

        String[] queryParts = query.toLowerCase().trim().split("\\s+");
        Set<String> qSet = new HashSet<>(Arrays.asList(queryParts));

        String[] documentParts = document.toLowerCase().trim().split("\\s+");
        Set<String> dSet = new HashSet<>(Arrays.asList(documentParts));

        Set<String> unionSet = new HashSet<>();
        unionSet.addAll(qSet);
        unionSet.addAll(dSet);

        Set<String> intersectionSet = new HashSet<>();
        intersectionSet.addAll(qSet);
        intersectionSet.retainAll(dSet);

        return intersectionSet.size() * 1.0d / unionSet.size();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String query;

        while (true) {
            System.out.print("search query >>> ");
            query = input.nextLine();

            if (query.equals("quit!")) {
                break;
            }

            JaccardCoefficient jaccardCoefficient = new JaccardCoefficient();
            List<SearchHit> ranking = jaccardCoefficient.computeRanking(query);

            if (ranking.isEmpty()) {
                System.out.println("Leider gab es für Ihre Anfrage keine Treffer ;(");
            }
            else {
                System.out.println("Es wurden " + ranking.size() + " Treffer gefunden. Nachfolgend das Ranking der Dokumente:");
                for (SearchHit searchHit : ranking) {
                    System.out.println(searchHit);
                }
            }
        }
    }

}
