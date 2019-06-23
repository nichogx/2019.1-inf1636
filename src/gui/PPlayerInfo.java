package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import logica.*;
import observer.*;

@SuppressWarnings("serial")
public class PPlayerInfo extends JPanel implements ObservadorIF {
	
	private FPlayerInfo frame;
	
	JComboBox<String> cb = new JComboBox<String>();
	
	public PPlayerInfo(FPlayerInfo frame) {

		this.frame = frame;
		this.setLayout(null);
		
		JButton bVenda = new JButton("Vender uma Propriedade");
		bVenda.setBounds(frame.LARG_DEFAULT/2 - 220/2, frame.ALT_DEFAULT - 120, 180, 30);
		bVenda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!CtrlRegras.getInstance().getPlayerInfo().getPropriedades().isEmpty())
					CtrlRegras.getInstance().execVendaProp();
				else
					JOptionPane.showMessageDialog(null, "Você não tem propriedades para vender!");
			}
		});
		this.add(bVenda);
		
		JButton bCompraCasa = new JButton("Comprar Casa/Hotel");
		bCompraCasa.setBounds(frame.LARG_DEFAULT/2 - 220/2, frame.ALT_DEFAULT - 160, 180, 30);
		bCompraCasa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!CtrlRegras.getInstance().getPlayerInfo().getPropriedades().isEmpty())
					CtrlRegras.getInstance().execCompraCasa();
				else
					JOptionPane.showMessageDialog(null, "Você não tem propriedades para comprar casas/hotel!");
			}
		});
		this.add(bCompraCasa);
		
		cb.setBounds(20, 120, 200, 20);
		this.add(cb);
		
		// subscreve como observado
		CtrlRegras.getInstance().add(this);
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
		
		// mostra todas as propriedades de um jogador em uma combo box (sugestão do professor)
		g2d.drawString("Propriedades:", 20, 110);
		String[] propriedades = CtrlRegras.getInstance().getJogadorPropriedades();

		cb.removeAllItems();
		cb.addItem("Clique para visualizar");
		for(int i = 0; i < propriedades.length; i++) {
			cb.addItem(propriedades[i]);
		}
	}

	public void notify(ObservadoIF o) {
		this.repaint();
	}
}
