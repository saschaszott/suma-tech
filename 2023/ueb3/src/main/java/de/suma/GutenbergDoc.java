package de.suma;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein E-Book aus der digitalen Bibliothek Projekt Gutenberg
 */
public class GutenbergDoc {

    /**
     * eindeutige Dokument-ID (entspricht der Gutenberg-ID)
     */
    private String docId;

    /**
     * Dokumenttitel
     */
    private String title;

    /**
     * Liste von Subject Headings
     */
    private List<String> subjectHeadings;

    /**
     * Name des Autors (Nachname, Vorname)
     */
    private String author;

    /**
     * Wikipedia-URL des Autors (englischsprachige Version)
     */
    private String authorWikipediaURL;

    /**
     * Anzahl der Downloads innerhalb der letzten 30 Tage
     * (entspricht dem Zeitraum Nov bis Dez 2017)
     */
    private String numOfDownloadsLast30Days;

    /**
     * Sprache(n) des Dokuments
     */
    private List<String> languages;

    /**
     * Dokumenttyp
     */
    private String docType;

    /**
     * URL des Volltexts (UTF-8 codiert)
     * Hinweis: wird nicht indexiert (wurde im Rahmen der Vorbereitung für das Herunterladen der Volltextdatei benötigt)
     */
    private String fulltextUrl;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSubjectHeadings() {
        return subjectHeadings;
    }

    public void setSubjectHeadings(List<String> subjectHeadings) {
        this.subjectHeadings = subjectHeadings;
    }

    public void addSubjectHeading(String subjectHeading) {
        if (subjectHeadings == null) {
            subjectHeadings = new ArrayList<>();
        }
        subjectHeadings.add(subjectHeading);
    }

    /**
     * Autor ist kein Pflichtfelder. Es ist sichergestellt, dass kein Ebook mehr als einen Autor hat (habe ich vorher herausgefiltert).
     * @return
     */
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorWikipediaURL() {
        return authorWikipediaURL;
    }

    public void setAuthorWikipediaURL(String authorWikipediaURL) {
        this.authorWikipediaURL = authorWikipediaURL;
    }

    public String getNumOfDownloadsLast30Days() {
        return numOfDownloadsLast30Days;
    }

    public void setNumOfDownloadsLast30Days(String numOfDownloadsLast30Days) {
        this.numOfDownloadsLast30Days = numOfDownloadsLast30Days;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void addLanguage(String language) {
        if (languages == null) {
            languages = new ArrayList<>();
        }
        languages.add(language);
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFulltextUrl() {
        return fulltextUrl;
    }

    public void setFulltextUrl(String fulltextUrl) {
        this.fulltextUrl = fulltextUrl;
    }
}