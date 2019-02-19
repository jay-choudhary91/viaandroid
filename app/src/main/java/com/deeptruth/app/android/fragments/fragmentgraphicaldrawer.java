package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.sensor.AttitudeIndicator;
import com.deeptruth.app.android.sensor.Orientation;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.layout_datalatency)
    LinearLayout layout_datalatency;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_compass)
    ImageView img_compass;
    @BindView(R.id.linechart)
    LineChart mChart;
    @BindView(R.id.attitude_indicator)
    AttitudeIndicator attitudeindicator;

    View rootview;
    GoogleMap mgooglemap;
    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;

    private boolean isinbackground=false,ismediaplayer = false,isgraphicopen=false,isdatacomposing=false;
    String latitude = "", longitude = "",screenheight = "",screenwidth = "",lastsavedangle="";
    private float currentDegree = 0f;
    String hashmethod= "",videostarttransactionid = "",valuehash = "",metahash = "";
    ArrayList<Entry> latencyvalues = new ArrayList<Entry>();
    private Orientation mOrientation;
    @Override
    public int getlayoutid() {
        return R.layout.frag_graphicaldrawer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootview == null) {

            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            msensormanager = (SensorManager) applicationviavideocomposer.getactivity().getSystemService(Context.SENSOR_SERVICE);

          tvaddress.setTextColor(getResources().getColor(R.color.white));
          tvlatitude.setTextColor(getResources().getColor(R.color.white));
          tvlongitude.setTextColor(getResources().getColor(R.color.white));
          tvaltitude.setTextColor(getResources().getColor(R.color.white));
          tvspeed.setTextColor(getResources().getColor(R.color.white));
          tvheading.setTextColor(getResources().getColor(R.color.white));
          tvtraveled.setTextColor(getResources().getColor(R.color.white));
          tvxaxis.setTextColor(getResources().getColor(R.color.white));
          tvyaxis.setTextColor(getResources().getColor(R.color.white));
          tvzaxis.setTextColor(getResources().getColor(R.color.white));
          tvphone.setTextColor(getResources().getColor(R.color.white));
          tvnetwork.setTextColor(getResources().getColor(R.color.white));
          tvconnection.setTextColor(getResources().getColor(R.color.white));
          tvversion.setTextColor(getResources().getColor(R.color.white));
          tvwifi.setTextColor(getResources().getColor(R.color.white));
          tvgpsaccuracy.setTextColor(getResources().getColor(R.color.white));
          tvscreen.setTextColor(getResources().getColor(R.color.white));
          tvcountry.setTextColor(getResources().getColor(R.color.white));
          tvcpuusage.setTextColor(getResources().getColor(R.color.white));
          tvbrightness.setTextColor(getResources().getColor(R.color.white));
          tvtimezone.setTextColor(getResources().getColor(R.color.white));
          tvmemoryusage.setTextColor(getResources().getColor(R.color.white));
          tvbluetooth.setTextColor(getResources().getColor(R.color.white));
          tvlocaltime.setTextColor(getResources().getColor(R.color.white));
          tvstoragefree.setTextColor(getResources().getColor(R.color.white));
          tvlanguage.setTextColor(getResources().getColor(R.color.white));
          tvuptime.setTextColor(getResources().getColor(R.color.white));
          tvbattery.setTextColor(getResources().getColor(R.color.white));
          tvblockchainid.setTextColor(getResources().getColor(R.color.white));
          tvblockid.setTextColor(getResources().getColor(R.color.white));
          tvblocknumber.setTextColor(getResources().getColor(R.color.white));
          tvmetahash.setTextColor(getResources().getColor(R.color.white));
          tvlocationanalytics.setTextColor(getResources().getColor(R.color.white));
          tvlocationtracking.setTextColor(getResources().getColor(R.color.white));
          tvorientation.setTextColor(getResources().getColor(R.color.white));
          tvPhoneanalytics.setTextColor(getResources().getColor(R.color.white));
          tvencryption.setTextColor(getResources().getColor(R.color.white));
          tvdataletency.setTextColor(getResources().getColor(R.color.white));

            //setmetadatavalue();
            mOrientation = new Orientation(applicationviavideocomposer.getactivity());
            loadmap();
            setchartdata();

        }
        return rootview;
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        //attitudeindicator.setAttitude(pitch, roll);
    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        if(xdata.getinstance().getSetting(config.latency).trim().isEmpty() && gethelper().getrecordingrunning())
        {
            float pitch = orientation[1] * -57;
            float roll = orientation[2] * -57;
            attitudeindicator.setAttitude(pitch, roll);
        }
    }

    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
            if(isdatacomposing)
            {
                mgooglemap.clear();
            }
            else
            {
                mgooglemap.addMarker(new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
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
        Log.e("distancetravelled",xdata.getinstance().getSetting(config.distancetravelled));

        if(! latitudedegree.isEmpty() && (! latitudedegree.equalsIgnoreCase("NA")))
        {
            common.setdrawabledata(getResources().getString(R.string.latitude),"\n"+latitudedegree, tvlatitude);
        }
        else
        {
            common.setdrawabledata(getResources().getString(R.string.latitude),"\n"+"NA", tvlatitude);
        }

        if(! longitudedegree.isEmpty() && (! longitudedegree.equalsIgnoreCase("NA")))
        {
            common.setdrawabledata(getResources().getString(R.string.longitude),"\n"+longitudedegree, tvlongitude);
        }
        else
        {
            common.setdrawabledata(getResources().getString(R.string.longitude),"\n"+"NA", tvlongitude);
        }

        if(! altitude.isEmpty() && (! altitude.equalsIgnoreCase("NA")))
        {
            common.setdrawabledata(getResources().getString(R.string.altitude),"\n"+altitude, tvaltitude);
        }
        else
        {
            common.setdrawabledata(getResources().getString(R.string.altitude),"\n"+"NA", tvaltitude);
        }

        if((! value.equalsIgnoreCase("NA")) && (! value.equalsIgnoreCase("NA")))
        {
            double heading=Double.parseDouble(value);
            int headingg=(int)heading;
            common.setdrawabledata(getResources().getString(R.string.heading),"\n"+headingg, tvheading);
        }
        else
        {
            common.setdrawabledata(getResources().getString(R.string.heading),"\n"+"NA", tvheading);
        }

            common.setdrawabledata(getResources().getString(R.string.traveled),"\n"+xdata.getinstance().getSetting(config.distancetravelled), tvtraveled);
            common.setdrawabledata(getResources().getString(R.string.speed),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)), tvspeed);
            common.setdrawabledata("","\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Address)), tvaddress);
            common.setdrawabledata(getResources().getString(R.string.xaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_x), tvxaxis);
            common.setdrawabledata(getResources().getString(R.string.yaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_y), tvyaxis);
            common.setdrawabledata(getResources().getString(R.string.zaxis),"\n"+xdata.getinstance().getSetting(config.acceleration_z), tvzaxis);
            common.setdrawabledata(getResources().getString(R.string.phone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.PhoneType)), tvphone);
            common.setdrawabledata(getResources().getString(R.string.network),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.CellProvider)), tvnetwork);
            common.setdrawabledata(getResources().getString(R.string.connection),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Connectionspeed)), tvconnection);
            common.setdrawabledata(getResources().getString(R.string.version),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.OSversion)), tvversion);
            common.setdrawabledata(getResources().getString(R.string.wifi),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.WIFINetwork)), tvwifi);
            common.setdrawabledata(getResources().getString(R.string.gpsaccuracy),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.GPSAccuracy)), tvgpsaccuracy);
            common.setdrawabledata(getResources().getString(R.string.screen),"\n"+xdata.getinstance().getSetting(config.ScreenWidth) +"*" +xdata.getinstance().getSetting(config.ScreenHeight), tvscreen);
            common.setdrawabledata(getResources().getString(R.string.country),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Country)), tvcountry);
            common.setdrawabledata(getResources().getString(R.string.cpuusage),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.CPUUsage)), tvcpuusage);
            common.setdrawabledata(getResources().getString(R.string.brightness),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Brightness)), tvbrightness);
            common.setdrawabledata(getResources().getString(R.string.timezone),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.TimeZone)), tvtimezone);
            common.setdrawabledata(getResources().getString(R.string.memoryusage),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.MemoryUsage)), tvmemoryusage);
            common.setdrawabledata(getResources().getString(R.string.bluetooth),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Bluetooth)), tvbluetooth);
            common.setdrawabledata(getResources().getString(R.string.localtime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.LocalTime)), tvlocaltime);
            common.setdrawabledata(getResources().getString(R.string.storagefree),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.StorageAvailable)), tvstoragefree);
            common.setdrawabledata(getResources().getString(R.string.language),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Language)), tvlanguage);
            common.setdrawabledata(getResources().getString(R.string.uptime),"\n"+ common.getxdatavalue(xdata.getinstance().getSetting(config.SystemUptime)), tvuptime);
            common.setdrawabledata(getResources().getString(R.string.battery),"\n"+common.getxdatavalue(xdata.getinstance().getSetting(config.Battery)), tvbattery);
            common.setdrawabledata(getResources().getString(R.string.blockchain_id)," "+common.getxdatavalue(xdata.getinstance().getSetting(config.blockchainid)), tvblockchainid);
            common.setdrawabledata(getResources().getString(R.string.block_id)," "+common.getxdatavalue(xdata.getinstance().getSetting(config.hashformula)), tvblockid);
            common.setdrawabledata(getResources().getString(R.string.block_number), " "+common.getxdatavalue(xdata.getinstance().getSetting(config.datahash)), tvblocknumber);
            common.setdrawabledata(getResources().getString(R.string.metrichash)," "+common.getxdatavalue(xdata.getinstance().getSetting(config.matrichash)), tvmetahash);

            String latitude=xdata.getinstance().getSetting(config.Latitude);
            String longitude=xdata.getinstance().getSetting(config.Longitude);
            if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                    (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
            {
                populatelocationonmap(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                drawmappoints(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
            }

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

        if(! xdata.getinstance().getSetting(config.attitude_data).trim().isEmpty())
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
                attitudeindicator.setAttitude(pitch, roll);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        latencyvalues.clear();
        String latency = xdata.getinstance().getSetting(config.latency).toString();
        if(latency != null && (! latency.trim().isEmpty()) )
        {
            layout_datalatency.setVisibility(View.VISIBLE);
            String[] latencyarray = latency.split(",");
            for(int i = 0 ;i< latencyarray.length;i++){
                //float val = (float) (Math.random() * 20) + 3;
                Entry entry=new Entry();
                entry.setX(latencyvalues.size());
                entry.setY(Float.parseFloat(latencyarray[i]));
                if(latencyvalues.size() <= 10)
                {
                    Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.fade_red);
                    entry.setIcon(drawable);
                }
                else if(latencyvalues.size() <= 20)
                {
                    Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.gradient_verify_green);
                    entry.setIcon(drawable);
                }
                else if(latencyvalues.size() <= 30)
                {
                    Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.gradient_fade_header_blue);
                    entry.setIcon(drawable);
                }
                else if(latencyvalues.size() <= 40)
                {
                    Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.gradient_verify_yellow);
                    entry.setIcon(drawable);
                }

                latencyvalues.add(entry);
            }
            setlatencydata();
            mChart.animateX(10);
            Legend l = mChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
        }
        else
        {
            layout_datalatency.setVisibility(View.GONE);
        }
    }


    public void setmetadatavalue(){

        common.setspannable(getResources().getString(R.string.latitude),"\n"+"N/A", tvlatitude);
        common.setspannable(getResources().getString(R.string.longitude),"\n"+"N/A", tvlongitude);
        common.setspannable(getResources().getString(R.string.altitude),"\n"+"N/A", tvaltitude);
        common.setspannable(getResources().getString(R.string.speed),"\n"+"N/A", tvspeed);
        common.setspannable(getResources().getString(R.string.heading),"\n"+"N/A", tvheading);
        common.setspannable(getResources().getString(R.string.traveled),"\n"+"N/A", tvtraveled);
        common.setspannable("","N/A", tvaddress);
        common.setspannable(getResources().getString(R.string.xaxis),"\n"+"N/A", tvxaxis);
        common.setspannable(getResources().getString(R.string.yaxis),"\n"+"N/A", tvyaxis);
        common.setspannable(getResources().getString(R.string.zaxis),"\n"+"N/A", tvzaxis);
        common.setspannable(getResources().getString(R.string.phone),"\n"+"N/A", tvphone);
        common.setspannable(getResources().getString(R.string.network),"\n"+"N/A", tvnetwork);
        common.setspannable(getResources().getString(R.string.connection),"\n"+"N/A", tvconnection);
        common.setspannable(getResources().getString(R.string.version),"\n"+"N/A", tvversion);
        common.setspannable(getResources().getString(R.string.wifi),"\n"+"N/A", tvwifi);
        common.setspannable(getResources().getString(R.string.gpsaccuracy),"\n"+"N/A", tvgpsaccuracy);
        common.setspannable(getResources().getString(R.string.screen),"\n"+screenwidth+"x"+screenheight, tvscreen);
        common.setspannable(getResources().getString(R.string.country),"\n"+"N/A", tvcountry);
        common.setspannable(getResources().getString(R.string.cpuusage),"\n"+"N/A", tvcpuusage);
        common.setspannable(getResources().getString(R.string.brightness),"\n"+"N/A", tvbrightness);
        common.setspannable(getResources().getString(R.string.timezone),"\n"+"N/A", tvtimezone);
        common.setspannable(getResources().getString(R.string.memoryusage),"\n"+"N/A", tvmemoryusage);
        common.setspannable(getResources().getString(R.string.bluetooth),"\n"+"N/A", tvbluetooth);
        common.setspannable(getResources().getString(R.string.localtime),"\n"+"N/A", tvlocaltime);
        common.setspannable(getResources().getString(R.string.storagefree),"\n"+"N/A", tvstoragefree);
        common.setspannable(getResources().getString(R.string.language),"\n"+"N/A", tvlanguage);
        common.setspannable(getResources().getString(R.string.uptime),"\n"+"N/A", tvuptime);
        common.setspannable(getResources().getString(R.string.battery),"\n"+"N/A", tvbattery);

    }




    public static void locationAnalyticsdata(final TextView txt_latitude, final TextView txt_longitude, final TextView txt_altitude, final TextView txt_heading,
                                             final TextView txt_orientation, final TextView txt_speed, final TextView txt_address) {

        {
            StringBuilder mformatbuilder = new StringBuilder();
            final String latitude=xdata.getinstance().getSetting(config.LatitudeDegree);

            if(! latitude.isEmpty() && (! latitude.equalsIgnoreCase("NA")))
            {
                mformatbuilder.append(config.Latitude+System.getProperty("line.separator")+ latitude);
            }
            else
            {
                mformatbuilder.append(System.getProperty("line.separator")+config.Latitude+System.getProperty("line.separator")+"NA");
            }

            final String altitude=xdata.getinstance().getSetting(config.Altitude);

            if(! altitude.isEmpty() && (! altitude.equalsIgnoreCase("NA")))
            {
                mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Altitude+System.getProperty("line.separator")+
                        common.getxdatavalue(xdata.getinstance().getSetting(config.Altitude)));
            }
            else
            {
                mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Altitude+System.getProperty("line.separator")+"NA");
            }
            String value=common.getxdatavalue(xdata.getinstance().getSetting(config.Heading));
            if((! value.equalsIgnoreCase("NA")) && (! value.equalsIgnoreCase("NA")))
            {
                double heading=Double.parseDouble(value);
                int headingg=(int)heading;
                mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Heading+System.getProperty("line.separator")+headingg);
            }
            else
            {
                mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Heading+System.getProperty("line.separator"));
            }

            txt_latitude.setText(mformatbuilder.toString());
        }

        {
            StringBuilder mformatbuilder = new StringBuilder();
            final String longitude=xdata.getinstance().getSetting(config.LongitudeDegree);
            if(! longitude.isEmpty() && (! longitude.equalsIgnoreCase("NA")))
            {
                mformatbuilder.append(config.Longitude+System.getProperty("line.separator")+
                        longitude);
            }
            else
            {
                mformatbuilder.append(System.getProperty("line.separator")+config.Longitude+System.getProperty("line.separator")+"NA");
            }

            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Speed+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)));
            mformatbuilder.append(System.getProperty("line.separator")+System.getProperty("line.separator")+config.Orientation+System.getProperty("line.separator")+
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Orientation)));

            txt_longitude.setText(mformatbuilder.toString());

            txt_address.setText(common.getxdatavalue(xdata.getinstance().getSetting(config.Address)));
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

    public void setdatacomposing(boolean isdatacomposing)
    {
        this.isdatacomposing=isdatacomposing;
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

            List<ILineDataSet> dataSets=mChart.getLineData().getDataSets();
            /*ArrayList<ILineDataSet> newdataSets = new ArrayList<ILineDataSet>();
            for(int i=0;i<dataSets.size();i++)
            {
                ILineDataSet ilineDataSet=dataSets.get(i);
                int count=ilineDataSet.getEntryCount();
                count=count/2;
                for(int j=0;j<count;j++)
                {
                    if(j < count)
                    {
                        Entry entry=ilineDataSet.getEntryForIndex(j);
                        Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.fade_red);
                        entry.setIcon(drawable);
                    }

                }
                newdataSets.add(ilineDataSet);
            }*/
            LineData data = new LineData(dataSets);
          //  set1.setColors(new int[] {Color.BLACK, Color.GREEN, Color.GRAY, Color.BLACK, Color.BLUE,Color.RED,Color.BLACK});
            // set data
            mChart.setData(data);
            mChart.notifyDataSetChanged();
            //List<ILineDataSet> dataSets1=mChart.getLineData().getDataSets();
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

        /*if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(), R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        }*/

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }
}
