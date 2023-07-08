package com.u2020.sdk.sched.history;

import com.u2020.sdk.sched.LoopBuilder;
import com.u2020.sdk.sched.LoopService;
import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ViewPortLayoutTest {
    private final static Runnable runnable = () -> {
        System.out.println("Visitor runs");
    };
    private final Visitor visitor = new ViewPortLayout(null);
    private final LoopService service = LoopBuilder.create().build();
    Request<Void> request1 = RequestExecutors.pingable(runnable);
    Request<Void> request2 = RequestExecutors.pingable(new Runnable() {
        @Override
        public void run() {
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException ignored) {
            }
        }
    });
    Request<Void> request3 = RequestExecutors.pingable(runnable);
    Request<Void> request4 = RequestExecutors.pingable(runnable);

    @Test
    public void addRequestLayout() {
        visitor.addRequestLayout(request1);
        visitor.addRequestLayout(request2);
        visitor.addRequestLayout(request3);
        visitor.addRequestLayout(request3);
        assertEquals(3, visitor.preferredLayoutSize());
    }

    @Test
    public void removeRequestLayout() throws Exception {
        visitor.addRequestLayout(request1);
        visitor.addRequestLayout(request2);
        visitor.addRequestLayout(request3);

        visitor.removeRequestLayoutIf(new Random().nextInt());
        visitor.removeRequestLayoutIf(request1.getId());
        assertEquals(2, visitor.preferredLayoutSize());
        assertEquals(Request.RenderState.CANCELED, request1.getState());

        visitor.removeRequestLayoutIf(request3.getId());
        assertEquals(1, visitor.preferredLayoutSize());
        assertEquals(Request.RenderState.CANCELED, request3.getState());

        service.ping(request3);
        assertFalse(service.removeIf(request3.getId()));
        assertTrue(request3.isCanceled());
        assertFalse(request3.isFinished());
        service.ping(request2);
        service.removeIf(request2.getId());
        service.ping(request4);
        //noinspection ConstantConditions
        assertNull(request4.get(1L, TimeUnit.SECONDS));
    }

    @Test
    public void erase() {
        visitor.addRequestLayout(request1);
        visitor.addRequestLayout(request2);
        visitor.addRequestLayout(request3);
        visitor.erase();
        assertTrue(request1.isCanceled());
        assertTrue(request2.isCanceled());
        assertTrue(request3.isCanceled());
    }
}