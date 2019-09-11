package de.suma;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;

import java.io.PrintWriter;

/**
 * Erzeugung eines Finite-State-Transducer (FST) und Ausgabe als dot-Datei,
 * die mit Graphviz visualisiert werden kann.
 *
 * @author Sascha Szott
 */
public class App {

    public static final String DOT_FILE_NAME = "fst.dot";

    public static void main(String[] args) throws Exception {
        // Input values (keys). These must be provided to Builder in Unicode sorted order!
        String inputValues[] = {"car", "cards", "cast", "cat", "cats", "do", "dogs", "sea", "seal", "seat", "zoo", "zoom"};
        long outputValues[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
        Builder<Long> builder = new Builder<Long>(FST.INPUT_TYPE.BYTE1, outputs); // wenn nicht mehr als 256 Eingabewerte verwendet werden, reicht ein Byte zur Speicherung aus
        IntsRefBuilder scratchInts = new IntsRefBuilder();
        for (int i = 0; i < inputValues.length; i++) {
            BytesRef scratchBytes = new BytesRef(inputValues[i]);
            builder.add(Util.toIntsRef(scratchBytes, scratchInts), outputValues[i]);
        }
        FST<Long> fst = builder.finish();

        PrintWriter pw = new PrintWriter(DOT_FILE_NAME);
        Util.toDot(fst, pw, false, false);
        pw.close();
    }
}
