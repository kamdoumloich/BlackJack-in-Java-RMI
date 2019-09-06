package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;	

public class Server extends JFrame{
	private JButton Deal;
	private Deck newdeck;
	private JTextArea displayArea;
	private ExecutorService executor;
	private ServerSocket server;
	private SockServer[] sockServer;
	private int counter = 1;
	private String dcard1, dcard2;
	private List<Hand> spieler;
	private Hand dealerKarten;
	private int playersleft;
	private int playersdisconnect = 0;

	public Server() {

		super("Server");

		spieler = new ArrayList<Hand>();
		sockServer = new SockServer[100];
		executor = Executors.newFixedThreadPool(100);

		Deal = new JButton("Karten verteilen");

		Deal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Deal.setEnabled(false);
				newdeck = new Deck();
				kartenVerteilen();
				zeigtNachricht("\n\n Karten Verteilen \n\n");

			}
		});

		add(Deal, BorderLayout.SOUTH);

		displayArea = new JTextArea();
		displayArea.setEditable(false);
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		setSize(500, 300);
		setVisible(true);
	}

	public void runDeal() {
		try {
			server = new ServerSocket(10000, 100);

			while (true) {
				try {
					sockServer[counter] = new SockServer(counter);

					sockServer[counter].waitForConnection();

					executor.execute(sockServer[counter]);

				} catch (EOFException eofException) {
					zeigtNachricht("\n Server ist nicht mehr zu erreichen!");
				} finally {
					++counter;
				}
			}
		} catch (IOException ioException) {
		}
	}

	private void zeigtNachricht(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayArea.append(messageToDisplay);
			}
		});
	}

	private void kartenVerteilen() {

		try {
			playersleft = counter - 1;
			playersleft -= playersdisconnect;
			newdeck.mix();
			dcard1 = newdeck.karteZiehen();
			dcard2 = newdeck.karteZiehen();
			zeigtNachricht("\n\n" + dcard1 + " " + dcard2);

			for (int i = 1; i < counter; i++) {
				String c1, c2;
				c1 = newdeck.karteZiehen();
				c2 = newdeck.karteZiehen();
				Hand p = new Hand(c1, c2);
				spieler.add(p);
				sockServer[i].schickeNachricht("\n Karten verteilt:\n" + c1 + " und " + c2);
				sockServer[i].schickeNachricht("\n Anzahl Ihre Punkte: " + p.getKartenSumme());

			}
		} catch (NullPointerException n) {
		}
	}

	private void ergebnis() {
		try {
			for (int i = 1; i <= counter; i++) {
				sockServer[i].schickeNachricht("Dealer hat " + dealerKarten.getKartenSumme());

				if ((dealerKarten.getKartenSumme() <= 21) && (spieler.get(i - 1).getKartenSumme() <= 21)) {

					if (dealerKarten.getKartenSumme() > spieler.get(i - 1).getKartenSumme()) {
						sockServer[i].schickeNachricht("\n Sie haben verloren!");
					}

					if (dealerKarten.getKartenSumme() < spieler.get(i - 1).getKartenSumme()) {
						sockServer[i].schickeNachricht("\n Sie haben gewinnen!");
					}

					if (dealerKarten.getKartenSumme() == spieler.get(i - 1).getKartenSumme()) {
						sockServer[i].schickeNachricht("\n Tie!");
					}

				}

				if (dealerKarten.CheckBust()) {

					if (spieler.get(i - 1).CheckBust()) {
						sockServer[i].schickeNachricht("\n Tie!");//tie
					}
					if (spieler.get(i - 1).getKartenSumme() <= 21) {
						sockServer[i].schickeNachricht("\n Sie haben gewinnen!");
					}
				}

				if (spieler.get(i - 1).CheckBust() && dealerKarten.getKartenSumme() <= 21) {
					sockServer[i].schickeNachricht("\n Sie haben verloren!");
				}
			}

		} catch (NullPointerException n) {
		}
	}

	private class SockServer implements Runnable {
		private ObjectOutputStream output;
		private ObjectInputStream input;
		private Socket verbindung;
		private int verbindungID;

		public SockServer(int counterIn) {
			verbindungID = counterIn;
		}

		public void run() {
			try {
				try {
					getStreams();
					processConnection();

				} catch (EOFException eofException) {
					zeigtNachricht("\nServer" + verbindungID + " ist nicht mehr verbunden");
				} finally {
					closeConnection();
				}
			} catch (IOException ioException) {
			}
		}

		private void waitForConnection() throws IOException {

			zeigtNachricht(verbindungID+" Warten auf Verbindung eines neuen Hosts \n");
			verbindung = server.accept();
			zeigtNachricht("Neuer Host " + verbindung.getInetAddress().getHostName()+ " verbunden \n");
			for (int i = 1; i < counter; i++) {
				sockServer[i].schickeNachricht("Neue Spieler gefunden \n");
			}
		}

		private void getStreams() throws IOException {

			output = new ObjectOutputStream(verbindung.getOutputStream());
			output.flush();

			input = new ObjectInputStream(verbindung.getInputStream());
		}

		private void processConnection() throws IOException {
			String message = "Verbindung " + verbindungID + " successful";
			schickeNachricht(message);
			do {
				try {
					if (message.contains("neue_karte")) {
						kartenVerteilt();
					}

					if (message.contains("spiel_enden")) {
						this.schickeNachricht("Warten Bitte!");
						playersleft--;
						CheckDone();
					}

					message = (String) input.readObject();

				} catch (ClassNotFoundException classNotFoundException) {
					zeigtNachricht("\nUnknown object type received");
				}

			} while (!message.equals(null));
		}

		private void beginne() {
			dealerKarten = new Hand(dcard1, dcard2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (dealerKarten.getKartenSumme() < 16) {
				while (dealerKarten.getKartenSumme() < 16) {
					String karte1 = newdeck.karteZiehen();
					dealerKarten.kartenVerteilen(karte1);
					zeigtNachricht("Dealer karten..." + karte1 + "\n"); 
					zeigtNachricht("Anzahl:" + dealerKarten.getKartenSumme() + "\n");
				}
			}
			if (dealerKarten.CheckBust()) {
				zeigtNachricht("Dealer fail!");
			} else {
				zeigtNachricht("Dealer hat" + " " + dealerKarten.getKartenSumme());
			}

			ergebnis();
		}

		private void kartenVerteilt() {
			String nextc = newdeck.karteZiehen();
			schickeNachricht(nextc);
			spieler.get(this.verbindungID - 1).kartenVerteilen(nextc);
			schickeNachricht("Ihre gesamte Punkte: " + spieler.get(this.verbindungID - 1).getKartenSumme());
			if (spieler.get(this.verbindungID - 1).CheckBust()) {
				schickeNachricht("fail\n");
				playersleft--;
				CheckDone();
			}

		}

		private void CheckDone() {

			if (playersleft == 0) {
				beginne();
			}
		}

		private void closeConnection() {
			zeigtNachricht("\nVerbindung geschlossen " + verbindungID + "\n");
			playersdisconnect++;

			try {
				output.close();
				input.close();
				verbindung.close();
			} catch (IOException ioException) {
			}
		}

		private void schickeNachricht(String nachricht) {
			try {
				output.writeObject(nachricht);
				output.flush();

			} catch (IOException ioException) {
				displayArea.append("\nError writing object");
			}
		}

	}

}
