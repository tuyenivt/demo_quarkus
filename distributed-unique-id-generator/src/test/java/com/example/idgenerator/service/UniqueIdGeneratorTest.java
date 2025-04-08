package com.example.idgenerator.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UniqueIdGeneratorTest {

    @Inject
    UniqueIdGenerator idGenerator;

    @Test
    void testGenerateUniqueIds() {
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            long id = idGenerator.generateId();
            assertTrue(ids.add(id), "Generated duplicate ID: " + id);
        }
    }

    @Test
    void testGenerateBatchIds() {
        long[] ids = idGenerator.generateBatch(100);
        assertEquals(100, ids.length);
        
        Set<Long> uniqueIds = new HashSet<>();
        for (long id : ids) {
            assertTrue(uniqueIds.add(id), "Generated duplicate ID in batch: " + id);
        }
    }

    @Test
    void testConcurrentIdGeneration() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        int idsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Set<Long> allIds = ConcurrentHashMap.newKeySet();

        Future<?>[] futures = new Future[threadCount];
        for (int i = 0; i < threadCount; i++) {
            futures[i] = executor.submit(() -> {
                Set<Long> threadIds = new HashSet<>();
                for (int j = 0; j < idsPerThread; j++) {
                    long id = idGenerator.generateId();
                    assertTrue(threadIds.add(id), "Generated duplicate ID in thread");
                    assertTrue(allIds.add(id), "Generated duplicate ID across threads");
                }
            });
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(threadCount * idsPerThread, allIds.size());
    }

    @Test
    void testInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> idGenerator.generateBatch(0));
        assertThrows(IllegalArgumentException.class, () -> idGenerator.generateBatch(1001));
    }
} 