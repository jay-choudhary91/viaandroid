package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.interfaces.progressupdate;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.noise;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

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

    public void showvideorecordlengthalert(String title,String message,String gravity)
    {
        baseactivity.getinstance().showinapppurchasepopup(applicationviavideocomposer.
                getactivity(), title, message, new adapteritemclick(){
            @Override
            public void onItemClicked(Object object) {
                gethelper().inapppurchase(object.toString());
            }
            @Override
            public void onItemClicked(Object object, int type) {

            }
        },gravity);
    }

    public View getmediaseekbarbackgroundview(String weight,String color)
    {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT,Float.parseFloat(weight));
        View view = new View(applicationviavideocomposer.getactivity());
        view.setLayoutParams(param);
        if((! color.isEmpty()))
        {
            view.setBackgroundColor(Color.parseColor(common.getcolorbystring(color)));
        }
        else
        {
            view.setBackgroundColor(Color.parseColor(config.color_code_white_transparent));
        }
        return view;
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
        public void xapi_uploadfile(Context mContext,String serverurl,String filepath,apiresponselistener mListener,progressupdate mprogressupdate);
        public void setsoundwaveinformation(int ampletudevalue ,int decibelvalue,boolean issoundwaveshow);
        //public void showinapppurchasepopup(final Context activity,final adapteritemclick mitemclick);
        public void showsharepopupsub(final String path, final String type, final String mediatoken,boolean ismediatrimmed);
        public void inapppurchase(String productid);
        public void finishactivity();
        public basefragment getcurrentfragment();

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

    private void start() {

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

    public void settextviewcolor(TextView view, int color){
        view.setTextColor(color);
    }

    public void setframeborder(View view, String bordercolor,String viewcolor){
        GradientDrawable gd = (GradientDrawable) view.getBackground().getCurrent();
        gd.setColor(Color.parseColor(viewcolor));
        gd.setCornerRadius(30);
        gd.setStroke(15, Color.parseColor(bordercolor), 0, 0);
    }
}
