package com.example.fyp.RecommendationsFolder.FoodRecs;

public class OrganicRec {
    double organicTSaved;
    String suggestion;

    public OrganicRec() {
    }

    public OrganicRec(double organicTSaved, String suggestion) {
        this.organicTSaved = organicTSaved;
        this.suggestion = suggestion;
    }

    public double getOrganicTSaved() {
        return organicTSaved;
    }

    public void setOrganicTSaved(double organicTSaved) {
        this.organicTSaved = organicTSaved;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
