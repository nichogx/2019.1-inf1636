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

	private final int starterMoney = 2458;
	private int bankMoney = 50000;
	
	private int cartaAtual = -1;

	private int numPlayers = -1;
	private Jogador[] players = null;

	private Dado[] dados = new Dado[2];
	
	private Propriedade[] propriedade = FabricaPropriedades.cria();

	private int vez = 0;
	private boolean podeRolarDado = true;
	private boolean jaIniciou = false; // savegame
	private int vezesDadosIguais = 0;
	
	private ArrayList<Integer> cartasSortes = new ArrayList<Integer>();
	private int[] sortes = { // especiais: 9 [8], 11 [10], 23 [22]
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
			
			// recriando casas e hoteis
			for (int i = 0; i < propriedade.length; i++) {
				// empresas
				Pattern pattern = Pattern.compile("(\t)(((empresa) " + i + ";)|((terreno) " + i + ": casa (\\d+), hotel (\\d+);))");
				Matcher matcher = pattern.matcher(fStr);
				matcher.find();
				if (matcher.group(6) != null && matcher.group(6).contentEquals("terreno") && propriedade[i] instanceof Terreno) {
					Terreno t = (Terreno) propriedade[i];
					int casa = Integer.parseInt(matcher.group(7));
					int hotel = Integer.parseInt(matcher.group(8));
					t.loadCasaHotel(casa, hotel);
				}
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
				JOptionPane.showMessageDialog(null,"Fim de jogo! Todos faliram.");
				endgame();
			}
			vez = (vez + 1) % numPlayers; // passa pro outro
		}
		if (vezInicial == vez) {
			// próximo não falido era ele mesmo
			JOptionPane.showMessageDialog(null,"Fim de jogo! Todos os outros jogadores faliram.");
			endgame();
		}
		
		podeRolarDado = true;
		vezesDadosIguais = 0;
		
		cartaAtual = -1;
		this.notificaAll();
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
		
		this.notificaAll();
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
		cartaAtual = cartasSortes.remove(0);
		this.notificaAll();
		
		// verifica especiais
		if (cartaAtual == 8) { // sair da prisão
			players[vez].darCartaSair();
			
			// carta fica com o jogador, não vai pro fim da lista
			return cartaAtual;
		} else if (cartaAtual == 10) { // receber 50 de cada um
			for (int i = 0; i < numPlayers; i++) {
				if (players[i].getMoney() > 0) {
					players[i].modifyMoney(-50);
					players[vez].modifyMoney(50);
				}
			}
		} else if (cartaAtual == 22) { // ir para prisão
			boolean foiPreso = players[vez].irPrisao();
			this.podeRolarDado = false;
			this.notificaAll();
			if (!foiPreso) {
				JOptionPane.showMessageDialog(null,"Você usou sua carta de sair da prisão!");
				cartasSortes.add(8); // devolver carta de sair da prisão pro deck
			}
		} else {
			// modifica dinheiro do jogador
			players[vez].modifyMoney(sortes[cartaAtual]);
			bankMoney -= sortes[cartaAtual];
		}
		
		// coloca a carta no fim da lista
		cartasSortes.add(cartaAtual);
		
		return cartaAtual;
	}
	
	/**
	 * @return int o oposto do índice menos 2 da propriedade em que o jogador está
	 */
	private int execPropriedade(int prop) { //TODO executa as funções de compra e aluguel de propriedade 
		
		if(propriedade[prop].getProprietario() != -1) {
			if(vez != propriedade[prop].getProprietario()) {
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
					
					if(!playerFaliu) {
						players[propriedade[prop].getProprietario()].modifyMoney(aluguel); // proprietario recebe aluguel
						this.notificaAll();
						JOptionPane.showMessageDialog(null, "Você pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor());
					} else {
						bankMoney += players[vez].getMoney(); // banco paga aluguel pelo falido
						players[propriedade[prop].getProprietario()].modifyMoney(aluguel); // proprietario recebe aluguel
						this.notificaAll();
						JOptionPane.showMessageDialog(null, "Você não pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+" e por isso você faliu!");
					}
						
				} else {
					boolean playerFaliu = false;
					int aluguel = ((Terreno)propriedade[prop]).getAluguel();
					int resultado = players[vez].modifyMoney(-aluguel);
					while(resultado < 0 && !playerFaliu) {
						int resultadoAnt = resultado;
						JOptionPane.showMessageDialog(null, "Você não tem dinheiro o suficiente para pagar o aluguel ($"+aluguel+") da propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+". Caso tenha, venda uma de suas propriedades para compensar a falta de dinheiro ($"+(-resultado)+").");
						resultado = execVendaProp();
						
						if (resultado == resultadoAnt) {
							playerFaliu = true;
						}
					}
					
					if(!playerFaliu) {
						players[propriedade[prop].getProprietario()].modifyMoney(aluguel); // proprietario recebe aluguel
						this.notificaAll();
						JOptionPane.showMessageDialog(null, "Você pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor());
					} else {
						bankMoney += players[vez].getMoney(); // banco paga aluguel pelo falido
						players[propriedade[prop].getProprietario()].modifyMoney(aluguel); // proprietario recebe aluguel
						this.notificaAll();
						JOptionPane.showMessageDialog(null, "Você não pagou $"+aluguel+" em aluguel para a propriedade "+propriedade[prop].getNome()+" do jogador "+players[propriedade[prop].getProprietario()].getCor()+" e por isso você faliu!");
					}
				}
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
					cartaAtual = -prop-2;
					notificaAll();
					JOptionPane.showMessageDialog(null, "Você comprou a propriedade: "+propriedade[prop].getNome()+" por $"+propriedade[prop].getPreco());
				}
			}
			
		}
		
		return -prop-2;
	}
	
	public int execVendaProp() {
		
		if(players[vez].getPropriedades().size() != 0) {
			JComboBox<String> cbVenda = new JComboBox<String>(getJogadorPropriedades());
			Object[] cbVendaDisplay = {"Escolha uma propriedade para vender:", cbVenda};
			int esc = JOptionPane.showOptionDialog(null, cbVendaDisplay, "Venda de Propriedades",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); // escolhe um propriedade para vender
			
			if (esc == JOptionPane.OK_OPTION) {
				int prop = players[vez].getPropriedades().get(cbVenda.getSelectedIndex());
				if(propriedade[prop] instanceof Empresa) { // empresas não tem verificação de construção
					propriedade[prop].setProprietario(-1);
					
					players[vez].vendePropriedade(prop);
					int preco = (propriedade[prop].getPreco()*9)/10;
					players[vez].modifyMoney(preco);
					bankMoney -= preco;
					this.notificaAll();
					JOptionPane.showMessageDialog(null, "Você vendeu a empresa "+cbVenda.getSelectedItem()+" por $"+preco);
					
				} else {
					int i = prop < 5 ? 0 : prop - 5; // pior caso: propriedade de mesma cor 5 casas do tabuleiro atrás
					int preco_construcoes = 0;
					boolean ehProprietario = true; // é proprietário do grupo de terrenos?
					ArrayList<Propriedade> subsetColor = new ArrayList<Propriedade>();
					String cor = ((Terreno)propriedade[prop]).getCor();
					
					while(i < propriedade.length) { // procura se o player é dono de todas as casas da cor do terreno
						if(propriedade[i] instanceof Terreno) {
							if(i > prop && !cor.equals(((Terreno)propriedade[i]).getCor())) {
								break;
							} else if(cor.equals(((Terreno)propriedade[i]).getCor())){
								if(propriedade[i].getProprietario() != vez) {
									ehProprietario = false;
									break;
								}
								subsetColor.add(propriedade[i]);
							}
						}
						i++;
					}
					
					if(ehProprietario) { // ele é proprietário da cor
					
						for(Propriedade unit : subsetColor) {
							preco_construcoes += ((Terreno)unit).getPrecoVendaConstrucoes();
						}
						
						String construcao = "";
						if(preco_construcoes > 0) { // significa que tem construções
							int opt = JOptionPane.showOptionDialog(null, "Ao confirmar, estará vendendo tanto a propriedade escolhida como também\n"
									+ "todas as casas do grupo de terrenos de cor "+cor+".\nDeseja prosseguir?", "Valor dos Dados",
									JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); // último aviso ao jogador
							if(opt != JOptionPane.OK_OPTION) {
								return players[vez].getMoney();
							} else {
								for(Propriedade unit : subsetColor) {
									((Terreno)unit).vendeConstrucoes();
								}
								preco_construcoes = (preco_construcoes*9)/10;
								players[vez].modifyMoney(preco_construcoes);
								bankMoney -= preco_construcoes;
								construcao = "E você vendeu todas as casas do grupo de terrenos de cor "+cor+" por $"+preco_construcoes+".";
							}
						}
						
						propriedade[prop].setProprietario(-1);
						players[vez].vendePropriedade(prop);
						int preco = (propriedade[prop].getPreco()*9)/10;
						players[vez].modifyMoney(preco);
						bankMoney -= preco;
						JOptionPane.showMessageDialog(null, "Você vendeu o terreno "+cbVenda.getSelectedItem()+" por $"+preco+".\n"+construcao);
						this.notificaAll();
						
					} else {
						
						propriedade[prop].setProprietario(-1);
						players[vez].vendePropriedade(prop);
						int preco = (propriedade[prop].getPreco()*9)/10;
						players[vez].modifyMoney(preco);
						bankMoney -= preco;
						JOptionPane.showMessageDialog(null, "Você vendeu o terreno "+cbVenda.getSelectedItem()+" por $"+preco+".");
						this.notificaAll();
					}
				}
			}
		}
		
		return players[vez].getMoney();
	}
	
	public void execCompraCasa() {
		
		ArrayList<String> propriedadesJogNome = new ArrayList<String>(); // array com nomes dos terrenos do jogador
		ArrayList<Integer> propriedadesJog = players[vez].getPropriedades(); // array com posições dos terrenos do jogador
		String[] nomesProp = getJogadorPropriedades(); // array com nomes de todas as propriedades do jogador
		
		for(int i = 0, j = 0; i < nomesProp.length; i++) { // irá tirar do array as propriedades que são Empresa
			if (propriedade[propriedadesJog.get(j)] instanceof Terreno) {
				propriedadesJogNome.add(nomesProp[i]);
			} else {
				propriedadesJog.remove(j);
				j--;
			}
			j++;
		}
		
		if(propriedadesJogNome.size() == 0) {
			JOptionPane.showMessageDialog(null, "Você tem apenas empresas. Não é possível comprar casas/hoteis nesse tipo de propriedade.");
		} else {
		
			String[] nomesProp2 = propriedadesJogNome.toArray(new String[propriedadesJogNome.size()]);
			
			JComboBox<String> cbCompra = new JComboBox<String>(nomesProp2);
			Object[] cbCompraDisplay = {"Escolha uma propriedade para comprar uma casa/hotel:", cbCompra};
			int esc = JOptionPane.showOptionDialog(null, cbCompraDisplay, "Compra de Construções",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			if (esc == JOptionPane.OK_OPTION) {
				int prop = propriedadesJog.get(cbCompra.getSelectedIndex()); // índice da propriedade escolhida
				int i = prop < 5 ? 0 : prop - 5; // pior caso: propriedade de mesma cor 5 casas atrás
				int min_construcoes = 5;
				ArrayList<Propriedade> subsetColor = new ArrayList<Propriedade>();
				String cor = ((Terreno)propriedade[prop]).getCor();
				
				
				while(i < propriedade.length) { // procura se o player é dono de todas as casas da cor do terreno e se há casas
					if(propriedade[i] instanceof Terreno) {
						if(i > prop && !cor.equals(((Terreno)propriedade[i]).getCor())) {
							break;
						} else if(cor.equals(((Terreno)propriedade[i]).getCor())){
							if(propriedade[i].getProprietario() != vez) {
								JOptionPane.showMessageDialog(null, "Não é possível fazer a compra de casas para esse terreno, "
										+ "pois você não é dono de todos os terrenos dessa cor.");
								return;
							}
							subsetColor.add(propriedade[i]);
						}
					}
					i++;
				}
				
				int num_construcoes; // numero de construções no terreno no qual deseja-se comprar casas
				for(Propriedade unit : subsetColor) {
					num_construcoes = ((Terreno)unit).getCasas() + ((Terreno)unit).getHotel(); // uso auxiliar de num_construcoes
					min_construcoes = min_construcoes > num_construcoes ? num_construcoes : min_construcoes;
				}
				
				num_construcoes = ((Terreno) propriedade[prop]).getCasas()+((Terreno) propriedade[prop]).getHotel();
				
				if(num_construcoes == 5) {
					JOptionPane.showMessageDialog(null, "Esse terreno já tem o número máximo de casas e hoteis.");
				} else if(num_construcoes == min_construcoes) {
					int preco;
					String tipo;
					if(num_construcoes == 4) {
						preco = ((Terreno)propriedade[prop]).compraHotel();
						tipo = " hotel";
					} else {
						preco = ((Terreno)propriedade[prop]).compraCasa();
						tipo = "a casa";
					}
					
					players[vez].modifyMoney(-preco);
					bankMoney += preco;
					this.notificaAll();
					JOptionPane.showMessageDialog(null, "Você comprou um"+tipo+" no terreno "+cbCompra.getSelectedItem()+" por $"+preco);
				} else {
					JOptionPane.showMessageDialog(null, "Você deve ter um número igual de casas ou hoteis nos outros terrenos dessa cor"
							+ " antes de poder comprar mais casas ou hoteis.");
				}
				
			}
		}
		
	}
	
	private void execVenderTodasPropriedades(int numJog) {
		
		ArrayList<Integer> properties = players[numJog].getPropriedades(); // todas as propriedades do jogador
		int total = 0;
		while(properties.size() != 0) {
			int index = properties.remove(0); // pega o índice da propriedade
			propriedade[index].setProprietario(-1); // muda o proprietário para o banco
			players[numJog].vendePropriedade(index); // retira do proprietário a propriedade
			if(propriedade[index] instanceof Terreno)
				total += (((Terreno)propriedade[index]).vendeConstrucoes()*9)/10; // vende construções se for terreno
			total += (propriedade[index].getPreco()*9)/10; // vende propriedade
		}
		
		players[numJog].modifyMoney(total);
		bankMoney -= total;
		this.notificaAll();
	}
	
	public String[] getJogadorPropriedades() {
		ArrayList<Integer> propriedadesIndex = players[vez].getPropriedades();
		String[] nomesProps = new String[propriedadesIndex.size()];
		
		for(int i = 0; i < propriedadesIndex.size(); i++) {
			nomesProps[i] = propriedade[propriedadesIndex.get(i)].getNome();
		}
		
		return nomesProps;
	}
	
	public void endgame() {

		// não dá pra usar sort em int[] com função, só em Integer[]
		Integer[] sorted = new Integer[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			sorted[i] = i;
			
			// só se jogador não estiver falido
			if (players[i].getMoney() >= 0) {
				execVenderTodasPropriedades(i);
			}
		}

		Arrays.sort(sorted, (a, b) -> {
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
		writer.append("propriedades: " + propriedade.length + ";\n");
		for (int i = 0; i < propriedade.length; i++) {
			Propriedade p = propriedade[i];
			if (p instanceof Terreno) {
				writer.append("\tterreno " + i + ": ");
				Terreno t = (Terreno) p;
				writer.append(t.genSaveString());
				writer.append(";\n");
			} else {
				writer.append("\tempresa " + i + ";\n");
			}
		}
		
		writer.close();
		
		JOptionPane.showMessageDialog(null,"Jogo foi salvo!");
	}

	public void add(ObservadorIF o) {
		observadores.add(o);
	}

	public void remove(ObservadorIF o) {
		observadores.remove(o);
	}
	
	public int get(int var) {
		if (var == 1) {
			return cartaAtual;
		}
		return -1;
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
