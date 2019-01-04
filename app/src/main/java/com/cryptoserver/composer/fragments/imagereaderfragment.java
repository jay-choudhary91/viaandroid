package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
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
import com.cryptoserver.composer.utils.FullDrawerLayout;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class imagereaderfragment extends basefragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener ,AdapterView.OnItemSelectedListener {

    View rootview;


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
    @BindView(R.id.txt_slot1)
    TextView txtSlot1;
    @BindView(R.id.txt_slot2)
    TextView txtSlot2;
    @BindView(R.id.txt_slot3)
    TextView txtSlot3;
    @BindView(R.id.txt_metrics)
    TextView txt_metrics;
    @BindView(R.id.txt_hashes)
    TextView txt_hashes;
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
    @BindView(R.id.scrollview_metrices)
    ScrollView scrollview_metrices;
    @BindView(R.id.scrollview_hashes)
    ScrollView scrollview_hashes;
    @BindView(R.id.fragment_graphic_drawer_container)
    FrameLayout fragment_graphic_container;
    @BindView(R.id.content)
    LinearLayout linearLayout;
    @BindView(R.id.handle)
    ImageView handleimageview;
    @BindView(R.id.righthandle)
    ImageView righthandle;
    @BindView(R.id.recyview_item)
    RecyclerView recyview_hashes;
    @BindView(R.id.recyview_metrices)
    RecyclerView recyview_metrices;
   /* @BindView(R.id.recyview_hashes)
    RecyclerView recyview_hashes;*/

    //tabdetails
    @BindView(R.id.spinner)
    Spinner photospinner;
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
    @BindView(R.id.layout_halfscrnimg)
    RelativeLayout layout_halfscrnimg;
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


 //   LinearLayout layout_bottom, layout_drawer;
    videoframeadapter mmetricesadapter, mhashesadapter;
    private LinearLayoutManager mLayoutManager;
    private boolean isdraweropen = false;
    public int selectedsection = 1;
    graphicalfragment fragmentgraphic;
    fragmentgraphicaldrawer  graphicaldrawerfragment;
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
    boolean img_fullscrnshow=false;
   // RelativeLayout layout_photoreader,layout_mediatype;
    int targetheight,previousheight;
    FullDrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

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
    mediadate = "",mediatime = "",mediasize="",lastsavedangle="";
    private float currentDegree = 0f;
    ImageView img_compass;

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

            mDrawer = (FullDrawerLayout) rootview.findViewById(R.id.drawer_layout);

            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(), mDrawer, R.string.drawer_open, R.string.drawer_close);
            // Where do I put this?
            mDrawerToggle.syncState();

            mDrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

            img_dotmenu.setOnClickListener(this);
            img_folder.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_arrow_back.setOnClickListener(this);

            img_dotmenu.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.VISIBLE);
            img_camera.setVisibility(View.VISIBLE);
            img_arrow_back.setVisibility(View.VISIBLE);


            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);
            tab_photoreader.setOnClickListener(this);

            //tabs_detail
            txtslotmedia.setText(getResources().getString(R.string.photo));
            List<String> categories = new ArrayList<String>();
            categories.add("All Media");
            categories.add("Photo");
            categories.add("Video");
            categories.add("Audio");
            photospinner.setOnItemSelectedListener(this);
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

            photospinner.setOnItemSelectedListener(this);
            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);

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

            fragment_graphic_container.setVisibility(View.VISIBLE);

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

            if (graphicaldrawerfragment == null) {
               // fragmentgraphic = new graphicalfragment();
                graphicaldrawerfragment =new fragmentgraphicaldrawer();
                graphicaldrawerfragment.setphotocapture(true);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_graphic_drawer_container, graphicaldrawerfragment);
                transaction.commit();
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adaptermedialist to spinner
            photospinner.setAdapter(dataAdapter);

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

            loadmap();
            setmetriceshashesdata();
            setupimagedata();

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
              edt_medianame.setSelection(edt_medianame.getText().length());
              edt_medianame.requestFocus();
              InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.img_edit_notes:
                edt_medianotes.setClickable(true);
                edt_medianotes.setEnabled(true);
                edt_medianotes.setFocusable(true);
                edt_medianotes.setFocusableInTouchMode(true);
                edt_medianotes.setSelection(edt_medianotes.getText().length());
                edt_medianotes.requestFocus();
                InputMethodManager immn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

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

                break;
            case R.id.img_camera:
                launchbottombarfragment();
                break;

            case R.id.img_arrow_back:
                gethelper().onBack();
                break;
            case R.id.img_fullscreen:
                if(img_fullscrnshow){
                    collapse(tab_photoreader,100,previousheight);
                    layout_photodetails.setVisibility(View.VISIBLE);
                    tab_layout.setVisibility(View.VISIBLE);
                    scrollview_detail.setVisibility(View.VISIBLE);
                    layout_footer.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.img_fullscreen);
                    img_fullscrnshow=false;
                }else{
                    expand(tab_photoreader,100,targetheight);
                    layout_photodetails.setVisibility(View.GONE);
                    scrollview_detail.setVisibility(View.GONE);
                    scrollview_meta.setVisibility(View.GONE);
                    scrollView_encyrption.setVisibility(View.GONE);
                    tab_layout.setVisibility(View.GONE);
                    layout_footer.setVisibility(View.GONE);
                    layout_mediatype.setVisibility(View.GONE);
                    img_fullscreen.setVisibility(View.INVISIBLE);
                 //   img_fullscreen.setImageResource(R.drawable.img_halfscreen);
                  //  img_fullscrnshow=true;
                }

                break;

            case R.id.tab_photoreader:
                Log.e("ontouch","ontouch");
                if(layout_photodetails.getVisibility()==View.GONE){
                    img_fullscreen.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.img_halfscreen);
                    layout_mediatype.setVisibility(View.VISIBLE);
                    img_fullscrnshow=true;
                    layout_footer.setVisibility(View.VISIBLE);
                }else{
                    img_fullscreen.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.img_fullscreen);
                    img_fullscrnshow=false;
                }

                break;
        }

    }

    public void launchbottombarfragment()
    {
        bottombarfragment fragbottombar=new bottombarfragment();
        fragbottombar.setData(new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {

            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
        gethelper().replaceFragment(fragbottombar, false, true);
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
        tvsize.setText(common.filesize(imageurl));
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

                /*if (graphicaldrawerfragment != null)
                    graphicaldrawerfragment.setdrawerproperty(graphicopen);

                graphicaldrawerfragment.setmetricesdata();*/

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

                graphicaldrawerfragment.getencryptiondata(metricmainarraylist.get(0).getHashmethod(), metricmainarraylist.get(0).getVideostarttransactionid(),
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
                if (graphicaldrawerfragment != null)
                    graphicaldrawerfragment.setmetricesdata();

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

                    if (audiostatus.equalsIgnoreCase(config.sync_complete) && metricmainarraylist.size() == 0) {

                        if(!mediadate.isEmpty()){

                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                            Date date = format.parse(mediadate);
                            String time = new SimpleDateFormat("hh:mm:ss aa").format(date);
                            String filecreateddate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                            tvdate.setText(filecreateddate);
                            txt_createdtime.setText(time);
                            tvtime.setText(time);
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
                    } else {

                        if(!mediadate.isEmpty()){

                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                            Date date = format.parse(mediadate);
                            String time = new SimpleDateFormat("hh:mm:ss aa").format(date);
                            String filecreateddate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                            tvdate.setText(filecreateddate);
                            txt_createdtime.setText(time);
                            tvtime.setText(time);
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