package io.github.csolo.core.tracing;

import io.github.csolo.core.NodeNo;
import java.util.concurrent.atomic.AtomicLong;

/** Generator for creating new TraceIds. Thread-safe singleton implementation. */
public class TraceIdGenerator {

  private static final TraceIdGenerator INSTANCE = new TraceIdGenerator();

  private final AtomicLong chunkRegistry = new AtomicLong(0);
  private volatile int currentChunk = 0;
  private volatile int counter = 0x3FF; // Start at max to trigger chunk refresh

  private TraceIdGenerator() {}

  /** Gets the singleton instance. */
  public static TraceIdGenerator getInstance() {
    return INSTANCE;
  }

  /** Generates a new TraceId using the current node number. */
  public TraceId generate() {
    return generate(NodeNo.getNodeNo());
  }

  // spotless:off
  /**
   * Generates a new TraceId with specific node number.
   * Structure (64 bits):
   * - 1 bit 0 (zero)
   * - 25 bits timestamp in secs
   * - 16 bits node_no
   * - 12 bits (chunk_no & 0xfff)
   * - 10 bits counter
   */
  // spotless:on
  public synchronized TraceId generate(short nodeNo) {
    if (counter >= 0x3FF) {
      // if chunk is exhausted, inc chuck and reset counter
      currentChunk = getNextChunk();
      counter = 0;
    }

    counter++;

    // Build bottom part: chunk (12 bits) + counter (10 bits)
    long bottom = ((long) currentChunk << 10) | counter;
    long timestamp = nowInSecTruncatedTo25Bits();

    return new TraceId((timestamp << 38) | ((long) nodeNo << 22) | bottom);
  }

  /** Increments chunk and truncated it to 12 bits */
  private int getNextChunk() {
    return (int) (chunkRegistry.getAndIncrement() & 0xFFF);
  }

  /** Gets current time truncated to 25 bits (seconds). */
  private long nowInSecTruncatedTo25Bits() {
    return (System.currentTimeMillis() / 1000) & 0x1FF_FFFFL;
  }
}
