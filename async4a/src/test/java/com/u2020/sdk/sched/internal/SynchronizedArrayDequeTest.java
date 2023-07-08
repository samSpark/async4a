package com.u2020.sdk.sched.internal;

import com.u2020.sdk.sched.LoopBuilder;
import com.u2020.sdk.sched.LoopService;
import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;
import com.u2020.sdk.sched.RequestInfo;
import com.u2020.sdk.sched.bridge.BiCompletionConsumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Deque;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SynchronizedArrayDequeTest {
    private final LoopService service = LoopBuilder.create().build();

    @Test
    public void addAndPollFirst() {
        Deque<Object> deque = new SynchronizedArrayDeque<>();
        Object obj1 = new Object();
        Object obj2 = new Object();
        deque.addFirst(obj1);
        deque.addFirst(obj2);
        assertEquals(obj2, deque.pollFirst());
        assertEquals(obj1, deque.pollFirst());
    }


    @Test
    public void addAndPollFirstInEx() throws InterruptedException {
        Deque<Object> deque = new SynchronizedArrayDeque<>();
        Object obj1 = new Object();
        Object obj2 = new Object();
        Request<Void> request1 = RequestExecutors.pingable(() -> deque.addFirst(obj1));
        Request<Void> request2 = RequestExecutors.pingable(() -> deque.addFirst(obj2));
        service.ping(request1, request2).thenRun(() -> assertEquals(2, deque.size()));
        TimeUnit.MILLISECONDS.sleep(10);
    }
}