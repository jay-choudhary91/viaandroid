package com.cryptoserver.composer.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.cryptoserver.composer.R;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.fragments.audiocomposerfragment;
import com.cryptoserver.composer.fragments.audioreaderfragment;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.imagecomposerfragment;
import com.cryptoserver.composer.fragments.imagereaderfragment;
import com.cryptoserver.composer.fragments.readermedialist;
import com.cryptoserver.composer.fragments.videocomposerfragment;
import com.cryptoserver.composer.fragments.videoreaderfragment;
import com.cryptoserver.composer.interfaces.apiresponselistener;
import com.cryptoserver.composer.models.mediametadatainfo;
import com.cryptoserver.composer.models.startmediainfo;
import com.cryptoserver.composer.netutils.connectivityreceiver;
import com.cryptoserver.composer.netutils.xapi;
import com.cryptoserver.composer.netutils.xapipost;
import com.cryptoserver.composer.netutils.xapipostjson;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.taskresult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public abstract class baseactivity extends AppCompatActivity implements basefragment.fragmentnavigationhelper,
        connectivityreceiver.ConnectivityReceiverListener{
    public static baseactivity instance;
    public boolean isapprunning = false;
    private basefragment mcurrentfragment;
    private SharedPreferences prefs;
    private Stack<Fragment> mfragments = new Stack<Fragment>();
    private static final int permission_location_request_code = 91;
    public boolean isisapprunning() {
        return isapprunning;
    }

    public static baseactivity getinstance() {
        return instance;
    }
    public void isapprunning(boolean b) {
        isapprunning = b;
    }

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

    @Override
    public void onnetworkconnectionchanged(boolean isconnected) {
        if(isconnected)
        {

        }
    }

    public basefragment getcurrentfragment() {
        return mcurrentfragment;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(getcurrentfragment() instanceof videoreaderfragment){

            ((videoreaderfragment) getcurrentfragment()).onRestart();
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
    @Override
    public void replacetabfragment(basefragment f, boolean clearBackStack, boolean addToBackstack) {
        replacetabfragment(f, R.id.tab_container, clearBackStack, addToBackstack);
    }

    public void replacetabfragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack) {
        if (clearBackStack) {
            clearfragmentbackstack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutId, f);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        // Placed after crash on 2018-11-16
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            // Do fragment's transaction commit
            transaction.commit();
        }

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

        if (getSupportFragmentManager().getBackStackEntryCount() <= getMinNumberOfFragments()) {
            finish();
            return;
        }


        if(getcurrentfragment() instanceof readermedialist){
            finish();
            return;
        }


        if(getcurrentfragment() instanceof videocomposerfragment || getcurrentfragment() instanceof audiocomposerfragment
                || getcurrentfragment() instanceof imagecomposerfragment)
        {
            setbackpress();
            onBack();
        }

        else if(getcurrentfragment() instanceof audioreaderfragment
                || getcurrentfragment() instanceof videoreaderfragment || getcurrentfragment() instanceof imagereaderfragment)
        {
            setbackpress();

        }
        else
        {
            setbackpress();
        }
    }

    public void setbackpress()
    {
        getSupportFragmentManager().popBackStack();
        if(mfragments.size() > 0)
        {
            mfragments.pop();
            mcurrentfragment = (basefragment) (mfragments.isEmpty() ? null : ((mfragments.peek()
                    instanceof basefragment) ? mfragments.peek() : null));
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
    protected void onPause() {
        super.onPause();
        getinstance().isapprunning(false);
    }


    @Override
    public void xapi_send(Context mContext, String Action, HashMap<String,String> mPairList, apiresponselistener mListener) {
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

    @Override
    public void xapipost_send(Context mContext, String Action, HashMap<String,String> mPairList, apiresponselistener mListener) {
        xapipost api = new xapipost(mContext,Action,mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            String argvalue = (String)mPairList.get(key);
            api.add(key,argvalue);
        }
        api.execute();
    }

    @Override
    public void xapipost_sendjson(Context mContext, String Action, HashMap<String,Object> mPairList, apiresponselistener mListener) {
        xapipostjson api = new xapipostjson(mContext,Action,mListener);
        Set keys = mPairList.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            Object argvalue = mPairList.get(key);
            api.add(key,argvalue);
        }
        api.execute();
    }

}
