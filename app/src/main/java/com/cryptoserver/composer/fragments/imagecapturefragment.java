package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.metadata.MetaDataInsert;
import com.cryptoserver.composer.models.frameinfo;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.xdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

import static com.cryptoserver.composer.fragments.videocomposerfragment.CAMERA_BACK;
import static com.cryptoserver.composer.fragments.videocomposerfragment.CAMERA_FRONT;

/**
 * Created by devesh on 5/11/18.
 */

public class imagecapturefragment extends basefragment  implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener{



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


    private CaptureRequest.Builder mPreviewBuilder;


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
    private String mCameraId;

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
    private File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
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
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

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

    public int selectedsection=1;

    ImageView mrecordimagebutton,imgflashon,rotatecamera,handle;

    public Dialog maindialogshare,subdialogshare;
    View rootview = null;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler timerhandler;
    int Seconds, Minutes, MilliSeconds ;
    String keytype = config.prefs_md5,currenthashvalue="";
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    ArrayList<frameinfo> muploadframelist =new ArrayList<>();
    long currentframenumber =0;
    long frameduration =15, mframetorecordcount =0,apicallduration=5,apicurrentduration=0;
    public boolean autostartvideo=false,camerastatusok=false;
    adapteritemclick madapterclick;
    File lastrecordedvideo=null;
    String selectedvideofile ="",videokey="",selectedmetrices="", selectedhashes ="";
    int metriceslastupdatedposition=0;
    //private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();
    videoframeadapter mmetricesadapter,mhashesadapter;

    databasemanager mdbhelper;
    private boolean isdraweropen=false,isgraphicalshown=false;
    private Handler myHandler;
    private Runnable myRunnable;
    private int lastmetricescount=0;
    private boolean issavedtofolder=false;
    JSONArray metadatametricesjson=new JSONArray();

    String localkey = null;
    private LinearLayoutManager mLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    graphicalfragment fragmentgraphic;

    CameraManager manager;

    ImageView captureimage;

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
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_INACTIVE == afState /*add this*/) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
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
                        captureStillPicture();
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

    public static imagecapturefragment newInstance() {
        return new imagecapturefragment();
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

        if(rootview == null) {


            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            captureimage = rootview.findViewById(R.id.img_image_capture);

            mTextureView = (AutoFitTextureView)rootview.findViewById(R.id.texture);
            rotatecamera = (ImageView) rootview.findViewById(R.id.img_rotate_camera);
            imgflashon = (ImageView) rootview.findViewById(R.id.img_flash_on);


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

            captureimage.setOnClickListener(this);
            timerhandler = new Handler() ;
            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);
            rotatecamera.setOnClickListener(this);
            imgflashon.setOnClickListener(this);


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

            setmetriceshashesdata();
        }
        return rootview;
    }


    public void setimagehash(){

        /*Bitmap bitmap = mTextureView.getBitmap(10,10);
        bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), mTextureView.getTransform( null ), true );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);
        byte[] byteArray = stream.toByteArray();*/

       // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
       // Bitmap bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath(),bmOptions);

        Bitmap bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);
        byte[] byteArray = stream.toByteArray();

        final String keyhash =  getkeyvalue(byteArray);

        Log.e("keyhash = ","" +keyhash);

        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mhashesitems.clear();
                mhashesadapter.notifyDataSetChanged();

                mhashesitems.add(new videomodel(keyhash));
                mhashesadapter.notifyDataSetChanged();
                recyview_hashes.scrollToPosition(mhashesitems.size()-1);
            }
        });
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mFile = new File(getActivity().getExternalFilesDir(null), fileName+".jpg");*/
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

        startBackgroundThread();
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
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).


    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    /*private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {

            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);

                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
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

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
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
         manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
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
                            } catch (CameraAccessException e) {
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
        } catch (CameraAccessException e) {
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
    private void takePicture() {
        //lockFocus();

        captureStillPicture();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            selectedmetrices="";
            mmetricsitems.clear();
            mmetricesadapter.notifyDataSetChanged();

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setimagehash();
                        }
                    }).start();

                    if(madapterclick != null)
                        madapterclick.onItemClicked(null,1);

                    showsharepopupmain();
                    Log.d(TAG, mFile.toString());
                    unlockFocus();
                    //gethelper().onBack();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
    private void unlockFocus() {
        try {
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
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_image_capture: {
                getImageFile(getActivity());
                takePicture();

                break;
            }
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

            case R.id.img_flash_on:
               // navigateflash();
                break;

            case R.id.img_rotate_camera:
               // switchCamera();
                break;
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
        //metadatametricesjson.put(object);
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

                        if(! selectedmetrices.toString().trim().isEmpty())
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
        txt_share_btn2.setText(getResources().getString(R.string.share_partial_video));
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

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }


    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

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

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
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
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mFile=new File(config.videodir, fileName+".jpg");

        File destinationDir=new File(config.videodir);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        selectedvideofile=mFile.getAbsolutePath();
        return mFile;
    }

    private void navigateflash() {
        try {
            if(isflashon) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    imgflashon.setImageResource(R.drawable.flash_off);
                    manager.setTorchMode(mCameraId, true);
                    isflashon = false;
                }
               /* mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
                mPreviewRequestBuilder.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);*/
                //isflashon = false;
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setTorchMode(mCameraId, false);
                    imgflashon.setImageResource(R.drawable.flash_on);

                    isflashon = true;
                }

               /* mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                mPreviewRequestBuilder.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                isflashon = true;*/
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void turnOnFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setTorchMode(mCameraId, false);
                /*playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.off);*/

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchCamera() {
        if (mCameraId.equals(CAMERA_FRONT)) {
            mCameraId = CAMERA_BACK;
            closeCamera();
            reopenCamera();

        } else if (mCameraId.equals(CAMERA_BACK)) {
            mCameraId = CAMERA_FRONT;
            closeCamera();
            reopenCamera();
        }
    }

    public void reopenCamera() {
        if (mTextureView.isAvailable())
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());

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

   /* private void playOnOffSound(){

        mp = MediaPlayer.create(FlashLightActivity.this, R.raw.flash_sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }*/

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
   /* public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }*/

    public void setData(adapteritemclick madapterclick) {
        this.madapterclick = madapterclick;
    }
}
