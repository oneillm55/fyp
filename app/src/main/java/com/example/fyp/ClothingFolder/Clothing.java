package com.example.fyp.ClothingFolder;

import com.google.android.material.slider.Slider;

public class Clothing {
    double airDryT, secondHandT, sustainableT, coldWashT, returnT, totalT, airDryPercent, secondHandPercent, sustainablePercent, coldWashPercent, returnPercent, returnOnlinePercent;

    public Clothing() {
    }

    public Clothing(double airDryT, double secondHandT, double sustainableT, double coldWashT, double returnT, double totalT, double airDryPercent, double secondHandPercent, double sustainablePercent, double coldWashPercent, double returnPercent, double returnOnlinePercent) {
        this.airDryT = airDryT;
        this.secondHandT = secondHandT;
        this.sustainableT = sustainableT;
        this.coldWashT = coldWashT;
        this.returnT = returnT;
        this.totalT = totalT;
        this.airDryPercent = airDryPercent;
        this.secondHandPercent = secondHandPercent;
        this.sustainablePercent = sustainablePercent;
        this.coldWashPercent = coldWashPercent;
        this.returnPercent = returnPercent;
        this.returnOnlinePercent = returnOnlinePercent;
    }

    public double getAirDryT() {
        return airDryT;
    }

    public void setAirDryT(double airDryT) {
        this.airDryT = airDryT;
    }

    public double getSecondHandT() {
        return secondHandT;
    }

    public void setSecondHandT(double secondHandT) {
        this.secondHandT = secondHandT;
    }

    public double getSustainableT() {
        return sustainableT;
    }

    public void setSustainableT(double sustainableT) {
        this.sustainableT = sustainableT;
    }

    public double getColdWashT() {
        return coldWashT;
    }

    public void setColdWashT(double coldWashT) {
        this.coldWashT = coldWashT;
    }

    public double getReturnT() {
        return returnT;
    }

    public void setReturnT(double returnT) {
        this.returnT = returnT;
    }

    public double getTotalT() {
        return totalT;
    }

    public void setTotalT(double totalT) {
        this.totalT = totalT;
    }

    public double getAirDryPercent() {
        return airDryPercent;
    }

    public void setAirDryPercent(double airDryPercent) {
        this.airDryPercent = airDryPercent;
    }

    public double getSecondHandPercent() {
        return secondHandPercent;
    }

    public void setSecondHandPercent(double secondHandPercent) {
        this.secondHandPercent = secondHandPercent;
    }

    public double getSustainablePercent() {
        return sustainablePercent;
    }

    public void setSustainablePercent(double sustainablePercent) {
        this.sustainablePercent = sustainablePercent;
    }

    public double getColdWashPercent() {
        return coldWashPercent;
    }

    public void setColdWashPercent(double coldWashPercent) {
        this.coldWashPercent = coldWashPercent;
    }

    public double getReturnPercent() {
        return returnPercent;
    }

    public void setReturnPercent(double returnPercent) {
        this.returnPercent = returnPercent;
    }

    public double getReturnOnlinePercent() {
        return returnOnlinePercent;
    }

    public void setReturnOnlinePercent(double returnOnlinePercent) {
        this.returnOnlinePercent = returnOnlinePercent;
    }

    
    //return largest emmitter lbs

    //return biggest %
}
