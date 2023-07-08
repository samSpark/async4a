package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.internal.RequestScheduler;

abstract class ScheduledRunnable extends PriorityRunnable {
    private final RequestScheduler<?> requestScheduler;

    public ScheduledRunnable(RequestScheduler<?> requestScheduler) {
        super(Integer.MAX_VALUE);
        this.requestScheduler = requestScheduler;
    }

    public Request<?> getRequest() {
        return requestScheduler.getRequest();
    }

    public Scheduler getScheduler() {
        return requestScheduler.getScheduler();
    }

}
