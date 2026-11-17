package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class CheckoutPriceTest {
    /** F04 victim: this test is what actually fails when PromoCodeTest ran earlier. */
    @Test
    @Tag("F04-victim")
    @DisplayName("F04 victim: assumes no promo is active")
    void f04_victim_priceWithoutPromo() {
        long list = 1_000;
        long price = list - list * TestFixtures.promoPercent / 100;
        assertEquals(1_000, price);
    }
}
