package io.github.csolo.network.socket;

import static org.junit.jupiter.api.Assertions.*;

import io.github.csolo.network.protocol.Handshake;
import io.github.csolo.network.socket.raw.RawSocket;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Socket Tests")
class SocketTest {

  @Test
  @DisplayName("Should create socket from raw socket and handshake")
  void shouldCreateSocketFromRawSocketAndHandshake() throws IOException {
    // Given
    SocketChannel mockChannel = SocketChannel.open();
    RawSocket rawSocket = new RawSocket(mockChannel, new RawSocket.Info.Tcp(null, null));
    Handshake handshake = new Handshake((short) 1, 123L, 0);

    // When
    Socket socket = Socket.of(rawSocket, handshake);

    // Then
    assertNotNull(socket);
    assertNotNull(socket.raw());
    assertNotNull(socket.peer());
    assertNotNull(socket.handshake());
    assertEquals(mockChannel, socket.raw().channel());

    assertEquals((short) 1, socket.peer().nodeNo());
    assertEquals(123L, socket.peer().launchId());

    // Cleanup
    mockChannel.close();
  }

  @Test
  @DisplayName("Should detect self connection")
  void shouldDetectSelfConnection() throws IOException {
    // Given
    SocketChannel mockChannel = SocketChannel.open();
    RawSocket rawSocket = new RawSocket(mockChannel, new RawSocket.Info.Tcp(null, null));
    Handshake handshake = new Handshake((short) 1, 123L, 0);
    Socket socket = Socket.of(rawSocket, handshake);

    // When & Then
    assertTrue(socket.isSelfConnection((short) 1));
    assertFalse(socket.isSelfConnection((short) 2));

    // Cleanup
    mockChannel.close();
  }
}
