package com.u2020.sdk.sched.finitestate;


import com.u2020.sdk.sched.bridge.Transporter;
import com.u2020.sdk.sched.internal.Logger;

import java.util.HashMap;

public class StateMachine {
    private final HaltingState haltingState = new HaltingState();
    private final QuittingState quittingState = new QuittingState();
    private final HashMap<State, StateInfo> stateInfo = new HashMap<>();
    private final String name;
    private State initialState;
    private State destState;
    private Transporter transporter;
    private StateInfo[] stateStack;
    private StateInfo[] tempStateStack;
    private int tempStateStackCount;
    private int stateStackTopIndex = -1;
    private boolean dbg = false;
    private boolean hasQuit = false;

    protected StateMachine(String name) {
        this.name = name;
        initStateMachine();
    }

    private void initStateMachine() {
        addState(haltingState, null);
        addState(quittingState, null);
    }


    protected void addState(State state, State parent) {
        addStateAndThen(state, parent);
    }

    protected final void addState(State state) {
        addStateAndThen(state, null);
    }

    private final StateInfo addStateAndThen(State state, State parent) {
        if (dbg) {
            log("addStateInternal: E state=" + state.getName() + ",parent="
                    + ((parent == null) ? "" : parent.getName()));
        }
        StateInfo parentStateInfo = null;
        if (parent != null) {
            parentStateInfo = stateInfo.get(parent);
            if (parentStateInfo == null) {
                parentStateInfo = addStateAndThen(parent, null);
            }
        }
        StateInfo stateInfo = this.stateInfo.get(state);
        if (stateInfo == null) {
            stateInfo = new StateInfo();
            this.stateInfo.put(state, stateInfo);
        }

        if ((stateInfo.parentStateInfo != null)
                && (stateInfo.parentStateInfo != parentStateInfo)) {
            throw new RuntimeException("state already added");
        }
        stateInfo.state = state;
        stateInfo.parentStateInfo = parentStateInfo;
        stateInfo.active = false;
        if (dbg) log("addStateInternal: X stateInfo: " + stateInfo);
        return stateInfo;
    }

    protected Transporter getCurrentTransporter() {
        return transporter;
    }

    protected synchronized String getCurrentState() {
        if(isQuit()) return quittingState.getName();
        if (stateStackTopIndex <= 0) return stateStack[0].state.getName();
        return stateStack[stateStackTopIndex].state.getName();
    }

    protected void haltedProcessMessage(Transporter var) {
    }

    protected void onHalting() {
    }

    protected void onQuitting() {
    }

    protected void unhandledMessage(Transporter msg) {
    }

    public void start() {
        completeConstruction();
    }

    private void completeConstruction() {
        if (dbg) log("completeConstruction: E");
        int maxDepth = 0;//the maximum depth of the state machine
        for (StateInfo si : stateInfo.values()) {
            int depth = 0;
            for (StateInfo i = si; i != null; depth++) {
                i = i.parentStateInfo;
            }
            if (maxDepth < depth) {
                maxDepth = depth;
            }
        }
        if (dbg) log("completeConstruction: maxDepth=" + maxDepth);
        stateStack = new StateInfo[maxDepth];
        tempStateStack = new StateInfo[maxDepth];
        setupInitialStateStack();

        invokeEnterMethods(0);
        if (dbg) log("completeConstruction: X");
    }

    private void setupInitialStateStack() {
        if (dbg) {
            log("setupInitialStateStack: E mInitialState=" + initialState.getName());
        }

        StateInfo curStateInfo = stateInfo.get(initialState);
        for (tempStateStackCount = 0; curStateInfo != null; tempStateStackCount++) {
            tempStateStack[tempStateStackCount] = curStateInfo;
            curStateInfo = curStateInfo.parentStateInfo;
        }
        //Empty the StateStack
        stateStackTopIndex = -1;
        moveTempStateStackToStateStack();
    }

    private void performTransitions(State destState) {
        if (destState != null) {
            while (true) {
                if (dbg) log("handleMessage: new destination call exit/enter");

                StateInfo commonStateInfo = setupTempStateStackWithStatesToEnter(destState);
                invokeExitMethods(commonStateInfo);
                int stateStackEnteringIndex = moveTempStateStackToStateStack();
                invokeEnterMethods(stateStackEnteringIndex);
                if (stateStackTopIndex == -1) break;
                destState = processMsg(transporter);
                if (this.destState != null && destState != null && destState != this.destState) {//if this.destState is changed
                    destState = this.destState;
                } else {
                    break;
                }
            }
            this.destState = null;
        }

        if (destState != null) {
            if (destState == quittingState) {
                hasQuit = true;
                onQuitting();
                cleanupAfterQuitting();
            } else if (destState == haltingState) {
                onHalting();
            }
        }
    }

