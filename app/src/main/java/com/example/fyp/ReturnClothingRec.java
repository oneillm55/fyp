package com.example.fyp;

public class ReturnClothingRec {
    double returnTSaved , returnOnlineTSaved;// hold the balue of potentential tonnes of co2 saved
    boolean returnClothing, returnOnline;// the status of if the user could benifit from a reccomendation for each field
    public ReturnClothingRec() {
    }

    public ReturnClothingRec(double returnTSaved, double returnOnlineTSaved, boolean returnClothing, boolean returnOnline) {
        this.returnTSaved = returnTSaved;
        this.returnOnlineTSaved = returnOnlineTSaved;
        this.returnClothing = returnClothing;
        this.returnOnline = returnOnline;
    }

    public double getReturnTSaved() {
        return returnTSaved;
    }

    public void setReturnTSaved(double returnTSaved) {
        this.returnTSaved = returnTSaved;
    }

    public double getReturnOnlineTSaved() {
        return returnOnlineTSaved;
    }

    public void setReturnOnlineTSaved(double returnOnlineTSaved) {
        this.returnOnlineTSaved = returnOnlineTSaved;
    }

    public boolean isReturnClothing() {
        return returnClothing;
    }

    public void setReturnClothing(boolean returnClothing) {
        this.returnClothing = returnClothing;
    }

    public boolean isReturnOnline() {
        return returnOnline;
    }

    public void setReturnOnline(boolean returnOnline) {
        this.returnOnline = returnOnline;
    }

    public double getTotalReturnTSaved() {
        return returnTSaved + returnOnlineTSaved;
    }
}
