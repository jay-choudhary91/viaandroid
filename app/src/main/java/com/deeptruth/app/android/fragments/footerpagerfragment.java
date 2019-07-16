package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.homeactivity;
import com.deeptruth.app.android.models.intro;



/**
 * Created by root on 26/7/18.
 */

public class footerpagerfragment extends Fragment {
    View rootview;
    boolean isgifloaded =false;
    intro introobject=null;
    TextView btnstartrecord;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_pager_footer, container, false);
            TextView txt_title = (TextView) rootview.findViewById(R.id.txt_title);
            TextView txt_description = (TextView) rootview.findViewById(R.id.txt_description);
            introobject=(intro)getArguments().getParcelable("object");
            btnstartrecord = (TextView) rootview.findViewById(R.id.btn_start_record);

            txt_title.setText(introobject.getTitle());
            txt_description.setText(introobject.getDescription());

           if(introobject.getPosition() == 6){
               txt_title.setVisibility(View.GONE);
               txt_description.setVisibility(View.INVISIBLE);
               btnstartrecord.setVisibility(View.VISIBLE);
           }

            btnstartrecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in=new Intent(getActivity(),homeactivity.class);
                    startActivity(in);
                    getActivity().overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
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


    public static footerpagerfragment newInstance(intro intro) {
        footerpagerfragment f = new footerpagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }
}
