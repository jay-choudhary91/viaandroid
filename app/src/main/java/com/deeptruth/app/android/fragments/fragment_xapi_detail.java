package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.views.customfonttextview;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragment_xapi_detail extends basefragment {

    View rootview;
    @BindView(R.id.txt_starttime)
    customfonttextview txtstarttime;
    @BindView(R.id.txt_endtime)
    customfonttextview txtendtime;
    @BindView(R.id.txt_config)
    customfonttextview txtconfigaction;
    @BindView(R.id.txt_parameter)
    customfonttextview txtparameter;
    @BindView(R.id.txt_result)
    customfonttextview txtresult;
    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;
    @BindView(R.id.llrootlayout)
    LinearLayout llrootlayout;

    String key = "";
    HashMap<String,String> xapidatamap;
    int navigationbarheight = 0;

    @Override
    public int getlayoutid() {
        return R.layout.fragment_xapi_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        if(rootview==null){
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            txt_title_actionbar.setText("Xapi Detail");
            navigationbarheight =  common.getnavigationbarheight();
           // setlayoutmargin();

            String startdate = xapidatamap.get(config.API_START_DATE);
            String enddate = xapidatamap.get(config.API_RESPONCE_DATE);
            String configaction = xapidatamap.get(config.API_ACTION);
            String result = xapidatamap.get(config.API_RESULT);
            String url = xapidatamap.get(config.API_STORE_URL);

            txtstarttime.setText(startdate);
            txtendtime.setText(enddate);
            txtconfigaction.setText(configaction);
            txtparameter.setText(startdate);
            txtresult.setText(result);

        }
        return rootview;
    }

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        llrootlayout.setLayoutParams(params);
        llrootlayout.requestLayout();
    }


    public void setdata(String key, HashMap<String,String> xapidatamap){
        this.key=key;
        this.xapidatamap = xapidatamap;
    }
}