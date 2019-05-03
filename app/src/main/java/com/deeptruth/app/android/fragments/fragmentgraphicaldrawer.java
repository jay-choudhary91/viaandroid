package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.itemupdatelistener;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.sensor.AttitudeIndicator;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.utils.AnalogClock;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    @BindView(R.id.txt_availablewifinetwork)
    customfonttextview txt_availablewifinetwork;

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
    @BindView(R.id.phone_time_clock)
    AnalogClock phone_time_clock;
    @BindView(R.id.world_time_clock)
    AnalogClock world_time_clock;
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
    @BindView(R.id.layout_videoaudiodata)
    LinearLayout layout_videoaudiodata;
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

    View rootview;
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
    private boolean isdatacomposing=false,isrecodrunning=false;
    String lastsavedangle="";
    private float currentDegree = 0f;
    private Orientation mOrientation;
    private String[] transparentarray=common.gettransparencyvalues();
    int navigationbarheight = 0;
    arraycontainer arraycontainerformetric =null;
    String screenwidth,screenheight;
    private Marker locationindicatemarker=null;

    ValueAnimator lastPulseAnimator=null;
    Circle lastUserCircle=null;
    LatLng usercurrentlocation=null;
    int pulsatinglocationcounter=0;
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

          txt_availablewifinetwork.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvaddress.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvlatitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvlongitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvaltitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvspeed.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvheading.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvtraveled.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvxaxis.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvyaxis.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvzaxis.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvphone.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvnetwork.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvconnection.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvversion.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvwifi.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvgpsaccuracy.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvscreen.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvcountry.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvcpuusage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvbrightness.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvtimezone.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvmemoryusage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvbluetooth.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvlocaltime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvstoragefree.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvlanguage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvuptime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvbattery.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvdate.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvtime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvblockchainid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvblockid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvblocknumber.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvmetahash.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvlocationanalytics.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvlocationtracking.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvorientation.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvPhoneanalytics.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvencryption.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          tvdataletency.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
          txtdegree.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));

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
            layout_videoaudiodata.setVisibility(View.GONE);
            layout_mediametadata.setVisibility(View.GONE);
            txt_mediainformation.setVisibility(View.GONE);


          TimeZone timezone = TimeZone.getDefault();
          String timezoneid=timezone.getID();
          phone_time_clock.setTimeZone(timezoneid, new itemupdatelistener() {
                @Override
                public void onitemupdate(Object object,Object timezoneobject) {
                    if(object != null)
                    {
                        Calendar calendar=(Calendar)object;
                        txt_phone_time.setText(common.appendzero(calendar.get(Calendar.HOUR))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                +":"+common.appendzero(calendar.get(Calendar.SECOND))+" "+timezoneobject.toString());
                    }
                }

                @Override
                public void onitemupdate(Object object, int type) {

                }
            });
          world_time_clock.setTimeZone("GMT", new itemupdatelistener() {
                @Override
                public void onitemupdate(Object object,Object timezoneobject) {
                    if(object != null)
                    {
                        Calendar calendar=(Calendar)object;
                        txt_world_time.setText(common.appendzero(calendar.get(Calendar.HOUR))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                +":"+common.appendzero(calendar.get(Calendar.SECOND))+" GMT");
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
                    if(seekParams.progress > 10)
                    {
                        float progress=seekParams.progress;
                        progress=progress/100;
                        googlemap.setAlpha(progress);
                    }
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
                googlemap.setAlpha(progress);
            }
            else
            {
                seekbartransparency.setProgress(50f);
                googlemap.setAlpha(0.5f);
            }

            loadmap();

            halfpaichartdate(chart_memoeyusage);
            halfpaichartdate(chart_cpuusage);
            halfpaichartdate(chart_battery);

            setchartdata(linechart_speed,60);
            setchartdata(linechart_traveled,100);
            setchartdata(linechart_altitude,2000);

            initlinechart(linechart_connectionspeed,25f);
            initlinechart(linechart_datatimedelay,10f);
            initlinechart(linechart_gpsaccuracy,100f);
            vertical_slider_gpsaccuracy.setMax(100);
            vertical_slider_connectionspeed.setMax(25);
            vertical_slider_connectiondatatimedely.setMax(10);

            /*linechart_speed.setVisibility(View.GONE);
            linechart_traveled.setVisibility(View.GONE);
            linechart_altitude.setVisibility(View.GONE);*/

            vertical_slider_speed.setMax(60);
            vertical_slider_altitude.setMax(2000);
            vertical_slider_traveled.setMax(100);

        }
        return rootview;
    }

    public void setlayouttransparency(int progress)
    {
        xdata.getinstance().saveSetting(config.drawer_transparency,""+progress);
        progress= transparentarray.length-progress;
        String colorString="#"+transparentarray[progress]+"0E6479";
        layout_constraint.setBackgroundColor(Color.parseColor(colorString));
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        //attitudeindicator.setAttitude(pitch, roll);
    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        if(isdatacomposing)
        {
            float pitch = orientation[1] * -57;
            float roll = orientation[2] * -57;
           // attitudeindicator.setAttitude(pitch, roll);

            if(img_phone_orientation != null)
                img_phone_orientation.setRotation(roll);
        }
        //Log.e("Pitch roll ",""+pitch+" "+roll);
    }

    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
            try {
                if(isdatacomposing)
                {
                    if(locationindicatemarker != null)
                    {
                       // mgooglemap.clear();
                        locationindicatemarker.remove();
                        locationindicatemarker=null;
                        locationindicatemarker=null;

                    }
                }
                else
                {
                    if(locationindicatemarker == null)
                    {
                        locationindicatemarker=mgooglemap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .icon(common.bitmapdescriptorfromvector(applicationviavideocomposer.getactivity(),
                                        R.drawable.rounded_gps_dot)));
                    }
                    else
                    {
                        locationindicatemarker.setPosition(latlng);
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
        if (mgooglemap == null)
            return;

        googlemap.setVisibility(View.VISIBLE);

        float zoomlevel = mgooglemap.getCameraPosition().zoom;
        if(zoomlevel <= 3)
        {
            mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
        else
        {
            mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomlevel));
        }
    }

    public void setmetricesdata()
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

            /**/

            if(common.getxdatavalue(xdata.getinstance().getSetting(config.Address)).equalsIgnoreCase("NA")) {
                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.address),": "+common.getxdatavalue(xdata.getinstance().getSetting(config.Address)), tvaddress);
            }else{ // remove "/n" from address
                common.setdrawabledata("",common.getxdatavalue(xdata.getinstance().getSetting(config.Address)), tvaddress);

            }
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.xaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_x), tvxaxis);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.yaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_y), tvyaxis);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.zaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_z), tvzaxis);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.phone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.PhoneType)), tvphone);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.network),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.CellProvider)), tvnetwork);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.version),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.OSversion)), tvversion);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.wifi),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.WIFINetwork)), tvwifi);

            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.screen),"\n"+xdata.getinstance().getSetting(config.ScreenWidth) +"*" +xdata.getinstance().getSetting(config.ScreenHeight), tvscreen);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.country),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Country)), tvcountry);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.brightness),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Brightness)), tvbrightness);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.timezone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.TimeZone)), tvtimezone);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.bluetooth),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Bluetooth)), tvbluetooth);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.localtime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.LocalTime)), tvlocaltime);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.storagefree),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.StorageAvailable)), tvstoragefree);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.language),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Language)), tvlanguage);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.uptime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.SystemUptime)), tvuptime);

            String latitude=xdata.getinstance().getSetting(config.Latitude);
            String longitude=xdata.getinstance().getSetting(config.Longitude);
            String altitude=xdata.getinstance().getSetting(config.Altitude);
            String speed=common.getxdatavalue(xdata.getinstance().getSetting(config.Speed));
            String traveled=xdata.getinstance().getSetting(config.distancetravelled);
            String strconnectionspeed=xdata.getinstance().getSetting(config.Connectionspeed);
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

            if(isdatacomposing)
            {

                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.speed),
                        "\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)), tvspeed);

                updateverticalsliderlocationdata(speed,vertical_slider_speed);
                updateverticalsliderlocationdata(altitude,vertical_slider_altitude);
                updateverticalsliderlocationdata(traveled,vertical_slider_traveled);

                updateverticalsliderlocationdata(gpsaccuracy,vertical_slider_gpsaccuracy);
                updateverticalsliderlocationdata(strconnectionspeed,vertical_slider_connectionspeed);
                updateverticalsliderlocationdata(connectiontimedelaystr,vertical_slider_connectiondatatimedely);

                if((! gpsaccuracy.trim().isEmpty()) && (! gpsaccuracy.equalsIgnoreCase("NA"))
                        && (! gpsaccuracy.equalsIgnoreCase("null")))
                {
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                            "\n"+gpsaccuracy+" feet", tvgpsaccuracy);
                }
                else
                {
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),
                            "\n"+ gpsaccuracy , tvgpsaccuracy);
                }

                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),
                        "\n"+traveled, tvtraveled);

                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.connection),"\n"+
                        common.getxdatavalue(strconnectionspeed), tvconnection);

                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_time_delay),
                        "\n"+connectiontimedelaystr, txt_datatimedelay);

                if(phone_time_clock != null)
                    phone_time_clock.setPostRecordData(true,"");

                if(world_time_clock != null)
                    world_time_clock.setPostRecordData(true,"");

                common.setdrawabledata("",common.getdate(), tvdate);
                common.setdrawabledata("",common.gettime(), tvtime);

                latitude=xdata.getinstance().getSetting("lat");
                longitude=xdata.getinstance().getSetting("lng");

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

                    {
                        Float connectionspeed=0.0f;
                        if((! strconnectionspeed.trim().isEmpty()) && (! strconnectionspeed.equalsIgnoreCase("null")) &&
                                (! strconnectionspeed.equalsIgnoreCase("NA")))
                        {
                            String[] array=strconnectionspeed.split(" ");
                            if(array.length >0)
                                connectionspeed=Float.parseFloat(array[0]);
                        }
                        setlinechartdata(linechart_connectionspeed,connectionspeed,connectionspeedvalues);
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
                        setlinechartdata(linechart_datatimedelay,connectiondelay,connectiondatadelayvalues);
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
                        setlinechartdata(linechart_gpsaccuracy,gpsaccuracydata,gpsaccuracyvalues);
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
                        setspeedtraveledaltitudechart(linechart_speed,data,speedgraphitems);
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
                        setspeedtraveledaltitudechart(linechart_traveled,data,travelledgraphitems);
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
                        setspeedtraveledaltitudechart(linechart_altitude,data,altitudegraphitems);
                    }

                    common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.blockchainid)), tvblockchainid);
                    common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.hashformula)), tvblockid);
                    common.setdrawabledata("", " "+common.getxdatavalue(xdata.getinstance().getSetting(config.datahash)), tvblocknumber);
                    common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.matrichash)), tvmetahash);
                }
                else
                {
                    common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),
                            "\n"+"0.0 feets", tvtraveled);

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
            }

            if(! isdatacomposing)
            {
                if(xdata.getinstance().getSetting(config.Heading).toString().trim().length() > 0)
                {
                    String strdegree=common.getxdatavalue(xdata.getinstance().getSetting(config.Heading));

                    if((! strdegree.trim().isEmpty()) && (! strdegree.equalsIgnoreCase("NA")) &&
                            (! strdegree.equalsIgnoreCase("null")))
                    {
                        int degree = Math.abs((int)Double.parseDouble(strdegree));
                        rotatecompass(degree);
                        String direction= common.getcompassdirection(degree);

                        common.setdrawabledata("",strdegree +"° " + direction , txtdegree);

                        double heading=Double.parseDouble(strdegree);
                        int headingg=(int)heading;
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),"\n"+headingg + "°" , tvheading);
                    }
                    else
                    {
                        common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),"\n"+"NA", tvheading);
                    }

                }
            }

            if(xdata.getinstance().getSetting(config.orientation).toString().trim().length() > 0)
            {
                String strdegree=xdata.getinstance().getSetting(config.orientation);
                if(! strdegree.equals(lastsavedangle))
                {
                    if(strdegree.equalsIgnoreCase("NA"))
                        strdegree="0.0";

                    int degree = Math.abs((int)Double.parseDouble(strdegree));
                 //   rotatecompass(degree);
                }
                lastsavedangle=strdegree;
            }

        if(! xdata.getinstance().getSetting(config.attitude_data).trim().isEmpty() && (! xdata.getinstance().getSetting(config.attitude_data).equalsIgnoreCase("NA")))
        {
            try {
                float[] adjustedRotationMatrix = new float[9];
                String attitude = xdata.getinstance().getSetting(config.attitude_data).toString();
                String[] attitudearray = attitude.split(",");
                for(int i = 0 ;i< attitudearray.length;i++){
                    //float val = (float) (Math.random() * 20) + 3;
                    adjustedRotationMatrix[i]=Float.parseFloat(attitudearray[i]);
                }
                // Transform rotation matrix into azimuth/pitch/roll
                float[] orientation = new float[3];
                SensorManager.getOrientation(adjustedRotationMatrix, orientation);

                float pitch = orientation[1] * -57;
                float roll = orientation[2] * -57;

                if(img_phone_orientation != null)
                    img_phone_orientation.setRotation(roll);

            }catch (Exception e)
            {
                e.printStackTrace();
            }

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
    }

    public void setdatacomposing(boolean isdatacomposing,String mediafilepath)
    {
        this.isdatacomposing=isdatacomposing;
        if(! isdatacomposing)
        {

            if(lastPulseAnimator != null)
                lastPulseAnimator.cancel();

            if(lastUserCircle != null && mgooglemap != null)
            {
                lastUserCircle.remove();
                lastUserCircle=null;
            }

            metricmainarraylist.clear();
            if(mgooglemap != null)
                mgooglemap.clear();

            fetchmetadatafromdb(mediafilepath);
            if(metricmainarraylist != null && metricmainarraylist.size() > 0)
            {
                layout_videoaudiodata.setVisibility(View.VISIBLE);
                layout_mediametadata.setVisibility(View.VISIBLE);
                txt_mediainformation.setVisibility(View.VISIBLE);
                drawmappath();
                drawmediainformation();
            }
        }
        else
        {
            layout_videoaudiodata.setVisibility(View.GONE);
            layout_mediametadata.setVisibility(View.GONE);
            txt_mediainformation.setVisibility(View.GONE);
            resetmediainformation();
        }
    }

    public void resetmediainformation()
    {

        try
        {
            txt_videoaudio_validframes.setText("0 Frames");
            txt_videoaudio_cautionframes.setText("0 Frames");
            txt_videoaudio_invalidframes.setText("0 Frames");
            txt_meta_validframes.setText("0 Frames");
            txt_meta_cautionframes.setText("0 Frames");
            txt_meta_invalidframes.setText("0 Frames");

            // emptymediapiechartdata(pie_videoaudiochart);
          //  emptymediapiechartdata(pie_metadatachart);


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

        ArrayList<Float> arrayspeed=new ArrayList<>();
        ArrayList<Float> arraytravelled=new ArrayList<>();
        ArrayList<Float> arrayaltitude=new ArrayList<>();
        PolylineOptions options = new PolylineOptions().width(7).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < metricmainarraylist.size(); i++)
        {
            arraycontainer container=metricmainarraylist.get(i);
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
                            if(connectionspeedvalues.size() > 0)
                            {
                                if(connectionspeedvalues.get(connectionspeedvalues.size()-1).getY() != Float.parseFloat(speedarray[0]))
                                    connectionspeedvalues.add(new Entry(connectionspeedvalues.size(), Float.parseFloat(speedarray[0]), 0));
                            }
                            else
                            {
                                connectionspeedvalues.add(new Entry(connectionspeedvalues.size(), Float.parseFloat(speedarray[0]), 0));
                            }
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
                            if(connectiondatadelayvalues.size() > 0)
                            {
                                if(connectiondatadelayvalues.get(connectiondatadelayvalues.size()-1).getY() != Float.parseFloat(array[0]))
                                    connectiondatadelayvalues.add(new Entry(connectiondatadelayvalues.size(), Float.parseFloat(array[0]), 0));
                            }
                            else
                            {
                                connectiondatadelayvalues.add(new Entry(connectionspeedvalues.size(), Float.parseFloat(array[0]), 0));
                            }
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
                            if(gpsaccuracyvalues.size() > 0)
                            {
                                if(gpsaccuracyvalues.get(gpsaccuracyvalues.size()-1).getY() != Float.parseFloat(itemarray[0]))
                                    gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), Float.parseFloat(itemarray[0]), 0));
                            }
                            else
                            {
                                gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), Float.parseFloat(itemarray[0]), 0));
                            }
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
                    String speed=model.getMetricTrackValue();
                    String[] itemarray=speed.split(" ");
                    if(itemarray.length > 0)
                    {
                        try
                        {
                            if(speedgraphitems.size() > 0)
                            {
                                if(speedgraphitems.get(speedgraphitems.size()-1).getY() != Float.parseFloat(itemarray[0]))
                                    speedgraphitems.add(new Entry(speedgraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
                            else
                            {
                                speedgraphitems.add(new Entry(speedgraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
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
                    String travelled=model.getMetricTrackValue();
                    String[] itemarray=travelled.split(" ");
                    if(itemarray.length > 0)
                    {
                        try
                        {
                            if(travelledgraphitems.size() > 0)
                            {
                                if(travelledgraphitems.get(travelledgraphitems.size()-1).getY() != Float.parseFloat(itemarray[0]))
                                    travelledgraphitems.add(new Entry(travelledgraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
                            else
                            {
                                travelledgraphitems.add(new Entry(travelledgraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
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
                            /*if(altitudegraphitems.size() > 0)
                            {
                                if(altitudegraphitems.get(altitudegraphitems.size()-1).getY() != Float.parseFloat(itemarray[0]))
                                    altitudegraphitems.add(new Entry(altitudegraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
                            else
                            {
                                altitudegraphitems.add(new Entry(altitudegraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }*/
                            altitudegraphitems.add(new Entry(altitudegraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            LatLng point = new LatLng(latitude,longitude);
            options.add(point);
        }
        if(mgooglemap != null)
            mgooglemap.addPolyline(options);
            // Source ->   https://stackoverflow.com/questions/17425499/how-to-draw-interactive-polyline-on-route-google-maps-v2-android

        linechart_speed.setVisibility(View.VISIBLE);
        linechart_traveled.setVisibility(View.VISIBLE);
        linechart_altitude.setVisibility(View.VISIBLE);

        if(connectionspeedvalues.size() > 0)
            setlinechartdata(linechart_connectionspeed,-1f,connectionspeedvalues);

        if(connectiondatadelayvalues.size() > 0)
            setlinechartdata(linechart_datatimedelay,-1f,connectiondatadelayvalues);

        if(gpsaccuracyvalues.size() > 0)
            setlinechartdata(linechart_gpsaccuracy,-1f,gpsaccuracyvalues);

        if(speedgraphitems.size() > 0)
            setspeedtraveledaltitudechart(linechart_speed,-1f,speedgraphitems);

        if(travelledgraphitems.size() > 0)
            setspeedtraveledaltitudechart(linechart_traveled,-1f,travelledgraphitems);

        if(altitudegraphitems.size() > 0)
            setspeedtraveledaltitudechart(linechart_altitude,-1f,altitudegraphitems);
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
                if (currentmediaposition < metricmainarraylist.size() && metricmainarraylist.size() > 0) {
                    arraycontainerformetric = new arraycontainer();
                    arraycontainerformetric = metricmainarraylist.get(currentmediaposition);

                    ArrayList<metricmodel> metricItemArraylist = arraycontainerformetric.getMetricItemArraylist();
                    String latitude="",longitude="";
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
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvheading);
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
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("systemuptime")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.uptime),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvuptime);
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
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.address),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvaddress);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("wifiname")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.address),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvwifi);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("screenwidth")){
                            screenwidth = metricItemArraylist.get(j).getMetricTrackValue();
                        }else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("screenheight")){
                            screenheight = metricItemArraylist.get(j).getMetricTrackValue();
                            common.setspannable(getResources().getString(R.string.screen),"\n"+screenwidth+"x"+screenheight, tvscreen);
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

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("devicedate"))
                            tvdate.setText(metricItemArraylist.get(j).getMetricTrackValue());

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequencestarttime"))
                            tvtime.setText(metricItemArraylist.get(j).getMetricTrackValue());

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequenceendtime"))
                            tvtime.setText(tvtime.getText()+" - "+metricItemArraylist.get(j).getMetricTrackValue());

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.phoneclocktime))
                        {
                            String time=metricItemArraylist.get(j).getMetricTrackValue();
                            if(phone_time_clock != null && (! time.trim().isEmpty()) && (! time.equalsIgnoreCase("NA")))
                                phone_time_clock.setPostRecordData(false,time);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.worldclocktime))
                        {
                            String time=metricItemArraylist.get(j).getMetricTrackValue();
                            if(world_time_clock != null && (! time.trim().isEmpty()) && (! time.equalsIgnoreCase("NA")))
                                world_time_clock.setPostRecordData(false,time);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.availablewifinetwork))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.availablewifinetwork),"\n"+
                                    metricItemArraylist.get(j).getMetricTrackValue(), txt_availablewifinetwork);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.speed))
                        {
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
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
                            String value = metricItemArraylist.get(j).getMetricTrackValue();
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
                        drawmappoints(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                    }

                    tvblockchainid.setText(arraycontainerformetric.getVideostarttransactionid());
                    tvblockid.setText(arraycontainerformetric.getHashmethod());
                    tvblocknumber.setText(arraycontainerformetric.getValuehash());
                    tvmetahash.setText(arraycontainerformetric.getMetahash());
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

        if(pulsatinglocationcounter == 0 || pulsatinglocationcounter >= 3)
        {
            pulsatinglocationcounter=0;
            addPulsatingEffect(location);
        }
        pulsatinglocationcounter++;

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
            {
                mgooglemap.setMyLocationEnabled(true);
            }
            else
            {
                mgooglemap.setMyLocationEnabled(false);
            }

            mgooglemap.getUiSettings().setZoomControlsEnabled(true);
            mgooglemap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void addPulsatingEffect(final LatLng userLatlng){

        String gpsaccuracy=xdata.getinstance().getSetting(config.GPSAccuracy);
        if((! gpsaccuracy.trim().isEmpty()) && (! gpsaccuracy.equalsIgnoreCase("NA"))
                && (! gpsaccuracy.equalsIgnoreCase("null")))
        {
            try {
                if(lastPulseAnimator != null)
                    lastPulseAnimator.cancel();

                if(lastUserCircle != null)
                    lastUserCircle.setCenter(userLatlng);

                lastPulseAnimator = valueAnimate(getDisplayPulseRadius(), 3000,
                        new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if(lastUserCircle != null)
                            lastUserCircle.setRadius((Float) animation.getAnimatedValue());
                        else {
                            lastUserCircle = mgooglemap.addCircle(new CircleOptions()
                                .center(userLatlng)
                                .radius((Float) animation.getAnimatedValue())
                                .strokeWidth(0)
                                .fillColor(adjustAlpha(Color.BLUE, 1 - animation.getAnimatedFraction())));
                        }
                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(40, red, green, blue);
    }

    protected float getDisplayPulseRadius() {
        if(mgooglemap == null)
            return 0.0f;

        float currentzoomlevel=mgooglemap.getCameraPosition().zoom;
        Log.e("zoom level ",""+currentzoomlevel);

        if(currentzoomlevel >= 20)
        {
            return 30f;
        }
        else if(currentzoomlevel >= 18)
        {
            return 30f;
        }
        else if(currentzoomlevel >= 16)
        {
            return 50f;
        }
        else if(currentzoomlevel >= 15)
        {
            return 100f;
        }
        else if(currentzoomlevel >= 14)
        {
            return 300f;
        }
        else if(currentzoomlevel >= 13)
        {
            return 400f;
        }
        else if(currentzoomlevel >= 12)
        {
            return 500f;
        }
        else if(currentzoomlevel >= 11)
        {
            return 600f;
        }
        else if(currentzoomlevel >= 10)
        {
            return 700f;
        }
        return 500f;
        /*float diff = (mgooglemap.getMaxZoomLevel() - mgooglemap.getCameraPosition().zoom);
        if (diff < 3)
            return radius;
        if (diff < 3.7)
            return radius * (diff / 2);
        if (diff < 4.5)
            return (radius * diff);
        if (diff < 5.5)
            return (radius * diff) * 1.5f;
        if (diff < 7)
            return (radius * diff) * 2f;
        if (diff < 7.8)
            return (radius * diff) * 3.5f;
        if (diff < 8.5)
            return (float) (radius * diff) * 5;
        if (diff < 10)
            return (radius * diff) * 10f;
        if (diff < 12)
            return (radius * diff) * 18f;
        if (diff < 13)
            return (radius * diff) * 28f;
        if (diff < 16)
            return (radius * diff) * 40f;
        if (diff < 18)
            return (radius * diff) * 60;
        return (radius * diff) * 80;*/
    }

    protected ValueAnimator valueAnimate(float accuracy,long duration, ValueAnimator.AnimatorUpdateListener updateListener){
        Log.d( "valueAnimate: ", "called");
        ValueAnimator va = ValueAnimator.ofFloat(0,accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);

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
                //  Log.e("degree ",""+degree);
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
    public void setchartdata(LineChart linechart,int maximumrangeY)
    {
        // set an alternative background color
        // linechart_speed.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it

        linechart.setNoDataText("");
        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = linechart.getXAxis();
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

        YAxis leftAxis = linechart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(maximumrangeY);
        leftAxis.setAxisMinimum(0);

        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(false);

        linechart.getAxisRight().setEnabled(false);
        linechart.getAxisLeft().setEnabled(false);
        xAxis.setEnabled(false);

        //linechart_speed.getViewPortHandler().setMaximumScaleY(2f);
        //linechart_speed.getViewPortHandler().setMaximumScaleX(2f);

        linechart.setOnChartGestureListener(this);
        linechart.setOnChartValueSelectedListener(this);
        linechart.setDrawGridBackground(false);

        // no description text
        linechart.getDescription().setEnabled(false);

        // enable touch gestures
        linechart.setTouchEnabled(false);

        // enable scaling and dragging
        linechart.setDragEnabled(false);
        linechart.setScaleEnabled(false);
        // linechart_speed.setScaleXEnabled(true);
        // linechart_speed.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        linechart.setPinchZoom(false);
    }

    public  void initlinechart(LineChart chart,Float maxrange)
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
        yAxis.setAxisMaximum(maxrange);
        yAxis.setAxisMinimum(0f);

        // // Create Limit Lines // //
        LimitLine llXAxis = new LimitLine(9f, "Index 10");
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
    }


    public void setlinechartdata(final LineChart chart, Float value, ArrayList<Entry> gpsaccuracyvalues)
    {
        if(gpsaccuracyvalues.size() > 0)
        {
            if(gpsaccuracyvalues.get(gpsaccuracyvalues.size()-1).getY() == value)
                return;
        }

        if(value != -1)
            gpsaccuracyvalues.add(new Entry(gpsaccuracyvalues.size(), value, 0));

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(gpsaccuracyvalues);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(gpsaccuracyvalues, "");

            set1.setDrawIcons(false);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.GREEN);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            set1.setFillColor(Color.TRANSPARENT);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }

        chart.animateX(0);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setspeedtraveledaltitudechart(LineChart linechart,Float value, ArrayList<Entry> arrayitems) {

        if(arrayitems.size() > 0)
        {
            if(arrayitems.get(arrayitems.size()-1).getY() == value)
                return;
        }

        if(value != -1)
            arrayitems.add(new Entry(arrayitems.size(), value, 0));

        LineDataSet set1;
        if (linechart.getData() != null && linechart.getData().getDataSetCount() > 0 && arrayitems.size() > 0)
        {
            set1 = (LineDataSet) linechart.getData().getDataSetByIndex(0);
            set1.setValues(arrayitems);
            set1.setHighlightEnabled(true);
            LineData data = new LineData(set1);
            linechart.setData(data);
            linechart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(arrayitems, "");
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

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
            linechart.setData(data);
        }

        linechart.animateX(0);
        Legend l = linechart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    public void setlayoutmargin(){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        layout_constraint.setLayoutParams(params);
        layout_constraint.requestLayout();
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
        String chartdatavalue = value;
        if(chartdatavalue.equalsIgnoreCase("NA")){
            chartdatavalue = "0";
        }else{
            chartdatavalue= chartdatavalue.substring(0, chartdatavalue.indexOf("%"));
        }
        int remainingvalue = 100 - Integer.parseInt(chartdatavalue);

        entries.add(new PieEntry(Integer.parseInt(chartdatavalue),
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
        colors.add(Color.parseColor(config.color_code_transparent));

        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        piechart.setData(data);
    }

    public void emptymediapiechartdata(PieChart piechart)
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
        entries.add(new PieEntry(10,items[0 % items.length],0));
        PieDataSet dataSet = new PieDataSet(entries, "");
        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        piechart.setData(data);
    }
}
