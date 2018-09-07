package com.cryptoserver.composer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.fragmentsettings;
import com.cryptoserver.composer.fragments.videoplayercomposerfragment;
import com.cryptoserver.composer.fragments.videoplayerreaderfragment;
import com.cryptoserver.composer.interfaces.ApiResponseListener;
import com.cryptoserver.composer.netutils.xapi;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.xdata;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public abstract class baseactivity extends AppCompatActivity implements basefragment.fragmentnavigationhelper {

    public static baseactivity instance;
    public boolean isapprunning = false;
    private basefragment mcurrentfragment;
    private SharedPreferences prefs;
    private Stack<Fragment> mfragments = new Stack<Fragment>();
    private static final int permission_location_request_code = 91;
    private SensorManager mSensorManager;
    private Sensor mAccelereometer;
    private BroadcastReceiver flightmodebroadcast ;
    IntentFilter filter;

    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;
    private float mCurrentDegree = 0f;
    TelephonyManager mTelephonyManager;
    public boolean isisapprunning() {
        return isapprunning;
    }

    public static baseactivity getinstance() {
        return instance;
    }
    public void isapprunning(boolean b) {
        isapprunning = b;
    }
    private IntentFilter intentFilter;
    private BroadcastReceiver mBroadcast;
    String CALL_STATUS="",CALL_DURATION="",CALL_REMOTE_NUMBER="",CALL_START_TIME="";
    MyPhoneStateListener mPhoneStatelistener;
    int mSignalStrength = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(config.prefs_name, Context.MODE_PRIVATE);

        isapprunning = true;
        instance = this;

        LayoutInflater inflater = getLayoutInflater();
        View contentview = inflater.inflate(getlayoutid(), null);
        setContentView(contentview);

        initviews(savedInstanceState);

    }

    public basefragment getcurrentfragment() {
        return mcurrentfragment;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(getcurrentfragment() instanceof videoplayercomposerfragment){

            ((videoplayercomposerfragment) getcurrentfragment()).onRestart();
        }
        else if(getcurrentfragment() instanceof videoplayerreaderfragment){

            ((videoplayerreaderfragment) getcurrentfragment()).onRestart();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            progressdialog.dismisswaitdialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void initviews(Bundle savedInstanceState) {

    }
    public abstract int getlayoutid();

    @Override
    public void addFragment(basefragment f, boolean clearBackStack, boolean addToBackstack) {
        addFragment(f, R.id.fragment_container, clearBackStack, addToBackstack);
    }

    public void addFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

        mcurrentfragment = f;
        mfragments.push(f);

        onfragmentbackstackchanged();
    }

    @Override
    public void replaceFragment(basefragment f, boolean clearBackStack, boolean addToBackstack) {
        replaceFragment(f, R.id.fragment_container, clearBackStack, addToBackstack);
    }

    public void replaceFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      // transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.replace(layoutId, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        mcurrentfragment = f;
        mfragments.push(f);

        onfragmentbackstackchanged();
    }

    public int minnumberoffragments = 1;

    public int getMinNumberOfFragments() {
        return minnumberoffragments;
    }

    @Override
    public void onBack() {

        int a=getSupportFragmentManager().getBackStackEntryCount();
        int b=getMinNumberOfFragments();

       /* if(getcurrentfragment() instanceof videoplayfragment){

            boolean finishapp = xdata.getinstance().getboolean("AppFinish");

            if(finishapp){
                finish();
                return;
            }
        }
*/
        if (getSupportFragmentManager().getBackStackEntryCount() <= getMinNumberOfFragments()) {
            finish();
            return;
        }

        getSupportFragmentManager().popBackStack();
        if(mfragments != null && (! mfragments.isEmpty()))
        {
            mfragments.pop();
            mcurrentfragment = (basefragment) (mfragments.isEmpty() ? null : ((mfragments.peek() instanceof basefragment) ? mfragments.peek() : null));

            onfragmentbackstackchanged();
        }
    }

    public void clearfragmentbackstack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount()-getMinNumberOfFragments(); i++) {
            fm.popBackStack();
        }

        if (!mfragments.isEmpty()) {
            Fragment homefragment = mfragments.get(0);
            mcurrentfragment = (basefragment) homefragment;
            mfragments.clear();
            mfragments.push(homefragment);
        }
    }

    public void onfragmentbackstackchanged() {
        if (mcurrentfragment != null) {
            mcurrentfragment.updateheader();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permission_location_request_code) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            }
            return;
        }
