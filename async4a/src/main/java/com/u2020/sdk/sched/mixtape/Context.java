package com.u2020.sdk.sched.mixtape;

interface Context {
    boolean isStarted();

    void stop();

    void clear();
}
