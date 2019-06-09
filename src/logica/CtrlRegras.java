package logica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JOptionPane;

public class CtrlRegras {

	public final int starterMoney = 2458;
	public int bankMoney = 50_000;

	private int numPlayers = 1;
	private Jogador[] players = null;

	private Dado[] dados = new Dado[2];
	
	private Propriedade[] propriedade = FabricaPropriedades.cria();

	private int vez = 0;
	private boolean podeRolarDado = true;
	private int vezesDadosIguais = 0;
	
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
	
	public void passaVez() {
		int vezInicial = vez;
		// passa a vez pro próximo
		vez = (vez + 1) % numPlayers;
		while (players[vez].getMoney() <= 0) { // está falido, passa pro próximo
			if (vezInicial == vez) { // fez loop e o atual está falido
				// TODO fim de jogo, todos faliram
				// não deve acontecer, pois se todos falirem menos um este ganhou
			}
			vez = (vez + 1) % numPlayers; // passa pro outro
		}
		
		podeRolarDado = true;
		vezesDadosIguais = 0;
	}
	
	public int rolarDados() {
		if (!podeRolarDado) {
			JOptionPane.showMessageDialog(null,"Você não pode mais rolar o dado.");
			return 0;
		}
		
		int roll1 = dados[0].roll();
		int roll2 = dados[1].roll();
		
		if (players[vez].isPreso()) {
			if (roll1 == roll2) {
				JOptionPane.showMessageDialog(null,"Você saiu da prisão!");
				players[vez].release();
			} else {
				JOptionPane.showMessageDialog(null,"Você não conseguiu sair da prisão!");
				podeRolarDado = false; // Ao não sair da prisão não pode girar o dado novamente
			}
			
			return 0;
		}
		
		if (roll1 == roll2) {
			vezesDadosIguais++;
			if (vezesDadosIguais >= 3) {
				JOptionPane.showMessageDialog(null,"Dados iguais três vezes seguidas! Você foi preso! :(");
				if (!players[vez].irPrisao()) {
					cartasSortes.add(8); // devolver carta de sair da prisão
				}
				
				podeRolarDado = false;
				
				return 0;
			}
		} else {
			podeRolarDado = false;
		}
		
		return roll1 + roll2;
	}

	public int executaVez(int dados) {		
		Jogador player = players[vez];
		player.movePino(dados);
		
		int casa = player.getCasa();
		
		Integer[] casasSorte = {2, 12, 16, 22, 27, 37};
		
		/*Integer[] casasTerreno = {
				1, 3, 4, 6, 8, 9,
				11, 13, 14, 17, 19,
				21, 23, 26, 28, 29,
				31, 33, 34, 36, 38, 39
		};
		Integer[] casasEmpresa = {5, 7, 15, 25, 32, 35};*/
		//TODO
		Integer[] casasPropriedade = {
				1, 3, 4, 5, 6, 7, 8, 9,
				11, 13, 14, 15, 17, 19,
				21, 23, 25, 26, 28, 29,
				31, 32, 33, 34, 35, 36, 38, 39
		};
		
		int casaGanha = 18;
		int casaPerde = 24;
		int casaPrisao = 30;
		
		if (casa == casaGanha) {
			JOptionPane.showMessageDialog(null,"Prêmio! Você ganhou $200 :D");
			player.modifyMoney(200);
		} else if (casa == casaPerde) {
			JOptionPane.showMessageDialog(null,"Impostos. Você perdeu $200 :(");
			player.modifyMoney(-200);
		} else if (casa == casaPrisao) {
			JOptionPane.showMessageDialog(null,"Azar! Você foi preso! :(");
			if (!player.irPrisao()) {
				cartasSortes.add(8); // devolver carta de sair da prisão
			}
		} else if (Arrays.asList(casasSorte).contains(casa)) {
			JOptionPane.showMessageDialog(null,"Você ganhou uma carta!");
			return execNextCarta();
		} else {
			for(int i = 0; i < casasPropriedade.length; i++) //TODO
				if(casasPropriedade[i] == casa)
					return execPropriedade(i);
		}
		
		return -1;
	}

	public Jogador getPlayer(int index) {
		return players[index];
	}

	public int getFaceDado(int index) {
		return dados[index].getFace();
	}
	
	/**
	 * @return int a carta que foi rodada
	 */
	private int execNextCarta() {
		int atual = cartasSortes.remove(0);
		
		// verifica especiais
		if (atual == 8) { // sair da prisão
			players[vez].darCartaSair();
			
			// carta fica com o jogador, não vai pro fim da lista
			return atual;
		} else if (atual == 10) { // receber 50 de cada um
			for (int i = 0; i < numPlayers; i++) {
				if (players[i].getMoney() > 0) {
					players[i].modifyMoney(-50);
					players[vez].modifyMoney(50);
				}
			}
		} else if (atual == 22) { // ir para prisão 
			if (!players[vez].irPrisao()) {
				cartasSortes.add(8); // devolver carta de sair da prisão
			}
		} else {
			// modifica dinheiro do jogador
			players[vez].modifyMoney(sortes[atual]);
		}
		
		// coloca a carta no fim da lista
		cartasSortes.add(atual);
		
		return atual;
	}
	
	private int execPropriedade(int prop) { //TODO executa as funções de compra e aluguel de propriedade 
		
		//if()
		return -1;
	}
	
	/**
	 * 
	 * @return array de posições do jogador (primeiro é ganhador)
	 */
	public int[] endgame() {
		// não dá pra usar sort em int[] com função, só em Integer[]
		Integer[] sorted = new Integer[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			sorted[i] = i;
		}

		Arrays.sort(sorted, (a, b) -> {
			// TODO aqui só compara o dinheiro. Comparar propriedades também
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
