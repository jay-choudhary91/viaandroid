package com.cryptoserver.composer.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.footerpagerfragment;
import com.cryptoserver.composer.fragments.headerpagerfragment;
import com.cryptoserver.composer.models.intro;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;

import java.util.Date;

public class introscreenactivity extends AppCompatActivity {

    int currentselected,currentselectedheader,nextselection;
    pagercustomduration viewpager_header,viewpager_footer;
    int touchstate=0;
    float positionoffset=0;
    boolean touched =false;
    boolean isinbackground=false;
    boolean slidebytime=false;
    boolean isrighttoleft=false;
    Date initialDate;
    private Handler myHandler;
    private Runnable myRunnable;
    RelativeLayout rootview;

    introscreenactivity.headerpageradapter view_pagerheader;
    introscreenactivity.footerpageradapter view_pagerfooter;
    TextView btn_start_record;

    RadioGroup radioGroup;


    @Override
    public void onStop() {
        super.onStop();
        isinbackground=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isinbackground=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isinbackground=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introactivity2);
        xdata.getinstance().saveSetting(xdata.developermode,"");
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            Intent in=new Intent(introscreenactivity.this,homeactivity.class);
            startActivity(in);
            finish();
        }

        initialDate=new Date();
        viewpager_header = (pagercustomduration) findViewById(R.id.viewpager_header);
        viewpager_footer = (pagercustomduration) findViewById(R.id.viewpager_footer);
        btn_start_record = (TextView) findViewById(R.id.btn_start_record);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        viewpager_header.setPageTransformer(false, new pageranimation());
        viewpager_footer.setPageTransformer(false, new pageranimation());

        view_pagerheader = new introscreenactivity.headerpageradapter(getSupportFragmentManager());
        view_pagerfooter = new introscreenactivity.footerpageradapter(getSupportFragmentManager());
        viewpager_header.setAdapter(view_pagerheader);
        viewpager_footer.setAdapter(view_pagerfooter);

        // viewpager_header.setAdapter(new headerpageradapter(getSupportFragmentManager()));
        //viewpager_footer.setAdapter(new footerpageradapter(getSupportFragmentManager()));

        btn_start_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(introscreenactivity.this,homeactivity.class);
                startActivity(in);
                finish();
            }
        });

        viewpager_header.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                initialDate = new Date();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touched = true;
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

        viewpager_footer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                initialDate = new Date();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touched = true;
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


        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                if(! isinbackground)
                {
                    Date currentDate=new Date();
                    int secondDifference= (int) (Math.abs(initialDate.getTime()-currentDate.getTime())/1000);
                    if(secondDifference > 3)
                    {
                        initialDate = new Date();

                        if(currentselected < viewpager_footer.getAdapter().getCount())
                        {
                            if(currentselected == 0)
                                currentselected++;

                            slidebytime=true;
                            setviewpager(currentselected);
                            currentselected++;
                        }

                        /*if(currentselectedheader < viewpager_header.getAdapter().getCount())
                        {
                            if(currentselectedheader == 0)
                                currentselectedheader++;

                            slidebytime=true;
                            setviewpagerheader(currentselectedheader);
                            currentselectedheader++;
                        }*/
                        /*if(currentselected == viewpager_header.getAdapter().getCount())
                        {
                            currentselected=0;
                            slidebytime=true;
                            setviewpager(currentselected);
                        }*/
                    }
                }
                myHandler.postDelayed(this, 300);
            }
        };
        myHandler.post(myRunnable);


        viewpager_header.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float positionandoffset=0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             //   Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;
               /* isrighttoleft = position + positionOffset > positionandoffset;
                positionandoffset = position + positionOffset;

                positionoffset=positionOffset;*/
                //  Log.e("isrighttoleft", " "+isrighttoleft);

            }

            @Override
            public void onPageSelected(int position) {
                //Log.e("position", position+" ") ;
                currentselected=position;
                //slidebytime=false;
                viewpager_footer.setCurrentItem(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                Log.e("scrollChangedheader ","" + state);
                /*if(touchstate == 1 && state == 2)
                {
                    if(isrighttoleft)
                    {
                        if(positionoffset > 0.5)
                        {
                            Log.e("isrighttoleft", " 1") ;
                            int newcount=currentselected+1;
                            if(newcount < viewpager_header.getAdapter().getCount())
                            {
                                currentselected++;
                                viewpager_header.setCurrentItem(currentselected, true);
                                //setviewpager(currentselected);
                            }
                        }
                    }
                    else
                    {
                        if(positionoffset < 0.5)
                        {
                            Log.e("islefttoright", " 2") ;
                            int newcount=currentselected-1;
                            if(newcount > -1)
                            {
                                currentselected--;
                                viewpager_header.setCurrentItem(currentselected, true);
                                //setviewpager(currentselected);
                            }
                        }
                    }
                }*/
                touchstate=state;
                /*onPageScrollStateChanged:        1             SCROLL_STATE_DRAGGING
                onPageScrollStateChanged:        2             SCROLL_STATE_SETTLING
                onPageScrollStateChanged:        0             SCROLL_STATE_IDLE*/
            }
        });

        viewpager_footer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;

            }

            @Override
            public void onPageSelected(int position) {
                //Log.e("position", position+" ") ;
                currentselected=position;
                viewpager_header.setCurrentItem(position, true);

                radioGroup.check(radioGroup.getChildAt(position%4).getId());

                //  tabLayout.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {


               /* if (currentselected == 0)
                    viewpager_header.setCurrentItem(3 - 1, false); // lastPageIndex is the index of the last item, in this case is pointing to the 2nd A on the list. This variable should be declared and initialzed as a global variable

                // For going from the last item to the first item, when the 2nd C goes to the 2nd A on the right, we let the ViewPager do it's job for us, once the movement is completed, we set the current item to the 1st A.
                // Set the current item to the second item if the current position is on the last
                if (currentselected == 3)
                    viewpager_header.setCurrentItem(0, false);*/


                touchstate=state;
            }
        });

    }
    public void setviewpager(int position)
    {
        Log.e("Positions ", position+" ") ;
        initialDate = new Date();
        viewpager_header.setCurrentItem(position, true);
        viewpager_footer.setCurrentItem(position, true);
        radioGroup.check(radioGroup.getChildAt(position%4).getId());
    }

    private class headerpageradapter extends FragmentStatePagerAdapter {

        public headerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 4;
            switch(fragmentPos) {

                case 0: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield_newgif));
                case 1: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.point_shoot),
                        getResources().getString(R.string.video_manager),R.drawable.mobile_newgif));
                case 2: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.gloab_newgif));
                case 3: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.key_newgif));

                default: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield_newgif));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }

    private class footerpageradapter extends FragmentStatePagerAdapter{

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 4;
            switch(fragmentPos) {

                case 0: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield_newgif));
                case 1: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.point_shoot),
                        getResources().getString(R.string.video_manager),R.drawable.mobile_newgif));
                case 2: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.gloab_newgif));
                case 3: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.key_newgif));

                default: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield_newgif));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);

          /*  if (position <= getCount()) {
                FragmentManager manager = ((Fragment) object).getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }*/


        }
    }
}
