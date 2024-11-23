package de.suma;

import org.apache.commons.lang3.StringUtils;

public class Tokenizer {

    /**
     * Erzeugt aus dem übergebenen Text ein Array von Tokens.
     * Dazu wird der Text an Whitespaces aufgetrennt. Sämtliche
     * Token werden anschließend in Kleinbuchstaben umgewandelt
     * (sogenanntes Lowercasing).
     *
     * @param text der zu tokenisierende Text
     * @return Array von Tokens nach der Whitespace-Tokenisierung
     */
    public String[] getTokens(String text) {
        return StringUtils.split(StringUtils.lowerCase(text));
    }

    /**
     * Erzeugt aus dem übergebenen Text ein Array von Tokens.
     * Dazu wird der Text an Whitespaces aufgetrennt. Die so entstehenden Tokens
     * werden noch von einer ggf. vorhandenen öffnenden Klammer am Anfang
     * bzw. einer Menge von Satzzeichen am Ende befreit.
     * Befindet sich nach der Säuberung des Tokens am Ende noch eine schließende Klammer,
     * so wird auch diese entfernt.
     * Endet das so entstandene Token mit dem Suffix "'s", so wird dieses entfernt
     * (hierbei handelt es sich um eine sehr einfache Variante eines Stemmers).
     *
     * @param text der zu tokenisierende Text
     * @return Array von Tokens (ohne Klammer am Anfang und Satzzeichen am Ende)
     */
    public String[] getTokensImproved(String text) {
        String[] tokens = getTokens(text);

        // TODO durch die Token in tokens iterieren und auf jedes Token folgende Schritte anwenden
        // TODO wenn Token mit "(" beginnt, die öffnende Klammer entfernen
        // TODO wenn Token mit ".", "!", "?", ",", ";", ":", "-" endet, das Suffix entfernen
        // TODO wenn das verbleibende Token mit ")" endet, die schließende Klammer entfernen
        // TODO besitzt das resultierende Token das Suffix "'s", so ist dieses zu entfernen

        return tokens;
    }
}
