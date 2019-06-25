package com.deeptruth.app.android.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ${matraex} on 25/6/19.
 */

public class coloredpoint {
    public LatLng coords;
    public String color;

    public coloredpoint(LatLng coords, String color) {
        this.coords = coords;
        this.color = color;
    }
}
