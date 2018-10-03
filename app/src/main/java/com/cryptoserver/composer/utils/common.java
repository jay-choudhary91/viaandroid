package com.cryptoserver.composer.utils;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.models.graphicalmodel;
import com.cryptoserver.composer.models.metricmodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.USB_SERVICE;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by root on 23/4/18.
 */

public class common {
    public static final String NETWORK_CDMA = "CDMA: Either IS95A or IS95B (2G)";
    public static final String NETWORK_EDGE = "EDGE (2.75G)";
    public static final String NETWORK_GPRS = "GPRS (2.5G)";
    public static final String NETWORK_UMTS = "UMTS (3G)";
    public static final String NETWORK_EVDO_0 = "EVDO revision 0 (3G)";
    public static final String NETWORK_EVDO_A = "EVDO revision A (3G - Transitional)";
    public static final String NETWORK_EVDO_B = "EVDO revision B (3G - Transitional)";
    public static final String NETWORK_1X_RTT = "1xRTT  (2G - Transitional)";
    public static final String NETWORK_HSDPA = "HSDPA (3G - Transitional)";
    public static final String NETWORK_HSUPA = "HSUPA (3G - Transitional)";
    public static final String NETWORK_HSPA = "HSPA (3G - Transitional)";
    public static final String NETWORK_IDEN = "iDen (2G)";
    public static final String NETWORK_LTE = "LTE (4G)";
    public static final String NETWORK_EHRPD = "EHRPD (3G)";
    public static final String NETWORK_HSPAP = "HSPAP (3G)";
    public static final String NETWORK_UNKOWN = "Unknown";

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    static AlertDialog alertdialog = null;

    public static void changefocusstyle(View view, int fullbordercolor, int fullbackcolor, float borderradius) {
        view.setBackgroundResource(R.drawable.style_rounded_view);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setStroke(2, fullbordercolor);
        drawable.setCornerRadius(borderradius);
        drawable.setColor(fullbackcolor);
    }

    public static boolean isnetworkconnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


    public static void hidekeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isChargerConnected(Context context) {

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        return acCharge;
    }

    public static void changeFocusStyle(View view, int solidcolor, int radius) {
        float borderradius = 5f;
        borderradius = (float) radius;

        view.setBackgroundResource(R.drawable.style_rounded);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setCornerRadius(borderradius);
        drawable.setColor(solidcolor);
    }

