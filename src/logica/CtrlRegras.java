package logica;

public class CtrlRegras {
	
	public final int starterMoney = 1000;

	private int numPlayers = 1;
	private Jogador[] players = null;
	
	private Dado[] dados = new Dado[2];
	
	private int vez = 0;
	
	public CtrlRegras(int numPlayers) {
		this.numPlayers = numPlayers;
				
		// Criando jogadores
		players = new Jogador[this.numPlayers];
		for (int i = 0; i < this.numPlayers; i++) {
			players[i] = new Jogador(starterMoney);
		}
		
		// Criando dados
		dados[0] = new Dado();
		dados[1] = new Dado();
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public void iniciaVez() {
		int roll1 = dados[0].roll();
		int roll2 = dados[1].roll();
		
		players[vez].movePino(roll1 + roll2);
		
		vez = (vez + 1) % numPlayers;
	}
	
	public Jogador getPlayer(int index) {
		return players[index];
	}
	
	public int getFaceDado(int index) {
		return dados[index].getFace();
	}
}
