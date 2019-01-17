package com.cryptoserver.composer.models;

import android.graphics.Bitmap;

/**
 * Created by root on 9/8/18.
 */

public class folder
{
    public String foldername="";
    public String folderdir="";
    public int filecount=0;
    public boolean isallfolder = false;
    public boolean isplus = false;

    public folder(String foldername,boolean isallfolder,boolean isplus)
    {
        setFoldername(foldername);
        setIsallfolder(isallfolder);
        setIsplus(isplus);
    }

    public folder(String foldername,String folderdir,int filecount,boolean isallfolder,boolean isplus)
    {
        setFoldername(foldername);
        setIsallfolder(isallfolder);
        setIsplus(isplus);
        setFolderdir(folderdir);
        setFilecount(filecount);
    }

    public int getFilecount() {
        return filecount;
    }

    public void setFilecount(int filecount) {
        this.filecount = filecount;
    }

    public String getFolderdir() {
        return folderdir;
    }

    public void setFolderdir(String folderdir) {
        this.folderdir = folderdir;
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
