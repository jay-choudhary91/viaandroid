package com.deeptruth.app.android.utils;

import android.content.Context;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by ${matraex} on 14/5/19.
 */

public class chartcustommarkerview extends MarkerView {

    private MPPointF mOffset;

    public chartcustommarkerview (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
       // tvContent.setText("" + e.getY());
        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }
}
