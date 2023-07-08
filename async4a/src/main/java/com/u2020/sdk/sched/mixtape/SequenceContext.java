package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class SequenceContext implements RunnableContext {
    private final SequenceExecutorService serialExecutor;
    private final AtomicReference<Transporter> transporter = new AtomicReference<>(new Transporter());

    public SequenceContext(Executor executor) {
        this.serialExecutor = new SequenceExecutor(executor);
    }

    @Override
    public Completion runUnion(Request<?>... requests) {
        PriorityRunnable[] runnableList = new PriorityRunnable[requests.length];
        for (int i = 0; i < requests.length; i++) {
            PriorityRunnable runnable = getPriorityRunnable(requests[i]);
            runnableList[i] = runnable;
        }
        serialExecutor.execute(runnableList);
        return null;
    }

    @Override
    public void reloadExecutor(Executor executor) {
        this.serialExecutor.setExecutor(executor);
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void stop() {
        transporter.set(null);
        serialExecutor.shutdownNow();
    }

    @Override
    public void clear() {
        transporter.set(null);
        serialExecutor.clear();
    }

    private PriorityRunnable getPriorityRunnable(Request<?> request) {
        return new PriorityRunnableImpl(request, transporter);
    }
}
