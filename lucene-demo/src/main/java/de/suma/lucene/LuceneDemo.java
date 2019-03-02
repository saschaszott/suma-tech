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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class LuceneDemo {

    private static final String ID_FIELD = "idField";

    private static final String STR_FIELD = "strField";

    private static final String TXT_FIELD = "txtField";

    private Directory directory;

    private IndexWriter indexWriter;

    public LuceneDemo() throws IOException {
        // für unseren ersten Test verwenden wir einen Lucene-Index, der nur im Hauptspeicher gehalten wird
        // der Index existiert daher nur solange bis Programm beendet wird
        directory = new RAMDirectory();

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
        Field txtField = new TextField(TXT_FIELD, "this field contains multiple words", Field.Store.YES);
        document.add(txtField);

        // es ist üblich jedem Dokument eine eindeutige ID zu geben, auch wenn es Lucene nicht einfordert
        Field idField = new StringField(ID_FIELD, "42", Field.Store.YES);
        document.add(idField);

        // nun indexieren wir das Dokument und legen es im Lucene-Index ab
        indexWriter.addDocument(document);

        // nach der Indexierung wird die Änderung übernommen
        indexWriter.commit();
    }

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
        }
    }

    public void printIndex() throws IOException {
        IndexReader indexReader = DirectoryReader.open(directory);

        Terms terms = MultiFields.getTerms(indexReader, TXT_FIELD);
        TermsEnum it = terms.iterator();
        BytesRef term = it.next();

        System.out.println("Ausgabe des invertierten Index:");

        while (term != null) {
            String termString = term.utf8ToString();
            System.out.print("\n" + termString + " -> ");
            for (LeafReaderContext lrc : indexReader.leaves()) {
                LeafReader lr = lrc.reader();
                PostingsEnum pe = lr.postings(new Term(TXT_FIELD, termString));
                if (pe != null) {
                    int docId = pe.nextDoc();
                    while (docId != PostingsEnum.NO_MORE_DOCS) {
                        Document doc = lr.document(docId);
                        System.out.print(" " + doc.get(ID_FIELD) + ":" + pe.freq());
                        docId = pe.nextDoc();
                    }
                }
            }
            term = it.next();
        }

        System.out.println();
    }

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
        demo.addDocument("43", "multiple fields and multiple words and a field");

        // Ausgabe des Index
        demo.printIndex();

        // lösche das Dokument mit der ID 43 aus dem Index
        demo.deleteDocumentById("43");

        // Suche nach dem Dokument mit ID 43 sollte nun keinen Treffer mehr liefern
        demo.search(ID_FIELD, "43");

        // neues Dokument mit ID 43 zum Index hinzufügen
        demo.addDocument("43", "another test document");

        // Suche nach dem Dokument mit ID 43 sollte nun Treffer liefern
        demo.search(ID_FIELD, "43");

        // Update des Dokuments mit ID 43: entspricht dem Löschen und Neueinfügen
        demo.updateDocument("43", "update of test document");

        // Suche nach dem Dokument mit ID 43 sollte nun den neuen Feldinhalt zurückliefern
        demo.search(ID_FIELD, "43");

        // jetzt löschen wir alle Dokumente aus dem Index
        demo.deleteAllDocs();

        // jetzt sollten keine Dokumente im Index sein
        System.out.println("\nAnzahl Dokumente im Index: " + demo.indexWriter.numDocs());
    }

    private Document createDocument(String id, String text) {
        Document doc = new Document();
        doc.add(new StringField(ID_FIELD, id, Field.Store.YES));
        doc.add(new TextField(TXT_FIELD, text, Field.Store.YES));
        return doc;
    }

    private void addDocument(String id, String text) throws IOException {
        Document doc = createDocument(id, text);
        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    private void updateDocument(String id, String text) throws IOException {
        Term term = new Term(ID_FIELD, id);
        Document doc = createDocument(id, text);
        indexWriter.updateDocument(term, doc);
        indexWriter.commit();
    }

    private void deleteDocumentById(String id) throws IOException {
        Term term = new Term(ID_FIELD, id);
        Query query = new TermQuery(term);
        indexWriter.deleteDocuments(query);
        indexWriter.commit();
    }

    private void deleteAllDocs() throws IOException {
        indexWriter.deleteAll();
        indexWriter.commit();
    }

}
