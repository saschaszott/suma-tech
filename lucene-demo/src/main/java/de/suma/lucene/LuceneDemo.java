package de.suma.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Demonstator für die grundlegenden Schritte zum Anlegen und Indexieren von Dokumenten sowie zum Formulieren
 * und Ausführen von Suchanfragen in Lucene 8.
 *
 * Zusätzlich wird der feldspezifische invertierte Index ausgegeben (Terme im Dictionary sowie Postinglisten mit
 * DocIDs und Positionsangabe).
 *
 * Das Programm erzeugten einen temporären Lucene-Index, der unterhalb von /tmp abgelegt wird.
 *
 * @author Sascha Szott
 */
public class LuceneDemo {

    private static final String ID_FIELD = "idField";

    private static final String STR_FIELD = "strField";

    private static final String TXT_FIELD = "txtField";

    private Directory directory;

    private IndexWriter indexWriter;

    /**
     * Konstruktor
     *
     * @throws IOException
     */
    public LuceneDemo() throws IOException {
        // Der erzeugte Lucene-Index wird im Dateisystem unterhalb von /tmp abgelegt.
        // Er überlebt damit einen Neustart der VM nicht.
        directory = new MMapDirectory(Files.createTempDirectory(Paths.get("/tmp"), "lucene-demo"));

        // ein Analyzer ist für die Behandlung der zu indexierenden Daten und der Suchanfrage zuständig
        // der StandardAnalyzer tokenisiert die Eingabe an Whitespaces, führt ein Lower-Casing durch und
        // entfernt englische Stoppwörter
        Analyzer analyzer = new StandardAnalyzer();

        // hier können Optionen für die Indexerzeugung angegeben werden: in unserem Fall wollen wir einen
        // neuen Index anlegen (ein ggf. bereits vorhandener Index würde hierbei überschrieben werden)
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // der IndexWriter ermöglicht das Hinzufügen von Dokumenten zum Lucene-Index
        indexWriter = new IndexWriter(directory, conf);
    }

    /**
     * Erzeugt ein Testdokument und fügt es zum Index hinzu.
     *
     * @throws IOException
     */
    public void createMy1stDocAndAddToIndex() throws IOException {
        // ein Dokument ist eine Sammlung von Indexfeldern, die jeweils einen Namen und einen Wert haben
        // zusätzlich kann der Typ des Felds angegeben werden, der u.a. festlegt, wie die im Feld gespeicherten
        // Daten während der Indexierung behandelt werden sollen (z.B. soll Tokenisierung durchgeführt werden)
        Document document = new Document();

        // ein StringField übernimmt den zu speichernden Wert ohne Veränderungen in das Indexfeld
        // später kann ein Matching mit einer Suchanfrage nur dann erfolgen, wenn die Suchanfrage exakt
        // mit dem gespeicherten Feldinhalt übereinstimmt
        Field strField = new StringField(STR_FIELD, "LUCENE", Field.Store.YES);
        document.add(strField);

        // ein TextField überführt den zu speichernden Wert mittels des zuvor festgelegten Analyzers in das Indexfeld
        // später kann z.B. ein Matching mit einer Suchanfrage auch dann erfolgen, wenn die Groß- und
        // Kleinschreibung nicht beachtet wird oder nur einzelne Terme des indexierten Wertes in der Suchanfrage auftreten
        // die drei Stoppwörter "and", "at" und "this" werden bei der Indexierung des Dokuments verworfen
        // alle übrigen Terme werden in Kleinbuchstaben ("Lowercasing") umgewandelt
        // Satzzeichen werden im Rahmen der Tokenisierung entfernt ("words" und "word" bleiben übrig)
        Field txtField = new TextField(TXT_FIELD, "this field contains multiple words, and at least ONE Stop Word!", Field.Store.YES);
        document.add(txtField);

        // es ist üblich jedem Dokument eine eindeutige ID zu geben, auch wenn es Lucene nicht einfordert
        // intern verwendet Lucene Dokument-IDs vom Typ int, die bei 0 starten - u.a. weil dadurch eine Komprimierung
        // der Postinglisten ermöglicht wird (d.h. das vorliegende Dokument bekommt die intere ID 0, weil es als erstes
        // Dokument in den Index hinzugefügt wird)
        Field idField = new StringField(ID_FIELD, "42", Field.Store.YES);
        document.add(idField);

        // nun indexieren wir das Dokument und legen es im Lucene-Index ab
        indexWriter.addDocument(document);

        // nach der Indexierung wird die Änderung übernommen
        indexWriter.commit();
    }

