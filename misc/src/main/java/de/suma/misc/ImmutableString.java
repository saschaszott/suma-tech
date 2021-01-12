package de.suma.misc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

/**
 * String objects in Java are immutable. Immutable means unmodifiable or unchangeable.
 * Once a string object is created its data or state can't be changed.
 */

public class ImmutableString {

    public static final int NUM_OF_CONCATS = 100000;

    // im Abstand von OUTPUT_RATE vielen Konkatenationen sollen Informationen zur
    // Garbage Collection ausgegeben werden
    // auf den Wert 0 setzen, um die Ausgabe abzuschalten
    public static final int GC_STATS_OUTPUT_RATE = 1000;

    public static void main(String[] args) {

        System.out.println("=== PART 1: Without StringBuilder ===");
        String whitespace = " ";
        long start = System.currentTimeMillis();
        String s1 = "";
        for (int i = 0; i < NUM_OF_CONCATS; i++) {
            s1 += whitespace + i; // hier werden implizit zwei neue String-Objekte erzeugt
            //s1 += s1; // das Aktivieren dieser Zeile führt garantiert zum OutOfMemoryError 😉
            if (GC_STATS_OUTPUT_RATE > 0 && i % GC_STATS_OUTPUT_RATE == 0) {
                printGCStats(i, 0, 0);
            }
        }
        System.out.print("part 1: creation of " + NUM_OF_CONCATS + " String objects took ");
        System.out.println(1 / 1000.0 * (System.currentTimeMillis() - start) + " seconds");
        if (NUM_OF_CONCATS <= 20) {
            System.out.println("s1 =" + s1);
        }

        long gcCount = getGcCount();
        long gcTotalTime = getGcTotalTime();
        System.out.println("part 1: total num of garbage collections: " + gcCount + " (took " + gcTotalTime + " ms)");

        System.out.println("\n=== PART 2: With StringBuilder ===");
        start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_OF_CONCATS; i++) {
            sb.append(whitespace);
            sb.append(i);
            if (GC_STATS_OUTPUT_RATE > 0 && i % GC_STATS_OUTPUT_RATE == 0) {
                printGCStats(i, gcCount, gcTotalTime);
            }
        }
        String s2 = sb.toString();

        System.out.print("part 2: creation of final String object using StringBuilder took ");
        System.out.println(1 / 1000.0 * (System.currentTimeMillis() - start) + " seconds");
        if (NUM_OF_CONCATS <= 20) {
            System.out.println("s2 =" + s2);
        }

        gcCount = getGcCount() - gcCount;
        gcTotalTime = getGcTotalTime() - gcTotalTime;
        System.out.println("part 2: total num of garbage collections: " + gcCount + " (took " + gcTotalTime + " ms)");

        // prove that both string are character equal
        System.out.println("\n=== Result of operations ===");

        if (s1.equals(s2)) {
            System.out.println("created string objects are character equal");
        } else {
            System.out.println("created string objects are NOT character equal");
        }
    }

    public static void printGCStats(int currNumOfIterations, long offsetCount, long offsetTime) {
        long totalGarbageCollections = getGcCount() - offsetCount;
        System.out.print("after " + currNumOfIterations + " iterations: # GCs: " + totalGarbageCollections);

        long garbageCollectionTime = getGcTotalTime() - offsetTime;
        System.out.println(" - total garbage collection time (ms): " + garbageCollectionTime);
    }

    public static long getGcCount() {
        long totalGarbageCollections = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();
            if (count >= 0) {
                totalGarbageCollections += count;
            }
        }
        return totalGarbageCollections;
    }

    public static long getGcTotalTime() {
        long garbageCollectionTime = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long time = gc.getCollectionTime();
            if (time >= 0) {
                garbageCollectionTime += time;
            }
        }
        return garbageCollectionTime;
    }
}
