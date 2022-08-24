package com.example.fyp;

import android.app.Application;

public class MyApplication extends Application {

    private Footprint userFootprint;

    public Footprint getUserFootprint() {
        return userFootprint;
    }

    public void setUserFootprint(Footprint userFootprint) {
        this.userFootprint = userFootprint;
    }
}
