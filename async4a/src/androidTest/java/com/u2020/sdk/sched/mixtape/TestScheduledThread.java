package com.u2020.sdk.sched.mixtape;

import android.os.Handler;
import android.os.Message;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.u2020.sdk.sched.internal.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class TestScheduledThread {
    private static final int n = 9;
    private static final CountDownLatch latch = new CountDownLatch(n);

    @Test
    public void test() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignored) {
                }
            }
        };
        TScheduledThread scheduledThread = new TScheduledThread("test");
        scheduledThread.start();
        for (int i = 0; i < n; i++) {
            scheduledThread.schedule(runnable);
        }
        latch.await();
    }

    static class TScheduledThread extends ScheduledThread {
        private final BlockingQueue<Runnable> cachedQueue = new ArrayBlockingQueue<>(8);
        private final int FLUSH = 0b01;
        private final int FIXED = 0b10;
        private Handler handler;

        public TScheduledThread(String name) {
            super(name);
        }

        protected void onLooperPrepared(Handler handler) {
            this.handler = handler;
            handler.sendEmptyMessage(FLUSH);
            Logger.d("send FLUSH");
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case FIXED:
                    Runnable runnable = (Runnable) msg.obj;
                    runnable.run();
                    Logger.d("runnable run");
                    latch.countDown();
                    break;
                case FLUSH:
                    for (; ; ) {
                        Runnable qr = cachedQueue.poll();
                        if (qr == null) break;
                        sendToTarget(qr, handler);
                    }
                    break;
            }
            return false;
        }

        public void schedule(Runnable runnable) {
            if (runnable == null)
                throw new NullPointerException();
            if (handler != null) {
                Logger.d("send schedule");
                sendToTarget(runnable, handler);
            } else try {
                Logger.d("queue try add runnable");
                cachedQueue.add(runnable);
            } catch (IllegalStateException ignored) {
                //noinspection StatementWithEmptyBody
                do {
                } while (handler == null);
                sendToTarget(runnable, handler);
                Logger.d("illegal exe");
            }
        }

        private void sendToTarget(Runnable runnable, Handler handler) {
            Message message = Message.obtain(handler);
            message.obj = runnable;
            message.what = FIXED;
            message.sendToTarget();
        }
    }

}
