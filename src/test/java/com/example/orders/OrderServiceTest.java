package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2024-06-01T12:00:00Z"), ZoneOffset.UTC);
    private final OrderService service = new OrderService(new OrderRepository(), new PricingService(), clock);

    @Test void createUsesClock() {
        assertEquals(Instant.parse("2024-06-01T12:00:00Z"), service.create().getCreatedAt());
    }
    @Test void addItemAccumulatesSubtotal() {
        Order o = service.create();
        service.addItem(o.getId(), "A", 2, 300);
        assertEquals(600, service.get(o.getId()).subtotalCents());
    }
    @Test void unknownOrderThrows() {
        assertThrows(NoSuchElementException.class, () -> service.get("nope"));
    }
    @Test void totalOfSmallOrderIncludesTax() {
        Order o = service.create();
        service.addItem(o.getId(), "A", 1, 1_000);
        assertEquals(1_080, service.total(o.getId()));
    }
    @Test void payAndShipFlow() {
        Order o = service.create();
        service.pay(o.getId());
        assertEquals(OrderStatus.SHIPPED, service.ship(o.getId()).getStatus());
    }
}
