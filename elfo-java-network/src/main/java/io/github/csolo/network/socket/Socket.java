package io.github.csolo.network.socket;

import io.github.csolo.network.config.Transport;
import io.github.csolo.network.protocol.Handshake;
import io.github.csolo.network.socket.raw.RawSocket;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a socket connection to elfo node.
 *
 * @param raw The underlying raw socket connection
 * @param peer Information about the connected peer node
 * @param handshake Handshake information exchanged during connection
 */
public record Socket(RawSocket raw, Peer peer, Handshake handshake) {

  /** Information about a peer node. */
  public record Peer(short nodeNo, long launchId) {
    static Peer of(Handshake handshake) {
      return new Peer(handshake.getNodeNo(), handshake.getLaunchId());
    }
  }

  /**
   * Create a new Socket from raw socket and handshake. Corresponds to
   *
   * @param rawSocket Raw socket connection
   * @param handshake Handshake information
   */
  public static Socket of(@NotNull RawSocket rawSocket, @NotNull Handshake handshake) {
    var peer = Socket.Peer.of(handshake);

    // TODO: Handle capabilities (LZ4 compression)
    // In Rust: if handshake.capabilities.contains(Capabilities::LZ4)
    // For now, we'll use basic read/write without compression

    return new Socket(rawSocket, peer, handshake);
  }

  /** Read data from the socket. */
  public Try<Integer> read(ByteBuffer buffer) {
    return Try.of(
        () -> {
          try {
            return raw.channel().read(buffer);
          } catch (Exception e) {
            throw new RuntimeException("Failed to read from socket", e);
          }
        });
  }

  /** Write data to the socket. */
  public Try<Integer> write(ByteBuffer buffer) {
    return Try.of(
        () -> {
          try {
            return raw.channel().write(buffer);
          } catch (Exception e) {
            throw new RuntimeException("Failed to write to socket", e);
          }
        });
  }

  /** Closes the socket connection. */
  public Try<Void> close() {
    return Try.of(
        () -> {
          try {
            raw.channel().close();
            return null; // Void return
          } catch (Exception e) {
            throw new RuntimeException("Failed to close socket", e);
          }
        });
  }

  /** Checks if this socket is connected to the same node. */
  public boolean isSelfConnection(short nodeNo) {
    return peer.nodeNo() == nodeNo;
  }

  /**
   * Connect to a remote elfo node.
   *
   * @param transport Transport address to connect to
   * @param nodeNo This node's number
   * @param launchId This node's launch ID
   * @param capabilities This node's capabilities
   */
  public static Socket connect(Transport transport, short nodeNo, long launchId, int capabilities)
      throws IOException {

    var rawSocket = RawSocket.connect(transport);
    var handshake = performHandshake(rawSocket.channel(), nodeNo, launchId, capabilities);

    return Socket.of(rawSocket, handshake);
  }

  /** Perform handshake over the given channel. */
  private static Handshake performHandshake(
      SocketChannel channel, short nodeNo, long launchId, int capabilities) throws IOException {

    // Send our handshake
    Handshake ourHandshake = new Handshake(nodeNo, launchId, capabilities);
    byte[] handshakeBytes = ourHandshake.toBytes();
    ByteBuffer buffer = ByteBuffer.wrap(handshakeBytes);
    while (buffer.hasRemaining()) {
      channel.write(buffer);
    }

    // Read peer's handshake
    ByteBuffer responseBuffer = ByteBuffer.allocate(Handshake.HANDSHAKE_LENGTH);
    while (responseBuffer.hasRemaining()) {
      int bytesRead = channel.read(responseBuffer);
      if (bytesRead == -1) {
        throw new IOException("Connection closed during handshake");
      }
    }

    return Handshake.fromBytes(responseBuffer.array());
  }
}
