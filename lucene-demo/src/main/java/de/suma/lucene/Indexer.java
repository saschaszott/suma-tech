package de.suma.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import java.util.Iterator;

public class Indexer {

    // Ablageort des erzeugten Lucene-Index
    public final static String INDEX_DIR = "/tmp/myindex.lucene";

    // 37 XML-Datei der Werke von Shakespeare
    public final static String DOCS_DIR = "/Users/sascha/wildau-vorlesung/shakespeare-xml";

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

        // die Werke liegen in englischer Sprache vor: daher nutzen wir einen sprachabhängigen Analyzer
        Analyzer analyzer = new EnglishAnalyzer();

        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        // evtl. bereits existierenden Lucene-Index im Verzeichnis INDEX_DIR überschreiben
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            IndexWriter indexWriter = new IndexWriter(dir, conf);

            int docId = 0;
            // durch die XML-Dateien im Verzeichnis DOCS_DOR iterieren (nicht rekursiv)
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
    }

    /**
     * Legt ein neues Dokument im Index für die übergenene Datei an.
     *
     * @param indexWriter
     * @param file
     * @param docId
     * @throws IOException
     */
    private void indexFile(IndexWriter indexWriter, File file, int docId) throws IOException {

        // neues Lucene-Dokument anlegen und mit Feldern befüllen
        Document doc = new Document();

        Field field = new StringField("id", Integer.toString(docId), Field.Store.YES);
        doc.add(field);

        field = new StringField("filename", file.getName(), Field.Store.YES);
        doc.add(field);

        String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        field = new TextField("content", fileContent, Field.Store.YES);
        doc.add(field);

        indexWriter.addDocument(doc);

        System.out.println("Indexierung von Dokument # " + docId + " mit Datei " + file.getName() + " erfolgreich");
    }

    public static void main(String[] args) {

        System.out.println("Indexierung mittels Apache Lucene");

        Indexer indexer = new Indexer();
        indexer.index();

    }
}
