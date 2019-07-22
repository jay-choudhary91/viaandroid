package com.deeptruth.app.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.inapputils.IabHelper;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.googledriveutils.DriveServiceHelper;
import com.deeptruth.app.android.utils.pinviewtext;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.hglvideotrimmer;
import com.deeptruth.app.android.videotrimmer.interfaces.onhglvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.ontrimvideolistener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class fragmentrimvideo extends DialogFragment implements View.OnClickListener, ontrimvideolistener, onhglvideolistener {
    @BindView(R.id.trimerview)
    hglvideotrimmer mvideotrimmer;
    @BindView(R.id.rootlayout)
    CardView rootlayout;
    @BindView(R.id.lyout_publish)
    LinearLayout lyoutpublish;
    @BindView(R.id.lyout_send)
    LinearLayout lyoutsend;
    @BindView(R.id.lyout_export)
    LinearLayout lyoutexport;
    @BindView(R.id.lyout_help)
    LinearLayout lyouthelp;
    @BindView(R.id.img_cancel)
    ImageView img_cancel;

    private progressdialog mprogressdialog;
    View rootview = null;
    String videopath="",videotoken="";
    int videoduration;
    int navigationbarheight = 0;

    IabHelper mHelper;
    // Debug tag, for logging
    final String TAG = "TestInApp";
    String SelectedSku = "";
    Dialog dialoginapppurchase =null,dialogupgradecode=null;
    private static final int REQUEST_CODE_SIGN_IN = 102;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_trimvideo, container, false);

            ButterKnife.bind(this, rootview);
            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();

            lyoutpublish.setOnClickListener(this);
            lyoutsend.setOnClickListener(this);
            lyoutexport.setOnClickListener(this);
            lyouthelp.setOnClickListener(this);
            img_cancel.setOnClickListener(this);

            if (mvideotrimmer != null) {
                mvideotrimmer.setMaxDuration(videoduration);
                mvideotrimmer.setOnTrimVideoListener(this);
                mvideotrimmer.setOnHgLVideoListener(this);
                mvideotrimmer.setVideoURI(Uri.parse(videopath));
                mvideotrimmer.setVideoInformationVisibility(true);
            }

        }

        rootview.post(new Runnable() {
            @Override
            public void run() {
                if(common.getapppaidlevel() <= 0)
                    checkinapppurchasestatus();
            }
        });

        return rootview;
    }

    public void checkinapppurchasestatus()
    {
        String message = applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_trim)+"\n"+"\n"+
                applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_upgrade);

        baseactivity.getinstance().showinapppurchasepopup(applicationviavideocomposer.
                getactivity(), message, new adapteritemclick(){
            @Override
            public void onItemClicked(Object object) {

            }
            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyout_publish:
                if(common.getapppaidlevel() <= 0)
                {
                    checkinapppurchasestatus();
                    return;
                }

                String publish = getActivity().getResources().getString(R.string.publish_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.publish_details2);
                if(xdata.getinstance().getSetting(config.enableplubishnotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enableplubishnotification).equalsIgnoreCase("0")) {
                         baseactivity.getinstance().share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_publish),publish);

                         return;
                }

                baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity());
                break;

            case R.id.lyout_send:
                if(common.getapppaidlevel() <= 0)
                {
                    checkinapppurchasestatus();
                    return;
                }

                String send = getActivity().getResources().getString(R.string.send_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.send_details2);
                if(xdata.getinstance().getSetting(config.enablesendnotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enablesendnotification).equalsIgnoreCase("0")) {
                         baseactivity.getinstance().share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_send),send);

                         return;
                }

                baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity());

                break;

            case R.id.lyout_export:
                if(common.getapppaidlevel() <= 0)
                {
                    checkinapppurchasestatus();
                    return;
                }

                String export = getActivity().getResources().getString(R.string.export_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.export_details2);
                if(xdata.getinstance().getSetting(config.enableexportnotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enableexportnotification).equalsIgnoreCase("0")) {
                        baseactivity.getinstance().share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_export),export);

                        return;
                }

                baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity());

                break;

            case R.id.img_cancel:
                getDialog().dismiss();
                break;

        }
    }


    @Override
    public void ontrimstarted() {
    }

    @Override
    public void getresult(Uri uri) {

    }

    @Override
    public void getresult(final String filePath) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressdialog.dismisswaitdialog();
                if(filePath != null){
                    //progressdialog.showwaitingdialog(getActivity());
                    String selectedvideopath = filePath;
                    getDialog().dismiss();
                   baseactivity.getinstance().showsharepopupsub(selectedvideopath,"video",videotoken);
                    //common.sharevideo(getActivity(),selectedvideopath);
                }
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
    }

    @Override
    public void cancelaction() {
        //mvideotrimmer.destroy();
    }

    @Override
    public void onerror(final String message) {

    }

    @Override
    public void onclik() {

        getDialog().dismiss();
    }

    @Override
    public void onvideoprepared() {

    }

    @Override
    public void onResume() {
        super.onResume();

        getscreenwidthheight(100,85);
    }

    public void setdata(String videoPath, int duration, String videotoken)
    {
        this.videopath =videoPath;
        this.videoduration =  duration;
        this.videotoken =  videotoken;

    }

    public void setlayoutmargin(){
        CardView.LayoutParams params = new CardView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        rootlayout.setLayoutParams(params);
        rootlayout.requestLayout();
    }

    public void getscreenwidthheight(int widthpercentage,int heightpercentage) {


        int width = common.getScreenWidth(getActivity());
        int height = common.getScreenHeight(getActivity());

        int percentageheight = (height / 100) * heightpercentage;
        int percentagewidth = (width / 100) * widthpercentage;

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        getDialog().getWindow().setLayout(percentagewidth, percentageheight);
    }
}
