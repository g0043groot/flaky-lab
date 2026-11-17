package com.example.orders;

import org.springframework.stereotype.Service;

@Service
public class PricingService {
    public static final long DISCOUNT_THRESHOLD_CENTS = 10_000;
    public static final int DISCOUNT_PERCENT = 10;
    public static final int TAX_PERCENT = 8;

    /** 10% off orders with a subtotal above the threshold. */
    public long discountCents(long subtotalCents) {
        return subtotalCents > DISCOUNT_THRESHOLD_CENTS ? subtotalCents * DISCOUNT_PERCENT / 100 : 0;
    }

    /** Tax, rounded half up. */
    public long taxCents(long taxableCents) {
        return (taxableCents * TAX_PERCENT + 50) / 100;
    }

    /** Subtotal - discount + tax on the discounted amount. */
    public long totalCents(long subtotalCents) {
        long discounted = subtotalCents - discountCents(subtotalCents);
        return discounted + taxCents(discounted);
    }
}
