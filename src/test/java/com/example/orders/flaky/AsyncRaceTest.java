package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.orders.AsyncJobRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** F01 / F02: fixed sleep racing a job that takes 80-120 ms. */
class AsyncRaceTest {

    /** F01 (timing, ~30%): job duration uniform 80-120 ms, test sleeps a fixed 108 ms. */
    @Test
    @Tag("F01")
    @DisplayName("F01 timing: fixed wait vs 80-120ms async job")
    void f01_asyncJobDoneAfterFixedWait() throws Exception {
        AsyncJobRunner.Job job = new AsyncJobRunner().submit(() -> {});
        Thread.sleep(108);
        assertEquals(AsyncJobRunner.State.DONE, job.state());
    }

    /** F02 (timing, rare ~3%): same race, but the wait is just past the 120 ms upper bound (only scheduler jitter loses). */
    @Test
    @Tag("F02")
    @DisplayName("F02 timing (rare): fixed wait nearly covers the 80-120ms job")
    void f02_asyncJobDoneAfterGenerousWait() throws Exception {
        AsyncJobRunner.Job job = new AsyncJobRunner().submit(() -> {});
        Thread.sleep(126);
        assertEquals(AsyncJobRunner.State.DONE, job.state());
    }
}
