
package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpConnectionFactory;

/**
 *  Klasse mit Funkitonen zum verbinden, senden und schließen in der TCP
 *  Auditlogverbindung
 *
 * @author Swoboda, Brosch, Lechner, Hofstetter
 *
 */

public class AuditLogConnectionTcp {
  private TcpConnection AuditConnection;

  /**
   *   Verbinden des Chatserver mit den Auditlogserver mithilfe von TCP.
   */


  public void connectAudit() throws Exception{
    try{
      AuditConnection = (TcpConnection) new
          TcpConnectionFactory().connectToServer("localhost",40001,0,30000,100000);
    } catch(Exception e) {
      throw new Exception();
    }
  }

  /**
   *  Senden der AuditlogPDU mithilfe von TCP
  */
  public synchronized void send(AuditLogPDU pdu) throws Exception{
    try {
      AuditConnection.send(pdu);
    } catch (Exception e) {
      throw new Exception();
    }
  }

  /**
   *   Schließen der TCP Verbindung
   */


  public void close() throws Exception{
    try {
      AuditConnection.close();
    } catch (Exception e) {
      System.out.println("Fehler");
    } throw new Exception();
  }
}

