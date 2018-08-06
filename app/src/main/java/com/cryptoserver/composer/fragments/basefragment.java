package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import static android.content.Context.LOCATION_SERVICE;

public abstract class basefragment extends Fragment {

    private fragmentnavigationhelper fragmentHelper;
    private SharedPreferences prefs;
    private View view;
    private TelephonyManager telephonymanager;
    public static final int my_permission_read_phone_state = 90;
    private static final int permission_location_request_code = 91;
    int gps_request_code =111;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        telephonymanager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentHelper = (fragmentnavigationhelper) activity;
        } catch (Exception e) {
        }

       // prefs = activity.getSharedPreferences(config.prefs_name, Context.MODE_PRIVATE);
    }

    public void updateheader() {

    }

    public void oncurrentlocationchanged(Location location) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1)
        {
            Log.e("Permissions","Allow or Revoke");
        }
    }

    public static boolean iscamerapermissionenabled(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isaudiopermissionenabled(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getlayoutid(), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initviews(view, savedInstanceState);

    }

    public fragmentnavigationhelper gethelper() {
        return this.fragmentHelper;
    }

    public View getparentview() {
        return this.view;
    }

    public abstract int getlayoutid();


    public void initviews(View parent, Bundle savedInstanceState) {

    }

    public SharedPreferences getSharedPreferences() {
        return prefs;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hidekeyboard();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public void hidekeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hidekeyboard(EditText input) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }


    public void onHeaderBtnClick(int btnid) {

    }

    public static boolean checkLocationEnable(Context context) {
        //check the Gps settings
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public View findViewById(int id) {
        return getparentview().findViewById(id);
    }

    /**
     * An interface to load and make navigation. The parent mActivity must provide an implementation for this interface.
     *
     * @author khawarraza
     */
    public interface fragmentnavigationhelper {

        public void addFragment(basefragment f, boolean clearBackStack, boolean addToBackstack);

        public void addFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack);

        public void replaceFragment(basefragment f, boolean clearBackStack, boolean addToBackstack);

        public void replaceFragment(basefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack);

        public void onBack();

        public void launchHome();

        public void updateheader(String txt);

        public void updateActionBar(int showHide, String color);

        public void updateActionBar(int showHide);

        public void showPermissionDialog();


    }


    public void dismissprogress()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //progressdialog.dismisswaitdialog();
            }
        });
    }

    public void changefocusstyle(View view, int fullbordercolor, int fullbackcolor, float borderradius)
    {
       /* view.setBackgroundResource(R.drawable.style_rounded_view);
        GradientDrawable drawable = (GradientDrawable)view.getBackground();
        drawable.setStroke(2, fullbordercolor);
        drawable.setCornerRadius(borderradius);
        drawable.setColor(fullbackcolor);*/
    }


    @Override
    public void onPause() {
        super.onPause();
    }

}
