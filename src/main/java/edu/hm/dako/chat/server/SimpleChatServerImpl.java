package edu.hm.dako.chat.server;

import edu.hm.dako.chat.AuditLog.AuditLogConnectionTcp;
import edu.hm.dako.chat.AuditLog.ProtocolGetType;
import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.PduType;
import edu.hm.dako.chat.tcp.TcpConnection;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import edu.hm.dako.chat.udp.AuditLogServerUdp;
import edu.hm.dako.chat.udp.UdpSend;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.ClientListEntry;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.connection.Connection;
import edu.hm.dako.chat.connection.ServerSocketInterface;
import javafx.concurrent.Task;

/**
 * <p/>
 * Simple-Chat-Server-Implementierung
 *
 * @author Peter Mandl überarbeitet durch Swoboda, Lechner, Brosch, Hofstetter.
 */
public class SimpleChatServerImpl extends AbstractChatServer {

	private static Log log = LogFactory.getLog(SimpleChatServerImpl.class);

	static boolean isUdp = ProtocolGetType.getUDP();
	static boolean isTcp =  ProtocolGetType.getTCP();

	//Verbindung fuer AuditLogServer

	// Threadpool fuer Worker-Threads
	private final ExecutorService executorService;

	// Socket fuer den Listener, der alle Verbindungsaufbauwuensche der Clients
	// entgegennimmt
	private ServerSocketInterface socket;

	/**
	 * Konstruktor
	 * 
	 * @param executorService
	 * @param socket
	 * @param serverGuiInterface
	 */
	public SimpleChatServerImpl(ExecutorService executorService,
			ServerSocketInterface socket, ChatServerGuiInterface serverGuiInterface) {
		log.debug("SimpleChatServerImpl konstruiert");
		this.executorService = executorService;
		this.socket = socket;
		this.serverGuiInterface = serverGuiInterface;
		counter = new SharedServerCounter();
		counter.logoutCounter = new AtomicInteger(0);
		counter.eventCounter = new AtomicInteger(0);
		counter.confirmCounter = new AtomicInteger(0);
	}

	@Override
	public void start() {
		// Verbindung für TCP-AuditLog-Server
		AuditLogConnectionTcp audit = new AuditLogConnectionTcp();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				// Clientliste erzeugen
				clients = SharedChatClientList.getInstance();

				//nur für TCP
				if (isTcp) {
					//AuditLogServer Connection starten
					try {
						audit.connectAudit();
					} catch (Exception e) {
					}
				}

				while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
					try {
						// Auf ankommende Verbindungsaufbauwuensche warten
						System.out.println(
								"SimpleChatServer wartet auf Verbindungsanfragen von Clients...");

						Connection connection = socket.accept();
						log.debug("Neuer Verbindungsaufbauwunsch empfangen");

						// Neuen Workerthread starten
						executorService.submit(new SimpleChatWorkerThreadImpl(connection, clients,
								counter, serverGuiInterface, audit));
					} catch (Exception e) {
						if (socket.isClosed()) {
							log.debug("Socket wurde geschlossen");
						} else {
							log.error(
									"Exception beim Entgegennehmen von Verbindungsaufbauwuenschen: " + e);
							ExceptionHandler.logException(e);
						}
					}
				}
				return null;
			}
		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
	/**
	 * ShutdownPDU erstellen
	 */

	public static void finish() throws Exception {
		// Shutdownpdu erstellen und an Auditlogserver übermitteln
		System.out.print("1");
		AuditLogPDU auditLogPDU4 = new AuditLogPDU();
		auditLogPDU4.setPduType(PduType.SHUTDOWN);
		if (isTcp) {

			TcpServerSocket socketTcp = new TcpServerSocket(40001,50000,50000);
			AuditLogConnectionTcp connection = (AuditLogConnectionTcp) socketTcp.accept();
			connection.send(auditLogPDU4);

			socketTcp.close();
			System.exit(0);

		} else if (isUdp) {
			UdpSend udpSend = new UdpSend();
			udpSend.send(auditLogPDU4);
		}
	}

	@Override
	public void stop() throws Exception {

		// Alle Verbindungen zu aktiven Clients abbauen
		Vector<String> sendList = clients.getClientNameList();
		for (String s : new Vector<String>(sendList)) {
			ClientListEntry client = clients.getClient(s);
			try {
				if (client != null) {
					client.getConnection().close();

					log.error("Verbindung zu Client " + client.getUserName() + " geschlossen");
				}
			} catch (Exception e) {
				log.debug(
						"Fehler beim Schliessen der Verbindung zu Client " + client.getUserName());
				ExceptionHandler.logException(e);
			}
		}

		// Loeschen der Userliste
		clients.deleteAll();
		Thread.currentThread().interrupt();
		socket.close();
		log.debug("Listen-Socket geschlossen");
		executorService.shutdown();
		log.debug("Threadpool freigegeben");

		System.out.println("SimpleChatServer beendet sich");
	}
}