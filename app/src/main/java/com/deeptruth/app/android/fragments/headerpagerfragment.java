package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.intro;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by root on 26/7/18.
 */

public class headerpagerfragment extends Fragment {
    GifImageView imggif;
    View rootview;
    boolean isanimationloaded =false;
    intro introobject=null;
    TextView txt_proofofthetruth;
    ImageView img_introicon;
    RelativeLayout headerlayouttwo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_pager_header, container, false);
            img_introicon= (ImageView) rootview.findViewById(R.id.img_introicon) ;
            txt_proofofthetruth = (TextView) rootview.findViewById(R.id.txt_proofofthetruth) ;
            headerlayouttwo = (RelativeLayout)  rootview.findViewById(R.id.headerlayouttwo) ;
            ImageView img_logoicon = (ImageView) rootview.findViewById(R.id.img_logoicon) ;
            introobject=(intro)getArguments().getParcelable("object");

            if(introobject.getPosition() != 1)
                loadheaderimageanimation(false,introobject.getImage());

            if(introobject.getPosition() == 1)
            {
                if(introobject.isShouldslidescreen())
                {
                    introobject.setShouldslidescreen(false);
                    loadheaderimageanimation(true,introobject.getImage());
                }
                else
                {
                    loadheaderimageanimation(false,introobject.getImage());
                }
            }
            else if(introobject.getPosition() == 6)
            {
                img_introicon.setVisibility(View.INVISIBLE);
                headerlayouttwo.setVisibility(View.VISIBLE);
                txt_proofofthetruth.setVisibility(View.VISIBLE);
                img_introicon.setVisibility(View.VISIBLE);
                img_introicon.setImageResource(introobject.getImage());
                loadAnimation();
            }
        }
        return rootview;
    }

    public void loadheaderimageanimation(boolean shoulddelay,int image)
    {
        if(shoulddelay)
        {
            img_introicon.setVisibility(View.VISIBLE);
            img_introicon.setImageResource(image);
            Animation animSlide = AnimationUtils.loadAnimation(getActivity(),R.anim.view_slide_from_right_slow);
            img_introicon.startAnimation(animSlide);
        }
        else
        {
            img_introicon.setVisibility(View.VISIBLE);
            img_introicon.setImageResource(image);
        }
    }

    public void loadAnimation()
    {
        try {
            isanimationloaded =true;
            Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fadein);
            animFadeIn.setDuration(3000);
            animFadeIn.setRepeatCount(1);
            txt_proofofthetruth.startAnimation(animFadeIn);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(txt_proofofthetruth != null)
            loadAnimation();
    }


    public static headerpagerfragment newInstance(intro intro) {
        headerpagerfragment f = new headerpagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }
}
