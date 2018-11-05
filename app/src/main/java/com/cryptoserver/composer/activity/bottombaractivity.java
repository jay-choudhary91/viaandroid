package com.cryptoserver.composer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.fragmentvideolist;

public class bottombaractivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_audio:
                  /* fragmentvideolist fragvideolist=new fragmentvideolist();
                    replaceFragment(fragvideolist, false, true);*/

                    mTextMessage.setText(R.string.title_audio);
                    return true;
                case R.id.navigation_video:
                    mTextMessage.setText(R.string.title_video);
                    return true;
                case R.id.navigation_image:
                    mTextMessage.setText(R.string.title_image);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottombaractivity);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
