/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.deeptruth.app.android.videotrimmer.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provide operations for
 * UiThread tasks.
 */
public final class uithreadexecutor {

    private static final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Runnable callback = msg.getCallback();
            if (callback != null) {
                callback.run();
                decrementtoken((token) msg.obj);
            } else {
                super.handleMessage(msg);
            }
        }
    };

    private static final Map<String, token> tokens = new HashMap<>();

    private uithreadexecutor() {
        // should not be instantiated
    }

    /**
     * Store a new task in the map for providing cancellation. This method is
     * used by AndroidAnnotations and not intended to be called by clients.
     *
     * @param id    the identifier of the task
     * @param task  the task itself
     * @param delay the delay or zero to run immediately
     */
    public static void runtask(String id, Runnable task, long delay) {
        if ("".equals(id)) {
            handler.postDelayed(task, delay);
            return;
        }
        long time = SystemClock.uptimeMillis() + delay;
        handler.postAtTime(task, nexttoken(id), time);
    }

    private static token nexttoken(String id) {
        synchronized (tokens) {
            token token = tokens.get(id);
            if (token == null) {
                token = new token(id);
                tokens.put(id, token);
            }
            token.runnablesCount++;
            return token;
        }
    }

    private static void decrementtoken(token token) {
        synchronized (tokens) {
            if (--token.runnablesCount == 0) {
                String id = token.id;
                uithreadexecutor.token old = tokens.remove(id);
                if (old != token) {
                    // a runnable finished after cancelling, we just removed a
                    // wrong token, lets put it back
                    tokens.put(id, old);
                }
            }
        }
    }

    /**
     * Cancel all tasks having the specified <code>id</code>.
     *
     * @param id the cancellation identifier
     */
    public static void cancelall(String id) {
        token token;
        synchronized (tokens) {
            token = tokens.remove(id);
        }
        if (token == null) {
            // nothing to cancel
            return;
        }
        handler.removeCallbacksAndMessages(token);
    }

    private static final class token {
        int runnablesCount = 0;
        final String id;

        private token(String id) {
            this.id = id;
        }
    }

}
