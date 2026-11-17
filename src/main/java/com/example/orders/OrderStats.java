package com.example.orders;

/** Counts orders placed. NOT thread-safe (known bug planted for F06); fine for single-threaded use. */
public class OrderStats {
    private int ordersPlaced;

    public void recordOrder() { ordersPlaced++; }
    public int ordersPlaced() { return ordersPlaced; }
}
