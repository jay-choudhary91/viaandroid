package com.deeptruth.app.android.utils;


import android.location.Location;
import android.os.Environment;

import com.deeptruth.app.android.BuildConfig;

import java.io.File;

public class config {

    public static String allmedia="All Media";
    public static String media="media";
    public static String audiotempfile="audiotemp.pcm";


    public static String settingpageurl="http://console.dev.crypto-servers.com/inapp-settings.php";

    public static final String rootdir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID;

    public static final String dirmedia = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+media;

    public static final String dirallmedia = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+media+File.separator+allmedia;

    public static final String hashesdir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"hashes";

    public static final String audiowavesdir = ""+ Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"Android/data/"+
            BuildConfig.APPLICATION_ID+File.separator+"audiowaves";

    public static final double STATIC_LAT = 43.65844179931329;
    public static final double STATIC_LNG = -116.69653460383417;

    public static final String location_elevationapi_baseurl = "https://maps.googleapis.com/maps/api/elevation/json?";
    public static final String prefs_name = "main_prefs";
    public static final String time_format = "hh:mm a";
    public static final String date_format = "MM/dd/yyyy";
    public static final String date_time_format = "MM-dd-yyyy hh:mm:ss";
    public static final String build_flavor_videocreater = "creater";
    public static final String build_flavor_videoviewer = "viewer";
    public static final String broadcast_call = "com.matraex.call_broadcast";
    public static final String composer_service_savemetadata = "composer_service_savemetadata";
    public static final String reader_service_getmetadata = "reader_service_getmetadata";
    public static final String composer_service_getencryptionmetadata = "composer_service_getencryptionmetadata";
    public static final String ismediadataservicerunning = "ismediadataservicerunning";
    public static final String broadcast_medialistnewitem = "broadcast_medialistnewitem";

    public static final String mediaquality480 = "480P";
    public static final String mediaquality720 = "720P";
    public static final String mediaquality1080 = "1080P";
    public static final String mediarecorderformat = "0:00:00.0";

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

    public static final String type_image_update = "image_update";
    public static final String type_image_start = "image_start";
    public static final String type_image_complete = "image_complete";
    public static final String type_image_find = "image_find";
    public static final String type_imageframes_get = "imageframes_get";

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
    public static final String attitude_data = "attitude_data";
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
    public static final String speed = "speed";
    public static final String address = "address";
    public static final String carrier = "carrier";

    public static final String blockchainid = "blockchainid";
    public static final String hashformula = "hashformula";
    public static final String datahash = "datahash";
    public static final String matrichash = "matrichash";



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

    public static final String sync_offline="offline";
    public static final String sync_pending="pending";
    public static final String sync_complete="complete";
    public static final String sync_inprogress="inprogress";
    public static final String sync_notfound="notfound";

    public static final String item_photo="PHOTO";
    public static final String item_video="VIDEO";
    public static final String item_audio="AUDIO";
    public static final String item_image="IMAGE";

    public static final String color_red="red";
    public static final String color_yellow="yellow";
    public static final String color_green="green";

    public static final String caution="CAUTION";
    public static final String verified="VERIFIED";

    public static final String selected_folder="selected_folder";

    public static final String latency = "latency";
    public static final String currentlatency = "currentlatency";
    public static final String phone_attitude = "phone_attitude";

    public static int selectedmediatype=0;   // video,photo,camera
    // An Enum class
    public enum permissions
    {
        LOCATION, STORAGE, CAMERA, AUDIO
    }

}
