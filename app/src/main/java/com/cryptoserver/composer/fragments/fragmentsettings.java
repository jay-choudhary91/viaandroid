package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptoserver.composer.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentsettings extends basefragment {

    View rootview = null;
    public fragmentsettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

        }
        return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_fragmentsettings;
    }

}
