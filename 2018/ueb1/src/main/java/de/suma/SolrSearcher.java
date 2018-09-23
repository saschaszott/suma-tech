package de.suma;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.Scanner;

public class SolrSearcher {

    private SolrClient solrClient;

    /**
     * Verbindung zum Solr-Server herstellen
     */
    private void connect() {

        solrClient = new HttpSolrClient.Builder(Configuration.SOLR_SERVER_URL + Configuration.SOLR_CORE_NAME).build();

        // Verbindung zum Solr-Server prüfen
        try {
            SolrPing ping = new SolrPing();
            //ping.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);
            SolrPingResponse pingResponse = ping.process(solrClient);
            if (pingResponse.getStatus() != 0) {
                System.out.println("Es gab einen unerwarteten Fehler beim Ping auf den Solr-Server (Status-Code ist " + pingResponse.getStatus() + ")");
            }
            else {
                System.out.println("Ping zum Solr-Server war erfolgreich und dauerte " + pingResponse.getQTime() + " ms");
            }
        } catch (SolrServerException|IOException e) {
            // do something reasonable
            System.err.println(e);
        }
    }

    /**
     * bestehende Verbindung zum Solr-Server schließen
     */
    private void close() {
        if (solrClient != null) {
            try {
                solrClient.close();
            } catch (IOException e) {
                // do something reasonable
                e.printStackTrace();
            }
            finally {
                solrClient = null;
            }
        }
    }

    /**
     * Verwendung des Standard Query Parsers, der nur ein Index durchsucht (standardmäßig soll im Feld fulltext_stemmed
     * gesucht werden)
     *
     * @param queryString Suchanfrage
     */
    private void runQuery(String queryString) {
        SolrQuery query = new SolrQuery();

        query.setQuery(queryString);
        // TODO Standardsuchfeld definieren mittels Query-Parameter df
        query.set("fl", "*,score"); // Feldliste im Resultat (* bedeutet, dass alle Felder zurückgegeben werden, die als stored="true" markiert sind)
        query.setRows(10);

        QueryRequest req = new QueryRequest(query);
        //req.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);

        QueryResponse response = null;
        try {
            response = req.process(solrClient);
        } catch (SolrServerException|IOException e) {
            // do something reasonable
            System.err.println(e);
        }

        printRanking(response);

    }

    /**
     * Verwendung des DisMax Query Parsers, um eine feldübergreifende Suche zu ermöglichen
     *
     * Unterschiedliche Gewichtung der Suchfelder gemäß Aufgabenstellung
     *
     * Boosting auf Basis der Anzahl der Downloads in den letzten 30 Tagen (Log-gewichtet)
     *
     * @param queryString Suchanfrage
     */
    private void runQueryOnMultipleFields(String queryString) {
        SolrQuery query = new SolrQuery();

        query.setQuery(queryString);

        // TODO DisMax Query Parser verwenden und geeignete Parameter setzen
        query.set("defType", "TODO");
        query.set("qf", "TODO");
        query.set("bf", "TODO");
        query.set("fl", "*,score"); // Feldliste im Resultat (* bedeutet, dass alle Felder zurückgegeben werden, die als stored="true" markiert sind)
        query.setRows(10);

        QueryRequest req = new QueryRequest(query);
        //req.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);

        QueryResponse response = null;
        try {
            response = req.process(solrClient);
        } catch (SolrServerException|IOException e) {
            // do something reasonable
            System.err.println(e);
        }

        printRanking(response);

    }

    /**
     * Ausgabe des Suchtreffer im Ranking
     * @param response
     */
    private void printRanking(QueryResponse response) {
        if (response != null) {
            SolrDocumentList results = response.getResults();

            if (results.isEmpty()) {
                System.out.println("Ihre Suchanfrage ergab leider keine Treffer. Bitte modifizieren Sie ihre Anfrage.");
            }
            else {
                System.out.println("Trefferanzahl: " + results.getNumFound());
                System.out.println("~~~~~~~~~ Trefferliste ~~~~~~~");
                int rank = 1;
                for (SolrDocument doc : results) {
                    System.out.println("Treffer #" + rank);
                    System.out.println("\tID: " + doc.get("id"));
                    System.out.println("\tTitel: " + doc.get("title"));

                    // TODO weitere Indexfelder (mit stored=true) ausgeben

                    System.out.println("\tScore: " + doc.get("score"));
                    System.out.println("\n- - - - - - - - - - -");
                    rank++;
                }
            }
        }
    }

    /**
     * Einstiegsmethode
     *
     * Liest solange Suchanfragen vom Benutzer ein, bis dieser "quit!" eingibt.
     *
     * @param args
     */
    public static void main(String[] args) {

        SolrSearcher solrSearcher = new SolrSearcher();
        solrSearcher.connect();

        Scanner input = new Scanner(System.in);
        String query;

        while (true) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.print("search query >>> ");
            query = input.nextLine();

            if (query.equals("quit!")) {
                break;
            }

            solrSearcher.runQuery(query);
            // TODO solrSearcher.runQueryOnMultipleFields(query);
        }

        input.close();
        solrSearcher.close();
        System.out.println("Good Bye!");
    }

}
