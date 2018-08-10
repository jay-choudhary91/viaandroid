package com.cryptoserver.composer.utils;

import android.location.Location;

/**
 * Created by Ejaz on 2/14/2017.
 */

public class GoogleUtils {

    /**
     * Saving the user curent location for the current location
     *
     * @param location user current location from location aware activity
     */
    public static void saveUserCurrentLocation(Location location) {
        xdata.getinstance().saveSetting("lat", "" + location.getLatitude());
        xdata.getinstance().saveSetting("lng", "" + location.getLongitude());
        xdata.getinstance().saveSetting("gpsaltittude", "" + location.getLongitude());
        xdata.getinstance().saveSetting("gpsquality", "" + location.getAccuracy());

    }
}
