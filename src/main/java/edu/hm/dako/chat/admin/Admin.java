//package edu.hm.dako.chat.admin;
//
//import edu.hm.dako.chat.AuditLog.AuditLogListenerThreadImpl;
//import edu.hm.dako.chat.AuditLog.AuditServerExecution;
//import edu.hm.dako.chat.AuditLog.ProtocolGetType;
//import edu.hm.dako.chat.server.ChatServerGUI;
//import edu.hm.dako.chat.tcp.TcpConnection;
//import edu.hm.dako.chat.tcp.TcpServerSocket;
//import edu.hm.dako.chat.udp.AuditLogServerUdp;
//import java.util.Scanner;
//
//public class Admin {
//
//  private TcpServerSocket socketTcp;
//  private TcpConnection connection;
//  private AuditLogListenerThreadImpl auditlistener;
//  private AuditLogServerUdp socketUdp;
//
//  static boolean isUDP = false;
//  static boolean isTCP = false;
//
//  static ProtocolGetType type;
//
//  public static void main(String[] args) {
//    Scanner scanner = new Scanner(System.in);
//
//    System.out.print("Server starten. AuditLog-Server starten? (UDP / TCP / 0)\n"
//        + "Eingabe: ");
//
//
//    String input;
//    input = scanner.nextLine();
//    if (input.equals("UDP")) {
//      ChatServerGUI.main("1");
//      AuditServerExecution auditLogServer = new AuditServerExecution();
//    } else if (input.equals("TCP")) {
//
//    } else if (input.equals("0")) {
//
//    } else {
//      System.out.print("Falsche Eingabe. Wiederholen: ");
//    }
//
//  }
//
//  public void openServerSocket() throws Exception {
//    try {
//      if (isTCP) {
//        socketTcp = new TcpServerSocket(50001, 50000, 50000);
//      } else if (isUDP) {
//        socketUdp = new AuditLogServerUdp(40001);
//        socketUdp.UdpReceive();
//      }
//    } catch (Exception e) {
//      System.out.println("Fehler113");
//    }
//  }
//  /**
//   * Starten des TCP threads
//   */
//  public void startThread() throws Exception {
//    try {
//      if (isTCP) {
//        connection = (TcpConnection) socketTcp.accept();
//        auditlistener = new AuditLogListenerThreadImpl(connection);
//        auditlistener.start();
//      } else if (isUDP) {
//      }
//    } catch (Exception e) {
//      System.out.println("Fehler2");
//    }
//  }
//
//}
