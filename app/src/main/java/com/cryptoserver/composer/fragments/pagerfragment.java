package com.cryptoserver.composer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.models.intro;


/**
 * Created by root on 26/7/18.
 */

public class pagerfragment extends Fragment {
    ImageView img_image;
    View RootView;
    boolean isGifLoaded=false;
    int gifFile=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(RootView == null)
        {
            RootView = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView txt_title = (TextView) RootView.findViewById(R.id.txt_title);
            TextView txt_description = (TextView) RootView.findViewById(R.id.txt_description);
            TextView btn_start_record = (TextView) RootView.findViewById(R.id.btn_start_record);
            img_image = (ImageView) RootView.findViewById(R.id.img_image);

            intro introobject=(intro)getArguments().getParcelable("Object");

            txt_title.setText(introobject.getTitle());
            txt_description.setText(introobject.getDescription());
            img_image.setImageResource(introobject.getImage());
            //tv.setBackgroundColor(Color.parseColor(getArguments().getString("color")));

        }

        return RootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    public static pagerfragment newInstance(intro intro) {

        Object object=null;
        pagerfragment f = new pagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("Object", (Parcelable) intro);

        f.setArguments(b);

        return f;
    }
}
