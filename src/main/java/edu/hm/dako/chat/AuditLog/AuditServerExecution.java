package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import edu.hm.dako.chat.udp.AuditLogServerUdp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import org.apache.log4j.PropertyConfigurator;

/**
 * Klasse zum Ausführen des Audit Server
 *
 * @author Swoboda
 */
class AuditServerExecution {

// Client send request an Socket
// Server receive request from Socket
//
// Server send response an Socket
// Client receive response from Socket

  private TcpServerSocket socketTcp;
  private TcpConnection connection;
  private AuditLogListenerThreadImpl auditlistener;
  private AuditLogServerUdp socketUdp;

  static boolean isUDP = false;
  static boolean isTCP = false;

  static ProtocolGetType type;

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("AuditLog-Server starten...\n"
        + "\"1\": UDP-Verbindung\n"
        + "\"2\": TCP-Verbindung\n\n"
        + "Eingabe: ");

    AuditServerExecution server = new AuditServerExecution();
    String input;
    boolean alive = true;

    while (alive) {
      input = scanner.nextLine();
      switch (input) {
        case "1":
          isUDP = true;
          type = new ProtocolGetType(true, false);
          System.out.println("AuditLog Server mit UDP gestartet.");
          try {
            server.openServerSocket();
            System.out.println("ServerSocket wird geöffnet");
          } catch (Exception e) {
            System.out.println("Exception 1");
          }
          break;
        case "2":
          isTCP = true;
          System.out.println("AuditLog Server mit TCP gestartet.");
          type = new ProtocolGetType(true, false);
          try {
            server.openServerSocket();
            server.startThread();
          } catch (Exception e) {
            System.out.println("Exception 2");
          }
          break;
        case "close":
          alive = false;
          break;
        default:
          System.out.println("Ungültige Eingabe");
          break;
      }
    }
    if (isUDP) {
      try {
        System.out.println("AuditLog-Server (UDP) wird beendet");
        server.closeConnection();
      } catch (Exception e) {
        System.out.println("Exception 4");
      }
    } else if (isTCP) {
      try {
        System.out.println("AuditLog-Server (TCP) wird beendet");
        server.closeConnection();
      } catch (Exception e) {
        System.out.println("Exception 3");
      }
    }

  }



  public void openServerSocket() throws Exception {
    try {
      if (isTCP) {
        socketTcp = new TcpServerSocket(40001, 30000, 100000);
      } else if (isUDP) {
        socketUdp = new AuditLogServerUdp(40001);
        socketUdp.UdpReceive();
      }
    } catch (Exception e) {
      System.out.println("Fehler113");
    }
  }

  public void startThread() throws Exception {
    try {
      if (isTCP) {
        connection = (TcpConnection) socketTcp.accept();
        auditlistener = new AuditLogListenerThreadImpl(connection);
        auditlistener.start();
      } else if (isUDP) {
        System.out.println("122: leere Methode für udp");
      }
    } catch (Exception e) {
      System.out.println("Fehler2");
    }
  }

  public void closeConnection() throws Exception {
    try {
      if (isTCP) {
        socketTcp.close();
        connection.close();
      } else if (isUDP) {
        socketUdp.close();
      }
    } catch (Exception e) {
      System.out.println("Fehler3");
    }
  }
}

//    if (isTCP) {
//      try {
//        System.out.println("Server mit TCP wird beendet");
//
//        // SHUTDOWN MESSAGE
//        AuditWriter shutdownWriter = new AuditWriter();
//        shutdownWriter.shutdownMessage();
//
//        server.closeConnection();
//      } catch (Exception e) {
//        System.out.println("Server wird beendet");
//      }
//    } else if (isUDP) {
//      try {
//        System.out.println("Server mit UDP wird beendet");
//        server.closeConnection();
//      } catch (Exception e) {
//        System.out.println("Server wird beendet");
//      }
//    }


//  PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);

/*  **************** Alte Version **************
  System.out.println("AuditlogServer gestartet");
  AuditServerExecution server = new AuditServerExecution();

  try {
    server.openServerSocket();
    server.startThread();
  } catch (Exception e) {
    System.out.println("Server wird beendet");
  } */

