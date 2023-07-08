package com.u2020.sdk.sched.mixtape;

interface DriftingQueue<E> {
    void add(E e);

    E poll();

    boolean isEmpty();

    void clear();
}
