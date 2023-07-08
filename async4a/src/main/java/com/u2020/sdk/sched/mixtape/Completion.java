package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestInfo;
import com.u2020.sdk.sched.Response;
import com.u2020.sdk.sched.bridge.BiCompletionConsumer;
import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.bridge.CompletionConsumer;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("rawtypes")
public final class Completion implements Response, BiConsumer {
    private final AtomicInteger oneOfAtomic = new AtomicInteger();
    private final AtomicInteger allOfAtomic = new AtomicInteger();
    private final CopyOnWriteArrayList<Pair> pairs = new CopyOnWriteArrayList<>();
    private CompletionConsumer<Pair<RequestInfo, Object>[], Object> allOfCompletion;
    private CompletionConsumer<Pair<RequestInfo, Object>, Object> oneOfCompletion, anyOfCompletion;
    private Runnable thenRunnable;
    private BiConsumer<RequestInfo, ? super Error> throwable;
    private volatile Error error;

    public Response allOfComplete(CompletionConsumer<Pair<RequestInfo, Object>[], Object> allOfCompletion) {
        this.allOfCompletion = allOfCompletion;
        return this;
    }

    public Response oneOfComplete(CompletionConsumer<Pair<RequestInfo, Object>, Object> oneOfCompletion) {
        this.oneOfCompletion = oneOfCompletion;
        return this;
    }

    public Response anyOfComplete(CompletionConsumer<Pair<RequestInfo, Object>, Object> anyOfCompletion) {
        this.anyOfCompletion = anyOfCompletion;
        return this;
    }

    public Response thenRun(Runnable runnable) {
        this.thenRunnable = runnable;
        return this;
    }

    public void orThrowable(BiConsumer<RequestInfo, ? super Error> throwable) {
        this.throwable = throwable;
    }

    @SuppressWarnings("unchecked")
    protected void whenComplete(Request<?>[] requests) {
        allOfAtomic.set(requests.length);
        for (Request<?> request : requests)
            //noinspection
            request.addListener(Completion.this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(Object pair, Object error) {
        Pair<RequestInfo, Object> paired;
        pairs.add(paired = (Pair<RequestInfo, Object>) pair);
        Error erred = (Error) error;

        if (throwable != null && erred != null) {
            try {
                throwable.accept(paired.first, erred);
            } catch (Throwable ignored) {
            }
        }

        if (this.error == null && erred != null) {
            this.error = erred;
        }

        try {
            if (oneOfCompletion != null && erred == null && oneOfAtomic.incrementAndGet() == 1) {
                if (oneOfCompletion instanceof BiCompletionConsumer)
                    ((BiCompletionConsumer)oneOfCompletion).andThen(paired, oneOfCompletion.accept(paired));
                else oneOfCompletion.accept(paired);
            }

            if (anyOfCompletion != null && erred == null) {
                if (anyOfCompletion instanceof BiCompletionConsumer)
                    ((BiCompletionConsumer)anyOfCompletion).andThen(paired, anyOfCompletion.accept(paired));
                else anyOfCompletion.accept(paired);
            }

            if (allOfAtomic.decrementAndGet() == 0) {
                if (allOfCompletion != null && this.error == null) {
                    Pair<RequestInfo, Object>[] pairArray = pairs.toArray(new Pair[0]);
                    if (allOfCompletion instanceof BiCompletionConsumer)
                        ((BiCompletionConsumer)allOfCompletion).andThen(pairArray, allOfCompletion.accept(pairArray));
                    else allOfCompletion.accept(pairArray);
                }
                pairs.clear();
                if (thenRunnable != null) {
                    thenRunnable.run();
                }
            }
        } catch (Throwable e) {
            if (throwable != null) {
                try {
                    throwable.accept(paired.first, new Error(e));
                } catch (Exception ignored) {
                }
            } else e.printStackTrace();
        }
    }
}
