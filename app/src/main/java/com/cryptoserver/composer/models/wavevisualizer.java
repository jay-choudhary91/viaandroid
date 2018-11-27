package com.cryptoserver.composer.models;

import android.graphics.Bitmap;

/**
 * Created by root on 9/8/18.
 */

public class wavevisualizer
{
    public int visulizervalue=0;
    boolean isinsert;

    public wavevisualizer(int visulizervalue,  boolean isinsert)
    {
        setVisulizervalue(visulizervalue);
        setIsinsert(isinsert);
    }

    public int getVisulizervalue() {
        return visulizervalue;
    }

    public void setVisulizervalue(int visulizervalue) {
        this.visulizervalue = visulizervalue;
    }

    public boolean isIsinsert() {
        return isinsert;
    }

    public void setIsinsert(boolean isinsert) {
        this.isinsert = isinsert;
    }
}
