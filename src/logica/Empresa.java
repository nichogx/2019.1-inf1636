package logica;

class Empresa extends Propriedade {
	
	Empresa(int cost, int[] loan, String name) {
		super(cost, loan, name);
	}
	
	public int getAluguel(int resDados) {
		return aluguel[0] * resDados;
	}
}
