package com.u2020.sdk.sched.mixtape;

import android.annotation.SuppressLint;

import com.u2020.sdk.sched.internal.SynchronizedArrayDeque;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;

final class DecadanceQueue<E extends PriorityRunnable> implements DriftingQueue<E> {
    private final LinkedBlockingQueue<E> schedulers = new LinkedBlockingQueue<>();
    private final Deque<E> requests;

    @SuppressLint("NewApi")
    DecadanceQueue() {
        Deque<E> deque;
        try {
            Class.forName("java.util.concurrent.ConcurrentLinkedDeque");
            deque = new ConcurrentLinkedDeque<>();
        } catch (Throwable e) {
            deque = new SynchronizedArrayDeque<>();
        }
        this.requests = deque;
    }

    @Override
    public void add(E e) {
        if (e.getPriority() == Integer.MAX_VALUE) {//highest priority/highest weight
            schedulers.add(e);
        } else {
            requests.addFirst(e);
        }
    }

    @Override
    public E poll() {
        E e = schedulers.poll();
        return e == null ? requests.pollFirst() : e;
    }

    @Override
    public boolean isEmpty() {
        return schedulers.isEmpty() && requests.isEmpty();
    }

    @Override
    public void clear() {
        schedulers.clear();
        requests.clear();
    }
}