    public static int[] getScreenWidthHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int[] Params = {width, height};
        return Params;
    }

    public static String getpath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docid = DocumentsContract.getDocumentId(uri);
                final String[] split = docid.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isdownloadsdocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contenturi = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getdatacolumn(context, contenturi, null, null);
            }
            // MediaProvider
            else if (ismediadocument(uri)) {
                final String docid = DocumentsContract.getDocumentId(uri);
                final String[] split = docid.split(":");
                final String type = split[0];

                Uri contenturi = null;
                if ("image".equals(type)) {
                    contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contenturi = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contenturi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionargs = new String[]{
                        split[1]
                };

                return getdatacolumn(context, contenturi, selection, selectionargs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getdatacolumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getdatacolumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /* Get uri related content real local file path. */
    public static String getUriRealPath(Context ctx, Uri uri) {
        String ret = "";

        if (isAboveKitKat()) {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        } else {
            // Android OS below sdk version 19
            ret = getImageRealPath(ctx.getContentResolver(), uri, null);
        }

        return ret;
    }

    public static String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";

        if (ctx != null && uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(ctx.getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if (isMediaDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    public static boolean isAboveKitKat() {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    public static boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    public static boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    public static boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    public static boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    public static boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    public static boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    public static boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    public static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public static boolean isDeviceInPortraitMode(Context mContext) {
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if (rotation == 0)
            return true;

        return false;
    }

    public static void setDevicePortraitMode(boolean mode) {
        if (mode) {
            applicationviavideocomposer.getactivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            applicationviavideocomposer.getactivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isdownloadsdocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean ismediadocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void shareMedia(Context context, String videoPath) {
        File file = new File(videoPath);
        if (file.exists()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = Uri.fromFile(file);
            //Uri uri = Uri.fromFile(file);
            sharingIntent.setType("video/*");
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            applicationviavideocomposer.getactivity().startActivity(Intent.createChooser(sharingIntent, "Share video using"));

        }
    }

    public static long getvideoduration(String selectedvideopath) {
        File file = new File(selectedvideopath);
        if (!file.exists())
            return 0;

        long duration = 0; //may be default

        MediaExtractor extractor = new MediaExtractor();

        try {
            //Adjust data source as per the requirement if file, URI, etc.
            extractor.setDataSource(selectedvideopath);
            int numTracks = extractor.getTrackCount();
            for (int i = 0; i < numTracks; ++i) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    if (format.containsKey(MediaFormat.KEY_DURATION)) {
                        duration = format.getLong(MediaFormat.KEY_DURATION);
                        duration = duration / 1000000;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Release stuff
            extractor.release();
        }
        return duration;
    }

    public static String mapNetworkTypeToName(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        Log.e("networkType", " " + networkType);

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return NETWORK_CDMA;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return NETWORK_EDGE;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return NETWORK_GPRS;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return NETWORK_UMTS;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return NETWORK_EVDO_0;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return NETWORK_EVDO_A;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return NETWORK_EVDO_B;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return NETWORK_1X_RTT;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return NETWORK_HSDPA;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return NETWORK_HSPA;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return NETWORK_HSUPA;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_IDEN;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_LTE;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return NETWORK_EHRPD;
//            case TelephonyManager.NETWORK_TYPE_HSPAP:
            //              return NETWORK_HSPAP;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return NETWORK_UNKOWN;
        }
    }
    public static String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }

        return returnString;
    }

    public static String removeextension(String str)
    {
        if(str.contains("."))
        {
            String str1=str.substring(0,str.indexOf("."));
            return str1;
        }
        return str;
    }

    public static String getGpsDirection(float degree) {
        String direction_text = "";
        if (degree == 0 && degree < 45 || degree >= 315
                && degree == 360) {
            direction_text = ("You are: Northbound");
        }

        if (degree >= 45 && degree < 90) {
            direction_text = ("NorthEastbound");
        }

        if (degree >= 90 && degree < 135) {
            direction_text = ("Eastbound");
        }

        if (degree >= 135 && degree < 180) {
            direction_text = ("SouthEastbound");
        }

        if (degree >= 180 && degree < 225) {
            direction_text = ("SouthWestbound");
        }

        if (degree >= 225 && degree < 270) {
            direction_text = ("Westbound");
        }

        if (degree >= 270 && degree < 315) {
            direction_text = ("NorthWestbound");
        }
        return direction_text;
    }

    public static String metric_display(String key) {
        String metricItemName = "";
        if (key.equalsIgnoreCase("imeinumber")) {
            metricItemName = "imeinumber";
        } else if (key.equalsIgnoreCase("deviceid")) {
            metricItemName = "deviceid";
        } else if (key.equalsIgnoreCase("simserialnumber")) {
            metricItemName = "simserialnumber";
        } else if (key.equalsIgnoreCase("carrier")) {
            metricItemName = "carrier";
        } else if (key.equalsIgnoreCase("carrierVOIP")) {
            metricItemName = "carrierVOIP";
        } else if (key.equalsIgnoreCase("manufacturer")) {
            metricItemName = "manufacturer";
        } else if (key.equalsIgnoreCase("model")) {
            metricItemName = "model";
        } else if (key.equalsIgnoreCase("version")) {
            metricItemName = "version";
        } else if (key.equalsIgnoreCase("osversion")) {
            metricItemName = "osversion";
        } else if (key.equalsIgnoreCase("devicetime")) {
            metricItemName = "devicetime";
        } else if (key.equalsIgnoreCase("softwareversion")) {
            metricItemName = "softwareversion";
        } else if (key.equalsIgnoreCase("networkcountry")) {
            metricItemName = "networkcountry";
        } else if (key.equalsIgnoreCase("deviceregion")) {
            metricItemName = "deviceregion";
        } else if (key.equalsIgnoreCase("timezone")) {
            metricItemName = "timezone";
        } else if (key.equalsIgnoreCase("devicelanguage")) {
            metricItemName = "devicelanguage";
        } else if (key.equalsIgnoreCase("dataconnection")) {
            metricItemName = "dataconnection";
        } else if (key.equalsIgnoreCase("networktype")) {
            metricItemName = "networktype";
        } else if (key.equalsIgnoreCase("cellnetworkconnect")) {
            metricItemName = "cellnetworkconnect";
        } else if (key.equalsIgnoreCase("screenwidth")) {
            metricItemName = "screenwidth";
        } else if (key.equalsIgnoreCase("screenheight")) {
            metricItemName = "screenheight";
        } else if (key.equalsIgnoreCase("gpslatitude")) {
            metricItemName = "gpslatitude";
        } else if (key.equalsIgnoreCase("gpslongitude")) {
            metricItemName = "gpslongitude";
        } else if (key.equalsIgnoreCase("gpsquality")) {
            metricItemName = "gpsquality";
        } else if (key.equalsIgnoreCase("gpsaltittude")) {
            metricItemName = "gpsaltittude";
        } else if (key.equalsIgnoreCase("gpsverticalaccuracy")) {
            metricItemName = "gpsverticalaccuracy";
        } else if (key.equalsIgnoreCase("brightness")) {
            metricItemName = "brightness";
        } else if (key.equalsIgnoreCase("wifi")) {
            metricItemName = "wifi";
        } else if (key.equalsIgnoreCase("wifinetworksaveailable")) {
            metricItemName = "wifinetworksaveailable";
        } else if (key.equalsIgnoreCase("wificonnect")) {
            metricItemName = "wificonnect";
        } else if (key.equalsIgnoreCase("bluetoothonoff")) {
            metricItemName = "bluetoothonoff";
        } else if (key.equalsIgnoreCase("battery")) {
            metricItemName = "battery";
        } else if (key.equalsIgnoreCase("totalspace")) {
            metricItemName = "totalspace";
        } else if (key.equalsIgnoreCase("usedspace")) {
            metricItemName = "usedspace";
        } else if (key.equalsIgnoreCase("freespace")) {
            metricItemName = "freespace";
        } else if (key.equalsIgnoreCase("rammemory")) {
            metricItemName = "rammemory";
        } else if (key.equalsIgnoreCase("usedram")) {
            metricItemName = "usedram";
        } else if (key.equalsIgnoreCase("freeram")) {
            metricItemName = "freeram";
        } else if (key.equalsIgnoreCase("devicecurrency")) {
            metricItemName = "devicecurrency";
        } else if (key.equalsIgnoreCase("systemuptime")) {
            metricItemName = "systemuptime";
        } else if (key.equalsIgnoreCase("pluggedin")) {
            metricItemName = "pluggedin";
        } else if (key.equalsIgnoreCase("headphonesattached")) {
            metricItemName = "headphonesattached";
        } else if (key.equalsIgnoreCase("deviceorientation")) {
            metricItemName = "deviceorientation";
        } else if (key.equalsIgnoreCase("wifiname")) {
            metricItemName = "wifiname";
        } else if (key.equalsIgnoreCase("connectedwifiquality")) {
            metricItemName = "connectedwifiquality";
        } else if (key.equalsIgnoreCase("username")) {
            metricItemName = "username";
        } else if (key.equalsIgnoreCase("isaccelerometeravailable")) {
            metricItemName = "isaccelerometeravailable";
        } else if (key.equalsIgnoreCase("acceleration.x")) {
            metricItemName = "acceleration.x";
        } else if (key.equalsIgnoreCase("acceleration.y")) {
            metricItemName = "acceleration.y";
        } else if (key.equalsIgnoreCase("acceleration.z")) {
            metricItemName = "acceleration.z";
        } else if (key.equalsIgnoreCase("gravitysensorenabled")) {
            metricItemName = "gravitysensorenabled";
        } else if (key.equalsIgnoreCase("gyroscopesensorenabled")) {
            metricItemName = "gyroscopesensorenabled";
        } else if (key.equalsIgnoreCase("proximitysensorenabled")) {
            metricItemName = "proximitysensorenabled";
        } else if (key.equalsIgnoreCase("lightsensorenabled")) {
            metricItemName = "lightsensorenabled";
        } else if (key.equalsIgnoreCase("internalip")) {
            metricItemName = "internalip";
        } else if (key.equalsIgnoreCase("externalip")) {
            metricItemName = "externalip";
        } else if (key.equalsIgnoreCase("processorcount")) {
            metricItemName = "processorcount";
        } else if (key.equalsIgnoreCase("activeprocessorcount")) {
            metricItemName = "activeprocessorcount";
        } else if (key.equalsIgnoreCase("usbconnecteddevicename")) {
            metricItemName = "usbconnecteddevicename";
        } else if (key.equalsIgnoreCase("cpuusageuser")) {
            metricItemName = "cpuusageuser";
        } else if (key.equalsIgnoreCase("cpuusagesystem")) {
            metricItemName = "cpuusagesystem";
        } else if (key.equalsIgnoreCase("cpuusageiow")) {
            metricItemName = "cpuusageiow";
        } else if (key.equalsIgnoreCase("cpuusageirq")) {
            metricItemName = "cpuusageirq";
        } else if (key.equalsIgnoreCase("multitaskingenabled")) {
            metricItemName = "multitaskingenabled";
        } else if (key.equalsIgnoreCase("heading")) {
            metricItemName = "heading";
        } else if (key.equalsIgnoreCase("speed")) {
            metricItemName = "speed";
        } else if (key.equalsIgnoreCase("seisometer")) {
            metricItemName = "seisometer";
        } else if (key.equalsIgnoreCase("connectedphonenetworkquality")) {
            metricItemName = "connectedphonenetworkquality";
        } else if (key.equalsIgnoreCase("compass")) {
            metricItemName = "compass";
        } else if (key.equalsIgnoreCase("barometer")) {
            metricItemName = "barometer";
        } else if (key.equalsIgnoreCase("accessoriesattached")) {
            metricItemName = "accessoriesattached";
        } else if (key.equalsIgnoreCase("attachedaccessoriescount")) {
            metricItemName = "attachedaccessoriescount";
        } else if (key.equalsIgnoreCase("nameattachedaccessories")) {
            metricItemName = "nameattachedaccessories";
        } else if (key.equalsIgnoreCase("decibel")) {
            metricItemName = "decibel";
        } else if (key.equalsIgnoreCase("gpsnumberofsatelites")) {
            metricItemName = "gpsnumberofsatelites";
        } else if (key.equalsIgnoreCase("distancetraveled")) {
            metricItemName = "distancetraveled";
        } else if (key.equalsIgnoreCase("debuggerattached")) {
            metricItemName = "debuggerattached";
        } else if (key.equalsIgnoreCase("currentcallinprogress")) {
            metricItemName = "currentcallinprogress";
        } else if (key.equalsIgnoreCase("currentcallremotenumber")) {
            metricItemName = "currentcallremotenumber";
        } else if (key.equalsIgnoreCase("currentcalldurationseconds")) {
            metricItemName = "currentcalldurationseconds";
        } else if (key.equalsIgnoreCase("currentcallvolume")) {
            metricItemName = "currentcallvolume";
        } else if (key.equalsIgnoreCase("currentcalldecibel")) {
            metricItemName = "currentcalldecibel";
        } else if (key.equalsIgnoreCase("phonetime")) {
            metricItemName = "phonetime";
        } else if (key.equalsIgnoreCase("airplanemode")) {
            metricItemName = "airplanemode";
        } else if (key.equalsIgnoreCase("gpsonoff")) {
            metricItemName = "gpsonoff";
        } else if (key.equalsIgnoreCase("syncphonetime")) {
            metricItemName = "syncphonetime";
        } else if(key.equalsIgnoreCase("country")){
            metricItemName ="country";
        } else if(key.equalsIgnoreCase("gpsaccuracy")){
            metricItemName ="gpsaccuracy";
        } else if(key.equalsIgnoreCase("connectionspeed")){
            metricItemName ="connectionspeed";
        } else if(key.equalsIgnoreCase("address")){
            metricItemName ="address";
        }
        return metricItemName;
    }

    public static String getUsername() {
        AccountManager manager = AccountManager.get(applicationviavideocomposer.getactivity());
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public static String getInternalMemory(long value) {
        String internalmermory = null;

        double b = value;
        double k = value / 1024.0;
        double m = ((value / 1024.0) / 1024.0);
        double g = (((value / 1024.0) / 1024.0) / 1024.0);
        double t = ((((value / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            internalmermory = dec.format(t);
        } else if (g > 1) {
            internalmermory = dec.format(g);
        } else if (m > 1) {
            internalmermory = dec.format(m);
        } else if (k > 1) {
            internalmermory = dec.format(k);
        } else {

            internalmermory = dec.format(b);
        }
        return internalmermory;
    }

    public static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String displayCurrencyInfoForLocale(Locale locale) {
        System.out.println("Locale: " + locale.getDisplayName());
        Currency currency = Currency.getInstance(locale);

        return currency.getDisplayName();
    }

    public static String getSystemUptime() {
        long uptimeMillis = SystemClock.elapsedRealtime();
        String wholeUptime = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(uptimeMillis),
                TimeUnit.MILLISECONDS.toMinutes(uptimeMillis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(uptimeMillis)),
                TimeUnit.MILLISECONDS.toSeconds(uptimeMillis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(uptimeMillis)));

        String time = wholeUptime;
        String timeSplit[] = time.split(":");
        int seconds = Integer.parseInt(timeSplit[0]) * 60 * 60 + Integer.parseInt(timeSplit[1]) * 60 + Integer.parseInt(timeSplit[2]);

        String Uptime = String.valueOf(seconds);

        return Uptime;
    }

    public static boolean isHeadsetOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDevices = new AudioDeviceInfo[0];
            audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo deviceInfo : audioDevices) {
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                    return true;
                }
            }
        } else {
            return audioManager.isWiredHeadsetOn();
        }
        return false;

    }

    public static String getOriantation(Context context) {
        String oriantation = null;
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //code for portrait mode
            oriantation = "PORTRAIT";

        } else {
            //code for landscape mode
            oriantation = "LANDSCAPE";
        }
        return oriantation;
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipaddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

        return ipaddress;
        //return Formatter.formatIpAddress(ip);
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {

            ex.printStackTrace();
        }
        return null;
    }

    private static String translateDeviceClass(int deviceClass) {
        switch (deviceClass) {
            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";
            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";
            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";
            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";
            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";
            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";
            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";
            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";
            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";
            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";
            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";
            default:
                return "Unknown USB class!";
        }
    }


    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                UsbManager manager = (UsbManager) context.getSystemService(USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

                String i = "";
                while (deviceIterator.hasNext()) {
                    UsbDevice device = deviceIterator.next();
                    i += "\n" +
                            "DeviceID: " + device.getDeviceId() + "\n" +
                            "DeviceName: " + device.getDeviceName() + "\n" +
                            "DeviceClass: " + device.getDeviceClass() + " - "
                            + translateDeviceClass(device.getDeviceClass()) + "\n" +
                            "DeviceSubClass: " + device.getDeviceSubclass() + "\n" +
                            "VendorID: " + device.getVendorId() + "\n" +
                            "ProductID: " + device.getProductId() + "\n";
                }

                //textInfo.setText(i);
                Toast.makeText(context, "" + i, Toast.LENGTH_LONG).show();

                Log.e("Usb connected device = ", "" + i);


            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            Log.e("permission granted = ", "" + device);
                        }
                    } else {
                        // Log.d(TAG, "permission denied for device " + device);

                        Log.e("permission denied  = ", "" + device);
                    }
                }
            }
        }
    };

    public static String appendzero(long value) {
        if (("" + value).length() == 1)
            return "0" + value;

        return "" + value;
    }

    public static String getvideoformat(String path) {
        String format = "";
        if (!path.trim().isEmpty()) {
            int extIndex = path.lastIndexOf(".");
            format = path.substring(extIndex + 1, path.length());
        }
        return format;
    }

    public static void showalert(Activity activity, String meg) {
        alertdialog = new AlertDialog.Builder(activity)
                .setTitle("Alert")
                .setMessage(meg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        alertdialog.dismiss();

                    }
                })
                .show();
    }

    public static boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String converttimeformates(long millis) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }


    public static String converttimeformate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        Date date = new Date(milliSeconds);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static boolean isdevelopermodeenable() {
        if (xdata.getinstance().getSetting(xdata.developermode).toString().trim().isEmpty() ||
                xdata.getinstance().getSetting(xdata.developermode).toString().equalsIgnoreCase("0")) {
            return false;
        }
        return true;
    }

    public static ArrayList<graphicalmodel> locationAnalyticsdata(ArrayList<metricmodel> metricItemArraylist) {
        ArrayList<graphicalmodel> locationanalytics = new ArrayList<>();
        Log.e("commonmetriclist", "" + metricItemArraylist.size());

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude")) {

                Log.e("commonlatitude", metricItemArraylist.get(i).getMetricTrackValue());

                try {
                    String metrictracklatitude=metricItemArraylist.get(i).getMetricTrackValue();
                    double latitude = Double.valueOf(metrictracklatitude);
                    locationanalytics.add(new graphicalmodel(config.Latitude, convertlatitude(latitude)));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }


            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpslongitude")) {
                try {
                    double longitude = Double.valueOf(String.valueOf(metricItemArraylist.get(i).getMetricTrackValue()));
                    locationanalytics.add(new graphicalmodel(config.Longitude, convertlongitude(longitude)));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaltittude")) {
                locationanalytics.add(new graphicalmodel(config.Altitude, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("speed")) {
                locationanalytics.add(new graphicalmodel(config.Speed, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("heading")) {
                locationanalytics.add(new graphicalmodel(config.Heading, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.compass)) {
                locationanalytics.add(new graphicalmodel(config.Orientation, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("address")) {
                locationanalytics.add(new graphicalmodel("", metricItemArraylist.get(i).getMetricTrackValue()));
            }
        }

        Log.e("commonlocation", "" + locationanalytics.size());

        return locationanalytics;
    }

    public static ArrayList<graphicalmodel> phoneAnalytics(ArrayList<metricmodel> metricItemArraylist) {

        ArrayList<graphicalmodel> phoneanalytics = new ArrayList<>();

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("model")) {
                Log.e("commonlatitude", metricItemArraylist.get(i).getMetricTrackValue());
                phoneanalytics.add(new graphicalmodel(config.PhoneType, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("carrier")) {
                phoneanalytics.add(new graphicalmodel(config.CellProvider, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("connectionspeed")) {
                phoneanalytics.add(new graphicalmodel(config.Connectionspeed, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("osversion")) {
                phoneanalytics.add(new graphicalmodel(config.OSversion, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("wifiname")) {
                phoneanalytics.add(new graphicalmodel(config.WIFINetwork, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("gpsaccuracy")) {
                phoneanalytics.add(new graphicalmodel(config.GPSAccuracy, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("screenwidth")) {//+"*"+metricItemArraylist.get(i).getMetricTrackValue())
                phoneanalytics.add(new graphicalmodel(config.ScreenSize, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("country")) {
                phoneanalytics.add(new graphicalmodel(config.Country, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.cpuusagesystem)) {
                phoneanalytics.add(new graphicalmodel(config.CPUUsage, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("brightness")) {
                phoneanalytics.add(new graphicalmodel(config.Brightness, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("timezone")) {
                phoneanalytics.add(new graphicalmodel(config.TimeZone, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("usedspace")) {
                phoneanalytics.add(new graphicalmodel(config.MemoryUsage, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("bluetoothonoff")) {
                phoneanalytics.add(new graphicalmodel(config.Bluetooth, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("phonetime")) {
                phoneanalytics.add(new graphicalmodel(config.LocalTime, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("freespace")) {
                phoneanalytics.add(new graphicalmodel(config.StorageAvailable, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("devicelanguage")) {
                phoneanalytics.add(new graphicalmodel(config.Language, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("systemuptime")) {
                      String systemuptime=metricItemArraylist.get(i).getMetricTrackValue();

                phoneanalytics.add(new graphicalmodel(config.SystemUptime, systemuptime(Long.parseLong(systemuptime))));

            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase("battery")) {
                phoneanalytics.add(new graphicalmodel(config.Battery, metricItemArraylist.get(i).getMetricTrackValue()));
            }
        }
        return phoneanalytics;
    }

    public static ArrayList<graphicalmodel> orientationarraylist(ArrayList<metricmodel> metricItemArraylist) {

        ArrayList<graphicalmodel> orientationarraylist = new ArrayList<>();

        for (int i = 0; i < metricItemArraylist.size(); i++) {
            if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_x)) {
                Log.e("commonlatitude", metricItemArraylist.get(i).getMetricTrackValue());
                orientationarraylist.add(new graphicalmodel(config.Xaxis, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_y)) {
                orientationarraylist.add(new graphicalmodel(config.Yaxis, metricItemArraylist.get(i).getMetricTrackValue()));
            } else if (metricItemArraylist.get(i).getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_z)) {
                orientationarraylist.add(new graphicalmodel(config.Zaxis, metricItemArraylist.get(i).getMetricTrackValue()));
            }
        }
        return orientationarraylist;
    }

    public static String systemuptime(long uptime) {

        String time="";
        String wholeUptime="";

     /*  long hours=uptime/3600;
       long minutes=((uptime/60)%60);
       long seconds=(uptime%60);*/

        int day = (int) (uptime / (24 * 3600));
        Log.e("days", String.valueOf(day));

        uptime = uptime % (24 * 3600);
        int hour = (int) (uptime / 3600);

        uptime %= 3600;
        int minutes = (int) (uptime / 60);

        uptime %= 60;
        int seconds = (int) uptime;

       if(hour<=24){
            wholeUptime = String.format("%02d:%02d:%02d",hour, minutes,seconds);
            time = wholeUptime;

       }else {
           if(day==1){
               wholeUptime  = String.format("%d day %02d:%02d:%02d", day,hour,minutes, seconds);
           }else{
               wholeUptime = String.format("%d days %02d:%02d:%02d", day,hour,minutes, seconds);
           }
           time = wholeUptime;
           Log.e("systemtime", "" + time);
       }
      return time;
    }

    private static String convertlatitude(double latitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append(" ");
        return builder.toString();
    }



    private static String convertlongitude(double longitude){
        StringBuilder builder = new StringBuilder();
        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");
        Log.e("locationcordinate",builder.toString());
        return builder.toString();
    }

}
