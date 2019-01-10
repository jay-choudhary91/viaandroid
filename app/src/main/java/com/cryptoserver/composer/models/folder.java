package com.cryptoserver.composer.models;

import android.graphics.Bitmap;

/**
 * Created by root on 9/8/18.
 */

public class folder
{
    public String foldername="";
    public boolean isallfolder = false;
    public boolean isplus = false;

    public folder(String foldername,boolean isallfolder,boolean isplus)
    {
        setFoldername(foldername);
        setIsallfolder(isallfolder);
        setIsplus(isplus);
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public boolean isIsallfolder() {
        return isallfolder;
    }

    public void setIsallfolder(boolean isallfolder) {
        this.isallfolder = isallfolder;
    }

    public boolean isIsplus() {
        return isplus;
    }

    public void setIsplus(boolean isplus) {
        this.isplus = isplus;
    }
}
