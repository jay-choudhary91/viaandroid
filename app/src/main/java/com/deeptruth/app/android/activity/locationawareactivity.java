package com.deeptruth.app.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
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
import android.os.IBinder;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.mediametadatainfo;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.startmediainfo;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.services.locationservice;
import com.deeptruth.app.android.services.readmediadataservice;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.googleutils;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.noise;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class locationawareactivity extends baseactivity implements GpsStatus.Listener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_update_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_updated_time_key";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 99;

    private LocationRequest mlocationrequest;
    private Location mcurrentlocation;
    private GoogleApiClient mgoogleapiclient;
    int GPS_REQUEST_CODE = 111;
    public Bundle newsavedinstancestate = null;
    boolean updateitem = false;
    private TelephonyManager telephonymanager;
    private LocationManager manager;
    private ArrayList<metricmodel> metricitemarraylist = new ArrayList<>();

    private SensorManager msensormanager;
    private Sensor maccelereometer;
    TelephonyManager mtelephonymanager;

    private IntentFilter intentFilter;
    private BroadcastReceiver phonecallbroadcast;
    String CALL_STATUS = "", CALL_DURATION = "", CALL_REMOTE_NUMBER = "", CALL_START_TIME = "", currentaddress = "", connectionspeed = "";
    MyPhoneStateListener mPhoneStatelistener;
    int mSignalStrength = 0, dbtoxapiupdatecounter = 0;

    noise mNoise;
    private static final int PERMISSION_RECORD_AUDIO = 92;
    public static final int my_permission_read_phone_state = 90;
    private static final int request_permissions = 101;
    private Runnable doafterallpermissionsgranted;
    private Location oldlocation;
    private Handler myHandler;
    private Runnable myRunnable;
    public boolean isrecording = false;
    public boolean iscameracapture = false;

    long startTime;
    long endTime;
    long fileSize;
    OkHttpClient client = new OkHttpClient();
    // bandwidth in kbps
    private int POOR_BANDWIDTH = 150;

    List<CellInfo> towerinfolist=new ArrayList<>() ;
    private boolean isservicebound=false;
    private databasemanager mdbhelper;
    boolean updatesync = true;
    ArrayList<startmediainfo> unsyncedmediaitems=new ArrayList<>();
    int syncupdationcounter=0;
    boolean isdatasyncing=false;
    int readercallingcounter=0;
    ArrayList<video> readerarraymedialist = new ArrayList<video>();
    private BroadcastReceiver coredatabroadcastreceiver;
    Date initialdate ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationviavideocomposer.setActivity(locationawareactivity.this);
        newsavedinstancestate = savedInstanceState;
        telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        initialdate = new Date();
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
        {
            registerallbroadcast();
            getconnectionspeed();
        }
        else
        {
            startmetrices();
        }
    }

    @Override
    public void setcameracapture(boolean toggle) {
        iscameracapture=toggle;
    }

    @Override
    public boolean iscameracapture() {
        return iscameracapture;
    }

    public void getconnectionspeed() {
        if (!common.isnetworkconnected(locationawareactivity.this)) {
            connectionspeed = "NA";
            return;
        }

        Request request = new Request.Builder()
                .url("https://m.media-amazon.com/images/S/aplus-media/vc/6a9569ab-cb8e-46d9-8aea-a7022e58c74a.jpg")
                .build();

        startTime = System.currentTimeMillis();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    //  Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                InputStream input = response.body().byteStream();

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileSize = bos.size();

                } finally {
                    input.close();
                }

                endTime = System.currentTimeMillis();

                // calculate how long it took by subtracting endtime from starttime
                double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                final int kilobytePerSec = (int) Math.round(1024 / timeTakenSecs);

                if (kilobytePerSec <= POOR_BANDWIDTH) {
                    // slow connection
                }

                // get the download speed by dividing the file size by time taken to download
                double speed = fileSize / timeTakenMills;
                double speedinmb = kilobytePerSec / 1024;
                connectionspeed = speedinmb + " mbps";
                /*Log.d("Case1 ", "Time taken in secs: " + timeTakenSecs);
                Log.d("Case2 ", "kilobyte per sec: " + kilobytePerSec);
                Log.d("Case3 ", "Download Speed: " + speed);
                Log.d("Case4 ", "File size: " + fileSize);*/
            }

        });
    }

    public void getallpermissions() {
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            if (common.getphonelocationdeniedpermissions().isEmpty()) {
                // All permissions are granted

                doafterallpermissionsgranted();

                if (isMyServiceRunning(locationservice.class)) {
                    Log.e("Running ", "Yes");
                } else {
                    Log.e("Running ", "No");

                    final Intent locationService = new Intent(applicationviavideocomposer.getactivity(), locationservice.class);
                    applicationviavideocomposer.getactivity().startService(locationService);
                    isservicebound =applicationviavideocomposer.getactivity().bindService(locationService, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            } else {
                String[] array = new String[common.getphonelocationdeniedpermissions().size()];
                array = common.getphonelocationdeniedpermissions().toArray(array);
                ActivityCompat.requestPermissions(locationawareactivity.this, array, request_permissions);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) applicationviavideocomposer.getactivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            String name = className.getClassName();

            if (name.endsWith("LocationService")) {
                /*locationService = ((LocationService.LocationServiceBinder) service).getService();
                locationService.startUpdatingLocation();*/
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            /*if (className.getClassName().equals("LocationService")) {
                locationService.stopUpdatingLocation();
                locationService = null;
            }*/
        }
    };

    @Override
    public void setrecordingrunning(boolean toggle) {
        isrecording = toggle;
    }

    @Override
    public boolean getrecordingrunning() {
        return isrecording;
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
                getallpermissions();
            }
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
    }

    private void doafterallpermissionsgranted() {
        enableGPS(locationawareactivity.this);
     //   registerallbroadcast();
        preparemetricesdata();
    }

    @Override
    public void showPermissionDialog() {
        updateitem = true;
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        } else {
            enableGPS(locationawareactivity.this);
        }
    }

    public void initLocationAPIs(Bundle savedInstanceState) {
        mgoogleapiclient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        updateValuesFromBundle(savedInstanceState);

        mgoogleapiclient.connect();
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkCameraPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            if(isservicebound)
                applicationviavideocomposer.getactivity().unbindService(serviceConnection);

            try {

                try {
                    unregisterReceiver(phonecallbroadcast);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (msensormanager != null)
                    msensormanager.unregisterListener(maccelerometerlistener);

                if (msensormanager != null)
                    msensormanager.unregisterListener(mBarometerListener);

                if (msensormanager != null)
                    msensormanager.unregisterListener(mcompasslistener);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if (myHandler != null && myRunnable != null)
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
        try
        {
            if (mgoogleapiclient != null && mgoogleapiclient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mgoogleapiclient, mlocationrequest, this);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void stopLocationUpdates() {
        try
        {
            if (mgoogleapiclient != null && mgoogleapiclient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleapiclient, this);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @SuppressLint("RestrictedApi")
    protected LocationRequest createLocationRequest() {
        mlocationrequest = new LocationRequest();
        mlocationrequest.setInterval(10000);
        mlocationrequest.setFastestInterval(10000);
        mlocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mlocationrequest;
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

            // Update the value of mcurrentlocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mcurrentlocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //saving the user current location
        if (location != null) {

            googleutils.saveUserCurrentLocation(location);

            if (location.getLatitude() == 0.0)
                return;

            mcurrentlocation = location;
            if (getcurrentfragment() != null) {
                getcurrentfragment().oncurrentlocationchanged(location);
                updatelocationsparams(location);
                fetchcompleteaddress(location);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public Location getCurrentLocation() {
        return mcurrentlocation;
    }

    @SuppressLint("MissingPermission")
    public static boolean checkLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }


    public void setNavigateWithLocation() {
        if (locationawareactivity.checkLocationEnable(locationawareactivity.this)) {
            initLocationAPIs(newsavedinstancestate);
            if (getcurrentfragment() != null) {
                getcurrentfragment().getAccurateLocation();
            }
        }

    }

    public void enableGPS(final Context context) {
        if (!locationawareactivity.checkLocationEnable(context)) {
            showgpsalert(context);
        } else {
            setNavigateWithLocation();
        }
    }

    public void showgpsalert(final Context context) {

        new AlertDialog.Builder(context, R.style.customdialogtheme)
                .setTitle("GPS")
                .setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                        if (dialog != null)
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


    private void preparemetricesdata()
    {
        if(metricitemarraylist.size() == 0)
        {
            metricitemarraylist.clear();
            String[] items = common.getmetricesarray();
            for (int i = 0; i < items.length; i++)
                metricitemarraylist.add(new metricmodel(items[i], "", true));

            startmetrices();
        }
    }

    public void startmetrices() {
        if (myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            xdata.getinstance().saveSetting("gpsenabled", "1");
                            if (! locationawareactivity.checkLocationEnable(locationawareactivity.this))
                                xdata.getinstance().saveSetting("gpsenabled", "0");

                            if(getcurrentfragment() != null)
                                getcurrentfragment().updatewifigpsstatus();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
                        {
                            getconnectionspeed();
                            towerinfolist = gettowerinfo();
                            for (int i = 0; i < metricitemarraylist.size(); i++) {
                                if (metricitemarraylist.get(i).isSelected()) {
                                    String value = metric_read(metricitemarraylist.get(i).getMetricTrackKeyName());
                                    metricitemarraylist.get(i).setMetricTrackValue(metricitemarraylist.get(i).getMetricTrackValue());

                                    if (!value.trim().isEmpty())
                                        metricitemarraylist.get(i).setMetricTrackValue(value);
                                }
                            }
                            if (getcurrentfragment() instanceof videocomposerfragment) {
                                isrecording = ((videocomposerfragment) getcurrentfragment()).isvideorecording();
                                String datainsertion=xdata.getinstance().getSetting(config.ismediadataservicerunning);
                                if (isrecording || (datainsertion.equalsIgnoreCase("1")))
                                    return;
                            }
                        }

                        dbtoxapiupdatecounter++;
                        if (dbtoxapiupdatecounter > 1) {
                            dbtoxapiupdatecounter = 0;

                            if(! isdatasyncing)
                                syncmediadatabase();
                        }

                    }
                }).start();

                myHandler.postDelayed(this, 3000);
            }
        };
        myHandler.post(myRunnable);
    }


    public ArrayList<metricmodel> getmetricarraylist() {
        List<metricmodel> settingupdatedlist = getMetricList();
        if (settingupdatedlist == null || settingupdatedlist.size() == 0) {

        } else {
            for (int i = 0; i < settingupdatedlist.size(); i++) {
                if (settingupdatedlist.get(i).isSelected()) {
                    metricitemarraylist.get(i).setSelected(true);
                } else {
                    metricitemarraylist.get(i).setSelected(false);
                }
            }
        }


        return metricitemarraylist;
    }


    public List<metricmodel> getMetricList() {
        Gson gson = new Gson();
        List<metricmodel> metricList = new ArrayList<>();

        String value = xdata.getinstance().getSetting(config.metriclist);
        if (value.trim().length() > 0) {
            Type type = new TypeToken<List<metricmodel>>() {
            }.getType();
            metricList = gson.fromJson(value, type);
        }
        return metricList;
    }

    @SuppressLint("MissingPermission")
    public String metric_read(String key) {
        String metricItemValue = "";

        if (key.equalsIgnoreCase(config.decibel) || key.equalsIgnoreCase(config.currentcalldecibel)) {
            if (ContextCompat.checkSelfPermission(locationawareactivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(locationawareactivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_RECORD_AUDIO);
            } else {
                //start();
            }
            return "NA";
        } else if (key.equalsIgnoreCase("connectedphonenetworkquality")) {
            String str = telephonymanager.getNetworkOperatorName();
            if (str != null) {
                if (!str.trim().isEmpty()) {
                    registerMobileNetworkStrength();
                    return "";
                }
            }
        } else if (key.equalsIgnoreCase(config.cpuusageuser) || key.equalsIgnoreCase(config.cpuusagesystem)
                || key.equalsIgnoreCase(config.cpuusageiow) || key.equalsIgnoreCase(config.cpuusageirq)) {
            getsystemusage();
            return "";
        } else if (key.equalsIgnoreCase(config.currentcallinprogress) || key.equalsIgnoreCase(config.currentcalldurationseconds)
                || key.equalsIgnoreCase(config.currentcallremotenumber)) {
            getCallInfo();
            return "";
        } else if (key.equalsIgnoreCase("imeinumber")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                metricItemValue = telephonymanager.getImei();
            } else {
                metricItemValue = telephonymanager.getDeviceId();
            }
        } else if (key.equalsIgnoreCase("username")) {
            metricItemValue = common.getUsername();
        } else if (key.equalsIgnoreCase("deviceid")) {
            metricItemValue = Settings.Secure.getString(locationawareactivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else if (key.equalsIgnoreCase("simserialnumber")) {
            metricItemValue = telephonymanager.getSimSerialNumber();
        } else if (key.equalsIgnoreCase("carrier")) {
            metricItemValue = telephonymanager.getNetworkOperatorName();
        } else if (key.equalsIgnoreCase("carrierVOIP")) {

        } else if (key.equalsIgnoreCase("manufacturer")) {
            metricItemValue = Build.MANUFACTURER;
        } else if (key.equalsIgnoreCase("model") || key.equalsIgnoreCase("phonetype")) {
            metricItemValue = Build.MODEL;
        } else if (key.equalsIgnoreCase("version")) {
            metricItemValue = "" + Build.VERSION.SDK_INT;
        } else if (key.equalsIgnoreCase("osversion")) {
            metricItemValue = Build.VERSION.RELEASE;
        } else if (key.equalsIgnoreCase("devicetime")) {
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(config.time_format);
            String time = simpleDateFormat.format(calander.getTime());
            metricItemValue = time;
        } else if (key.equalsIgnoreCase("softwareversion")) {
            metricItemValue = "" + telephonymanager.getDeviceSoftwareVersion();
        } else if (key.equalsIgnoreCase("deviceregion") || (key.equalsIgnoreCase("country"))) {
            metricItemValue = "" + Locale.getDefault().getCountry();
        } else if (key.equalsIgnoreCase("timezone")) {
            TimeZone timezone = TimeZone.getDefault();
            metricItemValue = timezone.getDisplayName();
        } else if (key.equalsIgnoreCase("devicelanguage")) {
            metricItemValue = Locale.getDefault().getDisplayLanguage();
        } else if (key.equalsIgnoreCase("brightness")) {
            try {
                int brightnessValue = Settings.System.getInt(locationawareactivity.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);

                float total = (brightnessValue * 100) / 255;
                metricItemValue = "" + (int) total + "%";
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (key.equalsIgnoreCase("dataconnection")) {
            metricItemValue = "Not Connected!";
            ConnectivityManager cm =
                    (ConnectivityManager) locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                boolean TYPE_WIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                boolean TYPE_BLUETOOTH = activeNetwork.getType() == ConnectivityManager.TYPE_BLUETOOTH;

                if (TYPE_WIFI)
                    metricItemValue = "Wifi";

                if (TYPE_MOBILE)
                    metricItemValue = "Mobile Data";

                if (TYPE_BLUETOOTH)
                    metricItemValue = "Bluetooth";
            }

        } else if (key.equalsIgnoreCase("cellnetworkconnect")) {
            metricItemValue = "NO";
            ConnectivityManager cm =
                    (ConnectivityManager) locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                if (TYPE_MOBILE)
                    metricItemValue = "YES";
            }

        } else if (key.equalsIgnoreCase("networktype") || key.equalsIgnoreCase("internalip")) {
            metricItemValue = "NA";
            ConnectivityManager cm =
                    (ConnectivityManager) locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                boolean TYPE_WIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

                if (TYPE_MOBILE && key.equalsIgnoreCase("networktype")) {
                    metricItemValue = common.mapNetworkTypeToName(locationawareactivity.this);
                }
                if (TYPE_WIFI && key.equalsIgnoreCase("networktype")) {
                    metricItemValue = "Wifi";
                }

                if (key.equalsIgnoreCase("internalip") && TYPE_MOBILE)
                    metricItemValue = common.getLocalIpAddress();

            }

        } else if (key.equalsIgnoreCase("screenwidth")) {
            Display display = applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            metricItemValue = "" + width;
        } else if (key.equalsIgnoreCase("screenheight")) {
            Display display = applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            metricItemValue = "" + height;
        } else if (key.equalsIgnoreCase("wificonnect") || key.equalsIgnoreCase(config.wifinetworkavailable) || key.equalsIgnoreCase("wifiname")
                || key.equalsIgnoreCase("connectedwifiquality") || key.equalsIgnoreCase("externalip")) {
            metricItemValue = "NO";
            ConnectivityManager connManager = (ConnectivityManager) locationawareactivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                final WifiManager wifiManager = (WifiManager) locationawareactivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {

                    if (key.equalsIgnoreCase(config.wifinetworkavailable) || key.equalsIgnoreCase("wificonnect"))
                        metricItemValue = "true";

                    if (key.equalsIgnoreCase("externalip"))
                        metricItemValue = common.getWifiIPAddress(locationawareactivity.this);

                    if (key.equalsIgnoreCase("wifiname"))
                    {
                        metricItemValue = connectionInfo.getSSID();
                        xdata.getinstance().saveSetting("wificonnected", "1");
                    }

                    metricItemValue=metricItemValue.replace("\"", "");
                    if (key.equalsIgnoreCase("connectedwifiquality")) {
                        int rssi = connectionInfo.getRssi();
                        if (rssi >= -50) {
                            metricItemValue = "Excellent";
                        } else if (rssi <= -50 && rssi >= -60) {
                            metricItemValue = "Good";
                        } else if (rssi <= -60 && rssi > -70) {
                            metricItemValue = "Fair";
                        } else if (rssi <= -70) {
                            metricItemValue = "Weak";
                        }
                    }
                }
            } else {
                metricItemValue = "NO";
                xdata.getinstance().saveSetting("wificonnected", "0");
            }
            return metricItemValue;
        } else if (key.equalsIgnoreCase("bluetoothonoff")) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                metricItemValue = ("Not supported");
            } else {
                if (bluetoothAdapter.isEnabled()) {
                    metricItemValue = ("ON");
                } else {
                    metricItemValue = ("OFF");
                }
            }
            return metricItemValue;
        } else if (key.equalsIgnoreCase("battery")) {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = locationawareactivity.this.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            float batteryPct = level / (float) scale;

            int percentage = (int) (batteryPct * 100);
            metricItemValue = "" + percentage + "%";
        } else if (key.equalsIgnoreCase("gpslatitude") || key.equalsIgnoreCase("gpslongitude") || key.equalsIgnoreCase(config.gpsaltitude) ||
                key.equalsIgnoreCase("gpsverticalaccuracy")
                || key.equalsIgnoreCase("gpsquality") || key.equalsIgnoreCase("heading")
                || key.equalsIgnoreCase("speed") || key.equalsIgnoreCase("gpsaccuracy")) {
            metricItemValue = "";
        } else if (key.equalsIgnoreCase("totalspace")) {
            String totalinternalsize = null;

            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long totalinternalmemory = totalBlocks * blockSize;

            metricItemValue = common.getInternalMemory(totalinternalmemory);

        } else if (key.equalsIgnoreCase("usedspace")) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            long totalinternalmemory = totalBlocks * blockSize;
            long availablefreesapce = availableBlocks * blockSize;

            long usedSize = totalinternalmemory - availablefreesapce;

            metricItemValue = common.getInternalMemory(usedSize);

        } else if (key.equalsIgnoreCase("freespace")) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            long availablefreesapce = availableBlocks * blockSize;

            metricItemValue = common.getInternalMemory(availablefreesapce);

        } else if (key.equalsIgnoreCase("rammemory")) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) locationawareactivity.this.getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamMemorySize = mi.totalMem;


            metricItemValue = common.getInternalMemory(totalRamMemorySize);

        } else if (key.equalsIgnoreCase("usedram") || key.equalsIgnoreCase("memoryusage")) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) locationawareactivity.this.getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamFreeSize = mi.availMem;
            long totalRamMemorySize = mi.totalMem;
            long usedrammemorysize = totalRamMemorySize - totalRamFreeSize;

            if (key.equalsIgnoreCase("usedram")) {
                metricItemValue = common.getInternalMemory(usedrammemorysize);
            } else {
                long usedinpercentage = (100 * usedrammemorysize) / totalRamMemorySize;
                metricItemValue = "" + usedinpercentage + "%";
            }

        } else if (key.equalsIgnoreCase("freeram")) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) locationawareactivity.this.getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamFreeSize = mi.availMem;

            metricItemValue = common.getInternalMemory(totalRamFreeSize);
        } else if (key.equalsIgnoreCase("devicecurrency")) {
            Locale defaultLocale = Locale.getDefault();
            metricItemValue = common.displayCurrencyInfoForLocale(defaultLocale);
        } else if (key.equalsIgnoreCase("systemuptime")) {
            // Get the whole uptime
            metricItemValue = common.getSystemUptime();
            metricItemValue = common.systemuptime(Long.parseLong(metricItemValue));

        } else if (key.equalsIgnoreCase("pluggedin")) {
            metricItemValue = common.isChargerConnected(locationawareactivity.this) == true ? "true" : "false";
        } else if (key.equalsIgnoreCase("headphonesattached")) {
            metricItemValue = common.isHeadsetOn(locationawareactivity.this) == true ? "true" : "false";
        } else if (key.equalsIgnoreCase("deviceorientation")) {
            metricItemValue = common.getOriantation(locationawareactivity.this);
        } else if (key.equalsIgnoreCase("isaccelerometeravailable")
                || key.equalsIgnoreCase("seisometer") || key.equalsIgnoreCase("proximitySensorEnabled")
                || key.equalsIgnoreCase("lightSensorEnabled") || key.equalsIgnoreCase("gravitysensorenabled") ||
                key.equalsIgnoreCase("gyroscopeSensorEnabled")) {
            metricItemValue = "NO";
            SensorManager sensorManager = (SensorManager) applicationviavideocomposer.getactivity().getSystemService(Context.SENSOR_SERVICE);
            if (key.equalsIgnoreCase("isaccelerometeravailable") ||
                    key.equalsIgnoreCase("seisometer")) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    metricItemValue = "YES";
                }
            }

            if (key.equalsIgnoreCase("proximitySensorEnabled")) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
                    metricItemValue = "YES";
                }
            }

            if (key.equalsIgnoreCase("lightSensorEnabled")) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                    metricItemValue = "YES";
                }
            }

            if (key.equalsIgnoreCase("gravitySensorEnabled")) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
                    metricItemValue = "YES";
                }
            }

            if (key.equalsIgnoreCase("gyroscopeSensorEnabled")) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                    metricItemValue = "YES";
                }
            }

        } else if (key.equalsIgnoreCase("processorcount") || key.equalsIgnoreCase("activeprocessorcount")) {
            if (Build.VERSION.SDK_INT >= 17) {
                int processors = Runtime.getRuntime().availableProcessors();
                metricItemValue = "" + processors;
            }
        } else if (key.equalsIgnoreCase("usbconnecteddevicename")) {


        } else if (key.equalsIgnoreCase("accessoriesattached")) {
            if (common.isChargerConnected(locationawareactivity.this) == true || common.isHeadsetOn(locationawareactivity.this) == true) {
                metricItemValue = "true";
            } else {
                metricItemValue = "false";
            }
        } else if (key.equalsIgnoreCase("attachedaccessoriescount")) {
            if (common.isChargerConnected(locationawareactivity.this) == true && common.isHeadsetOn(locationawareactivity.this) == true) {
                metricItemValue = "2";
            } else if (common.isChargerConnected(locationawareactivity.this) == true || common.isHeadsetOn(locationawareactivity.this) == true) {
                metricItemValue = "1";
            } else {
                metricItemValue = "NA";
            }

        } else if (key.equalsIgnoreCase("nameattachedaccessories")) {
            if (common.isChargerConnected(locationawareactivity.this) == true && common.isHeadsetOn(locationawareactivity.this) == true) {

                metricItemValue = ("Charger" + "," + "headphone");

            } else if (common.isChargerConnected(locationawareactivity.this) == true) {
                metricItemValue = "Charger";
            } else if (common.isHeadsetOn(locationawareactivity.this) == true) {
                metricItemValue = "headphone";
            } else {
                metricItemValue = "NA";
            }
        } else if (key.equalsIgnoreCase("multitaskingenabled")) {
            metricItemValue = "true";

        } else if (key.equalsIgnoreCase("debuggerattached")) {
            metricItemValue = "false";
            if (Debug.isDebuggerConnected())
                metricItemValue = "true";

        } else if (key.equalsIgnoreCase("currentcallvolume")) {
            try {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int STREAM_VOICE_CALL = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                metricItemValue = "" + STREAM_VOICE_CALL;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (key.equalsIgnoreCase("devicetime")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
            String time = sdf.format(c.getTime());
            metricItemValue = time;
        } else if (key.equalsIgnoreCase("gpsonoff")) {
            //  LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE );
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                metricItemValue = "OFF";
            } else {
                metricItemValue = "ON";
            }
        } else if (key.equalsIgnoreCase("syncphonetime")) {
            if (android.provider.Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1) {
                metricItemValue = "ON";
            } else if ((android.provider.Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1)) {
                metricItemValue = "OFF";
            }
        } else if (key.equalsIgnoreCase("connectionspeed")) {
            metricItemValue = "" + connectionspeed;
        } else if (key.equalsIgnoreCase("address")) {
            metricItemValue = "" + currentaddress;
        }
        else if (key.equalsIgnoreCase("celltowersignalstrength") || key.equalsIgnoreCase("celltowerid")) {

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();

            if(towerinfolist != null && towerinfolist.size() > 0){
                CellInfo info = towerinfolist.get(0);
                if (telephonyManager.getSimState() == telephonyManager.SIM_STATE_ABSENT) {
                    metricItemValue = "";
                } else {
                    if(key.equalsIgnoreCase("celltowersignalstrength")){

                        if(info != null){
                            try{
                                if(TelephonyManager.NETWORK_TYPE_LTE == networkType){
                                    CellSignalStrengthLte gsm = ((CellInfoLte) info).getCellSignalStrength();
                                    int signalstrength =  gsm.getDbm();
                                    metricItemValue = "" + signalstrength + " " + "dBm";
                                }else{

                                    CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                                    int signalstrength =  gsm.getDbm();
                                    metricItemValue = "" + signalstrength + " " + "dBm";
                                }


                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                    }else if(key.equalsIgnoreCase("celltowerid")){
                        if(info != null) {

                            try{
                                if(TelephonyManager.NETWORK_TYPE_LTE == networkType){
                                    CellIdentityLte identity = ((CellInfoLte) info).getCellIdentity();
                                    int celltowerid = identity.getCi();
                                    metricItemValue = "" + celltowerid;
                                }else{
                                    CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                                    int celltowerid = identityGsm.getCid();
                                    metricItemValue = "" + celltowerid;
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        else if (key.equalsIgnoreCase("numberoftowers")) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getSimState() == telephonyManager.SIM_STATE_ABSENT) {
                metricItemValue = "" ;
            }
            else
            {
                metricItemValue = "0";
                if(towerinfolist != null)
                    metricItemValue = "" + towerinfolist.size();
            }
        } else if (key.equalsIgnoreCase("connectionspeed")) {
            metricItemValue = "" + connectionspeed;
        } else if (key.equalsIgnoreCase("address")) {
            metricItemValue = "" + currentaddress;
        } else if (key.equalsIgnoreCase("numberofsatellites")) {
            // metricItemValue = "" + numberofsatellites;
            metricItemValue = xdata.getinstance().getSetting("gpsnumberofsatelites");
        } else if (key.equalsIgnoreCase("satelliteangle")) {
            //metricItemValue = "" + anglesatellite;
            metricItemValue = xdata.getinstance().getSetting("satelliteangle");
        } else if (key.equalsIgnoreCase("satelliteid")) {
            //metricItemValue = "" + satelliteid;
            metricItemValue = xdata.getinstance().getSetting("satelliteid");
        } else if (key.equalsIgnoreCase("strengthofsatellites")) {
            metricItemValue =xdata.getinstance().getSetting("strengthofsatellites");;
        }
        else if (key.equalsIgnoreCase(config.airplanemode)) {
            metricItemValue = "ON";
            if(Settings.Global.getInt(locationawareactivity.this.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON, 0) == 0)
            {
                metricItemValue = "OFF";
            }
        }

        if (metricItemValue == null)
            metricItemValue = "";

        return metricItemValue;
    }

    private void start() {

        //Log.i("noise", "==== start ===");

        try {

            if (mNoise != null)
                mNoise.stop();

            mNoise = new noise();

            if (mNoise != null) {
                if (mNoise != null)
                    mNoise.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread thread = new Thread() {
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Do something after 100ms

                try {
                    if (mNoise != null) {
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
        try {
            if (mNoise != null) {
                mNoise.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateDisplay(String status, double signalEMA) {
        final String deciblevalue = String.valueOf(new DecimalFormat("##.####").format(signalEMA));
        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatearrayitem(config.decibel, deciblevalue);
                updatearrayitem(config.currentcalldecibel, deciblevalue);
                stop();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
            getallpermissions();
    }

    @Override
    public void registerAccelerometerSensor() {
        Thread thread = new Thread() {
            public void run() {
                msensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (msensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    maccelereometer = msensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    msensormanager.registerListener(maccelerometerlistener, maccelereometer, SensorManager.SENSOR_DELAY_NORMAL);

                }
            }
        };
        thread.start();
    }

    SensorEventListener maccelerometerlistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent sensorEvent) {
            float lux = sensorEvent.values[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getcurrentfragment() != null) {
                        float deltaX = Math.abs(sensorEvent.values[0]);
                        float deltaY = Math.abs(sensorEvent.values[1]);
                        float deltaZ = Math.abs(sensorEvent.values[2]);
                        Math.sin(deltaX);

                        String x = String.valueOf(new DecimalFormat("#.#").format(deltaX));
                        String y = String.valueOf(new DecimalFormat("#.#").format(deltaY));
                        String z = String.valueOf(new DecimalFormat("#.#").format(deltaZ));

                        updatearrayitem(config.acceleration_x, "" + x);
                        updatearrayitem(config.acceleration_y, "" + y);
                        updatearrayitem(config.acceleration_z, "" + z);
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

        Thread thread = new Thread() {
            public void run() {

                msensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                if (msensormanager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {

                    Sensor pS = msensormanager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                    msensormanager.registerListener(mBarometerListener, pS, SensorManager.SENSOR_DELAY_UI);
                }
            }
        };
        thread.start();
    }


    SensorEventListener mBarometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent sensorEvent) {
            float lux = sensorEvent.values[0];
            float[] values = sensorEvent.values;
            final String data = String.format("%3f", values[0]);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getcurrentfragment() != null)
                        updatearrayitem(config.barometer, data);

                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void getsystemusage() {

        Thread thread = new Thread() {
            public void run() {
                try {
                    String system = common.executeTop();
                    if(system.contains("Tasks")){
                        system=system.substring(system.indexOf("Tasks"),system.length());
                        system=  system.replace("Tasks:","");
                    }
                    String[] cpuArray = system.split(",");
                    final String[] value1 = {cpuArray[0]};
                    final String[] value2 = {cpuArray[1]};
                    final String[] value3 = {cpuArray[2]};
                    final String[] value4 = {cpuArray[3]};


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getcurrentfragment() != null) {
                                value1[0] = value1[0].replace("User", "");
                                updatearrayitem(config.cpuusageuser, value1[0]);

                                value2[0] = value2[0].replace("System", "");
                                updatearrayitem(config.cpuusagesystem, value2[0]);

                                value3[0] = value3[0].replace("IOW", "");
                                updatearrayitem(config.cpuusageiow, value3[0]);

                                value4[0] = value4[0].replace("IRQ", "");
                                updatearrayitem(config.cpuusageirq, value4[0]);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            mSignalStrength = signalStrength.getGsmSignalStrength();

            String result = "";

            if (mSignalStrength <= 2 || mSignalStrength == 99)
                result = "Unknown";
            else if (mSignalStrength >= 12) {
                result = "Excellent";
            } else if (mSignalStrength >= 8) {
                result = "Good";
            } else if (mSignalStrength >= 5) {
                result = "Moderate";
            } else {
                result = "Poor";
            }

            if (getcurrentfragment() != null)
                updatearrayitem(config.connectedphonenetworkquality, result);
        }
    }

    @Override
    public void registerMobileNetworkStrength() {
        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhoneStatelistener = new MyPhoneStateListener();
                mtelephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                mtelephonymanager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        });
    }

    @Override
    public void getairplanemodeon() {

    }

    @Override
    public void registerCompassSensor() {
        msensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Thread thread = new Thread() {
            public void run() {
                msensormanager.registerListener(mcompasslistener, msensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                        SensorManager.SENSOR_DELAY_GAME);
            }
        };
        thread.start();
    }

    SensorEventListener mcompasslistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getcurrentfragment() != null) {
                        int heading = Math.round(event.values[0]);

                        int heading1 = Math.round(event.values[0]);
                        int heading2 = Math.round(event.values[1]);
                        int heading3 = Math.round(event.values[2]);
                       // Log.e("Heading ",""+heading);

                        String strdirection = "East";
                        if (heading > 23 && heading <= 67) {
                            strdirection = "North East";
                        } else if (heading > 68 && heading <= 112) {
                            strdirection = "East";
                        } else if (heading > 113 && heading <= 167) {
                            strdirection = "South East";
                        } else if (heading > 168 && heading <= 202) {
                            strdirection = "South";
                        } else if (heading > 203 && heading <= 247) {
                            strdirection = "South West";
                        } else if (heading > 248 && heading <= 293) {
                            strdirection = "West";
                        } else if (heading > 294 && heading <= 337) {
                            strdirection = "North West";
                        } else if (heading >= 338 || heading <= 22) {
                            strdirection = "North";
                        }

                        updatearrayitem(config.compass, strdirection);
                        updatearrayitem(config.orientation, "" + heading);
                        updatearrayitem(config.heading, "" + heading);
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
        try {
            String duration = "";
            if (!CALL_START_TIME.isEmpty()) {
                long startTime = Long.parseLong(CALL_START_TIME);

                if (startTime > 0) {
                    Date callEndTime = new Date();
                    long diff = callEndTime.getTime() - startTime;

                    long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                    //long diffSeconds = diff / 1000 % 60;
                    duration = "" + diffSeconds;
                }
            }

            xdata.getinstance().saveSetting("CALL_STATUS", (CALL_STATUS.isEmpty()) ? "None" : CALL_STATUS);
            xdata.getinstance().saveSetting("CALL_DURATION", (duration.isEmpty()) ? "None" : duration);
            xdata.getinstance().saveSetting("CALL_REMOTE_NUMBER", (CALL_REMOTE_NUMBER.isEmpty()) ? "None" : CALL_REMOTE_NUMBER);

            updatearrayitem(config.currentcallinprogress, xdata.getinstance().getSetting("CALL_STATUS"));
            updatearrayitem(config.currentcalldurationseconds, xdata.getinstance().getSetting("CALL_STATUS"));
            updatearrayitem(config.currentcallremotenumber, xdata.getinstance().getSetting("CALL_STATUS"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerallbroadcast() {
        try {
            intentFilter = new IntentFilter(config.broadcast_call);
            phonecallbroadcast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        if (intent != null) {
                            CALL_STATUS = intent.getStringExtra("CALL_STATUS");
                            CALL_DURATION = intent.getStringExtra("CALL_DURATION");
                            CALL_REMOTE_NUMBER = intent.getStringExtra("CALL_REMOTE_NUMBER");
                            CALL_START_TIME = intent.getStringExtra("CALL_START_TIME");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            try
            {
                registerReceiver(phonecallbroadcast, intentFilter);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                getairplanemodeon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            registerAccelerometerSensor();

            {
                SensorManager mSensorManager = (SensorManager) locationawareactivity.this.getSystemService(Context.SENSOR_SERVICE);
                if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
                    registerCompassSensor();
            }

            {
                SensorManager mSensorManager = (SensorManager) locationawareactivity.this.getSystemService(Context.SENSOR_SERVICE);
                if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null)
                    registerBarometerSensor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatearrayitem(String key, String value) {
        for (int i = 0; i < metricitemarraylist.size(); i++) {

            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(key)) {
                metricitemarraylist.get(i).setMetricTrackValue(value);
                break;
            }
        }
    }

    public void updatelocationsparams(Location location) {


        for (int i = 0; i < metricitemarraylist.size(); i++) {
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude")) {
                metricitemarraylist.get(i).setMetricTrackValue("" + location.getLatitude());
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslongitude")) {
                metricitemarraylist.get(i).setMetricTrackValue("" + location.getLongitude());
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.gpslatitudedegree)) {
                String degree = common.convertlatitude(location.getLatitude());
                metricitemarraylist.get(i).setMetricTrackValue("" + degree);
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.gpslongitudedegree)) {
                String degree = common.convertlongitude(location.getLongitude());
                metricitemarraylist.get(i).setMetricTrackValue("" + degree);
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.distancetravelled)) {
                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting("distancetravelled"));
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaccuracy")) {
                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting("gpsaccuracy"));
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("address")) {
                metricitemarraylist.get(i).setMetricTrackValue("" + currentaddress);
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("speed")) {

                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting("speed"));
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude)) {
                String altitude=xdata.getinstance().getSetting(config.gpsaltitude);
                if(altitude.trim().isEmpty())
                    altitude="0";

                metricitemarraylist.get(i).setMetricTrackValue("" + altitude + " ft");
            }
            oldlocation = location;
        }
    }

    //get complete address
    private void fetchcompleteaddress(final Location location) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String strAdd = "";
                Geocoder geocoder = new Geocoder(locationawareactivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);

                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i));
                        }
                        currentaddress = strReturnedAddress.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public List<CellInfo> gettowerinfo() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        List<CellInfo> infos = telephonyManager.getAllCellInfo();

        return infos;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            IntentFilter intentFilter = new IntentFilter(config.reader_service_getmetadata);
            coredatabroadcastreceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        updatesyncedreaderitems();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            registerReceiver(coredatabroadcastreceiver, intentFilter);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            applicationviavideocomposer.getactivity().unregisterReceiver(coredatabroadcastreceiver);
        }
    }

    public void syncmediadatabase()
    {
        Log.e("Method ","syncmediadatabase");
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
        {
            if(getcurrentfragment() instanceof videocomposerfragment)
            {
                boolean isrecording=((videocomposerfragment) getcurrentfragment()).isvideorecording();
                if(isrecording)
                {
                    isdatasyncing=false;
                    return;
                }

                if(! common.isnetworkconnected(locationawareactivity.this))
                {
                    isdatasyncing=false;
                    return;
                }

            }

            if (mdbhelper == null) {
                mdbhelper = new databasemanager(locationawareactivity.this);
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            // Fetch data which are unsynced or sync = 0
            unsyncedmediaitems = mdbhelper.fetchunsynceddata();
            try {
                mdbhelper.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            syncupdationcounter=0;
            if(unsyncedmediaitems.size() > 0)
            {
                isdatasyncing=true;
                updateunsyncedcomposeritems();
            }
            else
            {
                isdatasyncing=false;
            }
        }
        else if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            Date currentdate=new Date();
            String initialdatereaderapi=xdata.getinstance().getSetting("initialdatereaderapi");
            long olddate=0;
            if(! initialdatereaderapi.trim().isEmpty())
                olddate=Long.parseLong(initialdatereaderapi);

            int seconddifference= (int) (Math.abs(olddate-currentdate.getTime())/1000);
            if(seconddifference > 15 || olddate == 0)
                updatesyncedreaderitems();
        }
    }

    //------------------------------------------
    //** start code of media reader sync process
    public void updatesyncedreaderitems()
    {
        File videodir = new File(config.videodir);
        if(! videodir.exists())
            return;

        databasemanager mdbhelper=null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {

            if(readerarraymedialist.size() > 0)
            {
                if(readerarraymedialist.get(readerarraymedialist.size()-1).isIscheck())
                    readerarraymedialist.clear();
            }
            if(readerarraymedialist.size() == 0)
            {
                Cursor cursor = mdbhelper.getallmediastartdata();
                if(cursor != null && cursor.getCount() > 0)
                {
                    if(cursor.moveToFirst())
                    {
                        do{
                            String location = "" + cursor.getString(cursor.getColumnIndex("location"));
                            String type = "" + cursor.getString(cursor.getColumnIndex("type"));
                            String status = "" + cursor.getString(cursor.getColumnIndex("status"));

                            video videoobj = new video();
                            videoobj.setPath(config.videodir+File.separator+location);
                            videoobj.setmimetype(type);
                            videoobj.setMediastatus(status);
                            if(! status.equalsIgnoreCase(config.sync_complete) && (! status.equalsIgnoreCase(config.sync_notfound)))
                                readerarraymedialist.add(videoobj);

                        }while(cursor.moveToNext());
                    }
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if (readerarraymedialist != null && readerarraymedialist.size() > 0) {
            for (int i = 0; i < readerarraymedialist.size(); i++) {
                String status = readerarraymedialist.get(i).getMediastatus();
                if (! readerarraymedialist.get(i).isIscheck())
                {
                    readerarraymedialist.get(i).setIscheck(true);
                    if (!common.isnetworkconnected(applicationviavideocomposer.getappcontext()))
                        readerarraymedialist.get(i).setMediastatus(config.sync_offline);

                    if (status.equalsIgnoreCase(config.sync_inprogress) || status.equalsIgnoreCase(config.sync_pending))
                    {
                        readerarraymedialist.get(i).setMediastatus(config.sync_inprogress);
                        //initialdate = new Date();
                        callreadersyncservice(readerarraymedialist.get(i).getPath(),readerarraymedialist.get(i).getmimetype());
                        break;
                    }
                }
            }
        }
    }

    public void callreadersyncservice(String mediafilepath,String mimetype)
    {
        String keytype = common.checkkey();
        String firsthash="";

        if(mimetype.startsWith("image/"))
            firsthash= md5.fileToMD5(mediafilepath);

        Intent intent = new Intent(applicationviavideocomposer.getactivity(), readmediadataservice.class);
        intent.putExtra("firsthash", firsthash);
        intent.putExtra("mediapath", mediafilepath);
        intent.putExtra("keytype", keytype);
        if(mimetype.startsWith("video"))
        {
            intent.putExtra("mediatype","video");
        }
        else if(mimetype.startsWith("audio"))
        {
            intent.putExtra("mediatype","audio");
        }
        else if(mimetype.startsWith("image"))
        {
            intent.putExtra("mediatype","image");
        }
        initialdate=new Date();
        xdata.getinstance().saveSetting("initialdatereaderapi",""+initialdate.getTime());
        applicationviavideocomposer.getactivity().startService(intent);
    }
    //** end code of media reader sync process
    //--------------------------------------------------


    //** start code of media composer sync process
    public void updateunsyncedcomposeritems()
    {
        String hashmethod="",hashvalue="",mediatitle="",selectedid="", header = "", type = "",localkey = "", token = "", mediakey = "",
                sync = "",sync_date = "",action_type="",apirequestdevicedate = "",videostartdevicedate= "",devicetimeoffset = "",
                videocompletedevicedate = "";

        String synchdefaultversion = "1", synchstatus = config.sync_inprogress, synchcompletedate = "", synchlastsequence = "";

        HashMap<String,Object> mpairslist=new HashMap<String, Object>();

        if(unsyncedmediaitems != null && unsyncedmediaitems.size() > 0 && syncupdationcounter < unsyncedmediaitems.size())
        {
            selectedid= unsyncedmediaitems.get(syncupdationcounter).getId().toString();
            header = unsyncedmediaitems.get(syncupdationcounter).getHeader().toString();
            type = unsyncedmediaitems.get(syncupdationcounter).getType().toString();
            localkey = unsyncedmediaitems.get(syncupdationcounter).getLocalkey().toString();
            token = unsyncedmediaitems.get(syncupdationcounter).getToken().toString();
            mediakey = unsyncedmediaitems.get(syncupdationcounter).getVideokey().toString();
            sync = unsyncedmediaitems.get(syncupdationcounter).getSync().toString();
            action_type = unsyncedmediaitems.get(syncupdationcounter).getAction_type().toString();
            sync_date = unsyncedmediaitems.get(syncupdationcounter).getSync_date().toString();
            apirequestdevicedate = unsyncedmediaitems.get(syncupdationcounter).getApirequestdevicedate().toString();
            videostartdevicedate = unsyncedmediaitems.get(syncupdationcounter).getVideostartdevicedate().toString();
            devicetimeoffset = unsyncedmediaitems.get(syncupdationcounter).getDevicetimeoffset().toString();
            videocompletedevicedate = unsyncedmediaitems.get(syncupdationcounter).getVideocompletedevicedate().toString();
        }

        if(sync.equalsIgnoreCase("0")){

            String currentdate[] = common.getcurrentdatewithtimezone();
            String firstdate = currentdate[0];

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("version",synchdefaultversion);
            map.put("firstdate",firstdate);
            map.put("lastdate",firstdate);
            map.put("lastsequence",synchlastsequence);
            map.put("status",synchstatus);
            map.put("completedate",synchcompletedate);

            Gson gson = new Gson();
            String json = gson.toJson(map);
            Log.e("json",""+json);
            updatedatasync(json,selectedid);
        }

        Log.e("video_updateid ",""+selectedid);

        if(!header.isEmpty()){
            try {
                JSONObject obj = new JSONObject(header);
                hashmethod  = obj.getString("hashmethod");
                hashvalue  = obj.getString("firsthash");
                mediatitle  = obj.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final String  finalselectedid = selectedid;

        mpairslist.put("html","0");
        mpairslist.put("hashmethod",""+hashmethod);
        mpairslist.put("hashvalue",""+hashvalue);
        mpairslist.put("title",mediatitle);
        mpairslist.put("apirequestdevicedate",apirequestdevicedate);
        mpairslist.put("devicetimeoffset",devicetimeoffset);

        if(type.equalsIgnoreCase("video"))
        {
            mpairslist.put("videostartdevicedate",videostartdevicedate);
        }
        else if(type.equalsIgnoreCase("audio"))
        {
            mpairslist.put("audiostartdevicedate",videostartdevicedate);
        }
        else if(type.equalsIgnoreCase("image"))
        {
            mpairslist.put("imagestartdevicedate",videostartdevicedate);
        }

        if(mediakey.trim().isEmpty())
        {
            // api calling for video_start or audio_start
            xapipost_sendjson(locationawareactivity.this,action_type,mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response)
                {
                    if(response.isSuccess())
                    {
                        try {
                            String token="",transactionid="";
                            JSONObject object = (JSONObject) response.getData();
                            String key=object.getString("key");
                            if(object.has("videotoken"))
                                token=object.getString("videotoken");

                            if(object.has("audiotoken"))
                                token=object.getString("audiotoken");

                            if(object.has("imagetoken"))
                                token=object.getString("imagetoken");

                            if(object.has("videostarttransactionid"))
                                transactionid=object.getString("videostarttransactionid");

                            if(object.has("audiostarttransactionid"))
                                transactionid=object.getString("audiostarttransactionid");

                            if(object.has("imagestarttransactionid"))
                                transactionid=object.getString("imagestarttransactionid");

                            updatevideokeytoken(finalselectedid,key,token,transactionid);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    triggersyncstatus();
                }
            });
        }
        else
        {
            if(sync_date.equalsIgnoreCase("0"))
                runmediaupdate(header,localkey,mediakey,token,sync,devicetimeoffset,apirequestdevicedate,finalselectedid,videocompletedevicedate,type);
        }
    }

    public void triggersyncstatus()
    {
        syncupdationcounter++;
        if(syncupdationcounter < unsyncedmediaitems.size() && unsyncedmediaitems.size() > 0)
        {
            updateunsyncedcomposeritems();
        }
        else
        {
            isdatasyncing=false;
        }
    }

    public void runmediaupdate(String finalheader, String finallocalkey, String finalvideokey, String finaltoken, String finalsync,
                               String finaldevicetimeoffset, String finalapirequestdevicedate, String startselectedid,
                               String finalvideocompletedevicedate,String type){

        String selectedid = "", blockchain= "",valuehash= "",hashmethod= "",localkey= "",metricdata= "",
                recordate= "",rsequenceno= "",sequencehash= "",sequenceno= "",serverdate= "",videoupdatedevicedate= "",sequencedevicedate = "";

        HashMap<String,Object> mpairslist=new HashMap<String, Object>();
        JSONObject finalobject = null;

        JSONArray array=new JSONArray();
        String matadata[] = new String[0];

        String currenttimewithoffset[] = common.getcurrentdatewithtimezone();

        String currentdate[] = common.getcurrentdatewithtimezone();
        videoupdatedevicedate = currentdate[0];

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {

            ArrayList<mediametadatainfo>  mediametadatainfosarray  =  mdbhelper.setmediametadatainfo(finallocalkey);


            if(mediametadatainfosarray != null && mediametadatainfosarray.size()>0){

                selectedid= mediametadatainfosarray.get(0).getId();
                blockchain = mediametadatainfosarray.get(0).getBlockchain();
                valuehash =mediametadatainfosarray.get(0).getValuehash();
                hashmethod = mediametadatainfosarray.get(0).getHashmethod();
                localkey = mediametadatainfosarray.get(0).getLocalkey();
                metricdata = mediametadatainfosarray.get(0).getMetricdata();
                recordate = mediametadatainfosarray.get(0).getRecordate();
                rsequenceno = mediametadatainfosarray.get(0).getRsequenceno();
                sequencehash =mediametadatainfosarray.get(0).getSequencehash();
                sequenceno =mediametadatainfosarray.get(0).getSequenceno();
                serverdate = mediametadatainfosarray.get(0).getServerdate();
                sequencedevicedate = mediametadatainfosarray.get(0).getSequencedevicedate();

            }else{

                callvideocompletedapi(startselectedid,finalheader,finallocalkey,finalapirequestdevicedate,finalvideokey,
                        finaldevicetimeoffset,finaltoken,finalvideocompletedevicedate,finalsync,type);
                return ;

            }

            final String finalselectedid =  selectedid;

            try {

                JSONArray jsonArray = new JSONArray(metricdata);

                JSONObject mainobject=new JSONObject();
                finalobject=new JSONObject();

                Gson userGson=new GsonBuilder().create();

                mainobject.put("dictionary",userGson.toJson(jsonArray.get(0)));
                mainobject.put("sequenceno",sequenceno);
                mainobject.put("recorddate",""+recordate);
                mainobject.put("dictionaryhashmethod",""+hashmethod);
                mainobject.put("sequencehashvalue",sequencehash);
                mainobject.put("dictionaryhashvalue",""+valuehash);
                mainobject.put("sequencedevicedate",""+sequencedevicedate);
                mainobject.put("sequencehashmethod",""+hashmethod);

                array.put(mainobject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mpairslist.put("html","0");
            mpairslist.put("key",""+finalvideokey);
            mpairslist.put("devicetimeoffset",""+finaldevicetimeoffset);
            mpairslist.put("apirequestdevicedate",""+finalapirequestdevicedate);

            mpairslist.put("sequencelist",  array);

            final String finalSequenceno = sequenceno;
            String actiontype = "";

            if(type.equalsIgnoreCase("video"))
            {
                actiontype = config.type_video_update;
                mpairslist.put("videotoken",finaltoken);
                mpairslist.put("videoupdatedevicedate",""+videoupdatedevicedate);
            }
            else if(type.equalsIgnoreCase("audio"))
            {
                actiontype = config.type_audio_update;
                mpairslist.put("audiotoken",finaltoken);
                mpairslist.put("audioupdatedevicedate",""+videoupdatedevicedate);
            }
            else if(type.equalsIgnoreCase("image"))
            {
                actiontype = config.type_image_update;
                mpairslist.put("imagetoken",finaltoken);
                mpairslist.put("imageupdatedevicedate",""+videoupdatedevicedate);
            }

            // api calling for video_update or audio_update
            xapipost_sendjson(locationawareactivity.this,actiontype, mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response)
                {
                    if(response.isSuccess())
                    {
                        try {

                            JSONObject object = (JSONObject) response.getData();
                            JSONObject jsonObject = object.getJSONObject("sequences");
                            String sequenceid =  "",mediaframetransactionid = "",serverdictionaryhash="",sequencekey = "";

                            Iterator itr = jsonObject.keys();
                            while(itr.hasNext()){
                                sequencekey = (String)itr.next();
                                JSONObject jobject = jsonObject.getJSONObject(sequencekey);

                                //  get id from  issue
                                sequenceid = jobject.getString("sequenceid");

                                if(jobject.has("videoframetransactionid"))
                                    mediaframetransactionid = jobject.getString("videoframetransactionid");

                                if(jobject.has("audioframetransactionid"))
                                    mediaframetransactionid = jobject.getString("audioframetransactionid");

                                if(jobject.has("imageframetransactionid"))
                                    mediaframetransactionid = jobject.getString("imageframetransactionid");

                                serverdictionaryhash = jobject.getString("serverdictionaryhash");
                            }

                            String serverdate = object.getString("serverdate");
                            updatevideoupdateapiresponse(finalselectedid,sequencekey,serverdate,serverdictionaryhash,sequenceid,mediaframetransactionid);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    triggersyncstatus();
                }
            });
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void callvideocompletedapi(String startselectedid,String finalheader,String finallocalkey,String finalapirequestdevicedate,
                                      String finalvideokey,String finaldevicetimeoffset,String finaltoken,
                                      String finalvideocompletedevicedate,String finalsync,String type){

        HashMap<String,Object> mpairslist=new HashMap<String, Object>();
        final String localkey = finallocalkey;
        final String header = finalheader,sync =finalsync;
        String synchdefaultversion = "1", synchstatus = "completed", synchcompletedate = "", synchlastsequence = "";

        String framecount="",videocompletedevicedate="",videoduration="",hassync="",synccurrentdate = "";

        String currentdate[] = common.getcurrentdatewithtimezone();
        String Lastdate = currentdate[0];

        try {

            JSONObject obj = new JSONObject(sync);
            JSONObject objheader = new JSONObject(header);


            framecount  = objheader.getString("frmaecounts");
            videoduration  = objheader.getString("duration");

            synccurrentdate = obj.getString("firstdate");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("version",synchdefaultversion);
        map.put("firstdate",synccurrentdate);
        map.put("lastdate",Lastdate);
        map.put("lastsequence",framecount);
        map.put("status",synchstatus);
        map.put("completedate",finalvideocompletedevicedate);

        Gson gson = new Gson();
        String json = gson.toJson(map);
        Log.e("json",""+json);

        updatedatasync(json,startselectedid);

        mpairslist.put("html","0");
        mpairslist.put("key",""+finalvideokey);
        mpairslist.put("devicetimeoffset",""+finaldevicetimeoffset);
        mpairslist.put("apirequestdevicedate",""+finalapirequestdevicedate);
        mpairslist.put("framecount", framecount);

        String actiontype="";
        if(type.equalsIgnoreCase("video"))
        {
            actiontype = config.type_video_complete;
            mpairslist.put("videocompletedevicedate",""+finalvideocompletedevicedate);
            mpairslist.put("videotoken",finaltoken);
            mpairslist.put("videoduration", videoduration);
        }
        else if(type.equalsIgnoreCase("audio"))
        {
            actiontype = config.type_audio_complete;
            mpairslist.put("audiocompletedevicedate",""+finalvideocompletedevicedate);
            mpairslist.put("audiotoken",finaltoken);
            mpairslist.put("audioduration", videoduration);
        }
        else if(type.equalsIgnoreCase("image"))
        {
            actiontype = config.type_image_complete;
            mpairslist.put("imagecompletedevicedate",""+finalvideocompletedevicedate);
            mpairslist.put("imagetoken",finaltoken);
            mpairslist.put("imageduration", videoduration);
        }

        xapipost_sendjson(locationawareactivity.this,actiontype, mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response)
            {
                if(response.isSuccess())
                {
                    try {

                        JSONObject object = (JSONObject) response.getData();
                        String valuehash = object.getString("hashvalue");

                        updatecompletehashvalue(localkey,valuehash);
                        updatedatasyncdate(localkey,common.getCurrentDate(),config.sync_complete);

                        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer)){
                            Intent i = new Intent(config.composer_service_getencryptionmetadata);
                            sendBroadcast(i);
                        }
                     /* String sequence = object.getString("sequence");
                        String serverdate = object.getString("serverdate");
                        String serverdictionaryhash = object.getString("serverdictionaryhash");
                        updatevideoupdateapiresponse(finalselectedid,sequence,serverdate,serverdictionaryhash);*/

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                triggersyncstatus();
            }
        });
    }

    public void updatedatasync(String sync,String selectedid)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatesyncvalue(sync,selectedid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updatedatasyncdate(String localkey,String syncdate,String syncstatus)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideosyncdate(localkey,syncdate,syncstatus);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void updatecompletehashvalue(String localkey,String valuehash)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatecompletehashvalue(localkey,valuehash);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updatevideokeytoken(String videoid,String videokey,String tokan,String transactionid)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideokeytoken(videoid,videokey,tokan,transactionid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void updatevideoupdateapiresponse(String selectedid, String sequence, String serverdate, String serverdictionaryhash, String sequenceid, String videoframetransactionid)
    {

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideoupdateapiresponse(selectedid,sequence,serverdate,serverdictionaryhash,sequenceid,videoframetransactionid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //** end code of media composer sync process

}