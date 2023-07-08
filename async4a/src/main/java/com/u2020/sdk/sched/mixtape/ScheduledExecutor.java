package com.u2020.sdk.sched.mixtape;


final class ScheduledExecutor implements ScheduledExecutorService {
    private final FixedScheduledThread scheduledThread;

    public ScheduledExecutor() {
        scheduledThread = new FixedScheduledThread();
        scheduledThread.start();
    }

    @Override
    public void execute(ScheduledRunnable runnable) {
        scheduledThread.schedule(runnable);
    }

    @Override
    public boolean shutdownNow() {
        scheduledThread.quit();
        return true;
    }

    @Override
    public void clear() {
        scheduledThread.clear();
    }

    @Override
    public boolean isStarted() {
        return scheduledThread.isStarted();
    }
}
