package componentes.jogador;

import java.awt.Color;
import java.util.ArrayList;

public interface JogadorInfo {
	public int getPosX();
	public int getPosY();
	public int getCasa();
	public int getMoney();
	public boolean isPreso();
	public ArrayList<Integer> getPropriedades();
	public String getCor();
	public Color getCorObj();
}
