package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.adaptermediagrid;
import com.deeptruth.app.android.adapter.adaptermedialist;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.mediainfotablefields;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ${matraex} on 6/8/18.
 */

public class fragmentmedialist extends basefragment implements View.OnClickListener, View.OnTouchListener {

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

    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;

    @BindView(R.id.txt_mediatype_b)
    TextView txt_mediatype_b;
    @BindView(R.id.txt_mediatype_a)
    TextView txt_mediatype_a;
    @BindView(R.id.txt_mediatype_c)
    TextView txt_mediatype_c;
    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.img_settings)
    ImageView img_settings;
    @BindView(R.id.txt_searchcancel)
    TextView txt_searchcancel;
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;

    @BindView(R.id.edt_searchitem)
    EditText edt_searchitem;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_uploadmedia)
    ImageView img_uploadmedia;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_header_search)
    ImageView img_header_search;
    @BindView(R.id.img_search_editicon)
    ImageView img_search_editicon;
    @BindView(R.id.btn_gallerylist)
    ImageView btn_gallerylist;
    @BindView(R.id.btn_gridlist)
    ImageView btn_gridlist;

    int selectedstyletype=1,selectedmediatype=-1,listviewheight=0,dataupdator=0;
    RelativeLayout listlayout;
    private Handler myhandler;
    private Runnable myrunnable;
    boolean isinbackground=false;
    boolean shouldlaunchcomposer=true;

    View rootview = null;
    private static final int request_permissions = 1;
    ArrayList<video> arraymediaitemlist = new ArrayList<>();
    adaptermedialist adaptermedialist;
    adaptermediagrid adaptermediagrid;
    private RecyclerTouchListener onTouchListener;
    private static final int request_read_external_storage = 1;
    private BroadcastReceiver medialistitemaddreceiver;
    private BroadcastReceiver broadcastmediauploaded;
    private int REQUESTCODE_PICK=201,audiocount=0,videocount=0,imagecount=0;
    private FFmpeg ffmpeg;

    Handler handler = new Handler();
    int numberOfTaps = 0;
    long lastTapTimeMs = 0;
    long touchDownMs = 0;
    // Called just after any media uploaded
    public void registerbroadcastmediauploaded()
    {
        IntentFilter intentFilter = new IntentFilter(config.composer_service_getencryptionmetadata);
        broadcastmediauploaded = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                getallmedialistfromdb();
                if(adaptermedialist != null && arraymediaitemlist.size() > 0)
                    adaptermedialist.notifyitems(arraymediaitemlist);

                if(adaptermediagrid != null && arraymediaitemlist.size() > 0)
                    adaptermediagrid.notifyDataSetChanged();
            }
        };
        applicationviavideocomposer.getactivity().registerReceiver(broadcastmediauploaded, intentFilter);
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_medialist;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        applicationviavideocomposer.getactivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this,parent);
    }


    @Override
    public void onStart() {
        super.onStart();
        registerbroadcastmediauploaded();
    }

    @Override
    public void onStop() {
        super.onStop();
        isinbackground=true;
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(broadcastmediauploaded);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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

        if(myhandler != null)
            myhandler.removeCallbacks(myrunnable);
    }


    @Override
    public void onResume() {
        super.onResume();
      //  setheadermargine();
        gethelper().drawerenabledisable(false);
        isinbackground=false;
    }

    public void requestpermissions()
    {
        if (common.getstoragedeniedpermissions().isEmpty()) {
            // All permissions are granted
            fetchmedialistfromdirectory();
        } else {
            String[] array = new String[common.getstoragedeniedpermissions().size()];
            array = common.getstoragedeniedpermissions().toArray(array);
            ActivityCompat.requestPermissions(applicationviavideocomposer.getactivity(), array, request_permissions);
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
                arraymediaitemlist.clear();
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
           // setheadermargine();

            gethelper().drawerenabledisable(false);

            LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerviewlist.setLayoutManager(layoutManager);
            ((DefaultItemAnimator) recyclerviewlist.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerviewlist.getItemAnimator().setChangeDuration(0);
            lay_gridstyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
            lay_liststyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.audiowave));
            lay_gridstyle.setOnClickListener(this);
            lay_liststyle.setOnClickListener(this);
            txt_mediatype_a.setOnClickListener(this);
            txt_mediatype_b.setOnClickListener(this);
            txt_mediatype_c.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_folder.setOnClickListener(this);
            img_settings.setOnClickListener(this);
            img_header_search.setOnClickListener(this);
            txt_searchcancel.setOnClickListener(this);
            img_uploadmedia.setOnClickListener(this);
            listlayout.setOnClickListener(this);
            recyclerviewlist.setOnClickListener(this);
            recyclerviewgrid.setOnClickListener(this);

            img_camera.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.VISIBLE);
            img_header_search.setVisibility(View.VISIBLE);

            File folder = new File(xdata.getinstance().getSetting(config.selected_folder));
            txt_title_actionbarcomposer.setText(folder.getName());

            try {
                DrawableCompat.setTint(btn_gallerylist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                        , R.color.img_bg));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                DrawableCompat.setTint(btn_gridlist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                        , R.color.img_blue_bg));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

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


            onTouchListener = new RecyclerTouchListener(applicationviavideocomposer.getactivity(), recyclerviewlist);
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

            layout_mediatype.setOnTouchListener(this);

            IntentFilter intentFilter = new IntentFilter(config.broadcast_medialistnewitem);
            medialistitemaddreceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    requestpermissions();
                }
            };
            applicationviavideocomposer.getactivity().registerReceiver(medialistitemaddreceiver, intentFilter);

            selectedstyletype=1;
            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
                img_uploadmedia.setVisibility(View.VISIBLE);
                img_camera.setVisibility(View.GONE);
            }
            else
            {
                img_uploadmedia.setVisibility(View.GONE);
                img_camera.setVisibility(View.VISIBLE);
                if(common.isdevelopermodeenable())
                    img_settings.setVisibility(View.VISIBLE);

                if(shouldlaunchcomposer)
                    launchbottombarfragment();
            }

            loadffmpeglibrary();
            if (common.getstoragedeniedpermissions().isEmpty()) {
                // All permissions are granted
                fetchmedialistfromdirectory();
            }

            resetmedialist();
        }
        return rootview;
    }

    private void filter(String text) {
        if(arraymediaitemlist != null && arraymediaitemlist.size() > 0)
        {
            if(text.trim().length() > 0)
            {
                //new array list that will hold the filtered data
                ArrayList<video> filterdnames = new ArrayList<>();
                //looping through existing elements
                for (video object : arraymediaitemlist) {
                    //if the existing elements contains the search input
                    if (object.getMediatitle().contains(text.toLowerCase()) && object.isDoenable()) {
                        //adding the element to filtered list
                        filterdnames.add(object);
                    }
                }
                //calling a method of the adapter class and passing the filtered list
                if(adaptermediagrid != null && adaptermedialist != null)
                {
                    adaptermediagrid.filterlist(filterdnames);
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
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchDownMs = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_UP:
                if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
                {
                    handler.removeCallbacksAndMessages(null);
                    if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                        //it was not a tap
                        numberOfTaps = 0;
                        lastTapTimeMs = 0;
                        break;
                    }

                    if (numberOfTaps > 0
                            && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                        numberOfTaps += 1;
                    } else {
                        numberOfTaps = 1;
                    }

                    lastTapTimeMs = System.currentTimeMillis();

                    if (numberOfTaps == 10) {
                        // Toast.makeText(applicationviavideocomposer.getactivity(), "ten taps", Toast.LENGTH_SHORT).show();
                        //handle triple tap
                        /*if(! appdialog.isdialogshowing())
                            appdialog.showeggfeaturedialog(applicationviavideocomposer.getactivity());*/

                        if(! appdialog.isdialogshowing() && (! common.isdevelopermodeenable()))
                            appdialog.showeggfeaturedialog(applicationviavideocomposer.getactivity());
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.lay_gridstyle:
                selectedstyletype=1;
                lay_gridstyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                lay_liststyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.audiowave));
                recyclerviewlist.setVisibility(View.GONE);
                recyclerviewgrid.setVisibility(View.VISIBLE);
                try {
                    DrawableCompat.setTint(btn_gallerylist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                            , R.color.img_bg));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try {
                    DrawableCompat.setTint(btn_gridlist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                            , R.color.img_blue_bg));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                break;
            case R.id.lay_liststyle:
                selectedstyletype=2;
                lay_gridstyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.audiowave));
                lay_liststyle.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
                recyclerviewlist.setVisibility(View.VISIBLE);
                recyclerviewgrid.setVisibility(View.GONE);
                try {
                    DrawableCompat.setTint(btn_gridlist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                            , R.color.img_bg));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try {
                    DrawableCompat.setTint(btn_gallerylist.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                            , R.color.img_blue_bg));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
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
                hidekeyboard();
                layout_sectionsearch.setVisibility(View.GONE);
                break;
            case R.id.img_dotmenu:
                hidekeyboard();
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
                break;
            case R.id.img_uploadmedia:
                checkwritestoragepermission();
                break;
            case R.id.img_settings:
                {
                    framemetricssettings fragment=new framemetricssettings();
                    gethelper().replaceFragment(fragment, false, true);
                }
                break;
            case R.id.img_folder:
                hidekeyboard();
                myfolderfragment folderfragment=new myfolderfragment();
                folderfragment.setdata(new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {
                        resetmediaitemsadapter();
                    }
                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                gethelper().addFragment(folderfragment, false, true);
                break;
            case R.id.listlayout:
                hidekeyboard();
                break;
        }
    }

    private void resetmediaitemsadapter()
    {
        try
        {
            arraymediaitemlist.clear();
            if(adaptermediagrid != null)
                adaptermediagrid.notifyDataSetChanged();

            if(adaptermedialist != null)
                adaptermedialist.notifyitems(arraymediaitemlist);

            fetchmedialistfromdirectory();

        }catch (Exception e)
        {
            e.printStackTrace();
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
        for(int i = 0; i< arraymediaitemlist.size(); i++)
        {
            arraymediaitemlist.get(i).setDoenable(false);
            if(arraymediaitemlist.get(i).getmimetype().startsWith("video"))
            {
                videocount++;
            }
            else if(arraymediaitemlist.get(i).getmimetype().startsWith("image"))
            {
                imagecount++;
            }
            else if(arraymediaitemlist.get(i).getmimetype().startsWith("audio"))
            {
                audiocount++;
            }
            if(arraymediaitemlist.get(i).getmimetype().contains(checkitem))
                arraymediaitemlist.get(i).setDoenable(true);

        }

        if(adaptermedialist != null && adaptermediagrid != null)
        {
            adaptermedialist.notifyitems(arraymediaitemlist);
            adaptermediagrid.notifyDataSetChanged();
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
        File rootdir = new File(config.dirmedia);
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
            Toast.makeText(applicationviavideocomposer.getactivity(),"Please create a folder first",Toast.LENGTH_LONG).show();
            return;
        }

        String[] items = itemname.toArray(new String[itemname.size()]);
        new AlertDialog.Builder(applicationviavideocomposer.getactivity())
                .setTitle("Select folder")
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton)
                    {
                        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                        final int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    String folderpath=itempath.get(selectedPosition);
                                    if(! folderpath.equalsIgnoreCase(new File(sourcefilepath).getParent()))
                                    {
                                        if(common.movemediafile(new File(sourcefilepath),new File(folderpath)))
                                        {
                                            File destinationmediafile = new File(folderpath + File.separator + new File(sourcefilepath).getName());
                                            updatefilemediafolderdirectory(sourcefilepath,destinationmediafile.getAbsolutePath(),folderpath);
                                        }
                                    }
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        if(dialog != null)
                                            dialog.dismiss();

                                        progressdialog.dismisswaitdialog();
                                        resetmediaitemsadapter();
                                    }
                                });
                            }
                        }).start();
                    }
                }).show();
    }

    public void updatefilemediafolderdirectory(String sourcefile,String destinationfilepath,String destinationmediafolder)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            mdbhelper.updatefilemediafolderdir(sourcefile,destinationfilepath,destinationmediafolder);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
                databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                mdbhelper.createDatabase();

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
                        String id = "" + cursor.getString(cursor.getColumnIndex("id"));
                        //String videocompletedevicedate = "" + cursor.getString(cursor.getColumnIndex("videocompletedevicedate"));
                        String mediastartdevicedate = "" + cursor.getString(cursor.getColumnIndex("videostartdevicedate"));
                        String videostarttransactionid = "" + cursor.getString(cursor.getColumnIndex("videostarttransactionid"));
                        String localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                        String thumbnailurl = "" + cursor.getString(cursor.getColumnIndex("thumbnailurl"));
                        String media_name = "" + cursor.getString(cursor.getColumnIndex("media_name"));
                        String media_notes = "" + cursor.getString(cursor.getColumnIndex("media_notes"));
                        String color = "" + cursor.getString(cursor.getColumnIndex("color"));
                        String type = "" + cursor.getString(cursor.getColumnIndex("type"));
                        String header = "" + cursor.getString(cursor.getColumnIndex("header"));
                        String mediafilepath = "" + cursor.getString(cursor.getColumnIndex("mediafilepath"));
                        String mediaduration = "" + cursor.getString(cursor.getColumnIndex("mediaduration"));
                        String status = "" + cursor.getString(cursor.getColumnIndex("status"));

                        if(id.trim().isEmpty() || id.equalsIgnoreCase("null"))
                            id="0";

                        if(! isexistinarraay(mediafilepath))
                        {
                            video videoobject=new video();
                            videoobject.setId(Integer.parseInt(id));
                            videoobject.setPath(mediafilepath);
                            videoobject.setmimetype(type);
                            videoobject.setLocalkey(localkey);
                            videoobject.setVideostarttransactionid(videostarttransactionid);
                            videoobject.setThumbnailpath(thumbnailurl);
                            videoobject.setMediatitle(media_name);
                            videoobject.setMedianotes(media_notes);
                            videoobject.setMediacolor(color);
                            videoobject.setExtension(common.getvideoextension(location));
                            videoobject.setName(common.getfilename(location));
                            videoobject.setMediastatus(status);
                            videoobject.setDoenable(false);

                            if(mediaduration.trim().isEmpty() && (! type.equalsIgnoreCase("image")))
                            {
                                try {
                                    String duration = common.getvideotimefromurl(mediafilepath);
                                    videoobject.setDuration(duration);
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                videoobject.setDuration(mediaduration);
                            }

                            int gridviewwidth=(arraymediaitemlist.size() % 2) * 100 + 300 + (int) (Math.random() * 300);
                            videoobject.setGriditemheight(gridviewwidth);

                            try {
                                Date mediadatetime;
                                if(! mediastartdevicedate.trim().isEmpty())
                                {
                                    if(mediastartdevicedate.contains("T"))
                                    {
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                                        mediadatetime = format.parse(mediastartdevicedate);
                                    }
                                    else
                                    {
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH);
                                        mediadatetime = format.parse(mediastartdevicedate);
                                    }
                                    DateFormat datee = new SimpleDateFormat("z",Locale.getDefault());
                                    String localTime = datee.format(mediadatetime);

                                    final String filecreateddate = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH).format(mediadatetime);
                                    final String endtime = new SimpleDateFormat("hh:mm:ss.SS aa",Locale.ENGLISH).format(mediadatetime);
                                    videoobject.setCreatedate(filecreateddate);
                                    videoobject.setCreatetime((endtime)+ " "+localTime );
                                }

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            File file=new File(mediafilepath);
                            if(file.exists())
                                arraymediaitemlist.add(videoobject);
                        }
                    }while(cursor.moveToNext());
                }

                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Collections.sort( arraymediaitemlist, new Comparator()
                        {
                            public int compare(Object o1, Object o2) {
                                if (((video)o1).getId() > ((video)o2).getId()) {
                                    return -1;
                                } else if (((video)o1).getId() < ((video)o2).getId()) {
                                    return +1;
                                } else {
                                    return 0;
                                }
                            }
                        });


                        setmediaadapter();
                        if (arraymediaitemlist != null && arraymediaitemlist.size() > 0)
                        {
                            if(adaptermedialist != null && arraymediaitemlist.size() > 0)
                                adaptermedialist.notifyitems(arraymediaitemlist);

                            if(adaptermediagrid != null && arraymediaitemlist.size() > 0)
                                adaptermediagrid.notifyDataSetChanged();
                        }
                    }
                });

                try
                {
                    mdbhelper.close();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setmediaadapter()
    {
        recyclerviewgrid.post(new Runnable() {
            @Override
            public void run() {
                listviewheight=recyclerviewgrid.getHeight();
                adaptermedialist = new adaptermedialist(getActivity(), arraymediaitemlist, new adapteritemclick() {
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

                adaptermediagrid = new adaptermediagrid(getActivity(), arraymediaitemlist, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {
                    }
                    @Override
                    public void onItemClicked(Object object, int type) {
                        video videoobj=(video)object;
                        setAdapter(videoobj,type);
                    }

                });
                recyclerviewgrid.setAdapter(adaptermediagrid);
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

        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {

                dataupdator++;
               if(dataupdator >= 5)
               {
                   if (arraymediaitemlist != null && arraymediaitemlist.size() > 0)
                       getallmedialistfromdb();

                   dataupdator=0;
               }

               if(common.isdevelopermodeenable())
                   img_settings.setVisibility(View.VISIBLE);

                myhandler.postDelayed(this, 1000);
            }
        };
        myhandler.post(myrunnable);
    }


    public void getallmedialistfromdb()
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

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
                String videostarttransactionid = "" + cursor.getString(cursor.getColumnIndex("videostarttransactionid"));
                String localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                String thumbnailurl = "" + cursor.getString(cursor.getColumnIndex("thumbnailurl"));
                String media_name = "" + cursor.getString(cursor.getColumnIndex("media_name"));
                String media_notes = "" + cursor.getString(cursor.getColumnIndex("media_notes"));
                String color = "" + cursor.getString(cursor.getColumnIndex("color"));
                String mediaduration = "" + cursor.getString(cursor.getColumnIndex("mediaduration"));
                String status = "" + cursor.getString(cursor.getColumnIndex("status"));
                //String videocompletedevicedate = "" + cursor.getString(cursor.getColumnIndex("videocompletedevicedate"));
                String mediastartdevicedate = "" + cursor.getString(cursor.getColumnIndex("videostartdevicedate"));

                for(int i = 0; i< arraymediaitemlist.size(); i++)
                {
                    if(common.getfilename(arraymediaitemlist.get(i).getPath()).equalsIgnoreCase(location))
                    {
                        arraymediaitemlist.get(i).setVideostarttransactionid(videostarttransactionid);
                        arraymediaitemlist.get(i).setThumbnailpath(thumbnailurl);
                        arraymediaitemlist.get(i).setMediatitle(media_name);
                        arraymediaitemlist.get(i).setMedianotes(media_notes);
                        arraymediaitemlist.get(i).setMediacolor(color);
                        arraymediaitemlist.get(i).setLocalkey(localkey);
                        arraymediaitemlist.get(i).setMediastatus(status);
                        if(! mediaduration.trim().isEmpty())
                            arraymediaitemlist.get(i).setDuration(mediaduration);

                        try {
                            Date mediadatetime;
                            if(! mediastartdevicedate.trim().isEmpty())
                            {
                                if(mediastartdevicedate.contains("T"))
                                {
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                                    mediadatetime = format.parse(mediastartdevicedate);
                                }
                                else
                                {
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH);
                                    mediadatetime = format.parse(mediastartdevicedate);
                                }
                                DateFormat datee = new SimpleDateFormat("z",Locale.getDefault());
                                String localTime = datee.format(mediadatetime);

                                final String filecreateddate = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH).format(mediadatetime);
                                final String endtime = new SimpleDateFormat("hh:mm:ss.SS aa",Locale.ENGLISH).format(mediadatetime);
                                arraymediaitemlist.get(i).setCreatedate(filecreateddate);
                                arraymediaitemlist.get(i).setCreatetime((endtime)+ " "+localTime);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
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

    public void deletemediainfo(String localkey)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

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
    }

    public void updatemedialocation(String transactionid,String mediapath)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

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

    public void launchbottombarfragment()
    {
        composeoptionspagerfragment fragbottombar=new composeoptionspagerfragment();
        gethelper().replaceFragment(fragbottombar, false, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        //recyclerviewlist.removeOnItemTouchListener(onTouchListener);
    }

    public void setAdapter(final video videoobj, int type)
    {
        if(type == 1)   // Media shairing
        {
            if(adaptermedialist != null && adaptermediagrid != null)
                adaptermedialist.notifyitems(arraymediaitemlist);

            if(videoobj.getmimetype().startsWith("image")){
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(videoobj.getPath()));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("image/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                applicationviavideocomposer.getactivity().startActivity(Intent.createChooser(share, "Share photo"));
            }else if(videoobj.getmimetype().startsWith("audio")){
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(videoobj.getPath()));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("audio/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                applicationviavideocomposer.getactivity().startActivity(Intent.createChooser(share, "Share audio"));

            }else if(videoobj.getmimetype().startsWith("video")){
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(videoobj.getPath()));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("video/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                applicationviavideocomposer.getactivity().startActivity(Intent.createChooser(share, "Share video"));
            }

        }
        else if(type == 2)   // Delete prompt
        {
            if(videoobj.getmimetype().startsWith("image")){
                showalertdialog(videoobj,getActivity().getResources().getString(R.string.dlt_cnfm_photo));

            }else if(videoobj.getmimetype().startsWith("audio")){
                showalertdialog(videoobj,getActivity().getResources().getString(R.string.dlt_cnfm_audio));

            }else if(videoobj.getmimetype().startsWith("video")){
                showalertdialog(videoobj,getActivity().getResources().getString(R.string.delete_confirm_video));

            }
        }else if(type == 3){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showselectedmediatypeitems();
                    if(! videoobj.getVideostarttransactionid().isEmpty())
                        updatemedialocation(videoobj.getVideostarttransactionid(),videoobj.getPath());
                }
            },500);

        }else if(type == 4)     // Clicked on media item
        {
            if(videoobj.getmimetype().startsWith("image"))
            {
                xdata.getinstance().saveSetting("selectedphotourl",""+videoobj.getPath());
                imagereaderfragment fragmentimagereader = new imagereaderfragment();
                fragmentimagereader.setdata(mcontrollernavigator);
                gethelper().replaceFragment(fragmentimagereader, false, true);

            }else if(videoobj.getmimetype().startsWith("audio"))
            {
                xdata.getinstance().saveSetting("selectedaudiourl",""+videoobj.getPath());
                audioreaderfragment fragmentaudioreader = new audioreaderfragment();
                fragmentaudioreader.setdata(mcontrollernavigator);
                gethelper().replaceFragment(fragmentaudioreader, false, true);

            }else if(videoobj.getmimetype().startsWith("video"))
            {
                xdata.getinstance().saveSetting("selectedvideourl",""+videoobj.getPath());
                videoreaderfragment fragmentvideoreader=new videoreaderfragment();
                fragmentvideoreader.setdata(mcontrollernavigator);
                gethelper().replaceFragment(fragmentvideoreader, false, true);
            }
        }
        else if(type == 6)
        {
            showfolderdialog(videoobj.getPath());
        }
    }

    public void shouldlaunchcomposer(boolean shouldlaunchcomposer)
    {
        this.shouldlaunchcomposer=shouldlaunchcomposer;
    }

    adapteritemclick mcontrollernavigator=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int type) {

            if(type == 1)
            {
                if (arraymediaitemlist != null && arraymediaitemlist.size() > 0)
                {
                    getallmedialistfromdb();
                    if(adaptermedialist != null && arraymediaitemlist.size() > 0)
                        adaptermedialist.notifyitems(arraymediaitemlist);
                }
            }
            else if(type == 2)
            {
                String filepath=(String)object;
                if(arraymediaitemlist.size() > 0)
                {
                    for(int i = 0; i< arraymediaitemlist.size(); i++)
                    {
                        if(filepath.equalsIgnoreCase(arraymediaitemlist.get(i).getPath()))
                        {
                            video videoobj= arraymediaitemlist.get(i);
                            File fdelete = new File(videoobj.getPath());
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    String localkey=videoobj.getLocalkey();
                                    System.out.println("file Deleted :" + videoobj.getPath());
                                    arraymediaitemlist.remove(videoobj);
                                    if(! localkey.trim().isEmpty())
                                        deletemediainfo(localkey);

                                } else {
                                    System.out.println("file not Deleted :" + videoobj.getPath());
                                }
                            }
                            break;
                        }
                    }
                }
            }
            else if(type == 3)    // Calling from media details screen where user is going to change media directory.
            {
                resetmediaitemsadapter();
            }
        }
    };

    public boolean isexistinarraay(String filepath)
    {
        if(arraymediaitemlist != null && arraymediaitemlist.size() > 0)
        {
            for(int i = 0; i< arraymediaitemlist.size(); i++)
            {
                if(arraymediaitemlist.get(i).getPath().equalsIgnoreCase(filepath))
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
                                arraymediaitemlist.remove(videoobj);
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


    //==============================================================================================================

    // Reader app methods
    private void checkwritestoragepermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }
            if (ContextCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                uploadmediafromgallery();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    Toast.makeText(getActivity(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        request_read_external_storage);
            }
        }
        else
        {
            uploadmediafromgallery();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_PICK) {
            if (resultCode == RESULT_OK) {
                Uri selectedvideouri = data.getData();

                String  mediafilepath="";
                try {
                    //VIDEO_URL=common.getUriRealPath(applicationviavideocomposer.getactivity(),selectedvideouri);
                    mediafilepath = common.getpath(getActivity(), selectedvideouri);
                    try {
                        databasemanager mdbhelper = null;
                        if (mdbhelper == null) {
                            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                            mdbhelper.createDatabase();
                        }

                        try {
                            mdbhelper.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(mediafilepath));
                        if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
                        {
                            common.showalert(applicationviavideocomposer.getactivity(), getResources().getString(R.string.media_already_exist));
                            return;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    common.showalert(applicationviavideocomposer.getactivity(), getResources().getString(R.string.file_uri_parse_error));
                    return;
                }
                copymediafromgallery(mediafilepath);
            }
        }
    }

    public  void uploadmediafromgallery()
    {
        Intent intent = null;
        String type = null;
        if(selectedmediatype == -1)
        {
            config.selectedmediatype=1;
            selectedmediatype=config.selectedmediatype;
        }

        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            if(selectedmediatype == 0){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                type="video/*";
            }else if(selectedmediatype == 1){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                type="image/*";
            }else if(selectedmediatype == 2){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                type="audio/*";
            }
        }
        else
        {
            if(selectedmediatype == 0){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                type="video/*";
            }else if(selectedmediatype == 1){
                intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                type="image/*";
            }else if(selectedmediatype == 2){
                intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                type="audio/*";
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

    public void loadffmpeglibrary()
    {
        if (ffmpeg == null) {
            Log.d("ffmpeg", "ffmpeg : is loading..");

            ffmpeg = FFmpeg.getInstance(applicationviavideocomposer.getactivity());
        }
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg", "ffmpeg : loaded..");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void copymediafromgallery(final String selectedmediafile){

        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                File sourceFile = new File(selectedmediafile);

                if(sourceFile.exists())
                {
                    String destinationdir = xdata.getinstance().getSetting(config.selected_folder);
                    // check for existance of file.
                    final File destinationFile;
                    File pathFile=new File(destinationdir+File.separator+sourceFile.getName());
                    if(pathFile.exists())
                    {
                        String extension = pathFile.getAbsolutePath().substring(pathFile.getAbsolutePath().lastIndexOf("."));
                        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ENGLISH).format(new Date());
                        destinationFile = new File(destinationdir+File.separator+fileName+extension);
                    }
                    else
                    {
                        destinationFile = new File(destinationdir+File.separator+sourceFile.getName());
                    }

                    try
                    {
                        if (!destinationFile.getParentFile().exists())
                            destinationFile.getParentFile().mkdirs();

                        if (!destinationFile.exists()) {
                            destinationFile.createNewFile();
                        }

                        InputStream in = new FileInputStream(selectedmediafile);
                        OutputStream out = new FileOutputStream(destinationFile);

                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;

                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        in.close();
                        out.close();

                        insertintomediastartdata(destinationFile.getAbsolutePath());

                        if(destinationFile.getAbsolutePath().contains(".m4a") || destinationFile.getAbsolutePath().contains(".mp3"))
                        {
                            final File destinationfilepath = common.gettempfileforaudiowave();
                            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                                    BuildConfig.APPLICATION_ID + ".provider", new File(destinationFile.getAbsolutePath()));

                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(applicationviavideocomposer.getactivity(),uri);
                            long mediatotalduration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                            String starttime = common.converttimeformate(0);
                            String endtime = common.converttimeformate(mediatotalduration);

                            String[] command = { "-ss", starttime,"-i", destinationFile.getAbsolutePath(), "-to",endtime, "-filter_complex",
                                    "compand=gain=-10,showwavespic=s=400x400:colors=#0076a6", "-frames:v","1",destinationfilepath.getAbsolutePath()};

                            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                                @Override
                                public void onFailure(String s) {
                                    Log.e("Failure with output : ","IN onFailure");
                                }

                                @Override
                                public void onSuccess(String s) {
                                    Log.e("SUCCESS with output : ","onSuccess");
                                    updateaudiothumbnail(common.getfilename(destinationFile.getAbsolutePath()),destinationfilepath.getAbsolutePath());

                                }

                                @Override
                                public void onProgress(String s) {
                                    Log.e( "audiothumbnail : " , "audiothumbnail onProgress");

                                }

                                @Override
                                public void onStart() {
                                    Log.e("Start with output : ","IN onStart");
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("Start with output : ","IN onFinish");
                                }
                            });


                        }

                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressdialog.dismisswaitdialog();
                                Toast.makeText(getActivity(),"Media uploaded successfully!",Toast.LENGTH_SHORT).show();
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

    public void insertintomediastartdata(String selectedmediafile)
    {
        String mediatype="image";
        if(selectedmediafile.contains(".m4a") || selectedmediafile.contains(".mp3"))
        {
            mediatype="audio";
        }
        else if(selectedmediafile.contains(".jpg") || selectedmediafile.contains(".png") || selectedmediafile.contains(".jpeg"))
        {
            mediatype="image";
        }
        else if(selectedmediafile.contains(".mp4") || selectedmediafile.contains(".mov"))
        {
            mediatype="video";
        }
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            String syncdate[] = common.getcurrentdatewithtimezone();
            mdbhelper.insertstartvideoinfo(new mediainfotablefields("",mediatype,common.getfilename(selectedmediafile),"",
                    "","","",syncdate[0]  , "",
                    "","","","","",
                    "","",config.sync_pending,"",""
                    ,"","","","",xdata.getinstance().getSetting(config.selected_folder),selectedmediafile,selectedmediafile,"",""
                    ,"",""));

            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updateaudiothumbnail(String filepath,String thumbnailurl)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            mdbhelper.updateaudiothumbnail(common.getfilename(filepath),thumbnailurl);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setheadermargine(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,Integer.parseInt(xdata.getinstance().getSetting("statusbarheight")),0,0);
        layout_mediatype.setLayoutParams(params);
    }

}
