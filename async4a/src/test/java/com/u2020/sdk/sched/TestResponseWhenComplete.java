package com.u2020.sdk.sched;

import com.u2020.sdk.sched.bridge.BiCompletionConsumer;
import com.u2020.sdk.sched.bridge.BiConsumer;
import com.u2020.sdk.sched.bridge.CompletionConsumer;
import com.u2020.sdk.sched.bridge.Supplier;
import com.u2020.sdk.sched.internal.Error;
import com.u2020.sdk.sched.internal.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class TestResponseWhenComplete {
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
    public void allOfBiComplete() throws InterruptedException {
        Request<Void> r1 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
            }
        });
        Request<Void> r2 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                String greeting = "Say Hello";
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println(greeting + " at " + System.currentTimeMillis());
            }
        });
        Response response = looper.ping(r1, r2);
        response.allOfComplete(new BiCompletionConsumer<Pair<RequestInfo, ? super Object>[], Object>() {
            @Override
            public Object accept(Pair<RequestInfo, ? super Object>[] pairs) {
                return "allOfComplete when " + System.currentTimeMillis();
            }

            @Override
            public void andThen(Pair<RequestInfo, ? super Object>[] pairs, Object o) {
                super.andThen(pairs, o);
                System.out.println(o);
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void allOfComplete() throws InterruptedException {
        Request<Void> r1 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
            }
        });
        Request<Void> r2 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                String greeting = "Say Hello";
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(greeting + " at " + System.currentTimeMillis());
            }
        });
        Response response = looper.ping(r1, r2);
        response.allOfComplete(new CompletionConsumer<Pair<RequestInfo, ? super Object>[], Object>() {
            @Override
            public Void accept(Pair<RequestInfo, ? super Object>[] pairs) {
                System.out.println("allOfComplete at " + System.currentTimeMillis());
                return null;
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void allOfCompleteWithException() throws InterruptedException {
        Request<Void> r1 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
            }
        });
        Request<Void> r2 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Wrong Cat");
            }
        });
        Response response = looper.ping(r1, r2);
        response.allOfComplete(new CompletionConsumer<Pair<RequestInfo, ? super Object>[], Object>() {
            @Override
            public Object accept(Pair<RequestInfo, ? super Object>[] pairs) {
                System.out.println("allOfComplete when " + System.currentTimeMillis());
                return null;
            }
        }).orThrowable(new BiConsumer<RequestInfo, Error>() {
            @Override
            public void accept(RequestInfo requestInfo, Error error) {
                System.err.println(error.getMessage());
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void oneOfComplete() throws InterruptedException {
        Request<String> r1 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return  greeting;
            }
        });
        Request<String> r2 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hello";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return greeting;
            }
        });
        Response response = looper.ping(r1, r2);
        response.oneOfComplete(new CompletionConsumer<Pair<RequestInfo, ? super Object>, Object>() {

            @Override
            public Void accept(Pair<RequestInfo, ? super Object> pair) {
                System.out.println(pair.second + " accept at " + System.currentTimeMillis());
                return null;
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void oneOfBiComplete() throws InterruptedException {
        Request<String> r1 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return  greeting;
            }
        });
        Request<String> r2 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hello";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return greeting;
            }
        });
        Response response = looper.ping(r1, r2);
        response.oneOfComplete(new BiCompletionConsumer<Pair<RequestInfo, ? super Object>, Object>() {

            @Override
            public Object accept(Pair<RequestInfo, ? super Object> pair) {
                return pair.second + " accept at " + System.currentTimeMillis();
            }

            @Override
            public void andThen(Pair<RequestInfo, ? super Object> requestInfoPair, Object o) {
                super.andThen(requestInfoPair, o);
                System.out.println(o);
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void oneOfBiCompleteWithException() throws InterruptedException {
        Request<Void> r1 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Wrong Cat");
            }
        });
        Request<Void> r2 = RequestExecutors.pingable(new Runnable() {
            @Override
            public void run() {
                String greeting = "Say Hello";
                System.out.println(greeting + " at " + System.currentTimeMillis());
            }
        });
        Response response = looper.ping(r1, r2);
        response.oneOfComplete(new BiCompletionConsumer<Pair<RequestInfo, ? super Object>, Object>() {

            @Override
            public Object accept(Pair<RequestInfo, ? super Object> requestInfoPair) {
                return "oneOfComplete when " + System.currentTimeMillis();
            }

            @Override
            public void andThen(Pair<RequestInfo, ? super Object> requestInfoPair, Object o) {
                super.andThen(requestInfoPair, o);
                System.out.println(o);
            }
        }).orThrowable(new BiConsumer<RequestInfo, Error>() {
            @Override
            public void accept(RequestInfo requestInfo, Error error) {
                System.err.println(error.getMessage());
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void anyOfComplete() throws InterruptedException {
        Request<String> r1 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return  greeting;
            }
        });
        Request<String> r2 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hello";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return greeting;
            }
        });
        Response response = looper.ping(r1, r2);
        response.anyOfComplete(new CompletionConsumer<Pair<RequestInfo, ? super Object>, Object>() {

            @Override
            public Object accept(Pair<RequestInfo, ? super Object> pair) {
                System.out.println(pair.second + " accept at " + System.currentTimeMillis());
                return null;
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void anyOfBiComplete() throws InterruptedException {
        Request<String> r1 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String greeting = "Say Hi";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return  greeting;
            }
        });
        Request<String> r2 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hello";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return greeting;
            }
        });
        Response response = looper.ping(r1, r2);
        response.anyOfComplete(new BiCompletionConsumer<Pair<RequestInfo, ? super Object>, Object>() {

            @Override
            public Object accept(Pair<RequestInfo, ? super Object> pair) {
                return pair.second + " accept at " + System.currentTimeMillis();
            }

            @Override
            public void andThen(Pair<RequestInfo, ? super Object> requestInfoPair, Object o) {
                super.andThen(requestInfoPair, o);
                System.out.println(o);
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void anyOfBiCompleteWithException() throws InterruptedException {
        Request<String> r1 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                throw new RuntimeException("Wrong Cat");
            }
        });
        Request<String> r2 = RequestExecutors.pingable(new Supplier<String>() {
            @Override
            public String get() {
                String greeting = "Say Hello";
                System.out.println(greeting + " at " + System.currentTimeMillis());
                return greeting;
            }
        });
        Response response = looper.ping(r1, r2);
        response.anyOfComplete(new BiCompletionConsumer<Pair<RequestInfo, ? super Object>, Object>() {

            @Override
            public Object accept(Pair<RequestInfo, ? super Object> pair) {
                return pair.second + " accept at " + System.currentTimeMillis();
            }

            @Override
            public void andThen(Pair<RequestInfo, ? super Object> requestInfoPair, Object o) {
                super.andThen(requestInfoPair, o);
                System.out.println(o);
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void completeThenRun() throws InterruptedException {
        Request<String> greeter = RequestExecutors.pingable(() -> "Say Hi");
        looper.ping(greeter).thenRun(new Runnable() {
            @Override
            public void run() {
                String greeting = greeter.get();
                System.out.println(greeting);
            }
        });
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void orThrowable() throws InterruptedException {
        Request<Void> throwable = RequestExecutors.pingable((Runnable) () -> {
            throw new RuntimeException("Caught the Wrong Cat");
        });
        looper.ping(throwable).thenRun(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Caught the Wrong Cat again");
            }
        }).orThrowable(new BiConsumer<RequestInfo, Error>() {
            @Override
            public void accept(RequestInfo requestInfo, Error error) {
                System.err.println(error.getMessage());
            }
        });
        TimeUnit.SECONDS.sleep(1);
    }
    @Test
    public void test() {
        LoopService loopService = LoopBuilder.create().build();
        Request<Void> hi = RequestExecutors.pingable(() -> System.out.println("Hi"));
        Request<Void> hello = RequestExecutors.pingable(() -> System.out.println("Hello"));
        Request<Void> how = RequestExecutors.pingable(() -> System.out.println("How are you"));
        Response response = looper.ping(hi, hello, how);
        response.oneOfComplete(pair -> {
            System.out.println("oneOf:" + pair.second);
            return null;
        }).anyOfComplete(pair -> {
            System.out.println("anyOf:" + pair.second);
            return null;
        }).allOfComplete(pairs -> {
            System.out.println("allOf:" +pairs.length);
            return null;
        }).orThrowable((requestInfo, error) -> {error.printStackTrace();});
    }
}