package com.cryptoserver.composer.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class xdata {
    private static final String PREF_NAME = "application";


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

        SharedPreferences prefs = this.mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
        return value;
    }

    public String getSetting(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = prefs.getString(key, "");
        return data;
    }
}
