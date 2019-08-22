package com.deeptruth.app.android.models;

/**
 * Created by devesh on 21/8/19.
 */

public class mediafilteroptions {

    public String mediafiltername ="";
    public boolean isselected ;
    public boolean ascending ;

    public mediafilteroptions(String mediafiltername, boolean isselected, boolean ascending) {
        this.mediafiltername = mediafiltername;
        this.isselected = isselected;
        this.ascending = ascending;
    }

    public boolean isIsselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getMediafiltername() {
        return mediafiltername;
    }

    public void setMediafiltername(String mediafiltername) {
        this.mediafiltername = mediafiltername;
    }
}
