package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.bridge.ConsumableFunction;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.atomic.AtomicReference;

public class BrokenConsumer implements ConsumableFunction<Void, AtomicReference<Transporter>> {

    @Override
    public Void apply(AtomicReference<Transporter> var) throws Exception {
        throw new RuntimeException();
    }
}
