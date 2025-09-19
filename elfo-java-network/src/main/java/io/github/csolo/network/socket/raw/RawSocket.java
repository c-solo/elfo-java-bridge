package io.github.csolo.network.socket.raw;

import io.github.csolo.network.config.Transport;
import io.vavr.control.Try;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import org.jetbrains.annotations.NotNull;

/** Raw socket connection without handshake. */
public record RawSocket(SocketChannel channel, Info info) {

  /** Information about the raw socket connection. */
  public sealed interface Info {

    /** TCP socket information. */
    record Tcp(@NotNull SocketAddress local, @NotNull SocketAddress peer) implements Info {}

    /** Unix Domain Socket information. */
    record Uds(@NotNull String path, Long peerPid) implements Info {}
  }

  /** Connect to a raw socket. */
  public static Try<RawSocket> connect(Transport transport) {
    return Try.of(
        () ->
            switch (transport) {
              case Transport.Tcp tcp -> TcpRawSocket.connect(tcp);
              case Transport.Uds uds -> UdsRawSocket.connect(uds);
            });
  }

  /** Close the raw socket connection. */
  public Try<Void> close() {
    return Try.of(
        () -> {
          try {
            channel.close();
            return null;
          } catch (Exception e) {
            throw new RuntimeException("Failed to close raw socket", e);
          }
        });
  }
}
