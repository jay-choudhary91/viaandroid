package com.deeptruth.app.android.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.Itemmetricadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.minmaxfilter;
import com.deeptruth.app.android.utils.xdata;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class framemetricssettings extends basefragment implements View.OnClickListener {

    @BindView(R.id.recyview_itemmetric)
    RecyclerView recyviewItem;
    LinearLayoutManager mLayoutManager;
    View rootview = null;
    public framemetricssettings() {
        // Required empty public constructor
    }
    private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    Itemmetricadapter itemMetricAdapter;

    LinearLayout layout_view;
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
    EditText edt_update_every;
    String keytype =config.prefs_md5;
    int framecount=15;
    int updateevery=5;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            layout_md =(RelativeLayout)rootview.findViewById(R.id.layout_md);
            layout_md_salt =(RelativeLayout)rootview.findViewById(R.id.layout_md_salt);
            layout_sha =(RelativeLayout)rootview.findViewById(R.id.layout_sha);
            layout_sha_salt =(RelativeLayout)rootview.findViewById(R.id.layout_sha_salt);

            img_md =(ImageView)rootview.findViewById(R.id.img_md);
            img_md_salt= (ImageView)rootview.findViewById(R.id.img_md_salt);
            img_sha =(ImageView)rootview.findViewById(R.id.img_sha);
            img_sha_salt =(ImageView)rootview.findViewById(R.id.img_sha_salt);

            edt_update_every =(EditText) rootview.findViewById(R.id.edt_update_every);
            edt_framescount =(EditText) rootview.findViewById(R.id.edt_framescount);
            edt_md_salt =(EditText) rootview.findViewById(R.id.edt_md_salt);
            edt_sha_salt =(EditText) rootview.findViewById(R.id.edt_sha_salt);

            layout_view=rootview.findViewById(R.id.layout_view);


            layout_md.setOnClickListener(this);
            layout_md_salt.setOnClickListener(this);
            layout_sha.setOnClickListener(this);
            layout_sha_salt.setOnClickListener(this);
            layout_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    clearfocus();
                    return false;
                }

            });

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

                }
            });

            edt_framescount.setFilters(new InputFilter[]{ new minmaxfilter("1", "1000")});

            edt_update_every.setText(""+updateevery);
            if(! xdata.getinstance().getSetting(config.frameupdateevery).trim().isEmpty())
                edt_update_every.setText(xdata.getinstance().getSetting(config.frameupdateevery));

            edt_update_every.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            edt_update_every.setFilters(new InputFilter[]{ new minmaxfilter("1", "1000")});
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    metricItemArraylist= gethelper().getmetricarraylist();

                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemMetricAdapter = new Itemmetricadapter(getActivity(), metricItemArraylist, new adapteritemclick() {
                                @Override
                                public void onItemClicked(Object object) {

                                }

                                @Override
                                public void onItemClicked(Object object, int type) {

                                }
                            });

                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyviewItem.setNestedScrollingEnabled(false);
                            recyviewItem.setLayoutManager(mLayoutManager);
                            recyviewItem.setItemAnimator(new DefaultItemAnimator());
                            recyviewItem.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                            recyviewItem.setAdapter(itemMetricAdapter);
                        }
                    });
                }
            }).start();
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {

        return R.layout.fragment_framemetricssettings;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_md:
                clearfocus();
                showselectedunselected(img_md,img_md_salt,img_sha,img_sha_salt,config.prefs_md5);
                break;

            case R.id.layout_md_salt:
                clearfocus();
                showselectedunselected(img_md_salt,img_md,img_sha,img_sha_salt,config.prefs_md5_salt);
                break;

            case R.id.layout_sha:
                clearfocus();
                showselectedunselected(img_sha,img_md,img_md_salt,img_sha_salt,config.prefs_sha);
                break;

            case R.id.layout_sha_salt:
                clearfocus();
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
    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void saveData()
    {

        if(keytype.equalsIgnoreCase(config.prefs_md5_salt))
        {
            if(edt_md_salt.getText().toString().trim().length() == 0)
            {
                Toast.makeText(getActivity(),"Please enter SALT value",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if(keytype.equalsIgnoreCase(config.prefs_sha_salt))
        {
            if(edt_sha_salt.getText().toString().trim().length() == 0)
            {
                Toast.makeText(getActivity(),"Please enter SALT value",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        xdata.getinstance().saveSetting(config.hashtype,keytype);
        xdata.getinstance().saveSetting(config.framecount,edt_framescount.getText().toString().trim());
        xdata.getinstance().saveSetting(config.frameupdateevery,edt_update_every.getText().toString().trim());
        xdata.getinstance().saveSetting(config.prefs_sha_salt,edt_sha_salt.getText().toString());
        xdata.getinstance().saveSetting(config.prefs_md5_salt,edt_md_salt.getText().toString());

        // save metric list
        Gson gson = new Gson();
        String json = gson.toJson(metricItemArraylist);
        xdata.getinstance().saveSetting(config.metriclist,json);

        gethelper().onBack();
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid) {
            case R.id.img_back:
                saveData();

                break;
            case R.id.img_cancel:
                gethelper().onBack();
                break;
        }
    }

    public void clearfocus(){
        edt_framescount.clearFocus();
        edt_md_salt.clearFocus();
        edt_sha_salt.clearFocus();
        common.hidekeyboard(applicationviavideocomposer.getactivity());
    }
}
