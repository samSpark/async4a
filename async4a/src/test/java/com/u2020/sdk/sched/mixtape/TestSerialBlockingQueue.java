package com.u2020.sdk.sched.mixtape;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSerialBlockingQueue {
    private final SerialBlockingQueue<PriorityRequest> serialBlockingQueue = new SerialBlockingQueue<>();
    private final PriorityRequest pr1 = new PriorityRequest(1);
    private final PriorityRequest pr2 = new PriorityRequest(2);
    private final PriorityRequest pr3 = new PriorityRequest(3);

    @Before
    public void add() {
        assertTrue(serialBlockingQueue.isEmpty());
        serialBlockingQueue.add(pr1);
        serialBlockingQueue.add(pr3);
        serialBlockingQueue.add(pr2);
        assertFalse(serialBlockingQueue.isEmpty());
    }

    @Test
    public void poll() {
        assertEquals(pr3, serialBlockingQueue.poll());
        assertEquals(pr2, serialBlockingQueue.poll());
        assertEquals(pr1, serialBlockingQueue.poll());
        assertTrue(serialBlockingQueue.isEmpty());
    }

    @Test
    public void addAll() {
        List<PriorityRequest> prs = new ArrayList<>();
        prs.add(new PriorityRequest(4));
        prs.add(new PriorityRequest(5));
        prs.add(new PriorityRequest(6));
        serialBlockingQueue.addAll(prs);
        assertEquals(6, serialBlockingQueue.poll().getPriority());
        assertEquals(5, serialBlockingQueue.poll().getPriority());
        assertEquals(4, serialBlockingQueue.poll().getPriority());
        assertEquals(3, serialBlockingQueue.poll().getPriority());
        assertEquals(2, serialBlockingQueue.poll().getPriority());
        assertEquals(1, serialBlockingQueue.poll().getPriority());
    }

    @Test
    public void remove() {
        serialBlockingQueue.remove(pr3);
        assertEquals(pr2, serialBlockingQueue.poll());
    }

    @Test
    public void clear() {
        serialBlockingQueue.clear();
        assertTrue(serialBlockingQueue.isEmpty());
    }
}
