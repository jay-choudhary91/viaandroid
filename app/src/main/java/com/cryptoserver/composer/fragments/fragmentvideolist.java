package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.adaptervideolist;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.appdialog;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.xdata;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by devesh on 6/8/18.
 */

public class fragmentvideolist extends basefragment {

    @BindView(R.id.rv_videolist)
    RecyclerView recyrviewvideolist;

    RelativeLayout listlayout;
    boolean touched =false;
    private Handler myHandler;
    private Runnable myRunnable;
    boolean isinbackground=false;
    Date initialDate;

    View rootview = null;
    private static final int request_permissions = 1;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adaptervideolist adapter;
    private RecyclerTouchListener onTouchListener;
    int request_take_gallery_video = 101;

    private static final int request_read_external_storage = 1;
    private static final int request_write_external_storage = 2;
    private Uri selectedimageuri =null;
    private String selectedvideopath ="";

    @Override
    public int getlayoutid() {
        return R.layout.fragment_videolist;
    }


    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public void onStop() {
        super.onStop();
        isinbackground=true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removehandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        isinbackground=false;
        recyrviewvideolist.addOnItemTouchListener(onTouchListener);
    }

    public void requestpermissions()
    {
        if (common.getstoragedeniedpermissions().isEmpty()) {
            // All permissions are granted
            getVideoList();
        } else {
            String[] array = new String[common.getstoragedeniedpermissions().size()];
            array = common.getstoragedeniedpermissions().toArray(array);
            ActivityCompat.requestPermissions(getActivity(), array, request_permissions);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_permissions) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted) {
                arrayvideolist.clear();
                adapter.notifyDataSetChanged();
                getVideoList();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this,rootview);
            listlayout=rootview.findViewById(R.id.listlayout);

            LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyrviewvideolist.setLayoutManager(layoutManager);
            ((DefaultItemAnimator) recyrviewvideolist.getItemAnimator()).setSupportsChangeAnimations(false);
            recyrviewvideolist.getItemAnimator().setChangeDuration(0);
            DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
            itemDecor.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.devidercolor));
            recyrviewvideolist.addItemDecoration(itemDecor);


            onTouchListener = new RecyclerTouchListener(getActivity(), recyrviewvideolist);
            onTouchListener.setSwipeOptionViews(R.id.btn_edit).setSwipeable( R.id.rl_rowfg,R.id.bottom_wraper,
            new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                @Override
                public void onSwipeOptionClicked(int viewID, int position) {
                    arrayvideolist.get(position).setSelected(true);
                    adapter.notifyDataSetChanged();
                    Log.e("selected Position = " ,""+ position);
                }
            });

            launchvideocomposer(false);
            if (common.getstoragedeniedpermissions().isEmpty()) {
                // All permissions are granted
                getVideoList();
            }


            recyrviewvideolist.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            touched = true;
                            initialDate = new Date();
                            Log.e("user touch","on touch" + touched);
                            break;

                        case MotionEvent.ACTION_UP:
                            touched = false;
                            Log.e("on touch end ","on touch end" + touched);
                            break;
                    }
                    return false;
                }
            });

            detectdevelopermode();

        }
        return rootview;
    }

    public void detectdevelopermode()
    {
        if(! common.isdevelopermodeenable())
        {
            myHandler=new Handler();
            myRunnable = new Runnable() {
                @Override
                public void run() {

                    if(! isinbackground && (! common.isdevelopermodeenable()))
                    {
                        if(touched==true){
                            Date currentDate=new Date();
                            int secondDifference= (int) (Math.abs(initialDate.getTime()-currentDate.getTime())/1000);
                            if(secondDifference > 4)
                            {
                                initialDate = new Date();
                                if(! appdialog.isdialogshowing())
                                    appdialog.showeggfeaturedialog(applicationviavideocomposer.getactivity());
                            }
                        }
                    }
                    myHandler.postDelayed(this, 100);
                }
            };
            myHandler.post(myRunnable);
        }
    }

    public void removehandler()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }

    public void getVideoList()
    {
        /*applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayvideolist.clear();
                adapter.notifyDataSetChanged();
            }
        });*/

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                File videodir = new File(config.videodir);
                if(! videodir.exists())
                    return;

                File[] files = videodir.listFiles();
                for (File file : files)
                {

                    Date lastModDate = new Date(file.lastModified());
                    DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String outputdatestr = outputFormat.format(lastModDate);

                    if(! isexistinarraay(file.getName(),outputdatestr))
                    {
                        video videoobj=new video();
                        videoobj.setPath(file.getAbsolutePath());
                        videoobj.setName(file.getName());
                        videoobj.setCreatedate(outputdatestr);
                        videoobj.setLastmodifiedtime(file.lastModified());

                        boolean ismedia=false;
                        MediaExtractor extractor = new MediaExtractor();
                        try {

                            if(videoobj.getPath().contains(".jpg") || videoobj.getPath().contains(".png"))
                            {
                                videoobj.setmimetype("image/");
                                ismedia=true;
                            }
                            else
                            {
                                //Adjust data source as per the requirement if file, URI, etc.
                                extractor.setDataSource(file.getAbsolutePath());
                                int numTracks = extractor.getTrackCount();
                                if(numTracks > 0)
                                {
                                    for (int i = 0; i < numTracks; ++i) {
                                        MediaFormat format = extractor.getTrackFormat(i);
                                        String mime = format.getString(MediaFormat.KEY_MIME);
                                        if(i == 0)
                                            videoobj.setmimetype(mime);

                                        if (mime.startsWith("video/") || mime.startsWith("audio/"))
                                        {
                                            if (format.containsKey(MediaFormat.KEY_DURATION)) {
                                                long seconds = format.getLong(MediaFormat.KEY_DURATION);
                                                seconds=seconds/1000000;
                                                int day = (int) TimeUnit.SECONDS.toDays(seconds);
                                                long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
                                                long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
                                                long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
                                                videoobj.setDuration(""+common.appendzero(minute)+":"+common.appendzero(second)+"");
                                                if(second > 0 || minute > 0 || hours > 0)
                                                {
                                                    videoobj.setDuration(""+common.appendzero(hours)+":"+common.appendzero(minute)+":"+common.appendzero(second)+"");
                                                    ismedia=true;
                                                }
                                            }
                                        }
                                        else if (mime.startsWith("image/"))
                                        {
                                            ismedia=true;
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            //Release stuff
                            extractor.release();
                        }
                        //String md= md5.calculatemd5(file);
                        //videoobj.setMd5(""+md);
                        if(ismedia)
                            arrayvideolist.add(videoobj);
                    }


                }
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort( arrayvideolist, new Comparator()
                        {
                            public int compare(Object o1, Object o2) {
                                if (((video)o1).getLastmodifiedtime() > ((video)o2).getLastmodifiedtime()) {
                                    return -1;
                                } else if (((video)o1).getLastmodifiedtime() < ((video)o2).getLastmodifiedtime()) {
                                    return +1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        setmediaadapter();
                    }
                });
            }
        }).start();

    }

    /*public Bitmap getthumbnail(String filepath)
    {
        Bitmap bitmap=null;
        try {
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(100, 100);

            bitmap = Glide.
                    with(getActivity()).
                    asBitmap().
                    load(filepath).
                    apply(myOptions).
                    submit().
                    get();// Width and height
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }*/

    private void checkwritestoragepermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED ) {
                opengallery();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        request_read_external_storage);
            }
        }
        else
        {
            opengallery();
        }
    }



    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_upload_icon:
                checkwritestoragepermission();
                break;
            case R.id.img_setting:
                fragmentsettings fragmatriclist=new fragmentsettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
            case R.id.img_add_icon:
                launchvideocomposer(false);
                break;
        }
    }

    public void launchvideocomposer(boolean autostart)
    {
        bottombarfragment fragbottombar=new bottombarfragment();
        fragbottombar.setData(new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                requestpermissions();
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
        gethelper().replaceFragment(fragbottombar, false, true);
    }

    public  void opengallery()
    {
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
        if (requestCode == request_take_gallery_video) {
            if (resultCode == RESULT_OK) {
                selectedimageuri = data.getData();
                // OI FILE Manager
                selectedvideopath = common.getpath(getActivity(), selectedimageuri);

                if(selectedvideopath == null){
                    common.showalert(getActivity(),getResources().getString(R.string.file_not_supported));

                    return;
                }
                setcopyvideo(selectedvideopath);

                }
            }
        }

    public void setcopyvideo(final String selectedvideopath){

        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressdialog.dismisswaitdialog();
                                Toast.makeText(getActivity(),"Video upload successfully!",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressdialog.dismisswaitdialog();
                                Toast.makeText(getActivity(),"An error occured!",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
                else
                {
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressdialog.dismisswaitdialog();
                            Toast.makeText(getActivity(),"File doesn't exist!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestpermissions();
                    }
                });
            }
        }).start();


    }



    @Override
    public void onPause() {
        super.onPause();
        recyrviewvideolist.removeOnItemTouchListener(onTouchListener);
    }

    public void setAdapter(final video videoobj,int type)
    {
        if(type == 1)
        {
            Uri uri = Uri.parse("file://"+videoobj.getPath());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("video/*");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(Intent.createChooser(share, "Share video"));
        }
        else if(type == 2)
        {
            new AlertDialog.Builder(getActivity(),R.style.customdialogtheme)
            .setTitle("Alert!!")
            .setMessage(getActivity().getResources().getString(R.string.delete_confirm))
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
            File fdelete = new File(videoobj.getPath());
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :" + videoobj.getPath());
                    arrayvideolist.remove(videoobj);
                    dialog.dismiss();
                } else {
                    System.out.println("file not Deleted :" + videoobj.getPath());
                }
            }
            adapter.notifyDataSetChanged();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }else if(type == 3){
            arrayvideolist.clear();
            adapter.notifyDataSetChanged();
            getVideoList();
        }else if(type == 4){
            xdata.getinstance().saveSetting("selectedvideourl",""+videoobj.getPath());
            composervideoplayerfragment videoplayercomposerfragment = new composervideoplayerfragment();
            videoplayercomposerfragment.setdata(videoobj.getPath());
            gethelper().replaceFragment(videoplayercomposerfragment, false, true);
        }
    }

    public boolean isexistinarraay(String name,String modifieddatetime)
    {
        if(arrayvideolist.size() > 0)
        {
            for(int i=0;i<arrayvideolist.size();i++)
            {
                if(arrayvideolist.get(i).getName().equalsIgnoreCase(name) && arrayvideolist.get(i).getCreatedate().
                        equalsIgnoreCase(modifieddatetime))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void setmediaadapter()
    {
        adapter = new adaptervideolist(getActivity(),arrayvideolist, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
            }
            @Override
            public void onItemClicked(Object object, int type) {
                video videoobj=(video)object;
                setAdapter(videoobj,type);
            }
        });
        recyrviewvideolist.setAdapter(adapter);
    }
}
