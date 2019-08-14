package com.deeptruth.app.android.fragments;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.deeptruth.app.android.rangeseekbar.interfaces.onrangeseekbarchangelistener;
import com.deeptruth.app.android.rangeseekbar.widgets.crystalrangeseekbar;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.hglvideotrimmer;
import com.deeptruth.app.android.videotrimmer.interfaces.onhglvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.ontrimvideolistener;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    @BindView(R.id.img_audiothumb_timeline)
    ImageView img_audiothumb_timeline;
    @BindView(R.id.rangeseekbar)
    crystalrangeseekbar rangeSeekbar;
    @BindView(R.id.layout_progresslineleft)
    RelativeLayout layout_progresslineleft;
    @BindView(R.id.layout_progresslineright)
    RelativeLayout layout_progresslineright;
    @BindView(R.id.txt_mediatimemin)
    TextView txt_mediatimemin;
    @BindView(R.id.txt_mediatimemax)
    TextView txt_mediatimemax;
    @BindView(R.id.view_seekbarleftnavigation)
    View view_seekbarleftnavigation;
    @BindView(R.id.view_seekbarrightnavigation)
    View view_seekbarrightnavigation;

    View rootview = null;
    String mediafilepath ="", trimmedmediapath="",mediatoken ="",mediatype="",mediathumbnailurl="";
    int mediaduration = 0;
    float rangeseekbarwidth= 0;
    int navigationbarheight = 0;
    private int request_permissions = 1, selecteditemclick= 0;
    private Handler myHandler;
    private Runnable myRunnable;
    private long starttrimlength=0,endtrimlength=0;
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

            Drawable drawableleftarrow=null,drawablerightarrow=null;
            {
                // Read your drawable from somewhere
                Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.rightarrow);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                drawableleftarrow = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                        (int)common.convertDpToPixel(15,applicationviavideocomposer.getactivity()
                        ), (int)common.convertDpToPixel(25,applicationviavideocomposer.getactivity()
                        ), true));
            }

            {
                // Read your drawable from somewhere
                Drawable drawable = ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.leftarrow);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                drawablerightarrow = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                        (int)common.convertDpToPixel(15,applicationviavideocomposer.getactivity()
                        ), (int)common.convertDpToPixel(25,applicationviavideocomposer.getactivity()
                        ), true));
            }

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
                img_thumbnail.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                img_thumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
                img_audiothumb_timeline.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.black));
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

                    Glide.with(applicationviavideocomposer.getactivity()).
                            load(uri).
                            thumbnail(0.1f).
                            into(img_audiothumb_timeline);

                    img_audiothumb_timeline.setVisibility(View.VISIBLE);
                }
            }

            if(! mediatype.equalsIgnoreCase(config.type_image))
            {
                setcolorbardata();
                rangeSeekbar.setVisibility(View.VISIBLE);
                rangeSeekbar.setCornerRadius(10f)
                        .setBarColor(Color.TRANSPARENT)
                        .setBarHighlightColor(Color.BLACK)
                        .setMinValue(0)
                        .setMaxValue(mediaduration)
                        .setSteps(500)
                        .setLeftThumbDrawable(drawableleftarrow)
                        .setLeftThumbHighlightDrawable(drawableleftarrow)
                        .setRightThumbDrawable(drawablerightarrow)
                        .setRightThumbHighlightDrawable(drawablerightarrow)
                        .setDataType(crystalrangeseekbar.DataType.LONG)
                        .apply();

                rangeSeekbar.post(new Runnable() {
                    @Override
                    public void run() {
                        rangeseekbarwidth=rangeSeekbar.getWidth();
                    }
                });

                rangeSeekbar.setOnRangeSeekbarChangeListener(new onrangeseekbarchangelistener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        updateleftrightthumbs(minValue.longValue(),maxValue.longValue());
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long gapebetween=1000;
                        if(mediaduration > 2000)
                            gapebetween=2000;

                        rangeSeekbar.setMinValue(0).setMaxValue(mediaduration).setMinStartValue(0).setMaxStartValue(mediaduration).
                                setSteps(500).setGap(gapebetween).apply();
                    }
                }, 500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout_progresslineleft.setVisibility(View.VISIBLE);
                        layout_progresslineright.setVisibility(View.VISIBLE);
                        updateleftrightthumbs(0,mediaduration);
                        view_seekbarleftnavigation.setVisibility(View.VISIBLE);
                        view_seekbarrightnavigation.setVisibility(View.VISIBLE);
                    }
                }, 1000);
            }

        }
        txt_media_endtime.setText(common.mediaplaytimeformatter(mediaduration));
        return rootview;
    }

    public void updateleftrightthumbs(long mintimemillis,long maxtimemillis)
    {
        ismediatrimmed=false;
        starttrimlength=mintimemillis;
        endtrimlength=maxtimemillis;

        RelativeLayout.LayoutParams paramsleft = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsleft.addRule(RelativeLayout.ABOVE, rangeSeekbar.getId());

        RelativeLayout.LayoutParams paramsright = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsright.addRule(RelativeLayout.ABOVE, rangeSeekbar.getId());

        /*float a=rangeSeekbar.getLeftThumbRect().left;
        float b=rangeSeekbar.getLeftThumbRect().right;
        float c=rangeSeekbar.getRightThumbRect().left;
        float d=rangeSeekbar.getRightThumbRect().right;
        Log.e("LeftLeft"," "+a+" "+b);
        Log.e("RightRight"," "+c+" "+d);
        Log.e("CenterCenter"," "+centerLeftX+" "+centerRightX);*/

        float centerLeftX=rangeSeekbar.getLeftThumbRect().centerX();
        float centerRightX=rangeSeekbar.getRightThumbRect().centerX();

        paramsleft.setMargins((int) (centerLeftX - common.dpToPx(applicationviavideocomposer.getactivity(), 25)), 0, 0, 0);
        paramsright.setMargins((int) (centerRightX - common.dpToPx(applicationviavideocomposer.getactivity(), 25)), 0, 0, 0);
        layout_progresslineleft.setLayoutParams(paramsleft);
        layout_progresslineright.setLayoutParams(paramsright);

        {
            RelativeLayout.LayoutParams paramsseekbarleft = new RelativeLayout.LayoutParams(
                    (int)centerLeftX,
                    (int)common.dpToPx(applicationviavideocomposer.getactivity(), 25));
            paramsseekbarleft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            paramsseekbarleft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            view_seekbarleftnavigation.setLayoutParams(paramsseekbarleft);
        }

        {
            RelativeLayout.LayoutParams paramsseekbarleft = new RelativeLayout.LayoutParams(
                    (int)(rangeseekbarwidth - centerRightX),
                    (int)common.dpToPx(applicationviavideocomposer.getactivity(), 25));
            paramsseekbarleft.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsseekbarleft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            view_seekbarrightnavigation.setLayoutParams(paramsseekbarleft);
        }

        txt_mediatimemin.setText(common.gettimestring(mintimemillis));
        txt_mediatimemax.setText(common.gettimestring(maxtimemillis));
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

    public boolean isneedtotrimmedia()
    {
        if(! ismediatrimmed)
        {
            if(starttrimlength != 0 && endtrimlength != mediaduration)
                return true;
            else
                ismediatrimmed=false;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyout_publish:
                selecteditemclick=1;
                publishitem();
                break;

            case R.id.lyout_send:
                selecteditemclick=2;
                senditem();
                break;

            case R.id.lyout_export:
                selecteditemclick=3;
                exportitem();
                break;

            case R.id.img_cancel:
                getDialog().dismiss();
                break;
        }
    }


    public void publishitem()
    {
        if(isneedtotrimmedia())
        {
            executecutmediacommand(starttrimlength,endtrimlength);
            return;
        }

        if(trimmedmediapath.trim().isEmpty())
            trimmedmediapath=mediafilepath;

        String publish = applicationviavideocomposer.getactivity().getResources().getString(R.string.publish_details1)+"\n"+"\n"+"\n"+
                applicationviavideocomposer.getactivity().getResources().getString(R.string.publish_details2);

        getDialog().dismiss();

        if(xdata.getinstance().getSetting(config.enableplubishnotification).isEmpty() ||
                xdata.getinstance().getSetting(config.enableplubishnotification).equalsIgnoreCase("0"))
        {
            baseactivity.getinstance().share_alert_dialog(applicationviavideocomposer.getactivity(),
                applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_publish), publish, new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {

                    baseactivity.getinstance().videolocksharedialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                            mediatype,ismediatrimmed,mediathumbnailurl,trimmedmediapath,
                            applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_publish));

                }
                @Override
                public void onItemClicked(Object object, int type) {

                }
            });
            return;
        }
        baseactivity.getinstance().videolocksharedialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                mediatype,ismediatrimmed,mediathumbnailurl,trimmedmediapath,
                applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_publish));
        //baseactivity.getinstance().showsharepopupsub(mediafilepath,config.item_video,mediatoken,ismediatrimmed);
    }

    public void senditem()
    {
        if(isneedtotrimmedia())
        {
            executecutmediacommand(starttrimlength,endtrimlength);
            return;
        }

        if(trimmedmediapath.trim().isEmpty())
            trimmedmediapath=mediafilepath;

        String send = getActivity().getResources().getString(R.string.send_details1)+"\n"+"\n"+
                getActivity().getResources().getString(R.string.send_details2);

        getDialog().dismiss();
        if(xdata.getinstance().getSetting(config.enablesendnotification).isEmpty() ||
                xdata.getinstance().getSetting(config.enablesendnotification).equalsIgnoreCase("0")) {
            baseactivity.getinstance().share_alert_dialog(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity().
                    getResources().getString(R.string.txt_send),send ,new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {
                    baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                            mediatype,ismediatrimmed,mediathumbnailurl,trimmedmediapath,applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_send));
                }
                @Override
                public void onItemClicked(Object object, int type) {

                }
            });
            return;
        }
        baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                mediatype,ismediatrimmed,mediathumbnailurl,trimmedmediapath,applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_send));
    }

    public void exportitem()
    {
        if(isneedtotrimmedia())
        {
            executecutmediacommand(starttrimlength,endtrimlength);
            return;
        }

        if(trimmedmediapath.trim().isEmpty())
            trimmedmediapath=mediafilepath;

        String export = applicationviavideocomposer.getactivity().getResources().getString(R.string.export_details1)+"\n"+"\n"+"\n"+
                applicationviavideocomposer.getactivity().getResources().getString(R.string.export_details2);

        getDialog().dismiss();
        if(xdata.getinstance().getSetting(config.enableexportnotification).isEmpty() ||
                xdata.getinstance().getSetting(config.enableexportnotification).equalsIgnoreCase("0")) {
            baseactivity.getinstance().share_alert_dialog(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity().
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

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void cancelaction() {
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
        return common.getScreenWidth(applicationviavideocomposer.getactivity());
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

        int width = common.getScreenWidth(applicationviavideocomposer.getactivity());
        int height = common.getScreenHeight(applicationviavideocomposer.getactivity());

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

    private void executecutmediacommand(long startMs, long endMs)
    {
        File destinationDir = null;
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "";
        String fileExtn = "";
        String filePrefix = "via_media";
        if(mediatype.equalsIgnoreCase(config.type_video))
        {
            fileName = "MEDIA_" + timeStamp + ".mp4";
            fileExtn = ".mp4";
            destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        }
        else if(mediatype.equalsIgnoreCase(config.type_audio))
        {
            fileName = "MEDIA_" + timeStamp + ".m4a";
            fileExtn = ".m4a";
            destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        }
        String filePath = getDestinationPath() + fileName;
        File dest = new File(filePath);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(destinationDir, filePrefix + fileNo + fileExtn);
        }
        String starttime = common.converttimeformat(startMs);
        String endtime = common.converttimeformat((endMs - startMs));
        String[] complexCommand = { "-y", "-i", mediafilepath,"-ss", "" + starttime, "-t", "" + endtime, "-c","copy", filePath};
        execffmpegbinary(complexCommand,dest);
    }

    private String getDestinationPath() {
        File folder = null;
        if(mediatype.equalsIgnoreCase(config.type_video))
            folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        else if(mediatype.equalsIgnoreCase(config.type_audio))
            folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        if (!folder.exists())
            folder.mkdirs();

        String mfinalpath = folder.getPath() + File.separator;
        return mfinalpath;
    }

    private void execffmpegbinary(final String[] command, final File dest) {
        try {
            try {
                applicationviavideocomposer.ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onFailure(String s) {
                        Log.e("Failure with output : ","IN onFailure");
                        Toast.makeText(applicationviavideocomposer.getactivity(),"Failed to trim media. Please retry!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("SUCCESS with output : ","SUCCESS");
                        ismediatrimmed=true;
                        trimmedmediapath=dest.toString();
                        progressdialog.dismisswaitdialog();

                        if(selecteditemclick == 1)
                            publishitem();
                        else if(selecteditemclick == 2)
                            senditem();
                        else if(selecteditemclick == 3)
                            exportitem();

                    }

                    @Override
                    public void onProgress(String s) {
                        Log.e( "Progress bar : " , "In Progress");
                    }

                    @Override
                    public void onStart() {
                        Log.e("Start with output : ","IN START");
                    }

                    @Override
                    public void onFinish() {
                        progressdialog.dismisswaitdialog();
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            // do nothing for now
        }
    }
}
