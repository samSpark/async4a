package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;
import com.u2020.sdk.sched.mixtape.SequenceContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TestSequenceContext {
    private ExecutorService executorService;
    private SequenceContext seqContext;

    @Before
    public void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        seqContext = new SequenceContext(executorService);
    }

    @After
    public void clear() throws InterruptedException {
        executorService.shutdownNow();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService = null;
        seqContext = null;
    }

    @Test
    public void testExecuteRequest() throws InterruptedException {
        PriorityConsumer consumer = new PriorityConsumer(1);
        Request<?> request = RequestExecutors.pingable(consumer);
        seqContext.runUnion(request);
        assertTrue(consumer.await(1, TimeUnit.SECONDS));
        System.out.println("after await");
        assertTrue(request.isFinished());
    }

    @Test
    public void testExecuteInEs() throws InterruptedException {
        PriorityConsumer consumer = new PriorityConsumer(1, new CountDownLatch(1));
        Request<?> request = RequestExecutors.pingable(consumer);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                seqContext.runUnion(request);
            }
        });
        assertTrue(consumer.await(2, TimeUnit.SECONDS));
        System.out.println("after await");
        assertTrue(request.isFinished());
    }

    @Test
    public void testExecuteForAwait() throws InterruptedException {
        Request request = RequestExecutors.pingable(new AwaitConsumer());
        seqContext.runUnion(request);
        TimeUnit.SECONDS.sleep(5);
        assertFalse(request.isFinished());
    }

    @Test
    public void testExecuteBrokenRunnable() throws InterruptedException {
        Request<?> request = RequestExecutors.pingable(new BrokenConsumer());
        seqContext.runUnion(request);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(request.isFinished());
    }

    @Test
    public void testExecuteWithoutRejection() throws InterruptedException {
        List<Request<?>> requests = new ArrayList<>();
        for (int i = 100; i > 0; i--) {
            PriorityConsumer consumer = new PriorityConsumer(i);
            Request<?> request = RequestExecutors.pingable(consumer);
            request.setPriority(i);
            requests.add(request);
        }
        seqContext.runUnion(requests.toArray(new Request[0]));
        TimeUnit.SECONDS.sleep(2);
        System.out.println("after await");
    }

    @Test
    public void testExecuteWithRejection() throws InterruptedException {
        executorService = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        seqContext = new SequenceContext(executorService);
        List<Request<?>> requests = new ArrayList<>();
        for (int i = 100; i > 0; i--) {
            PriorityConsumer consumer = new PriorityConsumer(i);
            Request<?> request = RequestExecutors.pingable(consumer);
            request.setPriority(i);
            requests.add(request);
        }
        seqContext.runUnion(requests.toArray(new Request[0]));
        TimeUnit.SECONDS.sleep(2);
        System.out.println("after await");
    }

    @Test
    public void testExecuteRequestAndInterruptIfRunning() throws InterruptedException {
        Request<String> request = RequestExecutors.pingable(new AwaitConsumer());
        seqContext.runUnion(request);
        TimeUnit.SECONDS.sleep(1);
        //1
        assertTrue(request.cancel(true));
        assertFalse(request.isFinished());
        assertTrue(request.isCanceled());
        //2
        assertFalse(request.cancel(false));
        assertFalse(request.isFinished());
        assertTrue(request.isCanceled());
    }
}
