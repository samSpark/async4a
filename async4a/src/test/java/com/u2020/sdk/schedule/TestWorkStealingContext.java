package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;
import com.u2020.sdk.sched.bridge.Supplier;
import com.u2020.sdk.sched.mixtape.RunnableContext;
import com.u2020.sdk.sched.mixtape.WorkStealingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWorkStealingContext {

    private RunnableContext workStealingContext;

    @Before
    public void before() {
        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        workStealingContext = new WorkStealingContext(executor);
        assertTrue(workStealingContext.isStarted());
    }

    @Test
    public void runUnion() throws InterruptedException {
        Integer result = 20230214;
        CountDownLatch latch = new CountDownLatch(1);
        Request<Integer> request = RequestExecutors.pingable(new Supplier<Integer>() {
            @Override
            public Integer get() {
                latch.countDown();
                return result;
            }
        });
        workStealingContext.runUnion(request);
        latch.await();
        assertEquals(result, request.get());
    }

    @After
    public void after() throws InterruptedException {
        workStealingContext.stop();
        workStealingContext.runUnion(RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                System.out.println("schedule runs nothing");
            }
        }));
        TimeUnit.SECONDS.sleep(1);
    }
}
