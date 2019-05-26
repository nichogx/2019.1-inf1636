package logica;

public class Pino {
	private int casa = 0;

	protected int getCasa() {
		return casa;
	}
	
	protected void pinoAnda(int res_dados) {
		casa = (casa + res_dados)%40;
	}
}
