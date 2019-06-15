package logica.componentes.propriedades;

public abstract class Propriedade {
	private int preco;
	int[] aluguel;
	private int proprietario = -1;
	private String nome;
	
	Propriedade(int cost, int[] loan, String nome) {
		preco = cost;
		aluguel = loan;
		this.nome = nome;
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
	
	public String getNome() {
		return this.nome;
	}
}
