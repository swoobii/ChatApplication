package edu.hm.dako.chat.udp;

import edu.hm.dako.chat.AuditLog.AuditWriter;
import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.PduType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.imageio.IIOException;

public class AuditListenerThreadUDP extends Thread {

  private int port;
  private DatagramSocket socket;

  public AuditListenerThreadUDP(int port, DatagramSocket socket) {
    this.port = port;
    this.socket = socket;
  }



  public void run(){
    try{
      this.Receive();

    } catch(Exception e){

    }

  }

  public void Receive() throws IOException,ClassNotFoundException{
    byte[] receiveData = new byte[1024];
    AuditWriter udpWriter = new AuditWriter();

    while (true) {

      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, InetAddress
          .getLocalHost(), 40001);

      socket.receive(receivePacket);
      ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
      ObjectInputStream ois = new ObjectInputStream(bais);
      AuditLogPDU receivedPdu = (AuditLogPDU) ois.readObject() ;
      udpWriter.writeInFile(receivedPdu);
      System.out.println(receivedPdu.toString());

      if (receivedPdu.getPduType() == PduType.SHUTDOWN) {
        socket.close();
        System.exit(0);
      }
    }
  }


}
