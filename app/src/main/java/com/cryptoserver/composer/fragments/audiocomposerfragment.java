package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.metadata.metadatainsert;
import com.cryptoserver.composer.models.frameinfo;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.customffmpegframegrabber;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.noise;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.randomstring;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.visualizeraudiorecorder;
import com.cryptoserver.composer.utils.xdata;

import org.bytedeco.javacv.Frame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 5/11/18.
 */

public class audiocomposerfragment extends basefragment  implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnTouchListener {

    @BindView(R.id.txt_timer)
    TextView txt_timer;
    @BindView(R.id.img_capture)
    ImageView img_capture;
    @BindView(R.id.myvisualizerview)
    visualizeraudiorecorder myvisualizerview;

    LinearLayout layout_bottom,layout_drawer;
    RecyclerView recyview_hashes;
    RecyclerView recyview_metrices;
    ImageView handleimageview,righthandle,handle;
    LinearLayout linearLayout;
    FrameLayout fragment_graphic_container;
    TextView txtSlot1;
    TextView txtSlot2;
    TextView txtSlot3,txt_metrics,txt_hashes;
    ScrollView scrollview_metrices,scrollview_hashes;
    adapteritemclick madapterclick;
    private boolean isdraweropen=false;
    private Handler myHandler;
    private Runnable myRunnable;
    public int selectedsection=1;
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    videoframeadapter mmetricesadapter,mhashesadapter;
    private String selectedfile = null,selectedmetrices="", selectedhashes ="";;
    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    View rootview;
    boolean isaudiorecording=false;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler timerhandler;
    int Seconds, Minutes, MilliSeconds ;
    public Dialog maindialogshare,subdialogshare;
    private LinearLayoutManager mLayoutManager;
    private graphicalfragment fragmentgraphic;
    private Handler wavehandler;
    private Runnable waverunnable;
    private String keytype =config.prefs_md5;
    JSONArray metadatametricesjson=new JSONArray();
    private long currentframenumber =0,mframetorecordcount=0,frameduration =15,framegap=0;
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
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
        if(rootview == null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            handle = (ImageView) rootview.findViewById(R.id.handle);
            layout_bottom = (LinearLayout) rootview.findViewById(R.id.layout_bottom);
            layout_drawer = (LinearLayout) rootview.findViewById(R.id.layout_drawer);
            txtSlot1 = (TextView) rootview.findViewById(R.id.txt_slot1);
            txtSlot2 = (TextView) rootview.findViewById(R.id.txt_slot2);
            txtSlot3 = (TextView) rootview.findViewById(R.id.txt_slot3);
            txt_metrics = (TextView) rootview.findViewById(R.id.txt_metrics);
            txt_hashes = (TextView) rootview.findViewById(R.id.txt_hashes);
            scrollview_metrices = (ScrollView) rootview.findViewById(R.id.scrollview_metrices);
            scrollview_hashes = (ScrollView) rootview.findViewById(R.id.scrollview_hashes);
            fragment_graphic_container = (FrameLayout) rootview.findViewById(R.id.fragment_graphic_container);
            linearLayout=rootview.findViewById(R.id.content);
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);

            recyview_hashes = (RecyclerView) rootview.findViewById(R.id.recyview_item);
            recyview_metrices = (RecyclerView) rootview.findViewById(R.id.recyview_metrices);

