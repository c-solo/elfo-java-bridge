package io.github.csolo.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("GroupNo Tests")
class GroupNoTest {

  @Nested
  @DisplayName("Creation Tests")
  class CreationTests {

    @ParameterizedTest
    @ValueSource(bytes = {1, 2, 10, 50, 100, 127, -128, -1}) // -128 to 127
    @DisplayName("Should create GroupNo from any non-zero byte value")
    void shouldCreateGroupNoFromAnyNonZeroValue(byte value) {
      // When
      GroupNo groupNo = GroupNo.fromBits(value);

      // Then
      assertNotNull(groupNo);
      assertEquals(value, groupNo.intoBits());
    }

    @Test
    @DisplayName("Should throw exception for zero value")
    void shouldThrowExceptionForZeroValueInFromBitsOrThrow() {
      // When & Then
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> GroupNo.fromBits((byte) 0));
      assertEquals("Group number cannot be zero", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("Network Mode Tests")
  class NetworkModeTests {

    @Test
    @DisplayName("Should create GroupNo with network randomization")
    void shouldCreateGroupNoWithNetworkRandomization() {
      // Given
      byte no = 10;
      NodeLaunchId launchId = NodeLaunchId.fromBits(0x123456789ABCDEF0L);

      // When
      GroupNo groupNo = GroupNo.newWithNetwork(no, launchId);

      // Then
      assertNotNull(groupNo);
      // The value should be different from input due to XOR
      assertNotEquals(no, groupNo.intoBits());
    }

    @Test
    @DisplayName("Should handle XOR collision in network mode")
    void shouldHandleXorCollisionInNetworkMode() {
      // Given - create a launch ID that would cause XOR collision
      byte no = 10;
      // Create launch ID where XOR would equal the input
      long launchIdBits = (long) no << 8; // Shift left by GROUP_NO_SHIFT (8)
      NodeLaunchId launchId = NodeLaunchId.fromBits(launchIdBits);

      // When
      GroupNo groupNo = GroupNo.newWithNetwork(no, launchId);

      // Then
      assertNotNull(groupNo);
      // Should use XOR value instead of input when collision occurs
      assertEquals(no, groupNo.intoBits()); // XOR of no with itself equals no
    }
  }

  @Nested
  @DisplayName("Equality and HashCode Tests")
  class EqualityAndHashCodeTests {

    @Test
    @DisplayName("Should be equal to another GroupNo with the same value")
    void shouldBeEqualToAnotherGroupNoWithSameValue() {
      // Given
      byte value = 10;
      GroupNo groupNo1 = GroupNo.fromBits(value);
      GroupNo groupNo2 = GroupNo.fromBits(value);

      // When & Then
      assertEquals(groupNo1, groupNo2);
      assertEquals(groupNo1.hashCode(), groupNo2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal to another GroupNo with different value")
    void shouldNotBeEqualToAnotherGroupNoWithDifferentValue() {
      // Given
      GroupNo groupNo1 = GroupNo.fromBits((byte) 10);
      GroupNo groupNo2 = GroupNo.fromBits((byte) 20);

      // When & Then
      assertNotEquals(groupNo1, groupNo2);
      assertNotEquals(groupNo1.hashCode(), groupNo2.hashCode());
    }
  }

  @Nested
  @DisplayName("ToString Tests")
  class ToStringTests {

    @Test
    @DisplayName("Should return string representation")
    void shouldReturnStringRepresentationOfUnsignedValue() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 127);

      // When
      String stringValue = groupNo.toString();

      // Then
      assertEquals("GroupNo[value=127]", stringValue);
    }
  }
}
