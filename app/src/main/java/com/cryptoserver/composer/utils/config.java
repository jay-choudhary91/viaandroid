package com.cryptoserver.composer.utils;


import android.location.Location;
import android.os.Environment;

import com.cryptoserver.composer.BuildConfig;

import java.io.File;

public class config {


    public static final String tempvideodir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"files";
    public static final String videodir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"media";

    public static final double STATIC_LAT = 43.65844179931329;
    public static final double STATIC_LNG = -116.69653460383417;

    public static final String location_elevationapi_baseurl = "https://maps.googleapis.com/maps/api/elevation/json?";
    public static final String prefs_name = "main_prefs";
    public static final String time_format = "hh:mm a";
    public static final String date_time_format = "MM-dd-yyyy hh:mm:ss";
    public static final String build_flavor_videocreater = "creater";
    public static final String build_flavor_videoviewer = "viewer";
    public static final String broadcast_call = "com.matraex.call_broadcast";
    public static final String composer_service_savemetadata = "composer_service_savemetadata";
    public static final String reader_service_getmetadata = "reader_service_getmetadata";
    public static final String build_flavor_reader = "reader";
    public static final String build_flavor_composer = "composer";
    public static Location MyLastLocation = null;

    public static final String type_video_update = "video_update";
    public static final String type_video_start = "video_start";
    public static final String type_video_complete = "video_complete";
    public static final String type_video_find = "video_find";
    public static final String type_videoframes_get = "videoframes_get";

    public static final String type_audio_update = "audio_update";
    public static final String type_audio_start = "audio_start";
    public static final String type_audio_complete = "audio_complete";
    public static final String type_audio_find = "audio_find";
    public static final String type_audioframes_get = "audioframes_get";

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
    public static final String gpsaltitude = "gpsaltitude";
    public static final String wifinetworkavailable = "wifinetworkavailable";
    public static final String acceleration_x = "acceleration.x";
    public static final String acceleration_y = "acceleration.y";
    public static final String acceleration_z = "acceleration.z";
    public static final String distancetravelled = "distancetravelled";
    public static final String connectedphonenetworkquality = "connectedphonenetworkquality";
    public static final String compass = "compass";
    public static final String airplanemode = "airplanemode";
    public static final String currentcallinprogress = "currentcallinprogress";
    public static final String currentcalldurationseconds = "currentcalldurationseconds";
    public static final String currentcallremotenumber = "currentcallremotenumber";
    public static final String decibel = "decibel";
    public static final String currentcalldecibel = "currentcalldecibel";
    public static final String orientation = "orientation";
    public static final String heading = "heading";
    public static final String gpslatitude = "gpslatitude";
    public static final String gpslongitude = "gpslongitude";
    public static final String gpslatitudedegree = "gpslatitudedegree";
    public static final String gpslongitudedegree = "gpslongitudedegree";

    //graphicaldata
    public static final String Latitude="Latitude";
    public static final String Longitude="Longitude";
    public static final String LatitudeDegree="LatitudeDegree";
    public static final String LongitudeDegree="LongitudeDegree";
    public static final String Altitude="Altitude";
    public static final String Speed="Speed";
    public static final String Heading="Heading";
    public static final String Orientation="Orientation";
    public static final String MarillastDallas="1500 Marilla St Dallas";
    public static final String Xaxis="X-axis";
    public static final String Yaxis="Y-axis";
    public static final String Zaxis="Z-axis";
    public static final String PhoneType="Phone Type";
    public static final String CellProvider="Cell Provider" ;
    public static final String Connectionspeed="Connection Speed";
    public static final String OSversion="OS version" ;
    public static final String WIFINetwork="WIFI Network";
    public static final String GPSAccuracy="GPS Accuracy";
    public static final String ScreenSize="Screen Size";
    public static final String ScreenWidth="Screen Width";
    public static final String ScreenHeight="Screen Height";
    public static final String Country="Country";
    public static final String CPUUsage="CPU Usage";
    public static final String Brightness="Brightness";
    public static final String TimeZone="Time Zone";
    public static final String MemoryUsage="Memory Usage";
    public static final String Bluetooth="Bluetooth";
    public static final String LocalTime="Local Time";
    public static final String StorageAvailable="Storage Available";
    public static final String Language="Language";
    public static final String SystemUptime="System Uptime";
    public static final String Battery="Battery";
    public static final String Address="Address";

    public static final String sync_pending="sync_pending";
    public static final String sync_complete="sync_complete";

}