            img_capture.setOnClickListener(this);
            timerhandler = new Handler() ;
            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);

            handleimageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation rightswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.right_slide);
                    linearLayout.startAnimation(rightswipe);
                    handleimageview.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    rightswipe.start();
                    righthandle.setVisibility(View.VISIBLE);
                    rightswipe.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            righthandle.setImageResource(R.drawable.righthandle);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            righthandle.setImageResource(R.drawable.lefthandle);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });

            righthandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation leftswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.left_slide);
                    linearLayout.startAnimation(leftswipe);
                    linearLayout.setVisibility(View.INVISIBLE);
                    righthandle.setVisibility(View.VISIBLE);
                    handleimageview.setVisibility(View.GONE);
                    leftswipe.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            handleimageview.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            txtSlot1.setOnClickListener(this);
            txtSlot2.setOnClickListener(this);
            txtSlot3.setOnClickListener(this);

            resetButtonViews(txtSlot1,txtSlot2,txtSlot3);
            txtSlot1.setVisibility(View.VISIBLE);
            txtSlot2.setVisibility(View.VISIBLE);
            txtSlot3.setVisibility(View.VISIBLE);
            txt_metrics.setVisibility(View.INVISIBLE);
            txt_hashes.setVisibility(View.INVISIBLE);
            recyview_hashes.setVisibility(View.VISIBLE);
            recyview_metrices.setVisibility(View.INVISIBLE);
            scrollview_metrices.setVisibility(View.INVISIBLE);
            scrollview_hashes.setVisibility(View.INVISIBLE);
            fragment_graphic_container.setVisibility(View.INVISIBLE);

            {

                mhashesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mhashesitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyview_hashes.setLayoutManager(mLayoutManager);
                recyview_hashes.setItemAnimator(new DefaultItemAnimator());
                recyview_hashes.setAdapter(mhashesadapter);
            }

            {
                mmetricesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mmetricsitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                mLayoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                recyview_metrices.setLayoutManager(mLayoutManager);
                recyview_metrices.setItemAnimator(new DefaultItemAnimator());
                recyview_metrices.setAdapter(mmetricesadapter);
                implementscrolllistener();
            }

            keytype=common.checkkey();
            frameduration=common.checkframeduration();
            startnoise();
            setmetriceshashesdata();


            /*int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);*/

            try {
                bufferSize = AudioRecord.getMinBufferSize(8000,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        return rootview;
    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3)
    {
        view1.setBackgroundResource(R.color.videolist_background);
        view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.white));

        view2.setBackgroundResource(R.color.white);
        view2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.videolist_background));

        view3.setBackgroundResource(R.color.white);
        view3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.videolist_background));
    }

    // Implement scroll listener
    private void implementscrolllistener() {
        recyview_metrices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
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
        if(fragmentgraphic == null)
        {
            fragmentgraphic  = new graphicalfragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_graphic_container,fragmentgraphic);
            transaction.commit();
        }
    }

    @Override
    public void onPause() {
        if(recorder != null)
        {
            isaudiorecording=false;
            stoptimer();
            resettimer();

            myvisualizerview.clear();
            myvisualizerview.setVisibility(View.INVISIBLE);
            try {
                if(common.getstoragedeniedpermissions().isEmpty() && (selectedfile != null )
                        && new File(selectedfile).exists())
                    common.deletefile(selectedfile);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try{
                recorder.stop();
                recorder.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_capture: {
                if(! isaudiorecording)
                {
                    myvisualizerview.setVisibility(View.VISIBLE);
                    startrecording();
                }
                else
                {
                    stoprecording();
                }
                break;
            }
            case R.id.txt_slot1:
                if(selectedsection != 1)
                {
                    selectedsection=1;
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);

                    recyview_hashes.setVisibility(View.VISIBLE);
                    recyview_metrices.setVisibility(View.INVISIBLE);
                    fragment_graphic_container.setVisibility(View.INVISIBLE);

                    txt_metrics.setVisibility(View.INVISIBLE);
                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);
                    resetButtonViews(txtSlot1,txtSlot2,txtSlot3);
                }

                break;

            case R.id.txt_slot2:
                if(selectedsection != 2)
                {
                    selectedsection=2;
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    fragment_graphic_container.setVisibility(View.INVISIBLE);

                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);

                    recyview_metrices.setVisibility(View.VISIBLE);
                    recyview_hashes.setVisibility(View.INVISIBLE);

                    resetButtonViews(txtSlot2,txtSlot1,txtSlot3);
                }

                break;

            case R.id.txt_slot3:
                if(selectedsection != 3)
                {
                    selectedsection=3;
                    fragment_graphic_container.setVisibility(View.VISIBLE);
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    recyview_metrices.setVisibility(View.INVISIBLE);
                    recyview_hashes.setVisibility(View.INVISIBLE);
                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);
                    resetButtonViews(txtSlot3,txtSlot1,txtSlot2);

                    if(fragmentgraphic != null)
                        fragmentgraphic.setvisualizer();
                }
                break;
        }
    }

    public void starttimer()
    {
        StartTime = SystemClock.uptimeMillis();
        timerhandler.postDelayed(runnable, 0);
    }

    public void stoptimer()
    {
        TimeBuff += MillisecondTime;
        timerhandler.removeCallbacks(runnable);
    }

    public void resettimer()
    {
        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;
        //timer.setText("00:00:00");
        gethelper().updateheader("00:00:00");
    }

    public Runnable runnable = new Runnable() {

        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MillisecondTime = SystemClock.uptimeMillis() - StartTime;
                    UpdateTime = TimeBuff + MillisecondTime;
                    Seconds = (int) (UpdateTime / 1000);
                    Minutes = Seconds / 60;
                    Seconds = Seconds % 60;
                    MilliSeconds = (int) (UpdateTime % 1000);

                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isaudiorecording)
                            {
                                gethelper().updateheader("" + String.format("%02d", Minutes) + ":"
                                        + String.format("%02d", Seconds) + ":"
                                        + String.format("%02d", (MilliSeconds / 10)));
                            }

                        }
                    });
                }
            }).start();
            timerhandler.postDelayed(this, 0);
        }
    };

    private void startrecording() {
        selectedhashes="";
        metadatametricesjson=new JSONArray();
        selectedmetrices="";
        mmetricsitems.clear();
        mmetricesadapter.notifyDataSetChanged();
        mhashesitems.clear();
        mhashesadapter.notifyDataSetChanged();
        currentframenumber =0;
        mframetorecordcount =0;
        currentframenumber = currentframenumber + frameduration;

        try {
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

            int i = recorder.getState();
            if(i==1)
                recorder.startRecording();

            starttimer();
            isaudiorecording=true;

            recordingThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    writeAudioDataToFile();
                }
            },"AudioRecorder Thread");

            recordingThread.start();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void writeAudioDataToFile(){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(isaudiorecording){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);

                        if(framegap == 7)
                        {
                            framegap=0;
                            Log.e("Frame count ",""+mframetorecordcount);
                            if(mframetorecordcount == currentframenumber)
                            {
                                updatelistitemnotify(data,currentframenumber,"Frame");
                                currentframenumber = currentframenumber + frameduration;
                            }
                            mframetorecordcount++;
                        }
                        framegap++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatelistitemnotify(final byte[] array, final long framenumber, final String message)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(array == null || array.length == 0)
                    return;
                final String keyvalue= getkeyvalue(array);

                mvideoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));

                if(! selectedhashes.trim().isEmpty())
                    selectedhashes=selectedhashes+"\n";

                if(isaudiorecording)
                {
                    selectedhashes =selectedhashes+mvideoframes.get(mvideoframes.size()-1).getframeinfo();
                    ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                    getselectedmetrics(mlocalarraylist);

                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(fragmentgraphic != null)
                            {
                                fragmentgraphic.currenthashvalue=keyvalue;
                            }
                        }
                    });
                }
                else
                {
                    selectedhashes="";
                }

            }
        }).start();
    }

    private void stoprecording() {
        // stops the recording activity

        if(null != recorder){
            try {
                isaudiorecording=false;
                recorder.stop();
                recorder.release();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            recorder = null;
            recordingThread = null;

            stoptimer();
            resettimer();
            if(recorder==null){
                myvisualizerview.clear();
                myvisualizerview.setVisibility(View.INVISIBLE);
            }

            mhashesitems.clear();
            mhashesadapter.notifyDataSetChanged();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        selectedfile=getfile().getAbsolutePath();
                        copyWaveFile(getTempFilename(),selectedfile);
                        setaudiohashes();
                      //  metadatainsert.writemetadata(selectedfile,""+common.getjson(metadatametricesjson));
                        common.deletefile(getTempFilename());;
                    }catch (Exception e)
                    {
                        Log.e("Meta data Error","Error");
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        showsharepopupmain();
    }

    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }


    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

           // AppLog.logString("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private File getfile() {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file=new File(config.videodir, fileName+""+AUDIO_RECORDER_FILE_EXT_WAV);

        File destinationDir=new File(config.videodir);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }


    public void setaudiohashes()
    {
        try {

            int count = 1;
            currentframenumber=0;
            currentframenumber = currentframenumber + frameduration;

            customffmpegframegrabber grabber = new customffmpegframegrabber(new File(selectedfile));
            grabber.start();
            videomodel lastframehash=null;
            int totalframes=grabber.getLengthInAudioFrames();
            for(int i = 0; i<grabber.getLengthInAudioFrames(); i++) {
                Frame frame = grabber.grabAudio();
                if (frame == null)
                    break;

                if(isaudiorecording)
                    break;

                ByteBuffer buffer= ((ByteBuffer) frame.samples[0].position(0));
                byte[] byteData = new byte[buffer.remaining()];
                buffer.get(byteData);

                String keyValue= getkeyvalue(byteData);

                if (count == currentframenumber) {
                    lastframehash=null;
                    mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));
                    if(! selectedhashes.trim().isEmpty())
                        selectedhashes=selectedhashes+"\n";

                    selectedhashes=selectedhashes+mvideoframes.get(mvideoframes.size()-1).gettitle()
                            +" "+ mvideoframes.get(mvideoframes.size()-1).getcurrentframenumber()+" "+
                            mvideoframes.get(mvideoframes.size()-1).getkeytype()+":"+" "+
                            mvideoframes.get(mvideoframes.size()-1).getkeyvalue();

                    currentframenumber = currentframenumber + frameduration;
                }
                else
                {
                    lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                }
                count++;
            }

            if(lastframehash != null && (! isaudiorecording))
            {
                selectedhashes=selectedhashes+"\n"+lastframehash.gettitle()+" "+ lastframehash.getcurrentframenumber()+" "+
                        lastframehash.getkeytype()+":"+" "+lastframehash.getkeyvalue();
            }
            grabber.flush();

        }catch (Exception e)
        {
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

        try {
            if (mNoise != null)
                mNoise.stop();

            mNoise = new noise();

            if (mNoise != null)
                mNoise.start();

            try {
                if (mNoise != null)
                {
                    myvisualizerview.setVisibility(View.VISIBLE);
                    getaudiowave();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e)
        {

        }

    }

    public void getaudiowave() {
        wavehandler =new Handler();
        waverunnable = new Runnable() {
            @Override
            public void run() {

                try {

                    if((isaudiorecording))
                    {
                        int x = mNoise.getAmplitudevoice();
                        myvisualizerview.addAmplitude(x); // update the VisualizeView
                        myvisualizerview.invalidate();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                wavehandler.postDelayed(this, 2);
            }
        };
        wavehandler.post(waverunnable);
    }


    private void stopnoise() {
        try {
            if(mNoise != null)
            {
                mNoise.stop();
                //myvisualizerview.updateAmplitude((float) 0,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wavehandler != null && waverunnable != null)
            wavehandler.removeCallbacks(waverunnable);

        try {
            stopnoise();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

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

                boolean graphicopen=false;
                if(isdraweropen)
                {
                    if(selectedsection == 1 && (! selectedhashes.trim().isEmpty()))
                    {
                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mhashesitems.add(new videomodel(selectedhashes));
                                mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                                selectedhashes="";
                            }
                        });
                    }

                    if(mmetricsitems.size() == 0)
                    {
                        ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                        getselectedmetrics(mlocalarraylist);

                        if(mmetricsitems.size() == 0 && (! selectedmetrices.toString().trim().isEmpty()))
                        {
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mmetricsitems.add(new videomodel(selectedmetrices));
                                    mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                                    selectedmetrices="";
                                }
                            });
                        }

                        if((! isaudiorecording) && (! selectedmetrices.toString().trim().isEmpty()))
                        {
                            mmetricsitems.add(new videomodel(selectedmetrices));
                            mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                            selectedmetrices="";
                        }
                    }


                    if((fragment_graphic_container.getVisibility() == View.VISIBLE))
                        graphicopen=true;
                }

                if((fragmentgraphic!= null && mmetricsitems.size() > 0 && selectedsection == 3))
                {
                    fragmentgraphic.setdrawerproperty(graphicopen);
                    fragmentgraphic.setmetricesdata();
                }

                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }


    public void showsharepopupmain()
    {
        if(maindialogshare != null && maindialogshare.isShowing())
            maindialogshare.dismiss();

        maindialogshare=new Dialog(applicationviavideocomposer.getactivity());
        maindialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        maindialogshare.setCanceledOnTouchOutside(true);
        maindialogshare.setContentView(R.layout.popup_sharescreen);
        //maindialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int[] widthHeight=common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int width=widthHeight[0];
        double height=widthHeight[1]/1.6;
        maindialogshare.getWindow().setLayout(width-20, (int)height);
        maindialogshare.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        TextView txt_share_btn1 = (TextView)maindialogshare.findViewById(R.id.txt_share_btn1);
        TextView txt_share_btn2 = (TextView)maindialogshare.findViewById(R.id.txt_share_btn2);
        TextView txt_share_btn3 = (TextView)maindialogshare.findViewById(R.id.txt_share_btn3);
        TextView txt_title1 = (TextView)maindialogshare.findViewById(R.id.txt_title1);
        TextView txt_title2 = (TextView)maindialogshare.findViewById(R.id.txt_title2);
        ImageView img_cancel=maindialogshare.findViewById(R.id.img_cancelicon);

        txt_share_btn1.setText(getResources().getString(R.string.share));
        txt_share_btn2.setText(getResources().getString(R.string.new_audio));
        txt_share_btn3.setText(getResources().getString(R.string.listen));

        txt_title1.setText(getResources().getString(R.string.audio_has_been_encrypted));
        txt_title2.setText(getResources().getString(R.string.congratulations_audio));

        common.changeFocusStyle(txt_share_btn1,getResources().getColor(R.color.share_a),20);
        common.changeFocusStyle(txt_share_btn2,getResources().getColor(R.color.share_b),20);
        common.changeFocusStyle(txt_share_btn3,getResources().getColor(R.color.share_c),20);

        txt_share_btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(maindialogshare != null && maindialogshare.isShowing())
                    maindialogshare.dismiss();

                showsharepopupsub();
            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(maindialogshare != null && maindialogshare.isShowing())
                    maindialogshare.dismiss();
            }
        });

        txt_share_btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(maindialogshare != null && maindialogshare.isShowing())
                    maindialogshare.dismiss();
            }
        });
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maindialogshare != null && maindialogshare.isShowing())
                    maindialogshare.dismiss();

            }
        });
        maindialogshare.show();
    }


    public void showsharepopupsub()
    {
        if(subdialogshare != null && subdialogshare.isShowing())
            subdialogshare.dismiss();

        subdialogshare=new Dialog(applicationviavideocomposer.getactivity());
        subdialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        subdialogshare.setCanceledOnTouchOutside(true);

        subdialogshare.setContentView(R.layout.popup_sharescreen);
        //subdialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int[] widthHeight=common.getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int width=widthHeight[0];
        double height=widthHeight[1]/1.6;
        subdialogshare.getWindow().setLayout(width-20, (int)height);

        TextView txt_share_btn1 = (TextView)subdialogshare.findViewById(R.id.txt_share_btn1);
        TextView txt_share_btn2 = (TextView)subdialogshare.findViewById(R.id.txt_share_btn2);
        TextView txt_share_btn3 = (TextView)subdialogshare.findViewById(R.id.txt_share_btn3);
        TextView txt_title1 = (TextView)subdialogshare.findViewById(R.id.txt_title1);
        TextView txt_title2 = (TextView)subdialogshare.findViewById(R.id.txt_title2);
        ImageView img_cancel= subdialogshare.findViewById(R.id.img_cancelicon);

        txt_share_btn1.setText(getResources().getString(R.string.shave_to_camera));
        txt_share_btn2.setText(getResources().getString(R.string.share_partial_audio));
        txt_share_btn3.setText(getResources().getString(R.string.cancel_viewlist));

        txt_title1.setText(getResources().getString(R.string.audio_how_would_you));
        txt_title2.setText("");

        common.changeFocusStyle(txt_share_btn1,getResources().getColor(R.color.share_a),5);
        common.changeFocusStyle(txt_share_btn2,getResources().getColor(R.color.share_b),5);
        common.changeFocusStyle(txt_share_btn3,getResources().getColor(R.color.share_c),5);

        txt_share_btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();
            }
        });

        txt_share_btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

                launchvideolist();
            }
        });
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();
            }
        });
        subdialogshare.show();
    }

    public void launchvideolist()
    {
        gethelper().onBack();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId())
        {
            case  R.id.handle:
                flingswipe.onTouchEvent(motionEvent);
                break;

            case  R.id.righthandle:
                flingswipe.onTouchEvent(motionEvent);
                break;
        }
        return true;
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener()
    {
        private final int flingactionmindstvac=common.getdrawerswipearea();
        private static final int flingactionmindspdvac = 10;

        @Override
        public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal,
                               float flingActionYcoSpdPsgVal)
        {
            if(fstMsnEvtPsgVal.getX() - lstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Right to Left fling
                swiperighttoleft();
                return false;
            }
            else if (lstMsnEvtPsgVal.getX() - fstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Left to Right fling
                swipelefttoright();
                return false;
            }

            if(fstMsnEvtPsgVal.getY() - lstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Bottom to Top fling

                return false;
            }
            else if (lstMsnEvtPsgVal.getY() - fstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Top to Bottom fling

                return false;
            }
            return false;
        }
    });

    public void swipelefttoright()
    {
        isdraweropen=true;
        Animation rightswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.right_slide);
        linearLayout.startAnimation(rightswipe);
        handleimageview.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        rightswipe.start();
        righthandle.setVisibility(View.VISIBLE);
        rightswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                righthandle.setImageResource(R.drawable.righthandle);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                righthandle.setImageResource(R.drawable.lefthandle);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void swiperighttoleft()
    {
        isdraweropen=false;
        Animation leftswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.left_slide);
        linearLayout.startAnimation(leftswipe);
        linearLayout.setVisibility(View.INVISIBLE);
        righthandle.setVisibility(View.VISIBLE);
        handleimageview.setVisibility(View.GONE);
        leftswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handleimageview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setData(adapteritemclick madapterclick) {
        this.madapterclick = madapterclick;
    }

}
