package de.suma;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.CosineDistance;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);

        while (true) {
            System.out.println("document (or type quit! to exit): ");
            String document = StringUtils.lowerCase(reader.nextLine());

            if (document.equals("quit!")) {
                break;
            }

            System.out.println("query: ");
            String query = StringUtils.lowerCase(reader.nextLine());

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");


            CosineDistance cd = new CosineDistance();
            System.out.println("Cosine Similarity (nnc.nnc weighted) between d and q is " + (1 - cd.apply(document, query)));
        }

        reader.close();
    }
}
