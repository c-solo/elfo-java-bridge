package io.github.csolo.core.tracing;

import static org.junit.jupiter.api.Assertions.*;

import io.github.csolo.core.NodeNo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TraceIdGenerator Tests")
class TraceIdGeneratorTest {

  @Nested
  @DisplayName("Singleton Tests")
  class SingletonTests {

    @Test
    @DisplayName("Should return same instance")
    void shouldReturnSameInstance() {
      // When
      TraceIdGenerator generator1 = TraceIdGenerator.getInstance();
      TraceIdGenerator generator2 = TraceIdGenerator.getInstance();

      // Then
      assertSame(generator1, generator2);
    }
  }

  @Nested
  @DisplayName("Generation Tests")
  class GenerationTests {

    @BeforeEach
    void tearDown() {
      // Reset node number after each test using package-private method
      NodeNo.setNodeNo(1);
    }

    @Test
    @DisplayName("Should generate unique TraceIds")
    void shouldGenerateUniqueTraceIds() {
      // Given
      TraceIdGenerator generator = TraceIdGenerator.getInstance();

      // When
      TraceId traceId1 = generator.generate();
      TraceId traceId2 = generator.generate();

      // Then
      assertNotEquals(traceId1.value(), traceId2.value());
      assertNotEquals(traceId1, traceId2);
    }

    @Test
    @DisplayName("Should generate TraceId with specific node number")
    void shouldGenerateTraceIdWithSpecificNodeNumber() {
      // Given
      TraceIdGenerator generator = TraceIdGenerator.getInstance();
      short nodeNo = 42;

      // When
      TraceId traceId = generator.generate(nodeNo);

      // Then
      assertEquals(nodeNo, traceId.getNodeNo());
    }

    @Test
    @DisplayName("Should generate TraceId with default node number")
    void shouldGenerateTraceIdWithDefaultNodeNumber() {
      // Given
      TraceIdGenerator generator = TraceIdGenerator.getInstance();

      // When
      TraceId traceId = generator.generate();

      // Then
      assertEquals(1, traceId.getNodeNo());
    }

    @Test
    @DisplayName("Should generate TraceIds with increasing counters")
    void shouldGenerateTraceIdsWithIncreasingCounters() {
      // Given
      TraceIdGenerator generator = TraceIdGenerator.getInstance();

      // When
      TraceId traceId1 = generator.generate();
      TraceId traceId2 = generator.generate();
      TraceId traceId3 = generator.generate();

      // Then
      assertTrue(traceId1.getCounter() < traceId2.getCounter());
      assertTrue(traceId2.getCounter() < traceId3.getCounter());
    }

    @Test
    @DisplayName("Should handle chunk exhaustion")
    void shouldHandleChunkExhaustion() {
      // Given
      TraceIdGenerator generator = TraceIdGenerator.getInstance();

      // When - generate many TraceIds to exhaust chunks
      TraceId[] traceIds = new TraceId[2000]; // More than 1024 (0x3FF) to trigger chunk change
      for (int i = 0; i < traceIds.length; i++) {
        traceIds[i] = generator.generate();
      }

      // Then - should have different chunk numbers
      boolean foundDifferentChunks = false;
      int firstChunk = traceIds[0].getChunkNo();
      for (int i = 1; i < traceIds.length; i++) {
        if (traceIds[i].getChunkNo() != firstChunk) {
          foundDifferentChunks = true;
          break;
        }
      }
      assertTrue(
          foundDifferentChunks, "Should have generated TraceIds with different chunk numbers");
    }
  }

  @Nested
  @DisplayName("Concurrency Tests")
  class ConcurrencyTests {

    @BeforeEach
    void tearDown() {
      // Reset node number after each test using package-private method
      NodeNo.setNodeNo(1);
    }

    @Test
    @DisplayName("Should be thread-safe")
    void shouldBeThreadSafe() throws InterruptedException {
      // Given
      TraceIdGenerator generator = TraceIdGenerator.getInstance();
      int threadCount = 10;
      int iterationsPerThread = 100;
      Thread[] threads = new Thread[threadCount];
      TraceId[][] results = new TraceId[threadCount][iterationsPerThread];

      // When - generate TraceIds from multiple threads
      for (int t = 0; t < threadCount; t++) {
        final int threadIndex = t;
        var x = Thread.ofVirtual();
        threads[t] =
            Thread.ofVirtual()
                .start(
                    () -> {
                      for (int i = 0; i < iterationsPerThread; i++) {
                        results[threadIndex][i] = generator.generate();
                      }
                    });
      }

      // Wait for all threads to complete
      for (Thread thread : threads) {
        thread.join();
      }

      // Then - all TraceIds should be unique
      for (int t1 = 0; t1 < threadCount; t1++) {
        for (int i1 = 0; i1 < iterationsPerThread; i1++) {
          for (int t2 = 0; t2 < threadCount; t2++) {
            for (int i2 = 0; i2 < iterationsPerThread; i2++) {
              if (t1 != t2 || i1 != i2) {
                assertNotEquals(
                    results[t1][i1].value(),
                    results[t2][i2].value(),
                    String.format(
                        "TraceIds should be unique: thread %d, iter %d vs thread %d, iter %d",
                        t1, i1, t2, i2));
              }
            }
          }
        }
      }
    }
  }
}
