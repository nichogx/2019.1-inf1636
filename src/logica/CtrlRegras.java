package logica;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import componentes.Dado;
import componentes.jogador.Jogador;
import componentes.jogador.JogadorInfo;
import componentes.propriedades.*;
import observer.ObservadoIF;
import observer.ObservadorIF;

public class CtrlRegras implements ObservadoIF {
	
	private static CtrlRegras instance = null;
	private ArrayList<ObservadorIF> observadores = new ArrayList<ObservadorIF>();

	public final int starterMoney = 2458;
	public int bankMoney = 50000;

	private int numPlayers = -1;
	private Jogador[] players = null;

	private Dado[] dados = new Dado[2];
	
	private Propriedade[] propriedade = FabricaPropriedades.cria();

	private int vez = 0;
	private boolean podeRolarDado = true;
	private boolean jaIniciou = false; // savegame
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

	private CtrlRegras() {
		
		// Escolher nova partida ou load de jogo salvo
		String[] optionsGame = {"Nova Partida", "Continuar"};
		int optGame = JOptionPane.showOptionDialog(null, "Iniciar uma nova partida ou continuar de jogo salvo?", 
				"Iniciar Partida", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsGame, optionsGame[0]);
		
		if (optGame == -1) {
			// sair caso tenha clicado X
			System.exit(0);
		} else if (optGame == 0) { // Nova partida
			// Escolhendo o numero de jogadores
			String[] optionsPlayers = {"2 Jogadores", "3 Jogadores", "4 Jogadores", "5 Jogadores", "6 Jogadores"};
			JComboBox<String> cbbox = new JComboBox<String>(optionsPlayers);
			Object[] cbboxDisplay = {"Escolha o número de jogadores:", cbbox};
			int esc = JOptionPane.showOptionDialog(null, cbboxDisplay, "Número de Jogadores",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			if (esc == JOptionPane.OK_OPTION) {
				numPlayers = cbbox.getSelectedIndex() + 2; // optionsPlayers[0] -> 2 Jogadores
			} else {
				// sair se clicou cancel/X
				System.exit(0);
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
		} else { // Jogo Salvo
			JFileChooser fc = new JFileChooser(".");
			fc.setFileFilter(new FileNameExtensionFilter("TXT Files (*.txt)", "txt"));

			if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				// sair caso tenha clicado cancel ou X
				System.exit(0);
			}
			
			if (fc.getSelectedFile().length() > 10000) {
				JOptionPane.showMessageDialog(null, "Erro: arquivo muito grande. Provavelmente não foi gerado pelo jogo.");
				System.exit(0);
			}
			
			Scanner sc = null;
			String fStr = null;
			try {
				sc = new Scanner(fc.getSelectedFile());
				while (sc.hasNextLine()) {
					fStr += sc.nextLine();
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Erro: arquivo não encontrado.");
				System.exit(0);
			}
			
			try {
				Pattern pattern;
				Matcher matcher;
				
				// recolocando número de jogadores
				pattern = Pattern.compile("(numplayers: )(\\d+)(;)");
				matcher = pattern.matcher(fStr);
				matcher.find();
				numPlayers = Integer.parseInt(matcher.group(2));
				
				// recolocando dinheiro do banco
				pattern = Pattern.compile("(bankmoney: )(\\d+)(;)");
				matcher = pattern.matcher(fStr);
				matcher.find();
				bankMoney = Integer.parseInt(matcher.group(2));
				
				// recriando jogadores e propriedades com dono
				players = new Jogador[this.numPlayers];
				String coresJogadores[] = {"Vermelho", "Azul", "Laranja", "Amarelo", "Roxo", "Cinza"};
				ArrayList<PropOwner> propOwners = new ArrayList<PropOwner>();
				for (int i = 0; i < numPlayers; i++) {
					pattern = Pattern.compile("(\t)(player )(" + i + ")(: casa )(\\d+)(, money )(\\d+)(, cartaSair )(false|true)(, preso )(false|true)(;)(\t\t)(propriedades: )(\\[(\\d+, )*\\d*?\\]);");
					matcher = pattern.matcher(fStr);
					matcher.find();
					int casa = Integer.parseInt(matcher.group(5));
					int money = Integer.parseInt(matcher.group(7));
					boolean cartaSair = Boolean.parseBoolean(matcher.group(9));
					boolean preso = Boolean.parseBoolean(matcher.group(11));
					
					players[i] = new Jogador(money, coresJogadores[i], casa, cartaSair, preso);

					String[] arrprop = matcher.group(15).split("(, )|\\[|(\\])");
					if (arrprop.length > 0) {
						for (String prop : Arrays.copyOfRange(arrprop, 1, arrprop.length)) {
							int pNum = Integer.parseInt(prop);
							players[i].compraPropriedade(pNum);
							propOwners.add(new PropOwner(pNum, i));
						}
					}
				}
				
				// seta os owners das propriedades novamente
				for (PropOwner i : propOwners) {
					propriedade[i.prop].setProprietario(i.owner);
				}
				
				// recuperar de quem era a vez
				pattern = Pattern.compile("(vez: )(\\d+)(;)");
				matcher = pattern.matcher(fStr);
				matcher.find();
				vez = Integer.parseInt(matcher.group(2));
				
				// recuperar as cartas de sorte reves
				pattern = Pattern.compile("(cartasSortes: )(\\[(\\d+, )*\\d*?\\]);");
				matcher = pattern.matcher(fStr);
				matcher.find();
				String[] arrcartas = matcher.group(2).split("(, )|\\[|(\\])");
				if (arrcartas.length > 0) {
					for (String carta : Arrays.copyOfRange(arrcartas, 1, arrcartas.length)) {
						cartasSortes.add(Integer.parseInt(carta));
					}
				}
			} catch (IllegalStateException e) {
				JOptionPane.showMessageDialog(null, "Erro: arquivo em formato inválido.");
				System.exit(0);
			}
			
			// recriando dados (default)
			dados[0] = new Dado();
			dados[1] = new Dado();
		}
	}
	
	public static CtrlRegras getInstance() {
		if (instance == null) {
			instance = new CtrlRegras();
		}
		
		return instance;
	}

	public int getNumPlayers() {
		return numPlayers;
	}
	
	public int getAtual() {
		return vez;
	}
	
	public void passaVez() {
		jaIniciou = false; // savegame
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
	
	public int rolarDadosRoubar() {
		if (!podeRolarDado) {
			JOptionPane.showMessageDialog(null,"Você não pode mais rolar o dado.");
			return 0;
		}
		
		String[] valDados = {"1", "2", "3", "4", "5", "6"};
		JComboBox<String> d1 = new JComboBox<String>(valDados);
		JComboBox<String> d2 = new JComboBox<String>(valDados);
		
		Object[] diags = {"Escolha valores para os dois dados\nDado 1:", d1, "Dado 2:", d2};
		int esc = JOptionPane.showOptionDialog(null, diags, "Valor dos Dados",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		
		if (esc != JOptionPane.OK_OPTION) {
			return rolarDados();
		}
		
		dados[0].forceFace(d1.getSelectedIndex() + 1);
		dados[1].forceFace(d2.getSelectedIndex() + 1);
		
		return executarDados();
	}
	
	public int rolarDados() {
		if (!podeRolarDado) {
			JOptionPane.showMessageDialog(null,"Você não pode mais rolar o dado.");
			return 0;
		}
		
		dados[0].roll();
		dados[1].roll();
		
		return executarDados();
	}
	
	private int executarDados() {
		// faz o repaint pois mudou estado dos dados
		this.notificaAll();
		
		int roll1 = dados[0].getFace();
		int roll2 = dados[1].getFace();
		
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
				boolean foiPreso = players[vez].irPrisao();
				this.notificaAll();
				JOptionPane.showMessageDialog(null,"Dados iguais três vezes seguidas! Você foi preso! :(");
				if (!foiPreso) {
					JOptionPane.showMessageDialog(null,"Você usou sua carta de sair da prisão!");
					cartasSortes.add(8); // devolver carta de sair da prisão pro deck
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
		jaIniciou = true; // savegame

		Jogador player = players[vez];
		player.movePino(dados);
		
		int casa = player.getCasa();
		
		Integer[] casasSorte = {2, 12, 16, 22, 27, 37};
		
		// Integer[] casasEmpresa = {5, 7, 15, 25, 32, 35}; // O resto são terrenos
		
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
			JOptionPane.showMessageDialog(null,"Prêmio! Você ganhou $200 :D");
			player.modifyMoney(200);
			bankMoney -= 200;
		} else if (casa == casaPerde) {
			JOptionPane.showMessageDialog(null,"Impostos. Você perdeu $200 :(");
			player.modifyMoney(-200);
			bankMoney += 200;
		} else if (casa == casaPrisao) {
			boolean foiPreso = players[vez].irPrisao();
			this.notificaAll();
			JOptionPane.showMessageDialog(null,"Azar! Você foi preso! :(");
			if (!foiPreso) {
				JOptionPane.showMessageDialog(null,"Você usou sua carta de sair da prisão!");
				cartasSortes.add(8); // devolver carta de sair da prisão
			}
			podeRolarDado = false;
		} else if (Arrays.asList(casasSorte).contains(casa)) {
			JOptionPane.showMessageDialog(null,"Você ganhou uma carta!");
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
	
	public int getBankMoney() {
		return bankMoney;
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
			boolean foiPreso = players[vez].irPrisao();
			this.notificaAll();
			if (!foiPreso) {
				JOptionPane.showMessageDialog(null,"Você usou sua carta de sair da prisão!");
				cartasSortes.add(8); // devolver carta de sair da prisão pro deck
			}
		} else {
			// modifica dinheiro do jogador
			players[vez].modifyMoney(sortes[atual]);
			bankMoney -= sortes[atual];
		}
		
		// coloca a carta no fim da lista
		cartasSortes.add(atual);
		
		return atual;
	}
	
	/**
	 * @return int o oposto do índice menos 2 da propriedade em que o jogador está
	 */
	private int execPropriedade(int prop) { //TODO executa as funções de compra e aluguel de propriedade 
		
		if(propriedade[prop].getProprietario() != -1) {
			if (propriedade[prop] instanceof Empresa) {
				boolean playerFaliu = false;
				int aluguel = ((Empresa)propriedade[prop]).getAluguel(dados[0].getFace()+dados[1].getFace());
				int resultado = players[vez].modifyMoney(-aluguel);
				while(resultado < 0 && !playerFaliu) {
					int resultadoAnt = resultado;
					JOptionPane.showMessageDialog(null, "Você não tem dinheiro o suficiente para pagar o aluguel ($"+aluguel+") da propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+". Venda uma de suas propriedades para compensar a falta de dinheiro ($"+(-resultado)+").");
					resultado = execVendaProp();
					
					if (resultado == resultadoAnt) {
						playerFaliu = true;
					}
				}
				
				if(!playerFaliu)
					JOptionPane.showMessageDialog(null, "Você pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor());
				else
					JOptionPane.showMessageDialog(null, "Você não pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+" e por isso você faliu!");
			
			} else {
				boolean playerFaliu = false;
				int aluguel = ((Terreno)propriedade[prop]).getAluguel();
				int resultado = players[vez].modifyMoney(-aluguel);
				while(resultado < 0 && !playerFaliu) {
					int resultadoAnt = resultado;
					JOptionPane.showMessageDialog(null, "Você não tem dinheiro o suficiente para pagar o aluguel ($"+aluguel+") da propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+". Venda uma de suas propriedades para compensar a falta de dinheiro ($"+(-resultado)+").");
					resultado = execVendaProp();
					
					if (resultado == resultadoAnt) {
						playerFaliu = true;
					}
				}
				
				if(!playerFaliu)
					JOptionPane.showMessageDialog(null, "Você pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor());
				else
					JOptionPane.showMessageDialog(null, "Você não pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+" e por isso você faliu!");
			}
			
		} else {
			String[] options = {"Sim", "Nao"};
			int resp = JOptionPane.showOptionDialog(null, "Deseja comprar a propriedade: "+propriedade[prop].getNome()+" por $"+propriedade[prop].getPreco()+"?",
									"Click a button", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if(resp == 0) {
				if(players[vez].getMoney() < propriedade[prop].getPreco())
				{
					JOptionPane.showMessageDialog(null, "Você não tem dinheiro suficiente para comprar a propriedade: "+propriedade[prop].getNome()+", pois ela custa $"+propriedade[prop].getPreco());
				} else {
					players[vez].modifyMoney(-propriedade[prop].getPreco());
					bankMoney += propriedade[prop].getPreco();
					propriedade[prop].setProprietario(vez);
					players[vez].compraPropriedade(prop);
					notificaAll();
					JOptionPane.showMessageDialog(null, "Você comprou a propriedade: "+propriedade[prop].getNome()+" por $"+propriedade[prop].getPreco());
				}
			}
			
		}
		
		
		return -prop-2;
	}
	
	public int execVendaProp() {
		return 0;
	}
	
	public void endgame() {
		// não dá pra usar sort em int[] com função, só em Integer[]
		Integer[] sorted = new Integer[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			sorted[i] = i;
		}

		Arrays.sort(sorted, (a, b) -> {
			// TODO aqui só compara o dinheiro. Comparar propriedades também
			// ("vender" tudo para contar??)
			// (acho que sim, parece uma boa e se nao me engano o Ivan falou isso)
			return players[b].getMoney() - players[a].getMoney();
		});
		
		String str = "FIM DE JOGO!\nPosição final dos jogadores:\n";
		int count = 1;
		for (int i = 0; i < numPlayers; i++) {
			int pl = sorted[i];
			str += String.format("%d: %-9s $%5d.00\n", count, players[pl].getCor() + " -", players[pl].getMoney());
			
			if (i + 1 < numPlayers && players[pl].getMoney() != players[sorted[i + 1]].getMoney()) {
				
				count++;
			}
		}
		
		JOptionPane.showMessageDialog(null, str);
		System.exit(0);
	}
	
	public boolean cansave() {
		return !jaIniciou;
	}
	
	public void savegame() throws IOException {
		JFileChooser fc = new JFileChooser(".");
		fc.setFileFilter(new FileNameExtensionFilter("TXT Files (*.txt)", "txt"));
		
		if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			// cancelar save caso tenha clicado cancel ou X
			return;
		}
		
		File file = fc.getSelectedFile();
		
		FileWriter writer = new FileWriter(file);
		
		writer.append("bankmoney: " + bankMoney + ";\n");
		writer.append("numplayers: " + numPlayers + ";\n");
		for (int i = 0; i < this.numPlayers; i++) {
			writer.append("\tplayer " + i + ": ");
			writer.append(players[i].genSaveString());
			writer.append(";\n");
		}
		writer.append("vez: " + vez + ";\n");
		writer.append("cartasSortes: " + cartasSortes.toString() + ";\n");
		
		writer.close();
		
		JOptionPane.showMessageDialog(null,"Jogo foi salvo!");
	}

	public void add(ObservadorIF o) {
		observadores.add(o);
	}

	public void remove(ObservadorIF o) {
		observadores.remove(o);
	}

	private void notificaAll() {
		for (ObservadorIF obs : observadores) {
			obs.notify(this);
		}
	}
}

// usada apenas para load do arquivo
// relacionar prop <-> owner
class PropOwner {
	final int prop;
	final int owner;
	
	PropOwner(int prop, int owner) {
		this.prop = prop;
		this.owner = owner;
	}
}
