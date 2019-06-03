package com.deeptruth.app.android.models;

/**
 * Created by ${matraex} on 29/3/19.
 */

public class celltowermodel
{
    public String type="";
    public int cid=0;
    public int mcc=0;
    public int mnc=0;
    public int lac=0;
    public int dbm=0;
    public int basestationid=0;
    public int systemid=0;
    public int networkid=0;
    public int ci=0;
    public int tac=0;
    public int psc=0;
    public double latitude=0;
    public double longitude=0;
    public boolean hasprocessed=false;
    private String country="";
    private String iso="";
    private String countrycode="";
    private String network="";

    public celltowermodel()
    {

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getDbm() {
        return dbm;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }

    public int getBasestationid() {
        return basestationid;
    }

    public void setBasestationid(int basestationid) {
        this.basestationid = basestationid;
    }

    public int getSystemid() {
        return systemid;
    }

    public void setSystemid(int systemid) {
        this.systemid = systemid;
    }

    public int getNetworkid() {
        return networkid;
    }

    public void setNetworkid(int networkid) {
        this.networkid = networkid;
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public int getPsc() {
        return psc;
    }

    public boolean isHasprocessed() {
        return hasprocessed;
    }

    public void setHasprocessed(boolean hasprocessed) {
        this.hasprocessed = hasprocessed;
    }

    public void setPsc(int psc) {
        this.psc = psc;
    }
}
