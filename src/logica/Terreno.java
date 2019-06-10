package logica;

class Terreno extends Propriedade {
	
	private int casa = 0;
	private int hotel = 0;
	
	Terreno(int cost, int[] loan) {
		super(cost, loan);
	}
	
	public int getAluguel() {
		return aluguel[casa + hotel];
	}
}
