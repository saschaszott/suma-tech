package de.suma.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Erzeugt einen Lucene-Index auf Basis der Shakespeare-Kollektion (37 XML-Dateien). Ein Index-Dokument enthält
 * hierbei vier Indexfelder: id, filename, content, content_stemmed
 *
 * @author Sascha Szott
 */
public class Indexer {

    // Ablageort des erzeugten Lucene-Index
    public final static String INDEX_DIR = "/tmp/shakespeare-lucene-index";

    // 37 XML-Datei der Werke von Shakespeare
    public final static String DOCS_DIR = "/Users/lehre/Desktop/shakespeare-xml";

    // Feldbezeichnung
    public static final String FIELD_ID = "id";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_CONTENT_STEMMED = "content_stemmed";

    public void index() {

        Path docsDirPath = Paths.get(DOCS_DIR);
        if (! Files.isDirectory(docsDirPath) || !Files.isReadable(docsDirPath)) {
            System.err.println("Fehler beim Einlesen des Dokument-Verzeichnisses " + DOCS_DIR);
            System.exit(1);
        }

        Directory dir = null;
        try {
            // in diesem Beispiel wollen wir einen persistenten Lucene-Index im Dateisystem speichern
            // der Index überlebt damit das Programmende und kann später erneut eingelesen werden
            dir = FSDirectory.open(Paths.get(INDEX_DIR));
        } catch (IOException e) {
            System.err.println("Fehler beim Zugriff auf das Index-Verzeichnis " + INDEX_DIR);
            System.exit(1);
        }

        // Definiton des Default Analyzers (wird verwendet, wenn nichts anderes angegeben ist)
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put(FIELD_CONTENT_STEMMED, new EnglishAnalyzer()); // Stemming und Entfernung von abschließendem 's
        PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

        IndexWriterConfig conf = new IndexWriterConfig(aWrapper);

        // evtl. bereits existierenden Lucene-Index im Verzeichnis INDEX_DIR überschreiben
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            IndexWriter indexWriter = new IndexWriter(dir, conf);

            int docId = 0;
            // durch die XML-Dateien im Verzeichnis DOCS_DIR iterieren (nicht rekursiv)
            Iterator<File> filesIterator = FileUtils.iterateFiles(new File(DOCS_DIR), new String[]{"xml"}, false);
            while (filesIterator.hasNext()) {
                File file = filesIterator.next();
                indexFile(indexWriter, file, docId);
                docId++;
            }

            indexWriter.close();
        } catch (IOException e) {
            System.err.println("Unerwarteter Fehler bei der Indexierung: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Index wurde erfolgreich im Dateisystem unter " +  INDEX_DIR + " gespeichert");

        long docsSize = FileUtils.sizeOf(new File(DOCS_DIR));
        System.out.println("Ausgangsgröße (37 XML-Dateien): " + docsSize * 1.0 / FileUtils.ONE_MB + " MB");

        long indexSize = FileUtils.sizeOf(new File(INDEX_DIR));
        System.out.println("Größe des Lucene-Index: " + indexSize * 1.0 / FileUtils.ONE_MB + " MB");

        System.out.println("Größe des Index im Vergleich zur Größe der Kollektion: " + (100.0 * indexSize / docsSize) + " %");

    }

    /**
     * Legt ein neues Dokument im Index für die übergebene XML-Datei an.
     *
     * @param indexWriter
     * @param file
     * @param docId
     * @throws IOException
     */
    private void indexFile(IndexWriter indexWriter, File file, int docId) throws IOException {

        // neues Lucene-Dokument anlegen und mit Feldern befüllen
        Document doc = new Document();

        Field field = new StringField(FIELD_ID, Integer.toString(docId), Field.Store.YES);
        doc.add(field);

        field = new StringField(FIELD_FILENAME, file.getName(), Field.Store.YES);
        doc.add(field);

        String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        // Feldinhalt wird nur indexiert, aber nicht gespeichert (der Feldinhalt kann daher im
        // Suchergebnis z.B. in Form eines Snippets nicht mehr zurückgegeben werden)
        // setzt man stored auf yes, dann wird der Index deutlich größer (5,5 MB statt 1,8 MB)
        field = new TextField(FIELD_CONTENT, fileContent, Field.Store.NO);
        doc.add(field);

        // Feldinhalt wird zusätzlich einem Stemming (Porter Stemmer) unterworfen sowie Entfernung von abschließendem 's
        field = new TextField(FIELD_CONTENT_STEMMED, fileContent, Field.Store.NO);
        doc.add(field);

        indexWriter.addDocument(doc);

        System.out.println("Indexierung von Dokument # " + docId + " mit Datei " + file.getName() + " erfolgreich");
    }

    /**
     * Hauptmethode
     *
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("Indexierung mittels Apache Lucene");

        Indexer indexer = new Indexer();
        indexer.index();
    }
}
