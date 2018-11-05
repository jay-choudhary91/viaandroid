package com.cryptoserver.composer.fragments;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.utils.common;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class bottombarfragment extends basefragment  {

    private static final int request_permissions = 1;
    public bottombarfragment() {
        // Required empty public constructor
    }
    private TextView mTextMessage;



    basefragment fragment ;
    View rootview = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_video:
                    fragmentvideolist fragvideolist=null;
                    if(fragvideolist == null)
                    {
                        fragvideolist=new fragmentvideolist();
                        gethelper().replacetabfragment(fragvideolist,false,true);

                    }
                    return true;
                case R.id.navigation_audio:
                    fragmentaudiolist fragaudiolist=null;
                    if(fragaudiolist == null)
                    {
                        fragaudiolist=new fragmentaudiolist();
                        gethelper().replacetabfragment(fragaudiolist,false,true);

                    }

                    return true;

                case R.id.navigation_image:
                    fragmentimagelist fragimglist=null;
                    if(fragimglist == null)
                    {
                        fragimglist=new fragmentimagelist();
                        gethelper().replacetabfragment(fragimglist,false,true);
                      /*  fragimglist=new fragmentimagelist();
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.add(R.id.tab_container,fragimglist);
                        transaction.commit();*/
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    public int getlayoutid() {
        return R.layout.fragment_bottombarfragment;
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
            BottomNavigationView navigation = (BottomNavigationView) rootview.findViewById(R.id.navigation);


            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            // launchvideocomposer(false);
        }
        return rootview;
    }

    public void launchvideocomposer(boolean autostart)
    {
        videocomposerfragment fragment=new videocomposerfragment();
        fragment.setData(autostart, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {

            }

            @Override
            public void onItemClicked(Object object, int type) {
                if(type == 1)
                {
                 //   requestpermissions();
                }
            }
        });
        gethelper().replaceFragment(fragment, false, true);
    }
   /* public void requestpermissions()
    {
        if (common.getstoragedeniedpermissions().isEmpty()) {
            // All permissions are granted
           // getVideoList();
        } else {
            String[] array = new String[common.getstoragedeniedpermissions().size()];
            array = common.getstoragedeniedpermissions().toArray(array);
            ActivityCompat.requestPermissions(getActivity(), array, request_permissions);
        }
    }*/


   /* @Override
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
                //  getVideoList();
            }
        }
    }*/

   /* @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid) {
            case R.id.img_upload_icon:
                //  checkwritestoragepermission();
                break;
            case R.id.img_setting:
                fragmentsettings fragmatriclist = new fragmentsettings();
                gethelper().replacetabfragment(fragmatriclist,false, true);
                break;
            case R.id.img_add_icon:

               if(fragment instanceof fragmentvideolist){
                   launchvideocomposer(false);
               }
                break;
        }
    }*/
}
