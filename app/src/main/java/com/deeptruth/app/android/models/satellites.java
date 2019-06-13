package com.deeptruth.app.android.models;

/**
 * Created by root on 9/8/18.
 */

public class satellites
{
    public String number="";
    public String altitude="";
    public String name="";

    public satellites(String number, String altitude, String name)
    {
        setNumber(number);
        setAltitude(altitude);
        setName(name);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
