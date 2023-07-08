package com.u2020.sdk.sched.internal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PairTest {

    @Test
    public void create() {
        Pair<String, Integer> pair = Pair.create("Hi", 88);
        assertEquals("Hi", pair.first);
        assertEquals(Integer.valueOf(88), pair.second);
    }
}