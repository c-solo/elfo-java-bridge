package io.github.csolo.network.socket.raw;

import static org.junit.jupiter.api.Assertions.*;

import io.github.csolo.network.config.Transport;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Raw Socket Tests")
class RawSocketTest {

  @Test
  @DisplayName("Should create TCP raw socket")
  void shouldCreateTcpRawSocket() throws IOException {
    Transport transport = new Transport.Tcp("127.0.0.1:9999");

    assertDoesNotThrow(
        () -> {
          try {
            var rawSocket = RawSocket.connect(transport);
            assertNotNull(rawSocket);
            assertNotNull(rawSocket.channel());
            assertNotNull(rawSocket.info());

            rawSocket.close();
          } catch (Exception e) {
            // Expected to fail since we're not connecting to a real server
            assertTrue(e.getMessage().contains("Connection refused"));
          }
        });
  }

  @Test
  @DisplayName("Should handle UDS raw socket")
  void shouldHandleUdsRawSocket() throws IOException {
    Transport transport = new Transport.Uds(java.nio.file.Paths.get("/tmp/test.sock"));

    assertDoesNotThrow(
        () -> {
          try {
            RawSocket.connect(transport);
            fail("Should have thrown UnsupportedOperationException");
          } catch (Exception e) {
            assertTrue(e.getMessage().contains("not yet implemented"));
          }
        });
  }
}
