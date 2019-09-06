package server;

import java.util.Random;

public class Deck {
	private Karten[] deck;
	private int index_karte; 
	private static final int ANZAHLDERKARTEN = 32;
	private static final Random rand = new Random();

	public Deck(){
		String[] werte = {"Ace","7","8","9","10","Jack","Queen","King"};
		deck = new Karten[ANZAHLDERKARTEN];
		index_karte = 0;

		for(int i=0; i<deck.length; i++){
			deck[i] = new Karten(werte[i%8]);
		}
	}

	// Hier werden die Karten gemischt.
	public void mix(){
		index_karte = 0;

		for(int i = 0; i < deck.length; i++){
			int random = rand.nextInt(ANZAHLDERKARTEN);
			Karten tmp = deck[i];
			deck[i] = deck[random];
			deck[random] = tmp;
		}
	}
	
	public String karteZiehen(){
		
		if(index_karte < deck.length){
			return deck[index_karte++].toString();
		}
		else{
			return null;
		}
	}
}
