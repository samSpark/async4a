package com.u2020.sdk.schedule;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.u2020.sdk.sched.LoopService;
import com.u2020.sdk.sched.LoopBuilder;
import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;
import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.bridge.Supplier;
import com.u2020.sdk.sched.internal.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestLooperInAndroid {
    private LoopService looper;

    @Before
    public void setUp() {
        long time = System.nanoTime();
        looper = LoopBuilder.create().build();
        System.out.println("interval time->" + (System.nanoTime() - time));
    }

    @Test
    public void testCancelSchedulerInLoop() throws InterruptedException {
        Request<Void> schedule = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                Logger.i("schedule runs");
            }
        });
        Scheduler scheduler = new Scheduler();
        scheduler.loop = true;
        scheduler.internalTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        looper.ping(schedule, scheduler);
        TimeUnit.SECONDS.sleep(5);
        schedule.cancel(true);
        Logger.i("cancel schedule");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void testCancelSchedulerWithTimes() throws InterruptedException {
        Request<Void> schedule = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                Logger.i("schedule runs");
            }
        });
        Scheduler scheduler = new Scheduler();
        scheduler.frequency = 5;
        scheduler.internalTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        looper.ping(schedule, scheduler);
        TimeUnit.SECONDS.sleep(5);
        schedule.cancel(true);
        Logger.i("cancel schedule");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void testClear() throws InterruptedException {
        Request<Void> schedule = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                Logger.i("schedule runs nothing");
            }
        });
        Scheduler scheduler = new Scheduler();
        scheduler.loop = true;
        scheduler.internalTime = 1;
        scheduler.timeUnit = TimeUnit.SECONDS;
        looper.ping(schedule, scheduler);
        Request<Integer> request = RequestExecutors.pingable(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return 20230215;
            }
        });
        looper.ping(request);
        looper.clear();
        TimeUnit.SECONDS.sleep(5);
        assertNull(request.get());
    }

    @Test
    public void stop() {
        assertFalse(looper.isStopped());
    }

    @After
    public void tearDown() throws InterruptedException {
        looper.stop();
        looper.ping(RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                Logger.i("schedule runs nothing");
            }
        }));
        TimeUnit.SECONDS.sleep(2);
        assertTrue(looper.isStopped());
    }
}
