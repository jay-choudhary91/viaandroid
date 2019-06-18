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
import com.deeptruth.app.android.utils.xdata;
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
    @BindView(R.id.txt_key)
    customfonttextview txt_key;
    @BindView(R.id.txt_token)
    customfonttextview txt_token;
    @BindView(R.id.txt_startframe)
    customfonttextview txt_startframe;
    @BindView(R.id.txt_endframe)
    customfonttextview txt_endframe;
    @BindView(R.id.txt_complete)
    customfonttextview tv_complete;
    @BindView(R.id.txt_totleframe)
    customfonttextview txt_totleframe;
    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;
    @BindView(R.id.llrootlayout)
    ScrollView llrootlayout;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.tv_delete)
    customfonttextview tv_delete;

    @BindView(R.id.view_key)
    View view_key;
    @BindView(R.id.view_token)
    View view_token;
    @BindView(R.id.view_startframe)
    View view_startframe;
    @BindView(R.id.view_endframe)
    View view_endframe;
    @BindView(R.id.view_completeframe)
    View view_completeframe;
    @BindView(R.id.view_totleframe)
    View view_totleframe;

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
            String end_frames = xapidatamap.get(config.end_frames);
            String started_frames = xapidatamap.get(config.started_frames);
            String total_frames = xapidatamap.get(config.total_frames);
            String media_key = xapidatamap.get(config.key_media);
            String media_token = xapidatamap.get(config.tokan_media);

            if(!completed_frames.isEmpty() && !end_frames.isEmpty()){
                txt_key.setVisibility(View.VISIBLE);
                txt_token.setVisibility(View.VISIBLE);
                txt_startframe.setVisibility(View.VISIBLE);
                tv_complete.setVisibility(View.VISIBLE);
                txt_endframe.setVisibility(View.VISIBLE);
                txt_totleframe.setVisibility(View.VISIBLE);

                view_key.setVisibility(View.VISIBLE);
                view_token.setVisibility(View.VISIBLE);
                view_startframe.setVisibility(View.VISIBLE);
                view_endframe.setVisibility(View.VISIBLE);
                view_completeframe.setVisibility(View.VISIBLE);
                view_totleframe.setVisibility(View.VISIBLE);

                setvalue( txt_key,getActivity().getResources().getString(R.string.xapi_key),media_key);
                setvalue( txt_token,getActivity().getResources().getString(R.string.xapi_token),media_token);
                setvalue( txt_startframe,getActivity().getResources().getString(R.string.xapi_started_frames),started_frames);
                setvalue( txt_endframe,getActivity().getResources().getString(R.string.xapi_end_frames),end_frames);
                setvalue( tv_complete,getActivity().getResources().getString(R.string.xapi_complete),completed_frames);
                setvalue( txt_totleframe,getActivity().getResources().getString(R.string.xapi_total_frames),total_frames);
            }else {

                txt_key.setVisibility(View.GONE);
                txt_token.setVisibility(View.GONE);
                txt_startframe.setVisibility(View.GONE);
                tv_complete.setVisibility(View.GONE);
                txt_endframe.setVisibility(View.GONE);
                txt_totleframe.setVisibility(View.GONE);

                view_key.setVisibility(View.GONE);
                view_token.setVisibility(View.GONE);
                view_startframe.setVisibility(View.GONE);
                view_endframe.setVisibility(View.GONE);
                view_completeframe.setVisibility(View.GONE);
                view_totleframe.setVisibility(View.GONE);
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
