package com.cryptoserver.composer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptoserver.composer.R;


/**
 * Created by root on 26/7/18.
 */

public class pagerfragment extends Fragment {
    ImageView img_gif;
    View RootView;
    boolean isGifLoaded=false;
    int gifFile=0;
    WebView webview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(RootView == null)
        {
            RootView = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView tv = (TextView) RootView.findViewById(R.id.tvFragFirst);
            img_gif = (ImageView) RootView.findViewById(R.id.img_gif);
            webview = (WebView) RootView.findViewById(R.id.webview);

            gifFile=getArguments().getInt("gif_code");

            if(getArguments().getString("msg").contains("Welcome") && (! isGifLoaded))
                loadAnimation();

            tv.setText(getArguments().getString("msg"));
            //tv.setBackgroundColor(Color.parseColor(getArguments().getString("color")));

        }

        return RootView;
    }

    public String getGif(int code)
    {
        switch (code)
        {
            case 1:
                return "file:///android_asset/nicolas.html";
            case 2:
                return "file:///android_asset/angus.html";
            case 3:
                return "file:///android_asset/sadi.html";
            case 4:
                return "file:///android_asset/forrest.html";
        }
        return "";
    }

    public void loadAnimation()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isGifLoaded=true;
                //Glide.with(getActivity()).load(getGif(gifFile)).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().
                //      into(img_gif);
                webview.loadUrl(getGif(gifFile));
            }
        },100);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(webview != null && (! isGifLoaded))
            loadAnimation();
    }

    public static pagerfragment newInstance(String text, String bgColor) {

        pagerfragment f = new pagerfragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        b.putString("color", bgColor);

        f.setArguments(b);

        return f;
    }

    public static pagerfragment newInstance(String text, String bgColor, int imageCode) {

        pagerfragment f = new pagerfragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        b.putString("color", bgColor);
        b.putInt("gif_code",imageCode);

        f.setArguments(b);

        return f;
    }
}
