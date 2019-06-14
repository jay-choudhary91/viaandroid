package com.deeptruth.app.android.models;


import java.util.HashMap;

/**
 * Created by devesh on 6/7/17.
 */

public class pair {



    public String KeyName="";
    public String KeyValue="";
    public String KeyAction="";
    public String ImageIcon = "";
    public String completevalue = "";
    public String completelessvalue = "";
    public String startedvalue = "";

    public HashMap<String,String> keyvaluemap;

    public HashMap<String, String> getKeyvaluemap() {
        return keyvaluemap;
    }

    public void setKeyvaluemap(HashMap<String, String> keyvaluemap) {
        this.keyvaluemap = keyvaluemap;
    }

    public String getKeyAction() {
        return KeyAction;
    }

    public void setKeyAction(String keyAction) {
        KeyAction = keyAction;
    }

    public String getImageIcon() {
        return ImageIcon;
    }

    public void setImageIcon(String imageIcon) {
        ImageIcon = imageIcon;
    }

    public String getKeyName() {
        return KeyName;
    }

    public void setKeyName(String keyName) {
        KeyName = keyName;
    }

    public String getKeyValue() {
        return KeyValue;
    }

    public void setKeyValue(String keyValue) {
        KeyValue = keyValue;
    }

    public String getCompletevalue() {
        return completevalue;
    }

    public void setCompletevalue(String completevalue) {
        this.completevalue = completevalue;
    }

    public String getCompletelessvalue() {
        return completelessvalue;
    }

    public void setCompletelessvalue(String completelessvalue) {
        this.completelessvalue = completelessvalue;
    }
}
