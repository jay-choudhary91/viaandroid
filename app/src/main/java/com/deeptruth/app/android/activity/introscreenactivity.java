package com.deeptruth.app.android.activity;

import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.fragments.footerpagerfragment;
import com.deeptruth.app.android.fragments.headerpagerfragment;
import com.deeptruth.app.android.models.intro;
import com.deeptruth.app.android.views.pageranimation;
import com.deeptruth.app.android.views.pagercustomduration;

import java.util.Date;

public class introscreenactivity extends AppCompatActivity {

    int currentselected;
    pagercustomduration viewpagerheader, viewpagerfooter;
    int touchstate=0,currentselectedduration=5;
    boolean touched =false;
    boolean isinbackground=false;
    boolean slidebytime=false;
    Date initialdate;
    private Handler myhandler;
    private Runnable myrunnable;
    headerpageradapter headerpageradapter;
    footerpageradapter footerpageradapter;
  //  TextView btnstartrecord;
    RadioGroup radiogroup;
    boolean shouldslidescreen=true;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_introactivity);

        //   xdata.getinstance().saveSetting(xdata.developermode,"");
        //getconnectionspeed();
        initialdate =new Date();
        viewpagerheader = (pagercustomduration) findViewById(R.id.viewpager_header);
        viewpagerfooter = (pagercustomduration) findViewById(R.id.viewpager_footer);
   //     btnstartrecord = (TextView) findViewById(R.id.btn_start_record);
        radiogroup = (RadioGroup)findViewById(R.id.radioGroup);
        viewpagerheader.setPageTransformer(false, new pageranimation());
        viewpagerfooter.setPageTransformer(false, new pageranimation());
        headerpageradapter = new introscreenactivity.headerpageradapter(getSupportFragmentManager());
        footerpageradapter = new introscreenactivity.footerpageradapter(getSupportFragmentManager());
        viewpagerheader.setAdapter(headerpageradapter);
        viewpagerfooter.setAdapter(footerpageradapter);
        viewpagerheader.setOffscreenPageLimit(6);
        viewpagerfooter.setOffscreenPageLimit(6);


        viewpagerheader.setOnTouchListener(new View.OnTouchListener() {
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


        /*myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {

                if(! isinbackground && (! touched))
                {
                    filter_date currentdate=new filter_date();
                    int seconddifference= (int) (Math.abs(initialdate.getTime()-currentdate.getTime())/1000);
                    if(seconddifference > currentselectedduration)
                    {
                        initialdate = new filter_date();

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
                myhandler.postDelayed(this, 100);
            }
        };
        myhandler.post(myrunnable);*/


        viewpagerheader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            }
        });

        viewpagerfooter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;

            }

            @Override
            public void onPageSelected(int position) {
                currentselected=position;
                viewpagerheader.setCurrentItem(position, true);
                radiogroup.check(radiogroup.getChildAt(position%6).getId());
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
        viewpagerheader.setCurrentItem(position, true);
        viewpagerfooter.setCurrentItem(position, true);
        radiogroup.check(radiogroup.getChildAt(position%6).getId());
    }

    private class headerpageradapter extends FragmentStatePagerAdapter {

        public headerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 6;
            switch(fragmentPos) {

                case 0: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_screenonelineone),
                        getResources().getString(R.string.intro_screenonelinetwo),
                        getResources().getString(R.string.intro_screenonelinethree),
                        getResources().getString(R.string.intro_screenonelinefour),
                        "",R.drawable.intro_icon1,1,shouldslidescreen));
                case 1: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile2),
                        getResources().getString(R.string.intro_screentwolineone),
                        getResources().getString(R.string.intro_screentwolinetwo),
                        getResources().getString(R.string.intro_screentwolinethree),
                        getResources().getString(R.string.intro_screentwolinefour),
                        getResources().getString(R.string.intro_screentwolinefive),R.drawable.intro_icon2,2,false));
                case 2: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile3),
                        getResources().getString(R.string.intro_screenthreelineone),
                        getResources().getString(R.string.intro_screenthreelinetwo),
                        getResources().getString(R.string.intro_screenthreelinethree),
                        getResources().getString(R.string.intro_screenthreelinefour),
                        "",R.drawable.intro_icon3,3,false));
                case 3: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile4),
                        getResources().getString(R.string.intro_screenfourlineone),
                        getResources().getString(R.string.intro_screenfourlinetwo),
                        getResources().getString(R.string.intro_screenfourlinethree),
                        getResources().getString(R.string.intro_screenfourlinefour),
                        getResources().getString(R.string.intro_screenfourlinefive),R.drawable.intro_icon5,4,false));
                case 4: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile5),
                        getResources().getString(R.string.intro_screenfivelineone),
                        getResources().getString(R.string.intro_screenfivelinetwo),
                        getResources().getString(R.string.intro_screenfivelinethree),
                        getResources().getString(R.string.intro_screenfivelinefour),
                        getResources().getString(R.string.intro_screenfivelinefive),R.drawable.intro_icon4,5,false));
                case 5: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_screenonelineone),
                        getResources().getString(R.string.intro_screenonelinetwo),
                        getResources().getString(R.string.intro_screenonelinethree),
                        getResources().getString(R.string.intro_screenfivelinefive),
                        getResources().getString(R.string.intro_detail1),R.drawable.splash_logo_icon,6,false));

                default: return headerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_screenonelineone),
                        getResources().getString(R.string.intro_screenonelinetwo),
                        getResources().getString(R.string.intro_screenonelinethree),
                        getResources().getString(R.string.intro_screenfivelinefive),
                        getResources().getString(R.string.intro_detail1),R.drawable.intro_icon1,1,false));
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
            int fragmentPos = pos % 6;
            switch(fragmentPos) {

                case 0: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_screenonelineone),
                        getResources().getString(R.string.intro_screenonelinetwo),
                        getResources().getString(R.string.intro_screenonelinethree),
                        getResources().getString(R.string.intro_screenonelinefour),
                        "",R.drawable.intro_icon1,1,shouldslidescreen));
                case 1: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile2),
                        getResources().getString(R.string.intro_screentwolineone),
                        getResources().getString(R.string.intro_screentwolinetwo),
                        getResources().getString(R.string.intro_screentwolinethree),
                        getResources().getString(R.string.intro_screentwolinefour),
                        getResources().getString(R.string.intro_screentwolinefive),R.drawable.intro_icon2,2,false));
                case 2: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile3),
                        getResources().getString(R.string.intro_screenthreelineone),
                        getResources().getString(R.string.intro_screenthreelinetwo),
                        getResources().getString(R.string.intro_screenthreelinethree),
                        getResources().getString(R.string.intro_screenthreelinefour),
                        getResources().getString(R.string.intro_screenthreelinefive),R.drawable.intro_icon3,3,false));
                case 3: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile4),
                        getResources().getString(R.string.intro_screenfourlineone),
                        getResources().getString(R.string.intro_screenfourlinetwo),
                        getResources().getString(R.string.intro_screenfourlinethree),
                        getResources().getString(R.string.intro_screenfourlinefour),
                        getResources().getString(R.string.intro_screenfourlinefive),R.drawable.intro_icon5,4,false));
                case 4: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile5),
                        getResources().getString(R.string.intro_screenfivelineone),
                        getResources().getString(R.string.intro_screenfivelinetwo),
                        getResources().getString(R.string.intro_screenfivelinethree),
                        getResources().getString(R.string.intro_screenfivelinefour),
                        getResources().getString(R.string.intro_screenfivelinefive),R.drawable.intro_icon4,5,false));
                case 5: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_screenonelineone),
                        getResources().getString(R.string.intro_screenonelinetwo),
                        getResources().getString(R.string.intro_screenonelinethree),
                        getResources().getString(R.string.intro_screenfivelinefive),
                        getResources().getString(R.string.intro_detail1),R.drawable.splash_logo_icon,6,false));

                default: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_screenonelineone),
                        getResources().getString(R.string.intro_screenonelinetwo),
                        getResources().getString(R.string.intro_screenonelinethree),
                        getResources().getString(R.string.intro_screenfivelinefive),
                        getResources().getString(R.string.intro_detail1),R.drawable.intro_icon1,1,false));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }
}
