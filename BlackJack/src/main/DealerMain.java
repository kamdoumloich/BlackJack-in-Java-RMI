package main;
import javax.swing.JFrame;

import server.Server;

public class DealerMain {
	public static void main( String[] args )
	   {
	      Server server = new Server(); 
	      server.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	      server.runDeal(); 
	   } 
}
