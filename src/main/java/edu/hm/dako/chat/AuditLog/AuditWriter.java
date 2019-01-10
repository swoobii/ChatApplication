package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.common.AuditLogPDU;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Erstellen der .txt Datei und schreiben in diese.
 *
 * @author Swoboda, Lechner, Brosch, Hofstetter
 */

public class AuditWriter {

  private static Log log = LogFactory.getLog(AuditWriter.class);

  /**
   * Schreiben in .txt File
   */

  public void writeInFile(AuditLogPDU receivedPDU) throws IOException {
    PrintWriter insert = new PrintWriter(new FileOutputStream("AuditLogFile.txt",true),false);
    System.out.println(receivedPDU.toString());
    insert.print(receivedPDU.toString());
    insert.close();
  }

  /**
   * Shutdown Nachricht und AuditLogServer schließen
   */


  public void shutdownMessage() throws IOException {
    PrintWriter insert = new PrintWriter(new FileOutputStream("AuditLogFile.txt",true),false);
    insert.print("+++++SHUTDOWN+++++");
    insert.close();
  }
}
