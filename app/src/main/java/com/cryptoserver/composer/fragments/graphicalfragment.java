package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.graphicaldataadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.utils.VisualizerView;
import com.cryptoserver.composer.utils.WaveFromView;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.noise;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class graphicalfragment extends basefragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraChangeListener, OnChartValueSelectedListener, OnChartGestureListener {


    public graphicalfragment() {
        // Required empty public constructor
    }

    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    MediaPlayer player;
    @BindView(R.id.scrollview_graphical)
    ScrollView scrollview_graphical;
    @BindView(R.id.linechart)
    LineChart mChart;
    View rootview;

    //graphical
    RecyclerView recyview_locationanalytics;
    RecyclerView recyview_orientation;
    RecyclerView recyview_phoneanalytics;
    graphicaldataadapter graphicallocationadapter,graphicalphoneadapter,graphicalorientationadapter;
    LinearLayout layout_locationanalytics,layout_orientation;

    //google map
    GoogleMap mGoogleMap;
    Location currentLocation = null;
    Location CurrentLocationChange = null;
    LatLng lastlocationchange = null;

    //WaveFromView myvisualizerview;

    VisualizerView myvisualizerview;

    private Handler waveHandler;
    private Runnable waveRunnable;
    noise mNoise;

    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            scrollview_graphical.setVisibility(View.VISIBLE);
            recyview_locationanalytics=rootview.findViewById(R.id.recyview_location_analytics);
            recyview_orientation=rootview.findViewById(R.id.recyview_orentation);
            recyview_phoneanalytics=rootview.findViewById(R.id.recyview_phoneanalytics);
            recyview_locationanalytics.setNestedScrollingEnabled(false);
            recyview_orientation.setNestedScrollingEnabled(false);
            recyview_phoneanalytics.setNestedScrollingEnabled(false);
            myvisualizerview = (VisualizerView)rootview.findViewById(R.id.myvisualizerview);
            layout_locationanalytics=rootview.findViewById(R.id.layout_locationAna);
            layout_orientation=rootview.findViewById(R.id.layout_orenAna);

            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(gethelper().getmetricarraylist().size() > 0)
                                    loadarraydata();

                                loadMap();
                                player = new MediaPlayer();
                                start();
                                setchartdata();

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            thread.start();


            /*if(gethelper().getmetricarraylist().size() > 0)
            {
                loadarraydata();
            }
            else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadarraydata();
                    }
                },5000);
            }*/
        }
        return rootview;
    }

    public void loadarraydata()
    {
        graphicallocationadapter=new graphicaldataadapter(common.locationAnalyticsdata(gethelper().getmetricarraylist()),getActivity());
        graphicalphoneadapter=new graphicaldataadapter(common.phoneAnalytics(gethelper().getmetricarraylist()),getActivity());
        graphicalorientationadapter=new graphicaldataadapter(common.orientationarraylist(gethelper().getmetricarraylist()),getActivity());

        RecyclerView.LayoutManager mphoneALayoutmanager=new GridLayoutManager(getActivity(),3);
        recyview_phoneanalytics.setLayoutManager(mphoneALayoutmanager);
        recyview_phoneanalytics.setAdapter(graphicalphoneadapter);

        GridLayoutManager mlocationALayoutmanager=new GridLayoutManager(getActivity(),2);
        mlocationALayoutmanager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 5 is the sum of items in one repeated section
                switch (position % 5) {
                    // first 3 items span 2 columns each
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        return 1;
                    // next 2 items span 1 columns each
                    case 4:
                        return 2;
                }
                throw new IllegalStateException("internal error");
            }
        });
        recyview_locationanalytics.setLayoutManager(mlocationALayoutmanager);
        recyview_locationanalytics.setAdapter(graphicallocationadapter);


        RecyclerView.LayoutManager morientationLayoutmanager=new GridLayoutManager(getActivity(),1);
        recyview_orientation.setLayoutManager(morientationLayoutmanager);
        recyview_orientation.setAdapter(graphicalorientationadapter);
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
        setMap(googleMap);
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void populateUserCurrentLocation(Location location) {
        // DeviceUser user = DeviceUserManager.getInstance().getUser();
        if (mGoogleMap == null)
            return;

        googlemap.setVisibility(View.VISIBLE);
        if (location.getLatitude() == config.STATIC_LAT) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5));

        } else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

    }

    public void locationupdate(Location location) {
        super.oncurrentlocationchanged(location);
        if (location == null) {
            return;
        }
        CurrentLocationChange = location;
        LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        LatLng evallatlng = new LatLng(Double.parseDouble(df.format(location.getLatitude())), Double.parseDouble(df.format(location.getLongitude())));
        if (lastlocationchange != null && lastlocationchange.latitude == evallatlng.latitude && lastlocationchange.longitude == evallatlng.longitude) {
            return;  // the locaiton hasnt change significatntly  do update anything on the screen and do not use google maps clicks
        }
        lastlocationchange = evallatlng;
        populateUserCurrentLocation(location);

    }

    public void getaudiowave() {
        waveHandler=new Handler();
        waveRunnable = new Runnable() {
            @Override
            public void run() {

                    try {
                        int x = mNoise.getAmplitudevoice();
                        myvisualizerview.addAmplitude(x); // update the VisualizeView
                        myvisualizerview.invalidate();
                        //myvisualizerview.updateAmplitude(1f , true);

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

            if (mNoise != null) {
                if (mNoise != null)
                    mNoise.start();
            }

            try {
                if (mNoise != null)
                    getaudiowave();

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

        stop();
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

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 0f, 0f);
            set1.enableDashedHighlightLine(10f, 0f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
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
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
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
}

