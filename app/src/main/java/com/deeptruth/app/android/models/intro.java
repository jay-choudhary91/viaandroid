package com.deeptruth.app.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 6/8/18.
 */

public class intro implements Parcelable {
    public String title="";
    public String description="";
    public int image=0;
    public int position = 0;

    public intro(String title,String description,int image,int position)
    {
        setTitle(title);
        setDescription(description);
        setImage(image);
        setPosition(position);
    }

    public static final Creator<intro> CREATOR = new Creator<intro>() {
        @Override
        public intro createFromParcel(Parcel in) {
            return new intro(in);
        }

        @Override
        public intro[] newArray(int size) {
            return new intro[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(image);
    }
}
