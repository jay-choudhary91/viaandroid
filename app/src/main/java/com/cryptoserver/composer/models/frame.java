package com.cryptoserver.composer.models;

import android.graphics.Bitmap;

/**
 * Created by root on 9/8/18.
 */

public class frame
{
    public int second=0;
    public Bitmap bitmap=null;
    public boolean isheaderfooter=false;

    public frame(int second, Bitmap bitmap,boolean isheaderfooter)
    {
        setSecond(second);
        setBitmap(bitmap);
        setIsheaderfooter(isheaderfooter);
    }

    public boolean isIsheaderfooter() {
        return isheaderfooter;
    }

    public void setIsheaderfooter(boolean isheaderfooter) {
        this.isheaderfooter = isheaderfooter;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
