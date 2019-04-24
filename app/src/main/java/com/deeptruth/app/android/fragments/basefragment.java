package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.noise;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public abstract class basefragment extends Fragment {

    private fragmentnavigationhelper fragmenthelper;
    private SharedPreferences prefs;
    private View view;
    private TelephonyManager telephonymanager;
    public static final int my_permission_read_phone_state = 90;
    private static final int permission_location_request_code = 91;
    int gps_request_code =111;
    noise noise;
    LocationManager manager;
    boolean isdraweropen=false;
    private static final int PERMISSION_RECORD_AUDIO= 92;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        telephonymanager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmenthelper = (fragmentnavigationhelper) activity;
        } catch (Exception e) {
        }

        prefs = activity.getSharedPreferences(config.prefs_name, Context.MODE_PRIVATE);
    }


    public void updateheader() {

    }

    public void setdraweropen(boolean isdraweropen)
    {
        this.isdraweropen=isdraweropen;
    }

    public boolean isdraweropened()
    {
        return isdraweropen;
    }

    public void oncurrentlocationchanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1)
        {
            Log.e("Permissions","Allow or Revoke");
        }
    }

    public static boolean iscamerapermissionenabled(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isaudiopermissionenabled(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getlayoutid(), container, false);
        view.setClickable(true);
         manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE );
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initviews(view, savedInstanceState);

    }

    public fragmentnavigationhelper gethelper() {
        return this.fragmenthelper;
    }

    public View getparentview() {
        return this.view;
    }

    public abstract int getlayoutid();


    public void initviews(View parent, Bundle savedInstanceState) {

    }

    public SharedPreferences getSharedPreferences() {
        return prefs;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hidekeyboard();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public void hidekeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hidekeyboard(EditText input) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }


    public static boolean checkLocationEnable(Context context) {
        //check the Gps settings
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public View findViewById(int id) {
        return getparentview().findViewById(id);
    }

    /**
     * An interface to load and make navigation. The parent mActivity must provide an implementation for this interface.
     *
     * @author khawarraza
     */
    public interface fragmentnavigationhelper {

        public void addFragment(basefragment f, boolean clearBackStack, boolean addToBackstack);
        public void addFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack);
        public void replaceFragment(basefragment f, boolean clearBackStack, boolean addToBackstack);
        public void replaceFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack);
        public void replacetabfragment(basefragment f,  boolean clearBackStack, boolean addToBackstack );
        public void onBack();
        public void switchtomedialist();
        public void updateheader(String txt);
        public void setdatacomposing(boolean isdatacomposing);
        public void setdatacomposing(boolean isdatacomposing,String mediafilepath);
        public void setcurrentmediaposition(int currentmediaposition);
        public void setisrecordrunning(boolean isrecodrunning);
        public void updateactionbar(int showHide, int color);
        public void updateactionbar(int showHide);
        public void updatezoomlevel(double latitude, double longitude);
        public void showPermissionDialog();
        public void setdraweropen(boolean isdraweropen);
        public boolean isdraweropened();
        public void registerAccelerometerSensor();
        public void registerMobileNetworkStrength();
        public void registerCompassSensor();
        public void registerBarometerSensor();
        public void getsystemusage();
        public void registerUsageSystem();
        public void registerUsageIow();
        public void registerUsageIrq();
        public void getCallInfo();
        public void setdrawerheightonfullscreen(int drawerheight);
        public void getairplanemodeon();
        public void drawerenabledisable(boolean enable);
        public void hidedrawerbutton(boolean enable);
        public void setwindowfitxy(boolean isfullscreen);
        public boolean isuserlogin();
        public void redirecttologin();
        public ArrayList<metricmodel> getmetricarraylist();
        public void setrecordingrunning(boolean toggle);
        public void xapi_send(Context mContext, HashMap<String, String> mPairList, apiresponselistener mListener);
        public void xapipost_send(Context mContext, HashMap<String, String> mPairList, apiresponselistener mListener);
        public void xapipost_sendjson(Context mContext, String Action, HashMap<String,Object> mPairList, apiresponselistener mListener);
        public void showsharepopupsub(String path,String type);

    }

    public void updatewifigpsstatus()
    {

    }
    public void onHeaderBtnClick(int btnid) {

    }

    public void showhideviewondrawer(boolean isshow) {

    }


    public void setmetriceslistitems(ArrayList<metricmodel> mitems) {

    }

    public void updateMobileNetworkStrength(String result) {

    }

    public void updateDecibelValue(String result) {

    }

    public void updateCallDecibelValue(String result) {

    }

    public void updateCompassDegree(String result) {

    }

    public void updateBarometerSensorData(String result) {

    }

    public void onUpdateAccelerometerValue(float deltaX,float deltaY,float deltaZ) {

    }

    public void getAccurateLocation()
    {

    }

    public void updateUsageUser(String value)
    {

    }

    public void updateUsageSystem(String value)
    {

    }

    public void updateUsageIow(String value)
    {

    }

    public void updateUsageIrq(String value)
    {

    }

    public void updateCallInfo(String callStaus,String callDuration,String CallerNumber)
    {

    }
    public void updateHeader() {

    }

    public void updateairplanemode(String result){

    }

    public void dismissprogress()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //progressdialog.dismisswaitdialog();
            }
        });
    }

    public void changefocusstyle(View view, int fullbordercolor, int fullbackcolor, float borderradius)
    {
       /* view.setBackgroundResource(R.drawable.style_rounded_view);
        GradientDrawable drawable = (GradientDrawable)view.getBackground();
        drawable.setStroke(2, fullbordercolor);
        drawable.setCornerRadius(borderradius);
        drawable.setColor(fullbackcolor);*/
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    public boolean metric_onoff(String key)
    {

        if(key.equalsIgnoreCase("gpslatitude"))
        {

            gethelper().showPermissionDialog();
            return false;
        }
        else if(key.equalsIgnoreCase("decibel") || key.equalsIgnoreCase("currentcalldecibel")){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_RECORD_AUDIO);
            }else{
                start();
            }
            return false;
        }
        else
        {
            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        my_permission_read_phone_state);

                return false;
            }
        }

        if(key.equalsIgnoreCase("acceleration.x") || key.equalsIgnoreCase("acceleration.y") ||
                key.equalsIgnoreCase("acceleration.z"))
        {
            gethelper().registerAccelerometerSensor();
            return false;
        }


        if(key.equalsIgnoreCase("compass")){

            SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
                gethelper().registerCompassSensor();
                return false;
            }else{
                return true;
            }
        }

        if(key.equalsIgnoreCase("barometer")){
            SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
                gethelper().registerBarometerSensor();
                return false;

            }else{
                return true;
            }
        }

        if(key.equalsIgnoreCase("connectedphonenetworkquality"))
        {
            String str = telephonymanager.getNetworkOperatorName();
            if(str != null)
            {
                if(! str.trim().isEmpty())
                {
                    gethelper().registerMobileNetworkStrength();
                    return false;
                }
            }
        }

        if(key.equalsIgnoreCase("cpuusageuser") )
        {
            gethelper().getsystemusage();
            return false;
        }

        if(key.equalsIgnoreCase("cpuusagesystem"))
        {
            gethelper().registerUsageSystem();
            return false;
        }

        if(key.equalsIgnoreCase("cpuusageiow"))
        {
            gethelper().registerUsageIow();
            return false;
        }

        if(key.equalsIgnoreCase("cpuusageirq"))
        {
            gethelper().registerUsageIrq();
            return false;
        }

        if(key.equalsIgnoreCase("currentcallinprogress") || key.equalsIgnoreCase("currentcallremotenumber")
                || key.equalsIgnoreCase("currentcalldurationseconds"))
        {
            gethelper().getCallInfo();
            return false;
        }

        return true;
    }
    @SuppressLint("MissingPermission")
    public String metric_read(String key)
    {
        String metricItemValue="";
        if(key.equalsIgnoreCase("imeinumber"))
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
            metricItemValue = Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
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
                int brightnessValue = Settings.System.getInt(getActivity().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);

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
                    (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
                    (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
            metricItemValue="NA";
            ConnectivityManager cm =
                    (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected)
            {
                boolean TYPE_MOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                boolean TYPE_WIFI = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

                if(TYPE_MOBILE && key.equalsIgnoreCase("networktype")){
                    metricItemValue= common.mapNetworkTypeToName(getActivity());
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
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {

                    if(key.equalsIgnoreCase("wifinetworksaveailable") || key.equalsIgnoreCase("wificonnect"))
                        metricItemValue = "YES";

                    if(key.equalsIgnoreCase("externalip"))
                        metricItemValue =  common.getWifiIPAddress(getActivity());

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
            Intent batteryStatus = getActivity().registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            float batteryPct = level / (float) scale;

            int percentage= (int) (batteryPct * 100);
            metricItemValue=""+percentage+"%";
        }
        else if(key.equalsIgnoreCase("gpslatitude")|| key.equalsIgnoreCase("gpslongitude") || key.equalsIgnoreCase("gpsaltittude") ||
                key.equalsIgnoreCase("gpsverticalaccuracy")
                || key.equalsIgnoreCase("gpsquality") || key.equalsIgnoreCase("heading")
                || key.equalsIgnoreCase("speed"))
        {
            metricItemValue="UpdateLater";
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
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamMemorySize = mi.totalMem;


            metricItemValue  = common.getInternalMemory(totalRamMemorySize);

        }else if(key.equalsIgnoreCase("usedram"))
        {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager)  getActivity().getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long totalRamFreeSize = mi.availMem;
            long totalRamMemorySize = mi.totalMem;
            long usedrammemorysize = totalRamMemorySize - totalRamFreeSize;

            metricItemValue  = common.getInternalMemory(usedrammemorysize);
        }else if(key.equalsIgnoreCase("freeram"))
        {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
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
            metricItemValue = common.isChargerConnected(getActivity())== true ? "True" : "False";
        }else if(key.equalsIgnoreCase("headphonesattached"))
        {
            metricItemValue = common.isHeadsetOn(getActivity())== true ? "True" : "False";
        }else if(key.equalsIgnoreCase("deviceorientation"))
        {
            metricItemValue = common.getOriantation(getActivity());
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
            if(common.isChargerConnected(getActivity())== true || common.isHeadsetOn(getActivity())== true ){
                metricItemValue = "true";
            }else{
                metricItemValue = "False";
            }
        }else if(key.equalsIgnoreCase("attachedaccessoriescount"))
        {
            if(common.isChargerConnected(getActivity())== true && common.isHeadsetOn(getActivity())== true ){
                metricItemValue = "2";
            }else if(common.isChargerConnected(getActivity())== true || common.isHeadsetOn(getActivity())== true ){
                metricItemValue = "1";
            }else{
                metricItemValue = "NA";
            }

        }else if(key.equalsIgnoreCase("nameattachedaccessories"))
        {
            if(common.isChargerConnected(getActivity())== true && common.isHeadsetOn(getActivity())== true ){

                metricItemValue = ("Charger"+","+"headphone");

            }else if(common.isChargerConnected(getActivity())== true){
                metricItemValue = "Charger";
            }else if(common.isHeadsetOn(getActivity())== true){
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
                AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                int STREAM_VOICE_CALL = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                metricItemValue=""+STREAM_VOICE_CALL;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        else if(key.equalsIgnoreCase("devicetime")){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
            String time = sdf.format(c.getTime());
            metricItemValue=time;
        }
        else if(key.equalsIgnoreCase("airplanemode")){
            gethelper().getairplanemodeon();
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
            if(android.provider.Settings.Global.getInt(getActivity().getContentResolver(), Settings.Global.AUTO_TIME, 0)==1) {
                metricItemValue = "ON";
            } else if((android.provider.Settings.Global.getInt(getActivity().getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0)==1)){
                metricItemValue="OFF";
            }
        }

        if(metricItemValue == null)
            metricItemValue="";

        return metricItemValue;
    }


    private void start() {

        //Log.i("noise", "==== start ===");



        try {

            if(noise != null)
                noise.stop();

            noise = new noise();

            if(noise != null)
            {
                if(noise != null)
                    noise.start();
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
                    if(noise != null)
                    {
                        double amp = noise.getAmplitude();
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
            if(noise != null)
            {
                noise.stop();
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
                updateDecibelValue(deciblevalue);
                updateCallDecibelValue(deciblevalue);
                stop();
            }
        });
    }
}
