package edu.hm.dako.chat.AuditLog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.connection.Connection;
import edu.hm.dako.chat.common.PduType;
import edu.hm.dako.chat.common.ExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Thread wartet auf ankommende Nachrichten vom Server und bearbeitet diese.
 *
 * @author Swoboda, Lechner, Brosch, Hofstetter
 *
 */
public class AuditLogListenerThreadImpl extends AbstractAuditLogListenerThread {

  private static Log log = LogFactory.getLog(
      edu.hm.dako.chat.AuditLog.AuditLogListenerThreadImpl.class);

  public AuditLogListenerThreadImpl(Connection con) {

    super(con);
  }

  /**
   * Bearbeitung aller vom Server ankommenden Nachrichten
   */
  public void run() {

    log.debug("SimpleMessageListenerThread gestartet");

    while (!finished) {

      try {
        // Naechste ankommende Nachricht empfangen
        log.debug("Auf die naechste Nachricht vom Server warten");
        AuditLogPDU receivedPdu =(AuditLogPDU) connection.receive();
        log.debug("Nach receive Aufruf, ankommende PDU mit PduType = "
            + receivedPdu.getPduType());
      } catch (Exception e) {
        finished = true;
      }
    }

    try {
      connection.close();
    } catch (Exception e) {
      ExceptionHandler.logException(e);
    }
  }
  /**
   * Erstellen eines .txt Files
   */

  public void createFile() throws IOException {
    File file = new File("C:/Users/" + System.getProperty("user.name") + "/Desktop/AuditLogFile.txt");
    if(!file.exists()) {
      file.createNewFile();
      log.debug("File erzeugt");
    }
  }

  /**
   * Schreiben in .txt File
   */

  public void writeInFile(AuditLogPDU receivedPDU) throws IOException {
    PrintStream insert = new PrintStream(new FileOutputStream("AuditLogFile.txt"));
    insert.print(receivedPDU.getUserName() + receivedPDU.getMessage() + receivedPDU.getPduType() +
    receivedPDU.getClientThreadName() + receivedPDU.getServerThreadName() + receivedPDU.getServerTime());
    insert.close();
  }
}


