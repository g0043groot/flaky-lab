package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.orders.OrderStats;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ConcurrentCounterTest {
    static final int INCREMENTS_PER_THREAD = 250;

    /** F06 (concurrency): OrderStats.recordOrder() is unsynchronised; 8 threads lose updates sometimes. */
    @Test
    @Tag("F06")
    @DisplayName("F06 concurrency: unsynchronised counter updated from 8 threads")
    void f06_counterIsExactUnderEightThreads() throws Exception {
        OrderStats stats = new OrderStats();
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (int t = 0; t < 8; t++) {
            pool.submit(() -> {
                for (int i = 0; i < INCREMENTS_PER_THREAD; i++) stats.recordOrder();
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        assertEquals(8 * INCREMENTS_PER_THREAD, stats.ordersPlaced());
    }
}
