package gui;

import java.awt.*;
import javax.swing.*;

import logica.CtrlRegras;

@SuppressWarnings("serial")
public class FBanco extends JFrame {
	final int LARG_DEFAULT=716;
	final int ALT_DEFAULT=737;
	
	public FBanco(CtrlRegras c) {
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension screenSize=tk.getScreenSize();
		int sl=screenSize.width;
		int sa=screenSize.height;
		int x=sl/2-LARG_DEFAULT/2;
		int y=sa/2-ALT_DEFAULT/2;
		setBounds(x,y,LARG_DEFAULT,ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(new PBanco(c, this));
		setTitle("Banco Imobiliario");
	}
	
	public static void main(String args[]) {

		(new FBanco(new CtrlRegras())).setVisible(true);
	}
}
