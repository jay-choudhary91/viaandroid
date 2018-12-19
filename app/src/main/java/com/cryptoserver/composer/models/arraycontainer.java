package com.cryptoserver.composer.models;

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



    public arraycontainer(ArrayList<metricmodel> metricItemArraylist, String hashmethod,String videostarttransactionid,
                          String valuehash,String metahash)
    {
        setMetricItemArraylist(metricItemArraylist);
        setHashmethod(hashmethod);
        setVideostarttransactionid(videostarttransactionid);
        setValuehash(valuehash);
        setMetahash(metahash);
    }

    public ArrayList<metricmodel> getMetricItemArraylist() {
        return metricItemArraylist;
    }

    public void setMetricItemArraylist(ArrayList<metricmodel> metricItemArraylist) {
        this.metricItemArraylist = metricItemArraylist;

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
}
