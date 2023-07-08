package com.u2020.sdk.sched.bridge;


import java.util.HashMap;
import java.util.Map;

public class Transporter {
    private final Map<String, Object> transport = new HashMap<>();

    public void putString(String key, String value) {
        transport.put(key, value);
    }

    public String getString(String key) {
        final Object o = transport.get(key);
        try {
            return (String) o;
        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
