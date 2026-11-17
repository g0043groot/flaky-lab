package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class LuckyDiscountTest {
    @Test void seededRollIsReproducible() {
        assertEquals(new LuckyDiscount(new Random(7)).roll(), new LuckyDiscount(new Random(7)).roll());
    }
    @Test void rollIsInUnitInterval() {
        LuckyDiscount d = new LuckyDiscount(new Random(1));
        for (int i = 0; i < 100; i++) {
            double r = d.roll();
            assertTrue(r >= 0 && r < 1);
        }
    }
}
