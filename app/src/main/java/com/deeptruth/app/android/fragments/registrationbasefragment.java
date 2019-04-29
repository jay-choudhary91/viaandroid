package com.deeptruth.app.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.config;

import java.util.HashMap;


public abstract class registrationbasefragment extends Fragment {

    private View view;
    private registrationfragmentnavigationhelper fragmenthelper;
    private SharedPreferences prefs;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmenthelper = (registrationfragmentnavigationhelper) activity;
        } catch (Exception e) {
        }

        prefs = activity.getSharedPreferences(config.prefs_name, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(getlayoutid(), container, false);
        view.setClickable(true);
        return view;
    }

    public interface registrationfragmentnavigationhelper {

        public void addFragment(registrationbasefragment f, boolean clearBackStack, boolean addToBackstack);
        public void addFragment(registrationbasefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack);
        public void replaceFragment(registrationbasefragment f, boolean clearBackStack, boolean addToBackstack);
        public void replaceFragment(registrationbasefragment f, int layoutId, boolean clearBackStack, boolean addToBackstack);
        public void onBack();
        public void setdatacomposing(boolean isdatacomposing);
        public void setdatacomposing(boolean isdatacomposing,String mediafilepath);
        public void updateactionbar(int showHide, int color);
        public void updateactionbar(int showHide);
        public boolean isuserlogin();
        public void redirecttologin();
        public void xapipost_send(Context mContext, HashMap<String, String> mPairList, apiresponselistener mListener);
        public void hidekeyboard();
        public void onUserLeaveHint();

      }



    public abstract int getlayoutid();

    public registrationfragmentnavigationhelper getHelper() {
        return this.fragmenthelper;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

}
