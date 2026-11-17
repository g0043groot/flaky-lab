package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.orders.ProductCache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class SharedCacheTest {
    /**
     * F05 (shared state): expects a cold shared cache but nothing clears it between tests.
     * Fails only if CatalogWarmupTest ran within the cache TTL (100 ms) before this test.
     */
    @Test
    @Tag("F05")
    @DisplayName("F05 shared state: static cache not cleared between tests")
    void f05_firstLookupOfSku42IsCacheMiss() {
        assertTrue(ProductCache.shared().get("sku-42").isEmpty(), "shared cache was polluted by another test");
    }
}
