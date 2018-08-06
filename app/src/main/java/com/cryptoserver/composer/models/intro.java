package com.cryptoserver.composer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 6/8/18.
 */

public class intro implements Parcelable {
    public String title="";
    public String description="";
    public int image=0;

    public intro(String title,String description,int image)
    {
        setTitle(title);
        setDescription(description);
        setImage(image);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    protected intro(Parcel in) {
        title = in.readString();
        description = in.readString();
        image = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
