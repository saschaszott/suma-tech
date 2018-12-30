package de.suma;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);

        while (true) {
            System.out.println("s1 (or type quit! to exit): ");
            String s1 = StringUtils.lowerCase(reader.nextLine());

            if (s1.equals("quit!")) {
                break;
            }

            System.out.println("s2: ");
            String s2 = StringUtils.lowerCase(reader.nextLine());

            LevenshteinDistance ld = new LevenshteinDistance();
            double ldValue = ld.apply(s1, s2);
            System.out.println("lev_dist(" + s1 + ", " + s2 + ") = " + ldValue);

            LevenshteinDetailedDistance ldDetail = new LevenshteinDetailedDistance();
            LevenshteinResults result = ldDetail.apply(s1, s2);
            System.out.println("Num of Insertions: " + result.getInsertCount());
            System.out.println("Num of Deletions: " + result.getDeleteCount());
            System.out.println("Num of Substitutions: " + result.getSubstituteCount());

            // Normalisierung der Levenshtein-Distanz auf das Intervall [0, 1]
            // die Levenshtein-Distanz zwischen zwei Zeichenketten kann maximal so groß sein, wie
            // die Länge der längeren Zeichenkette (alle Zeichen in der kürzeren Zeichenkette
            // ersetzen; Zeichen hinzufügen, bis die längere Zeichenkette erzeugt wurde)
            // Beispiel: lev_dist(aaa, bbbbb) = 5

            if (ldValue > 0) {
                int maxLength = Math.max(StringUtils.length(s1), StringUtils.length(s2));
                System.out.println("lev_dist_normalized(" + s1 + ", " + s2 + ") = " + (ldValue / maxLength));
            }

            // JaroWinklerDistance berechnet nicht die Distanz, sondern die Ähnlichkeit
            JaroWinklerDistance jwd = new JaroWinklerDistance();
            double jwdValue = 1 - jwd.apply(s1, s2);
            System.out.println("jw_dist(" + s1 + ", " + s2 + ") = " + jwdValue);

            // Berechnung der Cosinus-Distanz nur sinnvoll, wenn eine Eingabezeichenkette aus
            // mindestens 2 Termen besteht (getrennt durch Whitespace)
            // beide Zeichenketten werden durch Vektorren modelliert, wobei die einzelnen
            // Komponenten der Term Frequency (tf) entsprechen
            // beide Vektoren werden normalisiert mittels 2-Norm / Euklidische Norm
            if (s1.contains(" ") || s2.contains(" ")) {
                CosineDistance cd = new CosineDistance();
                System.out.println("cd_dist('" + s1 + "', '" + s2 + "') = " + cd.apply(s1, s2));
            }
        }

        reader.close();
    }
}
