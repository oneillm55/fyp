package com.example.fyp.RecommendationsFolder.ClothingRecs;

public class WashingRec {
    boolean coldWash,airDry;
    double coldWashTSaved, airDryTSaved;
    String coldWashSuggestion, airDrySuggestion;
    public WashingRec() {
    }

    public WashingRec(boolean coldWash, boolean airDry, double coldWashTSaved, double airDryTSaved) {
        this.coldWash = coldWash;
        this.airDry = airDry;
        this.coldWashTSaved = coldWashTSaved;
        this.airDryTSaved = airDryTSaved;
    }

    public boolean isColdWash() {
        return coldWash;
    }

    public void setColdWash(boolean coldWash) {
        this.coldWash = coldWash;
    }

    public boolean isAirDry() {
        return airDry;
    }

    public void setAirDry(boolean airDry) {
        this.airDry = airDry;
    }

    public double getColdWashTSaved() {
        return coldWashTSaved;
    }

    public void setColdWashTSaved(double coldWashTSaved) {
        this.coldWashTSaved = coldWashTSaved;
    }

    public double getAirDryTSaved() {
        return airDryTSaved;
    }

    public void setAirDryTSaved(double airDryTSaved) {
        this.airDryTSaved = airDryTSaved;
    }

    public String getColdWashSuggestion() {
        return coldWashSuggestion;
    }

    public void setColdWashSuggestion(String coldWashSuggestion) {
        this.coldWashSuggestion = coldWashSuggestion;
    }

    public String getAirDrySuggestion() {
        return airDrySuggestion;
    }

    public void setAirDrySuggestion(String airDrySuggestion) {
        this.airDrySuggestion = airDrySuggestion;
    }

    public double getTotalWashingTSaved() {
        return airDryTSaved+coldWashTSaved;
    }
}
