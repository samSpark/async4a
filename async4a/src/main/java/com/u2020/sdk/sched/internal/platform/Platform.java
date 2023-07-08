package com.u2020.sdk.sched.internal.platform;

public class Platform {
    public static boolean isAndroid() {
        // This explicit check avoids activating in Android Studio with Android specific classes
        // available when running plugins inside the IDE.
        return "Dalvik".equals(System.getProperty("java.vm.name"));
    }
}
