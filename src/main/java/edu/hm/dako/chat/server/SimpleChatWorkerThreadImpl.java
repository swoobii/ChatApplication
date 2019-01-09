package edu.hm.dako.chat.server;

import edu.hm.dako.chat.AuditLog.AuditLogConnectionTcp;
import edu.hm.dako.chat.AuditLog.ProtocolGetType;
import edu.hm.dako.chat.common.AuditLogPDU;
import edu.hm.dako.chat.common.PduType;
import edu.hm.dako.chat.tcp.TcpServerSocket;
import edu.hm.dako.chat.udp.AuditLogServerUdp;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chat.common.ChatPDU;
import edu.hm.dako.chat.common.ClientConversationStatus;
import edu.hm.dako.chat.common.ClientListEntry;
import edu.hm.dako.chat.common.ExceptionHandler;
import edu.hm.dako.chat.connection.Connection;
import edu.hm.dako.chat.connection.ConnectionTimeoutException;
import edu.hm.dako.chat.connection.EndOfFileException;

/**
 * Worker-Thread zur serverseitigen Bedienung einer Session mit einem Client.
 * Jedem Chat-Client wird serverseitig ein Worker-Thread zugeordnet.
 * 
 * @author Peter Mandl
 *
 */
public class SimpleChatWorkerThreadImpl extends AbstractWorkerThread {

	private static Log log = LogFactory.getLog(SimpleChatWorkerThreadImpl.class);

  static boolean isUdp = ProtocolGetType.getUDP();
  static boolean isTcp = ProtocolGetType.getTCP();

	private static int counterAuditLogSend = 1;


	// Verbindung für den AuditlogServer
	private AuditLogConnectionTcp audit;
	private TcpServerSocket socketTcp;

	public SimpleChatWorkerThreadImpl(Connection con, SharedChatClientList clients,
			SharedServerCounter counter, ChatServerGuiInterface serverGuiInterface, AuditLogConnectionTcp auditConnection) {

		super(con, clients, counter, serverGuiInterface);
		 this.audit = auditConnection;

//		 TODO: Funktionierenden Getter für TCP / UDP einfügen
//		 try {
//		 	socketTcp = new TcpServerSocket(40001, 30000, 100000);
//		 	socketTcp.close();
//		 	isUdp = true;
//		 } catch (Exception e) {
//		 	isTcp = true;
//		}
	}

	@Override
	public void run() {
		log.debug(
				"ChatWorker-Thread erzeugt, Threadname: " + Thread.currentThread().getName());
		while (!finished && !Thread.currentThread().isInterrupted()) {
			try {
				// Warte auf naechste Nachricht des Clients und fuehre
				// entsprechende Aktion aus
				handleIncomingMessage();
			} catch (Exception e) {
				log.error("Exception waehrend der Nachrichtenverarbeitung");
				ExceptionHandler.logException(e);
			}
		}
		log.debug(Thread.currentThread().getName() + " beendet sich");
		closeConnection();
	}

	/**
	 * Senden eines Login-List-Update-Event an alle angemeldeten Clients
	 * 
	 * @param pdu
	 *          Zu sendende PDU
	 */
	protected void sendLoginListUpdateEvent(ChatPDU pdu) {

		// Liste der eingeloggten bzw. sich einloggenden User ermitteln
		Vector<String> clientList = clients.getRegisteredClientNameList();

		log.debug("Aktuelle Clientliste, die an die Clients uebertragen wird: " + clientList);

		pdu.setClients(clientList);

		Vector<String> clientList2 = clients.getClientNameList();
		for (String s : new Vector<String>(clientList2)) {
			log.debug("Fuer " + s
					+ " wird Login- oder Logout-Event-PDU an alle aktiven Clients gesendet");

			ClientListEntry client = clients.getClient(s);
			try {
				if (client != null) {

					client.getConnection().send(pdu);
					log.debug(
							"Login- oder Logout-Event-PDU an " + client.getUserName() + " gesendet");
					clients.incrNumberOfSentChatEvents(client.getUserName());
					eventCounter.getAndIncrement();
				}
			} catch (Exception e) {
				log.error(
						"Senden einer Login- oder Logout-Event-PDU an " + s + " nicht moeglich");
				ExceptionHandler.logException(e);
			}
		}
	}

