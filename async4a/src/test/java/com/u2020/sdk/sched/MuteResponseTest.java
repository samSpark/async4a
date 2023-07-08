package com.u2020.sdk.sched;

import org.junit.Test;

public class MuteResponseTest {

    @Test
    public void thenRun() {
        MuteResponse response = new MuteResponse();
        response.allOfComplete(null);
        response.oneOfComplete(null);
        response.thenRun(null);
        response.orThrowable(null);
    }
}