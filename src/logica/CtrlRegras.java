package logica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JOptionPane;

public class CtrlRegras {

	public final int starterMoney = 1000;
	public int bankMoney = 50_000;

	private int numPlayers = 1;
	private Jogador[] players = null;

	private Dado[] dados = new Dado[2];

	private int vez = 0;
	
	public ArrayList<Integer> cartasSortes = new ArrayList<Integer>();
	public int[] sortes = { // especiais: 9 [8], 11 [10], 23 [22]
			25, 150, 80, 200, 50,
			50, 100, 100, 0, 200,
			0, 45, 100, 100, 20,
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
	
	public int getAtual() {
		return vez;
	}

	public void iniciaVez() {
		int roll1 = dados[0].roll();
		int roll2 = dados[1].roll();
		
		if (players[vez].isPreso()) {
			if (roll1 == roll2) {
				players[vez].release();
			}
		} else {
			Jogador player = players[vez];
			player.movePino(roll1 + roll2);
			
			int casa = player.getCasa();
			
			Integer[] casasSorte = {2, 12, 16, 22, 27, 37};
			int casaGanha = 18;
			int casaPerde = 24;
			int casaPrisao = 30;
			if (casa == casaGanha) {
				player.modifyMoney(200);
			} else if (casa == casaPerde) {
				player.modifyMoney(-200);
			} else if (casa == casaPrisao) {
				player.irPrisao();
			} else if (Arrays.asList(casasSorte).contains(casa)) {
				// TODO display carta (condi��o de retorno para o panel?)
				execNextCarta();
			}
		}

		int vezInicial = vez;
		// passa a vez pro pr�ximo
		vez = (vez + 1) % numPlayers;
		while (players[vez].getMoney() <= 0) { // est� falido, passa pro pr�ximo
			if (vezInicial == vez) { // fez loop e o atual est� falido
				// TODO fim de jogo, todos faliram
				// n�o deve acontecer, pois se todos falirem menos um este ganhou
			}
			vez = (vez + 1) % numPlayers; // passa pro outro
		}
	}

	public Jogador getPlayer(int index) {
		return players[index];
	}

	public int getFaceDado(int index) {
		return dados[index].getFace();
	}
	
	/**
	 * @return boolean true se jogador ainda tiver dinheiro, false se ele falir
	 */
	private boolean execNextCarta() {
		int atual = cartasSortes.remove(0);
		
		// verifica especiais
		if (atual == 8) { // sair da pris�o
			players[vez].darCartaSair();
			
			// carta fica com o jogador, n�o vai pro fim da lista
		} else if (atual == 10) { // receber 50 de cada um
			for (int i = 0; i < numPlayers; i++) {
				if (players[i].getMoney() > 0) {
					players[i].modifyMoney(-50);
					players[vez].modifyMoney(50);
				}
			}
			
			// coloca a carta no fim da lista
			cartasSortes.add(atual);
		} else if (atual == 22) { // ir para pris�o 
			players[vez].irPrisao();
			
			// coloca a carta no fim da lista
			cartasSortes.add(atual);
		} else {
			// modifica dinheiro do jogador
			players[vez].modifyMoney(sortes[atual]);
			
			// coloca a carta no fim da lista
			cartasSortes.add(atual);
		}
		
		
		if (players[vez].getMoney() > 0) {
			return true;
		} return false;
	}
	
	/**
	 * 
	 * @return array de posi��es do jogador (primeiro � ganhador)
	 */
	public int[] endgame() {
		// n�o d� pra usar sort em int[] com fun��o, s� em Integer[]
		Integer[] sorted = new Integer[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			sorted[i] = i;
		}

		Arrays.sort(sorted, (a, b) -> {
			// TODO aqui s� compara o dinheiro. Comparar propriedades tamb�m
			// ("vender" tudo para contar??)
			return players[b].getMoney() - players[a].getMoney();
		});
		
		// converter para tipo primitivo int[]
		int[] ret = new int[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			ret[i] = sorted[i];
		}
		return ret;
	}
}
