package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.mediaqualityadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.mediacompletiondialogmain;
import com.deeptruth.app.android.models.mediacompletiondialogsub;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.xdata;
import com.google.gson.Gson;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 5/11/18.
 */

public class imagecomposerfragment extends basefragment  implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener{



    @BindView(R.id.layout_seekbarzoom)
    RelativeLayout layout_seekbarzoom;
    @BindView(R.id.seekbarzoom)
    IndicatorSeekBar seekbarzoom;
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
    @BindView(R.id.txt_media_quality)
    TextView txt_media_quality;
    @BindView(R.id.txt_zoomlevel)
    TextView txt_zoomlevel;

    mediaqualityadapter qualityadapter;
    List<String> qualityitemslist=new ArrayList<>();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    private static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;


    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraid = CAMERA_BACK,selectedmediaquality="";
    private boolean isflashsupported=false,isvisibletouser=false;;
    private boolean isflashon = false;
    protected float fingerSpacing = 0;
    protected float zoomLevel = 1f;
    protected float maximumZoomLevel;
    protected Rect rectzoom;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    private static final int request_permissions = 1;
    private CameraCharacteristics characteristics;
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File capturedimagefile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), capturedimagefile));
        }

    };

    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported,brustmodeenabled=false;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    LinearLayout linearLayout;
    TextView txt_title_actionbarcomposer;

    ImageView imgflashon,img_dotmenu,img_warning,img_close,img_stop_watch;

    public Dialog maindialogshare,subdialogshare;
    View rootview = null;
    Handler timerhandler;
    String keytype = config.prefs_md5;
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    adapteritemclick madapterclick;
    String selectedvideofile ="",selectedmetrices="", selectedhashes ="",mediakey="";
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private Date zoomcontrollertimeout=new Date();
    private boolean isdraweropen=false;
    private Handler myhandler;
    private Runnable myrunnable;
    JSONObject metadatametricesjson=new JSONObject();
    private LinearLayoutManager mLayoutManager;
    public int flingactionmindstvac;
    private  final int flingactionmindspdvac = 10;
    ArrayList<dbitemcontainer> mdbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> mdbmiddleitemcontainer =new ArrayList<>();

    mediacompletiondialogmain mediacompletionpopupmain;
    mediacompletiondialogsub mediacompletionpopupsub;
    FragmentManager fm ;
    adapteritemclick popupclickmain;
    adapteritemclick popupclicksub;
    String hashvalue = "",metrichashvalue = "";
    RelativeLayout layoutbottom;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        capturestillpicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_INACTIVE == afState /*add this*/) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            capturestillpicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        capturestillpicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @BindView(R.id.linear_header)
    LinearLayout linearheader;


    public static imagecomposerfragment newInstance() {
        return new imagecomposerfragment();
    }

    @Override
    public int getlayoutid() {
        return R.layout.imagecapturefragment;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootview);
        gethelper().drawerenabledisable(true);
        gethelper().setdatacomposing(true);

        mTextureView = (AutoFitTextureView)rootview.findViewById(R.id.texture);
        imgflashon = (ImageView) rootview.findViewById(R.id.img_flash);
        img_dotmenu = (ImageView) rootview.findViewById(R.id.img_dotmenu);
        img_stop_watch = (ImageView) rootview.findViewById(R.id.img_stop_watch);
        img_warning= (ImageView) rootview.findViewById(R.id.img_warning);
        img_close = (ImageView) rootview.findViewById(R.id.img_close);
        txt_title_actionbarcomposer = (TextView) rootview.findViewById(R.id.txt_title_actionbarcomposer);
        linearLayout=rootview.findViewById(R.id.content);

        timerhandler = new Handler() ;
        mTextureView.setOnTouchListener(this);
        imgflashon.setOnClickListener(this);
        img_dotmenu.setOnClickListener(this);
        img_stop_watch.setOnClickListener(this);
        img_warning.setOnClickListener(this);
        img_close.setOnClickListener(this);
        txt_media_quality.setOnClickListener(this);
        txt_media_low.setOnClickListener(this);
        txt_media_medium.setOnClickListener(this);
        txt_media_high.setOnClickListener(this);
        txt_zoomlevel.setOnClickListener(this);

        img_dotmenu.setVisibility(View.VISIBLE);
        imgflashon.setVisibility(View.VISIBLE);
        img_stop_watch.setVisibility(View.VISIBLE);

        flingactionmindstvac=common.getdrawerswipearea();
        keytype=common.checkkey();
        brustmodeenabled=false;
        img_stop_watch.setImageResource(R.drawable.stopwatch);

        txt_title_actionbarcomposer.setText("");

        seekbarzoom.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                zoomcontrollertimeout=new Date();
                if(seekParams.fromUser)
                {
                    zoomLevel=seekParams.progressFloat;
                    setupcamerazoom();
                    fadeinzoomcontrollers();
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        expandable_layout.setVisibility(View.VISIBLE);
        txt_media_quality.setVisibility(View.VISIBLE);

        /*spinner_mediaquality.setVisibility(View.VISIBLE);
        qualityadapter = new mediaqualityadapter(applicationviavideocomposer.getactivity(),
                R.layout.row_mediaqualityadapter,qualityitemslist);
        spinner_mediaquality.setAdapter(qualityadapter);
        spinner_mediaquality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id)
            {
                if(! selectedmediaquality.equalsIgnoreCase(qualityitemslist.get(position)))
                {
                    selectedmediaquality=qualityitemslist.get(position);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        setmetriceshashesdata();
        /*setqualitydropdown();

        if(qualityitemslist.size() > 1)
            spinner_mediaquality.setSelection(1,true);*/

        txt_media_quality.setText(config.mediaquality720);
        txt_media_low.setText(config.mediaquality480);
        txt_media_medium.setText(config.mediaquality720);
        txt_media_high.setText(config.mediaquality1080);
        txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));

        expandable_layout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                // 0 closed, 3 opened
                if(state == 0)
                {
                    expandable_layout.setVisibility(View.GONE);
                    actionbarcomposer.setVisibility(View.VISIBLE);
                }
                else if(state == 3)
                {
                    txt_media_low.setVisibility(View.VISIBLE);
                    txt_media_medium.setVisibility(View.VISIBLE);
                    txt_media_high.setVisibility(View.VISIBLE);
                }

                Log.d("ExpandableLayout", "State: " + state);
            }
        });

        layout_seekbarzoom.setVisibility(View.GONE);
        return rootview;
    }


    public void setqualitydropdown()
    {
        qualityitemslist.clear();
        qualityadapter.notifyDataSetChanged();

        if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
            qualityitemslist.add(config.mediaquality480);

        if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P))
            qualityitemslist.add(config.mediaquality720);

        if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P))
            qualityitemslist.add(config.mediaquality1080);

        qualityadapter.notifyDataSetChanged();

    }

    public void changeiconsorientation(float rotateangle)
    {
        if(imgflashon != null)
            imgflashon.setRotation(rotateangle);

        if(img_dotmenu != null)
            img_dotmenu.setRotation(rotateangle);

        if(img_warning != null)
            img_warning.setRotation(rotateangle);

        if(img_close != null)
            img_close.setRotation(rotateangle);

        if(img_stop_watch != null)
            img_stop_watch.setRotation(rotateangle);

        if(txt_media_quality != null)
            txt_media_quality.setRotation(rotateangle);
    }


    // Initilize when get 1st frame
    public void savestartmediainfo()
    {
        try {

            String currenttimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mediakey=currenttimestamp;
            Log.e("localkey ",mediakey);

            String currenttimewithoffset[] = common.getcurrentdatewithtimezone();
            String devicestartdate = currenttimewithoffset[0];
            String timeoffset = currenttimewithoffset[1];

            if(mdbstartitemcontainer.size() == 0)
            {
                mdbstartitemcontainer.add(new dbitemcontainer("","image",capturedimagefile.getAbsolutePath(), mediakey,"","","0","0",
                        config.type_image_start,devicestartdate,devicestartdate,timeoffset,"","","",
                        xdata.getinstance().getSetting(config.selected_folder)));
                Log.e("startcontainersize"," "+mdbstartitemcontainer.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Calling after 1 by 1 frame duration.
    public void savemediaupdate(JSONArray metricesjsonarray,String sequenceno,String sequencehash)
    {
        String currentdate[] = common.getcurrentdatewithtimezone();
        try {
            try {
                if(metricesjsonarray != null && metricesjsonarray.length() > 0)
                {
                    JSONObject arrayobject=metricesjsonarray.getJSONObject(0);
                    arrayobject.put("interim_identifying_hashes","");
                    arrayobject.put("distancetravelled","0");
                }
                String metrichash = md5.calculatestringtomd5(metricesjsonarray.toString());
                mdbmiddleitemcontainer.add(new dbitemcontainer("", metrichash ,keytype, mediakey,""+metricesjsonarray.toString(),
                        currentdate[0],"0",sequencehash,sequenceno,"",currentdate[0],"",""));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setimagehash() throws FileNotFoundException
    {
        try {
            String keyvalue="";
            for(int i=0;i<5;i++)
            {
                keyvalue =  md5.fileToMD5(capturedimagefile.getAbsolutePath());
                if(keyvalue.trim().length() > 0)
                    break;
            }

            selectedhashes =  keyvalue;
            selectedhashes=keytype+" : "+selectedhashes;
            Log.e("imagekeyhash = ","" +selectedhashes);
            hashvalue = selectedhashes;

            ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
            getselectedmetrics(mlocalarraylist);

            JSONArray metricesarray=new JSONArray();
            metricesarray.put(metadatametricesjson);

            metrichashvalue = md5.calculatestringtomd5(metadatametricesjson.toString());

            savestartmediainfo();
            savemediaupdate(metricesarray,"1",keyvalue);

            insertstartmediainfo(keyvalue);
            Gson gson = new Gson();
            String list1 = gson.toJson(mdbstartitemcontainer);
            String list2 = gson.toJson(mdbmiddleitemcontainer);
            xdata.getinstance().saveSetting("liststart",list1);
            xdata.getinstance().saveSetting("listmiddle",list2);
            xdata.getinstance().saveSetting("mediapath",capturedimagefile.getAbsolutePath());
            xdata.getinstance().saveSetting("keytype",keytype);

            Intent intent = new Intent(applicationviavideocomposer.getactivity(), insertmediadataservice.class);
            applicationviavideocomposer.getactivity().startService(intent);

            mhashesitems.clear();

            mhashesitems.add(new videomodel(selectedhashes));

            if(madapterclick != null)
                madapterclick.onItemClicked(capturedimagefile.getAbsolutePath(),2);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void insertstartmediainfo(String firsthash)
    {
        if(capturedimagefile != null)
        {
            String medianame=common.getfilename(capturedimagefile.getAbsolutePath());
            String[] split=medianame.split("\\.");
            if(split.length > 0)
                medianame=split[0];

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps","1");
            map.put("firsthash", firsthash);
            map.put("hashmethod",keytype);
            map.put("name",medianame);
            map.put("duration","1");
            map.put("framecounts","1");
            map.put("finalhash",firsthash);

            Gson gson = new Gson();
            String json = gson.toJson(map);

            String updatecompletedate[] = common.getcurrentdatewithtimezone();
            String completeddate = updatecompletedate[0];

            mdbstartitemcontainer.get(0).setItem1(json);
            mdbstartitemcontainer.get(0).setItem3(capturedimagefile.getAbsolutePath());
            mdbstartitemcontainer.get(0).setItem15(capturedimagefile.getAbsolutePath());
            mdbstartitemcontainer.get(0).setItem13(completeddate);

            if(mdbstartitemcontainer != null && mdbstartitemcontainer.size() > 0)
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

                mdbhelper.insertstartvideoinfo(mdbstartitemcontainer.get(0).getItem1(),mdbstartitemcontainer.get(0).getItem2()
                        ,mdbstartitemcontainer.get(0).getItem3(),mdbstartitemcontainer.get(0).getItem4(),mdbstartitemcontainer.get(0).getItem5()
                        ,mdbstartitemcontainer.get(0).getItem6(),mdbstartitemcontainer.get(0).getItem7(),mdbstartitemcontainer.get(0).getItem8(),
                        mdbstartitemcontainer.get(0).getItem9(),mdbstartitemcontainer.get(0).getItem10(),mdbstartitemcontainer.get(0).getItem11()
                        ,mdbstartitemcontainer.get(0).getItem12(),mdbstartitemcontainer.get(0).getItem13(),"",mdbstartitemcontainer.get(0).getItem14()
                        ,"0","sync_pending","","","0","inprogress",medianame,"",
                        mdbstartitemcontainer.get(0).getItem16(),"");

                try {
                    mdbhelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

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

        if(deniedpermissions.isEmpty()){
            doafterallpermissions();
        }else {
                String[] array = new String[deniedpermissions.size()];
                array = deniedpermissions.toArray(array);
                ActivityCompat.requestPermissions(getActivity(), array, request_permissions);
            }
        }


        public void doafterallpermissions()
        {
            startBackgroundThread();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            }
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        if(layout_seekbarzoom != null)
            layout_seekbarzoom.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);
    }

    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics
                    = manager.getCameraCharacteristics(cameraid);
            Boolean availableflash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            isflashsupported = availableflash == null ? false : availableflash;
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, /*maxImages*/2);
            mImageReader.setOnImageAvailableListener(
                    mOnImageAvailableListener, mBackgroundHandler);
            int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            boolean swappedDimensions = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                        swappedDimensions = true;
                    }
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                        swappedDimensions = true;
                    }
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }

            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            int rotatedPreviewWidth = width;
            int rotatedPreviewHeight = height;
            int maxPreviewWidth = displaySize.x;
            int maxPreviewHeight = displaySize.y;

            if (swappedDimensions) {
                rotatedPreviewWidth = height;
                rotatedPreviewHeight = width;
                maxPreviewWidth = displaySize.y;
                maxPreviewHeight = displaySize.x;
            }

            if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                maxPreviewWidth = MAX_PREVIEW_WIDTH;
            }

            if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                maxPreviewHeight = MAX_PREVIEW_HEIGHT;
            }

            // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
            // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
            // garbage capture data.
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                    maxPreviewHeight, largest);

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(
                        mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(
                        mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            // Check if the flash is supported.
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mFlashSupported = available == null ? false : available;

//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
           // requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager  manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        // cameraId = manager.getCameraIdList()[0];
        // Choose the sizes for camera preview and video recording
        try {
            characteristics = manager.getCameraCharacteristics(cameraid);
            maximumZoomLevel = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            seekbarzoom.setMax(maximumZoomLevel);
            seekbarzoom.setMin(1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraid, mStateCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            if(mCameraOpenCloseLock != null)
            {
                mCameraOpenCloseLock.acquire();
                if (null != mCaptureSession) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
                if (null != mImageReader) {
                    mImageReader.close();
                    mImageReader = null;
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
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
        if(mBackgroundThread != null)
        {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                //setAutoFlash(mPreviewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");


                        }
                    }, null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
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
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    public void takePicture() {
        //lockFocus();

        if(madapterclick != null)
            madapterclick.onItemClicked(null,1);

        getImageFile(getActivity());
        capturestillpicture();

        brustmodeenabled=false;
        img_stop_watch.setImageResource(R.drawable.stopwatch);
    }

    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void capturestillpicture() {
        try {

            if(expandable_layout != null && expandable_layout.isExpanded())
                expendcollpaseview();

            final Activity activity = getActivity();
            metadatametricesjson=new JSONObject();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            selectedmetrices="";
            mdbstartitemcontainer.clear();
            mdbmiddleitemcontainer.clear();
            mmetricsitems.clear();

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            if(rectzoom != null)
                captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, rectzoom);

            int qualityvalue=100;
            if(selectedmediaquality.equalsIgnoreCase(config.mediaquality480))
            {
                qualityvalue=60;
            }
            else if(selectedmediaquality.equalsIgnoreCase(config.mediaquality720))
            {
                qualityvalue=80;
            }
            else if(selectedmediaquality.equalsIgnoreCase(config.mediaquality1080))
            {
                qualityvalue=100;
            }
            try {
                captureBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) qualityvalue);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

           // setAutoFlash(captureBuilder);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {

                    try {
                        setimagehash();
                        medialistitemaddbroadcast();
                        if(madapterclick != null)
                            madapterclick.onItemClicked(null,4);

                        zoomLevel=1.0f;
                        txt_zoomlevel.setText(zoomLevel+" x");

                        unlockfocus();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void medialistitemaddbroadcast()
    {
        Intent intent = new Intent(config.broadcast_medialistnewitem);
        applicationviavideocomposer.getactivity().sendBroadcast(intent);
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockfocus() {
        try {
            if(rectzoom != null)
                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, rectzoom);

            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
           // setAutoFlash(mPreviewRequestBuilder);

            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isbrustmodeenabled()
    {
        if(brustmodeenabled)
        {
            brustmodeenabled=false;
            return true;
        }
        return false;
    }

    public void expendcollpaseview()
    {
        if(expandable_layout.isExpanded())
        {
            qualityoptionanimations(1.0f,0f);
            expandable_layout.setDuration(100);
            expandable_layout.collapse();
            expandable_layout.setVisibility(View.GONE);
            actionbarcomposer.setVisibility(View.VISIBLE);
        }
        else
        {
            qualityoptionanimations(0f,1.0f);
            expandable_layout.setDuration(100);
            expandable_layout.expand();
            expandable_layout.setVisibility(View.VISIBLE);
            actionbarcomposer.setVisibility(View.GONE);
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

    public void setmediaquility(String quality)
    {
        selectedmediaquality=quality;
        txt_media_quality.setText(quality);
        expandable_layout.collapse();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                expandable_layout.setVisibility(View.GONE);
                actionbarcomposer.setVisibility(View.VISIBLE);
            }
        },600);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_media_low:
                setmediaquility(config.mediaquality480);
                txt_media_low.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                txt_media_high.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                break;

            case R.id.txt_media_medium:
                setmediaquility(config.mediaquality720);
                txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                txt_media_low.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                txt_media_high.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                break;

            case R.id.txt_media_high:
                setmediaquility(config.mediaquality1080);
                txt_media_high.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                txt_media_medium.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                txt_media_low.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
                break;

            case R.id.txt_media_quality:
                expendcollpaseview();
                break;

            case R.id.img_stop_watch:
                if(brustmodeenabled)
                {
                    brustmodeenabled=false;
                    img_stop_watch.setImageResource(R.drawable.stopwatch);
                }
                else
                {
                    brustmodeenabled=true;
                    img_stop_watch.setImageResource(R.drawable.stopwatchselected);
                }

                break;

            case R.id.info: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage("")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }

            case R.id.img_dotmenu:
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
                break;

            case R.id.img_flash:
                camraflashonoff();
                break;

            case R.id.txt_zoomlevel:
                zoomLevel++;
                if(zoomLevel > maximumZoomLevel)
                    zoomLevel=1f;

                setupcamerazoom();
                break;

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

            try {
                object.put(metric.getMetricTrackKeyName(),value);
                metadatametricesjson.put(metric.getMetricTrackKeyName(),value);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        builder.append("\n");
    }

    public void setmetriceshashesdata()
    {
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);

        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {

                Date currentdate=new Date();
                int seconddifference= (int) (Math.abs(zoomcontrollertimeout.getTime()-currentdate.getTime())/1000);
                if(seconddifference > 5 && layout_seekbarzoom.getVisibility() == View.VISIBLE)
                    fadeoutzoomcontrollers();

                if(! metrichashvalue.trim().isEmpty())
                {
                    common.setgraphicalblockchainvalue(config.blockchainid,"",true);
                    common.setgraphicalblockchainvalue(config.hashformula,keytype,true);
                    common.setgraphicalblockchainvalue(config.datahash,hashvalue,true);
                    common.setgraphicalblockchainvalue(config.matrichash,metrichashvalue,true);
                }
                myhandler.postDelayed(this, 1000);
            }
        };
        myhandler.post(myrunnable);
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId())
        {
            case  R.id.texture:
                /*if(madapterclick != null)
                    madapterclick.onItemClicked(motionEvent,3);*/
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
                            setupcamerazoom();
                            //seekbarzoom.setProgress(zoomLevel);
                            fadeinzoomcontrollers();

                        }
                        fingerSpacing = currentFingerSpacing;
                    }
                    else
                    {
                        if(madapterclick != null)
                            madapterclick.onItemClicked(motionEvent,3);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    public void setupcamerazoom()
    {
        DecimalFormat precision=new DecimalFormat("0.0");
        zoomcontrollertimeout=new Date();
        txt_zoomlevel.setText(precision.format(zoomLevel)+" x");

        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        float ratio = (float) 1 / zoomLevel;
        int croppedWidth = rect.width() - Math.round((float)rect.width() * ratio);
        int croppedHeight = rect.height() - Math.round((float)rect.height() * ratio);
        rectzoom = new Rect(croppedWidth/2, croppedHeight/2,
                rect.width() - croppedWidth/2, rect.height() - croppedHeight/2);
        Log.e("rectzoom level ",""+ rectzoom +" "+zoomLevel+" "+maximumZoomLevel);
        mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, rectzoom);
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fadeinzoomcontrollers()
    {
        if(layout_seekbarzoom.getVisibility() == View.GONE)
        {
            layout_seekbarzoom.setAlpha(layout_seekbarzoom.getAlpha());
            layout_seekbarzoom.setVisibility(View.VISIBLE);
            layout_seekbarzoom.animate().alpha(1.0f).setDuration(500).setListener(null);
        }
    }

    public void fadeoutzoomcontrollers()
    {
        layout_seekbarzoom.setAlpha(layout_seekbarzoom.getAlpha());
        layout_seekbarzoom.animate().alpha(0f).setDuration(800).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layout_seekbarzoom.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

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
                doafterallpermissions();
            } else {
                Toast.makeText(getActivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private File getImageFile(Context context) {
        String storagedirectory=xdata.getinstance().getSetting(config.selected_folder);
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        capturedimagefile =new File(storagedirectory, fileName+".jpg");

        File destinationDir=new File(storagedirectory);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        selectedvideofile= capturedimagefile.getAbsolutePath();
        return capturedimagefile;
    }

    public void camraflashonoff() {
        try {
            if (cameraid.equals(CAMERA_BACK)) {
                if (isflashsupported) {
                    if (isflashon) {
                        mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                        imgflashon.setImageResource(R.drawable.ic_no_flash_icon);
                        isflashon = false;
                    } else {
                        mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                        imgflashon.setImageResource(R.drawable.flash_on);
                        isflashon = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_menu:
                gethelper().onBack();
                break;
        }
    }

    public void switchCamera() {
        if (cameraid.equals(CAMERA_FRONT)) {
            cameraid = CAMERA_BACK;
            closeCamera();
            reopenCamera();

        } else if (cameraid.equals(CAMERA_BACK)) {
            cameraid = CAMERA_FRONT;
            closeCamera();
            reopenCamera();
        }
    }

    public void reopenCamera() {
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    public void setData(adapteritemclick madapterclick, RelativeLayout layoutbottom) {
        this.madapterclick = madapterclick;
        this.layoutbottom = layoutbottom;
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
}
