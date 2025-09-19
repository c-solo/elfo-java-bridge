package io.github.csolo.network.config;

import java.time.Duration;

/**
 * Configuration for elfo-network.
 *
 * @param pingInterval Interval between pings to check connection health. Default is 5 seconds.
 * @param discovery Discovery configuration.
 * @param compression Compression configuration.
 */
public record NetworkConfig(
    Duration pingInterval, DiscoveryConfig discovery, CompressionConfig compression) {

  public NetworkConfig(DiscoveryConfig discovery, CompressionConfig compression) {
    this(Duration.ofSeconds(5), discovery, compression);
  }
}
