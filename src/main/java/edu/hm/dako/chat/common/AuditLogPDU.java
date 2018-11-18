package edu.hm.dako.chat.common;

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

  public void setServerTime(long time) {
    this.serverTime = time;
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
   * Erzeugen einer Login-Response-PDU
   *
   * @param eventInitiator Urspruenglicher Client, der Login-Request-PDU gesendet hat
   * @param receivedPdu Empfangene PDU
   * @return Erzeugte PDU
   */
  public static AuditLogPDU createLoginResponsePdu(String eventInitiator,
      AuditLogPDU receivedPdu) {

    AuditLogPDU pdu = new AuditLogPDU();
    pdu.setPduType(PduType.LOGIN_RESPONSE);
    pdu.setServerThreadName(Thread.currentThread().getName());
    pdu.setClientThreadName(receivedPdu.getClientThreadName());
    pdu.setUserName(eventInitiator);
    return pdu;
  }

  /**
   * Erzeugen einer Logout-Response-PDU
   *
   * @param eventInitiator Urspruenglicher Client, der Logout-Request-PDU gesendet hat
   * @param clientThreadName Name des Client-Threads
   * @return Aufgebaute ChatPDU
   */
  public static AuditLogPDU createLogoutResponsePdu(String eventInitiator,
      String clientThreadName) {

    AuditLogPDU pdu = new AuditLogPDU();
    pdu.setPduType(PduType.LOGOUT_RESPONSE);
    pdu.setServerThreadName(Thread.currentThread().getName());
    pdu.setClientThreadName(clientThreadName);
    pdu.setUserName(eventInitiator);
    return pdu;
  }

  /**
   * Erzeugen einer Chat-Message-Response-PDU
   *
   * @param eventInitiator Urspruenglicher Client, der Chat-Message-Request-PDU gesendet hat
   * @param serverTime Requestbearbeitungszeit im Server
   * @return Erzeugte PDU
   */
  public static AuditLogPDU createChatMessageResponsePdu(String eventInitiator,
      String clientThreadName, long serverTime) {

    AuditLogPDU pdu = new AuditLogPDU();
    pdu.setPduType(PduType.CHAT_MESSAGE_RESPONSE);
    pdu.setServerThreadName(Thread.currentThread().getName());

    pdu.setClientThreadName(clientThreadName);
    pdu.setUserName(eventInitiator);

    // Serverbearbeitungszeit
    pdu.setServerTime(serverTime);
    return pdu;
  }
}
