package com.cryptoserver.composer.models;

/**
 * Created by root on 9/8/18.
 */

public class video
{
    public String path="";
    public String name="";
    public String createdate="";
    public String duration="";
    public String md5="";

    public video(String path,String name,String createdate,String duration,String md5)
    {
        setPath(path);
        setName(name);
        setCreatedate(createdate);
        setDuration(duration);
        setMd5(md5);
    }

    public  video()
    {

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
}
