package com.u2020.sdk.schedule;


import org.junit.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestScheduledThreadPool {
    @Test
    public void testIntervalTask() throws InterruptedException {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        }, 1, TimeUnit.SECONDS);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("1");
            }
        }, 1, TimeUnit.SECONDS);
        AtomicInteger i = new AtomicInteger(1);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                int r = i.incrementAndGet();
                if (r == 3) throw new RuntimeException();
                System.out.println("2");
            }
        }, 1, 1, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("3");
            }
        }, 1, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
    }
}
