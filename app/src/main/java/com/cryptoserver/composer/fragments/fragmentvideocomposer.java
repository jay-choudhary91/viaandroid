package com.cryptoserver.composer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SlidingDrawer;

import com.cryptoserver.composer.R;

import butterknife.ButterKnife;

/**
 * Created by devesh on 6/8/18.
 */

public class fragmentvideocomposer extends basefragment {


    View rootview = null;
    @Override
    public int getlayoutid() {
        return R.layout.fragment_videocomposer;
    }


    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this,rootview);
            final ImageView handleimageview = rootview.findViewById(R.id.handle);

           /* SlidingDrawer simpleSlidingDrawer =(SlidingDrawer) rootview.findViewById(R.id.simpleSlidingDrawer); // initiate the SlidingDrawer

            simpleSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
                @Override
                public void onDrawerOpened() {
                    handleimageview.setImageResource(R.drawable.righthandle);
// add code here for the Drawer Opened Event
                }
            });


            simpleSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
                @Override
                public void onDrawerClosed() {
                    // change the handle button text
                    handleimageview.setImageResource(R.drawable.lefthandle);
                }
            });
*/
        }

        return rootview;


    }


}
