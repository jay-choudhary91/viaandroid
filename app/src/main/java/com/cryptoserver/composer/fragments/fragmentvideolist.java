package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.adaptervideolist;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 6/8/18.
 */

public class fragmentvideolist extends basefragment {

    @BindView(R.id.rv_videolist)
    RecyclerView recyrviewvideolist;

    View rootview = null;
    private static final int request_permissions = 1;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adaptervideolist adapter;
    private RecyclerTouchListener onTouchListener;
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
    public void onResume() {
        super.onResume();
        requestPremission();
        recyrviewvideolist.addOnItemTouchListener(onTouchListener);
    }

    public void requestPremission()
    {
        String[] neededpermissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        List<String> deniedpermissions = new ArrayList<>();
        for (String permission : neededpermissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                deniedpermissions.add(permission);
            }
        }
        if (deniedpermissions.isEmpty()) {
            // All permissions are granted
            getVideoList();
        } else {
            String[] array = new String[deniedpermissions.size()];
            array = deniedpermissions.toArray(array);
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
                getVideoList();
            } else {
                Toast.makeText(getActivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this,rootview);

            adapter = new adaptervideolist(getActivity(),arrayvideolist, new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {

                }

                @Override
                public void onItemClicked(Object object, int type) {
                    final video videoobj=(video)object;
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
                                })
                                .show();
                    }else if(type == 3){

                        getVideoList();

                    }else if(type == 4){

                        fullscreenvideofragment fullscreenvideofragment = new fullscreenvideofragment();
                        fullscreenvideofragment.setdata(videoobj.getPath());
                        gethelper().replaceFragment(fullscreenvideofragment, false, true);

                    }
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyrviewvideolist.setLayoutManager(mLayoutManager);
            ((DefaultItemAnimator) recyrviewvideolist.getItemAnimator()).setSupportsChangeAnimations(false);
            recyrviewvideolist.getItemAnimator().setChangeDuration(0);
            recyrviewvideolist.setAdapter(adapter);
        }

        onTouchListener = new RecyclerTouchListener(getActivity(), recyrviewvideolist);
        onTouchListener
                .setSwipeOptionViews(R.id.btn_edit)
                .setSwipeable( R.id.rl_rowfg,R.id.bottom_wraper, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {


                        arrayvideolist.get(position).setSelected(true);
                        adapter.notifyDataSetChanged();
                        Log.e("selected Position = " ,""+ position);

                    }
                });


        /*recyrviewvideolist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hidekeyboard();
                return false;
            }
        });*/

        return rootview;
    }

    public void getVideoList()
    {
        arrayvideolist.clear();

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                File videodir = new File(config.videodir);
                if(! videodir.exists())
                    return;

                File[] files = videodir.listFiles();
                Arrays.sort( files, new Comparator()
                {
                    public int compare(Object o1, Object o2) {
                        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                            return -1;
                        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }
                });

                for (File file : files)
                {
                    video videoobj=new video();
                    Date lastModDate = new Date(file.lastModified());
                    DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String outputDateStr = outputFormat.format(lastModDate);

                    videoobj.setPath(file.getAbsolutePath());
                    videoobj.setName(file.getName());
                    videoobj.setCreatedate(outputDateStr);

                    boolean isVideo=true;
                    MediaExtractor extractor = new MediaExtractor();
                    try {
                        //Adjust data source as per the requirement if file, URI, etc.
                        extractor.setDataSource(file.getAbsolutePath());
                        int numTracks = extractor.getTrackCount();
                        if(numTracks > 0)
                        {
                            for (int i = 0; i < numTracks; ++i) {
                                MediaFormat format = extractor.getTrackFormat(i);
                                String mime = format.getString(MediaFormat.KEY_MIME);
                                if (mime.startsWith("video/")) {
                                    if (format.containsKey(MediaFormat.KEY_DURATION)) {
                                        long seconds = format.getLong(MediaFormat.KEY_DURATION);
                                        seconds=seconds/1000000;
                                        int day = (int) TimeUnit.SECONDS.toDays(seconds);
                                        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
                                        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
                                        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
                                        videoobj.setDuration(""+common.appendzero(minute)+":"+common.appendzero(second)+"");
                                        if(hours > 0)
                                            videoobj.setDuration(""+common.appendzero(hours)+":"+common.appendzero(minute)+":"+common.appendzero(second)+"");

                                    }
                                }
                            }
                        }
                        else
                        {
                            isVideo=false;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        //Release stuff
                        extractor.release();
                    }
                    //String md= md5.calculatemd5(file);
                    //videoobj.setMd5(""+md);
                    if(isVideo)
                        arrayvideolist.add(videoobj);

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
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
}
