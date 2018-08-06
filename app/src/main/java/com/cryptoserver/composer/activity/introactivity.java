package com.cryptoserver.composer.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.pagerfragment;
import com.cryptoserver.composer.models.intro;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;


public class introactivity extends FragmentActivity {

    int currentselected,nextselection;
    pagercustomduration viewpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_pager);

        viewpager = (pagercustomduration) findViewById(R.id.viewpager);
        //viewpager.setPageTransformer(false, new pageranimation());
        viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //setviewpager(0);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager, true);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("Position Off pix", position+" "+positionOffset+" "+positionOffsetPixels) ;
                if ( position == currentselected )
                {
                    // We are moving to next screen on right side
                    if ( positionOffset > 0.5 )
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
                    if ( positionOffset > 0.5 )
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
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("position", position+" ") ;
                currentselected = position;
                nextselection = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("state", state+" ") ;
            }
        });

    }

    public void setviewpager(int position)
    {
        viewpager.setCurrentItem(position, true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager, true);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return pagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.simply_secure_icon));
                case 1: return pagerfragment.newInstance(new intro(getResources().getString(R.string.point_shoot),
                        getResources().getString(R.string.video_manager),R.drawable.point_shoot));
                case 2: return pagerfragment.newInstance(new intro(getResources().getString(R.string.provable_protection),
                        getResources().getString(R.string.varifiable),R.drawable.provable_protection));

                default: return pagerfragment.newInstance(new intro(getResources().getString(R.string.simply_secure),
                        getResources().getString(R.string.modern_security),R.drawable.simply_secure_icon));
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}