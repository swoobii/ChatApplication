package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.PduType;
import edu.hm.dako.chat.udp.UdpSend;

public class ShutDown {


  static boolean isUdp = ProtocolGetType.getUDP();
  static boolean isTcp =  ProtocolGetType.getTCP();

  /**
   * ShutdownPDU erstellen
   */

  public void finish() throws Exception {
    // Shutdownpdu erstellen und an Auditlogserver übermitteln
    System.out.print("1");
    AuditLogPDU auditLogPDU4 = new AuditLogPDU();
    auditLogPDU4.setPduType(PduType.SHUTDOWN);
    if (isTcp) {
//      AuditLogConnectionTcp audit = new AuditLogConnectionTcp();
//      audit.connectAudit();
//      audit.send(auditLogPDU4);

    } else if (isUdp) {
      UdpSend udpSend = new UdpSend();
      udpSend.send(auditLogPDU4);
    }
  }

}
