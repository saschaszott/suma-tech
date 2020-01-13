package de.suma.misc;

/**
 * String objects in Java are immutable. Immutable means unmodifiable or unchangeable.
 * Once a string object is created its data or state can't be changed.
 *
 */

public class ImmutableString {

    public static final int NUM_OF_CONCATS = 100000;

    public static void main(String[] args) {

        System.out.println("=== Without StringBuilder ===");
        String whitespace = " ";
        long start = System.currentTimeMillis();
        String s1 = "";
        for (int i = 0; i < NUM_OF_CONCATS; i++) {
            s1 += whitespace + i;
        }
        System.out.print("Creation of " + NUM_OF_CONCATS + " String objects took ");
        System.out.println(1/1000.0 * (System.currentTimeMillis() - start) + " seconds");
        if (NUM_OF_CONCATS <= 20) {
            System.out.println("s1 =" + s1);
        }

        System.out.println("\n=== With StringBuilder ===");
        start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_OF_CONCATS; i++) {
            sb.append(whitespace);
            sb.append(i);
        }
        String s2 = sb.toString();

        System.out.print("creation of final String object using StringBuilder took ");
        System.out.println(1/1000.0 * (System.currentTimeMillis() - start) + " seconds");
        if (NUM_OF_CONCATS <= 20) {
            System.out.println("s2 =" + s2);
        }


        // prove that both string are character equal
        System.out.print("\nResult of operations: ");

        if (s1.equals(s2)) {
            System.out.println("created string objects are character equal");
        }
        else {
            System.out.println("created string objects are NOT character equal");
        }
    }
}
