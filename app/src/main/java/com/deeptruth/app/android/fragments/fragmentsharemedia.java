package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.inapputils.IabHelper;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.rangeseekbar.RangeSeekbar;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.hglvideotrimmer;
import com.deeptruth.app.android.videotrimmer.interfaces.onhglvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.ontrimvideolistener;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class fragmentsharemedia extends DialogFragment implements View.OnClickListener, ontrimvideolistener, onhglvideolistener {
    @BindView(R.id.trimerview)
    hglvideotrimmer mvideotrimmer;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
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
    @BindView(R.id.layout_colorsection)
    LinearLayout layout_colorsection;
    @BindView(R.id.layout_video_section)
    LinearLayout layout_video_section;
    @BindView(R.id.layout_imageaudio_section)
    LinearLayout layout_imageaudio_section;
    @BindView(R.id.img_thumbnail)
    ImageView img_thumbnail;
    @BindView(R.id.txt_media_starttime)
    TextView txt_media_starttime;
    @BindView(R.id.txt_media_endtime)
    TextView txt_media_endtime;
    @BindView(R.id.progressmediasync)
    ProgressBar progressmediasync;
    @BindView(R.id.layout_rangeseekbar)
    RangeSeekbar layout_rangeseekbar;

    private progressdialog mprogressdialog;
    View rootview = null;
    String mediafilepath ="", mediatoken ="",mediatype="",mediathumbnailurl="";
    int mediaduration = 0;
    int navigationbarheight = 0;
    private final int request_permissions = 1;
    private Handler myHandler;
    private Runnable myRunnable;

    IabHelper mHelper;
    // Debug tag, for logging
    final String TAG = "TestInApp";
    String SelectedSku = "";
    Dialog dialoginapppurchase =null,dialogupgradecode=null;
    private static final int REQUEST_CODE_SIGN_IN = 102;
    private boolean ismediatrimmed=false;
    private ArrayList<metadatahash> mitemlist = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_sharemedia, container, false);

            ButterKnife.bind(this, rootview);
            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();

            lyoutpublish.setOnClickListener(this);
            lyoutsend.setOnClickListener(this);
            lyoutexport.setOnClickListener(this);
            lyouthelp.setOnClickListener(this);
            img_cancel.setOnClickListener(this);

            if(mediatype.equalsIgnoreCase(config.type_video))
            {
                layout_video_section.setVisibility(View.VISIBLE);
                layout_imageaudio_section.setVisibility(View.GONE);
                if (mvideotrimmer != null) {
                    mvideotrimmer.setMaxDuration(mediaduration);
                    mvideotrimmer.setOnTrimVideoListener(this);
                    mvideotrimmer.setOnHgLVideoListener(this);
                    mvideotrimmer.setVideoURI(Uri.parse(mediafilepath));
                    mvideotrimmer.setVideoInformationVisibility(true);
                }
            }
            else if(mediatype.equalsIgnoreCase(config.type_image))
            {
                txt_media_starttime.setVisibility(View.GONE);
                txt_media_endtime.setVisibility(View.GONE);
                layout_colorsection.setVisibility(View.GONE);
                layout_video_section.setVisibility(View.GONE);
                layout_rangeseekbar.setVisibility(View.GONE);
                layout_imageaudio_section.setVisibility(View.VISIBLE);

                if(! mediathumbnailurl.trim().isEmpty() && new File(mediathumbnailurl).exists())
                {
                    img_thumbnail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    img_thumbnail.setBackgroundColor(getResources().getColor(R.color.transparent));
                    Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                            BuildConfig.APPLICATION_ID + ".provider", new File(mediathumbnailurl));
                    Glide.with(applicationviavideocomposer.getactivity()).
                            load(uri).
                            thumbnail(0.1f).
                            into(img_thumbnail);
                }
            }
            else if(mediatype.equalsIgnoreCase(config.type_audio))
            {
                img_thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img_thumbnail.setBackgroundColor(getResources().getColor(R.color.black));
                layout_video_section.setVisibility(View.GONE);
                layout_imageaudio_section.setVisibility(View.VISIBLE);

                if(! mediathumbnailurl.trim().isEmpty() && new File(mediathumbnailurl).exists())
                {
                    Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                            BuildConfig.APPLICATION_ID + ".provider", new File(mediathumbnailurl));
                    Glide.with(applicationviavideocomposer.getactivity()).
                            load(uri).
                            thumbnail(0.1f).
                            into(img_thumbnail);
                }
            }

            if(! mediatype.equalsIgnoreCase(config.type_image))
                setcolorbardata();
        }
        txt_media_endtime.setText(common.mediaplaytimeformatter(mediaduration));
        return rootview;
    }

    public void setcolorbardata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run()
            {
                drawmediainformation(mediafilepath);
                myHandler.postDelayed(this, 10000);
            }
        };
        myHandler.post(myRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyout_publish:
                String publish = getActivity().getResources().getString(R.string.publish_details1)+"\n"+"\n"+"\n"+
                        getActivity().getResources().getString(R.string.publish_details2);

                getDialog().dismiss();

                if(xdata.getinstance().getSetting(config.enableplubishnotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enableplubishnotification).equalsIgnoreCase("0"))
                {
                    baseactivity.getinstance().share_alert_dialog(getActivity(), getActivity().
                            getResources().getString(R.string.txt_publish), publish, new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {
                            //baseactivity.getinstance().showsharepopupsub(mediafilepath,config.item_video,mediatoken,ismediatrimmed);
                            baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                                    mediatype,ismediatrimmed,mediathumbnailurl);

                        }

                        @Override
                        public void onItemClicked(Object object, int type) {

                        }
                    });
                    return;
                }
                baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                        mediatype,ismediatrimmed,mediathumbnailurl);
                //baseactivity.getinstance().showsharepopupsub(mediafilepath,config.item_video,mediatoken,ismediatrimmed);
                break;

            case R.id.lyout_send:
                String send = getActivity().getResources().getString(R.string.send_details1)+"\n"+"\n"+
                        getActivity().getResources().getString(R.string.send_details2);

                getDialog().dismiss();
                if(xdata.getinstance().getSetting(config.enablesendnotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enablesendnotification).equalsIgnoreCase("0")) {
                    baseactivity.getinstance().share_alert_dialog(getActivity(),getActivity().
                            getResources().getString(R.string.txt_send),send ,new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {
                            baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                                    mediatype,ismediatrimmed,mediathumbnailurl);
                        }
                        @Override
                        public void onItemClicked(Object object, int type) {

                        }
                    });
                    return;
                }
                baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                        mediatype,ismediatrimmed,mediathumbnailurl);
                break;

            case R.id.lyout_export:
                String export = getActivity().getResources().getString(R.string.export_details1)+"\n"+"\n"+"\n"+
                        getActivity().getResources().getString(R.string.export_details2);

                getDialog().dismiss();
                if(xdata.getinstance().getSetting(config.enableexportnotification).isEmpty() ||
                        xdata.getinstance().getSetting(config.enableexportnotification).equalsIgnoreCase("0")) {
                    baseactivity.getinstance().share_alert_dialog(getActivity(),getActivity().
                            getResources().getString(R.string.txt_save),export ,new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {
                            checkwritepermission();
                        }
                        @Override
                        public void onItemClicked(Object object, int type) {
                        }
                    });
                    return;
                }
                checkwritepermission();
                break;

            case R.id.img_cancel:
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_permissions) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted)
                showmediashareoptions();
            else
                Toast.makeText(applicationviavideocomposer.getactivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
        }
    }

    public void checkwritepermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (common.getstoragedeniedpermissions().isEmpty())
                showmediashareoptions();
        }
        else
        {
            showmediashareoptions();
        }
    }

    public void showmediashareoptions()
    {
        if(mediatype.equalsIgnoreCase(config.type_image))
            common.shareimage(applicationviavideocomposer.getactivity(),mediafilepath);
        else if(mediatype.equalsIgnoreCase(config.type_video))
            common.sharevideo(applicationviavideocomposer.getactivity(),mediafilepath);
        else if(mediatype.equalsIgnoreCase(config.type_audio))
            common.shareaudio(applicationviavideocomposer.getactivity(),mediafilepath);
    }

    public void drawmediainformation(String mediafilepath)
    {
            databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mitemlist = mdbhelper.getmediametadatabyfilename(common.getfilename(mediafilepath));

            try
            {
                mdbhelper.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            if(mitemlist.size() > 0)
            {
                int sectioncount=0,invalidcount=0;
                String lastcolor="";
                ArrayList<String> colorsectioncount=new ArrayList<>();
                for (int i = 0; i < mitemlist.size(); i++)
                {
                    String strsequence=mitemlist.get(i).getSequenceno();
                    if(! strsequence.trim().isEmpty() && (! strsequence.equalsIgnoreCase("NA")) &&
                            (! strsequence.equalsIgnoreCase("null")))
                    {

                        if(mitemlist.get(i).getColor().trim().isEmpty())
                            invalidcount++;

                        sectioncount++;
                        if(mitemlist.get(i).getColor().trim().isEmpty())
                            mitemlist.get(i).setColor(config.color_transparent);

                        if(! lastcolor.equalsIgnoreCase(mitemlist.get(i).getColor()))
                        {
                            sectioncount=0;
                            sectioncount++;
                            colorsectioncount.add(mitemlist.get(i).getColor()+","+sectioncount);
                        }
                        else
                        {
                            colorsectioncount.set(colorsectioncount.size()-1,mitemlist.get(i).getColor()+","+sectioncount);
                        }
                        lastcolor=mitemlist.get(i).getColor();
                    }
                }

                if(invalidcount > 0)
                    progressmediasync.setVisibility(View.VISIBLE);
                else
                    progressmediasync.setVisibility(View.INVISIBLE);

                try {
                    layout_colorsection.removeAllViews();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                layout_colorsection.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.validating_white_bg));

                for(int i=0;i<colorsectioncount.size();i++)
                {
                    String item=colorsectioncount.get(i);
                    if(! item.trim().isEmpty())
                    {
                        String[] itemarray=item.split(",");
                        if(itemarray.length >= 2)
                        {
                            String color=itemarray[0];
                            String weight=itemarray[1];
                            if(! weight.trim().isEmpty())
                                layout_colorsection.addView(getmediaseekbarbackgroundview(weight,color));
                        }
                    }
                }
            }
    }

    public View getmediaseekbarbackgroundview(String weight,String color)
    {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT,Float.parseFloat(weight));
        View view = new View(applicationviavideocomposer.getactivity());
        view.setLayoutParams(param);
        if((! color.isEmpty()))
        {
            view.setBackgroundColor(Color.parseColor(common.getcolorbystring(color)));
        }
        else
        {
            view.setBackgroundColor(Color.parseColor(config.color_code_white_transparent));
        }
        return view;
    }


    @Override
    public void ontrimstarted() {

        ismediatrimmed=true;
        if(common.ismediatrimcountexceed(config.mediatrimcount))
            baseactivity.getinstance().checkinapppurchasestatus(config.gravitycenter);
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
                    //baseactivity.getinstance().showsharepopupsub(selectedvideopath,"video",mediatoken,ismediatrimmed);
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
    public int getwidth() {
        return common.getScreenWidth(getActivity());
    }

    @Override
    public void onvideoprepared() {

    }

    @Override
    public void onResume() {
        super.onResume();

        getscreenwidthheight(97,80);
    }

    public void setdata(String videoPath, int duration, String videotoken,String mediatype,String mediathumbnailurl)
    {
        this.mediafilepath =videoPath;
        this.mediaduration =  duration;
        this.mediatoken =  videotoken;
        this.mediatype =  mediatype;
        this.mediathumbnailurl =  mediathumbnailurl;
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

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().getWindow().setLayout(percentagewidth, percentageheight);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        double bottommargin = (height / 100) * 3;
        params.y = 10 + Integer.parseInt(xdata.getinstance().getSetting(config.TOPBAR_HEIGHT));
        getDialog().getWindow().setAttributes(params);

    }
}
