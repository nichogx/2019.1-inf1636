package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import regras.*;
import dado.*;
 
public class PBanco extends JPanel implements MouseListener {
	
	private FBanco frame = null;

	private CtrlRegras ctrl = null;
	
	private Dado[] dados = new Dado[2];
	
	private Image tabImg = null;
	private Image[] dadosFaces = new Image[6];
	
	public PBanco(CtrlRegras c, FBanco frame) {
		ctrl = c;
		this.frame = frame;
		
		try {
			tabImg = ImageIO.read(new File("resources/tabuleiro.png"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		for (int i = 0; i < 6; i++) {
			try {
				dadosFaces[i] = ImageIO.read(new File("resources/dados/die_face_" + (i + 1) + ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		
		dados[0] = new Dado();
		dados[1] = new Dado();
		
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
			
			this.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
