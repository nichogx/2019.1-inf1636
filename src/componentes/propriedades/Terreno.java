package componentes.propriedades;

public class Terreno extends Propriedade {
	
	private int preco_construcoes = 0;
	private int casa = 0;
	private int hotel = 0;
	private String cor = "";
	
	Terreno(int[] cost, int[] loan, String name, String color) {
		super(cost[0], loan, name);
		preco_construcoes = cost[1];
		cor = color;
	}
	
	public int getAluguel() {
		return aluguel[casa + hotel];
	}
	
	public String getCor() {
		return cor;
	}
	
	public int getPrecoVendaConstrucoes() {
		return preco_construcoes * (casa + hotel);
	}
	
	public int vendeConstrucoes() {
		int valor = preco_construcoes * (casa + hotel);
		casa = 0;
		hotel = 0;
		return valor;
	}
	
	public int compraCasa() {
		casa += 1;
		return preco_construcoes;
	}
	
	public int compraHotel() {
		hotel += 1;
		return preco_construcoes;
	}
	
	public int getCasas() {
		return casa;
	}
	
	public int getHotel() {
		return hotel;
	}
	
	public String genSaveString() {
		return String.format("casa %d, hotel %d", casa, hotel);
	}
	
	public void loadCasaHotel(int casa, int hotel) {
		this.casa = casa;
		this.hotel = hotel;
	}
}
