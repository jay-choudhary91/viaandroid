package com.deeptruth.app.android.fragments;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.deeptruth.app.android.models.synclogmodel;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.views.customfonttextview;
import com.deeptruth.app.android.views.divideritemdecoration;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class synclogdetailsfragment extends basefragment implements View.OnClickListener {

    @BindView(R.id.txt_localkey)
    TextView txt_localkey;
    @BindView(R.id.txt_type)
    TextView txt_type;
    @BindView(R.id.txt_mediakey)
    TextView txt_mediakey;
    @BindView(R.id.txt_totalframes)
    TextView txt_totalframes;
    @BindView(R.id.txt_syncedframes)
    TextView txt_syncedframes;
    @BindView(R.id.txt_asyncedframes)
    TextView txt_asyncedframes;
    @BindView(R.id.txt_mediastartdate)
    TextView txt_mediastartdate;
    @BindView(R.id.txt_mediacompleteddate)
    TextView txt_mediacompleteddate;
    @BindView(R.id.txt_transactionid)
    TextView txt_transactionid;
    @BindView(R.id.txt_token)
    TextView txt_token;

    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;

    ArrayList<synclogmodel> itemlist=new ArrayList<>();
    synclogmodel syncmodel;
    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this, parent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
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

            Cursor cursor = mdbhelper.getallmetadatabylocalkey(syncmodel.getLocalkey());
            int lastsequenceno=0,syncedsquence=0, a=0;
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do
                    {
                        String rsequenceno = "" + cursor.getString(cursor.getColumnIndex("rsequenceno"));
                        String strsequence = "" + cursor.getString(cursor.getColumnIndex("sequenceno"));

                        if(! strsequence.trim().isEmpty())
                        {
                            int sequenceno=Integer.parseInt(strsequence);   // 15  30  45
                            int sequencecount=sequenceno - lastsequenceno;   // 15  15  15
                            if(! rsequenceno.trim().isEmpty() && (! rsequenceno.equalsIgnoreCase("0")))
                                syncedsquence=syncedsquence+sequencecount;  // 15  30   45

                            lastsequenceno=sequenceno;
                        }
                    } while (cursor.moveToNext());
                }
            }

            txt_totalframes.setText(""+lastsequenceno);
            txt_syncedframes.setText(""+syncedsquence);
            txt_asyncedframes.setText(""+(lastsequenceno - syncedsquence));

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);

        txt_title_actionbar.setText("Logs details");
        img_arrow_back.setOnClickListener(this);

        if(syncmodel != null)
        {
            txt_localkey.setText(syncmodel.getLocalkey());
            txt_type.setText(syncmodel.getType().toUpperCase());
            txt_mediakey.setText(syncmodel.getMediakey());
            txt_mediastartdate.setText(syncmodel.getMediastartdevicedate());
            txt_mediacompleteddate.setText(syncmodel.getMediacompleteddevicedate());
            txt_transactionid.setText(syncmodel.getMediastarttransactionid());
            txt_token.setText(syncmodel.getToken());
            getdata();
        }

        return rootView;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_synclogdetails;
    }


    public void setdata(synclogmodel syncmodel)
    {
        this.syncmodel=syncmodel;
    }

}
