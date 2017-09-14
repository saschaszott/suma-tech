package de.suma;

import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class JaccardCoefficientTest {

    @Test
    public void testDocumentAsQuery() {
        int docId = 0;
        for (String document : JaccardCoefficient.documents) {
            JaccardCoefficient jaccardCoefficient = new JaccardCoefficient();
            List<SearchHit> ranking = jaccardCoefficient.computeRanking(document);
            assertTrue("non empty ranking expected", !ranking.isEmpty());
            assertEquals(docId, ranking.get(0).getId());
            assertEquals(JaccardCoefficient.documents[docId], ranking.get(0).getText());
            assertEquals(1.0d, ranking.get(0).getScore());
            docId++;
        }
    }

    @Test
    public void testEmptyQuery() {
        JaccardCoefficient jaccardCoefficient = new JaccardCoefficient();
        List<SearchHit> ranking = jaccardCoefficient.computeRanking("");
        assertTrue("empty ranking expected: found " + ranking.size() + " documents", ranking.isEmpty());
    }

    @Test
    public void testThatJaccardCoefficientIsIn01Interval() {
        for (String document : JaccardCoefficient.documents) {
            String[] parts = document.split("\\s+");
            for (String part : parts) {
                JaccardCoefficient jaccardCoefficient = new JaccardCoefficient();
                List<SearchHit> searchHits = jaccardCoefficient.computeRanking(part);
                assertTrue(!searchHits.isEmpty());
                for (SearchHit searchHit : searchHits) {
                    assertTrue("Jaccard coefficient cannot be lower than 0: found " + searchHit.getScore(), searchHit.getScore() > 0);
                    assertTrue("Jaccard coefficient cannot be greater than 1: found " + searchHit.getScore(), searchHit.getScore() <= 1);
                    assertTrue("document in ranking should contain query term", searchHit.getText().toLowerCase().contains(part.toLowerCase()));
                }
            }
        }

    }

}
