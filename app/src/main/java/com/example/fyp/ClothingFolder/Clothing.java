package com.example.fyp.ClothingFolder;

import com.google.android.material.slider.Slider;

public class Clothing {
    double airDryLbs, secondHandLbs, sustainableLbs, coldWashLbs, returnLbs, totalLbs, airDryPercent, secondHandPercent, sustainablePercent, coldWashPercent, returnPercent, returnOnlinePercent;

    public Clothing() {
    }

    public Clothing(double airDryLbs, double secondHandLbs, double sustainableLbs, double coldWashLbs, double returnLbs, double totalLbs, double airDryPercent, double secondHandPercent, double sustainablePercent, double coldWashPercent, double returnPercent, double returnOnlinePercent) {
        this.airDryLbs = airDryLbs;
        this.secondHandLbs = secondHandLbs;
        this.sustainableLbs = sustainableLbs;
        this.coldWashLbs = coldWashLbs;
        this.returnLbs = returnLbs;
        this.totalLbs = totalLbs;
        this.airDryPercent = airDryPercent;
        this.secondHandPercent = secondHandPercent;
        this.sustainablePercent = sustainablePercent;
        this.coldWashPercent = coldWashPercent;
        this.returnPercent = returnPercent;
        this.returnOnlinePercent = returnOnlinePercent;
    }

    public double getAirDryLbs() {
        return airDryLbs;
    }

    public void setAirDryLbs(double airDryLbs) {
        this.airDryLbs = airDryLbs;
    }

    public double getSecondHandLbs() {
        return secondHandLbs;
    }

    public void setSecondHandLbs(double secondHandLbs) {
        this.secondHandLbs = secondHandLbs;
    }

    public double getSustainableLbs() {
        return sustainableLbs;
    }

    public void setSustainableLbs(double sustainableLbs) {
        this.sustainableLbs = sustainableLbs;
    }

    public double getColdWashLbs() {
        return coldWashLbs;
    }

    public void setColdWashLbs(double coldWashLbs) {
        this.coldWashLbs = coldWashLbs;
    }

    public double getReturnLbs() {
        return returnLbs;
    }

    public void setReturnLbs(double returnLbs) {
        this.returnLbs = returnLbs;
    }

    public double getTotalLbs() {
        return totalLbs;
    }

    public void setTotalLbs(double totalLbs) {
        this.totalLbs = totalLbs;
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
}
