package logica;

class Empresa extends Propriedade {
	
	Empresa(int cost, int[] loan) {
		super(cost, loan);
	}
	
	public int getAluguel(int resDados) {
		return aluguel[0] * resDados;
	}
}
