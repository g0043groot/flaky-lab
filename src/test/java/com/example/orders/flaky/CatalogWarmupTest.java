package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.orders.ProductCache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** F05 helper (polluter): populates the process-wide shared cache and never clears it. */
class CatalogWarmupTest {
    @Test
    @Tag("F05-helper")
    @DisplayName("F05 helper: warms the shared cache with sku-42 (always passes)")
    void f05_helper_warmsSharedCache() {
        ProductCache.shared().put("sku-42", 999);
        assertTrue(ProductCache.shared().get("sku-42").isPresent());
    }
}
