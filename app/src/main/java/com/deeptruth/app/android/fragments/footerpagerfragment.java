package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.homeactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.intro;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;


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
            TextView txt_descriptionone = (TextView) rootview.findViewById(R.id.txt_descriptionone);
            TextView txt_descriptiontwo = (TextView) rootview.findViewById(R.id.txt_descriptiontwo);
            TextView txt_descriptionthree = (TextView) rootview.findViewById(R.id.txt_descriptionthree);
            TextView txt_descriptionfour = (TextView) rootview.findViewById(R.id.txt_descriptionfour);
            TextView txt_descriptionfive = (TextView) rootview.findViewById(R.id.txt_descriptionfive);
            introobject=(intro)getArguments().getParcelable("object");
            btnstartrecord = (TextView) rootview.findViewById(R.id.btn_start_record);

            txt_title.setText(introobject.getTitle());
            txt_descriptionone.setText(introobject.getScreenonelineone());
            txt_descriptiontwo.setText(introobject.getScreentwolinetwo());
            txt_descriptionthree.setText(introobject.getScreenthreelinethree());
            txt_descriptionfour.setText(introobject.getScreenfourlinefour());
            txt_descriptionfive.setText(introobject.getScreenfivelinefive());


            if(introobject.getPosition() == 1)
            {
                txt_descriptionone.setTextSize(17);
                txt_descriptiontwo.setTextSize(17);
                txt_descriptionthree.setTextSize(17);
                txt_descriptionfour.setTextSize(17);
                txt_descriptionfive.setTextSize(16.8f);
            }
            else if(introobject.getPosition() == 2)
            {
                txt_descriptionone.setTextSize(17);
                txt_descriptiontwo.setTextSize(17.8f);
                txt_descriptionthree.setTextSize(17);
                txt_descriptionfour.setTextSize(16.5f);
                txt_descriptionfive.setTextSize(17.8f);
            }
            else if(introobject.getPosition() == 3)
            {
                txt_descriptionone.setTextSize(17);
                txt_descriptiontwo.setTextSize(16.5f);
                txt_descriptionthree.setTextSize(17);
                txt_descriptionfour.setTextSize(17);
                txt_descriptionfive.setTextSize(17);
            }
            else if(introobject.getPosition() == 4)
            {
                txt_descriptionone.setTextSize(16);
                txt_descriptiontwo.setTextSize(17);
                txt_descriptionthree.setTextSize(16);
                txt_descriptionfour.setTextSize(16.5f);
                txt_descriptionfive.setTextSize(17);
            }
            else if(introobject.getPosition() == 5)
            {
                txt_descriptionone.setTextSize(16);
                txt_descriptiontwo.setTextSize(16);
                txt_descriptionthree.setTextSize(16);
                txt_descriptionfour.setTextSize(16);
                txt_descriptionfive.setTextSize(16);
            }


           if(introobject.getPosition() == 6){
               txt_title.setVisibility(View.GONE);
               txt_descriptionone.setVisibility(View.GONE);
               txt_descriptiontwo.setVisibility(View.GONE);
               txt_descriptionthree.setVisibility(View.INVISIBLE);
               txt_descriptionfour.setVisibility(View.INVISIBLE);
               txt_descriptionfive.setVisibility(View.INVISIBLE);
               btnstartrecord.setVisibility(View.VISIBLE);
           }

            btnstartrecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    common.showalertdialog(getActivity(), getActivity().getString(R.string.do_not_notify_me_again), new adapteritemclick() {
                            @Override
                            public void onItemClicked(Object object) {

                            }

                            @Override
                            public void onItemClicked(Object object, int type) {
                                if(type == 0 )
                                    xdata.getinstance().saveSetting(config.enableintroscreen,"1");
                                else
                                    xdata.getinstance().saveSetting(config.enableintroscreen,"0");


                                Intent in=new Intent(getActivity(),homeactivity.class);
                                startActivity(in);
                                getActivity().overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
                                getActivity().finish();
                        }
                   });
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
