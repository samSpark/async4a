package com.u2020.sdk.sched.mixtape;

//    @Override
//    public int compareTo(PriorityRunnable runnable) {
//        int c = runnable.getPriority() - this.getPriority();
//        return Integer.compare(c, 0);
//    }
abstract class PriorityRunnable implements Runnable {
    private final int priority;

    public PriorityRunnable(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

}
