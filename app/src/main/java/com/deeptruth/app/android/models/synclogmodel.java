package com.deeptruth.app.android.models;

/**
 * Created by root on 9/8/18.
 */

public class synclogmodel
{
    public String token="";
    public String mediakey="";
    public String localkey="";
    public String mediastarttransactionid="";
    public String sync_date="";

    public synclogmodel()
    {

    }


    public synclogmodel(String token, String mediakey, String localkey, String videostarttransactionid,
                        String sync_date)
    {
        setToken(token);
        setMediakey(mediakey);
        setLocalkey(localkey);
        setMediastarttransactionid(videostarttransactionid);
        setSync_date(sync_date);
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMediakey() {
        return mediakey;
    }

    public void setMediakey(String mediakey) {
        this.mediakey = mediakey;
    }

    public String getLocalkey() {
        return localkey;
    }

    public void setLocalkey(String localkey) {
        this.localkey = localkey;
    }

    public String getMediastarttransactionid() {
        return mediastarttransactionid;
    }

    public void setMediastarttransactionid(String videostarttransactionid) {
        this.mediastarttransactionid = videostarttransactionid;
    }

    public String getSync_date() {
        return sync_date;
    }

    public void setSync_date(String sync_date) {
        this.sync_date = sync_date;
    }
}
