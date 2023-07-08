package com.u2020.sdk.schedule;

import org.junit.Test;

public class TestSyncStateMachine {
    @Test
    public void testHsm() {
        SyncStateMachine hi = SyncStateMachine.makeHsm();
        hi.andThen();
    }
}