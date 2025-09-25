package io.github.csolo.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the node number and manages the current node number.
 *
 * <p>Cannot be 0, it's reserved to represent {Addr.NULL} unambiguously. Also provides singleton
 * functionality for managing the current node number.
 */
public record NodeNo(short value) {

  private static final AtomicInteger CURRENT_NODE_NO = new AtomicInteger(0);

  /**
   * Creates a NodeNo from raw short value.
   *
   * @throws IllegalArgumentException if bits is zero
   */
  public static NodeNo fromBits(short bits) {
    if (bits == 0) {
      throw new IllegalArgumentException("Node number cannot be zero");
    }
    return new NodeNo(bits);
  }

  /** Returns NodeNo as raw short. */
  public short intoBits() {
    return value;
  }

  /**
   * Gets the current node number.
   *
   * @throws IllegalStateException if node number is not set (0)
   */
  public static short getNodeNo() {
    int nodeNo = CURRENT_NODE_NO.get();
    if (nodeNo == 0) {
      throw new IllegalStateException("Node number cannot be zero");
    }

    return (short) nodeNo;
  }

  /**
   * Sets the current node number.
   *
   * @throws IllegalArgumentException if nodeNo is 0 or negative
   */
  public static void setNodeNo(int nodeNo) {
    if (nodeNo <= 0) {
      throw new IllegalArgumentException("Node number must be positive, got: " + nodeNo);
    }
    CURRENT_NODE_NO.set(nodeNo);
  }

  /** Checks if node number is set. */
  public static boolean isNodeNoSet() {
    return CURRENT_NODE_NO.get() != 0;
  }

  /**
   * Resets the node number to unset state (0). Package-private method for testing purposes only.
   */
  static void resetForTesting() {
    CURRENT_NODE_NO.set(0);
  }
}
