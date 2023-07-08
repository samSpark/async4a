package com.u2020.sdk.sched.bridge;

public interface ConsumableFunction<R, U> {
    R apply(U var) throws Exception;
}