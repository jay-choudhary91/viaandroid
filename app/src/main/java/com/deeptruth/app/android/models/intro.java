package com.deeptruth.app.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 6/8/18.
 */

public class intro implements Parcelable {
    public String title="";
    public String screenonelineone="";
    public String screentwolinetwo="";
    public String screenthreelinethree="";
    public String screenfourlinefour="";
    public String screenfivelinefive="";
    public int image=0;
    public int position = 0;
    public boolean shouldslidescreen=false;

    public intro(String title, String screenonelineone, String screentwolinetwo, String screenthreelinethree, String screenfourlinefour,
                 String screenfivelinefive, int image, int position,boolean shouldslidescreen)
    {
        setTitle(title);
        setScreenonelineone(screenonelineone);
        setScreentwolinetwo(screentwolinetwo);
        setScreenthreelinethree(screenthreelinethree);
        setScreenfourlinefour(screenfourlinefour);
        setScreenfivelinefive(screenfivelinefive);
        setImage(image);
        setPosition(position);
        setShouldslidescreen(shouldslidescreen);
    }

    public boolean isShouldslidescreen() {
        return shouldslidescreen;
    }

    public void setShouldslidescreen(boolean shouldslidescreen) {
        this.shouldslidescreen = shouldslidescreen;
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

    public String getScreenonelineone() {
        return screenonelineone;
    }

    public void setScreenonelineone(String screenonelineone) {
        this.screenonelineone = screenonelineone;
    }

    public String getScreentwolinetwo() {
        return screentwolinetwo;
    }

    public void setScreentwolinetwo(String screentwolinetwo) {
        this.screentwolinetwo = screentwolinetwo;
    }

    public String getScreenthreelinethree() {
        return screenthreelinethree;
    }

    public void setScreenthreelinethree(String screenthreelinethree) {
        this.screenthreelinethree = screenthreelinethree;
    }

    public String getScreenfourlinefour() {
        return screenfourlinefour;
    }

    public void setScreenfourlinefour(String screenfourlinefour) {
        this.screenfourlinefour = screenfourlinefour;
    }

    public String getScreenfivelinefive() {
        return screenfivelinefive;
    }

    public void setScreenfivelinefive(String screenfivelinefive) {
        this.screenfivelinefive = screenfivelinefive;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    protected intro(Parcel in) {
        title = in.readString();
        screenonelineone = in.readString();
        screentwolinetwo = in.readString();
        screenthreelinethree = in.readString();
        screenfourlinefour = in.readString();
        screenfivelinefive = in.readString();
        image = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(screenonelineone);
        parcel.writeString(screentwolinetwo);
        parcel.writeString(screenthreelinethree);
        parcel.writeString(screenfourlinefour);
        parcel.writeString(screenfivelinefive);
        parcel.writeInt(image);
    }
}
