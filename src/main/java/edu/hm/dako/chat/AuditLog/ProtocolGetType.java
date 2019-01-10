package edu.hm.dako.chat.AuditLog;

/**
 * Erstellen eines getter für TCP / UDP.
 *
 * @author Swoboda, Lechner, Brosch, Hofstetter
 */

public class ProtocolGetType {

  private static boolean udp;
  private static boolean tcp;
  private static boolean close;

  public void setClose(boolean c) {
    close = c;
  }

  public boolean getClose() {
    return close;
  }

  /**
   * Konstruktor für den  TCP / UDP Getter
   *
   * @param udp
   *
   * @para tcp
   */


  public ProtocolGetType(boolean udp, boolean tcp) {
    ProtocolGetType.udp = udp;
    ProtocolGetType.tcp = tcp;
  }

  /**
   * UDP getter
   *
   * @return udp
   */

  public static boolean getUDP(){
    return udp;
  }

  /**
   * TCP Getter
   *
   * @return tcp
   */

  public static boolean getTCP(){
    return tcp;
  }

}
