package com.deeptruth.app.android.models;



/**
 * Created by devesh on 6/7/17.
 */

public class managementcontroller {



    public String txtName;
    public String keyName="";
    public String action="";
    public String keyValue="";
    public boolean isAddString;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public managementcontroller(String txtName, boolean isAddString){

        this.txtName = txtName;
        this.isAddString = isAddString;

    }
    public managementcontroller(String txtName, String action, boolean isAddString){

        this.txtName = txtName;
        this.isAddString = isAddString;
        this.action = action;

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public managementcontroller(){
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public boolean isAddString() {
        return isAddString;
    }

    public void setAddString(boolean addString) {
        isAddString = addString;
    }
}
