package com.deeptruth.app.android.models;

import android.graphics.Bitmap;

/**
 * Created by root on 9/8/18.
 */

public class uploaddataparams
{
    public String filepath="";
    public String mediatoken="";
    public String mediatype="";
    public String mediastoredurl="";
    public String mediashareurl="";
    public String sharemethod="";
    public String sharekey="";
    public String shareid="";
    public String fileextension="";

    public uploaddataparams()
    {

    }

    public String getShareid() {
        return shareid;
    }

    public void setShareid(String shareid) {
        this.shareid = shareid;
    }

    public String getSharekey() {
        return sharekey;
    }

    public void setSharekey(String sharekey) {
        this.sharekey = sharekey;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filename) {
        this.filepath = filename;
    }

    public String getMediatoken() {
        return mediatoken;
    }

    public void setMediatoken(String mediatoken) {
        this.mediatoken = mediatoken;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getMediastoredurl() {
        return mediastoredurl;
    }

    public void setMediastoredurl(String mediauploadurl) {
        this.mediastoredurl = mediauploadurl;
    }

    public String getMediashareurl() {
        return mediashareurl;
    }

    public void setMediashareurl(String mediashareurl) {
        this.mediashareurl = mediashareurl;
    }

    public String getSharemethod() {
        return sharemethod;
    }

    public void setSharemethod(String sharemethod) {
        this.sharemethod = sharemethod;
    }

    public String getFileextension() {
        return fileextension;
    }

    public void setFileextension(String fileextension) {
        this.fileextension = fileextension;
    }
}
