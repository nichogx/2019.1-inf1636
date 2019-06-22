package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import logica.*;

@SuppressWarnings("serial")
public class PPlayerInfo extends JPanel implements MouseListener {
	
	private FPlayerInfo frame;
	
	JComboBox<String> cb = new JComboBox<String>();
	
	public PPlayerInfo(FPlayerInfo frame) {

		this.frame = frame;
		
		this.addMouseListener(this);
		this.setLayout(null);
		
		JButton bVenda = new JButton("Vender uma Propriedade");
		bVenda.setBounds(frame.LARG_DEFAULT/2 - 180/2, frame.ALT_DEFAULT - 120, 180, 30);
		bVenda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// aqui vai entrar a fun��o de venda
			}
		});
		this.add(bVenda);
		
		cb.setBounds(20, 110, 200, 20);
		this.add(cb);	
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// deixa texto mais bonito
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// escreve a cor do jogador atual
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		g2d.setColor(CtrlRegras.getInstance().getPlayerInfo().getCorObj());
		g2d.drawString("Jogador "+CtrlRegras.getInstance().getPlayerInfo().getCor().toUpperCase(), frame.LARG_DEFAULT/2-100, 50);
		g2d.setColor(new Color(0));
		
		// escreve o quanto de dinheiro o jogador atual tem
		g2d.setFont(new Font("Arial", Font.PLAIN, 18));
		g2d.drawString("Dinheiro: $"+CtrlRegras.getInstance().getPlayerInfo().getMoney(), 20, 80);
		
		// mostra todas as propriedades de um jogador em uma combo box (sugest�o do professor)
		g2d.drawString("Propriedades:", 20, 110);
		String[] propriedades = CtrlRegras.getInstance().getJogadorPropriedades();

		cb.removeAllItems();
		
		for(int i = 0; i < propriedades.length; i++) {
			cb.addItem(propriedades[i]);
			System.out.println(propriedades[i]);
		}
	}
	
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
}
