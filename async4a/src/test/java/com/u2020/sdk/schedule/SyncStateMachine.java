package com.u2020.sdk.schedule;

import com.u2020.sdk.sched.bridge.Transporter;
import com.u2020.sdk.sched.finitestate.State;
import com.u2020.sdk.sched.finitestate.StateMachine;

public class SyncStateMachine extends StateMachine {
    protected SyncStateMachine(String name) {
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
        setInitialState(mS5);
    }  /***{@link SyncStateMachine#andThen())**/

    @Override
    protected void haltedProcessMessage(Transporter var) {
        log("haltedProcessMessage");
    }

    @Override
    protected void onHalting() {
        log("halting");
    }

    @Override
    protected void onQuitting() {
        log("onQuitting");
        transitionTo(mP1, null);//try transition but sm is quit
    }

    public static SyncStateMachine makeHsm() {
        SyncStateMachine hi = new SyncStateMachine("F-SM");
        hi.start();
        return hi;
    }

    //transaction: event -> orgState-> transitions -> destState -> event
    //mS5->mS4->mS1->mS3->mS0->HaltingState
    public void andThen() {
        Transporter msg = new Transporter();
        msg.putString("msg", "a short msg");
        transitionTo(mS5, msg);
    }

    class mP0 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mP0 apply");
            return HANDLED;
        }
    }

    mP0 mP0 = new mP0();

    class mP1 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mP1 apply");
            return HANDLED;
        }
    }

    mP1 mP1 = new mP1();

    class mS0 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mS0 apply");
            return HANDLED;
        }
    }

    mS0 mS0 = new mS0();

    class mS2 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mS2 apply");
            return HANDLED;
        }
    }

    mS2 mS2 = new mS2();

    class mS1 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mS1 apply");
            transitionTo(mS3, msg);
            System.out.println("mS1-after-transition");
            return HANDLED;
        }
    }

    mS1 mS1 = new mS1();

    class mS3 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mS3 apply");
            transitionTo(mS0, msg);
            System.out.println("mS3-after-transition");
            return HANDLED;
        }
    }

    mS3 mS3 = new mS3();

    class mS4 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mS4 apply");
            //transitionToHaltingState(msg);//to onHalting
            //log("mS4 Halted");
            transitionTo(mS1, msg);
            System.out.println("mS4-after-transition");
            return HANDLED;
        }
    }

    mS4 mS4 = new mS4();

    class mS5 extends State {
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
        public Boolean apply(Transporter msg) {
            log("mS5 apply");
            transitionTo(mS4, msg);
            System.out.println("mS5-after-transition");
            quit();
            return HANDLED;
        }
    }

    mS5 mS5 = new mS5();
}
