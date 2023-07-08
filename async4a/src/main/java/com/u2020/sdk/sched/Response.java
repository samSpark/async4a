package com.u2020.sdk.sched;

import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.bridge.CompletionConsumer;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;

public interface Response {
    Response allOfComplete(CompletionConsumer<Pair<RequestInfo, ? super Object>[], ? super Object> allOfCompletion);

    Response oneOfComplete(CompletionConsumer<Pair<RequestInfo, ? super Object>, ? super Object> oneOfCompletion);

    Response anyOfComplete(CompletionConsumer<Pair<RequestInfo, ? super Object>, ? super Object> anyOfCompletion);

    Response thenRun(Runnable runnable);

    void orThrowable(BiConsumer<RequestInfo, ? super Error> throwable);
}
