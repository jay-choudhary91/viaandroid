package com.deeptruth.app.android.models;

/**
 * Created by root on 9/8/18.
 */

public class folder
{
    public String foldername="";
    public String folderdir="";
    public String thumbnailurl="";
    public String filecount="0";
    public boolean isallfolder = false;
    public boolean isplus = false;

    public folder(String foldername,boolean isallfolder,boolean isplus)
    {
        setFoldername(foldername);
        setIsallfolder(isallfolder);
        setIsplus(isplus);
    }

    public folder(String foldername,String folderdir,String thumbnailurl,String filecount,boolean isallfolder,boolean isplus)
    {
        setFoldername(foldername);
        setIsallfolder(isallfolder);
        setIsplus(isplus);
        setFolderdir(folderdir);
        setFilecount(filecount);
        setThumbnailurl(thumbnailurl);
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getFilecount() {
        return filecount;
    }

    public void setFilecount(String filecount) {
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