	@Override
	protected void loginRequestAction(ChatPDU receivedPdu) {

		ChatPDU pdu;
		log.debug("Login-Request-PDU fuer " + receivedPdu.getUserName() + " empfangen");

		// Neuer Client moechte sich einloggen, Client in Client-Liste
		// eintragen
		if (!clients.existsClient(receivedPdu.getUserName())) {
			log.debug("User nicht in Clientliste: " + receivedPdu.getUserName());
			ClientListEntry client = new ClientListEntry(receivedPdu.getUserName(), connection);
			client.setLoginTime(System.nanoTime());
			clients.createClient(receivedPdu.getUserName(), client);
			clients.changeClientStatus(receivedPdu.getUserName(),
					ClientConversationStatus.REGISTERING);
			log.debug("User " + receivedPdu.getUserName() + " nun in Clientliste");

			userName = receivedPdu.getUserName();
			clientThreadName = receivedPdu.getClientThreadName();
			Thread.currentThread().setName(receivedPdu.getUserName());
			log.debug("Laenge der Clientliste: " + clients.size());
			serverGuiInterface.incrNumberOfLoggedInClients();

			// Login-Event an alle Clients (auch an den gerade aktuell
			// anfragenden) senden

			Vector<String> clientList = clients.getClientNameList();
			pdu = ChatPDU.createLoginEventPdu(userName, clientList, receivedPdu);
			sendLoginListUpdateEvent(pdu);

			// Login Response senden
			ChatPDU responsePdu = ChatPDU.createLoginResponsePdu(userName, receivedPdu);

			try {
				clients.getClient(userName).getConnection().send(responsePdu);
			} catch (Exception e) {
				log.debug("Senden einer Login-Response-PDU an " + userName + " fehlgeschlagen");
				log.debug("Exception Message: " + e.getMessage());
			}

			log.debug("Login-Response-PDU an Client " + userName + " gesendet");

			// Zustand des Clients aendern
			clients.changeClientStatus(userName, ClientConversationStatus.REGISTERED);

		} else {
			// User bereits angemeldet, Fehlermeldung an Client senden,
			// Fehlercode an Client senden
			pdu = ChatPDU.createLoginErrorResponsePdu(receivedPdu, ChatPDU.LOGIN_ERROR);

			try {
				connection.send(pdu);
				log.debug("Login-Response-PDU an " + receivedPdu.getUserName()
						+ " mit Fehlercode " + ChatPDU.LOGIN_ERROR + " gesendet");
			} catch (Exception e) {
				log.debug("Senden einer Login-Response-PDU an " + receivedPdu.getUserName()
						+ " nicth moeglich");
				ExceptionHandler.logExceptionAndTerminate(e);
			}
		}
	}

