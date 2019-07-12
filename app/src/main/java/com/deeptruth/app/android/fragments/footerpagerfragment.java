package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.intro;


/**
 * Created by root on 26/7/18.
 */

public class footerpagerfragment extends Fragment {
    View rootview;
    boolean isgifloaded =false;
    intro introobject=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_pager_footer, container, false);
            TextView txt_title = (TextView) rootview.findViewById(R.id.txt_title);
            TextView txt_description = (TextView) rootview.findViewById(R.id.txt_description);
            ImageView img_introicon= (ImageView) rootview.findViewById(R.id.img_introicon) ;
            introobject=(intro)getArguments().getParcelable("object");
            Glide.with(this).load(introobject.getImage()).into(img_introicon);
            txt_title.setText(introobject.getTitle());
            txt_description.setText(introobject.getDescription());

            Glide.with(this).load(introobject.getImage()).into(img_introicon);
        }

        return rootview;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }


    public static footerpagerfragment newInstance(intro intro) {
        footerpagerfragment f = new footerpagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }
}
