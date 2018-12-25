package com.cryptoserver.composer.utils;

import android.util.Log;


public class logs {
    private static boolean showLogs = true;

    public static void show(String txt) {
        if (showLogs) {
            Log.d("App logs", txt);
        }

    }
}
