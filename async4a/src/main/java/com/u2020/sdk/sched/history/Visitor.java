package com.u2020.sdk.sched.history;


import com.u2020.sdk.sched.Request;

public interface Visitor {
    void addRequestLayout(Request<?>... requests);

    boolean removeRequestLayoutIf(int requestId);

    void erase();

    int preferredLayoutSize();
}
