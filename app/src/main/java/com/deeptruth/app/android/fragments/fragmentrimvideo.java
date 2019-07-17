package com.deeptruth.app.android.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.hglvideotrimmer;
import com.deeptruth.app.android.videotrimmer.interfaces.onhglvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.ontrimvideolistener;

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


    private progressdialog mprogressdialog;
    View rootview = null;
    String videopath="",videotoken="";
    int videoduration;
    int navigationbarheight = 0;

    /*@Override
    public int getlayoutid() {
        return R.layout.fragment_trimvideo;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);

        gethelper().updateheader("");
        ButterKnife.bind(this, parent);
    }*/

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


            if (mvideotrimmer != null) {
                mvideotrimmer.setMaxDuration(videoduration);
                mvideotrimmer.setOnTrimVideoListener(this);
                mvideotrimmer.setOnHgLVideoListener(this);
                mvideotrimmer.setVideoURI(Uri.parse(videopath));
                mvideotrimmer.setVideoInformationVisibility(true);
            }
        }
        return rootview;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyout_publish:
                String publish = getActivity().getResources().getString(R.string.publish_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.publish_details2);

                if(xdata.getinstance().getSetting(config.enablenotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enablenotification).equalsIgnoreCase("0")) {

                    /*if (xdata.getinstance().getSetting(config.enablenotification).isEmpty())
                        xdata.getinstance().saveSetting(config.enablenotification, "0");*/

                    appdialog.share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_publish),publish);

                }
                break;

            case R.id.lyout_send:
                String send = getActivity().getResources().getString(R.string.send_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.send_details2);
                appdialog.share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_send),send);
                break;


            case R.id.lyout_export:
                String export = getActivity().getResources().getString(R.string.export_details1)+"\n"+"\n"+"\n"+getActivity().getResources().getString(R.string.export_details2);
                appdialog.share_alert_dialog(getActivity(),getActivity().getResources().getString(R.string.txt_export),export);
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
                    //gethelper().showsharepopupsub(selectedvideopath,"video",videotoken);
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

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onclik() {

        getDialog().dismiss();

        //gethelper().onBack();
       // gethelper().showsharepopupsub(videopath,"video",videotoken);
    }

    @Override
    public void onvideoprepared() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(getactivity(), "onvideoprepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid)
        {
            case R.id.img_share_icon:

                if(mvideotrimmer != null)
                {
                    //progressdialog.showwaitingdialog(getActivity());
                    mvideotrimmer.onSaveClicked();
                }

                break;

            case R.id.img_menu:
                gethelper().onBack();
                break;
        }
    }
*/

    @Override
    public void onResume() {
        super.onResume();

        getscreenwidthheight(95,85);
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

        getDialog().getWindow().setLayout(percentagewidth, percentageheight);
    }
}
