package com.deeptruth.app.android.models;

/**
 * Created by root on 11/10/18.
 */

public class mediametadatainfo
{
    String id = "";
    String blockchain = "";
    String valuehash = "";
    String hashmethod = "";
    String localkey = "";
    String metricdata = "";
    String recordate = "";
    String rsequenceno = "";
    String sequencehash="";
    String sequenceno="";
    String serverdate="";
    String sequencedevicedate="";


    public mediametadatainfo( String id, String blockchain, String valuehash, String hashmethod, String localkey, String metricdata, String recordate,
            String rsequenceno,String sequencehash,String sequenceno,String serverdate,String sequencedevicedate )
    {
        setId(id);
        setBlockchain(blockchain);
        setValuehash(valuehash);
        setHashmethod(hashmethod);
        setLocalkey(localkey);
        setMetricdata(metricdata);
        setRecordate(recordate);
        setRsequenceno(rsequenceno);
        setSequencehash(sequencehash);
        setSequenceno(sequenceno);
        setServerdate(serverdate);
        setSequencedevicedate(sequencedevicedate);
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

    public String getRecordate() {
        return recordate;
    }

    public void setRecordate(String recordate) {
        this.recordate = recordate;
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
}
