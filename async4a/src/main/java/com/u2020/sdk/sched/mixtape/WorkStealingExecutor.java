package com.u2020.sdk.sched.mixtape;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

final class WorkStealingExecutor implements SequenceExecutorService, Runnable {
    private volatile Executor executor;
    private final DriftingQueue<PriorityRunnable> driftingQueue = new DecadanceQueue<>();
    private boolean isShutdown;

    public WorkStealingExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(PriorityRunnable... commands) {
        for (PriorityRunnable command : commands) {
            driftingQueue.add(command);
            try {
                executor.execute(this);
            } catch (RejectedExecutionException e) {
                driftingQueue.poll();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public boolean shutdownNow() {
        driftingQueue.clear();
        isShutdown = true;
        return true;
    }

    @Override
    public void clear() {
        driftingQueue.clear();
    }

    @Override
    public void run() {
        if (!isShutdown) {
            PriorityRunnable runnable = driftingQueue.poll();
            if (runnable != null)
                runnable.run();
        }
    }
}
