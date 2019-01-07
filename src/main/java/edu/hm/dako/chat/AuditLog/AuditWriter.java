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
    PrintWriter insert = new PrintWriter(new FileOutputStream("AuditLogFile.txt",true),false);
    insert.print(receivedPDU.toString());
    insert.close();
  }
}
