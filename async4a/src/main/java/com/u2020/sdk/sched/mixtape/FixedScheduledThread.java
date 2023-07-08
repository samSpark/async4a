package com.u2020.sdk.sched.mixtape;

import android.os.Handler;
import android.os.Message;

import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.internal.annotation.Android;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Android
final class FixedScheduledThread extends ScheduledThread {
    private final BlockingQueue<ScheduledRunnable> cachedQueue = new ArrayBlockingQueue<>(8);
    private final int FLUSH = 0b01;
    private final int FIXED = 0b10;
    private Handler handler;

    public FixedScheduledThread() {
        super(FixedScheduledThread.class.getSimpleName());
    }

    @Override
    protected void onLooperPrepared(Handler handler) {
        this.handler = handler;
        handler.sendEmptyMessage(FLUSH);
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case FIXED:
                if (msg.obj == null)
                    break;
                ScheduledRunnable runnable = (ScheduledRunnable) msg.obj;
                Scheduler scheduler = runnable.getScheduler();
                if (scheduler.loop) {
                    Message ball = Message.obtain(msg);
                    handler.sendMessageDelayed(ball, scheduler.timeUnit.toMillis(scheduler.internalTime));
                } else if (scheduler.frequency > 0) {
                    scheduler.frequency = scheduler.frequency - 1;
                    Message ball = Message.obtain(msg);
                    handler.sendMessageDelayed(ball, scheduler.timeUnit.toMillis(scheduler.internalTime));
                }
                runnable.run();//highest priority/highest weight
                msg.obj = null;//gc
                break;
            case FLUSH:
                for (;;) {
                    ScheduledRunnable cs = cachedQueue.poll();
                    if (cs == null) break;
                    sendToTarget(cs, handler);
                }
                break;
        }
        return true;
    }

    public final void schedule(ScheduledRunnable runnable) {
        if (runnable == null)
            throw new NullPointerException();
        if (handler != null) {
            sendToTarget(runnable, handler);
        } else try {
            cachedQueue.add(runnable);
        } catch (IllegalStateException ignored) {
            //noinspection StatementWithEmptyBody
            do {
            } while (handler == null);
            sendToTarget(runnable, handler);
        }
    }

    public final void clear() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void sendToTarget(ScheduledRunnable runnable, Handler handler) {
        Message message = Message.obtain();
        message.obj = runnable;
        message.what = FIXED;
        Scheduler scheduler = runnable.getScheduler();
        if(scheduler.timeUnit != null) {
            handler.sendMessageDelayed(message, scheduler.timeUnit.toMillis(scheduler.delayTime));
        } else handler.sendMessage(message);
    }
}
