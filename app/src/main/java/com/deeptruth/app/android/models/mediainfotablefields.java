package com.deeptruth.app.android.models;

/**
 * Created by ${matraex} on 6/2/19.
 */

public class mediainfotablefields 
{
    String header;
    String type;
    String location;
    String localkey;
    String token;
    String mediakey;
    String sync;
    String date ; 
    String action_type;
    String apirequestdevicedate;
    String mediastartdevicedate;
    String devicetimeoffset;
    String mediacompletedevicedate;
    String mediastarttransactionid;
    String firsthash;
    String mediaid;
    String status;
    String remainingframes;
    String lastframe;
    String framecount;
    String syncstatus;
    String medianame;
    String medianotes;
    String mediafolder;
    String mediafilepath;
    String thumbnailurl;
    String mediaduration;
    String color;
    String latency;
    String mediacompletedate;
    String colorreason;


    public mediainfotablefields()
    {

    }
    
    public mediainfotablefields(String header,String type,String location,String localkey,
        String token,String mediakey,String sync,String date , String action_type,
        String apirequestdevicedate,String mediastartdevicedate,String devicetimeoffset,
        String mediacompletedevicedate,String mediastarttransactionid,String firsthash,String mediaid,
        String status,String remainingframes,String lastframe,String framecount,String syncstatus,String medianame,
        String medianotes,String mediafolder,String mediafilepath,String thumbnailurl,String mediaduration,String mediacompletedate
        ,String color,String latency,String colorreason)
    {
        setHeader(header);
        setType(type);
        setLocation(location);
        setLocalkey(localkey);
        setToken(token);
        setMediakey(mediakey);
        setSync(sync);
        setDate(date);
        setAction_type(action_type);
        setApirequestdevicedate(apirequestdevicedate);
        setMediastartdevicedate(mediastartdevicedate);
        setDevicetimeoffset(devicetimeoffset);
        setMediacompletedevicedate(mediacompletedevicedate);
        setMediastarttransactionid(mediastarttransactionid);
        setFirsthash(firsthash);
        setMediaid(mediaid);
        setStatus(status);
        setRemainingframes(remainingframes);
        setLastframe(lastframe);
        setFramecount(framecount);
        setSyncstatus(syncstatus);
        setMedianame(medianame);
        setMedianotes(medianotes);
        setMediafolder(mediafolder);
        setMediafilepath(mediafilepath);
        setThumbnailurl(thumbnailurl);
        setMediaduration(mediaduration);
        setMediacompletedate(mediacompletedate);
        setColor(color);
        setLatency(latency);
        setColorreason(colorreason);
    }

    public String getMediacompletedate() {
        return mediacompletedate;
    }

    public void setMediacompletedate(String mediacompletedate) {
        this.mediacompletedate = mediacompletedate;
    }

    public String getSyncstatus() {
        return syncstatus;
    }

    public void setSyncstatus(String syncstatus) {
        this.syncstatus = syncstatus;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    public String getMediaduration() {
        return mediaduration;
    }

    public void setMediaduration(String mediaduration) {
        this.mediaduration = mediaduration;
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

    public String getMediakey() {
        return mediakey;
    }

    public void setMediakey(String mediakey) {
        this.mediakey = mediakey;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public String getApirequestdevicedate() {
        return apirequestdevicedate;
    }

    public void setApirequestdevicedate(String apirequestdevicedate) {
        this.apirequestdevicedate = apirequestdevicedate;
    }

    public String getMediastartdevicedate() {
        return mediastartdevicedate;
    }

    public void setMediastartdevicedate(String mediastartdevicedate) {
        this.mediastartdevicedate = mediastartdevicedate;
    }

    public String getDevicetimeoffset() {
        return devicetimeoffset;
    }

    public void setDevicetimeoffset(String devicetimeoffset) {
        this.devicetimeoffset = devicetimeoffset;
    }

    public String getMediacompletedevicedate() {
        return mediacompletedevicedate;
    }

    public void setMediacompletedevicedate(String mediacompletedevicedate) {
        this.mediacompletedevicedate = mediacompletedevicedate;
    }

    public String getMediastarttransactionid() {
        return mediastarttransactionid;
    }

    public void setMediastarttransactionid(String mediastarttransactionid) {
        this.mediastarttransactionid = mediastarttransactionid;
    }

    public String getFirsthash() {
        return firsthash;
    }

    public void setFirsthash(String firsthash) {
        this.firsthash = firsthash;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemainingframes() {
        return remainingframes;
    }

    public void setRemainingframes(String remainingframes) {
        this.remainingframes = remainingframes;
    }

    public String getLastframe() {
        return lastframe;
    }

    public void setLastframe(String lastframe) {
        this.lastframe = lastframe;
    }

    public String getFramecount() {
        return framecount;
    }

    public void setFramecount(String framecount) {
        this.framecount = framecount;
    }


    public String getMedianame() {
        return medianame;
    }

    public void setMedianame(String medianame) {
        this.medianame = medianame;
    }

    public String getMedianotes() {
        return medianotes;
    }

    public void setMedianotes(String medianotes) {
        this.medianotes = medianotes;
    }

    public String getMediafolder() {
        return mediafolder;
    }

    public void setMediafolder(String mediafolder) {
        this.mediafolder = mediafolder;
    }

    public String getColorreason() {
        return colorreason;
    }

    public void setColorreason(String colorreason) {
        this.colorreason = colorreason;
    }

}
