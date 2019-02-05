package com.deeptruth.app.android.fragments;


import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.folderdirectoryspinneradapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class imagereaderfragment extends basefragment implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener {

    View rootview;

    @BindView(R.id.img_delete_media)
    ImageView img_delete_media;
    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    /*@BindView(R.id.layout_bottom)
    LinearLayout layout_bottom;*/
    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
    @BindView(R.id.content)
    LinearLayout linearLayout;

    @BindView(R.id.righthandle)
    ImageView righthandle;

    //tabdetails
    @BindView(R.id.spinner)
    Spinner spinnermediafolder;
    @BindView(R.id.txt_slot4)
    TextView txtslotmedia;
    @BindView(R.id.txt_slot5)
    TextView txtslotmeta;
    @BindView(R.id.txt_slot6)
    TextView txtslotencyption;
    @BindView(R.id.img_share_media)
    ImageView img_share_media;
    @BindView(R.id.img_edit_name)
    ImageView img_edit_name;
    @BindView(R.id.img_edit_notes)
    ImageView img_edit_notes;
    @BindView(R.id.layout_photoreader)
    RelativeLayout layout_photoreader;
    @BindView(R.id.txt_createdtime)
    TextView txt_createdtime;
    @BindView(R.id.layout_halfscrn)
    RelativeLayout layout_halfscrn;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
    @BindView(R.id.layout_photodetails)
    RelativeLayout layout_photodetails;
    @BindView(R.id.scrollview_detail)
    ScrollView scrollview_detail;
    @BindView(R.id.img_fullscreen)
    ImageView img_fullscreen;
    @BindView(R.id.edt_medianame)
    EditText edt_medianame;
    @BindView(R.id.edt_medianotes)
    EditText edt_medianotes;
    @BindView(R.id.layout_footer)
    RelativeLayout layout_footer;
    @BindView(R.id.tab_layout)
    LinearLayout tab_layout;
    @BindView(R.id.scrollview_encyption)
    ScrollView scrollView_encyrption;
    @BindView(R.id.scrollview_meta)
    ScrollView scrollview_meta;
    @BindView(R.id.txt_videoupdatetransactionid)
    TextView txt_blockchainid;
    @BindView(R.id.txt_hash_formula)
    TextView txt_blockid;
    @BindView(R.id.txt_data_hash)
    TextView txt_blocknumber;
    @BindView(R.id.txt_dictionary_hash)
    TextView txt_metahash;
    @BindView(R.id.txt_size)
    TextView tvsize;
    @BindView(R.id.txt_date)
    TextView tvdate;
    @BindView(R.id.txt_time)
    TextView tvtime;
    @BindView(R.id.layout_date)
    LinearLayout layout_date;
    @BindView(R.id.layout_time)
    LinearLayout layout_time;
    @BindView(R.id.layout_validating)
    LinearLayout layout_validating;
    @BindView(R.id.txt_section_validating_secondary)
    TextView txt_section_validating_secondary;
    @BindView(R.id.photorootview)
    RelativeLayout photorootview;
    @BindView(R.id.layout_dtls)
    LinearLayout layout_dtls;


    private String imageurl = null;
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    @BindView(R.id.tab_photoreader)
    ImageView tab_photoreader;
    private Handler myhandler;
    private Runnable myrunnable;
    String selectedmetrices = "", selectedhashes = "";
    private String keytype = config.prefs_md5;
    public int flingactionmindstvac;
    private static final int request_read_external_storage = 1;
    private final int flingactionmindspdvac = 10;
    int targetheight=0,previousheight=0;

    GoogleMap mgooglemap;

    customfonttextview tvaddress,tvlatitude,tvlongitude,tvaltitude,tvspeed,tvheading,tvtraveled,tvxaxis,tvyaxis,tvzaxis,tvphone,
            tvnetwork,tvconnection,tvversion,tvwifi,tvgpsaccuracy,tvscreen,tvcountry,tvcpuusage,tvbrightness,tvtimezone,
            tvmemoryusage,tvbluetooth,tvlocaltime,tvstoragefree,tvlanguage,tvuptime,tvbattery;

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    boolean ismediaplayer = false;
    String medianame = "",medianotes = "",mediafolder = "",mediatransectionid = "",latitude = "", longitude = "",screenheight = "",screenwidth = "",
    mediastartdevicedate = "",mediatime = "",mediasize="",lastsavedangle="";
    private float currentDegree = 0f;
    ImageView img_compass;
    adapteritemclick mcontrollernavigator;

    private BroadcastReceiver getmetadatabroadcastreceiver;

    @Override
    public int getlayoutid() {
        return R.layout.fragment_phototabreaderfrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {

            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            loadviewdata();

        }
        return rootview;
    }

    public void loadviewdata()
    {
        gethelper().drawerenabledisable(false);
        img_dotmenu.setOnClickListener(this);
        img_folder.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_arrow_back.setOnClickListener(this);
        img_delete_media.setOnClickListener(this);

        img_dotmenu.setVisibility(View.VISIBLE);
        img_folder.setVisibility(View.VISIBLE);
        img_arrow_back.setVisibility(View.VISIBLE);

        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            img_camera.setVisibility(View.GONE);
        }
        else
        {
            img_camera.setVisibility(View.VISIBLE);
        }


        righthandle.setOnTouchListener(this);
        tab_photoreader.setOnClickListener(this);

        //tabs_detail
        txtslotmedia.setText(getResources().getString(R.string.photo));
        img_share_media.setOnClickListener(this);
        img_edit_name.setOnClickListener(this);
        img_edit_notes.setOnClickListener(this);
        img_fullscreen.setOnClickListener(this);
        scrollview_detail.setVisibility(View.VISIBLE);
        tab_layout.setVisibility(View.VISIBLE);
        layout_footer.setVisibility(View.VISIBLE);
        img_fullscreen.setVisibility(View.VISIBLE);
        layout_photodetails.setVisibility(View.VISIBLE);
        layout_mediatype.setVisibility(View.VISIBLE);
        layout_date.setVisibility(View.VISIBLE);
        layout_time.setVisibility(View.VISIBLE);
        layout_photodetails.setOnClickListener(this);
        layout_dtls.setOnClickListener(this);

        layout_photoreader.post(new Runnable() {
            @Override
            public void run() {
                targetheight= layout_photoreader.getHeight();
                Log.e("targetheight",""+targetheight);
            }
        });
        tab_photoreader.post(new Runnable() {
            @Override
            public void run() {
                previousheight = tab_photoreader.getHeight();
                Log.e("previousheight",""+previousheight);
            }
        });

        tvaddress=rootview.findViewById(R.id.txt_address);
        tvlatitude=rootview.findViewById(R.id.txt_latitude);
        tvlongitude=rootview.findViewById(R.id.txt_longitude);
        tvaltitude=rootview.findViewById(R.id.txt_altitude);
        tvspeed=rootview.findViewById(R.id.txt_speed);
        tvheading=rootview.findViewById(R.id.txt_heading);
        tvtraveled=rootview.findViewById(R.id.txt_traveled);
        tvxaxis=rootview.findViewById(R.id.txt_xaxis);
        tvyaxis=rootview.findViewById(R.id.txt_yaxis);
        tvzaxis=rootview.findViewById(R.id.txt_zaxis);
        tvphone=rootview.findViewById(R.id.txt_phone);
        tvnetwork=rootview.findViewById(R.id.txt_network);
        tvconnection=rootview.findViewById(R.id.txt_connection);
        tvversion=rootview.findViewById(R.id.txt_version);
        tvwifi=rootview.findViewById(R.id.txt_wifi);
        tvgpsaccuracy=rootview.findViewById(R.id.txt_gps_accuracy);
        tvscreen=rootview.findViewById(R.id.txt_screen);
        tvcountry=rootview.findViewById(R.id.txt_country);
        tvcpuusage=rootview.findViewById(R.id.txt_cpu_usage);
        tvbrightness=rootview.findViewById(R.id.txt_brightness);
        tvtimezone=rootview.findViewById(R.id.txt_timezone);
        tvmemoryusage=rootview.findViewById(R.id.txt_memoryusage);
        tvbluetooth=rootview.findViewById(R.id.txt_bluetooth);
        tvlocaltime=rootview.findViewById(R.id.txt_localtime);
        tvstoragefree=rootview.findViewById(R.id.txt_storagefree);
        tvlanguage=rootview.findViewById(R.id.txt_language);
        tvuptime=rootview.findViewById(R.id.txt_uptime);
        tvbattery=rootview.findViewById(R.id.txt_battery);
        layout_googlemap= rootview.findViewById(R.id.layout_googlemap);
        googlemap= rootview.findViewById(R.id.googlemap);
        img_compass= rootview.findViewById(R.id.img_compass);

        righthandle.setOnTouchListener(this);

        String blockchainid = " ";
        String blockid = " ";
        String blocknumber = " ";
        common.setspannable(getResources().getString(R.string.blockchain_id), blockchainid, txt_blockchainid);
        common.setspannable(getResources().getString(R.string.block_id), blockid, txt_blockid);
        common.setspannable(getResources().getString(R.string.block_number), blocknumber, txt_blocknumber);
        edt_medianame.setEnabled(false);
        edt_medianame.setClickable(false);
        edt_medianame.setFocusable(false);
        edt_medianame.setFocusableInTouchMode(false);

        edt_medianotes.setEnabled(false);
        edt_medianotes.setClickable(false);
        edt_medianotes.setFocusable(false);
        edt_medianotes.setFocusableInTouchMode(false);

        flingactionmindstvac = common.getdrawerswipearea();
        txtslotencyption.setOnClickListener(this);
        txtslotmeta.setOnClickListener(this);
        txtslotmedia.setOnClickListener(this);
        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

        edt_medianotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                    // if (arraymediaitemlist.size() > 0) {
                    String medianotes = edt_medianotes.getText().toString();

                    if(!mediatransectionid.isEmpty())
                        updatemediainfo(mediatransectionid,edt_medianame.getText().toString(),medianotes);
                }
            }
        });

        edt_medianame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edt_medianame.setKeyListener(null);
                    v.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                    // if (arraymediaitemlist.size() > 0) {
                    String renamevalue = edt_medianame.getText().toString();
                    if(!mediatransectionid.isEmpty())
                        updatemediainfo(mediatransectionid,renamevalue,edt_medianotes.getText().toString());

                    editabletext();
                }
            }
        });

        txt_section_validating_secondary.setText(config.caution);
        txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
        //detuct keyboard is open or not
        photorootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = photorootview.getRootView().getHeight() - photorootview.getHeight();
                if (heightDiff > dpToPx(applicationviavideocomposer.getactivity().getApplication(), 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here
                    layout_halfscrn.setVisibility(View.GONE);
                    layout_footer.setVisibility(View.GONE);

                }else
                {
                    if(layout_halfscrn.getVisibility()==View.GONE){
                        layout_halfscrn.setVisibility(View.VISIBLE);
                        layout_footer.setVisibility(View.VISIBLE);
                        edt_medianame.setFocusable(false);
                        edt_medianotes.setFocusable(false);
                    }
                }
            }
        });
        loadmap();
        setmetriceshashesdata();
        setupimagedata();
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this, parent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_slot4:
                resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                scrollview_detail.setVisibility(View.VISIBLE);
                scrollView_encyrption.setVisibility(View.INVISIBLE);
                scrollview_meta.setVisibility(View.INVISIBLE);
                break;

            case R.id.txt_slot5:
                resetButtonViews(txtslotmeta, txtslotmedia, txtslotencyption);
                scrollview_meta.setVisibility(View.VISIBLE);
                scrollView_encyrption.setVisibility(View.INVISIBLE);
                scrollview_detail.setVisibility(View.INVISIBLE);
                break;
            case R.id.txt_slot6:
                resetButtonViews(txtslotencyption, txtslotmedia, txtslotmeta);
                scrollView_encyrption.setVisibility(View.VISIBLE);
                scrollview_detail.setVisibility(View.INVISIBLE);
                scrollview_meta.setVisibility(View.INVISIBLE);
                break;
            case R.id.img_edit_name:
              InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
              edt_medianame.setSelection(edt_medianame.getText().length());
              edt_medianame.setClickable(true);
              edt_medianame.setEnabled(true);
              edt_medianame.setFocusable(true);
              edt_medianame.setFocusableInTouchMode(true);
              edt_medianame.requestFocus();
                break;
            case R.id.img_edit_notes:
                InputMethodManager immn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                edt_medianotes.setSelection(edt_medianotes.getText().length());
                edt_medianotes.setClickable(true);
                edt_medianotes.setEnabled(true);
                edt_medianotes.setFocusable(true);
                edt_medianotes.setFocusableInTouchMode(true);
                edt_medianotes.requestFocus();

                break;
            case R.id.img_share_media:
                if (imageurl != null && (!imageurl.isEmpty()))
                    common.shareimage(getActivity(), imageurl);

                break;

            case R.id.img_dotmenu:
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
                break;
            case R.id.img_folder:
                myfolderfragment folderfragment=new myfolderfragment();
                gethelper().addFragment(folderfragment, false, true);
                break;
            case R.id.img_camera:
                launchbottombarfragment();
                break;

            case R.id.img_delete_media:
                showalertdialog(getActivity().getResources().getString(R.string.dlt_cnfm_photo));
                break;

            case R.id.img_arrow_back:
                gethelper().onBack();
                break;
            case R.id.img_fullscreen:
                img_fullscreen.setClickable(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        img_fullscreen.setClickable(true);
                    }
                }, 150);
                if(layout_photodetails.getVisibility()==View.VISIBLE){
                    gethelper().drawerenabledisable(true);
                    expand(tab_photoreader,100,targetheight);
                    layout_photodetails.setVisibility(View.GONE);
                    scrollview_detail.setVisibility(View.GONE);
                    scrollview_meta.setVisibility(View.GONE);
                    scrollView_encyrption.setVisibility(View.GONE);
                    tab_layout.setVisibility(View.GONE);
                    layout_footer.setVisibility(View.GONE);
                    layout_mediatype.setVisibility(View.GONE);
                    img_fullscreen.setVisibility(View.INVISIBLE);

                } else{
                    gethelper().drawerenabledisable(false);
                    collapse(tab_photoreader,100,previousheight);
                    layout_photodetails.setVisibility(View.VISIBLE);
                    tab_layout.setVisibility(View.VISIBLE);
                    scrollview_detail.setVisibility(View.VISIBLE);
                    layout_footer.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                    gethelper().updateactionbar(0,getResources().getColor(R.color.dark_blue_solid));
                    resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                }
                break;

            case R.id.tab_photoreader:
                Log.e("ontouch","ontouch");

                if(layout_photodetails.getVisibility()==View.GONE){
                    if(layout_footer.getVisibility()==(View.GONE)){
                        img_fullscreen.setVisibility(View.VISIBLE);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                        layout_mediatype.setVisibility(View.VISIBLE);
                        layout_footer.setVisibility(View.VISIBLE);
                    } else {
                        img_fullscreen.setVisibility(View.GONE);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                        layout_mediatype.setVisibility(View.GONE);
                        layout_footer.setVisibility(View.GONE);
                    }

                } else {
                    img_fullscreen.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                }

                break;
            case R.id.layout_dtls:
                 Log.e("ontouch","ontouchscrollview");
                if(layout_halfscrn.getVisibility() == View.GONE){
                    v.clearFocus();
                    InputMethodManager immm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
                break;
        }

    }

    public void showalertdialog(String message){
        new AlertDialog.Builder(getActivity(),R.style.customdialogtheme)
                .setTitle("Alert!!")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mcontrollernavigator != null)
                            mcontrollernavigator.onItemClicked(imageurl,2);

                        gethelper().onBack();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void launchbottombarfragment()
    {
        composeoptionspagerfragment fragbottombar=new composeoptionspagerfragment();
        gethelper().replaceFragment(fragbottombar, false, true);
    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3)
    {
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable1 = (Drawable) view1.getBackground();
            drawable1.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
            view1.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

            Drawable drawable2 = (Drawable) view2.getBackground();
            drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view2.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));

            Drawable drawable3 = (Drawable) view3.getBackground();
            drawable3.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view3.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));

        }
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid) {
            case R.id.img_share_icon:
                if (imageurl != null && (!imageurl.isEmpty()))
                    common.shareimage(getActivity(), imageurl);
                break;
            case R.id.img_upload_icon:

                break;
            case R.id.img_setting:
                framemetricssettings fragmatriclist = new framemetricssettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
            case R.id.img_menu:
                gethelper().onBack();
                break;

        }
    }

    public void setupimagedata() {
        imageurl = xdata.getinstance().getSetting("selectedphotourl");
        tvsize.setText(common.filesize(imageurl));
        if (imageurl != null && (!imageurl.isEmpty())) {
            setupphoto(Uri.parse(imageurl));
            new Thread() {
                public void run() {
                    try {
                        fetchmetadatafromdb();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void setupphoto(final Uri selectedphotouri) {
        if (imageurl != null) {
            tab_photoreader.setImageURI(selectedphotouri);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);
        Log.e("ondestroy", "distroy");

    }

    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setmetriceshashesdata() {
        if (myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);

        myhandler = new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {
                setmetricesgraphicaldata();
                myhandler.postDelayed(this, 3000);
            }
        };
        myhandler.post(myrunnable);
    }

    public void setmetricesgraphicaldata() {

        if (metricmainarraylist.size() > 0) {
            if (!metricmainarraylist.get(metricmainarraylist.size() - 1).isIsupdated()) {

                metricmainarraylist.get(metricmainarraylist.size() - 1).setIsupdated(true);

                double latt = 0, longg = 0;
                ArrayList<metricmodel> metricItemArraylist = metricmainarraylist.get(metricmainarraylist.size() - 1).getMetricItemArraylist();

               /* graphicaldrawerfragment.getencryptiondata(metricmainarraylist.get(0).getHashmethod(), metricmainarraylist.get(0).getVideostarttransactionid(),
                        metricmainarraylist.get(0).getValuehash(), metricmainarraylist.get(0).getMetahash());
*/
                layout_validating.setVisibility(View.VISIBLE);
                txt_section_validating_secondary.setText(config.verified);
                if(metricmainarraylist.get(0).getColor().equalsIgnoreCase(config.color_green))
                {
                    txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                            getactivity().getResources().getColor(R.color.green_background));
                }
                else if(metricmainarraylist.get(0).getColor().equalsIgnoreCase(config.color_red))
                {
                    txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                            getactivity().getResources().getColor(R.color.red));
                }
                else if(metricmainarraylist.get(0).getColor().equalsIgnoreCase(config.color_yellow))
                {
                    txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                            getactivity().getResources().getColor(R.color.yellow_background));
                }
                else
                {
                    txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                            getactivity().getResources().getColor(R.color.green_background));
                }

                common.setgraphicalblockchainvalue(config.blockchainid,metricmainarraylist.get(0).getVideostarttransactionid(),true);
                common.setgraphicalblockchainvalue(config.hashformula,metricmainarraylist.get(0).getHashmethod(),true);
                common.setgraphicalblockchainvalue(config.datahash,metricmainarraylist.get(0).getValuehash(),true);
                common.setgraphicalblockchainvalue(config.matrichash,metricmainarraylist.get(0).getMetahash(),true);

                common.setspannable(getResources().getString(R.string.blockchain_id)," "+metricmainarraylist.get(0).getVideostarttransactionid(), txt_blockchainid);
                common.setspannable(getResources().getString(R.string.block_id)," "+metricmainarraylist.get(0).getHashmethod(), txt_blockid);
                common.setspannable(getResources().getString(R.string.block_number)," "+metricmainarraylist.get(0).getValuehash(), txt_blocknumber);
                common.setspannable(getResources().getString(R.string.metrichash)," "+metricmainarraylist.get(0).getMetahash(), txt_metahash);

                for (int j = 0; j < metricItemArraylist.size(); j++) {
                    selectedmetrices = selectedmetrices + "\n" + metricItemArraylist.get(j).getMetricTrackKeyName() + " - " +
                            metricItemArraylist.get(j).getMetricTrackValue();

                    common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                            metricItemArraylist.get(j).getMetricTrackValue(), true);

                    setmetadatavalue(metricItemArraylist.get(j));

                }
                selectedmetrices = selectedmetrices + "\n";
            }

            if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                    (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));

        }
    }



    @Override
    public void onStart() {
        super.onStart();
        registerbroadcastreciver();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(getmetadatabroadcastreceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerbroadcastreciver() {
        IntentFilter intentFilter = new IntentFilter(config.composer_service_savemetadata);
        getmetadatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchmetadatafromdb();
            }
        };
        getActivity().registerReceiver(getmetadatabroadcastreceiver, intentFilter);
    }

    public void fetchmetadatafromdb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                    Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(imageurl));
                    String mediacompleteddate="";
                    if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
                    {
                        do{
                            mediacompleteddate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                            mediastartdevicedate = "" + cur.getString(cur.getColumnIndex("videostartdevicedate"));
                            medianame = "" + cur.getString(cur.getColumnIndex("media_name"));
                            medianotes =  "" + cur.getString(cur.getColumnIndex("media_notes"));
                            mediafolder =  "" + cur.getString(cur.getColumnIndex("media_folder"));
                            mediatransectionid = "" + cur.getString(cur.getColumnIndex("videostarttransactionid"));
                        }while(cur.moveToNext());
                    }

                    if (!mediacompleteddate.isEmpty()){

                        ArrayList<metadatahash> mitemlist=mdbhelper.getmediametadatabyfilename(common.getfilename(imageurl));
                        if(metricmainarraylist.size() > 0)
                        {

                            for(int i=0;i<mitemlist.size();i++)
                            {
                                String sequencehash = mitemlist.get(i).getSequencehash();
                                String hashmethod = mitemlist.get(i).getHashmethod();
                                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                                String serverdictionaryhash = mitemlist.get(i).getValuehash();
                                String color = mitemlist.get(i).getColor();
                                metricmainarraylist.set(i,new arraycontainer(hashmethod,videostarttransactionid,
                                        sequencehash,serverdictionaryhash,color));
                            }

                        }else{

                            for(int i=0;i<mitemlist.size();i++)
                            {
                                String metricdata=mitemlist.get(i).getMetricdata();
                                String sequencehash = mitemlist.get(i).getSequencehash();
                                String hashmethod = mitemlist.get(i).getHashmethod();
                                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                                String serverdictionaryhash = mitemlist.get(i).getValuehash();
                                String color = mitemlist.get(i).getColor();
                                parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash
                                ,color);
                            }

                            if((!mediastartdevicedate.isEmpty()&& mediastartdevicedate != null) && (!mediacompleteddate.isEmpty() && mediacompleteddate!= null)){

                                final String finalMediacompleteddate = mediacompleteddate;
                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            SimpleDateFormat formatted = null;
                                            Date mediadate = null;
                                            if(finalMediacompleteddate.contains("T"))
                                            {
                                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                                                mediadate = format.parse(finalMediacompleteddate);
                                            }
                                            else
                                            {
                                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                mediadate = format.parse(finalMediacompleteddate);
                                            }
                                            SimpleDateFormat formatteddate = new SimpleDateFormat("MM/dd/yyyy");
                                            SimpleDateFormat formattedtime = new SimpleDateFormat("hh:mm:ss a");
                                            tvdate.setText(formatteddate.format(mediadate));
                                            tvtime.setText(formattedtime.format(mediadate));
                                            txt_title_actionbarcomposer.setText(formatteddate.format(mediadate));
                                            txt_createdtime.setText(formattedtime.format(mediadate));
                                            edt_medianame.setText(medianame);
                                            edt_medianotes.setText(medianotes);

                                            if(mediafolder.trim().length() > 0)
                                                setfolderspinner();

                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        }
                        try
                        {
                            mdbhelper.close();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setfolderspinner()
    {
        final List<folder> folderitem=common.getalldirfolders();
        folderdirectoryspinneradapter adapter = new folderdirectoryspinneradapter(applicationviavideocomposer.getactivity(),
                R.layout.row_myfolderspinneradapter,folderitem);

        int selectedposition=0;
        final File foldername = new File(mediafolder);
        for(int i=0;i<folderitem.size();i++)
        {
            if(folderitem.get(i).getFoldername().equalsIgnoreCase(foldername.getName()))
            {
                selectedposition=i;
                break;
            }
        }

        spinnermediafolder.setAdapter(adapter);
        spinnermediafolder.setSelection(selectedposition,true);
        spinnermediafolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id)
            {
                if(! folderitem.get(position).getFoldername().equalsIgnoreCase(foldername.getName()))
                {
                    progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String folderpath=folderitem.get(position).getFolderdir();
                                if(! folderpath.equalsIgnoreCase(new File(imageurl).getParent()))
                                {
                                    if(common.movemediafile(new File(imageurl),new File(folderpath)))
                                    {
                                        File destinationmediafile = new File(folderpath + File.separator + new File(imageurl).getName());
                                        updatefilemediafolderdirectory(imageurl,destinationmediafile.getAbsolutePath(),folderpath);
                                        imageurl=destinationmediafile.getAbsolutePath();
                                        xdata.getinstance().saveSetting("selectedphotourl",imageurl);
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog.dismisswaitdialog();
                                    if(mcontrollernavigator != null)
                                        mcontrollernavigator.onItemClicked(imageurl,3);

                                    loadviewdata();
                                }
                            });
                        }
                    }).start();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash
            ,String color) {
        try {

            Object json = new JSONTokener(metadata).nextValue();
            JSONObject jsonobject=null;
            if(json instanceof JSONObject)
            {
                jsonobject=new JSONObject(metadata);
                ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                Iterator<String> myIter = jsonobject.keys();
                while (myIter.hasNext()) {
                    String key = myIter.next();
                    String value = jsonobject.optString(key);
                    metricmodel model = new metricmodel();
                    model.setMetricTrackKeyName(key);
                    model.setMetricTrackValue(value);
                    metricItemArraylist.add(model);
                }
                metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,
                        hashvalue,metahash,color));
            }
            else if(json instanceof JSONArray)
            {
                JSONArray array = new JSONArray(metadata);
                for (int j = 0; j < array.length(); j++) {
                    ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                    JSONObject object = array.getJSONObject(j);
                    Iterator<String> myIter = object.keys();
                    while (myIter.hasNext()) {
                        String key = myIter.next();
                        String value = object.optString(key);
                        metricmodel model = new metricmodel();
                        model.setMetricTrackKeyName(key);
                        model.setMetricTrackValue(value);
                        metricItemArraylist.add(model);
                    }
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,
                            hashvalue,metahash,color));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getmetadetareader() {
        myhandler = new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {

                fetchmetadatafromdb();

                myhandler.postDelayed(this, 5000);
            }
        };
        myhandler.post(myrunnable);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void editabletext(){
        Editable editableText=  edt_medianame.getEditableText();
                if(editableText!=null) {
                    edt_medianame.setInputType(InputType.TYPE_CLASS_TEXT);
                    edt_medianame.setEllipsize(TextUtils.TruncateAt.END);
                    edt_medianame.setSingleLine();
                }
                else
                {
                    edt_medianame.setEnabled(false);
                    edt_medianame.setClickable(false);
                    edt_medianame.setKeyListener(null);
                }
    }

    public void expand(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }


    public void setmetadatavalue(metricmodel metricItemArraylist){

        if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.gpslatitude)){
            latitude = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.latitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvlatitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.gpslongitude)){
            longitude = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.longitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvlongitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude)){
            common.setspannable(getResources().getString(R.string.altitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvaltitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.speed)){
            common.setspannable(getResources().getString(R.string.speed),"\n"+metricItemArraylist.getMetricTrackValue(), tvspeed);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase((config.heading))){
            common.setspannable(getResources().getString(R.string.heading),"\n"+metricItemArraylist.getMetricTrackValue(), tvheading);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase((config.distancetravelled))){
            common.setspannable(getResources().getString(R.string.traveled),"\n"+metricItemArraylist.getMetricTrackValue(), tvtraveled);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase((config.address))){
            common.setspannable("",metricItemArraylist.getMetricTrackValue(), tvaddress);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_x)){
            common.setspannable(getResources().getString(R.string.xaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvxaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_y)){
            common.setspannable(getResources().getString(R.string.yaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvyaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_z)){
            common.setspannable(getResources().getString(R.string.zaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvzaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("phonetype")){
            common.setspannable(getResources().getString(R.string.phone),"\n"+metricItemArraylist.getMetricTrackValue(), tvphone);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("carrier")){
            common.setspannable(getResources().getString(R.string.network),"\n"+metricItemArraylist.getMetricTrackValue(), tvnetwork);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("connectionspeed")){
            common.setspannable(getResources().getString(R.string.connection),"\n"+metricItemArraylist.getMetricTrackValue(), tvconnection);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("osversion")){
            common.setspannable(getResources().getString(R.string.version),"\n"+metricItemArraylist.getMetricTrackValue(), tvversion);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("wifiname")){
            common.setspannable(getResources().getString(R.string.wifi),"\n"+metricItemArraylist.getMetricTrackValue(), tvwifi);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("gpsaccuracy")){
            common.setspannable(getResources().getString(R.string.gpsaccuracy),"\n"+metricItemArraylist.getMetricTrackValue(), tvgpsaccuracy);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("screenwidth")){
           screenwidth = metricItemArraylist.getMetricTrackValue();
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("screenheight")){
           screenheight = metricItemArraylist.getMetricTrackValue();
           common.setspannable(getResources().getString(R.string.screen),"\n"+screenwidth+"x"+screenheight, tvscreen);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("country")){
            common.setspannable(getResources().getString(R.string.country),"\n"+metricItemArraylist.getMetricTrackValue(), tvcountry);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("cpuusagesystem")){
            common.setspannable(getResources().getString(R.string.cpuusage),"\n"+metricItemArraylist.getMetricTrackValue(), tvcpuusage);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("brightness")){
            common.setspannable(getResources().getString(R.string.brightness),"\n"+metricItemArraylist.getMetricTrackValue(), tvbrightness);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("timezone")){
            common.setspannable(getResources().getString(R.string.timezone),"\n"+metricItemArraylist.getMetricTrackValue(), tvtimezone);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("memoryusage")){
            common.setspannable(getResources().getString(R.string.memoryusage),"\n"+metricItemArraylist.getMetricTrackValue(), tvmemoryusage);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("bluetoothonoff")){
            common.setspannable(getResources().getString(R.string.bluetooth),"\n"+metricItemArraylist.getMetricTrackValue(), tvbluetooth);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("devicetime")){
            common.setspannable(getResources().getString(R.string.localtime),"\n"+metricItemArraylist.getMetricTrackValue(), tvlocaltime);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("freespace")){
            common.setspannable(getResources().getString(R.string.storagefree),"\n"+metricItemArraylist.getMetricTrackValue(), tvstoragefree);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("devicelanguage")){
            common.setspannable(getResources().getString(R.string.language),"\n"+metricItemArraylist.getMetricTrackValue(), tvlanguage);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("systemuptime")){
            common.setspannable(getResources().getString(R.string.uptime),"\n"+metricItemArraylist.getMetricTrackValue(), tvuptime);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("battery")){
            common.setspannable(getResources().getString(R.string.battery),"\n"+metricItemArraylist.getMetricTrackValue(), tvbattery);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("deviceorientation")){

            String strdegree=xdata.getinstance().getSetting(config.orientation);
            if(! strdegree.equals(lastsavedangle))
            {
                if(strdegree.equalsIgnoreCase("NA"))
                    strdegree="0.0";

                int degree = Math.abs((int)Double.parseDouble(strdegree));
                rotatecompass(degree);
            }
            lastsavedangle=strdegree;
        }

        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)){
            if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                    (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                drawmappoints(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
            Log.e("drawpoints","drawvalues");
        }
    }

    private void loadmap() {
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.googlemap, mapFragment).commit();

        if (mgooglemap == null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setMap(googleMap);
                }
            });
        } else {
            setMap(mgooglemap);
        }
    }

    private void setMap(GoogleMap googleMap) {
        this.mgooglemap = googleMap;
        this.mgooglemap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        /*points.add(new LatLng(26.235896,74.24235896));
        points.add(new LatLng(26.34235896,74.24235896));
        points.add(new LatLng(26.232435896,74.424235896));
        points.add(new LatLng(26.22235896,74.2325896));
        points.add(new LatLng(26.454235896,74.24235896));
        points.add(new LatLng(26.534235896,74.24235896));
        points.add(new LatLng(26.5565235896,74.42235896));
        points.add(new LatLng(26.67235896,74.23235896));
        points.add(new LatLng(26.878235896,74.22135896));
        points.add(new LatLng(26.45235896,74.23335896));
        points.add(new LatLng(26.33235896,74.22335896));
        polylineOptions.addAll(points);
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(20);
        Polyline polyline =mgooglemap.addPolyline(polylineOptions);
        //polyline.setEndCap(new RoundCap());
        //polyline.setJointType(JointType.ROUND);
         polyline.setPattern(PATTERN_POLYLINE_DOTTED);*/

    }

    //https://stackoverflow.com/questions/32905939/how-to-customize-the-polyline-in-google-map/46559529

    private void populateUserCurrentLocation(final LatLng location) {
        // DeviceUser user = DeviceUserManager.getInstance().getUser();
        if (mgooglemap == null)
            return;

        googlemap.setVisibility(View.VISIBLE);


        //polyline.setPattern(PATTERN_POLYLINE_DOTTED);

        /*Polygon polygon = mgooglemap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(location));
        polygon.setTag("beta");


        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);*/

        mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 15));
        if (ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            if(!ismediaplayer)
            {
                mgooglemap.setMyLocationEnabled(true);
            }
            else
            {
                mgooglemap.setMyLocationEnabled(false);
            }
            mgooglemap.getUiSettings().setZoomControlsEnabled(false);
            mgooglemap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
                Log.e("GraphicalLatLng",""+latlng.latitude+" "+latlng.longitude);
                {
                    /*points.add(latlng);
                    polylineOptions.addAll(points);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.width(20);
                    Polyline polyline =mgooglemap.addPolyline(polylineOptions);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.DEFAULT);*/

                    mgooglemap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.horizontalline)));
                }
                /*{
                    points.add(latlng);
                    polylineOptions.addAll(points);
                    polylineOptions.color(Color.WHITE);
                    polylineOptions.width(20);
                    Polyline polyline =mgooglemap.addPolyline(polylineOptions);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.ROUND);
                }*/
        }
    }
    public void rotatecompass(int degree)
    {
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);
        ra.setFillAfter(true);
        img_compass.startAnimation(ra);
        currentDegree = -degree;
    }

    public void updatemediainfo(String transactionid,String medianame,String medianotes)
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
        mdbhelper.updatemediainfofromstarttransactionid(transactionid,medianame,medianotes);
        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(mcontrollernavigator != null)
            mcontrollernavigator.onItemClicked(null,1);
    }

    public void setdata(adapteritemclick mcontrollernavigator) {
        this.mcontrollernavigator = mcontrollernavigator;
    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}