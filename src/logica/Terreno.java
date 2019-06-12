package logica;

class Terreno extends Propriedade {
	
	private int casa = 0;
	private int hotel = 0;
	private String cor = "";
	
	Terreno(int cost, int[] loan, String color) {
		super(cost, loan);
		cor = color;
	}
	
	public int getAluguel() {
		return aluguel[casa + hotel];
	}
	
	public String getCor() {
		return cor;
	}
}
