package de.suma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Beispielimplementierung einer binären Suche
 *
 * @author Sascha Szott
 */
public class App {

    public static void main(String[] args) {

        int[] searchArray = new int[] {
            1, 2, 4, 5, 7, 11, 12, 15, 19, 22, 23, 25, 33, 35, 41
        };

        App app = new App();
        app.binarySearch(searchArray, 11);
        app.binarySearch(searchArray, 13);


        searchArray = new int[] {
            1, 3, 2
        };
        app.binarySearch(searchArray, 11);
    }

    /**
     * Führt eine binäre Suche auf dem übergebenen Array aus.
     *
     * @param searchArray das zu durchsuchende Array (muss sortiert vorliegen)
     * @param k Suchwert
     * @return gibt true genau dann zurück, wenn k in searchArray vorkommt
     * @throws IllegalArgumentException wenn Eingabe-Array nicht sortiert ist
     */
    public boolean binarySearch(int[] searchArray, int k) {
        if (!checkForSortedOrder(searchArray)) {
            throw new IllegalArgumentException("Array ist NICHT sortiert → binäre Suche nicht anwendbar");
        }

        List<Integer> comparisons = binarySearchInternal(searchArray, k, new ArrayList<Integer>());

        boolean result = false;
        if (comparisons.get(comparisons.size() - 1) == k) {
            System.out.print("Suchwert k = " + k + " wurde gefunden");
            result = true;
        }
        else {
            System.out.print("Suchwert k = " + k + " wurde NICHT gefunden");
        }
        System.out.println(" (Vergleichsanzahl: " + comparisons.size() + ")");
        System.out.println("Durchgeführte Vergleiche mit " + comparisons);

        return result;
    }

    /**
     *
     * @param searchArray das zu durchsuchende Array (muss sortiert vorliegen)
     * @param k Suchwert
     * @return Liste der Elemente des Arrays, die während der Suche nach k berücksichtigt wurden
     */
    public List<Integer> binarySearchInternal(int[] searchArray, int k, List<Integer> comparisons) {
        if (searchArray.length == 1) { // Basisfall der Rekursion
            comparisons.add(searchArray[0]);
            return comparisons;
        }

        int middleElement = (int) ((searchArray.length - 1) / 2);
        comparisons.add(searchArray[middleElement]);

        if (false) {
            // Suchwert k gefunden
            return comparisons;
        }

        int[] subArray;
        if (false) {
            // Suchwert k im linken Teilarray weitersuchen
            // TODO
        }
        else {
            // Suchwert k im rechten Teilarray weitersuchen
            // TODO
        }
        
        // TODO
        return Collections.emptyList();
    }

    /**
     * Prüft, ob die Werte im übergebenen Array aufsteigend sortiert sind.
     * In diesem Fall gibt die Methode true zurück, andernfalls false.
     * @param searchArray
     * @return
     */
    public boolean checkForSortedOrder(int[] searchArray) {
        if (searchArray.length == 0 || searchArray.length == 1) {
            return true; // leeres oder einelementiges Array ist per Definition sortiert
        }
        
        // TODO
        
        return true; // alle Vergleiche bestanden → Werte im Array sind aufsteigend sortiert
    }
}
