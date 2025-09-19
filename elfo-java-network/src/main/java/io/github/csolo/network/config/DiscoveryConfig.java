package io.github.csolo.network.config;

import java.time.Duration;
import java.util.List;

/**
 * Discovery configuration for elfo-network.
 *
 * @param predefined List of nodes to discover.
 * @param attemptInterval Interval between discovery attempts.
 */
public record DiscoveryConfig(List<Transport> predefined, Duration attemptInterval) {

  public DiscoveryConfig(List<Transport> predefined) {
    this(predefined, Duration.ofSeconds(10));
  }
}
