package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.CenteredImageSpan;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.xdata;
import com.google.gson.Gson;

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

/**
 * Created by devesh on 5/11/18.
 */

public class imagecomposerfragment extends basefragment  implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener{

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
    @BindView(R.id.linear_header)
    LinearLayout linearheader;
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
    @BindView(R.id.txt_section_gmttime)
    TextView txt_section_gmttime;
    @BindView(R.id.layout_wifi_gps_data)
    LinearLayout layout_wifi_gps_data;
    @BindView(R.id.ll_actionbarcomposer)
    LinearLayout ll_actionbarcomposer;

    /**
     * ID of the current {@link CameraDevice}.
     */
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraid = CAMERA_BACK,selectedmediaquality="720P";
    private boolean isflashsupported=false,isvisibletouser=false;;
    private boolean isflashon = false,previewupdated=false;
    protected float fingerSpacing = 0;
    protected float zoomLevel = 1f;
    protected float maximumzoomlevel;
    protected Rect rectzoom;
    FrameLayout framecontainer;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private int REQUEST_CAMERA_PERMISSION = 1,rotationangle=0;
    private static final String FRAGMENT_DIALOG = "dialog";
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    String devicecamera = "Back";
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
    private final TextureView.SurfaceTextureListener surfacetexturelistener
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
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView textureview;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession capturesession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice cameradevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size previewsize;

    private static final int request_permissions = 1;
    private CameraCharacteristics characteristics;
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback statecallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            cameraopencloselock.release();
            cameradevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraopencloselock.release();
            cameraDevice.close();
            cameradevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraopencloselock.release();
            cameraDevice.close();
            cameradevice = null;
            Activity activity = applicationviavideocomposer.getactivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread backgroundthread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler backgroundhandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader imagereader;

