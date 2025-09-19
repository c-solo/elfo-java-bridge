package io.github.csolo.network.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Transport Tests")
class TransportTest {

  @Nested
  @DisplayName("Transport Parsing Tests")
  class TransportParsingTests {

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n", " \t\n "})
    @DisplayName("Should reject empty or whitespace-only transport strings")
    void shouldRejectEmptyOrWhitespaceTransportStrings(String transportString) {
      // When & Then
      var ex = Transport.fromString(transportString).getCause();
      assertInstanceOf(IllegalArgumentException.class, ex);
      assertTrue(ex.getMessage().contains("Transport string cannot be null or empty"));
    }

    @Test
    @DisplayName("Should reject null transport string")
    void shouldRejectNullTransportString() {
      // When & Then
      var ex = Transport.fromString(null).getCause();
      assertInstanceOf(IllegalArgumentException.class, ex);
      assertTrue(ex.getMessage().contains("Transport string cannot be null or empty"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"://a/b", "tcp127.0.0.1:4242"})
    @DisplayName("Should reject transport without protocol or separator")
    void shouldRejectTransportWithoutProtocol(String transportString) {
      // When & Then
      var ex = Transport.fromString(transportString).getCause();
      assertInstanceOf(IllegalArgumentException.class, ex);
      assertTrue(ex.getMessage().contains("Transport must be in format 'protocol://address'"));
    }

    @Test
    @DisplayName("Should reject unknown protocol")
    void shouldRejectUnknownProtocol() {
      // When & Then
      var ex = Transport.fromString("foo://a").getCause();
      assertInstanceOf(IllegalArgumentException.class, ex);
      assertEquals("Unknown protocol: foo", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject UDS directory path")
    void shouldRejectUdsDirectoryPath() {
      // When & Then
      var ex = Transport.fromString("uds:///tmp/").getCause();
      assertInstanceOf(IllegalArgumentException.class, ex);
      assertEquals("Path to UDS socket cannot be directory", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"tcp://127.0.0.1:4242", "tcp://localhost:8080", "tcp://0.0.0.0:9000"})
    @DisplayName("Should parse various TCP addresses")
    void shouldParseVariousTcpAddresses(String transportString) {
      // When
      Transport transport = Transport.fromString(transportString).get();

      // Then
      assertInstanceOf(Transport.Tcp.class, transport);
      assertTrue(transport.isTcp());
      assertFalse(transport.isUds());
    }

    @ParameterizedTest
    @ValueSource(
        strings = {" tcp://127.0.0.1:4242", "tcp://127.0.0.1:4242 ", "  uds:///tmp/socket  "})
    @DisplayName("Should parse transport strings with leading/trailing whitespace")
    void shouldParseTransportStringsWithWhitespace(String transportString) {
      // When
      Transport transport = Transport.fromString(transportString).get();

      // Then
      assertTrue(transport.isTcp() || transport.isUds());
      // Should parse successfully after trimming whitespace
    }

    @ParameterizedTest
    @ValueSource(strings = {"uds:///tmp/socket", "uds://rel/socket", "uds:///var/run/app.sock"})
    @DisplayName("Should parse various UDS paths")
    void shouldParseVariousUdsPaths(String transportString) {
      // When
      Transport transport = Transport.fromString(transportString).get();

      // Then
      assertInstanceOf(Transport.Uds.class, transport);
      assertFalse(transport.isTcp());
      assertTrue(transport.isUds());
    }
  }
}
