package com.deeptruth.app.android.models;



/**
 * Created by devesh on 6/7/17.
 */

public class pair {



    public String KeyName="";
    public String KeyValue="";
    public String KeyAction="";
    public String ImageIcon = "";

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
}
