package com.u2020.sdk.sched;
import com.u2020.sdk.sched.bridge.MechanicOperator;

import java.util.Collection;
import java.util.List;

public interface LoopService {
    Response ping(Request<?>... requests);

    void ping(Collection<Request<?>> requests);

    void ping(Request<?> request, Scheduler scheduler);

    void pong(MechanicOperator listener);

    RequestInfo getRequestInfo(int requestId);

    List<RequestInfo> getRequestInfo();

    boolean removeIf(int requestId);

    void clear();

    void stop();

    boolean isStopped();

    boolean fork();
}
