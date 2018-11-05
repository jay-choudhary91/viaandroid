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
public class fragmentimagelist extends basefragment {

   View rootview;
    public fragmentimagelist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview==null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
        }return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_fragmentimagelist;
    }
    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid) {
            case R.id.img_upload_icon:
                //  checkwritestoragepermission();
                break;
            case R.id.img_setting:
                fragmentsettings fragmatriclist = new fragmentsettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
            case R.id.img_add_icon:
                //   launchvideocomposer(false);
                break;
        }
    }
}
