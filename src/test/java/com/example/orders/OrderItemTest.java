package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OrderItemTest {
    @Test void lineTotalMultipliesQuantityByPrice() {
        assertEquals(750, new OrderItem("A", 3, 250).lineTotalCents());
    }
    @Test void rejectsBlankSku() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(" ", 1, 100));
    }
    @Test void rejectsNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem("A", 0, 100));
    }
    @Test void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem("A", 1, -1));
    }
}
