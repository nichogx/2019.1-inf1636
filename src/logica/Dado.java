package logica;

import java.util.Random;

public class Dado {
	private int face = 6;
	
	protected int roll() {
		Random r = new Random();
		
		this.face = r.nextInt(6) + 1;
		
		return this.face; // inteiro entre 1 e 6
	}
	
	protected int getFace() {
		return this.face;
	}
}
