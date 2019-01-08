package edu.hm.dako.chat.AuditLog;

public class ProtocolGetType {

  private static boolean udp;
  private static boolean tcp;

  public ProtocolGetType(boolean udp, boolean tcp) {
    ProtocolGetType.udp = udp;
    ProtocolGetType.tcp = tcp;
  }
  public static boolean getUDP(){
    return udp;
  }

  public static boolean getTCP(){
    return tcp;
  }

}
