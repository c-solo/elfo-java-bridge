package io.github.csolo.core;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the global, usually unique address of an actor or a group.
 *
 * <p>Structure (64-bit): 64 48 40 30 21 0
 * +------------+----------+------------+-------+-----------------+ | node_no | group_no |
 * generation | TID | page + offset | | 16b | 8b | 10b | 9b | 21b |
 * +------------+----------+------------+-------+-----------------+ (0 if local) ^----------- slot
 * key (40b) -----------^
 *
 * <p>Uniqueness: - Alive actors on the same node always have different addresses - Actors in
 * different nodes have different address spaces - Actors in different groups have different address
 * spaces - An address includes the version number to guard against the ABA problem - An address is
 * randomized between restarts of the same node if network feature is enabled
 */
public record Addr(long value) {

  private static final long NODE_NO_SHIFT = 48;
  private static final long GROUP_NO_SHIFT = 40;
  private static final long NODE_NO_MASK = (1L << NODE_NO_SHIFT) - 1;
  private static final long GROUP_NO_MASK = (1L << GROUP_NO_SHIFT) - 1;

  /** NULL address constant. */
  public static final Addr NULL = new Addr(0);

  /**
   * Creates a local address.
   *
   * @param slotKey The slot key (40 bits)
   * @param groupNo The group number
   * @param launchId The launch ID for randomization
   * @return Addr instance
   */
  public static Addr newLocal(long slotKey, GroupNo groupNo, NodeLaunchId launchId) {
    if (slotKey >= (1L << GROUP_NO_SHIFT)) {
      throw new IllegalArgumentException("Slot key too large");
    }

    // XOR with launch ID for network randomization
    long randomizedSlotKey = (slotKey ^ launchId.intoBits()) & GROUP_NO_MASK;
    return newLocalInner(randomizedSlotKey, groupNo);
  }

  private static Addr newLocalInner(long slotKey, GroupNo groupNo) {
    long addr = ((long) (groupNo.intoBits() & 0xFF) << GROUP_NO_SHIFT) | slotKey;
    return new Addr(addr);
  }

  /** Creates an Addr from raw bits. */
  public static Option<Addr> fromBits(long bits) {
    Addr addr = new Addr(bits);
    if (addr.isNull() ^ addr.groupNo().isDefined()) {
      return Option.of(addr);
    }
    return Option.none();
  }

  /** Gets the raw 64-bit value. */
  public long intoBits() {
    return value;
  }

  /** Checks if this is a NULL address. */
  public boolean isNull() {
    return this == NULL;
  }

  /** Checks if this is a local address. */
  public boolean isLocal() {
    return !isNull() && nodeNo().isEmpty();
  }

  /** Checks if this is a remote address. */
  public boolean isRemote() {
    return nodeNo().isDefined();
  }

  /** Gets the node number if present. */
  public Option<NodeNo> nodeNo() {
    long nodeBits = (value >> NODE_NO_SHIFT) & 0xFFFF;
    return Try.of(() -> NodeNo.fromBits((short) nodeBits)).toOption();
  }

  /** Gets the group number if present. */
  public Option<GroupNo> groupNo() {
    long groupBits = (value >> GROUP_NO_SHIFT) & 0xFF;
    return Try.of(() -> GroupNo.fromBits((byte) groupBits)).toOption();
  }

  /** Gets the node and group number combined (for network use). */
  public long nodeNoGroupNo() {
    return (value >> GROUP_NO_SHIFT);
  }

  /** Gets the slot key for the given launch ID. */
  public long slotKey(NodeLaunchId launchId) {
    // XOR the whole address with launch ID
    return (value ^ launchId.intoBits()) & GROUP_NO_MASK;
  }

  /**
   * Converts this address to a remote address.
   *
   * @param nodeNo The target node number
   * @return Remote address
   */
  public Addr intoRemote(NodeNo nodeNo) {
    if (isLocal()) {
      long remoteValue = value | ((long) (nodeNo.intoBits() & 0xFFFF) << NODE_NO_SHIFT);
      return new Addr(remoteValue);
    }
    return this;
  }

  /** Converts this address to a local address by removing the node number. */
  public Addr intoLocal() {
    return new Addr(value & NODE_NO_MASK);
  }

  @Override
  @NotNull
  public String toString() {
    if (isNull()) {
      return "null";
    }

    Option<GroupNo> groupNo = groupNo();
    if (groupNo.isEmpty()) {
      return "null";
    }
    var groupNoBits = groupNo.get().intoBits();

    long bottom = value & GROUP_NO_MASK;
    Option<NodeNo> nodeNo = nodeNo();

    if (nodeNo.isEmpty()) {
      return String.format("%d/%d", groupNoBits, bottom);
    } else {
      var nodeNoBits = nodeNo.get().intoBits();
      return String.format("%d/%d/%d", nodeNoBits, groupNoBits, bottom);
    }
  }
}
