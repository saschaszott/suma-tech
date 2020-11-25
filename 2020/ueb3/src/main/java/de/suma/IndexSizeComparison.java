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

    private static final String INDEX_FIELD_TITLE = "title";
    private static final String INDEX_FIELD_FULLTEXT = "fulltext";

    private static final String DIR_NAME_METADATA = "selection";
    private static final String DIR_NAME_FULLTEXTS = "fulltext";
    private static final String BASE_DIR_NAME = "ueb3-ebooks-sample";

    public static void main(String[] args) throws FileNotFoundException {
        IndexSizeComparison isc = new IndexSizeComparison();

        // Speicherplatz für unterschiedliche Varianten des invertierten Index für das Feld title
        isc.determineMemoryConsumption(INDEX_FIELD_TITLE);

        // Speicherplatz für unterschiedliche Varianten des invertierten Index für das Feld fulltext
        isc.determineMemoryConsumption(INDEX_FIELD_FULLTEXT);
    }

    /**
     * Ermittelt die Größe des Non-Positional Index und Positional Index für das übergebene Feld.
     *
     * @param fieldName
     */
    private void determineMemoryConsumption(String fieldName) throws FileNotFoundException {
        GutenbergRDFParser gutenbergRDFParser = new GutenbergRDFParser();
        String dirName = BASE_DIR_NAME + File.separator + DIR_NAME_METADATA;
        File folder = new File(dirName);

        if (folder == null || !folder.exists()) {
            throw new FileNotFoundException("Metadaten-Verzeichnis konnte nicht unter " + dirName + " gefunden werden");
        }

        IndexSizeStatistics stats = new IndexSizeStatistics(fieldName);

        for (File file : folder.listFiles()) {
            try {
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
    private String getTextToIndex(String fieldName, GutenbergDoc doc) throws FileNotFoundException {
        switch (fieldName) {
            case INDEX_FIELD_TITLE :
                return doc.getTitle();
            case INDEX_FIELD_FULLTEXT :
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
    private String getFulltext(String docId) throws FileNotFoundException {
        String dirName = BASE_DIR_NAME + File.separator + DIR_NAME_FULLTEXTS + File.separator + docId + ".txt";
        File fulltext = new File(dirName);
        if (fulltext == null || !fulltext.exists()) {
            throw new FileNotFoundException("Volltext-Verzeichnis konnte nicht unter " + dirName + " gefunden werden");
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
