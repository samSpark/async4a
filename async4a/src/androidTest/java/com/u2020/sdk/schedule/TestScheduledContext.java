package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.LoopBuilder;
import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;
import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.internal.Logger;
import com.u2020.sdk.sched.internal.RequestScheduler;
import com.u2020.sdk.sched.mixtape.ScheduledContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestScheduledContext {
    private ScheduledContext scheduledContext;

    @Before
    public void init() {
        scheduledContext = new ScheduledContext(LoopBuilder.create().build());
        assertTrue(scheduledContext.isStarted());
    }

    @Test
    public void runUnion() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 3;
        scheduler.delayTime = 1;
        scheduler.internalTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        CountDownLatch latch = new CountDownLatch(scheduler.frequency);
        Request<Void> request = RequestExecutors.pingable(() -> {
            Logger.d("schedule runs");
            latch.countDown();
        });
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(request, scheduler);
        scheduledContext.runUnion(requestScheduler);
        latch.await(scheduler.delayTime + scheduler.frequency * scheduler.internalTime, scheduler.timeUnit);
        assertEquals(0, latch.getCount());
    }

    @Test
    public void loopAndStop() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.loop = true;
        scheduler.delayTime = 1;
        scheduler.internalTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        Request<Void> request = RequestExecutors.pingable(() -> {
            Logger.d("schedule runs the loop");
        });
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(request, scheduler);
        scheduledContext.runUnion(requestScheduler);
        TimeUnit.SECONDS.sleep(10);
        scheduler.loop = false;
        Logger.d("stop the loop");
        TimeUnit.SECONDS.sleep(3);
    }

    @After
    public void stop() throws InterruptedException {
        scheduledContext.stop();
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 1;
        scheduler.internalTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        CountDownLatch latch = new CountDownLatch(scheduler.frequency);
        Request<Void> request = RequestExecutors.pingable(() -> {
            Logger.d("schedule runs nothing");
            latch.countDown();
        });
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(request, scheduler);
        scheduledContext.runUnion(requestScheduler);
        latch.await(scheduler.delayTime + scheduler.frequency * scheduler.internalTime, scheduler.timeUnit);
        assertEquals(scheduler.frequency, latch.getCount());
    }
}
