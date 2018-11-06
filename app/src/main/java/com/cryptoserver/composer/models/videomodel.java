package com.cryptoserver.composer.models;

/**
 * Created by root on 2/5/18.
 */

public class videomodel
{
    public String title ="";
    public String keytype ="";
    public long currentframenumber =0;
    public String keyvalue ="";
    public String videopath ="";
    public String md5 ="";
    public String frameinfo ="";
    public long frametotal =0;
    public long frameps =0;

    public videomodel(String keyvalue)
    {
        this.keyvalue = keyvalue;
    }

    public videomodel(String videopath, long frametotal, long frameps, String md5)
    {
        this.videopath = videopath;
        this.frametotal = frametotal;
        this.frameps = frameps;
        this.md5 = md5;
    }

    public videomodel(String title, String keytype, long currentframenumber, String keyvalue)
    {
        this.title =title;
        this.keytype = keytype;
        this.currentframenumber = currentframenumber;
        this.keyvalue = keyvalue;
    }


    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String getkeytype() {
        return keytype;
    }

    public void setkeytype(String keytype) {
        this.keytype = keytype;
    }

    public long getcurrentframenumber() {
        return currentframenumber;
    }

    public void setcurrentframenumber(int currentframenumber) {
        this.currentframenumber = currentframenumber;
    }

    public String getkeyvalue() {
        return keyvalue;
    }

    public void setkeyvalue(String keyvalue) {
        this.keyvalue = keyvalue;
    }

    public String getframeinfo() {
        return frameinfo;
    }

    public void setframeinfo(String frameinfo) {
        this.frameinfo = frameinfo;
    }

    public String getmd5() {
        return md5;
    }

    public void setmd5(String md5) {
        this.md5 = md5;
    }

    public String getvideopath() {
        return videopath;
    }

    public void setvideopath(String videopath) {
        this.videopath = videopath;
    }

    public long getframetotal() {
        return frametotal;
    }

    public void setframetotal(int frametotal) {
        this.frametotal = frametotal;
    }

    public long getframeps() {
        return frameps;
    }

    public void setframeps(int frameps) {
        this.frameps = frameps;
    }
}
