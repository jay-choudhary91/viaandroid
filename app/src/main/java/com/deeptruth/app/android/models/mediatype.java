package com.deeptruth.app.android.models;

/**
 * Created by root on 9/8/18.
 */

public class mediatype
{
    public String medianame="";
    public String mediacount="";
    public boolean ismediaselected=false;

    public mediatype(String medianame, String mediacount)
    {
        setMedianame(medianame);
        setMediacount(mediacount);
    }

    public boolean isIsmediaselected() {
        return ismediaselected;
    }

    public void setIsmediaselected(boolean ismediaselected) {
        this.ismediaselected = ismediaselected;
    }

    public String getMedianame() {
        return medianame;
    }

    public void setMedianame(String medianame) {
        this.medianame = medianame;
    }

    public String getMediacount() {
        return mediacount;
    }

    public void setMediacount(String mediacount) {
        this.mediacount = mediacount;
    }
}
