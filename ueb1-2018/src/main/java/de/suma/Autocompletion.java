package de.suma;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.params.TermsParams;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Autocompletion {

    private SolrClient solrClient;

    /**
     * Verbindung zum Solr-Server herstellen
     */
    private void connect() {

        solrClient = new HttpSolrClient.Builder(Configuration.SOLR_SERVER_URL + Configuration.SOLR_CORE_NAME).build();

        // Verbindung zum Solr-Server prüfen
        try {
            SolrPing ping = new SolrPing();
            ping.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);
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
     * Gibt numOfSuggestions viele Vervollständigungsvorschläge auf Basis der Benutzereingabe prefix aus.
     * Hierbei wird das Indexfeld indexFieldName als Basis verwendet.
     *
     * @param prefix momentane Benutzereingabe (Präfix des Suchterms)
     * @param indexFieldName Indexfeld, das für die Vervollständigung verwendet werden soll
     * @param numOfSuggestions Anzahl der Vervollständigungsvorschläge
     *
     */
    private void showTerms(String prefix, String indexFieldName, int numOfSuggestions) {

        // TODO Implementierung gemäß Aufgabenstellung

    }

    /**
     * Gibt für das übergebene Indexfeld die drei Indexterme aus, die die größte Document Frequency haben.
     * Es werden hierbei aber nur Indexterme betrachtet, die mit einem Buchstaben beginnen.
     *
     * @param indexFieldName Feldname des Indexfelds
     */
    private void printStats(String indexFieldName) {
        System.out.println("- - - - - Field Statistics for Index Field " + indexFieldName + " - - - - -");
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/terms");
        query.setTermsLimit(3);
        query.setTermsSortString(TermsParams.TERMS_SORT_COUNT);
        query.addTermsField(indexFieldName);
        query.setTermsRegex("^[a-zA-Z].*");

        QueryRequest request = new QueryRequest(query);
        request.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);
        try {
            List<TermsResponse.Term> terms = request.process(solrClient).getTermsResponse().getTerms(indexFieldName);
            if (terms != null) {
                for (TermsResponse.Term term : terms) {
                    System.out.println(term.getTerm() + " (df = " + term.getFrequency() + ")");
                }
            }
        } catch (SolrServerException|IOException e) {
            System.err.println("Fehler bei der Term-Lookup im Dictionary");
        }
    }

    /**
     * Einstiegsmethode
     *
     * Liest nacheinander einzelne Zeichen ein und zeigt Vervollständigungsvorschläge an.
     *
     * @param args
     */
    public static void main(String[] args) {

        Autocompletion solrSearcher = new Autocompletion();
        solrSearcher.connect();

        solrSearcher.printStats("title");
        solrSearcher.printStats("fulltext");
        solrSearcher.printStats("author");

        Scanner input = new Scanner(System.in);
        String query = "";

        while (true) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.print("search query >>> " + query);
            query += input.nextLine();

            if (query.endsWith(" ")) {
                break;
            }

            solrSearcher.showTerms(query, "fulltext", 15);
        }

        input.close();
        solrSearcher.close();
        System.out.println("Good Bye!");
    }

}
