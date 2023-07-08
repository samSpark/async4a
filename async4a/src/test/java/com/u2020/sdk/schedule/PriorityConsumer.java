package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.bridge.ConsumableFunction;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class PriorityConsumer implements ConsumableFunction<String, AtomicReference<Transporter>> {
    protected static CountDownLatch latch;
    private final int priority;
    private String result;

    public PriorityConsumer(int priority) {
        this.priority = priority;
    }

    public PriorityConsumer(int priority, CountDownLatch latch) {
        this.priority = priority;
        PriorityConsumer.latch = latch;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (latch == null) {
            latch = new CountDownLatch(1);
        }
        return latch.await(timeout, unit);
    }

    @Override
    public String apply(AtomicReference<Transporter> var) throws Exception {
        result = String.valueOf(priority);

        Transporter preTransport = var.get();
        String preResult = preTransport.getString("preResult");
        Transporter thisTransport = new Transporter();
        thisTransport.putString("preResult", result);
        var.compareAndSet(preTransport, thisTransport);

        Thread thread = Thread.currentThread();
        String log = "PriorityRequest-" + thread.getName() +
                "-priority-" + priority + "-preResult->" +
                preResult;

        System.out.println(log);
        if (latch != null)
            latch.countDown();
        return result;
    }

}
