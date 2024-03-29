package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.xapidetailadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.itemchanged;
import com.deeptruth.app.android.models.pair;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;
import com.deeptruth.app.android.views.divideritemdecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fetchxapivaluefragment extends basefragment implements itemchanged, View.OnClickListener  {

    View rootview;
    @BindView(R.id.recyview_item)
    RecyclerView recyviewItem;
    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;
    @BindView(R.id.llrootlayout)
    LinearLayout llrootlayout;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.tv_delete)
    customfonttextview tv_delete;
    @BindView(R.id.txt_inputdata)
    TextView edt_inputdata;

    ArrayList<pair> mItemList = new ArrayList<>();
    private xapidetailadapter mControllerAdapter;
    int navigationbarheight = 0;
    public String sharedprefkey="";
    boolean shouldshowallapidata =true;

    @Override
    public int getlayoutid() {
        return R.layout.fatch_apivalue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        if(rootview==null){
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            txt_title_actionbar.setText("Save Setting");
            tv_delete.setVisibility(View.VISIBLE);

            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();
            edt_inputdata.setText(xdata.getinstance().getSetting(xdata.xapi_url));
            img_arrow_back.setOnClickListener(this);
            tv_delete.setOnClickListener(this);

            getData();

            mControllerAdapter = new xapidetailadapter(getActivity(),mItemList,mItemClick);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyviewItem.setLayoutManager(mLayoutManager);
            recyviewItem.setHasFixedSize(true);
            recyviewItem.addItemDecoration(new divideritemdecoration(getActivity()));
            recyviewItem.setAdapter(mControllerAdapter);
        }
        return rootview;
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
    }

    @Override
    public void onItemChanged(Object object) {

    }

    adapteritemclick mItemClick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {
            pair pairdata = (pair) object;

            String key = pairdata.KeyName;
            String completevalue = pairdata.completevalue;
            String completelessvalue = pairdata.completelessvalue;
            String startedvalue = pairdata.startedvalue;
            HashMap<String,String> xapidatamap = pairdata.getKeyvaluemap();

            fragment_xapi_detail xapi_detail=new fragment_xapi_detail();
            xapi_detail.setdata(key,xapidatamap,completevalue,completelessvalue);
            gethelper().addFragment(xapi_detail,false,true);
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }
    };

    private void getData() {

        mItemList = new ArrayList<>();
        Gson gson = new Gson();
        HashMap<String,String> xapivalue = new HashMap<String,String>();

        for(int i =0; i < Integer.MAX_VALUE;i++)
        {
            if(! xdata.getinstance().getSetting(sharedprefkey +""+i).trim().isEmpty())
            {
                pair pair=new pair();
                pair.setKeyName(sharedprefkey +""+i);
                String value = xdata.getinstance().getSetting(sharedprefkey +i);;
                if (value.trim().length() > 0)
                {
                    Type type = new TypeToken< HashMap<String,String>>() {
                    }.getType();
                    xapivalue = gson.fromJson(value, type);
                }

                HashMap<String,String> geturl=xapivalue;
                String a=geturl.get(config.API_STORE_URL);
                if(a.trim().length() >= 300)
                    a=a.substring(0,299);

                pair.setKeyvaluemap(xapivalue);
                pair.setKeyValue(a);

                mItemList.add(pair);
            }
            else
            {
                break;
            }
        }
    }

    public void setdata(boolean shouldshowallapidata)
    {
        this.shouldshowallapidata = shouldshowallapidata;
        sharedprefkey=config.all_xapi_list;
        if(! shouldshowallapidata)
            sharedprefkey=config.sidecar_xapi_actions;
    }

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,0,0,navigationbarheight);
        llrootlayout.setLayoutParams(params);
        llrootlayout.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_arrow_back:
                tv_delete.setVisibility(View.GONE);
                gethelper().onBack();
                break;

            case R.id.tv_delete:
                common.clearxapidate(mControllerAdapter,mItemList,sharedprefkey);
                break;
        }
    }
}
