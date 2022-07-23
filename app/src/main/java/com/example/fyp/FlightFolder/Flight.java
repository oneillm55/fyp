package com.example.fyp.FlightFolder;

public class Flight {
    private String Arrive;

    public Flight(String arrive, String depart, String score, String uuid) {
        Arrive = arrive;
        Depart = depart;
        Score = score;
        this.uuid = uuid;
    }

    private String Depart;
    private String Score;
    private String uuid;

//    public Flight() {
//    }

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

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
