package com.deeptruth.app.android.utils;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.Formatter;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.splashactivity;
import com.deeptruth.app.android.adapter.xapidetailadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.pair;
import com.deeptruth.app.android.models.sharepopuptextspanning;
import com.deeptruth.app.android.views.customfontedittext;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    static AlertDialog alertdialog = null;
    static Dialog custompermissiondialog = null;
    static int systemdialogshowrequestcode = 110;
    static Dialog subdialogshare = null;
    static String actiontype = "";
    static String parameter = "";

    public static final double METERS_IN_ONE_MILE = 1609.0;
    public static final double METERS_IN_ONE_FOOT = 0.3048;
    public static final double FEET_IN_ONE_MILE = 5280;

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

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
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
    private static String getdatacolumn(Context context, Uri uri, String selection,
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

    public static void sharevideo(Context context, String videoPath) {
        File file = new File(videoPath);
        if (file.exists()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            //  Uri uri = Uri.fromFile(file);
            //Uri uri = Uri.fromFile(file);
            sharingIntent.setType("video/*");
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            applicationviavideocomposer.getactivity().startActivityForResult
                    (Intent.createChooser(sharingIntent, "Share video using"), systemdialogshowrequestcode);

        }
    }

    public static void shareaudio(Context context, String videoPath) {
        File file = new File(videoPath);
        if (file.exists()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            // Uri uri = Uri.fromFile(file);
            //Uri uri = Uri.fromFile(file);
            sharingIntent.setType("audio/*");
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            applicationviavideocomposer.getactivity().startActivityForResult
                    (Intent.createChooser(sharingIntent, "Share audio using"), systemdialogshowrequestcode);

        }
    }

    public static void shareimage(Context context, String videoPath) {
        File file = new File(videoPath);
        if (file.exists()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            // Uri uri = Uri.fromFile(file);
            //Uri uri = Uri.fromFile(file);
            sharingIntent.setType("image/*");
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            applicationviavideocomposer.getactivity().startActivityForResult
                    (Intent.createChooser(sharingIntent, "Share photo using"), systemdialogshowrequestcode);

        }
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

    public static String refactormetadataformat(String meta) {
        meta = meta.replace("u00b0", "°");
        meta = meta.replace("&#39;", "\'");
        meta = meta.replace("&#36;", "\'");
        meta = meta.replace("&#34;", "");
        meta = meta.replaceAll(config.updatinglocation,"");
        meta = meta.replaceAll("1.90mbps","1.90 mbps");
        return meta;
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

    public static String removeextension(String str) {
        if (str.contains(".")) {
            String str1 = str.substring(0, str.indexOf("."));
            return str1;
        }
        return str;
    }

    public static void resetgraphicaldata() {
        xdata.getinstance().saveSetting(config.PhoneType, "NA");
        xdata.getinstance().saveSetting(config.CellProvider, "NA");
        xdata.getinstance().saveSetting(config.OSversion, "NA");
        xdata.getinstance().saveSetting(config.WIFINetwork, "NA");
        xdata.getinstance().saveSetting(config.GPSAccuracy, "NA");
        xdata.getinstance().saveSetting(config.ScreenWidth, "NA");
        xdata.getinstance().saveSetting(config.ScreenHeight, "NA");
        xdata.getinstance().saveSetting(config.Country, "NA");
        xdata.getinstance().saveSetting(config.Brightness, "NA");
        xdata.getinstance().saveSetting(config.TimeZone, "NA");
        xdata.getinstance().saveSetting(config.MemoryUsage, "NA");
        xdata.getinstance().saveSetting(config.Bluetooth, "NA");
        xdata.getinstance().saveSetting(config.LocalTime, "NA");
        xdata.getinstance().saveSetting(config.StorageAvailable, "NA");
        xdata.getinstance().saveSetting(config.Language, "NA");
        xdata.getinstance().saveSetting(config.SystemUptime, "NA");
        xdata.getinstance().saveSetting(config.Battery, "NA");
        xdata.getinstance().saveSetting(config.CPUUsage, "NA");
        xdata.getinstance().saveSetting(config.Orientation, "NA");
        xdata.getinstance().saveSetting(config.Heading, "0");
        xdata.getinstance().saveSetting(config.orientation, "0");
        xdata.getinstance().saveSetting(config.Latitude, "");
        xdata.getinstance().saveSetting(config.Longitude, "");
        xdata.getinstance().saveSetting(config.LatitudeDegree, "");
        xdata.getinstance().saveSetting(config.LongitudeDegree, "");
        xdata.getinstance().saveSetting(config.Altitude, "");
        xdata.getinstance().saveSetting(config.GPSAccuracy, "NA");
        xdata.getinstance().saveSetting(config.Address, "NA");
        xdata.getinstance().saveSetting(config.Speed, "NA");
        xdata.getinstance().saveSetting(config.acceleration_x, "NA");
        xdata.getinstance().saveSetting(config.acceleration_y, "NA");
        xdata.getinstance().saveSetting(config.acceleration_z, "NA");
        xdata.getinstance().saveSetting(config.Connectionspeed, "NA");
        xdata.getinstance().saveSetting(config.blockchainid, "NA");
        xdata.getinstance().saveSetting(config.hashformula, "NA");
        xdata.getinstance().saveSetting(config.datahash, "NA");
        xdata.getinstance().saveSetting(config.matrichash, "NA");
        xdata.getinstance().saveSetting(config.distancetravelled, "NA");
        xdata.getinstance().saveSetting(config.latency, "");
        xdata.getinstance().saveSetting(config.currentlatency, "");
        xdata.getinstance().saveSetting(config.attitude_data, "");
        xdata.getinstance().saveSetting(config.availablewifinetwork, "");
        xdata.getinstance().saveSetting(config.phoneclockdate, "");
        xdata.getinstance().saveSetting(config.worldclockdate, "");
        xdata.getinstance().saveSetting(config.deviceconnection, "NA");
        xdata.getinstance().saveSetting(config.devicecurrency, "NA");
        xdata.getinstance().saveSetting(config.gpsonoff, "NA");
        xdata.getinstance().saveSetting(config.deviceorientation, "NA");
        xdata.getinstance().saveSetting(config.screenorientatioin, "NA");
        xdata.getinstance().saveSetting(config.picturequality, "NA");
    }

    public static void setgraphicalitems(String keyname, String value, boolean ismetricsselected) {
        if (keyname.equalsIgnoreCase("model") || keyname.equalsIgnoreCase("phonetype")) {
            xdata.getinstance().saveSetting(config.PhoneType, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("carrier")) {
            xdata.getinstance().saveSetting(config.CellProvider, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("osversion")) {
            xdata.getinstance().saveSetting(config.OSversion, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("wifiname")) {
            xdata.getinstance().saveSetting(config.WIFINetwork, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("gpsaccuracy") || keyname.equalsIgnoreCase("gpshorizontalaccuracy")) {
            xdata.getinstance().saveSetting(config.GPSAccuracy, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("screenwidth")) {
            xdata.getinstance().saveSetting(config.ScreenWidth, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("screenheight")) {
            xdata.getinstance().saveSetting(config.ScreenHeight, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("deviceregion")) {
            xdata.getinstance().saveSetting(config.Country, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("brightness")) {
            xdata.getinstance().saveSetting(config.Brightness, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("timezone")) {
            xdata.getinstance().saveSetting(config.TimeZone, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("memoryusage")) {
            xdata.getinstance().saveSetting(config.MemoryUsage, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("bluetoothonoff")) {
            xdata.getinstance().saveSetting(config.Bluetooth, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("devicetime")) {
            xdata.getinstance().saveSetting(config.LocalTime, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("freespace")) {
            xdata.getinstance().saveSetting(config.StorageAvailable, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("devicelanguage")) {
            xdata.getinstance().saveSetting(config.Language, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.systemuptimeseconds)) {
            xdata.getinstance().saveSetting(config.SystemUptime, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("battery")) {
            xdata.getinstance().saveSetting(config.Battery, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.cpuusageuser)) {
            xdata.getinstance().saveSetting(config.CPUUsage, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.compass)) {
            xdata.getinstance().saveSetting(config.Orientation, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.orientation) || keyname.equalsIgnoreCase("heading")) {
            xdata.getinstance().saveSetting(config.Heading, ((ismetricsselected) ? value : "0"));
            xdata.getinstance().saveSetting(config.orientation, ((ismetricsselected) ? value : "0"));
        } else if (keyname.equalsIgnoreCase("gpslatitude")) {
            xdata.getinstance().saveSetting(config.Latitude, (ismetricsselected) ? value : "");
        } else if (keyname.equalsIgnoreCase("gpslongitude")) {
            xdata.getinstance().saveSetting(config.Longitude, (ismetricsselected) ? value : "");
        } else if (keyname.equalsIgnoreCase(config.gpslatitudedegree)) {
            xdata.getinstance().saveSetting(config.LatitudeDegree, (ismetricsselected) ? value : "");
        } else if (keyname.equalsIgnoreCase(config.gpslongitudedegree)) {
            xdata.getinstance().saveSetting(config.LongitudeDegree, (ismetricsselected) ? value : "");
        } else if (keyname.equalsIgnoreCase(config.gpsaltitude)) {
            xdata.getinstance().saveSetting(config.Altitude, (ismetricsselected) ? value : "");
        } else if (keyname.equalsIgnoreCase("gpsaccuracy")) {
            xdata.getinstance().saveSetting(config.GPSAccuracy, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("address")) {
            xdata.getinstance().saveSetting(config.Address, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("speed")) {
            xdata.getinstance().saveSetting(config.Speed, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.acceleration_x)) {
            xdata.getinstance().saveSetting(config.acceleration_x, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.acceleration_y)) {
            xdata.getinstance().saveSetting(config.acceleration_y, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.acceleration_z)) {
            xdata.getinstance().saveSetting(config.acceleration_z, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("connectionspeed")) {
            xdata.getinstance().saveSetting(config.Connectionspeed, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.connectiondatadelay)) {
            xdata.getinstance().saveSetting(config.connectiondatadelay, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("distancetravelled")) {
            xdata.getinstance().saveSetting(config.distancetravelled, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase("attitude")) {
            xdata.getinstance().saveSetting(config.attitude_data, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.airplanemode)) {
            xdata.getinstance().saveSetting(config.airplanemode, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.availablewifinetwork)) {
            xdata.getinstance().saveSetting(config.availablewifinetwork, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.worldclockdate)) {
            xdata.getinstance().saveSetting(config.worldclockdate, ((ismetricsselected) ? value : "NA"));
        } else if (keyname.equalsIgnoreCase(config.phoneclockdate)) {
            xdata.getinstance().saveSetting(config.phoneclockdate, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.deviceconnection)){
            xdata.getinstance().saveSetting(config.deviceconnection, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.devicecurrency)){
            xdata.getinstance().saveSetting(config.devicecurrency, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.gpsonoff)){
            xdata.getinstance().saveSetting(config.gpsonoff, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.deviceorientation)){
            xdata.getinstance().saveSetting(config.deviceorientation, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.screenorientatioin)){
            xdata.getinstance().saveSetting(config.screenorientatioin, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.picturequality)){
            xdata.getinstance().saveSetting(config.picturequality, ((ismetricsselected) ? value : "NA"));
        }else if(keyname.equalsIgnoreCase(config.jailbroken)) {
            xdata.getinstance().saveSetting(config.jailbroken, ((ismetricsselected) ? value : "NO"));
        }
    }


    public static void setgraphicalblockchainvalue(String keyname, String value, boolean ismetricsselected) {

        if (keyname.equalsIgnoreCase(config.blockchainid)) {
            xdata.getinstance().saveSetting(config.blockchainid, (ismetricsselected) ? value : "NA");
        } else if (keyname.equalsIgnoreCase(config.hashformula)) {
            xdata.getinstance().saveSetting(config.hashformula, (ismetricsselected) ? value : "NA");
        } else if (keyname.equalsIgnoreCase(config.datahash)) {
            xdata.getinstance().saveSetting(config.datahash, (ismetricsselected) ? value : "NA");
        } else if (keyname.equalsIgnoreCase(config.matrichash)) {
            xdata.getinstance().saveSetting(config.matrichash, (ismetricsselected) ? value : "NA");
        }else if(keyname.equalsIgnoreCase(config.camera)) {
            xdata.getinstance().saveSetting(config.camera, (ismetricsselected) ? value : "NA");
        }
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
        } else if (key.equalsIgnoreCase("memoryusage")) {
            metricItemName = "memoryusage";
        } else if (key.equalsIgnoreCase("phonetype")) {
            metricItemName = "phonetype";
        } else if (key.equalsIgnoreCase("version")) {
            metricItemName = "version";
        } else if (key.equalsIgnoreCase("osversion")) {
            metricItemName = "osversion";
        } else if (key.equalsIgnoreCase("devicetime")) {
            metricItemName = "devicetime";
        } else if (key.equalsIgnoreCase("devicedate")) {
            metricItemName = "devicedate";
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
        } else if (key.equalsIgnoreCase(config.gpslatitudedegree)) {
            metricItemName = config.gpslatitudedegree;
        } else if (key.equalsIgnoreCase(config.gpslongitudedegree)) {
            metricItemName = config.gpslongitudedegree;
        } else if (key.equalsIgnoreCase("gpslongitude")) {
            metricItemName = "gpslongitude";
        } else if (key.equalsIgnoreCase("gpsquality")) {
            metricItemName = "gpsquality";
        } else if (key.equalsIgnoreCase(config.gpsaltitude)) {
            metricItemName = config.gpsaltitude;
        } else if (key.equalsIgnoreCase("gpsverticalaccuracy")) {
            metricItemName = "gpsverticalaccuracy";
        } else if (key.equalsIgnoreCase("brightness")) {
            metricItemName = "brightness";
        } else if (key.equalsIgnoreCase("wifi")) {
            metricItemName = "wifi";
        } else if (key.equalsIgnoreCase(config.wifinetworkavailable)) {
            metricItemName = config.wifinetworkavailable;
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
        } else if (key.equalsIgnoreCase(config.systemuptimeminutes)) {
            metricItemName = config.systemuptimeminutes;
        }
        else if (key.equalsIgnoreCase(config.systemuptimeseconds)) {
            metricItemName = config.systemuptimeseconds;
        }
        else if (key.equalsIgnoreCase("pluggedin")) {
            metricItemName = "pluggedin";
        } else if (key.equalsIgnoreCase("headphonesattached")) {
            metricItemName = "headphonesattached";
        } else if (key.equalsIgnoreCase("deviceorientation")) {
            metricItemName = "deviceorientation";
        } else if (key.equalsIgnoreCase("orientation")) {
            metricItemName = "orientation";
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
        } else if (key.equalsIgnoreCase(config.distancetravelled)) {
            metricItemName = config.distancetravelled;
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
        } else if (key.equalsIgnoreCase("devicetime")) {
            metricItemName = "devicetime";
        } else if (key.equalsIgnoreCase("devicedate")) {
            metricItemName = "devicedate";
        } else if (key.equalsIgnoreCase("airplanemode")) {
            metricItemName = "airplanemode";
        } else if (key.equalsIgnoreCase("gpsonoff")) {
            metricItemName = "gpsonoff";
        } else if (key.equalsIgnoreCase("syncphonetime")) {
            metricItemName = "syncphonetime";
        } else if (key.equalsIgnoreCase("country")) {
            metricItemName = "country";
        } else if (key.equalsIgnoreCase("gpsaccuracy")) {
            metricItemName = "gpsaccuracy";
        } else if (key.equalsIgnoreCase("connectionspeed")) {
            metricItemName = "connectionspeed";
        } else if (key.equalsIgnoreCase("address")) {
            metricItemName = "address";
        } else if (key.equalsIgnoreCase("speed")) {
            metricItemName = "speed";
        } else if (key.equalsIgnoreCase("heading")) {
            metricItemName = "heading";
        } else if (key.equalsIgnoreCase("celltowersignalstrength")) {
            metricItemName = "celltowersignalstrength";
        } else if (key.equalsIgnoreCase("celltowerid")) {
            metricItemName = "celltowerid";
        }else if (key.equalsIgnoreCase("deviceconnection")) {
            metricItemName = "deviceconnection";
        } else if (key.equalsIgnoreCase("numberoftowers")) {
            metricItemName = "numberoftowers";
        } else if (key.equalsIgnoreCase("numberofsatellites")) {
            metricItemName = "numberofsatellites";
        } else if (key.equalsIgnoreCase("satelliteangle")) {
            metricItemName = "satelliteangle";
        } else if (key.equalsIgnoreCase("satelliteid")) {
            metricItemName = "satelliteid";
        } else if (key.equalsIgnoreCase("strengthofsatellites")) {
            metricItemName = "strengthofsatellites";
        }else if (key.equalsIgnoreCase("screenorientatioin")) {
            metricItemName = "screenorientatioin";
        } else if (key.equalsIgnoreCase("attitude")) {
            metricItemName = config.phone_attitude;
        } else if (key.equalsIgnoreCase(config.availablewifinetwork)) {
            metricItemName = config.availablewifinetwork;
        } else if (key.equalsIgnoreCase(config.worldclocktime)) {
            metricItemName = config.worldclocktime;
        } else if (key.equalsIgnoreCase(config.worldclockdate)) {
            metricItemName = config.worldclockdate;
        } else if (key.equalsIgnoreCase(config.phoneclocktime)) {
            metricItemName = config.phoneclocktime;
        } else if (key.equalsIgnoreCase(config.phoneclockdate)) {
            metricItemName = config.phoneclockdate;
        } else if (key.equalsIgnoreCase(config.connectiondatadelay)) {
            metricItemName = config.connectiondatadelay;
        } else if (key.equalsIgnoreCase(config.satellitedate)) {
            metricItemName = config.satellitedate;
        } else if (key.equalsIgnoreCase(config.satellitesdata)) {
            metricItemName = config.satellitesdata;
        } else if (key.equalsIgnoreCase(config.remoteip)) {
            metricItemName = config.remoteip;
        }else if (key.equalsIgnoreCase(config.jailbroken)) {
            metricItemName = config.jailbroken;
        }
        else if (key.equalsIgnoreCase(config.camera)) {
            metricItemName = config.camera;
        }
        else if (key.equalsIgnoreCase(config.picturequality)) {
            metricItemName = config.picturequality;
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

    public static int getdrawerswipearea() {
        int[] screenWidthHeight = getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int screenwidth = screenWidthHeight[0];
        int width = ((screenwidth) / 2);
        return width;
    }

    public static int getcomposerswipearea() {
        int[] screenWidthHeight = getScreenWidthHeight(applicationviavideocomposer.getactivity());
        int screenwidth = screenWidthHeight[0];
        int width = ((screenwidth) / 5);
        return width;
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
            internalmermory = dec.format(t).concat(" tb");
        } else if (g > 1) {
            internalmermory = dec.format(g).concat(" gb");
        } else if (m > 1) {
            internalmermory = dec.format(m).concat(" mb");
        } else if (k > 1) {
            internalmermory = dec.format(k).concat(" kb");
        } else {

            internalmermory = dec.format(b);
        }
        return internalmermory;
    }

    public static double distancebetweenpoints(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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

    public static String anglefromcoordinate(double lat1, double long1, double lat2,
                                             double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return String.valueOf(new DecimalFormat("#.#").format(brng));
    }

    public static List<String> getstorageaudiorecorddeniedpermissions() {
        String[] neededpermissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        List<String> deniedpermissions = new ArrayList<>();
        for (String permission : neededpermissions) {
            if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                deniedpermissions.add(permission);
            }
        }
        return deniedpermissions;
    }

    public static List<String> getstoragedeniedpermissions() {
        String[] neededpermissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        List<String> deniedpermissions = new ArrayList<>();
        for (String permission : neededpermissions) {
            if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                deniedpermissions.add(permission);
            }
        }
        return deniedpermissions;
    }

    public static List<String> getphonelocationdeniedpermissions() {
        String[] neededpermissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        List<String> deniedpermissions = new ArrayList<>();
        for (String permission : neededpermissions) {
            if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                deniedpermissions.add(permission);
            }
        }
        return deniedpermissions;
    }

    public static List<String> getlocationdeniedpermissions() {
        String[] neededpermissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        List<String> deniedpermissions = new ArrayList<>();
        for (String permission : neededpermissions) {
            if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                deniedpermissions.add(permission);
            }
        }
        return deniedpermissions;
    }

    public static void deletefile(String filename) {
        File sourcefile = new File(filename);
        try {
            File dir = new File(config.dirallmedia);
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    if (children[i].contains(sourcefile.getName()))
                        new File(dir, children[i]).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delete(File f) throws IOException {
        if (f.isDirectory()) {
            int count = f.listFiles().length;
            if (count > 0) {
                for (File c : f.listFiles()) {
                    delete(c);
                }
            }
            f.delete();
        } else {
            if (f.delete()) {
                return true;
            } else {
                new FileNotFoundException("Failed to delete file: " + f);
            }
        }
        return false;
    }

    public static void exportaudio(final File file, boolean savetohome) {
        String sourcePath = file.getAbsolutePath();
        File sourceFile = new File(sourcePath);

        File destinationDir = null;

        if (savetohome) {
            destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC), BuildConfig.APPLICATION_ID);
        } else {
            destinationDir = new File(config.dirallmedia);
        }

        if (!destinationDir.exists())
            destinationDir.mkdirs();

        final File mediaFile = new File(destinationDir.getPath() + File.separator +
                sourceFile.getName());
        try {
            if (!mediaFile.getParentFile().exists())
                mediaFile.getParentFile().mkdirs();

            if (!mediaFile.exists()) {
                mediaFile.createNewFile();
            }

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(mediaFile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Video export ", "Error2");
        }
    }

    public static List<folder> getalldirfolders() {
        List<folder> folderitem = new ArrayList<>();
        File rootdir = new File(config.dirmedia);
        if (!rootdir.exists())
            return folderitem;

        File[] files = rootdir.listFiles();
        for (File file : files) {
            if ((!file.getName().equalsIgnoreCase(config.cachefolder)) && (!file.getName().equalsIgnoreCase(config.allmedia))) {
                folder myfolder = new folder();
                myfolder.setFoldername(file.getName());
                myfolder.setFolderdir(file.getAbsolutePath());
                folderitem.add(myfolder);
            }
        }
        return folderitem;
    }

    public static boolean copyfile(File sourcefile, File destinationfolder) {
        String sourcePath = sourcefile.getAbsolutePath();
        File sourceFile = new File(sourcePath);

        File destinationDir = destinationfolder;

        if (!destinationDir.exists())
            destinationDir.mkdirs();

        final File mediaFile = new File(destinationDir.getPath() + File.separator +
                sourceFile.getName());
        try {
            if (!mediaFile.getParentFile().exists())
                mediaFile.getParentFile().mkdirs();

            if (!mediaFile.exists()) {
                mediaFile.createNewFile();
            }

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(mediaFile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean movemediafile(File sourcemediafile, File destinationfolder) {
        File sourcefile = new File(sourcemediafile.getAbsolutePath());

        if (!destinationfolder.exists())
            destinationfolder.mkdirs();

        final File destinationmediafile = new File(destinationfolder.getPath() + File.separator + sourcefile.getName());
        try {
            if (!destinationmediafile.getParentFile().exists())
                destinationmediafile.getParentFile().mkdirs();

            if (!destinationmediafile.exists()) {
                destinationmediafile.createNewFile();
            }

            InputStream in = new FileInputStream(sourcefile);
            OutputStream out = new FileOutputStream(destinationmediafile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            if (delete(sourcefile)) {
                return true;
            } else {
                Log.e("Operation ", "File move operation failed! ");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String displayCurrencyInfoForLocale(Locale locale) {
        System.out.println("Locale: " + locale.getDisplayName());
        Currency currency = Currency.getInstance(locale);
        return currency.getDisplayName();
    }

    public static int getsystemuptimeinseconds() {
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
        return seconds;
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
            oriantation = "Portrait";

        } else {
            //code for landscape mode
            oriantation = "Landscape";
        }
        return oriantation;
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) applicationviavideocomposer.getactivity().getApplicationContext().getSystemService(WIFI_SERVICE);
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

    public static String getfileextension(String path) {
        String format = "";
        if (!path.trim().isEmpty()) {
            int extIndex = path.lastIndexOf(".");
            format = path.substring(extIndex, path.length());
        }
        return format;
    }

    public static String getfilename(String path) {

        String filename = path.substring(path.lastIndexOf("/") + 1);

        return filename;
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

    public static void showalertdialog(Activity activity, String msg, final adapteritemclick mitemclick) {


        final Dialog dialog =new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.introscreencheckbox_popup);

        final CheckBox notifycheckbox = (CheckBox) dialog.findViewById(R.id.showintroscreen_checkbox);
        notifycheckbox.setText(msg);
        notifycheckbox.setTypeface(applicationviavideocomposer.comfortaaregular, Typeface.BOLD);
        TextView ok = (TextView) dialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (notifycheckbox.isChecked())
                    {
                        if(mitemclick != null)
                            mitemclick.onItemClicked(null,0);
                    }
                    else
                    {
                        if(mitemclick != null)
                            mitemclick.onItemClicked(null,1);
                    }
                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void showalert(Activity activity, String meg, final adapteritemclick mitemclick) {

        if(alertdialog != null && alertdialog.isShowing())
            alertdialog.dismiss();

        alertdialog = new AlertDialog.Builder(activity)
                .setTitle("Alert")
                .setMessage(meg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertdialog.dismiss();

                        if(mitemclick != null)
                            mitemclick.onItemClicked(null);
                    }
                })
                .show();
    }

    public static void shownotification(Context context){

        Intent notificationIntent = new Intent(context, splashactivity.class);
        notificationIntent.putExtra(config.launchtype,config.launchtypemedialist);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        String channelId = context.getResources().getString(R.string.default_notification_channel_id);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.app_name)+" "+
                        context.getResources().getString(R.string.bg_sync_complete))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.app_icon_round)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId,   context.getResources().getString(R.string.app_name)
                    , NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getResources().getString(R.string.app_name)+" "+
                    context.getResources().getString(R.string.bg_sync_complete));
            notificationManager.createNotificationChannel(channel);
        }


        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void clearnotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(0);
    }

    public static String converttimeformat(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        Date date = new Date(milliSeconds);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static void locationAnalyticsdata(final TextView txt_latitude, final TextView txt_longitude, final TextView txt_altitude, final TextView txt_heading,
                                             final TextView txt_orientation, final TextView txt_speed, final TextView txt_address) {

        {
            StringBuilder mformatbuilder = new StringBuilder();
            final String latitude = xdata.getinstance().getSetting(config.LatitudeDegree);
            if (!latitude.isEmpty() && (!latitude.equalsIgnoreCase("NA"))) {
                mformatbuilder.append(config.Latitude + System.getProperty("line.separator") + latitude);
            } else {
                mformatbuilder.append(System.getProperty("line.separator") + config.Latitude + System.getProperty("line.separator") + "NA");
            }

            final String altitude = xdata.getinstance().getSetting(config.Altitude);
            if (!altitude.isEmpty() && (!altitude.equalsIgnoreCase("NA"))) {
                mformatbuilder.append(System.getProperty("line.separator") + System.getProperty("line.separator") + config.Altitude + System.getProperty("line.separator") +
                        common.getxdatavalue(xdata.getinstance().getSetting(config.Altitude)));
            } else {
                mformatbuilder.append(System.getProperty("line.separator") + System.getProperty("line.separator") + config.Altitude + System.getProperty("line.separator") + "NA");
            }
            String value = common.getxdatavalue(xdata.getinstance().getSetting(config.Heading));
            if ((!value.equalsIgnoreCase("NA")) && (!value.equalsIgnoreCase("NA"))) {
                double heading = Double.parseDouble(value);
                int headingg = (int) heading;
                mformatbuilder.append(System.getProperty("line.separator") + System.getProperty("line.separator") + config.Heading + System.getProperty("line.separator") + headingg);
            } else {
                mformatbuilder.append(System.getProperty("line.separator") + System.getProperty("line.separator") + config.Heading + System.getProperty("line.separator"));
            }

            txt_latitude.setText(mformatbuilder.toString());
        }

        {
            StringBuilder mformatbuilder = new StringBuilder();
            final String longitude = xdata.getinstance().getSetting(config.LongitudeDegree);
            if (!longitude.isEmpty() && (!longitude.equalsIgnoreCase("NA"))) {
                mformatbuilder.append(config.Longitude + System.getProperty("line.separator") +
                        longitude);
            } else {
                mformatbuilder.append(System.getProperty("line.separator") + config.Longitude + System.getProperty("line.separator") + "NA");
            }

            mformatbuilder.append(System.getProperty("line.separator") + System.getProperty("line.separator") + config.Speed + System.getProperty("line.separator") +
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Speed)));
            mformatbuilder.append(System.getProperty("line.separator") + System.getProperty("line.separator") + config.Orientation + System.getProperty("line.separator") +
                    common.getxdatavalue(xdata.getinstance().getSetting(config.Orientation)));
            txt_longitude.setText(mformatbuilder.toString());

            txt_address.setText(common.getxdatavalue(xdata.getinstance().getSetting(config.Address)));
        }
    }

    public static String getxdatavalue(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.toString().trim().isEmpty())
            value = "NA";

        return value;
    }

    public static String systemuptime(String uptime) {

        if(uptime.trim().isEmpty() || uptime.equalsIgnoreCase("NA"))
            return "";

        long longuptime=Long.parseLong(uptime);

        Log.e("Uptimesystem ",""+longuptime);
        String time = "";
        String wholeUptime = "";

        int day = (int) (longuptime / (24 * 3600));

        longuptime = longuptime % (24 * 3600);
        int hour = (int) (longuptime / 3600);

        longuptime %= 3600;
        int minutes = (int) (longuptime / 60);

        longuptime %= 60;
        int seconds = (int) longuptime;

        if(day >= 1)
        {
            if (day == 1) {
                wholeUptime = String.format("%d day %02d:%02d:%02d", day, hour, minutes, seconds);
            } else {
                wholeUptime = String.format("%d days %02d:%02d:%02d", day, hour, minutes, seconds);
            }
            time = wholeUptime;
        }
        else
        {
            wholeUptime = String.format("%02d:%02d:%02d", hour, minutes, seconds);
            time = wholeUptime;
        }
        return time;
    }

    public static String gettimezoneshortname() {
        String tz = TimeZone.getDefault().getDisplayName();
        String[] stz = tz.split(" ");
        StringBuilder sName = new StringBuilder();
        for (int i = 0; i < stz.length; i++) {
            sName.append(stz[i].charAt(0));
        }
        return sName.toString();
    }

    public static String convertlatitude(double latitude) {
        StringBuilder builder = new StringBuilder();

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append(" ");
        if (latitude < 0) {
            builder.append(" S");
        } else {
            builder.append(" N");
        }
        return builder.toString();
    }

    public static void switchtodevelopmentconnection(boolean switchtodevelopment)
    {
        if(switchtodevelopment)
            xdata.getinstance().saveSetting(xdata.xapi_url,config.development_url);
        else
            xdata.getinstance().saveSetting(xdata.xapi_url,config.production_url);
    }

    public static int getapppaidlevel()
    {
        int paidlevel=0;
        if(! xdata.getinstance().getSetting(xdata.app_paid_level).isEmpty())
            paidlevel = Integer.parseInt(xdata.getinstance().getSetting(xdata.app_paid_level));

        return paidlevel;
    }

    public static int getunpaidvideorecordlength()
    {
        int unpaidvideorecordlength=30;
        if(! xdata.getinstance().getSetting(xdata.unpaid_media_record_length).isEmpty())
            unpaidvideorecordlength = Integer.parseInt(xdata.getinstance().getSetting(xdata.unpaid_media_record_length));

        return unpaidvideorecordlength;
    }

    public static String convertlongitude(double longitude) {
        StringBuilder builder = new StringBuilder();


        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");
        if (longitude < 0) {
            builder.append(" W");
        } else {
            builder.append(" E");
        }
        return builder.toString();
    }

    public static String[] getmetricesarray() {
        String[] items = {"battery", "phonetype", "imeinumber", "simserialnumber", "version", "osversion", "softwareversion", "model",
                "manufacturer", "brightness", "gpslatitude", "gpslongitude", config.gpslatitudedegree, config.gpslongitudedegree,
                config.gpsaltitude, "gpsquality", "carrier", "screenwidth","screenheight", config.systemuptimeminutes,
                config.systemuptimeseconds,"multitaskingenabled",
                "proximitysensorenabled", "pluggedin", "devicedate", "devicetime","deviceregion", "devicelanguage", "devicecurrency",
                "timezone", "headphonesattached", "accessoriesattached", "nameattachedaccessories", "attachedaccessoriescount",
                "totalspace", "usedspace", "memoryusage", "freespace","orientation", "deviceorientation", "rammemory", "usedram",
                "freeram", "wificonnect", "cellnetworkconnect", "internalip","externalip", "networktype",
                config.connectedphonenetworkquality, "gravitysensorenabled", "gyroscopesensorenabled",
                "lightsensorenabled", "debuggerattached", "deviceid", "bluetoothonoff", "wifiname", config.wifinetworkavailable,
                "processorcount", "activeprocessorcount", config.cpuusageuser, config.cpuusagesystem, config.cpuusageiow,
                config.cpuusageirq, config.compass, config.decibel, config.barometer, config.acceleration_x, config.acceleration_y,
                config.acceleration_z, config.distancetravelled, config.currentcallinprogress, config.currentcalldurationseconds,
                config.currentcallremotenumber, config.currentcalldecibel, config.airplanemode,
                "camera",config.picturequality,config.jailbroken,"screenorientatioin",
                "isaccelerometeravailable", "dataconnection", "currentcallvolume", "gpsonoff", "syncphonetime", "country","jailbroken",
                "connectionspeed", "gpsaccuracy", "speed", "heading", "address", "celltowersignalstrength", "celltowerid","deviceconnection", "numberoftowers",
                "numberofsatellites","satelliteangle", "satelliteid", "strengthofsatellites", "attitude", config.availablewifinetwork,
                "phoneclocktime", "worldclocktime", config.connectiondatadelay,config.satellitedate,config.satellitesdata,config.remoteip,config.worldclockdate,config.phoneclockdate};

        return items;
    }
    // config.sister_metric,config.json_blob

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static int checkframeduration() {
        int frameduration = 15;

        if (!xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
            frameduration = Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

        return frameduration;
    }

    public static String checkkey() {
        String key = "";
        if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                xdata.getinstance().getSetting(config.hashtype).trim().isEmpty()) {
            key = config.prefs_md5;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt)) {
            key = config.prefs_md5_salt;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha)) {
            key = config.prefs_sha;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt)) {
            key = config.prefs_sha_salt;
        }
        return key;
    }

    public static String[] getcurrentdatewithtimezone() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime);


        int convertedVal = Integer.parseInt(localTime);

        if (convertedVal > 0) {
            convertedVal = convertedVal / 100;
            localTime = "+" + "" + convertedVal;
        } else {

            convertedVal = convertedVal / 100;

            localTime = "" + convertedVal;
        }

        DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

        String currenttime[] = {date1.format(currentLocalTime), localTime};


        return currenttime;

    }

    public static String getvideotimefromurl(String url) {

        String duration = "";
        MediaExtractor extractor = new MediaExtractor();
        try {
            //Adjust data source as per the requirement if file, URI, etc.
            extractor.setDataSource(url);
            int numTracks = extractor.getTrackCount();
            if (numTracks > 0) {
                for (int i = 0; i < numTracks; ++i) {
                    MediaFormat format = extractor.getTrackFormat(i);
                    if (format.containsKey(MediaFormat.KEY_DURATION)) {
                        long seconds = format.getLong(MediaFormat.KEY_DURATION);
                        seconds = seconds / 1000000;
                        int day = (int) TimeUnit.SECONDS.toDays(seconds);
                        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
                        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
                        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
                        long millis = TimeUnit.SECONDS.toMillis(seconds) - (TimeUnit.SECONDS.toSeconds(seconds) * 60);

                        String milliseconds = "0";
                        char[] chararray = String.valueOf(millis).toCharArray();
                        if (chararray != null && chararray.length > 0)
                            milliseconds = "" + chararray[0];

                        if (second == 0)
                            second = 1;

                        duration = ("" + hours + ":" + common.appendzero(minute) + ":" + common.appendzero(second) + "." + milliseconds);
                        break;
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

    public static File gettempfileforaudiowave() {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(config.audiowavesdir, fileName + ".png");

        File destinationDir = new File(config.audiowavesdir);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File gettempfileforhash(String hashtype) {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = null;

        if(hashtype.equalsIgnoreCase(config.prefs_md5))
            file = new File(config.hashesdir, fileName + ".framemd5");
        else if(hashtype.equalsIgnoreCase(config.prefs_md5_salt))
            file = new File(config.hashesdir, fileName + ".framemd5");
        else if(hashtype.equalsIgnoreCase(config.prefs_sha))
            file = new File(config.hashesdir, fileName + ".sha256");
        else if(hashtype.equalsIgnoreCase(config.prefs_sha_salt))
            file = new File(config.hashesdir, fileName + ".framemd5");

        File destinationDir = new File(config.hashesdir);
        try {

            if (!destinationDir.exists())
                destinationDir.mkdirs();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static boolean ishashfileexist(String filepath) {
        String filename = common.getfilename(filepath);
        String[] array = filename.split("\\.");
        if (array.length > 0) {
            File file = new File(config.hashesdir, array[0] + ".framemd5");
            if (file.exists())
                return true;
        }
        return false;
    }

    public static String getexisthashfilepath(String filepath) {
        String filename = common.getfilename(filepath);
        String[] array = filename.split("\\.");
        if (array.length > 0) {
            File file = new File(config.hashesdir, array[0] + ".framemd5");
            if (file.exists())
                return file.getAbsolutePath();
        }
        return "";
    }

    public static File createtempfileofmedianameforhash(String hashtype,String filepath) {
        String filename = common.getfilename(filepath);
        String[] array = filename.split("\\.");
        if (array.length > 0) {
            File file = new File(config.hashesdir, array[0] + ".framemd5");
            File destinationDir = new File(config.hashesdir);
            try {

                if (!destinationDir.exists())
                    destinationDir.mkdirs();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return file;
        }
        return gettempfileforhash(hashtype);
    }

    public static void showcustompermissiondialog(Context context, final adapteritemclick mitemclick, final String permission) {
        if (custompermissiondialog != null && custompermissiondialog.isShowing())
            custompermissiondialog.dismiss();

        custompermissiondialog = new Dialog(context);
        custompermissiondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custompermissiondialog.setCanceledOnTouchOutside(false);
        custompermissiondialog.setCancelable(false);
        custompermissiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        custompermissiondialog.setContentView(R.layout.dialog_custom_permission);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(custompermissiondialog.getWindow().getAttributes());

        TextView txt_permission_title = (TextView) custompermissiondialog.findViewById(R.id.txt_permission_title);
        TextView txt_permission_desc = (TextView) custompermissiondialog.findViewById(R.id.txt_permission_desc);
        TextView txt_allow = (TextView) custompermissiondialog.findViewById(R.id.txt_allow);
        TextView txt_skip = (TextView) custompermissiondialog.findViewById(R.id.txt_skip);
        ImageView logo_icon = (ImageView) custompermissiondialog.findViewById(R.id.logo_icon);


        if (permission.equalsIgnoreCase(Manifest.permission.CAMERA)) {
            logo_icon.setImageResource(R.drawable.permission_camera);
            txt_permission_title.setText(context.getResources().getString(R.string.deeptruth_camera));
            txt_permission_desc.setText(context.getResources().getString(R.string.camera_take_picture));
        } else if (permission.equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
            txt_permission_title.setText(context.getResources().getString(R.string.deeptruth_current_location));
            txt_permission_desc.setText(context.getResources().getString(R.string.your_current_location));
        } else if (permission.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            txt_permission_title.setText(context.getResources().getString(R.string.deeptruth_current_location));
            txt_permission_desc.setText(context.getResources().getString(R.string.your_current_location));
        } else if (permission.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            txt_permission_title.setText(context.getResources().getString(R.string.deeptruth_current_location));
            txt_permission_desc.setText(context.getResources().getString(R.string.your_current_location));
        } else if (permission.equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                permission.equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
            logo_icon.setImageResource(R.drawable.location_icon);
            txt_permission_title.setText(context.getResources().getString(R.string.deeptruth_current_location));
            txt_permission_desc.setText(context.getResources().getString(R.string.your_current_location));
        }

        txt_allow.setText(context.getResources().getString(R.string.allow));
        txt_skip.setText(context.getResources().getString(R.string.skip_for_now));

        txt_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mitemclick != null)
                    mitemclick.onItemClicked(permission, 1);
            }
        });

        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mitemclick != null)
                    mitemclick.onItemClicked(permission, 0);

                dismisscustompermissiondialog();
            }
        });

        custompermissiondialog.getWindow().setAttributes(lp);
        custompermissiondialog.show();
    }

    public static void dismisscustompermissiondialog() {
        if (custompermissiondialog != null && custompermissiondialog.isShowing())
            custompermissiondialog.dismiss();
    }

    public static void setspannable(String encryption_key, String encryption_value, TextView txt_encryption) {

        String encryptionstr = encryption_key.concat(encryption_value);
        // int substring=encryptionblock.lastIndexOf(":");
        SpannableStringBuilder encryptionstring = new SpannableStringBuilder(encryptionstr);
        encryptionstring.setSpan(new StyleSpan(applicationviavideocomposer.regularfonttype.getStyle()), 0, encryption_key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        encryptionstring.setSpan(new StyleSpan(applicationviavideocomposer.semiboldfonttype.getStyle()), encryption_key.length(), encryptionstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        encryptionstring.setSpan(new RelativeSizeSpan(1.1f), encryption_key.length(), encryptionstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_encryption.setText(encryptionstring);
    }

    public static void setdrawabledata(String keyname, String keyvalue, TextView txt_encryption) {

        if (txt_encryption == null)
            return;

        if (!keyname.trim().isEmpty()) {
            String encryptionstr = keyname.concat(keyvalue);
            SpannableStringBuilder encryptionstring = new SpannableStringBuilder(encryptionstr);
            encryptionstring.setSpan(new StyleSpan(applicationviavideocomposer.regularfonttype.getStyle()), 0, keyname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            encryptionstring.setSpan(new StyleSpan(applicationviavideocomposer.semiboldfonttype.getStyle()), keyname.length(), encryptionstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            encryptionstring.setSpan(new RelativeSizeSpan(1.1f), keyname.length(), encryptionstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_encryption.setText(encryptionstring);
        } else {
            SpannableStringBuilder encryptionstring = new SpannableStringBuilder(keyvalue);
            encryptionstring.setSpan(new StyleSpan(applicationviavideocomposer.semiboldfonttype.getStyle()), 0, keyvalue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            encryptionstring.setSpan(new RelativeSizeSpan(1.1f), 0, keyvalue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_encryption.setText(encryptionstring);
        }
    }

    public static String filesize(String imageurl) {
        String img_size = null;

        File file = new File(imageurl);
        float photosize = file.length();
        float k = photosize / 1024;
        float m = ((photosize / 1024) / 1024);
        float g = (((photosize / 1024) / 1024) / 1024);
        float t = ((((photosize / 1024) / 1024) / 1024) / 1024);

        DecimalFormat dec = new DecimalFormat("0.0");

        if (t > 1) {
            img_size = dec.format(t) + "Tb";
        } else if (g > 1) {
            img_size = dec.format(g) + "Gb";
        } else if (m > 1) {
            img_size = dec.format(m) + "Mb";
        } else if (k > 1) {
            img_size = dec.format(k) + "Kb";
        } else {
            img_size = dec.format(photosize);
        }
        return img_size;
    }

    public static String getvideotimefromurl(Context context, String url) {

        MediaPlayer mpl = MediaPlayer.create(context, Uri.parse(url));
        int millis=0;
        if(mpl != null)
            millis = mpl.getDuration();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        //   convent into milisec and then minus from millis
        int milisec = (int) (seconds * 1000);
        int milisecond = (int) (millis - milisec);
        String milliseconds = "0";
        char[] chararray = String.valueOf(milisecond).toCharArray();
        if (chararray != null && chararray.length > 0)
            milliseconds = "" + chararray[0];
        int milise = Integer.parseInt(milliseconds = "" + chararray[0]);
        Log.e("milliseconds", "" + milliseconds);

        String duration = ("" + hours + ":" + common.appendzero(minutes) + ":" + common.appendzero(seconds) + "." + milise);
        return duration;

    }

    public static String gettimestring(long millis) {
        StringBuffer buf = new StringBuffer();
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        //   convent into milisec and then minus from millis
        int milisec = (int) (seconds * 1000);
        int milisecond = (int) (millis - milisec);
        String milliseconds = "0";
        char[] chararray = String.valueOf(milisecond).toCharArray();
        if (chararray != null && chararray.length > 0)
            milliseconds = "" + chararray[0];
        int milise = Integer.parseInt(milliseconds = "" + chararray[0]);

        if (hours == 0) {
            buf.append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds))
                    .append(".")
                    .append(String.format("%d", milise));

        } else {
            buf.append(String.format("%02d", hours))
                    .append(":")
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds))
                    .append(".")
                    .append(String.format("%d", milise));

        }
        return buf.toString();
    }


    public static void slidetoabove(final LinearLayout layout_mediatype) {
        layout_mediatype.animate()
                .translationY(-40)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout_mediatype.setVisibility(View.GONE);
                    }
                });

    }

    public static void slidetodown(LinearLayout layout_mediatype) {
        layout_mediatype.setAlpha(0.0f);
        // Start the animation
        layout_mediatype.animate()
                .translationY(-1)
                .alpha(1.0f)
                .setListener(null);
        layout_mediatype.setVisibility(View.VISIBLE);

    }


    public static boolean isdevelopermodeenable() {
        if (xdata.getinstance().getSetting(xdata.developer_mode).toString().trim().isEmpty() ||
                xdata.getinstance().getSetting(xdata.developer_mode).toString().equalsIgnoreCase("0")) {
            return false;
        }
        return true;
    }

    public static String getcompassdirection(int heading) {

        String strdirection = "E";
        if (heading > 23 && heading <= 67) {
            strdirection = "NE";
        } else if (heading > 68 && heading <= 112) {
            strdirection = "E";
        } else if (heading > 113 && heading <= 157) {
            strdirection = "SE";
        } else if (heading > 158 && heading <= 202) {
            strdirection = "S";
        } else if (heading > 203 && heading <= 247) {
            strdirection = "SW";
        } else if (heading > 248 && heading <= 292) {
            strdirection = "W";
        } else if (heading > 293 && heading <= 337) {
            strdirection = "NW";
        } else if (heading >= 338 || heading <= 22) {
            strdirection = "N";
        }
        return strdirection;
    }

    public static String[] gettransparencyvalues() {
        String[] values = {"FF", "FC", "FA", "F7", "F5", "F2", "F0", "ED", "EB", "E8", "E6", "E3", "E0", "DE", "DB", "D9", "D6", "D4", "D1", "CF", "CC", "C9",
                "C7", "C4", "C2", "BF", "BD", "BA", "B8", "B5", "B3", "B0", "AD", "AB", "A8", "A6", "A3", "A1", "9E", "9C", "99", "96", "94", "91", "8F", "8C",
                "8A", "87", "85", "82", "80", "7D", "7A", "78", "75", "73", "70", "6E", "6B", "69", "66", "63", "61", "5E", "5C", "59", "57", "54", "52",
                "4F", "4D", "4A", "47", "45", "42", "40", "3D", "3B", "38", "36", "33", "30", "2E", "2B", "29", "26", "24", "21", "1F", "1C",
                "1A", "17", "14", "12", "0F", "0D", "0A", "08", "05", "03", "00"};
        return values;
    }

    public static GradientDrawable getyelloradargradient() {
        // a hexadecimal color string to an integer value (int color)
        int[] colors = {Color.parseColor("#00FDD012"), Color.parseColor("#00FDD012"), Color.parseColor("#14FDD012"),
                Color.parseColor("#4AFDD012"), Color.parseColor("#85FDD012"), Color.parseColor("#C2FDD012"),
                Color.parseColor("#FFFDD012"),
                Color.parseColor("#C2FDD012"), Color.parseColor("#85FDD012"), Color.parseColor("#4AFDD012"),
                Color.parseColor("#14FDD012"), Color.parseColor("#00FDD012"), Color.parseColor("#00FDD012")};

        //create a new gradient color
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, colors);

        gd.setCornerRadius(0f);
        return gd;
    }

    public static GradientDrawable getblueradargradient() {
        // a hexadecimal color string to an integer value (int color)
        int[] colors = {Color.parseColor("#00FDD012"), Color.parseColor("#00FDD012"), Color.parseColor("#14004860"),
                Color.parseColor("#4A004860"), Color.parseColor("#85004860"), Color.parseColor("#C2004860"),
                Color.parseColor("#FF004860"),
                Color.parseColor("#C2004860"), Color.parseColor("#85004860"), Color.parseColor("#4A004860"),
                Color.parseColor("#14004860"), Color.parseColor("#00FDD012"), Color.parseColor("#00FDD012")};

        //create a new gradient color
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, colors);

        gd.setCornerRadius(0f);
        return gd;
    }

    public static int getnavigationbarheight() {
        Resources resources = applicationviavideocomposer.getactivity().getResources();
        boolean value = hasNavBar(resources);
        if (value) {
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public static String get24hourformat() {
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(applicationviavideocomposer.getactivity().getApplicationContext());
        Calendar currenttime;
        currenttime = Calendar.getInstance();
        String devicedate;
        if (is24HourFormat == true) {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("HH:mm");
            devicedate = devicetimeformat.format(currenttime.getTime());
        } else {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("hh:mm:ss aa");
            devicedate = devicetimeformat.format(currenttime.getTime());
        }
        return devicedate;
    }

    public static int getmediaorientation(Context context, String mediapath) {
        int rotate = 0;
        try {
            File mediafile = new File(mediapath);
            Uri uri = FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", mediafile);

            context.getContentResolver().notifyChange(uri, null);
            ExifInterface exifInterface = new ExifInterface(mediafile.getAbsolutePath());
            int orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            /*String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = context.getContentResolver().query(uri, orientationColumn, null, null, null);
            int orientation = -1;
            if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }*/

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int sortmediatype(int type) {
        if (type == 0) {
            return 2;
        } else if (type == 1) {
            return 3;
        } else if (type == 2) {
            return 4;
        }
        return 2;
    }

    public static double convertmetertofeets(double meter) {
        return meter / 0.305;
    }

    public static double convertmpstomph(double mps) {
        return mps * 2.236936;
    }

    public static double convertmpstokmph(double mps) {
        return  mps * 3.6 ;
    }
    public static double convertmetertomiles(double meters) {
        return meters * 0.000621371192;
    }

    public static double convertmetertokm(double meters) {
        return  meters/1000;
    }
    public static double convertmilestometer(float miles) {
        return miles * 1609.344;
    }

    public static int convertmetertofeet(float meter) {
        return (int) (meter / 0.3048);
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,valueInDp, context.getResources().getDisplayMetrics());
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    public static boolean isdeviceinportraitmode(Context mContext) {
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if (rotation == 0)
            return true;

        return false;
    }

    public static String getcolorprogresspercentage(int currentprogress, int totalitem) {
        double current = currentprogress;
        double max = totalitem;
        double progresspercentage = (current * 100) / max;
        //DecimalFormat precision=new DecimalFormat("0.00");
        String output = "" + Math.round(progresspercentage) + "%";
        if (progresspercentage == 100)
            output = "100%";

        return output;
    }

    public static String getcolorbystring(String colorname) {
        String color = "#FF3B30";
        if (colorname.equalsIgnoreCase("green")) {
            color = config.color_code_green;
        } else if (colorname.equalsIgnoreCase("yellow")) {
            color = config.color_code_yellow;
        } else if (colorname.equalsIgnoreCase("red")) {
            color = config.color_code_red;
        } else if (colorname.equalsIgnoreCase("gray")) {
            color = config.color_code_transparent;
        } else if (colorname.equalsIgnoreCase(config.color_transparent)) {
            color = config.color_code_transparent;
        } else if (colorname.equalsIgnoreCase("white")) {
            color = config.color_code_white;
        } else if (colorname.equalsIgnoreCase("blue")) {
            color = config.color_code_blue;
        }
        return color;
    }

    public static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isvalidusername(String target) {
        if (target == null) {
            return false;
        } else {
            char[] chars = target.toCharArray();

            if (chars != null && chars.length > 0) {
                Log.e("arrayvalue", "" + chars[0]);
                int value = (int) chars[0];
                if ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z')) {
                    return true;
                }
            }
            return false;
        }
    }


    public static boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static boolean checkemailvalidation(Context mcontext, customfontedittext edtusername) {

        if (edtusername.getText().toString().trim().toString().length() == 0) {
            Toast.makeText(mcontext, "Please enter email!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!common.isValidEmail(edtusername.getText().toString().trim())) {
            Toast.makeText(mcontext, "Please enter valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    ;

    public static String getactionbarcolor() {
        String colorString = "#" + common.gettransparencyvalues()[65] + "004860";
        return colorString;
    }

    public static BitmapDescriptor bitmapdescriptorfromvector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static int getscreenwidthheight(int percentage) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int percentageheight = (height / 100) * percentage;

        return percentageheight;
    }

    public static  int getviewheight(double percentage){
        percentage = (percentage) * (common.getscreenheight())/100;
        return (int) percentage;
    }
    public static double getpercentage(int height){
        Log.e("percentage" , ""+height );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenheight = displayMetrics.heightPixels;

        height = ( height *100 )/screenheight;
        Log.e("percentageheight" , ""+height );
        return height;
    }
    public static int getscreenheight() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        return height;
    }

    public static int getscreenwidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }


    public static String getdate() {
        //Date date = new Date();
        //return DateFormat.getDateInstance().format(date);
        Date date = new Date();
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        return formatter.format(date);
    }

    public static String parsedateformat(Date date) {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        return formatter.format(date);
    }

    public static String parsetimeformat(Date date) {
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(applicationviavideocomposer.getactivity().getApplicationContext());
        String devicetime;
        if (is24HourFormat == true) {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("HH:mm:ss");
            devicetime = devicetimeformat.format(date);
        } else {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("hh:mm:ss.SS a",Locale.ENGLISH);
            devicetime = devicetimeformat.format(date);
        }
        return devicetime;
    }


    public static String gettime() {

        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(applicationviavideocomposer.getactivity().getApplicationContext());
        Calendar currenttime;
        currenttime = Calendar.getInstance();
        String devicedate;
        if (is24HourFormat == true) {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("HH:mm:ss");
            devicedate = devicetimeformat.format(currenttime.getTime());
        } else {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("hh:mm:ss.SS aa", Locale.ENGLISH);
            devicedate = devicetimeformat.format(currenttime.getTime());
        }
        return devicedate;

    }

    public static String getphonetime() {

        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(applicationviavideocomposer.getactivity().getApplicationContext());
        Calendar currenttime;
        currenttime = Calendar.getInstance();
        String devicedate;
        if (is24HourFormat == true) {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("HH:mm:ss");
            devicedate = devicetimeformat.format(currenttime.getTime());
        } else {
            SimpleDateFormat devicetimeformat = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            devicedate = devicetimeformat.format(currenttime.getTime());
        }
        return devicedate;

    }

    public static String currenttime_analogclock() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatted = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        String formattedDate = formatted.format(date);
        return formattedDate;
    }

    public static void setting_check(String key, String defaultvalue) {
        if (setting_get(key).trim().isEmpty())
            setting_set(key, defaultvalue);
    }

    public static void setting_set(String key, String keyvalue) {
        xdata.getinstance().saveSetting(key, keyvalue);
    }

    public static void sharemessagewithapps(String sharemessage) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharemessage);
        sendIntent.setType("text/plain");
        applicationviavideocomposer.getactivity().startActivity(Intent.createChooser(sendIntent,
                applicationviavideocomposer.getactivity().getResources().getText(R.string.send_to)));
    }

    public static String setting_get(String key) {
        return xdata.getinstance().getSetting(key);
    }

    public static String createurl(List<NameValuePair> nameValuePairList, String baseurl) {
        String newcreateurl = "";
        parameter = "";

        for (int i = 0; i < nameValuePairList.size(); i++) {
            String key = nameValuePairList.get(i).getName();
            String value = nameValuePairList.get(i).getValue();
            getactiontype(key, value);
            if (newcreateurl.isEmpty()) {
                newcreateurl = baseurl + "&" + key + "=" + value;
                parameter = key + "=" + value;
            } else {
                newcreateurl = newcreateurl + "&" + key + "=" + value;
                parameter = parameter + "&" + key + "=" + value;
            }
        }
        Log.e("finalurl", newcreateurl);
        return newcreateurl;
    }

    public static void setxapirequestresponses(String jsonurl, String action, String parameterxapi, List<NameValuePair> nameValuePairList,
                                               String baseurl, Object object, Date starttime, Date endtime, String sharedprefkey) {
        HashMap<String, String> xapivalue = new HashMap<String, String>();

        if (nameValuePairList == null) {
            xapivalue.put(config.API_STORE_URL, jsonurl);
            xapivalue.put(config.API_ACTION, action);
            xapivalue.put(config.API_PARAMETER, parameterxapi);
        } else {
            xapivalue.put(config.API_STORE_URL, common.createurl(nameValuePairList, baseurl));
            xapivalue.put(config.API_ACTION, actiontype);
            xapivalue.put(config.API_PARAMETER, parameter);
        }
        xapivalue.put(config.API_RESULT, object.toString());
        xapivalue.put(config.API_START_DATE, "" + starttime);
        xapivalue.put(config.API_RESPONCE_DATE, timedifference(starttime, endtime));

        xapivalue.put(config.started_frames, xdata.getinstance().getSetting(config.frame_started));
        xapivalue.put(config.completed_frames, xdata.getinstance().getSetting(config.frame_complete));
        xapivalue.put(config.incompleted_frames, xdata.getinstance().getSetting(config.frame_completeness));

        timedifference(starttime, endtime);

        Gson gson = new Gson();
        String json = gson.toJson(xapivalue);

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (xdata.getinstance().getSetting(sharedprefkey + i).isEmpty()) {
                xdata.getinstance().saveSetting(sharedprefkey + i, json);
                //xdata.getinstance().saveSettingApiArray("xapi"+""+i, json);
                break;
            }
        }
    }

    public static String convertdateintostring(Date time) {

        String pattern = "HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        /*Date today = Calendar.getInstance().getTime();*/
        String converttime = df.format(time);

        return converttime;
    }

    public static void getactiontype(String key, String value) {
        if (key.equalsIgnoreCase("action")) {
            actiontype = value;
        }
    }

    public static String timedifference(Date starttime, Date endtime) {

        long diff = endtime.getTime() - starttime.getTime();

        long seconds = diff / 1000;
        long milisecond = diff % 1000;

        return ("" + seconds + "." + "" + milisecond);
    }

    public static void clearxapidate(xapidetailadapter mControllerAdapter, ArrayList<pair> mItemList, String sharedprefkey) {

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!xdata.getinstance().getSetting(sharedprefkey + "" + i).isEmpty()) {
                xdata.getinstance().clearsharevalue(sharedprefkey + "" + i);
            } else {
                if (mControllerAdapter != null && mItemList != null) {
                    mItemList.clear();
                    mControllerAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    public static String convertstringintodate(String date) {
        String dateformat = "";
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(date);
            dateformat = parsedateformat(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateformat;
    }


    public static String speedformatter(String value) {
        if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA") && (! value.equalsIgnoreCase("null"))))
        {
            String[] array=value.split(" ");
            if(array.length >0)
                value=array[0];

            DecimalFormat precision = new DecimalFormat("0.0");
            double speedinmps=Double.parseDouble(value);
            Locale locale = Locale.getDefault();
            /*if (useMiles(locale)) {
                return ""+precision.format(convertmpstomph(speedinmps))+" mp/h";
            } else {
                return ""+precision.format(convertmpstokmph(speedinmps))+" km/h";
            }*/
            return ""+precision.format(convertmpstokmph(speedinmps))+" km/h";
        }
        return "0 km/h";
        //return value;
    }

    public static String travelleddistanceformatter(String value) {
        if((! value.trim().isEmpty()) && (! value.equalsIgnoreCase("NA") && (! value.equalsIgnoreCase("null"))))
        {
            String[] array=value.split(" ");
            if(array.length >0)
                value=array[0];

            DecimalFormat precision = new DecimalFormat("0.0");
            Locale locale = Locale.getDefault();
            /*if (useMiles(locale)) {
                return ""+precision.format(convertmetertomiles(distanceInMeters))+" miles";
            } else {
                return ""+precision.format(convertmetertokm(distanceInMeters))+" km";
            }*/
            return ""+precision.format(convertmetertokm(Float.parseFloat(value)))+" km";
        }
        return "0 km";
        //return value;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String format(double distanceInMeters, boolean realTime, Locale locale,
                                String units) {

        DecimalFormat decimalFormat;decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern("#.#");

        if (distanceInMeters == 0) {
            return "";
        }

        switch (units) {
            case "miles":
                return formatMiles(distanceInMeters, realTime);
            case "kilometers":
                return formatKilometers(distanceInMeters, realTime);
            default:
                return "";
        }
    }

    private static String formatMiles(double distanceInMeters, boolean realTime) {
        double distanceInFeet = distanceInMeters / METERS_IN_ONE_FOOT;
        if (distanceInFeet < 10) {
            return formatDistanceLessThanTenFeet(distanceInFeet, realTime);
        } else if (distanceInFeet < FEET_IN_ONE_MILE / 10) {
            return formatDistanceOverTenFeet(distanceInFeet);
        } else {
            return formatDistanceInMiles(distanceInMeters);
        }
    }

    private static String formatKilometers(double distanceInMeters, boolean realTime) {
        if (distanceInMeters >= 100) {
            return "";
        } else if (distanceInMeters > 10) {
            return formatDistanceOverTenMeters(distanceInMeters);
        } else {
            return formatShortMeters(distanceInMeters, realTime);
        }
    }

    private static String formatDistanceInMiles(double distanceInMeters) {
        Locale locale = Locale.getDefault();
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        return String.format(Locale.getDefault(), "%s mi",
                decimalFormat.format(distanceInMeters / METERS_IN_ONE_MILE));
    }

    private static int roundDownToNearestTen(double distance) {
        return (int) Math.floor(distance / 10) * 10;
    }

    private static String formatDistanceOverTenMeters(double distanceInMeters) {
        return String.format(Locale.getDefault(), "%s m", distanceInMeters);
    }

    private static String formatShortMeters(double distanceInMeters, boolean realTime) {
        if (realTime) {
            return "now";
        } else {
            return formatDistanceOverTenMeters(distanceInMeters);
        }
    }

    private static String formatDistanceLessThanTenFeet(double distanceInFeet, boolean realTime) {
        if (realTime) {
            return "now";
        } else {
            return String.format(Locale.getDefault(), "%d ft", (int) Math.floor(distanceInFeet));
        }
    }

    private static String formatDistanceOverTenFeet(double distanceInFeet) {
        int roundedDistanceInFeet = roundDownToNearestTen(distanceInFeet);
        return String.format(Locale.getDefault(), "%d ft", roundedDistanceInFeet);
    }

    private static boolean useMiles(Locale locale) {
        return locale.equals(Locale.US) || locale.equals(Locale.UK);
    }

    public static String getFormattedTime(String time) {

        String timeformat = "";
        DateFormat format = new SimpleDateFormat("hh:mm:ss:SS aa", Locale.ENGLISH);
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(time);
            timeformat = parsetimeformat(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeformat;
    }

    public static String getapplicationversion(Context context){
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
             version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getapplicationname(Context context) {
        final PackageManager pm = context.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( context.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        return applicationName;
    }

    public static String getworldclocktime(){
        String worldclocktime = "";
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
        DateFormat gmtFormat = new SimpleDateFormat(config.time_format);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);
        String dateformat= gmtFormat.format(new Date());
        try {
            worldclocktime = date24Format.format(date12Format.parse(dateformat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return worldclocktime +" GMT";
    }

    public static boolean is24hourstimeformat()
    {
        return android.text.format.DateFormat.
                is24HourFormat(applicationviavideocomposer.getactivity().getApplicationContext());
    }

    public static String getworldclocktimewithsec(){
        String worldclocktime = "";
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(applicationviavideocomposer.getactivity().getApplicationContext());
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat time12Format = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm:ss");
        DateFormat gmtFormat = new SimpleDateFormat(config.worldclocktime_format);
        DateFormat gmtFormat12hours = new SimpleDateFormat(config.phonetime_format);
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);
        gmtFormat12hours.setTimeZone(gmtTime);
        String dateformat= gmtFormat.format(new Date());
        Log.e("dateformat",""+dateformat);
        try {
            if (is24HourFormat == true) {
                worldclocktime = date24Format.format(date12Format.parse(dateformat));
            } else {
                worldclocktime = time12Format.format(date24Format.parse(gmtFormat12hours.format(new Date())));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return worldclocktime +" GMT";
    }

    public static float getlocationcircleradius(float currentzoomlevel) {

        Log.e("Zoomlevel ",""+currentzoomlevel);
        if(currentzoomlevel >= 21)
            return 0.5f;
        else if(currentzoomlevel >= 20)
            return 1f;
        else if(currentzoomlevel >= 19)
            return 2f;
        else if(currentzoomlevel >= 18)
            return 3f;
        else if(currentzoomlevel >= 17)
            return 10f;
        else if(currentzoomlevel >= 16)
            return 15f;
        else if(currentzoomlevel >= 15)
            return 35f;
        else if(currentzoomlevel >= 14)
            return 60f;
        else if(currentzoomlevel >= 13)
            return 120f;
        else if(currentzoomlevel >= 12)
            return 180f;
        else if(currentzoomlevel >= 11)
            return 250f;
        else if(currentzoomlevel >= 10)
            return 350f;

        return 150f;
    }

    public static int getzoomlevelfromcircle(Circle circle) {
        int zoomLevel=0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    /**
     * set up markers to all position
     *
     * @param pathlist list of markers
     */
    public static void mapzoomalongwithtraveledpath(Context context, GoogleMap map, List<LatLng> pathlist,
                                                    int mapwidth,int mapheight) {
        int padding = 20;

        if (pathlist.size() == 0) {
            return;
        }
        /**create for loop for get the latLngbuilder from the marker list*/
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng ltlng : pathlist) {
            builder.include(ltlng);
        }
        /**initialize the padding for map boundary*/

        /**create the bounds from latlngBuilder to set into map camera*/
        LatLngBounds bounds = builder.build();
        /**create the camera with bounds and padding to set into map*/
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,mapwidth,mapheight,padding);
        // map.setPadding(50,headerhEIGHT+10,50,bottomHeight+10);
        map.moveCamera(cu);
    }

    public static  boolean isservicerunning(Context context,Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldrestrictmedialimit(String xdatakey)
    {
        if(common.getapppaidlevel() > 0)
            return false;

        int count=0;
        if(xdata.getinstance().getSetting(xdatakey).trim().isEmpty())
            xdata.getinstance().saveSetting(xdatakey,"0");
        else
            count=Integer.parseInt(xdata.getinstance().getSetting(xdatakey).trim());

        Log.e(xdatakey,"Steps "+xdata.getinstance().getSetting(xdatakey).trim());

        if(count >= Integer.parseInt(xdata.getinstance().getSetting(xdata.unpaid_media_record_trim_count)))
            return true;

        return false;
    }

    public static boolean ismediatrimcountexceed(String xdatakey)
    {
        if(common.getapppaidlevel() > 0)
            return false;

        int count=0;
        if(xdata.getinstance().getSetting(xdatakey).trim().isEmpty())
            xdata.getinstance().saveSetting(xdatakey,"0");
        else
            count=Integer.parseInt(xdata.getinstance().getSetting(xdatakey).trim());

        if(count > Integer.parseInt(xdata.getinstance().getSetting(xdata.unpaid_media_record_trim_count)))
            return true;

        return false;
    }

    // url = file path or whatever suitable URL you want.
    public static String getmimetype(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean shouldshowupgradepopup(String xdatakey)
    {
        if(common.getapppaidlevel() > 0)
            return false;

        int count=0;
        if(xdata.getinstance().getSetting(xdatakey).trim().isEmpty())
            xdata.getinstance().saveSetting(xdatakey,"1");
        else
            count=Integer.parseInt(xdata.getinstance().getSetting(xdatakey).trim());

        count++;
        xdata.getinstance().saveSetting(xdatakey,""+count);
        Log.e(xdatakey,""+xdata.getinstance().getSetting(xdatakey).trim());

        if(count >= Integer.parseInt(xdata.getinstance().getSetting(xdata.unpaid_media_record_trim_count)))
            return true;

        return false;
    }

    public static String mediaplaytimeformatter(int millis) {
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        //   convent into milisec and then minus from millis
        int milisec = (int) (seconds * 1000);
        int milisecond = (int) (millis - milisec);
        String milliseconds = "00";
        char[] chararray = String.valueOf(milisecond).toCharArray();
        if (chararray != null && chararray.length > 1)
            milliseconds = "" + chararray[0]+""+chararray[1];

        int milise = Integer.parseInt(milliseconds);

        String timeduration = (common.appendzero(minutes) + ":" + common.appendzero(seconds) + "." + milise);
        return timeduration;

    }

    public static void setspanning(ArrayList<sharepopuptextspanning> spanarraylist, TextView txtview){
        String line = spanarraylist.get(0).getLinecontent();
        // char[] linecontent = line.toCharArray();

        Log.e("line",line);
        SpannableString ss1=  new SpannableString(line);
        for (int i =0;i< spanarraylist.size();i++)
        {
            ss1.setSpan(new RelativeSizeSpan(spanarraylist.get(i).getTextsize()), spanarraylist.get(i).getStartindex(),spanarraylist.get(i).getEndindex(), 0); // set size

        }
        txtview.setText(ss1);
    }
}

