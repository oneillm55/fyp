package com.example.fyp.RecommendationsFolder.ClothingRecs;

public class PurchasingRec {
    double secondHandTSaved , sustainableTSaved;
    boolean sustainable, secondHand;

    public PurchasingRec() {
    }

    public PurchasingRec(double secondHandTSaved, double sustainableTsaved, boolean sustainable, boolean secondHand) {
        this.secondHandTSaved = secondHandTSaved;
        this.sustainableTSaved = sustainableTsaved;
        this.sustainable = sustainable;
        this.secondHand = secondHand;
    }

    public double getSecondHandTSaved() {
        return secondHandTSaved;
    }

    public void setSecondHandTSaved(double secondHandTSaved) {
        this.secondHandTSaved = secondHandTSaved;
    }

    public double getSustainableTSaved() {
        return sustainableTSaved;
    }

    public void setSustainableTSaved(double sustainableTSaved) {
        this.sustainableTSaved = sustainableTSaved;
    }

    public boolean isSustainable() {
        return sustainable;
    }

    public void setSustainable(boolean sustainable) {
        this.sustainable = sustainable;
    }

    public boolean isSecondHand() {
        return secondHand;
    }

    public void setSecondHand(boolean secondHand) {
        this.secondHand = secondHand;
    }

    public double getTotalPurchasingTSaved() {
        return sustainableTSaved+secondHandTSaved;
    }

}
