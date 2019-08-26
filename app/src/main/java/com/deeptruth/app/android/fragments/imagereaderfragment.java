package com.deeptruth.app.android.fragments;


import android.animation.ValueAnimator;
import android.content.Context;
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
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.adapter.encryptiondataadapter;
import com.deeptruth.app.android.adapter.folderdirectoryspinneradapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.LinearLayoutManagerWithSmoothScroller;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.simpledivideritemdecoration;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static android.widget.RelativeLayout.TRUE;

/**
 * A simple {@link Fragment} subclass.
 */
public class imagereaderfragment extends basefragment implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener ,customedittext.OnKeyListener {

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
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;


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
    LinearLayout layout_mediatype;
    @BindView(R.id.layout_photodetails)
    public RelativeLayout layout_photodetails;
    @BindView(R.id.scrollview_detail)
    ScrollView scrollview_detail;
    @BindView(R.id.img_fullscreen)
    ImageView img_fullscreen;
    @BindView(R.id.edt_medianame)
    customedittext edt_medianame;
    @BindView(R.id.edt_medianotes)
    customedittext edt_medianotes;
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
    @BindView(R.id.photorootview)
    RelativeLayout photorootview;
    @BindView(R.id.layout_dtls)
    LinearLayout layout_dtls;
    @BindView(R.id.layout_validating)
    LinearLayout layout_validating;
    @BindView(R.id.txt_section_validating_secondary)
    TextView txt_section_validating_secondary;
    @BindView(R.id.img_scanover)
    ImageView img_scanover;
    @BindView(R.id.layout_item_encryption)
    LinearLayout layout_item_encryption;
    @BindView(R.id.recycler_encryption)
    RecyclerView recycler_encryption;
    @BindView(R.id.tab_photoreader)
    ImageView tab_photoreader;
    @BindView(R.id.metainfocontainer)
    FrameLayout metainfocontainer;
    metainformationfragment fragmentmetainformation;

