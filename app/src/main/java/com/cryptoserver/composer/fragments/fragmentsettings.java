package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cryptoserver.composer.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentsettings extends basefragment {

    View rootview = null;
    FrameLayout framelayout;
    public fragmentsettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            framelayout=rootview.findViewById(R.id.matrictracklist);
            fragment_matrictracklist fragment_matrictracklist=new fragment_matrictracklist();
            gethelper().replaceFragment(fragment_matrictracklist,R.id.matrictracklist, false, true);
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_fragmentsettings;
    }

}
