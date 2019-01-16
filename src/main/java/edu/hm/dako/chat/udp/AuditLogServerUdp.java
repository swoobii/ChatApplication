package edu.hm.dako.chat.udp;

import edu.hm.dako.chat.AuditLog.AuditWriter;
import edu.hm.dako.chat.client.ClientModel;
import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.PduType;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Erstellen eines UDP Servers
 *
 * @author Swoboda, Lechner, Brosch, Hofstetter
 */


public class AuditLogServerUdp {

  private DatagramSocket udpSocket;
  private int port;
  private AuditWriter udpWriter = new AuditWriter();

  /**
   * Konstruktor für übergeben eines Ports
   *
   * @para port
   */

  public AuditLogServerUdp(int port) throws SocketException, IOException {
    this.port = port;



  }

  /**
   * Empfangen der Dateien
   * Schließen des UDP Auditlogserver
   *
   */

  public void UdpReceive() throws IOException,ClassNotFoundException{
    udpSocket = new DatagramSocket(port);
    byte[] receiveData = new byte[1024];

    AuditListenerThreadUDP udp = new AuditListenerThreadUDP(port, udpSocket);
    udp.start();

//    while (true) {
//
//      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, InetAddress.getLocalHost(), 40001);
//
//      udpSocket.receive(receivePacket);
//      ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
//      ObjectInputStream ois = new ObjectInputStream(bais);
//      AuditLogPDU receivedPdu = (AuditLogPDU) ois.readObject() ;
//      udpWriter.writeInFile(receivedPdu);
//      System.out.println(receivedPdu.toString());
//
//      if (receivedPdu.getPduType() == PduType.SHUTDOWN) {
//        udpSocket.close();
//        System.exit(0);
//      }
//    }


  }
  /**
   * Schließen des UDP Auditlogserver
   */


  public void close() throws IOException {
    udpSocket.close();
  }


}