package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.encryptiondataadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.models.graphicalmodel;
import com.cryptoserver.composer.models.wavevisualizer;
import com.cryptoserver.composer.utils.VisualizerView;
import com.cryptoserver.composer.utils.VisualizerViewMedia;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.noise;
import com.cryptoserver.composer.utils.xdata;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class graphicalfragment extends basefragment implements
        OnChartValueSelectedListener, OnChartGestureListener,SensorEventListener {


    public graphicalfragment() {
        // Required empty public constructor
    }

    @BindView(R.id.txt_latitude)
    TextView txt_latitude;
    @BindView(R.id.txt_longitude)
    TextView txt_longitude;
    @BindView(R.id.txt_altitude)
    TextView txt_altitude;
    @BindView(R.id.txt_speed)
    TextView txt_speed;
    @BindView(R.id.txt_heading)
    TextView txt_heading;
    @BindView(R.id.txt_orientation)
    TextView txt_orientation;
    @BindView(R.id.txt_address)
    TextView txt_address;
    @BindView(R.id.txt_xaxis)
    TextView txt_xaxis;
    @BindView(R.id.txt_yaxis)
    TextView txt_yaxis;
    @BindView(R.id.txt_zaxis)
    TextView txt_zaxis;
    @BindView(R.id.txt_phonetype)
    TextView txt_phonetype;
    @BindView(R.id.txt_cellprovider)
    TextView txt_cellprovider;
    @BindView(R.id.txt_connection_speed)
    TextView txt_connection_speed;
    @BindView(R.id.txt_osversion)
    TextView txt_osversion;
    @BindView(R.id.txt_wifinetwork)
    TextView txt_wifinetwork;
    @BindView(R.id.txt_gps_accuracy)
    TextView txt_gps_accuracy;
    @BindView(R.id.txt_screensize)
    TextView txt_screensize;
    @BindView(R.id.txt_country)
    TextView txt_country;
    @BindView(R.id.txt_cpuusage)
    TextView txt_cpuusage;
    @BindView(R.id.txt_brightness)
    TextView txt_brightness;
    @BindView(R.id.txt_timezone)
    TextView txt_timezone;
    @BindView(R.id.txt_memoryusage)
    TextView txt_memoryusage;
    @BindView(R.id.txt_bluetooth)
    TextView txt_bluetooth;
    @BindView(R.id.txt_localtime)
    TextView txt_localtime;
    @BindView(R.id.txt_storageavailable)
    TextView txt_storageavailable;
    @BindView(R.id.txt_language)
    TextView txt_language;
    @BindView(R.id.txt_systemuptime)
    TextView txt_systemuptime;
    @BindView(R.id.txt_battery)
    TextView txt_battery;
    @BindView(R.id.txt_hash_formula)
    TextView txt_hash_formula;
    @BindView(R.id.txt_data_hash)
    TextView txt_data_hash;
    @BindView(R.id.txt_dictionary_hash)
    TextView txt_dictionary_hash;
    @BindView(R.id.txt_videoupdatetransactionid)
    TextView txt_videoupdatetransactionid;

    @BindView(R.id.layout_encryption)
    LinearLayout layout_encryption;
    @BindView(R.id.layout_datalatency)
    LinearLayout layout_datalatency;
    @BindView(R.id.layout_soundinfo)
    LinearLayout layout_soundinformation;
    @BindView(R.id.layout_phoneanalytics)
    LinearLayout layout_phoneanalytics;
    @BindView(R.id.layout_orientationanalytics)
    LinearLayout layout_orientationanalytics;
    @BindView(R.id.layout_locationanalytics)
    LinearLayout layout_locationanalytics;
    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;


    @BindView(R.id.layout_graphical)
    LinearLayout layout_graphical;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.recyview_encryption)
    RecyclerView recyview_encryption;
    @BindView(R.id.scrollview_graphical)
    ScrollView scrollview_graphical;
    @BindView(R.id.linechart)
    LineChart mChart;
    View rootview;
    private float currentDegree = 0f;


    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;

    encryptiondataadapter encryptionadapter;
    RecyclerView.LayoutManager encryptionmanager;
    ImageView img_compass;
    GoogleMap mgooglemap;
    private ArrayList<graphicalmodel> frameslist = new ArrayList<>();

    VisualizerView myvisualizerview;
    VisualizerViewMedia myvisualizerviewmedia;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    Uri videoUri = null;

    private Handler waveHandler;
    private Runnable waveRunnable;
    noise mNoise;
    boolean isgraphicopen=false,photocapture=false;
    public String currenthashvalue="",lastsavedangle="";
    boolean ismediaplayer = false;
    private boolean isinbackground=false;
    ArrayList<LatLng> points=new ArrayList<>();
    PolylineOptions polylineOptions = new PolylineOptions();
    private static final int PATTERN_GAP_LENGTH_PX = 5;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    String[] visulizerdataarray ;
    String soundamplitudealue = "";
    int position=0;
    String hashmethod= "",videostarttransactionid = "",valuehash = "",metahash = "";


    int hitcounter=0;
    String[] soundamplitudealuearray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            scrollview_graphical.setVisibility(View.VISIBLE);
            myvisualizerview = (VisualizerView)rootview.findViewById(R.id.myvisualizerview);
            myvisualizerviewmedia = (VisualizerViewMedia) rootview.findViewById(R.id.myvisualizerviewmedia);

            myvisualizerview.setVisibility(View.VISIBLE);

            img_compass = (ImageView) rootview.findViewById(R.id.img_compass);
            recyview_encryption.setNestedScrollingEnabled(false);
            encryptionadapter=new encryptiondataadapter(frameslist,applicationviavideocomposer.getactivity());
            encryptionmanager=new LinearLayoutManager(applicationviavideocomposer.getactivity());
            msensormanager = (SensorManager) applicationviavideocomposer.getactivity().getSystemService(Context.SENSOR_SERVICE);
            recyview_encryption.setLayoutManager(encryptionmanager);
            recyview_encryption.setAdapter(encryptionadapter);

            if(xdata.getinstance().getSetting(config.hashtype).toString().trim().length() == 0)
                xdata.getinstance().saveSetting(config.hashtype,config.prefs_md5);

            loadmap();
            setchartdata();
        }
        return rootview;
    }

    public void sethashesdata(final String currenthashvalue)
    {
        if(txt_data_hash != null)
        {
            txt_data_hash.post(new Runnable() {
                @Override
                public void run() {
                    txt_data_hash.setText(currenthashvalue);
                }
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        isinbackground=true;
    }

    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
            if(ismediaplayer || photocapture)
            {

                Log.e("GraphicalLatLng",""+latlng.latitude+" "+latlng.longitude);
                {
                    /*points.add(latlng);
                    polylineOptions.addAll(points);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.width(20);
                    Polyline polyline =mgooglemap.addPolyline(polylineOptions);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.DEFAULT);*/

                    mgooglemap.addMarker(new MarkerOptions()
                     .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.horizontalline)));
                }

                /*{
                    points.add(latlng);
                    polylineOptions.addAll(points);
                    polylineOptions.color(Color.WHITE);
                    polylineOptions.width(20);
                    Polyline polyline =mgooglemap.addPolyline(polylineOptions);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.ROUND);
                }*/
            }
        }
    }

    public void setmetricesdata()
    {
        if(isinbackground)
            return;

        if(! isgraphicopen && (! ismediaplayer))
            return;


        hitcounter++;
        if(hitcounter == 1)
        {
            if(layout_googlemap.getVisibility() == View.GONE)
                layout_googlemap.setVisibility(View.VISIBLE);

            if(layout_locationanalytics.getVisibility() == View.GONE)
                layout_locationanalytics.setVisibility(View.VISIBLE);

            if(layout_orientationanalytics.getVisibility() == View.GONE)
                layout_orientationanalytics.setVisibility(View.VISIBLE);
        }

        common.locationAnalyticsdata(txt_latitude,txt_longitude,
                txt_altitude,txt_heading,txt_orientation,txt_speed,txt_address);


        if(hitcounter == 3)
        {
            StringBuilder mformatbuilder = new StringBuilder();
            mformatbuilder.append("X-Axis  "+System.getProperty("line.separator")+
                    xdata.getinstance().getSetting(config.acceleration_x));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+"Y-Axis  "+
                    System.getProperty("line.separator")+xdata.getinstance().getSetting(config.acceleration_y));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+"Z-Axis  "+
                    System.getProperty("line.separator")+xdata.getinstance().getSetting(config.acceleration_z)
            );

            txt_xaxis.setText(mformatbuilder.toString());
        }

        if(hitcounter == 4)
        {
            if(layout_phoneanalytics.getVisibility() == View.GONE)
                layout_phoneanalytics.setVisibility(View.VISIBLE);

            if(layout_encryption.getVisibility() == View.GONE)
                layout_encryption.setVisibility(View.VISIBLE);

            if(layout_datalatency.getVisibility() == View.GONE)
                layout_datalatency.setVisibility(View.VISIBLE);

            if(photocapture)
            {
                layout_soundinformation.setVisibility(View.GONE);

            }
            else
            {
                layout_soundinformation.setVisibility(View.VISIBLE);

            }
        }

        if(hitcounter == 5)
        {

            StringBuilder mformatbuilder = new StringBuilder();
            mformatbuilder.append(config.PhoneType+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.PhoneType)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.OSversion+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.OSversion)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.ScreenSize+System.getProperty("line.separator")+
                    xdata.getinstance().getSetting(config.ScreenWidth) +"*" +xdata.getinstance().getSetting(config.ScreenHeight));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Brightness+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Brightness)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Bluetooth+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Bluetooth)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Language+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Language)));

            txt_phonetype.setText(mformatbuilder.toString());
        }

        if(hitcounter == 6)
        {
            StringBuilder mformatbuilder = new StringBuilder();
            mformatbuilder.append(config.CellProvider+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.CellProvider)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.WIFINetwork+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.WIFINetwork)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Country+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Country)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.TimeZone+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.TimeZone)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.LocalTime+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.LocalTime)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.SystemUptime+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.SystemUptime)));

            txt_cellprovider.setText(mformatbuilder.toString());

        }

        if(hitcounter == 7)
        {
            hitcounter=0;
            StringBuilder mformatbuilder = new StringBuilder();
            mformatbuilder.append(config.Connectionspeed+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Connectionspeed)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.GPSAccuracy+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.GPSAccuracy)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.CPUUsage+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.CPUUsage)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.MemoryUsage+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.MemoryUsage)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.StorageAvailable+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.StorageAvailable)));

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Battery+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Battery)));

            txt_connection_speed.setText(mformatbuilder.toString());
        }

        String latitude=xdata.getinstance().getSetting(config.Latitude);
        String longitude=xdata.getinstance().getSetting(config.Longitude);
        if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
            populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
        if(ismediaplayer || photocapture)
        {
            if(xdata.getinstance().getSetting(config.orientation).toString().trim().length() > 0)
            {
                String strdegree=xdata.getinstance().getSetting(config.orientation);
                if(! strdegree.equals(lastsavedangle))
                {
                    if(strdegree.equalsIgnoreCase("NA"))
                        strdegree="0.0";

                    int degree = Math.abs((int)Double.parseDouble(strdegree));
                    rotatecompass(degree);
                }
                lastsavedangle=strdegree;

            }
        }

        txt_data_hash.setText(valuehash);
        txt_hash_formula.setText(hashmethod);
        txt_dictionary_hash.setText(metahash);
        txt_videoupdatetransactionid.setText(videostarttransactionid);
    }

        public void getencryptiondata(String hashmethod, String videostarttransactionid, String valuehash, String metahash){

                this.hashmethod = hashmethod;
                this.videostarttransactionid = videostarttransactionid;
                this.valuehash = valuehash;
                this.metahash = metahash;
        }


    @Override
    public int getlayoutid() {
        return R.layout.fragment_graphicalfragment;
    }

    private void loadmap() {
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.googlemap, mapFragment).commit();

        if (mgooglemap == null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setMap(googleMap);
                }
            });
        } else {
            setMap(mgooglemap);
        }
    }

    private void setMap(GoogleMap googleMap) {
        this.mgooglemap = googleMap;
        this.mgooglemap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        /*points.add(new LatLng(26.235896,74.24235896));
        points.add(new LatLng(26.34235896,74.24235896));
        points.add(new LatLng(26.232435896,74.424235896));
        points.add(new LatLng(26.22235896,74.2325896));
        points.add(new LatLng(26.454235896,74.24235896));
        points.add(new LatLng(26.534235896,74.24235896));
        points.add(new LatLng(26.5565235896,74.42235896));
        points.add(new LatLng(26.67235896,74.23235896));
        points.add(new LatLng(26.878235896,74.22135896));
        points.add(new LatLng(26.45235896,74.23335896));
        points.add(new LatLng(26.33235896,74.22335896));
        polylineOptions.addAll(points);
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(20);
        Polyline polyline =mgooglemap.addPolyline(polylineOptions);
        //polyline.setEndCap(new RoundCap());
        //polyline.setJointType(JointType.ROUND);
         polyline.setPattern(PATTERN_POLYLINE_DOTTED);*/

    }

    //https://stackoverflow.com/questions/32905939/how-to-customize-the-polyline-in-google-map/46559529

    private void populateUserCurrentLocation(final LatLng location) {
        // DeviceUser user = DeviceUserManager.getInstance().getUser();
        if (mgooglemap == null)
            return;

        googlemap.setVisibility(View.VISIBLE);


        //polyline.setPattern(PATTERN_POLYLINE_DOTTED);

        /*Polygon polygon = mgooglemap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(location));
        polygon.setTag("beta");


        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);*/

        mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 15));
        if (ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            if(! ismediaplayer)
            {
                mgooglemap.setMyLocationEnabled(true);
            }
            else
            {
                mgooglemap.setMyLocationEnabled(false);
            }
            mgooglemap.getUiSettings().setZoomControlsEnabled(false);
            mgooglemap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void getaudiowave() {
        waveHandler=new Handler();
        waveRunnable = new Runnable() {
            @Override
            public void run() {

                    try {

                        if((gethelper().getrecordingrunning() || ismediaplayer))
                        {
                            int x = mNoise.getAmplitudevoice();

                            if(soundamplitudealue.isEmpty()){
                                soundamplitudealue = String.valueOf(x);
                            }else{
                                soundamplitudealue = soundamplitudealue +","+String.valueOf(x);
                            }


                            myvisualizerview.addAmplitude(x); // update the VisualizeView

                            myvisualizerview.invalidate();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                waveHandler.postDelayed(this, 40);
            }
        };
        waveHandler.post(waveRunnable);
    }

    private void start() {

        try {

            if (mNoise != null)
                mNoise.stop();

            mNoise = new noise();

            if (mNoise != null)
                mNoise.start();

            try {
                if (mNoise != null)
                {
                    myvisualizerview.setVisibility(View.VISIBLE);
                    getaudiowave();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e)
        {

        }

    }

    private void stop() {
        Log.e("noise", "==== Stop noise Monitoring===");
        try {
            if(mNoise != null)
            {
                mNoise.stop();
                //myvisualizerview.updateAmplitude((float) 0,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(waveHandler != null && waveRunnable != null)
            waveHandler.removeCallbacks(waveRunnable);

        try {
            stop();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void setdrawerproperty(boolean isgraphicopen)
    {
        this.isgraphicopen=isgraphicopen;
    }

    public void setphotocapture(boolean photocapture)
    {
        this.photocapture=photocapture;
    }

    public void setchartdata()
    {
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


      //  Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(150f, "");
        ll1.setLineWidth(0f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(0f);
       // ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-30f, "");
        ll2.setLineWidth(0f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(0f);
      //  ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(30f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        xAxis.setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(5, 25);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500);

        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
// mChart.invalidate();
    }


    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val, 0));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 0f, 0f);
            set1.enableDashedHighlightLine(10f, 0f, 0f);
            //set1.setColor(Color.BLACK);
            //set1.setCircleColor(Color.BLACK);
            set1.setDrawCircles(false);
            set1.setLineWidth(1f);
            set1.setCircleRadius(0f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == maccelerometersensormanager) {
            double deltaX = event.values[0];
            double deltaY = event.values[1];
            double deltaZ = event.values[2];

            /*txt_xaxis.setText("X-Axis \n"+deltaX);
            txt_yaxis.setText("Y-Axis \n"+deltaY);
            txt_zaxis.setText("Z-Axis \n"+deltaZ);*/

            /*if(isgraphicopen && (gethelper().getrecordingrunning() || ismediaplayer))
            {
                String x = String.valueOf(new DecimalFormat("#.#").format(deltaX));
                String y = String.valueOf(new DecimalFormat("#.#").format(deltaY));
                String z = String.valueOf(new DecimalFormat("#.#").format(deltaZ));

                txt_xaxis.setText("X-Axis \n"+x);
                txt_yaxis.setText("Y-Axis \n"+y);
                txt_zaxis.setText("Z-Axis \n"+z);
            }*/
        }
        else
        {
            if(gethelper().getrecordingrunning())
            {
                int degree = Math.round(event.values[0]);
              //  Log.e("degree ",""+degree);
                rotatecompass(degree);
            }
        }
    }

    public void rotatecompass(int degree)
    {
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);
        ra.setFillAfter(true);
        img_compass.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onResume() {
        super.onResume();
        isinbackground=false;
        // for the system's orientation sensor registered listeners
        msensormanager.registerListener(this, msensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        maccelerometersensormanager = msensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensormanager.registerListener(this, maccelerometersensormanager, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        // to stop the listener and save battery
        msensormanager.unregisterListener(this);
    }

    private void initAudio() {

        if(mMediaPlayer != null){

            applicationviavideocomposer.getactivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            myvisualizerviewmedia.setVisibility(View.VISIBLE);

            setupVisualizerFxAndUI();
            // Make sure the visualizer is enabled only when you actually want to
            // receive data, and
            // when it makes sense to receive data.
            mVisualizer.setEnabled(true);
            // When the stream ends, we don't need to collect any more data. We
            // don't do this in
            // setupVisualizerFxAndUI because we likely want to have more,
            // non-Visualizer related code
            // in this callback.
            mMediaPlayer
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            //mVisualizer.setEnabled(false);
                        }
                    });
            //mMediaPlayer.start();
        }
    }

    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

        if(mVisualizer != null && Visualizer.getCaptureSizeRange() != null && Visualizer.getCaptureSizeRange().length > 0)
        {
            try {
                mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                mVisualizer.setDataCaptureListener(
                        new Visualizer.OnDataCaptureListener() {
                            public void onWaveFormDataCapture(Visualizer visualizer,
                                                              byte[] bytes, int samplingRate) {

                              // int waveform = visualizer.getWaveForm(bytes);
                              // Log.e("waveform=",""+waveform);

                                double rms = 0;
                                int[] formattedVizData = getFormattedData(bytes);
                                if (formattedVizData.length > 0) {
                                    for (int i = 0; i < formattedVizData.length; i++) {
                                        int val = formattedVizData[i];
                                        rms += val * val;
                                    }
                                    rms = Math.sqrt(rms / formattedVizData.length);
                                }

                                int amplitude = (int)rms;

                                if(rms>0 && myvisualizerview.width != 0){
                                    int x= amplitude*100;
                                    myvisualizerview.addAmplitude(x); // update the VisualizeView
                                    myvisualizerview.invalidate();
                                }

                                //Log.e("waveform=",""+rms);

                                myvisualizerviewmedia.updateVisualizer(bytes);

                            }

                            public void onFftDataCapture(Visualizer visualizer,
                                                         byte[] bytes, int samplingRate) {

                                int i = mVisualizer.getFft(bytes);

                                Log.e("fftdataform=","" + i);

                            }
                        }, Visualizer.getMaxCaptureRate() / 2, true, false);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public void setmediaplayer(boolean ismediaplayer , MediaPlayer mediaPlayer){
        this.mMediaPlayer = mediaPlayer;
        this.ismediaplayer = ismediaplayer;
        setvisualizer();
    }

    public void setvisualizer(){
        if(ismediaplayer){
            initAudio();
        }else{
            soundamplitudealue = "";
            start();
        }
    }



    public void getvisualizerwave(ArrayList<wavevisualizer> amplitudevalue) {
        if(myvisualizerview != null){
            if(myvisualizerview.getVisibility() == View.VISIBLE && myvisualizerview.width != 0){
               for (;position < amplitudevalue.size();position++){
                       Log.e("position",""+position);
                    myvisualizerview.addAmplitude(amplitudevalue.get(position).getVisulizervalue()); // update the VisualizeView
                    myvisualizerview.invalidate();

                   Log.e("wavevaluescomposer", ""+ position);

               }
            }
        }
    }


    public void getvisualizerwavecomposer(ArrayList<wavevisualizer> amplitudevalue) {
        if(myvisualizerview != null){
            if(myvisualizerview.getVisibility() == View.VISIBLE && myvisualizerview.width != 0){
               for (;position < amplitudevalue.size();position++){

                    myvisualizerview.addAmplitude(amplitudevalue.get(position).getVisulizervalue());
                    myvisualizerview.addAmplitude(50); // update the VisualizeView
                    myvisualizerview.addAmplitude(50); // update the VisualizeView
                    myvisualizerview.addAmplitude(50);// update the VisualizeView
                    myvisualizerview.invalidate();

                    Log.e("wavevaluescomposer", ""+ position);

               }
            }
        }
    }

    public void getvisualizerwavereader(String[] amplitudearray) {

        if(myvisualizerview != null){
            if(myvisualizerview.getVisibility() == View.VISIBLE && (amplitudearray.length > 0)){

                setvisualizerwave();

                    for (int i=0; i < amplitudearray.length; i++) {
                            String value = amplitudearray[i];
                            int ampliteudevalue = Integer.parseInt(value);
                            myvisualizerview.addAmplitude(ampliteudevalue); // update the VisualizeView
                            myvisualizerview.invalidate();
                }
            }
        }
    }

    public void setvisualizerwave(){
        if(myvisualizerview!=null){
            position = 0;
            myvisualizerview.clear();
        }

    }

    public int[] getFormattedData(byte[] rawVizData) {
        int[] arraydata=new int[rawVizData.length];
        for (int i = 0; i < arraydata.length; i++) {
            // convert from unsigned 8 bit to signed 16 bit
            int tmp = ((int) rawVizData[i] & 0xFF) - 128;
            arraydata[i] = tmp;
        }
        return arraydata;
    }

}




