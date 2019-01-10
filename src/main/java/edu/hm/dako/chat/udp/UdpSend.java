package edu.hm.dako.chat.udp;

import edu.hm.dako.chat.common.AuditLogPDU;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Erstellen eines getter für TCP / UDP.
 *
 * @author Swoboda, Lechner, Brosch, Hofstetter
 */

public class UdpSend {

  /**
   * Senden der PDU mithilfe von UDP
   *
   * @para pdu
   */
  public synchronized void send(AuditLogPDU pdu) {
    try {
      DatagramSocket socket = new DatagramSocket();

      //Auswahl Localhost / manuelle IP
      InetAddress ip = InetAddress.getLocalHost();
      InetAddress addr = InetAddress.getByAddress(new byte[] {
          (byte)10, (byte)28, (byte)205, (byte)8}
      );
      //Erstellen der Streams
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(pdu);
      oos.close();
      //Erstellen des Datagram Paketes
      byte[] message = baos.toByteArray();
      DatagramPacket sendPacket = new DatagramPacket(message, message.length, ip, 40001);
      socket.send(sendPacket);
      socket.close();

    } catch(Exception e) {
      System.out.println("UDPSendFehler" + e.getMessage());
    }
  }
}
