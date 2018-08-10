package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.ffmpeg.data.frametorecord;
import com.cryptoserver.composer.ffmpeg.data.recordfragment;
import com.cryptoserver.composer.ffmpeg.fixedratiocroppedtextureview;
import com.cryptoserver.composer.ffmpeg.util.camerahelper;
import com.cryptoserver.composer.ffmpeg.util.miscutils;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.progressdialog;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;

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

import static java.lang.Thread.State.WAITING;

public class writerappactivity extends AppCompatActivity implements
        TextureView.SurfaceTextureListener, View.OnClickListener {
    private static final String log_tag = writerappactivity.class.getSimpleName();
    Chronometer timer;
    private static final int request_permissions = 1;

    private static final int preferred_preview_width = 720;
    private static final int preferred_preview_height = 1280;

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
    private int videowidth = 320;
    private int videoheight = 240;
    private int framerate = 30;
    private int framedepth = Frame.DEPTH_UBYTE;
    private int framechannels = 2;

    // Workaround for https://code.google.com/p/android/issues/detail?id=190966
    private Runnable doafterallpermissionsgranted;

    TextView txt_save;
    TextView txt_clear;
    LinearLayout layout_bottom;

    boolean isflashon = false,inPreview = true;

    fixedratiocroppedtextureview mpreview;
    ImageView mrecordimagebutton,imgflashon,rotatecamera;

    long currentframenumber =0;
    long frameduration =4, mframetorecordcount =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationviavideocomposer.setActivity(writerappactivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.fragment_videocomposer);
        //ButterKnife.bind(this);
        mpreview = (fixedratiocroppedtextureview) findViewById(R.id.camera_preview);
        mrecordimagebutton = (ImageView) findViewById(R.id.img_video_capture);
        imgflashon = (ImageView) findViewById(R.id.img_flash_on);
        rotatecamera = (ImageView) findViewById(R.id.img_rotate_camera);
        txt_save = (TextView) findViewById(R.id.txt_save);
        txt_clear = (TextView) findViewById(R.id.txt_clear);
        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
//        mcameraid = Camera.CameraInfo.CAMERA_FACING_BACK;
        mcameraid = Camera.CameraInfo.CAMERA_FACING_BACK;
        setpreviewsize(mpreviewwidth, mpreviewheight);
        mpreview.setcroppedsizeweight(videowidth, videoheight);
        mpreview.setSurfaceTextureListener(this);
        mrecordimagebutton.setOnClickListener(this);
        imgflashon.setOnClickListener(this);
        rotatecamera.setOnClickListener(this);

        // At most buffer 10 Frame
        mframetorecordqueue = new LinkedBlockingQueue<>(10);
        // At most recycle 2 Frame
        mrecycledframequeue = new LinkedBlockingQueue<>(2);
        mrecordfragments = new Stack<>();

        common.changefocusstyle(txt_save, Color.parseColor("#006495"),Color.parseColor("#006495"),30);
        common.changefocusstyle(txt_clear, Color.parseColor("#006495"),Color.parseColor("#006495"),30);

        txt_save.setOnClickListener(this);
        txt_clear.setOnClickListener(this);

        timer=findViewById(R.id.timer);
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });
        timer.setText("00:00:00");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stoprecorder();
        releaserecorder(true);
    }

    @Override
    protected void onResume() {
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
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedpermissions.add(permission);
                }
            }
            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterallpermissionsgranted();
            } else {
                String[] array = new String[deniedpermissions.size()];
                array = deniedpermissions.toArray(array);
                ActivityCompat.requestPermissions(this, array, request_permissions);
            }
        }
    }

    @Override
    protected void onPause() {
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
                        Toast.makeText(writerappactivity.this, R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
                        finish();
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
                if (mrecording) {
                    pauserecording();
                    new finishrecordingtask().execute();
                } else {
                   mrecordimagebutton.setClickable(false);
                    imgflashon.setVisibility(View.INVISIBLE);
                    rotatecamera.setVisibility(View.INVISIBLE);

                    //common.hidekeyboard(writerappactivity.this);
                    /*frameduration =Long.parseLong(edt_framesduration.getText().toString());
                    if(frameduration > 0)
                    {

                    }*/
                    currentframenumber =0;
                    mframetorecordcount =0;
                    currentframenumber = currentframenumber + frameduration;

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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timer.setBase(SystemClock.elapsedRealtime());
                                    starttimer();
                                }
                            });
                            return null;
                        }
                    }.execute();
                }

                break;
            case R.id.txt_save:
                progressdialog.showwaitingdialog(writerappactivity.this);
                saveTempFile();
                break;
            case R.id.txt_clear:
                resettimer();
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

    public void saveTempFile()
    {
        String sourcePath = mvideo.getAbsolutePath();
        File sourceFile = new File(sourcePath);

        String destinationPath = config.videodir;
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName="VIA_"+fileName;
        File destinationFile = new File(destinationPath+File.separator+fileName+".mp4");
        try
        {
            if (!destinationFile.getParentFile().exists())
                destinationFile.getParentFile().mkdirs();

            if (!destinationFile.exists()) {
                destinationFile.createNewFile();
            }

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destinationFile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            exportvideo(destinationFile);
            resettimer();
            clearvideolist();

            Toast.makeText(writerappactivity.this,"Video saved successfully!",Toast.LENGTH_SHORT).show();

        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(writerappactivity.this,"An error occured!",Toast.LENGTH_SHORT).show();
        }
        progressdialog.dismisswaitdialog();
    }

    public void resettimer()
    {
        timer.stop();
        timer.setText("00:00:00");
    }
    public void exportvideo(File file)
    {
        if(mvideo != null)
        {
           // mvideoframes.add(new videomodel("Exported video to camera roll"));
            try
            {
                ContentValues values = new ContentValues(3);
                values.put(MediaStore.Video.Media.TITLE, "Video hash");
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
                getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

       /* madapter.notifyDataSetChanged();
        recyviewitem.getLayoutManager().scrollToPosition(mvideoframes.size()-1);
        progressdialog.dismisswaitdialog();*/
    }

    public void clearvideolist()
    {
        runOnUiThread(new Runnable() {
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
        acquireCamera();
        SurfaceTexture surfaceTexture = mpreview.getSurfaceTexture();
        if (surfaceTexture != null) {
            // SurfaceTexture already created
            startpreview(surfaceTexture);
        }

        if(txt_save.getVisibility() == View.GONE)
        {
            layout_bottom.setVisibility(View.VISIBLE);
            mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
        }
    }

    private void setpreviewsize(int width, int height) {
        if (miscutils.isorientationlandscape(this)) {
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
                this, mcameraid));

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
            mrecordimagebutton.setClickable(true);
        }
    }

    private void pauserecording() {
        if (mrecording) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stoptimer();
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
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
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

            mrecordimagebutton.setImageResource(R.drawable.shape_recorder_off);
            layout_bottom.setVisibility(View.GONE);

            txt_save.setVisibility(View.VISIBLE);
            txt_clear.setVisibility(View.VISIBLE);

            //Toast.makeText(writerappactivity.this,"Saved ",Toast.LENGTH_SHORT).show();
            /*Intent intent = new Intent(writerappactivity.this, playbackactivity.class);
            intent.putExtra(playbackactivity.intent_name_video_path, mvideo.getpath());
            startActivity(intent);*/
        }
    }
    public void starttimer(){
        //timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();
    }

    public void stoptimer(){
        //  timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();

    }

    // Turning Off flash
    private void navigateflash() {
        if (mcamera == null || parameters == null) {
            return;
        }
        parameters = mcamera.getParameters();
        if(isflashon)
        {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            isflashon = false;
        }
        else
        {
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
    }


}

