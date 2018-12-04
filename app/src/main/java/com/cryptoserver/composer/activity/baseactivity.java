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


import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.fragments.audiocomposerfragment;
import com.cryptoserver.composer.fragments.audioreaderfragment;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.imagecomposerfragment;
import com.cryptoserver.composer.fragments.imagereaderfragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    boolean updatesync = true;
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
            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                onBack();
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

    public void fetchmetadatadb() {

         String hashmethod="",hashvalue="";

         String selectedid="", header = "", type = "", location = "", localkey = "", token = "", videokey = "",
                sync = "",sync_date = "",action_type="",apirequestdevicedate = "",videostartdevicedate= "",devicetimeoffset = "",
                 videocompletedevicedate = "";

         String synchdefaultversion = "1", synchstatus = "inprogress", synchcompletedate = "", synchlastsequence = "";

        HashMap<String,Object> mpairslist=new HashMap<String, Object>();

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

        ArrayList<startmediainfo> marray = mdbhelper.setmediainfo();
                if(marray != null && marray.size()>0){
                    selectedid= marray.get(0).getId().toString();
                    header = marray.get(0).getHeader().toString();
                    type = marray.get(0).getType().toString();
                    location = marray.get(0).getLocation().toString();
                    localkey = marray.get(0).getLocalkey().toString();
                    token = marray.get(0).getToken().toString();
                    videokey = marray.get(0).getVideokey().toString();
                    sync = marray.get(0).getSync().toString();
                    action_type = marray.get(0).getAction_type().toString();
                    sync_date = marray.get(0).getSync_date().toString();
                    apirequestdevicedate = marray.get(0).getApirequestdevicedate().toString();
                    videostartdevicedate = marray.get(0).getVideostartdevicedate().toString();
                    devicetimeoffset = marray.get(0).getDevicetimeoffset().toString();
                    videocompletedevicedate = marray.get(0).getVideocompletedevicedate().toString();
                }

            mdbhelper.close();

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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final String  finalheader = header;
        final String  finalselectedid = selectedid;
        final String  finallocalkey = localkey;
        final String finalvideokey = videokey;
        final String finaltoken = token;
        final String finalsync = sync;
        final String finaldevicetimeoffset = devicetimeoffset;
        final String  finalapirequestdevicedate = apirequestdevicedate;
        final String  finalvideocompletedevicedate = videocompletedevicedate;

        if(videokey.trim().isEmpty()){

            mpairslist.put("html","0");
            mpairslist.put("hashmethod",""+hashmethod);
            mpairslist.put("hashvalue",""+hashvalue);
            mpairslist.put("title","xx");
            mpairslist.put("apirequestdevicedate",apirequestdevicedate);
            mpairslist.put("videostartdevicedate",videostartdevicedate);
            mpairslist.put("devicetimeoffset",devicetimeoffset);


            Log.e("hashvalue",hashvalue +"--"+ hashmethod);


            xapipost_sendjson(baseactivity.this,config.type_video_start,mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response)
                {
                    if(response.isSuccess())
                    {
                         try {
                                JSONObject object = (JSONObject) response.getData();
                                String videokey=object.getString("key");
                                String videotoken=object.getString("videotoken");
                                String transactionid = object.getString("videostarttransactionid");
                                updatevideokeytoken(finalselectedid,videokey,videotoken,transactionid);

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
            });
        }else{


            if(sync_date.equalsIgnoreCase("0"))
                callUpdateapi(finalheader,finallocalkey,finalvideokey,finaltoken,finalsync,finaldevicetimeoffset,finalapirequestdevicedate,finalselectedid,finalvideocompletedevicedate);
            }

        }



    public void callUpdateapi(String finalheader,String finallocalkey, String finalvideokey, String finaltoken, String finalsync, String finaldevicetimeoffset,String finalapirequestdevicedate, String startselectedid,String finalvideocompletedevicedate){

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

                callvideocompletedapi(startselectedid,finalheader,finallocalkey,finalapirequestdevicedate,finalvideokey,finaldevicetimeoffset,finaltoken,finalvideocompletedevicedate,finalsync);
                return ;

            }

            final String finalselectedid =  selectedid;

            try {

                JSONArray jsonArray = new JSONArray(metricdata);

                JSONObject mainobject=new JSONObject();
                finalobject=new JSONObject();

                mainobject.put("dictionary",""+jsonArray.get(0));
                mainobject.put("sequenceno",sequenceno);
                mainobject.put("recorddate",""+recordate);
                mainobject.put("dictionaryhashmethod",""+hashmethod);
                mainobject.put("sequencehash",sequencehash);
                mainobject.put("dictionaryhashvalue",""+valuehash);
                mainobject.put("sequencedevicedate",""+sequencedevicedate);

                array.put(mainobject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
                mpairslist.put("html","0");
                mpairslist.put("key",""+finalvideokey);
                mpairslist.put("devicetimeoffset",""+finaldevicetimeoffset);
                mpairslist.put("apirequestdevicedate",""+finalapirequestdevicedate);
                mpairslist.put("videotoken",finaltoken);
                mpairslist.put("videoupdatedevicedate",""+videoupdatedevicedate);
                mpairslist.put("sequencelist",  array);

            final String finalSequenceno = sequenceno;

            xapipost_sendjson(baseactivity.this,config.type_video_update, mpairslist, new apiresponselistener() {
                @Override
                public void onResponse(taskresult response)
                {
                    if(response.isSuccess())
                    {
                        try {

                            JSONObject object = (JSONObject) response.getData();
                            JSONObject jsonObject = object.getJSONObject("sequences");
                            String sequenceid =  "",videoframetransactionid = "",serverdictionaryhash="",sequencekey = "";

                            Iterator itr = jsonObject.keys();
                            while(itr.hasNext()){
                                 sequencekey = (String)itr.next();
                                JSONObject issue = jsonObject.getJSONObject(sequencekey);

                                //  get id from  issue
                                 sequenceid = issue.getString("sequenceid");
                                 videoframetransactionid = issue.getString("videoframetransactionid");
                                 serverdictionaryhash = issue.getString("serverdictionaryhash");
                            }

                            String serverdate = object.getString("serverdate");
                            updatevideoupdateapiresponce(finalselectedid,sequencekey,serverdate,serverdictionaryhash,sequenceid,videoframetransactionid);


                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void callvideocompletedapi(String startselectedid,String finalheader,String finallocalkey,String finalapirequestdevicedate,String finalvideokey,String finaldevicetimeoffset,String finaltoken,String finalvideocompletedevicedate,String finalsync){

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
        mpairslist.put("videocompletedevicedate",""+finalvideocompletedevicedate);
        mpairslist.put("videotoken",finaltoken);
        mpairslist.put("framecount", framecount);
        mpairslist.put("videoduration", videoduration);

        xapipost_sendjson(baseactivity.this,config.type_video_complete, mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response)
            {
                if(response.isSuccess())
                {
                    try {

                        JSONObject object = (JSONObject) response.getData();
                        String valuehash = object.getString("hashvalue");

                        updatecompletehashvalue(localkey,valuehash);
                        updatedatasyncdate(localkey,common.getCurrentDate());


                     /*   String sequence = object.getString("sequence");
                        String serverdate = object.getString("serverdate");
                        String serverdictionaryhash = object.getString("serverdictionaryhash");
                        updatevideoupdateapiresponse(finalselectedid,sequence,serverdate,serverdictionaryhash);*/

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    public void updatedatasync(String sync,String selectedid)
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
            mdbhelper.updatesyncvalue(sync,selectedid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updatedatasyncdate(String localkey,String syncdate)
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
            mdbhelper.updatevideosyncdate(localkey,syncdate);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void updatecompletehashvalue(String localkey,String valuehash)
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
            mdbhelper.updatecompletehashvalue(localkey,valuehash);
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

    public void updatevideokeytoken(String videoid,String videokey,String tokan,String transactionid)
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
            mdbhelper.updatevideokeytoken(videoid,videokey,tokan,transactionid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void updatevideoupdateapiresponce(String selectedid,String sequence,String serverdate,String serverdictionaryhash,String sequenceid,String videoframetransactionid)
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
            mdbhelper.updatevideoupdateapiresponse(selectedid,sequence,serverdate,serverdictionaryhash,sequenceid,videoframetransactionid);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public static String[] getStringArray(JSONArray jsonArray) {
        String[] stringArray = null;
        if (jsonArray != null) {
            int length = jsonArray.length();
            stringArray = new String[length];
            for (int i = 0; i < length; i++) {
                stringArray[i] = jsonArray.optString(i);
            }
        }
        return stringArray;
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

    public String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }
}
