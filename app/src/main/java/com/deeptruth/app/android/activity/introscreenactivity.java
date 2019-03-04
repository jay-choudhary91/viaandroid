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
    pagercustomduration viewpagerfooter;
    Date initialdate;
    footerpageradapter footerpageradapter;
    RadioGroup radiogroup;

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        setContentView(R.layout.activity_introactivity2);

        initialdate =new Date();
        viewpagerfooter = (pagercustomduration) findViewById(R.id.viewpager_footer);
        radiogroup = (RadioGroup)findViewById(R.id.radioGroup);
        viewpagerfooter.setPageTransformer(false, new pageranimation());
        footerpageradapter = new footerpageradapter(getSupportFragmentManager());
        viewpagerfooter.setAdapter(footerpageradapter);
        viewpagerfooter.setOffscreenPageLimit(4);
        viewpagerfooter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentselected=position;
                radiogroup.check(radiogroup.getChildAt(position%4).getId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class footerpageradapter extends FragmentStatePagerAdapter{

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            int fragmentPos = pos % 4;
            switch(fragmentPos) {

                case 0: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.capture),
                        getResources().getString(R.string.start_by_documenting),R.drawable.mobile_newgif));
                case 1: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.verify),
                        getResources().getString(R.string.once_your_media_is_recorded),R.drawable.shield_newgif));
                case 2: return footerpagerfragment.newInstance(new intro(getResources().getString(R.string.Share),
                        getResources().getString(R.string.share_your_media_via_text),R.drawable.globe_newgif));
                case 3: return fourthheaderfragment.newInstance(new intro(getResources().getString(R.string.learn),
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
