package io.github.csolo.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Node Tests")
class NodeTest {

  @AfterEach
  void tearDown() {
    // Reset node number after each test using package-private method
    NodeNo.resetForTesting();
  }

  @Nested
  @DisplayName("Node Number Management Tests")
  class NodeNumberManagementTests {

    @Test
    @DisplayName("Should throw exception when node number is not set")
    void shouldThrowExceptionWhenNodeNumberIsNotSet() {
      // Given - node number is not set (default state)

      // When & Then
      var exception = assertThrows(IllegalStateException.class, NodeNo::getNodeNo);
      assertEquals("Node number cannot be zero", exception.getMessage());
      assertFalse(NodeNo.isNodeNoSet());
    }

    @Test
    @DisplayName("Should set and get node number")
    void shouldSetAndGetNodeNumber() {
      // Given
      int expectedNodeNo = 42;

      // When
      NodeNo.setNodeNo(expectedNodeNo);
      short actualNodeNo = NodeNo.getNodeNo();

      // Then
      assertEquals(expectedNodeNo, actualNodeNo);
    }

    @Test
    @DisplayName("Should reject zero node number")
    void shouldRejectZeroNodeNumber() {
      // When & Then
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> NodeNo.setNodeNo(0));
      assertEquals("Node number must be positive, got: 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject negative node number")
    void shouldRejectNegativeNodeNumber() {
      // When & Then
      var exception = assertThrows(IllegalArgumentException.class, () -> NodeNo.setNodeNo(-1));
      assertEquals("Node number must be positive, got: -1", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow maximum positive node number")
    void shouldAllowMaximumPositiveNodeNumber() {
      // Given
      int maxNodeNo = Short.MAX_VALUE; // Use max value that fits in short

      // When
      NodeNo.setNodeNo(maxNodeNo);
      short actualNodeNo = NodeNo.getNodeNo();

      // Then
      assertEquals(maxNodeNo, actualNodeNo);
      assertTrue(NodeNo.isNodeNoSet());
    }
  }
}
