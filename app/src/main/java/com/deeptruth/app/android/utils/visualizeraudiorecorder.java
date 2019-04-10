package com.deeptruth.app.android.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.deeptruth.app.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devesh on 3/10/18.
 */

public class visualizeraudiorecorder extends View {

    private static final int LINE_WIDTH = 10; // width of visualizer lines
    private float LINE_SCALE = 8; // scales visualizer lines
    private List<Float> amplitudes; // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePaint; // specifies line drawing characteristics
    private Context context;
    private boolean iscolorchange=false;

    // constructor
    public visualizeraudiorecorder(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        this.context=context;
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(getResources().getColor(R.color.wave_color)); // set color to green
        LINE_SCALE=common.convertDpToPixel(8,context);
        linePaint.setAlpha(255);
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
        //linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setPathEffect(new DashPathEffect(new float[]{LINE_WIDTH,5},5));
        // If we don't render in software mode, the dotted line becomes a solid line.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View

        LINE_SCALE=common.convertDpToPixel(8,context);
        Log.e("LINE_SCALEheight",""+LINE_SCALE);
        amplitudes = new ArrayList<Float>(width / LINE_WIDTH);
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        if(amplitudes != null)
            amplitudes.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * LINE_WIDTH >= width) {
            amplitudes.remove(0); // remove oldest power value
        }
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        float curX = 0; // start curX at zero
        // for each item in the amplitudes ArrayList
        for (float power : amplitudes) {
            float scaledHeight = power / LINE_SCALE; // scale the power
            curX += LINE_WIDTH+2; // increase X by LINE_WIDTH

            if(iscolorchange)
            {
                Shader shader = new LinearGradient(curX, middle + scaledHeight / 1, curX, middle
                        - scaledHeight / 1, new int[]{
                        ContextCompat.getColor(context, R.color.visualizer_primary),
                        ContextCompat.getColor(context, R.color.visualizer_primary),
                        ContextCompat.getColor(context, R.color.visualizer_primary),
                        ContextCompat.getColor(context, R.color.visualizer_primary),
                        ContextCompat.getColor(context, R.color.visualizer_primary)},new float[]{0.2f,0.4f,0.5f,0.6f,0.8f}, Shader.TileMode.MIRROR /*or REPEAT*/);
                linePaint.setShader(shader);
                iscolorchange=false;
            }
            else
            {
                Shader shader = new LinearGradient(curX, middle + scaledHeight / 1, curX, middle
                        - scaledHeight / 1, new int[]{
                        ContextCompat.getColor(context, R.color.visualizer_secondary),
                        ContextCompat.getColor(context, R.color.visualizer_secondary),
                        ContextCompat.getColor(context, R.color.visualizer_secondary),
                        ContextCompat.getColor(context, R.color.visualizer_secondary),
                        ContextCompat.getColor(context, R.color.visualizer_secondary)},new float[]{0.2f,0.4f,0.5f,0.6f,0.8f}, Shader.TileMode.MIRROR /*or REPEAT*/);
                linePaint.setShader(shader);
                iscolorchange=true;
            }

            //linePaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 5));
            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle + scaledHeight / 1, curX, middle- scaledHeight / 1, linePaint);

        }
    }
}
