package com.cryptoserver.composer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by devesh on 5/11/18.
 */

public class valueupdateservice extends Service {
    private Context context ;
    private String videourl = "";




   public valueupdateservice(Context context, String url){

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
