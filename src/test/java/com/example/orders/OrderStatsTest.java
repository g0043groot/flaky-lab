package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderStatsTest {
    @Test void startsAtZero() {
        assertEquals(0, new OrderStats().ordersPlaced());
    }
    @Test void countsSingleThreaded() {
        OrderStats s = new OrderStats();
        for (int i = 0; i < 1000; i++) s.recordOrder();
        assertEquals(1000, s.ordersPlaced());
    }
    @Test void countsExactlyWhenCallersSynchronise() throws Exception {
        OrderStats s = new OrderStats();
        Object lock = new Object();
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < 8; t++) {
            Thread th = new Thread(() -> {
                for (int i = 0; i < 500; i++) synchronized (lock) { s.recordOrder(); }
            });
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) th.join();
        assertEquals(4000, s.ordersPlaced());
    }
}
