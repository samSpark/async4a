package com.u2020.sdk.schedule;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestHsm {
    @Test
    public void testHsm() {
        Hsm hi = Hsm.makeHsm();
        hi.sendMessage(hi.obtainMessage());
    }
}
