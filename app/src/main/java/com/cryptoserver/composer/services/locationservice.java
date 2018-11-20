package com.cryptoserver.composer.services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cryptoserver.composer.activity.locationawareactivity;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class locationservice extends Service implements LocationListener, GpsStatus.Listener {
    public static final String LOG_TAG = locationservice.class.getSimpleName();

    private final LocationServiceBinder binder = new LocationServiceBinder();
    boolean isLocationManagerUpdatingLocation;

    LocationManager locationManager;

    ArrayList<Location> locationList;

    ArrayList<Location> oldLocationList;
    ArrayList<Location> noAccuracyLocationList;
    ArrayList<Location> inaccurateLocationList;
    ArrayList<Location> kalmanNGLocationList;
    private Location oldlocation;


    String satelliteid = "", satelliteangle = "",currentaddress = "";


    boolean isLogging;

    float currentSpeed = 0.0f; // meters/second

    long runStartTimeInMillis;

    ArrayList<Integer> batteryLevelArray;
    ArrayList<Float> batteryLevelScaledArray;
    int batteryScale;
    int gpsCount;

    public locationservice() {

    }

    @Override
    public void onCreate() {
        isLocationManagerUpdatingLocation = false;
        locationList = new ArrayList<>();
        noAccuracyLocationList = new ArrayList<>();
        oldLocationList = new ArrayList<>();
        inaccurateLocationList = new ArrayList<>();
        kalmanNGLocationList = new ArrayList<>();

        isLogging = false;

        batteryLevelArray = new ArrayList<>();
        batteryLevelScaledArray = new ArrayList<>();
        registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        startUpdatingLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addGpsStatusListener(this);
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        super.onStartCommand(i, flags, startId);
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG, "onRebind ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind ");

        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy ");
    }


    //This is where we detect the app is being killed, thus stop service.
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "onTaskRemoved ");
        this.stopUpdatingLocation();

        stopSelf();
    }

    /**
     * Binder class
     *
     * @author Takamitsu Mizutori
     *
     */
    public class LocationServiceBinder extends Binder {
        public locationservice getService() {
            return locationservice.this;
        }
    }


    /* LocationListener implemenation */
    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            notifyLocationProviderStatusUpdated(false);
        }

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            notifyLocationProviderStatusUpdated(true);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            if (status == LocationProvider.OUT_OF_SERVICE) {
                notifyLocationProviderStatusUpdated(false);
            } else {
                notifyLocationProviderStatusUpdated(true);
            }
        }
    }

    /* GpsStatus.Listener implementation */
    @Override
    public void onGpsStatusChanged(int event) {
        int satellites = 0;
        int satellitesInFix = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();
     //   Log.i(TAG, "Time to first fix = " + timetofix);
        for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
            if(sat.usedInFix()) {
                satellitesInFix++;
            }

            GpsSatellite satellite = sat;

            satelliteid = "" + satellite.getPrn();
            int angle=(int)satellite.getAzimuth();

            if(angle > 0)
                satelliteangle = "" +angle ;

                 xdata.getinstance().saveSetting("satelliteid", satelliteid);
                 xdata.getinstance().saveSetting("satelliteangle", satelliteangle +"°");

            satellites++;
        }
        xdata.getinstance().saveSetting("gpsnumberofsatelites", "" + satellites);
    }

    private void notifyLocationProviderStatusUpdated(boolean isLocationProviderAvailable) {
        //Broadcast location provider status change here
    }

    public void startUpdatingLocation() {
        if(this.isLocationManagerUpdatingLocation == false){
            isLocationManagerUpdatingLocation = true;
            runStartTimeInMillis = (long)(SystemClock.elapsedRealtimeNanos() / 1000000);


            locationList.clear();

            oldLocationList.clear();
            noAccuracyLocationList.clear();
            inaccurateLocationList.clear();
            kalmanNGLocationList.clear();

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //Exception thrown when GPS or Network provider were not available on the user's device.
            try {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE); //setAccuracyは内部では、https://stackoverflow.com/a/17874592/1709287の用にHorizontalAccuracyの設定に変換されている。
                    criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                //API level 9 and up
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                //criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                //criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

                Integer gpsFreqInMillis = 0;
                Integer gpsFreqInDistance = 0;  // in meters

                locationManager.addGpsStatusListener(this);

                locationManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, this, null);

                /* Battery Consumption Measurement */
                gpsCount = 0;
                batteryLevelArray.clear();
                batteryLevelScaledArray.clear();

            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (SecurityException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
        }
    }


    public void stopUpdatingLocation(){
        if(this.isLocationManagerUpdatingLocation == true){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(this);
            isLocationManagerUpdatingLocation = false;
        }
    }

    @Override
    public void onLocationChanged(final Location newLocation) {
        if(newLocation == null)
            return;

        Log.d(TAG, "(" + newLocation.getLatitude() + "," + newLocation.getLongitude() + ")");

        gpsCount++;

        Intent intent = new Intent("LocationUpdated");
        intent.putExtra("location", newLocation);

        updatelocationsparams(newLocation);
        LocalBroadcastManager.getInstance(this.getApplication()).sendBroadcast(intent);
    }

    /* Battery Consumption */
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryLevelScaled = batteryLevel / (float)scale;

            batteryLevelArray.add(Integer.valueOf(batteryLevel));
            batteryLevelScaledArray.add(Float.valueOf(batteryLevelScaled));
            batteryScale = scale;
        }
    };



    public void updatelocationsparams(Location location) {

        double doubleTotalDistance = 0.0;
        double currenttime = 0;
        double altitude = 0.0;

        double newTime = System.currentTimeMillis();
        if (oldlocation != null) {
            long meter = common.calculateDistance(location.getLatitude(), location.getLongitude(),
                    oldlocation.getLatitude(), oldlocation.getLongitude());
            doubleTotalDistance = doubleTotalDistance + meter;
        }


        xdata.getinstance().saveSetting("distancetravelled", "" + "" + ((int) doubleTotalDistance));
        xdata.getinstance().saveSetting("gpsaccuracy", "" + location.getAccuracy());
        xdata.getinstance().saveSetting("strengthofsatellites", "" + location.getAccuracy());

        if (location.hasSpeed()) {
            xdata.getinstance().saveSetting("speed", "" + location.getSpeed());
        } else {
            if (oldlocation != null) {
                long meter = common.calculateDistance(location.getLatitude(), location.getLongitude(),
                        oldlocation.getLatitude(), oldlocation.getLongitude());
                doubleTotalDistance = doubleTotalDistance + meter;
                double timeDifferance = (location.getTime() - oldlocation.getTime());
                String strspeed="0";
                if(meter > 0)
                {
                    double speed = meter / timeDifferance;
                    strspeed = "" + speed;
                    if (strspeed.contains("."))
                        strspeed = strspeed.substring(0, strspeed.indexOf("."));
                }
                xdata.getinstance().saveSetting("speed", "" +strspeed);
            }
        }

        if (location.hasAltitude()) {
            int outValue = (int) (location.getAltitude() / 0.3048);
            xdata.getinstance().saveSetting(config.gpsaltitude, "" + outValue);

        } else {
            int outValue = (int) altitude;
            xdata.getinstance().saveSetting(config.gpsaltitude, "" + outValue);
        }

        oldlocation = location;
    }
}


