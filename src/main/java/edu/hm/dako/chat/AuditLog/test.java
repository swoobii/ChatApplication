package edu.hm.dako.chat.AuditLog;
import edu.hm.dako.chat.client.SharedClientData;
import java.net.InetAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.connection.Connection;
import edu.hm.dako.chat.connection.ServerSocketInterface;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import java.io.PrintWriter;

public class test {

  private static ServerSocketInterface socket;
  private static Connection connection;
  private static boolean finished = false;
  private static PrintWriter pr;
  private AuditLogListenerThreadImpl auditThread = new AuditLogListenerThreadImpl(connection);

  public static void main(String[] args) throws Exception {
    if (args[0] != null && args[0].equals("UDP")) {
      try {
        socket = new UdpServerSocket(50001);
        System.out.println("Gestartet als UDP");
      } catch (Exception e) {
        System.out.println("Port 500001 auf dem Rechner schon in Benutzung, Bind Exception: " + e);
        throw e;
      }
      run();
    } else if (args[0] != null && args[0].equals("TCP")) {
      try {
        socket = new TcpServerSocket(50001, 1804, 1804);
        System.out.println("Gestartet als TCP mit " + InetAddress.getLocalHost().getHostAddress());
      } catch (Exception e) {
        System.out.println("Port 500001 auf dem Rechner schon in Benutzung, Bind Exception: " + e);
        throw e;
      }
      run();
    } else {
      System.out.println("Invalid argument")
    }

    // HIER FEHLT WAS !!
    public static void ankommendeNachrichtBearbeiten(AuditLogPDU received){

      if (received != null) {
        switch (received.getType()) {
          case LOGIN_EVENT: {
            pr.println("Login" + received.toString());
            pr.flush();
          }
          break;

          case CHAT_MESSAGE_EVENT: {
            System.out.println("Message " + received.toString());
            pr.println("Message " + received.toString());
            pr.flush();
          }
          break;

          case LOGOUT_EVENT: {
            pr.println("Logout " + received.toString());
            pr.flush();
          }
          break;

          case SHUTDOWN_REQUEST: {

          }
        }
      }
    }
  }
}