package com.deeptruth.app.android.models;

/**
 * Created by root on 9/8/18.
 */

public class metadatahash
{
    public String id="";
    public String blockchain="";
    public String valuehash="";
    public String hashmethod="";
    public String localkey="";
    public String metricdata="";
    public String recorddate="";
    public String rsequenceno="";
    public String sequencehash="";
    public String sequenceno="";
    public String serverdate="";
    public String sequencedevicedate="";
    public String videostarttransactionid="";
    public String serverdictionaryhash="";


    public String metahash="";

    public metadatahash(String id,String blockchain,String valuehash,String hashmethod,
                        String localkey,String recorddate,String metricdata,String rsequenceno,String sequencehash,String sequenceno,
                        String serverdate,String sequencedevicedate,String videostarttransactionid,String serverdictionaryhash,String metahash)
    {
        setId(id);
        setBlockchain(blockchain);
        setValuehash(valuehash);
        setHashmethod(hashmethod);
        setLocalkey(localkey);
        setRecorddate(recorddate);
        setMetricdata(metricdata);
        setRsequenceno(rsequenceno);
        setSequencehash(sequencehash);
        setSequenceno(sequenceno);
        setServerdate(serverdate);
        setSequencedevicedate(sequencedevicedate);
        setVideostarttransactionid(videostarttransactionid);
        setServerdictionaryhash(serverdictionaryhash);
        setMetahash(metahash);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public String getValuehash() {
        return valuehash;
    }

    public void setValuehash(String valuehash) {
        this.valuehash = valuehash;
    }

    public String getHashmethod() {
        return hashmethod;
    }

    public void setHashmethod(String hashmethod) {
        this.hashmethod = hashmethod;
    }

    public String getLocalkey() {
        return localkey;
    }

    public void setLocalkey(String localkey) {
        this.localkey = localkey;
    }

    public String getMetricdata() {
        return metricdata;
    }

    public void setMetricdata(String metricdata) {
        this.metricdata = metricdata;
    }

    public String getRecorddate() {
        return recorddate;
    }

    public void setRecorddate(String recorddate) {
        this.recorddate = recorddate;
    }

    public String getRsequenceno() {
        return rsequenceno;
    }

    public void setRsequenceno(String rsequenceno) {
        this.rsequenceno = rsequenceno;
    }

    public String getSequencehash() {
        return sequencehash;
    }

    public void setSequencehash(String sequencehash) {
        this.sequencehash = sequencehash;
    }

    public String getSequenceno() {
        return sequenceno;
    }

    public void setSequenceno(String sequenceno) {
        this.sequenceno = sequenceno;
    }

    public String getServerdate() {
        return serverdate;
    }

    public void setServerdate(String serverdate) {
        this.serverdate = serverdate;
    }

    public String getSequencedevicedate() {
        return sequencedevicedate;
    }

    public void setSequencedevicedate(String sequencedevicedate) {
        this.sequencedevicedate = sequencedevicedate;
    }

    public String getVideostarttransactionid() {
        return videostarttransactionid;
    }

    public void setVideostarttransactionid(String videostarttransactionid) {
        this.videostarttransactionid = videostarttransactionid;
    }
    public String getMetahash() {
        return metahash;
    }

    public void setMetahash(String metahash) {
        this.metahash = metahash;
    }

    public String getServerdictionaryhash() {
        return serverdictionaryhash;
    }

    public void setServerdictionaryhash(String serverdictionaryhash) {
        this.serverdictionaryhash = serverdictionaryhash;
    }


}
