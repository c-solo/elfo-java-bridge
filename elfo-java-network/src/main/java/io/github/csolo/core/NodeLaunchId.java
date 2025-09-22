package io.github.csolo.core;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomly generated identifier at the node start.
 *
 * <p>Used for several purposes:
 *
 * <ul>
 *   <li>To distinguish between different launches of the same node
 *   <li>To detect reusing of the same node no
 *   <li>To improve Addr uniqueness in the cluster
 * </ul>
 */
public record NodeLaunchId(long value) {

  /** Generates a new random NodeLaunchId. */
  public static NodeLaunchId generate() {
    // In Rust: RandomState with seed 0xE1F0E1F0E1F0E1F0
    // We'll use ThreadLocalRandom for simplicity
    long seed = 0xE1F0E1F0E1F0E1F0L;
    ThreadLocalRandom random = ThreadLocalRandom.current();

    // Generate a random value based on the seed
    long randomValue = random.nextLong();
    return new NodeLaunchId(randomValue ^ seed);
  }

  /** Creates a NodeLaunchId from raw bits. */
  public static NodeLaunchId fromBits(long bits) {
    return new NodeLaunchId(bits);
  }

  /** Gets the raw 64-bit value. */
  public long intoBits() {
    return value;
  }
}
