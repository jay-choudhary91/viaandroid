package com.cryptoserver.composer.utils;


import android.os.Environment;

import com.cryptoserver.composer.BuildConfig;

import java.io.File;

public class config {


    public static final String videodir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"files";

    public static final String prefs_name = "main_prefs";
    public static final String time_format = "hh:mm a";
    public static final String date_time_format = "MM-dd-yyyy hh:mm:ss";
    public static final String build_flavor_videocreater = "creater";
    public static final String build_flavor_videoviewer = "viewer";
    public static final String broadcast_call = "com.matraex.call_broadcast";
    public static final String build_flavor_reader = "reader";
    public static final String build_flavor_composer = "composer";

    public static final String metriclist = "metriclist";
    public static final String framecount = "framecount";
    public static final String hashtype = "hashtype";
    public static final String prefs_md5 = "md5";
    public static final String prefs_md5_salt = "md5-salt";
    public static final String prefs_sha = "sha";
    public static final String prefs_sha_salt = "sha-salt";
}
