package com.deeptruth.app.android.models;

/**
 * Created by root on 11/10/18.
 */

public class videogroup
{
    String id="";
    String videoid="";
    String sync="";
    String videokey="";
    String actiontype="";

    public videogroup(String id,String videoid,String sync,String videokey,String actiontype)
    {
        setId(id);
        setVideoid(videoid);
        setVideokey(sync);
        setSync(videokey);
        setActiontype(actiontype);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getVideokey() {
        return videokey;
    }

    public void setVideokey(String videokey) {
        this.videokey = videokey;
    }

    public String getActiontype() {
        return actiontype;
    }

    public void setActiontype(String actiontype) {
        this.actiontype = actiontype;
    }
}
