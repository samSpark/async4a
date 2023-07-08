package com.u2020.sdk.sched.bridge;

import com.u2020.sdk.sched.internal.annotation.Nullable;

public abstract class BiCompletionConsumer<T, R> implements CompletionConsumer<T, R> {
    public void andThen(@Nullable T t, @Nullable R r) {}
}
