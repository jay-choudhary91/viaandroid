package com.deeptruth.app.android.activity;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.fragments.footerpagerfragment;
import com.deeptruth.app.android.fragments.fourthheaderfragment;
import com.deeptruth.app.android.models.intro;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.pageranimation;
import com.deeptruth.app.android.views.pagercustomduration;

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
    footerpageradapter footerpageradapter;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_introactivity2);

        initialdate =new Date();
        viewpagerfooter = (pagercustomduration) findViewById(R.id.viewpager_footer);
        radiogroup = (RadioGroup)findViewById(R.id.radioGroup);
        viewpagerfooter.setPageTransformer(false, new pageranimation());
        footerpageradapter = new footerpageradapter(getSupportFragmentManager());
        viewpagerfooter.setAdapter(footerpageradapter);
        viewpagerfooter.setOffscreenPageLimit(5);

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

        viewpagerfooter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;

            }

            @Override
            public void onPageSelected(int position) {
                currentselected=position;
                //   viewpagerheader.setCurrentItem(position, true);
                radiogroup.check(radiogroup.getChildAt(position%5).getId());
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
        viewpagerfooter.setCurrentItem(position, true);
        radiogroup.check(radiogroup.getChildAt(position%5).getId());
        Log.e("position",""+position%5);

    }

    private class footerpageradapter extends FragmentStatePagerAdapter{

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 5;
          /*  if(fragmentPos==3){
                btnstartrecord.setVisibility(View.VISIBLE);
            }else{
                btnstartrecord.setVisibility(View.INVISIBLE);
            }*/
            switch(fragmentPos) {

                case 0: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_detail1),R.drawable.intro_icon1));
                case 1: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile2),
                        getResources().getString(R.string.intro_detail2),R.drawable.intro_icon2));
                case 2: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile3),
                        getResources().getString(R.string.intro_detail3),R.drawable.intro_icon3));
                case 3: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile4),
                        getResources().getString(R.string.intro_detail4),R.drawable.intro_icon4));
                case 4: return fourthheaderfragment.newInstance(new intro(getResources().getString(R.string.intro_titile5),
                        getResources().getString(R.string.intro_detail5),R.drawable.intro_icon5));

                default: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.intro_titile1),
                        getResources().getString(R.string.intro_detail1),R.drawable.intro_icon1));
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }
}
