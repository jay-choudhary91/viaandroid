package com.deeptruth.app.android.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ali Muzaffar on 2/01/2016.
 */
public class uithreadpool {
    private static uithreadpool mInstance;
    private ThreadPoolExecutor mThreadPoolExec;
    private static final int MAX_POOL_SIZE = 4;
    private static final int KEEP_ALIVE = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

    public static synchronized void post(Runnable runnable) {
        if (mInstance == null) {
            mInstance = new uithreadpool();
        }
        mInstance.mThreadPoolExec.execute(runnable);
    }

    private uithreadpool() {
        int coreNum = Runtime.getRuntime().availableProcessors();
        mThreadPoolExec = new ThreadPoolExecutor(
                coreNum,
                MAX_POOL_SIZE,
                KEEP_ALIVE,
                TimeUnit.SECONDS,
                workQueue);
    }

    public static void finish() {
        mInstance.mThreadPoolExec.shutdown();
    }
}
