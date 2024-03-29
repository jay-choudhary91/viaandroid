package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.frameinfo;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.CenteredImageSpan;
import com.deeptruth.app.android.utils.visualizerview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.sha;
import com.deeptruth.app.android.utils.visualizeraudiorecorder;
import com.deeptruth.app.android.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 5/11/18.
 */

public class audiocomposerfragment extends basefragment  implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnTouchListener {

    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
    @BindView(R.id.myvisualizerview)
    visualizeraudiorecorder myvisualizerview;
    @BindView(R.id.layout_drawertouchable)
    RelativeLayout layout_drawertouchable;
    @BindView(R.id.linear_header)
    LinearLayout linearheader;
    @BindView(R.id.img_flash)
    ImageView img_flash;
    @BindView(R.id.layout_no_gps_wifi)
    RotateLayout layout_no_gps_wifi;
    @BindView(R.id.img_scanover)
    ImageView img_scanover;
    @BindView(R.id.txt_section_validating_secondary)
    TextView txt_section_validating_secondary;
    @BindView(R.id.img_warning)
    ImageView img_warning;
    @BindView(R.id.img_gpswifiwarning)
    ImageView img_gpswifiwarning;
    @BindView(R.id.txt_encrypting)
    TextView txt_encrypting;
    @BindView(R.id.img_close)
    ImageView img_close;
    @BindView(R.id.actionbar)
    RelativeLayout actionbar;
    @BindView(R.id.rl_visulizerview)
    RelativeLayout rlvisulizerview;
    @BindView(R.id.img_gps)
    ImageView img_gps;
    @BindView(R.id.img_data)
    ImageView img_data;
    @BindView(R.id.img_network)
    ImageView img_network;
    @BindView(R.id.txt_section_gps)
    TextView txt_section_gps;
    @BindView(R.id.txt_section_data)
    TextView txt_section_data;
    @BindView(R.id.txt_section_gmttime)
    TextView txt_section_gmttime;
    @BindView(R.id.layout_wifi_gps_data)
    LinearLayout layout_wifi_gps_data;

    LinearLayout layout_drawer;
    ImageView handle,img_dotmenu;;
    LinearLayout linearLayout;
    adapteritemclick madapterclick;
    private Handler myHandler;
    private Runnable myRunnable;
    ArrayList<videomodel> metricsitems =new ArrayList<>();
    ArrayList<videomodel> hashesitems =new ArrayList<>();
    private MediaRecorder mediarecorder;
    private String recordedmediafile = null,selectedmetrices="", selectedhashes ="",upgradeapptitle="",upgradeappmessage="";
    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    View rootview;
    boolean isaudiorecording=false,isvisibletouser=false;
    long millisecondtime, starttime, timebuff, updatetime = 0L ;
    Handler timerhandler;
    int hours, seconds, minutes, milliseconds;
    private String keytype =config.prefs_md5,mediakey="";
    JSONArray metadatametricesjson=new JSONArray();
    private long currentframenumber =0,frameduration =15;
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    String hashvalue = "",metrichashvalue = "";
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<dbitemcontainer> dbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> dbmiddleitemcontainer =new ArrayList<>();
    ArrayList<frameinfo> uploadframelist =new ArrayList<>();
    int layoutmediatypeheight = 0,handlerupdatorcounter=0;
    RelativeLayout layoutbottom;
    @BindView(R.id.img_roundblink)
    ImageView img_roundblink;
    Animation blinkanimation;
    Calendar sequencestarttime,sequenceendtime;
    @BindView(R.id.txt_weakgps)
    TextView txt_weakgps;
    @BindView(R.id.txt_no_gps_wifi)
    TextView txt_no_gps_wifi;
    int bufferSize;
    private List<visualizerview> mVisualizerviews = new ArrayList<>();
    visualizerview barvisualizer;
    FrameLayout framecontainer;
    ImageView recordbutton;

    @Override
    public int getlayoutid() {
        return R.layout.fragment_audiocomposer;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootview = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootview);
        gethelper().drawerenabledisable(true);
        gethelper().setdatacomposing(true);
        txt_section_validating_secondary.setVisibility(View.INVISIBLE);

        barvisualizer = (visualizerview)findViewById(R.id.barvisualizer);

