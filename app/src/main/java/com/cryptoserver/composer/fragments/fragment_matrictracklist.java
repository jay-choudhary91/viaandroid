package com.cryptoserver.composer.fragments;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.ItemMetricAdapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.AdapterItemClick;
import com.cryptoserver.composer.models.MetricModel;
import com.cryptoserver.composer.services.LocationService;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.xdata;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_matrictracklist extends basefragment implements View.OnClickListener {

    @BindView(R.id.recyview_itemmetric)
    RecyclerView recyviewItem;
    public fragment_matrictracklist() {
        // Required empty public constructor
    }
    private ArrayList<MetricModel> metricItemArraylist = new ArrayList<>();
    ItemMetricAdapter itemMetricAdapter;
   // private DatabaseManager mDbHelper;
    private View rootView = null;

    private static final String STATE_ID = "id";
    private static final String STATE_ITEMS = "items";

    private BroadcastReceiver locationUpdateReceiver;
    private Location oldLocation=null;
    private double doubleTotalDistance=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootView);

           // getHelper().updateHeader("Device Metrics");
           /* btn_refresh.setOnClickListener(this);
            btn_log.setOnClickListener(this);
*/
            if (savedInstanceState != null) {
                // Restore some state that needs to happen after the Activity was created
                //
                // Note #1: Our views haven't had their states restored yet
                // This could be a good place to restore a ListView's contents (and it's your last
                // opportunity if you want your scroll position to be restored properly)
                //
                // Note #2:
                // The following line will cause an unchecked type cast compiler warning
                // It's impossible to actually check the type because of Java's type erasure:
                //      At runtime all generic types become Object
                // So the best you can do is add the @SuppressWarnings("unchecked") annotation
                // and understand that you must make sure to not use a different type anywhere
                metricItemArraylist = (ArrayList<MetricModel>) savedInstanceState.getSerializable(STATE_ITEMS);
                setAdapter();
                setvalueadapter();
            }
            else
            {
                prepareData();
                setAdapter();
                setvalueadapter();
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

                                /*if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("distancetraveled")) {
                                    metricItemArraylist.get(i).setMetricTrackValue("" + ((int) doubleTotalDistance));
                                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                                        metricItemArraylist.get(i).setSelected(true);
                                }*/

                            }
                            itemMetricAdapter.notifyDataSetChanged();
                        }
                        /*else
                        {
                            for (int i = 0; i < metricItemArraylist.size(); i++) {

                                if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("distancetraveled")) {
                                    metricItemArraylist.get(i).setMetricTrackValue("" + (""+meter));
                                    if (metricItemArraylist.get(i).getTag().equalsIgnoreCase("checked"))
                                        metricItemArraylist.get(i).setSelected(true);
                                }

                            }
                            itemMetricAdapter.notifyDataSetChanged();
                        }*/

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
        }

        return rootView;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_fragment_matrictracklist;
    }
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore some state that needs to happen after the Activity was created
            //
            // Note #1: Our views haven't had their states restored yet
            // This could be a good place to restore a ListView's contents (and it's your last
            // opportunity if you want your scroll position to be restored properly)
            //
            // Note #2:
            // The following line will cause an unchecked type cast compiler warning
            // It's impossible to actually check the type because of Java's type erasure:
            //      At runtime all generic types become Object
            // So the best you can do is add the @SuppressWarnings("unchecked") annotation
            // and understand that you must make sure to not use a different type anywhere
            metricItemArraylist = (ArrayList<MetricModel>) savedInstanceState.getSerializable(STATE_ITEMS);
            setAdapter();
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            String name = className.getClassName();

            /*if (name.endsWith("LocationService")) {
                locationService = ((LocationService.LocationServiceBinder) service).getService();

                locationService.startUpdatingLocation();


            }*/
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
    public void onClick(View view) {
        switch (view.getId()) {

        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (locationUpdateReceiver != null) {
                getActivity().unregisterReceiver(locationUpdateReceiver);
            }

            getActivity().unbindService(serviceConnection);

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putSerializable(STATE_ITEMS, metricItemArraylist);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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


        @Override
        public void getAccurateLocation() {
            super.getAccurateLocation();

            if(isMyServiceRunning(LocationService.class))
            {
                Log.e("Running ","Yes");
            }
            else
            {
                Log.e("Running ","No");

                final Intent locationService = new Intent(getActivity(), LocationService.class);
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
        itemMetricAdapter.notifyDataSetChanged();
    }
    public void matchInArray(String key, String value) {

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (key.equals(metricItemArraylist.get(i).getMetricTrackKeyName())) {
                metricItemArraylist.get(i).setMetricTrackValue(value);
                metricItemArraylist.get(i).setSelected(true);
                itemMetricAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
    private void prepareData() {

        //metricItemArraylist.add(new MetricModel("username", "",false));
        metricItemArraylist.add(new MetricModel("battery", "", false));
        metricItemArraylist.add(new MetricModel("imeinumber", "", false));
        metricItemArraylist.add(new MetricModel("simserialnumber", "", false));
        metricItemArraylist.add(new MetricModel("version", "", false));
        metricItemArraylist.add(new MetricModel("osversion", "", false));
        metricItemArraylist.add(new MetricModel("softwareversion", "", false));
        metricItemArraylist.add(new MetricModel("model", "", false));
        metricItemArraylist.add(new MetricModel("manufacturer", "", false));
        metricItemArraylist.add(new MetricModel("brightness", "",false));
        metricItemArraylist.add(new MetricModel("gpslatitude", "", false));
        metricItemArraylist.add(new MetricModel("gpslongitude", "", false));
        metricItemArraylist.add(new MetricModel("gpsaltittude", "", false));
        metricItemArraylist.add(new MetricModel("gpsquality", "", false));
        metricItemArraylist.add(new MetricModel("carrier", "", false));
        metricItemArraylist.add(new MetricModel("screenwidth", "", false));
        metricItemArraylist.add(new MetricModel("screenheight", "", false));
        metricItemArraylist.add(new MetricModel("systemuptime", "", false));
        metricItemArraylist.add(new MetricModel("multitaskingenabled", "", false));
        metricItemArraylist.add(new MetricModel("proximitysensorenabled", "", false));
        metricItemArraylist.add(new MetricModel("pluggedin", "", false));
        metricItemArraylist.add(new MetricModel("devicetime", "", false));
        metricItemArraylist.add(new MetricModel("deviceregion", "", false));
        metricItemArraylist.add(new MetricModel("devicelanguage", "", false));
        metricItemArraylist.add(new MetricModel("devicecurrency", "", false));
        metricItemArraylist.add(new MetricModel("timezone", "", false));
        metricItemArraylist.add(new MetricModel("headphonesattached", "", false));
        metricItemArraylist.add(new MetricModel("accessoriesattached", "", false));
        metricItemArraylist.add(new MetricModel("nameattachedaccessories", "", false));
        metricItemArraylist.add(new MetricModel("attachedaccessoriescount", "", false));
        metricItemArraylist.add(new MetricModel("totalspace", "", false));
        metricItemArraylist.add(new MetricModel("usedspace", "", false));
        metricItemArraylist.add(new MetricModel("freespace", "", false));
        metricItemArraylist.add(new MetricModel("deviceorientation", "", false));
        metricItemArraylist.add(new MetricModel("rammemory", "", false));
        metricItemArraylist.add(new MetricModel("usedram", "", false));
        metricItemArraylist.add(new MetricModel("freeram", "", false));
        metricItemArraylist.add(new MetricModel("wificonnect", "", false));
        metricItemArraylist.add(new MetricModel("cellnetworkconnect", "", false));
        metricItemArraylist.add(new MetricModel("internalip", "", false));
        metricItemArraylist.add(new MetricModel("externalip", "", false));
        metricItemArraylist.add(new MetricModel("networktype", "", false));
        metricItemArraylist.add(new MetricModel("connectedphonenetworkquality", "", false));
        metricItemArraylist.add(new MetricModel("gravitysensorenabled", "", false));
        metricItemArraylist.add(new MetricModel("gyroscopesensorenabled", "", false));
        metricItemArraylist.add(new MetricModel("lightsensorenabled", "", false));
        metricItemArraylist.add(new MetricModel("debuggerattached", "", false));
        metricItemArraylist.add(new MetricModel("deviceid", "", false));
        metricItemArraylist.add(new MetricModel("bluetoothonoff", "", false));
        metricItemArraylist.add(new MetricModel("wifiname", "", false));
        metricItemArraylist.add(new MetricModel("wifinetworksaveailable", "", false));
        metricItemArraylist.add(new MetricModel("processorcount", "", false));
        metricItemArraylist.add(new MetricModel("activeprocessorcount", "", false));
        metricItemArraylist.add(new MetricModel("cpuusageuser", "", false));
        metricItemArraylist.add(new MetricModel("cpuusagesystem", "", false));
        metricItemArraylist.add(new MetricModel("cpuusageiow", "", false));
        metricItemArraylist.add(new MetricModel("cpuusageirq", "", false));
        metricItemArraylist.add(new MetricModel("compass", "", false));
        metricItemArraylist.add(new MetricModel("decibel", "", false));
        metricItemArraylist.add(new MetricModel("barometer", "", false));
        metricItemArraylist.add(new MetricModel("isaccelerometeravailable", "", false));
        metricItemArraylist.add(new MetricModel("acceleration.x", "", false));
        metricItemArraylist.add(new MetricModel("acceleration.y", "", false));
        metricItemArraylist.add(new MetricModel("acceleration.z", "", false));
        metricItemArraylist.add(new MetricModel("distancetraveled", "", false));
        metricItemArraylist.add(new MetricModel("dataconnection", "", false));
        metricItemArraylist.add(new MetricModel("currentcallinprogress", "", false));
        metricItemArraylist.add(new MetricModel("currentcallremotenumber", "", false));
        metricItemArraylist.add(new MetricModel("currentcalldurationseconds", "", false));
        metricItemArraylist.add(new MetricModel("currentcallvolume", "", false));
        metricItemArraylist.add(new MetricModel("currentcalldecibel", "", false));
        //metricItemArraylist.add(new MetricModel("carrierVOIP", "", false));
        //metricItemArraylist.add(new MetricModel("gpsverticalaccuracy", "", false));


        //metricItemArraylist.add(new MetricModel("usbconnecteddevicename", "", false));
    }
    public void setAdapter()
    {
        itemMetricAdapter = new ItemMetricAdapter(getActivity(), metricItemArraylist, new AdapterItemClick() {
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
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyviewItem.setLayoutManager(mLayoutManager);
        recyviewItem.setItemAnimator(new DefaultItemAnimator());
        recyviewItem.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyviewItem.setAdapter(itemMetricAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setvalueadapter();
            }
        }, 5000);
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
        itemMetricAdapter.notifyDataSetChanged();
    }

    /*public void saveRefreshItems() {
        if (mDbHelper == null) {
            mDbHelper = new DatabaseManager(getActivity());
            mDbHelper.createDatabase();
        }

        try {
            mDbHelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            Calendar calander = Calendar.getinstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Config.DATE_TIME_FORMAT);
            String datetime = simpleDateFormat.format(calander.getTime());
            Log.e("Current Time = ", ""+datetime);

            for (int i = 0; i < metricItemArraylist.size(); i++) {
                if (metricItemArraylist.get(i).isSelected()) {
                    mDbHelper.insertLog(datetime, metricItemArraylist.get(i).getMetricTrackKeyName(),
                            metricItemArraylist.get(i).getMetricTrackValue(), "0", "true");
                } *//*else {
                    mDbHelper.insertLog(datetime, metricItemArraylist.get(i).getMetricTrackKeyName(),
                            "", "0", "false");
                }*//*
            }
            mDbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).isSelected()) {
                if (metric_onoff(metricItemArraylist.get(i).getMetricTrackKeyName())) {
                    String value = metric_read(metricItemArraylist.get(i).getMetricTrackKeyName());
                    if (!value.isEmpty()) {
                        metricItemArraylist.get(i).setMetricTrackValue(value);
                    }
                }
            }
        }
        itemMetricAdapter.notifyDataSetChanged();
    }

    public void displayLog() {
        LogListFragment frag = new LogListFragment();
        getHelper().replaceFragment(frag, false, true);
    }
*/
   public void setvalueadapter(){
       for(int key=0;key<metricItemArraylist.size();key++){
           if(metricItemArraylist.get(key).getMetricTrackKeyName()==null){
               Log.d("metrictrak app",metricItemArraylist.get(key).getMetricTrackKeyName());
           }
           if (metric_onoff(metricItemArraylist.get(key).getMetricTrackKeyName())) {
               String value = metric_read(metricItemArraylist.get(key).getMetricTrackKeyName());
               if(value.equals("UpdateLater"))
               {

               }
               else
               {
                   if(value.isEmpty() && value != null)
                   {
                       matchInArray(metricItemArraylist.get(key).getMetricTrackKeyName(), "N/A");
                   }
                   else if(value != null)
                   {
                       matchInArray(metricItemArraylist.get(key).getMetricTrackKeyName(), value);
                   }
               }
           }
       }
   }
}