//        if (getcurrentfragment() != null) {
//            getcurrentfragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {

            if (getSupportFragmentManager().getBackStackEntryCount() <= getMinNumberOfFragments()) {
                finish();
                return true;
            } else {
                onBack();
                return true;
            }
        }
        return super.onKeyDown(keycode, event);
    }


    public void hidekeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showkeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public SharedPreferences getPrefs() {
        return prefs;
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
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getinstance().isapprunning(false);
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

                        getcurrentfragment().onUpdateAccelerometerValue(deltaX,deltaY,deltaZ);

                        if(mSensorManager != null)
                            mSensorManager.unregisterListener(mAccelerometerListener);
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
                        getcurrentfragment().updateBarometerSensorData(data);

                    if(mSensorManager != null)
                        mSensorManager.unregisterListener(mBarometerListener);
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


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getcurrentfragment() != null)
                        {
                            value1[0] = value1[0].replace("User","");
                            getcurrentfragment().updateUsageUser(value1[0]);
                        }
                    }
                });

            }
        };
        thread.start();
    }

    @Override
    public void registerUsageSystem() {

        Thread thread = new Thread(){
            public void run(){
                String system = common.executeTop();
                String[] cpuArray = system.split(",");
                final String[] value2 = {cpuArray[1]};

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getcurrentfragment() != null)
                        {
                            value2[0] = value2[0].replace("System","");
                            getcurrentfragment().updateUsageSystem(value2[0]);
                        }
                    }
                });

            }
        };
        thread.start();
    }

    @Override
    public void registerUsageIow() {

        Thread thread = new Thread(){
            public void run(){
                String system = common.executeTop();
                String[] cpuArray = system.split(",");
                final String[] value3 = {cpuArray[2]};

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getcurrentfragment() != null)
                        {
                            value3[0] = value3[0].replace("IOW","");
                            getcurrentfragment().updateUsageIow(value3[0]);
                        }
                    }
                });

            }
        };
        thread.start();
    }

    @Override
    public void registerUsageIrq() {

        Thread thread = new Thread(){
            public void run(){
                String system = common.executeTop();
                String[] cpuArray = system.split(",");
                final String[] value4 = {cpuArray[3]};

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getcurrentfragment() != null)
                        {
                            value4[0] = value4[0].replace("IRQ","");
                            getcurrentfragment().updateUsageIrq(value4[0]);
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
                getcurrentfragment().updateMobileNetworkStrength(result);
        }
    }

    @Override
    public void registerMobileNetworkStrength() {
        mPhoneStatelistener = new MyPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

  @Override
    public void getairplanemodeon() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                registerReceiver(flightmodebroadcast,filter);

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
                            getcurrentfragment().updateCompassDegree(compassValue);

                            if (mSensorManager != null)
                                mSensorManager.unregisterListener(mCompassListener);
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

            if(getcurrentfragment() instanceof fragmentsettings)
                getcurrentfragment().updateCallInfo(CALL_STATUS,CALL_DURATION,CALL_REMOTE_NUMBER);

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
                    getcurrentfragment().updateairplanemode(turn);
                }
            }
        };
    }

    @Override
    public void xapi_send(Context mContext, String Action, HashMap<String,String> mPairList, ApiResponseListener mListener) {
        xapi api = new xapi(mContext,Action,mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            String argvalue = (String)mPairList.get(key);
            api.add(key,argvalue);
        }
        api.execute();
    }


}
