package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PromoCodeTest {
    /** F04 (order-dependent POLLUTER): always passes itself; leaves promoPercent=50 behind. */
    @Test
    @Tag("F04")
    @DisplayName("F04 order-dependent polluter: leaves promo state set (passes itself)")
    void f04_polluter_enablesPromoAndNeverResets() {
        TestFixtures.promoPercent = 50;
        assertEquals(50, TestFixtures.promoPercent);
    }
}
