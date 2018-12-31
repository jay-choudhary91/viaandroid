package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.adapterreadermedialist;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.services.readmediadataservice;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.xdata;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
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
 * A simple {@link Fragment} subclass.
 */
public class readermedialist extends basefragment {

    View rootview;
    RelativeLayout listreaderlayout;
    private static final int request_permissions = 1;

    @BindView(R.id.rv_readervideolist)
    RecyclerView recyrviewvideolist;
    private Handler myhandler;
    private Runnable myrunnable;

    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapterreadermedialist adapter;
    private RecyclerTouchListener onTouchListener;

    private static final int request_read_external_storage = 1;
    private int REQUESTCODE_PICK=201;
    int mediatype;
    String keytype= "",mediatitle = "";
    private BroadcastReceiver coredatabroadcastreceiver;
    Date initialdate ;


    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }

    @Override
    public void onStop() {
        super.onStop();
       // isinbackground=true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removehandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        //isinbackground=false;
       // recyrviewvideolist.addOnItemTouchListener(onTouchListener);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) applicationviavideocomposer.getactivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            listreaderlayout = rootview.findViewById(R.id.listlayout);
            LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyrviewvideolist.setLayoutManager(layoutManager);
            ((DefaultItemAnimator) recyrviewvideolist.getItemAnimator()).setSupportsChangeAnimations(false);
            recyrviewvideolist.getItemAnimator().setChangeDuration(0);
            DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
            itemDecor.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.devidercolor));
            recyrviewvideolist.addItemDecoration(itemDecor);

            if (common.getstoragedeniedpermissions().isEmpty())
                getVideoList();

            initialdate = new Date();
            recallservice();
        }
        return rootview;
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_readermedialist;
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_read_external_storage) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted) {
                opengallery();
            }
        }
    }

    public  void opengallery()
    {
        Intent intent = null;
        String type = null;
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            if(mediatype==1){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                 type="video/*";
            }else if(mediatype==2){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                type="audio/*";
            }else if(mediatype==3){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                type="image/*";
            }
        }
        else
        {
            if(mediatype==1){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                type="video/*";
            }else if(mediatype==2){
                intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                type="audio/*";
            }else if(mediatype==3){
                intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                type="image/*";
            }


        }
        Activity activity=getActivity();
        if(type!=null || activity!=null){
            intent.setType(type);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra("return-data", true);
            startActivityForResult(intent,REQUESTCODE_PICK);
        }

    }

    public void getVideoList()
    {
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
                    long file_size = file.length();
                    Log.e("Filesize ",""+file.getAbsolutePath()+" "+file_size);
                    if(file_size >= 0)
                    {
                        Date lastModDate = new Date(file.lastModified());
                        DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String outputdatestr = outputFormat.format(lastModDate);

                        if(! isexistinarraay(file.getName(),outputdatestr))
                        {
                            boolean ismedia=false;
                            video videoobj = null;
                            MediaExtractor extractor = new MediaExtractor();

                            if(mediatype == 3 && (common.getvideoextension(file.getAbsolutePath()).equalsIgnoreCase(".jpg") ||
                                    common.getvideoextension(file.getAbsolutePath()).equalsIgnoreCase(".png") ||
                                    common.getvideoextension(file.getAbsolutePath()).equalsIgnoreCase(".jpeg"))) {
                                videoobj = new video();
                                videoobj.setPath(file.getAbsolutePath());
                                videoobj.setExtension(common.getvideoextension(file.getAbsolutePath()));
                                videoobj.setName(file.getName());
                                videoobj.setCreatedate(outputdatestr);
                                videoobj.setLastmodifiedtime(file.lastModified());

                                videoobj.setmimetype("image/");
                                ismedia = true;
                            }
                            else {
                                try {
                                    //Adjust data source as per the requirement if file, URI, etc.
                                    String keymime = "";
                                    extractor.setDataSource(file.getAbsolutePath());
                                    int numTracks = extractor.getTrackCount();
                                    if (numTracks > 0) {
                                        for (int i = 0; i < numTracks; ++i) {
                                            MediaFormat format = extractor.getTrackFormat(i);
                                            String mime = format.getString(MediaFormat.KEY_MIME);

                                            if(i ==0)
                                                keymime = mime;

                                            if (mediatype == 2 && keymime.startsWith("audio/")) {
                                                if (format.containsKey(MediaFormat.KEY_DURATION)) {

                                                    videoobj = setvideoaudiodata(file,outputdatestr ,format,keymime);

                                                    ismedia = true;

                                                }
                                            }else if(mediatype == 1 && mime.startsWith("video/")){

                                                  videoobj = setvideoaudiodata(file,outputdatestr ,format,keymime);
                                                  ismedia = true;
                                            }
                                            else if (mime.startsWith("image/")) {
                                                ismedia = true;
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    //Release stuff
                                    extractor.release();
                                }
                            }
                            //String md= md5.calculatemd5(file);
                            //videoobj.setMd5(""+md);
                            if( videoobj != null && ismedia)
                                   arrayvideolist.add(videoobj);
                        }
                    }
                    else
                    {
                        common.deletefile(file.getAbsolutePath());
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
                        resetmedialist();
                    }
                });
            }
        }).start();
    }

    public void resetmedialist(){

        if (arrayvideolist != null && arrayvideolist.size() > 0)
        {
            for (int i = 0; i < arrayvideolist.size(); i++) {
                //String status = arrayvideolist.get(i).getMediastatus();
                if(! arrayvideolist.get(i).getMediastatus().equalsIgnoreCase(config.sync_complete))
                {
                    String[] getdata = getlocalkey(common.getfilename(arrayvideolist.get(i).getPath()));
                    String status = getdata[0];
                    String localkey = getdata[2];

                    if (getdata[0].isEmpty() || getdata[0].equalsIgnoreCase("null")) {
                        arrayvideolist.get(i).setMediastatus(config.sync_pending);

                        databasemanager mdbhelper=null;
                        if (mdbhelper == null) {
                            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                            mdbhelper.createDatabase();
                        }

                        try {
                            mdbhelper.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            String syncdate[] = common.getcurrentdatewithtimezone();
                            mdbhelper.insertstartvideoinfo("",arrayvideolist.get(i).getmimetype(),common.getfilename(arrayvideolist.get(i).getPath()),"",
                                    "","","",syncdate[0]  , "",
                                    "","","","","",
                                    "","",config.sync_pending,"","","","");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            mdbhelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if (status.equalsIgnoreCase(config.sync_inprogress) || status.equalsIgnoreCase(config.sync_pending)
                                || status.equalsIgnoreCase(config.sync_notfound))
                    {
                        arrayvideolist.get(i).setMediastatus(status);
                        arrayvideolist.get(i).setLocalkey(status);
                    }
                    else if (status.equalsIgnoreCase(config.sync_complete))
                    {
                        arrayvideolist.get(i).setMediastatus(status);
                        arrayvideolist.get(i).setLocalkey(status);
                        arrayvideolist.get(i).setVideostarttransactionid(getdata[1]);
                    }
                }
            }
        }

        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void recallservice(){
        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {
                resetmedialist();
                myhandler.postDelayed(this, 5000);
            }
        };
        myhandler.post(myrunnable);
    }

    public String[] getlocalkey(String filename)
    {
        databasemanager mdbhelper=null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        String[] getdata = mdbhelper.getreaderlocalkeybylocation(filename);

        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return getdata;
    }


    public void removehandler()
    {
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);
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
        Log.e("arraylistsize",""+arrayvideolist.size());
        adapter = new adapterreadermedialist(getActivity(),arrayvideolist, new adapteritemclick() {
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
    public void setAdapter(final video videoobj, int type)
    {
        if(type == 1)
        {
            if(videoobj.getmimetype().startsWith("image/")){
                File file = new File(videoobj.getPath());
           //     Uri uri = Uri.parse("file://"+videoobj.getPath());
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("image/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(Intent.createChooser(share, "Share photo"));
            }else if(videoobj.getmimetype().startsWith("audio/")){
                File file = new File(videoobj.getPath());
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("audio/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(Intent.createChooser(share, "Share audio"));

            }else if(videoobj.getmimetype().startsWith("video/")){
                File file = new File(videoobj.getPath());
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("video/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(Intent.createChooser(share, "Share video"));
            }

        }
        else if(type == 2)
        {
            if(videoobj.getmimetype().startsWith("image/")){
                showalertdialog(videoobj,getActivity().getResources().getString(R.string.dlt_cnfm_photo));

            }else if(videoobj.getmimetype().startsWith("audio/")){
                showalertdialog(videoobj,getActivity().getResources().getString(R.string.dlt_cnfm_audio));

            }else if(videoobj.getmimetype().startsWith("video/")){
                showalertdialog(videoobj,getActivity().getResources().getString(R.string.delete_confirm_video));

            }
        }else if(type == 3){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            },500);
            //getVideoList();

        }else if(type == 4){
            if(videoobj.getmimetype().startsWith("image/")){
                xdata.getinstance().saveSetting("selectedphotourl",""+videoobj.getPath());
                imagereaderfragment phototabfrag = new imagereaderfragment();
                // phototabfrag.setdata(videoobj.getPath());
                gethelper().replaceFragment(phototabfrag, false, true);

            }else if(videoobj.getmimetype().startsWith("audio/")){
                xdata.getinstance().saveSetting("selectedaudiourl",""+videoobj.getPath());
                audioreaderfragment audiotabfrag = new audioreaderfragment();
                //  audiotabfrag.setdata(videoobj.getPath());
                gethelper().replaceFragment(audiotabfrag, false, true);

            }else if(videoobj.getmimetype().startsWith("video/")){
                xdata.getinstance().saveSetting("selectedvideourl",""+videoobj.getPath());
                videoreaderfragment readervideofragment=new videoreaderfragment();
                gethelper().replaceFragment(readervideofragment, false, true);
            }

        }
    }

    public void showalertdialog(final video videoobj, String message){
        new AlertDialog.Builder(getActivity(),R.style.customdialogtheme)
                .setTitle("Alert!!")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(videoobj.getPath());
                        if (file.exists()) {
                            String type="video";
                            if(videoobj.getmimetype().startsWith("image/")){
                                type="image";
                            }else if(videoobj.getmimetype().startsWith("audio/")){
                                type="audio";
                            }else if(videoobj.getmimetype().startsWith("video/")){
                                type="video";
                            }

                            deletemediainfo(videoobj.getPath(),videoobj.getLocalkey());

                            if (file.delete()) {
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
    }

    public String deletemediainfo(String filepath,String localkey)
    {
        databasemanager mdbhelper=null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(! localkey.trim().isEmpty())
            mdbhelper.deletefrommetadatabylocalkey(localkey);

        mdbhelper.deletefromstartvideoinfobyfilename(common.getfilename(filepath));
        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return localkey;
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
                               // Toast.makeText(getActivity(),"Video upload successfully!",Toast.LENGTH_SHORT).show();
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
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
           /* case R.id.img_share_icon:
                if(VIDEO_URL != null && (! VIDEO_URL.isEmpty()))
                    common.sharevideo(getActivity(),VIDEO_URL);
                break;*/
            case R.id.img_menu:
                checkwritestoragepermission();
                break;
            case R.id.img_setting:
                //destroyvideoplayer();
                fragmentsettings fragmatriclist=new fragmentsettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
        }
    }

    private void checkwritestoragepermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }
            if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED  &&  ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED ) {

                opengallery();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    Toast.makeText(getActivity(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                        request_read_external_storage);
            }
        }
        else
        {
            opengallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_PICK) {
            if (resultCode == RESULT_OK) {
               Uri selectedvideouri = data.getData();

                try {
                    //VIDEO_URL=common.getUriRealPath(applicationviavideocomposer.getactivity(),selectedvideouri);
                  String  video_url = common.getpath(getActivity(), selectedvideouri);
                  setcopyvideo(video_url);

                } catch (Exception e) {
                    e.printStackTrace();
                    common.showalert(getActivity(), getResources().getString(R.string.file_uri_parse_error));
                    return;
                }

            }
        }
    }


    public video setvideoaudiodata(File file ,String outputdatestr,MediaFormat format, String mime ){

        video videoobj = new video();
        videoobj.setPath(file.getAbsolutePath());
        videoobj.setExtension(common.getvideoextension(file.getAbsolutePath()));
        videoobj.setName(file.getName());
        videoobj.setCreatedate(outputdatestr);
        videoobj.setLastmodifiedtime(file.lastModified());
        videoobj.setmimetype(mime);

        if (format.containsKey(MediaFormat.KEY_DURATION)) {
            long seconds = format.getLong(MediaFormat.KEY_DURATION);
            seconds = seconds / 1000000;
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
            long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
            videoobj.setDuration("" + common.appendzero(minute) + ":" + common.appendzero(second) + "");
            if (second > 0 || minute > 0 || hours > 0) {
                videoobj.setDuration("" + common.appendzero(hours) + ":" + common.appendzero(minute) + ":" + common.appendzero(second) + "");
            }
        }


        return videoobj;
    }

    public int settype(int type){
        if(type==1){
            mediatype=1;
            Log.e("video","video");
        }else if(type==2){
            mediatype=2;
            Log.e("audio","audio");
        }else if(type==3){
            mediatype=3;
            Log.e("image","image");
        }
        return mediatype;
    }
}
