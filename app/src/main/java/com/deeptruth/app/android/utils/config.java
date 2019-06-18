package com.deeptruth.app.android.utils;


import android.location.Location;
import android.os.Environment;

import com.deeptruth.app.android.BuildConfig;

import java.io.File;

public class config {

    public static int requestcode_login=11;
    public static String allmedia="All Media";
    public static String media="media";
    public static String cachefolder="cache";
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
    public static final String broadcast_callshareapi = "broadcast_callshareapi";

    public static final String drawer_transparency = "drawer_transparency";

    public static final String mediaquality480 = "480P";
    public static final String mediaquality720 = "720P";
    public static final String mediaquality1080 = "1080P";
    public static final String mediarecorderformat = "00:00.00";

    public static final String imagequality = "imagequality";
    public static final String videoquality = "videoquality";

    public static final String development_url = "http://dev.api.deeptruth.com/xapi.php?";
    public static final String production_url = "http://prod.api.deeptruth.com/xapi.php?";
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
    public static final String itemgpsaccuracy = "gpsaccuracy";
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
    public static final String createaccount = "fragmentcreateaccount";
    public static final String forgotpassword = "fragmentforgotpassword";
    public static final String loginpage = "loginpage";


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
    public static final String phoneclocktime="phoneclocktime";
    public static final String worldclocktime="worldclocktime";
    public static final String connectiondatadelay="connectiondatadelay";
    public static final String availablewifinetwork="availablewifinetwork";
    public static final String availablewifis="availablewifis";

    public static final String sync_offline="offline";
    public static final String sync_pending="pending";
    public static final String sync_complete="complete";
    public static final String sync_inprogress="inprogress";
    public static final String sync_notfound="notfound";

    public static final String item_photo="PHOTO";
    public static final String item_video="VIDEO";
    public static final String item_audio="AUDIO";
    public static final String item_image="IMAGE";

    public static final String item_crypto_photo="CryptoPHOTO";
    public static final String item_crypto_video="CryptoVIDEO";
    public static final String item_crypto_audio="CryptoAUDIO";

    public static final String color_red="red";
    public static final String color_gray="gray";
    public static final String color_yellow="yellow";
    public static final String color_green="green";
    public static final String color_white="white";
    public static final String color_transparent="transparent";

    public static final String color_code_red="#FF3B30";
    public static final String color_code_gray="#b1afaa";
    public static final String color_code_yellow="#FDD012";
    public static final String color_code_green="#4CD964";
    public static final String color_code_white_transparent ="#50a9a9a9";
    public static final String color_code_transparent ="#00000000";
    public static final String color_code_white="#ffffff";

    public static final String color_code_drawer_location="00a0dc";
    public static final String color_code_drawer_time="00aaa0";
    public static final String color_code_drawer_sound="a0aa00";
    public static final String color_code_drawer_phone="ff9600";
    public static final String color_code_drawer_connection="ff8278";
    public static final String color_code_drawer_summary="d27ddc";
    public static final String color_code_drawer_encryption="8282ff";


    public static final String item_valid="Valid";
    public static final String item_caution="Caution";
    public static final String item_unsent="Processing";
    public static final String item_invalid="Invalid";

    public static final String type_public="public";
    public static final String type_private="private";
    public static final String type_linkinvite="linkinvite";

    public static final String caution="CAUTION";
    public static final String verified="VERIFIED";
    public static final String validating="VALIDATING";
    public static final String invalid="INVALID";
    public static final String encrypting="ENCRYPTING";
    public static final String no_title="No Title";
    public static final String istravelleddistanceneeded="istravelleddistanceneeded";
    public static final String enableintroscreen="enableintroscreen";
    public static final String enableproduction="enableproduction";
    public static final String enabledevelopment ="enabledevelopment";
    public static final String authtoken="authtoken";
    public static final String clientemail="clientemail";
    public static final String usernameemailaddress="usernameemailaddress";
    public static final String reset_authtoken="reset_authtoken";
    public static final String clientid="clientid";
    public static final String satellitedate="satellitedate";
    public static final String remoteip="remoteip";
    public static final String satellitesdata="satellites";
    public static final String sister_metric="sister metric";
    public static final String json_blob="json blob";
    public static final String json_towerlist="json_towerlist";

    public static final String item_satellitesdate ="item_satellitesdate";
    public static final String item_remoteip="item_remoteip";
    public static final String item_satellitesdata="item_satellitesdata";

    public static final String selected_folder="selected_folder";
    public static final String selectedphotourl="selectedphotourl";
    public static final String selectedaudiourl="selectedaudiourl";
    public static final String selectedvideourl="selectedvideourl";

    public static final String latency = "latency";
    public static final String currentlatency = "currentlatency";
    public static final String phone_attitude = "phone_attitude";

    public static final long transition_fragment_millis_0 = 0;
    public static final long transition_fragment_millis_700 = 700;
    public static final long transition_fragment_millis_300 = 300;

    public static int selectedmediatype=1;   // video,photo,camera
    //public static int selectedtypemedia=1;   // video,photo,camera
    // An Enum class
    public enum permissions
    {
        LOCATION, STORAGE, CAMERA, AUDIO
    }

    public static final String all_xapi_list = "xapi_list_item_";
    public static final String sidecar_xapi_actions = "sidecar_xapi_actions_item_";
    public static final String frame_complete = "frame_complete";
    public static final String frame_completeness = "frame_completeness";
    public static final String frame_started = "frame_started";

    public static final String completed_frames = "completed_frames";
    public static final String incompleted_frames = "incompleted_frames";
    public static final String started_frames = "started_frames";

    public static final String PREFS_NAME = "main_prefs";
    public static final String LIST_XAPI = "Xapi";
    public static final String LIST_SYNC_LOG = "sync log";
    public static final String LIST_In_APP_PURCHASE = "In App purchase";
    public static final String LIST_IMAGES = "images";
    public static final String LIST_CONFIGACTION = "configaction";
    public static final String LIST_SETTINGS = "Settings";
    public static final String XAPI_URL = "api_fullurl";


    public static final String API_STORE_URL = "api_url";
    public static final String API_START_DATE = "startdate";
    public static final String API_RESPONCE_DATE = "responcedate";
    public static final String API_RESULT = "result";
    public static final String API_ACTION = "api_action";
    public static final String API_PARAMETER= "api_parameter";

    public static final String TEXT_GPS= "GPS: ";
    public static final String TEXT_DATA= "Data: ";
    public static final String TEXT_NETWORK = "Network: ";

}
