package com.example.orders;

import java.util.Random;

/** Random promotional roll in [0,1). Uses an unseeded Random by default. */
public class LuckyDiscount {
    private final Random random;

    public LuckyDiscount() { this(new Random()); }
    public LuckyDiscount(Random random) { this.random = random; }

    public double roll() { return random.nextDouble(); }
    public boolean wins(double threshold) { return roll() < threshold; }
}
