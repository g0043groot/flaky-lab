package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PricingServiceTest {
    private final PricingService pricing = new PricingService();

    @Test void noDiscountAtOrBelowThreshold() {
        assertEquals(0, pricing.discountCents(10_000));
    }
    @Test void tenPercentDiscountAboveThreshold() {
        assertEquals(1_001, pricing.discountCents(10_010));
    }
    @Test void taxRoundsHalfUp() {
        assertEquals(1, pricing.taxCents(7));   // 0.56 -> 1
        assertEquals(0, pricing.taxCents(6));   // 0.48 -> 0
    }
    @Test void zeroSubtotalCostsNothing() {
        assertEquals(0, pricing.totalCents(0));
    }
    @Test void smallOrderTotalIsSubtotalPlusTax() {
        assertEquals(1_080, pricing.totalCents(1_000));
    }
}
