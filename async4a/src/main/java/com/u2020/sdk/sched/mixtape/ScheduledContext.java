package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.LoopService;
import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.internal.RequestScheduler;

public final class ScheduledContext implements RunnableScheduledContext {
    private static LoopService loopService;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean isShutdown;

    public ScheduledContext(LoopService loopService) {
        this.scheduledExecutor = new ScheduledExecutor();
        ScheduledContext.loopService = loopService;
    }

    @Override
    public void runUnion(RequestScheduler<?> requestScheduler) {
        if (!isStarted()) return;
        ScheduledRunnable runnable = getScheduledRunnable(requestScheduler);
        scheduledExecutor.execute(runnable);
    }

    @Override
    public boolean isStarted() {
        return scheduledExecutor.isStarted() && !isShutdown;
    }

    @Override
    public void stop() {
        isShutdown = scheduledExecutor.shutdownNow();
        loopService = null;
    }

    @Override
    public void clear() {
        scheduledExecutor.clear();
    }

    private ScheduledRunnable getScheduledRunnable(RequestScheduler<?> requestScheduler) {
        return new ScheduledRunnableImpl(requestScheduler);
    }

    static class ScheduledRunnableImpl extends ScheduledRunnable {
        public ScheduledRunnableImpl(RequestScheduler<?> requestScheduler) {
            super(requestScheduler);
        }

        @Override
        public void run() {
            if(loopService != null) {
                Request<?> request = getRequest();
                request.setPriority(getPriority());
                loopService.ping(request);
            }
        }
    }
}
