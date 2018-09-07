package com.cryptoserver.composer.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class xdata {
    public final String prefname = "prefname";
    public static String keybaseurl = "keybaseurl";

    private static xdata ourInstance;
    private Context mContext;

    public static xdata getinstance() {
        if (ourInstance == null) {
            ourInstance = new xdata();
        }
        return ourInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public String saveSetting(String key, String value) {

        SharedPreferences prefs = this.mContext.getSharedPreferences(prefname, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
        return value;
    }

    public String getSetting(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(prefname, Context.MODE_PRIVATE);
        String data = prefs.getString(key, "");
        return data;
    }
}
