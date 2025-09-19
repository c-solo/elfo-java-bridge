package io.github.csolo.network.protocol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Handshake Tests")
class HandshakeTest {

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create handshake with valid parameters")
    void shouldCreateHandshakeWithValidParameters() {
      // Given
      short nodeNo = 100;
      long launchId = 12345L;
      int capabilities = 0x01;

      // When
      Handshake handshake = new Handshake(nodeNo, launchId, capabilities);

      // Then
      assertEquals(Handshake.THIS_NODE_VERSION, handshake.getVersion());
      assertEquals(nodeNo, handshake.getNodeNo());
      assertEquals(launchId, handshake.getLaunchId());
      assertEquals(capabilities, handshake.getCapabilities());
    }

    @Test
    @DisplayName("Should handle maximum values")
    void shouldHandleMaximumValues() {
      // Given
      short maxNodeNo = Short.MAX_VALUE;
      long maxLaunchId = Long.MAX_VALUE;
      int maxCapabilities = Integer.MAX_VALUE;

      // When
      Handshake handshake = new Handshake(maxNodeNo, maxLaunchId, maxCapabilities);

      // Then
      assertEquals(maxNodeNo, handshake.getNodeNo());
      assertEquals(maxLaunchId, handshake.getLaunchId());
      assertEquals(maxCapabilities, handshake.getCapabilities());
    }
  }

  @Nested
  @DisplayName("Serialization Deserialization Tests")
  class SerializationDeserializationTests {

    @Test
    @DisplayName("Should handle round-trip serialization")
    void shouldSerializeWithCorrectMagicNumber() {
      Handshake original = new Handshake((short) 100, 12345L, 0x01);
      byte[] data = original.toBytes();
      assertEquals(Handshake.HANDSHAKE_LENGTH, data.length);

      Handshake deserialized = Handshake.fromBytes(data);
      assertEquals(original.getVersion(), deserialized.getVersion());
      assertEquals(original.getNodeNo(), deserialized.getNodeNo());
      assertEquals(original.getLaunchId(), deserialized.getLaunchId());
      assertEquals(original.getCapabilities(), deserialized.getCapabilities());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 20, 38})
    @DisplayName("Should fail with various short data lengths")
    void shouldFailWithVariousShortDataLengths(int length) {
      // Given
      byte[] shortData = new byte[length];

      // When & Then
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> Handshake.fromBytes(shortData));
      assertTrue(exception.getMessage().contains("Expected handshake of length"));
    }

    @Test
    @DisplayName("Should fail with invalid magic number")
    void shouldFailWithInvalidMagicNumber() {
      // Given
      byte[] invalidData = new byte[Handshake.HANDSHAKE_LENGTH];
      // Set invalid magic number
      invalidData[0] = (byte) 0xDE;
      invalidData[1] = (byte) 0xAD;
      invalidData[2] = (byte) 0xBE;
      invalidData[3] = (byte) 0xEF;
      // Rest is zeros

      // When & Then
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> Handshake.fromBytes(invalidData));
      assertTrue(exception.getMessage().contains("Handshake magic did not match"));
    }

    @Test
    @DisplayName("Should fail with null data")
    void shouldFailWithNullData() {
      // When & Then
      assertThrows(AssertionError.class, () -> Handshake.fromBytes(null));
    }
  }
}
