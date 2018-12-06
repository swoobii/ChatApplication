package edu.hm.dako.chat.AuditLog;
import edu.hm.dako.chat.client.SharedClientData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.connection.Connection;

public class test {
  private static Serversocketinterface socket;
  private static Connection connection;
  private static boolean finished = false;
  private static PrintWriter pw;

  public static void main(String[] args) throws Exception {
    if (args[0] != null && args[0].equals("UDP")) {
      try {
        socket = new UdpServerSocket(port:50001);
        System.out.println("Gestartet als UDP");
      } catch (BlindException e) {
        System.out.println("Port 500001 auf dem Rechner schon in Benutzung, Bind Exception: " + e);
        throw e;
      }
      start();
    } else if (args[0] != null && args[0].equals("TCP")) {
      try {
        socket = new TcpServerSocket(port:50001, sendBufferSize:1804, receiveBufferSize:1804);
        System.out.println("Gestartet als TCP mit " + InetAdress.getLocalHost().getHostAdress());
      } catch (BlindException e) {
        System.out.println("Port 500001 auf dem Rechner schon in Benutzung, Bind Exception: " + e);
        throw e;
      }
      start();
    } else {
      System.out.prtinln("Invalid argument")
    }

    // HIER FEHLT WAS !!
    public static void ankommendeNachrichtBearbeiten(AuditPDU received)

    if (received != null) {
      switch (received.getType()) {
        case LOGIN_EVENT: {
          pw.println("Login" + received.toString());
          pw.flush();
        }
        break;

        case CHAT_MESSAGE_EVENT: {
          System.out.println("Message " + received.toString());
          pw.println("Message " + received.toString());
          pw.flush();
        }
        break;

        case LOGOUT_EVENT: {
          pw.println("Logout " + received.toString());
          pw.flush();
        }
        break;

        case SHUTDOWN_REQUEST: {
          
        }
      }
    }
  }
}