    /**
     * Erlaubt das Formulieren einer feldbasierten Suchanfrage. Die Methode gibt die
     * Suchergebnisse als Top-10-Ranking auf der Konsole aus.
     *
     * @param fieldName Name des Indexfelds
     * @param queryString Suchanfrage
     *
     * @throws IOException
     * @throws ParseException
     */
    public void search(String fieldName, String queryString) throws IOException, ParseException {
        // nun können wir eine Suchanfrage auf dem erzeugten Lucene-Index ausführen
        // dazu müssen wir den Index (der im Hauptspeicher existiert) öffnen
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // die Erzeugung einer Suchanfrage erfolgt mittels eines QueryParsers
        // bei der Erzeugung des QueryParsers wird festgelegt, welches Indexfeld durchsucht werden soll und
        // wie die Suchanfrage behandelt werden soll (z.B. soll die Suchanfrage tokenisiert werden?)
        QueryParser queryParser = null;
        Query query;

        switch (fieldName) {
            case ID_FIELD:
                // "exact match" der ID
                queryParser = new QueryParser(ID_FIELD, new KeywordAnalyzer());
                break;
            case STR_FIELD:
                // Suche im Indexfeld strField und Übernahme der Suchanfrage ohne Veränderung ("exact match")
                queryParser = new QueryParser(STR_FIELD, new KeywordAnalyzer());
                break;
            case TXT_FIELD:
                // Suche im Indexfeld txtField und Aufspaltung der Suchanfrage
                // in einzelne Tokens (Tokenisierung), Lower-Casing, Stoppwort-Entfernung
                queryParser = new QueryParser(TXT_FIELD, new StandardAnalyzer());
                break;
        }

        if (queryParser == null) {
            System.err.println("Unbekanntes Indexfeld " + fieldName);
            return;
        }

        query = queryParser.parse(queryString);

        // es sollen die 10 relevantesten Dokumente ermittelt werden (Top-10-Ranking)
        TopDocs docs = indexSearcher.search(query, 10);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(docs.totalHits + " Treffer gefunden für Suchanfrage " + query);

        // Ausgabe der einzelnen Dokumente im Suchergebnis (0..10)
        for (ScoreDoc doc : docs.scoreDocs) {
            Document document = indexSearcher.doc(doc.doc);
            System.out.println("Dokument mit ID " + document.get(ID_FIELD));
            System.out.println("\t" + STR_FIELD + ": " + document.get(STR_FIELD));
            System.out.println("\t" + TXT_FIELD + ": " + document.get(TXT_FIELD));
            System.out.println("\tscore value: " + doc.score);
        }
    }

