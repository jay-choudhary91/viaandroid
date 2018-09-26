package com.cryptoserver.composer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
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



public class introactivity extends FragmentActivity {

    int currentselected;
    pagercustomduration viewpager_header,viewpager_footer;
    int touchstate=0;
    boolean touched =false;
    boolean isinbackground=false;
    boolean slidebytime=false;
    Date initialdate;
    private Handler myhandler;
    private Runnable myrunnable;
    headerpageradapter headerpageradapter;
    footerpageradapter footerpageradapter;
    TextView btnstartrecord;
    RadioGroup radiogroup;

    @Override
    public void onStop() {
        super.onStop();
        isinbackground=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myhandler != null && myrunnable != null)
            myhandler.removeCallbacks(myrunnable);
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
        setContentView(R.layout.intro_pager);
        xdata.getinstance().saveSetting(xdata.developermode,"");
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            Intent in=new Intent(introactivity.this,homeactivity.class);
            startActivity(in);
            finish();
        }

        initialdate =new Date();
        viewpager_header = (pagercustomduration) findViewById(R.id.viewpager_header);
        viewpager_footer = (pagercustomduration) findViewById(R.id.viewpager_footer);
        btnstartrecord = (TextView) findViewById(R.id.btn_start_record);
        radiogroup = (RadioGroup)findViewById(R.id.radioGroup);

        viewpager_header.setPageTransformer(false, new pageranimation());
        viewpager_footer.setPageTransformer(false, new pageranimation());

        headerpageradapter = new headerpageradapter(getSupportFragmentManager());
        footerpageradapter = new footerpageradapter(getSupportFragmentManager());
        viewpager_header.setAdapter(headerpageradapter);
        viewpager_footer.setAdapter(footerpageradapter);

        btnstartrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(introactivity.this,homeactivity.class);
                startActivity(in);
                finish();
            }
        });

        viewpager_header.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                initialdate = new Date();
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
                initialdate = new Date();
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


        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {

                if(! isinbackground)
                {
                    Date currentDate=new Date();
                    int secondDifference= (int) (Math.abs(initialdate.getTime()-currentDate.getTime())/1000);
                    if(secondDifference > 3)
                    {
                        initialdate = new Date();

                        if(currentselected < viewpager_footer.getAdapter().getCount())
                        {
                            if(currentselected == 0)
                                currentselected++;

                            slidebytime=true;
                            setviewpager(currentselected);
                            currentselected++;
                        }

                        /*if(currentselectedheader < viewpagerheader.getAdapter().getCount())
                        {
                            if(currentselectedheader == 0)
                                currentselectedheader++;

                            slidebytime=true;
                            setviewpagerheader(currentselectedheader);
                            currentselectedheader++;
                        }*/
                        /*if(currentselected == viewpagerheader.getAdapter().getCount())
                        {
                            currentselected=0;
                            slidebytime=true;
                            setviewpager(currentselected);
                        }*/
                    }
                }
                myhandler.postDelayed(this, 300);
            }
        };
        myhandler.post(myrunnable);


        viewpager_header.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float positionandoffset=0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;
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
                            if(newcount < viewpagerheader.getAdapter().getCount())
                            {
                                currentselected++;
                                viewpagerheader.setCurrentItem(currentselected, true);
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
                                viewpagerheader.setCurrentItem(currentselected, true);
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
                viewpager_header.setCurrentItem(position%3, true);

                radiogroup.check(radiogroup.getChildAt(position%3).getId());

              //  tabLayout.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {


               /* if (currentselected == 0)
                    viewpagerheader.setCurrentItem(3 - 1, false); // lastPageIndex is the index of the last item, in this case is pointing to the 2nd A on the list. This variable should be declared and initialzed as a global variable

                // For going from the last item to the first item, when the 2nd C goes to the 2nd A on the right, we let the ViewPager do it's job for us, once the movement is completed, we set the current item to the 1st A.
                // Set the current item to the second item if the current position is on the last
                if (currentselected == 3)
                    viewpagerheader.setCurrentItem(0, false);*/


                touchstate=state;
            }
        });

    }

    public void setviewpager(int position)
    {
        Log.e("Positions ", position+" ") ;
        initialdate = new Date();
        viewpager_header.setCurrentItem(position%3, true);
        viewpager_footer.setCurrentItem(position%3, true);
        radiogroup.check(radiogroup.getChildAt(position%3).getId());
    }

    private class headerpageradapter extends FragmentPagerAdapter {

        public headerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 3;
            switch(fragmentPos) {

                case 0: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield));
                case 1: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.point_shoot),
                        getResources().getString(R.string.video_manager),R.drawable.mobile));
                case 2: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.mobile_new));

                default: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }

    private class footerpageradapter extends FragmentPagerAdapter{

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 3;
            switch(fragmentPos) {

                case 0: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield));
                case 1: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.point_shoot),
                        getResources().getString(R.string.video_manager),R.drawable.mobile));
                case 2: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.mobile_new));

                default: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }
}