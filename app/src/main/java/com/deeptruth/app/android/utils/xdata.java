package com.deeptruth.app.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;


public class xdata {
    public final String prefname = "prefname";
    public static String xapi_url = "xapi_url";
    public static String app_paid_level = "app_paid_level";
    public static String developer_mode = "developer_mode";
    public static String developermode = "developermode";
    private static final String PREF_SETTINGS_PAIR = "pref_settings_pair";
    private static final String PREF_API_PAIR = "pref_api_pair";

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


    public String saveSettingArray(String key, String value) {
        SharedPreferences editor = this.mContext.getSharedPreferences(PREF_SETTINGS_PAIR, Context.MODE_PRIVATE);
        editor.edit().putString(key, value).apply();
        return value;
    }

    public HashMap<String, String> getSettingArray() {
        HashMap<String, String> map=new HashMap<String, String>();

        SharedPreferences prefs = this.mContext.getSharedPreferences(PREF_SETTINGS_PAIR, Context.MODE_PRIVATE);
        for( Map.Entry entry : prefs.getAll().entrySet() )
            map.put( ""+entry.getKey(), entry.getValue().toString() );

        return map;
    }

    public String saveSettingApiArray(String key, String value) {
        SharedPreferences editor = this.mContext.getSharedPreferences(PREF_API_PAIR, Context.MODE_PRIVATE);
        editor.edit().putString(key, value).apply();
        return value;
    }

    public HashMap<String, String> getSettingApiArray() {
        HashMap<String, String> map=new HashMap<String, String>();

        SharedPreferences prefs = this.mContext.getSharedPreferences(PREF_API_PAIR, Context.MODE_PRIVATE);
        for( Map.Entry entry : prefs.getAll().entrySet() )
            map.put( ""+entry.getKey(), entry.getValue().toString() );

        return map;
    }
}
