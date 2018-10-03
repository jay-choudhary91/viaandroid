package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.graphicaldataadapter;
import com.cryptoserver.composer.utils.WaveFromView;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.noise;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class graphicalfragment extends basefragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraChangeListener{


    public graphicalfragment() {
        // Required empty public constructor
    }

    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    MediaPlayer player;
    @BindView(R.id.scrollview_graphical)
    ScrollView scrollview_graphical;
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

    WaveFromView myvisualizerview;

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
            myvisualizerview = (WaveFromView)rootview.findViewById(R.id.myvisualizerview);
            player = new MediaPlayer();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    start();
                }
            });
            layout_locationanalytics=rootview.findViewById(R.id.layout_locationAna);
            layout_orientation=rootview.findViewById(R.id.layout_orenAna);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    graphicallocationadapter=new graphicaldataadapter(common.locationAnalyticsdata(gethelper().getmetricarraylist()),getActivity());
                    graphicalphoneadapter=new graphicaldataadapter(common.phoneAnalytics(gethelper().getmetricarraylist()),getActivity());
                    graphicalorientationadapter=new graphicaldataadapter(common.orientationarraylist(gethelper().getmetricarraylist()),getActivity());

                    RecyclerView.LayoutManager mphoneALayoutmanager=new GridLayoutManager(getActivity(),3);
                    recyview_phoneanalytics.setLayoutManager(mphoneALayoutmanager);
                    recyview_phoneanalytics.setAdapter(graphicalphoneadapter);

                    RecyclerView.LayoutManager mlocationALayoutmanager=new GridLayoutManager(getActivity(),2);
                    recyview_locationanalytics.setLayoutManager(mlocationALayoutmanager);
                    recyview_locationanalytics.setAdapter(graphicallocationadapter);


                    RecyclerView.LayoutManager morientationLayoutmanager=new GridLayoutManager(getActivity(),1);
                    recyview_orientation.setLayoutManager(morientationLayoutmanager);
                    recyview_orientation.setAdapter(graphicalorientationadapter);
                }
            },5000);
            loadMap();
        }
        return rootview;
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

    @Override
    public void oncurrentlocationchanged(Location location) {
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
                        double amp = mNoise.getAmplitude();
                        amp = Math.abs(amp);
                        //text_sound.setText("" +(float) amp);

                        float ampvalue = common.getamplitudevalue((double)amp);

                        Log.e("amplitude value =", "" + ampvalue);
                        myvisualizerview.updateAmplitude( ampvalue, true);
                        //myvisualizerview.updateAmplitude(1f , true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                waveHandler.postDelayed(this, 800);
            }
        };
        waveHandler.post(waveRunnable);
    }

    private void start() {

        try {

            if(mNoise != null)
                mNoise.stop();

            mNoise = new noise();

            if(mNoise != null)
            {
                if(mNoise != null)
                    mNoise.start();
            }

            try {
                if(mNoise != null)
                    getaudiowave();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //visualizer.setEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        Log.e("noise", "==== Stop noise Monitoring===");
        try {
            if(mNoise != null)
            {
                mNoise.stop();
                myvisualizerview.updateAmplitude((float) 0,false);
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
}

