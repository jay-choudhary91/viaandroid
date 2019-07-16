package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.models.intro;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by root on 26/7/18.
 */

public class headerpagerfragment extends Fragment {
    GifImageView imggif;
    View rootview;
    boolean isgifloaded =false;
    intro introobject=null;
    TextView txt_proofofthetruth;
    RelativeLayout headerlayouttwo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_pager_header, container, false);
            ImageView img_introicon= (ImageView) rootview.findViewById(R.id.img_introicon) ;
            txt_proofofthetruth = (TextView) rootview.findViewById(R.id.txt_proofofthetruth) ;
            headerlayouttwo = (RelativeLayout)  rootview.findViewById(R.id.headerlayouttwo) ;
            ImageView img_logoicon = (ImageView) rootview.findViewById(R.id.img_logoicon) ;

            introobject=(intro)getArguments().getParcelable("object");

            Glide.with(this).load(introobject.getImage()).into(img_introicon);


            if(introobject.getPosition()==6){
                Glide.with(this).load(introobject.getImage()).into(img_logoicon);
                img_introicon.setVisibility(View.INVISIBLE);
                headerlayouttwo.setVisibility(View.VISIBLE);
                txt_proofofthetruth.setVisibility(View.VISIBLE);
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fadein);
                txt_proofofthetruth.startAnimation(animFadeIn);

            }

          /*  if((introobject.getTitle().contains("Simply Secure")))
                loadAnimation();*/
        }

        return rootview;
    }


    public void loadAnimation()
    {
        try {
            isgifloaded =true;
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(getResources(), introobject.getImage());
                gifDrawable.setLoopCount(1);
                gifDrawable.setSpeed(2.0f);
                int i= gifDrawable.getDuration();
                Log.e("duration",""+i+ "........"+introobject.getTitle());

            } catch (IOException e) {
                e.printStackTrace();
            }
            imggif.setImageDrawable(gifDrawable);
            imggif.setAlpha(0f);
            imggif.setVisibility(View.VISIBLE);
            imggif.animate()
                    .alpha(1.0f)
                    .setDuration(100)
                    .setListener(null);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if(imggif != null)
           // loadAnimation();*/
    }


    public static headerpagerfragment newInstance(intro intro) {
        headerpagerfragment f = new headerpagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }
}
