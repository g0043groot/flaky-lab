package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class EnvironmentDependentTest {
    /**
     * F15 (environment): requires FLAKY_LAB_EXPRESS_SHIPPING=enabled, which the CI matrix sets on only
     * some legs (see .github/workflows/ci.yml and scripts/run_suite_n_times.sh).
     */
    @Test
    @Tag("F15")
    @DisplayName("F15 environment: needs FLAKY_LAB_EXPRESS_SHIPPING=enabled")
    void f15_expressShippingFlagIsEnabled() {
        assertEquals("enabled", System.getenv("FLAKY_LAB_EXPRESS_SHIPPING"));
    }
}
