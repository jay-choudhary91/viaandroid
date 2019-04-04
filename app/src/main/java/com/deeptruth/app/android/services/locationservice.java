package com.deeptruth.app.android.services;

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

import com.deeptruth.app.android.activity.locationawareactivity;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.googleutils;
import com.deeptruth.app.android.utils.xdata;


import java.math.BigDecimal;
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


    String satelliteid = "", satelliteangle = "";


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
        try {
            unregisterReceiver(batteryInfoReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                 xdata.getinstance().saveSetting("satelliteangle", satelliteangle);

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
                criteria.setAltitudeRequired(true);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(true);

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

        if(location == null)
            return;

        if(common.isnetworkconnected(getApplicationContext()))
            fetchcompleteaddress(location);

        xdata.getinstance().saveSetting("gpsaccuracy", "" + location.getAccuracy());
        xdata.getinstance().saveSetting("strengthofsatellites", "" + location.getAccuracy());
        oldlocation = location;
    }

    //get complete address
    private void fetchcompleteaddress(final Location location) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String strAdd = "";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);

                        ArrayList<String> addressitems=new ArrayList<>();
                        addressitems.add(returnedAddress.getFeatureName());  // Mansarovar Plaza
                        //addressitems.add(returnedAddress.getLocality());   // Jaipur
                        addressitems.add(returnedAddress.getSubAdminArea()); // Jaipur
                        addressitems.add(returnedAddress.getAdminArea());    // Rajasthan
                        addressitems.add(returnedAddress.getCountryCode());  // IN

                        StringBuilder strReturnedAddress = new StringBuilder("");
                        for (int i = 0; i < addressitems.size(); i++)
                        {
                            if((! addressitems.get(i).trim().isEmpty()) || addressitems.get(i).equalsIgnoreCase("null"))
                                if(strReturnedAddress.toString().trim().isEmpty())
                                {
                                    strReturnedAddress.append(addressitems.get(i));
                                }
                                else
                                {
                                    strReturnedAddress.append(", "+addressitems.get(i));
                                }
                        }
                        String currentaddress = strReturnedAddress.toString();
                        xdata.getinstance().saveSetting("currentaddress", "" + currentaddress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}


