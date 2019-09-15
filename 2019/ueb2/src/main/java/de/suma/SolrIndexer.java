package de.suma;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;

/**
 * Dieses Programm indexiert die ausgewählte Kollektion von 1523 Gutenberg-E-Books.
 *
 * Die Metadaten sind in RDF/XML-Dateien gespeichert (eine XML-Datei pro E-Book).
 * Der Volltext zu jedem E-Book ist in einer separaten Textdatei gespeichert.
 *
 * Alle Dateien sind UTF-8 codiert.
 *
 * Die Rohdaten (Metadaten und Volltexte) laden Sie bitte aus dem Moodle-System
 * herunter. Dort ist ein TGZ-Archiv gutenberg.tgz abgelegt:
 *
 * https://elearning.th-wildau.de/mod/resource/view.php?id=175384
 *
 * Entpacken Sie die TGZ-Datei bitte im Wurzelverzeichnis dieses Projekts (ueb2).
 *
 * Andernfalls müssen Sie die Pfade beim Zugriff auf die XML- und Textdateien anpassen.
 *
 */
public class SolrIndexer {

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
     * Erzeugt aus dem übergebenen GutenbergDoc ein SolrInputDocument, das schließlich indexiert werden kann.
     * Hier werden die Felder aus dem Datenmodell auf die Indexfelder abgebildet.
     *
     * @param gutenbergDoc Datenhaltungsobjekt aus der Applikation
     * @return das zu indexierende Solr-Dokument
     */
    private SolrInputDocument buildSolrDoc(GutenbergDoc gutenbergDoc) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", gutenbergDoc.getDocId());
        document.addField("title", gutenbergDoc.getTitle());
        document.addField("title_stemmed", gutenbergDoc.getTitle());

        // TODO Abbildung der Daten im übergebenenen GutenbergDoc auf die Indexfelder vervollständigen

        String fulltext = getFulltext(gutenbergDoc.getDocId());
        // TODO Volltext indexieren (in den Indexfeldern fulltext und fulltext_stemmed ablegen)

        return document;
    }

    /**
     * Liest die Volltextdatei des E-Books mit der übergebenen ID ein und gibt das Resultat zurück.
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

    /**
     * Indexiert das übergebene Gutenberg-Dokument und übernimmt die Änderungen in den Solr-Index
     * durch das Ausführen eines Commits.
     *
     * @param gutenbergDoc das zu indexierende Dokument
     */
    private void indexAndCommitOneDocument(GutenbergDoc gutenbergDoc) {
        SolrInputDocument document = buildSolrDoc(gutenbergDoc);

        UpdateRequest updateRequest = new UpdateRequest();
        //updateRequest.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);
        updateRequest.add(document);
        updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);

        try {
            updateRequest.process(solrClient);
            System.out.println("Dokument " + gutenbergDoc.getDocId() + " erfolgreich indexiert!");
        }
        catch (SolrServerException|IOException e) {
            System.err.println("Fehler bei der Indexierung des Dokuments " + gutenbergDoc.getDocId() + ": " + e.getMessage());
            updateRequest.rollback();
        }
    }

    /**
     * Parst nacheinander die RDF/XML-Metadatendateien und
     * ruft für jedes erzeugte GutenbergDoc-Objekt die Indexierung auf.
     */
    private void indexGutenbergSelection() {

        File folder = new File("gutenberg" + File.separator + "selection");
        GutenbergRDFParser parser = new GutenbergRDFParser();

        // TODO Iteration über die Dateien im Verzeichnis selection
        // TODO jede Datei mittels der Klasse GutenbergRDFParser parsen
        // TODO und das erzeugte GutenbergDoc mittels der Methode indexAndCommitOneDocument indexieren

    }

    /**
     * Löscht alle Dokumente, die nicht mehr als maxNumOfDownloads viele Downloads in den letzten 30 Tagen hatten
     *
     * @param maxNumOfDownloads Schwellwert
     */
    private void deleteDocsByNumOfDownloads(int maxNumOfDownloads) {
        UpdateRequest updateRequest = new UpdateRequest();
        //updateRequest.setBasicAuthCredentials(Configuration.USERNAME, Configuration.PASSWORD);

        // TODO geeignete Delete Query hinzufügen

        updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);
        try {
            updateRequest.process(solrClient);
            System.out.println("Dokumente erfolgreich gelöscht!");
        } catch (SolrServerException|IOException e) {
            System.err.println("Fehler beim Löschen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        SolrIndexer solrIndexer = new SolrIndexer();
        solrIndexer.connect();

        // TODO Methodenaufruf indexGutenbergSelection für Indexierung der Dateien hinzufügen

        solrIndexer.close();
    }

}
