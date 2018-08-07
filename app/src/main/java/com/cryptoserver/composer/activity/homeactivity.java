package com.cryptoserver.composer.activity;

import android.os.Bundle;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.fragmentvideolist;

import butterknife.ButterKnife;

public class homeactivity extends baseactivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        applicationviavideocomposer.setActivity(homeactivity.this);

        fragmentvideolist frag=new fragmentvideolist();
        replaceFragment(frag, false, true);

    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_homeactivity;
    }

    @Override
    public void launchHome() {

    }

    @Override
    public void updateheader(String txt) {

    }

    @Override
    public void updateActionBar(int showHide, String color) {

    }

    @Override
    public void updateActionBar(int showHide) {

    }

    @Override
    public void showPermissionDialog() {

    }
}
