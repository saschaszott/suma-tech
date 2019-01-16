package de.suma;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;

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

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            // Soundex Code ist immer vierstellig (erste Stelle ist ein Buchstaben; dann folgen drei Ziffern aus {1,...6}
            Soundex soundex = new Soundex();

            try {
                String soundexEncodedS1 = soundex.encode(s1);
                System.out.println("soundex encoding of " + s1 + " is " + soundexEncodedS1);

                String soundexEncodedS2 = soundex.encode(s2);
                System.out.println("soundex encoding of " + s2 + " is " + soundexEncodedS2);


                // returns the number of characters in the two encoded Strings that are the same.
                // This return value ranges from 0 through 4: 0 indicates little or no similarity, and 4 indicates
                // strong similarity or identical values.
                int diff = soundex.difference(s1, s2);
                System.out.println("soundex_diff(" + s1 + ", " + s2 + ") = " + diff);
            }
            catch (EncoderException e) {
                System.err.println("unerwarteter Fehler bei der Codierung: " + e.getMessage());
            }
            catch (IllegalArgumentException e) {
                System.err.println("Soundex kann nur für ASCII-Zeichen berechnet werden");
            }

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            // refined soundex code is optimized for spell checking words.
            RefinedSoundex refinedSoundex = new RefinedSoundex();

            try {
                String refinedSoundexEncodedS1 = refinedSoundex.encode(s1);
                System.out.println("refined soundex encoding of " + s1 + " is " + refinedSoundexEncodedS1);

                String refinedSoundexEncodedS2 = refinedSoundex.encode(s2);
                System.out.println("refined soundex encoding of " + s2 + " is " + refinedSoundexEncodedS2);

                // for refined Soundex, the return value can be greater than 4.
                int diff = refinedSoundex.difference(s1, s2);
                System.out.println("refinedSoundex_diff(" + s1 + ", " + s2 + ") = " + diff);
            }
            catch (EncoderException e) {
                System.err.println("unerwarteter Fehler bei der Codierung: " + e.getMessage());
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Refined Soundex kann nur für ASCII-Zeichen berechnet werden");
            }

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            // Kölner Phonetik is a phonetic algorithm which is optimized for the German language (1969, H. J. Postel)
            ColognePhonetic colognePhonetic = new ColognePhonetic();

            String colognePhoneticEncodedS1 = colognePhonetic.encode(s1);
            System.out.println("cologne phonetic encoding of " + s1 + " is " + colognePhoneticEncodedS1);

            String colognePhoneticEncodedS2 = colognePhonetic.encode(s2);
            System.out.println("cologne phonetic encoding of " + s2 + " is " + colognePhoneticEncodedS2);

            boolean phoneticEqual = colognePhonetic.isEncodeEqual(s1, s2);
            System.out.println(s1 + " and " + s2 + " are equal with respect to cologne phonetic: " + phoneticEqual);

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        }

        reader.close();
    }
}
