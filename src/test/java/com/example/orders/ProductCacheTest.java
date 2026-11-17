package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class ProductCacheTest {
    private final AtomicLong now = new AtomicLong(1_000);
    private final Clock clock = new Clock() {
        public ZoneId getZone() { return ZoneId.of("UTC"); }
        public Clock withZone(ZoneId z) { return this; }
        public Instant instant() { return Instant.ofEpochMilli(now.get()); }
    };
    private final ProductCache cache = new ProductCache(clock, 100);

    @Test void returnsPutValue() {
        cache.put("A", 5);
        assertEquals(5L, cache.get("A").orElseThrow());
    }
    @Test void missingKeyIsEmpty() {
        assertTrue(cache.get("zzz").isEmpty());
    }
    @Test void entryExpiresAfterTtl() {
        cache.put("A", 5);
        now.addAndGet(100);
        assertTrue(cache.get("A").isEmpty());
    }
    @Test void clearRemovesEverything() {
        cache.put("A", 5);
        cache.put("B", 6);
        cache.clear();
        assertEquals(0, cache.size());
    }
    @Test void putOverwrites() {
        cache.put("A", 5);
        cache.put("A", 9);
        assertEquals(9L, cache.get("A").orElseThrow());
    }
}
