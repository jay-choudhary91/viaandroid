package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.frameinfo;
import com.deeptruth.app.android.models.mediacompletiondialogmain;
import com.deeptruth.app.android.models.mediacompletiondialogsub;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.noise;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.sha;
import com.deeptruth.app.android.utils.visualizeraudiorecorder;
import com.deeptruth.app.android.utils.xdata;
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
    @BindView(R.id.img_warning)
    ImageView img_warning;
    @BindView(R.id.img_close)
    ImageView img_close;
    @BindView(R.id.myvisualizerview)
    visualizeraudiorecorder myvisualizerview;
    @BindView(R.id.layout_drawertouchable)
    RelativeLayout layout_drawertouchable;
    @BindView(R.id.linear_header)
    LinearLayout linearheader;
    @BindView(R.id.img_flash)
    ImageView img_flash;

    LinearLayout layout_drawer;
    RecyclerView recyview_hashes;
    RecyclerView recyview_metrices;
    ImageView handle,img_dotmenu;;
    LinearLayout linearLayout;

    ScrollView scrollview_metrices,scrollview_hashes;
    adapteritemclick madapterclick;
    private boolean isdraweropen=false;
    private Handler myHandler;
    private Runnable myRunnable;
    public int selectedsection=1;
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private MediaRecorder mrecorder;
    private String recordedmediafile = null,selectedmetrices="", selectedhashes ="";;
    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    View rootview;
    boolean isaudiorecording=false,isvisibletouser=false;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler timerhandler;
    int Seconds, Minutes, MilliSeconds ;
    public Dialog maindialogshare,subdialogshare;
    private LinearLayoutManager mLayoutManager;
    private Handler wavehandler;
    private Runnable waverunnable;
    private String keytype =config.prefs_md5,mediakey="";
    JSONArray metadatametricesjson=new JSONArray();
    private long currentframenumber =0,mframetorecordcount=0,frameduration =15;
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    String hashvalue = "",metrichashvalue = "";
    noise mNoise;

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audiorecorder = null;
    private Thread recordingThread = null;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public int flingactionmindstvac;
    private  final int flingactionmindspdvac = 10;
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<dbitemcontainer> mdbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> mdbmiddleitemcontainer =new ArrayList<>();
    ArrayList<frameinfo> muploadframelist =new ArrayList<>();

    mediacompletiondialogmain mediacompletionpopupmain;
    mediacompletiondialogsub mediacompletionpopupsub;
    FragmentManager fm ;
    adapteritemclick popupclickmain;
    adapteritemclick popupclicksub;


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
            gethelper().drawerenabledisable(true);

            handle = (ImageView) rootview.findViewById(R.id.handle);
            img_dotmenu = (ImageView) rootview.findViewById(R.id.img_dotmenu);
            layout_drawer = (LinearLayout) rootview.findViewById(R.id.layout_drawer);
            linearLayout=rootview.findViewById(R.id.content);
            img_flash.setVisibility(View.GONE);
            timerhandler = new Handler() ;
            layout_drawertouchable.setOnTouchListener(this);
            img_dotmenu.setVisibility(View.VISIBLE);

            img_warning.setOnClickListener(this);
            img_close.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);

            keytype=common.checkkey();
            frameduration=common.checkframeduration();

            startnoise();
            txt_title_actionbarcomposer.setText("deeptruth");
            setmetriceshashesdata();
            try {
                int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                        RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return rootview;
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
    }

    @Override
    public void onPause() {
        if(mrecorder != null)
        {
            isaudiorecording=false;
            gethelper().setrecordingrunning(true);
            stoptimer();
            resettimer();
            img_dotmenu.setVisibility(View.VISIBLE);
            txt_title_actionbarcomposer.setText("deeptruth");

            try {
                if(common.getstoragedeniedpermissions().isEmpty() && (recordedmediafile != null )
                        && new File(recordedmediafile).exists())
                    common.deletefile(recordedmediafile);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try{
                mrecorder.stop();
                mrecorder.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    public void startstopaudiorecording()
    {
        if(! isaudiorecording)
        {
            try {
                startrecording();
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
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_dotmenu: {
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);

                break;
            }

            case R.id.img_warning:

                img_warning.setVisibility(View.GONE);
                img_close.setVisibility(View.VISIBLE);

                if(madapterclick != null)
                    madapterclick.onItemClicked(null,5);
                break;

            case R.id.img_close:
                img_warning.setVisibility(View.VISIBLE);
                img_close.setVisibility(View.GONE);

                if(madapterclick != null)
                    madapterclick.onItemClicked(null,6);
                break;

        }
    }

    public void showwarningorclosebutton()
    {
        if(img_warning == null || img_close == null)
            return;

        if(img_warning.getVisibility() == View.VISIBLE || img_close.getVisibility() == View.VISIBLE)
        {

        }
        else
        {
            img_warning.setVisibility(View.GONE);
            img_close.setVisibility(View.VISIBLE);
        }
    }

    public void hideallsection() {
        if (img_warning == null || img_close == null)
            return;

        img_warning.setVisibility(View.GONE);
        img_close.setVisibility(View.GONE);
    }

    public void showwarningsection(boolean showwarningsection)
    {
        if(img_warning == null || img_close == null)
            return;

        if(showwarningsection)
        {
            img_warning.setVisibility(View.GONE);
            img_close.setVisibility(View.VISIBLE);
        }
        else
        {
            img_warning.setVisibility(View.VISIBLE);
            img_close.setVisibility(View.GONE);
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
        txt_title_actionbarcomposer.setText("deeptruth");
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
                                txt_title_actionbarcomposer.setText("" + String.format("%02d", Minutes) + ":"
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

    public void startrecording() throws IOException {

        mediakey ="";
        mdbstartitemcontainer.clear();
        mdbmiddleitemcontainer.clear();
        wavevisualizerslist.clear();
        selectedhashes="";
        metadatametricesjson=new JSONArray();
        selectedmetrices="";
        mmetricsitems.clear();
        mhashesitems.clear();
        currentframenumber =0;
        mframetorecordcount =0;
        currentframenumber = currentframenumber + frameduration;
        myvisualizerview.setVisibility(View.VISIBLE);

        mrecorder = new MediaRecorder();
        mrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recordedmediafile = getfile().getAbsolutePath();
        Log.d("filename", recordedmediafile);
        mrecorder.setOutputFile(recordedmediafile);
        mrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mrecorder.prepare();
            mrecorder.start();

            audiorecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

            audiorecorder.startRecording();
            recordingThread = new Thread(new Runnable() {
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder Thread");
            recordingThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int framegap=0;
        while (isaudiorecording) {
            // gets the voice output from microphone to byte format
            audiorecorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short writing to file" + sData.toString());
            try {
                // writes the data to file from buffer stores the voice buffer
                byte data[] = short2byte(sData);
                os.write(data, 0, BufferElements2Rec * BytesPerElement);

                if(isaudiorecording)
                {
                    Log.e("Frame count ",""+mframetorecordcount);
                    if(framegap == frameduration || (mediakey.trim().isEmpty()))
                    {
                        if(mediakey.trim().isEmpty())
                        {
                            String currenttimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            //randomstring gen = new randomstring(20, ThreadLocalRandom.current());
                            mediakey=currenttimestamp;
                            Log.e("localkey ",mediakey);
                            String keyvalue= getkeyvalue(data);
                            savestartmediainfo(keyvalue);
                        }

                        framegap=0;
                        updatelistitemnotify(data,currentframenumber,"Frame");
                        currentframenumber = currentframenumber + frameduration;
                    }
                    framegap++;
                    mframetorecordcount++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initilize when get 1st frame from recorder
    public void savestartmediainfo(String firsthash)
    {
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps","30");
            map.put("firsthash", firsthash);
            map.put("hashmethod",keytype);
            map.put("name","");
            map.put("duration","");
            map.put("frmaecounts","");
            map.put("finalhash","");

            Gson gson = new Gson();
            String json = gson.toJson(map);

            //common.getCurrentDate();
            String currenttimewithoffset[] = common.getcurrentdatewithtimezone();
            String devicestartdate = currenttimewithoffset[0];
            String timeoffset = currenttimewithoffset[1];

            if(mdbstartitemcontainer.size() == 0)
            {
                mdbstartitemcontainer.add(new dbitemcontainer(json,"audio","Local storage path", mediakey,"","","0","0",
                        config.type_audio_start,devicestartdate,devicestartdate,timeoffset,""));
                Log.e("startcontainersize"," "+mdbstartitemcontainer.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getfile() {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file=new File(config.videodir, fileName+".m4a");

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

    private File gettempfile() {
        String fileName = "audiotemp.pcm";
        File file=new File(config.videodir, fileName);

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

    private void stoprecording() {

        try{
            mrecorder.stop();
            mrecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (null != audiorecorder) {
            try {
                isaudiorecording = false;
                gethelper().setrecordingrunning(false);
                audiorecorder.stop();
                audiorecorder.release();
                audiorecorder = null;
                recordingThread = null;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        mrecorder = null;
        stoptimer();
        resettimer();
        isaudiorecording=false;
        if(mrecorder==null){
            myvisualizerview.clear();
            myvisualizerview.setVisibility(View.INVISIBLE);
        }

        try {
            Gson gson = new Gson();
            String list1 = gson.toJson(mdbstartitemcontainer);
            String list2 = gson.toJson(mdbmiddleitemcontainer);
            xdata.getinstance().saveSetting("liststart",list1);
            xdata.getinstance().saveSetting("listmiddle",list2);
            xdata.getinstance().saveSetting("mediapath",recordedmediafile);
            xdata.getinstance().saveSetting("keytype",keytype);

            Intent intent = new Intent(applicationviavideocomposer.getactivity(), insertmediadataservice.class);
            applicationviavideocomposer.getactivity().startService(intent);

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

                    muploadframelist.add(new frameinfo(""+framenumber,"xxx",keyvalue,keytype,false,mlocalarraylist));
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

        for(int i=0;i<muploadframelist.size();i++)
        {
            try {
                Log.e("framenumber", muploadframelist.get(i).getFramenumber());
                sequenceno = muploadframelist.get(i).getFramenumber();
                sequencehash = muploadframelist.get(i).getHashvalue();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        muploadframelist.clear();

        try {
            metrichash = md5.calculatestringtomd5(metricesjsonarray.toString());
            mdbmiddleitemcontainer.add(new dbitemcontainer("", metrichash ,keytype, mediakey,""+metricesjsonarray.toString(),
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
        getaudiowave();
    }

    public void getaudiowave() {
        wavehandler =new Handler();
        waverunnable = new Runnable() {
            @Override
            public void run() {

                try {

                    if((isaudiorecording))
                    {
                        int x = mrecorder.getMaxAmplitude();

                        myvisualizerview.addAmplitude(x); // update the VisualizeView
                        myvisualizerview.invalidate();

                        wavevisualizerslist.add(new wavevisualizer(x,true));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                wavehandler.postDelayed(this, 2);
            }
        };
        wavehandler.post(waverunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myHandler != null)
            myHandler.removeCallbacks(myRunnable);
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
                if(isaudiorecording)
                {

                    if((! selectedmetrices.toString().trim().isEmpty()))
                    {
                        if(mmetricsitems.size() > 0)
                        {
                            mmetricsitems.set(0,new videomodel(selectedmetrices));
                        }
                        else
                        {
                            mmetricsitems.add(new videomodel(selectedmetrices));
                        }
                        selectedmetrices="";
                    }

                  /*  if((! isaudiorecording) && (! selectedmetrices.toString().trim().isEmpty()))
                    {
                        mmetricsitems.add(new videomodel(selectedmetrices));
                        selectedmetrices="";
                    }*/

                }

                common.setgraphicalblockchainvalue(config.blockchainid,"",true);
                common.setgraphicalblockchainvalue(config.hashformula,keytype,true);
                common.setgraphicalblockchainvalue(config.datahash,hashvalue,true);
                common.setgraphicalblockchainvalue(config.matrichash,metrichashvalue,true);




                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }



    public void showsharepopupmain()
    {
        fm = getActivity().getSupportFragmentManager();
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
                    if(maindialogshare != null && maindialogshare.isShowing())
                        maindialogshare.dismiss();

                    xdata.getinstance().saveSetting("selectedaudiourl",""+ recordedmediafile);
                    audioreaderfragment audiotabfrag = new audioreaderfragment();
                    //  audiotabfrag.setdata(videoobj.getPath());
                    gethelper().addFragment(audiotabfrag, false, true);
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        };

        mediacompletionpopupmain=new mediacompletiondialogmain(popupclickmain,getResources().getString(R.string.share),getResources().getString(R.string.new_audio),getResources().getString(R.string.listen),getResources().getString(R.string.audio_has_been_encrypted),getResources().getString(R.string.congratulations_audio));
        mediacompletionpopupmain.show(fm, "fragment_name");

    }


    public void showsharepopupsub()
    {

        fm = getActivity().getSupportFragmentManager();
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
        mediacompletionpopupsub.show(fm, "fragment_name");
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
                if(madapterclick != null)
                    madapterclick.onItemClicked(motionEvent,3);
                break;
        }
        return true;
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal,
                               float flingActionYcoSpdPsgVal)
        {
            if(fstMsnEvtPsgVal.getX() - lstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Right to Left fling
                return false;
            }
            else if (lstMsnEvtPsgVal.getX() - fstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Left to Right fling
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



    public void setData(adapteritemclick madapterclick) {
        this.madapterclick = madapterclick;
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

    }

    public static Fragment newInstance() {
        return new audiocomposerfragment();
    }
}
