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

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class backgroundexecutor {

    private static final String tag = "backgroundexecutor";

    public static final Executor default_executor = Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors());
    private static Executor executor = default_executor;
    private static final List<task> TASKS = new ArrayList<>();
    private static final ThreadLocal<String> current_serial = new ThreadLocal<>();

    private backgroundexecutor() {
    }

    /**
     * Execute a runnable after the given delay.
     *
     * @param runnable the task to execute
     * @param delay    the time from now to delay execution, in milliseconds
     *                 <p>
     *                 if <code>delay</code> is strictly positive and the current
     *                 executor does not support scheduling (if
     *                 Executor has been called with such an
     *                 executor)
     * @return Future associated to the running task
     * @throws IllegalArgumentException if the current executor set by Executor
     *                                  does not support scheduling
     */
    private static Future<?> directexecute(Runnable runnable, long delay) {
        Future<?> future = null;
        if (delay > 0) {
            /* no serial, but a delay: schedule the task */
            if (!(executor instanceof ScheduledExecutorService)) {
                throw new IllegalArgumentException("The executor set does not support scheduling");
            }
            ScheduledExecutorService scheduledexecutorservice = (ScheduledExecutorService) executor;
            future = scheduledexecutorservice.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        } else {
            if (executor instanceof ExecutorService) {
                ExecutorService executorservice = (ExecutorService) executor;
                future = executorservice.submit(runnable);
            } else {
                /* non-cancellable task */
                executor.execute(runnable);
            }
        }
        return future;
    }

    /**
     * Execute a task after (at least) its delay <strong>and</strong> after all
     * TASKS added with the same non-null <code>serial</code> (if any) have
     * completed execution.
     *
     * @param task the task to execute
     * @throws IllegalArgumentException if <code>task.delay</code> is strictly positive and the
     *                                  current executor does not support scheduling (if
     *                                  Executor has been called with such an
     *                                  executor)
     */
    public static synchronized void execute(task task) {
        Future<?> future = null;
        if (task.serial == null || !hasserialrunning(task.serial)) {
            task.executionasked = true;
            future = directexecute(task, task.remainingdelay);
        }
        if ((task.id != null || task.serial != null) && !task.managed.get()) {
            /* keep task */
            task.future = future;
            TASKS.add(task);
        }
    }

    /**
     * Indicates whether a task with the specified <code>serial</code> has been
     * submitted to the executor.
     *
     * @param serial the serial queue
     * @return <code>true</code> if such a task has been submitted,
     * <code>false</code> otherwise
     */
    private static boolean hasserialrunning(String serial) {
        for (backgroundexecutor.task task : TASKS) {
            if (task.executionasked && serial.equals(task.serial)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve and remove the first task having the specified
     * <code>serial</code> (if any).
     *
     * @param serial the serial queue
     * @return task if found, <code>null</code> otherwise
     */
    private static task take(String serial) {
        int len = TASKS.size();
        for (int i = 0; i < len; i++) {
            if (serial.equals(TASKS.get(i).serial)) {
                return TASKS.remove(i);
            }
        }
        return null;
    }

    /**
     * Cancel all TASKS having the specified <code>id</code>.
     *
     * @param id                    the cancellation identifier
     * @param mayInterruptIfRunning <code>true</code> if the thread executing this task should be
     *                              interrupted; otherwise, in-progress TASKS are allowed to
     *                              complete
     */
    public static synchronized void cancelall(String id, boolean mayInterruptIfRunning) {
        for (int i = TASKS.size() - 1; i >= 0; i--) {
            task task = TASKS.get(i);
            if (id.equals(task.id)) {
                if (task.future != null) {
                    task.future.cancel(mayInterruptIfRunning);
                    if (!task.managed.getAndSet(true)) {
						/*
						 * the task has been submitted to the executor, but its
						 * execution has not started yet, so that its run()
						 * method will never call postExecute()
						 */
                        task.postExecute();
                    }
                } else if (task.executionasked) {
                    Log.w(tag, "A task with id " + task.id + " cannot be cancelled (the executor set does not support it)");
                } else {
					/* this task has not been submitted to the executor */
                    TASKS.remove(i);
                }
            }
        }
    }

    public static abstract class task implements Runnable {

        private String id;
        private long remainingdelay;
        private long targettimemillis; /* since epoch */
        private String serial;
        private boolean executionasked;
        private Future<?> future;

        /*
         * A task can be cancelled after it has been submitted to the executor
         * but before its run() method is called. In that case, run() will never
         * be called, hence neither will postExecute(): the TASKS with the same
         * serial identifier (if any) will never be submitted.
         *
         * Therefore, cancelall() *must* call postExecute() if run() is not
         * started.
         *
         * This flag guarantees that either cancelall() or run() manages this
         * task post execution, but not both.
         */
        private AtomicBoolean managed = new AtomicBoolean();

        public task(String id, long delay, String serial) {
            if (!"".equals(id)) {
                this.id = id;
            }
            if (delay > 0) {
                remainingdelay = delay;
                targettimemillis = System.currentTimeMillis() + delay;
            }
            if (!"".equals(serial)) {
                this.serial = serial;
            }
        }

        @Override
        public void run() {
            if (managed.getAndSet(true)) {
                /* cancelled and postExecute() already called */
                return;
            }

            try {
                current_serial.set(serial);
                execute();
            } finally {
                /* handle next TASKS */
                postExecute();
            }
        }

        public abstract void execute();

        private void postExecute() {
            if (id == null && serial == null) {
				/* nothing to do */
                return;
            }
            current_serial.set(null);
            synchronized (backgroundexecutor.class) {
				/* execution complete */
                TASKS.remove(this);

                if (serial != null) {
                    task next = take(serial);
                    if (next != null) {
                        if (next.remainingdelay != 0) {
							/* the delay may not have elapsed yet */
                            next.remainingdelay = Math.max(0L, targettimemillis - System.currentTimeMillis());
                        }
						/* a task having the same serial was queued, execute it */
                        backgroundexecutor.execute(next);
                    }
                }
            }
        }
    }
}