    /**
     * This is the output file for our picture.
     */
    private File capturedimagefile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener onimageavailablelistener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            backgroundhandler.post(new ImageSaver(reader.acquireNextImage(), capturedimagefile));
        }

    };

    private CaptureRequest.Builder previewrequestbuilder;

    /**
     * {@link CaptureRequest} generated by {@link #previewrequestbuilder}
     */
    private CaptureRequest previewrequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int previewstate = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore cameraopencloselock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean brustmodeenabled=false;
    public boolean isimagecaptureprocessing=false;

    /**
     * Orientation of the camera sensor
     */
    private int sensororientation;
    LinearLayout linearLayout;
    TextView txt_title_actionbarcomposer;
    ImageView imgflashon,img_dotmenu,img_stop_watch;
    View rootview = null;
    Handler timerhandler;
    String keytype = config.prefs_md5;
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    adapteritemclick madapterclick;
    String selectedvideofile ="",selectedmetrices="", selectedhashes ="",mediakey="";
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private boolean flashsupported=false;
    private Handler myhandler;
    private Runnable myrunnable;
    JSONObject metadatametricesjson=new JSONObject();
    public int flingactionmindstvac;
    ArrayList<dbitemcontainer> dbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> dbmiddleitemcontainer =new ArrayList<>();
    String hashvalue = "",metrichashvalue = "";
    RelativeLayout layoutbottom,layout_seekbarzoom;
    ImageView recordbutton;
    boolean isexpandview =false;


    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (previewstate) {
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
                            previewstate = STATE_PICTURE_TAKEN;
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
                        previewstate = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        previewstate = STATE_PICTURE_TAKEN;
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
        final Activity activity = applicationviavideocomposer.getactivity();
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
        previewupdated=false;
        zoomLevel=1f;
        textureview = (AutoFitTextureView)rootview.findViewById(R.id.texture);
        imgflashon = (ImageView) rootview.findViewById(R.id.img_flash);
        img_dotmenu = (ImageView) rootview.findViewById(R.id.img_dotmenu);
        img_stop_watch = (ImageView) rootview.findViewById(R.id.img_stop_watch);
        txt_title_actionbarcomposer = (TextView) rootview.findViewById(R.id.txt_title_actionbarcomposer);
        linearLayout=rootview.findViewById(R.id.content);
        actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
       // layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));
        timerhandler = new Handler() ;
        textureview.setOnTouchListener(this);
        imgflashon.setOnClickListener(this);
        img_dotmenu.setOnClickListener(this);
        img_stop_watch.setOnClickListener(this);
        txt_media_quality.setOnClickListener(this);
        txt_media_low.setOnClickListener(this);
        txt_media_medium.setOnClickListener(this);
        txt_media_high.setOnClickListener(this);

        img_dotmenu.setVisibility(View.VISIBLE);
        imgflashon.setVisibility(View.VISIBLE);
        img_stop_watch.setVisibility(View.GONE);

        flingactionmindstvac=common.getdrawerswipearea();
        keytype=common.checkkey();
        brustmodeenabled=false;
        img_stop_watch.setImageResource(R.drawable.stopwatch);

        txt_title_actionbarcomposer.setText("");

        expandable_layout.setVisibility(View.GONE);
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
        if(!xdata.getinstance().getSetting(config.imagequality).isEmpty())
        {
            txt_media_quality.setText(xdata.getinstance().getSetting(config.imagequality));
            if(xdata.getinstance().getSetting(config.imagequality).contains("480"))
                expendcollpaseviewcolor(txt_media_low,txt_media_medium,txt_media_high);
            else if(xdata.getinstance().getSetting(config.imagequality).contains("720"))
                expendcollpaseviewcolor(txt_media_medium,txt_media_low,txt_media_high);
            else if(xdata.getinstance().getSetting(config.imagequality).contains("1080"))
                expendcollpaseviewcolor(txt_media_high,txt_media_low,txt_media_medium);
        }
        else
        {
            expendcollpaseviewcolor(txt_media_medium,txt_media_low,txt_media_high);
        }

        selectedmediaquality = txt_media_quality.getText().toString();

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
                    txt_media_quality.setVisibility(View.VISIBLE);
                    if(isexpandview)
                        txt_media_quality.setVisibility(View.GONE);
                    //actionbarcomposer.setVisibility(View.VISIBLE);
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

        return rootview;
    }

    public void expendcollpaseviewcolor(TextView textview1,TextView textview2,TextView textview3)
    {
        textview1.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
        textview2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        textview3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
    }

    public void changeiconsorientation(float angle)
    {
        rotationangle=(int)angle;
        if(imgflashon != null)
            imgflashon.setRotation(angle);

        if(img_dotmenu != null)
            img_dotmenu.setRotation(angle);

        if(img_stop_watch != null)
            img_stop_watch.setRotation(angle);

        if(txt_media_quality != null)
            txt_media_quality.setRotation(angle);
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

            if(dbstartitemcontainer.size() == 0)
            {
                dbstartitemcontainer.add(new dbitemcontainer("","image",capturedimagefile.getAbsolutePath(), mediakey,"","","0","0",
                        config.type_image_start,devicestartdate,devicestartdate,timeoffset,"","","",
                        xdata.getinstance().getSetting(config.selected_folder)));
                Log.e("startcontainersize"," "+ dbstartitemcontainer.size());
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
                Calendar sequencetime = Calendar.getInstance();
                String devicetime = common.get24hourformat();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SS aa");
                String starttime = sdf.format(sequencetime.getTime());
                String endtime = sdf.format(sequencetime.getTime());

                if(metricesjsonarray != null && metricesjsonarray.length() > 0)
                {
                    JSONObject arrayobject=metricesjsonarray.getJSONObject(0);
                    arrayobject.put("interim_identifying_hashes","");
                    arrayobject.put("distancetravelled","0");
                    arrayobject.put("sequencestarttime",starttime+" "+common.gettimezoneshortname());
                    arrayobject.put("sequenceendtime",endtime+" "+common.gettimezoneshortname());
                    arrayobject.put("devicetime",devicetime);
                }
                String metrichash = md5.calculatestringtomd5(metricesjsonarray.toString());
                dbmiddleitemcontainer.add(new dbitemcontainer("", metrichash ,keytype, mediakey,""+metricesjsonarray.toString(),
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
            /*for(int i=0;i<5;i++)
            {
                keyvalue =  md5.fileToMD5(capturedimagefile.getAbsolutePath());
                if(keyvalue != null &&  keyvalue.trim().length() > 0)
                    break;
            }*/

            keyvalue =  md5.fileToMD5(capturedimagefile.getAbsolutePath());

            selectedhashes =  keyvalue;
            selectedhashes=keytype+" : "+selectedhashes;
            Log.e("imagekeyhash = ","Values are: " +selectedhashes);
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
            String list1 = gson.toJson(dbstartitemcontainer);
            String list2 = gson.toJson(dbmiddleitemcontainer);
            xdata.getinstance().saveSetting(config.servicedata_liststart,list1);
            xdata.getinstance().saveSetting(config.servicedata_listmiddle,list2);
            xdata.getinstance().saveSetting(config.servicedata_mediapath,capturedimagefile.getAbsolutePath());
            xdata.getinstance().saveSetting(config.servicedata_keytype,keytype);

            Intent intent = new Intent(applicationviavideocomposer.getactivity(), insertmediadataservice.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                applicationviavideocomposer.getactivity().startForegroundService(intent);
            else
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

            dbstartitemcontainer.get(0).setItem1(json);
            dbstartitemcontainer.get(0).setItem3(capturedimagefile.getAbsolutePath());
            dbstartitemcontainer.get(0).setItem15(capturedimagefile.getAbsolutePath());
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
                        dbstartitemcontainer.get(0).getItem16(),"");

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] neededpermissions = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                };

                List<String> deniedpermissions = new ArrayList<>();

                for (String permission : neededpermissions) {
                    if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                        deniedpermissions.add(permission);
                    }
                }

                if(deniedpermissions.isEmpty()){
                    doafterallpermissions();
                }
                else
                {
                    String[] array = new String[deniedpermissions.size()];
                    array = deniedpermissions.toArray(array);
                    ActivityCompat.requestPermissions(applicationviavideocomposer.getactivity(), array, request_permissions);
                }
            }
        },config.transition_fragment_millis_0);
    }

    public void doafterallpermissions()
    {
        startBackgroundThread();
        if (textureview.isAvailable()) {
            openCamera(textureview.getWidth(), textureview.getHeight());
        }
        textureview.setSurfaceTextureListener(surfacetexturelistener);
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();

        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setUpCameraOutputs(int width, int height) {
        Activity activity = applicationviavideocomposer.getactivity();
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
            imagereader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, /*maxImages*/2);
            imagereader.setOnImageAvailableListener(
                    onimageavailablelistener, backgroundhandler);
            int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            sensororientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            boolean swappedDimensions = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (sensororientation == 90 || sensororientation == 270) {
                        swappedDimensions = true;
                    }
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (sensororientation == 0 || sensororientation == 180) {
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
            previewsize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                    maxPreviewHeight, largest);

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                textureview.setAspectRatio(
                        previewsize.getWidth(), previewsize.getHeight());
            } else {
                textureview.setAspectRatio(
                        previewsize.getHeight(), previewsize.getWidth());
            }

            // Check if the flash is supported.
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            flashsupported = available == null ? false : available;

