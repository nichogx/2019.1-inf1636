package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import logica.*;

@SuppressWarnings("serial")
public class PBanco extends JPanel implements MouseListener {

	private FBanco frame = null;

	private CtrlRegras ctrl = null;

	private Image tabImg = null;
	private Image[] dadosFaces = new Image[6];
	private Image[] pinImgs;
	private Image[] imgSortes = new Image[30];
	
	private boolean displayingCarta = false;

	public PBanco(CtrlRegras c, FBanco frame) {
		ctrl = c;
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

		// Importando imagens dos pinos
		int numPlayers = ctrl.getNumPlayers();
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
		Image d1 = this.dadosFaces[ctrl.getFaceDado(0) - 1];
		Image d2 = this.dadosFaces[ctrl.getFaceDado(1)- 1];
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
		
		// Escrever vez de quem
		g2d.setFont(new Font("Arial", Font.PLAIN, 18));
		g2d.drawString("� a vez de: Jogador", 110, 130);
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		g2d.drawString(ctrl.getCor().toUpperCase(), 275, 130);

		// desenhar jogadores e escrever dinheiro
		int sizeX = (int) (pinImgs[0].getWidth(null) * 0.5);
		int sizeY = (int) (pinImgs[0].getHeight(null) * 0.5);

		g2d.setFont(new Font("Consolas", Font.PLAIN, 18));
		for (int i = 0; i < ctrl.getNumPlayers(); i++) {
			g2d.drawString(String.format("%-9s $%5d.00", ctrl.getCor(i) + ":", ctrl.getPlayer(i).getMoney()), 110, 150 + 20 * i);
			g2d.drawImage(pinImgs[i],
					ctrl.getPlayer(i).getPosX() + (sizeX + 5) * (i % 3),
					ctrl.getPlayer(i).getPosY() + (sizeY * (i / 3 % 2 + 1)),
					sizeX, sizeY, null
			);
		}
		// parte inferior x= 100 -> 152 ... 155 -> 207 ...; y= 621 -> 691. (mais ou menos isso aqui) (pode ser util para a clickbox)
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// se clicar nos dados
		int dsize = (int) (this.dadosFaces[0].getWidth(null) * 0.25);
		if (x > (int) (frame.LARG_DEFAULT/2 - dsize * 1.2) && y > frame.ALT_DEFAULT/2 + dsize
			&& x < (int) (frame.LARG_DEFAULT/2 - dsize * 1.2) + 180 && x < frame.ALT_DEFAULT/2 + dsize + 76) {

			ctrl.iniciaVez();

			this.repaint();
		}

		// System.out.printf("x = %d, y = %d\n", x,y); Para encontrar a posi��o em um determinado ponto
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}