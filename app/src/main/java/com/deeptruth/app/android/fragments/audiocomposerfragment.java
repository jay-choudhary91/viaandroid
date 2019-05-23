package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.deeptruth.app.android.models.mediacompletiondialogmain;
import com.deeptruth.app.android.models.mediacompletiondialogsub;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.visualizerview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.sha;
import com.deeptruth.app.android.utils.visualizeraudiorecorder;
import com.deeptruth.app.android.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
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

    private TranslateAnimation validationbaranimation;
    LinearLayout layout_drawer;
    ImageView handle,img_dotmenu;;
    LinearLayout linearLayout;
    adapteritemclick madapterclick;
    private Handler myHandler;
    private Runnable myRunnable;
    ArrayList<videomodel> metricsitems =new ArrayList<>();
    ArrayList<videomodel> hashesitems =new ArrayList<>();
    private MediaRecorder mediarecorder;
    private String recordedmediafile = null,selectedmetrices="", selectedhashes ="";;
    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    View rootview;
    boolean isaudiorecording=false,isvisibletouser=false;
    long millisecondtime, starttime, timebuff, updatetime = 0L ;
    Handler timerhandler;
    int hours, seconds, minutes, milliseconds;
    private String keytype =config.prefs_md5,mediakey="";
    JSONArray metadatametricesjson=new JSONArray();
    private long currentframenumber =0,mframetorecordcount=0,frameduration =15;
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    String hashvalue = "",metrichashvalue = "";
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audiorecorder = null;
    private Thread recordingthread = null;
    int bufferelements2rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int bytesperelement = 2; // 2 bytes in 16bit format
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<dbitemcontainer> dbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> dbmiddleitemcontainer =new ArrayList<>();
    ArrayList<frameinfo> uploadframelist =new ArrayList<>();
    mediacompletiondialogmain mediacompletionpopupmain;
    mediacompletiondialogsub mediacompletionpopupsub;
    FragmentManager fragmentmanager;
    adapteritemclick popupclickmain;
    adapteritemclick popupclicksub;
    int layoutmediatypeheight = 0;
    FFmpeg ffmpeg;
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

        loadffmpeglibrary();
        startnoise();
        txt_title_actionbarcomposer.setText(config.mediarecorderformat);
        actionbar.setBackgroundColor(Color.parseColor(common.getactionbarcolor()));

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

        final AlphaAnimation fadeout_animation = new AlphaAnimation(1.0f, 0.0f);
        fadeout_animation.setDuration(2000); //You can manage the time of the blink with this parameter
        //fadeout_animation.setStartOffset(3500);
        fadeout_animation.setRepeatMode(1);

        Animation.AnimationListener fadeoutlistener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        fadeout_animation.setAnimationListener(fadeoutlistener);


        final AlphaAnimation fadein_animation = new AlphaAnimation(0.0f, 1.0f);
        fadein_animation.setDuration(1000); //You can manage the time of the blink with this parameter
        //fadein_animation.setStartOffset(1000);
        fadein_animation.setRepeatMode(1);

        Animation.AnimationListener alphalistener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                txt_section_validating_secondary.startAnimation(fadeout_animation);
                //fadeoutcontrollers();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        fadein_animation.setAnimationListener(alphalistener);

        validationbaranimation = new TranslateAnimation(-common.getScreenHeight(applicationviavideocomposer.getactivity()),
                common.getScreenHeight(applicationviavideocomposer.getactivity())+100 ,0.0f, 0.0f);
        validationbaranimation.setDuration(6000);
        validationbaranimation.setRepeatCount(Animation.INFINITE);
        validationbaranimation.setRepeatMode(ValueAnimator.RESTART);
        img_scanover.startAnimation(validationbaranimation);

        Animation.AnimationListener translatelistener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fadein_animation.setStartOffset(3000);
                txt_section_validating_secondary.startAnimation(fadein_animation);
            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                fadein_animation.setStartOffset(3000);
                txt_section_validating_secondary.startAnimation(fadein_animation);
            }
        };
        validationbaranimation.setAnimationListener(translatelistener);

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
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedpermissions.add(permission);
                }
            }
            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterallpermissionsgranted();
            } else {
                String[] array = new String[deniedpermissions.size()];
                array = deniedpermissions.toArray(array);
                ActivityCompat.requestPermissions(getActivity(), array, request_permissions);
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
                        Toast.makeText(getActivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
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
            if (audiorecorder != null && isaudiorecording)
            {
                try
                {
                    isaudiorecording = false;
                    gethelper().setrecordingrunning(false);
                    audiorecorder.stop();
                    audiorecorder.release();
                    mediarecorder.stop();
                    mediarecorder.release();
                    audiorecorder = null;
                    recordingthread = null;
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

            if(validationbaranimation != null)
            {
                validationbaranimation.cancel();
                validationbaranimation.reset();
            }

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

        if(validationbaranimation != null)
        {
            validationbaranimation.cancel();
            validationbaranimation.reset();
        }

        if(myHandler != null && myHandler != null)
            myHandler.removeCallbacks(myRunnable);
        super.onPause();
    }

    public void startstopaudiorecording()
    {
        if(! isaudiorecording)
        {
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
        else
        {
            stoprecording();
            stopblinkanimation();
            startwaverecord();
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
        mframetorecordcount =0;
        currentframenumber = currentframenumber + frameduration;

        /*try{
            if(mediarecorder != null)
            {
                mediarecorder.stop();
                mediarecorder.release();
            }

        }catch (Exception e){
            e.printStackTrace();
        }*/

        if(validationbaranimation != null)
            img_scanover.startAnimation(validationbaranimation);

        mediarecorder = new MediaRecorder();
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recordedmediafile = getfile().getAbsolutePath();
        Log.d("filename", recordedmediafile);
        mediarecorder.setOutputFile(recordedmediafile);
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediarecorder.prepare();
            mediarecorder.start();

            audiorecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, bufferelements2rec * bytesperelement);

            if (audiorecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e("AudioRecord","AudioRecord init failed");
                return;
            }
            audiorecorder.startRecording();
            recordingthread = new Thread(new Runnable() {
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder Thread");
            recordingthread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myvisualizerview.setVisibility(View.VISIBLE);
        barvisualizer.setVisibility(View.VISIBLE);
        starttimer();
        isaudiorecording=true;
        gethelper().setrecordingrunning(true);
    }

    //Conversion of short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte
        String filePath = gettempfile().getAbsolutePath();
        final short sData[] = new short[bufferelements2rec];


        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final int[] framegap = {0};
        final FileOutputStream finalOs = os;
        new Thread(new Runnable() {
            @Override
              public void run()
              {
               try {
                        while (isaudiorecording) {
                            // gets the voice output from microphone to byte format
                            audiorecorder.read(sData, 0, bufferelements2rec);
                            System.out.println("Short writing to file" + sData.toString());

                        // writes the data to file from buffer stores the voice buffer
                        Log.e("ShortBuffer",Arrays.toString(sData));
                        final byte data[] = short2byte(sData);

                        try {
                            finalOs.write(data, 0, bufferelements2rec * bytesperelement);
                            if(isaudiorecording)
                            {
                                Log.e("Frame count ",""+mframetorecordcount);
                                if(framegap[0] == frameduration || (mediakey.trim().isEmpty()))
                                {
                                    if(mediakey.trim().isEmpty())
                                    {
                                        sequencestarttime = Calendar.getInstance();
                                        String currenttimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                        mediakey=currenttimestamp;
                                        Log.e("localkey ",mediakey);
                                        String keyvalue= getkeyvalue(data);
                                        savestartmediainfo();
                                    }
                                    else
                                    {
                                        sequenceendtime = Calendar.getInstance();
                                    }

                                    framegap[0] =0;
                                    updatelistitemnotify(data,currentframenumber,"Frame");
                                    currentframenumber = currentframenumber + frameduration;
                                    sequencestarttime = Calendar.getInstance();
                                }
                                framegap[0]++;
                                mframetorecordcount++;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
         }).start();


        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    private File getaudiodir() {

        String storagedirectory=config.dirallmedia;
        String fileName = "audiotemp";
        File file=new File(storagedirectory, fileName+".m4a");
        File destinationDir=new File(storagedirectory);
        try {

            if (! destinationDir.exists())
                destinationDir.mkdirs();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }

    private File gettempfile() {
        String storagedirectory=xdata.getinstance().getSetting(config.selected_folder);
        String fileName = config.audiotempfile;
        File file=new File(storagedirectory, fileName);

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

    public void loadffmpeglibrary()
    {
        if (ffmpeg == null) {
            Log.d("ffmpeg", "ffmpeg : is loading..");

            ffmpeg = FFmpeg.getInstance(applicationviavideocomposer.getactivity());
        }
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg", "ffmpeg : loaded..");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void stoprecording() {

        try{
            mediarecorder.stop();
            mediarecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (audiorecorder != null)
        {
            try
            {
                isaudiorecording = false;
                gethelper().setrecordingrunning(false);
                audiorecorder.stop();
                audiorecorder.release();
                audiorecorder = null;
                recordingthread = null;
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

        if(validationbaranimation != null)
        {
            validationbaranimation.cancel();
            validationbaranimation.reset();
        }

        try {

            if(dbstartitemcontainer != null && dbstartitemcontainer.size() > 0 &&
                    dbstartitemcontainer.get(0).getItem2().equalsIgnoreCase("audio"))
            {

                final File destinationfilepath = common.gettempfileforaudiowave();
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(recordedmediafile));

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(applicationviavideocomposer.getactivity(),uri);
                long mediatotalduration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                String starttime = common.converttimeformate(0);
                String endtime = common.converttimeformate(mediatotalduration);

                /*final String[] command = { "-ss", starttime,"-i", recordedmediafile, "-to",endtime, "-filter_complex",
                        "compand=gain=-20,showwavespic=s=640x120:colors=#0076a6", "-frames:v","1",destinationfilepath.getAbsolutePath()};
*/
                final String[] command = { "-ss", starttime,"-i", recordedmediafile, "-to",endtime, "-filter_complex",
                        "compand=gain=-10,showwavespic=s=400x400:colors=#0076a6", "-frames:v","1",destinationfilepath.getAbsolutePath()};

                ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onFailure(String s) {
                        Log.e("Failure with output : ","IN onFailure");
                        callserviceforinsertintodb();
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("SUCCESS with output : ","onSuccess");
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
                            if(madapterclick != null)
                                madapterclick.onItemClicked(recordedmediafile,2);
                            img_dotmenu.setVisibility(View.VISIBLE);

                            if(madapterclick != null)
                                madapterclick.onItemClicked(null,4);

                            resetaudioreder();
                        }
                    }

                    @Override
                    public void onProgress(String s) {
                        Log.e( "audiothumbnail : " , "audiothumbnail onProgress");

                    }

                    @Override
                    public void onStart() {
                        Log.e("Start with output : ","IN onStart");
                    }

                    @Override
                    public void onFinish() {
                        Log.e("Start with output : ","IN onFinish");
                    }
                });
            }

        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    public void callserviceforinsertintodb()
    {
        insertstartmediainfo();

        Gson gson = new Gson();
        String list1 = gson.toJson(dbstartitemcontainer);
        String list2 = gson.toJson(dbmiddleitemcontainer);
        xdata.getinstance().saveSetting("liststart",list1);
        xdata.getinstance().saveSetting("listmiddle",list2);
        xdata.getinstance().saveSetting("mediapath",recordedmediafile);
        xdata.getinstance().saveSetting("keytype",keytype);

        Intent intent = new Intent(applicationviavideocomposer.getactivity(), insertmediadataservice.class);
        applicationviavideocomposer.getactivity().startService(intent);
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
            String duration = common.getvideotimefromurl(getActivity(),recordedmediafile);

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

    public void updatelistitemnotify(final byte[] array, final long framenumber, final String message)
    {

        if(array == null || array.length == 0)
            return;
        final String keyvalue= getkeyvalue(array);

        mvideoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));

        hashvalue = keyvalue;

        if(! selectedhashes.trim().isEmpty())
            selectedhashes=selectedhashes+"\n";

        if(isaudiorecording)
        {
            selectedhashes =selectedhashes+mvideoframes.get(mvideoframes.size()-1).getframeinfo();
            ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
            getselectedmetrics(mlocalarraylist);
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
        }
        else
        {
            selectedhashes="";
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
                arrayobject.put("sequencestarttime",starttime);
                arrayobject.put("sequenceendtime",endtime);
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

    public void setaudiohashes()
    {

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
                }

                if(! isaudiorecording)
                {
                    ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                    getselectedmetrics(mlocalarraylist);
                }

                common.setgraphicalblockchainvalue(config.blockchainid,"",true);
                common.setgraphicalblockchainvalue(config.hashformula,keytype,true);
                common.setgraphicalblockchainvalue(config.datahash,hashvalue,true);
                common.setgraphicalblockchainvalue(config.matrichash,metrichashvalue,true);

                try {
                    if(gethelper().isdraweropened())
                    {
                        layout_no_gps_wifi.setVisibility(View.GONE);
                        myHandler.postDelayed(this, 10);
                        return;
                    }
//xdata.getinstance().getSetting("wificonnected").equalsIgnoreCase("0")
                    if(! common.isnetworkconnected(getActivity()) ||
                            xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0"))
                    {
                        try {

                            GradientDrawable gradient=common.getyelloradargradient();
                            if(gradient != null)
                                img_scanover.setBackground(gradient);
                                visibleconnection();
                            if(img_close.getVisibility() != View.VISIBLE)
                            {
                                img_gpswifiwarning.setVisibility(View.VISIBLE);
                                txt_encrypting.setVisibility(View.VISIBLE);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        visiblewarningcontrollers();
                    }
                    else
                    {
                        GradientDrawable gradient=common.getblueradargradient();
                        if(gradient != null)
                            img_scanover.setBackground(gradient);

                        img_gpswifiwarning.setVisibility(View.GONE);
                        txt_encrypting.setVisibility(View.GONE);
                        img_close.setVisibility(View.GONE);
                        txt_no_gps_wifi.setVisibility(View.GONE);
                        validatingcontrollers();
                        checkgpsaccuracy();
                    }

                    if(isaudiorecording)
                    {
                        if(img_close.getVisibility() == View.VISIBLE)
                        {
                            txt_encrypting.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            txt_encrypting.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        img_gpswifiwarning.setVisibility(View.GONE);
                        txt_encrypting.setVisibility(View.GONE);
                        img_close.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                myHandler.postDelayed(this, 10);
            }
        };
        myHandler.post(myRunnable);
    }

    public void visiblewarningcontrollers(){

        if(isaudiorecording)
        {
            if(layout_no_gps_wifi != null)
                layout_no_gps_wifi.setVisibility(View.VISIBLE);
        }
        else
        {
            layout_no_gps_wifi.setVisibility(View.GONE);
        }
    }


    public void validatingcontrollers(){

        if(isaudiorecording)
        {
            hidewarningsection();
            layout_no_gps_wifi.setVisibility(View.VISIBLE);
        }
        else
        {
            hidewarningsection();
            layout_no_gps_wifi.setVisibility(View.GONE);
        }

    }

    public void hidewarningsection()
    {
        if(layout_no_gps_wifi != null)
            layout_no_gps_wifi.setVisibility(View.GONE);
    }


    public void showsharepopupmain()
    {
        fragmentmanager = getActivity().getSupportFragmentManager();
        popupclickmain=new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                int i= (int) object;
                if(i==1){
                    Log.e("popup","popup");
                    showsharepopupsub();
                }
                else if(i==2){
                    Log.e("popup 2","popup 2");

                }
                else if(i==3){
                    xdata.getinstance().saveSetting("selectedaudiourl",""+ recordedmediafile);
                    audioreaderfragment audiotabfrag = new audioreaderfragment();
                    gethelper().addFragment(audiotabfrag, false, true);
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        };

        mediacompletionpopupmain=new mediacompletiondialogmain(popupclickmain,getResources().getString(R.string.share),getResources().getString(R.string.new_audio),getResources().getString(R.string.listen),getResources().getString(R.string.audio_has_been_encrypted),getResources().getString(R.string.congratulations_audio));
        mediacompletionpopupmain.show(fragmentmanager, "fragment_name");

    }


    public void showsharepopupsub()
    {

        fragmentmanager = getActivity().getSupportFragmentManager();
        popupclicksub=new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                int i= (int) object;
                if(i==4){
                    progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            common.exportaudio(new File(recordedmediafile),true);
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog.dismisswaitdialog();
                                    launchmedialist();
                                }
                            });
                        }
                    }).start();
                }
                else if(i==5){

                }
                else if(i==6){
                    launchmedialist();
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        };
        mediacompletionpopupsub=new mediacompletiondialogsub(popupclicksub,getResources().getString(R.string.save_to_camera),getResources().getString(R.string.share_partial_audio),getResources().getString(R.string.cancel_viewlist),getResources().getString(R.string.audio_how_would_you),"");
        mediacompletionpopupsub.show(fragmentmanager, "fragment_name");
    }

    public void launchmedialist()
    {
        gethelper().onBack();
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


    public void setData(adapteritemclick madapterclick,RelativeLayout layoutbottom, int layoutbottomheight) {
        this.madapterclick = madapterclick;
        this.layoutbottom = layoutbottom;
        this.layoutmediatypeheight = layoutbottomheight;
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

    public static Fragment newInstance() {
        return new audiocomposerfragment();
    }

    @Override
    public void showhideviewondrawer(boolean isshow) {
        super.showhideviewondrawer(isshow);

        if(isshow){
            layoutbottom.setVisibility(View.GONE);
            linearheader.setVisibility(View.GONE);
        }else{
            layoutbottom.setVisibility(View.VISIBLE);
            linearheader.setVisibility(View.VISIBLE);
        }
    }

    public void visibleconnection(){
        if(isaudiorecording) {
            String value = "";
            if(! common.isnetworkconnected(getActivity()))
            {
                value=getResources().getString(R.string.no_internet);
                if(xdata.getinstance().getSetting(config.CellProvider).isEmpty()
                        || xdata.getinstance().getSetting(config.CellProvider).equalsIgnoreCase("NA")
                        ||xdata.getinstance().getSetting(config.CellProvider).equalsIgnoreCase("null")
                        || xdata.getinstance().getSetting(config.airplanemode).equals("ON"))
                {
                    value=getResources().getString(R.string.no_network);
                }
            }

            if(xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0"))
            {

                if(value.isEmpty())
                {
                    value=getResources().getString(R.string.no_gps);
                }
                else
                {
                    value=value.concat(" & ") +getResources().getString(R.string.no_gps);
                }
                txt_weakgps.setText("");
            }
            else
            {
                checkgpsaccuracy();
            }
            txt_no_gps_wifi.setVisibility(View.VISIBLE);
            txt_no_gps_wifi.setText(value);
        }
    }
    public void checkgpsaccuracy(){
        if(isaudiorecording) {
            if(xdata.getinstance().getSetting(config.GPSAccuracy)!= null && (! xdata.getinstance().getSetting(config.GPSAccuracy).isEmpty())
                    && (! xdata.getinstance().getSetting(config.GPSAccuracy).equalsIgnoreCase("NA")))
            {
                String[] arrayitem=xdata.getinstance().getSetting(config.GPSAccuracy).split(" ");
                if(arrayitem.length > 0)
                {
                    Double  gpsvalue = Double.valueOf(xdata.getinstance().getSetting(config.GPSAccuracy));
                    if(xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0")){
                        txt_weakgps.setVisibility(View.GONE);
                    }else {
                        if ((gpsvalue <= 50 && gpsvalue != 0)) {
                            txt_weakgps.setVisibility(View.VISIBLE);
                            txt_weakgps.setText(getResources().getString(R.string.weak_gps));
                        } else {
                            txt_weakgps.setVisibility(View.GONE);
                        }
                    }
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

    private void runRecording(final byte buf[]) {
        //final byte buf[] = new byte[bufferSize];
                // stop recording
                if (!isaudiorecording) {
                    return;
                }
                //audiorecorder.read(buf, 0, bufferelements2rec);
                Log.e("dataArray", Arrays.toString(buf));
                final int decibel = calculateDecibel(buf);

                Log.e("dataArray", Arrays.toString(buf));

                int mVisualizerViewssize = mVisualizerviews.size();
                if (mVisualizerviews != null && !mVisualizerviews.isEmpty()) {
                    for (int i = 0; i < mVisualizerviews.size(); i++) {
                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             mVisualizerviews.get(finalI).receive(decibel);
                         }
                     });
                    }
                }
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