//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
           // requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = applicationviavideocomposer.getactivity();
        CameraManager  manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        // cameraId = manager.getCameraIdList()[0];
        // Choose the sizes for camera preview and video recording
        try {
            characteristics = manager.getCameraCharacteristics(cameraid);
            maximumzoomlevel = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (!cameraopencloselock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraid, statecallback, backgroundhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            if(cameraopencloselock != null)
            {
                cameraopencloselock.acquire();
                if (null != capturesession) {
                    capturesession.close();
                    capturesession = null;
                }
                if (null != cameradevice) {
                    cameradevice.close();
                    cameradevice = null;
                }
                if (null != imagereader) {
                    imagereader.close();
                    imagereader = null;
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            cameraopencloselock.release();
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
        if(backgroundthread != null)
        {
            backgroundthread.quitSafely();
            try {
                backgroundthread.join();
                backgroundthread = null;
                backgroundhandler = null;
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
            SurfaceTexture texture = textureview.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewsize.getWidth(), previewsize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewrequestbuilder
                    = cameradevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewrequestbuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            cameradevice.createCaptureSession(Arrays.asList(surface, imagereader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == cameradevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            capturesession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                previewrequestbuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                //setAutoFlash(previewrequestbuilder);

                                // Finally, we start displaying the camera preview.
                                previewrequest = previewrequestbuilder.build();
                                capturesession.setRepeatingRequest(previewrequest,
                                        mCaptureCallback, backgroundhandler);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            previewupdated=true;
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
     * Configures the necessary {@link android.graphics.Matrix} transformation to `textureview`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureview` is fixed.
     *
     * @param viewWidth  The width of `textureview`
     * @param viewHeight The height of `textureview`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = applicationviavideocomposer.getactivity();
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
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureview.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    public void takePicture() {
        //lockFocus();

        if(madapterclick != null)
            madapterclick.onItemClicked(null,1);

        getImageFile(applicationviavideocomposer.getactivity());
        capturestillpicture();

        brustmodeenabled=false;
        isimagecaptureprocessing=true;
        img_stop_watch.setImageResource(R.drawable.stopwatch);
    }

    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            previewrequestbuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            previewstate = STATE_WAITING_PRECAPTURE;
            capturesession.capture(previewrequestbuilder.build(), mCaptureCallback,
                    backgroundhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void capturestillpicture() {
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

            final Activity activity = applicationviavideocomposer.getactivity();
            metadatametricesjson=new JSONObject();
            if (null == activity || null == cameradevice) {
                return;
            }
            selectedmetrices="";
            dbstartitemcontainer.clear();
            dbmiddleitemcontainer.clear();
            mmetricsitems.clear();

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    cameradevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imagereader.getSurface());

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
          //  Toast.makeText(applicationviavideocomposer.getactivity(),""+rotationangle,Toast.LENGTH_SHORT).show();
            if(rotationangle == 90)  // Landscape left side
            {
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(90));
            }
            else if(rotationangle == 0)   // Portrait 90
            {
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(0));
            }
            else if(rotationangle == -90)     // Landscape right side
            {
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(270));
            }
            else if(rotationangle == 180)     // Portrait 180
            {
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(90));
            }


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
                        if(madapterclick != null)
                            madapterclick.onItemClicked(""+zoomLevel+" x",5);

                        unlockfocus();

                        txt_media_quality.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    isimagecaptureprocessing=false;
                }
            };



            capturesession.stopRepeating();
            capturesession.abortCaptures();
            capturesession.capture(captureBuilder.build(), CaptureCallback, null);

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
        return (ORIENTATIONS.get(rotation) + sensororientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockfocus() {
        try {
            if(rectzoom != null)
                previewrequestbuilder.set(CaptureRequest.SCALER_CROP_REGION, rectzoom);

            // Reset the auto-focus trigger
            previewrequestbuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
           // setAutoFlash(previewrequestbuilder);

            capturesession.capture(previewrequestbuilder.build(), mCaptureCallback,
                    backgroundhandler);
            // After this, the camera will go back to the normal state of preview.
            previewstate = STATE_PREVIEW;
            capturesession.setRepeatingRequest(previewrequest, mCaptureCallback,
                    backgroundhandler);
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

    public void expendcollpaseview(boolean isplaying)
    {
        if(expandable_layout.isExpanded())
        {
            isexpandview =isplaying;
            qualityoptionanimations(1.0f,0f);
            expandable_layout.setDuration(100);
            expandable_layout.collapse();
            expandable_layout.setVisibility(View.GONE);
            txt_media_quality.setVisibility(View.VISIBLE);
            if(isplaying)
                txt_media_quality.setVisibility(View.GONE);
        }
        else
        {
            isexpandview =isplaying;
            qualityoptionanimations(0f,1.0f);
            expandable_layout.setDuration(100);
            expandable_layout.expand();
            expandable_layout.setVisibility(View.VISIBLE);
            txt_media_quality.setVisibility(View.GONE);
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
        selectedmediaquality=quality;
        txt_media_quality.setText(quality);
        xdata.getinstance().saveSetting(config.imagequality,quality);
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
                Activity activity = applicationviavideocomposer.getactivity();
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
        }
    }

    public void adjustzoom()
    {
        zoomLevel++;

        if(zoomLevel > maximumzoomlevel)
            zoomLevel=1f;

        applycamerazoom();
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

                if(! metrichashvalue.trim().isEmpty())
                {
                    common.setgraphicalblockchainvalue(config.blockchainid,"",true);
                    common.setgraphicalblockchainvalue(config.hashformula,keytype,true);
                    common.setgraphicalblockchainvalue(config.datahash,hashvalue,true);
                    common.setgraphicalblockchainvalue(config.matrichash,metrichashvalue,true);
                }

                if (cameraid.equals(CAMERA_FRONT)){
                    devicecamera = "Front";
                }else{
                    devicecamera = "Back";
                }

                ArrayList<metricmodel> mlocalarraylist=gethelper().getmetricarraylist();
                getselectedmetrics(mlocalarraylist);

                xdata.getinstance().saveSetting(config.camera, devicecamera);
                xdata.getinstance().saveSetting(config.picturequality, selectedmediaquality);

                if(recordbutton != null)
                    recordbutton.setClickable(true);

                actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
                layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
                visibleconnection();
                setactionbarbackgroundcolor();

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
                                if ((maximumzoomlevel - zoomLevel) <= delta) {
                                    delta = maximumzoomlevel - zoomLevel;
                                }
                                zoomLevel = zoomLevel + delta;
                            } else if (currentFingerSpacing < fingerSpacing){
                                if ((zoomLevel - delta) < 1f) {
                                    delta = zoomLevel - 1f;
                                }
                                zoomLevel = zoomLevel - delta;
                            }
                            applycamerazoom();

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

    public void applycamerazoom()
    {
        DecimalFormat precision=new DecimalFormat("0.0");
        if(madapterclick != null)
            madapterclick.onItemClicked(""+precision.format(zoomLevel)+" x",5);

        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        float ratio = (float) 1 / zoomLevel;
        int croppedWidth = rect.width() - Math.round((float)rect.width() * ratio);
        int croppedHeight = rect.height() - Math.round((float)rect.height() * ratio);
        rectzoom = new Rect(croppedWidth/2, croppedHeight/2,
                rect.width() - croppedWidth/2, rect.height() - croppedHeight/2);
        Log.e("rectzoom level ",""+ rectzoom +" "+zoomLevel+" "+ maximumzoomlevel);
        previewrequestbuilder.set(CaptureRequest.SCALER_CROP_REGION, rectzoom);
        try {
            capturesession.setRepeatingRequest(previewrequestbuilder.build(), mCaptureCallback,
                    backgroundhandler);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
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
                Toast.makeText(applicationviavideocomposer.getactivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();

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
                if (isflashsupported) {
                    if (isflashon) {
                        previewrequestbuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                        capturesession.setRepeatingRequest(previewrequestbuilder.build(), null, null);
                        imgflashon.setImageResource(R.drawable.icon_flashoff);
                        isflashon = false;
                    } else {
                        previewrequestbuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                        capturesession.setRepeatingRequest(previewrequestbuilder.build(), null, null);
                        imgflashon.setImageResource(R.drawable.icon_flashon);
                        isflashon = true;
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

    public void switchCamera(ImageView imageview) {
        if (cameraid.equals(CAMERA_FRONT)) {
            devicecamera="Back";
            imgflashon.setImageResource(R.drawable.icon_flashoff);
            isflashon = false;
            previewupdated=false;
            imageview.setImageResource(R.drawable.icon_cameraflip);
            cameraid = CAMERA_BACK;
            closeCamera();
            reopenCamera();

        } else if (cameraid.equals(CAMERA_BACK)) {
            devicecamera="Front";
            imgflashon.setImageResource(R.drawable.icon_flashoff);
            previewupdated=false;
            cameraid = CAMERA_FRONT;
            isflashon = false;
            imageview.setImageResource(R.drawable.icon_reversecamera);
            closeCamera();
            reopenCamera();
        }
        zoomLevel=1.0f;
        DecimalFormat precision=new DecimalFormat("0.0");
        if(madapterclick != null)
            madapterclick.onItemClicked(""+precision.format(zoomLevel)+" x",5);
    }

    public void reopenCamera() {
        if (textureview.isAvailable()) {
            openCamera(textureview.getWidth(), textureview.getHeight());
        } else {
            textureview.setSurfaceTextureListener(surfacetexturelistener);
        }
    }

    public void setData(adapteritemclick madapterclick, RelativeLayout layoutbottom, RelativeLayout layout_seekbarzoom, FrameLayout framecontainer,ImageView recordbutton) {
        this.madapterclick = madapterclick;
        this.layoutbottom = layoutbottom;
        this.layout_seekbarzoom = layout_seekbarzoom;
        this.framecontainer = framecontainer;
        this.recordbutton = recordbutton;
    }

    @Override
    public void showhideviewondrawer(boolean isshow) {
        super.showhideviewondrawer(isshow);

        if(layoutbottom == null || linearheader == null || framecontainer == null)
            return;

        if(isshow){
            layoutbottom.setVisibility(View.GONE);
            linearheader.setVisibility(View.GONE);
            layout_seekbarzoom.setVisibility(View.GONE);
            framecontainer.setVisibility(View.GONE);
        }else{
            layoutbottom.setVisibility(View.VISIBLE);
            linearheader.setVisibility(View.VISIBLE);
            layout_seekbarzoom.setVisibility(View.VISIBLE);
            framecontainer.setVisibility(View.VISIBLE);
        }
    }

    public void visibleconnection(){
        String value = "";

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

            layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
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
                    layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));
                    actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellowtransparent));

                } else {
                    layoutbottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.greentransparent));
                    actionbar.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.greentransparent));
                }
            }
        }
    }
}
