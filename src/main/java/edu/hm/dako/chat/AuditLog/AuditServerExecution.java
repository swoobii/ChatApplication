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
 * @author Swoboda, Lechner, Brosch, Hofstetter
 */

class AuditServerExecution {


  private TcpServerSocket socketTcp;
  private TcpConnection connection;
  private AuditLogListenerThreadImpl auditlistener;
  private AuditLogServerUdp socketUdp;

  static boolean isUDP = false;
  static boolean isTCP = false;

  static ProtocolGetType type;

  /**
   *
   * Ausführen des Auditlogservers und auswählen der Connection art.
   *
   * @param args
   *
   */

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("AuditLog-Server starten...\n"
        + "\"1\": UDP-Verbindung\n"
        + "\"2\": TCP-Verbindung\n\n"
        + "Eingabe: ");

    AuditServerExecution server = new AuditServerExecution();
    String input;
    boolean alive = true;

    //Unterscheidung von UDP und TCP mithilfe einer Eingabe

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

  }
  /**
   * Starten des sockets von TCP oder UDP
   */


  public void openServerSocket() throws Exception {
    try {
      if (isTCP) {
        socketTcp = new TcpServerSocket(40001, 50000, 50000);
      } else if (isUDP) {
        socketUdp = new AuditLogServerUdp(40001);
        socketUdp.UdpReceive();
      }
    } catch (Exception e) {
      System.out.println("Fehler113");
    }
  }
  /**
   * Starten des TCP threads
   */
  public void startThread() throws Exception {
    try {
      if (isTCP) {
        connection = (TcpConnection) socketTcp.accept();
        auditlistener = new AuditLogListenerThreadImpl(connection,socketTcp);
        auditlistener.start();
      } else if (isUDP) {
      }
    } catch (Exception e) {
      System.out.println("Fehler2");
    }
  }

  /**
   * Schließen des sockets und der connection von TCP oder
   * schließen des UDP sockets
   */

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


