package com.deeptruth.app.android.fragments;

import android.Manifest;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.adapter.adaptermedialist;
import com.deeptruth.app.android.adapter.adaptermediafilter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.mediainfotablefields;
import com.deeptruth.app.android.models.mediafilteroptions;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
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

    @BindView(R.id.rv_medialist_local)
    RecyclerView recyclerviewlocallist;
    @BindView(R.id.rv_medialist_published)
    RecyclerView recyclerviewpublishedlist;
    @BindView(R.id.layout_sectionsearch)
    RelativeLayout layout_sectionsearch;
    @BindView(R.id.actionbarcomposer)
    RelativeLayout actionbarcomposer;
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
    @BindView(R.id.txt_localfiles)
    TextView txt_localfiles;
    @BindView(R.id.txt_publishedfiles)
    TextView txt_publishedfiles;
    @BindView(R.id.medialistfilteroption)
    RecyclerView recyclerviewfilteroption;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.webview)
    WebView webview;

    private int selectedlisttype =0,listviewheight=0,dataupdator=0;
    private RelativeLayout listlayout;
    private Handler myhandler;
    private Runnable myrunnable;
    private boolean ascendinglistby =true;
    private View rootview = null;
    private static final int request_permissions = 1;
    private ArrayList<video> arraymediaitemlist = new ArrayList<>();
    private adaptermedialist adaptermedialistlocal;
    private adaptermedialist adaptermedialistpublished;
    private adaptermediafilter adaptermediafilter;
    private RecyclerTouchListener onTouchListener;
    private static final int request_read_external_storage = 1;
    private BroadcastReceiver medialistitemaddreceiver;
    private int REQUESTCODE_PICK=201;
    private int navigationbarheight = 0;
    private Handler handler = new Handler();
    private int numberOfTaps = 0, devicewidth = 0;
    private long lastTapTimeMs = 0;
    private long touchDownMs = 0;
    private String selectedmediafilter="";
    private boolean iskeyboardopen = false,shouldnavigatelist=true;
    private ArrayList<mediafilteroptions> arraymediafilterlist = new ArrayList<>();

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
    }

    @Override
    public void onStop() {
        super.onStop();
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
      //  setheadermargin();
        gethelper().drawerenabledisable(false);
        gethelper().setwindowfitxy(true);
        gethelper().updateactionbar(1);
        if(edt_searchitem != null)
            edt_searchitem.setTag(false);
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

                if(adaptermedialistpublished != null)
                    adaptermedialistpublished.notifyDataSetChanged();

                if(adaptermedialistlocal != null)
                    adaptermedialistlocal.notifyDataSetChanged();

                showselectedmediatypeitems(true);
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
           // setheadermargin();

            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setSupportZoom(false);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);
            WebSettings settings = webview.getSettings();
            settings.setDomStorageEnabled(true);


            try {
                Field f = mSwipeRefreshLayout.getClass().getDeclaredField("mCircleView");
                f.setAccessible(true);
                ImageView img = (ImageView)f.get(mSwipeRefreshLayout);
                img.setAlpha(0.0f);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            gethelper().drawerenabledisable(false);
            {
                LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerviewlocallist.setLayoutManager(layoutManager);
            }
            {
                LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerviewpublishedlist.setLayoutManager(layoutManager);
            }
            {
                LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerviewfilteroption.setLayoutManager(layoutManager);
            }

            ((DefaultItemAnimator) recyclerviewlocallist.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerviewlocallist.getItemAnimator().setChangeDuration(0);

            actionbarcomposer.setOnTouchListener(this);

            arraymediafilterlist.clear();

            arraymediafilterlist.add(new mediafilteroptions(config.filter_date,true,true));
            arraymediafilterlist.add(new mediafilteroptions(config.filter_title,false,false));
            arraymediafilterlist.add(new mediafilteroptions(config.filter_type,false,false));
            arraymediafilterlist.add(new mediafilteroptions(config.filter_size,false,false));
            arraymediafilterlist.add(new mediafilteroptions(config.filter_location,false,false));

            ascendinglistby =true;
            selectedmediafilter=config.filter_date;

            txt_localfiles.setOnClickListener(this);
            txt_publishedfiles.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_folder.setOnClickListener(this);
            img_settings.setOnClickListener(this);
            img_header_search.setOnClickListener(this);
            txt_searchcancel.setOnClickListener(this);
            img_uploadmedia.setOnClickListener(this);
            listlayout.setOnClickListener(this);
            recyclerviewlocallist.setOnClickListener(this);
            recyclerviewpublishedlist.setOnClickListener(this);

            img_camera.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.GONE);
            img_header_search.setVisibility(View.GONE);

            File folder = new File(xdata.getinstance().getSetting(config.selected_folder));
            txt_title_actionbarcomposer.setText(folder.getName());
            navigationbarheight =  common.getnavigationbarheight();

            try {
                DrawableCompat.setTint(img_header_search.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                        , R.color.white));
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

            setfooterlayout(true);

            onTouchListener = new RecyclerTouchListener(applicationviavideocomposer.getactivity(), recyclerviewlocallist);
            onTouchListener.setSwipeOptionViews(R.id.img_slide_delete).setSwipeable( R.id.rl_rowfg,R.id.bottom_wraper,
            new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                @Override
                public void onSwipeOptionClicked(int viewID, int position) {

                }
            });

            edt_searchitem.setTag(false);
            edt_searchitem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    edt_searchitem.setTag(hasFocus);
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
                    if((Boolean) edt_searchitem.getTag())
                        searchmediafromlist(editable.toString());
                }
            });

            actionbarcomposer.post(new Runnable() {
                @Override
                public void run() {
                    xdata.getinstance().saveSetting(config.TOPBAR_HEIGHT,"" + actionbarcomposer.getHeight());
                }
            });

            devicewidth=common.getScreenWidth(applicationviavideocomposer.getactivity());

            showselectedmediatypeitems(true);

            /*pagermediatype.setAdapter(new CustomPagerAdapter(applicationviavideocomposer.getactivity()));
            pagermediatype.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(final int position) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            selectedmediatype=position;
                            showselectedmediatypeitems(selectedmediatype,true);
                        }
                    },200);

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });*/

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    showsearchsection();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            IntentFilter intentFilter = new IntentFilter(config.broadcast_medialistnewitem);
            medialistitemaddreceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    shouldnavigatelist=true;
                    dataupdator = 6;
                    requestpermissions();
                }
            };
            applicationviavideocomposer.getactivity().registerReceiver(medialistitemaddreceiver, intentFilter);
            showlocallistitems(true);
            if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
            {
                img_uploadmedia.setVisibility(View.VISIBLE);
                img_camera.setVisibility(View.GONE);
            }
            else
            {
                img_uploadmedia.setVisibility(View.GONE);
                img_camera.setVisibility(View.VISIBLE);
            }

            if(common.isdevelopermodeenable())
                img_settings.setVisibility(View.VISIBLE);

            if (common.getstoragedeniedpermissions().isEmpty())
                fetchmedialistfromdirectory();

            resetmedialist();

            webview.loadUrl(config.publishedlist_url);

            webview.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                }
            });
        }
        return rootview;
    }

    public void showsearchsection()
    {
        layout_sectionsearch.setVisibility(View.VISIBLE);
        /*iskeyboardopen =  true;
        edt_searchitem.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
        edt_searchitem.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));*/
    }

    private void searchmediafromlist(String text) {
        if(arraymediaitemlist != null && arraymediaitemlist.size() > 0)
        {
            if(text.trim().length() > 0)
            {
                //new array list that will hold the filtered data
                ArrayList<video> filterdnames = new ArrayList<>();
                for (video object : arraymediaitemlist)
                {
                    //if the existing elements contains the search input
                    if(selectedmediafilter.equalsIgnoreCase(config.filter_date))
                    {
                        String datetime="";
                        if(object.getCreatedate().trim().isEmpty())
                            datetime="NA";
                        else
                            datetime=object.getCreatedate() +", "  + object.getCreatetime();

                        if (datetime.toLowerCase().contains(text.toLowerCase()) && selectedlisttype == 0 && (! object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                        else if (datetime.toLowerCase().contains(text.toLowerCase()) && selectedlisttype == 1 && (object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                    }
                    else if(selectedmediafilter.equalsIgnoreCase(config.filter_title))
                    {
                        if (object.getMediatitle().toLowerCase().
                                contains(text.toLowerCase()) && selectedlisttype == 0 && (! object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                        else if (object.getMediatitle().toLowerCase().
                                contains(text.toLowerCase()) && selectedlisttype == 1 && (object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                    }
                    else if(selectedmediafilter.equalsIgnoreCase(config.filter_size))
                    {
                        if (object.getfilesize().toLowerCase().
                                contains(text.toLowerCase()) && selectedlisttype == 0 && (! object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                        else if (object.getfilesize().toLowerCase().
                                contains(text.toLowerCase()) && selectedlisttype == 1 && (object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                    }
                    else if(selectedmediafilter.equalsIgnoreCase(config.filter_location))
                    {
                        if (object.getMedialocation().toLowerCase().
                                contains(text.toLowerCase()) && selectedlisttype == 0 && (! object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                        else if (object.getMedialocation().toLowerCase().
                                contains(text.toLowerCase()) && selectedlisttype == 1 && (object.ismediapublished()))
                        {
                            filterdnames.add(object);
                        }
                    }
                    else if(selectedmediafilter.equalsIgnoreCase(config.filter_type))
                    {
                        if(text.toLowerCase().equalsIgnoreCase("image") ||
                                text.toLowerCase().equalsIgnoreCase("photo"))
                        {
                            if(selectedlisttype == 0 && (! object.ismediapublished()))
                            {
                                if (object.getMediatype().equalsIgnoreCase("image"))
                                    filterdnames.add(object);
                            }
                            else if(selectedlisttype == 1 && (object.ismediapublished()))
                            {
                                if (object.getMediatype().equalsIgnoreCase("image"))
                                    filterdnames.add(object);
                            }
                        }
                        else if(text.toLowerCase().equalsIgnoreCase("video"))
                        {
                            if(selectedlisttype == 0 && (! object.ismediapublished()))
                            {
                                if (object.getMediatype().equalsIgnoreCase("video"))
                                    filterdnames.add(object);
                            }
                            else if(selectedlisttype == 1 && (object.ismediapublished()))
                            {
                                if (object.getMediatype().equalsIgnoreCase("video"))
                                    filterdnames.add(object);
                            }
                        }
                        else if(text.toLowerCase().equalsIgnoreCase("audio"))
                        {
                            if(selectedlisttype == 0 && (! object.ismediapublished()))
                            {
                                if (object.getMediatype().equalsIgnoreCase("audio"))
                                    filterdnames.add(object);
                            }
                            else if(selectedlisttype == 1 && (object.ismediapublished()))
                            {
                                if (object.getMediatype().equalsIgnoreCase("audio"))
                                    filterdnames.add(object);
                            }
                        }
                    }
                }
                //calling a method of the adapter class and passing the filtered list
                if(adaptermedialistpublished != null && adaptermedialistlocal != null)
                {
                    adaptermedialistpublished.filterlist(filterdnames);
                    adaptermedialistlocal.filterlist(filterdnames);
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
                break;
        }
        return true;
    }

    public void showlocallistitems(boolean visiblelist)
    {
        if(visiblelist)
        {
            selectedlisttype =0;
            recyclerviewlocallist.setVisibility(View.VISIBLE);
            recyclerviewfilteroption.setVisibility(View.VISIBLE);
            recyclerviewpublishedlist.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);

            txt_localfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                    getColor(R.color.blue_item_selected));
            txt_publishedfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                    getColor(R.color.blue_item_deselected));

            if(adaptermedialistlocal != null && arraymediaitemlist.size() > 0)
                adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);
        }
        else
        {
            selectedlisttype =1;
            recyclerviewlocallist.setVisibility(View.GONE);
            recyclerviewpublishedlist.setVisibility(View.GONE);
            recyclerviewfilteroption.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);

            txt_publishedfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                    getColor(R.color.blue_item_selected));
            txt_localfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                    getColor(R.color.blue_item_deselected));

            if(adaptermedialistpublished != null && arraymediaitemlist.size() > 0)
                adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);
        }
    }

    public void resetlistfilters()
    {
        edt_searchitem.setText("");
        hidekeyboard();
        layout_sectionsearch.setVisibility(View.GONE);
        iskeyboardopen = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txt_localfiles:
                selectedlisttype=0;
                recyclerviewlocallist.setVisibility(View.VISIBLE);
                recyclerviewfilteroption.setVisibility(View.VISIBLE);
                recyclerviewpublishedlist.setVisibility(View.GONE);
                webview.setVisibility(View.GONE);

                txt_localfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                        getColor(R.color.blue_item_selected));
                txt_publishedfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                        getColor(R.color.blue_item_deselected));
                showselectedmediatypeitems(true);
                resetlistfilters();
                break;
            case R.id.txt_publishedfiles:
                selectedlisttype=1;
                recyclerviewlocallist.setVisibility(View.GONE);
                recyclerviewpublishedlist.setVisibility(View.GONE);
                recyclerviewfilteroption.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);

                txt_publishedfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                        getColor(R.color.blue_item_selected));
                txt_localfiles.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().
                        getColor(R.color.blue_item_deselected));
                showselectedmediatypeitems(true);
                resetlistfilters();
                break;
            case R.id.img_camera:
                launchbottombarfragment();
                break;
            case R.id.img_header_search:
                layout_sectionsearch.setVisibility(View.VISIBLE);
                iskeyboardopen =  true;
                edt_searchitem.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                edt_searchitem.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
                break;
            case R.id.txt_searchcancel:
                resetlistfilters();
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
            if(adaptermedialistpublished != null)
                adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);

            if(adaptermedialistlocal != null)
                adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);

            fetchmedialistfromdirectory();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showselectedmediatypeitems(boolean scrolllisttotop)
    {
        setsearchbar();

        if(adaptermedialistlocal != null && adaptermedialistpublished != null)
        {
            adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);
            adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);

            if(scrolllisttotop)
                recyclerviewpublishedlist.smoothScrollToPosition(0);

            if(scrolllisttotop)
                recyclerviewlocallist.smoothScrollToPosition(0);
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
            if((! file.getName().equalsIgnoreCase(config.cachefolder)) && (! file.getName().equalsIgnoreCase(config.allmedia)))
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
                try
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
                            String token = "" + cursor.getString(cursor.getColumnIndex("token"));
                            String mediapublished = "" + cursor.getString(cursor.getColumnIndex("mediapublished"));

                            video videoobject=new video();
                            if(id.trim().isEmpty() || id.equalsIgnoreCase("null"))
                                id="0";

                            if(! isexistinarraay(mediafilepath))
                            {
                                String filesize=common.filesize(mediafilepath);
                                videoobject.setId(Integer.parseInt(id));
                                videoobject.setPath(mediafilepath);
                                videoobject.setmimetype(type);
                                videoobject.setMediatype(type);
                                videoobject.setLocalkey(localkey);
                                videoobject.setVideostarttransactionid(videostarttransactionid);
                                videoobject.setThumbnailpath(thumbnailurl);
                                videoobject.setMediatitle((media_name.trim().isEmpty())?config.no_title:media_name);
                                videoobject.setMedianotes(media_notes);
                                videoobject.setMediacolor(color);
                                videoobject.setfilesize(filesize);
                                videoobject.setFilesizebytes(new File(mediafilepath).length());
                                videoobject.setExtension(common.getfileextension(location));
                                videoobject.setName(common.getfilename(location));
                                videoobject.setMediastatus(status);
                                videoobject.setmediapublished((mediapublished.equalsIgnoreCase("1")?true:false));
                                videoobject.setVideotoken(token);

                                if(mediaduration.trim().isEmpty() && (! type.equalsIgnoreCase("image")))
                                {
                                    try {
                                        String duration = common.getvideotimefromurl(getActivity(),mediafilepath);
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
                                            mediadatetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)
                                                    .parse(mediastartdevicedate);
                                        }
                                        else
                                        {
                                            mediadatetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH)
                                                    .parse(mediastartdevicedate);
                                        }

                                        videoobject.setcreatedatetimestamp(mediadatetime.getTime());
                                        DateFormat dateformat = new SimpleDateFormat("z",Locale.getDefault());
                                        String localTime = dateformat.format(mediadatetime);

                                        videoobject.setCreatedate(common.parsedateformat(mediadatetime));
                                        videoobject.setCreatetime(common.parsetimeformat(mediadatetime)+ " "+localTime );
                                    }

                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                String recordedlocation = "";
                                Cursor cursor2 = mdbhelper.getmediacolor(localkey);
                                if (cursor2 != null && cursor2.getCount()> 0 && cursor2.moveToFirst())
                                {
                                    LayoutInflater inflater = LayoutInflater.from(applicationviavideocomposer.getactivity());
                                    View viewparent = inflater.inflate(R.layout.row_mediacolor, null, false);
                                    LinearLayout layout=(LinearLayout)viewparent.findViewById(R.id.linear_seekbarcolorview);
                                    ArrayList<String> arrayList=new ArrayList<>();
                                    int validcount=0,cautioncount=0,unsentcount=0,invalidcount=0;


                                    do{
                                        String framecolor=cursor2.getString(cursor2.getColumnIndex("color"));
                                        String metricdata=cursor2.getString(cursor2.getColumnIndex("metricdata"));
                                        if(recordedlocation.trim().isEmpty())
                                            recordedlocation=common.getlocationfrommetricdata(metricdata);

                                        arrayList.add(framecolor);
                                        if(framecolor.equalsIgnoreCase(config.color_green))
                                            validcount++;

                                        if(framecolor.equalsIgnoreCase(config.color_yellow))
                                            cautioncount++;

                                        if(framecolor.equalsIgnoreCase(config.color_red))
                                            invalidcount++;

                                        if(BuildConfig.FLAVOR.contains(config.build_flavor_composer))
                                        {
                                            if(framecolor.trim().isEmpty())
                                                unsentcount++;
                                        }

                                    }while (cursor2.moveToNext());

                                    int sectioncount=0,lastsequenceno=0,syncedframes=0,totalframes=0;
                                    String lastcolor="",strsequenceno="";
                                    ArrayList<String> colorsectioncount=new ArrayList<>();

                                    if(validcount != videoobject.getValidcount() || cautioncount != videoobject.getCautioncount()
                                            || unsentcount != videoobject.getUnsentcount())
                                    {
                                        cursor2.moveToFirst();
                                        do{
                                            String framecolor=cursor2.getString(cursor2.getColumnIndex("color"));
                                            strsequenceno=cursor2.getString(cursor2.getColumnIndex("sequenceno"));

                                            sectioncount++;
                                            if(framecolor.trim().isEmpty())
                                                framecolor=config.color_transparent;

                                            if(! lastcolor.equalsIgnoreCase(framecolor))
                                            {
                                                sectioncount=0;
                                                sectioncount++;
                                                colorsectioncount.add(framecolor+","+sectioncount);
                                            }
                                            else
                                            {
                                                colorsectioncount.set(colorsectioncount.size()-1,framecolor+","+sectioncount);
                                            }
                                            lastcolor=framecolor;

                                            if(! strsequenceno.trim().isEmpty())
                                            {
                                                if(! framecolor.equalsIgnoreCase(config.color_transparent))
                                                {
                                                    int sequenceno=Integer.parseInt(strsequenceno);
                                                    int sequencecount=sequenceno-lastsequenceno;

                                                    syncedframes=syncedframes+sequencecount;
                                                    lastsequenceno=sequenceno;
                                                }
                                                totalframes=Integer.parseInt(strsequenceno);
                                            }


                                        }while (cursor2.moveToNext());
                                    }

                                    videoobject.setFrameuploadstatus("");
                                    if(totalframes > 0)
                                        videoobject.setFrameuploadstatus("Frames : "+syncedframes+"/"+totalframes);

                                    videoobject.setColorsectionsarray(colorsectioncount);
                                    videoobject.setMediabarcolor(arrayList);
                                    videoobject.setMedialocation(recordedlocation);
                                    videoobject.setValidcount(validcount);
                                    videoobject.setCautioncount(cautioncount);
                                    videoobject.setInvalidcount(invalidcount);
                                    videoobject.setUnsentcount(unsentcount);
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

                            sortlistviewby("date");

                            setmediaadapter();
                            if (arraymediaitemlist != null && arraymediaitemlist.size() > 0)
                            {
                                if(adaptermedialistlocal != null && arraymediaitemlist.size() > 0)
                                    adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);

                                if(adaptermedialistpublished != null && arraymediaitemlist.size() > 0)
                                    adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);
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
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void sortlistviewby(String sortby)
    {
        if(sortby.equalsIgnoreCase("date"))
        {
            if(ascendinglistby)
            {
                // Latest date media is on top
                Collections.sort(arraymediaitemlist, new Comparator<video>() {
                    public int compare(video s1, video s2) {
                        // notice the cast to (Integer) to invoke compareTo
                        Integer value1=(int)s2.getcreatedatetimestamp();
                        Integer value2=(int)s1.getcreatedatetimestamp();
                        return (value1).compareTo(value2);
                    }
                });
            }
            else
            {
                // Latest date media is on bottom
                Collections.sort(arraymediaitemlist, new Comparator<video>() {
                    public int compare(video s1, video s2) {
                        // notice the cast to (Integer) to invoke compareTo
                        Integer value1=(int)s2.getcreatedatetimestamp();
                        Integer value2=(int)s1.getcreatedatetimestamp();
                        return (value2).compareTo(value1);
                    }
                });
            }
        }
        else if(sortby.equalsIgnoreCase("title") || sortby.equalsIgnoreCase("location") ||
                sortby.equalsIgnoreCase("size"))
        {
            if(ascendinglistby)
            {
                Collections.sort(arraymediaitemlist, new Comparator<video>() {
                    public int compare(video s1, video s2)
                    {
                        if(sortby.equalsIgnoreCase("location"))
                            return s1.getMedialocation().compareTo(s2.getMedialocation());
                        else if(sortby.equalsIgnoreCase("size"))
                        {
                            Integer value1=(int)s1.getFilesizebytes();
                            Integer value2=(int)s2.getFilesizebytes();
                            return (value1).compareTo(value2);
                        }
                        return s1.getMediatitle().compareTo(s2.getMediatitle());
                    }
                });
            }
            else
            {
                Collections.sort(arraymediaitemlist, new Comparator<video>() {
                    public int compare(video s1, video s2)
                    {
                        if(sortby.equalsIgnoreCase("location"))
                            return s2.getMedialocation().compareTo(s1.getMedialocation());
                        else if(sortby.equalsIgnoreCase("size"))
                        {
                            Integer value1=(int)s1.getFilesizebytes();
                            Integer value2=(int)s2.getFilesizebytes();
                            return (value2).compareTo(value1);
                        }

                        return s2.getMediatitle().compareTo(s1.getMediatitle());
                    }
                });
            }
        }
    }

    public void setmediaadapter()
    {
        recyclerviewlocallist.setVisibility(View.VISIBLE);
        recyclerviewlocallist.post(new Runnable() {
            @Override
            public void run() {

                listviewheight= recyclerviewlocallist.getHeight();
                if(adaptermedialistlocal == null)
                {
                    adaptermedialistlocal = new adaptermedialist(getActivity(), arraymediaitemlist, new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {
                        }
                        @Override
                        public void onItemClicked(Object object, int type) {
                            video videoobj=(video)object;
                            setAdapter(videoobj,type);
                        }

                    },listviewheight);
                    recyclerviewlocallist.setAdapter(adaptermedialistlocal);
                }

                if(adaptermedialistpublished == null)
                {
                    adaptermedialistpublished = new adaptermedialist(getActivity(), arraymediaitemlist, new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {
                        }
                        @Override
                        public void onItemClicked(Object object, int type) {
                            video videoobj=(video)object;
                            setAdapter(videoobj,type);
                        }

                    },listviewheight);
                    recyclerviewpublishedlist.setAdapter(adaptermedialistpublished);
                }
                if(adaptermediafilter == null)
                {
                    recyclerviewfilteroption.post(new Runnable() {
                        @Override
                        public void run() {
                            adaptermediafilter = new adaptermediafilter(getActivity(), arraymediafilterlist, new adapteritemclick() {
                                @Override
                                public void onItemClicked(Object object) {
                                }
                                @Override
                                public void onItemClicked(Object object, int type) {
                                    mediafilteroptions mediafilterobj=(mediafilteroptions)object;
                                    setfilterAdapter(mediafilterobj,type);
                                }

                            },recyclerviewfilteroption.getWidth());
                            recyclerviewfilteroption.setAdapter(adaptermediafilter);
                            adaptermediafilter.notifyDataSetChanged();

                        }
                    });

                }

                showselectedmediatypeitems(true);

                if(shouldnavigatelist)
                {
                    shouldnavigatelist=false;
                    showlocallistitems(true);
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
               if(dataupdator >= 10)
               {
                   if (arraymediaitemlist != null && arraymediaitemlist.size() > 0)
                       getallmedialistfromdb();

                   dataupdator=0;
               }

               if(common.isdevelopermodeenable() && (img_settings.getVisibility() == View.GONE ||
                       img_settings.getVisibility() == View.INVISIBLE))
               {
                   if(adaptermedialistlocal != null)
                       adaptermedialistlocal.notifyDataSetChanged();

                   if(adaptermedialistpublished != null)
                       adaptermedialistpublished.notifyDataSetChanged();

                   img_settings.setVisibility(View.VISIBLE);
               }

               if(arraymediafilterlist != null && arraymediafilterlist.size() >0){
                   if(adaptermediafilter != null){
                       adaptermediafilter.notifyDataSetChanged();
                   }
               }

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

        /*for(int i=0;i<arraymediaitemlist.size();i++)
        {
            localarray.add(arraymediaitemlist.get(i));
        }*/

        boolean needtoshiftitem=false;
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
                String mediastartdevicedate = "" + cursor.getString(cursor.getColumnIndex("videostartdevicedate"));
                String token = "" + cursor.getString(cursor.getColumnIndex("token"));
                String mediapublished = "" + cursor.getString(cursor.getColumnIndex("mediapublished"));

                for(int i = 0; i< arraymediaitemlist.size(); i++)
                {
                    if(common.getfilename(arraymediaitemlist.get(i).getPath()).equalsIgnoreCase(location))
                    {
                        boolean isneedtonotify=false;
                        arraymediaitemlist.get(i).setVideostarttransactionid(videostarttransactionid);
                        arraymediaitemlist.get(i).setThumbnailpath(thumbnailurl);
                        arraymediaitemlist.get(i).setMediacolor(color);
                        arraymediaitemlist.get(i).setLocalkey(localkey);
                        arraymediaitemlist.get(i).setMediastatus(status);
                        arraymediaitemlist.get(i).setVideotoken(token);


                        if(! arraymediaitemlist.get(i).ismediapublished() && mediapublished.equalsIgnoreCase("1"))
                            needtoshiftitem=true;

                        arraymediaitemlist.get(i).setmediapublished((mediapublished.equalsIgnoreCase("1")?true:false));

                        String title=(media_name.trim().isEmpty())?config.no_title:media_name;
                        if((! arraymediaitemlist.get(i).getMediatitle().equalsIgnoreCase(title)) || (! arraymediaitemlist.get(i).getMedianotes().equalsIgnoreCase(media_notes)))
                            isneedtonotify=true;

                        arraymediaitemlist.get(i).setMediatitle(title);
                        arraymediaitemlist.get(i).setMedianotes(media_notes);

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
                                arraymediaitemlist.get(i).setCreatedate(common.parsedateformat(mediadatetime));
                                arraymediaitemlist.get(i).setCreatetime((common.parsetimeformat(mediadatetime))+ " "+localTime);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        int sectioncount=0,validcount=0,cautioncount=0,invalidcount=0,unsentcount=0,lastsequenceno=0,
                                syncedframes=0,totalframes=0;
                        String lastcolor="",strsequenceno="",recordedlocation = "";;

                        ArrayList<String> colorsectioncount=new ArrayList<>();
                        ArrayList<String> arrayList=new ArrayList<>();
                        Cursor cursor2 = mdbhelper.getmediacolor(localkey);
                        if (cursor2 != null && cursor2.getCount()> 0 && cursor2.moveToFirst()) {

                            do {
                                String framecolor = cursor2.getString(cursor2.getColumnIndex("color"));
                                strsequenceno=cursor2.getString(cursor2.getColumnIndex("sequenceno"));
                                String metricdata=cursor2.getString(cursor2.getColumnIndex("metricdata"));
                                if(recordedlocation.trim().isEmpty())
                                    recordedlocation=common.getlocationfrommetricdata(metricdata);

                                arrayList.add(framecolor);
                                if (framecolor.equalsIgnoreCase(config.color_green))
                                    validcount++;

                                if (framecolor.equalsIgnoreCase(config.color_yellow))
                                    cautioncount++;

                                if (framecolor.equalsIgnoreCase(config.color_red))
                                    invalidcount++;

                                if (BuildConfig.FLAVOR.contains(config.build_flavor_composer)) {
                                    if (framecolor.trim().isEmpty())
                                        unsentcount++;
                                }

                                sectioncount++;
                                if (framecolor.trim().isEmpty())
                                    framecolor = config.color_transparent;

                                if (!lastcolor.equalsIgnoreCase(framecolor)) {
                                    sectioncount = 0;
                                    sectioncount++;
                                    colorsectioncount.add(framecolor + "," + sectioncount);
                                } else {
                                    colorsectioncount.set(colorsectioncount.size() - 1, framecolor + "," + sectioncount);
                                }
                                lastcolor = framecolor;

                                if(! strsequenceno.trim().isEmpty())
                                {
                                    if(! framecolor.equalsIgnoreCase(config.color_transparent))
                                    {
                                        int sequenceno=Integer.parseInt(strsequenceno);
                                        int sequencecount=sequenceno-lastsequenceno;

                                        syncedframes=syncedframes+sequencecount;
                                        lastsequenceno=sequenceno;
                                    }
                                    totalframes=Integer.parseInt(strsequenceno);
                                }

                            } while (cursor2.moveToNext());

                            arraymediaitemlist.get(i).setFrameuploadstatus("");
                            if(totalframes > 0)
                                arraymediaitemlist.get(i).setFrameuploadstatus("Frames : "+syncedframes+"/"+totalframes);

                            if(validcount != arraymediaitemlist.get(i).getValidcount() || cautioncount != arraymediaitemlist.get(i).getCautioncount()
                                    || unsentcount != arraymediaitemlist.get(i).getUnsentcount()) {
                                isneedtonotify = true;
                            }
                            arraymediaitemlist.get(i).setColorsectionsarray(colorsectioncount);
                            arraymediaitemlist.get(i).setMediabarcolor(arrayList);
                            arraymediaitemlist.get(i).setValidcount(validcount);
                            arraymediaitemlist.get(i).setCautioncount(cautioncount);
                            arraymediaitemlist.get(i).setInvalidcount(invalidcount);
                            arraymediaitemlist.get(i).setUnsentcount(unsentcount);
                            arraymediaitemlist.get(i).setMedialocation(recordedlocation);

                            if(isneedtonotify)
                            {
                                if(adaptermedialistlocal != null && arraymediaitemlist.size() > 0)
                                    adaptermedialistlocal.notifyItemChanged(i);

                                if(adaptermedialistpublished != null && arraymediaitemlist.size() > 0)
                                    adaptermedialistpublished.notifyItemChanged(i);
                            }
                        }
                        break;
                    }
                }

            }while(cursor.moveToNext());
        }

        if(needtoshiftitem)
        {
            if(adaptermedialistlocal != null)
                adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);

            if(adaptermedialistpublished != null)
                adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);
        }

        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void deletemediainfo(String localkey,String location)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(! localkey.trim().isEmpty())
        {
            mdbhelper.deletefrommetadatabylocalkey(localkey);
            mdbhelper.deletefromstartvideoinfobylocalkey(localkey);
        }
        else
        {
            mdbhelper.deletefrommetadatabylocation(location);
        }
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
        //recyclerviewlocallist.removeOnItemTouchListener(onTouchListener);
    }

    public void setAdapter(final video videoobj, int type)
    {
        if(type == 1)   // Media shairing
        {
            if(videoobj.getmimetype().startsWith(config.type_image))
            {
                baseactivity.getinstance().preparesharedialogfragment(videoobj.getPath(),videoobj.getVideotoken(),videoobj.getMediatype(),
                        videoobj.getThumbnailpath());
            }
            else if(videoobj.getmimetype().startsWith(config.type_audio))
            {
                baseactivity.getinstance().preparesharedialogfragment(videoobj.getPath(),videoobj.getVideotoken(),videoobj.getMediatype(),
                        videoobj.getThumbnailpath());
            }
            else if(videoobj.getmimetype().startsWith(config.type_video))
            {
                baseactivity.getinstance().preparesharedialogfragment(videoobj.getPath(),videoobj.getVideotoken(),videoobj.getMediatype(),
                        videoobj.getThumbnailpath());
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
                    showselectedmediatypeitems(false);
                    if(! videoobj.getVideostarttransactionid().isEmpty())
                        updatemedialocation(videoobj.getVideostarttransactionid(),videoobj.getPath());
                }
            },500);

        }else if(type == 4)     // Clicked on media item
        {
            if(videoobj.getmimetype().startsWith("image"))
            {
                xdata.getinstance().saveSetting(config.selectedphotourl,""+videoobj.getPath());
                imagereaderfragment fragmentimagereader = new imagereaderfragment();
                fragmentimagereader.setdata(mcontrollernavigator);
                gethelper().replaceFragment(fragmentimagereader, false, true);

            }else if(videoobj.getmimetype().startsWith("audio"))
            {
                xdata.getinstance().saveSetting(config.selectedaudiourl,""+videoobj.getPath());
                audioreaderfragment fragmentaudioreader = new audioreaderfragment();
                fragmentaudioreader.setdata(mcontrollernavigator);
                gethelper().replaceFragment(fragmentaudioreader, false, true);

            }else if(videoobj.getmimetype().startsWith("video"))
            {
                xdata.getinstance().saveSetting(config.selectedvideourl,""+videoobj.getPath());
                videoreaderfragment fragmentvideoreader=new videoreaderfragment();
                fragmentvideoreader.setdata(mcontrollernavigator);
                gethelper().replaceFragment(fragmentvideoreader, false, true);
            }
        }else if(type == 5)
        {
            senditem(videoobj.getPath(),videoobj.getVideotoken(),videoobj.getMediatype(),videoobj.getThumbnailpath());
        } else if(type == 6)
        {
            publishitem(videoobj.getPath(),videoobj.getVideotoken(),videoobj.getMediatype(),videoobj.getThumbnailpath());
        } else if(type == 7)
        {
            exportitem(videoobj);
        }
    }

    public void shouldlaunchcomposer(boolean shouldlaunchcomposer)
    {

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
                    if(adaptermedialistlocal != null && arraymediaitemlist.size() > 0)
                        adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);

                    if(adaptermedialistlocal != null && arraymediaitemlist.size() > 0)
                        adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);
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
                                    String location=videoobj.getPath();
                                    System.out.println("file Deleted :" + videoobj.getPath());
                                    arraymediaitemlist.remove(videoobj);
                                    if(! localkey.trim().isEmpty())
                                    {
                                        deletemediainfo(localkey,location);
                                    }
                                    else
                                    {
                                        deletemediainfo(localkey,location);
                                    }


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
                                String location=videoobj.getPath();
                                System.out.println("file Deleted :" + videoobj.getPath());
                                arraymediaitemlist.remove(videoobj);
                                dialog.dismiss();
                                if(! localkey.trim().isEmpty())
                                {
                                    deletemediainfo(localkey,location);
                                }
                                else
                                {
                                    deletemediainfo(localkey,location);
                                }

                            } else {
                                System.out.println("file not Deleted :" + videoobj.getPath());
                            }
                        }
                        showselectedmediatypeitems(false);
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
        final CharSequence[] items = {"Image", "Video", "Audio"};


        AlertDialog.Builder builder = new AlertDialog.Builder(applicationviavideocomposer.getactivity());
        builder.setTitle("Upload Media!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = null;
                String type = null;

                if (items[item].equals("Image"))
                {
                    if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                    {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        type="image/*";
                    }
                    else
                    {
                        intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        type="image/*";
                    }
                } else if (items[item].equals("Video"))
                {
                    if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                    {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        type="video/*";
                    }
                    else
                    {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                        type="video/*";
                    }
                }else if (items[item].equals("Audio"))
                {
                    if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                    {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                        type="audio/*";
                    }
                    else
                    {
                        intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                        type="audio/*";
                    }
                }

                fireintentforupload(intent,type);
            }
        });
        builder.show();

        /*Intent intent = null;
        String type = null;

        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            if(selectedmediatype == 0){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                type="image*//*";
            }else if(selectedmediatype == 1){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                type="video*//*";
            }else if(selectedmediatype == 2){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                type="audio*//*";
            }
        }
        else
        {
            if(selectedmediatype == 0){
                intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                type="image*//*";
            }else if(selectedmediatype == 1){
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                type="video*//*";
            }else if(selectedmediatype == 2){
                intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                type="audio*//*";
            }
        }
        Activity activity=getActivity();
        if(type!=null || activity!=null){
            intent.setType(type);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra("return-data", true);
            startActivityForResult(intent,REQUESTCODE_PICK);
        }*/
    }

    public void fireintentforupload(Intent intent,String type)
    {
        if(type!=null || applicationviavideocomposer.getactivity() !=null){
            intent.setType(type);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra("return-data", true);
            startActivityForResult(intent,REQUESTCODE_PICK);
        }
    }

    public void copymediafromgallery(final String selectedmediafile){

        if(selectedmediafile == null)
            return;

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

                            if(mediatotalduration > 10000)
                                mediatotalduration=10000;

                            String starttime = common.converttimeformat(0);
                            String endtime = common.converttimeformat(mediatotalduration);

                            String[] command = { "-ss", starttime,"-i", destinationFile.getAbsolutePath(), "-to",endtime, "-filter_complex",
                                    "compand=gain=-10,showwavespic=s=400x400:colors=#0EAE3E", "-frames:v","1",destinationfilepath.getAbsolutePath()};

                                applicationviavideocomposer.ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
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
            mediatype="audio";
        else if(selectedmediafile.contains(".jpg") || selectedmediafile.contains(".png") || selectedmediafile.contains(".jpeg"))
            mediatype="image";
        else if(selectedmediafile.contains(".mp4") || selectedmediafile.contains(".mov"))
            mediatype="video";

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
                    "","","","0"  , "",
                    "","","","","",
                    "","",config.sync_pending,"",""
                    ,"","","","",xdata.getinstance().getSetting(config.selected_folder),selectedmediafile,selectedmediafile,"",""
                    ,"","",""));

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

    public void setfooterlayout(boolean isfottermarginset){

        if(isfottermarginset){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(-4,-4,-4,navigationbarheight);
            listlayout.setLayoutParams(params);
        }else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,navigationbarheight);
            listlayout.setLayoutParams(params);
        }
    }

    public void setsearchbar(){
        if(iskeyboardopen){
            edt_searchitem.setText("");
            hidekeyboard();
        }
        iskeyboardopen = false;
    }

    public void setfilterAdapter(final mediafilteroptions mediafilterobj, int type)
    {
        if(type == 1)
        {
            if(mediafilterobj.isascending())
                mediafilterobj.setascending(false);
            else
                mediafilterobj.setascending(true);

            mediafilterobj.setfilterselected(true);

            ascendinglistby =mediafilterobj.isascending();

            if(mediafilterobj.getmediafiltername().equalsIgnoreCase(config.filter_date))
            {
                selectedmediafilter=config.filter_date;
                sortlistviewby("date");
            }
            else if(mediafilterobj.getmediafiltername().equalsIgnoreCase(config.filter_title))
            {
                selectedmediafilter=config.filter_title;
                sortlistviewby("title");
            }
            else if(mediafilterobj.getmediafiltername().equalsIgnoreCase(config.filter_type))
            {
                selectedmediafilter=config.filter_type;
            }
            else if(mediafilterobj.getmediafiltername().equalsIgnoreCase(config.filter_size))
            {
                selectedmediafilter=config.filter_size;
                sortlistviewby("size");
            }
            else if(mediafilterobj.getmediafiltername().equalsIgnoreCase(config.filter_location))
            {
                selectedmediafilter=config.filter_location;
                sortlistviewby("location");
            }

            if(adaptermedialistlocal != null && selectedlisttype == 0)
                adaptermedialistlocal.notifyitems(arraymediaitemlist,selectedlisttype);

            if(adaptermedialistpublished != null && selectedlisttype == 1)
                adaptermedialistpublished.notifyitems(arraymediaitemlist,selectedlisttype);

            for(int i = 0; i< arraymediafilterlist.size(); i++)
            {
                if(mediafilterobj.getmediafiltername().equalsIgnoreCase(arraymediafilterlist.get(i).getmediafiltername()))
                    arraymediafilterlist.get(i).setfilterselected(true);
                else
                    arraymediafilterlist.get(i).setfilterselected(false);
            }
        }
            if(adaptermediafilter != null)
                adaptermediafilter.notifyDataSetChanged();
    }

    public void senditem(String mediafilepath,String mediatoken,String mediatype,String mediathumbnailurl)
    {

        String send = getActivity().getResources().getString(R.string.send_details1)+"\n"+"\n"+
                getActivity().getResources().getString(R.string.send_details2);

        if(xdata.getinstance().getSetting(config.enablesendnotification).isEmpty() ||
                xdata.getinstance().getSetting(config.enablesendnotification).equalsIgnoreCase("0")) {
            baseactivity.getinstance().share_alert_dialog(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity().
                    getResources().getString(R.string.txt_send),send ,new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {
                    baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                            mediatype,false,mediathumbnailurl,mediafilepath,applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_send),true);
                }
                @Override
                public void onItemClicked(Object object, int type) {

                }
            });
            return;
        }
        baseactivity.getinstance().senditemsdialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                mediatype,false,mediathumbnailurl,mediafilepath,applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_send),true);
    }

    public void publishitem(String mediafilepath,String mediatoken,String mediatype,String mediathumbnailurl)
    {
        String publish = applicationviavideocomposer.getactivity().getResources().getString(R.string.publish_details1)+"\n"+"\n"+"\n"+
                applicationviavideocomposer.getactivity().getResources().getString(R.string.publish_details2);

        if(xdata.getinstance().getSetting(config.enableplubishnotification).isEmpty() ||
                xdata.getinstance().getSetting(config.enableplubishnotification).equalsIgnoreCase("0"))
        {
            baseactivity.getinstance().share_alert_dialog(applicationviavideocomposer.getactivity(),
                    applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_publish), publish, new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {

                            baseactivity.getinstance().videolocksharedialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                                    mediatype,false,mediathumbnailurl,mediafilepath,
                                    applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_publish),true,false);

                        }
                        @Override
                        public void onItemClicked(Object object, int type) {

                        }
                    });
            return;
        }
        baseactivity.getinstance().videolocksharedialog(applicationviavideocomposer.getactivity(),mediafilepath,mediatoken,
                mediatype,false,mediathumbnailurl,mediafilepath,
                applicationviavideocomposer.getactivity().getResources().getString(R.string.txt_publish),true,false);
        //baseactivity.getinstance().showsharepopupsub(mediafilepath,config.item_video,mediatoken,ismediatrimmed);
    }


    public void exportitem(video videoobj)
    {
        String export = applicationviavideocomposer.getactivity().getResources().getString(R.string.export_details1)+"\n"+"\n"+"\n"+
                applicationviavideocomposer.getactivity().getResources().getString(R.string.export_details2);

        if(xdata.getinstance().getSetting(config.enableexportnotification).isEmpty() ||
                xdata.getinstance().getSetting(config.enableexportnotification).equalsIgnoreCase("0")) {
            baseactivity.getinstance().share_alert_dialog(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity().
                    getResources().getString(R.string.txt_save),export ,new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {
                    checkwritepermission(videoobj);
                }
                @Override
                public void onItemClicked(Object object, int type) {
                }
            });
            return;
        }
        checkwritepermission(videoobj);
    }

    public void checkwritepermission(video videoobj)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (common.getstoragedeniedpermissions().isEmpty())
                showmediashareoptions(videoobj);
        }
        else
        {
            showmediashareoptions(videoobj);
        }
    }

    public void showmediashareoptions(video videoobj)
    {
        if(videoobj.getMediatype().equalsIgnoreCase(config.type_image))
            common.shareimage(applicationviavideocomposer.getactivity(),videoobj.getPath());
        else if(videoobj.getMediatype().equalsIgnoreCase(config.type_video))
            common.sharevideo(applicationviavideocomposer.getactivity(),videoobj.getPath());
        else if(videoobj.getMediatype().equalsIgnoreCase(config.type_audio))
            common.shareaudio(applicationviavideocomposer.getactivity(),videoobj.getPath());
    }
}
