package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
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
import com.cryptoserver.composer.utils.VisualizerView;
import com.cryptoserver.composer.utils.VisualizerViewMidea;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class graphicalfragment extends basefragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraChangeListener, OnChartValueSelectedListener, OnChartGestureListener,SensorEventListener {


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
    @BindView(R.id.txt_data_hash)
    TextView txt_data_hash;
    @BindView(R.id.txt_hash_formula)
    TextView txt_hash_formula;

    @BindView(R.id.layout_graphical)
    LinearLayout layout_graphical;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.recyview_encryption)
    RecyclerView recyview_encryption;
    MediaPlayer player;
    @BindView(R.id.scrollview_graphical)
    ScrollView scrollview_graphical;
    @BindView(R.id.linechart)
    LineChart mChart;
    View rootview;
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager msensormanager;
    private Sensor maccelerometersensormanager;

    encryptiondataadapter encryptionadapter;
    LinearLayout layout_orientation;
    RecyclerView.LayoutManager encryptionmanager;
    ImageView img_compass;
    GoogleMap mGoogleMap;
    Location CurrentLocationChange = null;
    LatLng lastlocationchange = null;
    private ArrayList<graphicalmodel> frameslist = new ArrayList<>();

    VisualizerView myvisualizerview;
    VisualizerViewMidea myvisualizerviewmedia;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    Uri videoUri = null;

    private Handler waveHandler;
    private Runnable waveRunnable;
    noise mNoise;
    boolean isgraphicopen=false;
    private Handler myhandler;
    private Runnable myrunnable;
    public String currenthashvalue="";
    boolean ismediaplayer = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            scrollview_graphical.setVisibility(View.VISIBLE);
            myvisualizerview = (VisualizerView)rootview.findViewById(R.id.myvisualizerview);
            myvisualizerviewmedia = (VisualizerViewMidea) rootview.findViewById(R.id.myvisualizerviewmedia);

            layout_orientation=rootview.findViewById(R.id.layout_orenAna);
            img_compass = (ImageView) rootview.findViewById(R.id.img_compass);
            recyview_encryption.setNestedScrollingEnabled(false);
            encryptionadapter=new encryptiondataadapter(frameslist,applicationviavideocomposer.getactivity());
            encryptionmanager=new LinearLayoutManager(applicationviavideocomposer.getactivity());
            msensormanager = (SensorManager) applicationviavideocomposer.getactivity().getSystemService(Context.SENSOR_SERVICE);
            recyview_encryption.setLayoutManager(encryptionmanager);
            recyview_encryption.setAdapter(encryptionadapter);

            layout_graphical.setVisibility(View.GONE);
            if(xdata.getinstance().getSetting(config.hashtype).toString().trim().length() == 0)
                xdata.getinstance().saveSetting(config.hashtype,config.prefs_md5);

            //loadMap();
            setvisualizer();
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

    public void setmetricesdata()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout_graphical.setVisibility(View.VISIBLE);
                        String latitude=xdata.getinstance().getSetting(config.Latitude);
                        String longitude=xdata.getinstance().getSetting(config.Longitude);
                        if((! latitude.trim().isEmpty()) && (! longitude.trim().isEmpty()))
                            populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));

                        if(ismediaplayer)
                        {
                            if(xdata.getinstance().getSetting(config.orientation).toString().trim().length() > 0)
                            {
                                String strdegree=xdata.getinstance().getSetting(config.orientation);
                                int degree = Math.abs((int)Double.parseDouble(strdegree));
                                rotatecompass(degree);
                            }
                        }
                    }
                });

                common.locationAnalyticsdata(txt_latitude,txt_longitude,
                        txt_altitude,txt_heading,txt_orientation,txt_speed,txt_address);
                common.phoneAnalytics(txt_phonetype,txt_cellprovider,txt_connection_speed,txt_osversion,txt_wifinetwork,
                        txt_gps_accuracy,txt_screensize,txt_country,txt_cpuusage,txt_brightness,txt_timezone,txt_memoryusage,txt_bluetooth,
                        txt_localtime,txt_storageavailable,txt_language,txt_systemuptime,txt_battery);

                if(! common.isnetworkconnected(applicationviavideocomposer.getactivity()))
                    xdata.getinstance().saveSetting(config.Connectionspeed,"N/A");

                txt_connection_speed.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_connection_speed.setText(config.Connectionspeed+"\n"+xdata.getinstance().getSetting(config.Connectionspeed));
                    }
                });
                txt_data_hash.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_data_hash.setText(currenthashvalue);
                    }
                });
                txt_hash_formula.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_hash_formula.setText(xdata.getinstance().getSetting(config.hashtype));
                    }
                });
                txt_xaxis.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_xaxis.setText("X-Axis \n"+xdata.getinstance().getSetting(config.acceleration_x));
                    }
                });
                txt_yaxis.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_yaxis.setText("Y-Axis \n"+xdata.getinstance().getSetting(config.acceleration_y));
                    }
                });
                txt_zaxis.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_zaxis.setText("Z-Axis \n"+xdata.getinstance().getSetting(config.acceleration_z));
                    }
                });
            }
        }).start();

    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_graphicalfragment;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //setMap(googleMap);
       // loadMap();
    }

    private void loadMap() {
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.googlemap, mapFragment).commit();

        if (mGoogleMap == null) {
            mapFragment.getMapAsync(this);
        } else {
            setMap(mGoogleMap);
        }
    }

    private void setMap(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnInfoWindowClickListener(this);
        this.mGoogleMap.setOnMarkerClickListener(this);
        this.mGoogleMap.setOnMapClickListener(this);
        this.mGoogleMap.setOnCameraMoveListener(this);
        this.mGoogleMap.setOnCameraMoveStartedListener(this);
        mGoogleMap.setOnCameraChangeListener(this);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        this.mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setOnCameraIdleListener(this);
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
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(26.8497, 75.7692), 5));
        }
    }

    private void populateUserCurrentLocation(LatLng location) {
        // DeviceUser user = DeviceUserManager.getInstance().getUser();
        if (mGoogleMap == null)
            return;

        googlemap.setVisibility(View.VISIBLE);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 15));
    }

    public void locationupdate(Location location) {
        super.oncurrentlocationchanged(location);
        if (location == null) {
            return;
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

        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);

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
            double deltaX = Math.abs(event.values[0]);
            double deltaY = Math.abs(event.values[1]);
            double deltaZ = Math.abs(event.values[2]);


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

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {

                        myvisualizerviewmedia.updateVisualizer(bytes);

                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    public void setmediaplayerdata(boolean ismediaplayer , MediaPlayer mediaPlayer){

        this.mMediaPlayer = mediaPlayer;
        this.ismediaplayer = ismediaplayer;
        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
            setvisualizer();
        }

    }

    public void setmediaplayerdata(boolean ismideaplayer){
        this.ismediaplayer = ismideaplayer;
    }


    public void setvisualizer(){
        if(ismediaplayer){
            initAudio();
        }else{
            start();
        }
    }

}


