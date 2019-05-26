package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import regras.*;
import logica.*;
 
public class PBanco extends JPanel implements MouseListener {
	
	private FBanco frame = null;

	private CtrlRegras ctrl = null;
	
	private int starterMoney = 1000;
	
	private Dado[] dados = new Dado[2];
	private Jogador[] players;
	
	private Image tabImg = null;
	private Image[] dadosFaces = new Image[6];
	private Image[] pinImgs;
	
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
		
		// Seta número de jogadores
		int numPlayers = 1;
		Jogador.setNumPlayers(numPlayers);
		
		// Importando imagens dos pinos
		pinImgs = new Image[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			try {
				pinImgs[i] = ImageIO.read(new File("resources/pinos/pin" + i + ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		
		dados[0] = new Dado();
		dados[1] = new Dado();
		
		// Criando jogadores
		players = new Jogador[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			players[i] = new Jogador(starterMoney);
		}
		
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.drawImage(this.tabImg, 0, 0, null);
				
		// desenhar dados
		Image d1 = this.dadosFaces[dados[0].getFace() - 1];
		Image d2 = this.dadosFaces[dados[1].getFace() - 1];
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
		
		// desenhar jogadores
		int sizeX = (int) (pinImgs[0].getWidth(null) * 0.5);
		int sizeY = (int) (pinImgs[0].getHeight(null) * 0.5);
		
		for (int i = 0; i < Jogador.getNumPlayers(); i++)
			g2d.drawImage(pinImgs[i], players[i].getPosX()+(sizeX+5)*(i%3), players[i].getPosY()+(sizeY*(i/3%2+1)), sizeX, sizeY, null);
		// parte inferior x= 100 -> 152 ... 155 -> 207 ...; y= 621 -> 691. (mais ou menos isso aqui) (pode ser útil para a clickbox)
		
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
	
		int dsize = (int) (this.dadosFaces[0].getWidth(null) * 0.25);
		if (x > (int) (frame.LARG_DEFAULT/2 - dsize * 1.2) && y > frame.ALT_DEFAULT/2 + dsize
			&& x < (int) (frame.LARG_DEFAULT/2 - dsize * 1.2) + 180 && x < frame.ALT_DEFAULT/2 + dsize + 76) {
			
			dados[0].roll();
			dados[1].roll();
			
			players[Jogador.getVez()].jogAnda(dados[0].getFace()+dados[1].getFace());
			
			Jogador.vezProx();
			
			this.repaint();
		}
		
		// System.out.printf("x = %d, y = %d\n", x,y); Para encontrar a posição em um determinado ponto
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
