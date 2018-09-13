package com.cryptoserver.composer.models;

import java.util.ArrayList;

/**
 * Created by root on 2/5/18.
 */

public class frameinfo
{
    public String title ="";
    public String framenumber ="";
    public String meta ="";
    public String hashvalue ="";
    public String hashmethod ="";
    public boolean isuploaded=false;
    public ArrayList<metricmodel> metriceslist=new ArrayList<metricmodel>();


    public frameinfo(String framenumber, String meta, String hashvalue, String hashmethod,boolean isuploaded,ArrayList<metricmodel> metriceslist)
    {
        setFramenumber(framenumber);
        setMeta(meta);
        setHashvalue(hashvalue);
        setHashmethod(hashmethod);
        setIsuploaded(isuploaded);
        setMetriceslist(metriceslist);
    }

    public ArrayList<metricmodel> getMetriceslist() {
        return metriceslist;
    }

    public void setMetriceslist(ArrayList<metricmodel> metriceslist) {
        this.metriceslist = metriceslist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFramenumber() {
        return framenumber;
    }

    public void setFramenumber(String framenumber) {
        this.framenumber = framenumber;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getHashvalue() {
        return hashvalue;
    }

    public void setHashvalue(String hashvalue) {
        this.hashvalue = hashvalue;
    }

    public String getHashmethod() {
        return hashmethod;
    }

    public void setHashmethod(String hashmethod) {
        this.hashmethod = hashmethod;
    }

    public boolean isIsuploaded() {
        return isuploaded;
    }

    public void setIsuploaded(boolean isuploaded) {
        this.isuploaded = isuploaded;
    }
}
