package com.u2020.sdk.sched.finitestate;

import com.u2020.sdk.sched.bridge.ConsumableFunction;

interface Transition<T, U> extends ConsumableFunction<T, U> {
    boolean HANDLED = true;
    boolean NOT_HANDLED = false;

    void enter();

    void exit();
}
