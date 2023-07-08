package com.u2020.sdk.sched.mixtape;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

final class SerialBlockingQueue<E extends PriorityRunnable> implements SequenceQueue<E> {
    private final int DEFAULT_INITIAL_CAPACITY = 6;
    private final PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<>(DEFAULT_INITIAL_CAPACITY, new Comparator<E>() {
        @Override
        public int compare(E o1, E o2) {
            int x = 0; int y = o1.getPriority() - o2.getPriority();
            return compareWith(x, y);
        }
    });

    private int compareWith(int x, int y) {
        return x < y ? -1 : 1;
    }

    @Override
    public void add(E e) {
        try {
            queue.add(e);
        } catch (OutOfMemoryError exception) {
            int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
            throw new OutOfMemoryError("out of the ReqBlockingQueue range->" + MAX_ARRAY_SIZE);
        }
    }

    @Override
    public void addAll(Collection<? extends E> c) {
        try {
            queue.addAll(c);
        } catch (OutOfMemoryError exception) {
            int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
            throw new OutOfMemoryError("out of the ReqBlockingQueue range->" + MAX_ARRAY_SIZE);
        }
    }

    @Override
    public void remove(E e) {
        if (e != null) {
            queue.remove(e);
        }
    }

    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }
}
