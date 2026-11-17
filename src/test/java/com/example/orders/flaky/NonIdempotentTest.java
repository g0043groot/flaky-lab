package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class NonIdempotentTest {
    /**
     * F14 (non-idempotent): writes a marker on first run and asserts it does not exist.
     * Passes in a clean workspace; fails if the suite is re-run in the same workspace
     * (flaky.state.dir persists, e.g. a CI re-run restoring the cached state dir).
     */
    @Test
    @Tag("F14")
    @DisplayName("F14 non-idempotent: fails on re-run in same workspace")
    void f14_firstRunWritesMarker() throws Exception {
        Path dir = Path.of(System.getProperty("flaky.state.dir", ".flaky-state"));
        Files.createDirectories(dir);
        Path marker = dir.resolve("f14.marker");
        assertFalse(Files.exists(marker), "state left behind by a previous run: " + marker);
        Files.writeString(marker, "seen");
    }
}
