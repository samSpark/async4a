package com.u2020.sdk.sched;

import com.u2020.sdk.sched.bridge.LayoutManger;
import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Request<T> extends LayoutManger<T> {
    int getId();

    String getName();

    Request<T> setName(String name);

    int getPriority();

    Request<T> setPriority(int priority);

    long getCreationTime();

    Request<T> addListener(BiConsumer<Pair<RequestInfo, T>, ? super Error> listener);

    @Request.RenderState String getState();

    @Retention(RetentionPolicy.SOURCE)
    @interface RenderState {
        String NEW = "NEW";
        String RUNNABLE = "RUNNABLE";
        //String INTERRUPTED = "INTERRUPTED";
        //String BLOCKED = "BLOCKED";
        //String WAITING = "WAITING";
        //String TIMED_WAITING = "TIMED_WAITING";
        String PENDING = "PENDING";
        String TERMINATED = "TERMINATED";
        String CANCELED = "CANCELED";
        String QUIT = "QUIT";
    }
}
