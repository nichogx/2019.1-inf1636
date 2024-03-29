package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import logica.*;
import observer.*;
import sun.security.jca.GetInstance;

@SuppressWarnings("serial")
public class PBanco extends JPanel implements MouseListener, ObservadorIF {

	private FBanco frame = null;

	private Image tabImg = null;
	private Image[] dadosFaces = new Image[6];
	private Image[] pinImgs;
	private Image[] imgSortes = new Image[30];
	private Image[] imgPropriedades = new Image[28];
	
	private JCheckBox roubar = null;
	private JButton bSave = null;
	
	// panel com informa��es do jogador da vez
	private FPlayerInfo playerFrame = null;
	
	private int displayCarta = -1; // a carta a mostrar

	public PBanco(FBanco frame) {
		this.frame = frame;

		// Importando imagem do tabuleiro
		try {
			tabImg = ImageIO.read(new File("resources/tabuleiro.png"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		// Importando imagens dos dados
		for (int i = 0; i < 6; i++) {
			try {
				dadosFaces[i] = ImageIO.read(new File("resources/dados/die_face_" + (i + 1) + ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		// Importando imagens das sortes (cartas)
		for (int i = 0; i < 30; i++) {
			try {
				imgSortes[i] = ImageIO.read(new File("resources/sorteReves/chance" + (i + 1) + ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		
		// Importando imagens das propriedades
		for (int i = 0; i < 28; i++) {
			try {
				imgPropriedades[i] = ImageIO.read(new File("resources/propriedades/propriedade" + (i + 1) + ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		// Importando imagens dos pinos
		int numPlayers = CtrlRegras.getInstance().getNumPlayers();
		pinImgs = new Image[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			try {
				pinImgs[i] = ImageIO.read(new File("resources/pinos/pin" + i + ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		this.addMouseListener(this);
		this.setLayout(null);
		playerFrame = new FPlayerInfo();
		
		// Desenhar checkbox roubar
		roubar = new JCheckBox("Escolher valor dos dados");
		roubar.setBounds(105, 570, 180, 30);
		roubar.setOpaque(false);
		this.add(roubar);
		
		// Desenhar bot�o de passar a vez
		JButton bPass = new JButton("Passar Vez");
		bPass.setBounds(frame.LARG_DEFAULT/2 - 150/2, 540, 150, 30);
		bPass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CtrlRegras.getInstance().passaVez();
			}
		});
		this.add(bPass);
		
		// Desenhar bot�o de salvar
		bSave = new JButton("Salvar");
		bSave.setBounds(frame.LARG_DEFAULT/2 + 150/2 + 10, 540, 75, 30);
		bSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CtrlRegras.getInstance().savegame();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Erro: estado do jogo n�o p�de ser escrito no arquivo.");
				}
			}
		});
		this.add(bSave);
		
		// Desenhar bot�o de finalizar jogo
		JButton bFin = new JButton("Finalizar");
		bFin.setBounds(frame.LARG_DEFAULT/2 - 150/2 - 10 - 100, 540, 100, 30);
		bFin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CtrlRegras.getInstance().endgame();
			}
		});
		this.add(bFin);
		
		// subscribe no observado
		CtrlRegras.getInstance().add(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// deixa texto mais bonito
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


		// desenha tabuleiro
		g2d.drawImage(this.tabImg, 0, 0, null);

		// desenhar dados
		Image d1 = this.dadosFaces[CtrlRegras.getInstance().getFaceDado(0) - 1];
		Image d2 = this.dadosFaces[CtrlRegras.getInstance().getFaceDado(1)- 1];
		int size = (int) (d1.getWidth(null) * 0.25);
		g2d.drawImage(
				d1, (int) (frame.LARG_DEFAULT/2 - size * 1.2), frame.ALT_DEFAULT/2 + size,
				size, size,
				null
		);

		g2d.drawImage(
				d2, (int) (frame.LARG_DEFAULT/2 + size * 0.2), frame.ALT_DEFAULT/2 + size,
				size, size,
				null
		);
		
		// Habilitar/desabilitar salvamento
		if (CtrlRegras.getInstance().cansave()) {
			bSave.setEnabled(true);
		} else {
			bSave.setEnabled(false);
		}
		
		// Desenhar carta
		if (displayCarta > -1) { // tem uma carta para display
			g2d.drawImage(imgSortes[displayCarta], 380, 150, null);
		} else if(displayCarta < -1) {
			g2d.drawImage(imgPropriedades[-displayCarta-2], 350, 150, null);
			String[] info = CtrlRegras.getInstance().getPropriedadeInfo(-displayCarta-2);
			g2d.setFont(new Font("Consolas", Font.PLAIN, 18));
			g2d.drawString("Propriet�rio da", 115, 340);
			g2d.drawString("Propriedade Atual:", 115, 360);
			g2d.drawString(""+info[0], 115, 380);
			if(info.length > 1) {
				g2d.drawString("Casas: "+ info[1], 115, 400);
				g2d.drawString("Hotel: "+ info[2], 115, 420);
			}
		}
		
		// Escrever vez de quem
		g2d.setFont(new Font("Arial", Font.PLAIN, 18));
		g2d.drawString("� a vez de: Jogador", 110, 130);
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		g2d.setColor(CtrlRegras.getInstance().getPlayerInfo().getCorObj());
		g2d.drawString(CtrlRegras.getInstance().getPlayerInfo().getCor().toUpperCase(), 275, 130);
		g2d.setColor(new Color(0));

		// desenhar jogadores e escrever dinheiro
		int sizeX = (int) (pinImgs[0].getWidth(null) * 0.5);
		int sizeY = (int) (pinImgs[0].getHeight(null) * 0.5);

		g2d.setFont(new Font("Consolas", Font.PLAIN, 18));
		CtrlRegras ctrl = CtrlRegras.getInstance();
		// dinheiro do banco
		g2d.drawString(String.format("Banco: $%5d.00", ctrl.getBankMoney()), 110, 150);
		// dinheiro dos jogadores
		for (int i = 0; i < ctrl.getNumPlayers(); i++) {
			g2d.drawString(String.format("%-9s $%5d.00", ctrl.getPlayerInfo(i).getCor() + ":", ctrl.getPlayerInfo(i).getMoney()), 110, 170 + 20 * i);
			g2d.drawImage(pinImgs[i],
					ctrl.getPlayerInfo(i).getPosX() + (sizeX + 5) * (i % 3),
					ctrl.getPlayerInfo(i).getPosY() + (sizeY * (i / 3 % 2 + 1)),
					sizeX, sizeY, null
			);
		}
		// parte inferior x= 100 -> 152 ... 155 -> 207 ...; y= 621 -> 691. (mais ou menos isso aqui) (pode ser util para a clickbox)
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// se clicar nos dados
		int dsize = (int) (this.dadosFaces[0].getWidth(null) * 0.25);
		if (x > (int) (frame.LARG_DEFAULT/2 - dsize * 1.2) && y > frame.ALT_DEFAULT/2 + dsize
			&& x < (int) (frame.LARG_DEFAULT/2 - dsize * 1.2) + 180 && y < frame.ALT_DEFAULT/2 + dsize + 76) {
			// rodar vez e mostrar a nova carta
			int valDados = 0;
			if (roubar.isSelected()) {
				valDados = CtrlRegras.getInstance().rolarDadosRoubar();
			} else {
				valDados = CtrlRegras.getInstance().rolarDados();
			}

			if (valDados != 0) {
				displayCarta = CtrlRegras.getInstance().executaVez(valDados);
			}
		}

	}

	public void mouseReleased(MouseEvent e) {}

	public void notify(ObservadoIF o) {
		displayCarta = o.get(1);
		this.repaint();
	}
}
