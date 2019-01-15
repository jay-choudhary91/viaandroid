package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;
import com.cryptoserver.composer.views.customfonttextview;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragmentgraphicaldrawer extends basefragment implements OnChartValueSelectedListener, OnChartGestureListener,SensorEventListener {

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
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_compass)
    ImageView img_compass;

    View rootview;
    GoogleMap mgooglemap;
    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;

    private boolean isinbackground=false,ismediaplayer = false,isgraphicopen=false,photocapture=false;
    String latitude = "", longitude = "",screenheight = "",screenwidth = "",lastsavedangle="";
    private float currentDegree = 0f;
    String hashmethod= "",videostarttransactionid = "",valuehash = "",metahash = "";

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


            loadmap();
        }
        return rootview;
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

        common.setdrawabledata(getResources().getString(R.string.blockchain_id),videostarttransactionid, tvblockchainid);
        common.setdrawabledata(getResources().getString(R.string.block_id),hashmethod, tvblockid);
        common.setdrawabledata(getResources().getString(R.string.block_number), valuehash, tvblocknumber);
        common.setdrawabledata(getResources().getString(R.string.metrichash),metahash, tvmetahash);

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

    public void setphotocapture(boolean photocapture)
    {
        this.photocapture=photocapture;
    }

    public void getencryptiondata(String hashmethod, String videostarttransactionid, String valuehash, String metahash){

        this.hashmethod = hashmethod;
        this.videostarttransactionid = videostarttransactionid;
        this.valuehash = valuehash;
        this.metahash = metahash;
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
            if(!ismediaplayer)
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
}
