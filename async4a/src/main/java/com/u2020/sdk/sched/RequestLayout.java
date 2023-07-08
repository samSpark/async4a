package com.u2020.sdk.sched;

import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.bridge.CancelableBiConsumer;
import com.u2020.sdk.sched.bridge.ConsumableFunction;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;
import com.u2020.sdk.sched.mixtape.BaseRequest;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class RequestLayout<T, U> extends BaseRequest<T, U> {
    private final ConsumableFunction<T, U> function;
    private final Set<BiConsumer<Pair<RequestInfo, T>, ? super Error>> consumers = new CopyOnWriteArraySet<>();
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private volatile T result, canceledResult;
    private volatile boolean isFinished;
    private volatile Thread runner;

    private RequestLayout(ConsumableFunction<T, U> function) {
        super();
        this.function = function;
    }

    private RequestLayout(ConsumableFunction<T, U> function, T result) {
        super();
        this.function = function;
        this.canceledResult = result;
    }

    public static <T, U> Request<T> of(ConsumableFunction<T, U> function) {
        return new RequestLayout<>(function);
    }

    public static <T, U> Request<T> of(ConsumableFunction<T, U> function, T result) {
        return new RequestLayout<>(function, result);
    }

    @Override
    public T get() {
        return result;
    }

    @Override
    public T get(Long timeout, TimeUnit unit) throws NullPointerException, InterruptedException, TimeoutException {
        if (timeout == null || unit == null)
            throw new NullPointerException();
        lock.lock();
        try {
            long timeoutNanos = unit.toNanos(timeout);
            long startTime = System.nanoTime();
            long elapsed = 0, gap;
            for (;;) {
                gap = timeoutNanos - elapsed;
                if (isFinished || result != null || gap <= 0)
                    break;
                if (done.awaitNanos(gap) <= 0)
                    throw new TimeoutException();
                elapsed = System.nanoTime() - startTime;
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public long getCreationTime() {
        return System.currentTimeMillis();
    }

    @Override
    public Request<T> addListener(BiConsumer<Pair<RequestInfo, T>, ? super Error> listener) {
        if (listener != null && RenderState.NEW.equals(getState())) {
            consumers.add(listener);
        }
        return this;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelable = false;
        if (thenApplyCancel()) {
            try {
                if (mayInterruptIfRunning) {
                    Thread t = runner;
                    if (t != null)
                        t.interrupt();
                }
            } finally {
                for (BiConsumer<Pair<RequestInfo, T>, ? super Error> consumer : consumers) {
                    if (consumer instanceof CancelableBiConsumer) {
                        RequestInfo requestInfo = new RequestInfo();
                        requestInfo.setId(getId());
                        requestInfo.setName(getName());
                        requestInfo.setCreationTime(getCreationTime());
                        requestInfo.setCancelTime(System.currentTimeMillis());
                        //noinspection rawtypes
                        ((CancelableBiConsumer) consumer).cancel(new Pair<>(requestInfo, result = canceledResult));
                    }
                }
                consumers.clear();
                cancelable = true;
            }
        }
        return cancelable;
    }

    @Override
    public boolean isCanceled() {
        return RenderState.CANCELED.equals(atomicState.get());
    }

    @Override
    public void runWith(U var) throws Exception {
        if (thenApplyRunning()) {
            runner = Thread.currentThread();
            try {
                result = function.apply(var);
                stateMachine.thenApply(RenderState.PENDING);
                atomicState.set(stateMachine.getCurrentStateName());
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setId(getId());
                requestInfo.setName(getName());
                requestInfo.setCreationTime(getCreationTime());
                requestInfo.setCompletionTime(System.currentTimeMillis());
                for (BiConsumer<Pair<RequestInfo, T>, ? super Error> consumer : consumers)
                    consumer.accept(new Pair<>(requestInfo, result), null);
                stateMachine.thenApply(RenderState.TERMINATED);
                atomicState.set(stateMachine.getCurrentStateName());
                isFinished = true;
                try {
                    lock.lock();
                    done.signalAll();
                } finally {
                    lock.unlock();
                }
            } catch (Throwable e) {
                if (consumers.isEmpty())
                    throw new Exception(e);
                for (BiConsumer<Pair<RequestInfo, T>, ? super Error> consumer : consumers)
                    consumer.accept(new Pair<>(new RequestInfo(), result), new Error(e));
            } finally {
                if (getPriority() != Integer.MAX_VALUE) {
                    stateMachine.thenApply(RenderState.QUIT);
                    atomicState.set(stateMachine.getCurrentStateName());
                }
                consumers.clear();
            }
        }
    }

    private boolean thenApplyRunning() {
        String state;
        String nextState;
        do {
            state = atomicState.get();
            if (RenderState.CANCELED.equals(state)) return false;//non-cancels
            else if (getPriority() != Integer.MAX_VALUE) {//non-max-priority
                if (!RenderState.NEW.equals(state))//non-new
                    return false;
            }
            stateMachine.thenApply(RenderState.RUNNABLE);
            nextState = stateMachine.getCurrentStateName();
        } while (!atomicState.compareAndSet(state, nextState));
        return true;
    }

    private boolean thenApplyCancel() {
        String state;
        String nextState;
        do {
            state = atomicState.get();
            if (RenderState.CANCELED.equals(state)
                    || RenderState.PENDING.equals(state)
                    || getPriority() != Integer.MAX_VALUE && RenderState.TERMINATED.equals(state)
                    || RenderState.QUIT.equals(state))
                return false;
            stateMachine.thenApply(RenderState.CANCELED);
            nextState = stateMachine.getCurrentStateName();
        } while (!atomicState.compareAndSet(state, nextState));
        return true;
    }

}
