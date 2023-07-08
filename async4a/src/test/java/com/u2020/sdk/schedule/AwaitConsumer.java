package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.bridge.ConsumableFunction;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class AwaitConsumer implements ConsumableFunction<String, AtomicReference<Transporter>> {

    @Override
    public String apply(AtomicReference<Transporter> var) throws Exception {
        new CountDownLatch(1).await();
        return "AwaitConsumer";
    }
}
