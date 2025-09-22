package io.github.csolo.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("NodeLaunchId Tests")
class NodeLaunchIdTest {

  @Nested
  @DisplayName("Generation Tests")
  class GenerationTests {

    @Test
    @DisplayName("Should generate unique NodeLaunchIds")
    void shouldGenerateUniqueNodeLaunchIds() {
      // When
      NodeLaunchId id1 = NodeLaunchId.generate();
      NodeLaunchId id2 = NodeLaunchId.generate();

      // Then
      assertNotNull(id1);
      assertNotNull(id2);
      assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should generate multiple unique NodeLaunchIds")
    void shouldGenerateMultipleUniqueNodeLaunchIds() {
      // Given
      int count = 100;
      Set<NodeLaunchId> ids = new HashSet<>();

      // When
      for (int i = 0; i < count; i++) {
        ids.add(NodeLaunchId.generate());
      }

      // Then
      assertEquals(count, ids.size()); // All should be unique
    }
  }

  @Nested
  @DisplayName("Creation Tests")
  class CreationTests {

    @Test
    @DisplayName("Should create NodeLaunchId from bits")
    void shouldCreateNodeLaunchIdFromBits() {
      // Given
      long bits = 0x123456789ABCDEF0L;

      // When
      NodeLaunchId id = NodeLaunchId.fromBits(bits);

      // Then
      assertNotNull(id);
      assertEquals(bits, id.intoBits());
    }

    @Test
    @DisplayName("Should handle zero value")
    void shouldHandleZeroValue() {
      // Given
      long bits = 0L;

      // When
      NodeLaunchId id = NodeLaunchId.fromBits(bits);

      // Then
      assertNotNull(id);
      assertEquals(bits, id.intoBits());
    }

    @Test
    @DisplayName("Should handle negative value")
    void shouldHandleNegativeValue() {
      // Given
      long bits = -1L;

      // When
      NodeLaunchId id = NodeLaunchId.fromBits(bits);

      // Then
      assertNotNull(id);
      assertEquals(bits, id.intoBits());
    }
  }

  @Nested
  @DisplayName("Equality and HashCode Tests")
  class EqualityAndHashCodeTests {

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
      // Given
      NodeLaunchId id = NodeLaunchId.generate();

      // When & Then
      assertEquals(id, id);
    }

    @Test
    @DisplayName("Should be equal to another NodeLaunchId with the same value")
    void shouldBeEqualToAnotherNodeLaunchIdWithSameValue() {
      // Given
      long value = 0x123456789ABCDEF0L;
      NodeLaunchId id1 = NodeLaunchId.fromBits(value);
      NodeLaunchId id2 = NodeLaunchId.fromBits(value);

      // When & Then
      assertEquals(id1, id2);
      assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal to another NodeLaunchId with different value")
    void shouldNotBeEqualToAnotherNodeLaunchIdWithDifferentValue() {
      // Given
      NodeLaunchId id1 = NodeLaunchId.fromBits(0x123456789ABCDEF0L);
      NodeLaunchId id2 = NodeLaunchId.fromBits(0xFEDCBA9876543210L);

      // When & Then
      assertNotEquals(id1, id2);
      // Note: hashCode() can be equal for different objects (hash collision)
      // We only test that equals() works correctly
    }
  }

  @Nested
  @DisplayName("ToString Tests")
  class ToStringTests {

    @Test
    @DisplayName("Should return string representation of the value")
    void shouldReturnStringRepresentationOfTheValue() {
      // Given
      long value = 0x123456789ABCDL;
      NodeLaunchId id = NodeLaunchId.fromBits(value);

      // When
      String stringValue = id.toString();

      // Then
      assertEquals("NodeLaunchId[value=" + value + "]", stringValue);
    }
  }
}
