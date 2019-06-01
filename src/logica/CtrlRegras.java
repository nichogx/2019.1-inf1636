package logica;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

public class CtrlRegras {

	public final int starterMoney = 1000;

	private int numPlayers = 1;
	private Jogador[] players = null;

	private Dado[] dados = new Dado[2];

	private int vez = 0;
	private String coresJogadores[] = {"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Cinza"};
	
	public ArrayList<Integer> cartasSortes = new ArrayList<Integer>();
	public int[] sortes = { // especiais: 9 [8], 11 [10], 23 [22]
			25, 150, 80, 200, 50,
			50, 100, 100, 0, 200,
			50 * (numPlayers - 1), 45, 100, 100, 20,
			-15, -25, -45, -30, -100,
			-100, -40, 0, -30, -50,
			-25, -30, -45, -50, -50
	};

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
		
		// preenche e faz shuffle das cartas
		for (int i = 0; i < 30; i++) {
			cartasSortes.add(i);
		}
		Collections.shuffle(cartasSortes);
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

	public String getCor() {
		return coresJogadores[vez];
	}
	
	public String getCor(int index) {
		return coresJogadores[index];
	}
}
