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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.composeoptionspagerfragment;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.celltowermodel;
import com.deeptruth.app.android.models.mediametadatainfo;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.startmediainfo;
import com.deeptruth.app.android.models.towerinfo;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.services.locationservice;
import com.deeptruth.app.android.services.readmediadataservice;
import com.deeptruth.app.android.utils.GetSpeedTestHostsHandler;
import com.deeptruth.app.android.utils.HttpDownloadTest;
import com.deeptruth.app.android.utils.PingTest;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.googleutils;
import com.deeptruth.app.android.utils.logs;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.noise;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class locationawareactivity extends baseactivity implements GpsStatus.Listener,LocationListener, Orientation.Listener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_update_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_updated_time_key";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 99;

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
    String CALL_STATUS = "", CALL_DURATION = "", CALL_REMOTE_NUMBER = "", CALL_START_TIME = "", connectionspeed = "",
            connectiondatadelay = "",availablewifinetwork="";
    MyPhoneStateListener mPhoneStatelistener;
    int mSignalStrength = 0, dbtoxapiupdatecounter = 0,sidecarupdatorinterval = 4, servermetricsgetupdatecounter = 0,currenttowercounter=0,currentsatellitecounter=-1;
    noise mNoise;
    private static final int PERMISSION_RECORD_AUDIO = 92;
    public static final int my_permission_read_phone_state = 90;
    private static final int request_permissions = 101;
    private Runnable doafterallpermissionsgranted;
    private Handler myHandler;
    private Runnable myRunnable;
    public boolean isrecording = false;
    public boolean iscameracapture = false;
    private WifiManager wifiManager;
    private List<ScanResult> results;

    long startTime;
    long endTime;
    long fileSize;
    OkHttpClient client = new OkHttpClient();
    // bandwidth in kbps
    private int POOR_BANDWIDTH = 150;

    List<CellInfo> towerinfolist = new ArrayList<>();
    private boolean isservicebound = false,isappinbackground=false;
    private databasemanager mdbhelper;
    boolean updatesync = true;
    ArrayList<towerinfo> cellinfofromassets = new ArrayList<>();
    ArrayList<celltowermodel> celltowers = new ArrayList<>();
    ArrayList<startmediainfo> unsyncedmediaitems = new ArrayList<>();
    int syncupdationcounter = 0;
    int downloadmetadataattempt = 2, systemdialogshowrequestcode = 110;
    ArrayList<video> readerarraymedialist = new ArrayList<video>();
    private BroadcastReceiver coredatabroadcastreceiver;
    Date initialdate;
    private Orientation mOrientation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest locationRequest;
    private Location oldlocation;
    private int lastinclination = -1, timercounterhandler = -1;
    private long workT, totalT, workAMT, totalprocess, totalBefore, work, workBefore, workAM, workAMBefore;
    String[] processsysteminfo;
    BroadcastReceiver wifiReceiver;
    Intent locationService;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationviavideocomposer.setActivity(locationawareactivity.this);
        tempBlackList = new HashSet<>();
        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
            getSpeedTestHostsHandler.start();
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(locationawareactivity.this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(locationawareactivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        if (location != null) {
                            try {
                                googleutils.saveUserCurrentLocation(location);

                                float mps = location.getSpeed();
                                //float mps = 0.2f;
                                xdata.getinstance().saveSetting("gpsspeed", "" + mps);

                                DecimalFormat precision = new DecimalFormat("0.0");
                                if (xdata.getinstance().getSetting(config.istravelleddistanceneeded).equalsIgnoreCase("true"))
                                {
                                    if(! xdata.getinstance().getSetting(config.travelleddistance).trim().isEmpty())
                                    {
                                        try
                                        {
                                            if (oldlocation == null)
                                                oldlocation = location;

                                            double value=Double.parseDouble(xdata.getinstance().getSetting(config.travelleddistance).trim());
                                            double meter=value+oldlocation.distanceTo(location);
                                            xdata.getinstance().saveSetting(config.travelleddistance, "" + precision.format(meter));
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                    {
                                        xdata.getinstance().saveSetting(config.travelleddistance, "0.0");
                                    }
                                }
                                else
                                {
                                    xdata.getinstance().saveSetting(config.travelleddistance, "0.0");
                                }

                                oldlocation = location;

                                if (location.hasAltitude()) {
                                    int altitudefeet = (int) (location.getAltitude() / 0.3048);
                                    xdata.getinstance().saveSetting(config.gpsaltitude, "" + altitudefeet);
                                }

                                if (location == null || location.getLatitude() == 0.0)
                                    return;

                                if (getcurrentfragment() != null) {
                                    getcurrentfragment().oncurrentlocationchanged(location);
                                    updatelocationsparams(location);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

                ;
            };
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        newsavedinstancestate = savedInstanceState;
        telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initialdate = new Date();
        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer)) {
            mOrientation = new Orientation(this);
            registerallbroadcast();
            getconnectionspeed();
        }
    }

    public void loadtowerinfofromassets() {
        if(cellinfofromassets.size() > 0)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    // source link -> https://github.com/musalbas/mcc-mnc-table
                   // InputStream is = applicationviavideocomposer.getactivity().getAssets().open("mcc-mnc-table.json");
                    InputStream is = applicationviavideocomposer.getactivity().getAssets().open("mcc-mnc-list.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    String json = new String(buffer, "UTF-8");
                    JSONArray array=new JSONArray(json);
                    for(int i=0;i<array.length();i++)
                    {
                        towerinfo info=new towerinfo();
                        JSONObject object=array.getJSONObject(i);
                        /*if(object.has("network"))
                            info.setNetwork(object.getString("network"));
                        if(object.has("country"))
                            info.setCountry(object.getString("country"));
                        if(object.has("mcc"))
                            info.setMcc(object.getString("mcc"));
                        if(object.has("iso"))
                            info.setIso(object.getString("iso"));
                        if(object.has("country_code"))
                            info.setCountrycode(object.getString("country_code"));
                        if(object.has("mnc"))
                            info.setMnc(object.getString("mnc"));*/
                        if(object.has("brand"))
                        {
                            String operatorname="";
                            if(! object.getString("brand").equalsIgnoreCase("null"))
                                operatorname=object.getString("brand");
                            if(! object.getString("operator").equalsIgnoreCase("null"))
                                operatorname=operatorname+" "+object.getString("operator");

                            info.setNetwork(operatorname);
                        }

                        if(object.has("countryName"))
                            info.setCountry(object.getString("countryName"));
                        if(object.has("mcc"))
                            info.setMcc(object.getString("mcc"));
                        if(object.has("countryCode"))
                            info.setIso(object.getString("countryCode"));
                        if(object.has("country_code"))
                            info.setCountrycode(object.getString("country_code"));
                        if(object.has("mnc"))
                            info.setMnc(object.getString("mnc"));

                        cellinfofromassets.add(info);
                    }

                    loadcellinfo();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        //attitudeindicator.setAttitude(pitch, roll);
    }

    @Override
    public boolean isuserlogin() {
        if (xdata.getinstance().getSetting(config.authtoken).trim().isEmpty())
            return false;

        return true;
    }

    @Override
    public void redirecttologin() {
        Intent intent=new Intent(locationawareactivity.this,registrationcontaineractivity.class);
        startActivityForResult(intent, config.requestcode_login);
        overridePendingTransition(R.anim.from_right_in,R.anim.from_left_out);
    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        if (adjustedRotationMatrix != null && adjustedRotationMatrix.length > 0) {
            String datavalue = "";
            for (float number : adjustedRotationMatrix) {
                if (datavalue.isEmpty()) {
                    datavalue = "" + number;
                } else {
                    datavalue = datavalue + "," + number;
                }
            }
            xdata.getinstance().saveSetting(config.phone_attitude, datavalue);
        }

        // Log.e("Pitch roll cases ",xdata.getinstance().getSetting(config.phone_attitude));
    }

    public void getsistermetric() {
        HashMap<String, String> requestparams = new HashMap<>();
        requestparams.put("action", "servermetrics_get");
        xapipost_send(locationawareactivity.this, requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if (response.isSuccess()) {
                    try {
                        JSONObject object = new JSONObject(response.getData().toString());
                        if (object.has("metrics")) {
                            xdata.getinstance().saveSetting(config.sister_metric, object.getJSONObject("metrics").toString());
                            JSONObject metricobject = object.getJSONObject("metrics");
                            if (metricobject.has("satellitedate"))
                                xdata.getinstance().saveSetting(config.item_satellitesdate, metricobject.getString("satellitedate"));

                            if (metricobject.has("satellites"))
                                xdata.getinstance().saveSetting(config.item_satellitesdata, metricobject.getJSONArray("satellites").toString());

                            if (metricobject.has("remote_ip"))
                                xdata.getinstance().saveSetting(config.item_remoteip, metricobject.getString("remote_ip"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getcurrentsatellitesget() {
        HashMap<String, String> requestparams = new HashMap<>();
        requestparams.put("action", "currentsatellites_get");
        xapipost_send(locationawareactivity.this, requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if (response.isSuccess()) {
                    try {
                        JSONObject object = new JSONObject(response.getData().toString());
                        /*if (object.has("metrics"))
                        {
                            xdata.getinstance().saveSetting(config.sister_metric, object.getJSONObject("metrics").toString());
                            JSONObject metricobject = object.getJSONObject("metrics");
                            if (metricobject.has("satellitedate"))
                                xdata.getinstance().saveSetting(config.satellitedate, metricobject.getString("satellitedate"));

                            if (metricobject.has("satellites"))
                                xdata.getinstance().saveSetting(config.satellitesdata, metricobject.getJSONArray("satellites").toString());

                            if (metricobject.has("remote_ip"))
                                xdata.getinstance().saveSetting(config.remoteip, metricobject.getString("remote_ip"));
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getconnectionspeed() {
        if (!common.isnetworkconnected(locationawareactivity.this)) {
            connectionspeed = "NA";
            connectiondatadelay = "NA";
            return;
        }

        httpdownloadtest downloadtest=new httpdownloadtest();
        downloadtest.execute();

        /*HttpDownloadTest downloadTest = new HttpDownloadTest
                ("https://m.media-amazon.com/images/S/aplus-media/vc/6a9569ab-cb8e-46d9-8aea-a7022e58c74a.jpg",
                        downloadspeeddelayupdate);
        downloadTest.start();*/

        /*Request request = new Request.Builder()
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
                double downloadElapsedTime = 0,finalDownloadRate=0,downloadedByte=0;
                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    //  Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                InputStream input = response.body().byteStream();

                try {

                    byte[] buffer = new byte[1024];
                    int len = 0,timeout=15;
                    outer:

                    while ((len = input.read(buffer)) != -1) {
                        downloadedByte += len;
                        endTime = System.currentTimeMillis();
                        downloadElapsedTime = (endTime - startTime) / 1000.0;
                        //setInstantDownloadRate(downloadedByte, downloadElapsedTime);
                        if (downloadElapsedTime >= timeout) {
                            break outer;
                        }
                    }

                } finally {
                    input.close();
                }

                endTime = System.currentTimeMillis();
                downloadElapsedTime = (endTime - startTime) / 1000.0;
                connectiondatadelay = "" + String.valueOf(new DecimalFormat("#.##").format(downloadElapsedTime)) + " second";
                finalDownloadRate = ((downloadedByte * 8) / (1000 * 1000.0)) / downloadElapsedTime;
                connectionspeed = String.valueOf(new DecimalFormat("#.##").format(finalDownloadRate)) + " mbps";
                Log.e("connectionspeed ",connectionspeed);

            }

        });*/
    }


    public class httpdownloadtest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpConn = null;
            long startTime = 0;
            long endTime = 0;
            double downloadElapsedTime = 0;
            int downloadedByte = 0;
            double finalDownloadRate = 0.0;
            int timeout = 15;

            URL url = null;
            downloadedByte = 0;
            int responseCode = 0;

            startTime = System.currentTimeMillis();
            try {
                url = new URL("https://m.media-amazon.com/images/S/aplus-media/vc/6a9569ab-cb8e-46d9-8aea-a7022e58c74a.jpg");
                httpConn = (HttpURLConnection) url.openConnection();
                responseCode = httpConn.getResponseCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    byte[] buffer = new byte[10240];

                    InputStream inputStream = httpConn.getInputStream();
                    int len = 0;

                    while ((len = inputStream.read(buffer)) != -1) {
                        downloadedByte += len;
                        endTime = System.currentTimeMillis();
                        downloadElapsedTime = (endTime - startTime) / 1000.0;
                        //setInstantDownloadRate(downloadedByte, downloadElapsedTime);
                        if (downloadElapsedTime >= timeout) {
                            break;
                        }
                    }

                    inputStream.close();
                    httpConn.disconnect();
                }

                endTime = System.currentTimeMillis();
                downloadElapsedTime = (endTime - startTime) / 1000.0;
                finalDownloadRate = ((downloadedByte * 8) / (1000 * 1000.0)) / downloadElapsedTime;
                connectiondatadelay = "" + String.valueOf(new DecimalFormat("#.##").format(downloadElapsedTime)) + " second";
                connectionspeed = String.valueOf(new DecimalFormat("#.##").format(finalDownloadRate)) + " mbps";
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /*public void setInstantDownloadRate(int downloadedByte, double elapsedTime) {

        if (downloadedByte >= 0) {
            this.instantDownloadRate = round((Double) (((downloadedByte * 8) / (1000 * 1000)) / elapsedTime), 2);
        } else {
            this.instantDownloadRate = 0.0;
        }
    }*/

    /*public void getconnectionspeed()
    {
        //Get egcodes.speedtest hosts
        int timeCount = 600; //1min
        while (!getSpeedTestHostsHandler.isFinished()) {
            timeCount--;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (timeCount <= 0) {
                // No internet
                getSpeedTestHostsHandler = null;
                return;
            }
        }

        //Find closest server
        HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
        HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
        double selfLat = getSpeedTestHostsHandler.getSelfLat();
        double selfLon = getSpeedTestHostsHandler.getSelfLon();
        double tmp = 19349458;
        double dist = 0.0;
        int findServerIndex = 0;
        for (int index : mapKey.keySet()) {
            if (tempBlackList.contains(mapValue.get(index).get(5))) {
                continue;
            }

            Location source = new Location("Source");
            source.setLatitude(selfLat);
            source.setLongitude(selfLon);

            List<String> ls = mapValue.get(index);
            Location dest = new Location("Dest");
            dest.setLatitude(Double.parseDouble(ls.get(0)));
            dest.setLongitude(Double.parseDouble(ls.get(1)));

            double distance = source.distanceTo(dest);
            if (tmp > distance) {
                tmp = distance;
                dist = distance;
                findServerIndex = index;
            }
        }
        String uploadAddr = mapKey.get(findServerIndex);
        final List<String> info = mapValue.get(findServerIndex);
        final double distance = dist;

        if (info == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
            return;
        }

        final List<Double> pingRateList = new ArrayList<>();
        final List<Double> downloadRateList = new ArrayList<>();
        Boolean pingTestStarted = false;
        Boolean pingTestFinished = false;
        Boolean downloadTestStarted = false;
        Boolean downloadTestFinished = false;

        //Init Test
        final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 6);
        final HttpDownloadTest downloadTest = new HttpDownloadTest(uploadAddr.replace(uploadAddr.split("/")[uploadAddr.split("/").length - 1], ""));

        //Tests
        while (true) {
            if (!pingTestStarted) {
                pingTest.start();
                pingTestStarted = true;
            }
            if (pingTestFinished && !downloadTestStarted) {
                downloadTest.start();
                downloadTestStarted = true;
            }

            //Ping Test
            if (pingTestFinished) {
                //Failure
                if (pingTest.getAvgRtt() == 0) {
                    System.out.println("Ping error...");
                } else {
                    //Success

                }
            }

            //Download Test
            if (pingTestFinished) {
                if (downloadTestFinished) {
                    //Failure
                    if (downloadTest.getFinalDownloadRate() == 0) {
                        System.out.println("Download error...");
                    } else {
                        //Success
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectionspeed=downloadTest.getFinalDownloadRate()+" Mbps";
                                String bb=downloadTest.getFinalDownloadRate()+" Mbps";

                            }
                        });
                    }
                }
            }
        }
    }*/

    public void getallpermissions() {
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            if (common.getphonelocationdeniedpermissions().isEmpty()) {
                // All permissions are granted

                doafterallpermissionsgranted();

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
        Log.e("locationservice","false");
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

            Log.e("serviceconnection","serviceconnection");

            if (name.endsWith("LocationService")) {
                /*locationService = ((LocationService.LocationServiceBinder) service).getService();
                locationService.startUpdatingLocation();*/
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen./*/*/*/*/*/*/*/*/*/**/*/*/*/*/*/*/*/*/*/
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
        if (!checkPermission(this)) {
            return;
        }

        loadtowerinfofromassets();
        getLastLocation();
        startLocationUpdates();
        enableGPS(locationawareactivity.this);
        preparemetricesdata();

        if (isMyServiceRunning(locationservice.class)) {
            Log.e("ServiceRunning ", "Yes");
        } else {
            Log.e("ServiceRunning ", "No");

            locationService = new Intent(applicationviavideocomposer.getactivity(), locationservice.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(locationService);
            else
                startService(locationService);

            isservicebound = applicationviavideocomposer.getactivity().bindService(locationService, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        if (mOrientation != null)
            mOrientation.startListening(this);
    }

    public void loadcellinfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    celltowers.clear();
                    TelephonyManager tm =
                            (TelephonyManager) getSystemService(
                                    Context.TELEPHONY_SERVICE);

                    int lCurrentApiVersion = Build.VERSION.SDK_INT;
                    try {

                        if (ActivityCompat.checkSelfPermission(locationawareactivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        List<CellInfo> cellInfoList = tm.getAllCellInfo();
                        if (cellInfoList != null && cellInfoList.size() > 0) {
                            for (final CellInfo info : cellInfoList)
                            {
                                celltowermodel celltower=new celltowermodel();
                                //Network Type
                                if (info instanceof CellInfoGsm) {
                                    final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                                    final CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                                    // Signal Strength
                                    celltower.setType("GSM ");
                                    celltower.setDbm(gsm.getDbm()); // [dBm]
                                    // Cell Identity
                                    celltower.setCid(identityGsm.getCid());
                                    celltower.setMcc(identityGsm.getMcc());
                                    celltower.setMnc(identityGsm.getMnc());
                                    celltower.setLac(identityGsm.getLac());
                                    if(celltower.getCid() != 2147483647)
                                        celltowers.add(celltower);

                                    //txt_item.append("\nbsic " + identityGsm.getBsic());
                                    //txt_item.append("\narfcn " + identityGsm.getArfcn());
                                }else if (info instanceof CellInfoLte) {
                                    final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                                    final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                                    // Signal Strength
                                    celltower.setType("LTE ");
                                    celltower.setDbm(lte.getDbm()); // [dBm]
                                    // Cell Identity
                                    celltower.setMcc(identityLte.getMcc());
                                    celltower.setMnc(identityLte.getMnc());
                                    celltower.setCi(identityLte.getCi());
                                    celltower.setCid(identityLte.getCi());
                                    //celltower.setTac(identityLte.getTac());
                                    celltower.setLac(identityLte.getTac());
                                    if(celltower.getCid() != 2147483647)
                                        celltowers.add(celltower);
                                    // txt_item.append("\ntiming advance " + ((""+lte.getTimingAdvance()).equals("2147483647")?"-":""+lte.getTimingAdvance()));

                                } else if (lCurrentApiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && info instanceof CellInfoWcdma) {
                                    final CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                                    final CellIdentityWcdma identityWcdma = ((CellInfoWcdma) info).getCellIdentity();
                                    // Signal Strength
                                    celltower.setType("WCDMA ");
                                    celltower.setDbm(wcdma.getDbm()); // [dBm]
                                    // Cell Identity
                                    celltower.setLac(identityWcdma.getLac());
                                    celltower.setMcc(identityWcdma.getMcc());
                                    celltower.setMnc(identityWcdma.getMnc());
                                    celltower.setCid(identityWcdma.getCid());
                                    celltower.setPsc(identityWcdma.getPsc());

                                    if(celltower.getCid() != 2147483647)
                                        celltowers.add(celltower);
                                }
                                /*else if (info instanceof CellInfoCdma) {
                                    final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                                    final CellIdentityCdma identityCdma = ((CellInfoCdma) info).getCellIdentity();
                                    // Signal Strength
                                    celltower.setType("CDMA ");
                                    celltower.setDbm(cdma.getDbm()); // [dBm]
                                    // Cell Identity
                                    celltower.setBasestationid(identityCdma.getBasestationId());
                                    celltower.setSystemid(identityCdma.getSystemId());
                                    celltower.setNetworkid(identityCdma.getNetworkId());
                                    celltowers.add(celltower);
                                }*/
                            }
                        }

                        JSONArray jsonArray=new JSONArray();
                        if(cellinfofromassets.size() > 0 && celltowers.size() > 0)
                        {
                            for(int i=0;i<cellinfofromassets.size();i++)
                            {
                                for(int j=0;j<celltowers.size();j++)
                                {
                                    if(cellinfofromassets.get(i).getMnc().equals(""+celltowers.get(j).getMnc()) &&
                                            cellinfofromassets.get(i).getMcc().equals(""+celltowers.get(j).getMcc()))
                                    {
                                        JSONObject object=new JSONObject();
                                        celltowers.get(j).setCountry(cellinfofromassets.get(i).getCountry());
                                        celltowers.get(j).setCountrycode(cellinfofromassets.get(i).getCountrycode());
                                        celltowers.get(j).setNetwork(cellinfofromassets.get(i).getNetwork());
                                        celltowers.get(j).setIso(cellinfofromassets.get(i).getIso());

                                        object.put("mcc",celltowers.get(j).getMcc());
                                        object.put("mnc",celltowers.get(j).getMnc());
                                        object.put("network",celltowers.get(j).getNetwork());
                                        object.put("country",celltowers.get(j).getCountry());
                                        object.put("iso",celltowers.get(j).getIso());
                                        object.put("dbm",celltowers.get(j).getDbm());
                                        object.put("country_code",celltowers.get(j).getCountrycode());
                                        object.put("cid",celltowers.get(j).getCid());
                                        jsonArray.put(object);
                                    }
                                }
                            }
                        }
                        if(jsonArray.length() > 0)
                            xdata.getinstance().saveSetting(config.json_towerlist,jsonArray.toString());
                        else
                            xdata.getinstance().saveSetting(config.json_towerlist,"");

                    } catch (NullPointerException npe) {
                        //Log.e("loadCellInfo: Unable : ", npe);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();


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

        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer)) {
            try {
                if (isservicebound)
                    applicationviavideocomposer.getactivity().unbindService(serviceConnection);

                try {

                    try {
                        unregisterReceiver(phonecallbroadcast);
                        unregisterReceiver(wifiReceiver);
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

            } catch (Exception e) {
                e.printStackTrace();
            }

            try
            {
                if(locationService != null)
                    stopService(locationService);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            stopLocationUpdates();
        }

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
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void stopLocationUpdates() {
        try {
            if (mFusedLocationClient != null)
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        //saving the user current location
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
            if (getcurrentfragment() != null) {
                getcurrentfragment().getAccurateLocation();
            }
        }

    }

    public void enableGPS(final Context context) {

        if (getcurrentfragment() != null) {
            if (getcurrentfragment() instanceof videocomposerfragment ||
                    getcurrentfragment() instanceof audiocomposerfragment || getcurrentfragment() instanceof imagecomposerfragment
                    || getcurrentfragment() instanceof composeoptionspagerfragment) {
                // its a case where user is on recorder (video/image/audio)
            } else {
                // its a case where it can show gps dialog in other fragments when onResume(activity) method called.
                return;
            }
        }
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
        } else if (requestCode == systemdialogshowrequestcode) {

        }
    }


    private void preparemetricesdata() {
        metricitemarraylist.clear();
        if (metricitemarraylist.size() == 0) {
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

                        try
                        {
                            if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
                                getconnectionspeed();

                            if (timercounterhandler == -1 || timercounterhandler >= 3) {
                                timercounterhandler = 0;
                                if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer) && (! isappinbackground)) {
                                    try {
                                        if (!locationawareactivity.checkLocationEnable(locationawareactivity.this))
                                            xdata.getinstance().saveSetting("gpsenabled", "0");
                                        else
                                            xdata.getinstance().saveSetting("gpsenabled", "1");

                                        if (getcurrentfragment() != null)
                                            getcurrentfragment().updatewifigpsstatus();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    towerinfolist = gettowerinfo();
                                    for (int i = 0; i < metricitemarraylist.size(); i++) {
                                        if (metricitemarraylist.get(i).isSelected()) {
                                            String value = metric_read(metricitemarraylist.get(i).getMetricTrackKeyName());
                                            metricitemarraylist.get(i).setMetricTrackValue(metricitemarraylist.get(i).getMetricTrackValue());

                                            if (!value.trim().isEmpty())
                                                metricitemarraylist.get(i).setMetricTrackValue(value);
                                        }
                                    }
                                }

                                if(! isappinbackground)
                                {
                                    servermetricsgetupdatecounter++;

                                    if ((servermetricsgetupdatecounter >= 10 && common.isnetworkconnected(locationawareactivity.this)) ||
                                            (xdata.getinstance().getSetting(config.item_satellitesdate).trim().isEmpty())) {
                                        servermetricsgetupdatecounter = 0;
                                        currentsatellitecounter++;
                                        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
                                            getsistermetric();
                                    }

                                    currenttowercounter++;
                                    if(currenttowercounter >= 10)
                                    {
                                        currenttowercounter=0;
                                        loadcellinfo();
                                    }

                                /*if ((currentsatellitecounter == -1 || (common.isnetworkconnected(locationawareactivity.this) &&
                                        currentsatellitecounter >= 5)))
                                {
                                    currentsatellitecounter = 0;
                                    getcurrentsatellitesget();
                                }*/
                                }
                            }

                        /*if((xdata.getinstance().getSetting(config.selectedsyncsetting).trim().isEmpty()) ||
                                (! xdata.getinstance().getSetting(config.selectedsyncsetting).
                                        equalsIgnoreCase(config.selectedsyncsetting_2))
                                || (! isappinbackground))
                        {

                        }*/

                            // Process for sync database with server for composer as well as reader app.
                            dbtoxapiupdatecounter++;
                            if (dbtoxapiupdatecounter > sidecarupdatorinterval)
                            {
                                dbtoxapiupdatecounter = 0;
                                syncmediadatabase();
                            }

                            timercounterhandler++;
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }).start();
                myHandler.postDelayed(this, 1000);
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
        } /*else if (key.equalsIgnoreCase(config.cpuusageuser) || key.equalsIgnoreCase(config.cpuusagesystem)
                || key.equalsIgnoreCase(config.cpuusageiow) || key.equalsIgnoreCase(config.cpuusageirq)) {
            getsystemusage();
            return "";
        }*/ else if (key.equalsIgnoreCase(config.cpuusagesystem)) {
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
            if(common.getxdatavalue(xdata.getinstance().getSetting(config.airplanemode)).equalsIgnoreCase("ON")){
                Log.e("airoplanemode","on");
                metricItemValue = "NA";
            }else{
                metricItemValue = telephonymanager.getNetworkOperatorName();
            }
        } else if (key.equalsIgnoreCase("carrierVOIP")) {

        } else if (key.equalsIgnoreCase("manufacturer")) {
            metricItemValue = Build.MANUFACTURER;
        } else if (key.equalsIgnoreCase("model") || key.equalsIgnoreCase("phonetype")) {
            metricItemValue = Build.MODEL;
        } else if (key.equalsIgnoreCase("version")) {
            metricItemValue = "" + Build.VERSION.SDK_INT;
        } else if (key.equalsIgnoreCase("osversion")) {
            metricItemValue = Build.VERSION.RELEASE;
        } else if (key.equalsIgnoreCase("devicedate")) {
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(config.date_format);
            String date = simpleDateFormat.format(calander.getTime());
            metricItemValue = date;
        } else if (key.equalsIgnoreCase("softwareversion")) {
            metricItemValue = "" + telephonymanager.getDeviceSoftwareVersion();
        } else if (key.equalsIgnoreCase("deviceregion") || (key.equalsIgnoreCase("country"))) {
            metricItemValue = "" + Locale.getDefault().getCountry();
        } else if (key.equalsIgnoreCase("timezone")) {
            //TimeZone timezone = TimeZone.getDefault();
            //metricItemValue = timezone.getID();
            metricItemValue = common.gettimezoneshortname();
        } else if (key.equalsIgnoreCase(config.phoneclocktime)) {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            String time = common.appendzero(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + common.appendzero(calendar.get(Calendar.MINUTE))
                    + ":" + common.appendzero(calendar.get(Calendar.SECOND));
            metricItemValue = time+" "+common.gettimezoneshortname();;
        } else if (key.equalsIgnoreCase(config.worldclocktime)) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            String time = common.appendzero(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + common.appendzero(calendar.get(Calendar.MINUTE))
                    + ":" + common.appendzero(calendar.get(Calendar.SECOND));
                metricItemValue = time +"GMT";
        }else if (key.equalsIgnoreCase(config.worldclockdate)) {
            DateFormat gmtFormat = new SimpleDateFormat(config.date_format);
            TimeZone gmtTime = TimeZone.getTimeZone("GMT");
            gmtFormat.setTimeZone(gmtTime);
            System.out.println("Current DateTime in GMT : " + gmtFormat.format(new Date()));
            metricItemValue = gmtFormat.format(new Date());
        }
        else if (key.equalsIgnoreCase(config.phoneclockdate)) {
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(config.date_format);
            String date = simpleDateFormat.format(calander.getTime());
            metricItemValue = date;
        }
        else if (key.equalsIgnoreCase("devicelanguage")) {
            metricItemValue = Locale.getDefault().getDisplayLanguage();
        } else if (key.equalsIgnoreCase("brightness")) {
            try {
                int brightnessValue = Settings.System.getInt(locationawareactivity.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                float total = 0;
                 if(brightnessValue >= 255){
                      total = (brightnessValue * 100) / 4095;
                 }else{
                      total = (brightnessValue * 100) / 255;
                 }

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
            metricItemValue = "NA";
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

                    if (key.equalsIgnoreCase("wifiname")) {
                        metricItemValue = connectionInfo.getSSID();
                        xdata.getinstance().saveSetting("wificonnected", "1");
                    }

                    metricItemValue = metricItemValue.replace("\"", "");
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
                metricItemValue = "NA";
                xdata.getinstance().saveSetting("wificonnected", "0");
                xdata.getinstance().saveSetting(config.availablewifis, "");
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
        }
        else if (key.equalsIgnoreCase("systemuptimeminutes"))
        {
            // Get the whole uptime
            metricItemValue = String.valueOf(common.getsystemuptimeinseconds()/60);
        }
        else if (key.equalsIgnoreCase("systemuptimeseconds"))
        {
            // Get the whole uptime
            int uptimeinseconds = common.getsystemuptimeinseconds();
            metricItemValue = String.valueOf(uptimeinseconds);
        }
        else if (key.equalsIgnoreCase("pluggedin")) {
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
            /*Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");*/
            String time = common.get24hourformat();
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
        } else if (key.equalsIgnoreCase(config.connectiondatadelay)) {
            metricItemValue = "" + connectiondatadelay;
        } else if (key.equalsIgnoreCase("address")) {
            metricItemValue = xdata.getinstance().getSetting("currentaddress");
        } else if (key.equalsIgnoreCase("celltowersignalstrength") || key.equalsIgnoreCase("celltowerid")|| (key.equalsIgnoreCase("deviceconnection"))) {

            if(common.getxdatavalue(xdata.getinstance().getSetting(config.airplanemode)).equalsIgnoreCase("ON")){
                metricItemValue = "NA";
            }else
            {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = telephonyManager.getNetworkType();

                if (towerinfolist != null && towerinfolist.size() > 0) {
                    CellInfo info = towerinfolist.get(0);
                    if (telephonyManager.getSimState() == telephonyManager.SIM_STATE_ABSENT) {
                        metricItemValue = "";
                    } else {
                        if (key.equalsIgnoreCase("celltowersignalstrength")) {

                            if (info != null) {
                                try {
                                    if (TelephonyManager.NETWORK_TYPE_LTE == networkType) {
                                        CellSignalStrengthLte gsm = ((CellInfoLte) info).getCellSignalStrength();
                                        int signalstrength = gsm.getDbm();
                                        metricItemValue = "" + signalstrength + " " + "dBm";
                                    } else {

                                        CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                                        int signalstrength = gsm.getDbm();
                                        metricItemValue = "" + signalstrength + " " + "dBm";
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else if (key.equalsIgnoreCase("celltowerid")) {
                            if (info != null) {

                                try {
                                    if (TelephonyManager.NETWORK_TYPE_LTE == networkType) {
                                        CellIdentityLte identity = ((CellInfoLte) info).getCellIdentity();
                                        int celltowerid = identity.getCi();
                                        metricItemValue = "" + celltowerid;
                                    } else {
                                        CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                                        int celltowerid = identityGsm.getCid();
                                        metricItemValue = "" + celltowerid;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (key.equalsIgnoreCase("deviceconnection")) {

                            switch (networkType) {
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                    metricItemValue = "2G";
                                    break;

                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    metricItemValue = "3G";
                                    break;

                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    metricItemValue = "4G";
                                    break;
                            }
                        }
                    }
                }
            }
        } else if (key.equalsIgnoreCase("numberoftowers")) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getSimState() == telephonyManager.SIM_STATE_ABSENT) {
                metricItemValue = "";
            } else {
                metricItemValue = "0";
                if (towerinfolist != null)
                    metricItemValue = "" + towerinfolist.size();
            }
        } else if (key.equalsIgnoreCase("connectionspeed")) {
            metricItemValue = "" + connectionspeed;
        } else if (key.equalsIgnoreCase("address")) {
            metricItemValue = xdata.getinstance().getSetting("currentaddress");
        } else if (key.equalsIgnoreCase("numberofsatellites")) {
            metricItemValue = xdata.getinstance().getSetting("gpsnumberofsatelites");
        } else if (key.equalsIgnoreCase("satelliteangle")) {
            metricItemValue = xdata.getinstance().getSetting("satelliteangle");
        } else if (key.equalsIgnoreCase("satelliteid")) {
            metricItemValue = xdata.getinstance().getSetting("satelliteid");
        } else if (key.equalsIgnoreCase("strengthofsatellites")) {
            metricItemValue = xdata.getinstance().getSetting("strengthofsatellites");
            ;
        } else if (key.equalsIgnoreCase("attitude")) {
            metricItemValue = xdata.getinstance().getSetting(config.phone_attitude);
        } else if (key.equalsIgnoreCase(config.airplanemode)) {
            metricItemValue = "ON";
            if (Settings.Global.getInt(locationawareactivity.this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {
                metricItemValue = "OFF";
            }
            if(metricItemValue.equalsIgnoreCase("ON"))
                xdata.getinstance().saveSetting(config.json_towerlist,"");

        } else if (key.equalsIgnoreCase(config.availablewifinetwork)) {
            getwifinetworks();
           //  metricItemValue = availablewifinetwork;
             metricItemValue = xdata.getinstance().getSetting(config.availablewifis);
        }
        else if (key.equalsIgnoreCase(config.satellitedate)) {
            metricItemValue = xdata.getinstance().getSetting(config.item_satellitesdate);
        }
        else if (key.equalsIgnoreCase(config.satellitesdata)) {
            metricItemValue = xdata.getinstance().getSetting(config.item_satellitesdata);
        }
        else if (key.equalsIgnoreCase(config.remoteip)) {
            metricItemValue = xdata.getinstance().getSetting(config.item_remoteip);
        }
        else if (key.equalsIgnoreCase(config.jailbroken)) {
            metricItemValue = xdata.getinstance().saveSetting(config.jailbroken,"NO");
        }
        else if (key.equalsIgnoreCase(config.camera)) {
            metricItemValue = xdata.getinstance().getSetting(config.camera);
        }
        else if (key.equalsIgnoreCase(config.picturequality)) {
            metricItemValue = xdata.getinstance().getSetting(config.picturequality);
        }
        else if (key.equalsIgnoreCase(config.screenorientatioin)) {
            metricItemValue = xdata.getinstance().getSetting(config.screenorientatioin);
        }
        /*else if (key.equalsIgnoreCase(config.sister_metric)) {
            metricItemValue=xdata.getinstance().getSetting(config.sister_metric);
        }
        else if (key.equalsIgnoreCase(config.json_blob)) {
            metricItemValue=xdata.getinstance().getSetting(config.json_blob);
        }*/

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
        isappinbackground=false;
        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
            getallpermissions();
        else if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            startmetrices();
    }

    @Override
    public void onPause() {
        stopLocationUpdates();
        super.onPause();
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
                        /*float deltaX = Math.abs(sensorEvent.values[0]);
                        float deltaY = Math.abs(sensorEvent.values[1]);
                        float deltaZ = Math.abs(sensorEvent.values[2]);
                        Math.sin(deltaX);

                        double X=deltaX * 180/Math.PI;
                        double Y=deltaY * 180/Math.PI;
                        double Z=deltaZ * 180/Math.PI;

                        String x = String.valueOf(new DecimalFormat("#.#").format(X));
                        String y = String.valueOf(new DecimalFormat("#.#").format(Y));
                        String z = String.valueOf(new DecimalFormat("#.#").format(Z));*/

                        double ax, ay, az;

                        ax = sensorEvent.values[0];
                        ay = sensorEvent.values[1];
                        az = sensorEvent.values[2];

                        /*int inclination = (int) Math.round(Math.toDegrees(Math.acos(ay)));
                        if (inclination != lastinclination) {

                        }*/
                        double xAngle = Math.atan(ax / (Math.round(Math.pow(ay, 2) + Math.pow(az, 2))));
                        double yAngle = Math.atan(ay / (Math.round(Math.pow(ax, 2) + Math.pow(az, 2))));
                        double zAngle = Math.atan(Math.round(Math.pow(ax, 2) + Math.pow(ay, 2)) / az);

                        xAngle *= 180.00;
                        yAngle *= 180.00;
                        zAngle *= 180.00;
                        xAngle /= 3.141592;
                        yAngle /= 3.141592;
                        zAngle /= 3.141592;

                        // calculation for x-axis
                        if(xAngle >= -90 && xAngle <= 0 && yAngle >= 0 && yAngle <= 90)
                        {
                            // xAxis is between 0-90
                            xAngle=Math.abs(xAngle);
                        }
                        else if(xAngle >= -90 && xAngle <= 0 && yAngle >= -90 && yAngle <= -1)
                        {
                            // xAxis is between 90-180
                            xAngle=Math.abs(xAngle);
                            xAngle=(90-xAngle);
                            xAngle=90+xAngle;
                        }
                        else if(xAngle >= 0 && xAngle <= 90 && yAngle >= -90 && yAngle <= 0)
                        {
                            // xAxis is between 180-270
                            xAngle=Math.abs(xAngle);
                            xAngle=xAngle+180;
                        }
                        else if(xAngle > 0 && xAngle <= 90 && yAngle > 0 && yAngle <= 90)
                        {
                            // xAxis is between 270-360
                            xAngle=Math.abs(xAngle);
                            //xAngle=(90-xAngle);
                            //xAngle=270+xAngle;
                        }

                        xAngle=Math.abs(xAngle);
                        yAngle=Math.abs(yAngle);
                        zAngle=Math.abs(zAngle);

                        /*updatearrayitem(config.acceleration_x, "" + new DecimalFormat("#.#").format(Math.abs(xAngle)) + "° ");
                        updatearrayitem(config.acceleration_y, "" + new DecimalFormat("#.#").format(Math.abs(yAngle)) + "° ");
                        updatearrayitem(config.acceleration_z, "" + new DecimalFormat("#.#").format(Math.abs(zAngle)) + "° ");*/

                        updatearrayitem(config.acceleration_x, "" + new DecimalFormat("#.#").format(xAngle) + "° ");
                        updatearrayitem(config.acceleration_y, "" + new DecimalFormat("#.#").format(yAngle) + "° ");
                        updatearrayitem(config.acceleration_z, "" + new DecimalFormat("#.#").format(zAngle) + "° ");

                        //lastinclination = inclination;
                        // Source link of degree conversion http://wizmoz.blogspot.com/2013/01/simple-accelerometer-data-conversion-to.html
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

                    try {
                        float totalcpu = 0, cpuAM = 0;
                        if (Build.VERSION.SDK_INT < 26) {
                            {
                                BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
                                processsysteminfo = reader.readLine().split("[ ]+", 9);
                                work = Long.parseLong(processsysteminfo[1]) + Long.parseLong(processsysteminfo[2]) + Long.parseLong(processsysteminfo[3]);
                                totalprocess = work + Long.parseLong(processsysteminfo[4]) + Long.parseLong(processsysteminfo[5]) + Long.parseLong(processsysteminfo[6]) + Long.parseLong(processsysteminfo[7]);
                                reader.close();
                            }

                            {
                                int pId = Process.myPid();
                                BufferedReader reader = new BufferedReader(new FileReader("/proc/" + pId + "/stat"));
                                processsysteminfo = reader.readLine().split("[ ]+", 18);
                                workAM = Long.parseLong(processsysteminfo[13]) + Long.parseLong(processsysteminfo[14]) + Long.parseLong(processsysteminfo[15]) + Long.parseLong(processsysteminfo[16]);
                                reader.close();
                            }

                            if (totalBefore != 0) {
                                totalT = totalprocess - totalBefore;
                                workT = work - workBefore;
                                workAMT = workAM - workAMBefore;

                                totalcpu = workT * 100 / (float) totalT;
                                cpuAM = workAMT * 100 / (float) totalT;
                                float cpuAM1 = workAMT * 100 / (float) totalT;
                            }
                            totalBefore = totalprocess;
                            workBefore = work;
                            workAMBefore = workAM;

                            final float finalTotalcpu = totalcpu;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (getcurrentfragment() != null) {
                                        updatearrayitem(config.cpuusageuser, "" + (int) (finalTotalcpu) + "%");
                                        updatearrayitem(config.cpuusagesystem, "" + (int) (finalTotalcpu) + "%");
                                        //updatearrayitem(config.cpuusageiow, ""+(int)(finalTotalcpu)+"%");
                                        //updatearrayitem(config.cpuusageirq, ""+(int)(finalTotalcpu)+"%");
                                    }
                                }
                            });
                        } else {
                            String system = common.executeTop();
                            if (system.contains("Tasks")) {
                                system = system.substring(system.indexOf("Tasks"), system.length());
                                system = system.replace("Tasks:", "");
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
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


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
                        // Log.e("Heading ",""+heading);;
                        String compassdirection = common.getcompassdirection(heading);
                        updatearrayitem(config.compass, compassdirection);
                        updatearrayitem(config.orientation, "" + (int) heading);
                        updatearrayitem(config.heading, "" + (int) heading);

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

            try {
                registerReceiver(phonecallbroadcast, intentFilter);
                registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
            } catch (Exception e) {
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

        if (mOrientation != null)
            mOrientation.startListening(this);

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
                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting(config.travelleddistance));
                if (xdata.getinstance().getSetting(config.travelleddistance).trim().isEmpty())
                    metricitemarraylist.get(i).setMetricTrackValue("0");
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.itemgpsaccuracy)) {
                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting(config.itemgpsaccuracy));
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("address")) {
                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting("currentaddress"));
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("speed")) {
                metricitemarraylist.get(i).setMetricTrackValue(xdata.getinstance().getSetting("gpsspeed"));
                if (xdata.getinstance().getSetting("gpsspeed").trim().isEmpty())
                    metricitemarraylist.get(i).setMetricTrackValue("0");
            }
            if (metricitemarraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude)) {
                String altitude = xdata.getinstance().getSetting(config.gpsaltitude);
                if (altitude.trim().isEmpty())
                    altitude = "0";

                metricitemarraylist.get(i).setMetricTrackValue("" + altitude + " feet");
            }
        }
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
        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)) {
            IntentFilter intentFilter = new IntentFilter(config.reader_service_getmetadata);
            coredatabroadcastreceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        updatesyncedreaderitems();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            registerReceiver(coredatabroadcastreceiver, intentFilter);
        }
        /*else if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
        {
            if(mFusedLocationClient != null)
            {
                getLastLocation();
                startLocationUpdates();
            }
        }*/
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(locationawareactivity.this,
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        isappinbackground=true;
        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)){
            try{
                applicationviavideocomposer.getactivity().unregisterReceiver(coredatabroadcastreceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
        {
            if (myHandler != null && myRunnable != null)
                myHandler.removeCallbacks(myRunnable);
        }

        if (mOrientation != null)
            mOrientation.stopListening();
    }

    public void syncmediadatabase() {

        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer)) {

            if (mdbhelper == null) {
                mdbhelper = new databasemanager(locationawareactivity.this);
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            unsyncedmediaitems = mdbhelper.fetchunsynceddata();
            try {
                mdbhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!common.isnetworkconnected(locationawareactivity.this))
                return;

            if(unsyncedmediaitems.size() > 0)
                updateunsyncedcomposeritems();

        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            if (common.isnetworkconnected(getApplicationContext())) {
                Date currentdate = new Date();
                String initialdatereaderapi = xdata.getinstance().getSetting("initialdatereaderapi");
                long olddate = 0;
                if (!initialdatereaderapi.trim().isEmpty())
                    olddate = Long.parseLong(initialdatereaderapi);

                int seconddifference = (int) (Math.abs(olddate - currentdate.getTime()) / 1000);
                if (seconddifference > 20 || olddate == 0)
                    updatesyncedreaderitems();
            }
        }
    }

    //------------------------------------------
    //** start code of media reader sync process
    public void updatesyncedreaderitems() {
        File videodir = new File(config.dirallmedia);
        if (!videodir.exists())
            return;

        databasemanager mdbhelper = null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            if (readerarraymedialist.size() > 0) {
                if (readerarraymedialist.get(readerarraymedialist.size() - 1).isIscheck())
                    readerarraymedialist.clear();
            }
            if (readerarraymedialist.size() == 0) {
                Cursor cursor = mdbhelper.getallmediastartdata();
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            String location = "" + cursor.getString(cursor.getColumnIndex("location"));
                            String type = "" + cursor.getString(cursor.getColumnIndex("type"));
                            String status = "" + cursor.getString(cursor.getColumnIndex("status"));
                            String mediafilepath = "" + cursor.getString(cursor.getColumnIndex("mediafilepath"));
                            String media_sync_attempt = "" + cursor.getString(cursor.getColumnIndex("media_sync_attempt"));

                            if (!status.equalsIgnoreCase(config.sync_complete) && (!status.equalsIgnoreCase(config.sync_notfound))) {
                                int totalattempt = 0;
                                if (media_sync_attempt.trim().isEmpty() || media_sync_attempt.equalsIgnoreCase("null")) {
                                    totalattempt = 0;
                                } else if (status.equalsIgnoreCase(config.sync_pending)) {
                                    totalattempt = Integer.parseInt(media_sync_attempt);
                                    totalattempt++;
                                }

                                if (totalattempt > downloadmetadataattempt) {
                                    status = config.sync_notfound;
                                    updatemediaattempt(location, totalattempt, status);
                                    sendbroadcastreader();
                                } else {
                                    updatemediaattempt(location, totalattempt, status);
                                    video videoobj = new video();
                                    videoobj.setPath(mediafilepath);
                                    videoobj.setmimetype(type);
                                    videoobj.setMediastatus(status);
                                    readerarraymedialist.add(videoobj);
                                }
                            }

                        } while (cursor.moveToNext());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (readerarraymedialist != null && readerarraymedialist.size() > 0)
        {
            for (int i = 0; i < readerarraymedialist.size(); i++) {
                String status = readerarraymedialist.get(i).getMediastatus();
                if (!readerarraymedialist.get(i).isIscheck()) {
                    readerarraymedialist.get(i).setIscheck(true);
                    if (!common.isnetworkconnected(applicationviavideocomposer.getappcontext()))
                        readerarraymedialist.get(i).setMediastatus(config.sync_offline);

                    if (status.equalsIgnoreCase(config.sync_inprogress) || status.equalsIgnoreCase(config.sync_pending)) {
                        readerarraymedialist.get(i).setMediastatus(config.sync_inprogress);
                        //initialdate = new filter_date();
                        callreadersyncservice(readerarraymedialist.get(i).getPath(), readerarraymedialist.get(i).getmimetype());
                        break;
                    }
                }
            }
            xdata.getinstance().saveSetting(config.sidecar_syncstatus,"1");   // 1 = syncing, 0 = not syncing
        }
        else
        {
            xdata.getinstance().saveSetting(config.sidecar_syncstatus,"0");   // 1 = syncing, 0 = not syncing
        }
    }

    public void callreadersyncservice(String mediafilepath, String mimetype) {
        if (!common.isnetworkconnected(getApplicationContext()))
            return;

        String keytype = common.checkkey();
        String firsthash = "";

        if (mimetype.startsWith("image"))
            firsthash = md5.fileToMD5(mediafilepath);

        Intent intent = new Intent(applicationviavideocomposer.getactivity(), readmediadataservice.class);
        intent.putExtra("firsthash", firsthash);
        intent.putExtra("mediapath", mediafilepath);
        intent.putExtra("keytype", keytype);
        if (mimetype.startsWith("video")) {
            intent.putExtra("mediatype", "video");
        } else if (mimetype.startsWith("audio")) {
            intent.putExtra("mediatype", "audio");
        } else if (mimetype.startsWith("image")) {
            intent.putExtra("mediatype", "image");
        }
        initialdate = new Date();
        xdata.getinstance().saveSetting("initialdatereaderapi", "" + initialdate.getTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);
    }
    //** end code of media reader sync process
    //--------------------------------------------------


    //** start code of media composer sync process
    public void updateunsyncedcomposeritems() {
        String hashmethod = "", hashvalue = "", mediatitle = "", selectedid = "", header = "", type = "", localkey = "", token = "", mediakey = "",
                sync = "", sync_date = "", action_type = "", apirequestdevicedate = "", videostartdevicedate = "", devicetimeoffset = "",
                videocompletedevicedate = "",mediafilepath="";

        String synchdefaultversion = "1", synchstatus = config.sync_inprogress, synchcompletedate = "", synchlastsequence = "";

        HashMap<String, Object> mpairslist = new HashMap<String, Object>();

        if (unsyncedmediaitems != null && unsyncedmediaitems.size() > 0 && syncupdationcounter < unsyncedmediaitems.size()) {
            selectedid = unsyncedmediaitems.get(syncupdationcounter).getId().toString();
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
            mediafilepath = unsyncedmediaitems.get(syncupdationcounter).getMediafilepath().toString();
            mediatitle = unsyncedmediaitems.get(syncupdationcounter).getMedianame().toString();
        }

        if (sync.equalsIgnoreCase("0")) {

            String currentdate[] = common.getcurrentdatewithtimezone();
            String firstdate = currentdate[0];

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("version", synchdefaultversion);
            map.put("firstdate", firstdate);
            map.put("lastdate", firstdate);
            map.put("lastsequence", synchlastsequence);
            map.put("status", synchstatus);
            map.put("completedate", synchcompletedate);

            Gson gson = new Gson();
            String json = gson.toJson(map);
            Log.e("json", "" + json);
            updatedatasync(json, selectedid);
        }

        Log.e("media_updateid ", "" + selectedid);

        if (!header.isEmpty()) {
            try {
                JSONObject obj = new JSONObject(header);
                hashmethod = obj.getString("hashmethod");
                hashvalue = obj.getString("firsthash");
                //mediatitle = obj.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final String finalselectedid = selectedid;

        mpairslist.put("html", "0");
        mpairslist.put("hashmethod", "" + hashmethod);
        mpairslist.put("hashvalue", "" + hashvalue);
        mpairslist.put("title", mediatitle);
        mpairslist.put("apirequestdevicedate", apirequestdevicedate);
        mpairslist.put("devicetimeoffset", devicetimeoffset);

        if (type.equalsIgnoreCase("video")) {
            mpairslist.put("videostartdevicedate", videostartdevicedate);
        } else if (type.equalsIgnoreCase("audio")) {
            mpairslist.put("audiostartdevicedate", videostartdevicedate);
        } else if (type.equalsIgnoreCase("image")) {
            mpairslist.put("imagestartdevicedate", videostartdevicedate);
        }

        if (mediakey.trim().isEmpty()) {
            // api calling for video_start or audio_start
            final String finalLocalkey = localkey;
            final String finalMediafilepath = mediafilepath;
            sidecarupdatorinterval=4;
            xapipost_sendjson(locationawareactivity.this, action_type, mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response) {
                    if (response.isSuccess()) {
                        try {
                            String token = "", transactionid = "";
                            JSONObject object = (JSONObject) response.getData();
                            String key = object.getString("key");
                            if (object.has("videotoken"))
                                token = object.getString("videotoken");

                            if (object.has("audiotoken"))
                                token = object.getString("audiotoken");

                            if (object.has("imagetoken"))
                                token = object.getString("imagetoken");

                            if (object.has("videostarttransactionid"))
                                transactionid = object.getString("videostarttransactionid");

                            if (object.has("audiostarttransactionid"))
                                transactionid = object.getString("audiostarttransactionid");

                            if (object.has("imagestarttransactionid"))
                                transactionid = object.getString("imagestarttransactionid");

                            if(object.has("imagetoken") && object.has("imagestarttransactionid"))
                            {
                                //isdatasyncing=true;
                                updatevideokeytoken(finalselectedid, key, token, transactionid);
                                //performsaveimageslice(finalLocalkey, finalMediafilepath);
                            }
                            else
                            {
                                updatevideokeytoken(finalselectedid, key, token, transactionid);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            if (sync_date.equalsIgnoreCase("0"))
                runmediaupdate(header, localkey, mediakey, token, sync, devicetimeoffset, apirequestdevicedate, finalselectedid, videocompletedevicedate, type);
        }
    }

    public void performsaveimageslice(String localkey, final String mediafilepath)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    File file=new File(mediafilepath);
                    if(file.exists())
                    {
                        Uri uri = FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                                BuildConfig.APPLICATION_ID + ".provider", file);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(locationawareactivity.this.getContentResolver(), uri);
                        ArrayList<String> splitimagearray=splitimage(bitmap,16);

                        if (mdbhelper == null) {
                            mdbhelper = new databasemanager(locationawareactivity.this);
                            mdbhelper.createDatabase();
                        }
                        try {
                            mdbhelper.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try
                        {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ArrayList<String> splitimage(Bitmap bitmap, int chunkNumbers) {

        //For the number of rows and columns of the grid to be displayed
        int rows,cols;
        //For height and width of the small image chunks
        int chunkHeight,chunkWidth;

        ArrayList<String> arrayList=new ArrayList<>();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int x = 0; x < rows; x++) {
            int xCoord = 0;
            for(int y = 0; y < cols; y++) {
                //Log.e("Properties ",""+xCoord+" "+yCoord+" "+chunkWidth+" "+chunkHeight+" "+chunkNumbers);
                Bitmap createdbitmap=Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                createdbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String value= md5.calculatebytemd5(byteArray);

                /*final int lnth=createdbitmap.getByteCount();
                ByteBuffer dst= ByteBuffer.allocate(lnth);
                createdbitmap.copyPixelsToBuffer( dst);
                byte[] barray=dst.array();
                String value= md5.calculatebytemd5(barray);
                Log.e("MD5 ",""+value);*/

                arrayList.add(value);
                /*storeImage(createdbitmap);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return arrayList;
    }

    public void runmediaupdate(String finalheader, final String finallocalkey, String finalvideokey, String finaltoken, String finalsync,
                               String finaldevicetimeoffset, String finalapirequestdevicedate, String startselectedid,
                               String finalvideocompletedevicedate, String type) {

        String selectedid = "", blockchain = "", valuehash = "", hashmethod = "", localkey = "", metricdata = "",
                recordate = "", rsequenceno = "", sequencehash = "", sequenceno = "", serverdate = "", videoupdatedevicedate = "", sequencedevicedate = "";

        HashMap<String, Object> mpairslist = new HashMap<String, Object>();
        JSONObject finalobject = null;

        JSONArray array = new JSONArray();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            ArrayList<mediametadatainfo> mediametadatainfosarray = mdbhelper.setmediametadatainfo(finallocalkey);
            if (mediametadatainfosarray == null || mediametadatainfosarray.size() == 0)
            {
                // calling media complete api
                callvideocompletedapi(startselectedid, finalheader, finallocalkey, finalapirequestdevicedate, finalvideokey,
                        finaldevicetimeoffset, finaltoken, finalvideocompletedevicedate, finalsync, type);
                return;
            }

            if (mediametadatainfosarray != null && mediametadatainfosarray.size() > 0){

                xdata.getinstance().saveSetting(config.frame_started,""+mediametadatainfosarray.get(0).getSequenceno());

                String logString="";
              for(int i=0;i<mediametadatainfosarray.size();i++)
                {
                    selectedid = mediametadatainfosarray.get(i).getId();
                    blockchain = mediametadatainfosarray.get(i).getBlockchain();
                    valuehash = mediametadatainfosarray.get(i).getValuehash();
                    hashmethod = mediametadatainfosarray.get(i).getHashmethod();
                    localkey = mediametadatainfosarray.get(i).getLocalkey();
                    metricdata = mediametadatainfosarray.get(i).getMetricdata();
                    recordate = mediametadatainfosarray.get(i).getRecordate();
                    rsequenceno = mediametadatainfosarray.get(i).getRsequenceno();
                    sequencehash = mediametadatainfosarray.get(i).getSequencehash();
                    sequenceno = mediametadatainfosarray.get(i).getSequenceno();
                    serverdate = mediametadatainfosarray.get(i).getServerdate();
                    sequencedevicedate = mediametadatainfosarray.get(i).getSequencedevicedate();

                    if(logString.isEmpty())
                    {
                        logString="A";
                        Log.e("Pickedsequenceno ",""+sequenceno);
                    }



                    try {

                        JSONArray jsonArray = new JSONArray(metricdata);
                        JSONObject mainobject = new JSONObject();

                        org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Iterator<String> myIter = jsonArray.getJSONObject(0).keys();
                            while (myIter.hasNext()) {
                                String key = myIter.next();
                                String value = jsonArray.getJSONObject(0).optString(key);
                                obj.put(key, value);
                            }
                        }

                        StringWriter out = new StringWriter();
                        obj.writeJSONString(out);

                        String jsontext = out.toString();
                        String dictionaryhashvalue = md5.calculatestringtomd5(jsontext);

                        mainobject.put("sequencedevicedate", "" + sequencedevicedate);
                        mainobject.put("sequenceno", sequenceno);
                        mainobject.put("sequencehashmethod", "" + hashmethod);
                        mainobject.put("dictionaryhashmethod", "" + hashmethod);
                        mainobject.put("dictionaryhashvalue", dictionaryhashvalue);
                        mainobject.put("recorddate", "" + recordate);
                        mainobject.put("dictionary", jsontext);
                        mainobject.put("sequencehashvalue", sequencehash);
                        array.put(mainobject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(mediametadatainfosarray.size() > 40)
                    sidecarupdatorinterval=20;
                else if(mediametadatainfosarray.size() > 30)
                    sidecarupdatorinterval=15;
                else if(mediametadatainfosarray.size() > 20)
                    sidecarupdatorinterval=12;
                else if(mediametadatainfosarray.size() > 10)
                    sidecarupdatorinterval=10;
                else if(mediametadatainfosarray.size() > 5)
                    sidecarupdatorinterval=8;
                else
                    sidecarupdatorinterval=4;


                // Block for calling media update api
                final String finalselectedid = selectedid;
                mpairslist.put("html", "0");
                mpairslist.put("key", "" + finalvideokey);
                mpairslist.put("devicetimeoffset", "" + finaldevicetimeoffset);
                mpairslist.put("apirequestdevicedate", "" + finalapirequestdevicedate);
                mpairslist.put("sequencelist", array);

                String actiontype = "";
                if (type.equalsIgnoreCase("video")) {
                    actiontype = config.type_video_update;
                    mpairslist.put("videotoken", finaltoken);
                    mpairslist.put("videoupdatedevicedate", "" + videoupdatedevicedate);
                } else if (type.equalsIgnoreCase("audio")) {
                    actiontype = config.type_audio_update;
                    mpairslist.put("audiotoken", finaltoken);
                    mpairslist.put("audioupdatedevicedate", "" + videoupdatedevicedate);
                } else if (type.equalsIgnoreCase("image")) {
                    actiontype = config.type_image_update;
                    mpairslist.put("imagetoken", finaltoken);
                    mpairslist.put("imageupdatedevicedate", "" + videoupdatedevicedate);
                }

                // api calling for video_update or audio_update

                //**************************************************************************final String finalDictionaryhashvalue = dictionaryhashvalue;

                final String finalDictionaryhashvalue = "";
                xapipost_sendjson(locationawareactivity.this, actiontype, mpairslist, new apiresponselistener() {
                    @Override
                    public void onResponse(taskresult response) {
                        if (response.isSuccess()) {
                            try {

                                JSONObject object = (JSONObject) response.getData();
                                JSONObject jsonObject = object.getJSONObject("sequences");
                                String sequenceid = "",serverdate="",color = "", latency = "", mediaframetransactionid = "",
                                        serverdictionaryhash = "", sequencekey = "",mediahashvalue="",colorreason="";

                                Iterator itr = jsonObject.keys();
                                String logString="";
                                while (itr.hasNext()) {
                                    sequencekey = (String) itr.next();
                                    JSONObject jobject = jsonObject.getJSONObject(sequencekey);

                                    //  get id from  issue
                                    sequenceid = jobject.getString("sequenceid");

                                    if(logString.isEmpty())
                                    {
                                        logString="A";
                                        Log.e("Gotsequenceno ",""+sequencekey);
                                    }

                                    if (jobject.has("videoframetransactionid"))
                                        mediaframetransactionid = jobject.getString("videoframetransactionid");

                                    if (jobject.has("audioframetransactionid"))
                                        mediaframetransactionid = jobject.getString("audioframetransactionid");

                                    if (jobject.has("imageframetransactionid"))
                                        mediaframetransactionid = jobject.getString("imageframetransactionid");

                                    if (jobject.has("color"))
                                        color = jobject.getString("color");

                                    if (jobject.has("colorreason"))
                                        colorreason = jobject.getString("colorreason");

                                    if (jobject.has("latency"))
                                        latency = jobject.getString("latency");

                                    if (jobject.has("serverdictionaryhash"))
                                        serverdictionaryhash = jobject.getString("serverdictionaryhash");

                                    if (jobject.has("serverdate"))
                                        serverdate = object.getString("serverdate");

                                    if (jobject.has("videoframehashvalue"))
                                        mediahashvalue = jobject.getString("videoframehashvalue");

                                    if (jobject.has("audioframehashvalue"))
                                        mediahashvalue = jobject.getString("audioframehashvalue");

                                    if (jobject.has("imageframehashvalue"))
                                        mediahashvalue = jobject.getString("imageframehashvalue");

                                    updatevideoupdateapiresponse(finalselectedid, sequencekey, serverdate, serverdictionaryhash,
                                            sequenceid, mediaframetransactionid, color, latency,mediahashvalue,colorreason);

                                }



                                updatevideoupdateapiresponse(finallocalkey);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            else
            {
                return;
            }
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callvideocompletedapi(String startselectedid, String finalheader, String finallocalkey, String finalapirequestdevicedate,
                                      String finalvideokey, String finaldevicetimeoffset, String finaltoken,
                                      String finalvideocompletedevicedate, String finalsync, String type) {

        HashMap<String, Object> mpairslist = new HashMap<String, Object>();
        final String localkey = finallocalkey;
        final String header = finalheader, sync = finalsync;
        String synchdefaultversion = "1", synchstatus = "completed", synchcompletedate = "", synchlastsequence = "";

        String framecount = "", videocompletedevicedate = "", videoduration = "", hassync = "", synccurrentdate = "";

        String currentdate[] = common.getcurrentdatewithtimezone();
        String Lastdate = currentdate[0];

        try {

            JSONObject obj = new JSONObject(sync);
            JSONObject objheader = new JSONObject(header);

            framecount = objheader.getString("framecounts");
            videoduration = objheader.getString("duration");

            synccurrentdate = obj.getString("firstdate");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("version", synchdefaultversion);
        map.put("firstdate", synccurrentdate);
        map.put("lastdate", Lastdate);
        map.put("lastsequence", framecount);
        map.put("status", synchstatus);
        map.put("completedate", finalvideocompletedevicedate);

        Gson gson = new Gson();
        String json = gson.toJson(map);
        Log.e("json", "" + json);

        updatedatasync(json, startselectedid);

        mpairslist.put("html", "0");
        mpairslist.put("key", "" + finalvideokey);
        mpairslist.put("devicetimeoffset", "" + finaldevicetimeoffset);
        mpairslist.put("apirequestdevicedate", "" + finalapirequestdevicedate);
        mpairslist.put("framecount", framecount);

        String actiontype = "";
        if (type.equalsIgnoreCase("video")) {
            actiontype = config.type_video_complete;
            mpairslist.put("videocompletedevicedate", "" + finalvideocompletedevicedate);
            mpairslist.put("videotoken", finaltoken);
            mpairslist.put("videoduration", videoduration);
        } else if (type.equalsIgnoreCase("audio")) {
            actiontype = config.type_audio_complete;
            mpairslist.put("audiocompletedevicedate", "" + finalvideocompletedevicedate);
            mpairslist.put("audiotoken", finaltoken);
            mpairslist.put("audioduration", videoduration);
        } else if (type.equalsIgnoreCase("image")) {
            actiontype = config.type_image_complete;
            mpairslist.put("imagecompletedevicedate", "" + finalvideocompletedevicedate);
            mpairslist.put("imagetoken", finaltoken);
            mpairslist.put("imageduration", videoduration);
        }

        sidecarupdatorinterval=4;
        xapipost_sendjson(locationawareactivity.this, actiontype, mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if (response.isSuccess()) {
                    try {

                        JSONObject object = (JSONObject) response.getData();
                        String valuehash = "", color = "",mediaid="",colorreason="";

                        if (object.has("hashvalue"))
                            valuehash = object.getString("hashvalue");

                        if (object.has("color"))
                            color = object.getString("color");

                        if (object.has("colorreason"))
                            colorreason = object.getString("colorreason");

                        if (object.has("imageid"))
                            mediaid = object.getString("imageid");

                        if (object.has("audioid"))
                            mediaid = object.getString("audioid");

                        if (object.has("videoid"))
                            mediaid = object.getString("videoid");

                        updatecompletehashvalue(localkey, valuehash);
                        updatedatasyncdate(localkey, common.getCurrentDate(), config.sync_complete, color,mediaid,colorreason);

                        sendbroadcastreader();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*if(isappinbackground  && xdata.getinstance().getSetting(config.selectedsyncsetting).
                            equalsIgnoreCase(config.selectedsyncsetting_0))
                        common.shownotification(applicationviavideocomposer.getactivity());*/
                }
            }
        });
    }

    //* Broadcast for notify medialist controller to get data from database
    public void sendbroadcastreader() {
        Intent i = new Intent(config.composer_service_getencryptionmetadata);
        sendBroadcast(i);
    }

    public void updatemediaattempt(String location, int totalattempt, String status) {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatemediaattempt(location, "" + totalattempt, status);
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatedatasync(String sync, String selectedid) {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatesyncvalue(sync, selectedid);
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatedatasyncdate(String localkey, String syncdate, String syncstatus, String color,String mediaid,String colorreason) {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideosyncdate(localkey, syncdate, syncstatus, color,mediaid,colorreason);
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updatecompletehashvalue(String localkey, String valuehash) {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatecompletehashvalue(localkey, valuehash);
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatevideokeytoken(String videoid, String videokey, String tokan, String transactionid) {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideokeytoken(videoid, videokey, tokan, transactionid);
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatevideoupdateapiresponse(String selectedid, String sequence, String serverdate, String serverdictionaryhash,
                                             String sequenceid, String videoframetransactionid, String color, String latency,
                                             String mediahashvalue,String colorreason) {

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideoupdateapiresponse(selectedid, sequence, serverdate,
                    serverdictionaryhash, sequenceid, videoframetransactionid, color, latency, mediahashvalue,colorreason);
            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //**
    public void updatevideoupdateapiresponse(String finallocalkey) {

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(locationawareactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Cursor cursorcomplete = mdbhelper.fetchmetacompletedata(1,finallocalkey);
            if(cursorcomplete !=null ){
                int count = cursorcomplete.getCount();
                /*for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (xdata.getinstance().getSetting(config.sidecar_xapi_complete + i).isEmpty()) {
                        xdata.getinstance().saveSetting(config.sidecar_xapi_complete + i, ""+count);
                        break;
                    }
                }*/
                xdata.getinstance().saveSetting(config.frame_complete,""+count);
                Log.e("countcomplete",""+count);
            }

            Cursor cursor = mdbhelper.fetchmetacompletedata(0,finallocalkey);
            if(cursor!=null ){
                int count = cursor.getCount();
                xdata.getinstance().saveSetting(config.frame_completeness,""+count);
                /*for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (xdata.getinstance().getSetting(config.sidecar_xapi_completeless + i).isEmpty()) {
                        xdata.getinstance().saveSetting(config.sidecar_xapi_completeless + i, ""+ count);
                        break;
                    }
                }*/
                Log.e("countuncomplete",""+count);
            }

            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //** end code of media composer sync process
    public void getwifinetworks(){
        if(wifiReceiver == null)
        {
            wifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String availablewifiname = "";
                    if((xdata.getinstance().getSetting("wificonnected").equalsIgnoreCase("0"))){
                        xdata.getinstance().saveSetting(config.availablewifis, "");
                    }
                    else{
                        results = wifiManager.getScanResults();
                        //  unregisterReceiver(this);

                        for (ScanResult scanResult : results) {

                            if (availablewifiname.isEmpty()) {
                                availablewifiname = "" + scanResult.SSID;
                            } else {
                                availablewifiname = availablewifiname + ", " + scanResult.SSID;
                            }
                        }
                        //  availablewifinetwork = availablewifiname;
                        xdata.getinstance().saveSetting(config.availablewifis, availablewifiname);
                    }
                }
            };
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
        wifiManager.startScan();
    }
}
