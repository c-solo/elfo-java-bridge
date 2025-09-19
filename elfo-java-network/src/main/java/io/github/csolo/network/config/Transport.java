package io.github.csolo.network.config;

import io.vavr.control.Try;
import java.nio.file.Path;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/** Transport protocol for elfo-network connections. */
public sealed interface Transport {

  /** TCP transport. */
  record Tcp(@NotNull String address) implements Transport {
    public Tcp {
      Objects.requireNonNull(address, "address must not be null");
    }

    @Override
    @NotNull
    public String toString() {
      return "tcp://" + address;
    }
  }

  /** Unix Domain Socket transport. */
  record Uds(@NotNull Path path) implements Transport {
    public Uds {
      Objects.requireNonNull(path, "path must not be null");
    }

    @Override
    @NotNull
    public String toString() {
      return "uds://" + path;
    }
  }

  /** Check if this transport is TCP. */
  default boolean isTcp() {
    return this instanceof Tcp;
  }

  /** Check if this transport is UDS. */
  default boolean isUds() {
    return this instanceof Uds;
  }

  /** Get the address/path as string. */
  default String getAddress() {
    return switch (this) {
      case Tcp tcp -> tcp.address();
      case Uds uds -> uds.path().toString();
    };
  }

  /**
   * Parse transport from string format.
   *
   * @param transportString String in format "protocol://address"
   */
  static Try<Transport> fromString(String transportString) {
    return Try.of(() -> fromStringImpl(transportString));
  }

  static Transport fromStringImpl(String transportString) {
    if (transportString == null || transportString.isEmpty() || transportString.trim().isEmpty()) {
      throw new IllegalArgumentException("Transport string cannot be null or empty");
    }

    // Trim whitespace from the entire string
    String trimmed = transportString.trim();
    String[] parts = trimmed.split("://", 2);
    if (parts.length != 2 || parts[0].isEmpty()) {
      throw new IllegalArgumentException("Transport must be in format 'protocol://address'");
    }

    String protocol = parts[0];
    String address = parts[1];

    return switch (protocol) {
      case "tcp" -> new Tcp(address);
      case "uds" -> {
        if (address.endsWith("/")) {
          throw new IllegalArgumentException("Path to UDS socket cannot be directory");
        }
        yield new Uds(Path.of(address));
      }
      default -> throw new IllegalArgumentException("Unknown protocol: " + protocol);
    };
  }
}
