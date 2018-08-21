package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.FullScreenVideoActivity;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.ffmpeg.data.frametorecord;
import com.cryptoserver.composer.ffmpeg.data.recordfragment;
import com.cryptoserver.composer.ffmpeg.fixedratiocroppedtextureview;
import com.cryptoserver.composer.ffmpeg.util.camerahelper;
import com.cryptoserver.composer.ffmpeg.util.miscutils;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.xdata;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

import butterknife.ButterKnife;

import static java.lang.Thread.State.WAITING;

public class writerappfragment extends basefragment implements
        TextureView.SurfaceTextureListener, View.OnClickListener {
    private static final String log_tag = writerappfragment.class.getSimpleName();
    private static final int request_permissions = 1;

    private static final int preferred_preview_width =  1280;
    private static final int preferred_preview_height = 720;

    private int mcameraid;
    private Camera mcamera;
    Camera.Parameters parameters;
    private FFmpegFrameRecorder mframerecorder;
    private videorecordthread mvideorecordthread;
    private audiorecordthread maudiorecordthread;
    private volatile boolean mrecording = false;
    private File mvideo;
    private LinkedBlockingQueue<frametorecord> mframetorecordqueue;
    private LinkedBlockingQueue<frametorecord> mrecycledframequeue;
    private int mframerecordedcount;
    private long mtotalprocessframetime;
    private Stack<recordfragment> mrecordfragments;

    private int sampleaudiorateinhz = 44100;
    /* The sides of width and height are based on camera orientation.
    That is, the preview size is the size before it is rotated. */
    private int mpreviewwidth = preferred_preview_width;
    private int mpreviewheight = preferred_preview_height;
    // Output video size
    private int videowidth = 480;
    private int videoheight = 720;
    private int framerate = 15;
    private int framedepth = Frame.DEPTH_UBYTE;
    private int framechannels = 2;

    // Workaround for https://code.google.com/p/android/issues/detail?id=190966
    private Runnable doafterallpermissionsgranted;

    TextView txt_save;
    TextView txt_clear;
    LinearLayout layout_bottom;
    SlidingDrawer simpleSlidingDrawer;
    RecyclerView recyviewitem;

    boolean isflashon = false,inPreview = true;

    fixedratiocroppedtextureview mpreview;
    ImageView mrecordimagebutton,imgflashon,rotatecamera,handle;

    float mDist = 0;
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
    public boolean autostartvideo=false;

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

            mpreview = (fixedratiocroppedtextureview) rootview.findViewById(R.id.camera_preview);
            mrecordimagebutton = (ImageView) rootview.findViewById(R.id.img_video_capture);
            imgflashon = (ImageView) rootview.findViewById(R.id.img_flash_on);
            rotatecamera = (ImageView) rootview.findViewById(R.id.img_rotate_camera);
            handle = (ImageView) rootview.findViewById(R.id.handle);
            txt_save = (TextView) rootview.findViewById(R.id.txt_save);
            txt_clear = (TextView) rootview.findViewById(R.id.txt_clear);
            layout_bottom = (LinearLayout) rootview.findViewById(R.id.layout_bottom);
            simpleSlidingDrawer = (SlidingDrawer) rootview.findViewById(R.id.simpleSlidingDrawer);
            recyviewitem = (RecyclerView) rootview.findViewById(R.id.recyview_item);
            mcameraid = Camera.CameraInfo.CAMERA_FACING_BACK;
            setpreviewsize(mpreviewwidth, mpreviewheight);
            mpreview.setcroppedsizeweight(videowidth, videoheight);
            mpreview.setSurfaceTextureListener(this);
            mrecordimagebutton.setOnClickListener(this);
            imgflashon.setOnClickListener(this);
            rotatecamera.setOnClickListener(this);

            mpreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    Camera.Parameters params = mcamera.getParameters();
                    int action = event.getAction();


                    if (event.getPointerCount() > 1) {
                        // handle multi-touch events
                        if (action == MotionEvent.ACTION_POINTER_DOWN) {
                            mDist = getFingerSpacing(event);
                        } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                            mcamera.cancelAutoFocus();
                            handleZoom(event, params);
                        }
                    } else {
                        // handle single touch events
                        if (action == MotionEvent.ACTION_UP) {
                            handleFocus(event, params);
                        }
                    }

                    return true;
                }
            });

            /*simpleSlidingDrawer.lock();
            handle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(simpleSlidingDrawer.isOpened())
                    {
                        simpleSlidingDrawer.unlock();
                        simpleSlidingDrawer.animateClose();
                    }
                    else
                    {
                        simpleSlidingDrawer.lock();
                        simpleSlidingDrawer.animateOpen();
                    }
                }
            });*/

            // At most buffer 10 Frame
            mframetorecordqueue = new LinkedBlockingQueue<>(10);
            // At most recycle 2 Frame
            mrecycledframequeue = new LinkedBlockingQueue<>(2);
            mrecordfragments = new Stack<>();

            common.changefocusstyle(txt_save, Color.parseColor("#006495"),Color.parseColor("#006495"),30);
            common.changefocusstyle(txt_clear, Color.parseColor("#006495"),Color.parseColor("#006495"),30);

            txt_save.setOnClickListener(this);
            txt_clear.setOnClickListener(this);

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
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            gethelper().updateheader("" + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%02d", (MilliSeconds/10)));
            timerhandler.postDelayed(this, 0);
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoprecorder();
        releaserecorder(true);
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
    public void onPause() {
        super.onPause();
        pauserecording();
        stoprecording();
        stoppreview();
        releasecamera();
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
                        gethelper().onBack();
                    }
                };
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        startpreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_video_capture:
                startstoprecording();
                break;
            case R.id.txt_save:

                break;
            case R.id.txt_clear:
                resetvideotimer();
                clearvideolist();
                break;

            case R.id.img_flash_on:
                navigateflash();
                break;

            case R.id.img_rotate_camera:
              setrotatecamera();
                break;
        }
    }

    public void startstoprecording()
    {
        if (mrecording) {
            pauserecording();
            progressdialog.showwaitingdialog(getActivity());
            new finishrecordingtask().execute();
        } else {
            mrecordimagebutton.setEnabled(false);
            imgflashon.setVisibility(View.VISIBLE);
            rotatecamera.setVisibility(View.INVISIBLE);

            currentframenumber =0;
            mframetorecordcount =0;
            currentframenumber = currentframenumber + frameduration;

            progressdialog.showwaitingdialog(getActivity());
            mrecordimagebutton.setImageResource(R.drawable.shape_recorder_on);
            new ProgressDialogTask<Void, Integer, Void>(R.string.initiating) {
                @Override
                protected Void doInBackground(Void... params) {
                    if (mframerecorder == null) {

                        initrecorder();
                        startrecorder();
                    }
                    startrecording();
                    resumerecording();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startvideotimer();
                            progressdialog.dismisswaitdialog();
                        }
                    });
                    return null;
                }
            }.execute();
        }
    }

    public void exportvideo()
    {
        /*String sourcePath = mvideo.getAbsolutePath();
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
                values.put(MediaStore.Video.Media.TITLE, "Video hash");
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.DATA, mediaFile.getAbsolutePath());
                getActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(),"An error occured!",Toast.LENGTH_SHORT).show();
        }*/

        try
        {
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Video.Media.TITLE, "Via composer");
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, mvideo.getAbsolutePath());
            getActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        progressdialog.dismisswaitdialog();
    }

    public void clearvideolist()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

               // mvideoframes.clear();
                //madapter.notifyDataSetChanged();
                txt_save.setVisibility(View.GONE);
                txt_clear.setVisibility(View.GONE);
                layout_bottom.setVisibility(View.VISIBLE);
                imgflashon.setVisibility(View.VISIBLE);
                rotatecamera.setVisibility(View.VISIBLE);

            }
        });
    }

    private void doafterallpermissionsgranted() {
        layout_bottom.setVisibility(View.VISIBLE);
        mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);

        acquireCamera();
        SurfaceTexture surfaceTexture = mpreview.getSurfaceTexture();
        if (surfaceTexture != null) {
            // SurfaceTexture already created
            startpreview(surfaceTexture);
        }
    }

    private void setpreviewsize(int width, int height) {
        if (miscutils.isorientationlandscape(getActivity())) {
            mpreview.setpreviewsize(width, height);
        } else {
            // Swap width and height
            mpreview.setpreviewsize(height, width);
        }
    }

    private void startpreview(SurfaceTexture surfaceTexture) {
        if (mcamera == null) {
            return;
        }

        parameters = mcamera.getParameters();
        List<Camera.Size> previewsizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewsize = camerahelper.getoptimalsize(previewsizes,
                preferred_preview_width, preferred_preview_height);
        // if changed, reassign values and request layout
        if (mpreviewwidth != previewsize.width || mpreviewheight != previewsize.height) {
            mpreviewwidth = previewsize.width;
            mpreviewheight = previewsize.height;
            setpreviewsize(mpreviewwidth, mpreviewheight);
            mpreview.requestLayout();
        }
        parameters.setPreviewSize(mpreviewwidth, mpreviewheight);
//        parameters.setPreviewFormat(ImageFormat.NV21);
        mcamera.setParameters(parameters);



        mcamera.setDisplayOrientation(camerahelper.getcameradisplayorientation(
                getActivity(), mcameraid));

        // YCbCr_420_SP (NV21) format
        byte[] bufferByte = new byte[mpreviewwidth * mpreviewheight * 3 / 2];
        mcamera.addCallbackBuffer(bufferByte);
        mcamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {

            private long lastPreviewFrameTime;

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                long thisPreviewFrameTime = System.currentTimeMillis();
                if (lastPreviewFrameTime > 0) {
                    Log.d(log_tag, "Preview frame interval: " + (thisPreviewFrameTime - lastPreviewFrameTime) + "ms");
                }
                lastPreviewFrameTime = thisPreviewFrameTime;

                // get video data
                if (mrecording) {
                    if (maudiorecordthread == null || !maudiorecordthread.isIsrunning()) {
                        // wait for AudioRecord to init and start
                        mrecordfragments.peek().setstarttimestamp(System.currentTimeMillis());
                    } else {
                        // pop the current record fragment when calculate total recorded time
                        recordfragment curFragment = mrecordfragments.pop();
                        long recordedTime = calculatetotalrecordedtime(mrecordfragments);
                        // push it back after calculation
                        mrecordfragments.push(curFragment);
                        long curRecordedTime = System.currentTimeMillis()
                                - curFragment.getstarttimestamp() + recordedTime;
                        // check if exceeds time limit
                        /*if (curRecordedTime > MAX_VIDEO_LENGTH) {
                            pauserecording();
                            new finishrecordingtask().execute();
                            return;
                        }*/

                        long timestamp = 1000 * curRecordedTime;
                        Frame frame;
                        frametorecord frametorecord = mrecycledframequeue.poll();
                        if (frametorecord != null) {
                            frame = frametorecord.getframe();
                            frametorecord.settimestamp(timestamp);
                        } else {
                            frame = new Frame(mpreviewwidth, mpreviewheight, framedepth, framechannels);
                            frametorecord = new frametorecord(timestamp, frame);
                        }

                        ((ByteBuffer) frame.image[0].position(0)).put(data);

                        try {
                            ByteBuffer buffer= ((ByteBuffer) frame.image[0].position(0));
                            //isFrameRemain=true;
                            if(mframetorecordcount == currentframenumber)
                            {
                                //isFrameRemain=false;
                                byte[] arr = new byte[buffer.remaining()];
                                buffer.get(arr);
                                updatelistitemnotify(arr,currentframenumber,"Frame");
                                currentframenumber = currentframenumber + frameduration;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (mframetorecordqueue.offer(frametorecord)) {
                            mframetorecordcount++;
                        }
                    }
                }
                mcamera.addCallbackBuffer(data);
            }
        });

        try {
            mcamera.setPreviewTexture(surfaceTexture);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        mcamera.startPreview();
        inPreview= true;

        if(autostartvideo)
        {
            autostartvideo=false;
            startstoprecording();
        }
    }

    private void stoppreview() {
        if (mcamera != null) {
            mcamera.stopPreview();

            mcamera.setPreviewCallbackWithBuffer(null);
        }
    }

    private void acquireCamera() {
        try {
            mcamera = Camera.open(mcameraid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasecamera() {
        if (mcamera != null) {
            mcamera.release();        // release the camera for other applications
            mcamera = null;
        }
    }

    private void initrecorder() {
        Log.i(log_tag, "init mframerecorder");

        mvideo = camerahelper.getoutputmediafile();

        Log.i(log_tag, "Output Video: " + mvideo);

        try {
            mframerecorder = new FFmpegFrameRecorder(mvideo, videowidth, videoheight, 1);
            mframerecorder.setFormat("mp4");
            mframerecorder.setSampleRate(sampleaudiorateinhz);
            mframerecorder.setFrameRate(framerate);
            // Use H264
            mframerecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            // See: https://trac.ffmpeg.org/wiki/Encode/H.264#crf
        /*
         * The range of the quantizer scale is 0-51: where 0 is lossless, 23 is default, and 51 is worst possible. A lower value is a higher quality and a subjectively sane range is 18-28. Consider 18 to be visually lossless or nearly so: it should look the same or nearly the same as the input but it isn't technically lossless.
         * The range is exponential, so increasing the CRF value +6 is roughly half the bitrate while -6 is roughly twice the bitrate. General usage is to choose the highest CRF value that still provides an acceptable quality. If the output looks good, then try a higher value and if it looks bad then choose a lower value.
         */
            mframerecorder.setVideoOption("crf", "28");
            mframerecorder.setVideoOption("preset", "superfast");
            mframerecorder.setVideoOption("tune", "zerolatency");

            Log.e("init recorder" ,"init mframerecorder");

            Log.i(log_tag, "mframerecorder initialize success");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void releaserecorder(boolean deleteFile) {
        if (mframerecorder != null) {
            try {
                mframerecorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            mframerecorder = null;

            if (deleteFile) {
                mvideo.delete();
            }
        }
    }

    private void startrecorder() {
        try {
            mframerecorder.start();
            Log.e("startrecorder()" ,"startrecorder in method");

        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    private void stoprecorder() {
        if (mframerecorder != null) {
            try {
                Log.e("stoprecorder()" ,"stoprecorder in method");
                mframerecorder.stop();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }

        mrecordfragments.clear();
    }

    private void startrecording() {
        maudiorecordthread = new audiorecordthread();
        maudiorecordthread.start();
        Log.e("maudiorecordthread" ,"audiorecordthread start");
        mvideorecordthread = new videorecordthread();
        mvideorecordthread.start();

        Log.e("mvideorecordthread" ,"videorecordthread start");
    }

    private void stoprecording() {
        if (maudiorecordthread != null) {
            if (maudiorecordthread.isIsrunning()) {
                maudiorecordthread.stopRunning();
                Log.e("stop Audio Trading" ,"maudiorecordthread stop");
            }
        }

        if (mvideorecordthread != null) {
            if (mvideorecordthread.isIsrunning()) {
                mvideorecordthread.stopRunning();
                Log.e("stop Video Trading" ,"mvideorecordthread stop");
            }
        }

        try {
            if (maudiorecordthread != null) {
                maudiorecordthread.join();
            }
            if (mvideorecordthread != null) {
                mvideorecordthread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        maudiorecordthread = null;
        mvideorecordthread = null;


        mframetorecordqueue.clear();
        mrecycledframequeue.clear();
    }

    private void resumerecording() {
        if (!mrecording) {
            Log.e("resumerecording()","ResumeRecording in method");
            recordfragment recordfragment = new recordfragment();
            recordfragment.setstarttimestamp(System.currentTimeMillis());
            mrecordfragments.push(recordfragment);

            mrecording = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mrecordimagebutton.setEnabled(true);
                }
            });
        }
    }

    private void pauserecording() {
        if (mrecording) {
            getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopvideotimer();
                    }
                });
            mrecordfragments.peek().setendtimestamp(System.currentTimeMillis());
            mrecording = false;
        }
    }

    private long calculatetotalrecordedtime(Stack<recordfragment> recordfragments) {
        long recordedtime = 0;
        for (recordfragment recordfragment : recordfragments) {
            recordedtime += recordfragment.getduration();
        }
        return recordedtime;
    }

    public void setData(boolean autostartvideo) {
        this.autostartvideo = autostartvideo;
    }

    class runningthread extends Thread {
        boolean isrunning;

        public boolean isIsrunning() {
            return isrunning;
        }

        public void stopRunning() {
            this.isrunning = false;
        }
    }

    class audiorecordthread extends runningthread {
        private AudioRecord maudiorecord;
        private ShortBuffer audiodata;

        public audiorecordthread() {
            int buffersize = AudioRecord.getMinBufferSize(sampleaudiorateinhz,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            maudiorecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleaudiorateinhz,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize);

            audiodata = ShortBuffer.allocate(buffersize);
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            Log.d(log_tag, "maudiorecord startrecording");
            maudiorecord.startRecording();

            isrunning = true;
            /* ffmpeg_audio encoding loop */
            while (isrunning) {
                if (mrecording && mframerecorder != null) {
                    int bufferReadResult = maudiorecord.read(audiodata.array(), 0, audiodata.capacity());
                    audiodata.limit(bufferReadResult);
                    if (bufferReadResult > 0) {
                        Log.v(log_tag, "bufferReadResult: " + bufferReadResult);
                        try {
                            mframerecorder.recordSamples(audiodata);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(log_tag, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.d(log_tag, "maudiorecord stoprecording");
            maudiorecord.stop();
            maudiorecord.release();
            maudiorecord = null;
            Log.d(log_tag, "maudiorecord released");
        }
    }

    class videorecordthread extends runningthread {
        @Override
        public void run() {
            int previewwidth = mpreviewwidth;
            int previewheight = mpreviewheight;

            List<String> filters = new ArrayList<>();
            // Transpose
            String transpose = null;
            String hflip = null;
            String vflip = null;
            String crop = null;
            String scale = null;
            int cropwidth;
            int cropheight;
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mcameraid, info);
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    switch (info.orientation) {
                        case 270:
                            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                transpose = "transpose=clock_flip"; // Same as preview display
                            } else {
                                transpose = "transpose=cclock"; // Mirrored horizontally as preview display
                            }
                            break;
                        case 90:
                            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                transpose = "transpose=cclock_flip"; // Same as preview display
                            } else {
                                transpose = "transpose=clock"; // Mirrored horizontally as preview display
                            }
                            break;
                    }
                    cropwidth = previewheight;
                    cropheight = cropwidth * videoheight / videowidth;
                    crop = String.format("crop=%d:%d:%d:%d",
                            cropwidth, cropheight,
                            (previewheight - cropwidth) / 2, (previewwidth - cropheight) / 2);
                    // swap width and height
                    scale = String.format("scale=%d:%d", videoheight, videowidth);
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    switch (rotation) {
                        case Surface.ROTATION_90:
                            // landscape-left
                            switch (info.orientation) {
                                case 270:
                                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                        hflip = "hflip";
                                    }
                                    break;
                            }
                            break;
                        case Surface.ROTATION_270:
                            // landscape-right
                            switch (info.orientation) {
                                case 90:
                                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                        hflip = "hflip";
                                        vflip = "vflip";
                                    }
                                    break;
                                case 270:
                                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                        vflip = "vflip";
                                    }
                                    break;
                            }
                            break;
                    }
                    cropheight = previewheight;
                    cropwidth = cropheight * videowidth / videoheight;
                    crop = String.format("crop=%d:%d:%d:%d",
                            cropwidth, cropheight,
                            (previewwidth - cropwidth) / 2, (previewheight - cropheight) / 2);
                    scale = String.format("scale=%d:%d", videowidth, videoheight);
                    break;
                case Surface.ROTATION_180:
                    break;
            }
            // transpose
            if (transpose != null) {
                filters.add(transpose);
            }
            // horizontal flip
            if (hflip != null) {
                filters.add(hflip);
            }
            // vertical flip
            if (vflip != null) {
                filters.add(vflip);
            }
            // crop
            if (crop != null) {
                filters.add(crop);
            }
            // scale (to designated size)
            if (scale != null) {
                filters.add(scale);
            }

            FFmpegFrameFilter frameFilter = new FFmpegFrameFilter(TextUtils.join(",", filters),
                    previewwidth, previewheight);
            frameFilter.setPixelFormat(avutil.AV_PIX_FMT_NV21);
            frameFilter.setFrameRate(framerate);
            try {
                frameFilter.start();
            } catch (FrameFilter.Exception e) {
                e.printStackTrace();
            }

            isrunning = true;
            frametorecord recordedFrame;

            while (isrunning || !mframetorecordqueue.isEmpty()) {
                try {
                    recordedFrame = mframetorecordqueue.take();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    try {
                        frameFilter.stop();
                    } catch (FrameFilter.Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                if (mframerecorder != null) {
                    long timestamp = recordedFrame.gettimestamp();
                    if (timestamp > mframerecorder.getTimestamp()) {
                        mframerecorder.setTimestamp(timestamp);
                    }
                    else {
                        Log.e(log_tag, "Incorrect timestamp: " + timestamp + " when Recorder on: " + mframerecorder.getTimestamp());
                    }
                    long startTime = System.currentTimeMillis();
//                    Frame filteredFrame = recordedFrame.getframe();
                    Frame filteredFrame = null;
                    try {
                        frameFilter.push(recordedFrame.getframe());
                        filteredFrame = frameFilter.pull();
                    } catch (FrameFilter.Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        mframerecorder.record(filteredFrame);
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    mtotalprocessframetime += processTime;
                    Log.d(log_tag, "This frame process time: " + processTime + "ms");
                    long totalAvg = mtotalprocessframetime / ++mframerecordedcount;
                    Log.d(log_tag, "Avg frame process time: " + totalAvg + "ms");
                }
                Log.d(log_tag, mframerecordedcount + " / " + mframetorecordcount);
                mrecycledframequeue.offer(recordedFrame);
            }
        }

        public void stopRunning() {
            super.stopRunning();
            if (getState() == WAITING) {
                interrupt();
            }
        }
    }

    abstract class ProgressDialogTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

        private int promptRes;

        public ProgressDialogTask(int promptRes) {
            this.promptRes = promptRes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);
//            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
        }
    }

    class finishrecordingtask extends ProgressDialogTask<Void, Integer, Void> {

        public finishrecordingtask() {
            super(R.string.processing);
        }

        @Override
        protected Void doInBackground(Void... params) {
            stoprecording();
            stoprecorder();
            releaserecorder(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //saveTempFile();

            mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
            layout_bottom.setVisibility(View.GONE);

            resetvideotimer();
            clearvideolist();

           new Thread(new Runnable() {
                @Override
                public void run() {
                    setvideoadapter();
                }
            }).start();

            showsharepopupmain();

          //  txt_save.setVisibility(View.VISIBLE);
          //  txt_clear.setVisibility(View.VISIBLE);


            //Toast.makeText(writerappactivity.this,"Saved ",Toast.LENGTH_SHORT).show();
            /*Intent intent = new Intent(writerappactivity.this, playbackactivity.class);
            intent.putExtra(playbackactivity.intent_name_video_path, mvideo.getpath());
            startActivity(intent);*/
        }
    }

    // Turning Off flash
    private void navigateflash() {
        if (mcamera == null || parameters == null) {
            return;
        }
        parameters = mcamera.getParameters();
        if(isflashon)
        {
            imgflashon.setImageResource(R.drawable.flash_off);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            isflashon = false;
        }
        else
        {
            imgflashon.setImageResource(R.drawable.flash_on);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            isflashon = true;
        }
        mcamera.setParameters(parameters);
    }

    public void setrotatecamera(){

        stoppreview();
        releasecamera();
        if(mcameraid == Camera.CameraInfo.CAMERA_FACING_BACK){
            mcameraid = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            mcameraid = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        doafterallpermissionsgranted();
        if(isflashon)
        {
            isflashon=false;
            navigateflash();
        }
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mcamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mcamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    public void setvideoadapter() {

        int count = 1;
        currentframenumber =0;
        currentframenumber = currentframenumber + frameduration;
        final ArrayList<videomodel> arrayList=new ArrayList<videomodel>();
        try
        {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mvideo.getAbsolutePath());
            grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            String format= common.getvideoformat(mvideo.getAbsolutePath());
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

            getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        madapter.notifyDataSetChanged();
                        recyviewitem.getLayoutManager().scrollToPosition(mvideoframes.size()-1);
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
                Intent in=new Intent(getActivity(), FullScreenVideoActivity.class);
                in.putExtra("videopath",mvideo.getAbsolutePath());
                startActivity(in);
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

                if(mvideo != null)
                    exportvideo();

                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

                if(maindialogshare != null && maindialogshare.isShowing())
                    maindialogshare.dismiss();

                launchvideolist();
            }
        });

        txt_share_btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(subdialogshare != null && subdialogshare.isShowing())
                    subdialogshare.dismiss();

                videoplayfragment videoplayfragment =new videoplayfragment();
                videoplayfragment.setdata(mvideo.getAbsolutePath());
                gethelper().replaceFragment(videoplayfragment, false, true);

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
        fragmentvideolist frag=new fragmentvideolist();
        gethelper().replaceFragment(frag, true, false);
    }
}

