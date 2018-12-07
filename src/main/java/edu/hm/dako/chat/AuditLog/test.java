package edu.hm.dako.chat.AuditLog;

import edu.hm.dako.chat.common.AuditLogPDU;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;

public class test {

  public static void main(String[] args) throws Exception {
    try {
      ServerSocket serverSocket = new ServerSocket(50000);
      Socket socketserver = new Socket("localhost",50000);
      ObjectInputStream in = new ObjectInputStream(socketserver.getInputStream());
      AuditLogPDU auditPDU = (AuditLogPDU) in.readObject();
      AuditWriter writer = new AuditWriter(auditPDU);
      writer.createFile();
      writer.writeInFile(auditPDU);
    } catch (Exception e) {
      System.out.println("Fehler");
    } throw new Exception();
  }
}

