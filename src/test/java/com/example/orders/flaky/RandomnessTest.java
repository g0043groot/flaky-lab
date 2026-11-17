package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.orders.LuckyDiscount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class RandomnessTest {
    /** F10 (randomness, ~5%): unseeded Random; fails whenever the roll is below 0.05. */
    @Test
    @Tag("F10")
    @DisplayName("F10 randomness: unseeded Random roll < 0.05 fails")
    void f10_rollIsAtLeastFivePercent() {
        double roll = new LuckyDiscount().roll();
        assertTrue(roll >= 0.05, "roll was " + roll);
    }
}
