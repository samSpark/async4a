package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.internal.inspection.Assert;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class SequenceExecutor implements SequenceExecutorService, Runnable {
    private volatile Executor executor;
    private final SequenceQueue<PriorityRunnable> sequenceQueue = new SerialBlockingQueue<>();
    private final AtomicBoolean pending = new AtomicBoolean();
    //private volatile boolean finished;

    public SequenceExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void run() {
        int i = 0;
        for (;;i++) {
            if (i % 20 == 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
            PriorityRunnable runnable = sequenceQueue.poll();
            if (runnable != null)
                runnable.run();
            else break;
        }
        pending.set(false);
        if (!sequenceQueue.isEmpty())
            tryRequestLoop(null);
        //else finished = true;
    }

    @Override
    public void execute(PriorityRunnable... commands) {
        Assert.assertNotNull(commands);
        sequenceQueue.addAll(Arrays.asList(commands));
        //finished = false;
        tryRequestLoop(commands[0]);
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public boolean shutdownNow() {
        sequenceQueue.clear();
        return true;
    }

    @Override
    public void clear() {
        sequenceQueue.clear();
    }

    private void tryRequestLoop(PriorityRunnable command) {
        if (pending.compareAndSet(false, true))
            try {
                executor.execute(this);
            } catch (RejectedExecutionException e) {
                sequenceQueue.remove(command);
                //finished = !sequenceQueue.isEmpty();
            } catch (Exception ignored) {
            } finally {
                pending.set(false);
            }
    }
}
