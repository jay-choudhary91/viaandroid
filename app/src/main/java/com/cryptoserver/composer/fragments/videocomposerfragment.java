package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.apiresponselistener;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.metadata.MetaDataInsert;
import com.cryptoserver.composer.metadata.MetaDataRead;
import com.cryptoserver.composer.models.frameinfo;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.customffmpegframegrabber;
import com.cryptoserver.composer.utils.randomstring;
import com.cryptoserver.composer.utils.taskresult;
import com.cryptoserver.composer.utils.camerautil;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.xdata;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public class videocomposerfragment extends basefragment implements View.OnClickListener,View.OnTouchListener {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "videocomposerfragment";

    protected float fingerSpacing = 0;
    protected float zoomLevel = 1f;
    protected float maximumZoomLevel;
    protected Rect zoom;

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    boolean upsideDown = false;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;
    /**
     * A refernce to the opened {@link android.hardware.camera2.CameraDevice}.
     */
    private CameraDevice mCameraDevice;
    /**
     * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for preview.
     */
    private CameraCaptureSession mPreviewSession;


    private CameraCharacteristics characteristics;
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener
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
            if(mIsRecordingVideo)
            {
                if(mTextureView == null)
                    return;

              Thread thread =new Thread(new Runnable() {
                  @Override
                  public void run() {

                      try {
                          if(mIsRecordingVideo)
                          {
                              if(mframetorecordcount == currentframenumber || (videokey.trim().isEmpty()))
                              {
                                  Bitmap bitmap = mTextureView.getBitmap(10,10);
                                  bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(),
                                          bitmap.getHeight(), mTextureView.getTransform( null ), true );
                                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                  bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
                                  byte[] byteArray = stream.toByteArray();
                                  if(videokey.trim().isEmpty())
                                  {
                                      randomstring gen = new randomstring(20, ThreadLocalRandom.current());
                                      videokey=gen.nextString();
                                      String keyvalue= getkeyvalue(byteArray);
                                      savevideostart(videokey,keytype,keyvalue);
                                    //  xapistartvideo(keyvalue);
                                  }

                                  if(mframetorecordcount == currentframenumber)
                                  {
                                      updatelistitemnotify(byteArray,currentframenumber,"Frame");
                                      currentframenumber = currentframenumber + frameduration;
                                  }
                                  bitmap.recycle();
                              }

                              mframetorecordcount++;
                         //     Log.e("Frame no ",""+mframetorecordcount);
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
    private Size mPreviewSize;
    /**
     * The {@link android.util.Size} of video recording.
     */
    private Size mVideoSize;
    /**
     * Camera preview.
     */
    private CaptureRequest.Builder mPreviewBuilder;
    /**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;
    /**
     * Whether the app is recording video now
     */
    public boolean mIsRecordingVideo=false;
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;
    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
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

    LinearLayout layout_bottom,layout_drawer;

    RecyclerView recyview_hashes;
    RecyclerView recyview_metrices;
    ImageView handleimageview,righthandle;
    LinearLayout linearLayout;
    FrameLayout fragment_graphic_container;
    boolean isflashon = false,inPreview = true;

    TextView txtSlot1;
    TextView txtSlot2;
    TextView txtSlot3,txt_metrics,txt_hashes;
    ScrollView scrollview_metrices,scrollview_hashes;

    ImageView mrecordimagebutton,imgflashon,rotatecamera,handle;

    public Dialog maindialogshare,subdialogshare;
    View rootview = null;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler timerhandler;
    int Seconds, Minutes, MilliSeconds ;
    String keytype =config.prefs_md5,currenthashvalue="";
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    ArrayList<frameinfo> muploadframelist =new ArrayList<>();
    long currentframenumber =0;
    long frameduration =15, mframetorecordcount =0,apicallduration=5,apicurrentduration=0;
    public boolean autostartvideo=false,camerastatusok=false;
    adapteritemclick madapterclick;
    File lastrecordedvideo=null;
    String selectedvideofile ="",videokey="",selectedmetrices="", selectedhashes ="";
    int metriceslastupdatedposition=0;
    private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    videoframeadapter mmetricesadapter,mhashesadapter;

    databasemanager mdbhelper;
    private boolean isdraweropen=false,isgraphicalshown=false;
    private Handler myHandler;
    private Runnable myRunnable;
    private int lastmetricescount=0;
    graphicalfragment fragmentgraphic;
    private boolean issavedtofolder=false;
    JSONArray metadatametricesjson=new JSONArray();
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
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this,rootview);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mTextureView = (AutoFitTextureView) rootview.findViewById(R.id.texture);
            mrecordimagebutton = (ImageView) rootview.findViewById(R.id.img_video_capture);
            imgflashon = (ImageView) rootview.findViewById(R.id.img_flash_on);
            rotatecamera = (ImageView) rootview.findViewById(R.id.img_rotate_camera);
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
            mrecordimagebutton.setOnClickListener(this);
            imgflashon.setOnClickListener(this);
            rotatecamera.setOnClickListener(this);

            if(! xdata.getinstance().getSetting(config.frameupdateevery).trim().isEmpty())
                apicallduration=Long.parseLong(xdata.getinstance().getSetting(config.frameupdateevery));

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

            timerhandler = new Handler() ;
            gethelper().updateheader("00:00:00");
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

            mTextureView.setOnTouchListener(this);
            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);

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
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                recyview_metrices.setLayoutManager(mLayoutManager);
                recyview_metrices.setItemAnimator(new DefaultItemAnimator());
                recyview_metrices.setAdapter(mmetricesadapter);
            }

            setmetriceshashesdata();
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

    public boolean isvideorecording()
    {
        return mIsRecordingVideo;
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

            case  R.id.texture:
                try {
                    Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                    if (rect == null) return false;
                    float currentFingerSpacing;

                    if (motionEvent.getPointerCount() == 2) { //Multi touch.
                        currentFingerSpacing = getFingerSpacing(motionEvent);
                        float delta = 0.05f;
                        if (fingerSpacing != 0) {
                            if (currentFingerSpacing > fingerSpacing) {
                                if ((maximumZoomLevel - zoomLevel) <= delta) {
                                    delta = maximumZoomLevel - zoomLevel;
                                }
                                zoomLevel = zoomLevel + delta;
                            } else if (currentFingerSpacing < fingerSpacing){
                                if ((zoomLevel - delta) < 1f) {
                                    delta = zoomLevel - 1f;
                                }
                                zoomLevel = zoomLevel - delta;
                            }
                            float ratio = (float) 1 / zoomLevel;
                            int croppedWidth = rect.width() - Math.round((float)rect.width() * ratio);
                            int croppedHeight = rect.height() - Math.round((float)rect.height() * ratio);
                            zoom = new Rect(croppedWidth/2, croppedHeight/2,
                                    rect.width() - croppedWidth/2, rect.height() - croppedHeight/2);
                            mPreviewBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
                        }
                        fingerSpacing = currentFingerSpacing;
                    } else if(!mIsRecordingVideo){
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                    if(layout_bottom.getVisibility() == View.VISIBLE) {
                                        hideshowcontroller(false);
                                        Log.e("layout visibale","layout visibale");
                                    }
                                    else {
                                        hideshowcontroller(true);

                                        Log.e("layout hide","layout hide");
                                    }
                        }
                    }

                    else {

                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                if(mIsRecordingVideo && (!(isdraweropen)))
                                {
                                    if(layout_bottom.getVisibility() == View.VISIBLE)
                                    {
                                        hideshowcontroller(false);
                                        Log.e("layout visibale","layout visibale");
                                    }
                                    else
                                    {
                                        hideshowcontroller(true);

                                        Log.e("layout hide","layout hide");

                                    }
                                }
                        }
                    }
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                    return true;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                break;
        }
        return true;
    }

    public void hideshowcontroller(boolean shouldshow)
    {
        if(isdraweropen)
            return;

        if(shouldshow)
        {
            gethelper().updateactionbar(1);
            layout_bottom.setVisibility(View.VISIBLE);
            handleimageview.setVisibility(View.VISIBLE);
        }
        else
        {
            gethelper().updateactionbar(0);
            layout_bottom.setVisibility(View.GONE);
            handleimageview.setVisibility(View.GONE);
        }
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener()
    {
        private static final int flingactionmindstvac = 60;
        private static final int flingactionmindspdvac = 100;

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
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    private void openCamera(int width, int height) {
        final Activity activity = applicationviavideocomposer.getactivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
           // cameraId = manager.getCameraIdList()[0];
            // Choose the sizes for camera preview and video recording
            characteristics = manager.getCameraCharacteristics(cameraId);

            maximumZoomLevel = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize);
            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (sensorOrientation == 270) {
                // Camera is mounted the wrong way...
                upsideDown = true;
            }

            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
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
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }
    /**
     * Start the camera preview.
     */
    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<Surface>();
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }
    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();


        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }


    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(getVideoFile(activity).getAbsolutePath());
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(15);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
       // int orientation = ORIENTATIONS.get(rotation);
        int orientation =  camerautil.getOrientation(rotation, upsideDown);

        mMediaRecorder.setOrientationHint(orientation);
        mMediaRecorder.prepare();
    }
    private File getVideoFile(Context context) {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file=new File(config.videodir, fileName+".mp4");

        File destinationDir=new File(config.videodir);
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
            // UI
            videokey="";
            issavedtofolder=false;
            mIsRecordingVideo = true;
            // Start recording
            mMediaRecorder.start();
            startvideotimer();
            mrecordimagebutton.setEnabled(true);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    private void stopRecordingVideo() {
        // UI
        issavedtofolder=true;
        mIsRecordingVideo = false;
        lastrecordedvideo=new File(selectedvideofile);
        /*Activity activity = getActivity();
        if (null != activity) {
            Toast.makeText(activity, "Video saved: " + getVideoFile(activity),
                    Toast.LENGTH_SHORT).show();
        }*/

        try {
            mPreviewSession.stopRepeating();
            mPreviewSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    mMediaRecorder.stop();
                }catch(RuntimeException stopException){
                    //handle cleanup here
                }
                mMediaRecorder.reset();

                try {
                    MetaDataInsert.writeRandomMetadata(lastrecordedvideo.getAbsolutePath(),""+metadatametricesjson.toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                startPreview();
                stopvideotimer();
                madapterclick.onItemClicked(null,1);
                mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
                layout_bottom.setVisibility(View.GONE);

                resetvideotimer();
                clearvideolist();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        savevideocomplete();
                        //common.exportvideo(lastrecordedvideo,false);
                        setvideoadapter();
                    }
                }).start();

                mrecordimagebutton.setEnabled(true);
                showsharepopupmain();
                setmetricesadapter();
            }
        },100);
    }

    public void setvideoadapter() {
        int count = 1;
        currentframenumber=0;
        selectedhashes="";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mhashesitems.clear();
                mhashesadapter.notifyDataSetChanged();
                mvideoframes.clear();
            }
        });

        currentframenumber = currentframenumber + frameduration;
        try
        {
            customffmpegframegrabber grabber = new customffmpegframegrabber(lastrecordedvideo.getAbsolutePath());

            grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            String format= common.getvideoformat(lastrecordedvideo.getAbsolutePath());
            if(format.equalsIgnoreCase("mp4"))
                grabber.setFormat(format);

            grabber.start();
            videomodel lastframehash=null;
            for(int i = 0; i<grabber.getLengthInFrames(); i++){
                Frame frame = grabber.grabImage();
                if (frame == null)
                    break;

                ByteBuffer buffer= ((ByteBuffer) frame.image[0].position(0));
                byte[] byteData = new byte[buffer.remaining()];
                buffer.get(byteData);
                String keyValue= getkeyvalue(byteData);
                if (count == currentframenumber) {
                    lastframehash=null;
                    mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));
                    currentframenumber = currentframenumber + frameduration;
                }
                else
                {
                    lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                }
                count++;
            }

            if(lastframehash != null)
            {
                mvideoframes.add(lastframehash);
            }
            else
            {
                if(mvideoframes.size() > 1)
                    mvideoframes.get(mvideoframes.size()-1).settitle("Last Frame ");
            }

            grabber.flush();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mhashesitems.clear();
                    mhashesadapter.notifyDataSetChanged();
                    for(int i=0;i<mvideoframes.size();i++)
                        mhashesitems.add(mvideoframes.get(i));

                    mhashesadapter.notifyDataSetChanged();
                    recyview_hashes.scrollToPosition(mhashesitems.size()-1);
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_video_capture: {
                startstopvideo();
                break;
            }

            case R.id.img_flash_on:
                navigateflash();
                break;

            case R.id.img_rotate_camera:
                switchCamera();
                break;
            case R.id.txt_slot1:
                scrollview_metrices.setVisibility(View.INVISIBLE);
                scrollview_hashes.setVisibility(View.INVISIBLE);

                recyview_hashes.setVisibility(View.VISIBLE);
                recyview_metrices.setVisibility(View.INVISIBLE);
                fragment_graphic_container.setVisibility(View.INVISIBLE);

                txt_metrics.setVisibility(View.INVISIBLE);
                txt_hashes.setVisibility(View.INVISIBLE);
                txt_metrics.setVisibility(View.INVISIBLE);
                resetButtonViews(txtSlot1,txtSlot2,txtSlot3);
                break;

            case R.id.txt_slot2:
                scrollview_metrices.setVisibility(View.INVISIBLE);
                scrollview_hashes.setVisibility(View.INVISIBLE);
                fragment_graphic_container.setVisibility(View.INVISIBLE);

                txt_hashes.setVisibility(View.INVISIBLE);
                txt_metrics.setVisibility(View.INVISIBLE);

                recyview_metrices.setVisibility(View.VISIBLE);
                recyview_hashes.setVisibility(View.INVISIBLE);

                resetButtonViews(txtSlot2,txtSlot1,txtSlot3);
                break;

            case R.id.txt_slot3:
                fragment_graphic_container.setVisibility(View.VISIBLE);
                scrollview_metrices.setVisibility(View.INVISIBLE);
                scrollview_hashes.setVisibility(View.INVISIBLE);
                recyview_metrices.setVisibility(View.INVISIBLE);
                recyview_hashes.setVisibility(View.INVISIBLE);
                txt_hashes.setVisibility(View.INVISIBLE);
                txt_metrics.setVisibility(View.INVISIBLE);
                resetButtonViews(txtSlot3,txtSlot1,txtSlot2);

                break;
        }
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
        if(fragmentgraphic != null)
            fragmentgraphic.locationupdate(location,currenthashvalue);
    }

    public void startstopvideo()
    {
        if (mIsRecordingVideo) {
            mrecordimagebutton.setEnabled(false);
            gethelper().updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
            layout_bottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
            stopRecordingVideo();
        } else {
            txt_hashes.setText("");
            txt_metrics.setText("");
            selectedhashes="";
            selectedmetrices="";
            mmetricsitems.clear();
            mhashesitems.clear();
            mmetricesadapter.notifyDataSetChanged();
            mhashesadapter.notifyDataSetChanged();

            mrecordimagebutton.setEnabled(false);
            mrecordimagebutton.setImageResource(R.drawable.shape_recorder_on);
            imgflashon.setVisibility(View.VISIBLE);
            rotatecamera.setVisibility(View.INVISIBLE);

            apicurrentduration =0;
            currentframenumber =0;
            mframetorecordcount =0;
            currentframenumber = currentframenumber + frameduration;

            mvideoframes.clear();
            gethelper().updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal_transparent));
            layout_bottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal_transparent));
            startRecordingVideo();
        }
    }



    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void setData(boolean autostartvideo,adapteritemclick madapterclick) {
        this.autostartvideo = autostartvideo;
        this.madapterclick = madapterclick;
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
    public void onResume() {
        super.onResume();

        hideshowcontroller(true);
        stopvideotimer();
        resetvideotimer();
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            String[] neededpermissions = {
                    Manifest.permission.CAMERA,
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
        gethelper().updateheader("00:00:00");
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

        if(fragmentgraphic == null)
        {
            fragmentgraphic  = new graphicalfragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_graphic_container,fragmentgraphic);
            transaction.commit();
        }

        camerastatusok=true;
        layout_bottom.setVisibility(View.VISIBLE);
        mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
        gethelper().updateheader("00:00:00");

        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        }


        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(autostartvideo)
                {
                    autostartvideo=false;
                    startstopvideo();
                }
            }
        },1000);


    }

    @Override
    public void onPause() {
        if(camerastatusok)
        {
            closeCamera();
            stopBackgroundThread();
            stopvideotimer();
            mIsRecordingVideo=false;
        }
        super.onPause();
    }

    /// camera2video code end


    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_menu:
                gethelper().onBack();
                break;
        }
    }

    public void startvideotimer()
    {
        StartTime = SystemClock.uptimeMillis();
        timerhandler.postDelayed(runnable, 0);
    }

    public void stopvideotimer()
    {
        TimeBuff += MillisecondTime;
        timerhandler.removeCallbacks(runnable);
    }

    public void resetvideotimer()
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
                            if(mIsRecordingVideo)
                            {
                                gethelper().updateheader("" + String.format("%02d", Minutes) + ":"
                                        + String.format("%02d", Seconds) + ":"
                                        + String.format("%02d", (MilliSeconds/10)));
                            }

                        }
                    });
                }
            }).start();
            timerhandler.postDelayed(this, 0);
        }
    };



    public void clearvideolist()
    {
        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout_bottom.setVisibility(View.VISIBLE);
                imgflashon.setVisibility(View.VISIBLE);
                rotatecamera.setVisibility(View.VISIBLE);
            }
        });
    }


    public void xapistartvideo(final String hashvalue)
    {
        Log.e("videokey"," videokey "+videokey);

        HashMap<String,String> mpairslist=new HashMap<String, String>();
        mpairslist.put("html","0");
        mpairslist.put("hashmethod",""+keytype);
        mpairslist.put("hashvalue",""+hashvalue);
        mpairslist.put("title","xx");
        gethelper().xapipost_send(getActivity(),config.type_video_start,mpairslist, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                if(response.isSuccess())
                {
                    try {
                        JSONObject object = (JSONObject) response.getData();
                        videokey=object.getString("key");
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    savevideostart(videokey,keytype,hashvalue);
                }
            }
        });
    }

    public void updatelistitemnotify(final byte[] array, final long framenumber, final String message)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(array == null || array.length == 0)
                    return;
                final String keyvalue= getkeyvalue(array);
                currenthashvalue=keyvalue;
                apicurrentduration++;

                JSONArray metricesarray=new JSONArray();
                metadatametricesjson=new JSONArray();

                metricItemArraylist.clear();
                metricItemArraylist.addAll(gethelper().getmetricarraylist());
                for(int j=0;j<metricItemArraylist.size();j++)
                {
                    if(metricItemArraylist.get(j).isSelected())
                    {
                        String value=metricItemArraylist.get(j).getMetricTrackValue();
                        common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),value,true);

                        if(metricItemArraylist.get(j).getMetricTrackValue().trim().isEmpty() ||
                                metricItemArraylist.get(j).getMetricTrackValue().equalsIgnoreCase("null"))
                        {
                            value="N/A";
                        }
                        selectedmetrices=selectedmetrices+"\n"+metricItemArraylist.get(j).getMetricTrackKeyName()+" - "+value;

                        JSONObject object=new JSONObject();
                        try {
                            object.put(metricItemArraylist.get(j).getMetricTrackKeyName(),value);
                            metricesarray.put(object);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(fragmentgraphic != null)
                        {
                            fragmentgraphic.setmetricesdata();
                            fragmentgraphic.currenthashvalue=keyvalue;
                        }
                    }
                });

                metadatametricesjson.put(metricesarray);

                if(! selectedmetrices.trim().isEmpty())
                    selectedmetrices=selectedmetrices+"\n\n";
                mvideoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));
                muploadframelist.add(new frameinfo(""+framenumber,"xxx",keyvalue,keytype,false,metricItemArraylist));

                if(! selectedhashes.trim().isEmpty())
                    selectedhashes=selectedhashes+"\n";

                selectedhashes =selectedhashes+mvideoframes.get(mvideoframes.size()-1).getframeinfo();
                if(apicurrentduration > apicallduration)
                    apicurrentduration=apicallduration;

                if(apicurrentduration == apicallduration)
                    savevideoupdate(metricItemArraylist);


                Log.e("current call, calldur ",apicurrentduration+" "+apicallduration);
                if(apicurrentduration == apicallduration)
                {
                    apicurrentduration=0;
                }
            }
        }).start();
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
                    if((recyview_hashes.getVisibility() == View.VISIBLE) && (! selectedhashes.trim().isEmpty()))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mhashesitems.add(new videomodel(selectedhashes));
                                mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                                selectedhashes="";
                            }
                        });

                    }

                    if((recyview_metrices.getVisibility() == View.VISIBLE) && (! selectedmetrices.trim().isEmpty()))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mmetricsitems.add(new videomodel(selectedmetrices));
                                mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                                selectedmetrices="";
                            }
                        });
                    }

                    if((fragment_graphic_container.getVisibility() == View.VISIBLE))
                        graphicopen=true;
                }

                if(fragmentgraphic != null)
                    fragmentgraphic.setdrawerproperty(graphicopen);

                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }

    public void setmetricesadapter()
    {
        if(selectedmetrices.toString().trim().length() > 0)
        {
            mmetricsitems.add(new videomodel(selectedmetrices));
            recyview_metrices.post(new Runnable() {
                @Override
                public void run() {
                    mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                    selectedmetrices="";
                }
            });
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if(! issavedtofolder && common.getdeniedpermissions().isEmpty() && (selectedvideofile != null )
                    && new File(selectedvideofile).exists())
                common.deletevideofile(selectedvideofile);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }

    public void savevideoupdate(ArrayList<metricmodel> mmetriceslist)
    {
        JSONArray hasharray=new JSONArray();
        JSONArray metricesarray=new JSONArray();
        {

            for(int i=0;i<muploadframelist.size();i++)
            {
                JSONObject object=new JSONObject();
                try {
                    object.put("framenumber",muploadframelist.get(i).getFramenumber());
                    object.put("meta",muploadframelist.get(i).getMeta());
                    object.put("hashvalue",muploadframelist.get(i).getHashvalue());
                    object.put("hashmethod",muploadframelist.get(i).getHashmethod());
                    hasharray.put(object);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            muploadframelist.clear();
            Log.e("Json array ",""+hasharray.toString());
        }

        {

            for(int i=0;i<mmetriceslist.size();i++)
            {
                JSONObject object=new JSONObject();
                try {
                    object.put(mmetriceslist.get(i).getMetricTrackKeyName(),mmetriceslist.get(i).getMetricTrackValue());
                    metricesarray.put(object);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (mdbhelper == null) {
            mdbhelper = new databasemanager(getActivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mdbhelper.insertframemetricesinfo(videokey,hasharray.toString(),""+metricesarray.toString(),
                    "","",config.type_video_update);

            mdbhelper.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savevideostart(String videokey,String hashmethod,String hashvalue)
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(getActivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mdbhelper.insertframemetricesinfo(videokey,hashmethod,hashvalue,hashmethod,hashvalue,config.type_video_start);

            mdbhelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savevideocomplete()
    {
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mdbhelper.insertframemetricesinfo(videokey,"","","","",config.type_video_complete);

            mdbhelper.close();
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
        txt_share_btn2.setText(getResources().getString(R.string.new_video));
        txt_share_btn3.setText(getResources().getString(R.string.watch));

        txt_title1.setText(getResources().getString(R.string.video_has_been_encrypted));
        txt_title2.setText(getResources().getString(R.string.congratulations_video));

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

                videoplayercomposerfragment fullvdofragmnet=new videoplayercomposerfragment();
                fullvdofragmnet.setdata(lastrecordedvideo.getAbsolutePath());
                gethelper().addFragment(fullvdofragmnet,false,true);
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
        txt_share_btn2.setText(getResources().getString(R.string.share_partial_video));
        txt_share_btn3.setText(getResources().getString(R.string.cancel_viewlist));

        txt_title1.setText(getResources().getString(R.string.how_would_you));
        txt_title2.setText("");

        common.changeFocusStyle(txt_share_btn1,getResources().getColor(R.color.share_a),5);
        common.changeFocusStyle(txt_share_btn2,getResources().getColor(R.color.share_b),5);
        common.changeFocusStyle(txt_share_btn3,getResources().getColor(R.color.share_c),5);

        txt_share_btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(subdialogshare != null && subdialogshare.isShowing())
                            subdialogshare.dismiss();

                        common.exportvideo(lastrecordedvideo,true);
                        launchvideolist();

                    }
                },100);
            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

                Uri selectedimageuri =Uri.fromFile(new File(lastrecordedvideo.getAbsolutePath()));

                final MediaPlayer mp = MediaPlayer.create(applicationviavideocomposer.getactivity(),selectedimageuri);
                if(mp != null)
                {
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            int duration = mp.getDuration();
                            videoplayfragment videoplayfragment =new videoplayfragment();
                            videoplayfragment.setdata(lastrecordedvideo.getAbsolutePath(), duration);
                            gethelper().replaceFragment(videoplayfragment, false, true);
                        }
                    });
                }
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

    private void navigateflash() {
        try {
            if(isflashon) {
                imgflashon.setImageResource(R.drawable.flash_off);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                isflashon = false;
            } else {
                imgflashon.setImageResource(R.drawable.flash_on);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                isflashon = true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void switchCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            closeCamera();
            reopenCamera();

        } else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            closeCamera();
            reopenCamera();
        }
    }

    public void reopenCamera() {
        if (mTextureView.isAvailable())
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());

    }

}

