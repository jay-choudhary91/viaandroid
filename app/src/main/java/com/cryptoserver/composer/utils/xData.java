package com.cryptoserver.composer.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class xData {
    private static final String PREF_NAME = "application";


    private static xData ourInstance;
    private Context mContext;

    public static xData getInstance() {
        if (ourInstance == null) {
            ourInstance = new xData();
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
