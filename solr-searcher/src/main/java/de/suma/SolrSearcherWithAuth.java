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

/**
 * Dieses Programm demonstriert die grundlegenden Schritte um mittels Java auf einen Solr-Server zuzugreifen,
 * eine Suchanfrage abzusetzen und das Ergebnis schließlich auf der Kommandozeile auszugeben.
 *
 * Hinweise zur SolrJ-Library unter https://lucene.apache.org/solr/guide/6_6/using-solrj.html
 *
 * @author Sascha Szott
 * @version 1.0
 *
 */
public class SolrSearcherWithAuth {

    private static final boolean DEBUG = false;


    // wenn Sie das Programm innerhalb der Ubuntu-VM ausführen, dann schreiben Sie statt der IP einfach localhost
    // in meinem Fall nutze ich die Entwicklungsumgebung direkt auf dem Wirt und lasse nur den Solr-Server in der VM laufen
    // in der VM habe ich die IP-Adresse mittels des Befehls ifconfig ermittelt
    private static final String SOLR_SERVER_URL = "http://192.168.0.16:8983/solr/";


    // diesen Core haben wir in der ersten Übung bereits angelegt
    private static final String SOLR_CORE_NAME = "shakespeare";


    // Zugangsdaten für den Zugriff auf den Solr-Server
    public static final String USERNAME = "sumatech";
    public static final String PASSWORD = "suma!tech$17";

    private SolrClient solrClient;

    /**
     * Verbindung zum Solr-Server herstellen
     */
    private void connect() {

        solrClient = new HttpSolrClient.Builder(SOLR_SERVER_URL + SOLR_CORE_NAME).build();

        // Verbindung zum Solr-Server prüfen
        try {
            SolrPing ping = new SolrPing();
            ping.setBasicAuthCredentials(USERNAME, PASSWORD);
            SolrPingResponse pingResponse = ping.process(solrClient);
            if (pingResponse.getStatus() != 0) {
                System.out.println("Es gab einen unerwarteten Fehler beim Ping auf den Solr-Server (Status-Code ist " + pingResponse.getStatus() + ")");
            }
            else {
                System.out.println("Ping zum Solr-Server war erfolgreich und dauerte " + pingResponse.getQTime() + " ms");
            }
        } catch (SolrServerException e) {
            // do something reasonable
            System.err.println(e);
        } catch (IOException e) {
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
     * Anfrage zum Solr-Server schicken und Ergebnis auf der Kommandozeile ausgeben
     *
     * @param queryString Suchanfrage
     */
    private void runQuery(String queryString) {
        SolrQuery query = new SolrQuery();

        query.setQuery(queryString);
        query.set("fl", "*,score"); // Feldliste im Resultat (* bedeutet, dass alle Felder zurückgegeben werden, die als stored="true" markiert sind)
        query.setRows(10);
        if (DEBUG) {
            query.set("debugQuery", "true"); // Details zur Score-Berechnung anzeigen
        }

        QueryRequest req = new QueryRequest(query);
        req.setBasicAuthCredentials(USERNAME, PASSWORD);

        QueryResponse response = null;
        try {
            response = req.process(solrClient);
        } catch (SolrServerException e) {
            // do something reasonable
            System.err.println(e);
        } catch (IOException e) {
            // do something reasonable
            System.err.println(e);
        }

        // Ausgabe des Top-10-Rankings
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
                    if (DEBUG) {
                        System.out.println("Score: " + doc.get("score"));
                    }
                    rank++;
                }
            }
            if (DEBUG) {
                showDebugOutput(response, results);
            }
        }
    }

    /**
     * Anzeige von Debug-Informationen
     * @param response
     * @param results
     */
    private void showDebugOutput(QueryResponse response, SolrDocumentList results) {
        // interne Interpretation der eingegebenen Suchanfrage
        System.out.println("rawquerystring: " + response.getDebugMap().get("rawquerystring"));
        System.out.println("parsedquery_toString: " + response.getDebugMap().get("parsedquery_toString"));

        // Erläuterung zur Scoreberechnung der einzelnen Dokumente
        for (SolrDocument doc : results) {
            String id = (String) doc.get("id");
            System.out.println("*** Scorewert für Dokument " + id + " ***");
            System.out.println(response.getExplainMap().get(id));
        }
    }

    /**
     * Versuch auf einen nicht erreichbaren Solr-Server zuzugreifen
     * @throws IOException
     * @throws SolrServerException
     */
    private void tryToConnectToUnreachableServer() throws IOException, SolrServerException {
        String solrServerUrl = "http://192.168.0.14:8984/solr/shakespeare";
        SolrClient client = new HttpSolrClient.Builder(solrServerUrl).build();
        try {
            client.ping();
        } catch (SolrServerException e) {
            System.err.println(e);
            throw e;
        } catch (IOException e) {
            System.err.println(e);
            throw e;
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

        SolrSearcherWithAuth solrSearcher = new SolrSearcherWithAuth();
        solrSearcher.connect();

        // Was passiert, wenn der Solr-Server nicht erreichbar ist?
        //solrSearcher.tryToConnectToUnreachableServer();

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
        }

        input.close();
        solrSearcher.close();
        System.out.println("Good Bye!");
    }

}
