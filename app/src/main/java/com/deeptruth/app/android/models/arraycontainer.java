package com.deeptruth.app.android.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by devesh on 20/4/18.
 */

public class arraycontainer implements Serializable {

    ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    boolean isupdated=false;
    String hashmethod = "";
    String videostarttransactionid = "" ;
    String valuehash = "";
    String metahash ="";
    String color ="";
    String latency ="";
    String sequenceno ="";

    String colorreason ="";



    public arraycontainer(ArrayList<metricmodel> metricItemArraylist, String hashmethod,String videostarttransactionid,
                          String valuehash,String metahash,String color,String latency,String sequenceno,String colorreason)
    {
        setMetricItemArraylist(metricItemArraylist);
        setHashmethod(hashmethod);
        setVideostarttransactionid(videostarttransactionid);
        setValuehash(valuehash);
        setMetahash(metahash);
        setColor(color);
        setLatency(latency);
        setSequenceno(sequenceno);
        setColorreason(colorreason);
    }

    public arraycontainer(ArrayList<metricmodel> metricItemArraylist, String hashmethod,String videostarttransactionid,
                          String valuehash,String metahash,String color,String colorreason)
    {
        setMetricItemArraylist(metricItemArraylist);
        setHashmethod(hashmethod);
        setVideostarttransactionid(videostarttransactionid);
        setValuehash(valuehash);
        setMetahash(metahash);
        setColor(color);
        setColorreason(colorreason);
    }

    public arraycontainer()
    {
    }

    public arraycontainer( String hashmethod,String videostarttransactionid,
                          String valuehash,String metahash,String color,String latency,String colorreason)
    {
        setMetricItemArraylist(metricItemArraylist);
        setHashmethod(hashmethod);
        setVideostarttransactionid(videostarttransactionid);
        setValuehash(valuehash);
        setMetahash(metahash);
        setColor(color);
        setLatency(latency);
        setColorreason(colorreason);
    }

    public arraycontainer( String hashmethod,String videostarttransactionid,
                           String valuehash,String metahash,String color,String colorreason)
    {
        setMetricItemArraylist(metricItemArraylist);
        setHashmethod(hashmethod);
        setVideostarttransactionid(videostarttransactionid);
        setValuehash(valuehash);
        setMetahash(metahash);
        setColor(color);
        setColorreason(colorreason);
    }

    public ArrayList<metricmodel> getMetricItemArraylist() {
        return metricItemArraylist;
    }

    public void setMetricItemArraylist(ArrayList<metricmodel> metricItemArraylist) {
        this.metricItemArraylist = metricItemArraylist;

    }

    public String getSequenceno() {
        return sequenceno;
    }

    public void setSequenceno(String sequenceno) {
        this.sequenceno = sequenceno;
    }

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isIsupdated() {
        return isupdated;
    }

    public void setIsupdated(boolean isupdated) {
        this.isupdated = isupdated;
    }

    public String getHashmethod() {
        return hashmethod;
    }

    public void setHashmethod(String hashmethod) {
        this.hashmethod = hashmethod;
    }

    public String getVideostarttransactionid() {
        return videostarttransactionid;
    }

    public void setVideostarttransactionid(String videostarttransactionid) {
        this.videostarttransactionid = videostarttransactionid;
    }

    public String getValuehash() {
        return valuehash;
    }

    public void setValuehash(String valuehash) {
        this.valuehash = valuehash;
    }

    public String getMetahash() {
        return metahash;
    }

    public void setMetahash(String metahash) {
        this.metahash = metahash;
    }

    public String getColorreason() {
        return colorreason;
    }

    public void setColorreason(String colorreason) {
        this.colorreason = colorreason;
    }
}
