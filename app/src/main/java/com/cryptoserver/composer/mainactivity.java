package com.cryptoserver.composer;

import android.os.Bundle;

import com.cryptoserver.composer.activity.baseactivity;
import com.cryptoserver.composer.fragments.fragmentvideolist;

import butterknife.ButterKnife;

public class mainactivity extends baseactivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        applicationviavideocomposer.setActivity(mainactivity.this);

        fragmentvideolist frag=new fragmentvideolist();
        replaceFragment(frag, false, true);

    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_mainactivity;
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
