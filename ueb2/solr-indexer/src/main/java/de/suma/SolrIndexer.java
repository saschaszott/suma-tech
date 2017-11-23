package de.suma;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dieses Programm demonstriert die grundlegenden Schritte um mittels Java Dokumente in einem Solr-Server
 * zu speichern.
 *
 * @author Sascha Szott
 * @version 1.0
 *
 */
public class SolrIndexer {

    // wenn Sie das Programm innerhalb der Ubuntu-VM ausführen, dann schreiben Sie statt der IP einfach localhost
    // in meinem Fall nutze ich die Entwicklungsumgebung direkt auf dem Wirt und lasse nur den Solr-Server in der VM laufen
    // in der VM habe ich die IP-Adresse mittels des Befehls ifconfig ermittelt
    private static final String SOLR_SERVER_URL = "http://192.168.0.15:8983/solr/";

    // diesen Core haben wir am Dienstag angelegt
    private static final String SOLR_CORE_NAME = "solr-ueb2";

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
     * Erzeugt aus dem übergebenen Dokument ein SolrInputDocument, das indexiert werden kann.
     *
     * @param gutenbergDoc Datenhaltungsobjekt aus der Applikation
     * @return das zu indexierende Dokument
     */
    private SolrInputDocument buildSolrDoc(GutenbergDoc gutenbergDoc) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", gutenbergDoc.getDocId());

        return document;
    }

    /**
     * Indexiert das übergebene Dokument und übernimmt die Änderungen in den Solr-Index
     * durch das Ausführen eines Commits.
     *
     * @param gutenbergDoc das zu indexierende Dokument
     */
    public void indexAndCommitOneDocument(GutenbergDoc gutenbergDoc) {
        SolrInputDocument document = buildSolrDoc(gutenbergDoc);

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setBasicAuthCredentials(USERNAME, PASSWORD);
        updateRequest.add(document);
        updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);

        try {
            updateRequest.process(solrClient);
        }
        catch (SolrServerException|IOException e) {
            System.err.println("Fehler bei der Indexierung des Dokuments " + gutenbergDoc.getDocId() + ": " + e.getMessage());
            updateRequest.rollback();
        }
    }

    /**
     * Indexiert die übergebene Liste von Dokumenten und führt am Ende ein Commit durch.
     *
     * Tritt während der Indexierung ein Fehler auf, so werden die Änderungen nicht
     * zurückgenommen, die bereits erfolgreich in den Solr-Index geschrieben werden konnten.
     *
     * @param gutenbergDocs die Liste der zu indexierenden Dokumente
     */
    public void indexDocumentsAndCommit(List<GutenbergDoc> gutenbergDocs) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setBasicAuthCredentials(USERNAME, PASSWORD);
        for (GutenbergDoc gutenbergDoc : gutenbergDocs) {
            SolrInputDocument solrInputDocument = buildSolrDoc(gutenbergDoc);
            if (gutenbergDoc.getDocId().equals("9")) {
                // füge zu Dokument 9 ein Feld hinzu, dass es nicht im Solr-Schema gibt
                solrInputDocument.addField("foo", "bar");
            }
            updateRequest.add(solrInputDocument);
        }
        updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);

        try {
            updateRequest.process(solrClient);
        }
        catch (SolrServerException|IOException e) {
            System.err.println("Fehler bei der Indexierung der Dokumente: " + e.getMessage());
            updateRequest.rollback();
        }
    }

    /**
     * Indexiert die übergebene Liste von Dokumenten und führt nach jedem Dokument einen Commit durch.
     *
     * Tritt während der Indexierung eines Dokuments ein Fehler auf, so gehen die zuvor bereits
     * erfolgreich indexierten Dokumente nicht verloren.
     *
     * @param gutenbergDocs die Liste der zu indexierenden Dokumente
     */
    public void indexAndCommitDocuments(List<GutenbergDoc> gutenbergDocs) {
        for (GutenbergDoc gutenbergDoc : gutenbergDocs) {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.setBasicAuthCredentials(USERNAME, PASSWORD);

            SolrInputDocument solrInputDocument = buildSolrDoc(gutenbergDoc);
            if (gutenbergDoc.getDocId().equals("9")) {
                // füge zu Dokument 9 ein Feld hinzu, dass es nicht im Solr-Schema gibt
                solrInputDocument.addField("foo", "bar");
            }
            updateRequest.add(solrInputDocument);
            updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);

            try {
                updateRequest.process(solrClient);
            }
            catch (SolrServerException|IOException e) {
                System.err.println("Fehler bei der Indexierung der Dokumente: " + e.getMessage());
                updateRequest.rollback();
            }
        }
    }

    /**
     * Stößt die Optimierung des Solr-Index an.
     */
    public void optimizeIndex() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setBasicAuthCredentials(USERNAME, PASSWORD);
        updateRequest.setAction(AbstractUpdateRequest.ACTION.OPTIMIZE, false, false);
        try {
            updateRequest.process(solrClient);
        } catch (SolrServerException|IOException e) {
            System.err.println("Fehler beim Index-Optimize: " + e.getMessage());
        }
    }

    /**
     * Löscht das Dokument mit der übergebene ID aus dem Solr-Index.
     *
     * @param id die ID des zu löschenden Dokuments
     */
    public void deleteDocById(String id) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setBasicAuthCredentials(USERNAME, PASSWORD);
        updateRequest.deleteById(id);
        updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);
        try {
            updateRequest.process(solrClient);
        } catch (SolrServerException|IOException e) {
            System.err.println("Fehler beim Löschen des Dokuments " + id + ": " + e.getMessage());
        }
    }

    /**
     * Entfernt alle Dokumente aus dem Solr-Index.
     */
    public void deleteAllDocs() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setBasicAuthCredentials(USERNAME, PASSWORD);
        updateRequest.deleteByQuery("*:*");
        updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);
        try {
            updateRequest.process(solrClient);
        } catch (SolrServerException|IOException e) {
            System.err.println("Fehler beim Löschen aller Dokumente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        SolrIndexer solrIndexer = new SolrIndexer();
        solrIndexer.connect();

        List<GutenbergDoc> gutenbergDocs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // prepare document and add to Solr index
            GutenbergDoc doc = new GutenbergDoc();
            doc.setDocId("" + i);
            doc.setTitle("Tiny test document");
            String[] subjectHeadings = new String[] {"Drama", "Computer networks", "Libraries"};
            doc.setSubjectHeadings(Arrays.asList(subjectHeadings));
            gutenbergDocs.add(doc);
        }


        if (false) {
            for (GutenbergDoc gutenbergDoc : gutenbergDocs) {
                solrIndexer.indexAndCommitOneDocument(gutenbergDoc);
            }
        }

        //solrIndexer.indexDocumentsAndCommit(gutenbergDocs);

        //solrIndexer.indexAndCommitDocuments(gutenbergDocs);

        //solrIndexer.deleteDocById("6");

        //solrIndexer.optimizeIndex();

        //solrIndexer.deleteAllDocs();

        solrIndexer.close();
    }

}