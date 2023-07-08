package com.u2020.sdk.sched.mixtape;

import android.annotation.SuppressLint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public final class ForkThreadFactoryBuilder extends ThreadFactoryBuilder {
    private ForkJoinPool.ForkJoinWorkerThreadFactory doBuildFork(ThreadFactoryBuilder builder) {
        return new DefaultForkJoinWorkerThreadFactory();
    }

    @SuppressLint("NewApi")
    private static final class DefaultForkJoinWorkerThreadFactory
            implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = new DefaultForkJoinWorkerThread(pool);
            return thread;
        }
    }

    @SuppressLint("NewApi")
    private static final class DefaultForkJoinWorkerThread extends ForkJoinWorkerThread {
        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        protected DefaultForkJoinWorkerThread(ForkJoinPool pool) {
            super(pool);
            setContextClassLoader(Thread.currentThread().getContextClassLoader());
        }
    }

    public ForkJoinPool.ForkJoinWorkerThreadFactory buildFork() {
        return doBuildFork(this);
    }

    @SuppressLint("NewApi")
    public ExecutorService buildForkJoinPool(int parallelism,
                                             Thread.UncaughtExceptionHandler handler,
                                             boolean asyncMode) {
        return new ForkJoinPool(parallelism, buildFork(),handler, asyncMode);
    }
}
