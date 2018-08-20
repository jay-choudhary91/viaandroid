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
package com.cryptoserver.composer.videoTrimmer;

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

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.videoTrimmer.interfaces.onhglvideolistener;
import com.cryptoserver.composer.videoTrimmer.interfaces.onprogressvideolistener;
import com.cryptoserver.composer.videoTrimmer.interfaces.onrangeseekbarlistener;
import com.cryptoserver.composer.videoTrimmer.interfaces.ontrimvideolistener;
import com.cryptoserver.composer.videoTrimmer.utils.backgroundexecutor;
import com.cryptoserver.composer.videoTrimmer.utils.uithreadexecutor;
import com.cryptoserver.composer.videoTrimmer.view.progressbarview;
import com.cryptoserver.composer.videoTrimmer.view.rangeseekbarview;
import com.cryptoserver.composer.videoTrimmer.view.thumb;
import com.cryptoserver.composer.videoTrimmer.view.timelineview;
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



public class hglvideotrimmer extends FrameLayout {

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
    private timelineview mtimelineview;

    private progressbarview mvideoprogressindicator;
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
        mrangeseekbarview = ((rangeseekbarview) findViewById(R.id.timeLineBar));
        mlinearvideo = ((RelativeLayout) findViewById(R.id.layout_surface_view));
        mvideoview = ((VideoView) findViewById(R.id.video_loader));
        mplayview = ((ImageView) findViewById(R.id.icon_video_play));
        mtimeinfocontainer = findViewById(R.id.timeText);
        mtextsize = ((TextView) findViewById(R.id.textSize));
        mtexttimeframe = ((TextView) findViewById(R.id.textTimeSelection));
        mtexttime = ((TextView) findViewById(R.id.textTime));
        mtimelineview = ((timelineview) findViewById(R.id.timeLineView));

        setuplisteners();
        setupmargins();

        loadffmpegbinary();
    }

    private void setuplisteners() {
        mlisteners = new ArrayList<>();
        mlisteners.add(new onprogressvideolistener() {
            @Override
            public void updateprogress(int time, int max, float scale) {
                updateVideoProgress(time);
            }
        });
        mlisteners.add(mvideoprogressindicator);

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
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        mrangeseekbarview.addonrangeseekbarlistener(mvideoprogressindicator);

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
        lp.setMargins(marge - widthSeek, 0, marge - widthSeek, 0);
        mholdertopview.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mtimelineview.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mtimelineview.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mvideoprogressindicator.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mvideoprogressindicator.setLayoutParams(lp);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    public void onSaveClicked() {
        if (mstartposition <= 0 && mendposition >= mduration) {
            if (montrimvideolistener != null)
                montrimvideolistener.getresult(msrc);
        } else {
            mplayview.setVisibility(View.VISIBLE);
            mvideoview.pause();

           /* int diff = (int) mendposition - mstartposition;

            if(diff < 3000){

                Toast.makeText(getContext(), "Video should be of minimum 3 seconds",
                        Toast.LENGTH_LONG).show();


            }else {*/
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
            }
       // }
    }

    private void onClickVideoPlayPause() {
        if (mvideoview.isPlaying()) {
            mplayview.setVisibility(View.VISIBLE);
            mmessagehandler.removeMessages(show_progress);
            mvideoview.pause();
        } else {
            mplayview.setVisibility(View.GONE);

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
            File folder = Environment.getExternalStorageDirectory();
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

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        mvideoview.setLayoutParams(lp);

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

        } else {
            mstartposition = 0;
            mendposition = mduration;
        }

        setProgressBarPosition(mstartposition);
        mvideoview.seekTo(mstartposition);

        mtimevideo = mduration;
        mrangeseekbarview.initmaxwidth();
    }

    private void setTimeFrames() {
        String seconds = getContext().getString(R.string.short_seconds);
        mtexttimeframe.setText(String.format("%s %s - %s %s", stringfortime(mstartposition), seconds, stringfortime(mendposition), seconds));
    }

    private void setTimeVideo(int position) {
        String seconds = getContext().getString(R.string.short_seconds);
        mtexttime.setText(String.format("%s %s", stringfortime(position), seconds));
    }

    public String stringfortime(int timeMs) {
        int totalseconds = timeMs / 1000;

        int seconds = totalseconds % 60;
        int minutes = (totalseconds / 60) % 60;
        int hours = totalseconds / 3600;

        Formatter mformatter = new Formatter();
        if (hours > 0) {
            return mformatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mformatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void onSeekThumbs(int index, float value) {
        switch (index) {
            case thumb.left: {
                mstartposition = (int) ((mduration * value) / 100L);
                mvideoview.seekTo(mstartposition);
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
        mplayview.setVisibility(View.VISIBLE);
    }

    private void onVideoCompleted() {
        mvideoview.seekTo(mstartposition);
    }

    private void notifyProgressUpdate(boolean all) {
        if (mduration == 0) return;

        int position = mvideoview.getCurrentPosition();
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
            mplayview.setVisibility(View.VISIBLE);
            mresetseekbar = true;
            return;
        }

        if (mholdertopview != null) {
            // use long to avoid overflow
            setProgressBarPosition(time);
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
                mtextsize.setText(String.format("%s %s", fileSizeInMB, getContext().getString(R.string.megabyte)));
            } else {
                mtextsize.setText(String.format("%s %s", fileSizeInKB, getContext().getString(R.string.kilobyte)));
            }
        }

        mvideoview.setVideoURI(msrc);
        mvideoview.requestFocus();

        mtimelineview.setvideo(msrc);
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

        String filePrefix = "cut_video";
        String fileExtn = ".mp4";
        String yourRealPath = file.getAbsolutePath();

        File dest = new File(filePath);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(tag, "starttrim: src: " + yourRealPath);
        Log.d(tag, "starttrim: dest: " + dest.getAbsolutePath());
        Log.d(tag, "starttrim: startMs: " + startMs);
        Log.d(tag, "starttrim: endMs: " + endMs);
        filePath = dest.getAbsolutePath();
        //String[] complexCommand = {"-i", yourRealPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, dest.getAbsolutePath()};
        //String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
        //String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000, filePath};

        String[] complexCommand = { "-y", "-i", yourRealPath,"-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, "-c","copy", filePath};

        execFFmpegBinary(complexCommand,dest);
    }

    /*private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder()
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create()
                .show();

    }*/

    private void execFFmpegBinary(final String[] command, final File dest) {
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
                    progressdialog.dismisswaitdialog();
                        if (montrimvideolistener != null)
                            montrimvideolistener.getresult(Uri.parse(dest.toString()));
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }
}