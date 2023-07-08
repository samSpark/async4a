package com.u2020.sdk.sched.bridge;

import com.u2020.sdk.sched.internal.annotation.Nullable;

public interface CompletionConsumer<T, R> {
    R accept(@Nullable T t);
}
