package de.suma.misc;

public class Division {

    public static void main(String[] args) {

        System.out.println("1/2 = " + 1/2); // integer division

        System.out.println("1.0/2 = " + 1.0/2); // double division

        System.out.println("1/2.0 = " + 1/2.0); // double division (yields the same result)

        System.out.println("(double) (1/2) = " + (double)(1/2)); // integer division

        System.out.println("(double) 1/2 = " + (double) 1/2); // double division


        /**
         * Runden von Gleitkommazahlen mit Math.round
         */

        System.out.println("3/4 = " + 3/4); // kein Runden: schneidet alle Nachkommastellen ab (0.75 -> 0)

        System.out.println("(int) (3.0/4) = " + (int) (3.0/4)); // kein Runden: schneidet alle Nachkommastellen ab (0.75 -> 0)

        System.out.println("Math.round(3.0/4) = " + Math.round(3.0/4)); // korrektes Runden (0.75 -> 1)
    }
}
