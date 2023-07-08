package com.u2020.sdk.sched.mixtape;

import java.util.concurrent.Executor;

interface SequenceExecutorService extends ExecutorService {
    void setExecutor(Executor executor);

    void execute(PriorityRunnable... commands);
}
