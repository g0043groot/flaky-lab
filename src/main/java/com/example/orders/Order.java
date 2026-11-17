package com.example.orders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Instant createdAt;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.NEW;

    public Order(String id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.NEW) throw new IllegalStateException("order is " + status);
        items.add(item);
    }

    public long subtotalCents() {
        return items.stream().mapToLong(OrderItem::lineTotalCents).sum();
    }

    public void markPaid() { transition(OrderStatus.NEW, OrderStatus.PAID); }
    public void markShipped() { transition(OrderStatus.PAID, OrderStatus.SHIPPED); }

    public void cancel() {
        if (status == OrderStatus.SHIPPED) throw new IllegalStateException("already shipped");
        status = OrderStatus.CANCELLED;
    }

    private void transition(OrderStatus from, OrderStatus to) {
        if (status != from) throw new IllegalStateException("expected " + from + " but was " + status);
        status = to;
    }
}
