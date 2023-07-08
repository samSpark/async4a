package com.u2020.sdk.sched.bridge;

public interface Consumer<T> {
    void accept(T t) throws Exception;
}
