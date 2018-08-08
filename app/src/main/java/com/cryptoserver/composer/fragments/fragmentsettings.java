package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentsettings extends basefragment implements View.OnClickListener{

    View rootview = null;
    FrameLayout framelayout;
    public fragmentsettings() {
        // Required empty public constructor
    }

    RelativeLayout layout_md;
    RelativeLayout layout_md_salt;
    RelativeLayout layout_sha;
    RelativeLayout layout_sha_salt;

    ImageView img_md;
    ImageView img_md_salt;
    ImageView img_sha;
    ImageView img_sha_salt;

    EditText edt_md_salt;
    EditText edt_sha_salt;
    String keytype ="md5";
    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);

            ButterKnife.bind(this, rootview);
           // framelayout=rootview.findViewById(R.id.matrictracklist);

            layout_md =(RelativeLayout)rootview.findViewById(R.id.layout_md);
            layout_md_salt =(RelativeLayout)rootview.findViewById(R.id.layout_md_salt);
            layout_sha =(RelativeLayout)rootview.findViewById(R.id.layout_sha);
            layout_sha_salt =(RelativeLayout)rootview.findViewById(R.id.layout_sha_salt);

            img_md =(ImageView)rootview.findViewById(R.id.img_md);
            img_md_salt= (ImageView)rootview.findViewById(R.id.img_md_salt);
            img_sha =(ImageView)rootview.findViewById(R.id.img_sha);
            img_sha_salt =(ImageView)rootview.findViewById(R.id.img_sha_salt);


            layout_md.setOnClickListener(this);
            layout_md_salt.setOnClickListener(this);
            layout_sha.setOnClickListener(this);
            layout_sha_salt.setOnClickListener(this);
            fragment_matrictracklist fragment_matrictracklist=new fragment_matrictracklist();
            gethelper().replaceFragment(fragment_matrictracklist,R.id.matrictracklist, false, false);
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_fragmentsettings;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_md:
                keytype = config.prefs_md5;
                common.showselectedunselected(img_md,img_md_salt,img_sha,img_sha_salt);
                break;

            case R.id.layout_md_salt:
                keytype = config.prefs_md5_salt;
                common.showselectedunselected(img_md_salt,img_md,img_sha,img_sha_salt);
                break;

            case R.id.layout_sha:
                keytype = config.prefs_sha;
                common.showselectedunselected(img_sha,img_md,img_md_salt,img_sha_salt);
                break;

            case R.id.layout_sha_salt:
                keytype = config.prefs_sha_salt;
                common.showselectedunselected(img_sha_salt,img_md,img_md_salt,img_sha);
                break;
        }
    }
}
