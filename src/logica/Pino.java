package logica;

class Pino {
	private int casa = 0;

	int getCasa() {
		return casa;
	}
	
	void pinoAnda(int res_dados) {
		casa = (casa + res_dados)%40;
	}
}
