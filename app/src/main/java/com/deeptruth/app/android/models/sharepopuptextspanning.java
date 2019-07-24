package com.deeptruth.app.android.models;

import java.util.ArrayList;

/**
 * Created by devesh on 24/7/19.
 */

public class sharepopuptextspanning {

   public float textsize = 0f;
   public int startindex =0;
   public int endindex = 0;
   public String linecontent = "";



    public sharepopuptextspanning(float textsize, int startindex, int endindex, String linecontent ) {
        this.textsize = textsize;
        this.endindex = endindex;
        this.linecontent = linecontent;
        this.startindex = startindex;
    }

    public float getTextsize() {
        return textsize;
    }

    public void setTextsize(float textsize) {
        this.textsize = textsize;
    }

    public int getEndindex() {
        return endindex;
    }

    public void setEndindex(int endindex) {
        this.endindex = endindex;
    }

    public String getLinecontent() {
        return linecontent;
    }

    public void setLinecontent(String linecontent) {
        this.linecontent = linecontent;
    }

    public int getStartindex() {
        return startindex;
    }

    public void setStartindex(int startindex) {
        this.startindex = startindex;
    }
}
