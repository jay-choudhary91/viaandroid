package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.adaptermediagrid;
import com.cryptoserver.composer.adapter.adaptermedialist;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.folder;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
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

public class fragmentmedialist extends basefragment implements View.OnClickListener {

    @BindView(R.id.rv_medialist_list)
    RecyclerView recyclerviewlist;
    @BindView(R.id.rv_medialist_grid)
    RecyclerView recyclerviewgrid;
    @BindView(R.id.lay_gridstyle)
    RelativeLayout lay_gridstyle;
    @BindView(R.id.lay_liststyle)
    RelativeLayout lay_liststyle;
    @BindView(R.id.layout_sectionsearch)
    RelativeLayout layout_sectionsearch;

    @BindView(R.id.txt_mediatype_b)
    TextView txt_mediatype_b;
    @BindView(R.id.txt_mediatype_a)
    TextView txt_mediatype_a;
    @BindView(R.id.txt_mediatype_c)
    TextView txt_mediatype_c;
    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.txt_searchcancel)
    TextView txt_searchcancel;

    @BindView(R.id.edt_searchitem)
    EditText edt_searchitem;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_header_search)
    ImageView img_header_search;
    @BindView(R.id.img_search_editicon)
    ImageView img_search_editicon;

    int selectedstyletype=1,selectedmediatype=-1,listviewheight=0;
    RelativeLayout listlayout;
    boolean touched =false;
    private Handler myhandler;
    private Runnable myrunnable;
    boolean isinbackground=false;
    Date initialdate;

    View rootview = null;
    private static final int request_permissions = 1;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adaptermedialist adaptermedialist;
    adaptermediagrid adaptergrid;
    private RecyclerTouchListener onTouchListener;
    int request_take_gallery_video = 101,audiocount=0,videocount=0,imagecount=0;

    private static final int request_read_external_storage = 1;
    private Uri selectedimageuri =null;
    private String selectedvideopath ="";
    private BroadcastReceiver medialistitemaddreceiver;
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
    public void onStart() {
        super.onStart();
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
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(medialistitemaddreceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(myhandler != null && myhandler != null)
            myhandler.removeCallbacks(myrunnable);
    }


    @Override
    public void onResume() {
        super.onResume();
        isinbackground=false;
        recyclerviewlist.addOnItemTouchListener(onTouchListener);
    }

    public void requestpermissions()
    {
        if (common.getstoragedeniedpermissions().isEmpty()) {
            // All permissions are granted
            fetchmedialistfromdirectory();
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
                showselectedmediatypeitems();
                fetchmedialistfromdirectory();
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
            recyclerviewlist.setLayoutManager(layoutManager);
            ((DefaultItemAnimator) recyclerviewlist.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerviewlist.getItemAnimator().setChangeDuration(0);
            //DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
            //itemDecor.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.devidercolor));
            //recyclerviewlist.addItemDecoration(itemDecor);

            selectedstyletype=1;
            lay_gridstyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
            lay_liststyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.grey_xxx));
            lay_gridstyle.setOnClickListener(this);
            lay_liststyle.setOnClickListener(this);
            txt_mediatype_a.setOnClickListener(this);
            txt_mediatype_b.setOnClickListener(this);
            txt_mediatype_c.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_folder.setOnClickListener(this);
            img_header_search.setOnClickListener(this);
            txt_searchcancel.setOnClickListener(this);

            img_camera.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.VISIBLE);
            img_header_search.setVisibility(View.VISIBLE);

            try {
                DrawableCompat.setTint(img_header_search.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                        , R.color.blue));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                DrawableCompat.setTint(img_search_editicon.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                        , R.color.grey_xxx));
            }catch (Exception e)
            {
                e.printStackTrace();
            }


            onTouchListener = new RecyclerTouchListener(getActivity(), recyclerviewlist);
            onTouchListener.setSwipeOptionViews(R.id.img_slide_delete).setSwipeable( R.id.rl_rowfg,R.id.bottom_wraper,
            new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                @Override
                public void onSwipeOptionClicked(int viewID, int position) {

                }
            });

            edt_searchitem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    filter(editable.toString());
                }
            });

            recyclerviewgrid.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            recyclerviewgrid.setLayoutManager(mLayoutManager);

            launchbottombarfragment();
            if (common.getstoragedeniedpermissions().isEmpty()) {
                // All permissions are granted
                fetchmedialistfromdirectory();
            }

            recyclerviewlist.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            touched = true;
                            initialdate = new Date();
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
            resetmedialist();
            IntentFilter intentFilter = new IntentFilter(config.broadcast_medialistnewitem);
            medialistitemaddreceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    requestpermissions();
                }
            };
            applicationviavideocomposer.getactivity().registerReceiver(medialistitemaddreceiver, intentFilter);
        }
        return rootview;
    }

    private void filter(String text) {
        if(arrayvideolist != null && arrayvideolist.size() > 0)
        {
            if(text.trim().length() > 0)
            {
                //new array list that will hold the filtered data
                ArrayList<video> filterdnames = new ArrayList<>();
                //looping through existing elements
                for (video object : arrayvideolist) {
                    //if the existing elements contains the search input
                    if (object.getName().contains(text.toLowerCase()) && object.isDoenable()) {
                        //adding the element to filtered list
                        filterdnames.add(object);
                    }
                }
                //calling a method of the adapter class and passing the filtered list
                if(adaptergrid != null && adaptermedialist != null)
                {
                    adaptergrid.filterlist(filterdnames);
                    adaptermedialist.filterlist(filterdnames);
                }
            }
            else
            {
                setmediaadapter();
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.lay_gridstyle:
                selectedstyletype=1;
                lay_gridstyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                lay_liststyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.grey_xxx));
                recyclerviewlist.setVisibility(View.GONE);
                recyclerviewgrid.setVisibility(View.VISIBLE);
                break;
            case R.id.lay_liststyle:
                selectedstyletype=2;
                lay_gridstyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.grey_xxx));
                lay_liststyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                recyclerviewlist.setVisibility(View.VISIBLE);
                recyclerviewgrid.setVisibility(View.GONE);
                break;
            case R.id.txt_mediatype_a:
                showselecteditemincenter(txt_mediatype_a,1);
                break;
            case R.id.txt_mediatype_b:

                break;
            case R.id.txt_mediatype_c:
                showselecteditemincenter(txt_mediatype_c,3);
                break;
            case R.id.img_camera:
                launchbottombarfragment();
                break;
            case R.id.img_header_search:
                layout_sectionsearch.setVisibility(View.VISIBLE);
                edt_searchitem.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                edt_searchitem.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
                break;
            case R.id.txt_searchcancel:
                edt_searchitem.setText("");
                layout_sectionsearch.setVisibility(View.GONE);
                break;
            case R.id.img_dotmenu:
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
                break;

            case R.id.img_folder:
                myfolderfragment folderfragment=new myfolderfragment();
                gethelper().addFragment(folderfragment, false, true);
                break;


        }
    }

    public void showselecteditemincenter(TextView textView,int sectionnumber)
    {
        String selectedvalue=textView.getText().toString();
        if(textView.getText().toString().startsWith(config.item_video))
        {
            selectedmediatype=0;
        }
        else if(textView.getText().toString().startsWith(config.item_photo))
        {
            selectedmediatype=1;
        }
        else if(textView.getText().toString().startsWith(config.item_audio))
        {
            selectedmediatype=2;
        }

        config.selectedmediatype=selectedmediatype;
        if(sectionnumber == 1)
        {
            textView.setText(txt_mediatype_c.getText().toString());
            txt_mediatype_c.setText(txt_mediatype_b.getText().toString());
        }
        else if(sectionnumber == 3)
        {
            textView.setText(txt_mediatype_a.getText().toString());
            txt_mediatype_a.setText(txt_mediatype_b.getText().toString());
        }
        txt_mediatype_b.setText(selectedvalue);
        showselectedmediatypeitems();
        /*updatecounter(txt_mediatype_a);
        updatecounter(txt_mediatype_b);
        updatecounter(txt_mediatype_c);*/
    }

    public void showselectedmediatypeitems()
    {
        String checkitem="";
        if(selectedmediatype == 0)
        {
            checkitem="video";
        }
        else if(selectedmediatype == 1)
        {
            checkitem="image";
        }
        else if(selectedmediatype == 2)
        {
            checkitem="audio";
        }
        audiocount=0;videocount=0;imagecount=0;
        for(int i=0;i<arrayvideolist.size();i++)
        {
            arrayvideolist.get(i).setDoenable(false);
            if(arrayvideolist.get(i).getmimetype().startsWith("video"))
            {
                videocount++;
            }
            else if(arrayvideolist.get(i).getmimetype().startsWith("image"))
            {
                imagecount++;
            }
            else if(arrayvideolist.get(i).getmimetype().startsWith("audio"))
            {
                audiocount++;
            }
            if(arrayvideolist.get(i).getmimetype().contains(checkitem))
                arrayvideolist.get(i).setDoenable(true);

        }

        if(adaptermedialist != null && adaptergrid != null)
        {
            adaptermedialist.notifyDataSetChanged();
            adaptergrid.notifyDataSetChanged();
            recyclerviewlist.smoothScrollToPosition(0);
            recyclerviewgrid.smoothScrollToPosition(0);
        }

        updatecounter(txt_mediatype_a);
        updatecounter(txt_mediatype_b);
        updatecounter(txt_mediatype_c);
    }

    public void updatecounter(TextView textView)
    {
        if(textView.getText().toString().startsWith(config.item_video))
        {
            textView.setText(config.item_video);
            if(videocount > 0)
                textView.setText(config.item_video+" ("+videocount+")");
        }
        else if(textView.getText().toString().startsWith(config.item_audio))
        {
            textView.setText(config.item_audio);
            if(audiocount > 0)
                textView.setText(config.item_audio+" ("+audiocount+")");
        }
        else if(textView.getText().toString().startsWith(config.item_photo))
        {
            textView.setText(config.item_photo);
            if(imagecount > 0)
                textView.setText(config.item_photo+" ("+imagecount+")");
        }
    }

    public void showfolderdialog(final String sourcefilepath)
    {
        File rootdir = new File(config.rootdir);
        if(! rootdir.exists())
            return;

        ArrayList<String> itemname=new ArrayList<>();
        final ArrayList<String> itempath=new ArrayList<>();
        File[] files = rootdir.listFiles();
        for (File file : files)
        {
            if((! file.getName().equalsIgnoreCase("cache")) && (! file.getName().equalsIgnoreCase(config.allmedia)))
            {
                itemname.add(file.getName());
                itempath.add(file.getAbsolutePath());
            }
        }

        if(itemname.size() == 0)
        {
            Toast.makeText(applicationviavideocomposer.getactivity(),"No folder created!",Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = itemname.toArray(new String[itemname.size()]);
        new AlertDialog.Builder(applicationviavideocomposer.getactivity())
                .setTitle("Select folder")
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {

                        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                        final int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                String folderpath=itempath.get(selectedPosition);
                                common.copyfile(new File(sourcefilepath),new File(folderpath));
                                // Do something useful withe the position of the selected radio button

                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismisswaitdialog();

                                        if(adaptermedialist != null && arrayvideolist.size() > 0)
                                            adaptermedialist.notifyDataSetChanged();

                                        if(adaptergrid != null && arrayvideolist.size() > 0)
                                            adaptergrid.notifyDataSetChanged();

                                        if(dialog != null)
                                            dialog.dismiss();
                                    }
                                });
                            }
                        }).start();
                    }
                })
                .show();
    }

    public void removehandler()
    {
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);
    }

    public void fetchmedialistfromdirectory()
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
                    long filelength = file.length();
                    int file_size = Integer.parseInt(String.valueOf(filelength/1024));
                    Log.e("Filesize ",""+file.getAbsolutePath()+" "+file_size);
                    if(file_size > 0)
                    {
                        Date lastModDate = new Date(file.lastModified());
                        DateFormat outputFormat = new SimpleDateFormat("MM-dd-yy");
                        String time = new SimpleDateFormat("hh:mm:ss aa").format(lastModDate);
                        String outputdatestr = outputFormat.format(lastModDate);

                        if(! isexistinarraay(file.getName(),outputdatestr))
                        {
                            video videoobj=new video();
                            videoobj.setPath(file.getAbsolutePath());
                            videoobj.setExtension(common.getvideoextension(file.getAbsolutePath()));
                            videoobj.setName(file.getName());
                            videoobj.setCreatedate(outputdatestr);
                            videoobj.setCreatetime(time);
                            videoobj.setLastmodifiedtime(file.lastModified());
                            videoobj.setDoenable(false);

                            boolean ismedia=false;
                            MediaExtractor extractor = new MediaExtractor();
                            try {

                                if(videoobj.getPath().contains(".jpg") || videoobj.getPath().contains(".png") || videoobj.getPath().contains(".jpeg"))
                                {
                                    videoobj.setmimetype("image/");
                                    String[] getdata = getlocalkey(common.getfilename(videoobj.getPath()));
                                    videoobj.setMediastatus(getdata[0]);
                                    videoobj.setVideostarttransactionid(getdata[1]);
                                    videoobj.setLocalkey(getdata[2]);
                                    videoobj.setMediatitle(getdata[4]);
                                    videoobj.setMedianotes(getdata[5]);
                                    //videoobj.setCreatedatetime(getdata[6]);

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
                                                String[] getdata = getlocalkey(common.getfilename(videoobj.getPath()));
                                                videoobj.setMediastatus(getdata[0]);
                                                videoobj.setVideostarttransactionid(getdata[1]);
                                                videoobj.setLocalkey(getdata[2]);
                                                videoobj.setThumbnailpath(getdata[3]);
                                                videoobj.setMediatitle(getdata[4]);
                                                videoobj.setMedianotes(getdata[5]);
                                                //videoobj.setCreatedatetime(getdata[6]);

                                                if (format.containsKey(MediaFormat.KEY_DURATION)) {
                                                    long seconds = format.getLong(MediaFormat.KEY_DURATION);
                                                    seconds=seconds/1000000;
                                                    int day = (int) TimeUnit.SECONDS.toDays(seconds);
                                                    long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
                                                    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
                                                    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
                                                    if(second == 0)
                                                        second=1;

                                                    videoobj.setDuration(""+common.appendzero(hours)+":"+common.appendzero(minute)+":"+common.appendzero(second)+"");

                                                    ismedia=true;
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
                            if(ismedia)
                            {
                                int gridviewwidth=(arrayvideolist.size() % 2) * 100 + 300 + (int) (Math.random() * 300);
                                videoobj.setGriditemheight(gridviewwidth);
                                arrayvideolist.add(videoobj);
                            }
                        }
                    }
                    else
                    {
                        try {
                            common.delete(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                        if (arrayvideolist != null && arrayvideolist.size() > 0)
                        {
                            getallmedialistfromdb();

                           if(adaptermedialist != null && arrayvideolist.size() > 0)
                                adaptermedialist.notifyDataSetChanged();

                           if(adaptergrid != null && arrayvideolist.size() > 0)
                               adaptergrid.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();
    }

    public void setmediaadapter()
    {
        recyclerviewgrid.post(new Runnable() {
            @Override
            public void run() {
                listviewheight=recyclerviewgrid.getHeight();
                adaptermedialist = new adaptermedialist(getActivity(),arrayvideolist, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {
                    }
                    @Override
                    public void onItemClicked(Object object, int type) {
                        video videoobj=(video)object;
                        setAdapter(videoobj,type);
                    }

                },listviewheight);
                recyclerviewlist.setAdapter(adaptermedialist);

                adaptergrid = new adaptermediagrid(getActivity(),arrayvideolist, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {
                    }
                    @Override
                    public void onItemClicked(Object object, int type) {
                        video videoobj=(video)object;
                        setAdapter(videoobj,type);
                    }

                });
                recyclerviewgrid.setAdapter(adaptergrid);
                if(selectedmediatype != config.selectedmediatype)
                {
                    selectedmediatype=config.selectedmediatype;
                    String mediatype="";
                    switch (selectedmediatype)
                    {
                        case 0:
                            mediatype=config.item_video;
                        break;

                        case 1:
                            mediatype=config.item_photo;
                        break;

                        case 2:
                            mediatype=config.item_audio;
                        break;
                    }
                    if(txt_mediatype_a.getText().toString().startsWith(mediatype))
                    {
                        showselecteditemincenter(txt_mediatype_a,1);
                    }
                    else if(txt_mediatype_c.getText().toString().startsWith(mediatype))
                    {
                        showselecteditemincenter(txt_mediatype_c,3);
                    }
                    else
                    {
                        showselectedmediatypeitems();
                    }
                }
                else
                {
                    showselectedmediatypeitems();
                }
            }
        });
    }

    public void resetmedialist(){

        if(myhandler != null && myhandler != null)
            myhandler.removeCallbacks(myrunnable);

        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {
               if (arrayvideolist != null && arrayvideolist.size() > 0)
               {
                       getallmedialistfromdb();

                       /*if(adaptermedialist != null && arrayvideolist.size() > 0)
                            adaptermedialist.notifyDataSetChanged();*/

                 /* if(adaptergrid != null && arrayvideolist.size() > 0)
                       adaptergrid.notifyDataSetChanged();*/
               }
                myhandler.postDelayed(this, 3000);
            }
        };
        myhandler.post(myrunnable);
    }


    public void getallmedialistfromdb()
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

        Cursor cursor = mdbhelper.getallmediastartdata();
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            do{

                String location = "" + cursor.getString(cursor.getColumnIndex("location"));
                String sync_status = "" + cursor.getString(cursor.getColumnIndex("sync_status"));
                String videostarttransactionid = "" + cursor.getString(cursor.getColumnIndex("videostarttransactionid"));
                String localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                String thumbnailurl = "" + cursor.getString(cursor.getColumnIndex("thumbnailurl"));
                String media_name = "" + cursor.getString(cursor.getColumnIndex("media_name"));
                String media_notes = "" + cursor.getString(cursor.getColumnIndex("media_notes"));
                for(int i=0;i<arrayvideolist.size();i++)
                {
                    if(common.getfilename(arrayvideolist.get(i).getPath()).equalsIgnoreCase(location))
                    {
                        arrayvideolist.get(i).setVideostarttransactionid(videostarttransactionid);
                        arrayvideolist.get(i).setThumbnailpath(thumbnailurl);
                        arrayvideolist.get(i).setMediatitle(media_name);
                        arrayvideolist.get(i).setMedianotes(media_notes);
                        break;
                    }
                }

            }while(cursor.moveToNext());
        }

        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String[] getlocalkey(String filename)
    {
        String localkey="";
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

        String[] getdata = mdbhelper.getlocalkeybylocation(filename);


        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return getdata;
    }

    public String deletemediainfo(String localkey)
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
        mdbhelper.deletefrommetadatabylocalkey(localkey);
        mdbhelper.deletefromstartvideoinfobylocalkey(localkey);
        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return localkey;
    }

    public void updatemedialocation(String transactionid,String mediapath)
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
        String filename=common.getfilename(mediapath);
        mdbhelper.updatemedialocationname(transactionid,filename);
        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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
                framemetricssettings fragmatriclist=new framemetricssettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
            case R.id.img_add_icon:
                launchbottombarfragment();
                break;
        }
    }

    public void launchbottombarfragment()
    {
        /*bottombarfragment fragbottombar=new bottombarfragment();
        fragbottombar.setData(new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                requestpermissions();
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
        gethelper().replaceFragment(fragbottombar, false, true);*/

        composeoptionspagerfragment fragbottombar=new composeoptionspagerfragment();
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
        recyclerviewlist.removeOnItemTouchListener(onTouchListener);
    }

    public void setAdapter(final video videoobj, int type)
    {
        if(type == 1)
        {
            if(videoobj.getmimetype().startsWith("image/")){
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(videoobj.getPath()));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("image/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(Intent.createChooser(share, "Share photo"));
            }else if(videoobj.getmimetype().startsWith("audio/")){
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(videoobj.getPath()));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("audio/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(Intent.createChooser(share, "Share audio"));

            }else if(videoobj.getmimetype().startsWith("video/")){
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(videoobj.getPath()));
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
            //arrayvideolist.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showselectedmediatypeitems();
                    if(! videoobj.getVideostarttransactionid().isEmpty())
                        updatemedialocation(videoobj.getVideostarttransactionid(),videoobj.getPath());
                }
            },500);

            //fetchmedialistfromdirectory();

        }else if(type == 4){
            if(videoobj.getmimetype().startsWith("image/")){
                xdata.getinstance().saveSetting("selectedphotourl",""+videoobj.getPath());
                imagereaderfragment phototabfrag = new imagereaderfragment();
                phototabfrag.setdata(mcontrollernavigator);
                gethelper().replaceFragment(phototabfrag, false, true);

            }else if(videoobj.getmimetype().startsWith("audio/")){
                xdata.getinstance().saveSetting("selectedaudiourl",""+videoobj.getPath());
                audioreaderfragment audiotabfrag = new audioreaderfragment();
                audiotabfrag.setdata(mcontrollernavigator);
                gethelper().replaceFragment(audiotabfrag, false, true);

            }else if(videoobj.getmimetype().startsWith("video/")){

                xdata.getinstance().saveSetting("selectedvideourl",""+videoobj.getPath());
                videoreaderfragment readervideofragment=new videoreaderfragment();
                readervideofragment.setdata(mcontrollernavigator);
                gethelper().replaceFragment(readervideofragment, false, true);

               /* xdata.getinstance().saveSetting("selectedvideourl",""+videoobj.getPath());
                composervideoplayerfragment videoplayercomposerfragment = new composervideoplayerfragment();
                //videoplayercomposerfragment.setdata(videoobj.getPath());
                gethelper().replaceFragment(videoplayercomposerfragment, false, true);*/
            }

        }
        else if(type == 6){
            showfolderdialog(videoobj.getPath());
        }
    }

    adapteritemclick mcontrollernavigator=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int type) {

            if(type == 1)
            {
                if (arrayvideolist != null && arrayvideolist.size() > 0)
                {
                    getallmedialistfromdb();
                    if(adaptermedialist != null && arrayvideolist.size() > 0)
                        adaptermedialist.notifyDataSetChanged();
                }
            }
            else if(type == 2)
            {
                String filepath=(String)object;
                if(arrayvideolist.size() > 0)
                {
                    for(int i=0;i<arrayvideolist.size();i++)
                    {
                        if(filepath.equalsIgnoreCase(arrayvideolist.get(i).getPath()))
                        {
                            video videoobj=arrayvideolist.get(i);
                            File fdelete = new File(videoobj.getPath());
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    String localkey=videoobj.getLocalkey();
                                    System.out.println("file Deleted :" + videoobj.getPath());
                                    arrayvideolist.remove(videoobj);
                                    if(! localkey.trim().isEmpty())
                                        deletemediainfo(localkey);

                                } else {
                                    System.out.println("file not Deleted :" + videoobj.getPath());
                                }
                            }
                            break;
                        }
                    }
                    showselectedmediatypeitems();
                }
            }
        }
    };

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

    public void showalertdialog(final video videoobj, String message){
        new AlertDialog.Builder(getActivity(),R.style.customdialogtheme)
                .setTitle("Alert!!")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File fdelete = new File(videoobj.getPath());
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                String localkey=videoobj.getLocalkey();
                                System.out.println("file Deleted :" + videoobj.getPath());
                                arrayvideolist.remove(videoobj);
                                dialog.dismiss();
                                if(! localkey.trim().isEmpty())
                                    deletemediainfo(localkey);

                            } else {
                                System.out.println("file not Deleted :" + videoobj.getPath());
                            }
                        }
                        showselectedmediatypeitems();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


}
