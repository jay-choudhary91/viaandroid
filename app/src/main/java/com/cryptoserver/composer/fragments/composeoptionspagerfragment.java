package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.permissions;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 6/11/18.
 */

public class composeoptionspagerfragment extends basefragment implements View.OnClickListener {

    @BindView(R.id.pager_header)
    pagercustomduration pagerheader;
    @BindView(R.id.pager_footer)
    pagercustomduration pagerfooter;

    @BindView(R.id.img_video_capture)
    ImageView mrecordimagebutton;
    @BindView(R.id.img_rotate_camera)
    ImageView imgrotatecamera;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutbottom;

    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecomposerfragment fragimgcapture=null;

    View rootview=null;
    int pageritems = 3;

    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    ArrayList<permissions> permissionslist =new ArrayList<>();
    @Override
    public int getlayoutid() {
        return R.layout.fragment_composeoptionspager;
    }
    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview==null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            mrecordimagebutton.setOnClickListener(this);
            imgrotatecamera.setOnClickListener(this);
        }
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            if(permissionslist.size() == 0)
            {
                permissionslist.add(new permissions(Manifest.permission.CAMERA,false,false));
                permissionslist.add(new permissions(Manifest.permission.ACCESS_COARSE_LOCATION,false,false));
                permissionslist.add(new permissions(Manifest.permission.ACCESS_FINE_LOCATION,false,false));
            }
            List<String> deniedpermissions = new ArrayList<>();
            for(int i=0;i<permissionslist.size();i++)
            {
                permissionslist.get(i).setIspermissionallowed(true);
                if (ContextCompat.checkSelfPermission(getActivity(), permissionslist.get(i).getPermissionname()) != PackageManager.PERMISSION_GRANTED)
                {
                    if(! permissionslist.get(i).isIspermissionskiped())
                    {
                        deniedpermissions.add(permissionslist.get(i).getPermissionname());
                        permissionslist.get(i).setIspermissionallowed(false);
                        break;
                    }
                }
            }

            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterallpermissionsgranted();
            } else {
                //String[] array = new String[deniedpermissions.size()];

                String[] array = new String[1];
                array = deniedpermissions.toArray(array);
                final String[] finalArray = array;

                common.showcustompermissiondialog(applicationviavideocomposer.getactivity(), new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {
                    }

                    @Override
                    public void onItemClicked(Object object, int type) {
                        if(type == 0)
                        {
                            if(finalArray.length > 0)
                            {
                                for(int i=0;i<permissionslist.size();i++)
                                {
                                    if(finalArray[0].equalsIgnoreCase(permissionslist.get(i).getPermissionname()))
                                    {
                                        permissionslist.get(i).setIspermissionskiped(true);
                                        break;
                                    }
                                }
                            }
                        }
                        else if(type == 1)
                        {
                            if(finalArray.length > 0)
                                ActivityCompat.requestPermissions(getActivity(), finalArray, request_permissions);
                        }
                    }
                },finalArray[0]);
            }
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
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        doafterallpermissionsgranted();
                    }
                };
            } else {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
                        //gethelper().onBack();
                    }
                };
            }
        }
    }

    private void doafterallpermissionsgranted() {
        if(fragvideocomposer == null)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initviewpager();
                }
            },10);
        }
    }

    public void initviewpager()
    {
        pagerheader.setPageTransformer(false, new pageranimation());
        //pagerfooter.setPageTransformer(false, new pageranimation());

        //pagerheader.setAdapter(headeradapter);
        pagerheader.setAdapter(new headerpageradapter(getChildFragmentManager()));
        pagerfooter.setAdapter(new footerpageradapter(getChildFragmentManager()));

        pagerheader.setOffscreenPageLimit(3);
        pagerfooter.setOffscreenPageLimit(3);
        int margin=(int)dipToPixels(applicationviavideocomposer.getactivity(),180);
        pagerfooter.setPageMargin(0-margin);

        mrecordimagebutton.setImageResource(0);
        mrecordimagebutton.setBackgroundResource(R.drawable.shape_recorder_off);

        pagerheader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("position1 ",""+position);
                if(pagerfooter.getCurrentItem() != position)
                    pagerfooter.setCurrentItem(position,true);

                setimagerecordstop();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        pagerfooter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("position2 ",""+position);
                if(pagerheader.getCurrentItem() != position)
                    pagerheader.setCurrentItem(position,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    pagerheader.setchildid(R.id.layout_drawertouchable);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        },4000);
    }

    public float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_video_capture:
                if(pagerheader.getCurrentItem() == 0)
                {
                    if(fragvideocomposer != null)
                        fragvideocomposer.startstopvideo();
                }
                else if(pagerheader.getCurrentItem() == 1)
                {
                    try {
                        if(fragaudiocomposer != null)
                            fragaudiocomposer.startstopaudiorecording();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(pagerheader.getCurrentItem() == 2)
                {
                    if(fragimgcapture != null)
                        fragimgcapture.takePicture();
                }
                break;
            case R.id.img_rotate_camera:
                if(pagerheader.getCurrentItem() == 0)
                {
                    if(fragvideocomposer != null)
                        fragvideocomposer.switchCamera();
                }
                else if(pagerheader.getCurrentItem() == 2)
                {
                    if(fragimgcapture != null)
                        fragimgcapture.switchCamera();
                }
                break;
        }
    }

    private class headerpageradapter extends FragmentStatePagerAdapter {

        public headerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0:
                    fragvideocomposer= (videocomposerfragment) videocomposerfragment.newInstance();
                    fragvideocomposer.setData(false,mitemclick);
                    return fragvideocomposer;

                case 1:
                    fragaudiocomposer=(audiocomposerfragment) audiocomposerfragment.newInstance();
                    fragaudiocomposer.setData(mitemclick);
                    return fragaudiocomposer;

                case 2:
                    fragimgcapture=(imagecomposerfragment) imagecomposerfragment.newInstance();
                    fragimgcapture.setData(mitemclick);
                    return fragimgcapture;

                default:
                    fragvideocomposer=(videocomposerfragment) videocomposerfragment.newInstance();
                    fragvideocomposer.setData(false,mitemclick);
                    return fragvideocomposer;
            }
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return pageritems;
        }

        @Override
        public float getPageWidth(int position) {
            return super.getPageWidth(position);
        }
    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int type) {
            if(type == 1)
            {
                setimagerecordstart();
            }
            else if(type == 2)
            {
                setimagerecordstop();
            }
        }
    };

    public void setimagerecordstart()
    {
        mrecordimagebutton.setImageResource(R.drawable.img_startrecord);
        mrecordimagebutton.setBackgroundResource(R.drawable.shape_recorder_off);
    }

    public void setimagerecordstop()
    {
        mrecordimagebutton.setImageResource(0);
        mrecordimagebutton.setBackgroundResource(R.drawable.shape_recorder_off);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class footerpageradapter extends FragmentPagerAdapter {

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return composefooterpagerfragment.newInstance("VIDEO");
                case 1: return composefooterpagerfragment.newInstance("AUDIO");
                case 2: return composefooterpagerfragment.newInstance("PHOTO");
                default: return composefooterpagerfragment.newInstance("VIDEO");
            }
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return pageritems;
        }
    }
}
