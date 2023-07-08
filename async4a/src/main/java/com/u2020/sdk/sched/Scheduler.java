package com.u2020.sdk.sched;

import java.util.concurrent.TimeUnit;

public class Scheduler {
    public volatile boolean loop;
    public int frequency;
    public long delayTime;
    public long internalTime;
    public TimeUnit timeUnit;
}
