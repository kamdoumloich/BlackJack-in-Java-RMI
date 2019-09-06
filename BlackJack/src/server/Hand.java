package server;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	private boolean bust = false;
	private int wertenSumme = 0;

	private List<String> karten;
	private List<String> aces;

	public Hand(String c1, String c2) {
		karten = new ArrayList<String>();
		aces = new ArrayList<String>();

		if (c1 == "Ace") {
			aces.add(c1);
		}
		else{
			karten.add(c1);
		}

		if (c2 == "Ace") {
			aces.add(c2);
		}
		else {
			karten.add(c2);
		}
		summeBerechnen();
	}

	public int getKartenSumme() {
		return wertenSumme;
	}
	
	public void kartenVerteilen(String karte_){
	
		if (karte_ == "Ace") {
			aces.add("Ace");
		}
		else{
			karten.add(karte_);
		}
	
		summeBerechnen();
		CheckBust();
	}

	private void summeBerechnen() {
	
		wertenSumme = 0;
		for(String c : karten){
			if (c == "Jack" || c =="Queen" || c =="King"){
				wertenSumme += 10;
			}
			else{
				wertenSumme += Integer.parseInt(c);
			}
		}
	
		for(String a : aces){
			if (wertenSumme <= 10){
				wertenSumme += 11;
			}
			else { 
				wertenSumme += 1;
			}
	
		}
	}

	public boolean CheckBust(){
		if(wertenSumme > 21){
			bust = true;
		}
		else {
			bust = false;
		}
		return bust;
	}

}
