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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.videomodel;
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
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public class videocomposerfragment extends basefragment implements View.OnClickListener,View.OnTouchListener {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "Camera2VideoFragment";
    int counter=0;

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
                counter++;

               // Log.e("Total frames ",""+counter);
                //surfaceTexture.
            }
            else
            {
                counter=0;
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
    private boolean mIsRecordingVideo;
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
            Activity activity = getActivity();
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

    LinearLayout layout_bottom;

    RecyclerView recyviewitem;
    ImageView handleimageview,righthandle;
    LinearLayout linearLayout;
    boolean isflashon = false,inPreview = true;

    ImageView mrecordimagebutton,imgflashon,rotatecamera,handle;

    public Dialog maindialogshare,subdialogshare;
    View rootview = null;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler timerhandler;
    int Seconds, Minutes, MilliSeconds ;
    String keytype ="md5";
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    videoframeadapter madapter;
    long currentframenumber =0;
    long frameduration =15, mframetorecordcount =0;
    public boolean autostartvideo=false,camerastatusok=false;;
    File lastrecordedvideo=null;
    String selectedvideofile ="";
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
          //  simpleSlidingDrawer = (SlidingDrawer) rootview.findViewById(R.id.simpleSlidingDrawer);
            linearLayout=rootview.findViewById(R.id.content);
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);

            recyviewitem = (RecyclerView) rootview.findViewById(R.id.recyview_item);
            mrecordimagebutton.setOnClickListener(this);
            imgflashon.setOnClickListener(this);
            rotatecamera.setOnClickListener(this);
            mTextureView.setOnTouchListener(this);


            handleimageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation rightswipe = AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide);
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
                    Animation leftswipe = AnimationUtils.loadAnimation(getActivity(), R.anim.left_slide);
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

            madapter = new videoframeadapter(getActivity(), mvideoframes, new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {
                }

                @Override
                public void onItemClicked(Object object, int type) {
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyviewitem.setLayoutManager(mLayoutManager);
            recyviewitem.setItemAnimator(new DefaultItemAnimator());
            recyviewitem.setAdapter(madapter);

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
        }
        return rootview;
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
            return Collections.min(bigEnough, new Camera2VideoFragment.CompareSizesByArea());
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
        final Activity activity = getActivity();
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
        mMediaRecorder.setVideoFrameRate(30);
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
        File file=new File(context.getExternalFilesDir(null), fileName+".mp4");
        selectedvideofile=file.getAbsolutePath();
        return file;
    }
    private void startRecordingVideo() {
        try {
            // UI
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
        mIsRecordingVideo = false;

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

                lastrecordedvideo=new File(selectedvideofile);
                /*Activity activity = getActivity();
                if (null != activity) {
                    Toast.makeText(activity, "Video saved: " + getVideoFile(activity),
                            Toast.LENGTH_SHORT).show();
                }*/
                startPreview();

                stopvideotimer();

                mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
                layout_bottom.setVisibility(View.GONE);

                resetvideotimer();
                clearvideolist();

                progressdialog.dismisswaitdialog();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setvideoadapter();
                    }
                }).start();

                progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressdialog.dismisswaitdialog();
                        mrecordimagebutton.setEnabled(true);
                        showsharepopupmain();
                    }
                },2000);
            }
        },100);


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
        }
    }

    public void startstopvideo()
    {
        if (mIsRecordingVideo) {
            mrecordimagebutton.setEnabled(false);

            stopRecordingVideo();
        } else {

            mrecordimagebutton.setEnabled(false);
            mrecordimagebutton.setImageResource(R.drawable.shape_recorder_on);
            imgflashon.setVisibility(View.VISIBLE);
            rotatecamera.setVisibility(View.INVISIBLE);

            currentframenumber =0;
            mframetorecordcount =0;
            currentframenumber = currentframenumber + frameduration;

            mvideoframes.clear();
            madapter.notifyDataSetChanged();

            startRecordingVideo();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
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
            } else { //Single touch point, needs to return true in order to detect one more touch point
                return true;
            }
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
            return true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void setData(boolean autostartvideo) {
        this.autostartvideo = autostartvideo;
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

        camerastatusok=true;
        layout_bottom.setVisibility(View.VISIBLE);
        mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
        gethelper().updateheader("00:00:00");

        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

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
        }
        super.onPause();
    }

    /// camera2video code end


    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_menu:
                fragmentvideolist frag=new fragmentvideolist();
                gethelper().replaceFragment(frag, true, false);
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

    public void exportvideo()
    {
        String sourcePath = lastrecordedvideo.getAbsolutePath();
        File sourceFile = new File(sourcePath);

        File destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), BuildConfig.APPLICATION_ID);

        if (!destinationDir.exists())
            destinationDir.mkdirs();

        File mediaFile = new File(destinationDir.getPath() + File.separator +
                sourceFile.getName());


        try
        {
            if (!mediaFile.getParentFile().exists())
                mediaFile.getParentFile().mkdirs();

            if (!mediaFile.exists()) {
                mediaFile.createNewFile();
            }

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(mediaFile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            try
            {
                ContentValues values = new ContentValues(3);
                values.put(MediaStore.Video.Media.TITLE, "Via composer");
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.DATA, mediaFile.getAbsolutePath());
                applicationviavideocomposer.getactivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(applicationviavideocomposer.getactivity(),"An error occured!",Toast.LENGTH_SHORT).show();
        }

        progressdialog.dismisswaitdialog();
    }

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

    public void setvideoadapter() {

        int count = 1;
        currentframenumber =0;
        currentframenumber = currentframenumber + frameduration;
        final ArrayList<videomodel> arrayList=new ArrayList<videomodel>();
        try
        {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(lastrecordedvideo.getAbsolutePath());
            grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            String format= common.getvideoformat(lastrecordedvideo.getAbsolutePath());
            if(format.equalsIgnoreCase("mp4"))
                grabber.setFormat(format);

            grabber.start();
            boolean isFrameRemain=true;
            ByteBuffer buffer=null;
            AndroidFrameConverter bitmapConverter = new AndroidFrameConverter();
            Log.e("Total frames ",""+grabber.getLengthInFrames());
            for(int i = 0; i<grabber.getLengthInFrames(); i++){
                //grabber.setFrameNumber(count);

                Frame frame = grabber.grabImage();

                if (count == currentframenumber) {
                    isFrameRemain = false;
                    if (frame != null)
                    {
                        //final Bitmap currentImage = bitmapConverter.convert(frame);
                        buffer= ((ByteBuffer) frame.image[0].position(0));

                        byte[] arr = new byte[buffer.remaining()];
                        buffer.get(arr);
                        arrayList.add(updatelistitem(arr,"Frame"));
                    }
                    else
                    {
                        Log.e("Frame ","null");
                    }
                    currentframenumber = currentframenumber + frameduration;
                }
                count++;
            }

            if(isFrameRemain && (buffer != null))
            {
                currentframenumber =count;
                byte[] arr = new byte[buffer.remaining()];
                buffer.get(arr);
                arrayList.add(updatelistitem(arr,"Last Frame"));
            }

            grabber.flush();

            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mvideoframes.clear();
                    madapter.notifyDataSetChanged();

                    mvideoframes.addAll(arrayList);
                    madapter.notifyDataSetChanged();
                    recyviewitem.getLayoutManager().scrollToPosition(0);
                    progressdialog.dismisswaitdialog();
                }
            });

        }
        catch (Exception e)
        {
            progressdialog.dismisswaitdialog();
            e.printStackTrace();
        }
    }

    public videomodel updatelistitem(byte[] array, String message)
    {
        if(array == null || array.length == 0)
            return null;

        String keyvalue= getkeyvalue(array);
        Log.e("number ",""+currentframenumber);
        return new videomodel(message+" "+ keytype +" "+ currentframenumber + ": " + keyvalue);
    }

    public void updatelistitemnotify(final byte[] array, final long framenumber, final String message)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(array == null || array.length == 0)
                    return;
                String keyvalue= getkeyvalue(array);
                mvideoframes.add(new videomodel(message+" "+ keytype +" "+ framenumber + ": " + keyvalue));
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mvideoframes.size() > 0)
                            madapter.notifyItemChanged(mvideoframes.size()-1);

                        //recyviewitem.getLayoutManager().scrollToPosition(mvideoframes.size()-1);
                    }
                });
            }
        }).start();


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

        maindialogshare=new Dialog(getActivity());
        maindialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        maindialogshare.setCanceledOnTouchOutside(true);
        maindialogshare.setContentView(R.layout.popup_sharescreen);
        //maindialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int[] widthHeight=common.getScreenWidthHeight(getActivity());
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

                fullscreenvideofragmentold fullvdofragmnet=new fullscreenvideofragmentold();
                fullvdofragmnet.setdata(lastrecordedvideo.getAbsolutePath());
                gethelper().addFragment(fullvdofragmnet,false,true);
            }
        });
        maindialogshare.show();
    }


    public void showsharepopupsub()
    {
        if(subdialogshare != null && subdialogshare.isShowing())
            subdialogshare.dismiss();

        subdialogshare=new Dialog(getActivity());
        subdialogshare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        subdialogshare.setCanceledOnTouchOutside(true);

        subdialogshare.setContentView(R.layout.popup_sharescreen);
        //subdialogshare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int[] widthHeight=common.getScreenWidthHeight(getActivity());
        int width=widthHeight[0];
        double height=widthHeight[1]/1.6;
        subdialogshare.getWindow().setLayout(width-20, (int)height);

        TextView txt_share_btn1 = (TextView)subdialogshare.findViewById(R.id.txt_share_btn1);
        TextView txt_share_btn2 = (TextView)subdialogshare.findViewById(R.id.txt_share_btn2);
        TextView txt_share_btn3 = (TextView)subdialogshare.findViewById(R.id.txt_share_btn3);
        TextView txt_title1 = (TextView)subdialogshare.findViewById(R.id.txt_title1);
        TextView txt_title2 = (TextView)subdialogshare.findViewById(R.id.txt_title2);

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
                progressdialog.showwaitingdialog(getActivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        exportvideo();

                        if(maindialogshare != null && maindialogshare.isShowing())
                            maindialogshare.dismiss();

                        if(subdialogshare != null && subdialogshare.isShowing())
                            subdialogshare.dismiss();

                        if(maindialogshare != null && maindialogshare.isShowing())
                            maindialogshare.dismiss();

                        launchvideolist();
                    }
                },2000);
            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

                Uri selectedimageuri =Uri.fromFile(new File(lastrecordedvideo.getAbsolutePath()));

                // int duration =  getmediaduration(selectedvideouri);

                final MediaPlayer mp = MediaPlayer.create(getActivity(),selectedimageuri);
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

                if(maindialogshare != null && maindialogshare.isShowing())
                    maindialogshare.dismiss();

                launchvideolist();
            }
        });
        subdialogshare.show();
    }

    public void launchvideolist()
    {
        progressdialog.dismisswaitdialog();
        fragmentvideolist frag=new fragmentvideolist();
        gethelper().replaceFragment(frag, true, false);
    }

    /*class MyCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                if(isflashon) {
                    mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                    isflashon = false;
                } else {
                    mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                    isflashon = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }
    }*/

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
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }



}

