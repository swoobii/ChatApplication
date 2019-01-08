package edu.hm.dako.chat.AuditLog;

public class ProtocolGetType {

  private static boolean udp;
  private static boolean tcp;

  public ProtocolGetType(boolean udp, boolean tcp) {
    this.udp = udp;
    this.tcp = tcp;
  }
  public static boolean getUDP(){
    boolean isUDP = udp;
    return true;
  }

  public static boolean getTCP(){
    boolean isTCP = tcp;
    return false;
  }

}
