package com.deeptruth.app.android.models;

import android.graphics.Bitmap;

/**
 * Created by root on 9/8/18.
 */

public class sharemedia
{
    public int mediaicon=0;
    public String medianame="";


    public sharemedia(int mediaicon, String medianame)
    {
        setMediaicon(mediaicon);
        setMedianame(medianame);
    }

    public int getMediaicon() {
        return mediaicon;
    }

    public void setMediaicon(int mediaicon) {
        this.mediaicon = mediaicon;
    }

    public String getMedianame() {
        return medianame;
    }

    public void setMedianame(String medianame) {
        this.medianame = medianame;
    }
}
