package com.example.orders;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/** Calendar-day helpers used by order exports. */
public final class OrderDates {
    private OrderDates() {}

    /** The calendar day an order was placed, in the server's default timezone. */
    public static LocalDate dayOf(Instant createdAt) {
        return LocalDate.ofInstant(createdAt, ZoneId.systemDefault());
    }
}
