package componentes.jogador;

class Pino {
	private int casa = 0;

	int getCasa() {
		return casa;
	}
	
	void irPara(int novaCasa) {
		casa = novaCasa;
	}
	
	void mover(int res_dados) {
		casa = (casa + res_dados) % 40;
	}
}
