package com.cryptoserver.composer.fragments;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.utils.common;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class bottombarfragment extends basefragment {

    private static final int request_permissions = 1;

    public bottombarfragment() {
        // Required empty public constructor
    }

    private TextView mTextMessage;
    View rootview = null;
    @Override
    public int getlayoutid() {
        return R.layout.fragment_bottombarfragment;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this, parent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            fragmentvideolist fragvideolist = null;
            if (fragvideolist == null) {
                fragvideolist = new fragmentvideolist();
                gethelper().replaceFragment(fragvideolist, false, true);
                }
        }
        return rootview;
    }
}
