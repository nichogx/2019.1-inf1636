package logica;

public abstract class Propriedade {
	private int preco;
	int[] aluguel;
	private int proprietario = -1;
	
	Propriedade(int cost, int[] loan) {
		preco = cost;
		aluguel = loan;
	}
	
	public int getProprietario() {
		return proprietario;
	}
	
	public int getPreco() {
		return preco;
	}
	
	public void setProprietario(int player) {
		proprietario = player;
	}
	
}
