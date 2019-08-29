package com.deeptruth.app.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.fragments.fragmentsignin;

import butterknife.ButterKnife;

public class registrationcontaineractivity extends registrationbaseactivity{


    @Override
    public int getlayoutid() {
        return R.layout.registrationcontainer_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        launchcomposerfragment();
    }

    public void launchcomposerfragment()
    {



        /*fragmentsignin fragbottombar=new fragmentsignin();
        replaceFragment(fragbottombar, false, true);*/
    }
}


