package com.u2020.sdk.sched.mixtape;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDecadanceQueue {

    private final DriftingQueue<PriorityRunnable> decadanceQueue = new DecadanceQueue<>();

    @Test
    public void addAndPollScheduler() {
        PriorityRunnable s1 = new PriorityRequest();
        PriorityRunnable s2 = new PriorityRequest();
        PriorityRunnable s3 = new PriorityRequest();
        decadanceQueue.add(s1);
        decadanceQueue.add(s2);
        decadanceQueue.add(s3);
        assertEquals(s1, decadanceQueue.poll());
        assertEquals(s2, decadanceQueue.poll());
        assertEquals(s3, decadanceQueue.poll());
    }

    @Test
    public void addAndPollRequest() {
        PriorityRunnable r1 = new PriorityRequest(1);
        PriorityRunnable r2 = new PriorityRequest(2);
        PriorityRunnable r3 = new PriorityRequest(3);
        decadanceQueue.add(r1);
        decadanceQueue.add(r2);
        decadanceQueue.add(r3);
        assertEquals(r3, decadanceQueue.poll());
        assertEquals(r2, decadanceQueue.poll());
        assertEquals(r1, decadanceQueue.poll());
    }


    @Test
    public void addAndPollDriftingQueue() {
        PriorityRunnable r = new PriorityRequest(1);
        PriorityRunnable s = new PriorityRequest();
        decadanceQueue.add(r);
        decadanceQueue.add(s);
        assertEquals(s, decadanceQueue.poll());
        assertEquals(r, decadanceQueue.poll());
    }

    @Test
    public void clearDriftingQueue() {
        PriorityRunnable r = new PriorityRequest(1);
        PriorityRunnable s = new PriorityRequest();
        decadanceQueue.add(r);
        assertFalse(decadanceQueue.isEmpty());
        decadanceQueue.clear();
        assertTrue(decadanceQueue.isEmpty());
        decadanceQueue.add(s);
        assertFalse(decadanceQueue.isEmpty());
        decadanceQueue.clear();
        assertTrue(decadanceQueue.isEmpty());
        decadanceQueue.add(r);
        decadanceQueue.add(s);
        assertFalse(decadanceQueue.isEmpty());
        decadanceQueue.clear();
        assertTrue(decadanceQueue.isEmpty());
    }
}
