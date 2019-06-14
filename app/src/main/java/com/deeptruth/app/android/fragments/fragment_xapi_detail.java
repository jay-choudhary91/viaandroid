package com.deeptruth.app.android.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.views.customfonttextview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fragment_xapi_detail extends basefragment implements View.OnClickListener{

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
    ScrollView llrootlayout;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.tv_delete)
    customfonttextview tv_delete;
    @BindView(R.id.txt_complete)
    customfonttextview tv_complete;
    @BindView(R.id.txt_started)
    customfonttextview txt_started;
    @BindView(R.id.txt_completeless)
    customfonttextview tv_completeless;
    @BindView(R.id.view_complet)
    View view_complet;
    @BindView(R.id.view_completless)
    View view_completless;

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

            txt_title_actionbar.setText("Detail");
            tv_delete.setVisibility(View.GONE);
            navigationbarheight =  common.getnavigationbarheight();
            img_arrow_back.setOnClickListener(this);
            setlayoutmargin();

            String startdate = xapidatamap.get(config.API_START_DATE);
            String enddate = xapidatamap.get(config.API_RESPONCE_DATE);
            String configaction = xapidatamap.get(config.API_ACTION);
            String result = xapidatamap.get(config.API_RESULT);
            String parameter = xapidatamap.get(config.API_PARAMETER);

            String completed_frames = xapidatamap.get(config.completed_frames);
            String incompleted_frames = xapidatamap.get(config.incompleted_frames);
            String started_frames = xapidatamap.get(config.started_frames);



            if(!completed_frames.isEmpty() && !incompleted_frames.isEmpty()){
                tv_complete.setVisibility(View.VISIBLE);
                tv_completeless.setVisibility(View.VISIBLE);
                view_complet.setVisibility(View.VISIBLE);
                view_completless.setVisibility(View.VISIBLE);

                setvalue( tv_complete,getActivity().getResources().getString(R.string.xapi_complete),completed_frames);
                setvalue( tv_completeless,getActivity().getResources().getString(R.string.xapi_completeness),incompleted_frames);
                setvalue( txt_started,getActivity().getResources().getString(R.string.xapi_started),started_frames);
            }
            try {
                JSONObject obj = new JSONObject(result);
                setvalue( txtstarttime,getActivity().getResources().getString(R.string.start_time),startdate);
                setvalue( txtendtime,getActivity().getResources().getString(R.string.elapsed),enddate);
                setvalue( txtconfigaction,getActivity().getResources().getString(R.string.config_action),configaction);
                setvalue( txtparameter,getActivity().getResources().getString(R.string.parameter),parameter);
                txtresult.setText(obj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rootview;
    }

    public void setlayoutmargin(){
        ScrollView.LayoutParams params = new ScrollView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,0,0,navigationbarheight);
        llrootlayout.setLayoutParams(params);
        llrootlayout.requestLayout();
    }


    public void setdata(String key, HashMap<String,String> xapidatamap, String completevalue,String completelessvalue){
        this.key=key;
        this.xapidatamap = xapidatamap;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_arrow_back:
                gethelper().onBack();
                break;
        }
    }

    public void setvalue(TextView textView,String title,String data){
        SpannableString ss1=  new SpannableString(title);
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
        textView.append(ss1);
        textView.append(data);
    }
}
