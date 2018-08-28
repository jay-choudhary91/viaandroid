package com.cryptoserver.composer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.footerpagerfragment;
import com.cryptoserver.composer.fragments.headerpagerfragment;
import com.cryptoserver.composer.models.intro;

import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;
import java.util.Date;



public class introactivity extends FragmentActivity {

    int currentselected,nextselection;
    pagercustomduration viewpager_header,viewpager_footer;
    int touchstate=0;
    boolean touched =false;
    boolean isinbackground=false;
    boolean slidebytime=false;
    Date initialDate;
    private Handler myHandler;
    private Runnable myRunnable;


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
        setContentView(R.layout.intro_pager);

        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            Intent in=new Intent(introactivity.this,homeactivity.class);
            startActivity(in);
            finish();
        }

        initialDate=new Date();
        viewpager_header = (pagercustomduration) findViewById(R.id.viewpager_header);
        viewpager_footer = (pagercustomduration) findViewById(R.id.viewpager_footer);

        viewpager_header.setPageTransformer(false, new pageranimation());
        viewpager_footer.setPageTransformer(false, new pageranimation());

        viewpager_header.setAdapter(new headerpageradapter(getSupportFragmentManager()));
        viewpager_footer.setAdapter(new footerpageradapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager_footer, true);

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
                    if(secondDifference > 4)
                    {
                        initialDate = new Date();

                        if(currentselected < viewpager_header.getAdapter().getCount())
                        {
                            if(currentselected == 0)
                                currentselected++;

                            slidebytime=true;
                            setviewpager(currentselected);
                            currentselected++;
                        }
                        else if(currentselected == viewpager_header.getAdapter().getCount())
                        {
                            currentselected=0;
                            slidebytime=true;
                            setviewpager(currentselected);
                        }
                    }
                }
                myHandler.postDelayed(this, 500);
            }
        };
        myHandler.post(myRunnable);


        viewpager_header.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               // Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;

                /*if ( position == currentselected )
                {
                    // We are moving to next screen on right side
                    if ( positionOffset > 0.5 )
                    {
                        // Closer to next screen than to current
                        if ( position + 1 != nextselection )
                        {
                            nextselection = position + 1;
                            currentselected=nextselection;
                            setviewpager( nextselection);
                        }
                    }
                    else
                    {
                        // Closer to current screen than to next
                        if ( position != nextselection )
                        {
                            nextselection = position;
                            currentselected=nextselection;
                            setviewpager( nextselection);
                        }
                    }
                }
                else
                {
                    // We are moving to next screen left side
                    if ( positionOffset > 0.5 )
                    {
                        // Closer to current screen than to next
                        if ( position + 1 != nextselection )
                        {
                            nextselection = position + 1;
                            currentselected=nextselection;
                            setviewpager( nextselection);
                        }
                    }
                    else
                    {
                        // Closer to next screen than to current
                        if ( position != nextselection )
                        {
                            nextselection = position;
                            currentselected=nextselection;
                            setviewpager( nextselection);
                        }
                    }
                }*/

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

                touchstate=state;
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
    }

    private class headerpageradapter extends FragmentPagerAdapter {

        public headerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

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
            return 3;
        }
    }

    private class footerpageradapter extends FragmentPagerAdapter {

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

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
            return 3;
        }
    }
}