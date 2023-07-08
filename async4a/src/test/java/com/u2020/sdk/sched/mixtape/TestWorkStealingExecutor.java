package com.u2020.sdk.sched.mixtape;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestWorkStealingExecutor {

    private SequenceExecutorService workStealingExecutor;

    @Before
    public void before() {
        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        workStealingExecutor = new WorkStealingExecutor(executor);
    }

    @Test
    public void execute() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        workStealingExecutor.execute(new PriorityRequest() {
            @Override
            public void run() {
                super.run();
                System.out.println("schedule of max-priority runs");
                latch.countDown();
            }
        }, new PriorityRequest(1) {
            @Override
            public void run() {
                super.run();
                System.out.println("schedule of priority1 runs");
                latch.countDown();
            }
        }, new PriorityRequest(2) {
            @Override
            public void run() {
                super.run();
                System.out.println("schedule of priority2 runs");
                latch.countDown();
            }
        });
        latch.await();
    }

    @After
    public void after() throws InterruptedException {
        workStealingExecutor.shutdownNow();
        workStealingExecutor.execute(new PriorityRequest() {
            @Override
            public void run() {
                super.run();
                System.out.println("schedule runs nothing");
            }
        });
        TimeUnit.SECONDS.sleep(1);
    }
}
