package io.github.csolo.core.tracing;

import static org.junit.jupiter.api.Assertions.*;

import io.github.csolo.core.NodeNo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("TraceId Tests")
class TraceIdTest {

  @Nested
  @DisplayName("Creation Tests")
  class CreationTests {

    @Test
    @DisplayName("Should reject zero value")
    void shouldRejectZeroValue() {
      // When & Then
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> TraceId.fromValue(0L));
      assertEquals("TraceId cannot be zero", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, Long.MIN_VALUE, Long.MAX_VALUE, 12345L, 17L})
    @DisplayName("Should create TraceId from any non-zero value")
    void shouldCreateTraceIdFromAnyNonZeroValue(long value) {
      // When
      TraceId traceId = TraceId.fromValue(value);

      // Then
      assertEquals(value, traceId.value());
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
      // When
      TraceId traceId1 = TraceId.generate();
      TraceId traceId2 = TraceId.generate();

      // Then
      assertNotEquals(traceId1.value(), traceId2.value());
      assertNotEquals(traceId1, traceId2);
      assertEquals(traceId1.getCounter() + 1, traceId2.getCounter());
    }

    @Test
    @DisplayName("Should generate TraceId with specific node number")
    void shouldGenerateTraceIdWithSpecificNodeNumber() {
      // Given
      short nodeNo = 42;
      // When
      TraceId traceId = TraceId.generate(nodeNo);
      // Then
      assertEquals(nodeNo, traceId.getNodeNo());
    }

    @Test
    @DisplayName("Should generate TraceId with default node number")
    void shouldGenerateTraceIdWithDefaultNodeNumber() {
      // When
      TraceId traceId = TraceId.generate();

      // Then
      assertEquals(1, traceId.getNodeNo());

      NodeNo.setNodeNo(17);
      TraceId traceId2 = TraceId.generate();
      assertEquals(17, traceId2.getNodeNo());
    }

    @Test
    @DisplayName("Should extract components")
    void shouldExtractComponents() {
      // Given
      // TraceId with timestamp = 1000, node = 42, chunk = 22, counter = 3
      long value = (1000L << 38) | (42L << 22) | (22L << 10) | 3L;
      TraceId traceId = TraceId.fromValue(value);

      // When
      long timestamp = traceId.getTimestamp();

      // Then
      assertEquals(1000L, timestamp);
      assertEquals(42, traceId.getNodeNo());
      assertEquals(22, traceId.getChunkNo());
      assertEquals(3, traceId.getCounter());
    }
  }

  @Nested
  @DisplayName("Utility Tests")
  class UtilityTests {

    @Test
    @DisplayName("Should check if TraceId is from specific node")
    void shouldCheckIfTraceIdIsFromSpecificNode() {
      // Given
      short nodeNo = 42;
      TraceId traceId = TraceId.generate(nodeNo);

      // When & Then
      assertTrue(traceId.isFromNode(nodeNo));
      assertFalse(traceId.isFromNode((short) (nodeNo + 1)));
    }

    @Test
    @DisplayName("Should calculate time delta between TraceIds")
    void shouldCalculateTimeDeltaBetweenTraceIds() {
      // Given
      long timestamp1 = 1000L;
      long timestamp2 = 1005L;

      long value1 = (timestamp1 << 38) | (1L << 22) | (2L << 10) | 3L;
      long value2 = (timestamp2 << 38) | (1L << 22) | (2L << 10) | 4L;

      TraceId traceId1 = TraceId.fromValue(value1);
      TraceId traceId2 = TraceId.fromValue(value2);

      // When
      long delta = traceId1.timeDelta(traceId2);

      // Then
      assertEquals(5L, delta);
    }
  }

  @Nested
  @DisplayName("Equality and HashCode Tests")
  class EqualityAndHashCodeTests {

    @Test
    @DisplayName("Should be equal for same value")
    void shouldBeEqualForSameValue() {
      // Given
      long value = 12345L;
      TraceId traceId1 = TraceId.fromValue(value);
      TraceId traceId2 = TraceId.fromValue(value);

      // When & Then
      assertEquals(traceId1, traceId2);
      assertEquals(traceId1.hashCode(), traceId2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal for different values")
    void shouldNotBeEqualForDifferentValues() {
      // Given
      TraceId traceId1 = TraceId.fromValue(12345L);
      TraceId traceId2 = TraceId.fromValue(54321L);

      // When & Then
      assertNotEquals(traceId1, traceId2);
      assertEquals("12345", traceId1.toString());
    }
  }
}
