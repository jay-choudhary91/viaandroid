package com.deeptruth.app.android.threadpool;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class threadpoolfactory {
    private static threadpoolfactory mInstance;
    private ThreadPoolExecutor mthreadpoolexec;
    private static int maxpoolsize;
    private static final int keepalive = 5000;
    BlockingQueue<Runnable> workqueue = new LinkedBlockingQueue<>();

    public static synchronized void post(Runnable runnable) {
        if (mInstance == null) {
            mInstance = new threadpoolfactory();
        }
        mInstance.mthreadpoolexec.execute(runnable);
    }

    private threadpoolfactory() {
        int coreNum = Runtime.getRuntime().availableProcessors();
        Log.e("availableprocessors"," "+coreNum);
        maxpoolsize = coreNum * 2;
        mthreadpoolexec = new ThreadPoolExecutor(
                coreNum,
                maxpoolsize,
                keepalive,
                TimeUnit.SECONDS,
                workqueue);
    }

    public static void finish() {
        mInstance.mthreadpoolexec.shutdown();
    }
}
