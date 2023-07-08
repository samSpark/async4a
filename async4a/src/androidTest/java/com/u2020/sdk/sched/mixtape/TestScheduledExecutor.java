package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.internal.Logger;
import com.u2020.sdk.sched.internal.RequestScheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestScheduledExecutor {
    private final ScheduledExecutor scheduledExecutor = new ScheduledExecutor();

    @Before
    public void isStarted() {
        assertTrue(scheduledExecutor.isStarted());
    }

    @Test
    public void execute() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 3;
        scheduler.delayTime = 1;
        scheduler.internalTime = 2;
        scheduler.timeUnit = TimeUnit.SECONDS;
        CountDownLatch latch = new CountDownLatch(scheduler.frequency);
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(null, scheduler);
        scheduledExecutor.execute(new ScheduledRunnable(requestScheduler) {
            @Override
            public void run() {
                Logger.d("schedule runs");
                latch.countDown();
            }
        });
        latch.await(scheduler.delayTime + scheduler.frequency * scheduler.internalTime, scheduler.timeUnit);
        assertEquals(0, latch.getCount());
    }

    @After
    public void shutDown() throws InterruptedException {
        scheduledExecutor.shutdownNow();
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 1;
        scheduler.delayTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        CountDownLatch latch = new CountDownLatch(scheduler.frequency);
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(null, scheduler);
        scheduledExecutor.execute(new ScheduledRunnable(requestScheduler) {
            @Override
            public void run() {
                Logger.d("schedule runs nothing after shutDown");
                latch.countDown();
            }
        });
        latch.await(scheduler.delayTime + scheduler.frequency * scheduler.internalTime, scheduler.timeUnit);
        assertEquals(1, latch.getCount());
    }
}
