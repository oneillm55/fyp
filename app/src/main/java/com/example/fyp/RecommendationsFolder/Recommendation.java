package com.example.fyp.RecommendationsFolder;

public abstract class Recommendation {
    String suggestion = null;
    double tSaved = 0;

    public Recommendation() {
    }

    public Recommendation(String suggestion, double tSaved) {
        this.suggestion = suggestion;
        this.tSaved = tSaved;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public double getTSaved() {
        return tSaved;
    }

    public void setTSaved(double tSaved) {
        this.tSaved = tSaved;
    }
}
