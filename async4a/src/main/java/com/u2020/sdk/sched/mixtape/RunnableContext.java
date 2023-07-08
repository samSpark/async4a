package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.Response;

import java.util.concurrent.Executor;


public interface RunnableContext extends Context {
    Response runUnion(Request<?>... request);
    void reloadExecutor(Executor executor);
}
