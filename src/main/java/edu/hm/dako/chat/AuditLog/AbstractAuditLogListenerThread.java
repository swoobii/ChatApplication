package edu.hm.dako.chat.AuditLog;
import edu.hm.dako.chat.client.SharedClientData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.connection.Connection;

/**
 *  Abstrakte Klasse mit Basisfunktionalitaet fuer serverseitige
 *  AudiLog-Threads
 * @author Swoboda, Brosch, Lechner, Hofstetter
 *
 */


abstract class AbstractAuditLogListenerThread extends Thread {

  private static Log log = LogFactory.getLog(AbstractAuditLogListenerThread.class);

  // Kennzeichen zum Beenden der Bearbeitung
  protected boolean finished = false;

  // Verbindung zum Server
  protected Connection connection;

  // Gemeinsame Daten zwischen Client-Thread und Message-Processing-Thread
  protected SharedClientData sharedClientData;

  public AbstractAuditLogListenerThread() {
  }

  public AbstractAuditLogListenerThread(Connection con) {

    this.connection = con;
  }

  /**
   * AuditLog-PDU empfangen
   *
   * @return Empfangene AudiLogPDU
   */
  protected AuditLogPDU receive() throws Exception {

    try {
      AuditLogPDU receivedPdu = (AuditLogPDU) connection.receive();
      return receivedPdu;
    } catch (Exception e) {
      ExceptionHandler.logException(e);
      return null;
    }
  }
}