    private Handler myHandler;
    private Runnable myRunnable;
    private String mediafilepath = "",sync_date = "";
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private int flingactionmindstvac,footerheight,navigationbarheight = 0,targetheight=0,previousheight=0,bottompadding,
            rootviewheight, devidedheight,actionbarheight,updatemetaattempt=0;
    private String medianame = "",medianotes = "",mediafolder = "",mediaid="0",localkey = "",latitude = "", longitude = "",
            screenheight = "",screenwidth = "",mediastartdevicedate="",mediatoken="";
    private float currentDegree = 0f;
    private adapteritemclick mcontrollernavigator;
    private TranslateAnimation validationbaranimation;
    private encryptiondataadapter encryptionadapter;
    private ArrayList<arraycontainer> encryptionarraylist = new ArrayList<>();
    private folderdirectoryspinneradapter folderspinneradapter;
    private arraycontainer arraycontainerformetric =null;
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
            navigationbarheight =  common.getnavigationbarheight();
            txt_section_validating_secondary.setVisibility(View.INVISIBLE);
            setfooterlayout();
            //    setheadermargin();
            loadviewdata();
        }
        return rootview;
    }

    public void loadviewdata()
    {
        gethelper().setdatacomposing(false);

        layout_item_encryption.setVisibility(View.GONE);
        recycler_encryption.setVisibility(View.VISIBLE);

        recycler_encryption.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(applicationviavideocomposer.getactivity()));
        recycler_encryption.addItemDecoration(new simpledivideritemdecoration(applicationviavideocomposer.getactivity()));
        encryptionadapter = new encryptiondataadapter(encryptionarraylist, applicationviavideocomposer.getactivity());
        recycler_encryption.setAdapter(encryptionadapter);
        photorootview.post(new Runnable() {
            @Override
            public void run() {
                rootviewheight = photorootview.getHeight();
                int rootviewwidth = photorootview.getWidth();
                Log.e("rootviewheight",""+rootviewheight);
                devidedheight= rootviewheight/2 ;

                layout_halfscrn.getLayoutParams().height = devidedheight;
                layout_halfscrn.requestLayout();
                layout_photodetails.getLayoutParams().height = (devidedheight-navigationbarheight);
                layout_photodetails.requestLayout();


                final AlphaAnimation alphanimation = new AlphaAnimation(0.0f, 1.0f);
                alphanimation.setDuration(1000); //You can manage the time of the blink with this parameter
                alphanimation.setStartOffset(1000);
                alphanimation.setRepeatMode(1);

                final AlphaAnimation fadeout_animation = new AlphaAnimation(1.0f, 0.0f);
                fadeout_animation.setDuration(1000); //You can manage the time of the blink with this parameter
                fadeout_animation.setStartOffset(1000);
                fadeout_animation.setRepeatMode(1);

                Animation.AnimationListener alphalistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        txt_section_validating_secondary.startAnimation(fadeout_animation);
                        //fadeoutcontrollers();
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                alphanimation.setAnimationListener(alphalistener);
                Animation.AnimationListener fadeoutlistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        txt_section_validating_secondary.startAnimation(alphanimation);
                        //fadeoutcontrollers();
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                fadeout_animation.setAnimationListener(fadeoutlistener);

                validationbaranimation = new TranslateAnimation(-rootviewwidth, rootviewwidth ,0.0f, 0.0f);
                validationbaranimation.setDuration(4000);
                validationbaranimation.setRepeatCount(Animation.INFINITE);
                validationbaranimation.setRepeatMode(ValueAnimator.RESTART);
                img_scanover.startAnimation(validationbaranimation);

                Animation.AnimationListener translatelistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        txt_section_validating_secondary.startAnimation(alphanimation);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        txt_section_validating_secondary.startAnimation(alphanimation);
                    }
                };
                validationbaranimation.setAnimationListener(translatelistener);

                setupimagedata();
                metainfocontainer.setVisibility(View.VISIBLE);
            }
        });
        gethelper().drawerenabledisable(false);
        img_dotmenu.setOnClickListener(this);
        img_folder.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_arrow_back.setOnClickListener(this);

        img_dotmenu.setOnClickListener(this);
        img_folder.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_arrow_back.setOnClickListener(this);

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


        tab_photoreader.setOnClickListener(this);

        //tabs_detail
        txtslotmedia.setText(getResources().getString(R.string.photo));
        img_share_media.setOnClickListener(this);
        img_edit_name.setOnClickListener(this);
        img_edit_notes.setOnClickListener(this);
        img_fullscreen.setOnClickListener(this);
        scrollview_detail.setVisibility(View.VISIBLE);
        tab_layout.setVisibility(View.VISIBLE);
        img_fullscreen.setVisibility(View.VISIBLE);
        layout_photodetails.setVisibility(View.VISIBLE);
        layout_mediatype.setVisibility(View.VISIBLE);
        layout_date.setVisibility(View.VISIBLE);
        layout_time.setVisibility(View.VISIBLE);
        img_share_media.setVisibility(View.VISIBLE);
        layout_photodetails.setOnClickListener(this);
        layout_dtls.setOnClickListener(this);

        layout_photoreader.post(new Runnable() {
            @Override
            public void run() {
                targetheight= rootviewheight;
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

        String blockchainid = " ";
        String blockid = " ";
        String blocknumber = " ";
        common.setspannable(getResources().getString(R.string.blockchain_id), blockchainid, txt_blockchainid);
        common.setspannable(getResources().getString(R.string.hash_formula), blockid, txt_blockid);
        common.setspannable(getResources().getString(R.string.mediahash), blocknumber, txt_blocknumber);
        edt_medianame.setEnabled(false);
        edt_medianame.setClickable(false);
        edt_medianame.setFocusable(false);
        edt_medianame.setFocusableInTouchMode(false);

      //  edt_medianotes.setEnabled(false);
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
                    //gethelper().setwindowfitxy(true);
                    InputMethodManager imm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);

                    callupdatemetaapi(mediaid,edt_medianame.getText().toString().trim(),edt_medianotes.getText().toString().trim());
                }
            }
        });

        edt_medianame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edt_medianame.setKeyListener(null);
                    v.setFocusable(false);
                    //gethelper().setwindowfitxy(true);
                    InputMethodManager imm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);

                    callupdatemetaapi(mediaid,edt_medianame.getText().toString().trim(),edt_medianotes.getText().toString().trim());

                    editabletext();
                }
            }
        });

        layout_mediatype.post(new Runnable() {
            @Override
            public void run() {
                actionbarheight = layout_mediatype.getHeight();
                bottompadding = layout_photodetails.getPaddingBottom();
            }
        });

        edt_medianotes.setOnKeyboardHidden(new customedittext.OnKeyboardHidden() {
            @Override
            public void onKeyboardHidden() {
                hidefocus(edt_medianotes);
            }
        });

        edt_medianame.setOnKeyboardHidden(new customedittext.OnKeyboardHidden() {
            @Override
            public void onKeyboardHidden() {
                hidefocus(edt_medianame);
            }
        });

        edt_medianotes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.edt_medianotes) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        edt_medianame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    edt_medianame.setEnabled(false);
                    edt_medianame.setClickable(false);
                    edt_medianame.setFocusable(false);
                    edt_medianame.setFocusableInTouchMode(false);
                    edt_medianame.setKeyListener(null);
                    removeheadermargin();
                    layout_halfscrn.setVisibility(View.VISIBLE);
                    editabletext();
                    return true;
                }else
                {
                    return false;
                }
            }
        });

        setmetriceshashesdata();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }


    public void setmetriceshashesdata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                if(updatemetaattempt == 0 || updatemetaattempt >= 3 &&
                        ((sync_date.trim().isEmpty()) || sync_date.equalsIgnoreCase("0")))
                {
                    updatemetaattempt=0;
                    getmediastartinfo();
                    getmediametadata();
                    gethelper().setcurrentmediaposition(0);
                }
                updatemetaattempt++;

                if(arraycontainerformetric != null)
                {
                    common.setgraphicalblockchainvalue(config.blockchainid,arraycontainerformetric.getVideostarttransactionid(),true);
                    common.setgraphicalblockchainvalue(config.hashformula,arraycontainerformetric.getHashmethod(),true);
                    common.setgraphicalblockchainvalue(config.datahash,arraycontainerformetric.getValuehash(),true);
                    common.setgraphicalblockchainvalue(config.matrichash,arraycontainerformetric.getMetahash(),true);

                    common.setspannable(getResources().getString(R.string.blockchain_id), " " + arraycontainerformetric.getVideostarttransactionid(), txt_blockchainid);
                    common.setspannable(getResources().getString(R.string.hash_formula), " " + arraycontainerformetric.getHashmethod(), txt_blockid);
                    common.setspannable(getResources().getString(R.string.mediahash), " " + arraycontainerformetric.getValuehash(), txt_blocknumber);
                    common.setspannable(getResources().getString(R.string.metrichash), " " + arraycontainerformetric.getMetahash(), txt_metahash);

                }
                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        applicationviavideocomposer.getactivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                showimageview();
                resetButtonViews(txtslotmeta, txtslotmedia, txtslotencyption);
                scrollview_meta.setVisibility(View.VISIBLE);
                scrollView_encyrption.setVisibility(View.INVISIBLE);
                scrollview_detail.setVisibility(View.INVISIBLE);
                if(fragmentmetainformation == null)
                {
                    fragmentmetainformation=new metainformationfragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.metainfocontainer,fragmentmetainformation);
                    transaction.commit();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(fragmentmetainformation != null)
                        {
                            fragmentmetainformation.setdatacomposing(false,xdata.getinstance().getSetting(config.selectedphotourl));
                            fragmentmetainformation.setcurrentmediaposition(0);
                        }
                    }
                },500);

                break;
            case R.id.txt_slot6:
                showimageview();
                resetButtonViews(txtslotencyption, txtslotmedia, txtslotmeta);
                scrollView_encyrption.setVisibility(View.VISIBLE);
                scrollview_detail.setVisibility(View.INVISIBLE);
                scrollview_meta.setVisibility(View.INVISIBLE);
                break;
            case R.id.img_edit_name:
                visiblefocus(edt_medianame);
                InputMethodManager imm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
                break;
            case R.id.img_edit_notes:
                visiblefocus(edt_medianotes);
                InputMethodManager imn = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);

                break;
            case R.id.img_share_media:
                img_share_media.setEnabled(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        img_share_media.setEnabled(true);
                    }
                }, 1500);
                if (mediafilepath != null && (!mediafilepath.isEmpty()))
                        baseactivity.getinstance().preparesharedialogfragment(mediafilepath,mediatoken,config.type_image,mediafilepath);

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

            case R.id.img_arrow_back:
                try {
                    img_arrow_back.setClickable(false);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
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
                }, 500);
                if(layout_photodetails.getVisibility()==View.VISIBLE){
                    gethelper().drawerenabledisable(true);
                    layout_halfscrn.getLayoutParams().height = (rootviewheight -navigationbarheight);
                    layout_photodetails.getLayoutParams().height = 0;
                    gethelper().drawerenabledisable(false);
                    gethelper().updateactionbar(0);
                    layout_photodetails.setVisibility(View.GONE);
                    layout_photodetails.requestLayout();
                    scrollview_detail.setVisibility(View.GONE);
                    scrollview_meta.setVisibility(View.GONE);
                    scrollView_encyrption.setVisibility(View.GONE);
                    tab_layout.setVisibility(View.GONE);
                    layout_mediatype.setVisibility(View.GONE);
                    img_fullscreen.setVisibility(View.INVISIBLE);

                } else{
                    removeheaderpadding();
                    gethelper().drawerenabledisable(false);
                    layout_halfscrn.getLayoutParams().height = devidedheight;
                    layout_photodetails.getLayoutParams().height = (devidedheight-navigationbarheight);
                    gethelper().updateactionbar(1);
                    layout_photodetails.setVisibility(View.VISIBLE);
                    tab_layout.setVisibility(View.VISIBLE);
                    scrollview_detail.setVisibility(View.VISIBLE);
                    collapseimg_view();
                    img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                    gethelper().updateactionbar(0,getResources().getColor(R.color.dark_blue_solid));
                    resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                }
                break;

            case R.id.tab_photoreader:
                Log.e("ontouch","ontouch");

                if(layout_photodetails.getVisibility()==View.GONE){
                    if(layout_mediatype.getVisibility()==(View.GONE)){
                       // layout_halfscrn.getLayoutParams().height = rootviewheight;
                        setbottomimgview();
                        gethelper().updateactionbar(1);
                        img_fullscreen.setVisibility(View.VISIBLE);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                        layout_mediatype.setVisibility(View.VISIBLE);
                        gethelper().drawerenabledisable(true);
                    } else {
                        gethelper().updateactionbar(0);
                        gethelper().drawerenabledisable(false);
                        layout_mediatype.setVisibility(View.GONE);
                        img_fullscreen.setVisibility(View.GONE);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                    }

                } else {
                    img_fullscreen.setVisibility(View.VISIBLE);
                    gethelper().drawerenabledisable(false);
                    img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                }

                break;
            case R.id.layout_dtls:
                Log.e("ontouch","ontouchscrollview");
                if(layout_halfscrn.getVisibility() == View.GONE){
                    removeheadermargin();
                    layout_halfscrn.setVisibility(View.VISIBLE);
                    if(metricmainarraylist.size() > 0){
                        if(!metricmainarraylist.get(0).getColor().equalsIgnoreCase("white") && !metricmainarraylist.get(0).getColor().isEmpty())
                            layout_validating.setVisibility(View.VISIBLE);
                    }
                    v.clearFocus();
                    InputMethodManager immm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immm.hideSoftInputFromWindow(v.getWindowToken(), 0);

               }
                break;
        }

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
            view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.white));

            Drawable drawable2 = (Drawable) view2.getBackground();
            drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view2.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.blue));

            Drawable drawable3 = (Drawable) view3.getBackground();
            drawable3.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view3.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.blue));

        }
    }

    public void setupimagedata() {
        mediafilepath = xdata.getinstance().getSetting(config.selectedphotourl);
        tvsize.setText(common.filesize(mediafilepath));
        if (mediafilepath != null && (!mediafilepath.isEmpty())) {
            setupphoto(Uri.parse(mediafilepath));
        }
    }

    public void setupphoto(final Uri selectedphotouri) {
        if (mediafilepath != null) {
            tab_photoreader.setImageURI(selectedphotouri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
        if(validationbaranimation != null)
        {
            validationbaranimation.cancel();
            validationbaranimation.reset();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(validationbaranimation != null)
            img_scanover.startAnimation(validationbaranimation);

        gethelper().setcurrentmediaposition(0);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void getmediastartinfo() {
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

                    Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(mediafilepath));
                    if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
                        do {
                            //mediacompleteddate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                            mediastartdevicedate = "" + cur.getString(cur.getColumnIndex("videostartdevicedate"));
                            medianame = "" + cur.getString(cur.getColumnIndex("media_name"));
                            medianotes = "" + cur.getString(cur.getColumnIndex("media_notes"));
                            mediatoken = "" + cur.getString(cur.getColumnIndex("token"));
                            mediafolder = "" + cur.getString(cur.getColumnIndex("media_folder"));
                            localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                            sync_date = "" + cur.getString(cur.getColumnIndex("sync_date"));
                            mediaid = "" + cur.getString(cur.getColumnIndex("videoid"));

                        } while (cur.moveToNext());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getmediametadata()
    {

        try {
            databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<metadatahash> mitemlist = mdbhelper.getmediametadatabyfilename(common.getfilename(mediafilepath));
            if (mitemlist.size() > 0)
            {
                for (int i = 0; i < mitemlist.size(); i++)
                {
                    if(metricmainarraylist.size() == i)
                    {
                        String metricdata = mitemlist.get(i).getMetricdata();
                        String sequencehash = mitemlist.get(i).getSequencehash();
                        String hashmethod = mitemlist.get(i).getHashmethod();
                        String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                        String serverdictionaryhash = mitemlist.get(i).getValuehash();
                        String color = mitemlist.get(i).getColor();
                        String latency = mitemlist.get(i).getLatency();
                        String sequenceno = mitemlist.get(i).getSequenceno();
                        String colorreason = mitemlist.get(i).getColorreason();
                        parsemetadata(metricdata, hashmethod, videostarttransactionid, sequencehash, serverdictionaryhash, color,
                                latency, sequenceno,colorreason);
                    }
                    else
                    {
                        String sequencehash = mitemlist.get(i).getSequencehash();
                        String hashmethod = mitemlist.get(i).getHashmethod();
                        String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                        String serverdictionaryhash = mitemlist.get(i).getValuehash();
                        String color = mitemlist.get(i).getColor();
                        String latency = mitemlist.get(i).getLatency();
                        String sequenceno = mitemlist.get(i).getSequenceno();
                        String colorreason = mitemlist.get(i).getColorreason();
                        metricmainarraylist.set(i, new arraycontainer(hashmethod, videostarttransactionid,
                                sequencehash, serverdictionaryhash, color, latency,colorreason));
                    }
                }
            }

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

        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {

                    if(tvdate.getText().toString().trim().length() == 0)
                    {
                        edt_medianame.setText(medianame);
                        edt_medianotes.setText(medianotes);
                    }

                    if(metricmainarraylist.size() >0 && metricmainarraylist.get(0) != null)
                    {
                        arraycontainerformetric = metricmainarraylist.get(0);
                        String color = "white";
                        if (metricmainarraylist.get(0).getColor() != null && (!metricmainarraylist.get(0).getColor().isEmpty()))
                            color = metricmainarraylist.get(0).getColor();

                        switch (color) {
                            case "green":
                                txt_section_validating_secondary.setText(config.validating);
                                try {
                                    DrawableCompat.setTint(img_scanover.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                                            , R.color.scanover_green));
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                layout_validating.setVisibility(View.VISIBLE);
                                //txt_section_validating_secondary.setBackgroundColor(Color.parseColor("#0EAE3E"));
                                break;
                            case "white":
                                layout_validating.setVisibility(View.GONE);
                                break;
                            case "red":
                                txt_section_validating_secondary.setText(config.invalid);
                                try {
                                    DrawableCompat.setTint(img_scanover.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                                            , R.color.scanover_red));
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                layout_validating.setVisibility(View.VISIBLE);
                                //txt_section_validating_secondary.setBackgroundColor(Color.parseColor("#FF3B30"));
                                break;
                            case "yellow":
                                txt_section_validating_secondary.setText(config.caution);
                                try {
                                    DrawableCompat.setTint(img_scanover.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                                            , R.color.scanover_yellow));
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                layout_validating.setVisibility(View.VISIBLE);
                                //txt_section_validating_secondary.setBackgroundColor(Color.parseColor("#FDD012"));
                                break;
                        }

                        if(encryptionarraylist.size() == 0)
                            encryptionarraylist.add(metricmainarraylist.get(0));

                        if(encryptionarraylist.size() > 0)
                        {
                            encryptionarraylist.set(0,metricmainarraylist.get(0));
                            encryptionadapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        layout_validating.setVisibility(View.GONE);
                    }

                    if(! mediastartdevicedate.trim().isEmpty())
                    {
                        SimpleDateFormat formatted = null;
                        Date mediadate = null;
                        if (mediastartdevicedate.contains("T")) {
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                            mediadate = format.parse(mediastartdevicedate);
                        } else {
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            mediadate = format.parse(mediastartdevicedate);
                        }

                        DateFormat datee = new SimpleDateFormat("z",Locale.getDefault());
                        String localTime = datee.format(mediadate);

                        SimpleDateFormat formatteddate = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat formattedtime = new SimpleDateFormat("hh:mm:ss aa",Locale.ENGLISH);

                        tvdate.setText(common.parsedateformat(mediadate));
                        tvtime.setText(common.parsetimeformat(mediadate) + " "+localTime);
                        // txt_title_actionbarcomposer.setText(formatteddate.format(mediadate));
                        txt_createdtime.setText(common.parsetimeformat(mediadate));
                    }

                    if (mediafolder.trim().length() > 0 && folderspinneradapter == null)
                        setfolderspinner();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }


    public void setfolderspinner()
    {
        final List<folder> folderitem=common.getalldirfolders();
        folderspinneradapter = new folderdirectoryspinneradapter(applicationviavideocomposer.getactivity(),
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

        spinnermediafolder.setAdapter(folderspinneradapter);
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
                                if(! folderpath.equalsIgnoreCase(new File(mediafilepath).getParent()))
                                {
                                    if(common.movemediafile(new File(mediafilepath),new File(folderpath)))
                                    {
                                        File destinationmediafile = new File(folderpath + File.separator + new File(mediafilepath).getName());
                                        updatefilemediafolderdirectory(mediafilepath,destinationmediafile.getAbsolutePath(),folderpath);
                                        mediafilepath =destinationmediafile.getAbsolutePath();
                                        xdata.getinstance().saveSetting(config.selectedphotourl, mediafilepath);
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
                                        mcontrollernavigator.onItemClicked(mediafilepath,3);

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

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency,String sequenceno,String colorreason) {
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
                metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,
                        color,latency,sequenceno,colorreason));
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
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,
                            color,latency,sequenceno,colorreason));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void resetmedianamenotes()
    {
        edt_medianame.setText(medianame);
        edt_medianotes.setText(medianotes);
    }

    public void callupdatemetaapi(String mediaid,String title,String description)
    {
        if(!gethelper().isuserlogin()){
            Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                    .getResources().getString(R.string.login_here),Toast.LENGTH_SHORT).show();
            resetmedianamenotes();
            gethelper().redirecttologin();
            return;
        }

        //mediaid="460123";

        if(mediaid.trim().isEmpty() || mediaid.equalsIgnoreCase("0")){
            Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                    .getResources().getString(R.string.invalid_empty_mediaid),Toast.LENGTH_SHORT).show();
            resetmedianamenotes();
            return;
        }

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type",config.type_image);
        requestparams.put("action","updatemeta");
        requestparams.put("id",mediaid);
        requestparams.put("authtoken",xdata.getinstance().getSetting(config.authtoken));
        requestparams.put("title",title);
        requestparams.put("description",description);
        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
        gethelper().xapipost_send(applicationviavideocomposer.getactivity(),requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    try {
                        JSONObject object=new JSONObject(response.getData().toString());
                        if(object.has("success"))
                        {
                            if(object.getString("success").equalsIgnoreCase("true") || object.getString("success").equalsIgnoreCase("1"))
                            {
                                Toast.makeText(applicationviavideocomposer.getactivity(), applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.metadata_updated), Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(object.has("error"))
                        {
                            Toast.makeText(applicationviavideocomposer.getactivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
                            //resetmedianamenotes();
                        }

                        medianame=edt_medianame.getText().toString().trim();
                        medianotes=edt_medianotes.getText().toString().trim();
                        if(! localkey.isEmpty())
                            updatemediainfo(localkey,edt_medianame.getText().toString().trim(),
                                    edt_medianotes.getText().toString().trim().trim());

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(applicationviavideocomposer.getactivity(), applicationviavideocomposer.getactivity().getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void updatemediainfo(String localkey,String medianame,String medianotes)
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
        mdbhelper.updatemediainfobylocalkey(localkey,medianame,medianotes);
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

    @Override
    public void showhideviewondrawer(boolean isshow) {
        super.showhideviewondrawer(isshow);

        if(isshow){
            layout_halfscrn.getLayoutParams().height = (rootviewheight -navigationbarheight);;
            gethelper().updateactionbar(0);
            common.slidetoabove(layout_mediatype);
            img_fullscreen.setVisibility(View.GONE);
            layout_validating.setVisibility(View.GONE);
        }else{
           // layout_halfscrn.getLayoutParams().height = rootviewheight;
            gethelper().updateactionbar(1);
            common.slidetodown(layout_mediatype);
            img_fullscreen.setVisibility(View.VISIBLE);
            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
            layout_mediatype.setVisibility(View.VISIBLE);

            if(metricmainarraylist.size() > 0){
                if(!metricmainarraylist.get(0).getColor().equalsIgnoreCase("white") && !metricmainarraylist.get(0).getColor().isEmpty())
                    layout_validating.setVisibility(View.VISIBLE);
            }

            setbottomimgview();
        }
    }

    public void setheadermargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,actionbarheight,0,0);
        layout_photodetails.setLayoutParams(params);
        layout_photodetails.requestLayout();
    }

    public void removeheadermargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.layout_halfscrn);
        params.setMargins(0,0,0,0);
         Log.e("bottompadding",""+bottompadding);

        layout_photodetails.setPadding(0,0,0,(footerheight));
        layout_photodetails.setLayoutParams(params);
        layout_photodetails.requestLayout();
    }

    public void removeheaderpadding(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.layout_halfscrn);
        layout_photodetails.setPadding(0,0,0,footerheight);
        layout_photodetails.setLayoutParams(params);
        layout_photodetails.requestLayout();
    }

    public void setbottomimgview(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,footerheight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM );
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img_fullscreen.setLayoutParams(params);
    }

    public void collapseimg_view(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,R.id.layout_halfscrn );
        img_fullscreen.setLayoutParams(params);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }


    public void setfooterlayout(){

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,TRUE);
            params.setMargins(0,0,0,navigationbarheight);
            layout_photoreader.setLayoutParams(params);
    }

    public void hidefocus(EditText edittext){

        edittext.setEnabled(false);
        edittext.setClickable(false);
        edittext.setFocusable(false);
        edittext.setFocusableInTouchMode(false);
        layout_halfscrn.setVisibility(View.VISIBLE);
        removeheadermargin();
        layout_validating.setVisibility(View.VISIBLE);
    }

    public void visiblefocus(EditText edittext){

        layout_halfscrn.setVisibility(View.GONE);
        layout_validating.setVisibility(View.GONE);
        setheadermargin();
        // gethelper().setwindowfitxy(false);
        edittext.setSelection(edittext.getText().length());
        edittext.setClickable(true);
        edittext.setEnabled(true);
        edittext.setFocusable(true);
        edittext.setFocusableInTouchMode(true);
        edittext.requestFocus();
    }

    public void showimageview(){
        if(layout_halfscrn.getVisibility() == View.GONE){
            hidekeyboard();
            layout_halfscrn.setVisibility(View.VISIBLE);
            layout_validating.setVisibility(View.VISIBLE);
            removeheadermargin();
        }
    }
}