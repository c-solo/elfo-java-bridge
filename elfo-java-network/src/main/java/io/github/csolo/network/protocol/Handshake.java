package io.github.csolo.network.protocol;

import io.vavr.control.Try;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// spotless:off
/**
 * Handshake protocol for elfo-network connections.
 *
 * ASCII Format (39 bytes total):
 *
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Magic Number (8 bytes)                     |
 * |                    0xE1F0E1F0E1F0E1F0                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Version|     Node Number (2 bytes)     |    Launch ID (8 bytes)|
 * |  (1)  |                               |                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Launch ID (cont.)                          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    Capabilities (4 bytes)     |        Reserved (16 bytes)    |
 * |                               |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Reserved (cont.)                           |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * Field descriptions:
 * - Magic Number: Identifies elfo-network handshake (0xE1F0E1F0E1F0E1F0)
 * - Version: Protocol version (currently 0)
 * - Node Number: Unique identifier for this node
 * - Launch ID: Unique identifier for this node instance
 * - Capabilities: Node capabilities bitmask
 * - Reserved: Reserved for future use (must be zero)
 */
// spotless:on
public class Handshake {
  public static final long HANDSHAKE_MAGIC = 0xE1F0E1F0E1F0E1F0L;
  public static final int HANDSHAKE_LENGTH = 39;
  public static final byte THIS_NODE_VERSION = 0;

  private final byte version;
  private final short nodeNo;
  private final long launchId;
  private final int capabilities;

  public Handshake(int nodeNo, long launchId, int capabilities) {
    this(THIS_NODE_VERSION, (short) nodeNo, launchId, capabilities);
  }

  public Handshake(byte version, short nodeNo, long launchId, int capabilities) {
    assert nodeNo > 0 : "Node number must be positive";
    assert launchId > 0 : "Launch ID must be positive";

    this.version = version;
    this.nodeNo = nodeNo;
    this.launchId = launchId;
    this.capabilities = capabilities;
  }

  /** Serializes handshake to bytes for sending over socket. */
  public byte[] toBytes() {
    ByteBuffer buffer = ByteBuffer.allocate(HANDSHAKE_LENGTH);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    buffer.putLong(HANDSHAKE_MAGIC);
    buffer.put(version);
    buffer.putShort(nodeNo);
    buffer.putLong(launchId);
    buffer.putInt(capabilities);

    var result = buffer.array();
    assert result.length == HANDSHAKE_LENGTH;
    return result;
  }

  /** Parses handshake from bytes from socket. */
  public static Try<Handshake> fromBytes(byte[] data) {
    return Try.of(() -> fromBytesImpl(data));
  }

  private static Handshake fromBytesImpl(byte[] data) {
    assert data != null : "Data cannot be null";

    if (data.length < HANDSHAKE_LENGTH) {
      throw new IllegalArgumentException(
          String.format(
              "Expected handshake of length %d, got %d instead", HANDSHAKE_LENGTH, data.length));
    }

    ByteBuffer buffer = ByteBuffer.wrap(data);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    long magic = buffer.getLong();
    if (magic != HANDSHAKE_MAGIC) {
      throw new IllegalArgumentException("Handshake magic did not match");
    }

    byte version = buffer.get();
    short nodeNo = buffer.getShort();
    long launchId = buffer.getLong();
    int capabilities = buffer.getInt();

    return new Handshake(version, nodeNo, launchId, capabilities);
  }

  public byte getVersion() {
    return version;
  }

  public short getNodeNo() {
    return nodeNo;
  }

  public long getLaunchId() {
    return launchId;
  }

  public int getCapabilities() {
    return capabilities;
  }

  @Override
  public String toString() {
    return String.format(
        "Handshake{version=%d, nodeNo=%d, launchId=%d, capabilities=%d}",
        version, nodeNo, launchId, capabilities);
  }
}
