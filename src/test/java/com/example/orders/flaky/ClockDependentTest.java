package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.orders.Order;
import com.example.orders.OrderDates;
import com.example.orders.OrderRepository;
import com.example.orders.OrderService;
import com.example.orders.PricingService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ClockDependentTest {

    /**
     * F08 (time-dependent, ~8%): assumes "created + 5s" is in the same wall-clock minute as "created".
     * Breaks when the test runs in the last 5 seconds of a minute (5/60 = 8.3%).
     */
    @Test
    @Tag("F08")
    @DisplayName("F08 time-dependent: breaks near a minute boundary")
    void f08_orderDeadlineStaysInSameMinute() {
        OrderService service = new OrderService(new OrderRepository(), new PricingService());
        Order order = service.create();
        Instant deadline = order.getCreatedAt().plusSeconds(5);
        assertEquals(order.getCreatedAt().truncatedTo(ChronoUnit.MINUTES), deadline.truncatedTo(ChronoUnit.MINUTES));
    }

    /**
     * F09 (timezone-dependent): OrderDates.dayOf uses the JVM default zone, the expectations assume UTC.
     * Passes only when TZ is UTC (checks one instant that breaks west of UTC and one that breaks east of it).
     */
    @Test
    @Tag("F09")
    @DisplayName("F09 timezone-dependent: day-of-order assumes UTC")
    void f09_orderDayMatchesUtcDate() {
        assertEquals(LocalDate.of(2024, 3, 15), OrderDates.dayOf(Instant.parse("2024-03-15T00:30:00Z")));
        assertEquals(LocalDate.of(2024, 3, 15), OrderDates.dayOf(Instant.parse("2024-03-15T23:00:00Z")));
    }
}
