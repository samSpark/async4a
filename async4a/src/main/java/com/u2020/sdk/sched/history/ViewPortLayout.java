package com.u2020.sdk.sched.history;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestInfo;
import com.u2020.sdk.sched.bridge.CancelableBiConsumer;
import com.u2020.sdk.sched.internal.Pair;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("rawtypes")
public class ViewPortLayout implements Visitor, CancelableBiConsumer {
    private final LayoutNodes layoutNodes;
    private final Semaphore threshold;

    public ViewPortLayout(Semaphore threshold) {
        this.threshold = threshold;
        this.layoutNodes = new LayoutNodes(threshold);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRequestLayout(Request<?>... requests) {
        for (Request<?> request : requests) {
            request.addListener(this);
            layoutNodes.add(request);
        }
    }

    @Override
    public boolean removeRequestLayoutIf(int requestId) {
        return layoutNodes.removeIf(requestId);
    }

    @Override
    public void erase() {
        layoutNodes.erase();
    }

    @Override
    public int preferredLayoutSize() {
        return layoutNodes.size();
    }

    @Override
    public void accept(Object pair, Object error) {
        cancel(pair);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cancel(Object pair) {
        Pair<RequestInfo, Object> paired = (Pair<RequestInfo, Object>) pair;
        layoutNodes.flush(paired.first.getId());
        if (threshold != null)
            threshold.release();
    }

    static final class LayoutNodes {
        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicInteger count = new AtomicInteger();
        private Semaphore threshold;
        private Node head;

        @SuppressWarnings("unused")
        private LayoutNodes() {
        }

        public LayoutNodes(Semaphore threshold) {
            this.threshold = threshold;
        }

        private int size() {
            return count.get();
        }

        private static class Node {
            Request<?> value;
            Node next;

            private Node(Request<?> value) {
                this.value = value;
            }
        }

        private boolean contains(Request<?> request) {
            return contains(request.getId());
        }

        private boolean contains(int requestId) {
            if (size() == 0) return false;
            lock.lock();
            try {
                Node cur = this.head;
                for (int i = 0; i < size(); i++) {
                    if (cur.value != null && requestId == cur.value.getId())
                        return true;
                    if (cur.next != null)
                        cur = cur.next;
                }
            } finally {
                lock.unlock();
            }
            return false;
        }

        private void add(Request<?> request) {
            if (request == null || Request.RenderState.CANCELED.equals(request.getState())
                    || Request.RenderState.QUIT.equals(request.getState())
                    || contains(request)) return;
            lock.lock();
            try {
                if (size() == 0) {
                    this.head = new Node(request);
                } else {
                    boolean reused = false;
                    Node last = this.head;
                    for (int i = 1; i < size(); i++) {
                        if (last != null && last.value == null) {
                            last.value = request;
                            reused = true;
                            break;
                        }
                        if(last != null)
                            last = last.next;
                    }
                    if (!reused && last != null)
                        last.next = new Node(request);
                }
                count.incrementAndGet();
            } finally {
                lock.unlock();
            }
        }

        private boolean removeIf(int requestId) {
            if (size() == 0) return false;
            boolean removeIf = false;
            lock.lock();
            try {
                Node next = this.head;
                if (head.value != null && head.value.getId() == requestId) {
                    next = head.next;
                    removeIf = erase(head.value);
                    head.value = null;
                    head = next;
                } else for (int i = 1; i < size(); i++) {
                    Node pre = next;
                    next = next.next;
                    if (next != null && next.value != null &&
                            next.value.getId() == requestId) {
                        Node join = next.next;
                        removeIf = erase(next.value);
                        next.value = null;
                        pre.next = join;
                        break;
                    }
                }
            } finally {
                lock.unlock();
            }
            return removeIf;
        }

        private void flush(int requestId) {
            if (size() == 0) return;
            lock.lock();
            try {
                Node next = this.head;
                if (head.value != null && head.value.getId() == requestId) {
                    head.value = null;
                } else for (int i = 1; i < size(); i++) {
                    if (next.value != null && next.value.getId() == requestId) {
                        next.value = null;
                        if (next.next != null)
                            next = next.next;
                        else break;
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        private void erase() {
            if (size() <= 0) return;
            lock.lock();
            try {
                Node cur = this.head;
                while (size() > 0) {
                    erase(cur.value);
                    cur.value = null;
                    if (cur.next != null)
                        cur = cur.next;
                    else break;
                }
            } finally {
                lock.unlock();
            }
        }

        private boolean erase(Request<?> request) {
            if (request != null) {
                int remain = count.decrementAndGet();
                boolean cancelable = request.cancel(true);
                if (remain > 0 && cancelable && threshold != null) {
                    threshold.release();
                }
                return cancelable;
            }
            return false;
        }
    }
}
