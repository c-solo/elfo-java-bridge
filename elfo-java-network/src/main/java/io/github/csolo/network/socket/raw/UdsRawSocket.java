package io.github.csolo.network.socket.raw;

import io.github.csolo.network.config.Transport;
import java.io.IOException;

/** Unix Domain Socket raw socket implementation */
public class UdsRawSocket {

  /** Connect to a Unix Domain Socket. */
  public static RawSocket connect(Transport.Uds uds) throws IOException {
    // TODO: Implement Unix Domain Socket later
    throw new UnsupportedOperationException("Unix Domain Sockets not yet implemented");
  }
}
