package logica;

public class FabricaPropriedades {
	static Propriedade[] cria() {
		int[] precos = {
				100, 60, 60, 200, 240, 200, 220, 220,
				220, 310, 310, 150, 350, 400,
				120, 100, 150, 160, 140, 140,
				250, 250, 250, 250, 250, 250, 250, 250 // preços arbitrarios por nao ter a informação
		};
		Propriedade[] prop = new Propriedade[28];
		for(int i = 0; i < 28; i++) {
			if (i == 4 || i == 6 || i == 12 || i == 17 || i == 22 || i == 25)
				prop[i] = new Empresa(precos[i]);
			else
				prop[i] = new Terreno(precos[i]);
		}
			
		
		return prop;
	} 
}
