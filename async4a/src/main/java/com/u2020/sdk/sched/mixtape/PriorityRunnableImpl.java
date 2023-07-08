package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.atomic.AtomicReference;

public class PriorityRunnableImpl extends PriorityRunnable {
    private final Request<?> request;
    private final AtomicReference<Transporter> transporter;

    public PriorityRunnableImpl(Request<?> request, AtomicReference<Transporter> transporter) {
        super(request.getPriority());
        this.request = request;
        this.transporter = transporter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {//noinspection rawtypes
            ((BaseRequest) request).runWith(transporter);
        } catch (Throwable e) {//TODO
        }
    }
}
