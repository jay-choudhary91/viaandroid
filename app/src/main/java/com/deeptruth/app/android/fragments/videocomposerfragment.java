package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.frameinfo;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.permissions;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.CenteredImageSpan;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.camerautil;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.sha;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.google.gson.Gson;


import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.RelativeLayout.TRUE;

public class videocomposerfragment extends basefragment implements View.OnClickListener,View.OnTouchListener, Orientation.Listener {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "videocomposerfragment";
    private boolean showwarningsection=true;
    public int rotationangle=0;
    protected float fingerspacing = 0;
    protected float zoomLevel = 1f;
    protected float maximumzoomlevel;
    protected Rect zoom;
    boolean firsthashvalue = true;
    Double gpsvalue = 0.0;
    FragmentManager fm ;
    Calendar sequencestarttime,sequenceendtime;
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";
    private String[] transparentarray=common.gettransparencyvalues();

    private String cameraId = CAMERA_BACK;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    boolean upsideDown = false,isvisibletouser=false;;

    String firsthash = "";

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView textureview;
    /**
     * A refernce to the opened {@link android.hardware.camera2.CameraDevice}.
     */
    private CameraDevice cameraDevice;

    /**
     * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for preview.
     */
    private CameraCaptureSession previewsession;


    private CameraCharacteristics characteristics;
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private TextureView.SurfaceTextureListener surfacetexturelistener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            Log.e("Surface  ","1");
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            Log.e("Surface  ","2");
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.e("Surface  ","3");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if(isvideorecording)
            {
                if(textureview == null)
                    return;

              Thread thread =new Thread(new Runnable() {
                  @Override
                  public void run() {

                      try {
                          if(isvideorecording)
                          {
                              if(mframetorecordcount == currentframenumber || (mediakey.trim().isEmpty()))
                              {
                                  /*Bitmap bitmap = textureview.getBitmap(10,10);
                                  bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(),
                                          bitmap.getHeight(), textureview.getTransform( null ), true );
                                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                  bitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);
                                  byte[] byteArray = stream.toByteArray();*/

                                  if(mediakey.trim().isEmpty())
                                  {
                                      sequencestarttime = Calendar.getInstance();
                                      String currenttimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                      mediakey=currenttimestamp;
                                      savestartmediainfo();
                                  }

                                  if(mframetorecordcount == currentframenumber)
                                  {
                                      sequenceendtime = Calendar.getInstance();
                                      updatelistitemnotify(currentframenumber,"Frame");
                                      currentframenumber = currentframenumber + frameduration;
                                  }
                                  //bitmap.recycle();
                              }
                              else
                              {
                                  long checkremain=mframetorecordcount;
                                  long decreaseamount=currentframenumber-frameduration;
                                  if(checkremain-decreaseamount == 1)
                                      sequencestarttime = Calendar.getInstance();

                              }

                              mframetorecordcount++;
                          }
                      }catch (Exception e)
                      {
                          e.printStackTrace();
                      }

                  }
              });
              thread.start();
            }
        }
    };
    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size previewsize;
    /**
     * The {@link android.util.Size} of video recording.
     */
    private Size videosize;
    /**
     * Camera preview.
     */
    private CaptureRequest.Builder previewbuilder;
    /**
     * MediaRecorder
     */
    private MediaRecorder mediarecorder;
    /**
     * Whether the app is recording video now
     */
    public boolean isvideorecording =false;
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread backgroundthread;
    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler backgroundhandler;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore cameraopencloselock = new Semaphore(1);
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
     */
    private CameraDevice.StateCallback statecallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            videocomposerfragment.this.cameraDevice = cameraDevice;
            startPreview(false);
            cameraopencloselock.release();
            if (null != textureview) {
                configureTransform(textureview.getWidth(), textureview.getHeight());
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraopencloselock.release();
            cameraDevice.close();
            videocomposerfragment.this.cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraopencloselock.release();
            cameraDevice.close();
            videocomposerfragment.this.cameraDevice = null;
            Activity activity = applicationviavideocomposer.getactivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    private static final String log_tag = videocomposerfragment.class.getSimpleName();
    private static final int request_permissions = 1;

    /* The sides of width and height are based on camera orientation.
    That is, the preview size is the size before it is rotated. */
    // Output video size
    private Runnable doafterallpermissionsgranted;
    LinearLayout linearLayout;
    boolean isflashon = false,inPreview = true;
    adapteritemclick popupclickmain;
    adapteritemclick popupclicksub;

    TextView txt_title_actionbarcomposer,txt_media_quality;

    ImageView imgflashon,img_dotmenu,handle;

    View rootview = null;
    long millisecondtime, starttime, timebuff, updatetime = 0L ;
    Handler timerhandler;
    int hours, seconds, minutes, milliSeconds,framepersecond=30,videobitrate=2000000 ; // 2000000 is equals to 2 MB. It means quality is exisiting around 420P. It also depands on frame rate.
    String keytype =config.prefs_md5,currenthashvalue="",selectedvideoquality="720P";
    ArrayList<videomodel> videoframes =new ArrayList<>();
    ArrayList<frameinfo> uploadframelist =new ArrayList<>();
    long currentframenumber =0;
    long frameduration =15, mframetorecordcount =0,apicallduration=5,apicurrentduration=0;
    public boolean autostartvideo=false,camerastatusok=false,isexpandview =false;
    adapteritemclick madapterclick;
    RelativeLayout layout_bottom,layout_seekbarzoom,layout_mediatype;
    int bottomlayoutheight = 0,layoutmediatypeheight = 0;
    FrameLayout framecontainer;
    File lastrecordedvideo=null;
    String selectedvideofile ="", mediakey ="",selectedmetrices="", selectedhashes ="",hashvalue = "",metrichashvalue = "";
    //private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    ArrayList<videomodel> metricsitems =new ArrayList<>();
    ArrayList<videomodel> hashesitems =new ArrayList<>();
    ArrayList<dbitemcontainer> dbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> dbmiddleitemcontainer =new ArrayList<>();
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<permissions> permissionslist =new ArrayList<>();

    private boolean isdraweropen=false,isgraphicalshown=false;
    private Handler myhandler,mysoundwavehandler;
    private Runnable myrunnable,mymysoundwaverunnable;
    private boolean issavedtofolder=false,previewupdated=false;;
    JSONArray metadatametricesjson=new JSONArray();


    private Orientation mOrientation;
    private ActionBarDrawerToggle drawertoggle;
    @BindView(R.id.header_container)
    RotateLayout headercontainer;
    @BindView(R.id.img_roundblink)
    ImageView img_roundblink;
    @BindView(R.id.spinner_mediaquality)
    Spinner spinner_mediaquality;
    @BindView(R.id.expandable_layout)
    ExpandableLayout expandable_layout;
    @BindView(R.id.actionbarcomposer)
    RelativeLayout actionbarcomposer;
    @BindView(R.id.txt_media_low)
    TextView txt_media_low;
    @BindView(R.id.txt_media_medium)
    TextView txt_media_medium;
    @BindView(R.id.txt_media_high)
    TextView txt_media_high;

    @BindView(R.id.img_warning)
    ImageView img_warning;
    @BindView(R.id.actionbar)
    RelativeLayout actionbar;
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
    @BindView(R.id.txt_section_network)
    TextView txt_section_network;
    @BindView(R.id.layout_wifi_gps_data)
    LinearLayout layout_wifi_gps_data;
    @BindView(R.id.ll_actionbarcomposer)
    LinearLayout ll_actionbarcomposer;

    Animation blinkanimation;
    List<String> qualityitemslist=new ArrayList<>();
    private TranslateAnimation validationbaranimation;
    RelativeLayout layout_recorder;

    @Override
    public int getlayoutid() {
        return R.layout.fragment_videocomposer;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this,rootview);

        zoomLevel=1f;
        previewupdated=false;
        applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textureview = (AutoFitTextureView) rootview.findViewById(R.id.texture);
        imgflashon = (ImageView) rootview.findViewById(R.id.img_flash);
        img_dotmenu = (ImageView) rootview.findViewById(R.id.img_dotmenu);
        handle = (ImageView) rootview.findViewById(R.id.handle);
        txt_title_actionbarcomposer = (TextView) rootview.findViewById(R.id.txt_title_actionbarcomposer);
        txt_media_quality = (TextView) rootview.findViewById(R.id.txt_media_quality);
        linearLayout=rootview.findViewById(R.id.content);

        gethelper().drawerenabledisable(true);
        gethelper().setdatacomposing(true);

        if(! xdata.getinstance().getSetting(config.frameupdateevery).trim().isEmpty())
            apicallduration=Long.parseLong(xdata.getinstance().getSetting(config.frameupdateevery));

        imgflashon.setVisibility(View.VISIBLE);
        txt_media_quality.setVisibility(View.VISIBLE);

        timerhandler = new Handler() ;
        txt_title_actionbarcomposer.setText(config.mediarecorderformat);
        if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
            frameduration=Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

        if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                xdata.getinstance().getSetting(config.hashtype).trim().isEmpty())
        {
            keytype=config.prefs_md5;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt))
        {
            keytype=config.prefs_md5_salt;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha))
        {
            keytype=config.prefs_sha;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt))
        {
            keytype=config.prefs_sha_salt;
        }

        layout_mediatype.post(new Runnable() {
            @Override
            public void run() {
                bottomlayoutheight=layout_bottom.getHeight();
                layout_mediatype.post(new Runnable() {
                    @Override
                    public void run() {
                        layoutmediatypeheight = layout_mediatype.getHeight();
                    }
                });
            }
        });

        img_dotmenu.setVisibility(View.VISIBLE);
        textureview.setOnTouchListener(this);
        actionbar.setBackgroundColor(getResources().getColor(R.color.yellowtransparent));
        imgflashon.setOnClickListener(this);
        img_dotmenu.setOnClickListener(this);
        txt_media_quality.setOnClickListener(this);
        txt_media_low.setOnClickListener(this);
        txt_media_medium.setOnClickListener(this);
        txt_media_high.setOnClickListener(this);

        expandable_layout.setVisibility(View.GONE);
        txt_media_quality.setVisibility(View.VISIBLE);

        txt_media_quality.setText(config.mediaquality720);
        if(! xdata.getinstance().getSetting(config.videoquality).isEmpty())
        {
            txt_media_quality.setText(xdata.getinstance().getSetting(config.videoquality));
            if(xdata.getinstance().getSetting(config.videoquality).contains("480")){
                expendcollpaseviewcolor(txt_media_low,txt_media_medium,txt_media_high);
            }
            else if(xdata.getinstance().getSetting(config.videoquality).contains("720")){
                expendcollpaseviewcolor(txt_media_medium,txt_media_low,txt_media_high);
            }
            else if(xdata.getinstance().getSetting(config.videoquality).contains("1080")){
                expendcollpaseviewcolor(txt_media_high,txt_media_low,txt_media_medium);
            }
        }
        else
        {
            expendcollpaseviewcolor(txt_media_medium,txt_media_low,txt_media_high);
        }

        txt_media_low.setText(config.mediaquality480);
        txt_media_medium.setText(config.mediaquality720);
        txt_media_high.setText(config.mediaquality1080);


        expandable_layout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                // 0 closed, 3 opened
                if(state == 0)
                {
                    expandable_layout.setVisibility(View.GONE);
                    txt_title_actionbarcomposer.setVisibility(View.VISIBLE);
                    txt_media_quality.setVisibility(View.VISIBLE);
                    if(isexpandview)
                        txt_media_quality.setVisibility(View.GONE);
                    //ll_actionbarcomposer.setVisibility(View.VISIBLE);
                }
                else if(state == 3) {

                    if (rotationangle == -90) {

                        LinearLayout.LayoutParams lpexpandable = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //lpexpandable.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        lpexpandable.setMargins(0, 0, 0, 0);
                        expandable_layout.setLayoutParams(lpexpandable);

                    } else if (rotationangle == 90) {

                        LinearLayout.LayoutParams lpexpandable = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       // lpexpandable.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        lpexpandable.setMargins(0, 0, 0, 0);
                        expandable_layout.setLayoutParams(lpexpandable);

                    } else {

                        LinearLayout.LayoutParams lpexpandable = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //lpexpandable.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        lpexpandable.setMargins(0, 0, 0, 0);
                        expandable_layout.setLayoutParams(lpexpandable);
                    }

                }
                    Log.d("ExpandableLayout", "State: " + state);
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

        Animation.AnimationListener translatelistener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fadein_animation.setStartOffset(3000);
            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                fadein_animation.setStartOffset(3000);
            }
        };
        validationbaranimation.setAnimationListener(translatelistener);

        mOrientation = new Orientation(applicationviavideocomposer.getactivity());
        return rootview;
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    public void changeiconsorientation(float rotateangle)
    {
        rotationangle=(int)rotateangle;

            if(headercontainer !=null){
                if(rotateangle == 180)
                    rotateangle=0;

                if(rotateangle == -90)
                {
                    if(!isvideorecording)
                    {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);

                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        setdynamiclayout(lp,null,0,0,0,bottomlayoutheight,0,
                                0,0,0,null,headercontainer,null,90);

                        img_dotmenu.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lpimgflashon = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                        setdynamiclayout(null,lpimgflashon,(int) getResources().getDimension(R.dimen.margin_10dp),
                                (int) getResources().getDimension(R.dimen.margin_4dp),0,0,0,0,0,0,imgflashon,null,null,0);

                    }else{
                        setbottommargin(headercontainer);
                    }

                    gethelper().hidedrawerbutton(true);  // hidden
                }
                else if(rotateangle == 90)
                {
                    if(!isvideorecording)
                    {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
                        //lp.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
                        setdynamiclayout(lp,null,0,0,0,bottomlayoutheight,0,
                                0,0,0,null,headercontainer,null,270);

                        img_dotmenu.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lpimgflashon = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        setdynamiclayout(null,lpimgflashon,(int) getResources().getDimension(R.dimen.margin_10dp),
                                (int) getResources().getDimension(R.dimen.margin_4dp),0,0,0,0,0,0,imgflashon,null,null,0);

                    }else{
                        setbottommargin(headercontainer);
                    }

                    if(headercontainer.getAngle() == 90)
                    {
                        gethelper().hidedrawerbutton(true);  // hidden
                    }
                    else
                    {
                        gethelper().hidedrawerbutton(false);  // shown
                    }
                }
                else
                {
                    if(!isvideorecording)
                    {

                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        setdynamiclayout(lp,null,0,0,0,0,0,0,0,0,null,headercontainer,null,(int)rotateangle);

                        RelativeLayout.LayoutParams  layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);

                        RelativeLayout.LayoutParams lpimg_dotmenu = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lpimg_dotmenu.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        img_dotmenu.setVisibility(View.VISIBLE);

                        setdynamiclayout(lpimg_dotmenu,null,0,(int) getResources().getDimension(R.dimen.margin_5dp),0,0,
                                (int) getResources().getDimension(R.dimen.margin_10dp),0,
                                (int) getResources().getDimension(R.dimen.margin_15dp),0,img_dotmenu,null,null,0);

                       /* RelativeLayout.LayoutParams lpexpandable = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lpexpandable.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        lpexpandable.setMargins(20, 0, 0,  0);
                        expandable_layout.setLayoutParams(lpexpandable);*/


                        LinearLayout.LayoutParams lpimgflashon = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        setdynamiclayout(null,lpimgflashon,(int) getResources().getDimension(R.dimen.margin_15dp),
                                (int) getResources().getDimension(R.dimen.margin_5dp),0,0,0,
                                0,0,0,imgflashon,null,null,0);
                    }else{
                        setbottommargin(headercontainer);
                    }
                    if(headercontainer.getAngle() == 90)
                    {
                        gethelper().hidedrawerbutton(true);  // hidden
                    }
                    else
                    {
                        gethelper().hidedrawerbutton(false);  // shown
                    }
                }
            }
        }

    public boolean isvideorecording() {
        return isvideorecording;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId())
        {
            case  R.id.texture:
                try {
                    Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                    if (rect == null) return false;
                    float currentFingerSpacing;

                    if (motionEvent.getPointerCount() == 2) { //Multi touch.
                        currentFingerSpacing = getFingerSpacing(motionEvent);
                        float delta = 0.05f;
                        if (fingerspacing != 0) {
                            if (currentFingerSpacing > fingerspacing) {
                                if ((maximumzoomlevel - zoomLevel) <= delta) {
                                    delta = maximumzoomlevel - zoomLevel;
                                }
                                zoomLevel = zoomLevel + delta;
                            } else if (currentFingerSpacing < fingerspacing){
                                if ((zoomLevel - delta) < 1f) {
                                    delta = zoomLevel - 1f;
                                }
                                zoomLevel = zoomLevel - delta;
                            }
                            applycamerazoom();
                        }
                        fingerspacing = currentFingerSpacing;
                    }
                    else
                    {
                        if(! isvideorecording)
                        {
                            if(madapterclick != null)
                                madapterclick.onItemClicked(motionEvent,3);
                        }

                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes larger
     * than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        backgroundthread = new HandlerThread("CameraBackground");
        backgroundthread.start();
        backgroundhandler = new Handler(backgroundthread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        backgroundthread.quitSafely();
        try {
            backgroundthread.join();
            backgroundthread = null;
            backgroundhandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isvisibletouser) {
        super.setUserVisibleHint(isvisibletouser);
        this.isvisibletouser=isvisibletouser;
        if(isvisibletouser && textureview != null && textureview.isAvailable())
        {
            camerastatusok=true;
            startBackgroundThread();
            if (textureview.isAvailable())
                openCamera(textureview.getWidth(), textureview.getHeight());

            textureview.setSurfaceTextureListener(surfacetexturelistener);
        }
        else
        {
            closemediawithtimer();
        }
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `statecallback`.
     */
    private void openCamera(int width, int height) {
        final Activity activity = applicationviavideocomposer.getactivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!cameraopencloselock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
           // cameraId = manager.getCameraIdList()[0];
            // Choose the sizes for camera preview and video recording
            characteristics = manager.getCameraCharacteristics(cameraId);

            maximumzoomlevel = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            videosize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            previewsize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, videosize);
            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                textureview.setAspectRatio(previewsize.getWidth(), previewsize.getHeight());
            } else {
                textureview.setAspectRatio(previewsize.getHeight(), previewsize.getWidth());
            }

            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (sensorOrientation == 270) {
                // Camera is mounted the wrong way...
                upsideDown = true;
            }

            configureTransform(width, height);
            mediarecorder = new MediaRecorder();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(cameraId, statecallback, null);
        } catch (Exception e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }
    private void closeCamera() {
        try {
            cameraopencloselock.acquire();
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != mediarecorder) {
                mediarecorder.release();
                mediarecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            cameraopencloselock.release();
        }
    }
    /**
     * Start the camera preview.
     */
    private void startPreview(boolean shouldrecorderstart) {
        if (null == cameraDevice || !textureview.isAvailable() || null == previewsize) {
            return;
        }
        try {
            if(shouldrecorderstart)
                setUpMediaRecorder();

            SurfaceTexture texture = textureview.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(previewsize.getWidth(), previewsize.getHeight());
            previewbuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<Surface>();
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            previewbuilder.addTarget(previewSurface);
            if(shouldrecorderstart)
            {
                Surface recorderSurface = mediarecorder.getSurface();
                surfaces.add(recorderSurface);
                previewbuilder.addTarget(recorderSurface);
            }

            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    previewsession = cameraCaptureSession;
                    updatePreview();
                    previewupdated=true;
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, backgroundhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Update the camera preview. {@link #startPreview(boolean)} ()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == cameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(previewbuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            previewsession.setRepeatingRequest(previewbuilder.build(), null, backgroundhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }
    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `textureview`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `textureview` is fixed.
     *
     * @param viewWidth  The width of `textureview`
     * @param viewHeight The height of `textureview`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == textureview || null == previewsize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();


        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewsize.getHeight(), previewsize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewsize.getHeight(),
                    (float) viewWidth / previewsize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureview.setTransform(matrix);
    }


    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }

        try
        {
            if(mediarecorder == null)
                mediarecorder=new MediaRecorder();

            mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediarecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediarecorder.setOutputFile(getVideoFile(activity).getAbsolutePath());

            CamcorderProfile profile=null;
            if(selectedvideoquality.equalsIgnoreCase(config.mediaquality480))
            {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            }
            else if(selectedvideoquality.equalsIgnoreCase(config.mediaquality720))
            {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            }
            else if(selectedvideoquality.equalsIgnoreCase(config.mediaquality1080))
            {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
            }
            else
            {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            }

            // In sample code
            mediarecorder.setVideoFrameRate(profile.videoFrameRate);
            //mediarecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mediarecorder.setVideoSize(videosize.getWidth(), videosize.getHeight());
            mediarecorder.setVideoEncodingBitRate(profile.videoBitRate);
            mediarecorder.setAudioEncodingBitRate(profile.audioBitRate);
            mediarecorder.setAudioSamplingRate(profile.audioSampleRate);
            mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            if(common.getapppaidlevel() <= 0)
            {
                int length=common.getunpaidvideorecordlength();
                int maxduartion=length * 1000;
                mediarecorder.setMaxDuration(maxduartion);
                mediarecorder.setOnInfoListener(mediainfolistener);
            }

            // edited
            /*
            mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
            mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
            mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);*/

                // my code
            /*mediarecorder.setVideoEncodingBitRate(profile.videoBitRate);
            mediarecorder.setVideoFrameRate(framepersecond);
            mediarecorder.setVideoSize(videosize.getWidth(), videosize.getHeight());
            mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);*/


            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            // int orientation = ORIENTATIONS.get(rotation);
            int orientation =  camerautil.getOrientation(rotation, upsideDown);
            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            //Log.e("cameraangle ",""+orientation);
            //Toast.makeText(getActivity(),""+orientation+" "+sensorOrientation,Toast.LENGTH_SHORT).show();

            if(rotationangle == 90)  // Landscape left side
            {
                mediarecorder.setOrientationHint(0);
            }
            else if(rotationangle == 0)   // Portrait 90
            {
                if (cameraId.equals(CAMERA_FRONT))
                {
                    mediarecorder.setOrientationHint(270);
                }
                else
                {
                    mediarecorder.setOrientationHint(90);
                }
            }
            else if(rotationangle == -90)     // Landscape right side
            {
                mediarecorder.setOrientationHint(180);
            }
            else if(rotationangle == 180)     // Portrait 180
            {
                if (cameraId.equals(CAMERA_FRONT))
                {
                    mediarecorder.setOrientationHint(90);
                }
                else
                {
                    mediarecorder.setOrientationHint(270);
                }
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            mediarecorder.prepare();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    MediaRecorder.OnInfoListener mediainfolistener=new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mediarecorder, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                Log.v("VIDEOCAPTURE","Maximum Duration Reached");
                try {

                    /*resetpreviewsession();
                    startmetaservices();
                    stopblinkanimation();

                    mediarecorder.stop();
                    mediarecorder.release();*/

                    startstopvideo();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                showvideorecordlengthalert();
            }
        }
    };

    public void showvideorecordlengthalert() {
        try
        {
            double length=common.getunpaidvideorecordlength();
            double recordtime=length/60;
            DecimalFormat precision = new DecimalFormat("0.0");
            String time=precision.format(recordtime)+" minutes";

            gethelper().showinapppurchasepopup(applicationviavideocomposer.getactivity(),"Recording is limited to "+ time +
                    " in the basic version. Upgrade",new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {
                    gethelper().inapppurchase(object.toString());
                }

                @Override
                public void onItemClicked(Object object, int type) {

                }
            });

            /*new AlertDialog.Builder(getActivity(), R.style.customdialogtheme)
                    .setTitle("Alert")
                    .setMessage("Recording is limited to "+ xdata.getinstance().getSetting(xdata.unpaid_video_record_length)+" seconds" +
                            " in the basic version. Upgrade")
                    .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    })
                    .show();*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private File getVideoFile(Context context) {
        String storagedirectory=xdata.getinstance().getSetting(config.selected_folder);
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file=new File(storagedirectory, fileName+".mp4");

        File destinationDir=new File(storagedirectory);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        selectedvideofile=file.getAbsolutePath();
        return file;
    }
    private void startRecordingVideo() {
        try {
            if (ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            if(! previewupdated)
                return;

            if(expandable_layout != null && expandable_layout.isExpanded())
                expendcollpaseview(true);

            metadatametricesjson=new JSONArray();
            mediakey ="";
            startPreview(true);
            if(mediarecorder != null)
            {
                // Start recording
                mediarecorder.start();
                startvideotimer();
                setsoundwaveinfo();
                //fragmentgraphic.setvisualizerwave();
                wavevisualizerslist.clear();


                //startnoise();
            }

            issavedtofolder=false;
            isvideorecording = true;
            startblinkanimation();

            if(madapterclick != null)
                madapterclick.onItemClicked(null,1);

            showhideactionbaricon(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetpreviewsession()
    {
        try
        {
            previewsession.stopRepeating();
            previewsession.abortCaptures();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopresetmediarecorder()
    {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try{
            if(mediarecorder != null)
                mediarecorder.stop();
        }catch(Exception stopException){
            Log.e("exception", String.valueOf(stopException));
        }

        try{
            if(mediarecorder != null)
                mediarecorder.reset();
        }catch(Exception stopException){
            Log.e("exception", String.valueOf(stopException));
        }
    }

    private void startmetaservices() {
        // UI
        if(validationbaranimation != null)
        {
            validationbaranimation.cancel();
            validationbaranimation.reset();
        }
        issavedtofolder=true;
        lastrecordedvideo=new File(selectedvideofile);

        if(mysoundwavehandler != null && mymysoundwaverunnable != null)
            mysoundwavehandler.removeCallbacks(mymysoundwaverunnable);

        startPreview(false);
        stopvideotimer();

        resetvideotimer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    insertstartmediainfo();

                    Gson gson = new Gson();
                    String list1 = gson.toJson(dbstartitemcontainer);
                    String list2 = gson.toJson(dbmiddleitemcontainer);
                    xdata.getinstance().saveSetting("liststart",list1);
                    xdata.getinstance().saveSetting("listmiddle",list2);
                    xdata.getinstance().saveSetting("mediapath",lastrecordedvideo.getAbsolutePath());
                    xdata.getinstance().saveSetting("keytype",keytype);

                    Intent intent = new Intent(applicationviavideocomposer.getactivity(), insertmediadataservice.class);
                    applicationviavideocomposer.getactivity().startService(intent);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        isvideorecording = false;
                        if(madapterclick != null)
                            madapterclick.onItemClicked(lastrecordedvideo.getAbsoluteFile(),2);

                        showhideactionbaricon(1);
                        firsthashvalue = true;
                        medialistitemaddbroadcast();

                        if(madapterclick != null)
                            madapterclick.onItemClicked(null,4);
                    }
                });

            }
        }).start();
    }

    public void medialistitemaddbroadcast()
    {
        Intent intent = new Intent(config.broadcast_medialistnewitem);
        applicationviavideocomposer.getactivity().sendBroadcast(intent);
    }

    public void adjustzoom()
    {
        zoomLevel++;

        if(zoomLevel > maximumzoomlevel)
            zoomLevel=1f;

        applycamerazoom();
    }

    public void applycamerazoom()
    {
        DecimalFormat precision=new DecimalFormat("0.0");
        if(madapterclick != null)
            madapterclick.onItemClicked(""+precision.format(zoomLevel)+" x",5);

        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        float ratio = (float) 1 / zoomLevel;
        int croppedWidth = rect.width() - Math.round((float)rect.width() * ratio);
        int croppedHeight = rect.height() - Math.round((float)rect.height() * ratio);
        zoom = new Rect(croppedWidth/2, croppedHeight/2,
                rect.width() - croppedWidth/2, rect.height() - croppedHeight/2);
        Log.e("rectzoom level ",""+zoom+" "+zoomLevel+" "+ maximumzoomlevel);

        previewbuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
        try {
            previewsession.setRepeatingRequest(previewbuilder.build(), null, backgroundhandler);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void expendcollpaseviewcolor(TextView textview1,TextView textview2,TextView textview3)
    {
        textview1.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
        textview2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        textview3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_flash:
                navigateflash();
                break;

            case R.id.txt_media_low:
                setmediaquality(config.mediaquality480);
                txt_media_low.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                txt_media_high.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                break;

            case R.id.txt_media_medium:
                setmediaquality(config.mediaquality720);
                txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                txt_media_low.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                txt_media_high.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                break;

            case R.id.txt_media_high:
                setmediaquality(config.mediaquality1080);
                txt_media_high.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                txt_media_low.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                break;

            case R.id.txt_media_quality:
                expendcollpaseview(false);
                break;

            case R.id.img_dotmenu:
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
                break;

        }
    }

    public void expendcollpaseview(boolean isplaying)
    {
        if(expandable_layout.isExpanded())
        {
            isexpandview =isplaying;
            qualityoptionanimations(1.0f,0f);
            expandable_layout.setDuration(200);
            expandable_layout.collapse();
            expandable_layout.setVisibility(View.GONE);
            txt_title_actionbarcomposer.setVisibility(View.VISIBLE);
            txt_media_quality.setVisibility(View.VISIBLE);

            if(isplaying)
                txt_media_quality.setVisibility(View.GONE);

            //ll_actionbarcomposer.setVisibility(View.VISIBLE);
        }
        else
        {
            isexpandview =isplaying;
            qualityoptionanimations(0f,1.0f);
            expandable_layout.setDuration(200);
            expandable_layout.expand();
            expandable_layout.setVisibility(View.VISIBLE);
            txt_title_actionbarcomposer.setVisibility(View.GONE);
            txt_media_quality.setVisibility(View.GONE);
            //ll_actionbarcomposer.setVisibility(View.GONE);
        }
    }

    public void qualityoptionanimations(float fromalpha,float toalpha)
    {
        AlphaAnimation animation1 = new AlphaAnimation(fromalpha, toalpha);
        animation1.setDuration(500);
        animation1.setFillAfter(false);
        txt_media_low.startAnimation(animation1);
        txt_media_high.startAnimation(animation1);
        txt_media_medium.startAnimation(animation1);
    }

    public void setmediaquality(String quality)
    {
        xdata.getinstance().saveSetting(config.videoquality,quality);
        expandable_layout.setDuration(100);
        qualityoptionanimations(1.0f,0f);
        expandable_layout.collapse();
        if(! selectedvideoquality.equalsIgnoreCase(quality))
        {
            txt_media_quality.setText(quality);
            selectedvideoquality=quality;
        }
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
    }

    public void startstopvideo()
    {
        if (isvideorecording) {
            gethelper().updateactionbar(1, applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
          //  layout_bottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
            resetpreviewsession();
            stopresetmediarecorder();
            startmetaservices();
            stopblinkanimation();
        } else {
            selectedhashes="";
            selectedmetrices="";
            metricsitems.clear();
            hashesitems.clear();
            dbstartitemcontainer.clear();
            dbmiddleitemcontainer.clear();

            if(imgflashon != null)
                imgflashon.setVisibility(View.VISIBLE);

            apicurrentduration =0;
            currentframenumber =0;
            mframetorecordcount =0;
            currentframenumber = currentframenumber + frameduration;

            videoframes.clear();
            gethelper().updateactionbar(1, applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
          //  layout_bottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal_transparent));
            startRecordingVideo();
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void setData(boolean autostartvideo, adapteritemclick madapterclick, RelativeLayout layout_bottom,RelativeLayout layout_mediatype, RelativeLayout layout_seekbarzoom, FrameLayout framecontainer,RelativeLayout layout_recorder) {
        this.autostartvideo = autostartvideo;
        this.madapterclick = madapterclick;
        this.layout_bottom = layout_bottom;
        this.layout_seekbarzoom = layout_seekbarzoom;
        this.framecontainer = framecontainer;
        this.layout_mediatype = layout_mediatype;
        this.layout_recorder = layout_recorder;

    }

    public static Fragment newInstance()
    {
        return new videocomposerfragment();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        float pitch = orientation[1] * -57;
        float roll = orientation[2] * -57;
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
    public static class ErrorDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage("This device doesn't support Camera2 API.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mOrientation != null)
            mOrientation.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mOrientation != null)
            mOrientation.startListening(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                common.dismisscustompermissiondialog();
                stopvideotimer();
                resetvideotimer();
                if (doafterallpermissionsgranted != null) {
                    doafterallpermissionsgranted.run();
                    doafterallpermissionsgranted = null;
                } else {
                    if(permissionslist.size() == 0)
                    {
                        permissionslist.add(new permissions(Manifest.permission.CAMERA,false,false));
                    }
                    List<String> deniedpermissions = new ArrayList<>();
                    for(int i=0;i<permissionslist.size();i++)
                    {
                        permissionslist.get(i).setIspermissionallowed(true);
                        if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permissionslist.get(i).getPermissionname()) != PackageManager.PERMISSION_GRANTED)
                        {
                            if(! permissionslist.get(i).isIspermissionskiped())
                            {
                                deniedpermissions.add(permissionslist.get(i).getPermissionname());
                                permissionslist.get(i).setIspermissionallowed(false);
                                break;
                            }
                        }
                    }

                    if (deniedpermissions.isEmpty()) {
                        // All permissions are granted
                        doafterallpermissionsgranted();
                    } else {
                        //String[] array = new String[deniedpermissions.size()];

                        String[] array = new String[1];
                        array = deniedpermissions.toArray(array);
                        final String[] finalArray = array;

                        common.showcustompermissiondialog(applicationviavideocomposer.getactivity(), new adapteritemclick() {
                            @Override
                            public void onItemClicked(Object object) {
                            }

                            @Override
                            public void onItemClicked(Object object, int type) {
                                if(type == 0)
                                {
                                    if(finalArray.length > 0)
                                    {
                                        for(int i=0;i<permissionslist.size();i++)
                                        {
                                            if(finalArray[0].equalsIgnoreCase(permissionslist.get(i).getPermissionname()))
                                            {
                                                permissionslist.get(i).setIspermissionskiped(true);
                                                break;
                                            }
                                        }
                                    }
                                }
                                else if(type == 1)
                                {
                                    if(finalArray.length > 0)
                                        ActivityCompat.requestPermissions(getActivity(), finalArray, request_permissions);
                                }
                            }
                        },finalArray[0]);
                    }
                }
                txt_title_actionbarcomposer.setText(config.mediarecorderformat);
            }
        },config.transition_fragment_millis_0);
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

    private void doafterallpermissionsgranted() {

        setmetriceshashesdata();
        if(! camerastatusok)
        {
            camerastatusok=true;
           // layout_bottom.setVisibility(View.VISIBLE);
            startBackgroundThread();
            if (textureview.isAvailable())
                openCamera(textureview.getWidth(), textureview.getHeight());
            textureview.setSurfaceTextureListener(surfacetexturelistener);
        }
    }

    @Override
    public void onPause() {
        Log.e("onpause","onpause");
        closemediawithtimer();
        showhideactionbaricon(1);
        stopblinkanimation();
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);

        if(validationbaranimation != null)
        {
            validationbaranimation.cancel();
            validationbaranimation.reset();
        }

        super.onPause();
    }

    public void closemediawithtimer()
    {
        if(camerastatusok)
        {
            camerastatusok=false;
            closeCamera();
            stopBackgroundThread();
            stopvideotimer();
            resetvideotimer();
            isvideorecording =false;
            try {
                if(! issavedtofolder && common.getstorageaudiorecorddeniedpermissions().isEmpty() && (selectedvideofile != null )
                        && new File(selectedvideofile).exists())
                    common.delete(new File(selectedvideofile));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /// camera2video code end

    public void startvideotimer()
    {
        starttime = SystemClock.uptimeMillis();
        timerhandler.postDelayed(runnable, 0);
    }

    public void stopvideotimer()
    {
        timebuff += millisecondtime;
        timerhandler.removeCallbacks(runnable);
    }

    public void resetvideotimer()
    {
        millisecondtime = 0L ;
        starttime = 0L ;
        timebuff = 0L ;
        updatetime = 0L ;
        seconds = 0 ;
        minutes = 0 ;
        hours = 0 ;
        milliSeconds = 0 ;
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
                    milliSeconds = (int) (updatetime % 1000);

                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isvideorecording)
                            {
                                //+ String.format("%01d", hours) + ":"+
                                txt_title_actionbarcomposer.setText(
                                        "" + String.format("%02d", minutes) + ":"
                                        + String.format("%02d", seconds) + "."
                                        + String.format("%02d", (milliSeconds /100)));
                            }

                        }
                    });
                }
            }).start();
            timerhandler.postDelayed(this, 0);
        }
    };


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
       // metricesarray.put(object);
        builder.append("\n");
        metadatametricesjson.put(object);
        selectedmetrices=builder.toString();
    }

    public void updatelistitemnotify(final long framenumber, final String message)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                /*if(array == null || array.length == 0)
                    return;
                final String keyvalue= getkeyvalue(array);*/
                final String keyvalue= "";
                currenthashvalue=keyvalue;
                apicurrentduration++;

                videoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));
                hashvalue = keyvalue;

                if(! selectedhashes.trim().isEmpty())
                    selectedhashes=selectedhashes+"\n";

                selectedhashes =selectedhashes+ videoframes.get(videoframes.size()-1).getframeinfo();


                if(apicurrentduration > apicallduration)
                    apicurrentduration=apicallduration;


                ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                getselectedmetrics(mlocalarraylist);

                JSONArray metricesarray=new JSONArray();
                if(metadatametricesjson.length() > 0)
                {
                    try
                    {
                        metricesarray.put(metadatametricesjson.get(metadatametricesjson.length()-1));
                        metrichashvalue = md5.calculatestringtomd5(metricesarray.toString());
                        uploadframelist.add(new frameinfo(""+framenumber,"xxx",keyvalue,keytype,false,mlocalarraylist));
                        savemediaupdate(metricesarray);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                Log.e("current call, calldur ",apicurrentduration+" "+apicallduration);
                if(apicurrentduration == apicallduration)
                    apicurrentduration=0;
            }
        }).start();
    }

    public void setsoundwaveinfo(){

        if(mysoundwavehandler != null && mymysoundwaverunnable != null)
            mysoundwavehandler.removeCallbacks(mymysoundwaverunnable);

        mysoundwavehandler =new Handler();
        mymysoundwaverunnable = new Runnable() {
            @Override
            public void run() {

                if (isvideorecording) {
                    if (mediarecorder != null) {
                        int ampletudevalue = mediarecorder.getMaxAmplitude();
                        double ampletude = 20 * Math.log10(ampletudevalue / 32767.0);
                        int decibelvalue = 50 - Math.abs((int) ampletude);

                        gethelper().setsoundwaveinformation(ampletudevalue, decibelvalue);
                    }
                    mysoundwavehandler.postDelayed(this, 50);
                }
            }
        };
        mysoundwavehandler.post(mymysoundwaverunnable);
    }

    public void setmetriceshashesdata()
    {
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);

        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {
                if(isvideorecording)
                {
                    if((! selectedmetrices.toString().trim().isEmpty()))
                    {

                        if(metricsitems.size() > 0)
                        {
                           // metricsitems.set(0,new videomodel(selectedmetrices));
                            metricsitems.add(new videomodel(selectedmetrices));
                        }
                        else
                        {
                            metricsitems.add(new videomodel(selectedmetrices));
                        }
                        //mmetricesadapter.notifyItemChanged(metricsitems.size()-1);
                        selectedmetrices="";
                    }
                }


                    //layout_no_gps_wifi.setVisibility(View.VISIBLE);

                    if(layout_recorder != null)
                        layout_recorder.setClickable(true);

                    layout_wifi_gps_data.setVisibility(View.VISIBLE);
                    actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

                    visibleconnection();
                    setactionbarbackgroundcolor();

                /*else{
                    actionbar.setBackgroundColor(Color.parseColor(common.getactionbarcolor()));
                    //layout_no_gps_wifi.setVisibility(View.GONE);
                    layout_wifi_gps_data.setVisibility(View.GONE);
                }*/

                if(! isvideorecording)
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
                        myhandler.postDelayed(this, 1000);
                        return;
                    }

                //    visiblewarningcontrollers();

                    if(madapterclick != null){
                        madapterclick.onItemClicked(null,7);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                myhandler.postDelayed(this, 1000);
            }
        };
        myhandler.post(myrunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Calling after 1 by 1 frame duration.
    public void savemediaupdate(JSONArray metricesjsonarray)
    {
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
            String devicetime = common.get24hourformat();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SS aa");
            String starttime = sdf.format(sequencestarttime.getTime());
            String endtime = sdf.format(sequenceendtime.getTime());

            if(metricesjsonarray != null && metricesjsonarray.length() > 0)
            {
                JSONObject arrayobject=metricesjsonarray.getJSONObject(0);
                arrayobject.put("sequencestarttime",starttime);
                arrayobject.put("sequenceendtime",endtime);
                arrayobject.put("devicetime",devicetime);
            }

            metrichash = md5.calculatestringtomd5(metricesjsonarray.get(0).toString());
            dbmiddleitemcontainer.add(new dbitemcontainer("", metrichash ,keytype, mediakey,""+metricesjsonarray.toString(),
                    currentdate[0],"0",sequencehash,sequenceno,"",currentdate[0],"","",""));
        } catch (Exception e) {
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
                dbstartitemcontainer.add(new dbitemcontainer("","video","Local storage path", mediakey,"","","0","0",
                        config.type_video_start,devicestartdate,devicestartdate,timeoffset,"","","",
                        xdata.getinstance().getSetting(config.selected_folder)));
                Log.e("startcontainersize"," "+ dbstartitemcontainer.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertstartmediainfo()
    {
        if(lastrecordedvideo != null)
        {
            String duration = common.getvideotimefromurl(getActivity(),lastrecordedvideo.getAbsolutePath());

            String medianame=common.getfilename(lastrecordedvideo.getAbsolutePath());
            String[] split=medianame.split("\\.");
            if(split.length > 0)
                medianame=split[0];

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps",""+framepersecond);
            map.put("firsthash", firsthash);
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
            dbstartitemcontainer.get(0).setItem3(lastrecordedvideo.getAbsolutePath());
            dbstartitemcontainer.get(0).setItem15(lastrecordedvideo.getAbsolutePath());
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

                try {
                    mdbhelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    private void navigateflash() {
        try {
            if(isflashon) {
                imgflashon.setImageResource(R.drawable.icon_flashoff);
                previewbuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
                previewsession.setRepeatingRequest(previewbuilder.build(), null, backgroundhandler);
                isflashon = false;
            } else {
                imgflashon.setImageResource(R.drawable.icon_flashon);
                previewbuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                previewsession.setRepeatingRequest(previewbuilder.build(), null, backgroundhandler);
                isflashon = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchCamera(ImageView imageView) {
        if (cameraId.equals(CAMERA_FRONT)) {
            imgflashon.setImageResource(R.drawable.icon_flashoff);
            isflashon = false;
            previewupdated=false;
            imageView.setImageResource(R.drawable.icon_cameraflip);
            cameraId = CAMERA_BACK;
            closeCamera();
            reopenCamera();

        } else if (cameraId.equals(CAMERA_BACK)) {
            imgflashon.setImageResource(R.drawable.icon_flashoff);
            isflashon = false;
            previewupdated=false;
            cameraId = CAMERA_FRONT;
            imageView.setImageResource(R.drawable.icon_reversecamera);
            closeCamera();
            reopenCamera();
        }
        zoomLevel=1.0f;
        DecimalFormat precision=new DecimalFormat("0.0");
        if(madapterclick != null)
            madapterclick.onItemClicked(""+precision.format(zoomLevel)+" x",5);
    }

    public void reopenCamera() {
        if (textureview.isAvailable())
            openCamera(textureview.getWidth(), textureview.getHeight());

    }
    public void showhideactionbaricon(int i){
        if(i == 0){
            txt_title_actionbarcomposer.setText(config.mediarecorderformat);
            img_dotmenu.setVisibility(View.INVISIBLE);
            txt_media_quality.setVisibility(View.INVISIBLE);

        }else{
            txt_title_actionbarcomposer.setText(config.mediarecorderformat);
            img_dotmenu.setVisibility(View.VISIBLE);
            txt_media_quality.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showhideviewondrawer(boolean isshow) {
        super.showhideviewondrawer(isshow);

        if(isshow){
            layout_bottom.setVisibility(View.GONE);
            headercontainer.setVisibility(View.GONE);
            layout_seekbarzoom.setVisibility(View.GONE);
            framecontainer.setVisibility(View.GONE);
        }else{
            layout_bottom.setVisibility(View.VISIBLE);
            headercontainer.setVisibility(View.VISIBLE);
            layout_seekbarzoom.setVisibility(View.VISIBLE);
            framecontainer.setVisibility(View.VISIBLE);
        }
    }


    public void setdynamiclayout(RelativeLayout.LayoutParams relativelayoutparams,LinearLayout.LayoutParams linearLayoutParams,int marginleft,int margintop,int marginright,int marginbottom,
                                 int paddingleft,int paddingtop,int paddingright,int paddingbottom,ImageView imageView,RotateLayout rotationheadercontainer,RotateLayout rotationwifilayout,
                                 int rotationangle){


        if(relativelayoutparams!= null){

            if(imageView !=null){
                relativelayoutparams.setMargins(marginleft,margintop,marginright,marginbottom);
                imageView.setLayoutParams(relativelayoutparams);
                imageView.setPadding(paddingleft,paddingtop,paddingright,paddingbottom);
            }
            if(rotationheadercontainer != null){
                relativelayoutparams.setMargins(marginleft,margintop,marginright,marginbottom);
                rotationheadercontainer.setLayoutParams(relativelayoutparams);
                rotationheadercontainer.setAngle(rotationangle);

            }else if(rotationwifilayout!= null){
                relativelayoutparams.setMargins(marginleft,margintop,marginright,marginbottom);
                rotationwifilayout.setLayoutParams(relativelayoutparams);
                rotationwifilayout.setAngle(rotationangle);
            }
        }else{

            linearLayoutParams.setMargins(marginleft, margintop, marginright,  marginbottom);
            imageView.setLayoutParams(linearLayoutParams);
        }
    }

    public void visibleconnection(){
            String value = "";

                if(xdata.getinstance().getSetting(config.CellProvider).isEmpty()
                        || xdata.getinstance().getSetting(config.CellProvider).trim().equalsIgnoreCase("NA")
                        || xdata.getinstance().getSetting(config.CellProvider).equalsIgnoreCase("null")
                        || xdata.getinstance().getSetting(config.airplanemode).equals("ON"))
                {

                    txt_section_network.setText(config.TEXT_NETWORK +"NA");
                    img_network.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.crossicon));

                }else{
                    txt_section_network.setText(config.TEXT_NETWORK + "" + common.getxdatavalue(xdata.getinstance().getSetting(config.CellProvider)));
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
                                 .append(" ", new CenteredImageSpan(getActivity(),R.drawable.ic_plusminus),0)
                                .append(gpsvalue+"ft");

                                txt_section_gps.setText(builder);
                                img_gps.setBackground(ContextCompat.getDrawable(applicationviavideocomposer.getactivity(),R.drawable.rightcheck));

                            } else {

                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                builder.append(config.TEXT_GPS)
                                        .append(" ", new CenteredImageSpan(getActivity(),R.drawable.ic_plusminus),0)
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

            if (!common.isnetworkconnected(getActivity()) ||
                    xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0") ||
                    xdata.getinstance().getSetting(config.CellProvider).equalsIgnoreCase("NA") ||
                    xdata.getinstance().getSetting(config.Connectionspeed).equalsIgnoreCase("NA") ||
                    xdata.getinstance().getSetting(config.GPSAccuracy).equalsIgnoreCase("NA") ||
                    xdata.getinstance().getSetting(config.CellProvider).equalsIgnoreCase("null") ||
                    xdata.getinstance().getSetting(config.airplanemode).equals("ON")||
                    xdata.getinstance().getSetting(config.Connectionspeed) == null ||
                    xdata.getinstance().getSetting(config.GPSAccuracy) == null) {

                actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

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
                        actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

                    } else {
                        actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.greentransparent));
                    }
                }
            }
    }

    public void setbottommargin(RotateLayout headercontainer){

        if(headercontainer.getAngle()==0){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            setdynamiclayout(lp,null,0,0,0,0,0,0,0,0,null,headercontainer,null,headercontainer.getAngle());
        }else{
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);

            if(headercontainer.getAngle()==90)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

            setdynamiclayout(lp,null,0,0,0,bottomlayoutheight-layoutmediatypeheight,0,
                    0,0,0,null,headercontainer,null,headercontainer.getAngle());
        }
    }
}

