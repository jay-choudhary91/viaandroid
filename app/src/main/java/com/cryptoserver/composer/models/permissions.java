package com.cryptoserver.composer.models;

/**
 * Created by root on 31/12/18.
 */

public class permissions {
    public String permissionname="";
    public boolean ispermissionallowed=false;
    public boolean ispermissionskiped=false;
    public boolean ispermissionviewed=false;

    public permissions(String permissionname,boolean ispermissionallowed,boolean ispermissionskiped)
    {
        this.permissionname=permissionname;
        this.ispermissionallowed=ispermissionallowed;
        this.ispermissionskiped=ispermissionskiped;
    }

    public permissions(String permissionname,boolean ispermissionallowed,boolean ispermissionskiped,boolean ispermissionviewed)
    {
        this.permissionname=permissionname;
        this.ispermissionallowed=ispermissionallowed;
        this.ispermissionskiped=ispermissionskiped;
        this.ispermissionviewed=ispermissionviewed;
    }

    public boolean isIspermissionviewed() {
        return ispermissionviewed;
    }

    public void setIspermissionviewed(boolean ispermissionviewed) {
        this.ispermissionviewed = ispermissionviewed;
    }

    public String getPermissionname() {
        return permissionname;
    }

    public void setPermissionname(String permissionname) {
        this.permissionname = permissionname;
    }

    public boolean isIspermissionallowed() {
        return ispermissionallowed;
    }

    public void setIspermissionallowed(boolean ispermissionallowed) {
        this.ispermissionallowed = ispermissionallowed;
    }

    public boolean isIspermissionskiped() {
        return ispermissionskiped;
    }

    public void setIspermissionskiped(boolean ispermissionskiped) {
        this.ispermissionskiped = ispermissionskiped;
    }
}
