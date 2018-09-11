package com.cryptoserver.composer.utils;


import android.os.Environment;

import com.cryptoserver.composer.BuildConfig;

import java.io.File;

public class config {


    public static final String tempvideodir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"files";
    public static final String videodir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"videos";

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
    public static final String frameupdateevery = "frameupdateevery";
    public static final String hashtype = "hashtype";
    public static final String prefs_md5 = "md5";
    public static final String prefs_md5_salt = "md5salt";
    public static final String prefs_sha = "sha";
    public static final String prefs_sha_salt = "shasalt";

    public static final String cpuusageuser = "cpuusageuser";
    public static final String cpuusagesystem = "cpuusagesystem";
    public static final String cpuusageiow = "cpuusageiow";
    public static final String cpuusageirq = "cpuusageirq";
    public static final String barometer = "barometer";
    public static final String acceleration_x = "acceleration.x";
    public static final String acceleration_y = "acceleration.y";
    public static final String acceleration_z = "acceleration.z";
    public static final String connectedphonenetworkquality = "connectedphonenetworkquality";
    public static final String compass = "compass";
    public static final String airplanemode = "airplanemode";
    public static final String currentcallinprogress = "currentcallinprogress";
    public static final String currentcalldurationseconds = "currentcalldurationseconds";
    public static final String currentcallremotenumber = "currentcallremotenumber";
    public static final String decibel = "decibel";
    public static final String currentcalldecibel = "currentcalldecibel";
}
