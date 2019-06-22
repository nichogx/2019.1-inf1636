package gui;

import java.awt.*;
import javax.swing.*;

import logica.CtrlRegras;

@SuppressWarnings("serial")
public class FPlayerInfo extends JFrame {
	
	final int LARG_DEFAULT_PMAIN=716;
	final int ALT_DEFAULT_PMAIN=737;
	final int LARG_DEFAULT=300;
	final int ALT_DEFAULT=500;
	
	public FPlayerInfo(CtrlRegras c) {
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension screenSize=tk.getScreenSize();
		int sl=screenSize.width;
		int sa=screenSize.height;
		int x=sl/2-LARG_DEFAULT_PMAIN/2-LARG_DEFAULT;
		int y=sa/2-ALT_DEFAULT_PMAIN/2;
		setBounds(x,y, LARG_DEFAULT, ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane().add(new PPlayerInfo(c, this));
		setTitle("Informações do jogador");
		this.setVisible(true);
	}
	
	public void sendRepaint() {
		this.getContentPane().repaint();
		System.out.println("teste");
	}

}
