package com.example.fyp;

import android.location.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatLngMap {
     Map<String, Location> locationMap = new HashMap<String, Location>();

    public LatLngMap() {
        Location ath = new Location("ATH");
        ath. setLatitude(37.9356467);
        ath.setLongitude(23.948415599);
        locationMap.put("ATH",ath  );

        Location bcn = new Location("BCN");
        bcn. setLatitude(41.2971);
        bcn.setLongitude(2.07846);
        locationMap.put("BCN",bcn );

        Location dub = new Location("DUB");
        dub. setLatitude(53.421299);
        dub.setLongitude(-6.27007);
        locationMap.put("DUB",dub );

        Location hkg = new Location("HKG");
        hkg. setLatitude(22.308901);
        hkg.setLongitude(113.915001);
        locationMap.put("HKG",hkg );

        Location lhr = new Location("LHR");
        lhr. setLatitude(51.4706);
        lhr.setLongitude(-0.461941);
        locationMap.put("LHR",lhr );

        Location lax = new Location("LAX");
        lax. setLatitude(33.94250107);
        lax.setLongitude(-118.4079971);
        locationMap.put("LAX",lax );

        Location snn = new Location("SNN");
        snn. setLatitude(52.702);
        snn.setLongitude(-8.92482);
        locationMap.put("SNN",snn  );

    }

    public Map<String, Location> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, Location> locationMap) {
        this.locationMap = locationMap;
    }
}
