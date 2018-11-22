package com.cryptoserver.composer.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class bottombarrederfrag extends basefragment {

    @BindView(R.id.tab_container)
    FrameLayout tab_reader_container;
    View rootview = null;
    adapteritemclick madapterclick;
    videoreaderfragment fragvideotabreader=null;
    audioreaderfragment fragaudiotabreader=null;
    imagereaderfragment fragphototabreader=null;
    int selectedtab=0,lastselectedtab=0;
    BottomNavigationView readernavigation;
    public boolean isviewloaded=true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_video:

                    lastselectedtab=1;
                    if(selectedtab != 1 && isviewloaded)
                    {
                        selectedtab=1;
                        loadfragments();
                        return true;
                    }
                    break;
                case R.id.navigation_audio:
                    lastselectedtab=2;
                    if(selectedtab != 2 && isviewloaded)
                    {
                        selectedtab=2;
                        loadfragments();
                        return true;
                    }
                    break;
                case R.id.navigation_image:
                    lastselectedtab=3;
                    if(selectedtab != 3 && isviewloaded)
                    {
                        selectedtab=3;
                        loadfragments();
                        return true;
                    }
                    break;
            }
            return false;
        }
    };

    public void loadfragments() {
        switch (selectedtab) {
            case 1:
                if (fragvideotabreader == null)
                    fragvideotabreader = new videoreaderfragment();

                // fragvideocomposer.setData(false, mclick);
                gethelper().replacetabfragment(fragvideotabreader, false, true);
                isviewloaded = false;
                startloadtimer(1500);
                break;
            case 2:
                if (fragaudiotabreader == null)
                    fragaudiotabreader = new audioreaderfragment();

                //    fragaudiocomposer.setData(mclick);
                gethelper().replacetabfragment(fragaudiotabreader, false, true);
                isviewloaded = false;
                startloadtimer(1000);
                break;
            case 3:
                if (fragphototabreader == null)
                    fragphototabreader = new imagereaderfragment();

                // fragimgcapture.setData(mclick);
                gethelper().replacetabfragment(fragphototabreader, false, true);
                isviewloaded = false;
                startloadtimer(1500);
                break;
        }
    }

    public void startloadtimer(long loadingtime)
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isviewloaded=true;
                if(lastselectedtab != selectedtab)
                {
                    if(lastselectedtab == 1)
                    {
                        readernavigation.setSelectedItemId(R.id.navigation_video);
                    }
                    else if(lastselectedtab == 2)
                    {
                        readernavigation.setSelectedItemId(R.id.navigation_audio);
                    }
                    else if(lastselectedtab == 3)
                    {
                        readernavigation.setSelectedItemId(R.id.navigation_image);
                    }
                }
            }
        },loadingtime);
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_bottombarrederfrag;
    }
    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview==null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            readernavigation = (BottomNavigationView) rootview.findViewById(R.id.readernavigation);
            readernavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            tab_reader_container.setBackgroundColor(Color.WHITE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readernavigation.setSelectedItemId(R.id.navigation_video);
                }
            },100);

            //launchvideocomposer();
        }
        return rootview;
    }

    adapteritemclick mclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int type) {
            if(type == 1)
            {
                madapterclick.onItemClicked(null);
            }
        }
    };

    public void setData(adapteritemclick madapterclick) {
        this.madapterclick = madapterclick;
    }
}


