package com.deeptruth.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.homeactivity;
import com.deeptruth.app.android.models.intro;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;


/**
 * Created by root on 26/7/18.
 */

public class footerpagerfragment extends Fragment {
    View rootview;
    intro introobject=null;
    TextView btnstartrecord;
    LinearLayout layout_checkbox,layout_textview_container;
    CheckBox notifycheckbox;
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
            layout_textview_container = (LinearLayout) rootview.findViewById(R.id.layout_textview_container);

            layout_checkbox = rootview.findViewById(R.id.layout_checkbox);
            notifycheckbox = (CheckBox) rootview.findViewById(R.id.showintroscreen_checkbox);
            notifycheckbox.setText(getResources().getString(R.string.dispaly_startup_screens_again));
            notifycheckbox.setTextColor(getResources().getColor(R.color.white));

            if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() ||
                    xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("1")){
              notifycheckbox.setChecked(true);
             }


            introobject=(intro)getArguments().getParcelable("object");
            btnstartrecord = (TextView) rootview.findViewById(R.id.btn_start_record);

            txt_title.setText(introobject.getTitle());
            txt_descriptionone.setText(introobject.getScreenonelineone());
            txt_descriptiontwo.setText(introobject.getScreentwolinetwo());
            txt_descriptionthree.setText(introobject.getScreenthreelinethree());
            txt_descriptionfour.setText(introobject.getScreenfourlinefour());
            txt_descriptionfive.setText(introobject.getScreenfivelinefive());

            if(introobject.getPosition() != 1)
                layout_textview_container.setVisibility(View.VISIBLE);

            if(introobject.getPosition() == 1)
            {
                txt_descriptionone.setTextSize(15);
                txt_descriptiontwo.setTextSize(15);
                txt_descriptionthree.setTextSize(15.1f);
                txt_descriptionfour.setTextSize(15.4f);
                txt_descriptionfive.setTextSize(15.2f);
                /*txt_betaversion.setText(getActivity().getResources().getString(R.string.betaversion)
                        + " " + common.betaversion_dateformat());*/
                if(introobject.isShouldslidescreen())
                {
                    introobject.setShouldslidescreen(false);
                    loadheaderimageanimation(true);
                }
                else
                {
                    loadheaderimageanimation(false);
                }
            }
            else if(introobject.getPosition() == 2)
            {
                txt_descriptionone.setTextSize(15);
                txt_descriptiontwo.setTextSize(15.9f);
                txt_descriptionthree.setTextSize(15);
                txt_descriptionfour.setTextSize(14.6f);
                txt_descriptionfive.setTextSize(15.8f);
            }
            else if(introobject.getPosition() == 3)
            {
                txt_descriptionone.setTextSize(15.2f);
                txt_descriptiontwo.setTextSize(15f);
                txt_descriptionthree.setTextSize(15.1f);
                txt_descriptionfour.setTextSize(15.4f);
                txt_descriptionfive.setTextSize(15.9f);
            }
            else if(introobject.getPosition() == 4)
            {
                txt_descriptionone.setTextSize(14.5f);
                txt_descriptiontwo.setTextSize(14.9f);
                txt_descriptionthree.setTextSize(14.5f);
                txt_descriptionfour.setTextSize(15f);
                txt_descriptionfive.setTextSize(15.1f);
            }
            else if(introobject.getPosition() == 5)
            {
                txt_descriptionone.setTextSize(14.5f);
                txt_descriptiontwo.setTextSize(14.6f);
                txt_descriptionthree.setTextSize(14.55f);
                txt_descriptionfour.setTextSize(14.8f);
                txt_descriptionfive.setTextSize(14.8f);
            }

           if(introobject.getPosition() == 6){
               txt_title.setVisibility(View.GONE);
               txt_descriptionone.setVisibility(View.GONE);
               txt_descriptiontwo.setVisibility(View.GONE);
               txt_descriptionthree.setVisibility(View.GONE);
               txt_descriptionfour.setVisibility(View.INVISIBLE);
               txt_descriptionfive.setVisibility(View.INVISIBLE);
               btnstartrecord.setVisibility(View.VISIBLE);
               layout_checkbox.setVisibility(View.VISIBLE);
           }

            btnstartrecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (notifycheckbox.isChecked())
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

        return rootview;
    }

    public void loadheaderimageanimation(boolean shoulddelay)
    {
        if(shoulddelay)
        {
            layout_textview_container.setVisibility(View.VISIBLE);
            Animation animSlide = AnimationUtils.loadAnimation(getActivity(),R.anim.view_slide_from_right_fast);
            layout_textview_container.startAnimation(animSlide);
        }
        else
        {
            layout_textview_container.setVisibility(View.VISIBLE);
        }
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
