package io.github.csolo.core.node;

import java.util.concurrent.atomic.AtomicInteger;

/** Node number management. */
public class NodeNo {

  private static final AtomicInteger NODE_NO = new AtomicInteger(0);

  /**
   * Gets the current node number.
   *
   * @throws IllegalStateException if node number is not set (0)
   */
  public static short getNodeNo() {
    int nodeNo = NODE_NO.get();
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
    NODE_NO.set(nodeNo);
  }

  /** Checks if node number is set. */
  public static boolean isNodeNoSet() {
    return NODE_NO.get() != 0;
  }

  /**
   * Resets the node number to unset state (0). Package-private method for testing purposes only.
   */
  static void resetForTesting() {
    NODE_NO.set(0);
  }
}
