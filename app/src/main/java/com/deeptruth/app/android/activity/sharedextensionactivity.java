package com.deeptruth.app.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.progressdialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class sharedextensionactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_extension);

        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);

        progressdialog.showwaitingdialog(sharedextensionactivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = getIntent();
                    String action = intent.getAction();
                    String type = intent.getType();
                    Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    File destinationDir=null;
                    if (Intent.ACTION_SEND.equals(action) && type != null)
                    {
                        if (type.startsWith("image")) {
                            destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID);
                        }
                        else if (type.startsWith("video"))
                        {
                            destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_MOVIES), BuildConfig.APPLICATION_ID);
                        }
                        else if (type.startsWith("audio"))
                        {
                            destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_MUSIC), BuildConfig.APPLICATION_ID);
                        }
                    }

                    if (!destinationDir.exists())
                        destinationDir.mkdirs();

                    //String  path = common.getpath(sharedextensionactivity.this, uri);
                    File sourceFile=new File(uri.getPath());
                    String newpath=sourceFile.getAbsolutePath();
                    newpath=newpath.replace("external_files","storage/emulated/0");
                    sourceFile=new File(newpath);
                    if(sourceFile.exists())
                    {
                        File mediaFile = new File(destinationDir.getPath() + File.separator +sourceFile.getName());

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

                        // Tell the media scanner about the new file so that it is
                        // immediately available to the user.
                        MediaScannerConnection.scanFile(sharedextensionactivity.this,new String[] { mediaFile.getAbsolutePath() }, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri)
                                    {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> uri=" + uri);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismisswaitdialog();
                                                Toast.makeText(sharedextensionactivity.this,"Media exported successfully!",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });

                                    }
                                });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressdialog.dismisswaitdialog();
                                finish();
                            }
                        });
                    }



                }catch (Exception e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressdialog.dismisswaitdialog();
                            Toast.makeText(sharedextensionactivity.this,"Media exporting failed!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }



    /*if (type.startsWith("image")) {
                    *//*ContentValues values = new ContentValues(3);
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.DATA, uri.toString());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*//*

        destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID);

    }
                else if (type.startsWith("video"))
    {
        destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), BuildConfig.APPLICATION_ID);

                    *//*ContentValues values = new ContentValues(3);
                    values.put(MediaStore.Video.Media.TITLE, getResources().getString(R.string.app_name));
                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                    values.put(MediaStore.Video.Media.DATA, uri.toString());
                    getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);*//*
    }
                else if (type.startsWith("audio")) {

        destinationDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), BuildConfig.APPLICATION_ID);

                    *//*ContentValues values = new ContentValues(3);
                    values.put(MediaStore.Video.Media.TITLE, getResources().getString(R.string.app_name));
                    values.put(MediaStore.Video.Media.MIME_TYPE, "audio*//**//*");
                    values.put(MediaStore.Video.Media.DATA, uri.toString());
                    getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);*//*
    }
}*/
}
