package com.cryptoserver.composer.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 6/11/18.
 */

public class bottombarfragment extends basefragment  {

    @BindView(R.id.tab_container)
    FrameLayout tab_container;

    View rootview = null;
    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecomposerfragment fragimgcapture=null;
    adapteritemclick madapterclick;
    int selectedtab=0,lastselectedtab=0;
    BottomNavigationView navigation;
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

    public void loadfragments()
    {
        switch (selectedtab)
        {
            case 1:
                if(fragvideocomposer == null)
                    fragvideocomposer=new videocomposerfragment();

                fragvideocomposer.setData(false, mclick);
                gethelper().replacetabfragment(fragvideocomposer,false,true);
                isviewloaded=false;
                startloadtimer(1500);
                break;
            case 2:
                if(fragaudiocomposer == null)
                    fragaudiocomposer=new audiocomposerfragment();

                fragaudiocomposer.setData(mclick);
                gethelper().replacetabfragment(fragaudiocomposer,false,true);
                isviewloaded=false;
                startloadtimer(1000);
                break;
            case 3:
                if(fragimgcapture == null)
                    fragimgcapture=new imagecomposerfragment();

                fragimgcapture.setData(mclick);
                gethelper().replacetabfragment(fragimgcapture,false,true);
                isviewloaded=false;
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
                        navigation.setSelectedItemId(R.id.navigation_video);
                    }
                    else if(lastselectedtab == 2)
                    {
                        navigation.setSelectedItemId(R.id.navigation_audio);
                    }
                    else if(lastselectedtab == 3)
                    {
                        navigation.setSelectedItemId(R.id.navigation_image);
                    }
                }
            }
        },loadingtime);
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_bottombarfragment;
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
            navigation = (BottomNavigationView) rootview.findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            tab_container.setBackgroundColor(Color.BLACK);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            navigation.setSelectedItemId(R.id.navigation_video);
                        }
                    });
                }
            }).start();
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
