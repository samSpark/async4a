package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.internal.Null;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestNull {
    TestNull testNull = null;

    @Test
    public void testNull() {
        Null obj = new Null();
        assertTrue(Null.isNull(obj));
        assertTrue(Null.isNull(testNull));
    }

    @Test
    public void testNullOrElse() {
        Null defaultValue = new Null();
        Object obj = Null.isNullOrElse(testNull, defaultValue);
        assertEquals(defaultValue, obj);
    }
}
