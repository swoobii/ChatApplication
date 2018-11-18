package edu.hm.dako.chat.AuditLog;
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

  public AbstractAuditLogListenerThread(Connection con) {

    this.connection = con;
  }

  /**
   * AudiLog-PDU empfangen
   *
   * @return Empfangene AudiLogPDU
   * @throws Exception
   */
  protected AuditLogPDU receive() throws Exception {
    try {
      AuditLogPDU receivedPdu = (AuditLogPDU) connection.receive();
      return receivedPdu;
    } catch (Exception e) {
      ExceptionHandler.logException(e);
    }
    return null;
  }

  /**
   * Aktion zur Behandlung ankommender ChatMessageEvents.
   *
   * @param receivedPdu
   *          Ankommende PDU
   */
  protected abstract void chatMessageResponseAction(AuditLogPDU receivedPdu);

  /**
   * Aktion zur Behandlung ankommender Logout-Events.
   *
   * @param receivedPdu
   *          Ankommende PDU
   */
  protected abstract void logoutResponseAction(AuditLogPDU receivedPdu);

  /**
   * Aktion zur Behandlung ankommender Login-Events.
   *
   * @param receivedPdu
   *          Ankommende PDU
   */

  protected abstract void loginResponseAction(AuditLogPDU receivedPdu);


}
