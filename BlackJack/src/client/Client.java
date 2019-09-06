package client;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Client extends JFrame{
	private JButton neue_karte;
	private JButton spiel_enden;
	private JPanel buttons;
	private JTextArea displayArea; 
	private ObjectOutputStream output; 
	private ObjectInputStream input; 
	private String nachricht; 
	private String host_name; 
	private Socket client; 
	
	// Konstruktor
	public Client( String host_name_ )
	{
		super( "Player" );

		host_name = host_name_; 

		buttons = new JPanel();
		buttons.setLayout(new GridLayout(1,2));
		neue_karte = new JButton("Neue Karte");
		spiel_enden = new JButton("Spiel Enden");
		
		neue_karte.addActionListener(
				new ActionListener() 
				{
					
					public void actionPerformed( ActionEvent event )
					{
						streamOut( "neue_karte" );
					} 
				} 
				); 
		
		spiel_enden.addActionListener(
				new ActionListener() 
				{
					public void actionPerformed( ActionEvent event )
					{
						streamOut( "spiel_enden" );
					} 
				} 
				); 

		buttons.add(neue_karte, BorderLayout.SOUTH);
		buttons.add(spiel_enden, BorderLayout.SOUTH);
		buttons.setVisible(true);
		add(buttons,BorderLayout.SOUTH);
		displayArea = new JTextArea();
		add( new JScrollPane( displayArea ), BorderLayout.CENTER );

		setSize( 300, 300 ); 
		setVisible( true ); 
	} 

	public void runClient() 
	{
		try 
		{
			connectToServer(); 
			getStreams(); 
			processConnection();
		}
		catch ( EOFException eofException ) 
		{
			zeigtNachricht( "\n Client hat die Verbindung geschlossen \n" );
		}
		catch ( IOException ioException ) 
		{} 
		finally 
		{
			closeConnection();
		} 
	} 

	private void connectToServer() throws IOException
	{      
		zeigtNachricht( "Verbindung...\n" );

		client = new Socket( InetAddress.getByName( host_name ), 10000 );
		
		zeigtNachricht( "Verbunden mit dem Name : " +client.getInetAddress().getHostName() );
	} 
	
	private void getStreams() throws IOException
	{
		output = new ObjectOutputStream( client.getOutputStream() );      
		output.flush(); 

		input = new ObjectInputStream( client.getInputStream() );
	} 

	private void processConnection() throws IOException
	{
		do 
		{ 
			try 
			{
				nachricht = ( String ) input.readObject(); 
				zeigtNachricht( "\n" + nachricht ); 
				if (nachricht.contains("fail") || nachricht.contains("Warten")){
					buttons.setVisible(false);				
				}
			} 
			catch ( ClassNotFoundException classNotFoundException ) 
			{
				zeigtNachricht( "\nUnknown object type received" );
			} 
		} while ( !nachricht.equals( null ) );
	}

	private void closeConnection() 
	{
		zeigtNachricht( "\n Verbindung beendet" );
		try 
		{
			output.close(); 
			input.close(); 
			client.close(); 
		} 
		catch ( IOException ioException ) 
		{} 
	} 

	// Schickt eine Nachricht ueber output
	private void streamOut(String nachricht )
	{
		try 
		{
			output.writeObject( nachricht );
			output.flush(); 	
		}
		catch ( IOException ioException )
		{
			displayArea.append( "\n Fehler beim Senden im Stream" );
		} 
	} 

	private void zeigtNachricht( final String nachricht )
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run() 
					{
						displayArea.append( nachricht );
					} 
				}  
				); 
	} 

}
