package com.example.orders;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> find(String id) { return Optional.ofNullable(orders.get(id)); }
    public Collection<Order> all() { return orders.values(); }
    public int count() { return orders.size(); }
}
