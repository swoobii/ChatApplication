package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import org.apache.log4j.PropertyConfigurator;

/**
 * Klasse zum Ausführen des Audit Server
 *
 * @author Swoboda
 */
class AuditServerExecution {

  private TcpServerSocket socket;
  private TcpConnection connection;
  private AuditLogListenerThreadImpl auditlistener;

  public static void main(String[] args) {
    PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);

    System.out.println("AuditlogServer gestartet");
    AuditServerExecution server = new AuditServerExecution();

    try {
      server.openServerSocket();
      server.startThread();
    } catch (Exception e) {
      System.out.println("Server wird beendet");
    }
  }

    public void openServerSocket() throws Exception {
      try {
        socket = new TcpServerSocket(40001, 30000, 100000);
      } catch (Exception e) {
        System.out.println("Fehler");
      }
    }
    public void startThread() throws Exception {
      try {
        connection = (TcpConnection) socket.accept();
        auditlistener = new AuditLogListenerThreadImpl(connection);
        auditlistener.start();
      } catch (Exception e) {
        System.out.println("Fehler");
      }
    }
    public void closeConnection() throws Exception {
      try {
        socket.close();
      } catch (Exception e) {
        System.out.println("Fehler");
      }
  }
}

