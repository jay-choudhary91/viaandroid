package com.cryptoserver.composer.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class phototabreaderfrag extends basefragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener {

    View rootview;
    ImageView handle;
    RecyclerView recyview_hashes;
    RecyclerView recyview_metrices;
    ImageView handleimageview, righthandle;
    LinearLayout linearLayout;
    FrameLayout fragment_graphic_container;
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
    private Uri selectedphotouri =null;
    private String photo_url = null;
    @BindView(R.id.tab_photoreader)
    ImageView tab_photoreader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {


            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            handle = (ImageView) rootview.findViewById(R.id.handle);
            layout_bottom = (LinearLayout) rootview.findViewById(R.id.layout_bottom);
            layout_drawer = (LinearLayout) rootview.findViewById(R.id.layout_drawer);
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


            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);


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

            txtSlot1.setOnClickListener(this);
            txtSlot2.setOnClickListener(this);
            txtSlot3.setOnClickListener(this);

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

        }


        return rootview;
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
    public int getlayoutid() {
        return R.layout.fragment_phototabreaderfrag;
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

                    if (fragmentgraphic != null)
                        fragmentgraphic.setvisualizer();
                }
                break;

        }

    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3) {
        view1.setBackgroundResource(R.color.videolist_background);
        view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));

        view2.setBackgroundResource(R.color.white);
        view2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.videolist_background));

        view3.setBackgroundResource(R.color.white);
        view3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.videolist_background));
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener() {
        private final int flingactionmindstvac = common.getdrawerswipearea();
        private static final int flingactionmindspdvac = 10;

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
            case R.id.img_upload_icon:
                //  checkwritestoragepermission();
                break;
            case R.id.img_setting:
                fragmentsettings fragmatriclist = new fragmentsettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
            case R.id.img_menu:
                opengallery();
                break;
        }
    }

    public void opengallery() {
        // destroyvideoplayer();
        Intent intent;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE_PICK) {

            if (resultCode == RESULT_OK) {
                  selectedphotouri = data.getData();

                try {
                    photo_url = common.getpath(getActivity(), selectedphotouri);
                    Bitmap bitmap= BitmapFactory.decodeFile(photo_url);
                    if(photo_url!=null){
                        tab_photoreader.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    common.showalert(getActivity(), getResources().getString(R.string.file_uri_parse_error));
                    return;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(photo_url != null && (! photo_url.isEmpty()))
        {
            tab_photoreader.setImageURI(selectedphotouri);

        }
    }
}