package com.u2020.sdk.sched.internal;

import org.junit.Test;

public class SnowflakeLocalIdTest {

    private final SnowflakeLocalId snowflakeLocalId = new SnowflakeLocalId();

    @Test
    public void nextId() {
        System.out.println(snowflakeLocalId.nextId());
    }

    @Test
    public void timeGen() {
        System.out.println(snowflakeLocalId.timeGen());
    }
}