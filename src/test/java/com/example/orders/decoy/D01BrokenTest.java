package com.example.orders.decoy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class D01BrokenTest {
    /** D01 (decoy): deterministic failure on every run, in every order/TZ/parallel mode. NOT flaky. */
    @Test
    @Tag("D01")
    @DisplayName("D01 decoy: always fails (broken test)")
    void d01_alwaysFails() {
        assertEquals(5, 2 + 2, "intentionally broken expectation");
    }
}
