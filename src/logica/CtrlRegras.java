package logica;

import javax.swing.JOptionPane;

public class CtrlRegras {
	
	public final int starterMoney = 1000;

	private int numPlayers = 1;
	private Jogador[] players = null;
	
	private Dado[] dados = new Dado[2];
	
	private int vez = 0;
	private String coresJogadores[] = {"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Cinza"};
	
	public CtrlRegras() {
		
		// Escolhendo o numero de jogadores
		while(true) {
			String nplay = JOptionPane.showInputDialog("Numero de Jogadores");
		
			try { 
				numPlayers = Integer.parseInt(nplay);
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		
			if(numPlayers > 0 && numPlayers <= 6) {
				break;
			}
			JOptionPane.showMessageDialog(null,"Insira um numero valido de jogadores (1 a 6)");
		}
				
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
	
	public String getVezCor() {
		return coresJogadores[vez];
	}
}
