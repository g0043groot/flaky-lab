package com.example.orders.decoy;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class D04DisabledTest {
    /** D04 (decoy): @Disabled; always reported as skipped, never as pass or fail. */
    @Test
    @Tag("D04")
    @Disabled("D04 decoy: disabled until the legacy exporter is rewritten")
    @DisplayName("D04 decoy: disabled test")
    void d04_disabled() {
        fail("should never run");
    }
}
