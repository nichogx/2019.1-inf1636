package componentes.propriedades;

public class Terreno extends Propriedade {
	
	private int casa = 0;
	private int hotel = 0;
	private String cor = "";
	
	Terreno(int cost, int[] loan, String name, String color) {
		super(cost, loan, name);
		cor = color;
	}
	
	public int getAluguel() {
		return aluguel[casa + hotel];
	}
	
	public String getCor() {
		return cor;
	}
}
