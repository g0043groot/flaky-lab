package com.example.orders;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Small TTL cache for product prices. Has an instance API and a process-wide shared instance. */
@Component
public class ProductCache {
    private static final ProductCache SHARED = new ProductCache(Clock.systemUTC(), 100);

    public static ProductCache shared() { return SHARED; }

    private record Entry(long priceCents, long expiresAtMillis) {}

    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private final Clock clock;
    private final long ttlMillis;

    @Autowired
    public ProductCache() { this(Clock.systemUTC(), 60_000); }

    public ProductCache(Clock clock, long ttlMillis) {
        this.clock = clock;
        this.ttlMillis = ttlMillis;
    }

    public void put(String sku, long priceCents) {
        entries.put(sku, new Entry(priceCents, clock.millis() + ttlMillis));
    }

    public Optional<Long> get(String sku) {
        Entry e = entries.get(sku);
        if (e == null) return Optional.empty();
        if (e.expiresAtMillis() <= clock.millis()) {
            entries.remove(sku);
            return Optional.empty();
        }
        return Optional.of(e.priceCents());
    }

    public void clear() { entries.clear(); }
    public int size() { return entries.size(); }
}
