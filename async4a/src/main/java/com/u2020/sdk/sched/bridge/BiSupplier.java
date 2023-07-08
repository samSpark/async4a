package com.u2020.sdk.sched.bridge;

public interface BiSupplier<T, S, R> {
    R get(T t, S s) throws Exception;
}