package com.cryptoserver.composer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.homeactivity;
import com.cryptoserver.composer.activity.introscreenactivity;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.models.intro;

/**
 * A simple {@link Fragment} subclass.
 */
public class fourthheaderfragment extends Fragment {
    View rootview;
    boolean isgifloaded =false;
    TextView btnstartrecord;
    intro introobject=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_fourthheaderfragment, container, false);
            TextView txt_title = (TextView) rootview.findViewById(R.id.txt_title);
            TextView txt_description = (TextView) rootview.findViewById(R.id.txt_description);
            btnstartrecord = (TextView) rootview.findViewById(R.id.btn_start_record);


            introobject=(intro)getArguments().getParcelable("object");

            txt_title.setText(introobject.getTitle());
            txt_description.setText(introobject.getDescription());

            btnstartrecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in=new Intent(getActivity(),homeactivity.class);
                    startActivity(in);
                    getActivity().finish();
                }
            });
        }

        return rootview;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }


    public static fourthheaderfragment newInstance(intro intro) {
        fourthheaderfragment f = new fourthheaderfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }


}
