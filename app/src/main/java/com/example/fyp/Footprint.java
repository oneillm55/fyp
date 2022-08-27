package com.example.fyp;

public class Footprint {

    double food;
    double flight;
    double clothing;
    String ambition;

    public Footprint(double food, double flight, double clothing, String ambition) {
        this.food = food;
        this.flight = flight;
        this.clothing = clothing;
        this.ambition = ambition;
    }

    public Footprint() {
    }

    public double getFood() {
        return food;
    }

    public void setFood(double food) {
        this.food = food;
    }

    public double getFlight() {
        return flight;
    }

    public void setFlight(double flight) {
        this.flight = flight;
    }

    public double getClothing() {
        return clothing;
    }

    public void setClothing(double clothing) {
        this.clothing = clothing;
    }
    public String getAmbition() {
        return ambition;
    }

    public void setAmbition(String ambition) {
        this.ambition = ambition;
    }
}