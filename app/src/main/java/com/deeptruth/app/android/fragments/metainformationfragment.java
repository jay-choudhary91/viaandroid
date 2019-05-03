package com.deeptruth.app.android.fragments;


import android.Manifest;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.android.gms.maps.model.LatLng;
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
    private String[] transparentarray= common.gettransparencyvalues();
    int navigationbarheight = 0;
    arraycontainer arraycontainerformetric =null;
    View rootview;
    String screenwidth,screenheight;

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

            settextviewcolor();
            layout_encryptioninfo.setVisibility(View.GONE);

            seekbar_mediavideoaudio.setEnabled(false);
            seekbar_mediametadata.setEnabled(false);
            vertical_slider_speed.setEnabled(false);
            vertical_slider_altitude.setEnabled(false);
            vertical_slider_traveled.setEnabled(false);
            linear_mediametadata.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));
            linear_mediavideoaudio.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));

           /* TimeZone timezone = TimeZone.getDefault();
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
            });*/

            navigationbarheight =  common.getnavigationbarheight();

            //setmetadatavalue();
            mOrientation = new Orientation(applicationviavideocomposer.getactivity());

            loadmap();

            halfpaichartdate(chart_memoeyusage);
            halfpaichartdate(chart_cpuusage);
            halfpaichartdate(chart_battery);

            setchartdata(linechart_speed,60);
            setchartdata(linechart_traveled,30);
            setchartdata(linechart_altitude,2000);

            initlinechart(linechart_connectionspeed);
            initlinechart(linechart_datatimedelay);
            initlinechart(linechart_gpsaccuracy);

            //visiblity gone of media information
            layout_videoaudiodata.setVisibility(View.GONE);
            layout_mediametadata.setVisibility(View.GONE);
            txt_mediainformation.setVisibility(View.GONE);

            vertical_slider_speed.setMax(60);
            vertical_slider_altitude.setMax(2000);
            vertical_slider_traveled.setMax(30);
            vertical_slider_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

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
        if(! isdatacomposing)
        {
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
                    for (int j = 0; j < metricItemArraylist.size(); j++)
                    {

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.heading)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.heading),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvheading);

                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpslatitudedegree)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.latitude),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvlatitude);
                        }else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.gpslongitudedegree)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.longitude),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvlongitude);
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
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("country")){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.country),"\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvcountry);
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
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.blockchainid)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.blockchain_id),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvblockchainid);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.hashformula)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.hash_formula),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvblockid);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.datahash)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.metadata),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvblocknumber);
                        }
                        else if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.matrichash)){
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.mediahash),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue(), tvmetahash);
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
                       //     tvdate.setText(metricItemArraylist.get(j).getMetricTrackValue());

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequencestarttime"))
                    //        tvtime.setText(metricItemArraylist.get(j).getMetricTrackValue());

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("sequenceendtime"))
                    //        tvtime.setText(tvtime.getText()+" - "+metricItemArraylist.get(j).getMetricTrackValue());

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

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("connectionspeed"))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.connection),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue() , tvconnection);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.connectiondatadelay))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.data_time_delay),
                                    "\n"+metricItemArraylist.get(j).getMetricTrackValue() ,txt_datatimedelay);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.availablewifinetwork))
                        {
                            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.availablewifinetwork),"\n"+
                                    metricItemArraylist.get(j).getMetricTrackValue(), txt_availablewifinetwork);
                        }

                        if(metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase(config.itemgpsaccuracy))
                        {
                            String value=metricItemArraylist.get(j).getMetricTrackValue();
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
                    }
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
                    seekbar.setProgress(Integer.parseInt(array[0]));
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
        speedgraphitems.clear();

        ArrayList<Float> arrayspeed=new ArrayList<>();
        ArrayList<Float> arraytravelled=new ArrayList<>();
        ArrayList<Float> arrayaltitude=new ArrayList<>();
        PolylineOptions options = new PolylineOptions().width(7).color(Color.RED).geodesic(true);
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
                            if(altitudegraphitems.size() > 0)
                            {
                                if(altitudegraphitems.get(altitudegraphitems.size()-1).getY() != Float.parseFloat(itemarray[0]))
                                    altitudegraphitems.add(new Entry(altitudegraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
                            else
                            {
                                altitudegraphitems.add(new Entry(altitudegraphitems.size(), Float.parseFloat(itemarray[0]), 0));
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            LatLng point = new LatLng(latitude,longitude);
            if(latitude != 0){
                populatelocationonmap(new LatLng(latitude,longitude));
                drawmappoints(new LatLng(latitude,longitude));
            }
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

                if(! metricmainarraylist.get(i).getColor().trim().isEmpty())
                {
                    sectioncount++;
                    if(! lastcolor.equalsIgnoreCase(metricmainarraylist.get(i).getColor()))
                    {
                        colorsectioncount.add(metricmainarraylist.get(i).getColor()+","+sectioncount);
                    }
                    else
                    {
                        colorsectioncount.set(colorsectioncount.size()-1,metricmainarraylist.get(i).getColor()+","+sectioncount);
                    }
                    lastcolor=metricmainarraylist.get(i).getColor();
                }
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

    public View getmediaseekbarbackgroundview(String weight,String color)
    {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
        View view = new View(applicationviavideocomposer.getactivity());
        view.setLayoutParams(param);
        if((! color.isEmpty()))
        {
            view.setBackgroundColor(Color.parseColor(common.getcolorbystring(color)));
        }
        else
        {
            view.setBackgroundColor(Color.parseColor(config.color_code_gray));
        }
        return view;
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

    public  void initlinechart(LineChart chart)
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
        //yAxis.setAxisMaximum(20f);
        //yAxis.setAxisMinimum(0f);

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

        googlemap.setVisibility(View.VISIBLE);

        zoomgooglemap(location.latitude,location.longitude);
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
    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
            try {
                if(isdatacomposing)
                {
                    mgooglemap.clear();
                }
                else
                {
                    mgooglemap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .icon(common.bitmapdescriptorfromvector(applicationviavideocomposer.getactivity(),
                                    R.drawable.rounded_gps_dot)));
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void settextviewcolor(){
        txt_availablewifinetwork.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvaddress.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvlatitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvlongitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvaltitude.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvspeed.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvheading.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvtraveled.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvxaxis.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvyaxis.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvzaxis.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvphone.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvnetwork.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvconnection.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvversion.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvwifi.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvgpsaccuracy.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvscreen.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvcountry.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvcpuusage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvbrightness.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvtimezone.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvmemoryusage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvbluetooth.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvlocaltime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvstoragefree.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvlanguage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvuptime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvbattery.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvlocationanalytics.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvlocationtracking.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvorientation.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvPhoneanalytics.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_meta_cautionframes.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_meta_validframes.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_meta_invalidframes.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_Memory.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_mediainformation.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_videoaudio_cautionframes.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_videoaudio_invalidframes.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_videoaudio_validframes.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_videoaudiodata.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_cpuusage.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_phonetime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txtbattery.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_valid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_caution.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_invalid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_timeinformation.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_validmeta.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_invalidmeta.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_cautionmeta.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_worldclocktime.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        text_meta.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_connectioninformation.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_datatimedelay.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_world_time.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        txt_phone_time.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvblockchainid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvblockchainid.setVisibility(View.GONE);
        tvblockid.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvblockid.setVisibility(View.GONE);
        tvblocknumber.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvblocknumber.setVisibility(View.GONE);
        tvmetahash.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        tvmetahash.setVisibility(View.GONE);
        text_encryption.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
        text_encryption.setVisibility(View.GONE);
    }
}
