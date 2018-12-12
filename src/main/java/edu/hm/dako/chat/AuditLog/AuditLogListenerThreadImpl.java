package edu.hm.dako.chat.AuditLog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.connection.Connection;


/**
 * Thread wartet auf ankommende Nachrichten vom Server und bearbeitet diese.
 *
 * @author Swoboda, Lechner, Brosch, Hofstetter
 *
 */
public class AuditLogListenerThreadImpl extends AbstractAuditLogListenerThread {

  private static Log log = LogFactory.getLog(
      AuditLogListenerThreadImpl.class);
  private Connection connection;


  public AuditLogListenerThreadImpl(Connection con) {

    //super(con);
    this.connection = con;
  }

  /**
   * Bearbeitung aller vom Server ankommenden Nachrichten
   */
  public void run() {
    AuditWriter writer = new AuditWriter();
    try {
      writer.createFile();
    } catch (Exception e) {
    }

    log.debug("AuditLogListenerThread gestartet");

    while (true) {

      try {
        // Naechste ankommende Nachricht empfangen
        log.debug("Auf die naechste Nachricht vom Server warten");
        AuditLogPDU receivedPdu = (AuditLogPDU) connection.receive();
        System.out.print(receivedPdu.toString());
        writer.writeInFile(receivedPdu);
        log.debug("Nach receive Aufruf, ankommende PDU mit PduType = "
            + receivedPdu.getPduType());
      } catch (Exception e) {
      }
    }
  }
}




