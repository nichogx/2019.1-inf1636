package logica.componentes;

import java.util.Random;

public class Dado {
	private int face = 6;
	
	public int roll() {
		Random r = new Random();
		
		this.face = r.nextInt(6) + 1;
		
		return this.face; // inteiro entre 1 e 6
	}
	
	public void forceFace(int face) {
		this.face = face;
	}
	
	public int getFace() {
		return this.face;
	}
}
