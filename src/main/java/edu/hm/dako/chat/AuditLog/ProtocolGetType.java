package edu.hm.dako.chat.AuditLog;

public class ProtocolGetType {

  private static boolean udp;
  private static boolean tcp;

  public ProtocolGetType(boolean tcp, boolean udp) {
    this.tcp = tcp;
    this.udp = udp;
  }
  public static boolean getUDP(){
    boolean isUDP = udp;
    return isUDP;
  }

  public static boolean getTCP(){
    boolean isTCP = tcp;
    return isTCP;
  }

}
