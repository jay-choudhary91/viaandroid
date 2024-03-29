package com.deeptruth.app.android.fragments;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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
import com.deeptruth.app.android.adapter.adaptersynclogs;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
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
    ArrayList<synclogmodel> mainsynclist =new ArrayList<>();
    ArrayList<synclogmodel> mainasynclist =new ArrayList<>();
    int navigationbarheight = 0;

    private Handler myHandler;
    private Runnable myRunnable;

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

    public void starttimer() {
        if (myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run()
            {
                getdata();
                myHandler.postDelayed(this, 5000);
            }
        };
        myHandler.post(myRunnable);
    }

    public void getdata()
    {


        new Thread(new Runnable() {
            @Override
            public void run()
            {
                final ArrayList<synclogmodel> synclist=new ArrayList<>();
                final ArrayList<synclogmodel> asynclist=new ArrayList<>();

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
                                String type = "" + cursor.getString(cursor.getColumnIndex("type"));
                                String videostartdevicedate = "" + cursor.getString(cursor.getColumnIndex("videostartdevicedate"));
                                String videocompleteddevicedate = "" + cursor.getString(cursor.getColumnIndex("videocompletedevicedate"));

                                synclogmodel model=new synclogmodel(token,mediakey,localkey,videostarttransactionid,sync_date,type,
                                        videocompleteddevicedate,videostartdevicedate);
                                if(sync_date.trim().length() == 0 || sync_date.equalsIgnoreCase("0"))
                                {
                                    Cursor cursor1=mdbhelper.fetchunsyncedmetaframe(localkey);
                                    if(videostarttransactionid.trim().isEmpty())
                                    {
                                        asynclist.add(model);
                                    }
                                    else
                                    {
                                        if(cursor1 == null || cursor1.getCount() == 0)
                                            synclist.add(model);
                                        else
                                            asynclist.add(model);
                                    }
                                }
                                else
                                {
                                    synclist.add(model);
                                }
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

                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(synclist.size() != mainsynclist.size() || asynclist.size() != mainasynclist.size())
                        {
                            mainsynclist.clear();
                            adapterSync.notifyDataSetChanged();
                            mainasynclist.clear();
                            adapterAsync.notifyDataSetChanged();

                            mainsynclist.addAll(synclist);
                            mainasynclist.addAll(asynclist);

                            adapterSync.notifyDataSetChanged();
                            adapterAsync.notifyDataSetChanged();

                        }

                        txt_synced.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.synced_media)+" ("+
                                mainsynclist.size()+")");
                        txt_asynced.setText(applicationviavideocomposer.getactivity().getResources().getString(R.string.asynced_media)+" ("+
                                mainasynclist.size()+")");

                    }
                });

            }
        }).start();

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
            adapterSync = new adaptersynclogs(getActivity(), mainsynclist,mitemclick);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rv_sync_list.setLayoutManager(mLayoutManager);
            rv_sync_list.addItemDecoration(new divideritemdecoration(applicationviavideocomposer.getactivity()));
            rv_sync_list.setItemAnimator(new DefaultItemAnimator());
            rv_sync_list.setAdapter(adapterSync);
        }

        {
            adapterAsync = new adaptersynclogs(getActivity(), mainasynclist,mitemclick);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rv_async_list.setLayoutManager(mLayoutManager);
            rv_async_list.addItemDecoration(new divideritemdecoration(applicationviavideocomposer.getactivity()));
            rv_async_list.setItemAnimator(new DefaultItemAnimator());
            rv_async_list.setAdapter(adapterAsync);
        }

        starttimer();
        navigationbarheight =  common.getnavigationbarheight();
        setlayoutmargin();

        rv_sync_list.setVisibility(View.VISIBLE);
        rv_async_list.setVisibility(View.GONE);
        return rootView;
    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {
            synclogmodel model=(synclogmodel)object;
            synclogdetailsfragment fragment=new synclogdetailsfragment();
            fragment.setdata(model);
            gethelper().addFragment(fragment, false, true);
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_syncloglist;
    }

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,0,0,navigationbarheight);
        layout_rootview.setLayoutParams(params);
        layout_rootview.requestLayout();
    }

}
