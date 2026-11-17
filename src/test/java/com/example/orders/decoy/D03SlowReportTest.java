package com.example.orders.decoy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.orders.AsyncJobRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class D03SlowReportTest {
    /** D03 (decoy): slow (~20 s) but fully deterministic. Exclude with -Dtest.excludedGroups=slow. */
    @Test
    @Tag("D03")
    @Tag("slow")
    @DisplayName("D03 decoy: slow (20s) deterministic nightly report")
    void d03_slowButDeterministic() {
        AsyncJobRunner.Job job = new AsyncJobRunner(() -> 20_000).submit(() -> {});
        job.join();
        assertEquals(AsyncJobRunner.State.DONE, job.state());
    }
}
