package edu.hm.dako.chat.udp;

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


public class AuditLogServerUdp {

  private DatagramSocket udpSocket;
  private int port = 9999;



  public AuditLogServerUdp(int port) throws SocketException, IOException {
    this.port = port;
    this.udpSocket = new DatagramSocket(this.port);
  }


  public static void main(String[]args) throws Exception{
    AuditLogServerUdp udp = new AuditLogServerUdp(40001);
    udp.UdpReceive();

  }

  public void UdpReceive() throws IOException,ClassNotFoundException{
    udpSocket = new DatagramSocket(port);
    byte[] receiveData = new byte[1024];
    System.out.println("......");

    while (true) {
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

      udpSocket.receive(receivePacket);
      ByteArrayInputStream baos = new ByteArrayInputStream(receiveData);
      ObjectInputStream oos = new ObjectInputStream(baos);
      AuditLogPDU receivedPDu = (AuditLogPDU) oos.readObject() ;

      System.out.println("RECEIVED: " + receivedPDu);
    }

  }
  public void close() throws IOException {
    udpSocket.close();
  }


}