package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.Itemmetricadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.services.locationservice;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.minmaxfilter;
import com.cryptoserver.composer.utils.xdata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentsettings extends basefragment implements View.OnClickListener {

    @BindView(R.id.recyview_itemmetric)
    RecyclerView recyviewItem;
    LinearLayoutManager mLayoutManager;
    View rootview = null;
    public fragmentsettings() {
        // Required empty public constructor
    }
    private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    Itemmetricadapter itemMetricAdapter;
   // private DatabaseManager mDbHelper;
   // private View rootView = null;

    private static final String STATE_ID = "id";
    private static final String STATE_ITEMS = "items";

    private BroadcastReceiver locationUpdateReceiver;
    private Location oldLocation=null;
    private double doubleTotalDistance=0;
    private static final int request_permissions = 1;
    private Runnable doafterallpermissionsgranted;
    LinearLayout layout_view;
    RelativeLayout layout_md;
    RelativeLayout layout_md_salt;
    RelativeLayout layout_sha;
    RelativeLayout layout_sha_salt;

    ImageView img_md;
    ImageView img_md_salt;
    ImageView img_sha;
    ImageView img_sha_salt;

    EditText edt_md_salt;
    EditText edt_sha_salt;
    EditText edt_framescount;
    String keytype ="md5";
    int framecount=15;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        applicationviavideocomposer.setActivity(getActivity());
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            layout_md =(RelativeLayout)rootview.findViewById(R.id.layout_md);
            layout_md_salt =(RelativeLayout)rootview.findViewById(R.id.layout_md_salt);
            layout_sha =(RelativeLayout)rootview.findViewById(R.id.layout_sha);
            layout_sha_salt =(RelativeLayout)rootview.findViewById(R.id.layout_sha_salt);

            img_md =(ImageView)rootview.findViewById(R.id.img_md);
            img_md_salt= (ImageView)rootview.findViewById(R.id.img_md_salt);
            img_sha =(ImageView)rootview.findViewById(R.id.img_sha);
            img_sha_salt =(ImageView)rootview.findViewById(R.id.img_sha_salt);

            edt_framescount =(EditText) rootview.findViewById(R.id.edt_framescount);
            edt_md_salt =(EditText) rootview.findViewById(R.id.edt_md_salt);
            edt_sha_salt =(EditText) rootview.findViewById(R.id.edt_sha_salt);

            layout_view=rootview.findViewById(R.id.layout_view);


            layout_md.setOnClickListener(this);
            layout_md_salt.setOnClickListener(this);
            layout_sha.setOnClickListener(this);
            layout_sha_salt.setOnClickListener(this);
            layout_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    clearfocus();
                    return false;
                }

            });

            edt_framescount.setText(""+framecount);
            if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
                edt_framescount.setText(xdata.getinstance().getSetting(config.framecount));
            edt_framescount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            edt_framescount.setFilters(new InputFilter[]{ new minmaxfilter("1", "1000")});
            if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                    xdata.getinstance().getSetting(config.hashtype).trim().isEmpty())
            {
                showselectedunselected(img_md,img_md_salt,img_sha,img_sha_salt,config.prefs_md5);
            }
            else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt))
            {
                showselectedunselected(img_md_salt,img_md,img_sha,img_sha_salt,config.prefs_md5_salt);
                edt_md_salt.setText(xdata.getinstance().getSetting(config.prefs_md5_salt));

            }
            else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha))
            {
                showselectedunselected(img_sha,img_md,img_md_salt,img_sha_salt,config.prefs_sha);
            }
            else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt))
            {
                showselectedunselected(img_sha_salt,img_md,img_md_salt,img_sha,config.prefs_sha_salt);
                edt_sha_salt.setText(xdata.getinstance().getSetting(config.prefs_sha_salt));
            }
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {

        return R.layout.fragment_settings;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_md:
                clearfocus();
                showselectedunselected(img_md,img_md_salt,img_sha,img_sha_salt,config.prefs_md5);
                break;

            case R.id.layout_md_salt:
                clearfocus();
                showselectedunselected(img_md_salt,img_md,img_sha,img_sha_salt,config.prefs_md5_salt);
                break;

            case R.id.layout_sha:
                clearfocus();
                showselectedunselected(img_sha,img_md,img_md_salt,img_sha_salt,config.prefs_sha);
                break;

            case R.id.layout_sha_salt:
                clearfocus();
                showselectedunselected(img_sha_salt,img_md,img_md_salt,img_sha,config.prefs_sha_salt);
                break;
        }
    }

    public void showselectedunselected(ImageView img1, ImageView img2, ImageView img3, ImageView img4,String keyname)
    {
        img1.setImageResource(R.drawable.selectedicon);

        img2.setImageResource(R.drawable.unselectedicon);
        img3.setImageResource(R.drawable.unselectedicon);
        img4.setImageResource(R.drawable.unselectedicon);

        keytype=keyname;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            String name = className.getClassName();

            /*if (name.endsWith("locationservice")) {
                locationService = ((locationservice.LocationServiceBinder) service).getService();

                locationService.startUpdatingLocation();


            }*/
        }
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            /*if (className.getClassName().equals("locationservice")) {
                locationService.stopUpdatingLocation();
                locationService = null;
            }*/
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (locationUpdateReceiver != null) {
                getActivity().unregisterReceiver(locationUpdateReceiver);
            }

            getActivity().unbindService(serviceConnection);

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
/* private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) applicationviavideocomposer.getactivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }*/




    private void prepareData() {

        metricItemArraylist.clear();
        itemMetricAdapter.notifyDataSetChanged();;

        ArrayList<metricmodel> mlist = new ArrayList<>();
        mlist.addAll(getMetricList());
        if(mlist.size() > 0)
        {
            for(int i=0;i<mlist.size();i++)
            {
                metricItemArraylist.add(mlist.get(i));
            }

            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                   // setvalueadapter();
                    itemMetricAdapter.notifyDataSetChanged();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setvalueadapter();
                        }
                    },2000);
                }
            });
            //metricItemArraylist.addAll(getMetricList());
        }
        else
        {
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
            metricItemArraylist.add(new metricmodel("connectedphonenetworkquality", "", true));
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
            metricItemArraylist.add(new metricmodel("cpuusageuser", "", true));
            metricItemArraylist.add(new metricmodel("cpuusagesystem", "", true));
            metricItemArraylist.add(new metricmodel("cpuusageiow", "", true));
            metricItemArraylist.add(new metricmodel("cpuusageirq", "", true));
            metricItemArraylist.add(new metricmodel("compass", "", true));
            metricItemArraylist.add(new metricmodel("decibel", "", true));
            metricItemArraylist.add(new metricmodel("barometer", "", true));
            metricItemArraylist.add(new metricmodel("isaccelerometeravailable", "", true));
            metricItemArraylist.add(new metricmodel("acceleration.x", "", true));
            metricItemArraylist.add(new metricmodel("acceleration.y", "", true));
            metricItemArraylist.add(new metricmodel("acceleration.z", "", true));
            metricItemArraylist.add(new metricmodel("distancetraveled", "", true));
            metricItemArraylist.add(new metricmodel("dataconnection", "", true));
            metricItemArraylist.add(new metricmodel("currentcallinprogress", "", true));
            metricItemArraylist.add(new metricmodel("currentcallremotenumber", "", true));
            metricItemArraylist.add(new metricmodel("currentcalldurationseconds", "", true));
            metricItemArraylist.add(new metricmodel("currentcallvolume", "", true));
            metricItemArraylist.add(new metricmodel("currentcalldecibel", "", true));
            metricItemArraylist.add(new metricmodel("gpsonoff","",true));
            metricItemArraylist.add(new metricmodel("airplanemode","",true));
            metricItemArraylist.add(new metricmodel("phonetime","",true));
            metricItemArraylist.add(new metricmodel("syncphonetime","",true));

            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setvalueadapter();
                }
            });
        }
    }

    public void saveData()
    {

        if(keytype.equalsIgnoreCase(config.prefs_md5_salt))
        {
            if(edt_md_salt.getText().toString().trim().length() == 0)
            {
                Toast.makeText(getActivity(),"Please enter SALT value",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if(keytype.equalsIgnoreCase(config.prefs_sha_salt))
        {
            if(edt_sha_salt.getText().toString().trim().length() == 0)
            {
                Toast.makeText(getActivity(),"Please enter SALT value",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        xdata.getinstance().saveSetting(config.hashtype,keytype);
        xdata.getinstance().saveSetting(config.framecount,edt_framescount.getText().toString().trim());
        xdata.getinstance().saveSetting(config.prefs_sha_salt,edt_sha_salt.getText().toString());
        xdata.getinstance().saveSetting(config.prefs_md5_salt,edt_md_salt.getText().toString());

        // save metric list
        Gson gson = new Gson();
        String json = gson.toJson(metricItemArraylist);
        xdata.getinstance().saveSetting(config.metriclist,json);

        gethelper().onBack();
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid) {
            case R.id.img_back:
                saveData();

                break;
            case R.id.img_cancel:
                gethelper().onBack();
                break;
        }
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

    public void setAdapter()
    {
        itemMetricAdapter = new Itemmetricadapter(getActivity(), metricItemArraylist, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                String key = (String) object;
                if (metric_onoff(key)) {
                    String value = metric_read(key);
                    if(value.equals("UpdateLater"))
                    {

                    }
                    else
                    {
                        if(value.isEmpty() && value != null)
                        {
                            matchInArray(key, "N/A");
                        }
                        else if(value != null)
                        {
                            matchInArray(key, value);
                        }
                    }
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyviewItem.setNestedScrollingEnabled(false);
        recyviewItem.setLayoutManager(mLayoutManager);
        recyviewItem.setItemAnimator(new DefaultItemAnimator());
        recyviewItem.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyviewItem.setAdapter(itemMetricAdapter);
    }

    @Override
    public void getAccurateLocation() {
        super.getAccurateLocation();

        if(common.isMyServiceRunning(getActivity(),locationservice.class)!=true)
        {
            Log.e("Running ","Yes");
        }
        else
        {
            Log.e("Running ","No");

            final Intent locationService = new Intent(getActivity(), locationservice.class);
            getActivity().startService(locationService);
            getActivity().bindService(locationService, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    @SuppressLint("MissingPermission")
    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
        if(oldLocation != null)
        {
            long meter=common.calculateDistance(location.getLatitude(),location.getLongitude(),
                    oldLocation.getLatitude(),oldLocation.getLongitude());
            doubleTotalDistance=doubleTotalDistance+meter;
        }
        oldLocation=location;

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + location.getLatitude());
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslongitude")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + location.getLongitude());
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("distancetraveled")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + ((int)doubleTotalDistance));
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }

        if(! xdata.getinstance().getSetting("gpsaltittude").trim().isEmpty())
        {
            for (int i = 0; i < metricItemArraylist.size(); i++) {

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaltittude")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("gpsaltittude")));
                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                        metricItemArraylist.get(i).setSelected(true);
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("heading")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + xdata.getinstance().getSetting("heading"));
                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                        metricItemArraylist.get(i).setSelected(true);
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("speed")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("speed")));
                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                        metricItemArraylist.get(i).setSelected(true);
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsquality")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("gpsquality")));
                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                        metricItemArraylist.get(i).setSelected(true);
                }

                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsnumberofsatelites")) {
                    metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("gpsnumberofsatelites")));
                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                        metricItemArraylist.get(i).setSelected(true);
                }
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateAccelerometerValue(float deltaX, float deltaY, float deltaZ) {
        super.onUpdateAccelerometerValue(deltaX, deltaY, deltaZ);

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("acceleration.x")) {
                metricItemArraylist.get(i).setMetricTrackValue(""+deltaX);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("acceleration.y")) {
                metricItemArraylist.get(i).setMetricTrackValue(""+deltaY);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("acceleration.z")) {
                metricItemArraylist.get(i).setMetricTrackValue(""+deltaZ);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }
    @Override
    public void updateMobileNetworkStrength(String result) {
        super.updateMobileNetworkStrength(result);

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("connectedphonenetworkquality")) {
                metricItemArraylist.get(i).setMetricTrackValue(result);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateairplanemode(String result) {
        super.updateairplanemode(result);
        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("airplanemode")) {
                metricItemArraylist.get(i).setMetricTrackValue(result);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }


    @Override
    public void updateCompassDegree(String result) {
        super.updateCompassDegree(result);
        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("compass")) {
                metricItemArraylist.get(i).setMetricTrackValue(result);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateBarometerSensorData(String result) {
        super.updateBarometerSensorData(result);
        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("barometer")) {
                metricItemArraylist.get(i).setMetricTrackValue(result);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateDecibelValue(String result) {
        super.updateDecibelValue(result);

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("decibel")) {
                metricItemArraylist.get(i).setMetricTrackValue(result);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCallDecibelValue(String result) {
        super.updateCallDecibelValue(result);

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("currentcalldecibel")) {
                metricItemArraylist.get(i).setMetricTrackValue(result);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }
    public void matchInArray(String key, String value) {

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (key.equals(metricItemArraylist.get(i).getMetricTrackKeyName())) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                metricItemArraylist.get(i).setSelected(true);
                if(itemMetricAdapter != null)
                    itemMetricAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void updateCallInfo(String callStaus, String callDuration, String CallerNumber) {
        super.updateCallInfo(callStaus, callDuration, CallerNumber);
        for (int i = 0; i < metricItemArraylist.size(); i++) {

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("currentcallinprogress")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + (xdata.getinstance().getSetting("CALL_STATUS")));
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("currentcalldurationseconds")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + ((xdata.getinstance().getSetting("CALL_DURATION"))));
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("currentcallremotenumber")) {
                metricItemArraylist.get(i).setMetricTrackValue("" + ((xdata.getinstance().getSetting("CALL_REMOTE_NUMBER"))));
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

        }

        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();

    }
    @Override
    public void updateUsageSystem(String value) {
        super.updateUsageSystem(value);

        for (int i = 0; i < metricItemArraylist.size(); i++) {

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("cpuusagesystem")) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateUsageUser(String value) {
        super.updateUsageUser(value);

        for (int i = 0; i < metricItemArraylist.size(); i++) {

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("cpuusageuser")) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateUsageIow(String value) {
        super.updateUsageIow(value);

        for (int i = 0; i < metricItemArraylist.size(); i++) {

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("cpuusageiow")) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }

        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateUsageIrq(String value) {
        super.updateUsageIrq(value);

        for (int i = 0; i < metricItemArraylist.size(); i++) {

            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("cpuusageirq")) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                    metricItemArraylist.get(i).setSelected(true);
            }
        }
        if(itemMetricAdapter != null)
            itemMetricAdapter.notifyDataSetChanged();
    }

   public void setvalueadapter(){
       for(int i=0;i<metricItemArraylist.size();i++){
           if(metricItemArraylist.get(i).isSelected())
           {
               if (metric_onoff(metricItemArraylist.get(i).getMetricTrackKeyName())) {
                   String value = metric_read(metricItemArraylist.get(i).getMetricTrackKeyName());
                   if(! value.equals("UpdateLater"))
                   {
                       if(value.isEmpty() && value != null)
                       {
                           matchInArray(metricItemArraylist.get(i).getMetricTrackKeyName(), "N/A");
                       }
                       else if(value != null)
                       {
                           matchInArray(metricItemArraylist.get(i).getMetricTrackKeyName(), value);
                       }
                   }
               }
           }

       }
       itemMetricAdapter.notifyDataSetChanged();
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
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                };
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
     /*   permissiongrants();*/
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            String[] neededpermissions = {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO
                    , Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            List<String> deniedpermissions = new ArrayList<>();
            for (String permission : neededpermissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedpermissions.add(permission);
                }
            }
            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterallpermissionsgranted();
            } else {
                String[] array = new String[deniedpermissions.size()];
                array = deniedpermissions.toArray(array);
                ActivityCompat.requestPermissions(getActivity(), array, request_permissions);
            }
        }
    }

    private void doafterallpermissionsgranted(){

        setAdapter();

        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareData();

            }
        }).start();

        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location location = intent.getParcelableExtra("location");

                String str="Speed accuracy altitude "+""+location.getSpeed()+" "+location.getAccuracy()+" "+
                        location.getAltitude();


                //Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();

                doubleTotalDistance=doubleTotalDistance+location.getSpeed();
                if(xdata.getinstance().getSetting("gpsaltittude").trim().isEmpty())
                {
                    for (int i = 0; i < metricItemArraylist.size(); i++) {

                        if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaltittude")) {
                            metricItemArraylist.get(i).setMetricTrackValue("" + (location.getAltitude()));
                            if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                                metricItemArraylist.get(i).setSelected(true);
                        }

                        if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("heading")) {
                            metricItemArraylist.get(i).setMetricTrackValue("" + common.getGpsDirection(location.getBearing()));
                            if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                                metricItemArraylist.get(i).setSelected(true);
                        }

                        if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("speed")) {
                            metricItemArraylist.get(i).setMetricTrackValue("" + (location.getSpeed()));
                            if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                                metricItemArraylist.get(i).setSelected(true);
                        }

                        if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsquality")) {
                            metricItemArraylist.get(i).setMetricTrackValue("" + ((int) location.getAccuracy()));
                            if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                                metricItemArraylist.get(i).setSelected(true);
                        }

                    }
                    if(itemMetricAdapter != null)
                        itemMetricAdapter.notifyDataSetChanged();
                }

                xdata.getinstance().saveSetting("gpsaltittude",""+location.getAltitude());
                xdata.getinstance().saveSetting("heading",""+common.getGpsDirection(location.getBearing()));
                xdata.getinstance().saveSetting("speed",""+location.getSpeed());
                xdata.getinstance().saveSetting("distancetraveled",""+doubleTotalDistance);
                xdata.getinstance().saveSetting("gpsquality",""+location.getAccuracy());


            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                locationUpdateReceiver,
                new IntentFilter("LocationUpdated"));
    }

    public void clearfocus(){
        edt_framescount.clearFocus();
        edt_md_salt.clearFocus();
        edt_sha_salt.clearFocus();
        common.hidekeyboard(applicationviavideocomposer.getactivity());
    }
}
