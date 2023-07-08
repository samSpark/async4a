package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.internal.RequestScheduler;

public interface RunnableScheduledContext extends Context {
    void runUnion(RequestScheduler<?> requestScheduler);
}
