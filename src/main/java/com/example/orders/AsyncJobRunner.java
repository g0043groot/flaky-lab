package com.example.orders;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Simulates a fulfilment/report job that runs on a background thread. */
@Component
public class AsyncJobRunner {
    public enum State { RUNNING, DONE, FAILED }

    public static class Job {
        private final AtomicReference<State> state = new AtomicReference<>(State.RUNNING);
        private volatile CompletableFuture<Void> future;

        public State state() { return state.get(); }
        public void join() { future.join(); }
    }

    private final LongSupplier durationMillis;

    /** Production default: a job takes 80-120 ms. */
    @Autowired
    public AsyncJobRunner() {
        this(() -> ThreadLocalRandom.current().nextLong(80, 121));
    }

    public AsyncJobRunner(LongSupplier durationMillis) {
        this.durationMillis = durationMillis;
    }

    public Job submit(Runnable work) {
        long d = durationMillis.getAsLong();
        Job job = new Job();
        job.future = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(d);
                work.run();
                job.state.set(State.DONE);
            } catch (Exception e) {
                job.state.set(State.FAILED);
            }
        });
        return job;
    }
}
