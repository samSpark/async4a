package com.u2020.sdk.schedule;

import android.os.Message;

import com.u2020.sdk.sched.internal.hms.AsyncState;
import com.u2020.sdk.sched.internal.hms.AsyncStateMachine;


public class Hsm extends AsyncStateMachine {
    public Hsm(String name) {
        super(name);
        setDbg(false);
        addState(mP0, null);
        addState(mP1, mP0);
        addState(mS2, mP1);
        addState(mS3, mS2);
        addState(mS4, mS2);
        addState(mS1, mP1);
        addState(mS5, mS1);
        addState(mS0, mP0);
        //mS5->mS4->mS1->mS3->mS0->HaltingState
        setInitialState(mS5);
    }

    @Override
    protected void onHalting() {
        log("Hsm halting");
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    protected void onQuitting() {
        log("Hsm onQuitting");
    }

    public static Hsm makeHsm() {
        Hsm hw = new Hsm("F-SM");
        hw.start();
        return hw;
    }

    public

    class mP0 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mP0 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mP0 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mP0 processMessage");
            return HANDLED;
        }
    }

    mP0 mP0 = new mP0();

    class mP1 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mP1 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mP1 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mP1 processMessage");
            return HANDLED;
        }
    }

    mP1 mP1 = new mP1();

    class mS0 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mS0 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mS0 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mS0 processMessage");
            quit();
            return HANDLED;
        }
    }

    mS0 mS0 = new mS0();

    class mS2 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mS2 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mS2 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mS2 processMessage");
            return HANDLED;
        }
    }

    mS2 mS2 = new mS2();

    class mS1 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mS1 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mS1 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mS1 processMessage");
            transitionTo(mS3);
            deferMessage(message);
            return HANDLED;
        }
    }

    mS1 mS1 = new mS1();

    class mS3 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mS3 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mS3 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mS3 processMessage");
            transitionTo(mS0);
            deferMessage(message);
            return HANDLED;
        }
    }

    mS3 mS3 = new mS3();

    class mS4 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mS4 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mS4 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mS4 processMessage");
            transitionTo(mS1);
            deferMessage(message);
            return HANDLED;
        }
    }

    mS4 mS4 = new mS4();

    class mS5 extends AsyncState {
        @Override
        public void enter() {
            super.enter();
            log("mS5 enter");
        }

        @Override
        public void exit() {
            super.exit();
            log("mS5 exit");
        }

        @Override
        public boolean processMessage(Message message) {
            log("mS5 processMessage");
            transitionTo(mS4);
            deferMessage(message);
            //transitionToHaltingState();
            //log("mS4 Halted");
            return HANDLED;
        }
    }

    mS5 mS5 = new mS5();
}


