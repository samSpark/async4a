package com.u2020.sdk.sched.mixtape;

import java.util.Collection;

interface SequenceQueue<E extends PriorityRunnable> extends DriftingQueue<E> {
    void addAll(Collection<? extends E> c);

    void remove(E e);
}