    protected final synchronized void transitionTo(State destState, Transporter msg) {
        if (isQuit()) {
            if(dbg) log(name + " is quit");
            return;
        }
        this.destState = destState;
        this.transporter = msg;
        if (dbg) log("transitionTo: destState=" + destState.getName());
        performTransitions(destState);
    }

    protected final void transitionToHaltingState(Transporter msg) {
        transitionTo(haltingState, msg);
    }

    protected final boolean isQuit() {
        return this.destState == quittingState || hasQuit;
    }

    private State processMsg(Transporter msg) {
        StateInfo curStateInfo = stateStack[stateStackTopIndex];
        if (dbg) {
            log("processMsg: " + curStateInfo.state.getName());
        }

        if (!isQuit()) {
            while (!curStateInfo.state.apply(msg)) {
                curStateInfo = curStateInfo.parentStateInfo;
                if (curStateInfo == null) {
                    unhandledMessage(msg);
                    break;
                }
                if (dbg) {
                    log("processMsg: " + curStateInfo.state.getName());
                }
            }
        }
        return (curStateInfo != null) ? curStateInfo.state : null;
    }

    private StateInfo setupTempStateStackWithStatesToEnter(State destState) {
        tempStateStackCount = 0;
        StateInfo curStateInfo = stateInfo.get(destState);
        while ((curStateInfo != null) && !curStateInfo.active) {
            tempStateStack[tempStateStackCount++] = curStateInfo;
            curStateInfo = curStateInfo.parentStateInfo;
        }

        if (dbg) {
            log("setupTempStateStackWithStatesToEnter: X mTempStateStackCount="
                    + tempStateStackCount + ",curStateInfo: " + curStateInfo);
        }
        return curStateInfo;
    }

    private void invokeExitMethods(StateInfo commonStateInfo) {
        while ((stateStackTopIndex >= 0)
                && (stateStack[stateStackTopIndex] != commonStateInfo)) {
            State curState = stateStack[stateStackTopIndex].state;
            if (dbg) log("invokeExitMethods: " + curState.getName());
            curState.exit();
            stateStack[stateStackTopIndex].active = false;
            stateStackTopIndex -= 1;
        }
    }

    private int moveTempStateStackToStateStack() {
        int startingIndex = stateStackTopIndex + 1;
        int i = tempStateStackCount - 1;
        int j = startingIndex;
        while (i >= 0) {
            if (dbg) log("moveTempStackToStateStack: i=" + i + ",j=" + j);
            stateStack[j] = tempStateStack[i];
            j += 1;
            i -= 1;
        }

        stateStackTopIndex = j - 1;
        if (dbg) {
            log("moveTempStackToStateStack: X mStateStackTop=" + stateStackTopIndex
                    + ",startingIndex=" + startingIndex + ",Top="
                    + stateStack[stateStackTopIndex].state.getName());
        }
        return startingIndex;
    }

    private void invokeEnterMethods(int stateStackEnteringIndex) {
        for (int i = stateStackEnteringIndex; i <= stateStackTopIndex; i++) {
            if (dbg) log("invokeEnterMethods: " + stateStack[i].state.getName());
            stateStack[i].state.enter();
            stateStack[i].active = true;
        }
    }

    protected final void quit() {
        transitionTo(quittingState, null);
    }

    private void cleanupAfterQuitting() {
        transporter = null;
        stateStack = null;
        tempStateStack = null;
        stateInfo.clear();
        initialState = null;
        destState = null;
    }

    protected void log(String s) {
        Logger.d(name, s);
    }


    protected final void setInitialState(State initialState) {
        if (dbg) log("setInitialState: initialState=" + initialState.getName());
        this.initialState = initialState;
    }

    public void setDbg(boolean dbg) {
        this.dbg = dbg;
    }

    private static class QuittingState extends State {
        @Override
        public Boolean apply(Transporter var) {
            return NOT_HANDLED;
        }
    }

    private static class StateInfo {
        State state;
        StateInfo parentStateInfo;
        boolean active;

        @Override
        public String toString() {
            return "state=" + state.getName() + ",active=" + active + ",parent="
                    + ((parentStateInfo == null) ? "null" : parentStateInfo.state.getName());
        }
    }

    private class HaltingState extends State {
        @Override
        public Boolean apply(Transporter var) {
            haltedProcessMessage(var);
            return true;
        }
    }
}
