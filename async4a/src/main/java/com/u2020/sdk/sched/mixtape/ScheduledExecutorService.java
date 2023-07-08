package com.u2020.sdk.sched.mixtape;


interface ScheduledExecutorService extends ExecutorService {
    boolean isStarted();

    void execute(ScheduledRunnable runnable);
}
