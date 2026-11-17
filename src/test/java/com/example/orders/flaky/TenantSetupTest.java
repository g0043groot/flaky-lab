package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** F03 helper ("setter"): always passes, but leaves static state that TenantReportTest depends on. */
class TenantSetupTest {
    @Test
    @Tag("F03-helper")
    @DisplayName("F03 helper: registers tenant in static state (always passes)")
    void f03_helper_registersTenant() {
        TestFixtures.tenant = "acme";
        assertEquals("acme", TestFixtures.tenant);
    }
}
