package com.deeptruth.app.android.models;

/**
 * Created by root on 11/10/18.
 */

public class startmediainfo
{
    String id = "";
    String header = "";
    String type = "";
    String location = "";
    String localkey = "";
    String token = "";
    String videokey = "";
    String sync = "";
    String action_type="";
    String sync_date="";
    String apirequestdevicedate="";
    String videostartdevicedate="";
    String devicetimeoffset="";
    String videocompletedevicedate="";
    String mediafilepath="";


    public startmediainfo(String id, String header, String type , String location , String localkey , String token, String videokey , String sync,
                          String action_type, String sync_date, String apirequestdevicedate, String videostartdevicedate,
                          String devicetimeoffset, String videocompletedevicedate,String mediafilepath )
    {

        setId(id);
        setHeader(header);
        setType(type);
        setLocation(location);
        setLocalkey(localkey);
        setToken(token);
        setVideokey(videokey);
        setSync(sync);
        setAction_type(action_type);
        setSync_date(sync_date);
        setApirequestdevicedate(apirequestdevicedate);
        setVideostartdevicedate(videostartdevicedate);
        setDevicetimeoffset(devicetimeoffset);
        setVideocompletedevicedate(videocompletedevicedate);
        setMediafilepath(mediafilepath);
    }

    public String getMediafilepath() {
        return mediafilepath;
    }

    public void setMediafilepath(String mediafilepath) {
        this.mediafilepath = mediafilepath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSync_date() {
        return sync_date;
    }

    public void setSync_date(String sync_date) {
        this.sync_date = sync_date;
    }

    public String getApirequestdevicedate() {
        return apirequestdevicedate;
    }

    public void setApirequestdevicedate(String apirequestdevicedate) {
        this.apirequestdevicedate = apirequestdevicedate;
    }

    public String getVideostartdevicedate() {
        return videostartdevicedate;
    }

    public void setVideostartdevicedate(String videostartdevicedate) {
        this.videostartdevicedate = videostartdevicedate;
    }

    public String getDevicetimeoffset() {
        return devicetimeoffset;
    }

    public void setDevicetimeoffset(String devicetimeoffset) {
        this.devicetimeoffset = devicetimeoffset;
    }

    public String getVideocompletedevicedate() {
        return videocompletedevicedate;
    }

    public void setVideocompletedevicedate(String videocompletedevicedate) {
        this.videocompletedevicedate = videocompletedevicedate;
    }
}
