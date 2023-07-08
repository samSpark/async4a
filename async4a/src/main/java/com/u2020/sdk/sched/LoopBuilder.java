package com.u2020.sdk.sched;

import java.util.concurrent.TimeUnit;

public class LoopBuilder {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 3 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    public static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT, 4) - 1);
    public static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2;

    private int capacity = Integer.MAX_VALUE;
    private int corePoolSize = CORE_POOL_SIZE;
    private int maximumPoolSize = MAXIMUM_POOL_SIZE;
    private long keepAliveTime = 60L;
    private TimeUnit unit = TimeUnit.SECONDS;
    private boolean allowAliveTimeOut = true;

    public static LoopBuilder create() {
        return new LoopBuilder()
        .setCorePoolSize(CORE_POOL_SIZE)
        .setMaximumPoolSize(MAXIMUM_POOL_SIZE)
        .keepAliveTime(60L)
        .setUnit(TimeUnit.SECONDS)
        .allowAliveTimeOut(true);
    }

    public LoopBuilder setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public LoopBuilder setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public LoopBuilder setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public LoopBuilder allowAliveTimeOut(boolean allowAliveTimeOut) {
        this.allowAliveTimeOut = allowAliveTimeOut;
        return this;
    }

    public LoopBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public LoopBuilder setUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    public LoopService build() {
        return new LoopSupport(capacity, corePoolSize, maximumPoolSize, keepAliveTime, unit, allowAliveTimeOut);
    }
}
