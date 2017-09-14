package de.suma;

public class SearchHit {

    private int id;

    private String text;

    private Double score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String toString() {
        return id + ": " + text + " (score: " + score + ")";
    }
}