    /**
     * Ausgabe des invertierten Index für das angegebene Indexfeld.
     *
     * @param fieldName Name des Indexfelds
     * @throws IOException
     */
    public void printIndex(String fieldName) throws IOException {
        System.out.print("~~~~\n~~~~ Ausgabe des invertierten Index für das Indexfeld " +  fieldName + "\n~~~~");

        IndexReader indexReader = DirectoryReader.open(directory);
        LuceneDictionary ld = new LuceneDictionary(indexReader, fieldName);
        BytesRefIterator iterator = ld.getEntryIterator();
        BytesRef byteRef;
        while ((byteRef = iterator.next()) != null) {
            String term = byteRef.utf8ToString();
            System.out.print("\n" + term + " -> ");
            for (LeafReaderContext lrc : indexReader.leaves()) {
                LeafReader lr = lrc.reader();
                PostingsEnum pe = lr.postings(new Term(fieldName, term));
                if (pe != null) {
                    int docId = pe.nextDoc();
                    while (docId != PostingsEnum.NO_MORE_DOCS) {
                        Document doc = lr.document(docId);
                        // Achtung: die Ausgabe der IDs ist hier nicht sortiert, z.B. steht in der Postingliste für den
                        // Term "test" oder "document" die ID 43 erst an der vorletzten Stelle
                        System.out.print(" " + doc.get(ID_FIELD) + ":" + pe.freq());
                        docId = pe.nextDoc();
                    }
                }
            }
        }
        System.out.println();
    }

    /**
     * Hauptmethode des Programms
     */
    public static void main(String[] args) throws IOException, ParseException {

        LuceneDemo demo = new LuceneDemo();

        // erzeuge Index und füge ein erstes Testdokument hinzu
        demo.createMy1stDocAndAddToIndex();

        // Demonstration einiger Suchanfragen
        demo.search(STR_FIELD, "lucene"); // findet keinen Treffer, weil die Schreibweise nicht übereinstimmt
        demo.search(STR_FIELD, "LUCENE"); // findet das zuvor indexierte Dokument (exact match)

        demo.search(TXT_FIELD, "FIELD"); // findet das zuvor indexierte Dokument (Lower-Casing)
        demo.search(TXT_FIELD, "a field"); // findet das zuvor indexierte Dokument (Stopword-Removal)
        demo.search(TXT_FIELD, "multiple field words lucene"); // findet das zuvor indexierte Dokument (Matching der Einzeltokens / partial match)

        // Boolesches Retrieval mittels Lucene
        demo.search(TXT_FIELD, "+field +lucene"); // findet nicht das zuvor indexierte Dokument
        demo.search(TXT_FIELD, "+field -lucene"); // findet das zuvor indexierte Dokument


        // ein weiteres Dokument wird zum Index hinzugefügt
        // auch hier werden Stoppwörter entfernt
        demo.addDocument("43", "multiple fields, and multiple words, and A FIELD!");

        // Ausgabe des invertierten Index für das Indexfeld txtField
        demo.printIndex(TXT_FIELD);

        // lösche das Dokument mit der ID 43 aus dem Index
        demo.deleteDocumentById("43");

        // Suche nach dem Dokument mit ID 43 sollte nun keinen Treffer mehr liefern
        demo.search(ID_FIELD, "43");

        // 100 neue Dokumente mit ID 43, .., 142 zum Index hinzufügen
        for (int i = 0; i < 100; i++) {
            int docId = 43 + i;
            demo.addDocument("" + docId, "test another test document");
        }

        // jetzt sollten insgesamt 101 Dokumente im Index enthalten sein
        System.out.println("\nAnzahl Dokumente im Index: " + demo.getNumDocs());

        // Suche nach dem Dokument mit ID 43 sollte nun Treffer liefern
        demo.search(ID_FIELD, "43");

        // Suche nach another sollte 100 Treffer liefern, wobei nur 10 Treffer ausgegeben werden (Top-k-Suche)
        demo.search(TXT_FIELD, "another");

        // Update des Dokuments mit ID 43: entspricht dem Löschen und Neueinfügen des Dokuments
        // vor dem Update gibt es keine gelöschten Dokumente im Index
        System.out.println("Es existieren gelöschte Dokumente: " + demo.indexWriter.hasDeletions());
        demo.updateDocument("43", "new test of update of test document of test with a brand new word");
        // nach dem Update gibt es ein gelöschtes Dokument im Index (weil Löschen eine Teiloperation vom Update)
        System.out.println("Es existieren gelöschte Dokumente: " + demo.indexWriter.hasDeletions());

        // Suche nach dem Dokument mit ID 43 sollte nun den neuen Feldinhalt zurückliefern
        demo.search(ID_FIELD, "43");

        // Suche nach another sollte nur noch 99 Treffer liefern
        // beachte, dass sich die Score-Werte vergrößern, weil sich die Document Frequency für den Term "another" verringert
        demo.search(TXT_FIELD, "another");

        // jetzt sollen alle gelöschten Dokumente tatsächlich aus dem Index verschwinden (wenn man das nicht machen würde,
        // so würde bei der nachfolgenden Ausgabe des invertierten Index immer noch 43 in der Postingliste von "another"
        // auftreten, obwohl in dem aktualisierten Dokument mit ID 43 dieser Term gar nicht mehr auftritt
        System.out.println("Es existieren gelöschte Dokumente: " + demo.indexWriter.hasDeletions());
        demo.indexWriter.forceMerge(1); // optimize wurde in Lucene 3.5 abgeschafft
        System.out.println("Es existieren gelöschte Dokumente: " + demo.indexWriter.hasDeletions());
        demo.indexWriter.commit();

        demo.printIndex(TXT_FIELD);

        // jetzt löschen wir alle Dokumente aus dem Index
        demo.deleteAllDocs();

        // jetzt sollten keine Dokumente im Index sein
        System.out.println("\nAnzahl Dokumente im Index: " + demo.getNumDocs());

        // am Ende sollte der IndexWriter sauber geschlossen, so dass er auf der Festplatte persistiert werden
        // (da wir hier einen In-Memory-Index verwenden, ist das Unterlassen des Aufrufs kein Problem)
        demo.indexWriter.close();
    }

