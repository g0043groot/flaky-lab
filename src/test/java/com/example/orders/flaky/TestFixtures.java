package com.example.orders.flaky;

/** Process-wide mutable state shared by tests. Deliberately never reset (F03/F04). */
public final class TestFixtures {
    private TestFixtures() {}

    /** Set by TenantSetupTest (F03 setter), read by TenantReportTest (F03 victim). */
    public static volatile String tenant = null;

    /** Set by PromoCodeTest (F04 polluter), assumed to be 0 by CheckoutPriceTest (F04 victim). */
    public static volatile int promoPercent = 0;
}
