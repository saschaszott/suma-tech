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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class LuceneDemo {

    private static final String ID_FIELD = "idField";

    private static final String STR_FIELD = "strField";

    private static final String TXT_FIELD = "txtField";

    public Directory index() throws IOException {
        // für unseren ersten Test verwenden wir einen Lucene-Index, der nur im Hauptspeicher gehalten wird
        // der Index existiert daher nur solange bis Programm beendet wird
        Directory directory = new RAMDirectory();

        // ein Analyzer ist für die Behandlung der zu indexierenden Daten und der Suchanfrage zuständig
        // der StandardAnalyzer tokenisiert die Eingabe an Whitespaces, führt ein Lower-Casing durch und
        // entfernt englische Stoppwörter
        Analyzer analyzer = new StandardAnalyzer();

        // hier können Optionen für die Indexerzeugung angegeben werden: in unserem Fall wollen wir einen
        // neuen Index anlegen (ein ggf. bereits vorhandener Index würde hierbei überschrieben werden)
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // der IndexWriter ermöglicht das Hinzufügen von Dokumenten zum Lucene-Index
        IndexWriter indexWriter = new IndexWriter(directory, conf);

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

        // nach der Indexierung wird der Index geschlossen
        indexWriter.close();

        return directory;
    }

    public void search(Directory directory, String fieldName, String queryString) throws IOException, ParseException {
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

    public void printIndex(Directory directory) throws IOException {
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
        Directory directory = demo.index();

        demo.search(directory,STR_FIELD, "lucene"); // findet keinen Treffer, weil die Schreibweise nicht übereinstimmt
        demo.search(directory,STR_FIELD, "LUCENE"); // findet das zuvor indexierte Dokument (exact match)

        demo.search(directory,TXT_FIELD, "FIELD"); // findet das zuvor indexierte Dokument (Lower-Casing)
        demo.search(directory,TXT_FIELD, "a field"); // findet das zuvor indexierte Dokument (Stopword-Removal)
        demo.search(directory,TXT_FIELD, "multiple field words lucene"); // findet das zuvor indexierte Dokument (Matching der Einzeltokens / partial match)

        // Boolesches Retrieval mittels Lucene
        demo.search(directory,TXT_FIELD, "+field +lucene"); // findet nicht das zuvor indexierte Dokument
        demo.search(directory,TXT_FIELD, "+field -lucene"); // findet das zuvor indexierte Dokument

        // wir fügen ein weiteres Dokument zum Index hinzu
        Document doc = new Document();
        doc.add(new StringField(ID_FIELD, "43", Field.Store.YES));
        doc.add(new TextField(TXT_FIELD, "multiple fields and multiple words and a field", Field.Store.YES));
        IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
        conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        IndexWriter indexWriter = new IndexWriter(directory, conf);
        indexWriter.addDocument(doc);
        indexWriter.close();

        // Ausgabe des Dictionary
        demo.printIndex(directory);
    }

}
