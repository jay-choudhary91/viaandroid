package com.cryptoserver.composer.activity;

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
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.pagerfragment;
import com.cryptoserver.composer.models.intro;

import com.cryptoserver.composer.views.pagercustomduration;
import java.util.Date;



public class introactivity extends FragmentActivity {

    int currentselected,nextselection;
    pagercustomduration viewpager;
    int touchstate=0;
    boolean pressed=false;
    Date initialDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_pager);
        initialDate=new Date();
        viewpager = (pagercustomduration) findViewById(R.id.viewpager);
        //viewpager.setPageTransformer(false, new pageranimation());
        viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager, true);

        viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                initialDate = new Date();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        pressed = true;
                        Log.e("user touch","on touch" + pressed);
                        break;

                    case MotionEvent.ACTION_UP:
                        pressed = false;
                        Log.e("on touch end ","on touch end" + pressed);
                        break;
                }
                return false;
            }
        });
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,100);
                Date currentDate=new Date();
                int secondDifference= (int) (Math.abs(initialDate.getTime()-currentDate.getTime())/1000);
                //Log.e("insec",""+secondDifference);
                if(secondDifference >= 4)
                {
                    initialDate = new Date();

                    if(currentselected < viewpager.getAdapter().getCount())
                    {
                        if(currentselected == 0)
                            currentselected++;

                        setviewpager(currentselected);
                        currentselected++;
                    }
                    else if(currentselected == viewpager.getAdapter().getCount())
                    {
                        currentselected=0;
                        setviewpager(currentselected);
                    }
                }
            }
        },100);


        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               // Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;
              /*  if(touchstate > 0)
                    return;*/

                /*if ( position == currentselected )
                {
                    // We are moving to next screen on right side
                    if ( positionOffset > 0.4 )
                    {
                        // Closer to next screen than to current
                        if ( position + 1 != nextselection )
                        {
                            nextselection = position + 1;
                            setviewpager( nextselection);
                        }
                    }
                    else
                    {
                        // Closer to current screen than to next
                        if ( position != nextselection )
                        {
                            nextselection = position;
                            setviewpager( nextselection);
                        }
                    }
                }
                else
                {
                    // We are moving to next screen left side
                    if ( positionOffset > 0.4 )
                    {
                        // Closer to current screen than to next
                        if ( position + 1 != nextselection )
                        {
                            nextselection = position + 1;
                            setviewpager( nextselection);
                        }
                    }
                    else
                    {
                        // Closer to next screen than to current
                        if ( position != nextselection )
                        {
                            nextselection = position;
                            setviewpager( nextselection);
                        }
                    }
                }*/
            }

            @Override
            public void onPageSelected(int position) {
                //Log.e("position", position+" ") ;
                currentselected=position;
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
        viewpager.setCurrentItem(position, true);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return pagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield));
                case 1: return pagerfragment.newInstance(new intro(getResources().getString(R.string.point_shoot),
                        getResources().getString(R.string.video_manager),R.drawable.mobile));
                case 2: return pagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.globe));

                default: return pagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.shield));
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}