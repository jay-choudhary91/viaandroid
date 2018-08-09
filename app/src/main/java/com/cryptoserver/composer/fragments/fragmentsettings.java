package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cryptoserver.composer.utils.xdata;

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
    EditText edt_framescount;
    String keytype ="md5";
    int framecount=15;
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

            edt_framescount =(EditText) rootview.findViewById(R.id.edt_framescount);
            edt_md_salt =(EditText) rootview.findViewById(R.id.edt_md_salt);
            edt_sha_salt =(EditText) rootview.findViewById(R.id.edt_sha_salt);


            layout_md.setOnClickListener(this);
            layout_md_salt.setOnClickListener(this);
            layout_sha.setOnClickListener(this);
            layout_sha_salt.setOnClickListener(this);
            edt_md_salt.addTextChangedListener(new MyTextWatcher(edt_md_salt));
            edt_sha_salt.addTextChangedListener(new MyTextWatcher(edt_sha_salt));

            edt_framescount.setText(""+framecount);
            if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
                edt_framescount.setText(xdata.getinstance().getSetting(config.framecount));

            edt_framescount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    xdata.getinstance().saveSetting(config.framecount,editable.toString());
                }
            });
            if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                    xdata.getinstance().getSetting(config.hashtype).trim().isEmpty())
            {
                showselectedunselected(img_md,img_md_salt,img_sha,img_sha_salt,config.prefs_md5);
            }
            else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt))
            {
                showselectedunselected(img_md_salt,img_md,img_sha,img_sha_salt,config.prefs_md5_salt);
                edt_md_salt.setText(xdata.getinstance().getSetting(config.prefs_md5_salt));

            }
            else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha))
            {
                showselectedunselected(img_sha,img_md,img_md_salt,img_sha_salt,config.prefs_sha);
            }
            else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt))
            {
                showselectedunselected(img_sha_salt,img_md,img_md_salt,img_sha,config.prefs_sha_salt);
                edt_sha_salt.setText(xdata.getinstance().getSetting(config.prefs_sha_salt));
            }
            //fragment_matrictracklist fragment_matrictracklist=new fragment_matrictracklist();
            //gethelper().replaceFragment(fragment_matrictracklist,R.id.matrictracklist, false, false);
        }
        return rootview;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edt_md_salt:
                    xdata.getinstance().saveSetting(config.prefs_md5_salt,editable.toString());
                    break;
                case R.id.edt_sha_salt:
                    xdata.getinstance().saveSetting(config.prefs_sha_salt,editable.toString());
                    break;
            }
        }
    }


    @Override
    public int getlayoutid() {
        return R.layout.fragment_fragmentsettings;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_md:
                showselectedunselected(img_md,img_md_salt,img_sha,img_sha_salt,config.prefs_md5);
                break;

            case R.id.layout_md_salt:
                showselectedunselected(img_md_salt,img_md,img_sha,img_sha_salt,config.prefs_md5_salt);
                break;

            case R.id.layout_sha:
                showselectedunselected(img_sha,img_md,img_md_salt,img_sha_salt,config.prefs_sha);
                break;

            case R.id.layout_sha_salt:
                showselectedunselected(img_sha_salt,img_md,img_md_salt,img_sha,config.prefs_sha_salt);
                break;
        }
    }

    public void showselectedunselected(ImageView img1, ImageView img2, ImageView img3, ImageView img4,String keyname)
    {
        img1.setImageResource(R.drawable.selectedicon);
        img2.setImageResource(R.drawable.unselectedicon);
        img3.setImageResource(R.drawable.unselectedicon);
        img4.setImageResource(R.drawable.unselectedicon);

        keytype=keyname;
        xdata.getinstance().saveSetting(config.hashtype,keyname);
    }
}
