package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class AsyncJobRunnerTest {
    @Test void jobCompletesAndRunsWork() {
        AtomicBoolean ran = new AtomicBoolean();
        AsyncJobRunner.Job job = new AsyncJobRunner(() -> 20).submit(() -> ran.set(true));
        job.join();
        assertTrue(ran.get());
        assertEquals(AsyncJobRunner.State.DONE, job.state());
    }
    @Test void jobIsRunningUntilDone() {
        AsyncJobRunner.Job job = new AsyncJobRunner(() -> 300).submit(() -> {});
        assertEquals(AsyncJobRunner.State.RUNNING, job.state());
        job.join();
    }
    @Test void failingWorkMarksJobFailed() {
        AsyncJobRunner.Job job = new AsyncJobRunner(() -> 1).submit(() -> { throw new IllegalStateException("x"); });
        job.join();
        assertEquals(AsyncJobRunner.State.FAILED, job.state());
    }
}
