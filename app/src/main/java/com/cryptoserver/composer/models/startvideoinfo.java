package com.cryptoserver.composer.models;

/**
 * Created by root on 11/10/18.
 */

public class startvideoinfo
{
    String header = "";
    String type = "";
    String location = "";
    String localkey = "";
    String token = "";
    String videokey = "";
    String sync = "";
    String action_type="";
    String sync_date="";

    public String getSync_date() {
        return sync_date;
    }

    public void setSync_date(String sync_date) {
        this.sync_date = sync_date;
    }

    public startvideoinfo(String header, String type , String location , String localkey , String token, String videokey , String sync, String action_type,String sync_date
    )
    {
        setHeader(header);
        setType(type);
        setLocation(location);
        setLocalkey(localkey);
        setToken(token);
        setVideokey(videokey);
        setSync(sync);
        setAction_type(action_type);
        setSync_date(sync_date);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocalkey() {
        return localkey;
    }

    public void setLocalkey(String localkey) {
        this.localkey = localkey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVideokey() {
        return videokey;
    }

    public void setVideokey(String videokey) {
        this.videokey = videokey;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }
}
