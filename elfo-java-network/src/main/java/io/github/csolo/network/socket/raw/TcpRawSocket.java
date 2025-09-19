package io.github.csolo.network.socket.raw;

import io.github.csolo.network.config.Transport;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/** TCP raw socket implementation. */
public class TcpRawSocket {

  /** Connect to a TCP address. */
  public static RawSocket connect(Transport.Tcp tcp) throws IOException {

    var addr = tcp.toSocketAddress();
    var channel = SocketChannel.open();
    // we use blocking mode because we use virtual threads
    channel.configureBlocking(true);
    channel.connect(addr);

    // Configure socket settings
    configureSocket(channel);

    // Create socket info
    var socketInfo = new RawSocket.Info.Tcp(channel.getLocalAddress(), addr);

    return new RawSocket(channel, socketInfo);
  }

  /** Configure socket settings. */
  private static void configureSocket(SocketChannel channel) {
    try {
      channel.setOption(java.net.StandardSocketOptions.TCP_NODELAY, true);
    } catch (Exception e) {
      System.err.printf("Warning: cannot configure socket settings: %s%n", e.getMessage());
    }
  }
}
