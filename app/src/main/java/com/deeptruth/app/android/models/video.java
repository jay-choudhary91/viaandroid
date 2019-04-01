package com.deeptruth.app.android.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by root on 9/8/18.
 */

public class video
{
    public int id=0;
    public String path="";
    public String name="";
    public String createdate="";
    public String createtime="";
    public String duration="";
    public String md5="";
    public String mimetype="";
    public String extension="";
    public String localkey="";
    public long lastmodifiedtime=0;
    public Bitmap thumbnail=null;
    public boolean isSelected = false;
    public String mediastatus = "";
    public String videostarttransactionid = "";
    public String thumbnailpath = "";
    public String mediatitle = "";
    public String medianotes = "";
    public String mediacolor = "";
    public boolean ischeck = false;
    public boolean doenable = false;
    public int griditemheight = 0;
    public ArrayList<String> mediabarcolor=new ArrayList<>();



    public video(String path, String name, String createdate, String duration, String md5, long lastmodifiedtime)
    {
        setPath(path);
        setName(name);
        setCreatedate(createdate);
        setDuration(duration);
        setMd5(md5);
        setLastmodifiedtime(lastmodifiedtime);
    }

    public  video()
    {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMediacolor() {
        return mediacolor;
    }

    public void setMediacolor(String mediacolor) {
        this.mediacolor = mediacolor;
    }

    public String getMediatitle() {
        return mediatitle;
    }

    public void setMediatitle(String mediatitle) {
        this.mediatitle = mediatitle;
    }

    public String getMedianotes() {
        return medianotes;
    }

    public void setMedianotes(String medianotes) {
        this.medianotes = medianotes;
    }

    public String getThumbnailpath() {
        return thumbnailpath;
    }

    public void setThumbnailpath(String thumbnailpath) {
        this.thumbnailpath = thumbnailpath;
    }

    public int getGriditemheight() {
        return griditemheight;
    }

    public void setGriditemheight(int griditemheight) {
        this.griditemheight = griditemheight;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getLastmodifiedtime() {
        return lastmodifiedtime;
    }

    public void setLastmodifiedtime(long lastmodifiedtime) {
        this.lastmodifiedtime = lastmodifiedtime;
    }

    public String getmimetype() {
        return mimetype;
    }

    public void setmimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getLocalkey() {
        return localkey;
    }

    public void setLocalkey(String localkey) {
        this.localkey = localkey;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMediastatus() {
        return mediastatus;
    }

    public void setMediastatus(String mediastatus) {
        this.mediastatus = mediastatus;
    }

    public String getVideostarttransactionid() {
        return videostarttransactionid;
    }

    public void setVideostarttransactionid(String videostarttransactionid) {
        this.videostarttransactionid = videostarttransactionid;
    }

    public boolean isDoenable() {
        return doenable;
    }

    public void setDoenable(boolean doenable) {
        this.doenable = doenable;
    }

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getCreatetime() {
        return createtime;
    }
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public ArrayList<String> getMediabarcolor() {
        return mediabarcolor;
    }

    public void setMediabarcolor(ArrayList<String> mediabarcolor) {
        this.mediabarcolor = mediabarcolor;
    }
}
