package com.u2020.sdk.sched;

import com.u2020.sdk.sched.bridge.ConsumableFunction;
import com.u2020.sdk.sched.bridge.Consumer;
import com.u2020.sdk.sched.bridge.Supplier;
import com.u2020.sdk.sched.bridge.Transporter;

import java.util.concurrent.atomic.AtomicReference;

public final class RequestExecutors {
    public static Request<Void> pingable(Runnable runnable) {
        return RequestLayout.of(new DelegatePropagator<>(runnable));
    }

    public static <T> Request<T> pingable(Runnable runnable, T value) {
        return RequestLayout.of(new DelegatePropagator<>(runnable, value), value);
    }

    public static <T> Request<T> pingable(Supplier<T> supplier) {
        return RequestLayout.of(new DelegatePropagator<>(supplier));
    }

    public static <T> Request<T> pingable(ConsumableFunction<T, AtomicReference<Transporter>> function) {
        return RequestLayout.of(new DelegatePropagator<>(function));
    }

    public static Request<Void> pingable(Consumer<AtomicReference<Transporter>> consumer) {
        return RequestLayout.of(new DelegatePropagator<>(consumer));
    }

    public static <T> Request<T> pingable(Consumer<AtomicReference<Transporter>> consumer, T value) {
        return RequestLayout.of(new DelegatePropagator<>(consumer, value), value);
    }

    private static class DelegatePropagator<T> implements Runnable, Supplier<T>, Consumer<AtomicReference<Transporter>>,
            ConsumableFunction<T, AtomicReference<Transporter>> {
        private Runnable runnable;
        private Supplier<T> supplier;
        private Consumer<AtomicReference<Transporter>> consumer;
        private ConsumableFunction<T, AtomicReference<Transporter>> consumable;
        private T value;

        public DelegatePropagator(Runnable runnable) {
            this.runnable = runnable;
        }

        public DelegatePropagator(Runnable runnable, T value) {
            this.runnable = runnable;
            this.value = value;
        }

        public DelegatePropagator(Consumer<AtomicReference<Transporter>> consumer) {
            this.consumer = consumer;
        }

        public DelegatePropagator(Consumer<AtomicReference<Transporter>> consumer, T value) {
            this.consumer = consumer;
            this.value = value;
        }

        public DelegatePropagator(ConsumableFunction<T, AtomicReference<Transporter>> consumable) {
            this.consumable = consumable;
        }

        public DelegatePropagator(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T apply(AtomicReference<Transporter> var) throws Exception {
            if (consumable != null) {
                return consumable.apply(var);
            } else if (consumer != null) {
                accept(var);
            } else if (runnable != null) {
                run();
            } else if (supplier != null) {
                value = get();
            }
            return value;
        }

        @Override
        public void accept(AtomicReference<Transporter> var) throws Exception {
            consumer.accept(var);
        }

        @Override
        public void run() {
            runnable.run();
        }

        @Override
        public T get() {
            return supplier.get();
        }
    }
}
