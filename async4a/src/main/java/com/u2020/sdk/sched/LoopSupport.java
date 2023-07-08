package com.u2020.sdk.sched;

import android.annotation.SuppressLint;

import com.u2020.sdk.sched.bridge.MechanicOperator;
import com.u2020.sdk.sched.history.ViewPortLayout;
import com.u2020.sdk.sched.history.Visitor;
import com.u2020.sdk.sched.internal.Logger;
import com.u2020.sdk.sched.internal.RequestScheduler;
import com.u2020.sdk.sched.internal.inspection.Assert;
import com.u2020.sdk.sched.mixtape.ForkThreadFactoryBuilder;
import com.u2020.sdk.sched.mixtape.MechanicThreadPoolExecutor;
import com.u2020.sdk.sched.mixtape.RunnableContext;
import com.u2020.sdk.sched.mixtape.RunnableScheduledContext;
import com.u2020.sdk.sched.mixtape.ScheduledContext;
import com.u2020.sdk.sched.mixtape.SequenceContext;
import com.u2020.sdk.sched.mixtape.WorkStealingContext;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

final class LoopSupport implements LoopService {
    private int corePoolSize;
    private Semaphore threshold;
    private Visitor viewPortLayout;
    private ExecutorService executorService;
    private RunnableScheduledContext scheduledContext;
    private RunnableContext workStealingContext;
    private RunnableContext sequenceContext;
    private volatile boolean isShutdown;
    private volatile boolean isForked;
    private final Object fork = new Object();

    @SuppressWarnings("unused")
    private LoopSupport() {
    }

    protected LoopSupport(int capacity, int corePoolSize, int maximumPoolSize,
                          long keepAliveTime, TimeUnit unit, boolean allowAliveTimeOut) {
        this.corePoolSize = corePoolSize;
        this.threshold = new Semaphore(capacity);
        this.viewPortLayout = new ViewPortLayout(threshold);
        this.executorService = new MechanicThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, allowAliveTimeOut);
        this.workStealingContext = new WorkStealingContext(executorService);
        this.sequenceContext = new SequenceContext(executorService);
        this.scheduledContext = new ScheduledContext(this);
    }

    @Override
    public Response ping(Request<?>... requests) {
        Assert.assertNotNull("LoopService pings nothing", requests);
        if (isStopped()) {
            Logger.e("Ping but LoopService is stopped");
        } else if (!threshold.tryAcquire(requests.length)) {
            Logger.e("Capacity is overload");//TODO
        } else {
            viewPortLayout.addRequestLayout(requests);
            return workStealingContext.runUnion(requests);
        }
        return new MuteResponse();
    }

    @Override
    public void ping(Collection<Request<?>> requests) {
        Assert.assertNotNull("LoopService pings nothing", requests);
        if (isStopped()) {
            Logger.e("Ping but LoopService is stopped");
        } else if (!threshold.tryAcquire(requests.size())) {
            Logger.e("Capacity is overload");//TODO
        } else {
            Request<?>[] array = requests.toArray(new Request<?>[0]);
            viewPortLayout.addRequestLayout(array);
            sequenceContext.runUnion(array);
        }
    }

    @Override
    public void ping(Request<?> request, Scheduler scheduler) {
        Assert.assertNotNull("LoopService pings nothing", request);
        Assert.assertNotNull("Scheduler is null", scheduler);
        if (scheduler.loop || scheduler.frequency > 0)
            Assert.assertNotNull("TimeUnit of scheduler is null", scheduler.timeUnit);
        if (isStopped()) {
            Logger.e("Ping but LoopService is stopped");
        } else if (!threshold.tryAcquire()) {
            Logger.e("Capacity is overload");//TODO
        } else {
            viewPortLayout.addRequestLayout(request);
            scheduledContext.runUnion(new RequestScheduler<>(request, scheduler));
        }
    }

    @Override
    public void pong(MechanicOperator listener) {
        Assert.assertNotNull("LoopService listens nothing", listener);
    }

    @Override
    public RequestInfo getRequestInfo(int requestId) {
        return null;//TODO
    }

    @Override
    public List<RequestInfo> getRequestInfo() {
        return null;//TODO
    }

    @Override
    public boolean removeIf(int requestId) {
        return viewPortLayout.removeRequestLayoutIf(requestId);
    }

    @Override
    public void clear() {
        if (isStopped()) {
            Logger.e("Clear but LoopService is stopped");
        } else {
            scheduledContext.clear();
            workStealingContext.clear();
            sequenceContext.clear();
            viewPortLayout.erase();
        }
    }

    @Override
    public void stop() {
        if (isStopped()) {
            Logger.e("LoopService is stopped");
        } else {
            scheduledContext.stop();
            executorService.shutdownNow();
            sequenceContext.stop();
            scheduledContext = null;
            executorService = null;
            sequenceContext = null;
            workStealingContext = null;
            isShutdown = true;
        }
    }

    @Override
    public boolean isStopped() {
        return isShutdown || executorService != null && executorService.isShutdown();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean fork() {
        synchronized (fork) {
            if (isForked) {
                Logger.e("LoopService is already forked");
                return false;
            }
            try {
                isForked = true;
                Class.forName("java.util.concurrent.ForkJoinPool");
            } catch (Throwable e) {
                isForked = false;
            }
            isForked = !isStopped() && isForked;
            if (isForked) {
                ExecutorService es = executorService;
                try {
                    executorService = new ForkThreadFactoryBuilder().buildForkJoinPool(corePoolSize, null, false);//TODO CONFIG AND FORK INTERFACE
                    if (es != null) es.shutdown();
                    workStealingContext.reloadExecutor(executorService);
                    sequenceContext.reloadExecutor(executorService);
                } catch (Throwable e) {
                    isForked = false;
                }
            }
            return isForked;
        }
    }
}
