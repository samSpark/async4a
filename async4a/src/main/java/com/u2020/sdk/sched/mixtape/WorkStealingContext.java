package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.Response;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class WorkStealingContext implements RunnableContext {
    private final SequenceExecutorService workStealingExecutor;
    private final AtomicReference<Transporter> transporter = new AtomicReference<>(new Transporter());
    private volatile boolean isShutdown;

    public WorkStealingContext(Executor executor) {
        workStealingExecutor = new WorkStealingExecutor(executor);
    }

    @Override
    public Response runUnion(Request<?>... requests) {
        if (!isStarted()) return null;
        Completion response = new Completion();
        response.whenComplete(requests);
        PriorityRunnable[] commands = new PriorityRunnable[requests.length];
        for (int i = 0; i < requests.length; i++) {
            PriorityRunnable runnable = getPriorityRunnable(requests[i]);
            commands[i] = runnable;
        }
        workStealingExecutor.execute(commands);
        return response;
    }

    @Override
    public boolean isStarted() {
        return !isShutdown;
    }

    @Override
    public void stop() {
        transporter.set(null);
        isShutdown = workStealingExecutor.shutdownNow();
    }

    @Override
    public void clear() {
        transporter.set(null);
        workStealingExecutor.clear();
    }

    @Override
    public void reloadExecutor(Executor executor) {
        workStealingExecutor.setExecutor(executor);
    }

    private PriorityRunnable getPriorityRunnable(Request<?> request) {
        return new PriorityRunnableImpl(request, transporter);
    }
}
