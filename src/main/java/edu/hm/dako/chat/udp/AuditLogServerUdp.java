package edu.hm.dako.chat.udp;

import edu.hm.dako.chat.AuditLog.AuditWriter;
import edu.hm.dako.chat.client.ClientModel;
import edu.hm.dako.chat.common.AuditLogPDU;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

// Client send request an Socket
// Server receive request from Socket
//
// Server send response an Socket
// Client receive response from Socket
public class AuditLogServerUdp {

  private DatagramSocket udpSocket;
  private int port;

  public AuditLogServerUdp(int port) throws SocketException, IOException {
    this.port = port;
  }

  public void UdpReceive() throws IOException,ClassNotFoundException{
    udpSocket = new DatagramSocket(port);
    byte[] receiveData = new byte[1024];

    while (true) {

      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, InetAddress.getLocalHost(), 40001);

      udpSocket.receive(receivePacket);

      ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
      ObjectInputStream ois = new ObjectInputStream(bais);
      AuditLogPDU receivedPdu = (AuditLogPDU) ois.readObject() ;

      AuditWriter udpWriter = new AuditWriter();
      udpWriter.createFile();
      udpWriter.writeInFile(receivedPdu);
    }
  }

  public void close() throws IOException {
    udpSocket.close();
  }

  public static void main(String[]args) throws Exception{
    AuditLogServerUdp udp = new AuditLogServerUdp(40001);
    udp.UdpReceive();
  }
}