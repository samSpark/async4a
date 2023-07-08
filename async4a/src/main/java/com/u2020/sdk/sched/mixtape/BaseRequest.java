package com.u2020.sdk.sched.mixtape;

import com.u2020.sdk.sched.Request;
import com.u2020.sdk.sched.finitestate.State;
import com.u2020.sdk.sched.finitestate.StateMachine;
import com.u2020.sdk.sched.internal.IdGenerator;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseRequest<T, U> implements Request<T> {
    protected final AtomicReference<String> atomicState;
    protected final RequestHsm stateMachine;
    private final int id = IdGenerator.nextId();
    private String name;
    private int priority;

    public BaseRequest() {
        atomicState = new AtomicReference<>(RenderState.NEW);
        stateMachine = new RequestHsm(id);
        stateMachine.start();
        stateMachine.thenApply(RenderState.NEW);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Request<T> setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Request<T> setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String getState() {
        return atomicState.get();
    }

    public abstract void runWith(U var) throws Exception;

    protected static final class RequestHsm extends StateMachine {
        protected final State NEW = new NEW();
        protected final State RUNNABLE = new RUNNABLE();
        protected final State PENDING = new PENDING();
        protected final State TERMINATED = new TERMINATED();
        protected final State CANCELED = new CANCELED();
        protected final State QUIT = new QUIT();

        private RequestHsm(int id) {
            super("RequestHsm_" + id);
            addState(NEW, null);
            addState(RUNNABLE, NEW);
            addState(CANCELED, NEW);
            addState(PENDING, RUNNABLE);
            addState(TERMINATED, PENDING);
            addState(QUIT, TERMINATED);
            setInitialState(NEW);
        }

        public void thenApply(String state) {
            switch (state) {
                case RenderState.NEW:
                    transitionTo(NEW, null);
                    break;
                case RenderState.RUNNABLE:
                    transitionTo(RUNNABLE, null);
                    break;
                case RenderState.PENDING:
                    transitionTo(PENDING, null);
                    break;
                case RenderState.TERMINATED:
                    transitionTo(TERMINATED, null);
                    break;
                case RenderState.CANCELED:
                    transitionTo(CANCELED, null);
                    break;
                case RenderState.QUIT:
                    quit();
                    break;
            }
        }

        public String getCurrentStateName() {
            String state = getCurrentState();
            if ("QuittingState".equals(state)) {
                state = RenderState.QUIT;
            }
            return state;
        }

        private static final class NEW extends State {
            @Override
            public String getName() {
                return RenderState.NEW;
            }
        }

        private static final class RUNNABLE extends State {
            @Override
            public String getName() {
                return RenderState.RUNNABLE;
            }
        }

        private static final class PENDING extends State {
            @Override
            public String getName() {
                return RenderState.PENDING;
            }
        }

        private static final class TERMINATED extends State {
            @Override
            public String getName() {
                return RenderState.TERMINATED;
            }
        }

        private static final class CANCELED extends State {
            @Override
            public String getName() {
                return RenderState.CANCELED;
            }
        }

        private static final class QUIT extends State {
            @Override
            public String getName() {
                return RenderState.QUIT;
            }
        }
    }
}
