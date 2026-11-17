package com.example.orders.flaky;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * F13 (filesystem): both tests write/read/delete the same fixed file name in the temp dir.
 * Fine sequentially; collides under parallel execution (two methods share the F13 ID).
 */
@Execution(ExecutionMode.CONCURRENT) // required: a configured MethodOrderer otherwise forces same-thread method execution
class FixedTempFileTest {
    static final Path FILE = Path.of(System.getProperty("java.io.tmpdir"), "flaky-lab-order-export.csv");

    private void writeReadDelete(String content) throws Exception {
        Files.writeString(FILE, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Thread.sleep(ThreadLocalRandom.current().nextInt(20, 80));
        assertEquals(content, Files.readString(FILE));
        Files.delete(FILE);
    }

    @Test
    @Tag("F13")
    @DisplayName("F13 filesystem: fixed temp file name (instance A)")
    void f13_fixedTempFile_a() throws Exception { writeReadDelete("export-A"); }

    @Test
    @Tag("F13")
    @DisplayName("F13 filesystem: fixed temp file name (instance B)")
    void f13_fixedTempFile_b() throws Exception { writeReadDelete("export-B"); }
}
