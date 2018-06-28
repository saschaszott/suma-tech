package de.suma.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Searcher {

    // Indexfeld, das für die Suche verwendet werden soll
    public static final String INDEX_FIELD = "content";

    // Top-k-Ranking berechnen
    public static final int k = 10;

    public void search() throws IOException, ParseException {

        QueryParser queryParser = new QueryParser(INDEX_FIELD, new StandardAnalyzer());
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(Indexer.INDEX_DIR)));
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        while (true) {
            System.out.println("Suchanfrage eingeben (oder ENTER zum Beenden): ");
            String queryString = in.readLine();
            if ("".equals(queryString)) {
                break;
            }

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
            for (ScoreDoc hit : hits) {
                Document document = indexSearcher.doc(hit.doc);

                String id = document.get("id");
                System.out.println("Doc ID: " + id);

                float score = hit.score;
                System.out.println("Score: " + score);

                String fileName = document.get("filename");
                System.out.println("File Name: " + fileName);
            }

        }

        indexReader.close();
    }

    public static void main(String[] args) throws IOException, ParseException {

        System.out.println("Suche auf dem zuvor generierten Lucene-Index im Verzeichnis " + Indexer.INDEX_DIR);

        Searcher searcher = new Searcher();
        searcher.search();

    }

}
