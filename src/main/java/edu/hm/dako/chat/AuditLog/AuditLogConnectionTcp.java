
package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpConnectionFactory;

public class AuditLogConnectionTcp {
  private TcpConnection AuditConnection;

  public void connectAudit() throws Exception{
    try{
      System.out.println("auditconnection tcp lauft");
      AuditConnection = (TcpConnection) new
          TcpConnectionFactory().connectToServer("localhost",40001,0,40000,40000);
    } catch(Exception e) {
      throw new Exception();
    }
  }

  public void send(AuditLogPDU pdu) throws Exception{
    try {
      AuditConnection.send(pdu);
      System.out.println("pdu an tcp connection");
    } catch (Exception e) {
      System.out.println("Fehler im send Tcp");
      throw new Exception();
    }
  }

  public void close() throws Exception{
    try {
      AuditConnection.close();
    } catch (Exception e) {
      System.out.println("Fehler");
    } throw new Exception();
  }
}

