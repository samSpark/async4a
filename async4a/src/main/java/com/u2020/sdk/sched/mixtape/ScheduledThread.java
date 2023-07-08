package com.u2020.sdk.sched.mixtape;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

abstract class ScheduledThread extends Thread implements Handler.Callback {
    private final int priority;
    private int threadId;
    private volatile boolean started;
    private Looper looper;

    public ScheduledThread(String name) {
        super(name);
        this.priority = Process.THREAD_PRIORITY_DEFAULT;
    }

    private ScheduledThread(String name, int priority) {
        super(name);
        this.priority = priority;
    }

    protected abstract void onLooperPrepared(Handler handler);

    @Override
    public synchronized void start() {
        if (started) return;
        super.start();
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void run() {
        this.threadId = Process.myTid();
        Process.setThreadPriority(priority);
        Looper.prepare();
        this.looper = Looper.myLooper();
        if (looper == null)
            throw new NullPointerException();
        Handler handler = new Handler(looper, this);
        onLooperPrepared(handler);
        Looper.loop();
        this.threadId = -1;
    }

    public long getThreadId() {
        return threadId;
    }

    protected boolean quit() {
        if (looper != null) {
            looper.quit();
            return true;
        } else return this.isAlive();
    }
}
