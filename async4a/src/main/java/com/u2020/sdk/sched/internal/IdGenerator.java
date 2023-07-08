package com.u2020.sdk.sched.internal;

public class IdGenerator {
    private static final SnowflakeLocalId snowflakeLocalId = new SnowflakeLocalId();
    public static int nextId() {
        return snowflakeLocalId.nextId();
    }
}
