package com.deeptruth.app.android.models;

/**
 * Created by devesh on 21/8/19.
 */

public class mediafilteroptions {

    public String mediafiltername ="";
    public boolean isfilterselected =false ;
    public boolean ascending=true ;

    public mediafilteroptions(String mediafiltername, boolean isselected, boolean ascending) {
        this.mediafiltername = mediafiltername;
        this.isfilterselected = isselected;
        this.ascending = ascending;
    }

    public boolean isfilterselected() {
        return isfilterselected;
    }

    public void setfilterselected(boolean isfilterselected) {
        this.isfilterselected = isfilterselected;
    }

    public boolean isascending() {
        return ascending;
    }

    public void setascending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getmediafiltername() {
        return mediafiltername;
    }

    public void setMediafiltername(String mediafiltername) {
        this.mediafiltername = mediafiltername;
    }
}
