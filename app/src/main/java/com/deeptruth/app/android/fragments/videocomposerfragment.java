package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.dbitemcontainer;
import com.deeptruth.app.android.models.frameinfo;
import com.deeptruth.app.android.models.mediacompletiondialogmain;
import com.deeptruth.app.android.models.mediacompletiondialogsub;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.permissions;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.camerautil;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.md5;
import com.deeptruth.app.android.utils.sha;
import com.deeptruth.app.android.utils.xdata;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class videocomposerfragment extends basefragment implements View.OnClickListener,View.OnTouchListener {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "videocomposerfragment";

    public int selectedsection=1;
    protected float fingerSpacing = 0;
    protected float zoomLevel = 1f;
    protected float maximumZoomLevel;
    protected Rect zoom;
    boolean firsthashvalue = true;
    mediacompletiondialogmain mediacompletionpopupmain;
    mediacompletiondialogsub mediacompletionpopupsub;
    FragmentManager fm ;

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

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
            if(isvideorecording)
            {
                if(mTextureView == null)
                    return;

              Thread thread =new Thread(new Runnable() {
                  @Override
                  public void run() {

                      try {
                          if(isvideorecording)
                          {
                              if(mframetorecordcount == currentframenumber || (mediakey.trim().isEmpty()))
                              {
                                  Bitmap bitmap = mTextureView.getBitmap(10,10);
                                  bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(),
                                          bitmap.getHeight(), mTextureView.getTransform( null ), true );
                                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                  bitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);
                                  byte[] byteArray = stream.toByteArray();

                                  if(mediakey.trim().isEmpty())
                                  {
                                      String currenttimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                      //randomstring gen = new randomstring(20, ThreadLocalRandom.current());
                                      mediakey=currenttimestamp;
                                      Log.e("localkey ",mediakey);
                                      String keyvalue= getkeyvalue(byteArray);

                                      savestartmediainfo();
                                  }

                                  if(mframetorecordcount == currentframenumber)
                                  {
                                      updatelistitemnotify(byteArray,currentframenumber,"Frame");
                                      currentframenumber = currentframenumber + frameduration;
                                  }
                                  //bitmap.recycle();
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
    private MediaRecorder mediarecorder;
    /**
     * Whether the app is recording video now
     */
    public boolean isvideorecording =false;
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

    RecyclerView recyview_hashes;
    RecyclerView recyview_metrices;
    LinearLayout linearLayout;
    boolean isflashon = false,inPreview = true;
    adapteritemclick popupclickmain;
    adapteritemclick popupclicksub;

    TextView txt_title_actionbarcomposer,txt_media_quality;

    ImageView imgflashon,img_dotmenu,img_warning,img_close,handle;


    public Dialog maindialogshare,subdialogshare;
    View rootview = null;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler timerhandler;
    int Seconds, Minutes, MilliSeconds,framepersecond=30,videobitrate=2000000 ; // 2000000 is equals to 2 MB. It means quality is exisiting around 420P. It also depands on frame rate.
    String keytype =config.prefs_md5,currenthashvalue="";
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    ArrayList<frameinfo> muploadframelist =new ArrayList<>();
    long currentframenumber =0;
    long frameduration =15, mframetorecordcount =0,apicallduration=5,apicurrentduration=0;
    public boolean autostartvideo=false,camerastatusok=false;
    adapteritemclick madapterclick;
    RelativeLayout layout_bottom;
    File lastrecordedvideo=null;
    String selectedvideofile ="", mediakey ="",selectedmetrices="", selectedhashes ="",hashvalue = "",metrichashvalue = "";
    int metriceslastupdatedposition=0;
    //private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    ArrayList<dbitemcontainer> mdbstartitemcontainer =new ArrayList<>();
    ArrayList<dbitemcontainer> mdbmiddleitemcontainer =new ArrayList<>();
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<permissions> permissionslist =new ArrayList<>();

    private boolean isdraweropen=false,isgraphicalshown=false;
    private Handler myHandler;
    private Runnable myRunnable;
    private int lastmetricescount=0;
    private boolean issavedtofolder=false;
    JSONArray metadatametricesjson=new JSONArray();

    private LinearLayoutManager mLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int flingactionmindstvac;
    private  final int flingactionmindspdvac = 10;

    private ActionBarDrawerToggle drawertoggle;
    @BindView(R.id.linear_header)
    LinearLayout linearheader;

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

            applicationviavideocomposer.getactivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mTextureView = (AutoFitTextureView) rootview.findViewById(R.id.texture);
            imgflashon = (ImageView) rootview.findViewById(R.id.img_flash);
            img_dotmenu = (ImageView) rootview.findViewById(R.id.img_dotmenu);
            img_warning = (ImageView) rootview.findViewById(R.id.img_warning);
            img_close = (ImageView) rootview.findViewById(R.id.img_close);
            handle = (ImageView) rootview.findViewById(R.id.handle);
            txt_title_actionbarcomposer = (TextView) rootview.findViewById(R.id.txt_title_actionbarcomposer);
            txt_media_quality = (TextView) rootview.findViewById(R.id.txt_media_quality);
            linearLayout=rootview.findViewById(R.id.content);

            gethelper().drawerenabledisable(true,layout_bottom,linearheader,null,null,null);

            if(! xdata.getinstance().getSetting(config.frameupdateevery).trim().isEmpty())
                apicallduration=Long.parseLong(xdata.getinstance().getSetting(config.frameupdateevery));
            flingactionmindstvac=common.getdrawerswipearea();

            imgflashon.setVisibility(View.VISIBLE);
            txt_media_quality.setVisibility(View.VISIBLE);

            if(videobitrate == 2000000)
                txt_media_quality.setText("420p");

            timerhandler = new Handler() ;
            txt_title_actionbarcomposer.setText("deeptruth");
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

            img_dotmenu.setVisibility(View.VISIBLE);
            mTextureView.setOnTouchListener(this);

            imgflashon.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);
            img_warning.setOnClickListener(this);
            img_close.setOnClickListener(this);


            setmetriceshashesdata();
        }
        return rootview;
    }


    public boolean isvideorecording()
    {
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
                        try {
                            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
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

    @Override
    public void setUserVisibleHint(boolean isvisibletouser) {
        super.setUserVisibleHint(isvisibletouser);
        this.isvisibletouser=isvisibletouser;
        if(isvisibletouser && mTextureView!= null && mTextureView.isAvailable())
        {
            camerastatusok=true;
            startBackgroundThread();
            if (mTextureView.isAvailable())
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());

            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        else
        {
            closemediawithtimer();
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
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (Exception e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mediarecorder) {
                mediarecorder.release();
                mediarecorder = null;
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
            Surface recorderSurface = mediarecorder.getSurface();
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
        } catch (Exception e) {
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

        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediarecorder.setOutputFile(getVideoFile(activity).getAbsolutePath());
        mediarecorder.setVideoEncodingBitRate(videobitrate);
        mediarecorder.setVideoFrameRate(framepersecond);
        mediarecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
       // int orientation = ORIENTATIONS.get(rotation);
        int orientation =  camerautil.getOrientation(rotation, upsideDown);

        mediarecorder.setOrientationHint(orientation);

        mediarecorder.prepare();
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
            // UI
            metadatametricesjson=new JSONArray();
            mediakey ="";
            issavedtofolder=false;
            isvideorecording = true;
            if(mediarecorder != null)
            {
                // Start recording
                mediarecorder.start();
                startvideotimer();
                //fragmentgraphic.setvisualizerwave();
                wavevisualizerslist.clear();
                //startnoise();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stopRecordingVideo() {
        // UI
        issavedtofolder=true;
        isvideorecording = false;
        lastrecordedvideo=new File(selectedvideofile);

        try {
            mPreviewSession.stopRepeating();
            mPreviewSession.abortCaptures();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try{
            mediarecorder.stop();
        }catch(RuntimeException stopException){
            Log.e("exception", String.valueOf(stopException));
        }
        mediarecorder.reset();

        startPreview();
        stopvideotimer();

        resetvideotimer();
        clearvideolist();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    insertstartmediainfo();

                    Gson gson = new Gson();
                    String list1 = gson.toJson(mdbstartitemcontainer);
                    String list2 = gson.toJson(mdbmiddleitemcontainer);
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_flash:
                navigateflash();
                break;


            case R.id.img_dotmenu:
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
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

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
    }

    public void startstopvideo()
    {
        if (isvideorecording) {
            gethelper().updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
          //  layout_bottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
            stopRecordingVideo();

        } else {
            selectedhashes="";
            selectedmetrices="";
            mmetricsitems.clear();
            mhashesitems.clear();
            mdbstartitemcontainer.clear();
            mdbmiddleitemcontainer.clear();

            imgflashon.setVisibility(View.VISIBLE);
            apicurrentduration =0;
            currentframenumber =0;
            mframetorecordcount =0;
            currentframenumber = currentframenumber + frameduration;

            mvideoframes.clear();
            gethelper().updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
          //  layout_bottom.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal_transparent));
            startRecordingVideo();

            if(madapterclick != null)
                madapterclick.onItemClicked(null,1);
            showhideactionbaricon(0);
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void setData(boolean autostartvideo, adapteritemclick madapterclick, RelativeLayout layout_bottom) {
        this.autostartvideo = autostartvideo;
        this.madapterclick = madapterclick;
        this.layout_bottom = layout_bottom;
    }

    public static Fragment newInstance()
    {
        return new videocomposerfragment();
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
                if (ContextCompat.checkSelfPermission(getActivity(), permissionslist.get(i).getPermissionname()) != PackageManager.PERMISSION_GRANTED)
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
        txt_title_actionbarcomposer.setText("deeptruth");
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

        if(! camerastatusok)
        {
            camerastatusok=true;
           // layout_bottom.setVisibility(View.VISIBLE);
            startBackgroundThread();
            if (mTextureView.isAvailable())
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        Log.e("onpause","onpause");
        closemediawithtimer();
        showhideactionbaricon(1);
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
                if(! issavedtofolder && common.getstoragedeniedpermissions().isEmpty() && (selectedvideofile != null )
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
        txt_title_actionbarcomposer.setText("00:00:00");
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
                            if(isvideorecording)
                            {
                                txt_title_actionbarcomposer.setText("" + String.format("%02d", Minutes) + ":"
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
             //   layout_bottom.setVisibility(View.VISIBLE);
             //   imgflashon.setVisibility(View.VISIBLE);
              //  rotatecamera.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setvideoadapter() {

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
       // metricesarray.put(object);
        builder.append("\n");
        metadatametricesjson.put(object);
        selectedmetrices=builder.toString();
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

                mvideoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));
                hashvalue = keyvalue;

                if(! selectedhashes.trim().isEmpty())
                    selectedhashes=selectedhashes+"\n";

                selectedhashes =selectedhashes+mvideoframes.get(mvideoframes.size()-1).getframeinfo();


                if(apicurrentduration > apicallduration)
                    apicurrentduration=apicallduration;


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

                Log.e("current call, calldur ",apicurrentduration+" "+apicallduration);
                if(apicurrentduration == apicallduration)
                    apicurrentduration=0;
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
                if(isvideorecording)
                {
                    if((! selectedmetrices.toString().trim().isEmpty()))
                    {

                        if(mmetricsitems.size() > 0)
                        {
                           // mmetricsitems.set(0,new videomodel(selectedmetrices));
                            mmetricsitems.add(new videomodel(selectedmetrices));
                        }
                        else
                        {
                            mmetricsitems.add(new videomodel(selectedmetrices));
                        }
                        //mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                        selectedmetrices="";
                    }
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


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
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

            metrichash = md5.calculatestringtomd5(metricesjsonarray.get(0).toString());
            Log.e("Metric hash",""+metrichash);

            mdbmiddleitemcontainer.add(new dbitemcontainer("", metrichash ,keytype, mediakey,""+metricesjsonarray.toString(),
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

            if(mdbstartitemcontainer.size() == 0)
            {
                mdbstartitemcontainer.add(new dbitemcontainer("","video","Local storage path", mediakey,"","","0","0",
                        config.type_video_start,devicestartdate,devicestartdate,timeoffset,"","","",
                        xdata.getinstance().getSetting(config.selected_folder)));
                Log.e("startcontainersize"," "+mdbstartitemcontainer.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertstartmediainfo()
    {
        if(lastrecordedvideo != null)
        {
            String duration = common.getvideotimefromurl(lastrecordedvideo.getAbsolutePath());

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fps",""+framepersecond);
            map.put("firsthash", firsthash);
            map.put("hashmethod",keytype);
            map.put("name",common.getfilename(lastrecordedvideo.getAbsolutePath()));
            map.put("duration",duration);
            map.put("framecounts","");
            map.put("finalhash","");

            Gson gson = new Gson();
            String json = gson.toJson(map);

            String updatecompletedate[] = common.getcurrentdatewithtimezone();
            String completeddate = updatecompletedate[0];
            String medianame=common.getfilename(lastrecordedvideo.getAbsolutePath());

            mdbstartitemcontainer.get(0).setItem1(json);
            mdbstartitemcontainer.get(0).setItem3(lastrecordedvideo.getAbsolutePath());
            mdbstartitemcontainer.get(0).setItem15(lastrecordedvideo.getAbsolutePath());
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
                mdbstartitemcontainer.get(0).getItem16());

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
                imgflashon.setImageResource(R.drawable.ic_no_flash_icon);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                isflashon = false;
            } else {
                imgflashon.setImageResource(R.drawable.flash_on);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                isflashon = true;
            }
        } catch (Exception e) {
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
    public void showhideactionbaricon(int i){
        if(i == 0){
            txt_title_actionbarcomposer.setText("00:00:00");
            img_dotmenu.setVisibility(View.INVISIBLE);
            txt_media_quality.setVisibility(View.INVISIBLE);

        }else{
            txt_title_actionbarcomposer.setText("deeptuth");
            img_dotmenu.setVisibility(View.VISIBLE);
            txt_media_quality.setVisibility(View.VISIBLE);
        }

    }
}

