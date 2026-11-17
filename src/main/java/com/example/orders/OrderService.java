package com.example.orders;

import java.time.Clock;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final PricingService pricing;
    private final Clock clock;

    @Autowired
    public OrderService(OrderRepository repository, PricingService pricing) {
        this(repository, pricing, Clock.systemUTC());
    }

    public OrderService(OrderRepository repository, PricingService pricing, Clock clock) {
        this.repository = repository;
        this.pricing = pricing;
        this.clock = clock;
    }

    public Order create() {
        return repository.save(new Order(UUID.randomUUID().toString(), clock.instant()));
    }

    public Order addItem(String orderId, String sku, int qty, long unitPriceCents) {
        Order order = get(orderId);
        order.addItem(new OrderItem(sku, qty, unitPriceCents));
        return order;
    }

    public Order get(String id) {
        return repository.find(id).orElseThrow(() -> new NoSuchElementException("no order " + id));
    }

    public long total(String orderId) {
        return pricing.totalCents(get(orderId).subtotalCents());
    }

    public Order pay(String id) { Order o = get(id); o.markPaid(); return o; }
    public Order ship(String id) { Order o = get(id); o.markShipped(); return o; }
}
