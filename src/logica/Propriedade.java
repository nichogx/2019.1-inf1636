package logica;

public class Propriedade {
	private int preco;
	int[] aluguel;
	private int proprietario = 0;
	
	Propriedade(int cost, int[] loan) {
		preco = cost;
		aluguel = loan;		
	}
	
	public int getProprietario() {
		return proprietario;
	}
	
	public int getAluguel() {
		return 1;
	}
	
	public int getAluguel(int resDados) {
		return 1;
	}
	
	public int getPreco() {
		return preco;
	}
	
	public void setProprietario(int player) {
		proprietario = player;
	}
	
}
