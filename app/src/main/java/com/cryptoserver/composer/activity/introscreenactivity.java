package com.cryptoserver.composer.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.footerpagerfragment;
import com.cryptoserver.composer.fragments.headerpagerfragment;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.intro;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;

import java.util.Date;

public class introscreenactivity extends AppCompatActivity {

    int currentselected;
    pagercustomduration viewpagerheader, viewpagerfooter;
    int touchstate=0,currentselectedduration=3;
    boolean touched =false;
    boolean isinbackground=false;
    boolean slidebytime=false;
    Date initialdate;
    private Handler myhandler;
    private Runnable myrunnable;
   // headerpageradapter headerpageradapter;
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
        setContentView(R.layout.activity_introactivity2);
        xdata.getinstance().saveSetting(xdata.developermode,"");

        //getconnectionspeed();
        initialdate =new Date();
       // viewpagerheader = (pagercustomduration) findViewById(R.id.viewpager_header);
        viewpagerfooter = (pagercustomduration) findViewById(R.id.viewpager_footer);
        btnstartrecord = (TextView) findViewById(R.id.btn_start_record);
        radiogroup = (RadioGroup)findViewById(R.id.radioGroup);
//        viewpagerheader.setPageTransformer(false, new pageranimation());
        viewpagerfooter.setPageTransformer(false, new pageranimation());
        //headerpageradapter = new introscreenactivity.headerpageradapter(getSupportFragmentManager());
        footerpageradapter = new introscreenactivity.footerpageradapter(getSupportFragmentManager());
       // viewpagerheader.setAdapter(headerpageradapter);
        viewpagerfooter.setAdapter(footerpageradapter);
     //   viewpagerheader.setOffscreenPageLimit(4);
        viewpagerfooter.setOffscreenPageLimit(4);
        btnstartrecord.setVisibility(View.GONE);
        common.changeFocusStyle(btnstartrecord,getResources().getColor(R.color.btn_background),40);
        btnstartrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(introscreenactivity.this,homeactivity.class);
                startActivity(in);
                finish();
            }
        });

       /* viewpagerheader.setOnTouchListener(new View.OnTouchListener() {
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
*/
        viewpagerfooter.setOnTouchListener(new View.OnTouchListener() {
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

                if(! isinbackground && (! touched))
                {
                    Date currentdate=new Date();
                    int seconddifference= (int) (Math.abs(initialdate.getTime()-currentdate.getTime())/1000);
                    if(seconddifference > currentselectedduration)
                    {
                        initialdate = new Date();

                        if(currentselected < viewpagerfooter.getAdapter().getCount())
                        {
                            if(currentselected == 0)
                                currentselected++;

                            slidebytime=true;
                            setviewpager(currentselected);
                            currentselected++;
                        }
                    }
                }
                myhandler.postDelayed(this, 200);
            }
        };
        myhandler.post(myrunnable);


       /* viewpagerheader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentselected=position;
                viewpagerfooter.setCurrentItem(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("scrollChangedheader ","" + state);
                touchstate=state;
                *//*onPageScrollStateChanged:        1             SCROLL_STATE_DRAGGING
                onPageScrollStateChanged:        2             SCROLL_STATE_SETTLING
                onPageScrollStateChanged:        0             SCROLL_STATE_IDLE*//*
            }
        });*/

        viewpagerfooter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;

            }

            @Override
            public void onPageSelected(int position) {
                currentselected=position;
             //   viewpagerheader.setCurrentItem(position, true);
                radiogroup.check(radiogroup.getChildAt(position%4).getId());
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
        initialdate = new Date();
//        viewpagerheader.setCurrentItem(position, true);
        viewpagerfooter.setCurrentItem(position, true);
        if (position % 4 == 3) {
            btnstartrecord.setVisibility(View.VISIBLE);
        }else{
            btnstartrecord.setVisibility(View.INVISIBLE);
        }
        radiogroup.check(radiogroup.getChildAt(position%4).getId());
        Log.e("position",""+position%4);

    }

    /*private class headerpageradapter extends FragmentStatePagerAdapter {

        public headerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 4;
            switch(fragmentPos) {

                case 0: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.capture),
                        getResources().getString(R.string.start_by_documenting),R.drawable.mobile_newgif));
                case 1: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.verify),
                        getResources().getString(R.string.once_your_media_is_recorded),R.drawable.shield_newgif));
                case 2: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.Share),
                        getResources().getString(R.string.share_your_media_via_text),R.drawable.globe_newgif));
                case 3: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.learn),
                        getResources().getString(R.string.watch_helpful_videos),R.drawable.key_newgif));

                default: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.capture),
                        getResources().getString(R.string.start_by_documenting),R.drawable.shield_newgif));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }*/



    private class footerpageradapter extends FragmentStatePagerAdapter{

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 4;
            if(fragmentPos==3){
                btnstartrecord.setVisibility(View.VISIBLE);
            }else{
                btnstartrecord.setVisibility(View.INVISIBLE);
            }
            switch(fragmentPos) {

                case 0: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.capture),
                        getResources().getString(R.string.start_by_documenting),R.drawable.mobile_newgif));
                case 1: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.verify),
                        getResources().getString(R.string.once_your_media_is_recorded),R.drawable.shield_newgif));
                case 2: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.Share),
                        getResources().getString(R.string.share_your_media_via_text),R.drawable.globe_newgif));
                case 3: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.learn),
                        getResources().getString(R.string.watch_helpful_videos),R.drawable.key_newgif));

                default: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.capture),
                        getResources().getString(R.string.start_by_documenting),R.drawable.shield_newgif));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }
}
