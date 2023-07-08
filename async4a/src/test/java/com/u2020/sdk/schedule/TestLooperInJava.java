package com.u2020.sdk.schedule;


import com.u2020.sdk.sched.LoopBuilder;
import com.u2020.sdk.sched.LoopService;
import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.RequestExecutors;
import com.u2020.sdk.sched.RequestInfo;
import com.u2020.sdk.sched.Scheduler;
import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.bridge.ConsumableFunction;
import com.u2020.sdk.sched.bridge.Consumer;
import com.u2020.sdk.sched.bridge.Supplier;
import com.u2020.sdk.sched.bridge.Transporter;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TestLooperInJava {
    private LoopService looper;

    @Before
    public void setUp() {
        looper = LoopBuilder.create().build();
    }

    @After
    public void tearDown() {
        looper.stop();
    }

    @Test
    public void testPropagatorRequest() throws InterruptedException {
        final String greetingByConsumable = "Say Hi";
        final String greetingByRunnable = "Say Hello";
        final String greetingByConsumer = "How are you";
        Request<String> consumable = RequestExecutors.pingable(new ConsumableFunction<String, AtomicReference<Transporter>>() {
            @Override
            public String apply(AtomicReference<Transporter> var) throws Exception {
                System.out.println("Consumable:");
                return greetingByConsumable;
            }
        })
                .addListener(new BiConsumer<Pair<RequestInfo, String>, Error>() {
                    @Override
                    public void accept(Pair<RequestInfo, String> pair, Error error) {
                        System.out.println(pair.second);
                    }
                });
        Request<String> runnable = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable:");
            }
        }, greetingByRunnable)
                .addListener(new BiConsumer<Pair<RequestInfo, String>, Error>() {
                    @Override
                    public void accept(Pair<RequestInfo, String> pair, Error error) {
                        System.out.println(pair.second);
                    }
                });
        Request<String> consumer = RequestExecutors.pingable(new Consumer<AtomicReference<Transporter>>() {
            @Override
            public void accept(AtomicReference<Transporter> transporterAtomicReference) throws Exception {
                System.out.println("Consumer:");
            }
        }, greetingByConsumer)
                .addListener(new BiConsumer<Pair<RequestInfo, String>, Error>() {
                    @Override
                    public void accept(Pair<RequestInfo, String> pair, Error error) {
                        System.out.println(pair.second);
                    }
                });
        looper.ping(consumable, runnable, consumer);
        TimeUnit.SECONDS.sleep(1);
        assertTrue(consumable.isFinished());
        assertTrue(runnable.isFinished());
        assertTrue(consumer.isFinished());
        assertEquals(greetingByConsumable, consumable.get());
        assertEquals(greetingByRunnable, runnable.get());
        assertEquals(greetingByConsumer, consumer.get());
    }

    @Test
    public void testPingOneRequest() throws InterruptedException {
        Request<String> request = RequestExecutors.pingable(new ConsumableFunction<String, AtomicReference<Transporter>>() {
            @Override
            public String apply(AtomicReference<Transporter> var) throws Exception {
                return "Say Hi";
            }
        })
                .addListener(new BiConsumer<Pair<RequestInfo, String>, Error>() {
                    @Override
                    public void accept(Pair<RequestInfo, String> pair, Error error) {
                        System.out.println(pair.second);
                    }
                });
        looper.ping(request);
        TimeUnit.SECONDS.sleep(1);
        assertTrue(request.isFinished());
        assertEquals("Say Hi", request.get());
    }

    @Test
    public void testPingOneRequestWithTimeout() {
        Request<String> request = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignored) {
                }
                return "Say Hi";
            }
        });
        looper.ping(request);
        try {
            request.get(1L, TimeUnit.SECONDS);
        } catch (Exception e) {
            assertTrue(e instanceof TimeoutException);
            assertNull(request.get());
        }
    }

    @Test
    public void testPingRequestWithTimeoutInEs() throws InterruptedException {
        String result = "Say Hi";
        Request<String> request = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println("get at" + System.currentTimeMillis());
                } catch (InterruptedException ignored) {
                }
                return result;
            }
        });
        looper.ping(request);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    assertEquals(result, request.get(3L, TimeUnit.SECONDS));
                    System.out.println("get at" + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    assertEquals(result, request.get(4L, TimeUnit.SECONDS));
                    System.out.println("get at" + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void testPingSerialRequests() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1000);
        List<Request<?>> requests = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {//(int i = 1; i <= 1000; i++); (int i = 1000; i > 0; i--)
            Request<Void> request = RequestExecutors.pingable(latch::countDown)
                    .setPriority(i);
            request.addListener(new BiConsumer<Pair<RequestInfo, Void>, Error>() {
                @Override
                public void accept(Pair<RequestInfo, Void> requestInfoVoidPair, Error error) {
                    System.out.println(request.getPriority());
                }
            });
            requests.add(request);
        }
        looper.ping(requests);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testForkWhenStop() {
        looper.stop();
        boolean fork = looper.fork();
        assertFalse(fork);
    }

    @Test
    public void testFork() throws Exception {
        boolean fork = looper.fork();
        assertTrue(fork);
        testPropagatorRequest();
        Scheduler scheduler = new Scheduler();//empty scheduler->highest priority/highest weight
        Request<String> request = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                return "Good";
            }
        });
        looper.ping(request, scheduler);
        assertEquals("Good", request.get(1L, TimeUnit.SECONDS));
    }

    @Test
    public void testCapacity() throws InterruptedException {
        looper = LoopBuilder.create().setCapacity(3).build();
        Request<?> cr;
        looper.ping(RequestExecutors.pingable(runnable), cr = RequestExecutors.pingable(runnable));
        cr.cancel(true);
        looper.ping(RequestExecutors.pingable(runnable), RequestExecutors.pingable(runnable));
        looper.ping(RequestExecutors.pingable(runnable));
        TimeUnit.SECONDS.sleep(1);
    }

    private final static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ignored) {
            }
            System.out.println("Request runs at " + System.currentTimeMillis());
        }
    };
}
