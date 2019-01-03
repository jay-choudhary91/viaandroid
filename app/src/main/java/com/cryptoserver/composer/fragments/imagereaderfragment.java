package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.arraycontainer;
import com.cryptoserver.composer.models.metadatahash;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.xdata;
import com.cryptoserver.composer.views.customfonttextview;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class imagereaderfragment extends basefragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener ,AdapterView.OnItemSelectedListener {

    View rootview;
    ImageView handle,img_edit_name,img_edit_notes,img_share_media;
    RecyclerView recyview_hashes;
    RecyclerView recyview_metrices;
    ImageView handleimageview, righthandle;

    LinearLayout linearLayout;
    FrameLayout fragment_graphic_container;
    TextView txtslotmedia, txtslotmeta, txtslotencyption;

    TextView txtSlot1;
    TextView txtSlot2;
    TextView txtSlot3, txt_metrics, txt_hashes;
    ScrollView scrollview_metrices, scrollview_hashes;
    LinearLayout layout_bottom, layout_drawer;
    videoframeadapter mmetricesadapter, mhashesadapter;
    private LinearLayoutManager mLayoutManager;
    private boolean isdraweropen = false;
    public int selectedsection = 1;
    graphicalfragment fragmentgraphic;
    ArrayList<videomodel> mmetricsitems = new ArrayList<>();
    ArrayList<videomodel> mhashesitems = new ArrayList<>();
    private int REQUESTCODE_PICK = 301;
    private Uri selectedphotouri = null;
    private String imageurl = null;
    private ArrayList<videomodel> mainphotoframes = new ArrayList<>();
    private ArrayList<videomodel> mphotoframes = new ArrayList<>();
    private ArrayList<videomodel> mallframes = new ArrayList<>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    @BindView(R.id.tab_photoreader)
    ImageView tab_photoreader;
    private Handler myhandler;
    private Runnable myrunnable;
    String selectedmetrices = "", selectedhashes = "";
    private String keytype = config.prefs_md5, firsthash = "";
    private boolean suspendframequeue = false, suspendbitmapqueue = false, isnewphotofound = false;
    private boolean ishashprocessing = false;
    JSONArray metadatametricesjson = new JSONArray();
    public int flingactionmindstvac;
    private static final int request_read_external_storage = 1;
    private final int flingactionmindspdvac = 10;
    Spinner photospinner;
    ScrollView scrollView_encyrption,scrollview_detail,scrollview_meta;

    GoogleMap mgooglemap;
    EditText edt_medianame,edt_medianotes;
    TextView txt_blockchainid, txt_blockid, txt_blocknumber,txt_metahash,tvsize,tvdate,tvtime;
    customfonttextview tvaddress,tvlatitude,tvlongitude,tvaltitude,tvspeed,tvheading,tvtraveled,tvxaxis,tvyaxis,tvzaxis,tvphone,
            tvnetwork,tvconnection,tvversion,tvwifi,tvgpsaccuracy,tvscreen,tvcountry,tvcpuusage,tvbrightness,tvtimezone,
            tvmemoryusage,tvbluetooth,tvlocaltime,tvstoragefree,tvlanguage,tvuptime,tvbattery;
    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    boolean ismediaplayer = false;
    String medianame = "",medianotes = "",mediafolder = "",mediatransectionid = "",latitude = "", longitude = "",screenheight = "",screenwidth = "",
    mediadate = "",mediatime = "",mediasize;



    private BroadcastReceiver getmetadatabroadcastreceiver, getencryptionmetadatabroadcastreceiver;

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
            List<String> categories = new ArrayList<String>();
            categories.add("All Media");
            categories.add("Photo");
            categories.add("Video");
            categories.add("Audio");

            handle = (ImageView) rootview.findViewById(R.id.handle);
            layout_bottom = (LinearLayout) rootview.findViewById(R.id.layout_bottom);
            layout_drawer = (LinearLayout) rootview.findViewById(R.id.layout_drawer);
            txtslotmedia = rootview.findViewById(R.id.txt_slot4);
            txtslotmeta = rootview.findViewById(R.id.txt_slot5);
            txtslotencyption = rootview.findViewById(R.id.txt_slot6);
            txtSlot1 = (TextView) rootview.findViewById(R.id.txt_slot1);
            txtSlot2 = (TextView) rootview.findViewById(R.id.txt_slot2);
            txtSlot3 = (TextView) rootview.findViewById(R.id.txt_slot3);
            txt_metrics = (TextView) rootview.findViewById(R.id.txt_metrics);
            txt_hashes = (TextView) rootview.findViewById(R.id.txt_hashes);
            scrollview_metrices = (ScrollView) rootview.findViewById(R.id.scrollview_metrices);
            scrollview_hashes = (ScrollView) rootview.findViewById(R.id.scrollview_hashes);
            fragment_graphic_container = (FrameLayout) rootview.findViewById(R.id.fragment_graphic_container);
            linearLayout = rootview.findViewById(R.id.content);
            handleimageview = rootview.findViewById(R.id.handle);
            righthandle = rootview.findViewById(R.id.righthandle);

            recyview_hashes = (RecyclerView) rootview.findViewById(R.id.recyview_item);
            recyview_metrices = (RecyclerView) rootview.findViewById(R.id.recyview_metrices);
            photospinner = rootview.findViewById(R.id.spinner);

            photospinner.setOnItemSelectedListener(this);


            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);

            //tabs_detail
            img_share_media=rootview.findViewById(R.id.img_share_media);
            img_edit_name=rootview.findViewById(R.id.img_edit_name);
            img_edit_notes=rootview.findViewById(R.id.img_edit_notes);
            edt_medianame=rootview.findViewById(R.id.edt_medianame);
            edt_medianotes=rootview.findViewById(R.id.edt_medianotes);
            img_share_media.setOnClickListener(this);
            img_edit_name.setOnClickListener(this);
            img_edit_notes.setOnClickListener(this);
            txt_blockchainid = rootview.findViewById(R.id.txt_videoupdatetransactionid);
            txt_blockid = rootview.findViewById(R.id.txt_hash_formula);
            txt_blocknumber = rootview.findViewById(R.id.txt_data_hash);
            txt_metahash = rootview.findViewById(R.id.txt_dictionary_hash);
            scrollView_encyrption = rootview.findViewById(R.id.scrollview_encyption);
            scrollview_meta = rootview.findViewById(R.id.scrollview_meta);
            tvsize = rootview.findViewById(R.id.txt_size);
            tvdate = rootview.findViewById(R.id.txt_date);
            tvtime = rootview.findViewById(R.id.txt_time);

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

            photospinner.setOnItemSelectedListener(this);
            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);

            scrollview_detail=rootview.findViewById(R.id.scrollview_detail);
            String blockchainid = " EOLZ03D0K91734JADFL2";
            String blockid = " ZD38MGUQ4FADLK5A";
            String blocknumber = " 4";
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

            handleimageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation rightswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.right_slide);
                    linearLayout.startAnimation(rightswipe);
                    handleimageview.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    rightswipe.start();
                    righthandle.setVisibility(View.VISIBLE);
                    rightswipe.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            righthandle.setImageResource(R.drawable.righthandle);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            righthandle.setImageResource(R.drawable.lefthandle);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });

            righthandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation leftswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.left_slide);
                    linearLayout.startAnimation(leftswipe);
                    linearLayout.setVisibility(View.INVISIBLE);
                    righthandle.setVisibility(View.VISIBLE);
                    handleimageview.setVisibility(View.GONE);
                    leftswipe.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            handleimageview.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
            flingactionmindstvac = common.getdrawerswipearea();

            txtSlot1.setOnClickListener(this);
            txtSlot2.setOnClickListener(this);
            txtSlot3.setOnClickListener(this);

            txtslotencyption.setOnClickListener(this);
            txtslotmeta.setOnClickListener(this);
            txtslotmedia.setOnClickListener(this);
            resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

            resetButtonViews(txtSlot1, txtSlot2, txtSlot3);
            txtSlot1.setVisibility(View.VISIBLE);
            txtSlot2.setVisibility(View.VISIBLE);
            txtSlot3.setVisibility(View.VISIBLE);
            txt_metrics.setVisibility(View.INVISIBLE);
            txt_hashes.setVisibility(View.INVISIBLE);
            recyview_hashes.setVisibility(View.VISIBLE);
            recyview_metrices.setVisibility(View.INVISIBLE);
            scrollview_metrices.setVisibility(View.INVISIBLE);
            scrollview_hashes.setVisibility(View.INVISIBLE);
            fragment_graphic_container.setVisibility(View.INVISIBLE);

            {
                mhashesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mhashesitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyview_hashes.setLayoutManager(mLayoutManager);
                recyview_hashes.setItemAnimator(new DefaultItemAnimator());
                recyview_hashes.setAdapter(mhashesadapter);
            }

            {
                mmetricesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mmetricsitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                mLayoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                recyview_metrices.setLayoutManager(mLayoutManager);
                recyview_metrices.setItemAnimator(new DefaultItemAnimator());
                recyview_metrices.setAdapter(mmetricesadapter);
                implementscrolllistener();
            }

            if (fragmentgraphic == null) {
                fragmentgraphic = new graphicalfragment();
                fragmentgraphic.setphotocapture(true);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_graphic_container, fragmentgraphic);
                transaction.commit();
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adaptermedialist to spinner
            photospinner.setAdapter(dataAdapter);

            loadmap();
            setmetriceshashesdata();
            setupimagedata();

            edt_medianotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        v.setFocusable(false);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                        // if (arrayvideolist.size() > 0) {
                        String medianotes = edt_medianotes.getText().toString();

                        if(!mediatransectionid.isEmpty())
                            updatemediainfo(mediatransectionid,edt_medianame.getText().toString(),medianotes,"allmedia");
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
                       // if (arrayvideolist.size() > 0) {
                            String renamevalue = edt_medianame.getText().toString();
                            if(!mediatransectionid.isEmpty())
                                updatemediainfo(mediatransectionid,renamevalue,edt_medianotes.getText().toString(),"allmedia");

                            editabletext();
                     //   edt_medianame.setKeyListener(null);
                     //   }
                    }
                }
            });

            edt_medianame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if  ((actionId == EditorInfo.IME_ACTION_DONE)) {
                  /*  Log.i(TAG,"Here you can write the code");*/
                      //  if (arrayvideolist.size() > 0) {
                        String renamevalue = edt_medianame.getText().toString();
                        editabletext();


                        edt_medianame.setKeyListener(null);
                        }
                    // }
                    return false;
                }
            });
        }
        return rootview;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this, parent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {
            case R.id.handle:
                flingswipe.onTouchEvent(motionEvent);
                break;

            case R.id.righthandle:
                flingswipe.onTouchEvent(motionEvent);
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_slot1:
                if (selectedsection != 1) {
                    selectedsection = 1;
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);

                    recyview_hashes.setVisibility(View.VISIBLE);
                    recyview_metrices.setVisibility(View.INVISIBLE);
                    fragment_graphic_container.setVisibility(View.INVISIBLE);

                    txt_metrics.setVisibility(View.INVISIBLE);
                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);
                    resetButtonViews(txtSlot1, txtSlot2, txtSlot3);
                }

                break;

            case R.id.txt_slot2:
                if (selectedsection != 2) {
                    selectedsection = 2;
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    fragment_graphic_container.setVisibility(View.INVISIBLE);

                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);

                    recyview_metrices.setVisibility(View.VISIBLE);
                    recyview_hashes.setVisibility(View.INVISIBLE);

                    resetButtonViews(txtSlot2, txtSlot1, txtSlot3);
                }

                break;

            case R.id.txt_slot3:
                if (selectedsection != 3) {
                    selectedsection = 3;
                    fragment_graphic_container.setVisibility(View.VISIBLE);
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    recyview_metrices.setVisibility(View.INVISIBLE);
                    recyview_hashes.setVisibility(View.INVISIBLE);
                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);
                    resetButtonViews(txtSlot3, txtSlot1, txtSlot2);

                    /*if (fragmentgraphic != null)
                        fragmentgraphic.setphotocapture(true);*/
                }
                break;

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
              edt_medianame.setClickable(true);
              edt_medianame.setEnabled(true);
              edt_medianame.setFocusable(true);
              edt_medianame.setFocusableInTouchMode(true);
              edt_medianame.requestFocus();
              InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.img_edit_notes:
                edt_medianotes.setClickable(true);
                edt_medianotes.setEnabled(true);
                edt_medianotes.setFocusable(true);
                edt_medianotes.setFocusableInTouchMode(true);
                edt_medianotes.requestFocus();
                InputMethodManager immn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                break;
            case R.id.img_share_media:
                if (imageurl != null && (!imageurl.isEmpty()))
                    common.shareimage(getActivity(), imageurl);

                scrollview_meta.setVisibility(View.GONE);
                break;
        }

    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3) {
        view1.setBackgroundResource(R.color.blue);
        view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));

        view2.setBackgroundResource(R.color.white);
        view2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));

        view3.setBackgroundResource(R.color.white);
        view3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.blue));
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal,
                               float flingActionYcoSpdPsgVal) {
            if (fstMsnEvtPsgVal.getX() - lstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Right to Left fling
                swiperighttoleft();
                return false;
            } else if (lstMsnEvtPsgVal.getX() - fstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Left to Right fling
                swipelefttoright();
                return false;
            }

            if (fstMsnEvtPsgVal.getY() - lstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Bottom to Top fling

                return false;
            } else if (lstMsnEvtPsgVal.getY() - fstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Top to Bottom fling

                return false;
            }
            return false;
        }
    });

    public void swipelefttoright() {
        isdraweropen = true;
        Animation rightswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.right_slide);
        linearLayout.startAnimation(rightswipe);
        handleimageview.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        rightswipe.start();
        righthandle.setVisibility(View.VISIBLE);
        rightswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                righthandle.setImageResource(R.drawable.righthandle);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                righthandle.setImageResource(R.drawable.lefthandle);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void swiperighttoleft() {
        isdraweropen = false;
        Animation leftswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.left_slide);
        linearLayout.startAnimation(leftswipe);
        linearLayout.setVisibility(View.INVISIBLE);
        righthandle.setVisibility(View.VISIBLE);
        handleimageview.setVisibility(View.GONE);
        leftswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handleimageview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void implementscrolllistener() {
        recyview_metrices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
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
                //  checkwritestoragepermission();
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
        if (imageurl != null && (!imageurl.isEmpty())) {
            setupphoto(Uri.parse(imageurl));
            new Thread() {
                public void run() {
                    try {
                        findmediafirsthash();
                        getmediadata();
                        if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)) {
                            getmetadetareader();
                        }
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
                boolean graphicopen = false;

                if (!isdraweropen) {
                    if ((recyview_hashes.getVisibility() == View.VISIBLE) && (!selectedhashes.trim().isEmpty())) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mhashesitems.add(new videomodel(selectedhashes));
                                mhashesadapter.notifyItemChanged(mhashesitems.size() - 1);
                                selectedhashes = "";
                            }
                        });
                    }

                    if ((tab_photoreader != null) && (!selectedmetrices.toString().trim().isEmpty())) {
                        mmetricsitems.add(new videomodel(selectedmetrices));
                        mmetricesadapter.notifyItemChanged(mmetricsitems.size() - 1);
                        selectedmetrices = "";
                    }

                    setmetricesgraphicaldata();

                    if ((fragment_graphic_container.getVisibility() == View.VISIBLE))
                        graphicopen = true;
                }

                if (fragmentgraphic != null)
                    fragmentgraphic.setdrawerproperty(graphicopen);
                fragmentgraphic.setmetricesdata();

                myhandler.postDelayed(this, 1000);
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

                fragmentgraphic.getencryptiondata(metricmainarraylist.get(0).getHashmethod(), metricmainarraylist.get(0).getVideostarttransactionid(),
                        metricmainarraylist.get(0).getValuehash(), metricmainarraylist.get(0).getMetahash());

                common.setspannable(getResources().getString(R.string.blockchain_id),metricmainarraylist.get(0).getVideostarttransactionid(), txt_blockchainid);
                common.setspannable(getResources().getString(R.string.block_id),metricmainarraylist.get(0).getHashmethod(), txt_blockid);
                common.setspannable(getResources().getString(R.string.block_number),metricmainarraylist.get(0).getValuehash(), txt_blocknumber);
                common.setspannable(getResources().getString(R.string.metrichash),metricmainarraylist.get(0).getMetahash(), txt_metahash);

                for (int j = 0; j < metricItemArraylist.size(); j++) {
                    selectedmetrices = selectedmetrices + "\n" + metricItemArraylist.get(j).getMetricTrackKeyName() + " - " +
                            metricItemArraylist.get(j).getMetricTrackValue();

                    common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                            metricItemArraylist.get(j).getMetricTrackValue(), true);

                    setmetadatavalue(metricItemArraylist.get(j));

                    if (mgooglemap != null) {
                        if (metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude")) {
                            if (!metricItemArraylist.get(j).getMetricTrackValue().equalsIgnoreCase("NA")) {
                                latt = Double.parseDouble(metricItemArraylist.get(j).getMetricTrackValue());
                                if (longg != 0) {
                                    if (mgooglemap != null) {
                                        drawmappoints(new LatLng(latt, longg));
                                        latt = 0;
                                        longg = 0;
                                    }
                                }
                            }
                        }
                        if (metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpslongitude")) {
                            if (!metricItemArraylist.get(j).getMetricTrackValue().equalsIgnoreCase("NA")) {
                                longg = Double.parseDouble(metricItemArraylist.get(j).getMetricTrackValue());
                                if (latt != 0) {
                                    if (mgooglemap != null) {
                                        drawmappoints(new LatLng(latt, longg));
                                        latt = 0;
                                        longg = 0;
                                    }
                                }
                            }
                        }

                    }
                }
                selectedmetrices = selectedmetrices + "\n";
            }

            if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                    (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));


            if (fragment_graphic_container.getVisibility() == View.VISIBLE) {
                if (fragmentgraphic != null)
                    fragmentgraphic.setmetricesdata();

            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        registerbroadcastreciver();
        registerbroadcastreciverforencryptionmetadata();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(getmetadatabroadcastreceiver);
            applicationviavideocomposer.getactivity().unregisterReceiver(getencryptionmetadatabroadcastreceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerbroadcastreciver() {
        IntentFilter intentFilter = null;
        /*if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            //intentFilter = new IntentFilter(config.reader_service_getmetadata);
        }
        else
        {

        }*/

        intentFilter = new IntentFilter(config.composer_service_savemetadata);

        getmetadatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Thread thread = new Thread() {
                    public void run() {
                        if (mhashesitems.size() == 0)
                            getmediadata();
                    }
                };
                thread.start();
            }
        };
        getActivity().registerReceiver(getmetadatabroadcastreceiver, intentFilter);
    }


    public void registerbroadcastreciverforencryptionmetadata() {
        IntentFilter intentFilter = new IntentFilter(config.composer_service_getencryptionmetadata);
        getencryptionmetadatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getmediadata();
            }
        };
        getActivity().registerReceiver(getencryptionmetadatabroadcastreceiver, intentFilter);
    }

    public void getmediadata() {
        try {
            databasemanager mdbhelper = null;
            String videoid = "", videotoken = "", audiostatus = "";
            if (mdbhelper == null) {
                mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (!firsthash.trim().isEmpty()) {
                Cursor mediainfocursor = mdbhelper.getmediainfobyfirsthash(firsthash);

                if (mediainfocursor != null && mediainfocursor.getCount() > 0) {
                    if (mediainfocursor.moveToFirst()) {
                        do {
                            audiostatus = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("status"));
                            mediadate = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("videostartdevicedate"));
                            medianame = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("media_name"));
                            medianotes =  "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("media_notes"));
                            mediafolder =  "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("media_folder"));
                            mediatransectionid = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("videostarttransactionid"));

                            videoid = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("videoid"));
                        } while (mediainfocursor.moveToNext());
                    }
                }
            }


            String completedate = mdbhelper.getcompletedate(firsthash);
            if (!completedate.isEmpty()) {

                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textfetchdata.setVisibility(View.GONE);
                    }
                });*/
                ArrayList<metadatahash> mitemlist = mdbhelper.getmediametadata(firsthash);
                //metricmainarraylist.clear();
                String framelabel = "";

                if (metricmainarraylist.size() > 0 && BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer)) {

                    for (int i = 0; i < mitemlist.size(); i++) {
                        String sequencehash = mitemlist.get(i).getSequencehash();
                        String hashmethod = mitemlist.get(i).getHashmethod();
                        String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                        String serverdictionaryhash = mitemlist.get(i).getValuehash();

                        metricmainarraylist.set(i, new arraycontainer(hashmethod, videostarttransactionid, sequencehash, serverdictionaryhash));
                    }

                } else {

                    if (audiostatus.equalsIgnoreCase("complete") && metricmainarraylist.size() == 0) {

                        if(!medianame.isEmpty()){
                            int index =  medianame.lastIndexOf('.');
                            if(index >=0)
                                medianame = medianame.substring(0, medianame.lastIndexOf('.'));

                            edt_medianame.setText(medianame);
                        }

                        if(!medianotes.isEmpty())
                            edt_medianotes.setText(medianotes);

                           setmetricdata(mitemlist);
                    } else {

                        if(!mediadate.isEmpty()){
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mediadate);
                            String newString = new SimpleDateFormat("H:mm").format(date);

                            tvtime.setText(newString);

                        }

                        if(!medianame.isEmpty()){
                            int index =  medianame.lastIndexOf('.');
                            if(index >=0)
                                medianame = medianame.substring(0, medianame.lastIndexOf('.'));

                            edt_medianame.setText(medianame);
                        }

                        if(!medianotes.isEmpty())
                            edt_medianotes.setText(medianotes);

                        setmetricdata(mitemlist);
                    }
                }

                try {
                    mdbhelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* mhashesitems.clear();
                        mhashesadapter.notifyDataSetChanged();*/

                        mphotoframes.add(new videomodel(selectedhashes));
                        mhashesadapter.notifyDataSetChanged();
                        recyview_hashes.scrollToPosition(mhashesitems.size() - 1);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmetricdata(ArrayList<metadatahash> mitemlist) {
        String framelabel = "";
        for (int i = 0; i < mitemlist.size(); i++) {
            String metahash = "";
            String metricdata = mitemlist.get(i).getMetricdata();
            String valuehash = mitemlist.get(i).getSequencehash();
            String hashmethod = mitemlist.get(i).getHashmethod();
            String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();

            if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer)) {
                metahash = mitemlist.get(i).getValuehash();
            } else {
                metahash = mitemlist.get(i).getMetahash();
            }
            parsemetadata(metricdata, valuehash, hashmethod, videostarttransactionid, metahash);
            selectedhashes = selectedhashes + "\n";
            framelabel = "Frame ";
            selectedhashes = selectedhashes + framelabel + mitemlist.get(i).getSequenceno() + " " + mitemlist.get(i).getHashmethod() +
                    ": " + mitemlist.get(i).getSequencehash();
        }
    }


    public void parsemetadata(String metadata, String valuehash, String hashmethod, String videostarttransactionid, String metahash) {
        try {

            if (BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)) {
                JSONObject object = new JSONObject(metadata);
                Iterator<String> myIter = object.keys();
                ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                while (myIter.hasNext()) {
                    String key = myIter.next();
                    String value = object.optString(key);
                    metricmodel model = new metricmodel();
                    model.setMetricTrackKeyName(key);
                    model.setMetricTrackValue(value);
                    metricItemArraylist.add(model);
                }
                metricmainarraylist.add(new arraycontainer(metricItemArraylist, hashmethod, videostarttransactionid, valuehash, metahash));

            } else {
                JSONArray array = new JSONArray(metadata);
                if (array.length() > 0) {
                    JSONObject object = array.getJSONObject(0);
                    Iterator<String> myIter = object.keys();
                    ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                    while (myIter.hasNext()) {
                        String key = myIter.next();
                        String value = object.optString(key);
                        metricmodel model = new metricmodel();
                        model.setMetricTrackKeyName(key);
                        model.setMetricTrackValue(value);
                        metricItemArraylist.add(model);
                    }
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist, hashmethod, videostarttransactionid, valuehash, metahash));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String findmediafirsthash() {
        firsthash = md5.fileToMD5(imageurl);
        /*if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            if(! firsthash.trim().isEmpty())
            {
                Intent intent = new Intent(applicationviavideocomposer.getactivity(), readmediadataservice.class);
                intent.putExtra("firsthash", firsthash);
                intent.putExtra("mediapath", imageurl);
                intent.putExtra("keytype",keytype);
                intent.putExtra("mediatype","image");
                applicationviavideocomposer.getactivity().startService(intent);
            }
        }*/
        return firsthash;
    }

    public void getmetadetareader() {
        myhandler = new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {

                getmediadata();

                myhandler.postDelayed(this, 5000);
            }
        };
        myhandler.post(myrunnable);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //  Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
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

    public void setmetadatavalue(metricmodel metricItemArraylist){

        if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("gpslatitude")){
            latitude = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.latitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvlatitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("gpslongitude")){
            longitude = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.longitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvlongitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("gpsaltitude")){
            common.setspannable(getResources().getString(R.string.altitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvaltitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("speed")){
            common.setspannable(getResources().getString(R.string.speed),"\n"+metricItemArraylist.getMetricTrackValue(), tvspeed);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("heading")){
            common.setspannable(getResources().getString(R.string.heading),"\n"+metricItemArraylist.getMetricTrackValue(), tvheading);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("distancetravelled")){
            common.setspannable(getResources().getString(R.string.traveled),"\n"+metricItemArraylist.getMetricTrackValue(), tvtraveled);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("address")){
            common.setspannable("",metricItemArraylist.getMetricTrackValue(), tvaddress);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("acceleration.x")){
            common.setspannable(getResources().getString(R.string.xaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvxaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("acceleration.y")){
            common.setspannable(getResources().getString(R.string.yaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvyaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("acceleration.z")){
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

    public void updatemediainfo(String transactionid,String medianame,String medianotes,String mediafolder)
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
        mdbhelper.updatemediainfofromstarttransactionid(transactionid,medianame,medianotes,mediafolder);
        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}