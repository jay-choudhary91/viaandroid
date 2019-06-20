package com.deeptruth.app.android.fragments;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.locationawareactivity;
import com.deeptruth.app.android.adapter.adaptersynclogs;
import com.deeptruth.app.android.adapter.inapppageradapter;
import com.deeptruth.app.android.adapter.managementcontrolleradapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.models.synclogmodel;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.views.customfonttextview;
import com.deeptruth.app.android.views.divideritemdecoration;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class synclogfragment extends basefragment implements View.OnClickListener {

    @BindView(R.id.rv_sync_list)
    RecyclerView rv_sync_list;
    @BindView(R.id.rv_async_list)
    RecyclerView rv_async_list;
    @BindView(R.id.txt_synced)
    TextView txt_synced;
    @BindView(R.id.txt_asynced)
    TextView txt_asynced;
    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.layout_rootview)
    LinearLayout layout_rootview;

    adaptersynclogs adapterSync,adapterAsync;
    ArrayList<synclogmodel> synclist=new ArrayList<>();
    ArrayList<synclogmodel> asynclist=new ArrayList<>();
    int navigationbarheight = 0;

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this, parent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txt_synced:
                rv_sync_list.setVisibility(View.VISIBLE);
                rv_async_list.setVisibility(View.GONE);
                resetButtonViews(txt_synced, txt_asynced);
                break;
            case R.id.txt_asynced:
                rv_sync_list.setVisibility(View.GONE);
                rv_async_list.setVisibility(View.VISIBLE);
                resetButtonViews(txt_asynced, txt_synced);
                break;
            case R.id.img_arrow_back:
                gethelper().onBack();
                break;
        }
    }

    public void getdata()
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try
        {

            Cursor cursor = mdbhelper.fetchallmediastartinfo();
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do
                    {
                        String token = "" + cursor.getString(cursor.getColumnIndex("token"));
                        String mediakey = "" + cursor.getString(cursor.getColumnIndex("videokey"));
                        String localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                        String videostarttransactionid = "" + cursor.getString(cursor.getColumnIndex("videostarttransactionid"));
                        String sync_date = "" + cursor.getString(cursor.getColumnIndex("sync_date"));

                        synclogmodel model=new synclogmodel(token,mediakey,localkey,videostarttransactionid,sync_date);
                        if(sync_date.trim().length() == 0 || sync_date.equalsIgnoreCase("0"))
                            asynclist.add(model);
                        else
                            synclist.add(model);
                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void resetButtonViews(TextView view1, TextView view2)
    {
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable1 = (Drawable) view1.getBackground();
            drawable1.setColorFilter(getResources().getColor(R.color.dark_blue_solid_a), PorterDuff.Mode.MULTIPLY);
            view1.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

            Drawable drawable2 = (Drawable) view2.getBackground();
            drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view2.setTextColor(ContextCompat.getColor(getActivity(),R.color.dark_blue_solid_a));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);

        txt_title_actionbar.setText("Logs");

        img_arrow_back.setOnClickListener(this);
        txt_synced.setOnClickListener(this);
        txt_asynced.setOnClickListener(this);
        resetButtonViews(txt_synced, txt_asynced);

        {
            adapterSync = new adaptersynclogs(getActivity(),synclist,null);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rv_sync_list.setLayoutManager(mLayoutManager);
            rv_sync_list.addItemDecoration(new divideritemdecoration(applicationviavideocomposer.getactivity()));
            rv_sync_list.setItemAnimator(new DefaultItemAnimator());
            rv_sync_list.setAdapter(adapterSync);
        }

        {
            adapterAsync = new adaptersynclogs(getActivity(),asynclist,null);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rv_async_list.setLayoutManager(mLayoutManager);
            rv_async_list.addItemDecoration(new divideritemdecoration(applicationviavideocomposer.getactivity()));
            rv_async_list.setItemAnimator(new DefaultItemAnimator());
            rv_async_list.setAdapter(adapterAsync);
        }

        getdata();
        navigationbarheight =  common.getnavigationbarheight();
        setlayoutmargin();

        adapterSync.notifyDataSetChanged();
        adapterAsync.notifyDataSetChanged();

        rv_sync_list.setVisibility(View.VISIBLE);
        rv_async_list.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_syncloglist;
    }

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        layout_rootview.setLayoutParams(params);
        layout_rootview.requestLayout();
    }

}
