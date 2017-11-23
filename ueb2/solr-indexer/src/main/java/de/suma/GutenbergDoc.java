package de.suma;

import java.util.List;

/**
 * Repräsentiert ein E-Book aus der digitalen Bibliothek Projekt Gutenberg
 */
public class GutenbergDoc {

    private String docId;

    private String title;

    private List<String> subjectHeadings;

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
}
