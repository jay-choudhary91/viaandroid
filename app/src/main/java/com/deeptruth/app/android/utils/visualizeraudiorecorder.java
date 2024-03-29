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

    private static final int LINE_WIDTH = 8; // width of visualizer lines
    private float LINE_SCALE = 8; // scales visualizer lines
    private List<Float> amplitudes; // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePainttop,linePaintbottom; // specifies line drawing characteristics
    private Context context;
    private boolean iscolorchange=false;

    // constructor
    public visualizeraudiorecorder(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        this.context=context;
        linePainttop = new Paint(); // create Paint for lines
        linePaintbottom = new Paint(); // create Paint for lines
        LINE_SCALE=common.convertDpToPixel(8,context);


        linePainttop.setColor(getResources().getColor(R.color.wave_color)); // set color to green
        linePainttop.setAlpha(255);
        linePainttop.setStrokeWidth(LINE_WIDTH); // set stroke width
        //linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePainttop.setPathEffect(new DashPathEffect(new float[]{LINE_WIDTH,2},2));

        linePaintbottom.setColor(getResources().getColor(R.color.wave_color)); // set color to green
        linePaintbottom.setAlpha(255);
        linePaintbottom.setStrokeWidth(LINE_WIDTH); // set stroke width
        //linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaintbottom.setPathEffect(new DashPathEffect(new float[]{LINE_WIDTH,2},2));
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

        LINE_SCALE=common.convertDpToPixel(4,context);
        amplitudes = new ArrayList<Float>(width / LINE_WIDTH);

    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        if(amplitudes != null)
            amplitudes.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {

        if(amplitudes == null)
            amplitudes = new ArrayList<Float>(width / LINE_WIDTH);

        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * 10 >= width) {
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

                Shader shader = new LinearGradient(curX, (middle+10) + scaledHeight / 6, curX, (middle+10), new int[]{
                        ContextCompat.getColor(context, R.color.visualizer_forthcolor),
                        ContextCompat.getColor(context, R.color.visualizer_thiredcolor),
                        ContextCompat.getColor(context, R.color.visualizer_secondcolor),
                        ContextCompat.getColor(context, R.color.visualizer_firstcolor)
                       },new float[]{0.2f,0.3f,0.5f,0.7f}, Shader.TileMode.MIRROR /*or REPEAT*/);
            linePaintbottom.setShader(shader);

                Shader shader1 = new LinearGradient(curX, middle-10 , curX, (middle-10)- scaledHeight / 6, new int[]{
                        ContextCompat.getColor(context, R.color.visualizer_firstcolor),
                        ContextCompat.getColor(context, R.color.visualizer_secondcolor),
                        ContextCompat.getColor(context, R.color.visualizer_thiredcolor),
                        ContextCompat.getColor(context, R.color.visualizer_forthcolor)},new float[]{0.2f,0.3f,0.5f,0.7f}, Shader.TileMode.MIRROR /*or REPEAT*/);
            linePainttop.setShader(shader1);

            //linePaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 5));
            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle-10, curX, (middle-10)- scaledHeight / 6, linePaintbottom);
            canvas.drawLine(curX, (middle+10) + scaledHeight / 6, curX, middle+10, linePainttop);

        }
    }
}
