package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.intro;


/**
 * Created by root on 26/7/18.
 */

public class composefooterpagerfragment extends Fragment {
    View rootview;
    boolean isgifloaded =false;
    intro introobject=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.adapter_composefooter, container, false);
            TextView txt_content = (TextView) rootview.findViewById(R.id.txt_content);
            String content=getArguments().getString("content");
            txt_content.setText(content);
        }

        return rootview;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }


    public static composefooterpagerfragment newInstance(String content) {
        composefooterpagerfragment f = new composefooterpagerfragment();
        Bundle b = new Bundle();
        b.putString("content", content);
        f.setArguments(b);
        return f;
    }
}
