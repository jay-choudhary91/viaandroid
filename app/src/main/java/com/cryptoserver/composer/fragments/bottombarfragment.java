package com.cryptoserver.composer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;

import butterknife.ButterKnife;

/**
 * Created by root on 6/11/18.
 */

public class bottombarfragment extends basefragment  {

    View rootview = null;
    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecapturefragment fragimgcapture=null;
    adapteritemclick madapterclick;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_video:

                    launchvideocomposer();
                    return true;
                case R.id.navigation_audio:
                    if(fragaudiocomposer == null)
                        fragaudiocomposer=new audiocomposerfragment();

                    fragaudiocomposer.setData(mclick);
                    gethelper().replacetabfragment(fragaudiocomposer,false,true);
                    return true;

                case R.id.navigation_image:

                    if(fragimgcapture == null)
                        fragimgcapture=new imagecapturefragment();

                    fragimgcapture.setData(mclick);
                    gethelper().replacetabfragment(fragimgcapture,false,true);
                    return true;
            }
            return false;
        }
    };

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
            BottomNavigationView navigation = (BottomNavigationView) rootview.findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            launchvideocomposer();
        }
        return rootview;
    }

    public void launchvideocomposer()
    {
        if(fragvideocomposer == null)
            fragvideocomposer=new videocomposerfragment();

        fragvideocomposer.setData(false, mclick);
        gethelper().replacetabfragment(fragvideocomposer,false,true);
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
