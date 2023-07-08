package com.u2020.sdk.sched.mixtape;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MechanicThreadPoolExecutor extends ThreadPoolExecutor {

    public MechanicThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                      long keepAliveTime, TimeUnit unit,
                                      boolean allowAliveTimeOut) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().build(),//try-caught in Runnable, not required that setUncaughtExceptionHandler
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        //Serialize
                    }
                });
        allowCoreThreadTimeOut(allowAliveTimeOut);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    public void execute(Runnable command) {//Don't catch
        if (command != null)
            super.execute(command);
    }
}
