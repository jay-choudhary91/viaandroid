package com.cryptoserver.composer.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by devesh on 1/10/18.
 */

public class WaveFromView extends View {

    private static final float defaultFrequency          = 1.5f;
    private static final float defaultAmplitude          = 1.0f;
    private static final float defaultIdleAmplitude      = 0.01f;
    private static final float defaultNumberOfWaves      = 5.0f;
    private static final float defaultPhaseShift         = -0.15f;
    private static final float defaultDensity            = 5.0f;
    private static final float defaultPrimaryLineWidth   = 3.0f;
    private static final float defaultSecondaryLineWidth = 1.0f;

    private float phase;
    private float amplitude;
    private float frequency;
    private float idleAmplitude;
    private float numberOfWaves;
    private float phaseShift;
    private float density;
    private float primaryWaveLineWidth;
    private float secondaryWaveLineWidth;
    Paint mPaintColor;
    Rect rect;
    boolean isStraightLine = false;

    public WaveFromView(Context context) {
        super(context);
        setUp();
    }

    public WaveFromView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    public WaveFromView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WaveFromView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp();
    }

    private void setUp() {
        this.frequency = defaultFrequency;

        this.amplitude = defaultAmplitude;
        this.idleAmplitude = defaultIdleAmplitude;

        this.numberOfWaves = 1;
        this.phaseShift = defaultPhaseShift;
        this.density = defaultDensity;

        this.primaryWaveLineWidth = defaultPrimaryLineWidth;
        this.secondaryWaveLineWidth = defaultSecondaryLineWidth;
        mPaintColor = new Paint();
        mPaintColor.setColor(Color.WHITE);
    }

    public void updateAmplitude(float ampli, boolean isSpeaking) {
        this.amplitude = Math.max(ampli, idleAmplitude);


        isStraightLine = isSpeaking;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        rect = new Rect(0,0,canvas.getWidth(),canvas.getWidth());
        canvas.drawColor(Color.BLUE);
          /*canvas.drawRect(rect, mPaintColor);*/
        if(isStraightLine) {
            for (int i = 0; i < numberOfWaves; i++) {
                mPaintColor.setStrokeWidth(i == 0 ? primaryWaveLineWidth : secondaryWaveLineWidth);
                float halfHeight = canvas.getHeight() / 2;
                float width = canvas.getWidth();
                float mid = canvas.getWidth() / 2;

                float maxAmplitude = halfHeight - 4.0f;
                float progress = 1.0f - (float) i / this.numberOfWaves;
                float normedAmplitude = (1.5f * progress - 0.5f) * this.amplitude;

                Path path = new Path();

                float multiplier = Math.min(1.0f, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

                for (float x = 0; x < width + density; x += density) {
                    // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                    float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);

                    float y = (float) (scaling * maxAmplitude * normedAmplitude * Math.sin(2 * Math.PI * (x / width) * frequency + phase) + halfHeight);

                   // Log.e("waveheight = ",""+y);

                    if (x == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
                mPaintColor.setStyle(Paint.Style.STROKE);
                mPaintColor.setAntiAlias(true);
                canvas.drawPath(path, mPaintColor);

            }
        } else {
            canvas.drawLine(5,canvas.getHeight()/2,canvas.getWidth(),canvas.getHeight()/2,mPaintColor );
            canvas.drawLine(0,canvas.getHeight()/2,canvas.getWidth(),canvas.getHeight()/2,mPaintColor);
            canvas.drawLine(-5, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight()/2,mPaintColor );
        }
        this.phase += phaseShift;
        invalidate();
    }
}