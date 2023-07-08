package com.u2020.sdk.sched.internal.platform;

public class JdkVersion {
    private static final String javaVersion = System.getProperty("java.version");
    public static String getJavaVersion() {
        return javaVersion;
    }
}
