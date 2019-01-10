package edu.hm.dako.chat.udp;

import edu.hm.dako.chat.common.AuditLogPDU;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSend {
  public synchronized void send(AuditLogPDU pdu) {
    try {
      DatagramSocket socket = new DatagramSocket();
      InetAddress ip = InetAddress.getLocalHost();
      InetAddress addr = InetAddress.getByAddress(new byte[] {
          (byte)10, (byte)28, (byte)205, (byte)8}
      );

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(pdu);
      oos.close();

      byte[] message = baos.toByteArray();
      DatagramPacket sendPacket = new DatagramPacket(message, message.length, ip, 40001);
      socket.send(sendPacket);
      socket.close();

    } catch(Exception e) {
      System.out.println("UDPSendFehler" + e.getMessage());
    }
  }
}
