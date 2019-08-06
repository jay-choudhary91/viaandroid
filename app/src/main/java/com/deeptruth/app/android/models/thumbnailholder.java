package com.deeptruth.app.android.models;

/**
 * Created by root on 9/8/18.
 */

public class thumbnailholder
{
    public String mediafilepath="";
    public String thumbnailurl="";

    public thumbnailholder()
    {

    }

    public thumbnailholder(String mediafilepath, String thumbnailurl)
    {
        setMediafilepath(mediafilepath);
        setThumbnailurl(thumbnailurl);
    }

    public String getMediafilepath() {
        return mediafilepath;
    }

    public void setMediafilepath(String mediafilepath) {
        this.mediafilepath = mediafilepath;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }
}
