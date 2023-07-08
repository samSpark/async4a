package com.u2020.sdk.sched.internal;

public class Error {
    private final Throwable throwable;

    public Error(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void printStackTrace() {
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public String getMessage() {
        if (throwable != null) {
            return throwable.getMessage();
        }
        return null;
    }
}
