package com.cryptoserver.composer.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.fragmentsettings;
import com.cryptoserver.composer.fragments.fragmentvideocomposer;
import com.cryptoserver.composer.fragments.fragmentvideolist;
import com.cryptoserver.composer.fragments.writerappfragment;
import com.cryptoserver.composer.services.CallService;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeactivity extends LocationAwareActivity implements View.OnClickListener {

    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.img_add_icon)
    ImageView imgaddicon;
    @BindView(R.id.img_setting)
    ImageView imgsettingsicon;
    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.img_upload_icon)
    ImageView imguploadicon;
    @BindView(R.id.img_cancel)
    ImageView img_cancel;
    @BindView(R.id.img_menu)
    ImageView img_menu;
    @BindView(R.id.img_help)
    ImageView img_help;
    @BindView(R.id.actionbar)
    RelativeLayout actionbar;

    int request_take_gallery_video = 101;

    private static final int request_read_external_storage = 1;
    private static final int request_write_external_storage = 2;
    Uri selectedimageuri =null;
    private String selectedvideopath ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        applicationviavideocomposer.setActivity(homeactivity.this);

        writerappfragment frag=new writerappfragment();
        frag.setData(true);
        replaceFragment(frag, true, false);

        /*fragmentvideolist frag=new fragmentvideolist();
        replaceFragment(frag, false, true);*/

        imgaddicon.setOnClickListener(this);
        imgsettingsicon.setOnClickListener(this);
        img_back.setOnClickListener(this);
        imguploadicon.setOnClickListener(this);
        img_cancel.setOnClickListener(this);


        CallService mService = new CallService();
        Intent mIntent = new Intent(homeactivity.this, CallService.class);

        if (!isMyServiceRunning(mService.getClass()))
            startService(mIntent);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_homeactivity;
    }
    @Override

    public void launchHome() {

    }

    @Override
    public void updateheader(String txt) {
        txt_title.setText(txt);
    }

    @Override
    public void updateActionBar(int showHide, String color) {

    }

    @Override
    public void updateActionBar(int showHide) {

    }

    @Override
    public void onfragmentbackstackchanged() {
        super.onfragmentbackstackchanged();
        basefragment fragment = getcurrentfragment();
        img_back.setVisibility(View.GONE);
        img_cancel.setVisibility(View.GONE);
        img_menu.setVisibility(View.GONE);
        img_help.setVisibility(View.GONE);

        if (fragment instanceof fragmentvideolist) {
            imgaddicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setVisibility(View.VISIBLE);
            imguploadicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setEnabled(true);
            updateheader("");
        }
        else if (fragment instanceof writerappfragment) {
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            img_menu.setVisibility(View.VISIBLE);
            img_help.setVisibility(View.VISIBLE);

        }
        else if(fragment instanceof fragmentsettings){
            img_back.setVisibility(View.VISIBLE);
            img_cancel.setVisibility(View.VISIBLE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            updateheader("");

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getcurrentfragment().onHeaderBtnClick(R.id.img_back);
                break;
            case R.id.img_cancel:
                getcurrentfragment().onHeaderBtnClick(R.id.img_cancel);
                break;
            case R.id.img_add_icon:
                //Intent in=new Intent(homeactivity.this,writerappactivity.class);
               // startActivity(in);
                {
                    writerappfragment fragment=new writerappfragment();
                    addFragment(fragment, false, true);
                }
                break;
            case R.id.img_setting:
                imgsettingsicon.setEnabled(false);
                fragmentsettings fragmatriclist=new fragmentsettings();
                replaceFragment(fragmatriclist, false, true);
                break;
            case R.id.img_upload_icon:
                checkwritestoragepermission();
                break;
        }
    }


    private void checkwritestoragepermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED ) {
                opengallery();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_read_external_storage);
            }
        }
        else
        {
            opengallery();
        }
    }


    public  void opengallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), request_take_gallery_video);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == request_take_gallery_video) {
                selectedimageuri = data.getData();
                // OI FILE Manager
                selectedvideopath = getpath(this, selectedimageuri);

                if(selectedvideopath == null){
                    common.showalert(homeactivity.this,getResources().getString(R.string.file_not_supported));

                    return;
                }
                setcopyvideo(selectedvideopath);
                }
            }
        }

        public void setcopyvideo(String selectedvideopath){

            File sourceFile = new File(selectedvideopath);

            String destinationPath = config.videodir;

            String filename = sourceFile.getName();

            File destinationFile = new File(destinationPath+File.separator+filename);

            try
            {
                if (!destinationFile.getParentFile().exists())
                    destinationFile.getParentFile().mkdirs();

                if (!destinationFile.exists()) {
                    destinationFile.createNewFile();
                }

                InputStream in = new FileInputStream(selectedvideopath);
                OutputStream out = new FileOutputStream(destinationFile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Toast.makeText(homeactivity.this,"Video upload successfully!",Toast.LENGTH_SHORT).show();

            }catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(homeactivity.this,"An error occured!",Toast.LENGTH_SHORT).show();
            }
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
                final String[] selectionargs = new String[] {
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
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}

    /*@Override
    protected void onResume() {
        super.onResume();
        try
        {
            imgaddicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }*/

