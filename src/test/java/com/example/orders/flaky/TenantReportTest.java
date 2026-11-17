package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class TenantReportTest {
    /** F03 (order-dependent VICTIM): fails whenever it runs before TenantSetupTest. */
    @Test
    @Tag("F03")
    @DisplayName("F03 order-dependent victim: needs tenant set by another test")
    void f03_reportUsesTenantFromEarlierTest() {
        assertEquals("acme", TestFixtures.tenant);
    }
}
