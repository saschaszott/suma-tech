package de.suma;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Ziel der Übung: Bestimmung des Speicherplatzes für das Dictionary sowie die Posting-Listen beim Positional Index und
 * Non-Positional Index im Vergleich zur Größe der zu indexierenden Dokumentkollektion. Beim Non-Positional Index werden
 * unterschiedliche Dictionary-Typen (Unigramm, Bigramm, Trigramm) betrachtet.
 *
 * Vergleich mit der Größe eines Solr/Lucene-Index.
 */
public class IndexSizeComparison {

    public static void main(String[] args) {
        IndexSizeComparison isc = new IndexSizeComparison();

        // Speicherplatz für unterschiedliche Varianten des invertierten Index für das Feld title
        isc.determineMemoryConsumption("title", Integer.MAX_VALUE);

        // Speicherplatz für unterschiedliche Varianten des invertierten Index für das Feld fulltext
        isc.determineMemoryConsumption("fulltext", 200);
    }

    /**
     * Ermittelt die Größe des Non-Positional Index und Positional Index für das übergebene Feld.
     * Über den Parameter maxDocsToConsider kann die Anzahl der für die Größenberechnung zu berücksichtigenden
     * Dokumente in der vorliegenden Dokumentkollektion limitiert werden.
     *
     * @param fieldName
     * @param maxDocsToConsider
     */
    private void determineMemoryConsumption(String fieldName, int maxDocsToConsider) {
        GutenbergRDFParser gutenbergRDFParser = new GutenbergRDFParser();
        File folder = new File("gutenberg" + File.separator + "selection");

        IndexSizeStatistics stats = new IndexSizeStatistics(fieldName);

        for (File file : folder.listFiles()) {
            try {
                if (stats.getDocCount() >= maxDocsToConsider) {
                    break; // maximale Dokumentanzahl erreicht - beende die Berechnung
                }
                stats.incDocCount();

                GutenbergDoc doc = gutenbergRDFParser.parse(file);

                // ermittle den zu indexierenden Dokumentinhalt und forme ihn in Kleinschreibung um
                String textToIndex = StringUtils.lowerCase(getTextToIndex(fieldName, doc));
                if (StringUtils.isEmpty(textToIndex)) {
                    continue;
                }

                // bei der Speicherung des Dokuments müssen alle Zeichen (auch sämtliche Leerzeichen zwischen den Token)
                // gespeichert werden, so dass für die Größenberechnung die Anzahl der Zeichen ausschlaggebend ist
                stats.addNumOfOverallCharactersInCollection(textToIndex.length());

                for (int n = 1; n <= 3; n++) {
                    // Tokenisierung durchführen
                    String[] tokens = null;
                    switch (n) {
                        case 1:
                            tokens = getUnigrams(textToIndex);
                            break;
                        case 2:
                            tokens = getBigrams(textToIndex);
                            break;
                        case 3:
                            tokens = getTrigrams(textToIndex);
                            break;
                    }

                    if (n == 1) {
                        // beim Positional Index betrachten wir nur Unigramme im Dictionary

                        // für jedes Token muss eine Positionsangabe im Positional Index gespeichert werden
                        stats.addNumOfPositionsInAllPostingLists(tokens.length);
                    }

                    // ermittle die Menge der Terme, die im aktuellen Dokument existieren (jedes Token wird nur einmal berücksichtigt)
                    Set<String> tokenSet = new HashSet<>(Arrays.asList(tokens));

                    // füge ggf. neu auftretende Terme zum Dictionary hinzu
                    stats.getDictionary(n).addAll(tokenSet);

                    // für jeden Term aus dem aktuellen Dokument müssen wir die Dokument-ID in die zugehörige Postingliste schreiben
                    stats.addNumOfDocIdsInAllPostingLists(n, tokenSet.size());

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Ausgabe der ermittelten Indexgrößen
        stats.print();
    }

    /**
     * Ermittelt den Inhalt des übergebenen Felds im vorliegenden Dokument.
     * @param fieldName Name des Felds
     * @param doc Dokument
     * @return
     */
    private String getTextToIndex(String fieldName, GutenbergDoc doc) {
        switch (fieldName) {
            case "title" :
                return doc.getTitle();
            case "fulltext" :
                return getFulltext(doc.getDocId());
            default:
                return null;
        }
    }

    /**
     * Liest die Volltextdatei des Gutenberg E-Books mit der übergebenen ID ein und gibt den Dateiinhalt zurück.
     *
     * @param docId ID des E-Books
     * @return Volltext des E-Books oder null, wenn kein Volltext vorhanden oder Fehler beim Einlesen
     */
    private String getFulltext(String docId) {
        File fulltext = new File("gutenberg" + File.separator + "fulltext" + File.separator + docId + ".txt");
        if (!fulltext.exists()) {
            return null;
        }

        String fulltextStr = null;
        try {
            fulltextStr = FileUtils.readFileToString(fulltext, "UTF-8");
        } catch (IOException e) {
            System.err.println("Fehler beim Einlesen der Volltextdatei von Dokument " + docId);
        }
        return fulltextStr;
    }

    private String[] getUnigrams(String textToIndex) {
        return ngrams(1, textToIndex);
    }

    private String[] getBigrams(String textToIndex) {
        return ngrams(2, textToIndex);
    }

    private String[] getTrigrams(String textToIndex) {
        return ngrams(3, textToIndex);
    }

    /**
     * Ermittelt alle n-Gramme für den übergebenen Text. Ein n-Gramm besteht hierbei aus n aufeinanderfolgenden Token.
     * @param n
     * @param textToIndex
     * @return
     */
    private String[] ngrams(int n, String textToIndex) {
        String[] tokens = StringUtils.split(textToIndex);
        if (n == 1) {
            return tokens;
        }
        if (tokens.length - n < 0) {
            // es können keine n-Gramme gebildet werden, da der übergebene Text zu kurz ist
            return new String[0];
        }

        // TODO berechnen Sie die n-Gramme (n > 1) für den übergebenen Text
        // TODO ein n-Gramm ist eine Folge von n aufeinanderfolgenden Token (jeweils durch Leerzeichen getrennt)
        return null; // TODO entfernen und durch das von ihnen berechnete Ergebnis ersetzen
    }
}
