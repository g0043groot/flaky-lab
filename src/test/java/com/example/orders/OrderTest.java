package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrderTest {
    private Order order() { return new Order("o-1", Instant.parse("2024-01-01T00:00:00Z")); }

    @Test void newOrderIsNewAndEmpty() {
        Order o = order();
        assertEquals(OrderStatus.NEW, o.getStatus());
        assertEquals(0, o.subtotalCents());
    }
    @Test void subtotalSumsLines() {
        Order o = order();
        o.addItem(new OrderItem("A", 2, 100));
        o.addItem(new OrderItem("B", 1, 50));
        assertEquals(250, o.subtotalCents());
    }
    @Test void payThenShip() {
        Order o = order();
        o.markPaid();
        o.markShipped();
        assertEquals(OrderStatus.SHIPPED, o.getStatus());
    }
    @Test void cannotShipUnpaidOrder() {
        assertThrows(IllegalStateException.class, () -> order().markShipped());
    }
    @Test void cannotAddItemsAfterPayment() {
        Order o = order();
        o.markPaid();
        assertThrows(IllegalStateException.class, () -> o.addItem(new OrderItem("A", 1, 1)));
    }
    @Test void cannotCancelShippedOrder() {
        Order o = order();
        o.markPaid();
        o.markShipped();
        assertThrows(IllegalStateException.class, o::cancel);
    }
    @Test void itemsViewIsReadOnly() {
        assertThrows(UnsupportedOperationException.class, () -> order().getItems().add(new OrderItem("A", 1, 1)));
    }
}
