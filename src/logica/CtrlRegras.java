package logica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JOptionPane;

import logica.componentes.Dado;
import logica.componentes.jogador.Jogador;
import logica.componentes.jogador.JogadorInfo;
import logica.componentes.propriedades.*;

public class CtrlRegras {

	public final int starterMoney = 2458;
	public int bankMoney = 50000;

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
		String coresJogadores[] = {"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Cinza"};
		for (int i = 0; i < this.numPlayers; i++) {
			players[i] = new Jogador(starterMoney, coresJogadores[i]);
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
		// passa a vez pro pr�ximo
		vez = (vez + 1) % numPlayers;
		while (players[vez].getMoney() <= 0) { // est� falido, passa pro pr�ximo
			if (vezInicial == vez) { // fez loop e o atual est� falido
				// TODO fim de jogo, todos faliram
				// n�o deve acontecer, pois se todos falirem menos um este ganhou
			}
			vez = (vez + 1) % numPlayers; // passa pro outro
		}
		
		podeRolarDado = true;
		vezesDadosIguais = 0;
	}
	
	public int rolarDados() {
		if (!podeRolarDado) {
			JOptionPane.showMessageDialog(null,"Voc� n�o pode mais rolar o dado.");
			return 0;
		}
		
		int roll1 = dados[0].roll();
		int roll2 = dados[1].roll();
		
		if (players[vez].isPreso()) {
			if (roll1 == roll2) {
				JOptionPane.showMessageDialog(null,"Voc� saiu da pris�o!");
				players[vez].release();
			} else {
				JOptionPane.showMessageDialog(null,"Voc� n�o conseguiu sair da pris�o!");
				podeRolarDado = false; // Ao n�o sair da pris�o n�o pode girar o dado novamente
			}
			
			return 0;
		}
		
		if (roll1 == roll2) {
			vezesDadosIguais++;
			if (vezesDadosIguais >= 3) {
				JOptionPane.showMessageDialog(null,"Dados iguais tr�s vezes seguidas! Voc� foi preso! :(");
				if (!players[vez].irPrisao()) {
					cartasSortes.add(8); // devolver carta de sair da pris�o
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
		
		// Integer[] casasEmpresa = {5, 7, 15, 25, 32, 35}; // O resto s�o terrenos
		
		Integer[] casasPropriedade = {
				1, 3, 4, 5,
				6, 7, 8, 9,
				11, 13, 14, 15,
				17, 19,
				21, 23, 25,
				26, 28, 29,
				31, 32, 33, 34, 35, 36,
				38, 39
		};
		
		int casaGanha = 18;
		int casaPerde = 24;
		int casaPrisao = 30;
		
		if (casa == casaGanha) {
			JOptionPane.showMessageDialog(null,"Pr�mio! Voc� ganhou $200 :D");
			player.modifyMoney(200);
		} else if (casa == casaPerde) {
			JOptionPane.showMessageDialog(null,"Impostos. Voc� perdeu $200 :(");
			player.modifyMoney(-200);
		} else if (casa == casaPrisao) {
			JOptionPane.showMessageDialog(null,"Azar! Voc� foi preso! :(");
			if (!player.irPrisao()) {
				cartasSortes.add(8); // devolver carta de sair da pris�o
			}
		} else if (Arrays.asList(casasSorte).contains(casa)) {
			JOptionPane.showMessageDialog(null,"Voc� ganhou uma carta!");
			return execNextCarta();
		} else {
			for(int i = 0; i < casasPropriedade.length; i++)
				if(casasPropriedade[i] == casa)
					return execPropriedade(i);
		}
		
		return -1;
	}
	
	public JogadorInfo getPlayerInfo() {
		return players[vez];
	}

	public JogadorInfo getPlayerInfo(int index) {
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
		if (atual == 8) { // sair da pris�o
			players[vez].darCartaSair();
			
			// carta fica com o jogador, n�o vai pro fim da lista
			return atual;
		} else if (atual == 10) { // receber 50 de cada um
			for (int i = 0; i < numPlayers; i++) {
				if (players[i].getMoney() > 0) {
					players[i].modifyMoney(-50);
					players[vez].modifyMoney(50);
				}
			}
		} else if (atual == 22) { // ir para pris�o 
			if (!players[vez].irPrisao()) {
				cartasSortes.add(8); // devolver carta de sair da pris�o
			}
		} else {
			// modifica dinheiro do jogador
			players[vez].modifyMoney(sortes[atual]);
		}
		
		// coloca a carta no fim da lista
		cartasSortes.add(atual);
		
		return atual;
	}
	
	/**
	 * @return int o oposto do �ndice menos 2 da propriedade em que o jogador est�
	 */
	private int execPropriedade(int prop) { //TODO executa as fun��es de compra e aluguel de propriedade 
		
		if(propriedade[prop].getProprietario() != -1) {
			if (propriedade[prop] instanceof Empresa) {
				boolean playerFaliu = false;
				int aluguel = ((Empresa)propriedade[prop]).getAluguel(dados[0].getFace()+dados[1].getFace());
				int resultado = players[vez].modifyMoney(-aluguel);
				while(resultado < 0 && !playerFaliu) {
					int resultadoAnt = resultado;
					JOptionPane.showMessageDialog(null, "Voc� n�o tem dinheiro o suficiente para pagar o aluguel ($"+aluguel+") da propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+". Venda uma de suas propriedades para compensar a falta de dinheiro ($"+(-resultado)+").");
					resultado = execVendaProp();
					
					if (resultado == resultadoAnt) {
						playerFaliu = true;
					}
				}
				
				if(!playerFaliu)
					JOptionPane.showMessageDialog(null, "Voc� pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor());
				else
					JOptionPane.showMessageDialog(null, "Voc� n�o pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+" e por isso voc� faliu!");
			
			} else {
				boolean playerFaliu = false;
				int aluguel = ((Terreno)propriedade[prop]).getAluguel();
				int resultado = players[vez].modifyMoney(-aluguel);
				while(resultado < 0 && !playerFaliu) {
					int resultadoAnt = resultado;
					JOptionPane.showMessageDialog(null, "Voc� n�o tem dinheiro o suficiente para pagar o aluguel ($"+aluguel+") da propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+". Venda uma de suas propriedades para compensar a falta de dinheiro ($"+(-resultado)+").");
					resultado = execVendaProp();
					
					if (resultado == resultadoAnt) {
						playerFaliu = true;
					}
				}
				
				if(!playerFaliu)
					JOptionPane.showMessageDialog(null, "Voc� pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor());
				else
					JOptionPane.showMessageDialog(null, "Voc� n�o pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+" e por isso voc� faliu!");
			}
			
		} else {
			String[] options = {"Sim", "Nao"};
			int resp = JOptionPane.showOptionDialog(null, "Deseja comprar a propriedade: "+propriedade[prop].getNome()+" por $"+propriedade[prop].getPreco()+"?",
									"Click a button", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if(resp == 0) {
				if(players[vez].getMoney() < propriedade[prop].getPreco())
				{
					JOptionPane.showMessageDialog(null, "Voc� n�o tem dinheiro suficiente para comprar a propriedade: "+propriedade[prop].getNome()+", pois ela custa $"+propriedade[prop].getPreco());
				} else {
					players[vez].modifyMoney(-propriedade[prop].getPreco());
					propriedade[prop].setProprietario(vez);
					players[vez].compraPropriedade(prop);
					JOptionPane.showMessageDialog(null, "Voc� comprou a propriedade: "+propriedade[prop].getNome()+" por $"+propriedade[prop].getPreco());
				}
			}
			
		}
		
		
		return -prop-2;
	}
	
	public int execVendaProp() {
		return 0;
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
			// (acho que sim, parece uma boa e se nao me engano o Ivan falou isso)
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
