package com.u2020.sdk.sched.internal;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.Scheduler;

public class RequestScheduler<T> {
    private final Request<T> request;
    private final Scheduler scheduler;

    public RequestScheduler(Request<T> request, Scheduler scheduler) {
        this.request = request;
        this.scheduler = scheduler;
    }

    public Request<T> getRequest() {
        return request;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
