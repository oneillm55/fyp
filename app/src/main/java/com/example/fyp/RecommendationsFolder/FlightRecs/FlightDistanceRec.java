package com.example.fyp.RecommendationsFolder.FlightRecs;

public class FlightDistanceRec {
    double cost;
    double tSavedAmbitious;//the potential saved tonnes of co2 for an ambitious user
    double tSavedModerate;//the potential saved tonnes of co2 for a moderate user
    double percent;
    String suggestion;
    int count;//the number of flights in each class saved by the user

    public FlightDistanceRec() {
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getTSavedAmbitious() {
        return tSavedAmbitious;
    }

    public void setTSavedAmbitious(double tSavedAmbitious) {
        this.tSavedAmbitious = tSavedAmbitious;
    }

    public double getTSavedModerate() {
        return tSavedModerate;
    }

    public void setTSavedModerate(double tSavedModerate) {
        this.tSavedModerate = tSavedModerate;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
