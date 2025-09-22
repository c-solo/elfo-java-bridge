package io.github.csolo.core;

/**
 * Represents the actor group's number.
 *
 * <p>Cannot be 0, it's reserved to represent {Addr.NULL} unambiguously. XORed with random
 * NodeLaunchId.
 */
public record GroupNo(byte value) {

  /**
   * Creates a GroupNo from raw byte value.
   *
   * @param bits The raw byte value (must be non-zero)
   * @throws IllegalArgumentException if bits is zero
   */
  public static GroupNo fromBits(byte bits) {
    if (bits == 0) {
      throw new IllegalArgumentException("Group number cannot be zero");
    }
    return new GroupNo(bits);
  }

  /**
   * Creates a GroupNo with network randomization (XOR with launch ID). This corresponds to the
   * network-2 feature in Rust.
   *
   * @param groupNo The group number (must be non-zero)
   * @param launchId The node launch ID for randomization
   * @throws IllegalArgumentException if groupNo is zero
   */
  public static GroupNo newWithNetwork(byte groupNo, NodeLaunchId launchId) {
    if (groupNo == 0) {
      throw new IllegalArgumentException("Group number cannot be zero");
    }

    // XOR with launch ID (shifted right by GROUP_NO_SHIFT)
    long xor = (launchId.intoBits() >> 8) & 0xFF; // GROUP_NO_SHIFT is 8 in Rust

    // no = 0 is forbidden, thus there is no mapping to just xor
    byte no = (byte) (groupNo != xor ? groupNo ^ xor : xor);

    return new GroupNo(no);
  }

  /** Gets the raw byte value. */
  public byte intoBits() {
    return value;
  }
}
