package com.u2020.sdk.sched.internal;

import android.util.Log;

import com.u2020.sdk.sched.internal.platform.Platform;

public class Logger {
    private static final String TAG = "async4a";
    public static boolean LOGGABLE = true;
    private static boolean isAndroid = Platform.isAndroid();
    public static void i(String log) {
        Log.i(TAG, String.valueOf(log));
    }

    public static void d(String tag, String log) {
        if (LOGGABLE) {
            if(isAndroid) {
                Log.d(tag, String.valueOf(log));
            } else {
                System.out.println(log);
            }
        }
    }

    public static void d(String log) {
        if (LOGGABLE) {
            if(isAndroid) {
                Log.d(TAG, String.valueOf(log));
            } else {
                System.out.println(log);
            }
        }
    }

    public static void e(String log) {
        if (LOGGABLE) {
            if(isAndroid) {
                Log.e(TAG, String.valueOf(log));
            } else {
                System.err.println(log);
            }
        }
    }

    public static void w(String log, Throwable throwable) {
        Log.w(TAG, String.valueOf(log), throwable);
    }

    public static void e(String tag, String log) {
        Log.e(tag, String.valueOf(log));
    }

    public static void e(String log, Throwable throwable) {
        Log.e(TAG, String.valueOf(log), throwable);
    }

    public static void e(Throwable throwable) {
        Log.e(TAG, "Wrong Cat");
        throwable.printStackTrace();
    }

}
