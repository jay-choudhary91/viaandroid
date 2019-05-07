package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.settingsadapter;
import com.deeptruth.app.android.adapter.xapidetailadapter;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.itemchanged;
import com.deeptruth.app.android.models.metricmodel;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fatchxapivaluefragment extends basefragment implements itemchanged, View.OnClickListener  {

    View rootview;
    @BindView(R.id.recyview_item)
    RecyclerView recyviewItem;
    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;
    @BindView(R.id.llrootlayout)
    LinearLayout llrootlayout;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.edt_inputdata)
    EditText edt_inputdata;

    ArrayList<pair> mItemList = new ArrayList<>();
    private xapidetailadapter mControllerAdapter;
    int navigationbarheight = 0;

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
            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();
            edt_inputdata.setText(config.XAPI_BASE_URL);
            img_arrow_back.setOnClickListener(this);

            getData();

            mControllerAdapter = new xapidetailadapter(getActivity(),mItemList,mItemClick);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyviewItem.setLayoutManager(mLayoutManager);
            recyviewItem.addItemDecoration(new divideritemdecoration(getActivity()));
            recyviewItem.setItemAnimator(new DefaultItemAnimator());
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
          /*  ManagementController mController=new ManagementController();
            mController.setKeyName(pair.getKeyName());
            mController.setKeyValue(pair.getKeyValue());
            mController.setTxtName(Config.LIST_SETTINGS);
            popupView.showPopupView(getActivity(),getString(R.string.save_setting),"",mController,mItemChanged);*/
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }
    };

    private void getData() {
        HashMap<String, String> map= xdata.getinstance().getSettingApiArray();
        mItemList = new ArrayList<>();
        Gson gson = new Gson();
        HashMap<String,String> xapivalue = new HashMap<String,String>();

        Iterator myVeryOwnIterator = map.keySet().iterator();
        while(myVeryOwnIterator.hasNext())
        {
            String key=(String)myVeryOwnIterator.next();
            pair pair=new pair();
            pair.setKeyName(""+ key);
            String value = map.get(key);

            if (value.trim().length() > 0) {
                Type type = new TypeToken< HashMap<String,String>>() {
                }.getType();
                xapivalue = gson.fromJson(value, type);
            }

            pair.setKeyvaluemap(xapivalue);

            mItemList.add(pair);
        }
    }

    itemchanged mItemChanged=new itemchanged() {
        @Override
        public void onItemChanged(Object object) {
            getData();
            mControllerAdapter.addItems(mItemList);
        }
    };

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        llrootlayout.setLayoutParams(params);
        llrootlayout.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_arrow_back:
                gethelper().onBack();
                break;
        }
    }


    public String[] splitapistring(String fullstring){

        String[] separated = fullstring.split("$");
        Log.e("fullstring",fullstring);

        return separated;
    }
}