    /**
     * Erzeugt ein Lucene-Dokument mit der übergebenen ID und einem Textfeld, das den übergebenen Inhalt enthält.
     *
     * @param id Dokument-ID
     * @param text Textinhalt
     * @return Lucene-Dokument mit zwei Feldern
     */
    private Document createDocument(String id, String text) {
        Document doc = new Document();
        doc.add(new StringField(ID_FIELD, id, Field.Store.YES));
        doc.add(new TextField(TXT_FIELD, text, Field.Store.YES));
        return doc;
    }

    /**
     * Erzeugt ein Lucene-Dokument mit zwei Feldern, in denen der übergebene Inhalt steht. Fügt das Lucene-Dokument
     * zum Index hinzu.
     *
     * @param id Dokument-ID
     * @param text Textinhalt
     *
     * @throws IOException
     */
    private void addDocument(String id, String text) throws IOException {
        Document doc = createDocument(id, text);
        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    /**
     * Erlaubt die Aktualisierung eines bestehenden Lucene-Dokument im Index. Das Dokument wird hierbei durch seine
     * ID referenziert.
     *
     * @param id Dokument-ID
     * @param text aktualisierter Textinhalt
     *
     * @throws IOException
     */
    private void updateDocument(String id, String text) throws IOException {
        Term term = new Term(ID_FIELD, id);
        Document doc = createDocument(id, text);
        indexWriter.updateDocument(term, doc);
        indexWriter.commit();
    }

    /**
     * Löscht das Lucene-Dokument mit der übergebenen ID aus dem Index, sofern es existiert.
     *
     * @param id ID des zu löschenden Dokuments
     *
     * @throws IOException
     */
    private void deleteDocumentById(String id) throws IOException {
        Term term = new Term(ID_FIELD, id);
        Query query = new TermQuery(term);
        indexWriter.deleteDocuments(query);
        indexWriter.commit();
    }

    /**
     * Entfernt alle Dokument aus dem Index.
     *
     * @throws IOException
     */
    private void deleteAllDocs() throws IOException {
        indexWriter.deleteAll();
        indexWriter.commit();
    }

    /**
     * Ermittelt die aktuelle Dokumentanzahl im Lucene-Index.
     *
     * @return Dokumentanzahl
     */
    private int getNumDocs() {
        IndexWriter.DocStats stats = indexWriter.getDocStats();
        return stats.numDocs;
    }

}
