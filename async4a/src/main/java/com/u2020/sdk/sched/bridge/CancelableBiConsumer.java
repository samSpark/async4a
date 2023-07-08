package com.u2020.sdk.sched.bridge;

public interface CancelableBiConsumer<T, U> extends BiConsumer<T, U> {
    void cancel(T t);
}
