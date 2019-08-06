/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.deeptruth.app.android.videotrimmer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.videotrimmer.interfaces.onhglvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.onprogressvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.onrangeseekbarlistener;
import com.deeptruth.app.android.videotrimmer.interfaces.ontrimvideolistener;
import com.deeptruth.app.android.videotrimmer.utils.backgroundexecutor;
import com.deeptruth.app.android.videotrimmer.utils.uithreadexecutor;
import com.deeptruth.app.android.videotrimmer.view.progressbarview;
import com.deeptruth.app.android.videotrimmer.view.rangeseekbarview;
import com.deeptruth.app.android.videotrimmer.view.thumb;
import com.deeptruth.app.android.videotrimmer.view.timelineview;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class hglvideotrimmer extends FrameLayout implements View.OnClickListener {

    private static final String tag = hglvideotrimmer.class.getSimpleName();
    private static final int min_time_frame = 1000;
    private static final int show_progress = 2;

    private SeekBar mholdertopview;
    private rangeseekbarview mrangeseekbarview;
    private RelativeLayout mlinearvideo;
    private View mtimeinfocontainer;
    private VideoView mvideoview;
    private ImageView mplayview;
    private TextView mtextsize;
    private TextView mtexttimeframe;
    private TextView mtexttime;
    private TextView txt_cancel;
    private ImageView img_share_icon;
    private timelineview mtimelineview;
    RelativeLayout rl_playpausebtn;
    int devicewidth = 0;

    private progressbarview mvideoprogressindicator,mvideoprogressindicatorbottom;
    private Uri msrc;
    private String mfinalpath;

    private int mmaxduration;
    private List<onprogressvideolistener> mlisteners;

    private ontrimvideolistener montrimvideolistener;
    private onhglvideolistener monhglvideolistener;

    private int mduration = 0;
    private int mtimevideo = 0;
    private int mstartposition = 0;
    private int mendposition = 0;
    private long lasttime=0;
    Context context;

    FFmpeg ffmpeg;
    File file;


    private long moriginsizefile;
    private boolean mresetseekbar = true;
    private final MessageHandler mmessagehandler = new MessageHandler(this);

    public hglvideotrimmer(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;

    }

    public hglvideotrimmer(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_time_line, this, true);

        mholdertopview = ((SeekBar) findViewById(R.id.handlerTop));
        mvideoprogressindicator = ((progressbarview) findViewById(R.id.timeVideoView));
        mvideoprogressindicatorbottom = ((progressbarview) findViewById(R.id.timeVideoView1));
        mrangeseekbarview = ((rangeseekbarview) findViewById(R.id.timeLineBar));
        mlinearvideo = ((RelativeLayout) findViewById(R.id.layout_surface_view));
        mvideoview = ((VideoView) findViewById(R.id.video_loader));
        mplayview = ((ImageView) findViewById(R.id.icon_video_play));
        mtimeinfocontainer = findViewById(R.id.timeText);
        mtextsize = ((TextView) findViewById(R.id.textSize));
        mtexttimeframe = ((TextView) findViewById(R.id.textTimeSelection));
        mtexttime = ((TextView) findViewById(R.id.textTime));
        mtimelineview = ((timelineview) findViewById(R.id.timeLineView));
        txt_cancel = ((TextView) findViewById(R.id.txt_cancel));
        img_share_icon = ((ImageView) findViewById(R.id.img_share_icon));
        rl_playpausebtn = ((RelativeLayout) findViewById(R.id.rl_playpausebtn));

        img_share_icon.setOnClickListener(this);
        txt_cancel.setOnClickListener(this);

        try {
            if(mplayview.getDrawable() != null)
                DrawableCompat.setTint(mplayview.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        /*try {
            DrawableCompat.setTint(btn_gallerylist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                    , R.color.img_bg));
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/


        mholdertopview.setEnabled(false);

        setuplisteners();
        setupmargins();

        loadffmpegbinary();
    }

    private void setuplisteners() {
        mlisteners = new ArrayList<>();
        mlisteners.add(new onprogressvideolistener() {
            @Override
            public void updateprogress(int time, int max, float scale) {
               // Log.e("updated progress time","" + time);
                updateVideoProgress(time);
            }
        });
       // mlisteners.add(mvideoprogressindicator);
        //mlisteners.add(mvideoprogressindicatorbottom);

        findViewById(R.id.btCancel)
                .setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onCancelClicked();
                            }
                        }
                );

        findViewById(R.id.btSave)
                .setOnClickListener(
                        new OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
                            @Override
                            public void onClick(View view) {
                                onSaveClicked();
                            }
                        }
                );

        final GestureDetector gestureDetector = new
                GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        onClickVideoPlayPause();
                        return true;
                    }
                }
        );

        mvideoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                if (montrimvideolistener != null)
                    montrimvideolistener.onerror("Something went wrong reason : " + what);
                return false;
            }
        });



        mvideoview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                //gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        rl_playpausebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickVideoPlayPause();
            }
        });

        mrangeseekbarview.addonrangeseekbarlistener(mvideoprogressindicator);

        mrangeseekbarview.addonrangeseekbarlistener(mvideoprogressindicatorbottom);

        mrangeseekbarview.addonrangeseekbarlistener(new onrangeseekbarlistener() {
        @Override
        public void oncreate(rangeseekbarview rangeseekbarview, int index, float value) {

        }

        @Override
        public void onseek(rangeseekbarview rangeseekbarview, int index, float value) {
            onSeekThumbs(index, value);
        }

        @Override
        public void onseekstart(rangeseekbarview rangeseekbarview, int index, float value) {
            if (montrimvideolistener != null)
                montrimvideolistener.ontrimstarted();
        }

        @Override
        public void onseekstop(rangeseekbarview rangeseekbarview, int index, float value) {
            onStopSeekThumbs();
        }
        });



        mholdertopview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onPlayerIndicatorSeekChanged(progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onPlayerIndicatorSeekStart();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onPlayerIndicatorSeekStop(seekBar);
            }
        });

        mvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onVideoPrepared(mp);
            }
        });

        mvideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onVideoCompleted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupmargins() {
        int marge = mrangeseekbarview.getthumbs().get(0).getwidthbitmap();
        int widthSeek = mholdertopview.getThumb().getMinimumWidth() / 2;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mholdertopview.getLayoutParams();
        Log.e("margine Right","" + (marge - widthSeek));
        lp.setMargins(marge , 0, 15, 0);
        mholdertopview.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mtimelineview.getLayoutParams();
        lp.setMargins(marge , 0, marge, 0);
        mtimelineview.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mvideoprogressindicator.getLayoutParams();
        lp.setMargins(marge, 0, marge , 0);
        mvideoprogressindicator.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mvideoprogressindicatorbottom.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mvideoprogressindicatorbottom.setLayoutParams(lp);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)

    public void onSaveClicked() {
            mplayview.setBackgroundResource(R.drawable.ic_white_play);
            mplayview.setVisibility(View.VISIBLE);
            mvideoview.pause();

            progressdialog.showwaitingdialog(context);

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(getContext(), msrc);
            long METADATA_KEY_DURATION = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

           file = new File(msrc.getPath());

            if (mtimevideo < min_time_frame) {

                if ((METADATA_KEY_DURATION - mendposition) > (min_time_frame - mtimevideo)) {
                    mendposition += (min_time_frame - mtimevideo);
                } else if (mstartposition > (min_time_frame - mtimevideo)) {
                    mstartposition -= (min_time_frame - mtimevideo);
                }
            }

            //notify that video trimming started
            if (montrimvideolistener != null)
                    montrimvideolistener.ontrimstarted();

            backgroundexecutor.execute(
                    new backgroundexecutor.task("", 0L, "") {
                        @Override
                        public void execute() {
                            try {
                                executeCutVideoCommand(mstartposition, mendposition);
                                //trimvideoutils.starttrim(file, getDestinationPath(), mstartposition, mendposition, montrimvideolistener);
                            } catch (final Throwable e) {
                                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                            }
                        }
                    }
            );
       // }
    }

    private void onClickVideoPlayPause() {
        if (mvideoview.isPlaying()) {
            mplayview.setBackgroundResource(R.drawable.ic_white_play);
            mmessagehandler.removeMessages(show_progress);
            mvideoview.pause();
        } else {
            mplayview.setBackgroundResource(R.drawable.ic_white_pause);
            if (mresetseekbar) {
                mresetseekbar = false;
                mvideoview.seekTo(mstartposition);
            }

            mmessagehandler.sendEmptyMessage(show_progress);
            mvideoview.start();
        }
    }

    private void onCancelClicked() {
        mvideoview.stopPlayback();
        if (montrimvideolistener != null) {
            montrimvideolistener.cancelaction();
        }
    }

    private String getDestinationPath() {
        if (mfinalpath == null) {
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), BuildConfig.APPLICATION_ID);;

            if (!folder.exists())
                folder.mkdirs();

            mfinalpath = folder.getPath() + File.separator;
            Log.d(tag, "Using default path " + mfinalpath);
        }
        return mfinalpath;
    }

    private void onPlayerIndicatorSeekChanged(int progress, boolean fromUser) {

        int duration = (int) ((mduration * progress) / 1000L);

        if (fromUser) {
            if (duration < mstartposition) {
                setProgressBarPosition(mstartposition);
                duration = mstartposition;
            } else if (duration > mendposition) {
                setProgressBarPosition(mendposition);
                duration = mendposition;
            }
            setTimeVideo(duration);
        }
    }

    private void onPlayerIndicatorSeekStart() {
        mmessagehandler.removeMessages(show_progress);
        mvideoview.pause();
        mplayview.setVisibility(View.VISIBLE);
        notifyProgressUpdate(false);
    }

    private void onPlayerIndicatorSeekStop(@NonNull SeekBar seekBar) {
        mmessagehandler.removeMessages(show_progress);
        mvideoview.pause();

        mplayview.setBackgroundResource(R.drawable.ic_white_play);
        mplayview.setVisibility(View.VISIBLE);

        int duration = (int) ((mduration * seekBar.getProgress()) / 1000L);

        mvideoview.seekTo(duration);
        setTimeVideo(duration);
        notifyProgressUpdate(false);
    }

    private void onVideoPrepared(@NonNull MediaPlayer mp) {
        // Adjust the size of the video
        // so it fits on the screen
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = mlinearvideo.getWidth();
        int screenHeight = mlinearvideo.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        ViewGroup.LayoutParams lp = mvideoview.getLayoutParams();

        if (montrimvideolistener != null)
            devicewidth =   montrimvideolistener.getwidth();

        int percentagewidth = (devicewidth / 100) * 15;


        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight)-percentagewidth;
            lp.height = screenHeight;
        }
        mvideoview.setLayoutParams(lp);

        mplayview.setBackgroundResource(R.drawable.ic_white_play);
        mplayview.setVisibility(View.VISIBLE);

        mduration = mvideoview.getDuration();
        setSeekBarPosition();

        setTimeFrames();
        setTimeVideo(0);

        if (monhglvideolistener != null) {
            monhglvideolistener.onvideoprepared();
        }
    }

    private void setSeekBarPosition() {

        if (mduration >= mmaxduration) {
            mstartposition = mduration / 2 - mmaxduration / 2;
            mendposition = mduration / 2 + mmaxduration / 2;

            mrangeseekbarview.setThumbValue(0, (mstartposition * 100) / mduration);
            mrangeseekbarview.setThumbValue(1, (mendposition * 100) / mduration);

            lasttime = 0;

        } else {
            mstartposition = 0;
            lasttime = 0;
            mendposition = mduration;
        }

        setProgressBarPosition(mstartposition);
        mvideoview.seekTo(mstartposition);

        mtimevideo = mduration;
        mrangeseekbarview.initmaxwidth();
    }

    private void setTimeFrames() {
        String seconds = getContext().getString(R.string.short_seconds);
       // mtextsize.setText(String.format(stringfortime(mstartposition), seconds));
       // mtexttime.setText(String.format( stringfortime(mendposition), seconds));

       // mtexttimeframe.setText(String.format("%s %s - %s %s", stringfortime(mstartposition), seconds, stringfortime(mendposition), seconds));
       // mtexttimeframe.setVisibility(VISIBLE);
    }

    private void setTimeVideo(int position) {
        String seconds = getContext().getString(R.string.short_seconds);
       // mtexttime.setText(String.format("%s %s", stringfortime(position), seconds));
        //mtexttime.setVisibility(VISIBLE);
    }

    public String stringfortime(int timeMs) {
        int totalseconds = timeMs / 1000;
        int seconds = totalseconds % 60;
        int minutes = (totalseconds / 60) % 60;
        int hours = totalseconds / 3600;

        Formatter mformatter = new Formatter();
        if (hours > 0) {
            return mformatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mformatter.format("%02d:%02d:%02d",hours, minutes, seconds).toString();
        }
    }

    private void onSeekThumbs(int index, float value) {
        switch (index) {
            case thumb.left: {
                mstartposition = (int) ((mduration * value) / 100L);
                mvideoview.seekTo(mstartposition);
                lasttime=mstartposition;
                break;
            }
            case thumb.right: {
                mendposition = (int) ((mduration * value) / 100L);
                break;
            }
        }
        setProgressBarPosition(mstartposition);

        setTimeFrames();
        mtimevideo = mendposition - mstartposition;
    }

    private void onStopSeekThumbs() {
        mmessagehandler.removeMessages(show_progress);
        mvideoview.pause();
        mplayview.setBackgroundResource(R.drawable.ic_white_play);
        mplayview.setVisibility(View.VISIBLE);
    }

    private void onVideoCompleted() {
        mplayview.setBackgroundResource(R.drawable.ic_white_play);

       // Log.e("onComplete =", ""+ mstartposition);
        mvideoview.seekTo(mstartposition);
    }

    private void notifyProgressUpdate(boolean all) {
        if (mduration == 0) return;

        int position = mvideoview.getCurrentPosition();
        Log.e("videoview position2 = ","" + position);

        if (all) {
            for (onprogressvideolistener item : mlisteners) {
                item.updateprogress(position, mduration, ((position * 100) / mduration));
            }
        } else {

            mlisteners.get(1).updateprogress(position, mduration, ((position * 100) / mduration));
        }
    }

    private void updateVideoProgress(int time) {
        if (mvideoview == null) {
            return;
        }

        if (time >= mendposition) {
            mmessagehandler.removeMessages(show_progress);
            mvideoview.pause();
            mplayview.setBackgroundResource(R.drawable.ic_white_play);
            mplayview.setVisibility(View.VISIBLE);
            mresetseekbar = true;
            return;
        }

        Log.e("Player current pos ",""+time);

        if (mholdertopview != null) {
            // use long to avoid overflow

            if(time >= lasttime)
            {
                setProgressBarPosition(time);
            }
            else if(lasttime > 0)
            {
                setProgressBarPosition((int)lasttime);
            }


        }
        setTimeVideo(time);
    }

    private void setProgressBarPosition(int position) {
        if (mduration > 0) {
            long pos = 1000L * position / mduration;
              mholdertopview.setProgress((int) pos);
        }
    }

    /**
     * Set video information visibility.
     * For now this is for debugging
     *
     * @param visible whether or not the videoInformation will be visible
     */
    public void setVideoInformationVisibility(boolean visible) {
        mtimeinfocontainer.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Listener for events such as trimming operation success and cancel
     *
     * @param ontrimvideolistener interface for events
     */
    @SuppressWarnings("unused")
    public void setOnTrimVideoListener(ontrimvideolistener ontrimvideolistener) {
        montrimvideolistener = ontrimvideolistener;
    }

    /**
     * Listener for some {@link VideoView} events
     *
     * @param onhglvideolistener interface for events
     */
    @SuppressWarnings("unused")
    public void setOnHgLVideoListener(onhglvideolistener onhglvideolistener) {
        monhglvideolistener = onhglvideolistener;
    }

    /**
     * Sets the path where the trimmed video will be saved
     * Ex: /storage/emulated/0/MyAppFolder/
     *
     * @param finalPath the full path
     */
    @SuppressWarnings("unused")
    public void setDestinationPath(final String finalPath) {
        mfinalpath = finalPath;
        Log.d(tag, "Setting custom path " + mfinalpath);
    }

    /**
     * Cancel all current operations
     */
    public void destroy() {
        backgroundexecutor.cancelall("", true);
        uithreadexecutor.cancelall("");
    }

    /**
     * Set the maximum duration of the trimmed video.
     * The trimmer interface wont allow the user to set duration longer than maxDuration
     *
     * @param maxDuration the maximum duration of the trimmed video in seconds
     */
    @SuppressWarnings("unused")
    public void setMaxDuration(int maxDuration) {
       // mmaxduration = maxDuration * 1000;
        mmaxduration = maxDuration;
     }

    /**
     * Sets the uri of the video to be trimmer
     *
     * @param videoURI Uri of the video
     */
    @SuppressWarnings("unused")
    public void setVideoURI(final Uri videoURI) {
        msrc = videoURI;

        if (moriginsizefile == 0) {
            File file = new File(msrc.getPath());

            moriginsizefile = file.length();
            long fileSizeInKB = moriginsizefile / 1024;

            if (fileSizeInKB > 1000) {
                long fileSizeInMB = fileSizeInKB / 1024;
               // mtextsize.setText(String.format("%s %s", fileSizeInMB, getContext().getString(R.string.megabyte)));
            } else {
                //mtextsize.setText(String.format("%s %s", fileSizeInKB, getContext().getString(R.string.kilobyte)));
            }
           // mtextsize.setVisibility(VISIBLE);
        }

        mvideoview.setVideoURI(msrc);
        mvideoview.requestFocus();

        mtimelineview.setvideo(msrc);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.img_share_icon:
                onSaveClicked();
                break;

            case R.id.txt_cancel:
                if (montrimvideolistener != null)
                        montrimvideolistener.onclik();

                break;
        }

    }

    private static class MessageHandler extends Handler {

        @NonNull
        private final WeakReference<hglvideotrimmer> mView;

        MessageHandler(hglvideotrimmer view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            hglvideotrimmer view = mView.get();
            if (view == null || view.mvideoview == null) {
                return;
            }

            view.notifyProgressUpdate(true);

            if (view.mvideoview.isPlaying()) {
                sendEmptyMessageDelayed(0, 10);
            }
        }
    }


    private void loadffmpegbinary() {
        try {
            if (ffmpeg == null) {
                Log.d(tag, "ffmpeg : era nulo");

                ffmpeg = FFmpeg.getInstance(getContext());
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                   // showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d(tag, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //showUnsupportedExceptionDialog();
        } catch (Exception e) {
            Log.d(tag, "EXception no controlada : " + e);
        }
    }

    private void executeCutVideoCommand(int startMs, int endMs) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        final String fileName = "MP4_" + timeStamp + ".mp4";
        String filePath = getDestinationPath() + fileName;

        String filePrefix = "via_video";
        String fileExtn = ".mp4";
        String yourRealPath = file.getAbsolutePath();

        File dest = new File(filePath);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        String starttime = common.converttimeformat(startMs);
        String endtime = common.converttimeformat((endMs - startMs));

        String[] complexCommand = { "-y", "-i", yourRealPath,"-ss", "" + starttime, "-t", "" + endtime, "-c","copy", filePath};
        execFFmpegBinary(complexCommand,dest);
    }


    private void execFFmpegBinary(final String[] command, final File dest) {
        try {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                            @Override
                            public void onFailure(String s) {
                                Log.e("Failure with output : ","IN onFailure");
                            }

                            @Override
                            public void onSuccess(String s) {
                                Log.e("SUCCESS with output : ","SUCCESS");

                            }

                            @Override
                            public void onProgress(String s) {
                                Log.e( "Progress bar : " , "In Progress");

                            }

                            @Override
                            public void onStart() {
                                Log.e("Start with output : ","IN START");
                                Log.d(tag, "Started command : ffmpeg " + command);
                                //progressdialog.showwaitingdialog(getContext());
                            }

                            @Override
                            public void onFinish() {
                                //  progressDialog.dismiss();
                                //Toast.makeText(getContext(), "Video save", Toast.LENGTH_SHORT).show();

                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismisswaitdialog();
                                        if (montrimvideolistener != null)
                                            montrimvideolistener.getresult(dest.toString());
                                    }
                                });
                            }
                        });
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            // do nothing for now
        }
    }
}
