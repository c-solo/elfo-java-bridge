package io.github.csolo.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Addr Tests")
class AddrTest {

  @Nested
  @DisplayName("Creation Tests")
  class CreationTests {

    @Test
    @DisplayName("Should create local address")
    void shouldCreateLocalAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      long slotKey = 100;

      // When
      Addr addr = Addr.newLocal(slotKey, groupNo, launchId);

      // Then
      assertNotNull(addr);
      assertFalse(addr.isNull());
      assertTrue(addr.isLocal());
      assertFalse(addr.isRemote());
      assertTrue(addr.groupNo().isDefined());
      assertEquals(groupNo, addr.groupNo().get());
      assertTrue(addr.nodeNo().isEmpty());
    }

    @Test
    @DisplayName("Should create address from bits")
    void shouldCreateAddressFromBits() {
      // Given
      long bits = 0x123456789ABCDEF0L;

      // When
      var addrOption = Addr.fromBits(bits);

      // Then
      assertTrue(addrOption.isDefined());
      Addr addr = addrOption.get();
      assertEquals(bits, addr.intoBits());
    }

    @Test
    @DisplayName("Should reject invalid address from bits")
    void shouldRejectInvalidAddressFromBits() {
      // Given - address with group_no = 0 but not NULL
      long invalidBits = 1L; // group_no = 0, but value != 0

      // When
      var addrOption = Addr.fromBits(invalidBits);

      // Then
      assertTrue(addrOption.isEmpty());
    }

    @Test
    @DisplayName("Should reject slot key that is too large")
    void shouldRejectSlotKeyThatIsTooLarge() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      long tooLargeSlotKey = 1L << 40; // Too large for 40-bit field

