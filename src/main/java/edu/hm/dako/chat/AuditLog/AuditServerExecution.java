package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import edu.hm.dako.chat.udp.AuditLogServerUdp;
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

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);

    System.out.println("Für den Auditlogserver mit Udp starten die \"1\" eingeben.");
    System.out.println("Für den Auditlogserver mit Tcp starten die \"2\" eingeben.");

    AuditServerExecution server = new AuditServerExecution();

    String input = scanner.nextLine();

    if (input.equals("1")) {
      isUDP = true;
      type = new ProtocolGetType(true, false);
      System.out.println("AuditLog Server mit UDP gestartet.");
      try {
        server.openServerSocket(); // DER FEHLER IST HIER DRIN
        System.out.println("ServerSocket wird geöffnet");

        server.startThread();
        System.out.println("Thread wird gestartet");

      } catch (Exception e) {
        System.out.println("Server wird beendet");
      }

    } else if (input.equals("2")) {
      isTCP = true;
      System.out.println("AuditLog Server mit TCP gestartet.");
      type = new ProtocolGetType(true, false);


      try {
        server.openServerSocket();
        server.startThread();
      } catch (Exception e) {
        System.out.println("Server wird beendet");
      }

    } else {
      System.out.println("Invalid argument");
    }
    System.out.println("Server kann mit \"close\" geschlossen werden");
    input = scanner.nextLine();

    if (input.equals("close")){

      if(isTCP) {
        try {
          System.out.println("Server mit TCP wird beendet");
          server.closeConnection();
        } catch (Exception e) {
          System.out.println("Server wird beendet");
        }

      } else if (isUDP) {
        try {
          System.out.println("Server mit UDP wird beendet");
          server.closeConnection();
        } catch (Exception e) {
          System.out.println("Server wird beendet");
        }
      }

    }

    PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
/*  **************** Alte Version **************
    System.out.println("AuditlogServer gestartet");
    AuditServerExecution server = new AuditServerExecution();

    try {
      server.openServerSocket();
      server.startThread();
    } catch (Exception e) {
      System.out.println("Server wird beendet");
    } */
  }

    public void openServerSocket () throws Exception {
    try {
      System.out.println("isTcp" + isTCP + "isUdp" + isUDP);

      if (isTCP) {
        socketTcp = new TcpServerSocket(40001, 30000, 100000);

      } else if (isUDP) {

        socketUdp = new AuditLogServerUdp(40001);
        System.out.println("Udp-Server wurde erstellt");

        socketUdp.UdpReceive(); //DER FEHLER IST HIER DRIN
        System.out.println("116 UdpReceive wurde angewandt");

      }
    } catch (Exception e) {
      System.out.println("Fehler1");
    }
  }
    public void startThread () throws Exception {
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
    public void closeConnection () throws Exception {

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