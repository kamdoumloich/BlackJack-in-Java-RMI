package main;

import javax.swing.JFrame;

import client.Client;

public class PlayerMain {
	
	public static void main( String[] args )
	   {
	      Client client; 
	      
	      if ( args.length == 0 )
	    	  client = new Client( "127.0.0.1" );
	      else
	    	  client = new Client( args[ 0 ] ); 

	      client.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	      client.runClient(); 
	   }
}
