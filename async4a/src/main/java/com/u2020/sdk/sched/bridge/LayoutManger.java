package com.u2020.sdk.sched.bridge;

import java.util.concurrent.TimeUnit;

public interface LayoutManger<T> extends Supplier<T>, BiSupplier<Long, TimeUnit, T> {
    boolean isFinished();

    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCanceled();
}
