package com.cryptoserver.composer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.fragments.fragmentsettings;
import com.cryptoserver.composer.fragments.videocomposerfragment;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.googleutils;
import com.cryptoserver.composer.utils.noise;
import com.cryptoserver.composer.utils.xdata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public abstract class locationawareactivity extends baseactivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_update_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_updated_time_key";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 99;

    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private GoogleApiClient mGoogleApiClient;
    int GPS_REQUEST_CODE=111;
    public Bundle NewSavedInstanceState=null;
    boolean updateItem=false;
    private TelephonyManager telephonymanager;
    private LocationManager manager;
    private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();

    private SensorManager mSensorManager;
    private Sensor mAccelereometer;
    private BroadcastReceiver flightmodebroadcast ;
    IntentFilter aeroplacemodefilter;

    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;
    private float mCurrentDegree = 0f;
    TelephonyManager mTelephonyManager;

    private IntentFilter intentFilter;
    private BroadcastReceiver mBroadcast;
    String CALL_STATUS="",CALL_DURATION="",CALL_REMOTE_NUMBER="",CALL_START_TIME="";
    MyPhoneStateListener mPhoneStatelistener;
    int mSignalStrength = 0,dbtoxapiupdatecounter=0;

    noise mNoise;
    private static final int PERMISSION_RECORD_AUDIO= 92;
    public static final int my_permission_read_phone_state = 90;
    private static final int request_permissions = 101;
    private Runnable doafterallpermissionsgranted;
    private Location oldlocation;
    private Handler myHandler;
    private Runnable myRunnable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationviavideocomposer.setActivity(locationawareactivity.this);
        NewSavedInstanceState=savedInstanceState;
        telephonymanager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE );

        getallpermissions();
    }

    public void getallpermissions()
    {
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            String[] neededpermissions = {
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
            List<String> deniedpermissions = new ArrayList<>();
            for (String permission : neededpermissions) {
                if (ContextCompat.checkSelfPermission(locationawareactivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedpermissions.add(permission);
                }
            }
            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterallpermissionsgranted();
            } else {
                String[] array = new String[deniedpermissions.size()];
                array = deniedpermissions.toArray(array);
                ActivityCompat.requestPermissions(locationawareactivity.this, array, request_permissions);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_permissions) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted) {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        doafterallpermissionsgranted();
                    }
                };
            } else {
                /*doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(locationawareactivity.this, R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
                        //gethelper().onBack();
                    }
                };*/
                getallpermissions();
            }
        }
    }

    private void doafterallpermissionsgranted() {
        enableGPS(locationawareactivity.this);
        preparemetricesdata();
    }

    @Override
    public void showPermissionDialog() {
        updateItem=true;
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
        else
        {
            enableGPS(locationawareactivity.this);
        }
    }

    public void initLocationAPIs(Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        updateValuesFromBundle(savedInstanceState);

        mGoogleApiClient.connect();
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }
//        mRequestingLocationUpdates = true;
    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
