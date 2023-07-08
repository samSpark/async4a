package com.u2020.sdk.sched.mixtape;

public class PriorityRequest extends PriorityRunnable {

    public PriorityRequest() {
        super(Integer.MAX_VALUE);
    }

    public PriorityRequest(int priority) {
        super(priority);
    }

    @Override
    public void run() {

    }
}
