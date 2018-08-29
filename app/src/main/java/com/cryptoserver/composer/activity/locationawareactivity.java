package com.cryptoserver.composer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.googleutils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public abstract class locationawareactivity extends baseactivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_update_key";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_updated_time_key";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 99;

    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private GoogleApiClient mGoogleApiClient;
    int GPS_REQUEST_CODE=111;
    public Bundle NewSavedInstanceState=null;
    boolean updateItem=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewSavedInstanceState=savedInstanceState;
        /*if (!checkPermission(this))
        {
            locationServiceAlert(locationawareactivity.this);
        } else {
            enableGPS(locationawareactivity.this);
        }*/
    }

    @Override
    public void showPermissionDialog() {
        updateItem=true;
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
        else
        {
            enableGPS(locationawareactivity.this);
        }
    }

    public void initLocationAPIs(Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        updateValuesFromBundle(savedInstanceState);

        mGoogleApiClient.connect();
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }
//        mRequestingLocationUpdates = true;
    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
//        mRequestingLocationUpdates = false;
    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(
//                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
//        if (mRequestingLocationUpdates) {
        startLocationUpdates();
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //saving the user current location
        if(location != null)
        {
            googleutils.saveUserCurrentLocation(location);

            if(location.getLatitude() == 0.0)
                return;

            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            if (getcurrentfragment() != null) {
                getcurrentfragment().oncurrentlocationchanged(location);
            }
            stopLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                enableGPS(locationawareactivity.this);

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                enableGPS(locationawareactivity.this);
            }
            return;
        }

    }

    @SuppressLint("MissingPermission")
    public static boolean checkLocationEnable(Context context) {
        //check the Gps settings
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        /*GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            int satellites1 = gpsStatus.getMaxSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            String lSatellites = null;
            int i = 0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                lSatellites = "Satellite" + (i++) + ": "
                        + satellite.getPrn() + ","
                        + satellite.usedInFix() + ","
                        + satellite.getSnr() + ","
                        + satellite.getAzimuth() + ","
                        + satellite.getElevation()+ "\n\n";

                Log.d("SATELLITE",lSatellites);
            }
        }
*/
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public void onGpsStatusChanged(int event) {

    }

    public void setNavigateWithLocation()
    {
        if (locationawareactivity.checkLocationEnable(locationawareactivity.this))
        {
            initLocationAPIs(NewSavedInstanceState);
            if (getcurrentfragment() != null) {
                getcurrentfragment().getAccurateLocation();
            }
        }

    }

    public void enableGPS(final Context context)
    {
        if (!locationawareactivity.checkLocationEnable(context))
        {
            showgpsalert(context);
        }
        else
        {
            setNavigateWithLocation();
        }
    }

    public void showgpsalert(final Context context)
    {
/*        appdialog.showConfirmationDialog(context, "GPS", "GPS is disabled in your device. Would you like to enable it?","YES","NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        setNavigateWithLocation();
                    }
                }).show();*/

        new AlertDialog.Builder(context, R.style.customdialogtheme)
                .setTitle("GPS")
                .setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                        if(dialog != null)
                            dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            setNavigateWithLocation();
        }
    }
}
