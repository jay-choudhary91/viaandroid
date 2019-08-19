package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.satellitesdataadapter;
import com.deeptruth.app.android.adapter.toweritemadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.itemupdatelistener;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.celltowermodel;
import com.deeptruth.app.android.models.coloredpoint;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.satellites;
import com.deeptruth.app.android.sensor.AttitudeIndicator;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.utils.AnalogClock;
import com.deeptruth.app.android.utils.AnalogClockBlack;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.visualizeraudiorecorder;
import com.deeptruth.app.android.utils.visualizerview;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;
import com.deeptruth.app.android.views.customseekbar;
import com.deeptruth.app.android.views.verticalseekbar;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentgraphicaldrawer extends basefragment implements OnChartValueSelectedListener,
        OnChartGestureListener,SensorEventListener, Orientation.Listener {

    @BindView(R.id.txt_address)
    customfonttextview tvaddress;
    @BindView(R.id.txt_latitude)
    customfonttextview tvlatitude;
    @BindView(R.id.txt_longitude)
    customfonttextview tvlongitude;
    @BindView(R.id.txt_altitude)
    customfonttextview tvaltitude;
    @BindView(R.id.txt_speed)
    customfonttextview tvspeed;
    @BindView(R.id.txt_heading)
    customfonttextview tvheading;
    @BindView(R.id.txt_traveled)
    customfonttextview tvtraveled;
    @BindView(R.id.txt_xaxis)
    customfonttextview tvxaxis;
    @BindView(R.id.txt_yaxis)
    customfonttextview tvyaxis;
    @BindView(R.id.txt_zaxis)
    customfonttextview tvzaxis;
    @BindView(R.id.txt_phone)
    customfonttextview tvphone;
    @BindView(R.id.txt_network)
    customfonttextview tvnetwork;
    @BindView(R.id.txt_connection)
    customfonttextview tvconnection;
    @BindView(R.id.txt_version)
    customfonttextview tvversion;
    @BindView(R.id.txt_wifi)
    customfonttextview tvwifi;
    @BindView(R.id.txt_gps_accuracy)
    customfonttextview tvgpsaccuracy;
    @BindView(R.id.txt_screen)
    customfonttextview tvscreen;
    @BindView(R.id.txt_country)
    customfonttextview tvcountry;
    @BindView(R.id.txt_cpu_usage)
    customfonttextview tvcpuusage;
    @BindView(R.id.txt_brightness)
    customfonttextview tvbrightness;
    @BindView(R.id.txt_timezone)
    customfonttextview tvtimezone;
    @BindView(R.id.txt_memoryusage)
    customfonttextview tvmemoryusage;
    @BindView(R.id.txt_bluetooth)
    customfonttextview tvbluetooth;
    @BindView(R.id.txt_localtime)
    customfonttextview tvlocaltime;
    @BindView(R.id.txt_storagefree)
    customfonttextview tvstoragefree;
    @BindView(R.id.txt_language)
    customfonttextview tvlanguage;
    @BindView(R.id.txt_uptime)
    customfonttextview tvuptime;
    @BindView(R.id.txt_battery)
    customfonttextview tvbattery;
    @BindView(R.id.txt_date)
    customfonttextview tvdate;
    @BindView(R.id.txt_time)
    customfonttextview tvtime;
    @BindView(R.id.txt_videoupdatetransactionid)
    customfonttextview tvblockchainid;
    @BindView(R.id.txt_hash_formula)
    customfonttextview tvblockid;
    @BindView(R.id.txt_data_hash)
    customfonttextview tvblocknumber;
    @BindView(R.id.txt_dictionary_hash)
    customfonttextview tvmetahash;
    @BindView(R.id.txt_locationanalytics)
    customfonttextview tvlocationanalytics;
    @BindView(R.id.txt_locationtracking)
    customfonttextview tvlocationtracking;
    @BindView(R.id.txt_towerinfo)
    customfonttextview txt_towerinfo;
    @BindView(R.id.txt_orientation)
    customfonttextview tvorientation;
    @BindView(R.id.txt_Phoneanalytics)
    customfonttextview tvPhoneanalytics;
    @BindView(R.id.text_encryption)
    customfonttextview tvencryption;
    @BindView(R.id.text_dataletency)
    customfonttextview tvdataletency;
    @BindView(R.id.txt_degree)
    customfonttextview txtdegree;
    @BindView(R.id.txt_orientations)
    customfonttextview tvorientations;
    @BindView(R.id.txt_camera)
    customfonttextview tvcamera;
    @BindView(R.id.txt_picture_qty)
    customfonttextview tvpicture_qty;
    @BindView(R.id.txt_airoplan_mode)
    customfonttextview tvairoplan_mode;
    @BindView(R.id.txt_gps)
    customfonttextview tvgps;
    @BindView(R.id.txt_jailbroken)
    customfonttextview tvjailbroken;
    @BindView(R.id.txt_currency)
    customfonttextview tvcurrency;
    @BindView(R.id.txt_barometer)
    customfonttextview tvbarometer;
    @BindView(R.id.txt_deviceconnection)
    customfonttextview tvdeviceconnection;

    @BindView(R.id.txt_availablewifinetwork)
    customfonttextview txt_availablewifinetwork;
    @BindView(R.id.txt_Memory)
    TextView txt_Memory;
    @BindView(R.id.txt_cpuusage)
    TextView txt_cpuusage;
    @BindView(R.id.txtbattery)
    TextView txtbattery;
    @BindView(R.id.txt_videoaudiodata)
    TextView txt_videoaudiodata;
    @BindView(R.id.txt_valid)
    TextView txt_valid;
    @BindView(R.id.txt_caution)
    TextView txt_caution;
    @BindView(R.id.txt_invalid)
    TextView txt_invalid;
    @BindView(R.id.txt_validmeta)
    TextView txt_validmeta;
    @BindView(R.id.txt_cautionmeta)
    TextView txt_cautionmeta;
    @BindView(R.id.txt_invalidmeta)
    TextView txt_invalidmeta;
    @BindView(R.id.text_meta)
    TextView text_meta;
    @BindView(R.id.txt_connectioninformation)
    TextView txt_connectioninformation;
    @BindView(R.id.txt_timeinformation)
    TextView txt_timeinformation;
    @BindView(R.id.txt_phonetime)
    TextView txt_phonetime;
    @BindView(R.id.txt_worldclocktime)
    TextView txt_worldclocktime;

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.layout_datalatency)
    LinearLayout layout_datalatency;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_niddle)
    ImageView img_niddle;
    @BindView(R.id.attitude_indicator)
    AttitudeIndicator attitudeindicator;
    @BindView(R.id.view_latencyline)
    View view_latencyline;
    @BindView(R.id.img_phone_orientation)
    ImageView img_phone_orientation;
    @BindView(R.id.seekbar_transparency)
    IndicatorSeekBar seekbartransparency;
    @BindView(R.id.layout_constraint)
    RelativeLayout layout_constraint;
    @BindView(R.id.layout_transparency_meter)
    LinearLayout layout_transparency_meter;

    @BindView(R.id.phone_time_clock_white)
    AnalogClock phone_time_clock;
    @BindView(R.id.phone_time_clock_black)
    AnalogClockBlack phone_time_clock_black;

    @BindView(R.id.world_time_clock_white)
    AnalogClock world_time_clock;
    @BindView(R.id.world_time_clock_black)
    AnalogClockBlack world_time_clock_black;

    @BindView(R.id.txt_phone_time)
    TextView txt_phone_time;
    @BindView(R.id.txt_world_time)
    TextView txt_world_time;
    @BindView(R.id.pie_metadatachart)
    PieChart pie_metadatachart;
    @BindView(R.id.pie_videoaudiochart)
    PieChart pie_videoaudiochart;
    @BindView(R.id.chart_memoeyusage)
    PieChart chart_memoeyusage;
    @BindView(R.id.chart_cpuusage)
    PieChart chart_cpuusage;
    @BindView(R.id.chart_battery)
    PieChart chart_battery;
    @BindView(R.id.linechart_speed)
    LineChart linechart_speed;
    @BindView(R.id.linechart_traveled)
    LineChart linechart_traveled;
    @BindView(R.id.linechart_altitude)
    LineChart linechart_altitude;
    @BindView(R.id.txt_videoaudio_validframes)
    TextView txt_videoaudio_validframes;
    @BindView(R.id.txt_videoaudio_cautionframes)
    TextView txt_videoaudio_cautionframes;
    @BindView(R.id.txt_videoaudio_invalidframes)
    TextView txt_videoaudio_invalidframes;
    @BindView(R.id.txt_meta_validframes)
    TextView txt_meta_validframes;
    @BindView(R.id.txt_meta_cautionframes)
    TextView txt_meta_cautionframes;
    @BindView(R.id.txt_meta_invalidframes)
    TextView txt_meta_invalidframes;
    @BindView(R.id.linear_mediavideoaudio)
    LinearLayout linear_mediavideoaudio;
    @BindView(R.id.linear_mediametadata)
    LinearLayout linear_mediametadata;
    @BindView(R.id.seekbar_mediavideoaudio)
    customseekbar seekbar_mediavideoaudio;
    @BindView(R.id.seekbar_mediametadata)
    customseekbar seekbar_mediametadata;
    @BindView(R.id.linechart_connectionspeed)
    LineChart linechart_connectionspeed;
    @BindView(R.id.linechart_datatimedelay)
    LineChart linechart_datatimedelay;
    @BindView(R.id.txt_datatimedelay)
    TextView txt_datatimedelay;
    @BindView(R.id.linechart_gpsaccuracy)
    LineChart linechart_gpsaccuracy;
    @BindView(R.id.txt_mediainformation)
    TextView txt_mediainformation;
    @BindView(R.id.layout_mediametadata)
    LinearLayout layout_mediametadata;
    @BindView(R.id.vertical_slider_speed)
    verticalseekbar vertical_slider_speed;
    @BindView(R.id.vertical_slider_traveled)
    verticalseekbar vertical_slider_traveled;
    @BindView(R.id.vertical_slider_altitude)
    verticalseekbar vertical_slider_altitude;
    @BindView(R.id.vertical_slider_gpsaccuracy)
    verticalseekbar vertical_slider_gpsaccuracy;
    @BindView(R.id.vertical_slider_connectionspeed)
    verticalseekbar vertical_slider_connectionspeed;
    @BindView(R.id.vertical_slider_connectiondatatimedely)
    verticalseekbar vertical_slider_connectiondatatimedely;
    @BindView(R.id.myvisualizerview)
    visualizeraudiorecorder myvisualizerview;
    @BindView(R.id.barvisualizer)
    visualizerview barvisualizerview;
    @BindView(R.id.layout_soundiformation)
    LinearLayout layout_soundiformation;
    @BindView(R.id.view_connectionspeed)
    View view_connectionspeed;
    @BindView(R.id.view_datatimedelay)
    View view_datatimedelay;
    @BindView(R.id.view_gpsaccuracy)
    View view_gpsaccuracy;
    @BindView(R.id.recycler_towerlist)
    RecyclerView recycler_towerlist;
    @BindView(R.id.layout_towerinfo)
    LinearLayout layout_towerinfo;
    @BindView(R.id.recycler_satellite_itemlist)
    RecyclerView recycler_satellit;
    @BindView(R.id.txt_satellite_altitudes_at)
    TextView txt_satellite_altitudes_at;
    @BindView(R.id.layout_timeinformation)
    LinearLayout layout_timeinformation;
    @BindView(R.id.layout_locationanalytics)
    LinearLayout layout_locationanalytics;
    @BindView(R.id.layout_phoneanalytics)
    LinearLayout layout_phoneanalytics;
    @BindView(R.id.layout_connection)
    LinearLayout layout_connection;
    @BindView(R.id.layout_mediasummary)
    LinearLayout layout_mediasummary;
    @BindView(R.id.layout_encryptioninfo)
    LinearLayout layout_encryptioninfo;
    @BindView(R.id.img_drawer_encryption)
    ImageView img_drawer_encryption;
    @BindView(R.id.img_drawer_phone)
    ImageView img_drawer_phone;
    @BindView(R.id.img_drawer_sound)
    ImageView img_drawer_sound;
    @BindView(R.id.img_drawer_time)
    ImageView img_drawer_time;
    @BindView(R.id.img_drawer_location)
    ImageView img_drawer_location;
    @BindView(R.id.img_drawer_connection)
    ImageView img_drawer_connection;
    @BindView(R.id.img_drawer_summary)
    ImageView img_drawer_summary;
    @BindView(R.id.txt_world_date)
    TextView txt_world_date;
    @BindView(R.id.txt_phone_date)
    TextView txt_phone_date;
    @BindView(R.id.txt_sun)
    TextView txt_sun;
    @BindView(R.id.txt_moon)
    TextView txt_moon;

    View rootview;
    GoogleMap mgooglemap;
    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;
    public ScrollView scrollview_meta;
    ArrayList<Entry> speedgraphitems = new ArrayList<>();
    ArrayList<Entry> travelledgraphitems = new ArrayList<>();
    ArrayList<Entry> altitudegraphitems = new ArrayList<>();
    ArrayList<Entry> connectionspeedvalues = new ArrayList<>();
    ArrayList<Entry> connectiondatadelayvalues = new ArrayList<>();
    ArrayList<Entry> gpsaccuracyvalues = new ArrayList<>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private boolean isdatacomposing=true,isrecodrunning=false,isfromdrawer=true,setdefaultzoomlevel=true,clearmapneeded=false;
    String lastsavedangle="";
    private float currentDegree = 0f,lastzoomlevel=0;
    private Orientation mOrientation;
    private String[] transparentarray=common.gettransparencyvalues();
    int navigationbarheight = 0;
    arraycontainer arraycontainerformetric =null;
    String screenwidth,screenheight,satellitedate="",satellitedata="";

    ValueAnimator lastPulseAnimator=null;
    Circle mappulsatecircle =null;
    Marker locationmarker=null;
    LatLng usercurrentlocation=null;
    private Polyline mappathpolyline=null;
    private ArrayList<LatLng> mappathcoordinates=new ArrayList<>();
    toweritemadapter toweradapter;
    satellitesdataadapter satelliteadapter;
    ArrayList<celltowermodel> celltowers = new ArrayList<>();
    ArrayList<satellites> satelliteslist = new ArrayList<>();
    Marker mediacreatedpointmarker;
    int count = 0;
    Random random = new Random();
    float vlauetraveled = 100,valuespeed = 80,valuealtitude = 2000,valuegpsaccuracy = 25,valuedatatimedelay = 10,valueconnectionspeed=100;
    float traveledarray[] = {13,42,9,34,9,23,84,93,28,49,32,84,9,28,34,92,35,80,93,101,48,84,9,84,9,80,94,82,34,82,9,38,40,92,38,40,92,38,42,03,84,23,47,62,38,42,34,98,120,79,23,74,
            92,36,130,55,28,34,98,23,84,9,23,84,02,36,78,42,37,84,23,42,37,47,280,23,42,34,23,84,9,23,84,8,23,04,82,30,8,34,23,42,343,24,23,432,43,243,243,242,341,411,31,23,21,21,43,21,23,44,34,83,95,84,30,98,43,90};

    @Override
    public int getlayoutid() {
        return R.layout.frag_graphicaldrawer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootview == null)
        {
          rootview = super.onCreateView(inflater, container, savedInstanceState);
          ButterKnife.bind(this, rootview);
          applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
          msensormanager = (SensorManager) applicationviavideocomposer.getactivity().getSystemService(Context.SENSOR_SERVICE);
          scrollview_meta = (ScrollView) findViewById(R.id.scrollview_meta);
          phone_time_clock_black.setVisibility(View.GONE);
          world_time_clock_black.setVisibility(View.GONE);
          settextviewcolor();

          img_drawer_encryption.setColorFilter(Color.argb(255, 255, 255, 255));
          img_drawer_phone.setColorFilter(Color.argb(255, 255, 255, 255));
          img_drawer_sound.setColorFilter(Color.argb(255, 255, 255, 255));
          img_drawer_time.setColorFilter(Color.argb(255, 255, 255, 255));
          img_drawer_location.setColorFilter(Color.argb(255, 255, 255, 255));
          img_drawer_connection.setColorFilter(Color.argb(255, 255, 255, 255));
          img_drawer_summary.setColorFilter(Color.argb(255, 255, 255, 255));

          seekbar_mediavideoaudio.setEnabled(false);
          seekbar_mediametadata.setEnabled(false);
          vertical_slider_speed.setEnabled(false);
          vertical_slider_altitude.setEnabled(false);
          vertical_slider_traveled.setEnabled(false);
          vertical_slider_gpsaccuracy.setEnabled(false);
          vertical_slider_connectionspeed.setEnabled(false);
          vertical_slider_connectiondatatimedely.setEnabled(false);
          linear_mediametadata.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));
          linear_mediavideoaudio.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));

          //visiblity gone of media information
            layout_mediasummary.setVisibility(View.GONE);
            layout_mediametadata.setVisibility(View.GONE);
            txt_mediainformation.setVisibility(View.GONE);

          {
              toweradapter =new toweritemadapter(applicationviavideocomposer.getactivity(),celltowers) ;
              LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
              layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
              recycler_towerlist.setLayoutManager(layoutManager);
              ((DefaultItemAnimator) recycler_towerlist.getItemAnimator()).setSupportsChangeAnimations(false);
              recycler_towerlist.getItemAnimator().setChangeDuration(0);
              recycler_towerlist.setAdapter(toweradapter);
          }


          {
              satelliteadapter =new satellitesdataadapter(applicationviavideocomposer.getactivity(),satelliteslist,isfromdrawer) ;
              recycler_satellit.setLayoutManager(new GridLayoutManager(applicationviavideocomposer.getactivity(), 3));
              recycler_satellit.setAdapter(satelliteadapter);
          }


          TimeZone timezone = TimeZone.getDefault();
          String timezoneid=timezone.getID();
            phone_time_clock.settimezone(timezoneid, new itemupdatelistener() {
                @Override
                public void onitemupdate(Object object,Object timezoneobject) {
                    if(object != null)
                    {
                        Calendar calendar=(Calendar)object;
                        if(common.is24hourstimeformat())
                        {
                            txt_phone_time.setText(common.appendzero(calendar.get(Calendar.HOUR_OF_DAY))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                    +":"+common.appendzero(calendar.get(Calendar.SECOND))+" "+timezoneobject.toString());
                        }
                        else
                        {
                            txt_phone_time.setText(common.appendzero(calendar.get(Calendar.HOUR))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                    +":"+common.appendzero(calendar.get(Calendar.SECOND))+" "+timezoneobject.toString());
                        }

                    }
                }

                @Override
                public void onitemupdate(Object object, int type) {

                }
            });
            world_time_clock.settimezone("GMT", new itemupdatelistener() {
                @Override
                public void onitemupdate(Object object,Object timezoneobject) {
                    if(object != null)
                    {
                        Calendar calendar=(Calendar)object;
                        if(common.is24hourstimeformat())
                        {
                            txt_world_time.setText(common.appendzero(calendar.get(Calendar.HOUR_OF_DAY))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                    +":"+common.appendzero(calendar.get(Calendar.SECOND))+" GMT");
                        }
                        else
                        {
                            txt_world_time.setText(common.appendzero(calendar.get(Calendar.HOUR))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                    +":"+common.appendzero(calendar.get(Calendar.SECOND))+" GMT");
                        }

                    }
                }

                @Override
                public void onitemupdate(Object object, int type) {

                }
            });

            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();

            layout_transparency_meter.setVisibility(View.VISIBLE);
            //setmetadatavalue();
            mOrientation = new Orientation(applicationviavideocomposer.getactivity());

            seekbartransparency.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    setlayouttransparency(seekParams.progress);
                    float progress=seekParams.progress;
                    progress=progress/100;
                    if(progress <= 0.3)
                        googlemap.setAlpha(0.3f);
                    else
                        googlemap.setAlpha(progress);
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            if(! xdata.getinstance().getSetting(config.drawer_transparency).trim().isEmpty())
            {
                seekbartransparency.setProgress(Float.parseFloat(xdata.getinstance().getSetting(config.drawer_transparency)));
                float progress=Float.parseFloat(xdata.getinstance().getSetting(config.drawer_transparency));
                progress=progress/100;
                if(progress <= 0.3)
                    googlemap.setAlpha(0.3f);
                else
                    googlemap.setAlpha(progress);

            }
            else
            {
                seekbartransparency.setProgress(50f);
                googlemap.setAlpha(0.5f);
            }

            loadmap();
            setchartmargin(linechart_altitude);
            setchartmargin(linechart_speed);
            setchartmargin(linechart_traveled);

            halfpaichartdate(chart_memoeyusage);
            halfpaichartdate(chart_cpuusage);
            halfpaichartdate(chart_battery);

            setchartdata(linechart_speed,-5,200);
            setchartdata(linechart_altitude,-40,3000);
            setchartdata(linechart_traveled,-5,300);
            vertical_slider_speed.setMax(80);
            vertical_slider_altitude.setMax(2000);
            vertical_slider_traveled.setMax(100);

            initlinechart(linechart_connectionspeed,-3f,50f,true);
            initlinechart(linechart_datatimedelay,-3f,50f,true);
            initlinechart(linechart_gpsaccuracy,-18f,300f,true);
            vertical_slider_connectionspeed.setMax(50);
            vertical_slider_connectiondatatimedely.setMax(50);
            vertical_slider_gpsaccuracy.setMax(300);

            ViewTreeObserver observer = barvisualizerview.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    barvisualizerview.setBaseY(barvisualizerview.getHeight());
                    barvisualizerview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        return rootview;
    }

    public void setlayouttransparency(int progress)
    {
        xdata.getinstance().saveSetting(config.drawer_transparency,""+progress);
        progress= transparentarray.length-progress;
        layout_constraint.setBackgroundColor(Color.parseColor("#00000000"));

        setframeborder(layout_locationanalytics,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_location);
        setframeborder(layout_timeinformation,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_time);
        setframeborder(layout_soundiformation,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_sound);
        setframeborder(layout_phoneanalytics,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_phone);
        setframeborder(layout_connection,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_connection);
        setframeborder(layout_mediasummary,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_summary);
        setframeborder(layout_encryptioninfo,config.color_code_white,"#"+transparentarray[progress]+config.color_code_drawer_encryption);
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        //attitudeindicator.setAttitude(pitch, roll);
    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        if(isdatacomposing)
        {
            //float pitch = orientation[1] * -57;
            float roll = orientation[2] * -57;
           // attitudeindicator.setAttitude(pitch, roll);

            if(img_phone_orientation != null)
                img_phone_orientation.setRotation(roll);
        }
    }

    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
            try {
                if(isdatacomposing)
                {
                    if(isrecodrunning)
                    {
                        mappathcoordinates.add(latlng);
                        if(mappathpolyline == null)
                        {
                            mappathpolyline = mgooglemap.addPolyline(new PolylineOptions()
                                    .add(latlng,latlng).width(7).color(Color.BLUE));
                        }
                        else
                        {
                            mappathpolyline.setPoints(mappathcoordinates);
                        }

                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void zoomgooglemap(double latitude,double longitude)
    {
        if (mgooglemap == null || (! isdatacomposing))
            return;

        googlemap.setVisibility(View.VISIBLE);

        float zoomlevel = mgooglemap.getCameraPosition().zoom;
        if(setdefaultzoomlevel)
        {
            setdefaultzoomlevel=false;
            double iMeter = 0.5 * 1609.34;      // zoomlevel according to 0.5 miles.
            Circle circle = mgooglemap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).
                    radius(iMeter).strokeColor(Color.TRANSPARENT));
            circle.setVisible(false);
            int zoom=common.getzoomlevelfromcircle(circle);
            mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
        else
        {
            mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomlevel));
        }
    }

    public void setmetricesdata()
    {
            if(isdatacomposing)
            {
                if(gethelper().getcurrentfragment() instanceof videocomposerfragment ||
                        gethelper().getcurrentfragment() instanceof imagecomposerfragment ||
                        gethelper().getcurrentfragment() instanceof audiocomposerfragment)
                {
                    String latitudedegree  = xdata.getinstance().getSetting(config.Latitude);
                    String longitudedegree = xdata.getinstance().getSetting(config.Longitude);

                    if(! latitudedegree.isEmpty() && (! latitudedegree.equalsIgnoreCase("NA")))
                    {
                        String latitude = common.convertlatitude(Double.parseDouble(latitudedegree));
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.latitude),"\n"+latitude, tvlatitude);
                    }
                    else
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.latitude),"\n"+"NA", tvlatitude);
                    }

                    if(! longitudedegree.isEmpty() && (! longitudedegree.equalsIgnoreCase("NA")))
                    {
                        String longitude = common.convertlongitude(Double.parseDouble(longitudedegree));
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.longitude),"\n"+longitude, tvlongitude);
                    }
                    else
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.longitude),"\n"+"NA", tvlongitude);
                    }

                    if(common.getxdatavalue(xdata.getinstance().getSetting(config.Address)).equalsIgnoreCase("NA")) {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.address),": "+common.getxdatavalue(xdata.getinstance().getSetting(config.Address)), tvaddress);
                    }else{ // remove "/n" from address
                        common.setdrawabledata("",common.getxdatavalue(xdata.getinstance().getSetting(config.Address)), tvaddress);

                    }

                    String altitude=xdata.getinstance().getSetting(config.Altitude);
                    String speed=common.speedformatter(common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)));

                    String traveled=common.travelleddistanceformatter(xdata.getinstance().getSetting(config.distancetravelled));
                    String strconnectionspeed=xdata.getinstance().getSetting(config.Connectionspeed);
                    Log.e("connectionindisplay ",strconnectionspeed);
                    String gpsaccuracy=xdata.getinstance().getSetting(config.GPSAccuracy);
                    String connectiontimedelaystr=xdata.getinstance().getSetting(config.connectiondatadelay);

                    if(! altitude.isEmpty() && (! altitude.equalsIgnoreCase("NA")))
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude),"\n"+altitude, tvaltitude);
                    }
                    else
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude),"\n"+"NA", tvaltitude);
                    }

                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.xaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_x), tvxaxis);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.yaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_y), tvyaxis);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.zaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_z), tvzaxis);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.phone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.PhoneType)), tvphone);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.network),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.CellProvider)), tvnetwork);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.version),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.OSversion)), tvversion);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_barometer),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.barometer)), tvbarometer);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_orientation),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.screenorientatioin)), tvorientations);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_airplane_mode),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.airplanemode)), tvairoplan_mode);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_currency),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.devicecurrency)), tvcurrency);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.wifi),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.WIFINetwork)), tvwifi);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_gps),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.gpsonoff)), tvgps);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_jailbroken),"\n"+xdata.getinstance().getSetting(config.jailbroken), tvjailbroken);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_connection),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.deviceconnection)), tvdeviceconnection);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_picture_quality),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.picturequality)), tvpicture_qty);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_camera),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.camera)), tvcamera);

                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.screen),"\n"+xdata.getinstance().getSetting(config.ScreenWidth) +"*" +xdata.getinstance().getSetting(config.ScreenHeight), tvscreen);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.country),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Country)), tvcountry);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.brightness),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Brightness)), tvbrightness);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.timezone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.TimeZone)), tvtimezone);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.bluetooth),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Bluetooth)), tvbluetooth);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.localtime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.LocalTime)), tvlocaltime);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.storagefree),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.StorageAvailable)), tvstoragefree);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.language),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Language)), tvlanguage);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.uptime),"\n"+ common.systemuptime(common.getxdatavalue(xdata.getinstance().
                            getSetting(config.SystemUptime))), tvuptime);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.speed),
                            "\n"+ common.speedformatter(common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)))
                            , tvspeed);

                    if(xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0"))
                        gpsaccuracy="0";

                    if((! gpsaccuracy.trim().isEmpty()) && (! gpsaccuracy.equalsIgnoreCase("NA"))
                            && (! gpsaccuracy.equalsIgnoreCase("null")) && xdata.getinstance().getSetting("gpsenabled")
                            .equalsIgnoreCase("1"))
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                "\n"+gpsaccuracy+" feet", tvgpsaccuracy);
                    }
                    else
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                "\n"+ "NA" , tvgpsaccuracy);
                    }

                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),
                            "\n"+traveled, tvtraveled);

                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.connection),"\n"+
                            common.getxdatavalue(strconnectionspeed), tvconnection);

                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_time_delay),
                            "\n"+connectiontimedelaystr, txt_datatimedelay);

                    if(phone_time_clock != null)
                        phone_time_clock.setpostrecorddata(true,"");

                    if(world_time_clock != null)
                        world_time_clock.setpostrecorddata(true,"");

                    if(! xdata.getinstance().getSetting(config.phoneclockdate).isEmpty() && (! xdata.getinstance().getSetting(config.phoneclockdate).equalsIgnoreCase("NA")))
                        txt_phone_date.setText(common.getxdatavalue(xdata.getinstance().getSetting(config.phoneclockdate)));

                    if(! xdata.getinstance().getSetting(config.worldclockdate).isEmpty() && (! xdata.getinstance().getSetting(config.worldclockdate).equalsIgnoreCase("NA")))
                        txt_world_date.setText(common.getxdatavalue(xdata.getinstance().getSetting(config.worldclockdate)));

                    common.setdrawabledata("",common.getdate(), tvdate);
                    common.setdrawabledata("",common.gettime(), tvtime);

                    String latitude=xdata.getinstance().getSetting("lat");
                    String longitude=xdata.getinstance().getSetting("lng");

                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.sun_text)," "+applicationviavideocomposer.getactivity().getResources().getString(R.string.sun_direction), txt_sun);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.moon_text)," "+applicationviavideocomposer.getactivity().getResources().getString(R.string.moon_direction), txt_moon);


                    common.setdrawabledata("","\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.MemoryUsage)), tvmemoryusage);
                    common.setdrawabledata("","\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Battery)), tvbattery);
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.availablewifinetwork),"\n"+
                            common.getxdatavalue(xdata.getinstance().getSetting(config.availablewifis)), txt_availablewifinetwork);
                    if(chart_memoeyusage!= null)
                        sethalfpaichartData(chart_memoeyusage,common.getxdatavalue(xdata.getinstance().getSetting(config.MemoryUsage)));

                    if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                            (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                    {
                        populatelocationonmap(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                        drawmappoints(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                    }


                    if(chart_cpuusage!= null){
                        String cpuusagevalue = common.getxdatavalue(xdata.getinstance().getSetting(config.CPUUsage));
                        cpuusagevalue = cpuusagevalue.substring(cpuusagevalue.lastIndexOf(" ")+1);
                        if(cpuusagevalue.contains("total")){
                            sethalfpaichartData(chart_cpuusage,"33%");
                            common.setdrawabledata("","\n"+"33%", tvcpuusage);
                        }else{
                            sethalfpaichartData(chart_cpuusage,common.getxdatavalue(xdata.getinstance().getSetting(config.CPUUsage)));
                            common.setdrawabledata("","\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.CPUUsage)), tvcpuusage);
                        }
                    }
                    if(chart_battery!= null)
                        sethalfpaichartData(chart_battery,common.getxdatavalue(xdata.getinstance().getSetting(config.Battery)));

                    if(isrecodrunning)
                    {

                        setvisibility(false);
                        showhideverticalbar(false);
                        {
                            Float connectionspeed=0.0f;
                            if((! strconnectionspeed.trim().isEmpty()) && (! strconnectionspeed.equalsIgnoreCase("null")) &&
                                    (! strconnectionspeed.equalsIgnoreCase("NA")))
                            {
                                String[] array=strconnectionspeed.split(" ");
                                if(array.length >0)
                                    connectionspeed=Float.parseFloat(array[0]);
                            }
                            if(connectionspeed <= 1)
                            {
                                int newvalue=Math.round(connectionspeed);
                                connectionspeed=(float)newvalue;
                            }

                            setlinechartdata(linechart_connectionspeed,connectionspeed,connectionspeedvalues,"linechart_connectionspeed");
                        }

                        {
                            Float connectiondelay=0.0f;
                            if((! connectiontimedelaystr.trim().isEmpty()) && (! connectiontimedelaystr.equalsIgnoreCase("null")) &&
                                    (! connectiontimedelaystr.equalsIgnoreCase("NA")))
                            {
                                String[] array=connectiontimedelaystr.split(" ");
                                if(array.length >0)
                                    connectiondelay=Float.parseFloat(array[0]);
                            }
                            if(connectiondelay <= 1)
                            {
                                int newvalue=Math.round(connectiondelay);
                                connectiondelay=(float)newvalue;
                            }
                            setlinechartdata(linechart_datatimedelay,connectiondelay,connectiondatadelayvalues,"linechart_datatimedelay");
                        }

                        {
                            String gps=xdata.getinstance().getSetting(config.GPSAccuracy);
                            Float gpsaccuracydata=0.0f;
                            if((! gps.trim().isEmpty()) && (! gps.equalsIgnoreCase("null")) &&
                                    (! gps.equalsIgnoreCase("NA")))
                            {
                                String[] array=gps.split(" ");
                                if(array.length >0)
                                    gpsaccuracydata=Float.parseFloat(array[0]);

                            }
                            setlinechartdata(linechart_gpsaccuracy,gpsaccuracydata,gpsaccuracyvalues,"linechart_gpsaccuracy");
                        }

                        {
                            Float data=0.0f;
                            if((! speed.trim().isEmpty()) && (! speed.equalsIgnoreCase("null")) &&
                                    (! speed.equalsIgnoreCase("NA")))
                            {
                                String[] array=speed.split(" ");
                                if(array.length >0)
                                    data=Float.parseFloat(array[0]);

                            }
                            setspeedtraveledaltitudechart(linechart_speed,data,speedgraphitems,"linechart_speed");
                        }

                        {
                            Float data=0.0f;
                            if((! traveled.trim().isEmpty()) && (! traveled.equalsIgnoreCase("null")) &&
                                    (! traveled.equalsIgnoreCase("NA")))
                            {
                                String[] array=traveled.split(" ");
                                if(array.length >0)
                                    data=Float.parseFloat(array[0]);

                            }
                            setspeedtraveledaltitudechart(linechart_traveled,data,travelledgraphitems,"linechart_traveled");
                        }

                        {
                            Float data=0.0f;
                            if((! altitude.trim().isEmpty()) && (! altitude.equalsIgnoreCase("null")) &&
                                    (! altitude.equalsIgnoreCase("NA")))
                            {
                                String[] array=altitude.split(" ");
                                if(array.length >0)
                                    data=Float.parseFloat(array[0]);
                            }
                            setspeedtraveledaltitudechart(linechart_altitude,data,altitudegraphitems,"linechart_altitude");
                        }

                        /*common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.blockchainid)), tvblockchainid);
                        common.setdrawabledata("", " "+common.getxdatavalue(xdata.getinstance().getSetting(config.datahash)), tvblocknumber);
                        common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.matrichash)), tvmetahash);*/

                        common.setdrawabledata("","NA", tvblockchainid);
                        common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.hashformula)), tvblockid);
                        common.setdrawabledata("", "NA", tvblocknumber);
                        common.setdrawabledata("","NA", tvmetahash);

                    }
                    else if(! isrecodrunning)
                    {
                        //myvisualizerview.clear();
                        setvisibility(true);
                        //layout_soundiformation.setVisibility(View.GONE);
                        showhideverticalbar(true);
                        updateverticalsliderlocationdata(speed,vertical_slider_speed);
                        updateverticalsliderlocationdata(altitude,vertical_slider_altitude);
                        updateverticalsliderlocationdata(traveled,vertical_slider_traveled);

                        updateverticalsliderlocationdata(gpsaccuracy,vertical_slider_gpsaccuracy);
                        updateverticalsliderlocationdata(strconnectionspeed,vertical_slider_connectionspeed);
                        updateverticalsliderlocationdata(connectiontimedelaystr,vertical_slider_connectiondatatimedely);

                        if(mappathpolyline != null)
                            mappathpolyline.remove();

                        mappathpolyline=null;
                        mappathcoordinates.clear();
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),
                                "\n"+common.travelleddistanceformatter("0.0"), tvtraveled);

                        if(altitudegraphitems.size() > 0 || speedgraphitems.size() > 0 || travelledgraphitems.size() > 0)
                        {
                            altitudegraphitems.clear();
                            speedgraphitems.clear();
                            travelledgraphitems.clear();
                            linechart_speed.clear();
                            linechart_traveled.clear();
                            linechart_altitude.clear();
                        }

                        if(connectionspeedvalues.size() > 0 || connectiondatadelayvalues.size() > 0 || gpsaccuracyvalues.size() > 0)
                            clearlinegraphs();

                        tvblockchainid.setText("");
                        tvblockid.setText("");
                        tvblocknumber.setText("");
                        tvmetahash.setText("");
                    }


                    showsatellitesinfo(xdata.getinstance().getSetting(config.item_satellitesdate),
                            xdata.getinstance().getSetting(config.item_satellitesdata));

                    String towerinfo = xdata.getinstance().getSetting(config.json_towerlist);
                    if(towerinfo.trim().length() == 0)
                    {
                        celltowers.clear();
                        if(toweradapter != null)
                            toweradapter.notifyDataSetChanged();
                        if(layout_towerinfo != null)
                            layout_towerinfo.setVisibility(View.GONE);
                    }
                    else if(towerinfo.trim().length() > 0)
                    {
                        try
                        {
                            JSONArray jsonArray=new JSONArray(towerinfo);
                            if(jsonArray.length() > 0 && (celltowers.size() != jsonArray.length()))
                            {
                                celltowers.clear();
                                toweradapter.notifyDataSetChanged();

                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    celltowermodel model=new celltowermodel();
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    if(object.has("mnc"))
                                        model.setMnc(Integer.parseInt(object.getString("mnc")));
                                    if(object.has("mcc"))
                                        model.setMcc(Integer.parseInt(object.getString("mcc")));
                                    if(object.has("network"))
                                        model.setNetwork(object.getString("network"));
                                    if(object.has("country"))
                                        model.setCountry(object.getString("country"));
                                    if(object.has("iso"))
                                        model.setIso(object.getString("iso"));
                                    if(object.has("dbm"))
                                        model.setDbm(Integer.parseInt(object.getString("dbm")));
                                    if(object.has("country_code"))
                                        model.setCountrycode(object.getString("country_code"));
                                    if(object.has("cid"))
                                        model.setCid(Integer.parseInt(object.getString("cid")));
                                    celltowers.add(model);
                                }
                            }
                            toweradapter.notifyDataSetChanged();
                            if(celltowers.size() > 0)
                                layout_towerinfo.setVisibility(View.VISIBLE);
                            else
                                layout_towerinfo.setVisibility(View.GONE);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

            }

            if(xdata.getinstance().getSetting(config.orientation).trim().length() > 0)
            {
                String strdegree=xdata.getinstance().getSetting(config.orientation);
                if(! strdegree.equals(lastsavedangle))
                {
                    if(strdegree.equalsIgnoreCase("NA"))
                        strdegree="0.0";
                }
                lastsavedangle=strdegree;
            }

            if(! xdata.getinstance().getSetting(config.attitude_data).trim().isEmpty() && (! xdata.getinstance().getSetting(config.attitude_data).equalsIgnoreCase("NA")))
            {
                try {
                    float[] adjustedRotationMatrix = new float[9];
                    String attitude = xdata.getinstance().getSetting(config.attitude_data);
                    String[] attitudearray = attitude.split(",");
                    for(int i = 0 ;i< attitudearray.length;i++){
                        //float val = (float) (Math.random() * 20) + 3;
                        adjustedRotationMatrix[i]=Float.parseFloat(attitudearray[i]);
                    }
                    // Transform rotation matrix into azimuth/pitch/roll
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(adjustedRotationMatrix, orientation);

                    //float pitch = orientation[1] * -57;
                    float roll = orientation[2] * -57;

                    if(img_phone_orientation != null)
                        img_phone_orientation.setRotation(roll);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
    }

    public void showsatellitesinfo(String date,String data)
    {
        txt_satellite_altitudes_at.setText(" "+date);

        try
        {
            if(! data.trim().isEmpty() && (! satellitedata.equalsIgnoreCase(data)))
            {
                satelliteslist.clear();
                satelliteadapter.notifyDataSetChanged();
                JSONArray array=new JSONArray(data);
                for(int i=0;i<array.length();i++)
                {
                    String number="",altitude="",name="";
                    JSONObject object=array.getJSONObject(i);
                    if(object.has("number"))
                        number=object.getString("number");
                    if(object.has("altitude"))
                        altitude=object.getString("altitude");
                    if(object.has("name"))
                        name=object.getString("name");

                    satelliteslist.add(new satellites(number,altitude,name));
                }
                satelliteadapter.notifyDataSetChanged();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        satellitedate=date;
        satellitedata=data;
    }

    public void setsoundinformation(int ampletudevalue,int decibelvalue,boolean issoundwaveshow){

        if(layout_soundiformation == null)
            return;

        if(issoundwaveshow){
            layout_soundiformation.setVisibility(View.VISIBLE);

            if(myvisualizerview != null){
                myvisualizerview.addAmplitude(ampletudevalue); // update the VisualizeView
                myvisualizerview.invalidate();
            }

            if(barvisualizerview!=null)
                barvisualizerview.receive(decibelvalue);
        }else{
            if(myvisualizerview != null)
                    myvisualizerview.clear();

            layout_soundiformation.setVisibility(View.GONE);

        }

    }

    public void updateverticalsliderlocationdata(String value, verticalseekbar seekbar)
    {
        if(seekbar == null)
            return;

        seekbar.setProgress(0);
        if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA")) &&
                (! value.equalsIgnoreCase("null")))
        {
            String[] array=value.split(" ");
            if(array.length > 0)
            {
                try {
                    Float data=Float.parseFloat(array[0]);
                    seekbar.setProgress(Math.round(data));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        seekbar.updateThumb();
    }

    public void clearlinegraphs()
    {
        if(linechart_connectionspeed != null)
            linechart_connectionspeed.clear();

        if(linechart_datatimedelay != null)
            linechart_datatimedelay.clear();

        if(linechart_gpsaccuracy != null)
            linechart_gpsaccuracy.clear();

        if(linechart_speed != null)
            linechart_speed.clear();

        if(linechart_traveled != null)
            linechart_traveled.clear();

        if(linechart_altitude != null)
            linechart_altitude.clear();

        connectionspeedvalues.clear();
        connectiondatadelayvalues.clear();
        gpsaccuracyvalues.clear();
        speedgraphitems.clear();
        travelledgraphitems.clear();
        speedgraphitems.clear();
    }

    public void setrecordrunning(boolean isrecodrunning)
    {
        this.isrecodrunning=isrecodrunning;
        vlauetraveled = 100;valuespeed = 80;valuealtitude = 20000;valuegpsaccuracy = 25;valuedatatimedelay = 10;valueconnectionspeed=100;
        clearmapneeded=true;
    }

    public void setdraweropen(boolean isdraweropen)
    {

    }

    public void cleargooglemap()
    {
        if(mgooglemap != null)
        {
            mgooglemap.clear();

            if(mediacreatedpointmarker != null)
            {
                mediacreatedpointmarker.remove();
                mediacreatedpointmarker=null;
            }

            if(lastPulseAnimator != null)
            {
                lastPulseAnimator.cancel();
                lastPulseAnimator=null;
            }

            if(mappulsatecircle != null && mgooglemap != null)
            {
                mappulsatecircle.remove();
                mappulsatecircle=null;
                Log.e("PulsateRemove"," PulsateRemove");
            }
        }
    }

    public void setdatacomposing(boolean isdatacomposing,String mediafilepath)
    {
        this.isdatacomposing=isdatacomposing;

        metricmainarraylist.clear();
        cleargooglemap();

        if(! isdatacomposing)
        {
            fetchmetadatafromdb(mediafilepath);
            if(metricmainarraylist != null && metricmainarraylist.size() > 0)
            {
                layout_mediasummary.setVisibility(View.VISIBLE);
                layout_mediametadata.setVisibility(View.VISIBLE);
                txt_mediainformation.setVisibility(View.VISIBLE);
                txt_phone_date.setText("");
                txt_world_date.setText("");
                showhideverticalbar(false);
                drawmappath();
                drawmediainformation();
            }
        }
        else
        {
            setdefaultzoomlevel=true;
            layout_mediasummary.setVisibility(View.GONE);
            layout_mediametadata.setVisibility(View.GONE);
            txt_mediainformation.setVisibility(View.GONE);
            showhideverticalbar(true);
            resetmediainformation();
        }
    }

    public void showhideverticalbar(boolean shouldtrue)
    {
        if(shouldtrue)
        {
            vertical_slider_connectiondatatimedely.setVisibility(View.VISIBLE);
            vertical_slider_connectionspeed.setVisibility(View.VISIBLE);
            vertical_slider_gpsaccuracy.setVisibility(View.VISIBLE);
            vertical_slider_traveled.setVisibility(View.VISIBLE);
            vertical_slider_altitude.setVisibility(View.VISIBLE);
            vertical_slider_speed.setVisibility(View.VISIBLE);
        }
        else
        {
            vertical_slider_connectiondatatimedely.setVisibility(View.GONE);
            vertical_slider_connectionspeed.setVisibility(View.GONE);
            vertical_slider_gpsaccuracy.setVisibility(View.GONE);
            vertical_slider_traveled.setVisibility(View.GONE);
            vertical_slider_altitude.setVisibility(View.GONE);
            vertical_slider_speed.setVisibility(View.GONE);
        }
    }

    public void resetmediainformation()
    {
        try
        {
            txt_videoaudio_validframes.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.zero_frames));
            txt_videoaudio_cautionframes.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.zero_frames));
            txt_videoaudio_invalidframes.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.zero_frames));
            txt_meta_validframes.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.zero_frames));
            txt_meta_cautionframes.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.zero_frames));
            txt_meta_invalidframes.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.zero_frames));

            if(seekbar_mediametadata != null)
                seekbar_mediametadata.setProgress(0);

            if(seekbar_mediavideoaudio != null)
                seekbar_mediavideoaudio.setProgress(0);

            linear_mediametadata.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));
            linear_mediavideoaudio.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));

            clearlinegraphs();

            if(linechart_speed != null)
            {
                linechart_speed.clear();
                linechart_speed.setVisibility(View.GONE);
            }

            if(linechart_traveled != null)
            {
                linechart_traveled.clear();
                linechart_traveled.setVisibility(View.GONE);
            }

            if(linechart_altitude != null)
            {
                linechart_altitude.clear();
                linechart_altitude.setVisibility(View.GONE);
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            linear_mediametadata.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            linear_mediavideoaudio.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void drawmappath()
    {
        if(mediacreatedpointmarker != null)
        {
            mediacreatedpointmarker.remove();
            mediacreatedpointmarker=null;
        }

        linechart_connectionspeed.clear();
        linechart_datatimedelay.clear();
        linechart_gpsaccuracy.clear();
        connectionspeedvalues.clear();
        connectiondatadelayvalues.clear();
        gpsaccuracyvalues.clear();
        speedgraphitems.clear();
        travelledgraphitems.clear();
        altitudegraphitems.clear();
        linechart_speed.clear();
        linechart_traveled.clear();
        linechart_altitude.clear();
       // layout_soundiformation.setVisibility(View.GONE);

        String linecolor="";
        List<coloredpoint> linepoints = new ArrayList<>();
        for (int i = 0; i < metricmainarraylist.size(); i++)
        {
            arraycontainer container=metricmainarraylist.get(i);
            linecolor=container.getColor();
            ArrayList<metricmodel> arraylist=container.getMetricItemArraylist();
            double latitude=0.0,longitude=0.0;
            for(int j=0;j<arraylist.size();j++)
            {
                metricmodel model=arraylist.get(j);

                if(model.getMetricTrackKeyName().equalsIgnoreCase("gpslatitude") && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    latitude=Double.parseDouble(model.getMetricTrackValue());
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase("gpslongitude") && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    longitude=Double.parseDouble(model.getMetricTrackValue());
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase("connectionspeed") && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    String connectionspeed=model.getMetricTrackValue();
                    String[] speedarray=connectionspeed.split(" ");
                    if(speedarray.length > 0)
                    {
                        try
                        {
                            float value=0f;
                            if(Float.parseFloat(speedarray[0]) <= 1)
                            {
                                int newvalue=Math.round(Float.parseFloat(speedarray[0]));
                                value=(float) newvalue;
                            }
                            else
                                value=Float.parseFloat(speedarray[0]);

                            if(connectionspeedvalues.size() > 0)
                                connectionspeedvalues.add(new Entry(connectionspeedvalues.size(), value, 0));
                            else
                                connectionspeedvalues.add(new Entry(connectionspeedvalues.size(), value, 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase(config.connectiondatadelay) && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    String connectiondatadelay=model.getMetricTrackValue();
                    String[] array=connectiondatadelay.split(" ");
                    if(array.length > 0)
                    {
                        try
                        {
                            float value=0f;
                            if(Float.parseFloat(array[0]) <= 1)
                            {
                                int newvalue=Math.round(Float.parseFloat(array[0]));
                                value=(float)newvalue;
                            }
                            else
                                value=Float.parseFloat(array[0]);

                            if(connectiondatadelayvalues.size() > 0)
                                connectiondatadelayvalues.add(new Entry(connectiondatadelayvalues.size(), value, 0));
                            else
                                connectiondatadelayvalues.add(new Entry(connectionspeedvalues.size(), value, 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase("gpsaccuracy") && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {

                    String gpsaccuracy=model.getMetricTrackValue();
                    String[] itemarray=gpsaccuracy.split(" ");
                    if(itemarray.length > 0)
                    {
                        try
                        {
                            float value=0f;
                            if(Float.parseFloat(itemarray[0]) <= 5)
                            {
                                int newvalue=Math.round(Float.parseFloat(itemarray[0]));
                                value=(float) newvalue;
                            }
                            else
                                value=Float.parseFloat(itemarray[0]);

                            if(gpsaccuracyvalues.size() > 0)
                                gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), value, 0));
                            else
                                gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), value, 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase("speed") && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    String speed=common.speedformatter(model.getMetricTrackValue());
                    String[] itemarray=speed.split(" ");
                    if(itemarray.length > 0)
                    {
                        try
                        {
                            float value=1.0f;
                            if(Float.parseFloat(itemarray[0]) != 0)
                                value=Float.parseFloat(itemarray[0]);

                            if(speedgraphitems.size() > 0)
                                speedgraphitems.add(new Entry(speedgraphitems.size(), value, 0));
                            else
                                speedgraphitems.add(new Entry(speedgraphitems.size(), value, 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase(config.distancetravelled) && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    String travelled=common.travelleddistanceformatter(model.getMetricTrackValue());
                    String[] itemarray=travelled.split(" ");
                    if(itemarray.length > 0)
                    {
                        try
                        {
                            float value=1.0f;
                            if(Float.parseFloat(itemarray[0]) != 0)
                                value=Float.parseFloat(itemarray[0]);

                            if(travelledgraphitems.size() > 0)
                                travelledgraphitems.add(new Entry(travelledgraphitems.size(), value, 0));
                            else
                                travelledgraphitems.add(new Entry(travelledgraphitems.size(), value, 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude) && (! model.getMetricTrackValue().trim().isEmpty()) && (! model.getMetricTrackValue().equalsIgnoreCase("NA"))
                        && (! model.getMetricTrackValue().equalsIgnoreCase("null")))
                {
                    String travelled=model.getMetricTrackValue();
                    String[] itemarray=travelled.split(" ");
                    if(itemarray.length > 0)
                    {
                        try
                        {
                            float value=1.0f;
                            if(Float.parseFloat(itemarray[0]) != 0)
                                value=Float.parseFloat(itemarray[0]);

                            if(altitudegraphitems.size() > 0)
                                altitudegraphitems.add(new Entry(altitudegraphitems.size(), value, 0));
                            else
                                altitudegraphitems.add(new Entry(altitudegraphitems.size(), value, 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            /*if(i == metricmainarraylist.size()/2)
                mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));*/

            if(linecolor.trim().length() == 0)
                linecolor="blue";

            linepoints.add(new coloredpoint(new LatLng(latitude,longitude), linecolor));
        }

        showtraveledpath(linepoints);

        // Source ->   https://stackoverflow.com/questions/17425499/how-to-draw-interactive-polyline-on-route-google-maps-v2-android

        linechart_speed.setVisibility(View.VISIBLE);
        linechart_traveled.setVisibility(View.VISIBLE);
        linechart_altitude.setVisibility(View.VISIBLE);

        if(connectionspeedvalues.size() > 0)
            setlinechartdata(linechart_connectionspeed,-1f,connectionspeedvalues,"linechart_connectionspeed");

        if(connectiondatadelayvalues.size() > 0)
            setlinechartdata(linechart_datatimedelay,-1f,connectiondatadelayvalues,"linechart_datatimedelay");

        if(gpsaccuracyvalues.size() > 0)
            setlinechartdata(linechart_gpsaccuracy,-1f,gpsaccuracyvalues,"linechart_gpsaccuracy");

        if(speedgraphitems.size() > 0)
            setspeedtraveledaltitudechart(linechart_speed,-1f,speedgraphitems,"linechart_speed");

        if(travelledgraphitems.size() > 0)
            setspeedtraveledaltitudechart(linechart_traveled,-1f,travelledgraphitems,"linechart_traveled");

        if(altitudegraphitems.size() > 0)
            setspeedtraveledaltitudechart(linechart_altitude,-1f,altitudegraphitems,"linechart_altitude");
    }

    public void addmediacreatedpointmarker(LatLng latLng) {
        if(mediacreatedpointmarker != null)
        {
            mediacreatedpointmarker.setPosition(latLng);
            return;
        }

        Drawable circleDrawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.ic_blue_location_circle);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable, 30, 30);

        mediacreatedpointmarker=mgooglemap.addMarker(new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(markerIcon)
        );
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, int width, int height) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showtraveledpath(final List<coloredpoint> points)
    {
        googlemap.post(new Runnable() {
            @Override
            public void run() {

                if (points.size() < 2)
                    return;

                int position = 0;
                coloredpoint currentPoint  = points.get(position);
                String currentColor = currentPoint.color;
                List<LatLng> currentSegment = new ArrayList<>();
                currentSegment.add(currentPoint.coords);
                position++;

                while (position < points.size()) {
                    currentPoint = points.get(position);

                    if (currentPoint.color.equalsIgnoreCase(currentColor)) {
                        currentSegment.add(currentPoint.coords);
                    } else {
                        currentSegment.add(currentPoint.coords);
                        mgooglemap.addPolyline(new PolylineOptions()
                                .addAll(currentSegment)
                                .color(Color.parseColor(common.getcolorbystring(currentColor)))
                                .width(7));
                        currentColor = currentPoint.color;
                        currentSegment.clear();
                        currentSegment.add(currentPoint.coords);
                    }

                    position++;
                }

                if(mgooglemap != null){
                    mgooglemap.addPolyline(new PolylineOptions()
                            .addAll(currentSegment)
                            .color(Color.parseColor(common.getcolorbystring(currentColor)))
                            .width(7));

                    common.mapzoomalongwithtraveledpath(applicationviavideocomposer.getactivity(),mgooglemap,currentSegment,
                            googlemap.getWidth(),googlemap.getHeight());
                }

            }
        });
    }


    public void drawmediainformation()
    {
        int validcount=0,cautioncount=0,invalidcount=0,lastsequenceno=0,sectioncount=0,unsent=0;
        String lastcolor="";
        ArrayList<String> colorsectioncount=new ArrayList<>();
        for (int i = 0; i < metricmainarraylist.size(); i++)
        {
            String strsequence=metricmainarraylist.get(i).getSequenceno();
            if(! strsequence.trim().isEmpty() && (! strsequence.equalsIgnoreCase("NA")) && (! strsequence.equalsIgnoreCase("null")))
            {
                int sequenceno=Integer.parseInt(strsequence);   // 15  30  45
                int sequencecount=sequenceno-lastsequenceno;   // 15  15  15
                if(metricmainarraylist.get(i).getColor().equalsIgnoreCase(config.color_green))
                {
                    validcount=validcount+sequencecount;  // 15  30   45
                }
                else if(metricmainarraylist.get(i).getColor().equalsIgnoreCase(config.color_yellow))
                {
                    cautioncount=cautioncount+sequencecount;
                }
                else if(metricmainarraylist.get(i).getColor().equalsIgnoreCase(config.color_red))
                {
                    invalidcount=invalidcount+sequencecount;
                }
                else if(metricmainarraylist.get(i).getColor().isEmpty())
                {
                    unsent=unsent+sequencecount;
                }
                lastsequenceno=sequenceno;   // 15  30   45

                sectioncount++;
                if(metricmainarraylist.get(i).getColor().trim().isEmpty())
                    metricmainarraylist.get(i).setColor(config.color_gray);

                if(! lastcolor.equalsIgnoreCase(metricmainarraylist.get(i).getColor()))
                {
                    sectioncount=0;
                    sectioncount++;
                    colorsectioncount.add(metricmainarraylist.get(i).getColor()+","+sectioncount);
                }
                else
                {
                    colorsectioncount.set(colorsectioncount.size()-1,metricmainarraylist.get(i).getColor()+","+sectioncount);
                }
                lastcolor=metricmainarraylist.get(i).getColor();
            }
        }

        try {
            linear_mediavideoaudio.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            linear_mediametadata.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        linear_mediametadata.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));
        linear_mediavideoaudio.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));

        for(int i=0;i<colorsectioncount.size();i++)
        {
            String item=colorsectioncount.get(i);
            if(! item.trim().isEmpty())
            {
                String[] itemarray=item.split(",");
                if(itemarray.length >= 2)
                {
                    String color=itemarray[0];
                    String weight=itemarray[1];
                    if(! weight.trim().isEmpty())
                    {
                        linear_mediavideoaudio.addView(getmediaseekbarbackgroundview(weight,color));
                        linear_mediametadata.addView(getmediaseekbarbackgroundview(weight,color));
                    }
                }
            }
        }

        txt_videoaudio_validframes.setText(validcount+" Frames");
        txt_videoaudio_cautionframes.setText(cautioncount+" Frames");
        txt_videoaudio_invalidframes.setText(invalidcount+" Frames");
        txt_meta_validframes.setText(validcount+" Frames");
        txt_meta_cautionframes.setText(cautioncount+" Frames");
        txt_meta_invalidframes.setText(invalidcount+" Frames");
        mediapiechartdata(pie_videoaudiochart,validcount,cautioncount,invalidcount,unsent);
        mediapiechartdata(pie_metadatachart,validcount,cautioncount,invalidcount,unsent);

        seekbar_mediavideoaudio.setPadding(0,0,0,0);
        seekbar_mediametadata.setPadding(0,0,0,0);
        seekbar_mediavideoaudio.setMax(metricmainarraylist.size());
        seekbar_mediametadata.setMax(metricmainarraylist.size());
    }

    public void setcurrentmediaposition(int currentmediaposition)
    {
        try
        {
            if(! isdatacomposing)
            {
                if (currentmediaposition <= metricmainarraylist.size() && metricmainarraylist.size() > 0) {
                    arraycontainerformetric = new arraycontainer();

                    if(currentmediaposition == metricmainarraylist.size()){
                        arraycontainerformetric = metricmainarraylist.get(currentmediaposition-1);
                    }else{
                        arraycontainerformetric = metricmainarraylist.get(currentmediaposition);
                    }

                    ArrayList<metricmodel> metricItemArraylist = arraycontainerformetric.getMetricItemArraylist();
                    String framecolor = arraycontainerformetric.getColor();
                    String latitude="",longitude="",satellitedate="",satellitedata="";
                    for (int j = 0; j < metricItemArraylist.size(); j++)
                    {
                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpslatitude))
                        {
                            latitude=metricItemArraylist.get(j).getMetricTrackValue();
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpslongitude))
                        {
                            longitude=metricItemArraylist.get(j).getMetricTrackValue();
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.heading))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
                            if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA"))
                                    && (! value.equalsIgnoreCase("null")))
                            {
                                int degree = Integer.parseInt(metricItemArraylist.get(j).getMetricTrackValue());
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),
                                        "\n"+metricItemArraylist.get(j).getMetricTrackValue()+"° " +common.getcompassdirection(degree), tvheading);
                                rotatecompass(degree);
                            }
                            else
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),"\n"+"NA", tvheading);
                            }

                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpslatitudedegree))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.latitude)
                                    ,"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvlatitude);
                        }else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpslongitudedegree))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.longitude)
                                    ,"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvlongitude);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_x)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.xaxis),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvxaxis);
                        }else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_y)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.yaxis),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvyaxis);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_z)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.zaxis),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvzaxis);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("phonetype")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.phone),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvphone);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("osversion")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.version),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvversion);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("devicelanguage")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.language),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvlanguage);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("country")) {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.country), "\n" + metricItemArraylist.get(j).getMetricTrackValue(), tvcountry);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("carrier")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.network),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvnetwork);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("freespace")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.storagefree),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvstoragefree);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.systemuptimeseconds)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.uptime),"\n"+common.systemuptime(metricItemArraylist.get(j).
                                    getMetricTrackValue()), tvuptime);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("brightness")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.brightness),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvbrightness);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("devicetime")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.phone_time),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvlocaltime);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("timezone")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.timezone),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvtimezone);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.address)){
                            common.setdrawabledata("",metricItemArraylist.get(j).getMetricTrackValue(), tvaddress);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("wifiname")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.wifi),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvwifi);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("devicecurrency")){

                            Log.e("devicecurrency",""+metricItemArraylist.get(j).getMetricTrackValue());
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_currency),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvcurrency);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("deviceconnection")){
                            Log.e("deviceconnection",""+metricItemArraylist.get(j).getMetricTrackValue());
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_connection),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvdeviceconnection);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("screenorientatioin")){
                            Log.e("screenorientatioin",""+metricItemArraylist.get(j).getMetricTrackValue());

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_orientation),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvorientations);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("barometer")){
                            Log.e("barometer",""+metricItemArraylist.get(j).getMetricTrackValue());

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_barometer),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvbarometer);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("airplanemode")){
                            Log.e("airplanemode",""+metricItemArraylist.get(j).getMetricTrackValue());

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_airplane_mode),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvairoplan_mode);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpsonoff")){
                            Log.e("gpsonoff",""+metricItemArraylist.get(j).getMetricTrackValue());

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_gps),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvgps);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.jailbroken)){
                            Log.e("jailbroken",""+metricItemArraylist.get(j).getMetricTrackValue());
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_jailbroken),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvjailbroken);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("camera")){
                            Log.e("camera",""+metricItemArraylist.get(j).getMetricTrackValue());
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_camera),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvcamera);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.picturequality)){
                            Log.e("picturequality",""+metricItemArraylist.get(j).getMetricTrackValue());
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_picture_quality),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvpicture_qty);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("screenwidth")){
                            screenwidth = metricItemArraylist.get(j).getMetricTrackValue();
                        }else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("screenheight")){
                            screenheight = metricItemArraylist.get(j).getMetricTrackValue();
                            common.setspannable(getResources().getString(R.string.screen),"\n"+screenwidth+"x"+screenheight, tvscreen);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.blockchainid)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.blockchain_id),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvblockchainid);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.hashformula)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.hash_formula),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvblockid);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.datahash)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.metadata),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvblocknumber);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.matrichash)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.mediahash),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvmetahash);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.phoneclockdate)){
                            txt_phone_date.setText( metricItemArraylist.get(j).getMetricTrackValue());
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.worldclockdate)){
                            txt_world_date.setText( metricItemArraylist.get(j).getMetricTrackValue());
                        }
                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.Battery))
                        {
                            if(chart_battery!= null)
                                sethalfpaichartData(chart_battery,metricItemArraylist.get(j).getMetricTrackValue());

                            common.setdrawabledata("","\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvbattery);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("memoryusage"))
                        {
                            if(chart_memoeyusage!= null)
                                sethalfpaichartData(chart_memoeyusage,metricItemArraylist.get(j).getMetricTrackValue());

                            common.setdrawabledata("","\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvmemoryusage);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.cpuusageuser))
                        {
                            if(chart_cpuusage!= null){
                                String cpuusagevalue = metricItemArraylist.get(j).getMetricTrackValue();
                                cpuusagevalue = cpuusagevalue.substring(cpuusagevalue.lastIndexOf(" ")+1);
                                if(cpuusagevalue.equalsIgnoreCase("total"))
                                {
                                    sethalfpaichartData(chart_cpuusage,"33%");
                                    common.setdrawabledata("","\n"+"33%", tvcpuusage);
                                }
                                else
                                {
                                    sethalfpaichartData(chart_cpuusage,metricItemArraylist.get(j).getMetricTrackValue());
                                    common.setdrawabledata("","\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvcpuusage);
                                }
                            }
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("devicedate")){
                            tvdate.setText(common.convertstringintodate(metricItemArraylist.get(j).getMetricTrackValue()));
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequencestarttime")){
                            tvtime.setText(common.getFormattedTime(metricItemArraylist.get(j).getMetricTrackValue()));
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequenceendtime"))
                            tvtime.setText(tvtime.getText()+" - "+common.getFormattedTime(metricItemArraylist.get(j).getMetricTrackValue()));

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.phoneclocktime))
                        {
                            String time=metricItemArraylist.get(j).getMetricTrackValue();
                            if(phone_time_clock != null)
                            {
                                if((! time.trim().isEmpty()) && (! time.equalsIgnoreCase("NA")))
                                    phone_time_clock.setpostrecorddata(false,time);

                                phone_time_clock.setfromdrawercontroller(true);
                            }
                        }
                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.worldclocktime))
                        {
                            String time=metricItemArraylist.get(j).getMetricTrackValue();
                            if(world_time_clock != null)
                            {
                                if(world_time_clock != null && (! time.trim().isEmpty()) && (! time.equalsIgnoreCase("NA")))
                                    world_time_clock.setpostrecorddata(false,time);

                                world_time_clock.setfromdrawercontroller(true);
                            }

                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.availablewifinetwork))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.availablewifinetwork),"\n"+
                                    metricItemArraylist.get(j).getMetricTrackValue(), txt_availablewifinetwork);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.speed))
                        {
                            String value = common.speedformatter(metricItemArraylist.get(j).getMetricTrackValue());
                            updateverticalsliderlocationdata(value,vertical_slider_speed);
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.speed)
                                    ,"\n"+ value, tvspeed);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude))
                        {
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_altitude);
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude),"\n"+value, tvaltitude);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.distancetravelled))
                        {
                            String value = common.travelleddistanceformatter(metricItemArraylist.get(j).getMetricTrackValue());
                            updateverticalsliderlocationdata(value,vertical_slider_traveled);
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),"\n"+value, tvtraveled);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("connectionspeed"))
                        {
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_connectionspeed);
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.connection),
                                    "\n"+value , tvconnection);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.connectiondatadelay))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_connectiondatatimedely);
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_time_delay),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue() ,txt_datatimedelay);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.satellitedate))
                        {
                            satellitedate=metricItemArraylist.get(j).getMetricTrackValue();
                            if((! satellitedate.trim().isEmpty()) && (! satellitedata.trim().isEmpty()))
                                showsatellitesinfo(satellitedate,satellitedata);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.satellitesdata))
                        {
                            satellitedata=metricItemArraylist.get(j).getMetricTrackValue();
                            if((! satellitedate.trim().isEmpty()) && (! satellitedata.trim().isEmpty()))
                                showsatellitesinfo(satellitedate,satellitedata);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.itemgpsaccuracy))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_gpsaccuracy);
                            if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA"))
                                    && (! value.equalsIgnoreCase("null")))
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                        "\n"+value+" feet", tvgpsaccuracy);
                            }
                            else
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                        "\n"+value, tvgpsaccuracy);
                            }
                        }

                    }

                    if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                            (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                    {
                        populatelocationonmap(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                    }

                    tvblockchainid.setText(arraycontainerformetric.getVideostarttransactionid());
                    tvblockid.setText(arraycontainerformetric.getHashmethod());
                    tvblocknumber.setText(arraycontainerformetric.getValuehash());
                    tvmetahash.setText(arraycontainerformetric.getMetahash());

                    int mediarunningpercentage = (currentmediaposition * 100) / metricmainarraylist.size();

                    if(linechart_connectionspeed != null)
                        updatelinegraphwithposition(linechart_connectionspeed,connectionspeedvalues,mediarunningpercentage,vertical_slider_connectionspeed);

                    if(linechart_datatimedelay != null)
                        updatelinegraphwithposition(linechart_datatimedelay,connectiondatadelayvalues,mediarunningpercentage,vertical_slider_connectiondatatimedely);

                    if(linechart_gpsaccuracy != null)
                        updatelinegraphwithposition(linechart_gpsaccuracy,gpsaccuracyvalues,mediarunningpercentage,vertical_slider_gpsaccuracy);

                    if(linechart_speed != null && speedgraphitems.size() > 0)
                        updatelinegraphwithposition(linechart_speed,speedgraphitems,mediarunningpercentage,vertical_slider_speed);

                    if(linechart_traveled != null && travelledgraphitems.size() > 0)
                        updatelinegraphwithposition(linechart_traveled,travelledgraphitems,mediarunningpercentage,vertical_slider_traveled);

                    if(linechart_altitude != null && altitudegraphitems.size() > 0)
                        updatelinegraphwithposition(linechart_altitude,altitudegraphitems,mediarunningpercentage,vertical_slider_altitude);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    if(seekbar_mediavideoaudio != null)
                        seekbar_mediavideoaudio.setProgress(currentmediaposition,true);
                    if(seekbar_mediametadata != null)
                        seekbar_mediametadata.setProgress(currentmediaposition,true);
                }
                else
                {
                    if(seekbar_mediavideoaudio != null)
                        seekbar_mediavideoaudio.setProgress(currentmediaposition);
                    if(seekbar_mediametadata != null)
                        seekbar_mediametadata.setProgress(currentmediaposition);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setdatacomposing(boolean isdatacomposing)
    {
        this.isdatacomposing=isdatacomposing;
    }

    public void fetchmetadatafromdb(String mediafilepath) {
        try {
            databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<metadatahash> mitemlist=mdbhelper.getmediametadatabyfilename(common.getfilename(mediafilepath));
            for(int i=0;i<mitemlist.size();i++)
            {
                String metricdata=mitemlist.get(i).getMetricdata();
                String sequencehash = mitemlist.get(i).getSequencehash();
                String hashmethod = mitemlist.get(i).getHashmethod();
                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                String serverdictionaryhash = mitemlist.get(i).getValuehash();
                String color = mitemlist.get(i).getColor();
                String latency = mitemlist.get(i).getLatency();
                String sequenceno = mitemlist.get(i).getSequenceno();
                parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash,color,latency,sequenceno);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency,String sequenceno) {
        try {

            Object json = new JSONTokener(metadata).nextValue();
            JSONObject jsonobject;
            if(json instanceof JSONObject)
            {
                jsonobject=new JSONObject(metadata);
                ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                Iterator<String> myIter = jsonobject.keys();
                while (myIter.hasNext()) {
                    String key = myIter.next();
                    String value = jsonobject.optString(key);
                    metricmodel model = new metricmodel();
                    model.setMetricTrackKeyName(key);
                    model.setMetricTrackValue(value);
                    metricItemArraylist.add(model);
                }
                metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,
                        color,latency,sequenceno));

                Log.e("metrclistsize",""+metricmainarraylist.size());

            }
            else if(json instanceof JSONArray)
            {
                JSONArray array = new JSONArray(metadata);
                for (int j = 0; j < array.length(); j++) {
                    ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                    JSONObject object = array.getJSONObject(j);
                    Iterator<String> myIter = object.keys();
                    while (myIter.hasNext()) {
                        String key = myIter.next();
                        String value = object.optString(key);
                        metricmodel model = new metricmodel();
                        model.setMetricTrackKeyName(key);
                        model.setMetricTrackValue(value);
                        metricItemArraylist.add(model);
                    }
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,
                            metahash,color,latency,sequenceno));

                    Log.e("metrclistsize",""+metricmainarraylist.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rotatecompass(int degree)
    {
        if(degree>0){
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(250);
            ra.setFillAfter(true);
            img_niddle.startAnimation(ra);
            currentDegree = -degree;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mOrientation != null)
            mOrientation.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        msensormanager.registerListener(this, msensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        maccelerometersensormanager = msensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensormanager.registerListener(this, maccelerometersensormanager, SensorManager.SENSOR_DELAY_UI);
        if(mOrientation != null)
            mOrientation.startListening(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        msensormanager.unregisterListener(this);
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
    }

    private void populatelocationonmap(final LatLng location) {
        if (mgooglemap == null)
            return;

        usercurrentlocation=location;
        googlemap.setVisibility(View.VISIBLE);

        zoomgooglemap(location.latitude,location.longitude);

        if(clearmapneeded)
        {
            clearmapneeded=false;
            cleargooglemap();
        }

        addPulsatingEffect();

        mgooglemap.getUiSettings().setZoomControlsEnabled(true);
        mgooglemap.getUiSettings().setMyLocationButtonEnabled(true);
        mgooglemap.getUiSettings().setZoomGesturesEnabled(true);

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

            mgooglemap.setMyLocationEnabled(false);

            mgooglemap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove()
                {
                    float currentzoomlevel=mgooglemap.getCameraPosition().zoom;
                    if(isrecodrunning)
                    {
                        if(lastzoomlevel != currentzoomlevel)
                            addPulsatingEffect();
                    }
                    lastzoomlevel=currentzoomlevel;
                }
            });
        }
    }

    private void addPulsatingEffect()
    {
        try
        {
            addmediacreatedpointmarker(usercurrentlocation);

            if((lastPulseAnimator == null || (! lastPulseAnimator.isRunning())) && isdatacomposing)
            {
                if(lastPulseAnimator != null)
                    lastPulseAnimator.cancel();

                if(usercurrentlocation == null)
                    return;

                lastPulseAnimator = valueAnimate(getdisplaypulseradius(), 3500,
                        new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                if(mappulsatecircle != null)
                                    mappulsatecircle.setRadius((Float) animation.getAnimatedValue());
                                else {
                                    mappulsatecircle = mgooglemap.addCircle(new CircleOptions()
                                            .center(usercurrentlocation)
                                            .radius((Float) animation.getAnimatedValue())
                                            .strokeWidth(0)
                                            .fillColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.map_radius_color)));
                                }
                                if(mappulsatecircle != null)
                                    mappulsatecircle.setCenter(usercurrentlocation);
                            }
                        });

            }
            else
            {
                if(lastPulseAnimator != null && (! isrecodrunning))
                {
                    lastPulseAnimator.cancel();
                    lastPulseAnimator=null;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected float getdisplaypulseradius() {
        if(mgooglemap == null)
            return 0.0f;

        float currentzoomlevel=mgooglemap.getCameraPosition().zoom;
        Log.e("zoom level ",""+currentzoomlevel);

        if(currentzoomlevel >= 20)
            return 30f;
        else if(currentzoomlevel >= 18)
            return 30f;
        else if(currentzoomlevel >= 16)
            return 50f;
        else if(currentzoomlevel >= 15)
            return 100f;
        else if(currentzoomlevel >= 14)
            return 300f;
        else if(currentzoomlevel >= 13)
            return 400f;
        else if(currentzoomlevel >= 12)
            return 500f;
        else if(currentzoomlevel >= 11)
            return 600f;
        else if(currentzoomlevel >= 10)
            return 700f;

        return 500f;
    }

    protected ValueAnimator valueAnimate(float accuracy,long duration, ValueAnimator.AnimatorUpdateListener updateListener){
        Log.d( "valueAnimate: ", "called");
        ValueAnimator va = ValueAnimator.ofFloat(0,accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.setStartDelay(200);
        va.start();
        return va;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor == maccelerometersensormanager) {
        }
        else
        {
            if(isdatacomposing)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int heading = Math.round(event.values[0]);
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),"\n"+heading +"° " +common.getcompassdirection(heading), tvheading);
                        common.setdrawabledata("",+heading +"° " +common.getcompassdirection(heading) , txtdegree);
                    }
                });
                int degree = Math.round(event.values[0]);
                rotatecompass(degree);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
    public void setchartdata(LineChart linechart,int minvalue,int maxvalue)
    {
        linechart.setNoDataText("");
        LimitLine llXAxis = new LimitLine(10f, "");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = linechart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        LimitLine ll1 = new LimitLine(150f, "");
        ll1.setLineWidth(0f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(0f);
        LimitLine ll2 = new LimitLine(-30f, "");
        ll2.setLineWidth(0f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(0f);
        YAxis leftAxis = linechart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMinimum(minvalue);
        leftAxis.setAxisMaximum(maxvalue);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        linechart.getLegend().setEnabled(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        linechart.getAxisRight().setEnabled(false);
        linechart.getAxisLeft().setEnabled(false);
        xAxis.setEnabled(false);
        linechart.setOnChartGestureListener(this);
        linechart.setOnChartValueSelectedListener(this);
        linechart.setDrawGridBackground(false);
        linechart.getDescription().setEnabled(false);
        linechart.setTouchEnabled(false);
        linechart.setDragEnabled(false);
        linechart.setScaleEnabled(false);
        linechart.setPinchZoom(false);

    }

    public void initlinechart(LineChart chart,Float minrange,Float maxrange,boolean shouldusemaxrange)
    {

        chart.setNoDataText("");
        // background color
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setNoDataText("");
        XAxis xAxis;
        xAxis = chart.getXAxis();

        YAxis yAxis;
        yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        if(shouldusemaxrange)
            yAxis.setAxisMaximum(maxrange);

        yAxis.setAxisMinimum(minrange);

        // // Create Limit Lines // //
        LimitLine llXAxis = new LimitLine(10f, "");
        llXAxis.setLineWidth(0f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(0f);

        LimitLine ll1 = new LimitLine(150f, "");
        ll1.setLineWidth(0f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(0f);
        LimitLine ll2 = new LimitLine(-30f, "");
        ll2.setLineWidth(0f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(0f);
        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);
        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);
        chart.getLegend().setEnabled(false);
        xAxis.setDrawLimitLinesBehindData(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        xAxis.setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);

        //MarkerView
      /*  IMarker marker = new chartcustommarkerview(applicationviavideocomposer.getactivity(),
                R.layout.row_chartcustommarkerview);
        chart.setMarker(marker);*/
    }

    public void setlinechartdata(final LineChart chart, Float value, ArrayList<Entry> valuesarray,String chartname)
    {
        chart.setVisibility(View.VISIBLE);
        if(value == 0)
            value=1.0f;
        if(value != -1)  // It means chart is preparing on recording time
        {
            if(valuesarray.size() >= 190)
                valuesarray.remove(valuesarray.get(valuesarray.size()-1));
            valuesarray.add(new Entry(valuesarray.size(), value, 0));
        }
        LineDataSet set1;
        if (chart.getData() != null &&  chart.getData().getDataSetCount() > 0)
        {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(valuesarray);
            if(value != -1)
            {
                for(int i=0;i<set1.getEntryCount();i++)
                    set1.getEntryForIndex(i).setIcon(null);

                set1.getEntryForIndex(set1.getEntryCount()-1).setIcon(ContextCompat.getDrawable(getActivity(),
                        R.drawable.blue_black_ball));
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_8dp),10,
                        getActivity().getResources().getDimension(R.dimen.margin_50dp),
                        0);
            }
            else
            {
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_8dp),0,
                        getActivity().getResources().getDimension(R.dimen.margin_8dp),
                        0);
            }
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.refreshDrawableState();
            setyaxismaxrange(chart,chartname,value);
            chart.setVisibleXRangeMaximum(80);
            chart.moveViewToX(set1.getEntryCount());
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(valuesarray, "");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setDrawIcons(true);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.GREEN);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(false);
            set1.setHighLightColor(Color.WHITE);
            set1.setHighlightLineWidth(3);
            set1.setHighlightEnabled(true);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
            set1.setFillColor(Color.TRANSPARENT);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            LineData data = new LineData(dataSets);
            chart.setData(data);
            if(value == -1 && valuesarray.size() > 80)
            {
                chart.invalidate();
                chart.setVisibleXRange(0, 80);
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_8dp),0,
                        getActivity().getResources().getDimension(R.dimen.margin_8dp),
                        0);
            }
            else
            {
                chart.invalidate();
                chart.setVisibleXRange(0, valuesarray.size());
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_8dp),0,0,
                        0);
            }
        }
        chart.animateX(0);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setspeedtraveledaltitudechart(final LineChart linechart, Float value, ArrayList<Entry> arrayitems,String chartname) {

        linechart.setVisibility(View.VISIBLE);
        if(value == 0)
            value=0.0f;
        if(value != -1)
        {
            if(arrayitems.size() >= 190)
                arrayitems.remove(arrayitems.get(arrayitems.size()-1));
            arrayitems.add(new Entry(arrayitems.size(), value, 0));
        }
        LineDataSet set1;
        if (linechart.getData() != null && linechart.getData().getDataSetCount() > 0 && arrayitems.size() > 0)
        {
            set1 = (LineDataSet) linechart.getData().getDataSetByIndex(0);
            set1.setValues(arrayitems);
            set1.setHighlightEnabled(true);
            if(value != -1)
            {
                for(int i=0;i<set1.getEntryCount();i++)
                    set1.getEntryForIndex(i).setIcon(null);

                set1.getEntryForIndex(set1.getEntryCount()-1).setIcon(ContextCompat.getDrawable(getActivity(),
                        R.drawable.blue_black_ball));
                linechart.moveViewTo(set1.getEntryForIndex(set1.getEntryCount()-1).getX(),set1.getEntryForIndex(set1.getEntryCount()-1).getY(), YAxis.AxisDependency.LEFT);
                linechart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_7dp),10,40,getActivity().getResources().getDimension(R.dimen.margin_7dp));
            }
            else
            {
                linechart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_6dp),10,getActivity().getResources().getDimension(R.dimen.margin_8dp),getActivity().getResources().getDimension(R.dimen.margin_7dp));
            }
            LineData data = new LineData(set1);
            linechart.setData(data);
            setyaxismaxrange(linechart,chartname,value);
            linechart.setVisibleXRangeMaximum(80);
            linechart.getData().notifyDataChanged();
            linechart.notifyDataSetChanged();
            linechart.refreshDrawableState();

        }
        else
        {
            set1 = new LineDataSet(arrayitems, "");
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set1.setDrawIcons(true);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.GREEN);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setHighlightEnabled(false);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return linechart.getAxisLeft().getAxisMinimum();
                }
            });

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            LineData data = new LineData(dataSets);
            linechart.setData(data);

            if(value == -1 && arrayitems.size() > 80){
                linechart.invalidate();
                linechart.setVisibleXRange(0, 80);
                linechart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_7dp),10,getActivity().getResources().getDimension(R.dimen.margin_8dp),getActivity().getResources().getDimension(R.dimen.margin_7dp));

            }else{
                linechart.invalidate();
                linechart.setVisibleXRange(0, arrayitems.size());
                linechart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_7dp),10,getActivity().getResources().getDimension(R.dimen.margin_6dp),getActivity().getResources().getDimension(R.dimen.margin_7dp));
            }
        }
        linechart.animateX(0);
        Legend l = linechart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    public void updatelinegraphwithposition(final LineChart chart, final ArrayList<Entry> valuesarray, final int mediarunningpercentage, final verticalseekbar vertical_seekbar)
    {
        if (chart.getData() != null &&  chart.getData().getDataSetCount() > 0)
        {
            final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(valuesarray);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    int totalsize=set1.getEntryCount();
                    int selectedchartposition = (mediarunningpercentage * totalsize) / 100;
                    int count = 0;
                    if(selectedchartposition <= set1.getEntryCount())
                    {
                        for(int i=0;i<set1.getEntryCount();i++)
                            set1.getEntryForIndex(i).setIcon(null);

                        count =  set1.getEntryCount();
                        if (count != 1) {
                            if(selectedchartposition ==set1.getEntryCount())
                                selectedchartposition = selectedchartposition-1;

                            final int finalSelectedchartposition = selectedchartposition;
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    /*Highlight   high = new Highlight(set1.getEntryForIndex(selectedchartposition).getX(), 0, selectedchartposition);
                                    chart.highlightValue(high, false);*/
                                    if(finalSelectedchartposition < set1.getEntryCount())
                                    {
                                        if(finalSelectedchartposition <=39 && set1.getEntryCount() > 0){
                                            chart.moveViewToX(set1.getEntryForIndex(0).getX());
                                        }else{
                                            chart.moveViewToX(set1.getEntryForIndex(finalSelectedchartposition -39).getX());
                                        }
                                    }
                                }
                            });
                            set1.getEntryForIndex(selectedchartposition).setIcon(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),
                                    R.drawable.blue_black_ball));
                        }
                    }
                    final int finalCount = count;
                    final int finalSelectedchartposition1 = selectedchartposition;
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //chart.invalidate();
                            if(finalCount != 1){
                                if(vertical_seekbar.getVisibility() == View.VISIBLE)
                                    vertical_seekbar.setVisibility(View.GONE);
                            }else{
                                vertical_seekbar.setVisibility(View.VISIBLE);
                                if(finalSelectedchartposition1 < set1.getEntryCount())
                                {
                                    float sizey = set1.getEntryForIndex(finalSelectedchartposition1).getY();
                                    updateverticalsliderlocationdata(Float.toString(sizey),vertical_seekbar);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }

    public void setlayoutmargin(){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        layout_constraint.setLayoutParams(params);
        layout_constraint.requestLayout();
    }


    public void setchartmargin(LineChart chart){
        Log.e("leftbottommargin=","left ="+(int)getActivity().getResources().getDimension(R.dimen.margin_10dp)+"-"+"bottom"+(int)getActivity().getResources().getDimension(R.dimen.margin_10dp));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins((int)getActivity().getResources().getDimension(R.dimen.margin_10dp),0,0,(int)getActivity().getResources().getDimension(R.dimen.margin_10dp));
        chart.setLayoutParams(params);
        chart.requestLayout();
    }

    public void halfpaichartdate(PieChart chart){
        chart.setNoDataText("");
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setExtraOffsets(0, -14, 0, -77);
        // moveOffScreen();
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        // chart.setCenterText(generateCenterSpannableText());
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(58f);
        chart.setDrawCenterText(true);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setMaxAngle(180f); // HALF CHART
        chart.setRotationAngle(180f);
        chart.setCenterTextOffset(0, -20);
        chart.getLegend().setEnabled(false);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }

    private void sethalfpaichartData(PieChart chart,String value) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        String[] parties = new String[] {""};
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        String chartdatavalue = value.trim();
        if(chartdatavalue.trim().isEmpty() || chartdatavalue.equalsIgnoreCase("NA"))
            chartdatavalue = "0";
        else if(chartdatavalue.contains("%"))
            chartdatavalue= chartdatavalue.substring(0, chartdatavalue.indexOf("%"));


        double doubledata=Double.parseDouble(chartdatavalue.trim());

        int remainingvalue = 100 - (int)doubledata;
        entries.add(new PieEntry((int)doubledata,
                parties[0 % parties.length],
                0));
        entries.add(new PieEntry(remainingvalue,
                parties[1 % parties.length],
                0));

        PieDataSet dataSet = new PieDataSet(entries, "");
        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.btn_background));
        colors.add(getResources().getColor(R.color.drawer_seekbar_max));
        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        chart.setData(data);
        chart.invalidate();
    }

    public void mediapiechartdata(PieChart piechart,int valid,int caution,int invalid ,int unsent)
    {
        piechart.setNoDataText("");
        piechart.setExtraOffsets(0, 0, 0, 0);
        // add a selection listener
        piechart.setOnChartValueSelectedListener(this);
        piechart.getLegend().setEnabled(false);
        piechart.getDescription().setEnabled(false);
        piechart.setHoleColor(Color.TRANSPARENT);
        piechart.setBackgroundColor(Color.TRANSPARENT);
        piechart.getLegend().setEnabled(false);
        piechart.getDescription().setEnabled(false);
        piechart.setRotationEnabled(false);
        piechart.setHighlightPerTapEnabled(false);
        piechart.setHoleRadius(55f);
        //meta_pie_chart.setTransparentCircleColor(getResources().getColor(R.color.transparent));
       // meta_pie_chart.setHoleRadius(0.0f);
        ArrayList<PieEntry> entries = new ArrayList<>();
        String[] items = new String[] {""};
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries.add(new PieEntry(valid,items[0 % items.length],0));
        entries.add(new PieEntry(caution,items[1 % items.length],0));
        entries.add(new PieEntry(invalid,items[2 % items.length],0));
        entries.add(new PieEntry(unsent, items[3 % items.length],0));

        PieDataSet dataSet = new PieDataSet(entries, "");
        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor(config.color_code_green));
        colors.add(Color.parseColor(config.color_code_yellow));
        colors.add(Color.parseColor(config.color_code_red));
        colors.add(Color.parseColor(config.color_code_white_transparent));

        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        piechart.setData(data);
    }


    public void settextviewcolor() {
        settextviewcolor(txt_availablewifinetwork, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvaddress, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvlatitude, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvlongitude, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvaltitude, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvspeed, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvheading, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvtraveled, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvxaxis, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvyaxis, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvzaxis, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvphone, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvnetwork, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvconnection, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvversion, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvwifi, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvgpsaccuracy, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvscreen, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvcountry, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvcpuusage, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvbrightness, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvtimezone, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvmemoryusage, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvbluetooth, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvlocaltime, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_availablewifinetwork, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvstoragefree, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_availablewifinetwork, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvlanguage, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_availablewifinetwork, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvuptime, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvbattery, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvdate, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvtime, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvblockchainid, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvblockid, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvblocknumber, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvmetahash, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvlocationanalytics, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvlocationtracking, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvbattery, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvorientation, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvPhoneanalytics, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvencryption, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvdataletency, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txtdegree, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_meta_cautionframes, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_meta_validframes, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_meta_invalidframes, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_Memory, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_mediainformation, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_videoaudio_cautionframes, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_videoaudio_invalidframes, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_videoaudio_validframes, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_videoaudiodata, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_cpuusage, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_phonetime, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txtbattery, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_valid, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txtdegree, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_caution, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_invalid, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_timeinformation, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_validmeta, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_invalidmeta, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_cautionmeta, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_worldclocktime, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(text_meta, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_connectioninformation, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_datatimedelay, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_world_time, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_phone_time, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(txt_towerinfo, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvorientations, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvcamera, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvpicture_qty, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvairoplan_mode, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvgps, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvjailbroken, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvcurrency, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvbarometer, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        settextviewcolor(tvdeviceconnection, applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));

    }

    public void setvisibility(boolean isvisible){
        if(!isvisible){
            view_connectionspeed.setVisibility(View.VISIBLE);
            view_datatimedelay.setVisibility(View.VISIBLE);
            view_gpsaccuracy.setVisibility(View.VISIBLE);
        }else{

            if(view_connectionspeed!=null)
                  view_connectionspeed.setVisibility(View.GONE);
            if(view_datatimedelay!=null)
                  view_datatimedelay.setVisibility(View.GONE);
            if(view_gpsaccuracy!=null)
                  view_gpsaccuracy.setVisibility(View.GONE);
        }
    }

    public void setyaxismaxrange(LineChart chart,String ischart,float value)
    {
        if(value> vlauetraveled && ischart.equalsIgnoreCase("linechart_traveled")){
            chart.invalidate();
            vlauetraveled = value;
            chart.setVisibleYRange(0,value+5, YAxis.AxisDependency.LEFT);
        }
        else if(value > valuespeed && ischart.equalsIgnoreCase("linechart_speed")){
            chart.invalidate();
            valuespeed = value;
            chart.setVisibleYRange(0,value+5, YAxis.AxisDependency.LEFT);
        }
        else if(value>valuealtitude && ischart.equalsIgnoreCase("linechart_altitude")){
            chart.invalidate();
            valuealtitude = value;
            chart.setVisibleYRange(0,value+5, YAxis.AxisDependency.LEFT);
        }
        else if(value >= valuegpsaccuracy && ischart.equalsIgnoreCase("linechart_gpsaccuracy")){
            chart.invalidate();
            valuegpsaccuracy = value;
            chart.setVisibleYRange(-18f,value+5, YAxis.AxisDependency.LEFT);
        }
        else if(value>=valuedatatimedelay && ischart.equalsIgnoreCase("linechart_datatimedelay")){
            chart.invalidate();
            valuedatatimedelay = value;
            chart.setVisibleYRange(-3f,value+5, YAxis.AxisDependency.LEFT);
        }
        else if(value >= valueconnectionspeed && ischart.equalsIgnoreCase("linechart_connectionspeed")){
            chart.invalidate();
            valueconnectionspeed = value;
            chart.setVisibleYRange(-3f,value+5, YAxis.AxisDependency.LEFT);
        }
    }
}
