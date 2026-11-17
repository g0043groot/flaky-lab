package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class IterationOrderTest {
    /** Plain class: no equals/hashCode, so HashSet buckets by identity hash code. */
    static class LineRef {
        final String sku;
        LineRef(String sku) { this.sku = sku; }
        @Override public String toString() { return sku; }
    }

    /** F07 (iteration order, ~50%): assumes a HashSet of identity-hashed objects iterates in insertion order. */
    @Test
    @Tag("F07")
    @DisplayName("F07 iteration order: HashSet keyed by identity hash")
    void f07_hashSetIteratesInInsertionOrder() {
        LineRef a = new LineRef("A");
        LineRef b = new LineRef("B");
        Set<LineRef> set = new HashSet<>();
        set.add(a);
        set.add(b);
        assertEquals(List.of(a, b), new ArrayList<>(set));
    }
}
