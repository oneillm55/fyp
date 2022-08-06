package com.example.fyp.FlightFolder;

public class Flight {
    private String Arrive;
    private String Depart;
    private String flightClass;
    private double footprint;
    private boolean returnFlight;

    public Flight(String arrive, String depart, String flightClass, double footprint, boolean returnFlight) {
        Arrive = arrive;
        Depart = depart;
        this.flightClass = flightClass;
        this.footprint = footprint;
        this.returnFlight = returnFlight;
    }

    public Flight() {
    }

    public String getArrive() {
        return Arrive;
    }

    public void setArrive(String arrive) {
        Arrive = arrive;
    }

    public String getDepart() {
        return Depart;
    }

    public void setDepart(String depart) {
        Depart = depart;
    }

    public String getFlightClass() {
        return flightClass;
    }

    public void setFlightClass(String flightClass) {
        this.flightClass = flightClass;
    }

    public double getFootprint() {
        return footprint;
    }

    public void setFootprint(double footprint) {
        this.footprint = footprint;
    }

    public boolean isReturnFlight() {
        return returnFlight;
    }

    public void setReturnFlight(boolean returnFlight) {
        this.returnFlight = returnFlight;
    }
}
