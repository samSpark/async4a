package com.u2020.sdk.sched.finitestate;

import com.u2020.sdk.sched.bridge.Transporter;

public class State implements Transition<Boolean, Transporter> {
    protected State() {
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    @Override
    public Boolean apply(Transporter var) {
        return false;
    }

    public String getName() {
        String name = getClass().getName();
        int lastDollar = name.indexOf('$');
        return name.substring(lastDollar + 1);
    }
}
