package logica;

public class Jogador {

	private Pino pin = new Pino();
	private int money = 0;
	
	// saidas free da pris�o
	private boolean cartaSair = false;
	
	private boolean preso = false;

	protected Jogador(int m) {
		money = m;
	}

	protected void movePino(int res_dados) {
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
	
	public int getMoney() {
		return money;
	}
	
	protected int modifyMoney(int val) {
		money += val;
		return money;
	}
	
	protected void darCartaSair() {
		cartaSair = true;
	}
	
	protected boolean usarCartaSair() {
		if (!cartaSair || !preso) return false;
		
		cartaSair = false;
		preso = false;
		
		return true;
	}
	
	protected void irPrisao() {
		pin.irPara(10);
		preso = true;
	}
}
