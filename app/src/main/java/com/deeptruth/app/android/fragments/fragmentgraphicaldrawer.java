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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

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

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.layout_datalatency)
    LinearLayout layout_datalatency;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_niddle)
    ImageView img_niddle;
    @BindView(R.id.linechart)
    LineChart mChart;
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

    View rootview;
    GoogleMap mgooglemap;
    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;
    public ScrollView scrollview_meta;
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private boolean isinbackground=false,ismediaplayer = false,isgraphicopen=false,isdatacomposing=false;
    String latitude = "", longitude = "",screenheight = "",screenwidth = "",lastsavedangle="";
    private float currentDegree = 0f;
    String hashmethod= "",videostarttransactionid = "",valuehash = "",metahash = "";
    ArrayList<Entry> latencyvalues = new ArrayList<Entry>();
    private Orientation mOrientation;
    private String[] transparentarray=common.gettransparencyvalues();
    int navigationbarheight = 0;
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

            phone_time_clock.setTimeZone("MST", new itemupdatelistener() {
                @Override
                public void onitemupdate(Object object) {
                    if(object != null)
                    {
                        Calendar calendar=(Calendar)object;
                        txt_phone_time.setText(common.appendzero(calendar.get(Calendar.HOUR))+":"+common.appendzero(calendar.get(Calendar.MINUTE))
                                +":"+common.appendzero(calendar.get(Calendar.SECOND))+" MST");
                    }
                }

                @Override
                public void onitemupdate(Object object, int type) {

                }
            });
            world_time_clock.setTimeZone("GMT", new itemupdatelistener() {
                @Override
                public void onitemupdate(Object object) {
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

            /*Calendar calendar = Calendar.getInstance();
            phone_time_clock.setCalendar(calendar)
                    .setDiameterInDp(400.0f)
                    .setOpacity(1.0f)
                    .setShowSeconds(true)
                    .setColor(Color.WHITE);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.HOUR,-2);
            world_time_clock.setCalendar(calendar2)
                    .setDiameterInDp(400.0f)
                    .setOpacity(1.0f)
                    .setShowSeconds(true)
                    .setColor(Color.WHITE);*/

            loadmap();
            setchartdata();

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
        /*if(isinbackground)
            return;

        if(! isgraphicopen && (! ismediaplayer))
            return;*/
        String latitudedegree  = xdata.getinstance().getSetting(config.Latitude);
        String longitudedegree = xdata.getinstance().getSetting(config.Longitude);
        String altitude=xdata.getinstance().getSetting(config.Altitude);
        String value=common.getxdatavalue(xdata.getinstance().getSetting(config.Heading));

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

        if(! altitude.isEmpty() && (! altitude.equalsIgnoreCase("NA")))
        {
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude),"\n"+altitude, tvaltitude);
        }
        else
        {
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.altitude),"\n"+"NA", tvaltitude);
        }

            /**/

            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.traveled),"\n"+xdata.getinstance().getSetting(config.distancetravelled), tvtraveled);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.speed),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)), tvspeed);
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
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.connection),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Connectionspeed)), tvconnection);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.version),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.OSversion)), tvversion);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.wifi),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.WIFINetwork)), tvwifi);
            if(!common.getxdatavalue(xdata.getinstance().getSetting(config.GPSAccuracy)).equals("NA")){
                DecimalFormat precision=new DecimalFormat("0.0");
                double gpsaccuracy = Double.parseDouble(common.getxdatavalue(xdata.getinstance().getSetting(config.GPSAccuracy)));
                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),"\n"+precision.format(gpsaccuracy)+ " feet", tvgpsaccuracy);
            }else{
                common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.gpsaccuracy),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.GPSAccuracy)), tvgpsaccuracy);
            }
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.screen),"\n"+xdata.getinstance().getSetting(config.ScreenWidth) +"*" +xdata.getinstance().getSetting(config.ScreenHeight), tvscreen);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.country),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Country)), tvcountry);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.cpuusage),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.CPUUsage)), tvcpuusage);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.brightness),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Brightness)), tvbrightness);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.timezone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.TimeZone)), tvtimezone);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.memoryusage),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.MemoryUsage)), tvmemoryusage);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.bluetooth),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Bluetooth)), tvbluetooth);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.localtime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.LocalTime)), tvlocaltime);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.storagefree),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.StorageAvailable)), tvstoragefree);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.language),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Language)), tvlanguage);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.uptime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.SystemUptime)), tvuptime);
            common.setdrawabledata(applicationviavideocomposer.getactivity().getResources().getString(R.string.battery),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Battery)), tvbattery);
            common.setdrawabledata("",common.getdate(), tvdate);
            common.setdrawabledata("",common.gettime(), tvtime);
            common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.blockchainid)), tvblockchainid);
            common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.hashformula)), tvblockid);
            common.setdrawabledata("", " "+common.getxdatavalue(xdata.getinstance().getSetting(config.datahash)), tvblocknumber);
            common.setdrawabledata(""," "+common.getxdatavalue(xdata.getinstance().getSetting(config.matrichash)), tvmetahash);

            String latitude=xdata.getinstance().getSetting(config.Latitude);
            String longitude=xdata.getinstance().getSetting(config.Longitude);


            if(isdatacomposing)
            {
                latitude=xdata.getinstance().getSetting("lat");
                longitude=xdata.getinstance().getSetting("lng");
            }

            if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                    (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
            {
                populatelocationonmap(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                drawmappoints(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
            }

        if(! isdatacomposing)
        {
            if(xdata.getinstance().getSetting(config.Heading).toString().trim().length() > 0)
            {
                String strdegree=common.getxdatavalue(xdata.getinstance().getSetting(config.Heading));

                if((! strdegree.equalsIgnoreCase("NA")) && (! strdegree.equalsIgnoreCase("NA")))
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

        String latency = xdata.getinstance().getSetting(config.currentlatency).toString();
        if(latency != null && (! latency.trim().isEmpty()) )
        {
            final String[] currentlatencyarray = latency.split(",");
            if(latencyvalues.size() > 0)
            {
                if(currentlatencyarray.length < latencyvalues.size())
                {
                    layout_datalatency.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            float currentlatency=currentlatencyarray.length;
                            float maxlatency=latencyvalues.size();
                            float latencypercentage=(currentlatency/maxlatency);
                            latencypercentage=latencypercentage*100;

                            int viewmaxwidth=layout_datalatency.getWidth();
                            final float viewcurrentwidth=(latencypercentage*viewmaxwidth)/100;
                            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(2,
                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                            p.setMargins((int)viewcurrentwidth,0, 0, 0);
                            view_latencyline.setLayoutParams(p);

                            /*Animation a = new Animation() {

                                @Override
                                protected void applyTransformation(float interpolatedTime, Transformation t) {
                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_latencyline.getLayoutParams();
                                    params.leftMargin = (int)(viewcurrentwidth * interpolatedTime);
                                    view_latencyline.setLayoutParams(params);
                                }
                            };
                            a.setDuration(700); // in ms
                            a.setFillAfter(false);
                            view_latencyline.startAnimation(a);*/

                            /*TranslateAnimation animation = new TranslateAnimation(view_latencyline.getX(), viewcurrentwidth,
                                    0.0f, 0.0f);
                            animation.setDuration(700);
                            animation.setRepeatCount(1);
                            animation.setFillAfter(false);
                            view_latencyline.startAnimation(animation);*/
                        }
                    });

                }
            }
        }
    }

    public void setdatacomposing(boolean isdatacomposing,String mediafilepath)
    {
        if(! isdatacomposing)
        {
            metricmainarraylist.clear();
            if(mgooglemap != null)
                mgooglemap.clear();

            fetchmetadatafromdb(mediafilepath);
            drawmappath();
        }
    }

    public void setdatacomposing(boolean isdatacomposing)
    {
        this.isdatacomposing=isdatacomposing;
        if(! isdatacomposing)
        {
            latencyvalues.clear();
            setchartdata();
            String latency = xdata.getinstance().getSetting(config.latency).toString();
            if(latency != null && (! latency.trim().isEmpty()) )
            {
                if(layout_datalatency != null)
                    layout_datalatency.setVisibility(View.VISIBLE);

                String[] latencyarray = latency.split(",");
                for(int i = 0 ;i< latencyarray.length;i++)
                {
                    //float val = (float) (Math.random() * 20) + 3;
                    Entry entry=new Entry();
                    entry.setX(latencyvalues.size());
                    entry.setY(Float.parseFloat(latencyarray[i]));
                    latencyvalues.add(entry);
                }
                setlatencydata();
                mChart.animateX(10);
                Legend l = mChart.getLegend();
                l.setForm(Legend.LegendForm.LINE);
            }
            else
            {
                if(layout_datalatency != null)
                    layout_datalatency.setVisibility(View.GONE);
            }
        }
        else
        {
            if(layout_datalatency != null)
                layout_datalatency.setVisibility(View.GONE);
        }
    }

    public void drawmappath()
    {
        PolylineOptions options = new PolylineOptions().width(7).color(Color.RED).geodesic(true);
        for (int i = 0; i < metricmainarraylist.size(); i++) {
            arraycontainer container=metricmainarraylist.get(i);
            ArrayList<metricmodel> arraylist=container.getMetricItemArraylist();
            double latitude=0.0,longitude=0.0;
            for(int j=0;j<arraylist.size();j++)
            {
                metricmodel model=arraylist.get(j);
                if(model.getMetricTrackKeyName().equalsIgnoreCase("gpslatitude"))
                {
                    latitude=Double.parseDouble(model.getMetricTrackValue());
                }
                if(model.getMetricTrackKeyName().equalsIgnoreCase("gpslongitude"))
                {
                    longitude=Double.parseDouble(model.getMetricTrackValue());
                }
            }
            LatLng point = new LatLng(latitude,longitude);
            options.add(point);
        }
        /*options.add(new LatLng(26.763653, 75.645427));
        options.add(new LatLng(26.860102, 75.753649));
        options.add(new LatLng(26.888557, 75.810985));*/
        if(mgooglemap != null)
            mgooglemap.addPolyline(options);

        // Source ->   https://stackoverflow.com/questions/17425499/how-to-draw-interactive-polyline-on-route-google-maps-v2-android
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
                parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash,color,latency);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency) {
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
                        color,latency));
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
                            metahash,color,latency));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        ra.setDuration(250);
        ra.setFillAfter(true);
        img_niddle.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onStop() {
        super.onStop();
        isinbackground=true;
        if(mOrientation != null)
            mOrientation.stopListening();
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
        if(mOrientation != null)
            mOrientation.startListening(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        msensormanager.unregisterListener(this);
    }

    public void setdrawerproperty(boolean isgraphicopen)
    {
        this.isgraphicopen=isgraphicopen;
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
    public void setchartdata()
    {
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
        //leftAxis.setAxisMaximum(60f);
        //leftAxis.setAxisMinimum(0);

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
    }


    private void setlatencydata() {

        LineDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0 && latencyvalues.size() > 0)
        {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(latencyvalues);
            set1.setHighlightEnabled(true);
            LineData data = new LineData(set1);
            mChart.setData(data);
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(latencyvalues, "");
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
            mChart.setData(data);
        }
    }

    public void setlayoutmargin(){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        layout_constraint.setLayoutParams(params);
        layout_constraint.requestLayout();
    }
}
