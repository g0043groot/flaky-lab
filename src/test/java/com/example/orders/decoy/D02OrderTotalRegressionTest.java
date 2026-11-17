package com.example.orders.decoy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.orders.OrderRepository;
import com.example.orders.OrderService;
import com.example.orders.Order;
import com.example.orders.PricingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class D02OrderTotalRegressionTest {
    /**
     * D02 (decoy, real regression): deterministic. Passes at HEAD; fails on every run between the
     * commit that broke tax-after-discount (introduced_commit) and the fix (fixed_commit), see ground-truth.yaml.
     */
    @Test
    @Tag("D02")
    @DisplayName("D02 decoy: discounted order total charges tax on the discounted amount")
    void d02_discountedOrderTotal() {
        OrderService service = new OrderService(new OrderRepository(), new PricingService());
        Order o = service.create();
        service.addItem(o.getId(), "A", 4, 3_000); // subtotal 12000 -> discount 1200 -> 10800 -> tax 864
        assertEquals(11_664, service.total(o.getId()));
    }
}
