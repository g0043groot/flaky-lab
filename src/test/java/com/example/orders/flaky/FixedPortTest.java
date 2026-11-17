package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * F12 (resource): both tests bind the same hard-coded port and hold it for ~150 ms.
 * Never collides sequentially; collides when JUnit runs methods/classes in parallel.
 * Two methods share the F12 ID so a collision is possible within one class; whichever loses fails.
 */
@Execution(ExecutionMode.CONCURRENT) // required: a configured MethodOrderer otherwise forces same-thread method execution
class FixedPortTest {
    static final int PORT = 18765;

    private void bindAndHold() throws Exception {
        Thread.sleep(ThreadLocalRandom.current().nextInt(0, 30));
        try (ServerSocket s = new ServerSocket(PORT)) {
            assertTrue(s.isBound());
            Thread.sleep(150);
        }
    }

    @Test
    @Tag("F12")
    @DisplayName("F12 resource: binds fixed port 18765 (instance A)")
    void f12_bindsFixedPort_a() throws Exception { bindAndHold(); }

    @Test
    @Tag("F12")
    @DisplayName("F12 resource: binds fixed port 18765 (instance B)")
    void f12_bindsFixedPort_b() throws Exception { bindAndHold(); }
}
