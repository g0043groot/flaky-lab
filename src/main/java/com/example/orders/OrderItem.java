package com.example.orders;

public record OrderItem(String sku, int quantity, long unitPriceCents) {
    public OrderItem {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (unitPriceCents < 0) throw new IllegalArgumentException("price must not be negative");
    }

    public long lineTotalCents() {
        return quantity * unitPriceCents;
    }
}
