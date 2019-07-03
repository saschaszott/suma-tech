package de.suma.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Erlaubt das Ausführen von Suchanfragen auf dem für die Shakespeare-Kollektion erzeugten Lucene-Index.
 *
 * @author Sascha Szott
 */
public class Searcher {

    // Top-k-Ranking berechnen
    public static final int k = 10;

    private IndexSearcher indexSearcher;

    private IndexReader indexReader;

    public Searcher() throws IOException {
        indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(Indexer.INDEX_DIR)));
        indexSearcher = new IndexSearcher(indexReader);
    }

    /**
     * Führt Suchanfragen aus und gibt die resultierenden Top-10-Rankings aus.
     * Durch das Absetzen einer leeren Suchanfrage wird das Programm beendet.
     *
     * @throws IOException
     */
    public void search() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        while (true) {
            System.out.println("Suchanfrage eingeben (oder ENTER zum Beenden): ");
            String queryString = in.readLine();
            if ("".equals(queryString)) {
                break;
            }

            // Angabe des Indexfeld, das für die Suche verwendet werden soll sowie eines Analyzers, der die Verarbeitung
            // der Suchanfrage definiert (hier werden z.B. alle Terme in der Suchanfrage in Kleinbuchstaben umgewandelt)
            QueryParser queryParser = new QueryParser(Indexer.FIELD_CONTENT, new StandardAnalyzer());
            Query q;
            try {
                q = queryParser.parse(queryString);
                System.out.println("ausgeführte Suchanfrage: " + q.toString());
            } catch (ParseException e) {
                System.err.println("Unerwarteter Fehler beim Parsen der Suchanfrage: " + e.getMessage());
                continue;
            }

            TopDocs results = indexSearcher.search(q, k);
            System.out.println("Es wurden " + results.totalHits + " Treffer gefunden");


            // die einzelnen Suchtreffer im Top-k-Ranking ausgeben
            ScoreDoc[] hits = results.scoreDocs;

            if (hits.length > 0) {

                System.out.println("Ausgabe des Top-" + k + "-Rankings");
                int rank = 1;

                for (ScoreDoc hit : hits) {

                    System.out.println("Treffer Nr. " + rank++);

                    Document document = indexSearcher.doc(hit.doc);

                    String id = document.get("id");
                    System.out.println("\tDoc ID: " + id);

                    float score = hit.score;
                    System.out.println("\tScore: " + score);

                    String fileName = document.get("filename");
                    System.out.println("\tFile Name: " + fileName);
                }
            }
        }
    }

    /**
     * Gibt einige statistische Informationen zu den im Index gespeicherten Feldern aus.
     *
     * @throws IOException
     */
    public void printStats() throws IOException {
        System.out.println("Anzahl Dokument im Index: " + indexReader.numDocs());

        for (String fieldName : new String[] {Indexer.FIELD_ID, Indexer.FIELD_FILENAME, Indexer.FIELD_CONTENT}) {

            System.out.println("Indexfeld " + fieldName);
            CollectionStatistics collectionStatistics = indexSearcher.collectionStatistics(fieldName);
            System.out.println("Summe aller df-Werte: " + collectionStatistics.sumDocFreq());
            System.out.println("Summe aller tf-Werte: " + collectionStatistics.sumTotalTermFreq());
        }

        System.out.println("Ausgabe einiger df-Werte für das Indexfeld " + Indexer.FIELD_CONTENT);
        for (String termStr : new String[] {"caesar", "calpurnia", "brutus"}) {
            Term term = new Term(Indexer.FIELD_CONTENT, termStr);
            System.out.println("df('" + termStr + "') = " + indexReader.docFreq(term));
            System.out.println("total-tf('" + termStr + "') = " + indexReader.totalTermFreq(term));
        }
    }

    /**
     * Hauptmethode des Programms
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Suche auf dem zuvor generierten Lucene-Index im Verzeichnis " + Indexer.INDEX_DIR);

        Searcher searcher = new Searcher();

        // Ausgabe einiger statistischer Informationen über den verwendeten Lucene-Index
        searcher.printStats();

        // Unterschiedliche Suchergebnisse für Indexfelder
        //
        // Entfernung von abschließendem 's im Indexfeld content_stemmed
        // Suche nach content:Diana liefert 4 Treffer
        // Suche nach content_stemmed:Diana liefert 9 Treffer, z.B. auch dream.xml (dort taucht der Term einmal in der Form Diana's auf)
        //
        // Stemming (Porter Algoritmus)
        // Suche nach content:books liefert 16 Treffer
        // Suche nach content_stemmed:books liefert 0 Treffer, weil der Term "books" aufgrund des Stemmings nicht im Dictionary existiert
        //
        // Suche nach content:swim liefert 8 Treffer
        // Suche nach content_stemmed:swim liefert 10 Treffer (z.B. auch "swims" in coriolan.xml sowie hen_iv_2.xml)
        searcher.search();

        searcher.indexReader.close();
    }

}
