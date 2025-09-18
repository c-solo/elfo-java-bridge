package io.github.csolo.network.protocol;

import java.util.List;

// spotless:off
/** Protocol messages for elfo-network internode communication.
 *
 *           control connection
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *      (client)             (server)
 *                  ...
 *      SwitchToControl -->
 *                <-- SwitchToControl
 *                  ...
 *
 *            data connection
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *      (client)             (server)
 *                  ...
 *      SwitchToData -->
 *                   <-- SwitchToData
 *                  ...
 *      UpdateFlow -->
 *                  ...d
 *                     <-- UpdateFlow
 *
 *             any connection
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *                  ...
 *      Ping -->
 *                           <-- Pong
 *                  ...
 */
// spotless:on
public class ProtocolMessages {

  /** SwitchToControl message - exchanged during control connection setup. */
  public record SwitchToControl(List<GroupInfo> groups) {}

  /**
   * Group information for protocol messages.
   *
   * @param group_no Group number.
   * @param name Group name.
   * @param interests Remote group's names that this group is interested in.
   */
  public record GroupInfo(short group_no, String name, List<String> interests) {}

  /**
   * SwitchToData message - exchanged during data connection setup.
   *
   * @param myGroupNo Local group's number of a client.
   * @param yourGroupNo Local group's number of a server.
   * @param initialWindow Initial window size for every flow.
   */
  public record SwitchToData(short myGroupNo, short yourGroupNo, int initialWindow) {}

  /** UpdateFlow message - for flow control. */
  public record UpdateFlow(long addr, int windowDelta) {}

  /** CloseFlow message - for closing flows. */
  public record CloseFlow(long addr) {}

  /** Ping message - for keep-alive. */
  public record Ping(long payload) {}

  /** Pong message - response to ping. */
  public record Pong(long payload) {}
}
