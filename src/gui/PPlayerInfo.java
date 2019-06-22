package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import logica.*;

@SuppressWarnings("serial")
public class PPlayerInfo extends JPanel implements MouseListener {
	
	private FPlayerInfo frame;
	
	public PPlayerInfo(FPlayerInfo frame) {

		this.frame = frame;
		
		this.addMouseListener(this);
		this.setLayout(null);
		
		JButton bVenda = new JButton("Vender uma Propriedade");
		PPlayerInfo p = this;
		bVenda.setBounds(frame.LARG_DEFAULT/2 - 180/2, 20, 180, 30);
		bVenda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		this.add(bVenda);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// deixa texto mais bonito
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// escreve o nome do jogador
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		g2d.setColor(CtrlRegras.getInstance().getPlayerInfo().getCorObj());
		g2d.drawString(CtrlRegras.getInstance().getPlayerInfo().getCor().toUpperCase(), frame.LARG_DEFAULT, 20);
		g2d.setColor(new Color(0));
	}
	
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
}
