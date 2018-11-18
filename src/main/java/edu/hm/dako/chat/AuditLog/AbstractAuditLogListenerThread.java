package edu.hm.dako.chat.AuditLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.ChatPDU;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.connection.Connection;

/**
 *  Abstrakte Klasse mit Basisfunktionalitaet fuer serverseitige
 *  AudiLog-Threads
 * @author Swoboda, Brosch, Lechner, Hofstetter
 *
 */


public class AbstractAuditLogListenerThread extends Thread {

  private static Log log = LogFactory.getLog(AbstractAuditLogListenerThread.class);

  // Kennzeichen zum Beenden der Bearbeitung
  protected boolean finished = false;

  // Verbindung zum Server
  protected Connection connection;


  // Gemeinsame Daten zwischen Client-Thread und Message-Processing-Thread
  protected SharedClientData sharedClientData;

  /**
   * Event vom Server zur Veraenderung der UserListe (eingeloggte Clients)
   * verarbeiten
   *
   * @param receivedPdu
   *          Empfangene PDU
   */


  public AbstractMessageListenerThread(Connection con,
      SharedClientData sharedData) {

    this.connection = con;
    this.sharedClientData = sharedData;
  }

  /**
   * AudiLog-PDU empfangen
   *
   * @return Empfangene AudiLogPDU
   * @throws Exception
   */
  protected ChatPDU receive() throws Exception {
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
