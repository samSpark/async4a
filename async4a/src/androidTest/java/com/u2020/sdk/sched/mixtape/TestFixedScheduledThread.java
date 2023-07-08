package com.u2020.sdk.sched.mixtape;


import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.internal.Logger;
import com.u2020.sdk.sched.internal.RequestScheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class TestFixedScheduledThread {
    private final FixedScheduledThread scheduledThread = new FixedScheduledThread();

    @Before
    public void start() {
        scheduledThread.start();
        assertTrue(scheduledThread.isStarted());
    }

    @Test
    public void isStart() {
        assertTrue(scheduledThread.isStarted());
    }

    @Test
    public void schedule() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 3;
        scheduler.delayTime = 1;
        scheduler.internalTime = 2;
        scheduler.timeUnit = TimeUnit.SECONDS;
        CountDownLatch latch = new CountDownLatch(scheduler.frequency);
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(null, scheduler);
        scheduledThread.schedule(new ScheduledRunnable(requestScheduler) {
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
    public void quit() throws InterruptedException {
        assertTrue(scheduledThread.quit());
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 1;
        scheduler.delayTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        CountDownLatch latch = new CountDownLatch(scheduler.frequency);
        RequestScheduler<Void> requestScheduler = new RequestScheduler<>(null, scheduler);
        scheduledThread.schedule(new ScheduledRunnable(requestScheduler) {
            @Override
            public void run() {
                Logger.d("schedule runs nothing after quit");
                latch.countDown();
            }
        });
        latch.await(scheduler.delayTime + scheduler.frequency * scheduler.internalTime, scheduler.timeUnit);
        assertEquals(1, latch.getCount());
        assertFalse(scheduledThread.isAlive());
    }
}
