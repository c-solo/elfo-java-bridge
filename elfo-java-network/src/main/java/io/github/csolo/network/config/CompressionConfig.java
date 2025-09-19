package io.github.csolo.network.config;

/** Compression configuration for elfo-network. */
public record CompressionConfig(CompressionAlgorithm algorithm) {

  public CompressionConfig() {
    this(CompressionAlgorithm.NONE);
  }

  public CompressionConfig withAlgorithm(CompressionAlgorithm algorithm) {
    return new CompressionConfig(algorithm);
  }
}
