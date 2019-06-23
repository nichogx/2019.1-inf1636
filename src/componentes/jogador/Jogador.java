package componentes.jogador;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Jogador implements JogadorInfo {

	private Pino pin = new Pino();
	private int money = 0;
	public ArrayList<Integer> propriedades = new ArrayList<Integer>();
	private String cor;
	private Color corobj = new Color(0x000000);
	
	// saidas free da prisão
	private boolean cartaSair = false;
	
	private boolean preso = false;

	public Jogador(int money, String cor) {
		this.money = money;
		this.cor = cor;
		if (cor.equals("Vermelho")) {
			corobj = new Color(0xC10000);
		} else if (cor.equals("Azul")) {
			corobj = new Color(0x0050C1);
		} else if (cor.equals("Laranja")) {
			corobj = new Color(0xFF680A);
		} else if (cor.equals("Amarelo")) {
			corobj = new Color(0xEFCE15);
		} else if (cor.equals("Roxo")) {
			corobj = new Color(0x8E008C);
		} else if (cor.equals("Cinza")) {
			corobj = new Color(0x999999);
		}
	}
	
	public Jogador(int money, String cor, int casa, boolean cartaSair, boolean preso) {
		this(money, cor);
		pin.irPara(casa);
		this.cartaSair = cartaSair;
		this.preso = preso;
	}

	public void movePino(int res_dados) {
		pin.mover(res_dados);
	}

	public int getPosX() {
		int p = pin.getCasa();

		if (p <= 10) // x= 601..546..491..436..381..326..271..216..161..106..51 (-55) e y= 636	para 0-10
			return 601-55*p;
		if (p <= 19) // x= 21 e y= 539..484..429..374..319..264..209..154..99 (-55)				para 11-19
			return 21;
		if (p <= 30) // x= 51..106..161..216..271..326..381..436..491..546..601 (+55) e y= 16	para 20-30
			return 51+55*(p%20);
		return 629;  // x= 629 e y= 99..154..209..264..319..374..429..484..539 (+55)			para 31-39
	}

	public int getPosY() {
		int p = pin.getCasa();

		if (p <= 10)    // x= 601..546..491..436..381..326..271..216..161..106..51 (-55) e y= 636	para 0-10
			return 636;
		if (p <= 19)    // x= 21 e y= 539..484..429..374..319..264..209..154..99 (-55)				para 11-19
			return 539-55*(p%11);
		if (p <= 30)    // x= 51..106..161..216..271..326..381..436..491..546..601 (+55) e y= 16	para 20-30
			return 16;
		return 99+55*(p%31); // x= 629 e y= 99..154..209..264..319..374..429..484..539 (+55)		para 31-39
	}
	
	public int getCasa() {
		return pin.getCasa();
	}
	
	public int getMoney() {
		return money;
	}
	
	public int modifyMoney(int val) {
		money += val;
		if (money <= 0) {
			JOptionPane.showMessageDialog(null, cor + ": Seu dinheiro está negativo ou zerado! Se você passar a vez você irá à falência! Venda suas propriedades antes de passá-las ou não terá mais a chance.");
		}
		return money;
	}
	
	public void darCartaSair() {
		cartaSair = true;
	}
	
	/**
	 * 
	 * @return boolean true se ele foi pra prisão, false se usou a carta
	 */
	public boolean irPrisao() {
		pin.irPara(10);
		if (cartaSair) {
			cartaSair = false;
			return false;
		} else {			
			preso = true;
			return true;
		}
	}
	
	public boolean isPreso() {
		return preso;
	}
	
	public void release() {
		preso = false;
	}
	
	public ArrayList<Integer> getPropriedades() {
		return propriedades;
	}
	
	public void compraPropriedade(int property) {
		propriedades.add(property);
	}
	
	public void vendePropriedade(int property) {
		propriedades.remove(propriedades.indexOf(property));
	}
	
	public String getCor() {
		return cor;
	}
	
	public Color getCorObj() {
		return corobj;
	}
	
	public String genSaveString() {
		String ret = "";
		
		ret += String.format("casa %d, money %d, cartaSair %b, preso %b;\n",
				pin.getCasa(), money, cartaSair, preso);
		
		ret += "\t\tpropriedades: " + propriedades.toString();
		
		return ret;
	}
}
