package com.deeptruth.app.android.fragments;


import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.deeptruth.app.android.BuildConfig;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class metainformationfragment extends basefragment  implements OnChartValueSelectedListener,
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
    @BindView(R.id.txt_gpsaccuracy)
    customfonttextview tv_gpsaccuracy;
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
    @BindView(R.id.txt_locationanalytics)
    customfonttextview tvlocationanalytics;
    @BindView(R.id.txt_locationtracking)
    customfonttextview tvlocationtracking;
    @BindView(R.id.txt_orientation)
    customfonttextview tvorientation;
    @BindView(R.id.txt_Phoneanalytics)
    customfonttextview tvPhoneanalytics;
    @BindView(R.id.txt_degree)
    customfonttextview txtdegree;
    @BindView(R.id.txt_availablewifinetwork)
    customfonttextview txt_availablewifinetwork;
    @BindView(R.id.txt_world_date)
    TextView txt_world_date;
    @BindView(R.id.txt_phone_date)
    TextView txt_phone_date;
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

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_niddle)
    ImageView img_niddle;
    @BindView(R.id.attitude_indicator)
    AttitudeIndicator attitudeindicator;
    @BindView(R.id.img_phone_orientation)
    ImageView img_phone_orientation;

    @BindView(R.id.phone_time_clock_white)
    AnalogClock phone_time_clock_white;
    @BindView(R.id.phone_time_clock_black)
    AnalogClockBlack phone_time_clock;

    @BindView(R.id.world_time_clock_white)
    AnalogClock world_time_clock_white;
    @BindView(R.id.world_time_clock_black)
    AnalogClockBlack world_time_clock;

    @BindView(R.id.txt_phone_time)
    TextView txt_phone_time;
    @BindView(R.id.txt_world_time)
    TextView txt_world_time;
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
    @BindView(R.id.linear_mediavideoaudio)
    LinearLayout linear_mediavideoaudio;
    @BindView(R.id.seekbar_mediavideoaudio)
    customseekbar seekbar_mediavideoaudio;
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
    @BindView(R.id.layout_mediasummary)
    LinearLayout layout_mediasummary;
    @BindView(R.id.txt_timeinformation)
    TextView txt_timeinformation;
    @BindView(R.id.txt_phonetime)
    TextView txt_phonetime;
    @BindView(R.id.txt_worldclocktime)
    TextView txt_worldclocktime;
    @BindView(R.id.txt_Memory)
    TextView txt_Memory;
    @BindView(R.id.txt_cpuusage)
    TextView txt_cpuusage;
    @BindView(R.id.txtbattery)
    TextView txtbattery;
    @BindView(R.id.txt_connectioninformation)
    TextView txt_connectioninformation;
    @BindView(R.id.txt_videoupdatetransactionid)
    customfonttextview tvblockchainid;
    @BindView(R.id.txt_hash_formula)
    customfonttextview tvblockid;
    @BindView(R.id.txt_data_hash)
    customfonttextview tvblocknumber;
    @BindView(R.id.txt_dictionary_hash)
    customfonttextview tvmetahash;
    @BindView(R.id.text_encryption)
    customfonttextview text_encryption;
    @BindView(R.id.layout_encryptioninfo)
    LinearLayout layout_encryptioninfo;
    @BindView(R.id.layoutcompass)
    ImageView layoutcompass;
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
    @BindView(R.id.layout_soundiformation)
    LinearLayout layout_soundiformation;
    @BindView(R.id.recycler_satellite_itemlist)
    RecyclerView recycler_satellit;
    @BindView(R.id.txt_satellite_altitudes_at)
    TextView txt_satellite_altitudes_at;
    @BindView(R.id.txt_satellite_altitude)
    TextView txt_satellite_altitude;
    @BindView(R.id.layout_timeinformation)
    LinearLayout layout_timeinformation;
    @BindView(R.id.layout_locationanalytics)
    LinearLayout layout_locationanalytics;
    @BindView(R.id.layout_phoneanalytics)
    LinearLayout layout_phoneanalytics;
    @BindView(R.id.layout_connection)
    LinearLayout layout_connection;
    @BindView(R.id.layout_towerinfo)
    LinearLayout layout_towerinfo;
    @BindView(R.id.recycler_towerlist)
    RecyclerView recycler_towerlist;
    @BindView(R.id.txt_gps_no_signal)
    TextView txt_gps_no_signal;
    @BindView(R.id.txt_gps_low_quality)
    TextView txt_gps_low_quality;
    @BindView(R.id.txt_slowdata_connection)
    TextView txt_slowdata_connection;
    @BindView(R.id.parentview)
    RelativeLayout parentview;


    //view id's
    @BindView(R.id.view_speedverticalline)
    View view_speedverticalline;
    @BindView(R.id.view_speedhorizontalline)
    View view_speedhorizontalline;
    @BindView(R.id.horizontal_travelledline)
    View horizontal_travelledline;
    @BindView(R.id.vertical_travelledline)
    View vertical_travelledline;
    @BindView(R.id.horizontal_altitudeline)
    View horizontal_altitudeline;
    @BindView(R.id.vertical_altitudeline)
    View vertical_altitudeline;
    @BindView(R.id.vertical_gpsline)
    View vertical_gpsline;
    @BindView(R.id.horizontal_gpsline)
    View horizontal_gpsline;
    @BindView(R.id.vertical_connectionline)
    View vertical_connectionline;
    @BindView(R.id.horizontal_connectionline)
    View horizontal_connectionline;
    @BindView(R.id.horizontal_datatimedelayline)
    View horizontal_datatimedelayline;
    @BindView(R.id.vertical_datatimedelayline)
    View vertical_datatimedelayline;
    @BindView(R.id.txt_sun)
    TextView txt_sun;
    @BindView(R.id.txt_moon)
    TextView txt_moon;
    @BindView(R.id.barvisualizer)
    visualizerview barvisualizerview;
    @BindView(R.id.txt_total_frames)
    TextView txt_total_frames;
    @BindView(R.id.txt_start_time)
    TextView txt_start_time;
    @BindView(R.id.txt_end_time)
    TextView txt_end_time;
    @BindView(R.id.txt_total_length)
    TextView txt_total_length;
    @BindView(R.id.layout_trimmed_recording)
    LinearLayout layout_trimmed_recording;
    @BindView(R.id.txt_trimmed_recording)
    TextView txt_trimmed_recording;
    @BindView(R.id.txt_trimmed_date_time)
    TextView txt_trimmed_date_time;
    @BindView(R.id.txt_trimmed_start_time)
    TextView txt_trimmed_start_time;
    @BindView(R.id.txt_trimmed_end_time)
    TextView txt_trimmed_end_time;
    @BindView(R.id.txt_trimmed_total_length)
    TextView txt_trimmed_total_length;
    @BindView(R.id.txt_trimmed_total_frames)
    TextView txt_trimmed_total_frames;

    GoogleMap mgooglemap;
    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;
    public ScrollView scrollview_meta;
    ArrayList<Entry> speedgraphitems = new ArrayList<Entry>();
    ArrayList<Entry> travelledgraphitems = new ArrayList<Entry>();
    ArrayList<Entry> altitudegraphitems = new ArrayList<Entry>();
    ArrayList<Entry> connectionspeedvalues = new ArrayList<Entry>();
    ArrayList<Entry> connectiondatadelayvalues = new ArrayList<Entry>();
    ArrayList<Entry> gpsaccuracyvalues = new ArrayList<Entry>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private PolylineOptions mappathoptions=null;
    private Polyline mappathpolyline=null;
    private ArrayList<LatLng> mappathcoordinates=new ArrayList<>();
    private boolean isdatacomposing=false,isrecodrunning=false , isfrommeta=false,setdefaultzoomlevel=true;;
    String lastsavedangle="";
    private float currentDegree = 0f,lastzoomlevel=0,vlauetraveled = 100,valuespeed = 80,valuealtitude = 2000,valuegpsaccuracy = 25,valuedatatimedelay = 10,valueconnectionspeed=100;
    private Orientation mOrientation;
    private String[] transparentarray= common.gettransparencyvalues();
    int navigationbarheight = 0;
    ValueAnimator lastPulseAnimator=null;
    LatLng usercurrentlocation=null;
    Circle userlocationcircle =null;
    arraycontainer arraycontainerformetric =null;
    View rootview;
    String screenwidth,screenheight, satellitedata="",satellitedate="";
    ArrayList<celltowermodel> celltowers = new ArrayList<>();
    ArrayList<satellites> satelliteslist = new ArrayList<>();
    satellitesdataadapter satelliteadapter;
    private Marker locationindicatemarker=null;
    Marker mediacreatedpointmarker;
    toweritemadapter toweradapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            msensormanager = (SensorManager) applicationviavideocomposer.getactivity().getSystemService(Context.SENSOR_SERVICE);
            scrollview_meta = (ScrollView) findViewById(R.id.scrollview_meta);
            img_phone_orientation.setImageResource(R.drawable.img_phoneorientation);
            try {
                DrawableCompat.setTint(layoutcompass.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                        , R.color.uvv_gray));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            phone_time_clock_white.setVisibility(View.GONE);
            world_time_clock_white.setVisibility(View.GONE);

            settextviewcolor();
            setcoloronview(parentview);
            setgraphviewlinecolor();
            layout_soundiformation.setVisibility(View.GONE);

            seekbar_mediavideoaudio.setEnabled(false);
            vertical_slider_speed.setEnabled(false);
            vertical_slider_altitude.setEnabled(false);
            vertical_slider_traveled.setEnabled(false);
            vertical_slider_gpsaccuracy.setEnabled(false);
            vertical_slider_connectionspeed.setEnabled(false);
            vertical_slider_connectiondatatimedely.setEnabled(false);
            linear_mediavideoaudio.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));

            //visiblity gone of media information
            layout_mediasummary.setVisibility(View.GONE);
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
                satelliteadapter =new satellitesdataadapter(applicationviavideocomposer.getactivity(),satelliteslist,isfrommeta) ;
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
           // setlayoutmargin();


            //setmetadatavalue();
            mOrientation = new Orientation(applicationviavideocomposer.getactivity());


            if(! xdata.getinstance().getSetting(config.drawer_transparency).trim().isEmpty())
            {
                float progress=Float.parseFloat(xdata.getinstance().getSetting(config.drawer_transparency));
                progress=progress/100;
                if(progress <= 0.3)
                    googlemap.setAlpha(0.3f);
                else
                    googlemap.setAlpha(progress);

            }
            else
            {
                googlemap.setAlpha(0.5f);
            }

            loadmap();
            setchartmargin(linechart_altitude);
            setchartmargin(linechart_speed);
            setchartmargin(linechart_traveled);

            halfpaichartdate(chart_memoeyusage);
            halfpaichartdate(chart_cpuusage);
            halfpaichartdate(chart_battery);

            setchartdata(linechart_speed,-4,200);
            setchartdata(linechart_altitude,-40,3000);
            setchartdata(linechart_traveled,-5,300);
            vertical_slider_speed.setMax(80);
            vertical_slider_altitude.setMax(2000);
            vertical_slider_traveled.setMax(100);

            initlinechart(linechart_connectionspeed,-5f,50f,true);
            initlinechart(linechart_datatimedelay,-5f,50f,true);
            initlinechart(linechart_gpsaccuracy,-10f,100f,true);
            vertical_slider_connectionspeed.setMax(50);
            vertical_slider_connectiondatatimedely.setMax(50);
            vertical_slider_gpsaccuracy.setMax(100);

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

    @Override
    public int getlayoutid() {
        return R.layout.fragment_metainformationfragment;
    }

    public void setdatacomposing(boolean isdatacomposing)
    {
        this.isdatacomposing=isdatacomposing;
    }

    public void setdatacomposing(boolean isdatacomposing,String mediafilepath)
    {
        this.isdatacomposing=isdatacomposing;

        metricmainarraylist.clear();
        cleargooglemap();

        if(txt_gps_low_quality != null)
        {
            txt_gps_low_quality.setText("0 Frames");
            txt_gps_no_signal.setText("0 Frames");
            txt_slowdata_connection.setText("0 Frames");
        }

        if(! isdatacomposing)
        {
            fetchmetadatafromdb(mediafilepath);
            if(metricmainarraylist != null && metricmainarraylist.size() > 0)
            {
                layout_mediasummary.setVisibility(View.VISIBLE);
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
            if(layout_mediasummary != null && txt_mediainformation != null)
            {
                layout_mediasummary.setVisibility(View.GONE);
                txt_mediainformation.setVisibility(View.GONE);
            }

            showhideverticalbar(true);
            resetmediainformation();
        }
    }

    public void showhideverticalbar(boolean shouldtrue)
    {
        if(vertical_slider_connectiondatatimedely == null)
            return;

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
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.orientation))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
                            if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA"))
                                    && (! value.equalsIgnoreCase("null")))
                            {
                                int degree = Integer.parseInt(metricItemArraylist.get(j).getMetricTrackValue());
                                common.setdrawabledata("",
                                        metricItemArraylist.get(j).getMetricTrackValue()+"° " +common.getcompassdirection(degree), txtdegree);
                                rotatecompass(degree);
                            }
                            else
                            {
                                common.setdrawabledata("","NA", txtdegree);
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

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_currency),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvcurrency);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("deviceconnection")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_connection),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvdeviceconnection);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("screenorientatioin")){

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_orientation),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvorientations);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("barometer")){

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_barometer),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvbarometer);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("airplanemode")){

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_airplane_mode),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvairoplan_mode);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpsonoff")){

                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_gps),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvgps);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.jailbroken)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_jailbroken),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvjailbroken);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("camera")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_camera),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvcamera);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.picturequality)){
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


                       /* if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("devicedate")){
                            tvdate.setText(common.convertstringintodate(metricItemArraylist.get(j).getMetricTrackValue()));
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequencestarttime")){
                            tvtime.setText(common.getFormattedTime(metricItemArraylist.get(j).getMetricTrackValue()));
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequenceendtime"))
                            tvtime.setText(tvtime.getText()+" - "+common.getFormattedTime(metricItemArraylist.get(j).getMetricTrackValue()));
*/
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
                            /*common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.speed)
                                    ,"\n"+ value, tvspeed);*/
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude))
                        {
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_altitude);
                            //common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude),"\n"+value, tvaltitude);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.distancetravelled))
                        {
                            String value = common.travelleddistanceformatter(metricItemArraylist.get(j).getMetricTrackValue());
                            updateverticalsliderlocationdata(value,vertical_slider_traveled);
                            //common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),"\n"+value, tvtraveled);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("connectionspeed"))
                        {
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_connectionspeed);
                            /*common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.connection),
                                    "\n"+value , tvconnection);*/
                        }
                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.connectiondatadelay))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_connectiondatatimedely);
                            /*common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_time_delay),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue() ,txt_datatimedelay);*/
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

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.itemgpsaccuracy)) {
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
                            if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA"))
                                    && (! value.equalsIgnoreCase("null")))
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                        "\n"+"+/-"+value+" feet", tv_gpsaccuracy);
                            }
                            else
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                        "\n"+"+/-"+value, tv_gpsaccuracy);
                            }
                        }
                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.itemgpsaccuracy))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
                            updateverticalsliderlocationdata(value,vertical_slider_gpsaccuracy);
                           /* if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA"))
                                    && (! value.equalsIgnoreCase("null")))
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                        "\n"+ "+/- " +value+" feet", tvgpsaccuracy);
                            }
                            else
                            {
                                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                                        "\n"+value, tvgpsaccuracy);
                            }*/
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
                        updatelinegraphwithposition(linechart_connectionspeed,connectionspeedvalues,mediarunningpercentage,vertical_slider_connectionspeed,tvconnection,applicationviavideocomposer.getactivity().getResources().getString(R.string.connection));

                    if(linechart_datatimedelay != null)
                        updatelinegraphwithposition(linechart_datatimedelay,connectiondatadelayvalues,mediarunningpercentage,vertical_slider_connectiondatatimedely,txt_datatimedelay,applicationviavideocomposer.getactivity().getResources().getString(R.string.data_time_delay));
                    if(linechart_gpsaccuracy != null)
                        updatelinegraphwithposition(linechart_gpsaccuracy,gpsaccuracyvalues,mediarunningpercentage,vertical_slider_gpsaccuracy,tvgpsaccuracy,applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy));

                    if(linechart_speed != null && speedgraphitems.size() > 0)
                        updatelinegraphwithposition(linechart_speed,speedgraphitems,mediarunningpercentage,vertical_slider_speed,tvspeed,applicationviavideocomposer.getactivity().getResources().getString(R.string.speed));

                    if(linechart_traveled != null && travelledgraphitems.size() > 0)
                        updatelinegraphwithposition(linechart_traveled,travelledgraphitems,mediarunningpercentage,vertical_slider_traveled,tvtraveled,applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled));

                    if(linechart_altitude != null && altitudegraphitems.size() > 0)
                        updatelinegraphwithposition(linechart_altitude,altitudegraphitems,mediarunningpercentage,vertical_slider_altitude,tvaltitude,applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    if(seekbar_mediavideoaudio != null)
                        seekbar_mediavideoaudio.setProgress(currentmediaposition,true);
                }
                else
                {
                    if(seekbar_mediavideoaudio != null)
                        seekbar_mediavideoaudio.setProgress(currentmediaposition);
                }
                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.sun_text)," "+applicationviavideocomposer.getactivity().getResources().getString(R.string.sun_direction), txt_sun);
                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.moon_text)," "+applicationviavideocomposer.getactivity().getResources().getString(R.string.moon_direction), txt_moon);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
    public void fetchmetadatafromdb(String mediafilepath) {
        try {
            databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String localkey="",mediastartdevicedate="",type="",trimmedmediapath="",trimmeddatetime="",trimmedstarttime="",
                    trimmedendtime="",trimmedduration="",trimmedframes="";
            Cursor cursor=mdbhelper.getstartmediainfo(common.getfilename(mediafilepath));
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                        mediastartdevicedate = "" + cursor.getString(cursor.getColumnIndex("videostartdevicedate"));
                        type = "" + cursor.getString(cursor.getColumnIndex("type"));
                        trimmedmediapath = "" + cursor.getString(cursor.getColumnIndex("trimmedmediapath"));
                        trimmeddatetime = "" + cursor.getString(cursor.getColumnIndex("trimmeddatetime"));
                        trimmedstarttime = "" + cursor.getString(cursor.getColumnIndex("trimmedstarttime"));
                        trimmedendtime = "" + cursor.getString(cursor.getColumnIndex("trimmedendtime"));
                        trimmedduration = "" + cursor.getString(cursor.getColumnIndex("trimmedduration"));
                        trimmedframes = "" + cursor.getString(cursor.getColumnIndex("trimmedframes"));

                    } while (cursor.moveToNext());
                }
            }
            if(! localkey.trim().isEmpty())
            {
                ArrayList<metadatahash> mitemlist=mdbhelper.getmediametadatabylocalkey(localkey);
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
                    String colorreason = mitemlist.get(i).getColorreason();
                    parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash,color,latency,sequenceno,colorreason);
                }
            }

            try {

                txt_total_length.setText("");
                txt_start_time.setText("");
                txt_end_time.setText("");
                txt_total_frames.setText("");
                txt_trimmed_date_time.setText("");
                txt_trimmed_start_time.setText("");
                txt_trimmed_end_time.setText("");
                txt_trimmed_total_frames.setText("");
                txt_trimmed_total_length.setText("");
                layout_trimmed_recording.setVisibility(View.GONE);

                if(! mediastartdevicedate.trim().isEmpty())
                {
                    Date startdate = null;
                    if(mediastartdevicedate.contains("T"))
                    {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                        startdate = format.parse(mediastartdevicedate);
                    }
                    else
                    {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        startdate = format.parse(mediastartdevicedate);
                    }

                    if(! type.equalsIgnoreCase(config.type_image))
                    {
                        Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                                BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));

                        final MediaPlayer mp = MediaPlayer.create(applicationviavideocomposer.getactivity(), uri);
                        if (mp != null) {
                            Date finalStartdate = startdate;
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    int duration = mediaPlayer.getDuration();
                                    setmediatimedurationdata(duration,finalStartdate);
                                }
                            });
                        }

                        if(! trimmeddatetime.trim().isEmpty() && (! trimmeddatetime.equalsIgnoreCase("null")))
                        {
                            txt_trimmed_date_time.setText(trimmeddatetime);
                            txt_trimmed_start_time.setText(trimmedstarttime);
                            txt_trimmed_end_time.setText(trimmedendtime);
                            //txt_trimmed_total_frames.setText(trimmedframes);
                            txt_trimmed_total_frames.setText("-");
                            txt_trimmed_total_length.setText(trimmedduration);
                            layout_trimmed_recording.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        setmediatimedurationdata(0,startdate);
                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setmediatimedurationdata(int duration,Date finalStartdate)
    {
        int seconds=duration/1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(finalStartdate);
        calendar.add(Calendar.SECOND, seconds);
        Date enddate = calendar.getTime();
        DateFormat datee = new SimpleDateFormat("z",Locale.getDefault());
        String localTime = datee.format(enddate);
        txt_total_length.setText(common.gettimestring(duration));
        txt_start_time.setText(common.parsedateformat(finalStartdate) + " "+ common.parsetimeformat(finalStartdate) +" " +
                localTime);
        txt_end_time.setText(common.parsedateformat(enddate) + " "+ common.parsetimeformat(enddate) +" " +
                localTime);
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency,String sequenceno,String colorreason) {
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
                        color,latency,sequenceno,colorreason));

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
                            metahash,color,latency,sequenceno,colorreason));

                    Log.e("metrclistsize",""+metricmainarraylist.size());
                }
            }
        } catch (Exception e) {
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
        layout_soundiformation.setVisibility(View.GONE);

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

                if(model.getMetricTrackKeyName().equalsIgnoreCase("connectionspeed"))
                {
                    if((model.getMetricTrackValue().trim().isEmpty()) || (model.getMetricTrackValue().equalsIgnoreCase("NA"))
                            || (model.getMetricTrackValue().equalsIgnoreCase("null")))
                    {
                        model.setMetricTrackValue("0");
                    }

                    String connectionspeed=model.getMetricTrackValue();
                    String[] speedarray=connectionspeed.split(" ");
                    if(speedarray.length > 0)
                    {
                        try
                        {
                            float value=0f;
                            String unit = "";

                            if(speedarray.length > 1)
                                unit =  speedarray[1];

                            if(Float.parseFloat(speedarray[0]) <= 1)
                            {
                                int newvalue=Math.round(Float.parseFloat(speedarray[0]));
                                value=(float) newvalue;
                            }
                            else
                                value=Float.parseFloat(speedarray[0]);

                            if(connectionspeedvalues.size() > 0)
                                connectionspeedvalues.add(new Entry(connectionspeedvalues.size(), value, unit));
                            else
                                connectionspeedvalues.add(new Entry(connectionspeedvalues.size(), value, unit));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(model.getMetricTrackKeyName().equalsIgnoreCase(config.connectiondatadelay))
                {
                    if((model.getMetricTrackValue().trim().isEmpty()) || (model.getMetricTrackValue().equalsIgnoreCase("NA"))
                            || (model.getMetricTrackValue().equalsIgnoreCase("null")))
                    {
                        model.setMetricTrackValue("0");
                    }

                    String connectiondatadelay=model.getMetricTrackValue();
                    String[] array=connectiondatadelay.split(" ");
                    if(array.length > 0)
                    {
                        try
                        {
                            float value=0f;
                            String unit = "";

                            if(array.length > 1)
                                unit =  array[1];

                            if(Float.parseFloat(array[0]) <= 1)
                            {
                                int newvalue=Math.round(Float.parseFloat(array[0]));
                                value=(float)newvalue;
                            }
                            else
                                value=Float.parseFloat(array[0]);


                            if(connectiondatadelayvalues.size() > 0)
                                connectiondatadelayvalues.add(new Entry(connectiondatadelayvalues.size(), value, unit));
                            else
                                connectiondatadelayvalues.add(new Entry(connectionspeedvalues.size(), value, unit));
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
                            String unit = "";

                            if(Float.parseFloat(itemarray[0]) <= 5)
                            {
                                int newvalue=Math.round(Float.parseFloat(itemarray[0]));
                                value=(float) newvalue;

                                if(value >=100)
                                    value = 100f;

                            }
                            else
                                value=Float.parseFloat(itemarray[0]);

                            if(value >=100)
                                value = 100f;

                            if(gpsaccuracyvalues.size() > 0)
                                gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), value, "feet"));
                            else
                                gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), value, "feet"));
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
                            float value=0.0f;
                            String unit = "";

                            if(itemarray.length > 1)
                                unit =  itemarray[1];


                            if(Float.parseFloat(itemarray[0]) != 0)
                                value=Float.parseFloat(itemarray[0]);

                            if(speedgraphitems.size() > 0)
                                speedgraphitems.add(new Entry(speedgraphitems.size(), value, unit));
                            else
                                speedgraphitems.add(new Entry(speedgraphitems.size(), value,unit));
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
                            float value=0.0f;
                            String unit = "";

                            if(itemarray.length > 1)
                                unit =  itemarray[1];

                            if(Float.parseFloat(itemarray[0]) != 0)
                                value=Float.parseFloat(itemarray[0]);

                            if(travelledgraphitems.size() > 0)
                                travelledgraphitems.add(new Entry(travelledgraphitems.size(), value, unit));
                            else
                                travelledgraphitems.add(new Entry(travelledgraphitems.size(), value, unit));
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
                            float value=0.0f;
                            String unit = "";

                            if(itemarray.length > 1)
                                unit =  itemarray[1];


                            if(Float.parseFloat(itemarray[0]) != 0)
                                value=Float.parseFloat(itemarray[0]);

                            if(altitudegraphitems.size() > 0)
                                altitudegraphitems.add(new Entry(altitudegraphitems.size(), value, unit));
                            else
                                altitudegraphitems.add(new Entry(altitudegraphitems.size(), value, unit));
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

    public void drawmediainformation()
    {
        int validcount=0,cautioncount=0,invalidcount=0,lastsequenceno=0,sectioncount=0,unsent=0,gpslowqualitycount=0
                ,gpsnosignalcount=0,slowdataconnectioncount=0;
        String lastcolor="";
        ArrayList<String> colorsectioncount=new ArrayList<>();
        for (int i = 0; i < metricmainarraylist.size(); i++)
        {
            String strsequence=metricmainarraylist.get(i).getSequenceno();
            String coloreason=metricmainarraylist.get(i).getColorreason();
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
                    if(coloreason.toLowerCase().contains(config.coloreason_gpsturnedoff.toLowerCase()) ||
                            coloreason.toLowerCase().contains(config.coloreason_missing_gps_coordinates.toLowerCase()))
                    {
                        gpsnosignalcount=gpsnosignalcount+sequencecount;
                    }
                    else if(coloreason.toLowerCase().contains(config.coloreason_gps_accuracy.toLowerCase()))
                    {
                        gpslowqualitycount=gpslowqualitycount+sequencecount;
                    }
                    else if(coloreason.toLowerCase().contains(config.coloreason_slowdataconnection.toLowerCase()))
                    {
                        slowdataconnectioncount=slowdataconnectioncount+sequencecount;
                    }
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


        linear_mediavideoaudio.setBackgroundColor(applicationviavideocomposer.getactivity().
                getResources().getColor(R.color.validating_white_bg));

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
                        if(color.trim().isEmpty())
                            color="gray";

                        linear_mediavideoaudio.addView(getmediaseekbarbackgroundview(weight,color));
                    }
                }
            }
        }

        //mediapiechartdata(pie_videoaudiochart,20,10,40,30,100);
        mediapiechartdata(pie_videoaudiochart,validcount,cautioncount,invalidcount,unsent,lastsequenceno,gpslowqualitycount,
                gpsnosignalcount,slowdataconnectioncount);

        seekbar_mediavideoaudio.setPadding(0,0,0,0);
        seekbar_mediavideoaudio.setMax(metricmainarraylist.size());
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
    public void setlinechartdata(final LineChart chart, Float value, ArrayList<Entry> valuesarray,String chartname)
    {
        chart.setVisibility(View.VISIBLE);
        if(value == 0)
            value=1.0f;
        if(value != -1)  // It means chart is preparing on recording time
        {
            if(valuesarray.size() >= 190)
                valuesarray.subList(0, 10).clear();

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

                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_6dp),0,
                        getActivity().getResources().getDimension(R.dimen.margin_50dp),
                        0);
            }
            else
            {
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_6dp),0,
                        getActivity().getResources().getDimension(R.dimen.margin_8dp),
                        0);
            }


            if(set1.getEntryCount() == 190){
                set1.removeFirst();

                for(Entry entry: set1.getValues())
                    entry.setX(entry.getX()-1);
            }

            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.refreshDrawableState();
            //setyaxismaxrange(chart,chartname,value);
            chart.setVisibleXRangeMaximum(80);
            chart.moveViewToX(set1.getEntryCount());
            chart.invalidate();
            Log.e("EntryCount",""+set1.getEntryCount());

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
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_7dp),0,
                        getActivity().getResources().getDimension(R.dimen.margin_7dp),
                        0);
            }
            else
            {
                chart.invalidate();
                chart.setVisibleXRange(0, valuesarray.size());
                chart.setViewPortOffsets(getActivity().getResources().getDimension(R.dimen.margin_7dp),0,
                        getActivity().getResources().getDimension(R.dimen.margin_7dp),
                        0);
            }
        }
        chart.animateX(0);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
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

    private void setspeedtraveledaltitudechart(final LineChart linechart, Float value, ArrayList<Entry> arrayitems,String chartname) {

        linechart.setVisibility(View.VISIBLE);
        if(value == 0)
            value=0.0f;
        if(value != -1)
        {
            if(arrayitems.size() >= 190)
                arrayitems.subList(0, 10).clear();

            //arrayitems.remove(arrayitems.get(arrayitems.size()-1));

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

            if(set1.getEntryCount() == 190){
                set1.removeFirst();

                for(Entry entry: set1.getValues())
                    entry.setX(entry.getX()-1);
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

    public void mediapiechartdata(PieChart piechart,int valid,int caution,int invalid ,int unsent,int lastsequenceno,
                                  int gpslowqualitycount,int gpsnosignalcount,int slowdataconnectioncount)
    {
        piechart.setNoDataText("");
        piechart.setExtraOffsets(30, 10, 30, 10);
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
        piechart.setHoleRadius(0f);
        piechart.setDragDecelerationFrictionCoef(2f);
        piechart.setDrawCenterText(false);
        piechart.setDrawHoleEnabled(false);

        //meta_pie_chart.setTransparentCircleColor(getResources().getColor(R.color.transparent));
        // meta_pie_chart.setHoleRadius(0.0f);

        String validString="",cautionString="",invalidString="",unsentString="";

        if(valid > 0)
            validString=valid+" Valid Frames\n("+common.gettotalframepercentage(valid,lastsequenceno)+")";

        if(caution > 0)
            cautionString=caution+" Caution Frames\n("+common.gettotalframepercentage(caution,lastsequenceno)+")";

        if(invalid > 0)
            invalidString=invalid+" Invalid Frames\n("+common.gettotalframepercentage(invalid,lastsequenceno)+")";

        if(unsent > 0)
            unsentString=unsent+" Unsent Frames\n("+common.gettotalframepercentage(unsent,lastsequenceno)+")";

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if(valid > 0)
        {
            entries.add(new PieEntry(valid,validString,0));
            colors.add(Color.parseColor(config.color_code_green));
        }

        if(caution > 0)
        {
            entries.add(new PieEntry(caution,cautionString,0));
            colors.add(Color.parseColor(config.color_code_yellow));
        }

        if(invalid > 0)
        {
            entries.add(new PieEntry(invalid,invalidString,0));
            colors.add(Color.parseColor(config.color_code_red));
        }

        if(unsent > 0)
        {
            entries.add(new PieEntry(unsent, unsentString,0));
            colors.add(Color.parseColor(config.color_code_gray));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(2f);
        dataSet.setDrawIcons(false);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.7f);
        dataSet.setValueLinePart2Length(0.5f);
        dataSet.setValueLineColor(getActivity().getResources().getColor(R.color.white));

        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setDrawValues(false);
        dataSet.setColors(colors);
        piechart.setData(data);
        piechart.setEntryLabelColor(Color.WHITE);
        piechart.setEntryLabelTextSize(7f);
        piechart.highlightValues(null);
        piechart.invalidate();

        txt_total_frames.setText(""+lastsequenceno);
        txt_gps_low_quality.setText(""+gpslowqualitycount+" Frames");
        txt_gps_no_signal.setText(""+gpsnosignalcount+" Frames");
        txt_slowdata_connection.setText(""+slowdataconnectioncount+" Frames");

    }
    public void resetmediainformation()
    {
        try
        {
            if(seekbar_mediavideoaudio != null)
                seekbar_mediavideoaudio.setProgress(0);

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
            linear_mediavideoaudio.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {

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

        mgooglemap.getUiSettings().setZoomControlsEnabled(true);
        mgooglemap.getUiSettings().setMyLocationButtonEnabled(true);
        mgooglemap.getUiSettings().setZoomGesturesEnabled(true);

        if(lastPulseAnimator == null)
            addPulsatingEffect();
        else if(lastPulseAnimator != null && (! lastPulseAnimator.isRunning()))
            addPulsatingEffect();

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

            if(isdatacomposing)
                mgooglemap.setMyLocationEnabled(false);
            else
                mgooglemap.setMyLocationEnabled(false);



            mgooglemap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove()
                {
                    float currentzoomlevel=mgooglemap.getCameraPosition().zoom;
                    if(lastzoomlevel != currentzoomlevel)
                    {
                        if(lastPulseAnimator == null)
                            addPulsatingEffect();
                        else if(lastPulseAnimator != null && (! lastPulseAnimator.isRunning()))
                            addPulsatingEffect();

                        if(userlocationcircle != null)
                            userlocationcircle.setRadius(common.getlocationcircleradius(currentzoomlevel));
                    }
                    lastzoomlevel=currentzoomlevel;
                }
            });
        }
    }


    private void addPulsatingEffect(){

        try {
            if(lastPulseAnimator != null)
                lastPulseAnimator.cancel();

            if(usercurrentlocation == null)
                return;


            if(! isdatacomposing)
            {
                addmediacreatedpointmarker(usercurrentlocation);
                return;
            }
            else
            {
                if(mediacreatedpointmarker != null)
                {
                    mediacreatedpointmarker.remove();
                    mediacreatedpointmarker=null;
                }
            }

            if(userlocationcircle != null)
            {
                userlocationcircle.setCenter(usercurrentlocation);
            }
            else
            {
                userlocationcircle= mgooglemap.addCircle(new CircleOptions()
                        .center(usercurrentlocation)
                        .radius(common.getlocationcircleradius(mgooglemap.getCameraPosition().zoom))
                        .strokeWidth(0)
                        .fillColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.selectedimagehash)));
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void cleargooglemap()
    {
        if(mgooglemap != null)
        {
            mgooglemap.clear();

            if(lastPulseAnimator != null)
                lastPulseAnimator.cancel();

            if(userlocationcircle != null && mgooglemap != null)
            {
                userlocationcircle.remove();
                userlocationcircle=null;
            }
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

    public void settextviewcolor(){
        tvblockchainid.setVisibility(View.GONE);
        tvblockid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvblockid.setVisibility(View.GONE);
        tvblocknumber.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvblocknumber.setVisibility(View.GONE);
        tvmetahash.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvmetahash.setVisibility(View.GONE);
        text_encryption.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        text_encryption.setVisibility(View.GONE);
        layout_encryptioninfo.setVisibility(View.GONE);
        layout_locationanalytics.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        layout_phoneanalytics.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        layout_connection.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        layout_mediasummary.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        layout_timeinformation.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
    }

    public void setgraphviewlinecolor(){
        view_speedverticalline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        view_speedhorizontalline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        horizontal_travelledline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        vertical_travelledline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        horizontal_altitudeline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        vertical_altitudeline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        horizontal_gpsline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        vertical_gpsline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        horizontal_gpsline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        horizontal_connectionline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        vertical_connectionline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        horizontal_datatimedelayline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
        vertical_datatimedelayline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.gray_x));
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

    public void updatelinegraphwithposition(final LineChart chart, final ArrayList<Entry> valuesarray, final int mediarunningpercentage,
                                            final verticalseekbar vertical_seekbar,TextView tvallgraphvalue,String graphtype)
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
                    Log.e("selectedchartposition",""+selectedchartposition);

                    int count = 0;

                    if(selectedchartposition <= set1.getEntryCount())
                    {
                        Log.e("metaconnetion","connectiontimedelay");
                        for(int i=0;i<set1.getEntryCount();i++)
                            set1.getEntryForIndex(i).setIcon(null);

                        count =  set1.getEntryCount();
                        Log.e("count",""+count);
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

                            int value = (int) set1.getEntryForIndex(selectedchartposition).getY();
                            String unit = ""+set1.getEntryForIndex(selectedchartposition).getData();

                            if(graphtype.equalsIgnoreCase(applicationviavideocomposer.getactivity().getString(R.string.gps_accuracy)))
                            {
                                Log.e("metaconnetion","connectiontimedelay");
                                common.setdrawabledata(graphtype,
                                        "\n"+"+/- "+value+" "+unit , tvallgraphvalue);

                            }else
                            {
                                Log.e("connectiontimedelay","connectiontimedelay");
                                common.setdrawabledata(graphtype,
                                        "\n"+value+" "+unit , tvallgraphvalue);
                            }


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
    public void setcoloronview(ViewGroup viewgrp){
        for (int i=0; i<viewgrp.getChildCount();i++)
        {
            View view = viewgrp.getChildAt(i);
            if (view instanceof TextView){
                TextView txtview = (TextView) view;
                txtview.setTextColor(getResources().getColor(R.color.black));

            }
            if(view instanceof RelativeLayout)
            {
                RelativeLayout relativelayout = (RelativeLayout)view;
                setcoloronview(relativelayout);
                /*for (int j=0; j<relativelayout.getChildCount();j++)
                {
                    View view1 = relativelayout.getChildAt(j);
                    if (view1 instanceof TextView){
                        txt_connection = (TextView) view1;
                        txt_connection.setTextColor(getResources().getColor(R.color.red));
                    }
                    if(view1 instanceof LinearLayout)
                    {
                        linearlayout2 = (LinearLayout)view1;
                        for (int k=0; k<linearlayout2.getChildCount();k++) {

                            View view2 = linearlayout2.getChildAt(k);
                            if (view2 instanceof TextView) {
                                txt_connection = (TextView) view2;
                                txt_connection.setTextColor(getResources().getColor(R.color.yellow));
                            }
                        }
                    }
                }*/

            }
            if(view instanceof LinearLayout)
            {
                LinearLayout linearlayout2 = (LinearLayout)view;
                setcoloronview(linearlayout2);
            }
        }
    }
}
