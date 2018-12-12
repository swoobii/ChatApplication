package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpConnectionFactory;

public class AuditLogConnection {
  private TcpConnection AuditConnection;

  public void connectAudit() throws Exception{
    try{
      AuditConnection = (TcpConnection) new
          TcpConnectionFactory().connectToServer("localhost",40001,0,40000,40000);
    } catch(Exception e) {
      throw new Exception();
    }
  }

  public void send(AuditLogPDU pdu) throws Exception{
    try {
      AuditConnection.send(pdu);
    } catch (Exception e) {
      System.out.println("Fehler");
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
