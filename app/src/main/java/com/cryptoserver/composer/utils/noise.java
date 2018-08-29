package com.cryptoserver.composer.utils;

import android.app.Activity;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by devesh on 3/5/18.
 */

public class noise extends Activity {

    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    public void start() {

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try {
                mRecorder.prepare();

            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mRecorder.start();
            mRecorder.getMaxAmplitude();
            mEMA = 0.0;
        }
    }

    public void stop() {
        try{
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null){

            double amplitude = mRecorder.getMaxAmplitude();
            Log.e("Max Amplitude = ", "" + amplitude);

            return 20 * Math.log10(amplitude / 32767.0);
        } else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}