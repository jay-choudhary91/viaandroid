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
import com.cryptoserver.composer.fragments.fullscreenvideofragment;
import com.cryptoserver.composer.fragments.videoplayfragment;
import com.cryptoserver.composer.fragments.writerappfragment;
import com.cryptoserver.composer.services.CallService;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @BindView(R.id.img_share_icon)
    ImageView imgshareicon;
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
        imgshareicon.setOnClickListener(this);


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
        if((getcurrentfragment() instanceof writerappfragment))
        {
            txt_title.setText(txt);
        }
        else if((getcurrentfragment() instanceof fragmentvideolist) || getcurrentfragment() instanceof fragmentsettings
                || getcurrentfragment() instanceof fullscreenvideofragment  || getcurrentfragment() instanceof videoplayfragment)
        {
            txt_title.setText("");
        }

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
            imgshareicon.setVisibility(View.GONE);
            updateheader("");
        }
        else if (fragment instanceof writerappfragment) {
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            img_menu.setVisibility(View.VISIBLE);
            img_help.setVisibility(View.VISIBLE);
            imgshareicon.setVisibility(View.GONE);

        }
        else if(fragment instanceof fragmentsettings){
            img_back.setVisibility(View.VISIBLE);
            img_cancel.setVisibility(View.VISIBLE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            imgshareicon.setVisibility(View.GONE);
            updateheader("");

        }else if(fragment instanceof fullscreenvideofragment){
            img_back.setVisibility(View.GONE);
            img_cancel.setVisibility(View.GONE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            imgshareicon.setVisibility(View.VISIBLE);
            updateheader("");

        }
        else if(fragment instanceof videoplayfragment){
            img_back.setVisibility(View.GONE);
            img_cancel.setVisibility(View.GONE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            imgshareicon.setVisibility(View.VISIBLE);
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
            case R.id.img_share_icon:
                getcurrentfragment().onHeaderBtnClick(R.id.img_share_icon);
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
        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video*//*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), request_take_gallery_video);*/

        Intent intent;
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,request_take_gallery_video);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == request_take_gallery_video) {
                selectedimageuri = data.getData();
                // OI FILE Manager
                selectedvideopath = common.getpath(this, selectedimageuri);

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

            if(sourceFile.exists())
            {
                long space=sourceFile.getTotalSpace();

                String destinationDir = config.videodir;

                // check for existance of file.
                File destinationFile = null;
                File pathFile=new File(destinationDir+File.separator+sourceFile.getName());
                if(pathFile.exists())
                {
                    String extension = pathFile.getAbsolutePath().substring(pathFile.getAbsolutePath().lastIndexOf("."));
                    String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    destinationFile = new File(destinationDir+File.separator+fileName+extension);
                }
                else
                {
                    destinationFile = new File(destinationDir+File.separator+sourceFile.getName());
                }

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
            else
            {
                Toast.makeText(homeactivity.this,"File doesn't exist!",Toast.LENGTH_SHORT).show();
            }

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

