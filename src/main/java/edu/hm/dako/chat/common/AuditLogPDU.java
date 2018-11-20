package edu.hm.dako.chat.common;

import java.util.*;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p/>
 * Nachrichtenaufbau fuer AuditLog-Protokoll (fuer alle Nachrichtentypen: Request,
 * Response, Event, Confirm)
 *
 * @author Swoboda, Brosch, Lechner, Hofstetter
 */
public class AuditLogPDU implements Serializable {

  private static final long serialVersionUID = -6172619032079227585L;
  private static final Log log = LogFactory.getLog(ChatPDU.class);

  // Kommandos bzw. PDU-Typen
  private PduType pduType;

  // Login-Name des Clients
  private String userName;

  // Name des Client-Threads, der den Request absendet
  private String clientThreadName;

  // Name des Threads, der den Request im Server
  private String serverThreadName;

  // Nutzdaten (eigentliche Chat-Nachricht in Textform)
  private String message;

  // Zeit in Nanosekunden, die der Server fuer die komplette Bearbeitung einer
  // Chat-Nachricht benoetigt (inkl. kompletter Verteilung an alle
  // angemeldeten User).
  // Diese Zeit wird vom Server vor dem Absenden der Response eingetragen
  private long serverTime;

  public AuditLogPDU() {
    pduType = PduType.UNDEFINED;
    userName = null;
    clientThreadName = null;
    serverThreadName = null;
    message = null;
    serverTime = 0;
  }

  public AuditLogPDU(PduType cmd) {
    this.pduType = cmd;
  }

  public String toString() {

    return "\n"
        + "ChatPdu ****************************************************************************************************"
        + "\n" + "PduType: " + this.pduType + ", " + "\n" + "userName: " + this.userName
        + ", " + "\n" + "clientThreadName: " + this.clientThreadName + ", " + "\n"
        + "serverThreadName: " + this.serverThreadName + ", " + "\n"
        + "serverTime: " + this.serverTime + ", " + "\n" + "message: " + this.message + "\n"
        + "**************************************************************************************************** ChatPdu"
        + "\n";
  }

  public static void printPdu(AuditLogPDU pdu) {
    // System.out.println(pdu);
    log.debug(pdu);
  }

  public void setPduType(PduType pduType) {
    this.pduType = pduType;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setClientThreadName(String threadName) {
    this.clientThreadName = threadName;
  }

  public void setServerThreadName(String threadName) {
    this.serverThreadName = threadName;
  }

  public void setMessage(String msg) {
    this.message = msg;
  }

  public void setServerTime() {
    Date today = new Date();
    this.serverTime = today.getTime();
  }

  public PduType getPduType() {
    return pduType;
  }

  public String getUserName() {
    return userName;
  }

  public String getClientThreadName() {
    return (clientThreadName);
  }

  public String getServerThreadName() {
    return (serverThreadName);
  }

  public String getMessage() {
    return (message);
  }

  public long getServerTime() {
    return (serverTime);
  }

  /**
   * Erzeugen einer AuditLog-PDU durch übergebene ChatPDU
   *
   * @param receivedPCU
   * @return Erzeugte PDU
   */

  public AuditLogPDU makeAuditLogPDU (ChatPDU receivedPCU) {
    AuditLogPDU auditPDU = null;

    auditPDU.setPduType(receivedPCU.getPduType());
    auditPDU.setUserName(receivedPCU.getUserName());
    auditPDU.setClientThreadName(receivedPCU.getClientThreadName());
    auditPDU.setServerThreadName(receivedPCU.getServerThreadName());
    auditPDU.setMessage(receivedPCU.getMessage());
    auditPDU.setServerTime();

    return auditPDU;
  }
}
