package com.u2020.sdk.sched;

import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.bridge.CompletionConsumer;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;

public class MuteResponse implements Response {
    @Override
    public Response allOfComplete(CompletionConsumer<Pair<RequestInfo, ? super Object>[], ? super Object> allOfCompletion) {
        return null;
    }

    @Override
    public Response oneOfComplete(CompletionConsumer<Pair<RequestInfo, ? super Object>, ? super Object> oneOfCompletion) {
        return null;
    }

    @Override
    public Response anyOfComplete(CompletionConsumer<Pair<RequestInfo, ? super Object>, ? super Object> anyOfCompletion) {
        return null;
    }

    @Override
    public Response thenRun(Runnable runnable) {
        return null;
    }

    @Override
    public void orThrowable(BiConsumer<RequestInfo, ? super Error> throwable) {

    }
}