        handle = (ImageView) rootview.findViewById(R.id.handle);
        img_dotmenu = (ImageView) rootview.findViewById(R.id.img_dotmenu);
        layout_drawer = (LinearLayout) rootview.findViewById(R.id.layout_drawer);
        linearLayout=rootview.findViewById(R.id.content);
        img_flash.setVisibility(View.GONE);
        timerhandler = new Handler() ;
        layout_drawertouchable.setOnTouchListener(this);
        img_dotmenu.setVisibility(View.VISIBLE);
        myvisualizerview.setVisibility(View.VISIBLE);
        barvisualizer.setVisibility(View.VISIBLE);
        img_dotmenu.setOnClickListener(this);
        img_gpswifiwarning.setOnClickListener(this);
        img_close.setOnClickListener(this);

        keytype=common.checkkey();
        frameduration=common.checkframeduration();

        startnoise();
        txt_title_actionbarcomposer.setText(config.mediarecorderformat);
        actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
      //  layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));

        try {
             bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        linearheader.post(new Runnable() {
            @Override
            public void run() {
               int linearheaderheight = linearheader.getHeight();
                setlayoutmargin(linearheaderheight);
            }
        });

        link(barvisualizer);

        ViewTreeObserver observer = barvisualizer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                barvisualizer.setBaseY(barvisualizer.getHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    barvisualizer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    barvisualizer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });



        return rootview;
    }

    public void changeiconsorientation(float rotateangle)
    {
        if(img_dotmenu != null)
            img_dotmenu.setRotation(rotateangle);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        resettimer();
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            String[] neededpermissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
            List<String> deniedpermissions = new ArrayList<>();
            for (String permission : neededpermissions) {
                if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedpermissions.add(permission);
                }
            }
            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterallpermissionsgranted();
            } else {
                String[] array = new String[deniedpermissions.size()];
                array = deniedpermissions.toArray(array);
                ActivityCompat.requestPermissions(applicationviavideocomposer.getactivity(), array, request_permissions);
            }
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
            if (permissionsallgranted) {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        doafterallpermissionsgranted();
                    }
                };
            } else {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(applicationviavideocomposer.getactivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
                        //gethelper().onBack();
                    }
                };
            }
        }
    }

    private void doafterallpermissionsgranted()
    {
        setmetriceshashesdata();
        startwaverecord();
    }

    public void startwaverecord()
    {
        /*mediarecorder = new MediaRecorder();
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if(getaudiodir() != null)
        {
            mediarecorder.setOutputFile(getaudiodir().getAbsolutePath());
            mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            try {
                mediarecorder.prepare();
                mediarecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onPause() {
        if(mediarecorder != null)
        {
            if (isaudiorecording)
            {
                try
                {
                    isaudiorecording = false;
                    gethelper().setrecordingrunning(false);
                    mediarecorder.stop();
                    mediarecorder.release();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            mediarecorder = null;
            stoptimer();
            resettimer();
            isaudiorecording=false;
            if(myvisualizerview != null)
            {
                myvisualizerview.clear();
                myvisualizerview.setVisibility(View.INVISIBLE);

            }

            if(barvisualizer != null)
                barvisualizer.setVisibility(View.INVISIBLE);


            img_dotmenu.setVisibility(View.VISIBLE);
            txt_title_actionbarcomposer.setText(config.mediarecorderformat);

            try {
                if(common.getstorageaudiorecorddeniedpermissions().isEmpty() && (recordedmediafile != null )
                        && new File(recordedmediafile).exists())
                    common.deletefile(recordedmediafile);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            if(myvisualizerview != null)
            {
                myvisualizerview.clear();
                myvisualizerview.setVisibility(View.INVISIBLE);
            }

            if(barvisualizer != null)
                barvisualizer.setVisibility(View.INVISIBLE);

            stopblinkanimation();
        }

        if(myHandler != null && myHandler != null)
            myHandler.removeCallbacks(myRunnable);
        super.onPause();
    }

    public void startstopaudiorecording()
    {
        if(isaudiorecording)
        {
            if(common.shouldshowupgradepopup(config.mediarecordcount))
            {
                if(upgradeapptitle.contains("UPGRADE"))
                {
                    if(xdata.getinstance().getSetting(config.upgradedialog_mediastop).trim().isEmpty())
                    {
                        xdata.getinstance().saveSetting(config.upgradedialog_mediastop,"1");
                        showvideorecordlengthalert(upgradeapptitle,upgradeappmessage,config.gravitytop);
                    }
                }
                else
                {
                    showvideorecordlengthalert(upgradeapptitle,upgradeappmessage,config.gravitytop);
                }
            }

            stoprecording();
            stopblinkanimation();
            startwaverecord();
        }
        else
        {
            upgradeapptitle=applicationviavideocomposer.getactivity().getResources().getString(R.string.upgrade_app_title);
            upgradeappmessage=applicationviavideocomposer.getactivity().getResources().getString(R.string.thankyou_for_using);


            try {
                startrecording();
                startblinkanimation();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(madapterclick != null)
                madapterclick.onItemClicked(null,1);
            img_dotmenu.setVisibility(View.INVISIBLE);
        }
    }


    public void startblinkanimation()
    {
        img_roundblink.setVisibility(View.VISIBLE);
        blinkanimation = new AlphaAnimation(0.0f, 1.0f);
        blinkanimation.setDuration(200); //You can manage the time of the blink with this parameter
        blinkanimation.setStartOffset(50);
        blinkanimation.setRepeatMode(Animation.REVERSE);
        blinkanimation.setRepeatCount(Animation.INFINITE);
        img_roundblink.startAnimation(blinkanimation);
    }

    public void stopblinkanimation()
    {
        if(blinkanimation != null)
            blinkanimation.cancel();

        img_roundblink.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_dotmenu: {
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);

                break;
            }

            case R.id.img_gpswifiwarning:
                img_gpswifiwarning.setVisibility(View.GONE);
                img_close.setVisibility(View.VISIBLE);
                break;

            case R.id.img_close:
                img_gpswifiwarning.setVisibility(View.VISIBLE);
                img_close.setVisibility(View.GONE);
                break;

        }
    }

    public void starttimer()
    {
        starttime = SystemClock.uptimeMillis();
        timerhandler.postDelayed(runnable, 0);
    }

    public void stoptimer()
    {
        timebuff += millisecondtime;
        timerhandler.removeCallbacks(runnable);
    }

    public void resettimer()
    {
        millisecondtime = 0L ;
        starttime = 0L ;
        timebuff = 0L ;
        updatetime = 0L ;
        seconds = 0 ;
        minutes = 0 ;
        hours = 0 ;
        milliseconds = 0 ;
        //timer.setText("00:00:00");
        txt_title_actionbarcomposer.setText(config.mediarecorderformat);
    }

    public Runnable runnable = new Runnable() {

        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    millisecondtime = SystemClock.uptimeMillis() - starttime;
                    updatetime = timebuff + millisecondtime;
                    seconds = (int) (updatetime / 1000);
                    minutes = seconds / 60;
                    hours = minutes /60;
                    seconds = seconds % 60;
                    milliseconds = (int) (updatetime % 1000);

                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isaudiorecording)
                            {
                                //"" + String.format("%01d", hours) + ":"+
                                txt_title_actionbarcomposer.setText(
                                        "" + String.format("%02d", minutes) + ":"
                                        + String.format("%02d", seconds) + "."
                                        + String.format("%02d", (milliseconds /100)));
                            }

                        }
                    });
                }
            }).start();
            timerhandler.postDelayed(this, 0);
        }
    };

    public void startrecording() throws IOException {

        if (ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mediakey ="";
        dbstartitemcontainer.clear();
        dbmiddleitemcontainer.clear();
        wavevisualizerslist.clear();
        selectedhashes="";
        metadatametricesjson=new JSONArray();
        selectedmetrices="";
        metricsitems.clear();
        hashesitems.clear();
        currentframenumber =0;
        currentframenumber = currentframenumber + frameduration;

        mediarecorder = new MediaRecorder();
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recordedmediafile = getfile().getAbsolutePath();
        mediarecorder.setOutputFile(recordedmediafile);
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if(common.shouldrestrictmedialimit(config.mediarecordcount))
        {
            int length=common.getunpaidvideorecordlength();
            int maxduartion=length * 1000;
            mediarecorder.setMaxDuration(maxduartion);
            mediarecorder.setOnInfoListener(mediainfolistener);
        }

        try {
            mediarecorder.prepare();
            mediarecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        myvisualizerview.setVisibility(View.VISIBLE);
        barvisualizer.setVisibility(View.VISIBLE);
        starttimer();
        isaudiorecording=true;
        gethelper().setrecordingrunning(true);
    }

    MediaRecorder.OnInfoListener mediainfolistener=new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mediarecorder, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                Log.v("AUDIOCAPTURE","Maximum Duration Reached");
                try {

                    upgradeapptitle=applicationviavideocomposer.getactivity().getResources().getString(R.string.limit_reached_title);
                    upgradeappmessage=applicationviavideocomposer.getactivity().getResources().getString(R.string.limit_reached);

                    startstopaudiorecording();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    private void writeAudioDataToFile() {


        final int[] framegap = {0};
        while (isaudiorecording) {
            // gets the voice output from microphone to byte format
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {


                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getfile() {
        String storagedirectory=xdata.getinstance().getSetting(config.selected_folder);
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file=new File(storagedirectory, fileName+".m4a");
        File destinationDir=new File(storagedirectory);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }

    private void stoprecording() {

        try{
            mediarecorder.stop();
            mediarecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }

        mediarecorder = null;
        stoptimer();
        resettimer();
        isaudiorecording=false;

        if(barvisualizer != null)
            barvisualizer.setVisibility(View.INVISIBLE);

        try
        {
            final File destinationfilepath = common.gettempfileforaudiowave();
            Bitmap bitmap=getbitmapfromview(myvisualizerview);
            if(bitmap == null)
                return;

            if (destinationfilepath.exists())
                    destinationfilepath.delete ();

            try {
                FileOutputStream out = new FileOutputStream(destinationfilepath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            try {

                if(dbstartitemcontainer != null && dbstartitemcontainer.size() > 0 &&
                        dbstartitemcontainer.get(0).getItem2().equalsIgnoreCase("audio"))
                {

                    if(dbstartitemcontainer.size() > 0)
                    {
                        dbstartitemcontainer.get(0).setItem15(destinationfilepath.getAbsolutePath());
                        callserviceforinsertintodb();

                        try {
                            String selecteddir=xdata.getinstance().getSetting(config.selected_folder);
                            String selectedfile=selecteddir+File.separator+config.audiotempfile;
                            if(new File(selectedfile).exists())
                                common.delete(new File(selectedfile));

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        img_dotmenu.setVisibility(View.VISIBLE);

                        if(madapterclick != null)
                            madapterclick.onItemClicked(recordedmediafile,2);

                        if(madapterclick != null)
                            madapterclick.onItemClicked(null,4);

                        resetaudioreder();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(myvisualizerview != null)
        {
            myvisualizerview.clear();
            myvisualizerview.setVisibility(View.INVISIBLE);
        }

        /**/
    }

    public Bitmap getbitmapfromview(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm = view.getDrawingCache();
        return bm;
    }

    protected Bitmap getbitmapfromview1(LinearLayout view) {
        Bitmap map;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return map=view.getDrawingCache();
    }

    public static Bitmap getbitmapfromview1(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.RED);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public void callserviceforinsertintodb()
    {
        try {

            insertstartmediainfo();
            Gson gson = new Gson();
            String list1 = gson.toJson(dbstartitemcontainer);
            String list2 = gson.toJson(dbmiddleitemcontainer);
            xdata.getinstance().saveSetting(config.servicedata_liststart,list1);
            xdata.getinstance().saveSetting(config.servicedata_listmiddle,list2);
            xdata.getinstance().saveSetting(config.servicedata_mediapath,recordedmediafile);
            xdata.getinstance().saveSetting(config.servicedata_keytype,keytype);

            Intent intent = new Intent(applicationviavideocomposer.getactivity(), insertmediadataservice.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                applicationviavideocomposer.getactivity().startForegroundService(intent);
            else
                applicationviavideocomposer.getactivity().startService(intent);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Initilize when get 1st frame from recorder
    public void savestartmediainfo()
    {
        try {

            String currenttimewithoffset[] = common.getcurrentdatewithtimezone();
            String devicestartdate = currenttimewithoffset[0];
            String timeoffset = currenttimewithoffset[1];

            if(dbstartitemcontainer.size() == 0)
            {
                dbstartitemcontainer.add(new dbitemcontainer("","audio","Local storage path", mediakey,"","","0","0",
                        config.type_audio_start,devicestartdate,devicestartdate,timeoffset,"","","",
                        xdata.getinstance().getSetting(config.selected_folder)));
                Log.e("startcontainersize"," "+ dbstartitemcontainer.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertstartmediainfo()
    {
        if(recordedmediafile != null)
        {
            String duration = common.getvideotimefromurl(applicationviavideocomposer.getactivity(),recordedmediafile);

            String medianame=common.getfilename(recordedmediafile);
            String[] split=medianame.split("\\.");
            if(split.length > 0)
                medianame=split[0];

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps","30");
            map.put("firsthash", "");
            map.put("hashmethod",keytype);
            map.put("name",medianame);
            map.put("duration",duration);
            map.put("framecounts","");
            map.put("finalhash","");

            Gson gson = new Gson();
            String json = gson.toJson(map);

            String updatecompletedate[] = common.getcurrentdatewithtimezone();
            String completeddate = updatecompletedate[0];

            dbstartitemcontainer.get(0).setItem1(json);
            dbstartitemcontainer.get(0).setItem3(recordedmediafile);
            dbstartitemcontainer.get(0).setItem13(completeddate);

            if(dbstartitemcontainer != null && dbstartitemcontainer.size() > 0)
            {
                databasemanager mdbhelper=null;
                if (mdbhelper == null) {
                    mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                    mdbhelper.createDatabase();
                }

                try {
                    mdbhelper.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mdbhelper.insertstartvideoinfo(dbstartitemcontainer.get(0).getItem1(), dbstartitemcontainer.get(0).getItem2()
                        , dbstartitemcontainer.get(0).getItem3(), dbstartitemcontainer.get(0).getItem4(), dbstartitemcontainer.get(0).getItem5()
                        , dbstartitemcontainer.get(0).getItem6(), dbstartitemcontainer.get(0).getItem7(), dbstartitemcontainer.get(0).getItem8(),
                        dbstartitemcontainer.get(0).getItem9(), dbstartitemcontainer.get(0).getItem10(), dbstartitemcontainer.get(0).getItem11()
                        , dbstartitemcontainer.get(0).getItem12(), dbstartitemcontainer.get(0).getItem13(),"", dbstartitemcontainer.get(0).getItem14()
                        ,"0","sync_pending","","","0","inprogress","","",
                        dbstartitemcontainer.get(0).getItem16(),duration);

                mdbhelper.updateaudiothumbnail(common.getfilename(dbstartitemcontainer.get(0).getItem3()), dbstartitemcontainer.get(0).getItem15());

                try {
                    mdbhelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void updatelistitemnotify(final byte[] arraybytes, final long framenumber, final String message)
    {

        try
        {
            final String keyvalue= "";
            mvideoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));
            hashvalue = keyvalue;

            if(! selectedhashes.trim().isEmpty())
                selectedhashes=selectedhashes+"\n";

            if(isaudiorecording)
            {
                try
                {
                    ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                    getselectedmetrics(mlocalarraylist);

                    selectedhashes =selectedhashes+mvideoframes.get(mvideoframes.size()-1).getframeinfo();
                    JSONArray metricesarray=new JSONArray();
                    if(metadatametricesjson.length() > 0)
                    {
                        try {
                            metricesarray.put(metadatametricesjson.get(metadatametricesjson.length()-1));
                            metrichashvalue = md5.calculatestringtomd5(metricesarray.toString());

                            uploadframelist.add(new frameinfo(""+framenumber,"xxx",keyvalue,keytype,false,mlocalarraylist));
                            savemediaupdate(metricesarray);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                selectedhashes="";
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    // Calling after 1 by 1 frame duration.
    public void savemediaupdate(JSONArray metricesjsonarray)
    {
        JSONArray metricesarray=new JSONArray();
        String currentdate[] = common.getcurrentdatewithtimezone();
        String sequenceno = "",sequencehash = "", metrichash = "" ;

        for(int i = 0; i< uploadframelist.size(); i++)
        {
            try {
                Log.e("framenumber", uploadframelist.get(i).getFramenumber());
                sequenceno = uploadframelist.get(i).getFramenumber();
                sequencehash = uploadframelist.get(i).getHashvalue();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        uploadframelist.clear();

        try {

            if(sequencestarttime == null)
                sequencestarttime = Calendar.getInstance();

            if(sequenceendtime == null)
                sequenceendtime = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SS aa");
            String starttime = sdf.format(sequencestarttime.getTime());
            String endtime = sdf.format(sequenceendtime.getTime());
            String devicedate = common.get24hourformat();
            if(metricesjsonarray != null && metricesjsonarray.length() > 0)
            {
                JSONObject arrayobject=metricesjsonarray.getJSONObject(0);
                arrayobject.put("sequencestarttime",starttime+" "+common.gettimezoneshortname());
                arrayobject.put("sequenceendtime",endtime+" "+common.gettimezoneshortname());
                arrayobject.put("devicetime",devicedate);
                Log.e("devicedate",devicedate);
            }

            metrichash = md5.calculatestringtomd5(metricesjsonarray.toString());
            dbmiddleitemcontainer.add(new dbitemcontainer("", metrichash ,keytype, mediakey,""+metricesjsonarray.toString(),
                    currentdate[0],"0",sequencehash,sequenceno,"",currentdate[0],"",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getkeyvalue(byte[] data)
    {
        String value="";
        String salt="";

        switch (keytype)
        {
            case config.prefs_md5:
                value= md5.calculatebytemd5(data);
                break;

            case config.prefs_md5_salt:
                salt= xdata.getinstance().getSetting(config.prefs_md5_salt);
                if(! salt.trim().isEmpty())
                {
                    byte[] saltbytes=salt.getBytes();
                    try {
                        ByteArrayOutputStream outputstream = new ByteArrayOutputStream( );
                        outputstream.write(saltbytes);
                        outputstream.write(data);
                        byte updatedarray[] = outputstream.toByteArray();
                        value= md5.calculatebytemd5(updatedarray);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    value= md5.calculatebytemd5(data);
                }

                break;
            case config.prefs_sha:
                value= sha.sha1(data);
                break;
            case config.prefs_sha_salt:
                salt= xdata.getinstance().getSetting(config.prefs_sha_salt);
                if(! salt.trim().isEmpty())
                {
                    byte[] saltbytes=salt.getBytes();
                    try {
                        ByteArrayOutputStream outputstream = new ByteArrayOutputStream( );
                        outputstream.write(saltbytes);
                        outputstream.write(data);
                        byte updatedarray[] = outputstream.toByteArray();
                        value= sha.sha1(updatedarray);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    value= sha.sha1(data);
                }
                break;
        }
        return value;
    }

    private void startnoise() {
        myvisualizerview.setVisibility(View.VISIBLE);
        barvisualizer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getselectedmetrics(ArrayList<metricmodel> mlocalarraylist)
    {
        Log.e("getselectedmetrics"," getselectedmetrics");
        JSONArray metricesarray=new JSONArray();
        StringBuilder builder=new StringBuilder();
        JSONObject object=new JSONObject();
        for(int j=0;j<mlocalarraylist.size();j++)
        {
            metricmodel metric=mlocalarraylist.get(j);
            String value=metric.getMetricTrackValue();
            common.setgraphicalitems(metric.getMetricTrackKeyName(),value,true);

            if(metric.getMetricTrackValue().trim().isEmpty() ||
                    metric.getMetricTrackValue().equalsIgnoreCase("null"))
            {
                value="NA";
            }
            builder.append("\n"+metric.getMetricTrackKeyName()+" - "+value);
            //selectedmetrices=selectedmetrices+"\n"+metric.getMetricTrackKeyName()+" - "+value;

            try {
                object.put(metric.getMetricTrackKeyName(),value);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        builder.append("\n");
        metadatametricesjson.put(object);
        selectedmetrices=selectedmetrices+builder.toString();
    }

    public void setmetriceshashesdata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                if(isaudiorecording)
                {
                    handlerupdatorcounter=handlerupdatorcounter+10;
                    if(myvisualizerview != null)
                    {
                        if(mediarecorder != null)
                        {
                         int x = mediarecorder.getMaxAmplitude();
                         double ampletude = 20 * Math.log10(x / 32767.0);
                         int deciblevalue = 50 - Math.abs((int)ampletude);
                         barvisualizer.receive(deciblevalue);

                         myvisualizerview.addAmplitude(x); // update the VisualizeView
                         myvisualizerview.invalidate();
                         wavevisualizerslist.add(new wavevisualizer(x,true));
                        }
                    }

                    if((! selectedmetrices.toString().trim().isEmpty()))
                    {
                        if(metricsitems.size() > 0)
                        {
                            metricsitems.set(0,new videomodel(selectedmetrices));
                        }
                        else
                        {
                            metricsitems.add(new videomodel(selectedmetrices));
                        }
                        selectedmetrices="";
                    }

                    if(handlerupdatorcounter >= 200)
                    {
                        handlerupdatorcounter=0;

                        try {
                            if(isaudiorecording)
                            {
                                if(mediakey.trim().isEmpty())
                                {
                                    sequencestarttime = Calendar.getInstance();
                                    String currenttimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                    mediakey=currenttimestamp;
                                    savestartmediainfo();
                                }
                                else
                                {
                                    sequenceendtime = Calendar.getInstance();
                                }

                                updatelistitemnotify(new byte[2],currentframenumber,"Frame");
                                currentframenumber = currentframenumber + frameduration;
                                sequencestarttime = Calendar.getInstance();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                    getselectedmetrics(mlocalarraylist);
                }

                if(recordbutton != null)
                    recordbutton.setClickable(true);

                common.setgraphicalblockchainvalue(config.blockchainid,"",true);
                common.setgraphicalblockchainvalue(config.hashformula,keytype,true);
                common.setgraphicalblockchainvalue(config.datahash,hashvalue,true);
                common.setgraphicalblockchainvalue(config.matrichash,metrichashvalue,true);
                xdata.getinstance().saveSetting(config.camera, "NA");
                xdata.getinstance().saveSetting(config.picturequality, "NA");

                actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
                layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

                visibleconnection();
                setactionbarbackgroundcolor();
                myHandler.postDelayed(this, 10);
            }
        };
        myHandler.post(myRunnable);
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_menu:
                if(isaudiorecording)
                {
                    stoprecording();
                }
                else
                {
                    gethelper().onBack();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId())
        {
            case  R.id.layout_drawertouchable:
                if(! isaudiorecording)
                {
                    if(madapterclick != null)
                        madapterclick.onItemClicked(motionEvent,3);
                }

                break;
        }
        return true;
    }


    public void setData(adapteritemclick madapterclick,RelativeLayout layoutbottom, int layoutbottomheight ,FrameLayout framecontainer,ImageView recordbutton) {
        this.madapterclick = madapterclick;
        this.layoutbottom = layoutbottom;
        this.layoutmediatypeheight = layoutbottomheight;
        this.framecontainer = framecontainer;
        this.recordbutton = recordbutton;
    }

    @Override
    public void setUserVisibleHint(boolean isvisibletouser) {
        super.setUserVisibleHint(isvisibletouser);
        this.isvisibletouser=isvisibletouser;
        if(isvisibletouser)
        {

        }
    }

    public void resetaudioreder(){

        myvisualizerview.setVisibility(View.VISIBLE);
        barvisualizer.setVisibility(View.INVISIBLE);

    }

    @Override
    public void showhideviewondrawer(boolean isshow) {
        super.showhideviewondrawer(isshow);

        if(layoutbottom == null || linearheader == null || framecontainer == null)
            return;

        if(isshow){
            layoutbottom.setVisibility(View.GONE);
            linearheader.setVisibility(View.GONE);
            framecontainer.setVisibility(View.GONE);
        }else{
            layoutbottom.setVisibility(View.VISIBLE);
            linearheader.setVisibility(View.VISIBLE);
            framecontainer.setVisibility(View.VISIBLE);
        }
    }
    public void visibleconnection(){

        if(xdata.getinstance().getSetting(config.worldclocktime).isEmpty() && xdata.getinstance().getSetting(config.worldclocktime) == null)
        {
            txt_section_gmttime.setText("NA");
            img_network.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));

        }else{
            txt_section_gmttime.setText(config.TEXT_TIME +""+common.getworldclocktime());
            img_network.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.rightcheck));
        }

        if(xdata.getinstance().getSetting(config.Connectionspeed)!=null && !xdata.getinstance().getSetting(config.Connectionspeed).isEmpty()){
            if(xdata.getinstance().getSetting(config.Connectionspeed).equalsIgnoreCase("NA")){
                txt_section_data.setText(config.TEXT_DATA+""+xdata.getinstance().getSetting(config.Connectionspeed));
                img_data.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));
            }else {

                String[] arrayitem=xdata.getinstance().getSetting(config.Connectionspeed).split(" ");
                if(arrayitem.length > 0){
                    double connectionvalue = Double.valueOf(arrayitem[0]);
                    if(connectionvalue!=0.0){
                        txt_section_data.setText(config.TEXT_DATA+""+connectionvalue+"Mb/s");
                        img_data.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.rightcheck));

                    }else{
                        txt_section_data.setText(config.TEXT_DATA+""+connectionvalue+"Mb/s");
                        img_data.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));
                    }
                }
            }
        }else{
            txt_section_data.setText(config.TEXT_DATA+""+"NA");
            img_data.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));
        }

        if(xdata.getinstance().getSetting(config.GPSAccuracy)!= null && (! xdata.getinstance().getSetting(config.GPSAccuracy).isEmpty()))
        {
            if(xdata.getinstance().getSetting(config.GPSAccuracy).equalsIgnoreCase("NA")||
                    xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0"))
            {
                txt_section_gps.setText(config.TEXT_GPS+""+"NA");
                img_gps.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));
            }else{
                String[] arrayitem=xdata.getinstance().getSetting(config.GPSAccuracy).split(" ");
                if(arrayitem.length > 0)
                {
                    double gpsvalue = Double.valueOf(arrayitem[0]);
                    if ((gpsvalue <= 50 && gpsvalue != 0)) {

                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(config.TEXT_GPS)
                                .append(" ", new CenteredImageSpan(applicationviavideocomposer.getactivity(),R.drawable.ic_plusminus),0)
                                .append(gpsvalue+"ft");

                        txt_section_gps.setText(builder);
                        img_gps.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.rightcheck));

                    } else {

                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(config.TEXT_GPS)
                                .append(" ", new CenteredImageSpan(applicationviavideocomposer.getactivity(),R.drawable.ic_plusminus),0)
                                .append(gpsvalue+"ft");

                        txt_section_gps.setText(builder);

                        //  txt_section_gps.setText(config.TEXT_GPS+""+gpsvalue+"ft");
                        img_gps.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.warning_icon));
                    }
                }
            }
        }else{
            txt_section_gps.setText(config.TEXT_GPS+""+"NA");
            img_gps.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));
        }
    }

    public void setactionbarbackgroundcolor() {

        if (!common.isnetworkconnected(applicationviavideocomposer.getactivity()) ||
                xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0") ||
                xdata.getinstance().getSetting(config.Connectionspeed).equalsIgnoreCase("NA") ||
                xdata.getinstance().getSetting(config.GPSAccuracy).equalsIgnoreCase("NA") ||
                xdata.getinstance().getSetting(config.Connectionspeed) == null ||
                xdata.getinstance().getSetting(config.GPSAccuracy) == null) {

            actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
            layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

        } else {
            String[] arrayitemgps = xdata.getinstance().getSetting(config.GPSAccuracy).split(" ");
            String[] arrayitemconnection = xdata.getinstance().getSetting(config.Connectionspeed).split(" ");
            if (arrayitemgps.length > 0 && arrayitemconnection.length > 0) {
                double gpsvalue = 0, connectionvalue = 0;
                if (!arrayitemgps[0].trim().isEmpty())
                    gpsvalue = Double.valueOf(arrayitemgps[0]);

                if (!arrayitemconnection[0].trim().isEmpty())
                    connectionvalue = Double.valueOf(arrayitemconnection[0]);

                if ((gpsvalue >= 50 || gpsvalue == 0) || connectionvalue == 0.0) {
                    layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
                    actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

                } else {
                    layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.greentransparent));
                    actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.greentransparent));
                }
            }
        }
    }

    public void setlayoutmargin(int linearheaderheight){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,(linearheaderheight),0,layoutmediatypeheight);
        rlvisulizerview.setLayoutParams(params);
        rlvisulizerview.requestLayout();
    }

    private int calculateDecibel(byte[] buf) {
        int sum = 0;
        for (int i = 0; i < bufferSize; i++) {
            sum += Math.abs(buf[i]);
        }
        // avg 10-50
        return sum / bufferSize;
    }

    public void link(visualizerview visualizerview) {
        mVisualizerviews.add(visualizerview);
    }

}