//        mRequestingLocationUpdates = false;
    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(
//                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
//        if (mRequestingLocationUpdates) {
        startLocationUpdates();
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //saving the user current location
        if(location != null)
        {
            googleutils.saveUserCurrentLocation(location);

            if(location.getLatitude() == 0.0)
                return;

            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            if (getcurrentfragment() != null) {
                getcurrentfragment().oncurrentlocationchanged(location);
                updatelocationsparams(location);
                getCompleteAddressString(location);
            }
            //stopLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                enableGPS(locationawareactivity.this);

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                enableGPS(locationawareactivity.this);
            }
            return;
        }

    }*/

    @SuppressLint("MissingPermission")
    public static boolean checkLocationEnable(Context context) {
        //check the Gps settings
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        /*GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            int satellites1 = gpsStatus.getMaxSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            String lSatellites = null;
            int i = 0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                lSatellites = "Satellite" + (i++) + ": "
                        + satellite.getPrn() + ","
                        + satellite.usedInFix() + ","
                        + satellite.getSnr() + ","
                        + satellite.getAzimuth() + ","
                        + satellite.getElevation()+ "\n\n";

                Log.d("SATELLITE",lSatellites);
            }
        }
*/
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public void onGpsStatusChanged(int event) {

    }

    public void setNavigateWithLocation()
    {
        if (locationawareactivity.checkLocationEnable(locationawareactivity.this))
        {
            initLocationAPIs(NewSavedInstanceState);
            if (getcurrentfragment() != null) {
                getcurrentfragment().getAccurateLocation();
            }
        }

    }

    public void enableGPS(final Context context)
    {
        if (!locationawareactivity.checkLocationEnable(context))
        {
            showgpsalert(context);
        }
        else
        {
            setNavigateWithLocation();
        }
    }

    public void showgpsalert(final Context context)
    {
/*        appdialog.showConfirmationDialog(context, "GPS", "GPS is disabled in your device. Would you like to enable it?","YES","NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        setNavigateWithLocation();
                    }
                }).show();*/

        new AlertDialog.Builder(context, R.style.customdialogtheme)
                .setTitle("GPS")
                .setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                        if(dialog != null)
                            dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            setNavigateWithLocation();
        }
    }


    private void preparemetricesdata() {

        metricItemArraylist.clear();

        metricItemArraylist.add(new metricmodel("battery", "", true));
        metricItemArraylist.add(new metricmodel("imeinumber", "", true));
        metricItemArraylist.add(new metricmodel("simserialnumber", "", true));
        metricItemArraylist.add(new metricmodel("version", "", true));
        metricItemArraylist.add(new metricmodel("osversion", "", true));
        metricItemArraylist.add(new metricmodel("softwareversion", "", true));
        metricItemArraylist.add(new metricmodel("model", "", true));
        metricItemArraylist.add(new metricmodel("manufacturer", "", true));
        metricItemArraylist.add(new metricmodel("brightness", "",true));
        metricItemArraylist.add(new metricmodel("gpslatitude", "", true));
        metricItemArraylist.add(new metricmodel("gpslongitude", "", true));
        metricItemArraylist.add(new metricmodel("gpsaltittude", "", true));
        metricItemArraylist.add(new metricmodel("gpsquality", "", true));
        metricItemArraylist.add(new metricmodel("carrier", "", true));
        metricItemArraylist.add(new metricmodel("screenwidth", "", true));
        metricItemArraylist.add(new metricmodel("screenheight", "", true));
        metricItemArraylist.add(new metricmodel("systemuptime", "", true));
        metricItemArraylist.add(new metricmodel("multitaskingenabled", "", true));
        metricItemArraylist.add(new metricmodel("proximitysensorenabled", "", true));
        metricItemArraylist.add(new metricmodel("pluggedin", "", true));
        metricItemArraylist.add(new metricmodel("devicetime", "", true));
        metricItemArraylist.add(new metricmodel("deviceregion", "", true));
        metricItemArraylist.add(new metricmodel("devicelanguage", "", true));
        metricItemArraylist.add(new metricmodel("devicecurrency", "", true));
        metricItemArraylist.add(new metricmodel("timezone", "", true));
        metricItemArraylist.add(new metricmodel("headphonesattached", "", true));
        metricItemArraylist.add(new metricmodel("accessoriesattached", "", true));
        metricItemArraylist.add(new metricmodel("nameattachedaccessories", "", true));
        metricItemArraylist.add(new metricmodel("attachedaccessoriescount", "", true));
        metricItemArraylist.add(new metricmodel("totalspace", "", true));
        metricItemArraylist.add(new metricmodel("usedspace", "", true));
        metricItemArraylist.add(new metricmodel("freespace", "", true));
        metricItemArraylist.add(new metricmodel("deviceorientation", "", true));
        metricItemArraylist.add(new metricmodel("rammemory", "", true));
        metricItemArraylist.add(new metricmodel("usedram", "", true));
        metricItemArraylist.add(new metricmodel("freeram", "", true));
        metricItemArraylist.add(new metricmodel("wificonnect", "", true));
        metricItemArraylist.add(new metricmodel("cellnetworkconnect", "", true));
        metricItemArraylist.add(new metricmodel("internalip", "", true));
        metricItemArraylist.add(new metricmodel("externalip", "", true));
        metricItemArraylist.add(new metricmodel("networktype", "", true));
        metricItemArraylist.add(new metricmodel(config.connectedphonenetworkquality, "", true));
        metricItemArraylist.add(new metricmodel("gravitysensorenabled", "", true));
        metricItemArraylist.add(new metricmodel("gyroscopesensorenabled", "", true));
        metricItemArraylist.add(new metricmodel("lightsensorenabled", "", true));
        metricItemArraylist.add(new metricmodel("debuggerattached", "", true));
        metricItemArraylist.add(new metricmodel("deviceid", "", true));
        metricItemArraylist.add(new metricmodel("bluetoothonoff", "", true));
        metricItemArraylist.add(new metricmodel("wifiname", "", true));
        metricItemArraylist.add(new metricmodel("wifinetworksaveailable", "", true));
        metricItemArraylist.add(new metricmodel("processorcount", "", true));
        metricItemArraylist.add(new metricmodel("activeprocessorcount", "", true));
        metricItemArraylist.add(new metricmodel(config.cpuusageuser, "", true));
        metricItemArraylist.add(new metricmodel(config.cpuusagesystem, "", true));
        metricItemArraylist.add(new metricmodel(config.cpuusageiow, "", true));
        metricItemArraylist.add(new metricmodel(config.cpuusageirq, "", true));
        metricItemArraylist.add(new metricmodel(config.compass, "", true));
        metricItemArraylist.add(new metricmodel(config.decibel, "", true));
        metricItemArraylist.add(new metricmodel(config.barometer, "", true));
        metricItemArraylist.add(new metricmodel("isaccelerometeravailable", "", true));
        metricItemArraylist.add(new metricmodel(config.acceleration_x, "", true));
        metricItemArraylist.add(new metricmodel(config.acceleration_y, "", true));
        metricItemArraylist.add(new metricmodel(config.acceleration_z, "", true));
        metricItemArraylist.add(new metricmodel("distancetraveled", "", true));
        metricItemArraylist.add(new metricmodel("dataconnection", "", true));
        metricItemArraylist.add(new metricmodel(config.currentcallinprogress, "", true));
        metricItemArraylist.add(new metricmodel(config.currentcalldurationseconds, "", true));
        metricItemArraylist.add(new metricmodel(config.currentcallremotenumber, "", true));
        metricItemArraylist.add(new metricmodel("currentcallvolume", "", true));
        metricItemArraylist.add(new metricmodel(config.currentcalldecibel, "", true));
        metricItemArraylist.add(new metricmodel("gpsonoff","",true));
        metricItemArraylist.add(new metricmodel(config.airplanemode,"",true));
        metricItemArraylist.add(new metricmodel("phonetime","",true));
        metricItemArraylist.add(new metricmodel("syncphonetime","",true));
        metricItemArraylist.add(new metricmodel("country","",true));
        metricItemArraylist.add(new metricmodel("connectionspeed","",true));
        metricItemArraylist.add(new metricmodel("gpsaccuracy","",true));
        metricItemArraylist.add(new metricmodel("address","",true));

       // getmetricarraylist();
        startmetrices();

    }

    public void startmetrices()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(getcurrentfragment() instanceof videocomposerfragment)
                        {
                            boolean isrecording=((videocomposerfragment) getcurrentfragment()).isvideorecording();
                            if(isrecording)
                                return;
                        }
                        for(int i=0;i<metricItemArraylist.size();i++)
                        {
                            if(metricItemArraylist.get(i).isSelected())
                            {
                                String value=metric_read(metricItemArraylist.get(i).getMetricTrackKeyName());
                                if(! value.trim().isEmpty())
                                    metricItemArraylist.get(i).setMetricTrackValue(value);
                            }
                        }

                        dbtoxapiupdatecounter++;
                        if(dbtoxapiupdatecounter > 5)
                        {
                            dbtoxapiupdatecounter=0;
                            fetchmetadatadb();
                        }
                    }
                }).start();

                myHandler.postDelayed(this, 5000);
            }
        };
        myHandler.post(myRunnable);
    }



    public ArrayList<metricmodel> getmetricarraylist()
    {
        List<metricmodel> settingupdatedlist=getMetricList();
        if(settingupdatedlist == null || settingupdatedlist.size() == 0)
        {
            for(int i=0;i<metricItemArraylist.size();i++)
            {
                String value=metric_read(metricItemArraylist.get(i).getMetricTrackKeyName());
                if(! value.trim().isEmpty())
                    metricItemArraylist.get(i).setMetricTrackValue(value);
            }
        }
        else
        {
            for(int i=0;i<settingupdatedlist.size();i++)
            {
                if(settingupdatedlist.get(i).isSelected())
                {
                    metricItemArraylist.get(i).setSelected(true);
                }
                else
                {
                    metricItemArraylist.get(i).setSelected(false);
                }
            }
        }


        return metricItemArraylist;
    }

    public List<metricmodel> getMetricList() {
        Gson gson = new Gson();
        List<metricmodel> metricList=new ArrayList<>();

        String value = xdata.getinstance().getSetting(config.metriclist);
        if(value.trim().length() > 0)
        {
            Type type = new TypeToken<List<metricmodel>>() {
            }.getType();
            metricList = gson.fromJson(value, type);
        }
        return metricList;
    }

    @SuppressLint("MissingPermission")
    public String metric_read(String key)
    {
        String metricItemValue="";

        if(key.equalsIgnoreCase(config.decibel) || key.equalsIgnoreCase(config.currentcalldecibel)){
            if (ContextCompat.checkSelfPermission(locationawareactivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(locationawareactivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_RECORD_AUDIO);
            }else{
                //start();
            }
            return "N/A";
        }
        else if(key.equalsIgnoreCase(config.acceleration_x) || key.equalsIgnoreCase(config.acceleration_y) ||
                key.equalsIgnoreCase(config.acceleration_z))
        {
            registerAccelerometerSensor();
            return "";
        }
        else if(key.equalsIgnoreCase(config.compass)){

            SensorManager mSensorManager = (SensorManager) locationawareactivity.this.getSystemService(Context.SENSOR_SERVICE);
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
                registerCompassSensor();
                return "";
            }else{
                return "";
            }
        }
        else if(key.equalsIgnoreCase(config.barometer)){
            SensorManager mSensorManager = (SensorManager) locationawareactivity.this.getSystemService(Context.SENSOR_SERVICE);
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
                registerBarometerSensor();
                return "";

            }else{
                return "";
            }
        }
        else if(key.equalsIgnoreCase("connectedphonenetworkquality"))
        {
            String str = telephonymanager.getNetworkOperatorName();
            if(str != null)
            {
                if(! str.trim().isEmpty())
                {
                    registerMobileNetworkStrength();
                    return "";
                }
            }
        }
        else if(key.equalsIgnoreCase(config.cpuusageuser) || key.equalsIgnoreCase(config.cpuusagesystem)
                || key.equalsIgnoreCase(config.cpuusageiow) || key.equalsIgnoreCase(config.cpuusageirq))
        {
            registerUsageUser();
            return "";
        }

        else if(key.equalsIgnoreCase(config.currentcallinprogress) || key.equalsIgnoreCase(config.currentcalldurationseconds)
                || key.equalsIgnoreCase(config.currentcallremotenumber))
        {
            getCallInfo();
            return "";
        }

        else if(key.equalsIgnoreCase("imeinumber"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                metricItemValue = telephonymanager.getImei();
            }
            else
            {
                metricItemValue = telephonymanager.getDeviceId();
            }
        }
        else if(key.equalsIgnoreCase("username"))
        {
            metricItemValue = common.getUsername();
        }
        else if(key.equalsIgnoreCase("deviceid"))
        {
            metricItemValue = Settings.Secure.getString(locationawareactivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);
        }
        else if(key.equalsIgnoreCase("simserialnumber"))
        {
            metricItemValue = telephonymanager.getSimSerialNumber();
        }
        else if(key.equalsIgnoreCase("carrier"))
        {
            metricItemValue = telephonymanager.getNetworkOperatorName();
        }
        else if(key.equalsIgnoreCase("carrierVOIP"))
        {

        }
        else if(key.equalsIgnoreCase("manufacturer"))
        {
            metricItemValue = Build.MANUFACTURER;
        }
        else if(key.equalsIgnoreCase("model"))
        {
            metricItemValue = Build.MODEL;
        }
        else if(key.equalsIgnoreCase("version"))
        {
            metricItemValue = ""+Build.VERSION.SDK_INT;
        }
        else if(key.equalsIgnoreCase("osversion"))
        {
            metricItemValue = Build.VERSION.RELEASE;
        }
        else if(key.equalsIgnoreCase("devicetime"))
        {
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(config.time_format);
            String time = simpleDateFormat.format(calander.getTime());
            metricItemValue = time;
        }
        else if(key.equalsIgnoreCase("softwareversion"))
        {
            metricItemValue = ""+telephonymanager.getDeviceSoftwareVersion();
        }
        else if(key.equalsIgnoreCase("networkcountry"))
        {
            metricItemValue = ""+telephonymanager.getNetworkCountryIso();
        }else if(key.equalsIgnoreCase("deviceregion"))
        {
            metricItemValue = ""+ Locale.getDefault().getLanguage();
        }
        else if(key.equalsIgnoreCase("timezone"))
        {
            TimeZone timezone = TimeZone.getDefault();
            metricItemValue = timezone.getDisplayName();
        }else if(key.equalsIgnoreCase("devicelanguage"))
        {
            metricItemValue = Locale.getDefault().getDisplayLanguage();
        }
        else if(key.equalsIgnoreCase("brightness"))
        {
            try {
                int brightnessValue = Settings.System.getInt(locationawareactivity.this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);

                float total=(brightnessValue*100)/255;
                metricItemValue=""+(int)total+"%";
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if(key.equalsIgnoreCase("dataconnection") )
        {
            metricItemValue="Not Connected!";
            ConnectivityManager cm =
                    (ConnectivityManager)locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected)
            {
                boolean TYPE_WIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                boolean TYPE_BLUETOOTH = activeNetwork.getType() == ConnectivityManager.TYPE_BLUETOOTH;

                if(TYPE_WIFI)
                    metricItemValue="Wifi";

                if(TYPE_MOBILE)
                    metricItemValue="Mobile Data";

                if(TYPE_BLUETOOTH)
                    metricItemValue="Bluetooth";
            }

        }
        else if(key.equalsIgnoreCase("cellnetworkconnect"))
        {
            metricItemValue="NO";
            ConnectivityManager cm =
                    (ConnectivityManager)locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected)
            {
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                if(TYPE_MOBILE)
                    metricItemValue="YES";
            }

        }
        else if(key.equalsIgnoreCase("networktype") || key.equalsIgnoreCase("internalip"))
        {
            metricItemValue="N/A";
            ConnectivityManager cm =
                    (ConnectivityManager)locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected)
            {
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                boolean TYPE_WIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

                if(TYPE_MOBILE && key.equalsIgnoreCase("networktype")){
                    metricItemValue= common.mapNetworkTypeToName(locationawareactivity.this);
                }
                if(TYPE_WIFI && key.equalsIgnoreCase("networktype")){
                    metricItemValue="Wifi";
                }

                if(key.equalsIgnoreCase("internalip") && TYPE_MOBILE)
                    metricItemValue = common.getLocalIpAddress();

            }

        }
        else if(key.equalsIgnoreCase("screenwidth"))
        {
            Display display = applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            metricItemValue=""+width;
        }
        else if(key.equalsIgnoreCase("screenheight"))
        {
            Display display = applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            metricItemValue=""+height;
        }
        else if(key.equalsIgnoreCase("wificonnect") || key.equalsIgnoreCase("wifinetworksaveailable") || key.equalsIgnoreCase("wifiname")
                || key.equalsIgnoreCase("connectedwifiquality") || key.equalsIgnoreCase("externalip"))
        {
            metricItemValue = "NO";
            ConnectivityManager connManager = (ConnectivityManager) locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                final WifiManager wifiManager = (WifiManager)locationawareactivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {

                    if(key.equalsIgnoreCase("wifinetworksaveailable") || key.equalsIgnoreCase("wificonnect"))
                        metricItemValue = "YES";

                    if(key.equalsIgnoreCase("externalip"))
                        metricItemValue =  common.getWifiIPAddress(locationawareactivity.this);

                    if(key.equalsIgnoreCase("wifiname"))
                        metricItemValue = connectionInfo.getSSID();

                    if(key.equalsIgnoreCase("connectedwifiquality"))
                    {
                        int rssi=connectionInfo.getRssi();
                        if(rssi >= -50)
                        {
                            metricItemValue="Excellent";
                        }
                        else if(rssi <= -50 && rssi >= -60)
                        {
                            metricItemValue="Good";
                        }
                        else if(rssi <= -60 && rssi > -70)
                        {
                            metricItemValue="Fair";
                        }
                        else if(rssi <= -70)
                        {
                            metricItemValue="Weak";
                        }
                    }
                }
            }
            else
            {
                metricItemValue="NO";
            }
            return metricItemValue;
        }
        else if(key.equalsIgnoreCase("bluetoothonoff"))
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                metricItemValue= ("Not supported");
            }else {
                if (bluetoothAdapter.isEnabled()) {
                    metricItemValue = ("ON");
                } else {
                    metricItemValue = ("OFF");
                }
            }
            return metricItemValue;
        }
        else if(key.equalsIgnoreCase("battery"))
        {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = locationawareactivity.this.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            float batteryPct = level / (float) scale;

            int percentage= (int) (batteryPct * 100);
            metricItemValue=""+percentage+"%";
        }
        else if(key.equalsIgnoreCase("gpslatitude")|| key.equalsIgnoreCase("gpslongitude") || key.equalsIgnoreCase("gpsaltittude") ||
                key.equalsIgnoreCase("gpsverticalaccuracy")
                || key.equalsIgnoreCase("gpsquality") || key.equalsIgnoreCase("heading")
                || key.equalsIgnoreCase("speed") || key.equalsIgnoreCase("gpsaccuracy"))
        {
            metricItemValue="";
        }
        else if(key.equalsIgnoreCase("totalspace"))
        {
            String totalinternalsize = null;

            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long totalinternalmemory = totalBlocks * blockSize;

            metricItemValue  = common.getInternalMemory(totalinternalmemory);

        }else if(key.equalsIgnoreCase("usedspace"))
        {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            long totalinternalmemory = totalBlocks * blockSize;
            long  availablefreesapce =  availableBlocks * blockSize;

            long usedSize = totalinternalmemory - availablefreesapce;

            metricItemValue  = common.getInternalMemory(usedSize);

        }else if(key.equalsIgnoreCase("freespace"))
        {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            long availablefreesapce =  availableBlocks * blockSize;

            metricItemValue  = common.getInternalMemory(availablefreesapce);

        }else if(key.equalsIgnoreCase("rammemory"))
        {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) locationawareactivity.this.getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamMemorySize = mi.totalMem;


            metricItemValue  = common.getInternalMemory(totalRamMemorySize);

        }else if(key.equalsIgnoreCase("usedram"))
        {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager)  locationawareactivity.this.getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamFreeSize = mi.availMem;
            long totalRamMemorySize = mi.totalMem;
            long usedrammemorysize = totalRamMemorySize - totalRamFreeSize;

            metricItemValue  = common.getInternalMemory(usedrammemorysize);
        }else if(key.equalsIgnoreCase("freeram"))
        {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) locationawareactivity.this.getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamFreeSize = mi.availMem;

            metricItemValue  = common.getInternalMemory(totalRamFreeSize);
        }else if(key.equalsIgnoreCase("devicecurrency"))
        {
            Locale defaultLocale = Locale.getDefault();
            metricItemValue  = common.displayCurrencyInfoForLocale(defaultLocale);
        }else if(key.equalsIgnoreCase("systemuptime"))
        {
            // Get the whole uptime
            metricItemValue = common.getSystemUptime();

        }else if(key.equalsIgnoreCase("pluggedin"))
        {
            metricItemValue = common.isChargerConnected(locationawareactivity.this)== true ? "True" : "False";
        }else if(key.equalsIgnoreCase("headphonesattached"))
        {
            metricItemValue = common.isHeadsetOn(locationawareactivity.this)== true ? "True" : "False";
        }else if(key.equalsIgnoreCase("deviceorientation"))
        {
            metricItemValue = common.getOriantation(locationawareactivity.this);
        }
        else if(key.equalsIgnoreCase("isaccelerometeravailable")
                || key.equalsIgnoreCase("seisometer") || key.equalsIgnoreCase("proximitySensorEnabled")
                || key.equalsIgnoreCase("lightSensorEnabled") || key.equalsIgnoreCase("gravitysensorenabled") ||
                key.equalsIgnoreCase("gyroscopeSensorEnabled"))
        {
            metricItemValue="NO";
            SensorManager sensorManager = (SensorManager) applicationviavideocomposer.getactivity(). getSystemService(Context.SENSOR_SERVICE);
            if(key.equalsIgnoreCase("isaccelerometeravailable") ||
                    key.equalsIgnoreCase("seisometer"))
            {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    metricItemValue="YES";
                }

                if(key.equalsIgnoreCase("isaccelerometeravailable"))
                {
                    // metricItemValue="UpdateLater";
                    //getHelper().registerAccelerometerSensor();
                }
            }

            if(key.equalsIgnoreCase("proximitySensorEnabled"))
            {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
                    metricItemValue="YES";
                }
            }

            if(key.equalsIgnoreCase("lightSensorEnabled"))
            {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                    metricItemValue="YES";
                }
            }

            if(key.equalsIgnoreCase("gravitySensorEnabled"))
            {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
                    metricItemValue="YES";
                }
            }

            if(key.equalsIgnoreCase("gyroscopeSensorEnabled"))
            {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                    metricItemValue="YES";
                }
            }

        }
        else if(key.equalsIgnoreCase("processorcount") || key.equalsIgnoreCase("activeprocessorcount"))
        {
            if(Build.VERSION.SDK_INT >= 17) {
                int processors= Runtime.getRuntime().availableProcessors();
                metricItemValue=""+processors;
            }
        }
        else if(key.equalsIgnoreCase("usbconnecteddevicename"))
        {


        }else if(key.equalsIgnoreCase("accessoriesattached"))
        {
            if(common.isChargerConnected(locationawareactivity.this)== true || common.isHeadsetOn(locationawareactivity.this)== true ){
                metricItemValue = "true";
            }else{
                metricItemValue = "False";
            }
        }else if(key.equalsIgnoreCase("attachedaccessoriescount"))
        {
            if(common.isChargerConnected(locationawareactivity.this)== true && common.isHeadsetOn(locationawareactivity.this)== true ){
                metricItemValue = "2";
            }else if(common.isChargerConnected(locationawareactivity.this)== true || common.isHeadsetOn(locationawareactivity.this)== true ){
                metricItemValue = "1";
            }else{
                metricItemValue = "NA";
            }

        }else if(key.equalsIgnoreCase("nameattachedaccessories"))
        {
            if(common.isChargerConnected(locationawareactivity.this)== true && common.isHeadsetOn(locationawareactivity.this)== true ){

                metricItemValue = ("Charger"+","+"headphone");

            }else if(common.isChargerConnected(locationawareactivity.this)== true){
                metricItemValue = "Charger";
            }else if(common.isHeadsetOn(locationawareactivity.this)== true){
                metricItemValue = "headphone";
            }else{
                metricItemValue = "NA";
            }
        }
        else if(key.equalsIgnoreCase("multitaskingenabled"))
        {
            metricItemValue="True";

        }
        else if(key.equalsIgnoreCase("debuggerattached"))
        {
            metricItemValue="False";
            if(Debug.isDebuggerConnected())
                metricItemValue="True";

        }
        else if(key.equalsIgnoreCase("currentcallvolume"))
        {
            try {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int STREAM_VOICE_CALL = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                metricItemValue=""+STREAM_VOICE_CALL;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        else if(key.equalsIgnoreCase("phonetime")){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
            String time = sdf.format(c.getTime());
            metricItemValue=time;
        }
        else if(key.equalsIgnoreCase(config.airplanemode)){
            getairplanemodeon();
          /*  return false;*/
        }
        else if(key.equalsIgnoreCase("gpsonoff")){
            //  LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE );
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
                metricItemValue="OFF";
            }
            else{
                metricItemValue="ON";
            }
        }else if(key.equalsIgnoreCase("syncphonetime")){
            if(android.provider.Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0)==1) {
                metricItemValue = "ON";
            } else if((android.provider.Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0)==1)){
                metricItemValue="OFF";
            }
        }else if(key.equalsIgnoreCase("country")) {
            String locale = this.getResources().getConfiguration().locale.getCountry();
            metricItemValue = locale;
        }else if(key.equalsIgnoreCase("connectionspeed")){
            String linkSpeed = null;
            final WifiManager wifiManager = (WifiManager)locationawareactivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {

                 linkSpeed = String.valueOf(wifiInfo.getLinkSpeed()) +""+wifiInfo.LINK_SPEED_UNITS; //measured using WifiInfo.LINK_SPEED_UNITS
                 Log.e("linkspeed",wifiInfo.LINK_SPEED_UNITS);
            }
            metricItemValue=String.valueOf(linkSpeed);
        }else if(key.equalsIgnoreCase("address")){
            metricItemValue="";
        }

        if(metricItemValue == null)
            metricItemValue="";

        return metricItemValue;
    }

    private void start() {

        //Log.i("noise", "==== start ===");

        try {

            if(mNoise != null)
                mNoise.stop();

            mNoise = new noise();

            if(mNoise != null)
            {
                if(mNoise != null)
                    mNoise.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(){
            public void run(){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Do something after 100ms

                try {
                    if(mNoise != null)
                    {
                        double amp = mNoise.getAmplitude();
                        //Log.i("noise", "runnable mPollTask");
                        updateDisplay("Monitoring Voice...", amp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        thread.start();
    }
    private void stop() {
        Log.e("noise", "==== Stop noise Monitoring===");
        try {
            if(mNoise != null)
            {
                mNoise.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateDisplay(String status, double signalEMA) {

        Log.e("signalEMA = ", ""+ signalEMA);

        final String deciblevalue = String.valueOf(new DecimalFormat("##.####").format(signalEMA));

        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatearrayitem(config.decibel,deciblevalue);
                updatearrayitem(config.currentcalldecibel,deciblevalue);
                stop();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

            try {
                unregisterReceiver(mBroadcast);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            if(flightmodebroadcast!=null){
                unregisterReceiver(flightmodebroadcast);
            }

            if(mSensorManager != null)
                mSensorManager.unregisterListener(mAccelerometerListener);

            if(mSensorManager != null)
                mSensorManager.unregisterListener(mBarometerListener);

            if (mSensorManager != null)
                mSensorManager.unregisterListener(mCompassListener);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void registerAccelerometerSensor() {
        Thread thread = new Thread(){
            public void run(){
                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    mAccelereometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    mSensorManager.registerListener(mAccelerometerListener, mAccelereometer, SensorManager.SENSOR_DELAY_NORMAL);

                }
            }
        };
        thread.start();
    }

    SensorEventListener mAccelerometerListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent sensorEvent) {
            float lux = sensorEvent.values[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(getcurrentfragment() != null)
                    {
                        float deltaX = Math.abs(sensorEvent.values[0]);
                        float deltaY = Math.abs(sensorEvent.values[1]);
                        float deltaZ = Math.abs(sensorEvent.values[2]);

                        updatearrayitem(config.acceleration_x,""+deltaX);
                        updatearrayitem(config.acceleration_y,""+deltaY);
                        updatearrayitem(config.acceleration_z,""+deltaZ);
                    }
                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void registerBarometerSensor() {

        Thread thread = new Thread(){
            public void run(){

                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {

                    Sensor pS = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                    mSensorManager.registerListener(mBarometerListener, pS, SensorManager.SENSOR_DELAY_UI);
                }
            }
        };
        thread.start();
    }


    SensorEventListener mBarometerListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent sensorEvent) {
            float lux = sensorEvent.values[0];
            float[] values = sensorEvent.values;
            final String data=String.format("%3f",values[0]);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(getcurrentfragment() != null)
                        updatearrayitem(config.barometer,data);

                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void registerUsageUser() {

        Thread thread = new Thread(){
            public void run(){
                String system = common.executeTop();
                String[] cpuArray = system.split(",");
                final String[] value1 = {cpuArray[0]};
                final String[] value2 = {cpuArray[1]};
                final String[] value3 = {cpuArray[2]};
                final String[] value4 = {cpuArray[3]};


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getcurrentfragment() != null)
                        {
                            value1[0] = value1[0].replace("User","");
                            updatearrayitem(config.cpuusageuser,value1[0]);

                            value2[0] = value2[0].replace("System","");
                            updatearrayitem(config.cpuusagesystem,value2[0]);

                            value3[0] = value3[0].replace("IOW","");
                            updatearrayitem(config.cpuusageiow,value3[0]);

                            value4[0] = value4[0].replace("IRQ","");
                            updatearrayitem(config.cpuusageirq,value4[0]);
                        }
                    }
                });

            }
        };
        thread.start();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            mSignalStrength = signalStrength.getGsmSignalStrength();

            String result="";

            if (mSignalStrength <= 2 || mSignalStrength == 99)
                result = "Unknown";
            else if (mSignalStrength >= 12) {
                result = "Excellent";
            }
            else if (mSignalStrength >= 8)  {
                result = "Good";
            }
            else if (mSignalStrength >= 5)  {
                result = "Moderate";
            }
            else {
                result = "Poor";
            }

            if(getcurrentfragment() != null)
                updatearrayitem(config.connectedphonenetworkquality,result);
        }
    }

    @Override
    public void registerMobileNetworkStrength() {
        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhoneStatelistener = new MyPhoneStateListener();
                mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        });
    }

    @Override
    public void getairplanemodeon() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(aeroplacemodefilter == null)
                {
                    aeroplacemodefilter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    aeroplacemodefilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    registerReceiver(flightmodebroadcast,aeroplacemodefilter);
                }
            }
        });
    }

    @Override
    public void registerCompassSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Thread thread = new Thread(){
            public void run(){
                Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                mSensorManager.registerListener(mCompassListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(mCompassListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        };
        thread.start();
        //SensorManager snsMgr = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
    }

    SensorEventListener mCompassListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getcurrentfragment() != null)
                    {

                        int type = event.sensor.getType();
                        float[] data;
                        if (type == Sensor.TYPE_ACCELEROMETER) {
                            data = mGData;
                        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                            data = mMData;
                        } else {
                            // we should not be here.
                            return;
                        }
                        for (int i=0 ; i<3 ; i++)
                            data[i] = event.values[i];
                        SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
                        SensorManager.getOrientation(mR, mOrientation);
                        float incl = SensorManager.getInclination(mI);
                        if (mCount++ > 50) {
                            final float rad2deg = (float)(180.0f/Math.PI);
                            mCount = 0;
                            Log.d("Compass", "yaw: " + (int)(mOrientation[0]*rad2deg) +
                                    "  pitch: " + (int)(mOrientation[1]*rad2deg) +
                                    "  roll: " + (int)(mOrientation[2]*rad2deg) +
                                    "  incl: " + (int)(incl*rad2deg)
                            );

                            float azimuthInRadians = mOrientation[0];
                            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                            mCurrentDegree = -azimuthInDegress;

                            int degree = (int) mCurrentDegree;
                            degree=Math.abs(degree);
                            String compassValue  = "Northbound";

                            if (degree == 0 && degree < 45 || degree >= 315
                                    && degree == 360)
                            {
                                compassValue = "Northbound";
                            }
                            if (degree >= 45 && degree < 90)
                            {
                                compassValue = "NorthEastbound";
                            }
                            if (degree >= 90 && degree < 135)
                            {
                                compassValue = "Eastbound";
                            }
                            if (degree >= 135 && degree < 180)
                            {
                                compassValue = "SouthEastbound";
                            }
                            if (degree >= 180 && degree < 225)
                            {
                                compassValue = "SouthWestbound";
                            }
                            if (degree >= 225 && degree < 270)
                            {
                                compassValue = "Westbound";
                            }
                            if (degree >= 270 && degree < 315)
                            {
                                compassValue = "NorthWestbound";
                            }
                            updatearrayitem(config.compass,compassValue);

                        }
                    }
                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void getCallInfo() {
        try
        {
            String duration="";
            if(! CALL_START_TIME.isEmpty())
            {
                long startTime=Long.parseLong(CALL_START_TIME);

                if(startTime > 0)
                {
                    Date callEndTime = new Date();
                    long diff = callEndTime.getTime() - startTime;

                    long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                    //long diffSeconds = diff / 1000 % 60;
                    duration=""+diffSeconds;
                }
                Log.e("BROADCAST CALL ","BROADCAST CALL");
            }

            xdata.getinstance().saveSetting("CALL_STATUS",(CALL_STATUS.isEmpty())?"None":CALL_STATUS);
            xdata.getinstance().saveSetting("CALL_DURATION",(duration.isEmpty())?"None":duration);
            xdata.getinstance().saveSetting("CALL_REMOTE_NUMBER",(CALL_REMOTE_NUMBER.isEmpty())?"None":CALL_REMOTE_NUMBER);

            updatearrayitem(config.currentcallinprogress,xdata.getinstance().getSetting("CALL_STATUS"));
            updatearrayitem(config.currentcalldurationseconds,xdata.getinstance().getSetting("CALL_STATUS"));
            updatearrayitem(config.currentcallremotenumber,xdata.getinstance().getSetting("CALL_STATUS"));

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        intentFilter = new IntentFilter(config.broadcast_call);
        mBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                try
                {
                    if(intent != null)
                    {
                        CALL_STATUS=intent.getStringExtra("CALL_STATUS");
                        CALL_DURATION=intent.getStringExtra("CALL_DURATION");
                        CALL_REMOTE_NUMBER=intent.getStringExtra("CALL_REMOTE_NUMBER");
                        CALL_START_TIME=intent.getStringExtra("CALL_START_TIME");
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(mBroadcast, intentFilter);

        flightmodebroadcast= new BroadcastReceiver() {
            String turn;
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent!=null){
                    if(Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0)== 0)
                    {
                        turn="OFF";
                    }
                    else
                    {
                        turn="ON";
                    }
                    updatearrayitem(config.airplanemode,turn);
                }
            }
        };
    }

    public void updatearrayitem(String key,String value) {
        for (int i = 0; i < metricItemArraylist.size(); i++) {

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(key)) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                break;
            }
        }
    }

    public void updatelocationsparams(Location location) {
        double doubleTotalDistance=0.0;
        if(oldlocation != null)
        {
            long meter=common.calculateDistance(location.getLatitude(),location.getLongitude(),
                    oldlocation.getLatitude(),oldlocation.getLongitude());
            doubleTotalDistance=doubleTotalDistance+meter;
        }
        oldlocation=location;

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + location.getLatitude());
            }
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslongitude")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + location.getLongitude());
            }

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("distancetraveled")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + ((int)doubleTotalDistance));
            }
            if(metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaccuracy")){
                metricItemArraylist.get(i).setMetricTrackValue("" +location.getAccuracy());
            }
        }

        if(! xdata.getinstance().getSetting("gpsaltittude").trim().isEmpty())
        {
            for (int i = 0; i < metricItemArraylist.size(); i++) {

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaltittude")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("gpsaltittude")));
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("heading")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + xdata.getinstance().getSetting("heading"));
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("speed")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("speed")));
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsquality")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("gpsquality")));
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsnumberofsatelites")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("gpsnumberofsatelites")));
                }
            }
        }
    }

    //get complete address
    private String getCompleteAddressString(Location location) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                for(int i= 0;i<metricItemArraylist.size();i++){
                    if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("address")){
                        metricItemArraylist.get(i).setMetricTrackValue(strAdd);
                    }
                }

                Log.e("Myaddress", strReturnedAddress.toString());
            } else {
                Log.e("My address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Myaddress", "Canont get Address!");
        }
        return strAdd;
    }

}
