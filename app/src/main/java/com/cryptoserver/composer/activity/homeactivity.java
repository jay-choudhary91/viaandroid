package com.cryptoserver.composer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.fragmentvideocomposer;
import com.cryptoserver.composer.fragments.fragmentvideolist;
import com.cryptoserver.composer.fragments.writerappactivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeactivity extends baseactivity {

    @BindView(R.id.img_add_icon)
    ImageView imgaddicon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        applicationviavideocomposer.setActivity(homeactivity.this);

        fragmentvideolist frag=new fragmentvideolist();
        replaceFragment(frag, false, true);

        imgaddicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fragmentvideocomposer frag=new fragmentvideocomposer();
                //replaceFragment(frag, false, true);

                Intent in=new Intent(homeactivity.this,writerappactivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_homeactivity;
    }

    @Override
    public void launchHome() {

    }

    @Override
    public void updateheader(String txt) {

    }

    @Override
    public void updateActionBar(int showHide, String color) {

    }

    @Override
    public void updateActionBar(int showHide) {

    }

    @Override
    public void showPermissionDialog() {

    }

    @Override
    public void onfragmentbackstackchanged() {
        super.onfragmentbackstackchanged();
        basefragment fragment = getcurrentfragment();

        if (fragment instanceof fragmentvideolist) {

        }
        else if (fragment instanceof fragmentvideocomposer) {

        }
    }
}