	@Override
	protected void logoutRequestAction(ChatPDU receivedPdu) {

		ChatPDU pdu;
		logoutCounter.getAndIncrement();
		log.debug("Logout-Request von " + receivedPdu.getUserName() + ", LogoutCount = "
				+ logoutCounter.get());

		log.debug("Logout-Request-PDU von " + receivedPdu.getUserName() + " empfangen");

		if (!clients.existsClient(userName)) {
			log.debug("User nicht in Clientliste: " + receivedPdu.getUserName());
		} else {

			// Event an Client versenden
			Vector<String> clientList = clients.getClientNameList();
			pdu = ChatPDU.createLogoutEventPdu(userName, clientList, receivedPdu);

			clients.changeClientStatus(receivedPdu.getUserName(),
					ClientConversationStatus.UNREGISTERING);
			sendLoginListUpdateEvent(pdu);
			serverGuiInterface.decrNumberOfLoggedInClients();

			// Der Thread muss hier noch warten, bevor ein Logout-Response gesendet
			// wird, da sich sonst ein Client abmeldet, bevor er seinen letzten Event
			// empfangen hat. das funktioniert nicht bei einer grossen Anzahl an
			// Clients (kalkulierte Events stimmen dann nicht mit tatsaechlich
			// empfangenen Events ueberein.
			// In der Advanced-Variante wird noch ein Confirm gesendet, das ist
			// sicherer.

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				ExceptionHandler.logException(e);
			}

			clients.changeClientStatus(receivedPdu.getUserName(),
					ClientConversationStatus.UNREGISTERED);

			// Logout Response senden
			sendLogoutResponse(receivedPdu.getUserName());

			// Worker-Thread des Clients, der den Logout-Request gesendet
			// hat, auch gleich zum Beenden markieren
			clients.finish(receivedPdu.getUserName());
			log.debug("Laenge der Clientliste beim Vormerken zum Loeschen von "
					+ receivedPdu.getUserName() + ": " + clients.size());
		}
	}

	@Override
	protected void chatMessageRequestAction(ChatPDU receivedPdu) {

		ClientListEntry client = null;
		clients.setRequestStartTime(receivedPdu.getUserName(), startTime);
		clients.incrNumberOfReceivedChatMessages(receivedPdu.getUserName());
		serverGuiInterface.incrNumberOfRequests();
		log.debug("Chat-Message-Request-PDU von " + receivedPdu.getUserName()
				+ " mit Sequenznummer " + receivedPdu.getSequenceNumber() + " empfangen");

		if (!clients.existsClient(receivedPdu.getUserName())) {
			log.debug("User nicht in Clientliste: " + receivedPdu.getUserName());
		} else {
			// Liste der betroffenen Clients ermitteln
			Vector<String> sendList = clients.getClientNameList();
			ChatPDU pdu = ChatPDU.createChatMessageEventPdu(userName, receivedPdu);

			// Event an Clients senden
			for (String s : new Vector<String>(sendList)) {
				client = clients.getClient(s);
				try {
					if ((client != null)
							&& (client.getStatus() != ClientConversationStatus.UNREGISTERED)) {
						pdu.setUserName(client.getUserName());
						client.getConnection().send(pdu);
						log.debug("Chat-Event-PDU an " + client.getUserName() + " gesendet");
						clients.incrNumberOfSentChatEvents(client.getUserName());
						eventCounter.getAndIncrement();
						log.debug(userName + ": EventCounter erhoeht = " + eventCounter.get()
								+ ", Aktueller ConfirmCounter = " + confirmCounter.get()
								+ ", Anzahl gesendeter ChatMessages von dem Client = "
								+ receivedPdu.getSequenceNumber());
					}
				} catch (Exception e) {
					log.debug("Senden einer Chat-Event-PDU an " + client.getUserName()
							+ " nicht moeglich");
					ExceptionHandler.logException(e);
				}
			}

			client = clients.getClient(receivedPdu.getUserName());
			if (client != null) {
				ChatPDU responsePdu = ChatPDU.createChatMessageResponsePdu(
						receivedPdu.getUserName(), 0, 0, 0, 0,
						client.getNumberOfReceivedChatMessages(), receivedPdu.getClientThreadName(),
						(System.nanoTime() - client.getStartTime()));

				if (responsePdu.getServerTime() / 1000000 > 100) {
					log.debug(Thread.currentThread().getName()
							+ ", Benoetigte Serverzeit vor dem Senden der Response-Nachricht > 100 ms: "
							+ responsePdu.getServerTime() + " ns = "
							+ responsePdu.getServerTime() / 1000000 + " ms");
				}

				try {
					client.getConnection().send(responsePdu);
					log.debug(
							"Chat-Message-Response-PDU an " + receivedPdu.getUserName() + " gesendet");
				} catch (Exception e) {
					log.debug("Senden einer Chat-Message-Response-PDU an " + client.getUserName()
							+ " nicht moeglich");
					ExceptionHandler.logExceptionAndTerminate(e);
				}
			}
			log.debug("Aktuelle Laenge der Clientliste: " + clients.size());
		}
	}

	/**
	 * Verbindung zu einem Client ordentlich abbauen
	 */
	private void closeConnection() {

		log.debug("Schliessen der Chat-Connection zum " + userName);

		// Bereinigen der Clientliste falls erforderlich

		if (clients.existsClient(userName)) {
			log.debug("Close Connection fuer " + userName
					+ ", Laenge der Clientliste vor dem bedingungslosen Loeschen: "
					+ clients.size());

			clients.deleteClientWithoutCondition(userName);
			log.debug("Laenge der Clientliste nach dem bedingungslosen Loeschen von " + userName
					+ ": " + clients.size());
		}

		try {
			connection.close();
		} catch (Exception e) {
			log.debug("Exception bei close");
			// ExceptionHandler.logException(e);
		}
	}

	/**
	 * Antwort-PDU fuer den initiierenden Client aufbauen und senden
	 * 
	 * @param eventInitiatorClient
	 *          Name des Clients
	 */
	private void sendLogoutResponse(String eventInitiatorClient) {

		ClientListEntry client = clients.getClient(eventInitiatorClient);

		if (client != null) {
			ChatPDU responsePdu = ChatPDU.createLogoutResponsePdu(eventInitiatorClient, 0, 0, 0,
					0, client.getNumberOfReceivedChatMessages(), clientThreadName);

			log.debug(eventInitiatorClient + ": SentEvents aus Clientliste: "
					+ client.getNumberOfSentEvents() + ": ReceivedConfirms aus Clientliste: "
					+ client.getNumberOfReceivedEventConfirms());
			try {
				clients.getClient(eventInitiatorClient).getConnection().send(responsePdu);
			} catch (Exception e) {
				log.debug("Senden einer Logout-Response-PDU an " + eventInitiatorClient
						+ " fehlgeschlagen");
				log.debug("Exception Message: " + e.getMessage());
			}

			log.debug("Logout-Response-PDU an Client " + eventInitiatorClient + " gesendet");
		}
	}

	/**
	 * Prueft, ob Clients aus der Clientliste geloescht werden koennen
	 * 
	 * @return boolean, true: Client geloescht, false: Client nicht geloescht
	 */
	private boolean checkIfClientIsDeletable() {

		ClientListEntry client;

		// Worker-Thread beenden, wenn sein Client schon abgemeldet ist
		if (userName != null) {
			client = clients.getClient(userName);
			if (client != null) {
				if (client.isFinished()) {
					// Loesche den Client aus der Clientliste
					// Ein Loeschen ist aber nur zulaessig, wenn der Client
					// nicht mehr in einer anderen Warteliste ist
					log.debug("Laenge der Clientliste vor dem Entfernen von " + userName + ": "
							+ clients.size());
					if (clients.deleteClient(userName) == true) {
						// Jetzt kann auch Worker-Thread beendet werden

						log.debug("Laenge der Clientliste nach dem Entfernen von " + userName + ": "
								+ clients.size());
						log.debug("Worker-Thread fuer " + userName + " zum Beenden vorgemerkt");
						return true;
					}
				}
			}
		}

		// Garbage Collection in der Clientliste durchfuehren
		Vector<String> deletedClients = clients.gcClientList();
		if (deletedClients.contains(userName)) {
			log.debug("Ueber Garbage Collector ermittelt: Laufender Worker-Thread fuer "
					+ userName + " kann beendet werden");
			finished = true;
			return true;
		}
		return false;
	}

	@Override
	protected void handleIncomingMessage() throws Exception {
		if (checkIfClientIsDeletable() == true) {
			return;
		}

		// Warten auf naechste Nachricht
		ChatPDU receivedPdu = null;

		// Nach einer Minute wird geprueft, ob Client noch eingeloggt ist
		final int RECEIVE_TIMEOUT = 1200000;

		try {
			receivedPdu = (ChatPDU) connection.receive(RECEIVE_TIMEOUT);
			// Nachricht empfangen
			// Zeitmessung fuer Serverbearbeitungszeit starten
			startTime = System.nanoTime();

		} catch (ConnectionTimeoutException e) {

			// Wartezeit beim Empfang abgelaufen, pruefen, ob der Client
			// ueberhaupt noch etwas sendet
			log.debug(
					"Timeout beim Empfangen, " + RECEIVE_TIMEOUT + " ms ohne Nachricht vom Client");

			if (clients.getClient(userName) != null) {
				if (clients.getClient(userName)
						.getStatus() == ClientConversationStatus.UNREGISTERING) {
					// Worker-Thread wartet auf eine Nachricht vom Client, aber es
					// kommt nichts mehr an
					log.error(
							"Client ist im Zustand UNREGISTERING und bekommt aber keine Nachricht mehr");
					// Zur Sicherheit eine Logout-Response-PDU an Client senden und
					// dann Worker-Thread beenden
					finished = true;
				}
			}
			return;

		} catch (EndOfFileException e) {
			log.debug("End of File beim Empfang, vermutlich Verbindungsabbau des Partners fuer "
					+ userName);
			finished = true;
			return;

		} catch (java.net.SocketException e) {
			log.error("Verbindungsabbruch beim Empfang der naechsten Nachricht vom Client "
					+ getName());
			finished = true;
			return;

		} catch (Exception e) {
			log.error(
					"Empfang einer Nachricht fehlgeschlagen, Workerthread fuer User: " + userName);
			ExceptionHandler.logException(e);
			finished = true;
			return;
		}

		// Empfangene Nachricht bearbeiten
		try {
			switch (receivedPdu.getPduType()) {

			case LOGIN_REQUEST:
				// Login-Request vom Client empfangen
				loginRequestAction(receivedPdu);
        AuditLogPDU auditLogPDU1 = AuditLogPDU.makeAuditLogPDU(receivedPdu);
        if (isTcp) {
          audit.send(auditLogPDU1);
        } else if (isUdp) {
					udpSend(auditLogPDU1);
        }
				break;

			case CHAT_MESSAGE_REQUEST:
				// Chat-Nachricht angekommen, an alle verteilen
				chatMessageRequestAction(receivedPdu);
				AuditLogPDU auditLogPDU2 = AuditLogPDU.makeAuditLogPDU(receivedPdu);
				if (isTcp) {
          audit.send(auditLogPDU2);
        } else if (isUdp) {
					udpSend(auditLogPDU2);
        }
				 break;

			case LOGOUT_REQUEST:
				// Logout-Request vom Client empfangen
				logoutRequestAction(receivedPdu);
        AuditLogPDU auditLogPDU3 = AuditLogPDU.makeAuditLogPDU(receivedPdu);
        if (isTcp) {
          audit.send(auditLogPDU3);
        } else if (isUdp) {
					udpSend(auditLogPDU3);
				}
				break;

				case SHUTDOWN:
				//SHUTDOWN-PDU
					AuditLogPDU auditLogPDU4 = new AuditLogPDU();
					auditLogPDU4.setPduType(PduType.SHUTDOWN);
					if (isTcp) {
						audit.send(auditLogPDU4);
					} else if (isUdp) {
						udpSend(auditLogPDU4);
					}
				break;

			default:
				log.debug("Falsche PDU empfangen von Client: " + receivedPdu.getUserName()
						+ ", PduType: " + receivedPdu.getPduType());
				break;
			}
		} catch (Exception e) {
			log.error("Exception bei der Nachrichtenverarbeitung");
			ExceptionHandler.logExceptionAndTerminate(e);
		}
	}

  public synchronized void udpSend(AuditLogPDU pdu) {
    try {
      DatagramSocket socket = new DatagramSocket();
			// InetAddress ip = InetAddress.getLocalHost();
			InetAddress addr = InetAddress.getByAddress(new byte[] {
					(byte)10, (byte)28, (byte)205, (byte)8}
			);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(pdu);
      oos.close();

      byte[] message = baos.toByteArray();
      DatagramPacket sendPacket = new DatagramPacket(message, message.length, addr, 40001);
      socket.send(sendPacket);
      socket.close();

    } catch(Exception e) {
      System.out.println("UDPSendFehler" + e.getMessage());
    }
    System.out.println("UDPSend Nr.: " + counterAuditLogSend);
		counterAuditLogSend++;
  }
}
