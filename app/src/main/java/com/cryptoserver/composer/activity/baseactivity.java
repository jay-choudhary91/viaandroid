package com.cryptoserver.composer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.videocomposerfragment;
import com.cryptoserver.composer.fragments.videoplayerreaderfragment;
import com.cryptoserver.composer.interfaces.apiresponselistener;
import com.cryptoserver.composer.models.startvideoinfo;
import com.cryptoserver.composer.models.videogroup;
import com.cryptoserver.composer.netutils.connectivityreceiver;
import com.cryptoserver.composer.netutils.xapi;
import com.cryptoserver.composer.netutils.xapipost;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.taskresult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private databasemanager mdbhelper;
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

        if(getcurrentfragment() instanceof videoplayerreaderfragment){

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
    protected void onPause() {
        super.onPause();
        getinstance().isapprunning(false);
    }

    public void fetchmetadatadb() {

        String selectedid="",videolist="",hashmethod="",hashvalue="",videoid="",hassync="";

        String header = "", type = "", location = "", localkey = "", token = "", videokey = "", sync = "",sync_date = "",action_type="";


        if(getcurrentfragment() instanceof videocomposerfragment)
        {
            boolean isrecording=((videocomposerfragment) getcurrentfragment()).isvideorecording();
            if(isrecording)
                return;
        }

        if(! common.isnetworkconnected(baseactivity.this))
            return;

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(baseactivity.this);
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        ArrayList<startvideoinfo> marray=new ArrayList<>();

        try {
            Cursor cur = mdbhelper.fatchstartvideoinfo();


            if (cur != null) {
                while (!cur.isAfterLast()) {

                    header = "" + cur.getString(cur.getColumnIndex("header"));
                    type = "" + cur.getString(cur.getColumnIndex("type"));
                    location = "" + cur.getString(cur.getColumnIndex("location"));
                    localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                    token = "" + cur.getString(cur.getColumnIndex("token"));
                    videokey = "" + cur.getString(cur.getColumnIndex("videokey"));
                    sync = "" + cur.getString(cur.getColumnIndex("sync"));
                    action_type = "" + cur.getString(cur.getColumnIndex("action_type"));
                    sync_date = ""+ cur.getString(cur.getColumnIndex("sync_date"));

                    marray.add(new startvideoinfo(header, type, location,localkey,token, videokey,sync, action_type,sync_date));
                    //  cur.moveToLast();

                    //  cur.moveToLast();
                    Log.e("Marray =", "" + marray);

                    break;
                }
            }

            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        /*ArrayList<videogroup> marray=new ArrayList<>();

        try {
            Cursor cur = mdbhelper.fetchmetadata();


            if (cur != null) {
                while (!cur.isAfterLast()) {

                    selectedid= "" + cur.getString(cur.getColumnIndex("id"));
                    videokey = "" + cur.getString(cur.getColumnIndex("videokey"));
                    videoid = "" + cur.getString(cur.getColumnIndex("videoid"));
                    hassync = "" + cur.getString(cur.getColumnIndex("hassync"));
                    videolist = "" + cur.getString(cur.getColumnIndex("videolist"));
                    hashmethod = "" + cur.getString(cur.getColumnIndex("hashmethod"));
                    hashvalue = "" + cur.getString(cur.getColumnIndex("hashvalue"));
                    action_type = "" + cur.getString(cur.getColumnIndex("action_type"));

                    marray.add(new videogroup(selectedid,videoid,hassync,videokey,action_type));
                  //  cur.moveToLast();
                    break;
                }
            }
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/
        Log.e("video_updateid ",""+selectedid);




        if(! selectedid.trim().isEmpty())
        {
            HashMap<String,String> mpairslist=new HashMap<String, String>();
            if(action_type.equalsIgnoreCase(config.type_video_start))
            {
                mpairslist.put("html","0");
                mpairslist.put("hashmethod",""+hashmethod);
                mpairslist.put("hashvalue",""+hashvalue);
                mpairslist.put("title","xx");
            }
            else if(action_type.equalsIgnoreCase(config.type_video_update))
            {
                mpairslist.put("key",""+videokey);
                mpairslist.put("html","0");
                mpairslist.put("updatelist",""+videolist);
            }
            else if(action_type.equalsIgnoreCase(config.type_video_complete))
            {
                String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                mpairslist.put("completedatetime",""+datetime);
                mpairslist.put("key",""+videokey);
            }


            final String finalSelectedid = selectedid;
            final String finalAction_type = action_type;
            final String finalVideoid = videoid;
            xapipost_send(baseactivity.this,action_type,mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response)
                {
                    if(response.isSuccess())
                    {
                        if(finalAction_type.equalsIgnoreCase(config.type_video_start))
                        {
                            try {
                                JSONObject object = (JSONObject) response.getData();
                                String videokey=object.getString("key");
                                updatevideokey(finalVideoid,videokey);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    if(finalAction_type.equalsIgnoreCase(config.type_video_update))
                    {

                    }
                    else if(finalAction_type.equalsIgnoreCase(config.type_video_complete))
                    {

                    }

                    updatedatasync(finalSelectedid);
                    fetchmetadatadb();
                }
            });
        }
    }


    public void fetchstartvideoinfo() {

        //String selectedid="",videokey="",videolist="",action_type="",hashmethod="",hashvalue="",videoid="",hassync="";
        String hashmethod = "" , hashvalue = "";

        String header = "", type = "", location = "", localkey = "", token = "", videokey = "", sync = "",sync_date = "",action_type="";

        if(getcurrentfragment() instanceof videocomposerfragment)
        {
            boolean isrecording=((videocomposerfragment) getcurrentfragment()).isvideorecording();
            if(isrecording)
                return;
        }

        if(! common.isnetworkconnected(baseactivity.this))
            return;

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(baseactivity.this);
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        ArrayList<startvideoinfo> marray=new ArrayList<>();

        try {
            Cursor cur = mdbhelper.fatchstartvideoinfo();


            if (cur != null) {
                while (!cur.isAfterLast()) {

                    header = "" + cur.getString(cur.getColumnIndex("header"));
                    type = "" + cur.getString(cur.getColumnIndex("type"));
                    location = "" + cur.getString(cur.getColumnIndex("location"));
                    localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                    token = "" + cur.getString(cur.getColumnIndex("token"));
                    videokey = "" + cur.getString(cur.getColumnIndex("videokey"));
                    sync = "" + cur.getString(cur.getColumnIndex("sync"));
                    action_type = "" + cur.getString(cur.getColumnIndex("action_type"));
                    sync_date = ""+ cur.getString(cur.getColumnIndex("sync_date"));

                     marray.add(new startvideoinfo(header, type, location,localkey,token, videokey,sync, action_type,sync_date));
                    //  cur.moveToLast();

                    //  cur.moveToLast();

                    break;
                }
            }

            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            JSONObject obj = new JSONObject(header);
             hashmethod  = obj.getString("hashmethod");
             hashvalue  = obj.getString("firsthash");




        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("video_updateid ",""+localkey);
        if(! localkey.trim().isEmpty())
        {
            HashMap<String,String> mpairslist=new HashMap<String, String>();
            if(action_type.equalsIgnoreCase(config.type_video_start))
            {
                mpairslist.put("html","0");
                mpairslist.put("hashmethod",""+ hashmethod);
                mpairslist.put("hashvalue",""+ hashvalue);
                mpairslist.put("title","xx");
            }


            //final String finalSelectedid = selectedid;
            final String finalAction_type = action_type;
           // final String finalVideoid = videoid;
            xapipost_send(baseactivity.this,action_type,mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response)
                {
                    if(response.isSuccess())
                    {
                        if(finalAction_type.equalsIgnoreCase(config.type_video_start))
                        {
                            try {
                                JSONObject object = (JSONObject) response.getData();
                                Log.e("finale object",""+ object);
                               /* String videokey=object.getString("key");
                                updatevideokey(finalVideoid,videokey);*/
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    if(finalAction_type.equalsIgnoreCase(config.type_video_update))
                    {

                    }
                    else if(finalAction_type.equalsIgnoreCase(config.type_video_complete))
                    {

                    }

                  //  updatedatasync(finalSelectedid);
                }
            });
        }
    }


    public void deletemetadatarecord(String selectedid)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(baseactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.deletemetadata(selectedid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updatedatasync(String selectedid)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(baseactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatesyncstatus(selectedid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updatevideokey(String videoid,String videokey)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(baseactivity.this);
            mdbhelper.createDatabase();
        }
        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mdbhelper.updatevideokey(videoid,videokey);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
}