      // When & Then
      assertThrows(
          IllegalArgumentException.class, () -> Addr.newLocal(tooLargeSlotKey, groupNo, launchId));
    }
  }

  @Nested
  @DisplayName("Type Tests")
  class TypeTests {

    @Test
    @DisplayName("Should identify NULL address")
    void shouldIdentifyNullAddress() {
      // Given
      Addr nullAddr = Addr.NULL;

      // When & Then
      assertTrue(nullAddr.isNull());
      assertFalse(nullAddr.isLocal());
      assertFalse(nullAddr.isRemote());
      assertTrue(nullAddr.groupNo().isEmpty());
      assertTrue(nullAddr.nodeNo().isEmpty());
      assertEquals("null", nullAddr.toString());
    }

    @Test
    @DisplayName("Should identify local address")
    void shouldIdentifyLocalAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);

      // When & Then
      assertFalse(localAddr.isNull());
      assertTrue(localAddr.isLocal());
      assertFalse(localAddr.isRemote());
    }

    @Test
    @DisplayName("Should identify remote address")
    void shouldIdentifyRemoteAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr remoteAddr = localAddr.intoRemote(nodeNo);

      // When & Then
      assertFalse(remoteAddr.isNull());
      assertFalse(remoteAddr.isLocal());
      assertTrue(remoteAddr.isRemote());
    }
  }

  @Nested
  @DisplayName("Conversion Tests")
  class ConversionTests {

    @Test
    @DisplayName("Should convert local to remote address")
    void shouldConvertLocalToRemoteAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);

      // When
      Addr remoteAddr = localAddr.intoRemote(nodeNo);

      // Then
      assertTrue(remoteAddr.isRemote());
      assertFalse(remoteAddr.isLocal());
      assertTrue(remoteAddr.nodeNo().isDefined());
      assertEquals(nodeNo, remoteAddr.nodeNo().get());
      assertTrue(remoteAddr.groupNo().isDefined());
      assertEquals(groupNo, remoteAddr.groupNo().get());
    }

    @Test
    @DisplayName("Should convert remote to local address")
    void shouldConvertRemoteToLocalAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr remoteAddr = localAddr.intoRemote(nodeNo);

      // When
      Addr backToLocal = remoteAddr.intoLocal();

      // Then
      assertTrue(backToLocal.isLocal());
      assertFalse(backToLocal.isRemote());
      assertTrue(backToLocal.groupNo().isDefined());
      assertEquals(groupNo, backToLocal.groupNo().get());
      assertTrue(backToLocal.nodeNo().isEmpty());
    }

    @Test
    @DisplayName("Should not change already remote address")
    void shouldNotChangeAlreadyRemoteAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo1 = NodeNo.fromBits((short) 42);
      NodeNo nodeNo2 = NodeNo.fromBits((short) 43);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr remoteAddr = localAddr.intoRemote(nodeNo1);

      // When
      Addr stillRemote = remoteAddr.intoRemote(nodeNo2);

      // Then
      assertEquals(remoteAddr, stillRemote); // Should not change
      assertTrue(stillRemote.nodeNo().isDefined());
      assertEquals(nodeNo1, stillRemote.nodeNo().get()); // Should keep original node
    }
  }

  @Nested
  @DisplayName("Bit Operations Tests")
  class BitOperationsTests {

    @Test
    @DisplayName("Should extract node number correctly")
    void shouldExtractNodeNumberCorrectly() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr remoteAddr = localAddr.intoRemote(nodeNo);

      // When & Then
      assertTrue(remoteAddr.nodeNo().isDefined());
      assertEquals(nodeNo, remoteAddr.nodeNo().get());
      assertTrue(localAddr.nodeNo().isEmpty());
    }

    @Test
    @DisplayName("Should extract group number correctly")
    void shouldExtractGroupNumberCorrectly() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 5);
      NodeLaunchId launchId = NodeLaunchId.generate();
      Addr addr = Addr.newLocal(100, groupNo, launchId);

      // When & Then
      assertTrue(addr.groupNo().isDefined());
      assertEquals(groupNo, addr.groupNo().get());
    }

    @Test
    @DisplayName("Should calculate node and group number combined")
    void shouldCalculateNodeAndGroupNumberCombined() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 5);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr remoteAddr = localAddr.intoRemote(nodeNo);

      // When
      long nodeGroupNo = remoteAddr.nodeNoGroupNo();

      // Then
      long expected = ((long) nodeNo.intoBits() << 8) | groupNo.intoBits();
      assertEquals(expected, nodeGroupNo);
    }

    @Test
    @DisplayName("Should calculate slot key correctly")
    void shouldCalculateSlotKeyCorrectly() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      long originalSlotKey = 100;
      Addr addr = Addr.newLocal(originalSlotKey, groupNo, launchId);

      // When
      long slotKey = addr.slotKey(launchId);

      // Then
      assertEquals(originalSlotKey, slotKey);
    }
  }

  @Nested
  @DisplayName("Serialization Tests")
  class SerializationTests {

    @Test
    @DisplayName("Should serialize and deserialize address")
    void shouldSerializeAndDeserializeAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      Addr original = Addr.newLocal(100, groupNo, launchId);

      // When
      long bits = original.intoBits();
      var restoredOption = Addr.fromBits(bits);

      // Then
      assertTrue(restoredOption.isDefined());
      Addr restored = restoredOption.get();
      assertEquals(original, restored);
    }

    @Test
    @DisplayName("Should serialize and deserialize remote address")
    void shouldSerializeAndDeserializeRemoteAddress() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 1);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr original = localAddr.intoRemote(nodeNo);

      // When
      long bits = original.intoBits();
      var restoredOption = Addr.fromBits(bits);

      // Then
      assertTrue(restoredOption.isDefined());
      Addr restored = restoredOption.get();
      assertEquals(original, restored);
    }
  }

  @Nested
  @DisplayName("ToString Tests")
  class ToStringTests {

    @Test
    @DisplayName("Should format local address correctly")
    void shouldFormatLocalAddressCorrectly() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 5);
      NodeLaunchId launchId = NodeLaunchId.generate();
      Addr addr = Addr.newLocal(100, groupNo, launchId);

      // When
      String formatted = addr.toString();

      // Then
      assertTrue(formatted.startsWith("5/"));
      // The bottom part is XORed with launchId, so we can't predict the exact value
      assertTrue(formatted.matches("5/\\d+"));
    }

    @Test
    @DisplayName("Should format remote address correctly")
    void shouldFormatRemoteAddressCorrectly() {
      // Given
      GroupNo groupNo = GroupNo.fromBits((byte) 5);
      NodeLaunchId launchId = NodeLaunchId.generate();
      NodeNo nodeNo = NodeNo.fromBits((short) 42);
      Addr localAddr = Addr.newLocal(100, groupNo, launchId);
      Addr remoteAddr = localAddr.intoRemote(nodeNo);

      // When
      String formatted = remoteAddr.toString();

      // Then
      assertTrue(formatted.startsWith("42/"));
      assertTrue(formatted.contains("5/"));
      // The bottom part is XORed with launchId, so we can't predict the exact value
      assertTrue(formatted.matches("42/5/\\d+"));
    }
  }
}
