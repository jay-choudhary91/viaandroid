package com.cryptoserver.composer.utils;

import android.location.Location;
import android.util.Log;

/**
 * Created by Ejaz on 2/14/2017.
 */

public class googleutils {

    /**
     * Saving the user curent location for the current location
     *
     * @param location user current location from location aware activity
     */
    public static void saveUserCurrentLocation(Location location) {
        xdata.getinstance().saveSetting("lat", "" + location.getLatitude());
        xdata.getinstance().saveSetting("lng", "" + location.getLongitude());
        xdata.getinstance().saveSetting("gpsaltittude", "" + location.getAltitude());
        xdata.getinstance().saveSetting("gpsquality", "" + location.getAccuracy());
        xdata.getinstance().saveSetting("speed",""+ location.getSpeed());
        xdata.getinstance().saveSetting("heading", ""+ location.getBearing());

    }
    public static void saveuseraddress(String address) {
        xdata.getinstance().saveSetting("address", "" + address);

    }
}
